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
            setGmailSender(initGmail("jkelly.admin@gmail.com", "Manstein#3"));
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

        if (DBConnect.GotMail()) {

            //boot strap section - starts off i.e. initiates the process to send emails on business day intervals

            // Get the next business day
            DateTime nextBusiDay = getNextBusinessDay(now(), BusinessDaySet);

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

                            mailTime.schedule(mailTask,new Date(now().plusSeconds(random.nextInt(7200)).getMillis()));

                        }

                        // Recursive call to process more emails if they exist on the next business day.
                        //ProcessEmails();

                    } catch (Exception e) {
                        mailTime.cancel();
                        System.err.println(e.getMessage());
                    }
                }
            };

            //this.mailTime.schedule(mainTask, new Date(nextBusiDay.getMillis()));
            //Here is a piece of test code bro... fame for 15 seconds.
            //mailTime.schedule(mainTask, new Date(now().plusSeconds(120).getMillis()));
            mailTime.schedule(mainTask, new Date(now().getMillis()));
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
                    "jkelly.admin@gmail.com",
                    "John"
            );

            mailTask.run();

        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}
