package com.eryansky.core.cms;


import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.utils.AppConstants;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * word转html
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-01-09
 */
public class Word2Html {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static void main(String argv[]) {
        try {
            convert2Html(null,"D://角色栏目.doc", "D://1.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convert2Html(HttpServletRequest request,String fileName, String outPutFile)
            throws TransformerException, IOException,
            ParserConfigurationException {
        InputStream inputStream = new FileInputStream(fileName);
        return convert2Html(request,inputStream,outPutFile);

    }


    public static String convert2Html(final HttpServletRequest request,InputStream inputStream, String outPutFile)
            throws TransformerException, IOException,
            ParserConfigurationException {
        HWPFDocument wordDocument = new HWPFDocument(inputStream);//WordToHtmlUtils.loadDoc(new FileInputStream(inputFile));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .newDocument()
        );
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        final SessionInfo sessionInfo1 = sessionInfo;

        final long nowTime = System.currentTimeMillis();
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content,
                                      PictureType pictureType, String suggestedName,
                                      float widthInches, float heightInches) {
                String path = null;
                if (sessionInfo1 != null) {
                    path = request.getContextPath()+"/userfiles/" + sessionInfo1.getUserId() + "/images/cms/article/";
                } else {
                    path = request.getContextPath()+"/userfiles/images/cms/article/";
                }
                path += new SimpleDateFormat("yyyy/MM/").format(new Date());
                return path + nowTime + "_" + suggestedName;
            }
        });
        wordToHtmlConverter.processDocument(wordDocument);
        //save pictures
        List pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            String path = null;
            if (sessionInfo != null) {
                path = AppConstants.getCkBaseDir() + "/userfiles/" + sessionInfo.getUserId() + "/images/cms/article/";
            } else {
                path = AppConstants.getCkBaseDir() + "/userfiles/images/cms/article/";
            }
            path += new SimpleDateFormat("yyyy/MM/").format(new Date());
            FileUtils.checkSaveDir(path);
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
//                    pic.writeImageContent(new FileOutputStream(path+ pic.suggestFullFileName()));
                    pic.writeImageContent(new FileOutputStream(path + nowTime + "_" + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF8);
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");////是否添加空格
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
        String content = new String(out.toByteArray(), ENCODING_UTF8);
//        writeFile(content, outPutFile);
        return content;

    }

    public static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, ENCODING_UTF8));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }
}
