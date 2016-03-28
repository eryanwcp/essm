package com.eryansky.core.cms;

import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 查看CK上传的图片
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-01-21
 *
 */
public class CKFinderFilesServlet extends HttpServlet {

    private static final long serialVersionUID = 4595639013502930224L;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String userfilesPath = CKFinderConfig.CK_BASH_URL;
    private static final String CLIENT_ABORTEXCEPTION = "ClientAbortException";

    /**
     * 直接访问CKFinder上传的文件
     * @param req
     * @param resp
     */
    public void getFile(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Content-Type", "application/octet-stream");
        String filepath = req.getRequestURI();
        filepath = filepath.replaceAll("//","/");
        String preFix = req.getContextPath()+userfilesPath;
        if(filepath.startsWith(preFix)) {
            filepath = filepath.replaceFirst(preFix,"");
        }
        try {
            filepath = UriUtils.decode(filepath, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error(String.format("解释图片文件路径失败，URL地址为%s", filepath), e1);
        }
        File file = new File(AppConstants.getCkBaseDir()+userfilesPath + filepath);
        Exception exception = null;
        try {
            FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
            return;
        } catch (FileNotFoundException e) {
//			exception = new FileNotFoundException("请求的文件不存在");
            if(logger.isWarnEnabled()) {
                logger.warn(String.format("请求的文件%s不存在", file), e.getMessage());
            }
            String path = this.getServletConfig().getServletContext().getRealPath("/");
//            String ctx = req.getContextPath();
            File errorFile = new File(path+"/static/img/image_error.png");
            try {
                FileCopyUtils.copy(new FileInputStream(errorFile), resp.getOutputStream());
            } catch (IOException e1) {
                if(!CLIENT_ABORTEXCEPTION.equals(e1.getClass().getSimpleName())){//Tomcat日志过大 不输出该类型日志
                    if(logger.isErrorEnabled()) {
                        logger.error(String.format("输出文件%s出错", errorFile), e1);
                    }
                }
            }
            return;
        } catch (IOException e) {
            if(!CLIENT_ABORTEXCEPTION.equals(e.getClass().getSimpleName())){//Tomcat日志过大 不输出该类型日志
                exception = new IOException("输出文件出错，请联系管理员", e);
                if(logger.isErrorEnabled()) {
                    logger.error(String.format("输出文件%s出错", file), e);
                }
            }
        }
        try {
            if(null != exception) {
                WebUtils.exposeErrorRequestAttributes(req, exception, "cKFinderFilesServlet");
            }
            resp.setHeader("Content-Type", WebUtils.HTML_TYPE);
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
        } catch (Exception e) {
//			logger.error("跳转500网页出错", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        getFile(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        getFile(req, resp);
    }
}
