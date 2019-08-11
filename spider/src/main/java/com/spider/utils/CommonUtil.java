package com.spider.utils;

import java.net.*;

public class CommonUtil {



	/**
	 * Checks if specified link is full URL.
	 * 
	 * @param link
	 * @return True, if full URl, false otherwise.
	 */
	public static boolean isFullUrl(String link) {
		if (link == null) {
			return false;
		}
		link = link.trim().toLowerCase();
		return link.startsWith("http://") || link.startsWith("https://") || link.startsWith("file://");
	}

	/**
	 * 考虑各种链接格式，构造正确的url地址
	 * 
	 * @param pageUrl
	 *            当前页url地址
	 * @param link
	 *            链接属性
	 * @author liupf@buge.cn
	 */
	public static String fullUrl(String pageUrl, String link) {
        if (link == null)
            return "";

        if (!isFullUrl(pageUrl)) { // 保证当前页链接是以http://开始
            pageUrl = "http://" + pageUrl;
        }

        if (isFullUrl(link)) { // link本来就是绝对地址
            return link;
        } else if (link != null && link.startsWith("?")) { // link是查询参数，"?name=one"
            int qindex = pageUrl.indexOf('?');
            if (qindex < 0) { // pageUrl=http://www.test.com/user，返回"http://www.test.com/use?name=one"
                return pageUrl + link;
            } else { // pageUrl=http://www.test.com/user?name=one，返回"http://www.test.com/use?name=one"
                return pageUrl.substring(0, qindex) + link;
            }
        } else if (link.startsWith("//")) { // link是从根目录开始的地址
            int pageindex=pageUrl.indexOf("//");
            return pageUrl.substring(0,pageindex)+link;
        } else if(link.startsWith("/")){
            return makeUrl(pageUrl, link);
        }else if (link.startsWith("./")) { // link是从"./"开始的地址
            int qindex = pageUrl.indexOf('?');
            int lastSlashIndex = pageUrl.lastIndexOf('/');
            if(qindex>0){
                String qpage=pageUrl.substring(0,qindex);
                int lSlashIndex = qpage.lastIndexOf('/');
                if(lastSlashIndex>0) {
                    return qpage.substring(0, lSlashIndex + 1) + link.substring(1);
                }else {
                    return pageUrl.substring(0, lastSlashIndex) + link.substring(1);
                }
            }
			if (lastSlashIndex > 8) {
				return pageUrl.substring(0, lastSlashIndex) + link.substring(1);
			} else {
				return pageUrl + link.substring(1);
			}
		} else if (link.startsWith("../")) { // link是从"../"开始的地址，回退一级
			return makeAssembleUrl(pageUrl, link);
		} else {
		    if(pageUrl.contains("?")){
                int qindex = pageUrl.indexOf('?');
                String qpageUrk=pageUrl.substring(0,qindex);
                int yindex=qpageUrk.lastIndexOf('/');
                return qpageUrk.substring(0,yindex+1)+link;
            }
			int lastSlashIndex = pageUrl.lastIndexOf('/');
            int linkIndex=link.lastIndexOf('/');
			if (lastSlashIndex > 8) {
			   /* if(linkIndex!=-1) {
                    String sublink = link.substring(0, linkIndex);
                    String pageIndex = pageUrl.substring(0, lastSlashIndex);
                    if (pageIndex.endsWith(sublink)) {
                        int hh=pageIndex.indexOf(sublink);
                        String url=pageIndex.substring(0,hh);
                        return url+link;
                    }
                }*/   //注释掉对有两级相同目录的链接截取
                return pageUrl.substring(0, lastSlashIndex) + "/" + link;
			} else {
                return pageUrl + "/" + link;
			}
		}

	}

	private static String makeAssembleUrl(String base, String multiOmission) {
		String OMISSION = "../";
		String relative = multiOmission;
		while (relative.startsWith(OMISSION)) {
			base = getParentUrl(base);
			relative = relative.substring(OMISSION.length());
		}
		relative=relative.replace("./","");
        return base + relative;
	}

	private static String getParentUrl(String url) {
	    if(url.contains("?")){
	        int uindex=url.indexOf('?');
	        url=url.substring(0,uindex);
        }
		int lastSlashIndex = url.lastIndexOf('/');
		String rest = url.substring(0, lastSlashIndex);
		lastSlashIndex = rest.lastIndexOf('/');
		String domain=LinkUtil.getDomain(url);
		if(rest.substring(lastSlashIndex+1,rest.length()).equals(domain)){
		    return rest+"/";
        } else if (lastSlashIndex > 8) {
			return url.substring(0, lastSlashIndex + 1);
		} else {
			return url + "/";
		}
	}

	public static String makeUrl(String pageUrl, String path) {
		try {
                URL base = new URL(pageUrl);
                StringBuilder sb = new StringBuilder();
                sb.append(base.getProtocol());
                sb.append("://");
                sb.append(base.getHost());

                if (base.getPort() != 80 && base.getPort() > 0)
                    sb.append(":" + base.getPort());
                if (path.charAt(0) != '/')
                    sb.append('/');
                sb.append(path);
                return sb.toString();
		} catch (Exception e) {

		}

		return "";
	}

}
