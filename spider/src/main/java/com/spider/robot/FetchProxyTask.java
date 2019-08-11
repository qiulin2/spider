package com.spider.robot;

import com.spider.entity.IpEntity;
import com.spider.main.AbstractTask;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zql
 * @Date 2019/8/5
 * @Description 抓西刺免费代理，该网站代理不稳定，也可以去找其他比较稳定的网站
 **/
@Component
public class FetchProxyTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(FetchProxyTask.class);

    private static CloseableHttpClient httpClient; // 创建httpClient实例

    @Autowired
    ListOperations listOperations;

    /**
     * 先抓西刺的免费代理
     * 抓取成功后进行第一次验证，验证通过存入redis中
     * @return
     */
    @Override
    public int doJob() {
        logger.info("开始执行抓取代理任务");
        String xurl[]={"http://www.xicidaili.com/nn/",
                "http://www.xicidaili.com/nn/2",
                "http://www.xicidaili.com/nn/3" };
        String url="http://ip.chinaz.com/getip.aspx";
        for(int i=0;i<xurl.length;i++) {
            List<IpEntity> list =fetchProxyByXiciIp(xurl[i]);
            if (list.size() > 0) {
                for (IpEntity ipEntity : list) {
                    boolean isTrue = httpGet(url, ipEntity.getIpHost(), ipEntity.getIpPort());
                    if (isTrue) {
                        listOperations.rightPush("proxyIps",ipEntity);
                    }
                }
            }
        }
        return 1;
    }

    public List<IpEntity> fetchProxyByXiciIp(String url) {
        Document ipdoc = null;
        List<IpEntity> iplist=new ArrayList<>();
        int count=0;
        try {
            Connection conn = Jsoup.connect(url).ignoreContentType(true).timeout(5000);
            conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.header("Connection", "close");
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            ipdoc = conn.get();

            Elements trs = ipdoc.select("table[id=ip_list]").select("tbody").select("tr");
            for (Element element:trs) {
                count++;
                if(count<3){
                    continue;
                }
                IpEntity ipEntity = new IpEntity();
                String ipAddress = element.select("td").get(1).text();
                String ipPort = element.select("td").get(2).text();
                String serverAddress = element.select("td").get(3).text();
                String ipType = element.select("td").get(5).text();
                String ipSpeed = element.select("td").get(6).select("div[class=bar]").
                        attr("title");

                ipEntity.setIpHost(ipAddress);
                ipEntity.setIpPort(Integer.parseInt(ipPort));
                ipEntity.setServerAddress(serverAddress);
                ipEntity.setIpType(ipType);
                ipEntity.setIpSpeed(ipSpeed);

                iplist.add(ipEntity);
            }
        } catch (IOException e) {
            logger.error("获取ip页面失败，请重新尝试请求,错误信息：{}",e);
        }
        logger.info("抓取到ip数量：{}",iplist.size());
        return iplist;
    }

    /**
     * http请求方式验证ip有效性
     * @param url
     * @param ipHost
     * @param port
     * @return
     */
    public boolean httpGet(String url,String ipHost,int port){

        CloseableHttpResponse response = null;
        RequestConfig defaultRequestConfig = null;
        try {
            httpClient= HttpClients.createDefault();
            defaultRequestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                    .build();
            HttpGet httpGet = new HttpGet(url); // 创建httpget实例
            httpGet.setConfig(defaultRequestConfig);
            HttpHost proxy = new HttpHost(ipHost, port);//网上找的代理IP
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
            response = httpClient.execute(httpGet); // 执行http get请求
            response.close();
            httpClient.close();
            return true;
        }catch (Exception e){
            logger.error("检测ip失效,ip：{}",ipHost);
            return false;
        }

    }


}
