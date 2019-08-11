package com.spider.utils;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class LinkUtil {

    private final static Logger logger = LoggerFactory.getLogger(LinkUtil.class);
    public static final int Invalid_url=0;//无效链接
    public static final int media_url=1;//图片
    public static final int normal_url=2;//网页
    public static final int accessory_url=3;//附件
    public static final int js_url=4; // js
    public static final int css_url=5; //css
    public static final int video_url=6; //视频
    /**
     * Gets host.
     *
     * @param urlString the url string
     * @return the host
     */
    public static String getHost(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            logger.error("获取getHost失败："+e.toString());
            return "";
        }
    }

    /**
     * 根据当前url得到当前域名domain
     *
     * @param url the url
     * @return domain
     */
    public static String getDomain(String url) {
        //在url转换之前，将中文的冒号、点修改为英文的，空格去掉
        String newUrl = url.replace("：", ":")
                .replace("。", ".").replace(" ", "").replace("\\\\","");
        try {
            URL u = new URL(newUrl);
            return u.getHost().toLowerCase();//统一使用小写
        } catch (MalformedURLException e) {
            String reg = "^(https?)://.+$";
            if (!newUrl.matches(reg)) {
                return url;
            }
            logger.debug("获取url的域名异常：url="+url+" ##错误信息"+e.toString());
            return null;
        }
    }

    /**
     * 对中文和无http协议的url做处理
     * @param url
     * @return
     */
    public static String StringHttpURL(String url) {
        url=url.trim();
        String pageUrl=url.toLowerCase();
        String strUrl = null;
        String reg = "^(https?|ftp|file)://.+$";
        if (!pageUrl.matches(reg)) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * 对数字url的处理方法
     * @param url
     * @return
     */
    public static String numberUrlDispose(String url){
        String reg="((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        if(url.matches(reg)){
            return url;
        }
        logger.debug("Domain: " + url + " is illegal.");
        return null;
    }



    /**
     * 对mime类型做判断，区分text image application等
     * 对应siteDeadLinkDetail的资源类型（1图片、2网页、3附件、4其他）
     * @param contentType
     * @return
     */
    public static int mimeTypeDispose(String contentType){
        if(null==contentType)return Invalid_url;
        if(contentType.trim().startsWith("audio")){
            return video_url;
        }else if(contentType.trim().startsWith("image")){
            return media_url;
        }else if (contentType.trim().startsWith("text")){
            return normal_url;
        }else if(contentType.trim().startsWith("application")){
            return accessory_url;
        }else {
            return Invalid_url;
        }
    }
    /**
     * 通过url的后缀去简单判断
     * 对应的资源类型（1图片、2网页、3附件、4js、5css、6视频）
     * @param url
     * @return
     */
    public static int resourceType(String url){
        if(null==url)return Invalid_url;
        url=url.toLowerCase();//防止文件格式大小写不统一，先把所有的url字符转成小写
        String regImg=".+(.bmp|.jpg|.png|.tiff|.gif|.pcx|.tga|.exif|.fpx|.svg|.psd|.cdr|.pcd|.dxf|.ufo|.eps|.ai|.raw|.wmf)$";
        String regVideo = ".+(.avi|.mov|.navi|.3gp|.mkv|.flv|.f4v|.webm|.ra|.rm|.rmvb|.wmv|.mp4|.mp3|.mpeg|.mpe|.mpg|.asf|.divx|.vob|.dat)$";
        String redAccessory =".+(.doc|.docx|.xlsx|.pdf|.txt|.xls|.rar|.wps|.rtf|.zip|.cvs|.exe|.ceb|.pcx|.pptx|.mht|.ppt)$";
        String regJs=".+(.js)$";
        String regCss=".+(.css)$";
        if(url.matches(regImg)){
            return media_url;
        }else if(url.matches(regVideo)){
            return video_url;
        }else if(url.matches(redAccessory)){
            return accessory_url;
        }else if(url.matches(regJs)){
            return js_url;
        }else if(url.matches(regCss)){
            return css_url;
        }else {
            return normal_url;
        }
    }

    /**
     * 格式化 抽取的baseUri 用于相对路径 ./   ../ 的情况
     * @param baseUri
     * @return 格式化后的新链接
     */
    public static String formatBaseUri(String baseUri) throws Exception {
        //fixme
        String rst = null;
        if(StringUtils.isEmpty(baseUri)) return null;
        //last Dot Index
        int ldidx = baseUri.lastIndexOf('.');
        //last Slash Index
        int lsidx = baseUri.lastIndexOf('/');

        if(lsidx > ldidx && !baseUri.endsWith("/")){
            rst = baseUri + "/";
        }else{
            rst = baseUri;
        }
        return rst;
    }

    /**
     * 格式化 抽取的baseUri 用于相对路径 ./   ../ 的情况
     * @param doc  Document 提取uri出来
     */
    public static void formatBaseUri(Document doc) throws Exception {
        String baseUri = doc.baseUri();
        if(StringUtils.isNotEmpty(baseUri)){
            String newBaseUri = formatBaseUri(baseUri);
            doc.setBaseUri(newBaseUri);
        }
    }
}
