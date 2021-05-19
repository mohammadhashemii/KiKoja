package ir.sample.app.kikoja.database;

import ir.sample.app.kikoja.models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;

public class DbOperation {

    public static void registerPerson(Person person, Connection connection) {
        try {
            String loginQuery = "INSERT INTO person(id,firstName,lastName,email,phoneNumber,uniMajor,uniEduLevel,uniEntryYear,imageURL) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
            pstmt.setString(1, person.id);
            pstmt.setString(2, person.firstName);
            pstmt.setString(3, person.lastName);
            pstmt.setString(4, person.email);
            pstmt.setString(5, person.phoneNumber);
            pstmt.setString(6, person.uniMajor);
            pstmt.setString(7, person.uniEduLevel);
            pstmt.setString(8, person.uniEntryYear);
            pstmt.setString(9, person.imageURL);
            pLoginQuery.executeUpdate();
            pLoginQuery.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // public static void retrievePerson(){
    //
    // }
    //
    // public static LinkedList<String> getMatched(String personID){
    //
    // }
    //
    // public static void invitePerson(){
    //
    // }

}