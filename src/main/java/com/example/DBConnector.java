package com.example;

import java.sql.*;

/**
 * Created by john on 1/17/16.
 */
public class DBConnector {

    private Connection conn = null;
    private PreparedStatement prepStatement = null;

    public DBConnector(Connection conn){

        this.conn = conn;
        this.prepStatement = initUpdateStatement(this.conn);

    }
    private PreparedStatement initUpdateStatement(Connection conn){
        String SQL;
        SQL = "UPDATE RECRUITEMAILS SET SENT = TRUE, DATESENT = GETDATE() WHERE ID = ?;";

        try {
            return conn.prepareStatement(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getNextAddresses(){
        Statement stmt;
        String query;

        try{
            query = "SELECT TOP 100 ID, EMAIL, FIRSTNAME FROM RECRUITEMAILS WHERE SENT = FALSE ORDER BY ID;";

            stmt = conn.createStatement();

            return stmt.executeQuery(query);
        }
        catch(Exception e){

            System.err.println(e.getMessage());
            return null;
        }
    }
    public boolean GotMail(){
        Statement stmt;
        String query;
        ResultSet rs;
        boolean b = false;

        try{
            query = "SELECT CASEWHEN(EXISTS(SELECT ID FROM RECRUITEMAILS WHERE SENT = FALSE),TRUE,FALSE) AS GotMail;";

            stmt = conn.createStatement();

            rs = stmt.executeQuery(query);
            if(rs.first()) {
                b = rs.getBoolean("GotMail");
            }

            return b;
        }
        catch(Exception e){

            System.err.println(e.getMessage());
            return false;
        }
    }
    public void updateMail(int ID){

        try {
            this.prepStatement.setInt(1,ID);
            this.prepStatement.executeUpdate();
            this.conn.commit();
        }
        catch(Exception e){

            System.err.println(e.getMessage());
        }
    }
}
