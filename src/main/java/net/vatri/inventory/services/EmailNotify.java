package net.vatri.inventory.services;

import net.vatri.inventory.models.Product;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotify {
    private static String from = "maynardlonnie44@gmail.com";
    private static String host = "smtp.gmail.com";


    public static void sendEmail(String to, List<Product> products){
        Session session = sessionPrepare();
        try {
            String text = "<h1>Stock updated:</h1><ul>";

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            for(Product product: products){
                text +="<li>"+product.getName()+"</li>";
            }

            text+="</ul>";

            message.setSubject("New Updates on Inplace application!");

            message.setContent(text,
                    "text/html");

            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static Session sessionPrepare(){
        Properties prop = System.getProperties();


        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.ssl.trust", host);


        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("maynardlonnie44@gmail.com", "wqnpcmcrzvjjpjkf");

            }

        });

        session.setDebug(true);

        return session;
    }
}
