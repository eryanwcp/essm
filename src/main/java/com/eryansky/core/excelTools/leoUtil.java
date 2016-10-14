package com.eryansky.core.excelTools;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-31 20:36
 */
public class leoUtil {

	/**
	 * 验证字符串
	 * @param regex
	 * @param value
	 * @return
	 */
	public static boolean regex(String regex, String value) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	/**
	 * @description 验证日期
	 * @param regex
	 * @param date
	 * @return
	 */
	public static boolean regex(String regex, Date date) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) date);
		return matcher.matches();
	}

	/**
	 * @description 根据索引获得excel对应的列
	 * @param index
	 * @return
	 */
	public static String numToAbc(int index) {
		String[] abcString = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
				"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
				"Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK",
				"AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW",
				"AX", "AY", "AZ" };
		String result = null;
		if (index > 52) {
			result = "error";
		} else {
			result = abcString[index];
		}
		return result;
	}

	/**
	 * 将excel里的值转换成字符串
	 * 
	 * @param cell
	 * @return
	 */
	public static String ConvertCellToStr(Cell cell) {
		String cellStr = null;
		if (cell!= null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cellStr = cell.getStringCellValue().toString();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				// 得到Boolean对象的方法
				cellStr = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// 先看是否是日期格式
				if (DateUtil.isCellDateFormatted(cell)) {
					// 读取日期格式 2013/2/28
					cellStr = dateFormate(cell.getDateCellValue());
				} else {
					// 读取数字,如果为整数则输出整数，为小数则输出小数。
					cellStr = getValue(String.valueOf(cell.getNumericCellValue()));
				}
				break;
			case Cell.CELL_TYPE_FORMULA:
				// 读取公式
				cellStr = cell.getCellFormula().toString();
				break;
			}
		} else {
			return cellStr;
		}
		return cellStr;
	}
	
	
	public static Double ConvertCellToDouble(Cell cell) {
		Double cellDouble=null;
		if (cell!= null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cellDouble =Double.valueOf(cell.getStringCellValue().toString());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// 先看是否是日期格式
				if (DateUtil.isCellDateFormatted(cell)) {
					// 读取日期格式 2013/2/28
					cellDouble = Double.valueOf(dateFormate(cell.getDateCellValue()));
				} else {
					// 读取数字,如果为整数则输出整数，为小数则输出小数。
					cellDouble = Double.valueOf(getValue(String.valueOf(cell.getNumericCellValue())));
				}
				break;
			}
		} 
		return cellDouble;
	}

	/**
	 * @description cell转换成date()
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date ConvertCellToDate(Cell cell) {
		Date cellDate = new Date();
		if (DateUtil.isCellDateFormatted(cell)) {
			// 读取日期格式
			cellDate = cell.getDateCellValue();
		} else {
			cellDate = new Date(String.valueOf(cell.getNumericCellValue()));
		}
		return cellDate;
	}

	/**
	 * @description 将科学计数法还原，并去除多余的0,保留两位有效数字。
	 * @param number
	 * @return
	 */
	public static String getValue(String number) {
		return BigDecimal.valueOf(Double.parseDouble(number))
				.setScale(2, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros()
				.toPlainString();
	}

	/**
	 * @description 先根据suffix判断是否为excel文件，再获取文件头，读取前3个字节判断文件类型。
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean isExcel(File file) throws FileNotFoundException {
		Boolean bool = false;
		// 不能通过后缀来判断，因为上传得到的是tmp文件。
		// String suffix =
		// file.getName().substring(file.getName().lastIndexOf(".") + 1);

		FileInputStream fis = new FileInputStream(file);
		byte[] b = new byte[3];
		try {
			fis.read(b, 0, b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bytesToHexString(b).equalsIgnoreCase("d0cf11")
				|| bytesToHexString(b).equalsIgnoreCase("504b03")) {
			bool = true;
		}
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bool;
	}

	/**
	 * @description 获取文件头，读取前几个字节。
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * @description 将date格式化
	 * @param date
	 * @return String
	 */
	public static String dateFormate(Date date) {
		return new SimpleDateFormat("yyyy/MM/dd").format(date);
	}

	/**
	 * @description 格式化decimal数据
	 * @param num
	 * @return
	 */
	public static String numberFormate(String num) {
		NumberFormat nf = NumberFormat.getInstance();
		// 小数点最大两位
		nf.setMaximumFractionDigits(2);
		return nf.format(num);
	}

	/**
	 * @description 四舍五入，保留两位有效数字。
	 * @param num
	 * @return
	 */
	public static Double round(Double num) {
		int temp = (int) (num * 100 + 0.5);
		num = (double) temp / 100;
		return num;
	}

	/**
	 * @description 计算字符串表达式的值 注意 010 会当成八进制的8 0x10会当成十六进制的16 不支持 \ ^ 支持 % ( ) + - * /
	 * @param ex
	 * @return
	 */
	public static Double calculateExpression(String ex) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		try {
			Double d = (Double) engine.eval(ex);
			return d;
		} catch (ScriptException e) {
			return null;
		}
	}
	/**
	 * @description 将科学计数法还原
	 * @param account
	 * @return
	 * 
	 *         public static String getValue(String account) {
	 * 
	 *         String regex = "^((\\d+.?\\d+)[Ee]{1}(\\d+))$";
	 * 
	 *         Pattern pattern = Pattern.compile(regex);
	 * 
	 *         java.util.regex.Matcher m = pattern.matcher(account); boolean b =
	 *         m.matches(); if (b) { DecimalFormat df = new DecimalFormat("#"); account =
	 *         df.format(Double.parseDouble(account)); } return account;
	 * 
	 *         }
	 */

}
