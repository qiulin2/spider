package com.spider.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String操作工具类
 */
public class StringUtils {
    private static Logger logger= LoggerFactory.getLogger(StringUtils.class);
	private static MessageDigest digest = null;
	private static final String dfltEncName = "UTF-8";

	public static String numberToChar(String number) {
		if (isEmpty(number)) {
			return null;
		}
		//System.out.println(number);
		String str = "";
		switch (Integer.parseInt(number)) {
		case 1:
			str = "A";
			break;
		case 2:
			str = "B";
			break;
		case 3:
			str = "C";
			break;
		case 4:
			str = "D";
			break;
		case 5:
			str = "E";
			break;
		case 6:
			str = "F";
			break;
		case 7:
			str = "G";
			break;
		case 8:
			str = "H";
			break;
		case 9:
			str = "I";
			break;
		case 10:
			str = "J";
			break;
		default:
			break;
		}
		return str;
	}

	/**
	 * 过滤字符串
	 * 
	 * @param inputString
	 *            要过滤的字符串
	 * @param start
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return string
	 */
	public static String HtmlToText(String inputString, int start, int end) {
		String htmlStr = inputString;
		// if(org.apache.commons.lang.StringUtils.isBlank(inputString)){
		// return "";
		// }
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;

		Pattern p_html1;
		Matcher m_html1;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+>";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
		    logger.error("Html2Text:"+e.toString());
		}

		// if(org.apache.commons.lang.StringUtils.length(textStr)>end){
		// return org.apache.commons.lang.StringUtils.substring(textStr, start,
		// end).concat("...");// 返回文本字符串
		// }
		return textStr;

	}

	/**
	 * 描述: 判断字符串是否为空   作者 lxx 时间：2015-10-10下午03:30:44  
	 * 
	 * @param str
	 *            字符串  
	 * @return boolean
	 */
	public static boolean isNotNull(Object... str) {

		if (str == null) {
			return false;
		}

		for (Object s : str) {
			if (isEmptyObj(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 描述: 判断object是否为空  作者 lxx 时间：2015-10-10下午03:30:44  
	 * 
	 * @param o
	 *            字符串  
	 * @return boolean
	 */
	public static boolean isEmptyObj(final Object o) {
		if (o == null || o.toString().trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断参数是否为空，NULL与""返回true，反之false
	 * 
	 * @param s
	 * @return boolean
	 */
	public static boolean isEmpty(final String s) {
		if (s == null || s.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * 判断参数是否为空，NULL与""返回false，反之true
	 * 
	 * @param s
	 * @return boolean
	 */
	public static boolean isNotEmpty(final String s) {
		return !isEmpty(s);
	}

	public static String join(final String... params) {
		StringBuilder sb = new StringBuilder();
		for (String s : params)
			sb.append(s);
		return sb.toString();
	}

	/*--------------------------------------session hash----------------------------------*/
	public static final String hash(final String s) {
		return Md5Util.hash(s);
	}

	public static final String toHex(byte hash[]) {
		if (hash == null)
			return null;
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * 描述:解密字符串
	 * 作者：lxx
	 * 时间：2015-11-9下午04:17:40
	 * @param s		要解密的字符串
	 * @return
	 */
	public static String decode(String s) {
		String str = null;

		if (s == null)
			return null;
		try {
			str = URLDecoder.decode(s, dfltEncName);
		} catch (UnsupportedEncodingException e) {
		    logger.error("解密字符串失败："+e.toString());
		}
		return str;
	}

	public static String encode(String s) {
		String str = null;

		if (s == null)
			return null;
		try {
			str = URLEncoder.encode(s, dfltEncName);
			str = str.replaceAll("\\+", "%2B");
		} catch (UnsupportedEncodingException e) {
		    logger.error("匹配encode失败"+e.toString());
		}
		return str;
	}

	/**
	 * rest 匹配为"_"的数据
	 */
	public static String toHorizontal(final String str) {
		if (isEmpty(str))
			return str;
		if (str.equals("_"))
			return null;
		return str;
	}

	/**
	 * 去掉[]的toString方法
	 * 
	 * @param o
	 * @return String
	 */
	public static String toStringValue(Object o) {
		String tmp = toString(o);
		if (tmp == null || tmp.length() <= 2)
			return null;
		return tmp.substring(1, tmp.length() - 1);
	}

	public static String toString(Object o) {
		if (o == null)
			return null;
		if (o.getClass().isArray()) {
			String className = o.getClass().getName();
			if ("[I".equals(className)) {
				int[] a = (int[]) o;
				return Arrays.toString(a);
			} else if ("[J".equals(className)) {
				long[] a = (long[]) o;
				return Arrays.toString(a);
			} else if ("[S".equals(className)) {
				short[] a = (short[]) o;
				return Arrays.toString(a);
			} else if ("[C".equals(className)) {
				char[] a = (char[]) o;
				return Arrays.toString(a);
			} else if ("[B".equals(className)) {
				byte[] a = (byte[]) o;
				return Arrays.toString(a);
			} else if ("[F".equals(className)) {
				float[] a = (float[]) o;
				return Arrays.toString(a);
			} else if ("[D".equals(className)) {
				double[] a = (double[]) o;
				return Arrays.toString(a);
			} else if ("[Z".equals(className)) {
				boolean[] a = (boolean[]) o;
				return Arrays.toString(a);
			} else {
				Object[] a = (Object[]) o;
				return Arrays.toString(a);
			}
		} else {
			return o.toString();
		}
	}

	/**
	 * 判断字符串是否为数字组成
	 * 
	 * @param str
	 *            字符串
	 * @return true 数字、 false 非数字
	 */
	public static boolean isNumber(final String str) {
		if (null == str)
			return false;
		String pattern = "^[0-9]*";
		return str.matches(pattern);
	}

	/**
	 * 根据字符串返回Long数组 EG：101,205,303 ==> {101,205,303}
	 * 
	 * @param str
	 *            字符串
	 * @return Long数组
	 */
	public static Long[] getNumberArray(final String str) {

		// 字符串数组
		String sIds[] = null;
		// Long串数组
		Long lIds[] = null;

		// 字符串不为NULL切割字符串
		if (str != null && !"".equals(str)) {

			sIds = str.split(",");
			int length = 0;

			// 确定lIds长度
			for (int i = 0; i < sIds.length; i++) {

				if (isNumber(sIds[i])) {
					length++;
				}
			}

			lIds = new Long[length];

			// 转换Str数组->Long数组
			for (int i = 0; i < sIds.length; i++) {

				if (isNumber(sIds[i])) {
					lIds[i] = Long.parseLong(sIds[i]);
				}
			}

			return lIds;

		} else {
			return new Long[0];
		}

	}

	public static String getStringsValue(String... str) {

		String result = "";

		if (str == null)
			return result;

		for (int i = 0; i < str.length; i++) {
			if (str[i] != null && !str[i].equals("")) {
				String s = str[i].replace("'", "\\'");
				if (i < str.length - 1) {
					result = result + "'" + s + "',";
				} else {
					result = result + "'" + s + "'";
				}
			}
		}

		return result;
	}

	/**
	 * convert String to String array with special phrase, such
	 * as",space,dash,omit"
	 * 
	 * @param str
	 * @return String[]
	 */
	public static String[] ConvertString2Array(String str) {
		return ConvertString2Array(str, null);
	}

	public static String[] ConvertString2Array(String str, String specialDash) {
		String[] result = new String[0];
		if (str == null)
			return result;
		String[] ret = null;
		if (specialDash == null) {
			ret = str.split("[" + " ,;|　" + "]");
		} else {
			ret = str.split(specialDash);
		}

		if (ret == null)
			return result;
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < ret.length; i++) {
			if (ret[i] != null && !"".equals(ret[i])) {
				resultList.add(ret[i].trim());
			}
		}
		return resultList.toArray(result);
	}

	public static String full2HalfChange(String QJstr) throws UnsupportedEncodingException {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < QJstr.length(); i++) {
			Tstr = QJstr.substring(i, i + 1);
			if (Tstr.equals("　")) {
				outStrBuf.append(" ");
				continue;
			}
			b = Tstr.getBytes("unicode");
			if (b[2] == -1) {
				// 表示全角？
				b[3] = (byte) (b[3] + 32);
				b[2] = 0;
				outStrBuf.append(new String(b, "unicode"));
			} else {
				outStrBuf.append(Tstr);
			}
		} // end for.
		return outStrBuf.toString();
	}

	public static String half2Fullchange(String QJstr) throws UnsupportedEncodingException {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < QJstr.length(); i++) {
			Tstr = QJstr.substring(i, i + 1);
			if (Tstr.equals(" ")) {
				// 半角空格
				outStrBuf.append(Tstr);
				continue;
			}
			b = Tstr.getBytes("unicode");
			if (b[2] == 0) {
				// 半角?
				b[3] = (byte) (b[3] - 32);
				b[2] = -1;
				outStrBuf.append(new String(b, "unicode"));
			} else {
				outStrBuf.append(Tstr);
			}
		}
		return outStrBuf.toString();
	}

	/**
	 * 
	 * @param reg
	 * @param content
	 * @return true 非法 false 合法
	 */
	public static boolean checklegal(String reg, String content) {
		Pattern p = Pattern.compile(reg); // 正则表达式
		Matcher m = p.matcher(content);// 操作的字符串
		return m.find();
	}

	/**
	 * 从ip的字符串形式得到字节数组形式
	 * 
	 * @param ip
	 *            字符串形式的ip
	 * @return 字节数组形式的ip
	 */
	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
		try {
			ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
		} catch (Exception e) {
		    logger.error("获取ip字符串失败"+e.toString());
		}
		return ret;
	}

	/**
	 * 对原始字符串进行编码转换，如果失败，返回原始的字符串
	 * 
	 * @param s
	 *            原始字符串
	 * @param srcEncoding
	 *            源编码方式
	 * @param destEncoding
	 *            目标编码方式
	 * @return 转换编码后的字符串，失败返回原始字符串
	 */
	public static String getString(String s, String srcEncoding, String destEncoding) {
		try {
			return new String(s.getBytes(srcEncoding), destEncoding);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, String encoding) {
		try {
			return new String(b, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            要转换的起始位置
	 * @param len
	 *            要转换的长度
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, int offset, int len, String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
		    logger.error("将字节数组转化字符串失败"+e.toString());
			return new String(b, offset, len);
		}
	}

	/**
	 * @param ip 		ip的字节数组形式
	 * @return 字符串形式的ip
	 */
	public static String getIpStringFromBytes(byte[] ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(ip[0] & 0xFF);
		sb.append('.');
		sb.append(ip[1] & 0xFF);
		sb.append('.');
		sb.append(ip[2] & 0xFF);
		sb.append('.');
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}

	/**
	 * 获取32位的uuid
	 * 
	 * @return String
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
	}

	/**
	 * 根据url拆分获取跟域名
	 * 
	 * @param url
	 *            完整的url
	 * @return 根域名
	 */
	public static String getRootUrl(String url) {
		try {
			if (url == null && "".equals(url))
				return "";
			// 统一格式
			url = url.replace("www.", "");
			if (url.indexOf("http://") == -1) {
				url = "http://" + url;
			}
			Pattern p = Pattern
					.compile(
							"(?<=http://|\\.|www)[^.]*?\\.(com.cn|com|net.cn|net|org.cn|org|gov.cn|gov|cn|mobi|me|xyz|info|name|biz|cc|tv|asia|hk|政务|公益|网络|公司|中国)",
							Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(url);
			matcher.find();
			return matcher.group();
		} catch (Exception e) {
		    logger.error("拆分url获取根目录失败："+e.toString());
			return "";
		}
	}
	

	/**
	 * 
	 * 描述:格式化小数点位数
	 * 
	 * 作者：lxx	2015-11-9下午04:34:22
	 * @param size 		小数点后几位
	 * @param Obejct 	需要格式的数字
	 * @return String
	 */
	public static String formatDouble(int size ,Double Obejct){
		String temp = "#0";
		
		if(size > 0){
			String lenght = "";
			for (int i = 0; i < size ; i++) {
				lenght = (lenght + "0");
			}
			temp = temp + "."+lenght;
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat(temp);
		
		return df.format(Obejct);
	}

	/**
	 * 邮箱校验工具类
	 * 
	 * @param email
	 * @return true:验证通过 false:验证失败
	 */
	public static boolean checkEmail(String email) {
		String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
		return Pattern.matches(regex, email);
	}
	/**
	 * 
	 * 描述:取出末尾多余的0
	 * 
	 * 作者：lxx	2015-12-1上午09:16:04
	 * @param number
	 * @return
	 */
	public static String getPrettyNumber(String number) {
		String plainString = BigDecimal.valueOf(Double.parseDouble(number))  
        .stripTrailingZeros().toPlainString();
		if(plainString.equals("0.0")){
			plainString = "0";
		}
	    return plainString;  
	}
	
	public static void main(String args[]) {
		System.out.println(">>>>>>>>>>>>>>>>>");
		// byte[] a=getIpByteArrayFromString(args[0]);
		// for(int i=0;i< a.length;i++)
		// System.out.println(a[i]);
		// System.out.println(getIpStringFromBytes(a));
		
		int a = 187;
		int b = 0;
//		System.out.println(">>2>"+(3/6));
//		System.out.println(StringUtils.formatDouble(2,(double)a/(double)b));

		String temp = formatDouble(2, (double)b/a);
		System.out.println(temp+">>>"+(double)b/a);
//		String temp = hash("http://www.bjeit.gov.cn/zwgk/index.htm");
//		System.out.println(">>>>>>>>>>>:"+StringUtils.getPrettyNumber("3.01"));
	}
}
