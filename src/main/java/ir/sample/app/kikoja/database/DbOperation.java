package ir.sample.app.kikoja.database;

import ir.sample.app.kikoja.models.Favourite;
import ir.sample.app.kikoja.models.Person;
import ir.sample.app.kikoja.models.Skill;

import java.sql.*;
import java.sql.Connection;
import java.util.LinkedList;

public class DbOperation {

    public static void editPersonInfo(Person person, Connection connection) {
        try {
            String editInfoQuery = "UPDATE person SET firstName=?,lastName=?,email=?,phone=?,uniMajor=?,uniEduLevel=?,uniEntryYear=?,imageURL=? WHERE id=?";
            PreparedStatement pEditInfoQuery = connection.prepareStatement(editInfoQuery);
            pEditInfoQuery.setString(1, person.firstName);
            pEditInfoQuery.setString(2, person.lastName);
            pEditInfoQuery.setString(3, person.email);
            pEditInfoQuery.setString(4, person.phoneNumber);
            pEditInfoQuery.setString(5, person.uniMajor);
            pEditInfoQuery.setString(6, person.uniEduLevel);
            pEditInfoQuery.setInt(7, person.uniEntryYear);
            pEditInfoQuery.setString(8, person.imageURL);
            pEditInfoQuery.setString(9, person.id);
            pEditInfoQuery.executeUpdate();
            pEditInfoQuery.close();
        } catch (Exception e) {
            System.err.println("error during editing person info");
        }
    }

    // this method will fetch friend list of the specific person
    public static LinkedList<Person> retrieveFriendList(String PersonID, Connection connection) {
        // if there is two row of two people with true accept in relations they are
        // friends! simple.
        // people information who are friends
        LinkedList friendListPerson = new LinkedList<Person>();
        try {
            // people whom invited by this person
            LinkedList invitedPeopleID = new LinkedList<String>();
            // people who invited this person
            LinkedList friendListID = new LinkedList<String>();

            Person addFriend = new Person();
            ResultSet result;
            boolean isFriend = false;

            // find invitee people
            String getInvitedPeople = "SELECT inviteeid FROM relations WHERE inviterid = ? AND accepted = ?;";
            PreparedStatement pGetInvitedPeople = connection.prepareStatement(getInvitedPeople);
            pGetInvitedPeople.setString(1, PersonID);
            pGetInvitedPeople.setBoolean(2, true);
            result = pGetInvitedPeople.executeQuery();
            while (result.next()) {
                invitedPeopleID.add(result.getString(1));
            }

            // find people who invited
            for (String invitedID : (LinkedList<String>) invitedPeopleID) {
                String getInviteePeople = "SELECT accepted FROM relations WHERE inviterid = ? AND inviteeid = ?;";
                PreparedStatement pGetInviteePeople = connection.prepareStatement(getInviteePeople);
                pGetInviteePeople.setString(1, invitedID);
                pGetInviteePeople.setString(2, PersonID);
                result = pGetInviteePeople.executeQuery();
                while (result.next()) {
                    isFriend = result.getBoolean(1);
                }
                if (isFriend) {
                    friendListID.add(invitedID);
                }
            }
            // get friend information from database
            for (String friendID : (LinkedList<String>) friendListID) {
                String getFriendInformation = "SELECT firstname,lastname,email,phone,unimajor,uniedulevel,unientryyear,imageurl FROM relations WHERE id = ?;";
                PreparedStatement pGetFriendInformation = connection.prepareStatement(getFriendInformation);
                pGetFriendInformation.setString(1, friendID);
                result = pGetFriendInformation.executeQuery();
                addFriend = new Person();
                while (result.next()) {
                    addFriend.firstName = result.getString(1);
                    addFriend.lastName = result.getString(2);
                    addFriend.email = result.getString(3);
                    addFriend.phoneNumber = result.getString(4);
                    addFriend.uniMajor = result.getString(5);
                    addFriend.uniEduLevel = result.getString(6);
                    addFriend.uniEntryYear = result.getInt(7);
                    addFriend.imageURL = result.getString(8);
                    friendListPerson.add(addFriend);
                }
            }
        } catch (Exception e) {
            System.err.println("error during fetching friend list of specific person");
        }
        return friendListPerson;
    }

    public static boolean registerPerson(Person person, Connection connection) {
        try {
            String signUpQuery = "INSERT INTO person(id,firstName,lastName,email,phone,uniMajor,uniEduLevel,uniEntryYear,imageURL) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pSingUpQuery = connection.prepareStatement(signUpQuery);
            pSingUpQuery.setString(1, person.id);
            pSingUpQuery.setString(2, person.firstName);
            pSingUpQuery.setString(3, person.lastName);
            pSingUpQuery.setString(4, person.email);
            pSingUpQuery.setString(5, person.phoneNumber);
            pSingUpQuery.setString(6, person.uniMajor);
            pSingUpQuery.setString(7, person.uniEduLevel);
            pSingUpQuery.setInt(8, person.uniEntryYear);
            pSingUpQuery.setString(9, person.imageURL);
            pSingUpQuery.executeUpdate();
            pSingUpQuery.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
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
            return null;
        }
        return newPerson;
    }

    // this method will get a match list for specific person using the filter exists
    // in search
    public static LinkedList<Person> getMatched(String uniMajor, String uniEntryYear, String uniEduLevel, String favs,
                                                String skills, String userID, Connection connection) {
        LinkedList personList = new LinkedList<Person>();
        ResultSet result;
        try {
            String matchedQuery = "SELECT firstname, lastname, unimajor, unientryyear, id FROM person WHERE uniMajor=? AND uniedulevel=?";

            if (!uniEntryYear.equals("")) {
                matchedQuery += " AND unientryyear=?";
            }

            PreparedStatement pMatchQuery = connection.prepareStatement(matchedQuery);

            if (!uniMajor.equals(""))
                pMatchQuery.setString(1, uniMajor);
            else
                pMatchQuery.setString(1, "*");

            if (!uniEduLevel.equals(""))
                pMatchQuery.setString(2, uniEduLevel);
            else
                pMatchQuery.setString(2, "*");

            if (!uniEntryYear.equals("")) {
                pMatchQuery.setInt(3, Integer.parseInt(uniEntryYear));
            }

            result = pMatchQuery.executeQuery();
            while (result.next()) {
                Person newPerson = new Person();
                newPerson.firstName = result.getString(1);
                newPerson.lastName = result.getString(2);
                newPerson.uniMajor = result.getString(3);
                newPerson.uniEntryYear = Integer.parseInt(result.getString(4));
                newPerson.id = result.getString(5);
                personList.add(newPerson);
            }
        } catch (Exception e) {
            System.err.println("error at function getMatched");
        }

        // NOTE making a list of desired skills id using skill table
        String[] skillsArray = skills.replaceAll(" ", "").split(",");
        LinkedList skillIDLIST = new LinkedList<Skill>();
        String skillQuery = "SELECT skillid FROM skillinfo WHERE ";
        for (String skillString : skillsArray)
            skillQuery += "skill=\"" + skillString + "\" OR ";

        try {
            skillQuery = skillQuery.substring(0, skillQuery.length() - 3);
            PreparedStatement pSkillQuery = connection.prepareStatement(skillQuery);
            result = pSkillQuery.executeQuery();

            LinkedList skillIDList = new LinkedList<Integer>();
            while (result.next()) {
                skillIDLIST.add(result.getInt(1));
            }
        } catch (Exception e) {
            System.err.println("error at function getMatched : skillIDList section");
        }

        // filter skills
        try {
            for (Person targetPerson : (LinkedList<Person>) personList) {
                String personSkillQuery = "SELECT skillid FROM skills WHERE id=\"" + targetPerson.id + "\"";
                PreparedStatement pPersonSkillQuery = connection.prepareStatement(personSkillQuery);
                result = pPersonSkillQuery.executeQuery();
                // making person skill id list
                LinkedList personSkillsList = new LinkedList<Integer>();
                while (result.next()) {
                    personSkillsList.add(result.getInt(1));
                }
                // compare list with skillIDList list
                boolean isMatch = false;
                for (Integer skillID : (LinkedList<Integer>) skillIDLIST) {
                    if (personSkillsList.contains(skillID)) {
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    personList.remove(targetPerson);
                }
            }
        } catch (Exception e) {
            System.err.println("error at function getMatched : skillIDList filter");
        }

        // NOTE making a list of desired favs id using skill table
        LinkedList favIDList = new LinkedList<Integer>();
        try {
            String[] favsArray = favs.replaceAll(" ", "").split(",");
            String favQuery = "SELECT favid FROM favinfo WHERE ";
            for (String favString : favsArray)
                favQuery += "favourite=\"" + favString + "\" OR ";

            favQuery = favQuery.substring(0, favQuery.length() - 3);
            PreparedStatement pfavQuery = connection.prepareStatement(favQuery);
            result = pfavQuery.executeQuery();

            while (result.next()) {
                favIDList.add(result.getInt(1));
            }
        } catch (Exception e) {
            System.err.println("error at function getMatched : favIDLIST section");
        }

        // filter skills
        try {
            for (Person targetPerson : (LinkedList<Person>) personList) {
                String personFavQuery = "SELECT favid FROM favourites WHERE id=\"" + targetPerson.id + "\"";
                PreparedStatement pPersonFavQuery = connection.prepareStatement(personFavQuery);
                result = pPersonFavQuery.executeQuery();
                // making person skill id list
                LinkedList personFavList = new LinkedList<Integer>();
                while (result.next()) {
                    personFavList.add(result.getInt(1));
                }
                // compare list with skillIDList list
                boolean isMatch = false;
                for (Integer favID : (LinkedList<Integer>) favIDList) {
                    if (personFavList.contains(favID)) {
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    personList.remove(targetPerson);
                }
            }
        } catch (Exception e) {
            System.err.println("error at function getMatched : favIDLIST filter");
        }

        // delete blocked persons from person list
        try {
            for (Person targetPerson : (LinkedList<Person>) personList) {
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
        } catch (Exception e) {
            System.err.println("block person filter section error");
        }

        // TODO use personList to update the match page
        return personList;
    }

    // this method will update the relations table
    public static void setRelationShip(String inviterID, String inviteeID, boolean accepted, Connection connection) {
        // NOTE this method will update the relations table using the match page data
        try {
            String relationsUpdate = "INSERT INTO relations (inviterid, inviteeid, accepted) VALUES (?,?,?)";
            PreparedStatement pRelationsUpdate = connection.prepareStatement(relationsUpdate);
            pRelationsUpdate.setString(1, inviterID);
            pRelationsUpdate.setString(2, inviteeID);
            pRelationsUpdate.setBoolean(3, accepted);
            pRelationsUpdate.executeUpdate();
            pRelationsUpdate.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    // this method will fetch all skills from database, not for specific person
    public static LinkedList<Skill> getSkillList(Connection connection) {
        String selecSkillQuery = "SELECT skill,skillid FROM skillinfo;";
        ResultSet result;
        LinkedList skillList = new LinkedList<Skill>();
        try {
            PreparedStatement pSelecSkillQuery = connection.prepareStatement(selecSkillQuery);
            result = pSelecSkillQuery.executeQuery();
            while (result.next()) {
                Skill newSkill = new Skill();
                newSkill.skillName = result.getString(1);
                newSkill.skillID = result.getString(2);
                skillList.add(newSkill);
            }
        } catch (Exception e) {
            System.err.println("error during skill fetch operation");
        }
        return skillList;
    }

    // this method will fetch all favorites from database, not for specific person
    public static LinkedList<Favourite> getFavouriteList(Connection connection) {
        String selecFavQuery = "SELECT favourite,favid FROM favinfo;";
        ResultSet result;
        LinkedList favList = new LinkedList<Favourite>();
        try {
            PreparedStatement pSelecFavQuery = connection.prepareStatement(selecFavQuery);
            result = pSelecFavQuery.executeQuery();
            while (result.next()) {
                Favourite newFav = new Favourite();
                newFav.favName = result.getString(1);
                newFav.favID = result.getString(2);
                favList.add(newFav);
            }
        } catch (Exception e) {
            System.err.println("error during favourite fetch operation");
        }
        return favList;
    }

    // this method will fetch skills for specific person
    public static LinkedList<Skill> getPersonSkillList(String personID, Connection connection) {
        ResultSet result;
        Skill skill;
        LinkedList userSkillIDList = new LinkedList<String>();
        LinkedList userSkillList = new LinkedList<String>();
        String newSkillID;
        String newSkill;
        // first get all skill id's for the personList
        try {
            String selectUserSkillID = "SELECT skillid FROM skills WHERE id=?;";
            PreparedStatement pSelectUserSkillID = connection.prepareStatement(selectUserSkillID);
            pSelectUserSkillID.setString(1, personID);
            result = pSelectUserSkillID.executeQuery();
            while (result.next()) {
                newSkillID = result.getString(1);
                userSkillIDList.add(newSkillID);
            }
        } catch (Exception e) {
            System.err.println("error during fetching user skill id");
        }
        // use id's to get their values from the skill table
        for (String skillID : (LinkedList<String>) userSkillIDList) {
            try {
                String selectUserSkill = "SELECT skill FROM skillinfo WHERE skillid=?;";
                PreparedStatement pSelectUserSkill = connection.prepareStatement(selectUserSkill);
                pSelectUserSkill.setString(1, skillID);
                result = pSelectUserSkill.executeQuery();
                while (result.next()) {
                    skill = new Skill();
                    skill.skillID = skillID;
                    newSkill = result.getString(1);
                    skill.skillName = newSkill;
                    userSkillList.add(skill);
                }
            } catch (Exception e) {
                System.err.println("error during fetching user sill list from user skill id list");
            }
        }
        return userSkillList;
    }

    // this method will fetch favourites for specific person
    public static LinkedList<Favourite> getPersonFavouriteList(String personID, Connection connection) {
        ResultSet result;
        Favourite fav;
        LinkedList userFavIDList = new LinkedList<String>();
        LinkedList userFavList = new LinkedList<String>();
        String newFavID;
        String newFav;
        // first get all skill id's for the personList
        try {
            String selectUserFavID = "SELECT favid FROM favourites WHERE id=?;";
            PreparedStatement pSelectUserFavID = connection.prepareStatement(selectUserFavID);
            pSelectUserFavID.setString(1, personID);
            result = pSelectUserFavID.executeQuery();
            while (result.next()) {
                newFavID = result.getString(1);
                userFavIDList.add(newFavID);
            }
        } catch (Exception e) {
            System.err.println("error during fetching user favourite id");
        }
        // use id's to get their values from the favourite table
        for (String favID : (LinkedList<String>) userFavIDList) {
            try {
                String selectUserFav = "SELECT favourite FROM favinfo WHERE favid=?;";
                PreparedStatement pSelectUserFav = connection.prepareStatement(selectUserFav);
                pSelectUserFav.setString(1, favID);
                result = pSelectUserFav.executeQuery();
                while (result.next()) {
                    fav = new Favourite();
                    fav.favID = favID;
                    newFav = result.getString(1);
                    fav.favName = newFav;
                    userFavList.add(fav);
                }
            } catch (Exception e) {
                System.err.println("error during fetching user fav list from user fav id list");
            }
        }
        return userFavList;
    }

    // this method will insert new skill for specific person
    public static boolean insertNewSkillForSpecificPerson(String PersonID, String SkillName, Connection connection) {
        String SkillID = "";
        try {
            // first fetch skill id from skillinfo table
            String selecSkillQuery = "SELECT skillid FROM skillinfo WHERE skill=?;";
            ResultSet result;
            PreparedStatement pSelecSkillQuery = connection.prepareStatement(selecSkillQuery);
            pSelecSkillQuery.setString(1, SkillName);
            result = pSelecSkillQuery.executeQuery();
            while (result.next()) {
                SkillID = result.getString(1);
            }
            // insert data into skills table
            String insertSkillQuery = "INSERT INTO skills(id,skillid) VALUES(?,?)";
            PreparedStatement pInsertSkillQuery = connection.prepareStatement(insertSkillQuery);
            pInsertSkillQuery.setString(1, PersonID);
            pInsertSkillQuery.setString(2, SkillID);
            pInsertSkillQuery.executeUpdate();
            pInsertSkillQuery.close();
            return true;
        } catch (Exception e) {
            System.err.println("error during inserting skill for specific person");
            return false;
        }
    }

    // this method will insert new favourite for specific person
    public static boolean insertNewFavouriteForSpecificPerson(String PersonID, String FavName, Connection connection) {
        String FavID = "";
        try {
            // first fetch fav id from favinfo table
            String selecFavQuery = "SELECT favid FROM favinfo WHERE favourite=?;";
            ResultSet result;
            PreparedStatement pSelecFavQuery = connection.prepareStatement(selecFavQuery);
            pSelecFavQuery.setString(1, FavName);
            result = pSelecFavQuery.executeQuery();
            while (result.next()) {
                FavID = result.getString(1);
            }
            // insert data into skills table
            String insertFavQuery = "INSERT INTO favourites(id,favid) VALUES(?,?)";
            PreparedStatement pInsertFavQuery = connection.prepareStatement(insertFavQuery);
            pInsertFavQuery.setString(1, PersonID);
            pInsertFavQuery.setString(2, FavID);
            pInsertFavQuery.executeUpdate();
            pInsertFavQuery.close();
            return true;
        } catch (Exception e) {
            System.err.println("error during inserting favourite for specific person");
            return false;
        }
    }

    // this method will remove skill for specific person
    public static boolean removeSkillForSpecificPerson(String PersonID, String SkillName, Connection connection) {
        String SkillID = "";
        try {
            // first fetch skill id from skillinfo table
            String selecSkillQuery = "SELECT skillid FROM skillinfo WHERE skill=?;";
            ResultSet result;
            PreparedStatement pSelecSkillQuery = connection.prepareStatement(selecSkillQuery);
            pSelecSkillQuery.setString(1, SkillName);
            result = pSelecSkillQuery.executeQuery();
            while (result.next()) {
                SkillID = result.getString(1);
            }
            // delete data from skills table
            String deleteSkillQuery = "DELETE FROM skills WHERE id=? AND skillid=?;";
            PreparedStatement pDeleteSkillQuery = connection.prepareStatement(deleteSkillQuery);
            pDeleteSkillQuery.setString(1, PersonID);
            pDeleteSkillQuery.setString(2, SkillID);
            pDeleteSkillQuery.executeUpdate();
            pDeleteSkillQuery.close();
            return true;
        } catch (Exception e) {
            System.err.println("error during removing skill for specific person");
            return false;
        }
    }

    // this method will remove favourite for specific person
    public static boolean removeFavouriteForSpecificPerson(String PersonID, String FavName, Connection connection) {
        String FavID = "";
        try {
            // first fetch fav id from favinfo table
            String selecFavQuery = "SELECT favid FROM favinfo WHERE favourite=?;";
            ResultSet result;
            PreparedStatement pSelecFavQuery = connection.prepareStatement(selecFavQuery);
            pSelecFavQuery.setString(1, FavName);
            result = pSelecFavQuery.executeQuery();
            while (result.next()) {
                FavID = result.getString(1);
            }
            // remove data into skills table
            String deleteFavQuery = "DELETE FROM favourites WHERE id=? AND favid=?;";
            PreparedStatement pDeleteFavQuery = connection.prepareStatement(deleteFavQuery);
            pDeleteFavQuery.setString(1, PersonID);
            pDeleteFavQuery.setString(2, FavID);
            pDeleteFavQuery.executeUpdate();
            pDeleteFavQuery.close();
            return true;
        } catch (Exception e) {
            System.err.println("error during removing favourite for specific person");
            return false;
        }
    }
}