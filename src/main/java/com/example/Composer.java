package com.example;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Random;

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
            this.mailHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            this.mailMessage = new MimeMailMessage(mailHelper);
            this.firstName = firstname;

        }
        catch(Exception e){

            System.err.println(e.getMessage());
        }

        Compose(to);
    }

    private void Compose(String to){

        mailMessage.setTo(to);
        mailMessage.setSubject("Candidate referral by Richard Burton");

        try {
            mailHelper.setText(MailBody(),true);
            FileSystemResource file = new FileSystemResource(new File("/Users/John/IdeaProjects/RecruitByMail/src/main/resources/attachments/John_Kelly_Resume.doc"));
            mailHelper.addAttachment("John_Kelly_Resume.doc", file);
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    private String MailBody(){

        return "Hello " + firstName + "<p>" +
                "My name is John Kelly.  I am a <b>Vice President</b>, Software Development working for Greystone Labs in New York City " +
                "where I lead the charge to integrate our financial systems " +
                "via <b>SOA architecture</b> and <b>Cloud technologies</b>.  Richard Burton with whom you are connected to on LinkedIn " +
                "recommended I speak with you.  I'm pleased to make your acquaintance.<p>" +
                firstName + ", I am looking to make a career move in the new year and I am looking for a " +
                "<b>Senior Management role</b> e.g. <b>Director</b> or <b>VP</b> within IT.  <b>Publishing Media</b>, <b>Healthcare</b>, and <Financial> industries are in my background.  " +
                "However, I am open to other sectors as well.<p>" +
                "In regards to location, my preference is for <b>NYC</b>.<p>" +
                //"The compensation I am searching for is a base salary of <b>$175k per year</b>.<p>" +
                "My passion is to build solutions that support our business initiatives and provide leadership to the teams that make it happen.<p>" +
                "For more information in regards to my background and experience, please see my attached resume.<p>" +
                "Also, I'd like to invite you to connect with me on LinkedIn @ https://www.linkedin.com/in/johnjameskelly<br>" +
                //"And, you can find me on Facebook as well @ https://www.facebook.com/jkelly.admin <p>" +
                "Have a great day and I look forward to working with you.<p>" +
                getQuote();

//        return "Hello " + firstName + "<p>" +
//                "My name is John Kelly.  I am a <b>Senior Manager</b>, Software Development working for Harper Collins publishers, " +
//                "a subsidiary of News Corp symbol: FOXA where I lead the charge to integrate our world wide systems " +
//                "via <b>SOA architecture</b> and <b>Cloud technologies</b>.  Richard Burton with whom you are connected to on LinkedIn " +
//                "recommended I speak with you.  I'm pleased to make your acquaintance.<p>" +
//                firstName + ", I am looking to make a career move in the new year and I am in the market for a " +
//                "<b>Senior Management role</b> e.g. <b>Director</b> or <b>VP</b> within IT.  <b>Publishing</b> and <b>Healthcare</b> industries are in my background.  " +
//                "However, I am open to other sectors as well.<p>" +
//                "In regards to location, my preference is for <b>NYC</b>.  But, I am willing to consider other locations outside " +
//                "New York for the right opportunity.  The compensation I am searching for is a base salary of <b>$170k per year</b>.<p>" +
//                "My passion is to build solutions that support our business initiatives and provide leadership to the teams that make it happen.<p>" +
//                "For more information in regards to my background and experience, please see my attached resume.<p>" +
//                "Also, I'd like to invite you to connect with me on LinkedIn @ https://www.linkedin.com/in/johnjameskelly<br>" +
//                //"And, you can find me on Facebook as well @ https://www.facebook.com/jkelly.admin <p>" +
//                "Have a great day and I look forward to working with you.<p>" +
//                getQuote();

    }

    public MimeMailMessage getMailMessage(){

        return mailMessage;
    }

    private static String getQuote(){

        Random ran = new Random();
        int ranInt;
        String quote;

        ranInt = ran.nextInt(6);

        switch(ranInt){
            case 1: quote = "&quot;Start by doing what's necessary; then do what's possible; and suddenly you are doing the impossible.&quot;<br>" +
                    "- Francis of Assisi<br>";
                break;
            case 2: quote = "&quot;If opportunity doesn't knock, build a door.&quot;<br>" +
                    "- Milton Berle<br>";
                break;
            case 3: quote = "&quot;Your work is going to fill a large part of your life, and the only way to be truly satisfied is to do what you believe is great work. And the only way to do great work is to love what you do. If you haven't found it yet, keep looking. Don't settle. As with all matters of the heart, you'll know when you find it.&quot;<br>" +
                    "- Steve Jobs<br>";
                break;
            case 4: quote = "&quot;My mission in life is not merely to survive, but to thrive; and to do so with some passion, some compassion, some humor, and some style.&quot;<br>" +
                    "- Maya Angelou<br>";
                break;
            case 5: quote = "&quot;As we express our gratitude, we must never forget that the highest appreciation is not to utter words, but to live by them.&quot;<br>" +
                    "- John F. Kennedy<br>";
                break;
            case 6: quote = "&quot;Perfection is not attainable, but if we chase perfection we can catch excellence.&quot;<br>" +
                    "- Vince Lombardi<br>";
                break;
            default: quote = "&quot;Coming together is a beginning; keeping together is progress; working together is success.&quot;<br>" +
                    "- Henry Ford<br>";
                break;
        }

        return quote;

    }
}
