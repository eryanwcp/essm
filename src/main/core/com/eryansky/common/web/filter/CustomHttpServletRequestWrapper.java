package com.eryansky.common.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    protected Logger logger = LoggerFactory.getLogger(CustomHttpServletRequestWrapper.class);

    private final byte[] body;
    private ServletInputStream inputStream = null;

    public CustomHttpServletRequestWrapper(HttpServletRequest request)
            throws IOException {
        super(request);
        body = StreamUtils.copyToByteArray(request.getInputStream());
        this.inputStream = request.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return inputStream.isFinished();
            }

            @Override
            public boolean isReady() {
                return inputStream.isReady();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                inputStream.setReadListener(readListener);
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

}