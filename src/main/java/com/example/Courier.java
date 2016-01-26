package com.example;

import org.springframework.mail.MailSender;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by john on 1/1/16.
 */
public class Courier{

    private SimpleMailMessage message;
    private MailSender sender;

    public Courier(SimpleMailMessage message, MailSender sender){

        this.message = message;
        this.sender = sender;
    }

    public void send(){

        try{
            this.sender.send(this.message);
        }
        catch(MailException ex){
            throw ex;
        }
    }

}
