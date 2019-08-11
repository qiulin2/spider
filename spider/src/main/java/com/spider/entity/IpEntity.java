package com.spider.entity;

import lombok.Data;

/**
 * @Author zql
 * @Date 2019/8/5
 * @Description 代理ip信息实体类
 **/
@Data
public class IpEntity {

    private int id;
    /**ip地址*/
    private String ipHost;
    /**ip端口*/
    private int ipPort;
    /**ip所在地区*/
    private String serverAddress;
    /**ip类型*/
    private String ipType;
    /**ip请求速度*/
    private String ipSpeed;

}
