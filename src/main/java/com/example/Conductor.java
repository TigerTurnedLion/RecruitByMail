package com.example;

import org.joda.time.DateTime;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.Date;
import java.sql.*;

import static org.joda.time.DateTime.*;

/**
 * Created by john on 1/1/16.
 */

public final class Conductor {

    private static Conductor instance = null;
    private MailSender GmailSender = null;
    private Timer mailTime = null;
    private LinkedHashSet<String> BusinessDaySet = null;
    private DBConnector DBConnect = null;

    /*

    //Create a static email address for mock purposes.
    private static String address = "mrburton@gmail.com";

    */

    private Conductor() {

        try {
            // Initialize the connection to Gmail
            setGmailSender(initGmail("EMAIL", "PASSWORD"));
            this.mailTime = new Timer("TimesUp");
            this.BusinessDaySet = BusinessDayHydrate();
            this.DBConnect = new DBConnector(initConnection());
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }

    }

    public static Conductor getInstance() {
        if (instance == null) {
            instance = new Conductor();
        }
        return instance;
    }

    public JavaMailSenderImpl initGmail(String uid,String pwd){

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername(uid);
        sender.setPassword(pwd);

        Properties props = new Properties();
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.auth", "true");

        sender.setJavaMailProperties(props);

        return sender;

    }

    public Connection initConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("org.h2.Driver");
            Connection H2Connect = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/default", "", "");
            H2Connect.setAutoCommit(false);

            return H2Connect;
        }
        catch(ClassNotFoundException | SQLException e){
            throw e;
        }
    }

    public void setGmailSender(MailSender value){

        GmailSender = value;
    }

    public MailSender getGmailSender(){

        return GmailSender;
    }

    private static LinkedHashSet<String> BusinessDayHydrate(){

        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.add("Monday");
        set.add("Tuesday");
        set.add("Wednesday");
        set.add("Thursday");
        set.add("Friday");

        return set;

    }

    private static DateTime getNextBusinessDay(DateTime startDate, Iterable<String> BusinessDaySet){

        // Increment the startDate to the next day
        DateTime nextDay = startDate.plusDays(1);
        boolean isBusinessDay = false;

        for (String aBusinessDaySet : BusinessDaySet) {

            if (nextDay.dayOfWeek().getAsText().equalsIgnoreCase(aBusinessDaySet)){

                isBusinessDay = true;
            }
        }

        if(isBusinessDay){
            // set the time to 1pm before returning the next business day.
            nextDay = nextDay.withMillisOfDay(46800000);
            return nextDay;
        }
        else{

            return getNextBusinessDay(nextDay,BusinessDaySet);
        }
    }

    public void ProcessEmails() {

        if (this.DBConnect.GotMail()) {

            //boot strap section - starts off i.e. initiates the process to send emails on business day intervals

            // Get the next business day
            DateTime nextBusiDay = getNextBusinessDay(now(), this.BusinessDaySet);

            TimerTask mainTask = new TimerTask() {
                Random random = new Random();

                @Override
                public void run() {
                /* Test Code
                System.out.println("yo! hommie don't play that MOFO!");
                System.out.println(this.scheduledExecutionTime());
                */

                    /*
                    Steps to complete the main, outer task:
                        1. Connect to h2 and select the next 100 records to process
                        2. Loop through each record and schedule an inner task to send the associated email at a random time.
                        3. Reschedule the main, outer task to execute on the next business day.
                    */
                    ResultSet rs;

                    try {
                        rs = DBConnect.getNextAddresses();

                        while (rs.next()) {

                            MailTask mailTask = new MailTask(
                                    DBConnect,
                                    (JavaMailSenderImpl) GmailSender,
                                    rs.getInt("ID"),
                                    rs.getString("EMAIL"),
                                    rs.getString("FIRSTNAME")

                            );

                            // Generate random Datetime on the next business day between 1pm and 4pm
                            // to inject into the mailTime schedule
                            // limit for Random nextInt = 10800000

                            //mailTime.schedule(mailTask,new Date(now().plusSeconds(random.nextInt(10800)).getMillis()));
                            mailTime.schedule(mailTask, new Date(now().plusSeconds(random.nextInt(60)).getMillis()));

                        }

                        // Recursive call to process more emails if they exist on the next business day.
                        ProcessEmails();

                    } catch (Exception e) {
                        mailTime.cancel();
                        System.err.println(e.getMessage());
                    }
                }
            };

            //this.mailTime.schedule(mainTask, new Date(nextBusiDay.getMillis()));
            //Here is a piece of test code bro... fame for 15 seconds.
            this.mailTime.schedule(mainTask, new Date(now().plusSeconds(120).getMillis()));
        } else {
            this.mailTime.cancel();
        }
    }
    public void ProcessEmails_Test(){

        try{
            MailTask mailTask;
            mailTask = new MailTask(
                    DBConnect,
                    (JavaMailSenderImpl) GmailSender,
                    1,
                    "",
                    ""
            );

            mailTask.run();

        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

        /*

        //Compose email
        Composer composer = new Composer(address);
        SimpleMailMessage m = composer.getMailMessage();


        // Will use DI here.  The Conductor should read configuration details on how to connect to the SMTP server and
        // instantiate a concrete email sender i.e. JavaMailSenderImpl obj with those details.  Then inject the obj to
        // the Courier object.

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("jkelly.admin");
        sender.setPassword("Manstein#3");

        Properties props = new Properties();
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.auth", "true");

        sender.setJavaMailProperties(props);

        //Send email 
        Courier courier = new Courier(m, sender);

        //catch the errors
        try {
            courier.send();

            //update email as processed.
            //CODE!
        } catch (Exception e) {

            System.err.println(e.getMessage());
        }

        */
        /*
    }

    public static long GenRandom(Long min, Long max){

        Random ran = new Random();
        long l = ran.nextLong(max) + min;



        return l;
    }*/

/*    public static void RandomDates(){

        Random random = new Random();

        DateTime startTime = new DateTime(random.nextLong());

        System.out.println(startTime);

        startTime = new DateTime(random.nextLong()).withMillisOfSecond(111);

        System.out.println(startTime);


    }

    public static void RandomInts(int n){
        Random ran = new Random();

        int RandomInt = ran.nextInt(n);
        System.out.println(RandomInt);
    }*/
}
