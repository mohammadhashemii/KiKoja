package ir.sample.app.kikoja.database;

import ir.sample.app.kikoja.models.Person;

import java.sql.*;
import java.sql.Connection;

public class DbOperation {

    public static void registerPerson(Person person, Connection connection) {
        try {
            String signUpQuery = "INSERT INTO person(id,firstName,lastName,email,phoneNumber,uniMajor,uniEduLevel,uniEntryYear,imageURL) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pSingUpQuery = connection.prepareStatement(signUpQuery);
            pSingUpQuery.setString(1, person.id);
            pSingUpQuery.setString(2, person.firstName);
            pSingUpQuery.setString(3, person.lastName);
            pSingUpQuery.setString(4, person.email);
            pSingUpQuery.setString(5, person.phoneNumber);
            pSingUpQuery.setString(6, person.uniMajor);
            pSingUpQuery.setString(7, person.uniEduLevel);
            pSingUpQuery.setString(8, String.valueOf(person.uniEntryYear));
            pSingUpQuery.setString(9, person.imageURL);
            pSingUpQuery.executeUpdate();
            pSingUpQuery.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static Person retrievePerson(String StudentID, String PhoneNumber, Connection connection) {
        try {
            // enter desired list as INSERT_REQUIRED_FIELDS_HERE below
            String loginQuery = "SELECT <INSERT_REQUIRED_FIELDS_HERE> FROM person WHERE id = ? AND phone = ?";
            PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
            pLoginQuery.setString(1, StudentID);
            pLoginQuery.setString(2, PhoneNumber);
            ResultSet result = pLoginQuery.executeQuery();
            // if not valid user
            // TODO implement not valid user here by defining errors
            // if is valid user
            int INSERT_REQUIRED_FIELDS_LENGTH = 0;
            String data[] = new String[INSERT_REQUIRED_FIELDS_LENGTH];
            while (result.next()) {
                for (int i = 0; i < INSERT_REQUIRED_FIELDS_LENGTH; i++) {
                    data[i] = result.getString(i);
                }
                // TODO make use of the data array here
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static LinkedList<String> getMatched(String personID) {
        // NOTE this method will search for current user matched contacts
        String friendListQuery = "SELECT inviterid FROM relations WHERE accepted=true AND inviteeid=personID";
        PreparedStatement pMakeFriendListQuery = connection.prepareStatement(friendListQuery);
        ResultSet result = pMakeFriendListQuery.executeQuery();
        // TODO implement friend list update here , you can use interid to findout more
        // TODO about him
    }

    public static void invitePerson() {
        // NOTE this method will search for proper matches for current user from the use
        // NOTE table
        //

    }

}