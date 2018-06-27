/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.excelTools;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-31 20:36
 */
public class ExcelUtils {

	/**
	 * JavaBean转Map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!StringUtils.equals(name, "class")) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * 创建普通表头
	 * @param list 表头名称列表
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(List<String> list){
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		for(String title : list){
			TableColumn tc = new TableColumn();
			tc.setDisplay(title);
			headMeta.addColumn(tc);
		}
		return headMeta;
	}

	/**
	 * 创建普通表头
	 * @param titls 表头名称数组
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(String[] titls){
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		for(String title : titls){
			TableColumn tc = new TableColumn();
			tc.setDisplay(title);
			tc.setGrouped(true);
			headMeta.addColumn(tc);
		}
		return headMeta;
	}

	/**
	 * 创建普通表头
	 * @param titls 表头名称数组
	 * @param spanCount 需要行合并的列数。
	 * 		由第一列数据开始到指定列依次从左到右进行合并操作。
	 * 		如该值大于表头名称数组，则为全列合并
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(String[] titls,int spanCount){
		if(spanCount>titls.length)
			spanCount = titls.length;
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		for(int i=0;i<titls.length;i++){
			TableColumn tc = new TableColumn();
			tc.setDisplay(titls[i]);
			if(i<spanCount)
				tc.setGrouped(true);
			headMeta.addColumn(tc);
		}
		return headMeta;
	}

	/**
	 * 创建合并表头
	 * @param parents 父表头数组
	 * @param children 子表头数组
	 * @return
	 */
	public static TableHeaderMetaData createTableHeader(String[] parents,String[][] children){
		TableHeaderMetaData headMeta = new TableHeaderMetaData();
		TableColumn parentColumn = null;
		TableColumn sonColumn = null;
		for (int i = 0; i < parents.length; i++) {
			parentColumn = new TableColumn();
			parentColumn.setDisplay(parents[i]);
			if (children != null && children[i] != null) {
				for (int j = 0; j < children[i].length; j++) {
					sonColumn = new TableColumn();
					sonColumn.setDisplay(children[i][j]);
					parentColumn.addChild(sonColumn);
				}
			}
			headMeta.addColumn(parentColumn);
		}
		return headMeta;
	}

	/**
	 * 拼装数据
	 *
	 * @param list 数据集
	 * @param headMeta 表头
	 * @param fields 对象或Map属性数组（注意：顺序要与表头标题顺序对应，如数据集为List<Object[]>，则该参数可以为null）
	 * @return TableData
	 */
	@SuppressWarnings("unchecked")
	public static TableData createTableData(List list,TableHeaderMetaData headMeta,String[] fields){

		TableData td = new TableData(headMeta);
		TableDataRow row = null;
		if(list != null && list.size()>0){
			if(list.get(0).getClass().isArray()){//数组类型
				for (Object obj : list){
					row = new TableDataRow(td);
					for(Object o : (Object[])obj){
						row.addCell(o);
					}
					td.addRow(row);
				}
			}else{//JavaBean或Map类型
				for (Object obj : list){
					row = new TableDataRow(td);
					Map<String, Object> map = (obj instanceof Map)?(Map<String, Object>)obj:beanToMap(obj);
					for(String key : fields){
						row.addCell(map.get(key));
					}
					td.addRow(row);
				}
			}
		}
		return td;
	}

	/**
	 * 创建压缩输出流
	 * @param response
	 * @param zipName 压缩包名次
	 * @return
	 */
	public static ZipOutputStream createZipStream(HttpServletResponse response, String zipName) {
		response.reset();
		response.setContentType("application/vnd.ms-excel"); // 不同类型的文件对应不同的MIME类型
		try {
			response.setHeader("Content-Disposition", "attachment;filename="
					.concat(String.valueOf(URLEncoder.encode(zipName + ".zip", "UTF-8"))));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		OutputStream os = null;
		try {
			os = response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ZipOutputStream(os);
	}

	public static void copySheetStyle(HSSFWorkbook destwb, HSSFSheet dest,
                                      HSSFWorkbook srcwb, HSSFSheet src) {
		if (src == null || dest == null)
			return;

		dest.setAlternativeExpression(src.getAlternateExpression());
		dest.setAlternativeFormula(src.getAlternateFormula());
		dest.setAutobreaks(src.getAutobreaks());
		dest.setDialog(src.getDialog());
		if (src.getColumnBreaks() != null) {
			for (int col : src.getColumnBreaks()) {
				dest.setColumnBreak((short) col);
			}
		}
		dest.setDefaultColumnWidth(src.getDefaultColumnWidth());
		dest.setDefaultRowHeight(src.getDefaultRowHeight());
		dest.setDefaultRowHeightInPoints(src.getDefaultRowHeightInPoints());
		dest.setDisplayGuts(src.getDisplayGuts());
		dest.setFitToPage(src.getFitToPage());
		dest.setHorizontallyCenter(src.getHorizontallyCenter());
		dest.setDisplayFormulas(src.isDisplayFormulas());
		dest.setDisplayGridlines(src.isDisplayGridlines());
		dest.setDisplayRowColHeadings(src.isDisplayRowColHeadings());
		dest.setGridsPrinted(src.isGridsPrinted());
		dest.setPrintGridlines(src.isPrintGridlines());

		for (int i = 0; i < src.getNumMergedRegions(); i++) {
			dest.addMergedRegion(src.getMergedRegion(i));
		}

		if (src.getRowBreaks() != null) {
			for (int row : src.getRowBreaks()) {
				dest.setRowBreak(row);
			}
		}
		dest.setRowSumsBelow(src.getRowSumsBelow());
		dest.setRowSumsRight(src.getRowSumsRight());

		short maxcol = 0;
		for (int i = 0; i <= src.getLastRowNum(); i++) {
			HSSFRow row = src.getRow(i);
			if (row != null) {
				if (maxcol < row.getLastCellNum())
					maxcol = row.getLastCellNum();
			}
		}
		for (short col = 0; col <= maxcol; col++) {
			if (src.getColumnWidth(col) != src.getDefaultColumnWidth())
				dest.setColumnWidth(col, src.getColumnWidth(col));
			dest.setColumnHidden(col, src.isColumnHidden(col));
		}
	}

	public static String dumpCellStyle(HSSFCellStyle style) {
		StringBuffer sb = new StringBuffer();
		sb.append(style.getHidden()).append(",");
		sb.append(style.getLocked()).append(",");
		sb.append(style.getWrapText()).append(",");
		sb.append(style.getAlignment()).append(",");
		sb.append(style.getBorderBottom()).append(",");
		sb.append(style.getBorderLeft()).append(",");
		sb.append(style.getBorderRight()).append(",");
		sb.append(style.getBorderTop()).append(",");
		sb.append(style.getBottomBorderColor()).append(",");
		sb.append(style.getDataFormat()).append(",");
		sb.append(style.getFillBackgroundColor()).append(",");
		sb.append(style.getFillForegroundColor()).append(",");
		sb.append(style.getFillPattern()).append(",");
		sb.append(style.getIndention()).append(",");
		sb.append(style.getLeftBorderColor()).append(",");
		sb.append(style.getRightBorderColor()).append(",");
		sb.append(style.getRotation()).append(",");
		sb.append(style.getTopBorderColor()).append(",");
		sb.append(style.getVerticalAlignment());

		return sb.toString();
	}

	public static String dumpFont(HSSFFont font) {
		StringBuffer sb = new StringBuffer();
		sb.append(font.getItalic()).append(",").append(font.getStrikeout())
				.append(",").append(font.getBold()).append(",").append(
				font.getCharSet()).append(",").append(font.getColor())
				.append(",").append(font.getFontHeight()).append(",").append(
				font.getFontName()).append(",").append(
				font.getTypeOffset()).append(",").append(
				font.getUnderline());
		return sb.toString();
	}

	public static void copyCellStyle(HSSFWorkbook destwb, HSSFCell dest,
                                     HSSFWorkbook srcwb, HSSFCell src) {
		if (src == null || dest == null)
			return;

		HSSFCellStyle nstyle = findStyle(src.getCellStyle(), srcwb, destwb);
		if (nstyle == null) {
			nstyle = destwb.createCellStyle();
			copyCellStyle(destwb, nstyle, srcwb, src.getCellStyle());
		}
		dest.setCellStyle(nstyle);
	}

	private static boolean isSameColor(short a, short b, HSSFPalette apalette,
									   HSSFPalette bpalette) {
		if (a == b)
			return true;
		HSSFColor acolor = apalette.getColor(a);
		HSSFColor bcolor = bpalette.getColor(b);
		if (acolor == null)
			return true;
		if (bcolor == null)
			return false;
		return acolor.getHexString().equals(bcolor.getHexString());
	}

	private static short findColor(short index, HSSFWorkbook srcwb,
								   HSSFWorkbook destwb) {
		Integer id = new Integer(index);
		if (HSSFColor.getIndexHash().containsKey(id))
			return index;
		if (index == HSSFColor.AUTOMATIC.index)
			return index;
		HSSFColor color = srcwb.getCustomPalette().getColor(index);
		if (color == null) {
			return index;
		}

		HSSFColor ncolor = destwb.getCustomPalette().findColor(
				(byte) color.getTriplet()[0], (byte) color.getTriplet()[1],
				(byte) color.getTriplet()[2]);
		if (ncolor != null)
			return ncolor.getIndex();
		destwb.getCustomPalette().setColorAtIndex(index,
				(byte) color.getTriplet()[0], (byte) color.getTriplet()[1],
				(byte) color.getTriplet()[2]);
		return index;
	}

	public static HSSFCellStyle findStyle(HSSFCellStyle style,
                                          HSSFWorkbook srcwb, HSSFWorkbook destwb) {
		HSSFPalette srcpalette = srcwb.getCustomPalette();
		HSSFPalette destpalette = destwb.getCustomPalette();

		for (short i = 0; i < destwb.getNumCellStyles(); i++) {
			HSSFCellStyle old = destwb.getCellStyleAt(i);
			if (old == null)
				continue;

			if (style.getAlignment() == old.getAlignment()
					&& style.getBorderBottom() == old.getBorderBottom()
					&& style.getBorderLeft() == old.getBorderLeft()
					&& style.getBorderRight() == old.getBorderRight()
					&& style.getBorderTop() == old.getBorderTop()
					&& isSameColor(style.getBottomBorderColor(), old
					.getBottomBorderColor(), srcpalette, destpalette)
					&& style.getDataFormat() == old.getDataFormat()
					&& isSameColor(style.getFillBackgroundColor(), old
					.getFillBackgroundColor(), srcpalette, destpalette)
					&& isSameColor(style.getFillForegroundColor(), old
					.getFillForegroundColor(), srcpalette, destpalette)
					&& style.getFillPattern() == old.getFillPattern()
					&& style.getHidden() == old.getHidden()
					&& style.getIndention() == old.getIndention()
					&& isSameColor(style.getLeftBorderColor(), old
					.getLeftBorderColor(), srcpalette, destpalette)
					&& style.getLocked() == old.getLocked()
					&& isSameColor(style.getRightBorderColor(), old
					.getRightBorderColor(), srcpalette, destpalette)
					&& style.getRotation() == old.getRotation()
					&& isSameColor(style.getTopBorderColor(), old
					.getTopBorderColor(), srcpalette, destpalette)
					&& style.getVerticalAlignment() == old
					.getVerticalAlignment()
					&& style.getWrapText() == old.getWrapText()) {

				HSSFFont oldfont = destwb.getFontAt(old.getFontIndex());
				HSSFFont font = srcwb.getFontAt(style.getFontIndex());
				if (oldfont.getBold() == font.getBold()
						&& oldfont.getItalic() == font.getItalic()
						&& oldfont.getStrikeout() == font.getStrikeout()
						&& oldfont.getCharSet() == font.getCharSet()
						&& isSameColor(oldfont.getColor(), font.getColor(),
						srcpalette, destpalette)
						&& oldfont.getFontHeight() == font.getFontHeight()
						&& oldfont.getFontName().equals(font.getFontName())
						&& oldfont.getTypeOffset() == font.getTypeOffset()
						&& oldfont.getUnderline() == font.getUnderline()) {
					return old;
				}
			}
		}
		return null;
	}

	public static void copyCellStyle(HSSFWorkbook destwb, HSSFCellStyle dest,
                                     HSSFWorkbook srcwb, HSSFCellStyle src) {
		if (src == null || dest == null)
			return;
		dest.setAlignment(src.getAlignmentEnum());
		dest.setBorderBottom(src.getBorderBottomEnum());
		dest.setBorderLeft(src.getBorderLeftEnum());
		dest.setBorderRight(src.getBorderRightEnum());
		dest.setBorderTop(src.getBorderTopEnum());
		dest.setBottomBorderColor(findColor(src.getBottomBorderColor(), srcwb,
				destwb));
		dest.setDataFormat(destwb.createDataFormat().getFormat(
				srcwb.createDataFormat().getFormat(src.getDataFormat())));
		dest.setFillPattern(src.getFillPatternEnum());
		dest.setFillForegroundColor(findColor(src.getFillForegroundColor(),
				srcwb, destwb));
		dest.setFillBackgroundColor(findColor(src.getFillBackgroundColor(),
				srcwb, destwb));
		dest.setHidden(src.getHidden());
		dest.setIndention(src.getIndention());
		dest.setLeftBorderColor(findColor(src.getLeftBorderColor(), srcwb,
				destwb));
		dest.setLocked(src.getLocked());
		dest.setRightBorderColor(findColor(src.getRightBorderColor(), srcwb,
				destwb));
		dest.setRotation(src.getRotation());
		dest
				.setTopBorderColor(findColor(src.getTopBorderColor(), srcwb,
						destwb));
		dest.setVerticalAlignment(src.getVerticalAlignmentEnum());
		dest.setWrapText(src.getWrapText());

		HSSFFont f = srcwb.getFontAt(src.getFontIndex());
		HSSFFont nf = findFont(f, srcwb, destwb);
		if (nf == null) {
			nf = destwb.createFont();
			nf.setBold(f.getBold());//TODO
			nf.setCharSet(f.getCharSet());
			nf.setColor(findColor(f.getColor(), srcwb, destwb));
			nf.setFontHeight(f.getFontHeight());
			nf.setFontHeightInPoints(f.getFontHeightInPoints());
			nf.setFontName(f.getFontName());
			nf.setItalic(f.getItalic());
			nf.setStrikeout(f.getStrikeout());
			nf.setTypeOffset(f.getTypeOffset());
			nf.setUnderline(f.getUnderline());
		}
		dest.setFont(nf);
	}

	private static HSSFFont findFont(HSSFFont font, HSSFWorkbook src,
                                     HSSFWorkbook dest) {
		for (short i = 0; i < dest.getNumberOfFonts(); i++) {
			HSSFFont oldfont = dest.getFontAt(i);
			if (font.getBold() == oldfont.getBold()
					&& font.getItalic() == oldfont.getItalic()
					&& font.getStrikeout() == oldfont.getStrikeout()
					&& font.getCharSet() == oldfont.getCharSet()
					&& font.getColor() == oldfont.getColor()
					&& font.getFontHeight() == oldfont.getFontHeight()
					&& font.getFontName().equals(oldfont.getFontName())
					&& font.getTypeOffset() == oldfont.getTypeOffset()
					&& font.getUnderline() == oldfont.getUnderline()) {
				return oldfont;
			}
		}
		return null;
	}

	public static void copySheet(HSSFWorkbook destwb, HSSFSheet dest,
                                 HSSFWorkbook srcwb, HSSFSheet src) {
		if (src == null || dest == null)
			return;

		copySheetStyle(destwb, dest, srcwb, src);

		for (int i = 0; i <= src.getLastRowNum(); i++) {
			HSSFRow row = src.getRow(i);
			copyRow(destwb, dest.createRow(i), srcwb, row);
		}
	}

	public static void copyRow(HSSFWorkbook destwb, HSSFRow dest,
                               HSSFWorkbook srcwb, HSSFRow src) {
		if (src == null || dest == null)
			return;
		for (short i = 0; i <= src.getLastCellNum(); i++) {
			if (src.getCell(i) != null) {
				HSSFCell cell = dest.createCell(i);
				copyCell(destwb, cell, srcwb, src.getCell(i));
			}
		}

	}

	public static void copyCell(HSSFWorkbook destwb, HSSFCell dest,
                                HSSFWorkbook srcwb, HSSFCell src) {
		if (src == null) {
			dest.setCellType(HSSFCell.CELL_TYPE_BLANK);
			return;
		}

		if (src.getCellComment() != null)
			dest.setCellComment(src.getCellComment());
		if (src.getCellStyle() != null) {
			HSSFCellStyle nstyle = findStyle(src.getCellStyle(), srcwb, destwb);
			if (nstyle == null) {
				nstyle = destwb.createCellStyle();
				copyCellStyle(destwb, nstyle, srcwb, src.getCellStyle());
			}
			dest.setCellStyle(nstyle);
		}
		dest.setCellType(src.getCellType());

		switch (src.getCellType()) {
			case HSSFCell.CELL_TYPE_BLANK:

				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				dest.setCellValue(src.getBooleanCellValue());
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				dest.setCellFormula(src.getCellFormula());
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				dest.setCellErrorValue(src.getErrorCellValue());
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				dest.setCellValue(src.getNumericCellValue());
				break;
			default:
				dest.setCellValue(new HSSFRichTextString(src
						.getRichStringCellValue().getString()));
				break;
		}
	}


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