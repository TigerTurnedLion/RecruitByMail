package com.example;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by john on 1/1/16.
 */
public class Composer {

    //private SimpleMailMessage MailMessage;
    private JavaMailSenderImpl mailSender;
    private MimeMailMessage mailMessage;
    private MimeMessageHelper mailHelper;
    private String firstName;

    public Composer(String to, String firstname, JavaMailSenderImpl sender){

        try {

            this.mailSender = sender;
            mailHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            this.mailMessage = new MimeMailMessage(mailHelper);
            firstName = firstname;
        }
        catch(Exception e){

            System.err.println(e.getMessage());
        }

        Compose(to);
    }

    private void Compose(String to){

        mailMessage.setTo(to);
        mailMessage.setSubject("Candidate referral by Richard Burton");
        mailMessage.setText(MailBody());

        try {
            FileSystemResource file = new FileSystemResource(new File("/Users/John/IdeaProjects/RecruitByMail/src/main/resources/attachments/John_Kelly_Resume.doc"));
            mailHelper.addAttachment("John_Kelly_Resume.doc", file);
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    private String MailBody(){

        return "Hello " + firstName + " Its working!";
    }

    public MimeMailMessage getMailMessage(){

        return mailMessage;
    }
}
