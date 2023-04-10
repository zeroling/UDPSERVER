package com.example.udpserver.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Date;
import java.util.Properties;

public class mail {
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public static void sendMail(String themessage,String filename,byte[] receive) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.qq.com");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.socketFactory.class", SSL_FACTORY);  //使用JSSE的SSL socketfactory来取代默认的socketfactory
            properties.put("mail.smtp.socketFactory.fallback", "false");
            Session session = Session.getInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("721743425@qq.com"));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("1640691243@qq.com"));
            message.setSubject("举报内容", "UTF-8");
            // 添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(themessage, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            // 遍历添加文件附件
            if(receive!=null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(receive, "application/octet-stream");
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeText(filename, "UTF-8", "B"));
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            message.setSentDate(new Date());
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            //或者为企业邮箱和密码
            transport.connect("smtp.qq.com", 465,"721743425@qq.com", "odddknobsyibbddc");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
