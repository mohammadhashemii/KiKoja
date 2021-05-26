package ir.sample.app.kikoja.database;

import ir.sample.app.kikoja.models.Person;
import ir.sample.app.kikoja.models.Skill;

import java.sql.*;
import java.sql.Connection;
import java.util.LinkedList;

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
            System.out.println("done");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // this method will return profile page information for current user
    public static Person retrievePerson(String StudentID, String PhoneNumber, Connection connection) {
        Person newPerson = null;
        try {
            // enter desired list as INSERT_REQUIRED_FIELDS_HERE below field1,field2,...

            String loginQuery = "SELECT * FROM person WHERE id = ? AND phone = ?";
            PreparedStatement pLoginQuery = connection.prepareStatement(loginQuery);
            pLoginQuery.setString(1, StudentID);
            pLoginQuery.setString(2, PhoneNumber);
            ResultSet result = pLoginQuery.executeQuery();
            // if not valid user
            // TODO implement not valid user here by defining errors
            // if is valid user
            int personAttributes = 9;
            String[] data = new String[personAttributes];
            while (result.next()) {
                for (int i = 1; i <= personAttributes; i++) {
                    data[i - 1] = result.getString(i);
                }
            }
            newPerson = new Person();
            newPerson.id = data[0];
            newPerson.firstName = data[1];
            newPerson.lastName = data[2];
            newPerson.email = data[3];
            newPerson.phoneNumber = data[4];
            newPerson.uniMajor = data[5];
            newPerson.uniEduLevel = data[6];
            newPerson.uniEntryYear = Integer.parseInt(data[7]);
            newPerson.imageURL = data[8];

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return newPerson;
    }

    public static LinkedList<String> getMatched(String uniMajor, String uniEntryYear, String uniEduLevel, String favs,
            String skills, String userID, Connection connection) {

        String matchedQuery = "SELECT firstname, lastname, unimajor, unientryyear, id FROM person";
        if (!(uniMajor + uniEntryYear + uniEduLevel + favs + skills).equals(""))
            matchedQuery += " where ";

        if (!uniMajor.equals(""))
            matchedQuery += "unimajor=" + uniMajor + " ";

        if (!uniEntryYear.equals(""))
            matchedQuery += "unientryyear=" + uniMajor + " ";

        if (!uniEduLevel.equals(""))
            matchedQuery += "uniedulevel=" + uniMajor + " ";


        ResultSet result;
        LinkedList personList = new LinkedList<Person>();
        try{
            PreparedStatement pMatchQuery = connection.prepareStatement(matchedQuery);
            result = pMatchQuery.executeQuery();
            while (result.next()) {
                Person newPerson = new Person();
                newPerson.firstName = result.getString(0);
                newPerson.lastName = result.getString(1);
                newPerson.uniMajor = result.getString(2);
                newPerson.uniEntryYear = Integer.parseInt(result.getString(3));
                newPerson.id = result.getString(4);
                personList.add(newPerson);
            }
        }
        catch (Exception e){
            System.err.println("error at function getMatched");
        }


        // NOTE making a list of desired skills id using skill table
        String[] skillsArray = skills.split(",");
        LinkedList skillIDLIST = new LinkedList<Skill>();
        String skillQuery = "SELECT skillid FROM skillinfo WHERE ";
        for (String skillString : skillsArray)
            skillQuery += "skill=" + skillString + " OR ";

        try{
            skillQuery = skillQuery.substring(0, skillQuery.length() - 3);
            PreparedStatement pSkillQuery = connection.prepareStatement(skillQuery);
            result = pSkillQuery.executeQuery();

            LinkedList skillIDList = new LinkedList<Integer>();
            while (result.next()) {
                skillIDLIST.add(result.getInt(1));
            }
        }
        catch(Exception e){
            System.err.println("error at function getMatched : skillIDList section");
        }



        // filter skills
        try {
            for (Person targetPerson : (LinkedList<Person>)personList) {
                String personSkillQuery = "SELECT skillid FROM skills WHERE id=" + targetPerson.id;
                PreparedStatement pPersonSkillQuery = connection.prepareStatement(personSkillQuery);
                result = pPersonSkillQuery.executeQuery();
                // making person skill id list
                LinkedList personSkillsList = new LinkedList<Integer>();
                while (result.next()) {
                    personSkillsList.add(result.getInt(1));
                }
                // compare list with skillIDList list
                boolean isMatch = false;
                for (Integer skillID : (LinkedList<Integer>)skillIDLIST) {
                    if (personSkillsList.contains(skillID)) {
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    personList.remove(targetPerson);
                }
            }
        }catch (Exception e){
            System.err.println("error at function getMatched : skillIDList filter");
        }


        // NOTE making a list of desired favs id using skill table
        LinkedList favIDList = new LinkedList<Integer>();
        try{
            String[] favsArray = favs.split(",");
            String favQuery = "SELECT favid FROM favinfo WHERE ";
            for (String favString : favsArray)
                favQuery += "favourite=" + favString + " OR ";

            favQuery = favQuery.substring(0, favQuery.length() - 3);
            PreparedStatement pfavQuery = connection.prepareStatement(favQuery);
            result = pfavQuery.executeQuery();


            while (result.next()) {
                favIDList.add(result.getInt(1));
            }
        }catch (Exception e){
            System.err.println("error at function getMatched : favIDLIST section");
        }


        // filter skills
        try {
            for (Person targetPerson : (LinkedList<Person>)personList) {
                String personFavQuery = "SELECT favid FROM favourites WHERE id=" + targetPerson.id;
                PreparedStatement pPersonFavQuery = connection.prepareStatement(personFavQuery);
                result = pPersonFavQuery.executeQuery();
                // making person skill id list
                LinkedList personFavList = new LinkedList<Integer>();
                while (result.next()) {
                    personFavList.add(result.getInt(1));
                }
                // compare list with skillIDList list
                boolean isMatch = false;
                for (Integer favID : (LinkedList<Integer>)favIDList) {
                    if (personFavList.contains(favID)) {
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    personList.remove(targetPerson);
                }
            }
        }catch (Exception e){
            System.err.println("error at function getMatched : favIDLIST filter");
        }


        // delete blocked persons from person list
        try{
            for (Person targetPerson : (LinkedList<Person>)personList) {
                String isAcceptedQuery = "SELECT accepted FROM relations WHERE invitedid=? AND inviteeid=?";
                PreparedStatement pIsAcceptedQuery = connection.prepareStatement(isAcceptedQuery);
                pIsAcceptedQuery.setString(1, targetPerson.id);
                pIsAcceptedQuery.setString(2, userID);
                result = pIsAcceptedQuery.executeQuery();
                pIsAcceptedQuery.close();
                String isAccepted = "";
                while (result.next()) {
                    isAccepted = String.valueOf(result.getBoolean(1));
                }
                if (isAccepted.equals("false")) {
                    personList.remove(targetPerson);
                }

                isAcceptedQuery = "SELECT accepted FROM relations WHERE invitedid=? AND inviteeid=?";
                pIsAcceptedQuery = connection.prepareStatement(isAcceptedQuery);
                pIsAcceptedQuery.setString(1, userID);
                pIsAcceptedQuery.setString(2, targetPerson.id);
                result = pIsAcceptedQuery.executeQuery();
                pIsAcceptedQuery.close();
                isAccepted = "";
                while (result.next()) {
                    isAccepted = String.valueOf(result.getBoolean(1));
                }
                if (isAccepted.equals("false")) {
                    personList.remove(targetPerson);
                }
            }
        }
        catch (Exception e){
            System.err.println("block person filter section error");
        }


        // TODO use personList to update the match page
        return personList;
    }

    public static void invitePerson(String inviterID, String inviteeID, boolean accepted, Connection connection) {
        // NOTE this method will update the relations table using the match page data
        try {
            String relationsUpdate = "INSERT INTO relations (inviterid, inviteeid, accepted) VALUES (?,?,?)";
            PreparedStatement pRelationsUpdate = connection.prepareStatement(relationsUpdate);
            pRelationsUpdate.setString(1, inviterID);
            pRelationsUpdate.setString(2, inviteeID);
            pRelationsUpdate.setString(3, String.valueOf(accepted));
            pRelationsUpdate.executeUpdate();
            pRelationsUpdate.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

}