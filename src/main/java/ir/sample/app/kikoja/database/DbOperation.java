package ir.sample.app.kikoja.database;

import ir.sample.app.kikoja.models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;

public class DbOperation{


    public static void registerPerson(String id, Connection connection){
        try{
            String loginQuery = "INSERT INTO person(id,firstName,lastName,email,phoneNumber,uniMajor,uniEduLevel,uniEntryYear,skills,favourites) VALUES()";
            PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
            pLoginQuery.executeUpdate();
            pLoginQuery.close();
        }catch (Exception e){
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