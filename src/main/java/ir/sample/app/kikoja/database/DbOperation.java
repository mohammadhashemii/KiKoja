package ir.sample.app.kikoja.models;

import ir.sample.app.kikoja.models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbOperation{


    public static void registerPerson(String[] value){
        String loginQuery = "INSERT INTO person(id,firstName,lastName,email,phoneNumber,uniMajor,uniEduLevel,uniEntryYear,skills,favourites) VALUES("+value[0]+","+value[1]+","+value[2]+","+value[3]+","+value[4]+","+value[5]+","+value[6]+","+value[7]+","+value[8]+","+value[9]+")";
        PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
        pLoginQuery.executeUpdate();
        pLoginQuery.close();
    }

// public static void retrievePerson(){       
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