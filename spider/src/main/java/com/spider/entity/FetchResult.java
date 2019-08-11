package com.spider.entity;

import lombok.Data;
import org.junit.experimental.theories.DataPoint;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zql
 * @Date 2019/8/5
 * @Description 抓取网页源码实体类
 **/
@Data
public class FetchResult implements java.io.Serializable {

    /** 原始网页url */
    private String url;
    /** 自动跳转后的URL */
    private String followedUrl;
    /**抓取到的content-type类型*/
    private String contentType;
    /** 网页编码 */
    private String encoding;
    /** 网页内容，二进制 */
    private byte[] content;
    /** 网页http响应代码 */
    private int httpCode;
    /** http头部状态行 */
    private String statusLine;
    /** http响应头 */
    private Map<String, String> headers = new HashMap<String, String>();

    private String extField;
    /**请求响应时长*/
    private long responseIntervalTime;
    /** 网页抓取时间 */
    private LocalDateTime spiderTime;
    /**原始列表页地址**/
    private String listPageUrl;



}
