/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package test;

import com.beust.jcommander.internal.Maps;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.http.HttpCompoents;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模拟获取应用和集成待办信息
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-12-11
 */
public class HTTPTest {
    public static void main(String[] args) throws IOException {
        String url1 = "http://10.36.98.234/portal/AuthenService?USERID=superadmin&APP=portal&RESOURCE="
                +EncodeUtils.urlEncode("http://10.36.98.234/portal/cqpageinitcmd.cmd?method=daiban|userCaptcha=");
        System.out.println(url1);
        Map<String,String> headers = Maps.newHashMap();

        HttpCompoents httpCompoents = HttpCompoents.getInstance();
        try {

            String getResult = httpCompoents.get(url1);
            httpCompoents.printCookies();
            System.out.println(getResult);

            Document document = Jsoup.parse(getResult);
            Elements actionElements = document.getElementsByTag("form");
            Element actionElement = actionElements.get(0);
            String action = actionElement.attr("action");
            Elements inputElements = document.getElementsByTag("input");
            String userCaptcha = null;
            String RelayState = null;
            String SAMLRequest = null;
            for (Element inputElement : inputElements) {
                if (inputElement.attr("name").equals("userCaptcha") ) {
                    userCaptcha = inputElement.attr("value");
                } else if (inputElement.attr("name").equals("RelayState")) {
                    RelayState = inputElement.attr("value");
                } else if (inputElement.attr("name").equals("SAMLRequest")) {
                    SAMLRequest = inputElement.attr("value");
                }
            }
            System.out.println(action);
            System.out.println(userCaptcha);
            System.out.println(RelayState);
            System.out.println(SAMLRequest);
            String url2 = "http://10.36.98.234"+action;
            Map<String,String> data = Maps.newHashMap();
            data.put("userCaptcha", userCaptcha);
            data.put("RelayState", RelayState);
            data.put("SAMLRequest", SAMLRequest);

            //拼接参数
            String postResult = httpCompoents.post(url2, data);
            httpCompoents.printCookies();
            System.out.println(postResult);


            Document document1 = Jsoup.parse(postResult);
            Elements actionElements1 = document1.getElementsByTag("form");
            Element actionElement1 = actionElements1.get(0);
            String action1 = actionElement1.attr("action");
            Elements inputElements1 = document1.getElementsByTag("input");
            String RelayState1 = null;
            String SAMLRequest1 = null;
            String SAMLResponse = null;
            for (Element inputElement : inputElements1) {
                if (inputElement.attr("name").equals("RelayState") ) {
                    RelayState1 = inputElement.attr("value");
                } else if (inputElement.attr("name").equals("SAMLRequest")) {
                    SAMLRequest1 = inputElement.attr("value");
                } else if (inputElement.attr("name").equals("SAMLResponse")) {
                    SAMLResponse = inputElement.attr("value");
                }
            }
            System.out.println(action1);
            System.out.println(RelayState1);
            System.out.println(SAMLRequest1);
            System.out.println(SAMLResponse);
            String url3 = "http://10.36.98.234"+action1;
            HttpPost httpPost3 = new HttpPost(url3);
            //拼接参数
            List<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
            nvps1.add(new BasicNameValuePair("RelayState", RelayState1));
            nvps1.add(new BasicNameValuePair("SAMLRequest", SAMLRequest1));
            nvps1.add(new BasicNameValuePair("SAMLResponse", SAMLResponse));

            httpPost3.setEntity(new UrlEncodedFormEntity(nvps1));
            HttpResponse postResponse1 = httpCompoents.getHttpClient().execute(httpPost3);
            System.out.println(postResponse1.getStatusLine());
            if(postResponse1.getStatusLine().getStatusCode() == 302){
                String redirectUrl = postResponse1.getLastHeader("Location").getValue();
                System.out.println("result:"+HttpCompoents.getInstance().get(redirectUrl));

            }
            httpCompoents.printCookies();
        } finally {
        }

    }


}
