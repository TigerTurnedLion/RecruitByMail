package com.example;

import java.util.TimerTask;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

import javax.mail.internet.MimeMessage;

/**
 * Created by john on 1/18/16.
 */
public class MailTask extends TimerTask {

    private DBConnector db = null;
    private int ID;
    private String Email = null;
    private String FirstName = null;
    private JavaMailSenderImpl mailSender = null;


    public MailTask(DBConnector db, JavaMailSenderImpl sender, int id, String email, String firstName){
        this.db = db;
        this.mailSender = sender;
        this.ID = id;
        this.Email = email;
        this.FirstName = firstName;

    }
    @Override
    public void run(){

        try {
            // test code
            System.out.print("     ");
            System.out.print(this.FirstName + "     ");
            System.out.println(this.Email);


            //Send email and update the db
            // EMAIL CODE HERE
            Composer composer = new Composer(Email,FirstName,mailSender);
            MimeMailMessage m = composer.getMailMessage();

            mailSender.send(m.getMimeMessage());


            // Linkedin profile:  https://www.linkedin.com/in/john-kelly-02711915
            // Facebook profile:  https://www.facebook.com/jkelly.admin

            this.db.updateMail(this.ID);
        }
        catch(Exception e){
            throw e;
           // System.err.println(e.getMessage());
        }
    }
}
