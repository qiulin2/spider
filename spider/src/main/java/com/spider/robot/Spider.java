package com.spider.robot;

import com.spider.business.NewLinkOperation;
import com.spider.entity.FetchResult;
import com.spider.http.jsoup.JsoupConnectionUtil;
import com.spider.main.AbstractTask;
import com.spider.utils.LinkUtil;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zql
 * @Date 2019/8/5
 * @Description 执行爬虫抓取逻辑
 **/
@Component()
@Service()
public class Spider extends AbstractTask {

    private final static Logger logger = LoggerFactory.getLogger(Spider.class);

    //本次采集最大链接数
    private volatile int maxLink;

    //本次采集最大深度
    private volatile int maxDepth;

    //本次采集入口首页地址
    private String pageUrl;

    //domain
    private String domain;

    //任务队列里已经执行的总链接数
    private AtomicInteger totalLinks = new AtomicInteger(0);

    //保存当前抓取过的链接，主键是url，值是url对应的深度
    private Map<String, Integer> fetchedUrlMap = Collections.synchronizedMap(new HashMap<>());

    //当前采集url深度。初始是第一级
    private AtomicInteger currentDepth = new AtomicInteger(1);

    //任务是否采集结束的标志
    private AtomicBoolean running = new AtomicBoolean(true);

    //广度优先，一级一级的采集
    private Map<Integer, LinkedBlockingQueue<String>> depthQueues = Collections.synchronizedMap(new HashMap<>());


    @Autowired
    JsoupConnectionUtil jsoupConnectionUtil;

    public int doJob(){
        Map<String,Object> argumentsMap = getArgumentsMap();
//        this.pageUrl = "http://tl.cyg.changyou.com/goods/char_detail?serial_num=201912091748056873";
        this.pageUrl = argumentsMap.get("url").toString();
//        setMaxDepth(Integer.parseInt(argumentsMap.get("maxDepth").toString()));
//        setMaxLink(Integer.parseInt(argumentsMap.get("maxLink").toString()));
        logger.info("开始初始化爬虫任务");
        domain = LinkUtil.getDomain(pageUrl);
        //队列注入首页地址
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        queue.add(pageUrl);
        depthQueues.put(1, queue);

//        Thread thread = new Thread(createTask());
//        thread.start();

        try {
            //第一层只有一个首页，需要先开一个线程进行处理，防止使用线程池第一个线程拿到后 后面的线程拿到null 一直去加深度
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //使用线程池来进行多线程执行爬取任务
        Executor executor = Executors.newFixedThreadPool(10);
        executor.execute(createTask());

        logger.info("采集线程启动成功");

        //启动监控线程
        Thread monitor = new Thread(new Monitor());
        monitor.start();
        return 1;
    }

    private Runnable createTask(){
        return new Runnable() {
            @Override
            public void run() {
                while (running.get()) {
                    //从队列里面拿需要抓取的链接
                    String url = getTaskUrl();
                    try {
                        if (url == null) {
                            continue;
                        } else {
                            Thread.sleep(1000);
                        }
                        JsoupConnectionUtil jsoupConnectionUtil = new JsoupConnectionUtil();
                        FetchResult fetchResult = jsoupConnectionUtil.jsoupRequest(url);
                        //对抓取过的链接进行计数
                        totalLinks.addAndGet(1);
                        if (fetchResult.getHttpCode() >= 400)
                            continue;
                        // 抽取新链接并添加到任务队列
                        saveNewLink(url, fetchResult);
                    } catch (Exception e) {
                        logger.error("采集页面失败,url: {}",url);
                    }
                }
            }
        };
    }

    /**
     * 对于抓取回来的网页信息进行分析，摘取需要的相关数据
     *
     * @param url         当前采集的url
     * @param fetchResult 当前url采集后返回的数据
     * @throws Exception
     */
    private void saveNewLink(String url, FetchResult fetchResult) throws Exception {
        //将二进制网页源码换成html
        String html = new String(fetchResult.getContent(), fetchResult.getEncoding());
        System.out.println(html);
        //将html转换成可以进行操作的Document对象
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        //TODO 不同网站爬取需要自定义爬取规则
        Map<String, String> newLinks = NewLinkOperation.extractElemetSiteDeadLink(url, doc);
        logger.info("提取链接数：" + newLinks.size());
        List<String> links = new ArrayList<String>();
        for (String newUrl : newLinks.keySet()) {
            if (newUrl == null)
                continue;

            //如果domain不匹配，已经存在队列中，不是正常链接。不再继续采集
            if (!domain.equals(LinkUtil.getDomain(newUrl)) ||
                    fetchedUrlMap.containsKey(newUrl) || LinkUtil.resourceType(newUrl) != LinkUtil.normal_url) {
                continue;
            }

            //将子链接存放进下一深度队列当中
            LinkedBlockingQueue<String> queue = depthQueues.get(currentDepth.get() + 1);
            if (null == queue) {
                queue = new LinkedBlockingQueue<String>();
                depthQueues.put(currentDepth.get() + 1, queue);
            }
            fetchedUrlMap.put(newUrl, currentDepth.get());
            queue.offer(newUrl);
            links.add(newUrl);

        }
        logger.info("{} 有效新子链接数量：{}",url,links.size());
    }


    /**
     * 从队列里获取要执行的任务链接
     * @return
     */
    protected synchronized String getTaskUrl(){
        final int currentDep = this.currentDepth.get();
        final Map<Integer, LinkedBlockingQueue<String>> depQueues = this.depthQueues;

        LinkedBlockingQueue<String> depQueue = depQueues.get(currentDep);
        //先查当前深度是否有需要采集的链接
        if (null != depQueue && !depQueue.isEmpty()){
            return depQueue.poll();
        }

        //当前深度未获取到链接  开始采集当前深度+1
        depQueue = depQueues.get(currentDep + 1);
        if (null != depQueue && !depQueue.isEmpty()) {
            currentDepth.addAndGet(1);
            return depQueue.poll();
        }
        return null;
    }


    public int getMaxLink() {
        return maxLink;
    }

    public void setMaxLink(int maxLink) {
        this.maxLink = maxLink;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    /**
     * 监控线程，判断本次采集是否完成
     */
    protected class Monitor implements Runnable {

        @Override
        public void run() {
            while (true) {
                try { // 每3分钟检查一次网站采集状态
                    Thread.sleep(1000 * 180);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("currentDepth=" + currentDepth + ", totalLinks=" + totalLinks + ", 当前队列url数量=" + depthQueues.get(currentDepth.get()).size());

                if ((depthQueues.get(currentDepth.get()) == null || depthQueues.get(currentDepth.get()).size() < 2 || totalLinks.get() < 2) &&
                        (depthQueues.get(currentDepth.get() + 1) == null || depthQueues.get(currentDepth.get() + 1).isEmpty())) {
                    //logger.info("网站采集完毕");
                    System.exit(0);
                }

                if (currentDepth.get() >= getMaxDepth() || totalLinks.get() > getMaxLink()) {
                    logger.info("完成设置采集最大深度，当前深度为; " + currentDepth.get() + "采集链接数：" + totalLinks.get());
                    System.exit(0);
                }

            }

        }
    }


}
