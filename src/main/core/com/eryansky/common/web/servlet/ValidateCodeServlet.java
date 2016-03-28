/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.servlet;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 *  生成随机验证码.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-4 下午9:00:06
 */
@SuppressWarnings("serial")
public class ValidateCodeServlet extends HttpServlet {
	
	private int w = 70;
	private int h = 26;
	
	public ValidateCodeServlet() {
		super();
	}
	
	public void destroy() {
		super.destroy(); 
	}
	
	/**
	 * 忽略大小写校验.
	 */
	public static boolean validate(HttpServletRequest request, String validateCode){
		String code = (String)request.getSession().getAttribute(SysConstants.SESSION_VALIDATE_CODE);
		return validateCode.toUpperCase().equalsIgnoreCase(code); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String validateCode = request.getParameter(SysConstants.SESSION_VALIDATE_CODE); // AJAX验证，成功返回true
		if (StringUtils.isNotBlank(validateCode)){
			response.getOutputStream().print(validate(request, validateCode)?"true":"false");
		}else{
			this.doPost(request, response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		createImage(request,response);
	}
	
	private void createImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		
		/*
		 * 得到参数高，宽，都为数字时，则使用设置高宽，否则使用默认值
		 */
		String width = request.getParameter("width");
		String height = request.getParameter("height");
		if (StringUtils.isNumeric(width) && StringUtils.isNumeric(height)) {
			w = NumberUtils.toInt(width);
			h = NumberUtils.toInt(height);
		}
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		/*
		 * 生成背景
		 */
		createBackground(g);

		/*
		 * 生成字符
		 */
		String s = createCharacter(g);
		request.getSession().setAttribute(SysConstants.SESSION_VALIDATE_CODE, s);

		g.dispose();
		OutputStream out = response.getOutputStream();
		ImageIO.write(image, "JPEG", out);
		out.close();

	}
	
	private Color getRandColor(int fc,int bc) { 
		Random random=new Random();
        if(fc>255) fc=255; 
        if(bc>255) bc=255; 
        int r=fc+random.nextInt(bc-fc); 
        int g=fc+random.nextInt(bc-fc); 
        int b=fc+random.nextInt(bc-fc); 
        return new Color(r,g,b); 
	}
	
	private void createBackground(Graphics g) {
		// 填充背景
		g.setColor(getRandColor(220,250)); 
		g.fillRect(0, 0, w, h);
		// 加入干扰线条
		for (int i = 0; i < 10; i++) {
			g.setColor(getRandColor(40,150));
			Random random = new Random();
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int x1 = random.nextInt(w);
			int y1 = random.nextInt(h);
			g.drawLine(x, y, x1, y1);
		}
	}

	private String createCharacter(Graphics g) {
		char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		String[] fontTypes = {"\u5b8b\u4f53","\u65b0\u5b8b\u4f53","\u9ed1\u4f53","\u6977\u4f53","\u96b6\u4e66"}; 
		Random random = new Random();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
			g.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
			g.setFont(new Font(fontTypes[random.nextInt(fontTypes.length)],Font.BOLD,26)); 
			g.drawString(r, 15 * i + 5, 19 + random.nextInt(8));
//			g.drawString(r, i*w/4, h-5);
			s.append(r);
		}
		return s.toString();
	}
	
}
