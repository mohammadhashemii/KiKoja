package ir.sample.app.kikoja.models;

import ir.sample.app.kikoja.models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbOperation{


    public static void registerPerson(String id){
        String loginQuery = "INSERT INTO person(id,firstName,lastName,email,phoneNumber,uniMajor,uniEduLevel,uniEntryYear,skills,favourites) VALUES()";
        PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
        pLoginQuery.executeUpdate();
        pLoginQuery.close();
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