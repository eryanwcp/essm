/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.springframework.util.Assert;

import com.eryansky.common.exception.SystemException;


import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * java工具类
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2011-12-30下午2:31:16
 */
public class SysUtils {
	private static final int DEF_DIV_SCALE = 2;
	public static final String FULL_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String CHS_DATEFORMAT = "yyyy年MM月dd日";
	public static final String SHORT_DATEFORMAT = "yyyy-MM-dd";
	public static final String SHORT_TIMEFORMAT = "HH:mm:ss";
	public static final int BUFFER_SIZE = 16 * 1024;


	/**
	 * 字符填充
	 * 
	 * @param text
	 * @param length
	 * @param c
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */

	public static String rightPad(String text, int length, byte c,
			String charsetName) throws Exception {
		if (null == text)
			text = "";
		byte[] array = new byte[length];
		byte[] reference = text.getBytes(charsetName);
		Arrays.fill(array, reference.length, length, c);
		System.arraycopy(reference, 0, array, 0, reference.length);
		return new String(array, charsetName);
	}

	public static String rightPad(String text, int length, byte c) {
		if (null == text)
			text = "";
		byte[] array = new byte[length];
		byte[] reference = text.getBytes();
		Arrays.fill(array, reference.length, length, c);
		System.arraycopy(reference, 0, array, 0, reference.length);
		return new String(array);
	}

	/**
	 * 整数填充
	 * 
	 * @param o
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String decimalPad(Integer o, int length) {
		if (null == o)
			return rightPad("", length, (byte) ' ');
		byte[] array = new byte[length];
		Arrays.fill(array, 0, length, (byte) '0');
		return new DecimalFormat(new String(array)).format(o);
	}

	/**
	 * 金额数填充
	 * 
	 * @param o
	 * @param length
	 * @param scale
	 * @return
	 * @throws Exception
	 */
	public static String decimalPad(Double o, int length, int scale)
			throws Exception {
		if (length < (2 + scale))
			throw new Exception("长度不应少于4!");
		if (null == o)
			return rightPad("", length, (byte) ' ');
		byte[] array = new byte[length];
		Arrays.fill(array, 0, length, (byte) '0');
		array[length - (1 + scale)] = (byte) '.';
		return new DecimalFormat(new String(array)).format(o);
	}

	/**
	 * 金额格式化
	 * 
	 * @param o
	 * @return
	 */
	public static String moneyFormat(Double o) {
		return new DecimalFormat("0.00").format(null != o ? o : 0);
	}

	public static int getNowYear() {
		return getYear(new Date());
	}

	public static int getYear(Date date) {
		return getDatePar(Calendar.YEAR, date);
	}

	public static int getNowMonth() {
		return getMonth(new Date());
	}

	public static int getMonth(Date date) {
		return getDatePar(Calendar.MONTH, date);
	}

	public static int getNowDay() {
		return getDay(new Date());
	}

	public static int getDay(Date date) {
		return getDatePar(Calendar.DAY_OF_MONTH, date);
	}

	public static int getDatePar(int par, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int result = calendar.get(par);
		switch (par) {
		case Calendar.MONTH:
			result++;
		}
		return result;
	}

	public static String getNowDateStr() {
		String result = dateFormat(getNowDate(), CHS_DATEFORMAT);
		Calendar c = Calendar.getInstance();
		for (int i = 0; i < 8; i++) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			@SuppressWarnings("unused")
			int e = c.get(Calendar.DAY_OF_WEEK);
		}
		result += "  星期";
		switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			return result + "日";
		case Calendar.MONDAY:
			return result + "一";
		case Calendar.TUESDAY:
			return result + "二";
		case Calendar.WEDNESDAY:
			return result + "三";
		case Calendar.THURSDAY:
			return result + "四";
		case Calendar.FRIDAY:
			return result + "五";
		case Calendar.SATURDAY:
			return result + "六";
		default:
			return result;
		}
	}


	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 压缩
	 * 
	 * @param in_str
	 *            字符串
	 * @return 字节流
	 */
	public static byte[] zip_Str(String in_str) {
		byte[] input = new byte[0];
		try {
			input = in_str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ArrayList<Byte> al = new ArrayList<Byte>();

		byte[] output;
		Deflater compresser = new Deflater();
		compresser.setInput(input);
		compresser.finish();
		for (; !compresser.finished();) {
			output = new byte[100];
			compresser.deflate(output);
			for (int i = 0; i < output.length; i++) {
				al.add(new Byte(output[i]));
			}
		}
		output = new byte[al.size()];
		for (int i = 0; i < al.size(); i++) {
			output[i] = (al.get(i)).byteValue();
		}
		return output;
	}

	/**
	 * 解压
	 * 
	 * @param in
	 *            字节流
	 * @return 字符串
	 */
	public static String unZip_Str(byte[] in) {
		Inflater decompresser = new Inflater();
		decompresser.setInput(in);
		ArrayList<Byte> al = new ArrayList<Byte>();
		byte[] result;
		int count = 0;
		for (; !decompresser.finished();) {
			result = new byte[100];
			try {
				count += decompresser.inflate(result);
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < result.length; i++) {
				al.add(new Byte(result[i]));
			}
		}
		result = new byte[al.size()];
		for (int i = 0; i < al.size(); i++) {
			result[i] = (al.get(i)).byteValue();
		}
		decompresser.end();

		try {
			return new String(result, 0, count, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 判断是否为INT
	 * 
	 * @param expression
	 * @return
	 */
	public static boolean isInt(Object expression) {
		if (expression != null) {
			try {
				Integer.parseInt(expression.toString());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判读是否为DOUBLE
	 * 
	 * @param expression
	 * @return
	 */
	public static boolean isDuble(Object expression) {
		if (expression != null) {
			try {
				Double.parseDouble(expression.toString());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断给定的字符串数组中的数据是不是都为数值型
	 * 
	 * @param array
	 *            字符串数组
	 * @return 是否成功
	 */
	public static boolean isIntArray(String[] array) {
		if (array == null) {
			return false;
		}
		if (array.length < 1) {
			return false;
		}
		for (String string : array) {
			if (!isInt(string)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 清除HTML标记
	 * 
	 * @param htmlText
	 *            带HTML标记的字符串
	 * @return 纯文本字符串
	 */
	public static String cleanHtmlTag(String htmlText) {
		String reg = "</?[a-z][a-z0-9]*[^<>]*>?";
		return htmlText.replaceAll(reg, "");
	}

	/**
	 * 返回系统当前时间
	 * 
	 * @return String 当前时间
	 */
	public static String getNowTime() {
		return dateFormat(getNowDate(), FULL_DATEFORMAT);
	}

	/**
	 * 格式化内容，只保留前n个字符，并进一步确认是否要在后面加上...
	 * 
	 * @param str
	 *            要处理的字符串
	 * @param num
	 *            保留的字数
	 * @param hasDot
	 *            是否显示...
	 * @return
	 */

	public static String format(String str, int num, boolean hasDot) {
		if (str == null)
			return "";
		else {
			if (str.getBytes().length < num * 2)
				return str;
			else {
				byte[] ss = str.getBytes();
				byte[] bs = new byte[num * 2];
				for (int i = 0; i < bs.length; i++) {
					bs[i] = ss[i];
				}
				String subStr = SysUtils.substring(str, num * 2);
				if (hasDot) {
					subStr = subStr + "...";
				}
				return subStr;
			}
		}
	}

	/**
	 * 取出指定长度字符串
	 * 
	 * @param s
	 *            字符串
	 * @param maxLength
	 *            长度
	 * @return
	 */
	public static String substring(String s, int maxLength) {
		if (s.getBytes().length <= maxLength)
			return s;
		int i = 0;
		for (int k = 0; k < maxLength && i < s.length(); i++, k++) {
			if (s.charAt(i) > '一') {
				k++;
			}
		}
		if (i < s.length()) {
			s = s.substring(0, i);
		}
		return s;
	}

	/**
	 * 转换对象为字符串
	 * 
	 * @param s
	 *            对象
	 * @return 如果为空则返回""
	 */
	public static String null2String(Object s) {
		return null2String(s, "");
	}

	/**
	 * 随机生成指定位数且不重复的字符串.去除了部分容易混淆的字符，如1和l，o和0等，
	 * <p/>
	 * 随机范围1-9 a-z A-Z
	 * 
	 * @param length
	 *            指定字符串长度
	 * @return 返回指定位数且不重复的字符串
	 */
	public static String getRandomString(int length) {
		StringBuffer bu = new StringBuffer();
		String[] arr = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c",
				"d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
				"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		Random random = new Random();
		while (bu.length() < length) {
			String temp = arr[random.nextInt(57)];
			if (bu.indexOf(temp) == -1) {
				bu.append(temp);
			}
		}
		return bu.toString();
	}

	/**
	 * 获取某个范围内的随机整数
	 * 
	 * @param sek
	 *            随机种子
	 * @param min
	 *            最小范围
	 * @param max
	 *            最大范围
	 * @return 整数
	 */
	public static int getRandomInt(int sek, int min, int max) {

		Random random = new Random();

		int temp = 0;

		do {
			temp = random.nextInt(sek);
		} while (temp < min || temp > max);

		return temp;
	}

	public static String null2String(Object s, String def) {
		Assert.notNull(def, "def不能为空");
		return s == null ? "" : s.toString().trim();
	}

	/**
	 * 转换对象为INT
	 * 
	 * @param s
	 *            对象
	 * @return 如果为空则返回-1
	 */
	public static int null2Int(Object s) {
		return null2Int(s, -1);
	}

	/**
	 * 转换对象为INT
	 * 
	 * @param object
	 *            对象
	 * @param def
	 *            失败默认值
	 * @return INT值
	 */
	public static int null2Int(Object object, int def) {

		if (object != null) {
			try {
				return Integer.parseInt(object.toString());
			} catch (Exception e) {
			}
		}
		return def;
	}

	/**
	 * 转换对象为Long
	 * 
	 * @param object
	 *            对象
	 * @param def
	 *            失败默认值
	 * @return Long值
	 */
	public static Long null2Long(Object object, Long def) {

		if (object != null) {
			try {
				return Long.parseLong(object.toString());
			} catch (Exception e) {
			}
		}
		return def;
	}

	/**
	 * 转换对象为Float
	 * 
	 * @param s
	 *            对象
	 * @return 如果为空则返回-1
	 */
	public static float null2Float(Object s) {
		return null2Float(s, -1);
	}

	/**
	 * 转换对象为Float
	 * 
	 * @param s
	 *            对象
	 * @return 如果为空则返回-1
	 */
	public static float null2Float(Object s, float defValue) {
		if (s != null) {
			try {
				return Float.parseFloat(s.toString());
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * 转换对象为Float
	 * 
	 * @param s
	 *            对象
	 * @return 如果为空则返回-1
	 */
	public static double null2Double(Object s, double defValue) {
		if (s != null) {
			try {
				return Double.parseDouble(s.toString());
			} catch (Exception e) {
			}
		}
		return defValue;
	}

	/**
	 * object型转换为bool型
	 * 
	 * @param expression
	 *            要转换的对象
	 * @param defValue
	 *            缺省值
	 * @return 转换后的bool类型结果
	 */
	public static boolean null2Boolean(Object expression, boolean defValue) {
		try {
			return Boolean.parseBoolean(null2String(expression));
		} catch (Exception e) {
			return defValue;
		}
	}

	/**
	 * 按照指定方式格式化时间
	 * 
	 * @param date
	 *            时间
	 * @param type
	 *            格式
	 * @return
	 */
	public static String dateFormat(Date date, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		return sdf.format(date);
	}

	public static String dateFormatTally() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return dateFormat(calendar.getTime(), "yyyyMMdd");
	}

	public static String dateFormatTallyStr() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return dateFormat(calendar.getTime(), "yyyy-MM-dd");
	}

	public static String dateFormatAccountMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		return dateFormat(calendar.getTime(), "yyyyMM");
	}

	public static String dateFormatTallyCN() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return dateFormat(calendar.getTime(), "yyyy年MM月dd日");
	}

	/**
	 * 按照指定方式格式化时间
	 * 
	 * @param type
	 *            格式
	 * @return
	 */
	public static String dateFormat(String type) {
		return dateFormat(getNowDate(), type);
	}

	public static Date getNowDate(int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(field, amount);
		return calendar.getTime();
	}

	public static String getBeiJingDateTime() {
		return SysUtils.dateFormat(SysUtils.getNowDate(Calendar.HOUR_OF_DAY, 8),
				SysUtils.FULL_DATEFORMAT);
	}

	public static String getBeiJingDate() {
		return SysUtils.dateFormat(SysUtils.getNowDate(Calendar.HOUR_OF_DAY, 8),
				SysUtils.SHORT_DATEFORMAT);
	}

	public static long getNowDateBeginLong(int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(field, amount);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}

	public static long getNowDateEndLong(int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(field, amount);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}

	/**
	 * 返回系统当前时间的date对象
	 * 
	 * @return
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 返回系统当前日期
	 * 
	 * @return String 日期
	 */
	public static String getNowShortDate() {
		return dateFormat(getNowDate(), SHORT_DATEFORMAT);
	}

	/**
	 * 返回系统当前时间
	 * 
	 * @return 时间
	 */
	public static String getNowShortTime() {
		return dateFormat(getNowDate(), SHORT_TIMEFORMAT);
	}

	/**
	 * 判断时间格式
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static boolean checkDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			df.parse(date);
		} catch (Exception e) {
			// 如果不能转换,肯定是错误格式
			return false;
		}
		return true;
	}

	/**
	 * 计算时间差 (时间单位,开始时间,结束时间)<br>
	 * s - 秒,m - 分,h - 时,d - 天 调用方法
	 * howLong("h","2007-08-09 10:22:26","2007-08-09 20:21:30") ///9小时56分 返回9小时
	 * 
	 * @throws java.text.ParseException
	 */
	public static long howLong(String unit, String time1, String time2)
			throws ParseException {
		// 时间单位(如：不足1天(24小时) 则返回0)，开始时间，结束时间
		Date date1 = new SimpleDateFormat(FULL_DATEFORMAT).parse(time1);
		Date date2 = new SimpleDateFormat(FULL_DATEFORMAT).parse(time2);
		long ltime = date1.getTime() - date2.getTime() < 0 ? date2.getTime()
				- date1.getTime() : date1.getTime() - date2.getTime();
		if (unit.equals("s")) {
			return ltime / 1000;// 返回秒
		} else if (unit.equals("m")) {
			return ltime / 60000;// 返回分钟
		} else if (unit.equals("h")) {
			return ltime / 3600000;// 返回小时
		} else if (unit.equals("d")) {
			return ltime / 86400000;// 返回天数
		} else {
			return 0;
		}
	}

	/**
	 * 转换字符串为date类型
	 * 
	 * @param time
	 * @param type
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Date parseDate(String time, String type)
			throws ParseException {
		return new SimpleDateFormat(type).parse(time);
	}

	/**
	 * 转换字符串为date类型
	 * 
	 * @param time
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Date parseDate(String time) throws ParseException {
		return new SimpleDateFormat(FULL_DATEFORMAT).parse(time);
	}

	/**
	 * 返回相差的时间
	 * 
	 * @param time
	 *            时间
	 * @param tim
	 *            时间
	 * @param type
	 *            类型 'h' - 小时,'m' - 分,'s' - 秒
	 * @return
	 * @throws java.text.ParseException
	 */
	public static int strDateDiffTimes(String time, int tim, char type)
			throws ParseException {
		if (null2String(time).equals("")) {
			return 1;
		}
		long diff = 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat(FULL_DATEFORMAT).parse(time));
		switch (type) {
		case 'h':
			calendar.add(Calendar.HOUR_OF_DAY, tim);
			break;
		case 'm':
			calendar.add(Calendar.MINUTE, tim);
			break;
		case 's':
			calendar.add(Calendar.SECOND, tim);
			break;
		}
		Date date = calendar.getTime();
		long ltime = getNowDate().getTime() - date.getTime();
		switch (type) {
		case 'h':
			diff = ltime / 3600000;// 返回小时
			break;
		case 'm':
			diff = ltime / 60000;// 返回分
			break;
		case 's':
			diff = ltime / 1000;// 返回秒
			break;
		}
		if (diff > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else if (diff < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return SysUtils.null2Int(diff);
	}

	/**
	 * 返回相差的小时数
	 * 
	 * @param time
	 *            时间
	 * @param hours
	 *            小时
	 * @return
	 * @throws java.text.ParseException
	 */
	public static int strDateDiffHours(String time, int hours)
			throws ParseException {
		return strDateDiffTimes(time, hours, 'h');
	}

	/**
	 * 返回相差的分钟数
	 * 
	 * @param time
	 *            时间
	 * @param minutes
	 * @return
	 * @throws java.text.ParseException
	 */
	public static int strDateDiffMinutes(String time, int minutes)
			throws ParseException {
		return strDateDiffTimes(time, minutes, 'm');
	}

	/**
	 * 返回相差的秒数
	 * 
	 * @param time
	 *            时间
	 * @param sec
	 * @return
	 * @throws java.text.ParseException
	 */
	public static int strDateDiffSeconds(String time, int sec)
			throws ParseException {
		return strDateDiffTimes(time, sec, 's');
	}

	/**
	 * 检测邮件格式
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		Pattern pattern = Pattern.compile("\\w+(\\.\\w+)*@\\w+(\\.\\w+)+");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches())
			return true;
		return false;
	}

	/**
	 * 转换字符串为指定编码格式
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	public static String stringFormat(String s, String encod, String encoding) {
		try {
			return new String(s.getBytes(encod), encoding);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	public static String stringFormat(String s) {
		return stringFormat(s, "ISO8859-1", "UTF-8");
	}

	/**
	 * URL编码指定字符串
	 * 
	 * @param s
	 *            字符串
	 * @return URL编码结果
	 */
	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * URL解码指定字符串
	 * 
	 * @param s
	 *            字符串
	 * @return 解码结果
	 */
	public static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * 返回指定IP是否在指定的IP数组所限定的范围内<br>
	 * IP数组内的IP地址可以使用*表示该IP段任意, 例如192.168.1.*
	 * 
	 * @param ip
	 * @param ipArry
	 * @return
	 */
	public static boolean inIPArray(String ip, String[] ipArry) {
		String[] userip = ip.split("\\.");
		for (int ipIndex = 0; ipIndex < ipArry.length; ipIndex++) {
			String[] tempip = ipArry[ipIndex].split("\\.");
			int r = 0;
			for (int i = 0; i < tempip.length; i++) {
				if (tempip[i].equals("*")) {
					return true;
				}
				if (userip.length > i) {
					if (tempip[i].equals(userip[i])) {
						r++;
					} else {
						break;
					}
				} else {
					break;
				}
			}// end for
			if (r == 4) {
				return true;
			}
		}// end for
		return false;
	}

	/**
	 * 判断指定字符串在指定字符串数组中的位置
	 * 
	 * @param strSearch
	 *            字符串
	 * @param stringArray
	 *            字符串数组
	 * @param caseInsensetive
	 *            是否不区分大小写, true为不区分, false为区分
	 * @return 字符串在指定字符串数组中的位置, 如不存在则返回-1
	 */
	public static int getInArrayID(String strSearch, String[] stringArray,
			boolean caseInsensetive) {
		for (int i = 0; i < stringArray.length; i++) {
			if (caseInsensetive) {
				if (strSearch.toLowerCase()
						.equals(stringArray[i].toLowerCase())) {
					return i;
				}
			} else {
				if (strSearch.equals(stringArray[i])) {
					return i;
				}
			}

		}
		return -1;
	}

	/**
	 * 判断指定字符串在指定字符串数组中的位置
	 * 
	 * @param strSearch
	 *            字符串
	 * @param stringArray
	 *            字符串数组
	 * @return 字符串在指定字符串数组中的位置, 如不存在则返回-1
	 */
	public static int getInArrayID(String strSearch, String[] stringArray) {
		return getInArrayID(strSearch, stringArray, true);
	}

	/**
	 * 判断指定字符串是否属于指定字符串数组中的一个元素
	 * 
	 * @param str
	 *            字符串
	 * @param stringArray
	 *            字符串数组
	 * @param caseInsensetive
	 *            是否不区分大小写, true为不区分, false为区分
	 * @return 判断结果
	 */
	public static boolean inArray(String str, String[] stringArray,
			boolean caseInsensetive) {
		return getInArrayID(str, stringArray, caseInsensetive) >= 0;
	}

	/**
	 * 判断指定字符串是否属于指定字符串数组中的一个元素
	 * 
	 * @param str
	 *            字符串
	 * @param stringArray
	 *            字符串数组
	 * @return 判断结果
	 */
	public static boolean inArray(String str, String[] stringArray) {
		return inArray(str, stringArray, false);
	}

	/**
	 * 判断指定字符串是否属于指定字符串数组中的一个元素
	 * 
	 * @param str
	 *            字符串
	 * @param stringArray
	 *            内部以指定符号分割单词的字符串
	 * @param strsplit
	 *            分割字符串
	 * @param caseInsensetive
	 *            是否不区分大小写, true为不区分, false为区分
	 * @return 判断结果
	 */
	public static boolean inArray(String str, String stringArray,
			String strsplit, boolean caseInsensetive) {
		return inArray(str, stringArray.split(strsplit), caseInsensetive);
	}

	/**
	 * 判断指定字符串是否属于指定字符串数组中的一个元素
	 * 
	 * @param str
	 *            字符串
	 * @param stringArray
	 *            内部以指定符号分割单词的字符串
	 * @param strsplit
	 *            分割字符串
	 * @return 判断结果
	 */
	public static boolean inArray(String str, String stringArray,
			String strsplit) {
		return inArray(str, stringArray.split(strsplit), false);
	}

	/**
	 * 判断指定字符串是否属于指定字符串数组中的一个元素
	 * 
	 * @param str
	 *            字符串
	 * @param stringArray
	 *            内部以逗号分割单词的字符串
	 * @return 判断结果
	 */
	public static boolean inArray(String str, String stringArray) {
		return inArray(str, stringArray.split(","), false);
	}

	/**
	 * 检测是否有Sql危险字符
	 * 
	 * @param str
	 *            要判断字符串
	 * @return 判断结果
	 */
	public static boolean isSafeSqlString(String str) {
		Pattern pattern = Pattern
				.compile("[-|;|,|/|(|)|\\[|\\]|\\}|\\{|%|@|\\*|!|\\']");
		return !pattern.matcher(str).find();
	}

	/**
	 * 检测是否有危险的可能用于链接的字符串
	 * 
	 * @param str
	 *            要判断字符串
	 * @return 判断结果
	 */
	public static boolean isSafeUserInfoString(String str) {
		String es = "^\\s*$|^c:\\con\\con$|[%,\\*\\\\s\\t\\<\\>\\&]|游客|^Guest";
		System.out.println("es = " + es);
		Pattern pattern = Pattern.compile(es);
		return !pattern.matcher(str).find();
	}

	/**
	 * 返回文件是否存在
	 * 
	 * @param filePath
	 *            文件名
	 * @return 是否存在
	 */
	public static boolean fileExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 生成指定数量的html空格符号
	 * 
	 * @param spacesCount
	 * @return
	 */
	public static String getSpacesString(int spacesCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spacesCount; i++) {
			sb.append("&nbsp;&nbsp;");
		}
		return sb.toString();
	}

	/**
	 * 将字符串转换为Color
	 * 
	 * @param color
	 * @return
	 */
	public static Color toColor(String color) {
		int red, green, blue = 0;
		char[] rgb;
		color = "#" + color.trim();
		color = color.toLowerCase().replaceAll("[g-zG-Z]", "");
		switch (color.length()) {
		case 3:
			rgb = color.toCharArray();
			red = SysUtils.null2Int(rgb[0] + "" + rgb[0], 16);
			green = SysUtils.null2Int(rgb[1] + "" + rgb[1], 16);
			blue = SysUtils.null2Int(rgb[2] + "" + rgb[2], 16);
			return new Color(red, green, blue);
		case 6:
			rgb = color.toCharArray();
			red = SysUtils.null2Int(rgb[0] + "" + rgb[1], 16);
			green = SysUtils.null2Int(rgb[2] + "" + rgb[3], 16);
			blue = SysUtils.null2Int(rgb[4] + "" + rgb[5], 16);
			return new Color(red, green, blue);
		default:
			return Color.decode(color);
		}
	}

	/**
	 * 为脚本替换特殊字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceStrToScript(String str) {
		str = str.replace("\\", "\\\\");
		str = str.replace("'", "\\'");
		str = str.replace("\"", "\\\"");
		return str;
	}

	/**
	 * 判断文件名是否为浏览器可以直接显示的图片文件名
	 * 
	 * @param filename
	 *            文件名
	 * @return 是否可以直接显示
	 */
	public static boolean isImgFilename(String filename) {
		filename = filename.trim();
		if (filename.endsWith(".") || filename.indexOf(".") == -1) {
			return false;
		}
		String extname = filename.substring(filename.lastIndexOf(".") + 1)
				.toLowerCase();
		return (extname.equals("jpg") || extname.equals("jpeg")
				|| extname.equals("png") || extname.equals("bmp") || extname
					.equals("gif"));
	}

	/**
	 * 检测是否为IP
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIPSect(String ip) {
		return Pattern
				.compile(
						"^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){2}((2[0-4]\\d|25[0-5]|[01]?\\d\\d?|\\*)\\.)(2[0-4]\\d|25[0-5]|[01]?\\d\\d?|\\*)$")
				.matcher(ip).find();

	}

	/**
	 * 判断是否为时间
	 * 
	 * @param timeval
	 * @return
	 */
	public static boolean isTime(String timeval) {
		return Pattern
				.compile(
						"^((([0-1]?[0-9])|(2[0-3])):([0-5]?[0-9])(:[0-5]?[0-9])?)$")
				.matcher(timeval).find();
	}

	public static boolean isRuleTip(Map<String, String> newHash,
			String ruletype, Map<Integer, String> keyMap) {
		keyMap.put(0, "");
		ruletype = ruletype.trim().toLowerCase();
		Set<Map.Entry<String, String>> entrys = newHash.entrySet();
		for (Map.Entry<String, String> entry : entrys) {
			String[] single = entry.getValue().split("\r\n");
			for (String str : single) {
				try {
					if (!str.equals("")) {
						if (ruletype.equals("email")) {
							if (!checkEmail(str)) {
								throw new Exception();
							}
						} else if (ruletype.equals("ip")) {
							if (!isIPSect(str)) {
								throw new Exception();
							}
						} else if (ruletype.equals("timesect")) {
							String[] splitetime = str.split("-");
							if (!isTime(splitetime[1])
									|| !isTime(splitetime[0])) {
								throw new Exception();
							}
						}
					}
				} catch (Exception e) {
					keyMap.put(0, entry.getKey());
					return false;
				}
			}
		}

		return true;

	}

	public static boolean mkdir(String mkdirName) {
		try {
			File dirFile = new File(mkdirName);
			boolean bFile = dirFile.exists();
			if (bFile) {
				return true;
			} else {
				bFile = dirFile.mkdir();
				return bFile;
			}
		} catch (Exception err) {
			return false;
		}
	}

	/**
	 * 获取置顶邮件的服务器地址
	 * 
	 * @param strEmail
	 * @return
	 */
	public static String getEmailHostName(String strEmail) {
		if (strEmail.indexOf("@") < 0) {
			return "";
		}
		return strEmail.substring(strEmail.lastIndexOf("@")).toLowerCase();
	}

	/**
	 * HTML标签过滤
	 * 
	 * @param value
	 * @return
	 */
	public static String dhtmlspecialchars(String value) {
		if (matches(value, "(&|\"|<|>)")) {
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
		}
		return value;
	}

	/**
	 * HTML标签反
	 * 
	 * @param value
	 * @return
	 */
	public static String htmlspecialchars(String value) {
		if (matches(value, "(&amp;|&quot;|&lt;|&gt;)")) {
			value = value.replaceAll("&amp;", "&");
			value = value.replaceAll("&quot;", "\"");
			value = value.replaceAll("&lt;", "<");
			value = value.replaceAll("&gt;", ">");
		}
		return value;
	}

	public static boolean matches(String content, String regex) {
		boolean flag = false;
		try {
			flag = new Perl5Matcher().contains(content,
					new Perl5Compiler().compile(regex));
		} catch (MalformedPatternException e) {
		}
		return flag;
	}

	public static String combinatorial(String str) {
		if (null == str)
			return "";
		str = str.trim().replaceAll(" ", ",");
		char ch = ' ';
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ',') {
				if (ch != ',')
					result.append(str.charAt(i));
			} else {
				result.append(str.charAt(i));
			}
			ch = str.charAt(i);
		}
		return result.toString();
	}

	public static String subStringCN(String s, int len) {
		if (s.toCharArray().length <= len)
			return s;
		int i = 0;
		for (float k = 0; k < len && i < s.length(); i++) {
			if (s.charAt(i) > '一') {
				k += 1;
			} else {
				k += 0.5;
			}
		}
		if (i < s.length()) {
			s = s.substring(0, i - 1);
		}
		return s;

		// char[] chars = str.toCharArray();
		// StringBuffer result = new StringBuffer();
		// if (chars.length > len) {
		// for (int i = 0; i < len; i++) {
		// result.append(chars[i]);
		// }
		// result.append("...");
		// return result.toString();
		// } else {
		// return str;
		// }

	}

	public static void copy(File src, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			int byteread = 0;
			try {
				in = new BufferedInputStream(new FileInputStream(src),
						BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}

				//
				// byte[] buffer = new byte[bs];
				// while (in.read(buffer) > 0) {
				// out.write(buffer);
				// buffer = new byte[BUFFER_SIZE];
				// }
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String decode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

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

	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		StringTokenizer st = new StringTokenizer(ip, ".");
		try {
			ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
		} catch (Exception e) {
		}
		return ret;
	}

	public static String getString(String s, String srcEncoding,
			String destEncoding) {
		try {
			return new String(s.getBytes(srcEncoding), destEncoding);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	public static String getString(byte[] b, String encoding) {
		try {
			return new String(b, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}

	public static String getString(byte[] b, int offset, int len,
			String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b, offset, len);
		}
	}

	public static String composeString(List<String> strs, String fix) {
		if (null == strs || null == fix)
			return "";
		StringBuffer result = new StringBuffer();
		for (String str : strs) {
			result.append(str).append(fix);
		}
		return result.toString();
	}

	public static String makeFileName(String rootpath, String dir,
			String fileName) {
		String path = fileName;
		File dirFile = new File(rootpath + dir + path);
		int i = 1;
		while (dirFile.exists()) {
			if (fileName.lastIndexOf(".") == 0) {
				path = "[" + i++ + "]" + fileName;
			} else if (fileName.lastIndexOf(".") == -1) {
				path = fileName + "[" + i++ + "]";
			} else {
				path = fileName.substring(0, fileName.lastIndexOf(".")) + "["
						+ i++ + "]"
						+ fileName.substring(fileName.lastIndexOf("."));
			}
			dirFile = new File(rootpath + dir + path);
		}
		return dir + path; // To change body of created methods use File |
							// Settings | File Templates.
	}

	public static Date getDay0(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getDay2359(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getMonth1D() {
		return getMonth1D(new Date());
	}

	public static Date getMonth1D(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static Date getMonthLastD() {
		return getMonthLastD(new Date());
	}

	public static Date getMonthLastD(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}

	public static String[] toStringArray(String str) {
		char[] chrs = str.toCharArray();
		String[] result = new String[chrs.length];
		for (int i = 0; i < chrs.length; i++) {
			char[] tmp = new char[] { chrs[i] };
			result[i] = new String(tmp);
		}
		return result;
	}

	/**
	 * 134-、135-、136-、137-、138-、139-、150-、151-、152-、158-、159-、147-、187-、188-
	 * 182-
	 * <p/>
	 * 157 -
	 * 
	 * @param mobilephone
	 * @return
	 */
	public static boolean checkMobilephoneByCMCC(String mobilephone) {
		String cmcc = new String(
				"134,135,136,137,138,139,150,151,152,157,158,159,188,182,187,147,");
		try {
			if (mobilephone.length() != 11)
				return false;
			String sub = mobilephone.substring(0, 3) + ",";
			if (cmcc.indexOf(sub) == -1)
				return false;
			@SuppressWarnings("unused")
			float m = Float.parseFloat(mobilephone);
			return isIntArray(toStringArray(mobilephone));
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unused")
	public static boolean checkMobilephone(String mobilephone) {
		String cmcc = new String("13,15,18,");
		try {
			if (mobilephone.length() != 11)
				return false;
			String sub = mobilephone.substring(0, 2) + ",";
			if (cmcc.indexOf(sub) == -1)
				return false;
			float m = Float.parseFloat(mobilephone);
			return isIntArray(toStringArray(mobilephone));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 得到账务日期
	 * 
	 * @param hour
	 *            超过算第二天
	 * @return
	 */
	public static String getAccDate(int hour) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) >= hour) {
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return dateFormat(calendar.getTime(), "yyyyMMdd");
	}

	public static String getAccDateShort(int hour) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) >= hour) {
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return dateFormat(calendar.getTime(), "yyMMdd");
	}

	public static String fillzero(String str, int len) {
		if (str == null) {
			return "";
		}
		if (str.length() > len) {
			Throwable throwable = new Throwable("数值长度不正确,请检查!");
			throwable.printStackTrace();
		}

		while (str.length() < len) {
			str = "0" + str;
		}
		return str;
	}

	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	public static String bcdToAsc(byte[] bcdBytes) {
		char[] hex = new char[] { 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer temp = new StringBuffer(bcdBytes.length * 2);

		for (int i = 0; i < bcdBytes.length; i++) {
			byte hi = (byte) ((bcdBytes[i] & 0xf0) >>> 4);
			if (hi >= 0xA) {
				temp.append(hex[hi - 0xA]);
			} else {
				temp.append(hi);
			}

			byte lo = (byte) (bcdBytes[i] & 0x0f);
			if (lo >= 0xA) {
				temp.append(hex[lo - 0xA]);
			} else {
				temp.append(lo);
			}
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * json特殊字符处理.
	 * 
	 * @param str
	 *            json数据部分
	 * @return
	 */
	public static String jsonStrConvert(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Blob转byte[]
	 * 
	 * @param blob
	 * @return
	 * @author 尔演&Eryan eryanwcp@gmail.com
	 */
	public static byte[] blobToBytes(Blob blob) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(blob.getBinaryStream());
			byte[] bytes = new byte[(int) blob.length()];
			int len = bytes.length;
			int offset = 0;
			int read = 0;

			while (offset < len
					&& (read = is.read(bytes, offset, len - offset)) >= 0) {
				offset += read;
			}
			return bytes;
		} catch (Exception e) {
			return null;
		} finally {
			try {
                if(is != null){
				    is.close();
				    is = null;
                }
			} catch (IOException e) {
				return null;
			}
		}
	}

    /**
     * 将十六进制的字符串解码
     * @param hex
     * @return
     */
    public static final byte[] decodeHex(String hex) {
        char chars[] = hex.toCharArray();
        byte bytes[] = new byte[chars.length / 2];
        int byteCount = 0;
        for(int i = 0; i < chars.length; i += 2){
            int newByte = 0;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte)newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * 将字节数组加密成十六进制
     * @param bytes
     * @return
     */
    public static final String encodeHex(byte bytes[]) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for(int i = 0; i < bytes.length; i++) {
            if((bytes[i] & 0xff) < 16)
                buf.append("0");
            buf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }
 
    private static final byte hexCharToByte(char ch)  {
        switch(ch) {
        case 48: // '0'
            return 0;
        case 49: // '1'
            return 1;
        case 50: // '2'
            return 2;
        case 51: // '3'
            return 3;
        case 52: // '4'
            return 4;
        case 53: // '5'
            return 5;
        case 54: // '6'
            return 6;
        case 55: // '7'
            return 7;
        case 56: // '8'
            return 8;
        case 57: // '9'
            return 9;
        case 97: // 'a'
            return 10;
        case 98: // 'b'
            return 11;
        case 99: // 'c'
            return 12;
        case 100: // 'd'
            return 13;
        case 101: // 'e'
            return 14;
        case 102: // 'f'
            return 15;
        case 58: // ':'
        case 59: // ';'
        case 60: // '<'
        case 61: // '='
        case 62: // '>'
        case 63: // '?'
        case 64: // '@'
        case 65: // 'A'
        case 66: // 'B'
        case 67: // 'C'
        case 68: // 'D'
        case 69: // 'E'
        case 70: // 'F'
        case 71: // 'G'
        case 72: // 'H'
        case 73: // 'I'
        case 74: // 'J'
        case 75: // 'K'
        case 76: // 'L'
        case 77: // 'M'
        case 78: // 'N'
        case 79: // 'O'
        case 80: // 'P'
        case 81: // 'Q'
        case 82: // 'R'
        case 83: // 'S'
        case 84: // 'T'
        case 85: // 'U'
        case 86: // 'V'
        case 87: // 'W'
        case 88: // 'X'
        case 89: // 'Y'
        case 90: // 'Z'
        case 91: // '['
        case 92: // '\\'
        case 93: // ']'
        case 94: // '^'
        case 95: // '_'
        case 96: // '`'
        default:
            return 0;
        }
    }
}


