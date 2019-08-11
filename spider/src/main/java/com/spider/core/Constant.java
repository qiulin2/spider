package com.spider.core;

/**
 * create by Zql
 * 保存参数的常量类
 */
public class Constant {
    /** mongo链接地址 */
    public static String MONGODB_HOST="101.200.40.*";
    /** mongo库名 */
    public static String MONGODB_NAME="cloud_db";

    /** mongo账号 */
    public static String MONFODB_AUTH="cloud";

    /** mongo密码 */
    public static String MONGODB_PASSWORD="cloud";

    /** 链接存放的表 */
    public static String LINK="link";

    /** 文章存放的表 */
    public static String ARTICLE="article";

    /** 链接排重 */
    public static String FILTER_LINK="filterLink";

    /** redis链接地址 */
    public static String REDIS_HOST="101.200.40.*";

    /** redis端口 */
    public static int REDIS_PORT=6379;

    /** redis超时时间 */
    public static int REDIS_TIMEOUT=20000;

    /** redis密码 */
    public static String REDIS_PASSWORD="zql@2018";

    /** redis保存ip队列名 */
    public static String IP_REDIS_LIST= "redis_ipList";


}
