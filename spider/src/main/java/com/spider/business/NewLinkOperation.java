package com.spider.business;

import com.spider.utils.CommonUtil;
import com.spider.utils.LinkUtil;
import com.spider.utils.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对采集回来的源码做子连接抽取
 * 相对路径方式会造成链接丢失，采用拼接的方式提取
 */
public class NewLinkOperation {
    private final static Logger logger = LoggerFactory.getLogger(NewLinkOperation.class);

    /**
     * 通过爬虫节点获取到的网页源码，jsoup抽取信息
     * href链接用封装好的拼接方法采集
     * 抽取出来数据的格式为：源码内的url，拼接或者转换后的url，url的标题
     *
     * @param url
     * @param doc
     * @return
     */
    public static Map<String, String> extractElemetSiteDeadLink(String url, Document doc) {
        logger.debug("开始抽签子链接************");
        Map<String, String> linkMap = new HashMap<>();
        String baseUrl = "";
        Elements elements = doc.select("a");
        //检查网页是否有base链接
        Elements base = doc.select("base");
        if (base != null) {
            Element em = base.first();
            if (em != null) {
                baseUrl = em.attr("href");
            }
        }
        for (Element element : elements) {
            String src = "";
            String slink = element.attr("href");
            String link = slink.replace("\\", "/");
            link = link.trim();
            if (link.equals("\\")) {
                src = "http://" + LinkUtil.getDomain(url);
            } else if (baseUrl != null && baseUrl.startsWith("http")) {
                src = element.attr("abs:href");
            } else if (baseUrl.equals("/") && !link.startsWith("http")) {
                src = "http://" + LinkUtil.getDomain(url) + "/" + link;
            } else if (link.startsWith("ftp")) {
                continue;
            } else if (link.startsWith("window")) {
                continue;
            } else {
                if (url.endsWith("../")) {
                    url = subStringLink(url);
                }
                if (link.startsWith("./")) {
                    if (!url.endsWith(".html") && !url.endsWith("?") && !url.endsWith(".ycs") && !url.endsWith(".aspx")
                            && !url.endsWith(".asp") && !url.endsWith(".shtml") && !url.endsWith(".htm") &&
                            !url.endsWith(".jspx") && !url.endsWith(".jsp") && !url.endsWith(".php")
                            && !url.endsWith(".ashx") && !url.endsWith(".jhtml")) {
                        if (!url.endsWith("/")) {
                            url = url + "/";
                        }
                    }
                }
                src = CommonUtil.fullUrl(url, link);
            }
            if (url.startsWith(src)) continue;
            if (null == src || src.endsWith("null") || src.contains("#")) continue;
            String sublink = src.toLowerCase();//把所有的url转成小写用作正则比较
            String reg = "^((?!javascript|mailto|select).)*$";//如果不包含这些字符串返回为true
            if (StringUtils.isNotEmpty(src) && sublink.matches(reg)) {
                Pattern p = Pattern.compile("\\t*|\r*|\n*");
                Matcher m = p.matcher(src);
                src = m.replaceAll("");
                if (url.equals(src)) continue;
                int srcIndex = src.indexOf("//");
                if (srcIndex > 0) {
                    String swsrc = src.substring(0, srcIndex + 2);
                    String endsrc = src.substring(srcIndex + 2, src.length());
                    int endIndex = endsrc.indexOf("://");
                    if (endIndex > 0) {
                        String htsrc = endsrc.substring(0, endIndex);
                        String wsrc = endsrc.substring(endIndex + 3, endsrc.length());
                        src = swsrc + htsrc.replace("//", "/") + "://" + wsrc.replace("//", "/");
                    } else {
                        src = swsrc + endsrc.replace("//", "/");
                    }
                    if (src.contains("../")) {
                        int eIndex = src.lastIndexOf("../");
                        if (eIndex > 0) {
                            String endurl = src.substring(eIndex + 3, src.length());
                            String staUrl = src.substring(0, eIndex + 3);
                            src = subStringLink(staUrl) + "/" + endurl;
                        }
                    }
                    if (src.contains("jsessionid")) {
                        src = subJsessionidLink(src);
                    }
                    if (src.contains("jsessionid")) {
                        src = src.substring(0, src.lastIndexOf("jsessionid"));
                    }
                    //src为拼接后的完整链接
                    //slink为从源码中抽取出来的链接
                    //element.text()为链接在源码中的标题
                    linkMap.put(src, element.text());
                }
            }

        }
        return linkMap;
    }


    /**
     * 对不规则带../的url做截取处理
     *
     * @param url
     * @return
     */
    public static String subStringLink(String url) {
        String OMISSION = "../";
        String relative = url;
        int whileNum = 0;
        while (relative.endsWith(OMISSION)) {
            relative = relative.substring(0, (relative.length() - OMISSION.length()));
            whileNum++;
        }
        if (relative.endsWith("/")) {
            whileNum += 1;
        }
        for (int i = 0; i < whileNum; i++) {
            int laIndex = relative.lastIndexOf('/');
            if (laIndex > 8) {
                relative = relative.substring(0, laIndex);
            }
        }
        return relative;
    }


    /**
     * 对url中带有jsessionid的做截取处理，防止采集重复大量无效的链接
     *
     * @param url
     * @return
     */
    public static String subJsessionidLink(String url) {
        int jIndex = url.indexOf("jsessionid");
        int endIndex = url.indexOf("?");
        if (jIndex > 0 && endIndex > 0) {
            String jurl = url.substring(0, jIndex);
            if (jurl.endsWith(";")) {
                logger.debug(jurl);
                jurl = jurl.substring(0, jurl.length() - 1);
            }
            String endUrl = url.substring(endIndex, url.length());
            return url = jurl + endUrl;
        } else {
            return url;
        }
    }
}
