# spider
java基于jsoup的链接爬虫

项目主要是针对于网站url解析,网站采集规则：采集深度maxDepth 采集最大链接数：maxLink 站内采集:domain
针对ip限制问题使用动态代理,项目中抓取西刺免费ip进行切换ip(ip质量不太好,用于生产项目可购买其他平台稳定ip)

针对base,abs,ftp,window标签以及不规则的../等url验证和拼接
兼容jspx,ashx,jhtml,php,ycs,shtml,jsp等后缀url
对jsessionid动态url进行处理

项目第一版为只采集url链接,对于网页内容没有进行解析处理,针对不同网站需要自己写解析逻辑
