package com.spider.http.jsoup;

import com.spider.entity.FetchResult;
import com.spider.entity.IpEntity;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;


import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * creata by Zql
 * 构建jsoup对url进行请求，封装采集到的数据
 */
@Component
public class JsoupConnectionUtil {

    private final static Logger logger = LoggerFactory.getLogger(JsoupConnectionUtil.class);

    /**
     * 记录线程访问失败次数，达到阈值进行切换代理ip
     */
    private Map<Thread,Integer> threadFetchStatus = new HashMap<>();

    /**
     * 是否进行代理
     */
    private boolean isProxy = false;

    /**
     * 防止多个线程拿到一个ip代理 这里使用ThreadLocal
     */
    private ThreadLocal<IpEntity> threadLocal = new ThreadLocal<>();

    @Autowired
    ListOperations listOperations;

    /**
     * 模拟浏览器发起get请求获取网页数据
     * @param url
     * @return
     */
    public FetchResult jsoupRequest(String url) {
        FetchResult fetchResult = new FetchResult();
        Connection.Response response=null;
        long startTime = System.currentTimeMillis();
        try {
            //模拟浏览器，构造请求头等信息
            Connection conn = Jsoup.connect(url).ignoreContentType(true).timeout(5000);
            if(isProxy){
                IpEntity ipEntity = threadLocal.get();
                if(null == ipEntity){
                    //从redis中获取ip地址
                    threadLocal.set((IpEntity)listOperations.leftPop("proxyIps"));
                }else {
                    conn.proxy(ipEntity.getIpHost(),ipEntity.getIpPort());
                }
            }
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.header("Connection","close");
            //模拟安卓手机请求app链接
            //conn.header("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Mobile Safari/537.36");
            //模拟浏览器请求
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            response = conn.execute();//发起请求
            //开始封装响应回来的数据
            fetchResult.setContentType(response.contentType());//获取ContentType信息
            fetchResult.setFollowedUrl(response.url().toString());//获取跳转url信息
            fetchResult.setHttpCode(response.statusCode());//获取网页返回状态码结果
            fetchResult.setContent(response.body().getBytes());//获取网页数据
            fetchResult.setUrl(url);
            //从网页获取编码格式，如果获取为空，设置utf-8
            if(response.charset()==null){
                fetchResult.setEncoding("utf-8");
            }else {
                fetchResult.setEncoding(response.charset());
            }
            long endTime = System.currentTimeMillis();
            fetchResult.setSpiderTime(LocalDateTime.now());//抓取时间
            fetchResult.setResponseIntervalTime(endTime-startTime);//响应时间

        }catch (ConnectException e){
            fetchResult.setHttpCode(999);
            logger.error("爬虫请求链接超时：{}",e);
        }catch (HttpStatusException e){
            fetchResult.setHttpCode(e.getStatusCode());
            logger.error("爬虫请求网页失败，httpCode:{}",e.getStatusCode());
        }catch (UnknownHostException e) {
            fetchResult.setHttpCode(991);
            logger.error("Jsoup采集网页失败,url:{} 错误信息：{}",url,e);
        }catch (Exception e){
            fetchResult.setHttpCode(999);
            logger.error("Jsoup采集网页失败,url:{} 错误信息：{}",url,e);
        }

        //记录非正常HttpCode状态码
        if (fetchResult.getHttpCode() >= 400) {
            Thread thread = Thread.currentThread();
            if(threadFetchStatus.get(thread)==10){
                if(isProxy == false){
                    isProxy = true;
                }else {
                    //代理设置空，再次获取新的ip代理
                    threadLocal.remove();
                    //将线程状态重置为0
                    threadFetchStatus.put(thread,0);
                }
            }else {
                Integer status = threadFetchStatus.get(thread);
                threadFetchStatus.put(thread,status+1);
            }
        }
        return fetchResult;
    }

}
