package ir.sample.app.kikoja.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.sample.app.kikoja.database.DatabaseManager;
import ir.sample.app.kikoja.database.DbOperation;
import ir.sample.app.kikoja.models.*;
// import required models here
import ir.sample.app.kikoja.views.*;
import org.json.simple.JSONObject;
// import org.json.simple.JSONObject;
import java.sql.Connection;
import java.util.LinkedList;

//
public class KikojaService extends APSService {

    private final int FIRST_NAME_MAX_LENGTH = 15, FIRST_NAME_MIN_LENGTH = 1, LAST_NAME_MAX_LENGTH = 15,
            LAST_NAME_MIN_LENGTH = 1;
    private final String emptySelection = "انتخاب کنید";
    private final Connection connection = DatabaseManager.getConnection();
    private Person person = new Person();
    private LinkedList<Skill> skillList = new LinkedList<Skill>();
    private LinkedList<Favourite> favouriteList = new LinkedList<Favourite>();
    private LinkedList<Skill> personSkillList = new LinkedList<Skill>();
    private LinkedList<Favourite> personFavouriteList = new LinkedList<Favourite>();
    private LinkedList<Person> friendList = new LinkedList<Person>();
    private String selectedFav;
    private String selectedSkill;
    private View currentPage;
    private final LinkedList<String> filterFavouriteList = new LinkedList<String>();
    private final LinkedList<String> filterSkillList = new LinkedList<String>();
    private String filterSelectedFav;
    private String filterSelectedSkill;
    private LinkedList<Person> personLinkedList = new LinkedList<Person>();
    private String favouriteString;
    private String skillString;
    private String filterUniMajor;
    private String filterUniEduLevel;
    private int filterUniEntryYear;
    private int next = 0;

    public KikojaService(String channelName) {
        super(channelName);
    }

    // used by kernel when user opens the application: do not change
    @Override
    public String getServiceName() {
        // format: app:[user_name]:[application_name]
        return "app:kikoja:KiKoja";
    }

    public void makeNewFilters() {
        for (Skill skill : personSkillList) {
            filterSkillList.add(skill.skillName);
        }
        for (Favourite fav : personFavouriteList) {
            filterFavouriteList.add(fav.favName);
        }
        favouriteString = " ";
        skillString = " ";
        for(int i = 0; i < filterSkillList.size() ; i++) {
            if (i != 0) {
                skillString += " , ";
            }
            skillString+=filterSkillList.get(i);
        }
        skillString+=" ";

        for(int i = 0; i < filterFavouriteList.size() ; i++) {
            if (i != 0) {
                favouriteString += " , ";
            }
            favouriteString+=filterFavouriteList.get(i);
        }
        favouriteString+=" ";
    }

    public String makeFavouriteString(LinkedList<Favourite> favouriteList) {
        String string = " ";
        for(int i = 0; i < favouriteList.size() ; i++) {
            if (i != 0) {
                string += " , ";
            }
            string += favouriteList.get(i).favName;
        }
        string += " ";
        return string;
    }

    public String makeSkillString(LinkedList<Skill> skillList) {
        String string = " ";
        for(int i = 0; i < skillList.size() ; i++) {
            if (i != 0) {
                string += " , ";
            }
            string += skillList.get(i).skillName;
        }
        string += " ";
        return string;
    }

    // use this method to change pages
    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        // assign the next page to view variable and return it to change page
        currentPage = new StartingPage();
        // enter the command_name which entered in onclick second attribute here to
        // specify desired on create command
        // use pageData to get data from xml page elements using
        // their id :: pageData.get([element_id]).[desired_method]();
        // use view classes to create new page and return view class to open
        switch (command) {
            case "loginPage": {
                currentPage = new LoginPage();
                break;
            }
            case "startingPage": {
                currentPage = new StartingPage();
                break;
            }
            case "registerPageOne": {
                currentPage = new RegisterPage_1();
                break;
            }
            case "loginRegisterPage": {
                currentPage = new LoginRegisterPage();
                break;
            }
            case "getHomePage": {
                currentPage = new HomePage();
                next = 0;

                personLinkedList = DbOperation.getMatched(
                        filterUniMajor,
                        String.valueOf(filterUniEntryYear),
                        filterUniEduLevel,
                        favouriteString,
                        skillString,
                        person.id,
                        connection
                );
                Person currentPerson = personLinkedList.get(next);
                LinkedList<Favourite> currentFavouriteList = DbOperation.getPersonFavouriteList(currentPerson.id, connection);
                LinkedList<Skill> currentSkillList = DbOperation.getPersonSkillList(currentPerson.id, connection);
                HomePageData data = new HomePageData(currentPerson, makeFavouriteString(currentFavouriteList), makeSkillString(currentSkillList));
                currentPage.setMustacheModel(data);
                break;
            }
            case "getFavouritePage": {
                currentPage = new FavouritePersonsPage();
                friendList = DbOperation.retrieveFriendList(person.id, connection);
                FavouritePersonsData data = new FavouritePersonsData(friendList);
                currentPage.setMustacheModel(data);
                return currentPage;
            }
            case "getProfilePage": {
                currentPage = new ProfilePage();
                break;
            }
            case "getMoreInfoPage": {
                currentPage = new MoreInfoPage();
                break;
            }
            case "getNotFoundPage": {
                currentPage = new NotFoundPage();
                break;
            }
            case "getChangeFilterPage": {
                currentPage = new ChangeFilterPage();
                break;
            }
        }
        return currentPage;
    }

    // use this method to update current page
    @Override
    public ir.appsan.sdk.Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData,
                                           String userId) {
        // used to write warning messages inside the xml elements
        String WarningMessage;
        // enter the command_name which entered in onclick second attribute here to
        // specify desired on update command
        // use pageData to get data from xml page elements using
        // their id :: pageData.get([element_id]).[desired_method]();
        // use update to modify xml elements using their attribute and id
        // update.addChildUpdate([child_id],[attribute],[update_value]);
        // use view classes to create new page and return view class to open
        switch (updateCommand) {
            case "nextButtonOfFirstRegisterPage": {
                int firstNameInputLength = pageData.get("firstNameRegisterInput").toString().length();
                int lastNameInputLength = pageData.get("lastNameRegisterInput").toString().length();
                int studentNumberRegisterInputLength = pageData.get("studentNumberRegisterInput").toString().length();
                // success case
                if ((firstNameInputLength > FIRST_NAME_MIN_LENGTH) && (firstNameInputLength < FIRST_NAME_MAX_LENGTH)
                        && (lastNameInputLength > LAST_NAME_MIN_LENGTH) && (lastNameInputLength < LAST_NAME_MAX_LENGTH)
                        && (studentNumberRegisterInputLength == 8)) {
                    // lunch the second registering page if succeed
                    person.id = pageData.get("studentNumberRegisterInput").toString();
                    person.firstName = pageData.get("firstNameRegisterInput").toString();
                    person.lastName = pageData.get("lastNameRegisterInput").toString();
                    // REVIEW test section ALL PASSED!
                    currentPage = new RegisterPage_2();
                    return currentPage;
                }
                // fail case
                else {
                    if ((firstNameInputLength < FIRST_NAME_MIN_LENGTH)
                            || (firstNameInputLength > FIRST_NAME_MAX_LENGTH)) {
                        update.addChildUpdate("firstNameHint", "textcolor", "red");
                    }
                    else
                        update.addChildUpdate("firstNameHint", "textcolor", "black");
                    if ((lastNameInputLength < LAST_NAME_MIN_LENGTH) || (lastNameInputLength > LAST_NAME_MAX_LENGTH)) {
                        update.addChildUpdate("lastNameHint", "textcolor", "red");
                    } else
                        update.addChildUpdate("lastNameHint", "textcolor", "black");
                    if (studentNumberRegisterInputLength != 8) {
                        update.addChildUpdate("studentNumberHint", "textcolor", "red");
                    } else
                        update.addChildUpdate("studentNumberHint", "textcolor", "black");
                    return update;
                }
            }
            case "nextButtonOfSecondRegisterPage": {
                String uniMajor = pageData.get("uniMajorDropdown").toString();
                String uniEduLevel = pageData.get("uniEduLevelDropdown").toString();
                int uniEntryYear = Integer.parseInt(pageData.get("uniEntryYearDropdown").toString());
                if (!uniMajor.equals(emptySelection) && !uniEduLevel.equals(emptySelection) && uniEntryYear != 0) {
                    person.uniMajor = uniMajor;
                    person.uniEduLevel = uniEduLevel;
                    person.uniEntryYear = uniEntryYear;
                    currentPage = new RegisterPage_3();
                    return currentPage;
                } else {
                    if (uniMajor.equals(emptySelection))
                        update.addChildUpdate("uniMajorError", "text", "باید رشته تحصیلی را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniMajorError", "text", "");
                    if (uniEduLevel.equals(emptySelection))
                        update.addChildUpdate("uniEduLevelError", "text", "باید مقطع تحصیلی را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniEduLevelError", "text", "");
                    if (uniEntryYear == 0)
                        update.addChildUpdate("uniEntryYearError", "text", "باید سال ورود را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniEntryYearError", "text", "");
                    return update;
                }
            }
            case "nextButtonOfThirdRegisterPage":{
                if(pageData.get("emailRegisterInput").toString().equals(""))
                    update.addChildUpdate("emailError", "text", "باید ایمیل معتبر وارد کنید.");
                if(pageData.get("phoneNumberRegisterInput").toString().equals(""))
                    update.addChildUpdate("emailError", "text", "باید شماره تلفن معتبر وارد کنید.");
                if(!pageData.get("emailRegisterInput").toString().equals("") && !pageData.get("phoneNumberRegisterInput").toString().equals("")) {
                    person.email = pageData.get("emailRegisterInput").toString();
                    person.phoneNumber = pageData.get("phoneNumberRegisterInput").toString();
                    person.imageURL = "";
                    boolean registerResponse = DbOperation.registerPerson(person, connection);
                    if (registerResponse) {
                        filterUniMajor = person.uniMajor;
                        filterUniEduLevel = person.uniEduLevel;
                        filterUniEntryYear = person.uniEntryYear;

                        skillList = DbOperation.getSkillList(connection);
                        favouriteList = DbOperation.getFavouriteList(connection);

                        currentPage = new ProfilePage();
                        currentPage.setMustacheModel(person);
                        return currentPage;
                    }
                    else {
                        currentPage = new RegisterPage_1();
                        return currentPage;
                    }
                }
                break;
                //finalize registration process if successful
            }
            case "firstNameRegisterInputClick": {
                update.addChildUpdate("firstNameRegisterInput", "innerhtml", "");
                update.addChildUpdate("firstNameRegisterInput", "background", "black");
                return update;
            }
            case "lastNameRegisterInputClick": {
                update.addChildUpdate("lastNameRegisterInput", "innerhtml", "");
                update.addChildUpdate("lastNameRegisterInput", "background", "black");
                return update;
            }
            case "saveProfile": {
                filterUniMajor = person.uniMajor;
                filterUniEduLevel = person.uniEduLevel;
                filterUniEntryYear = person.uniEntryYear;
                currentPage = new HomePage();
                person.phoneNumber = pageData.get("phoneNumberInput").toString();
                person.email = pageData.get("emailInput").toString();
                DbOperation.editPersonInfo(person, connection);
                currentPage = new ChangeFilterPage();
                makeNewFilters();

                ProfilePageData data = new ProfilePageData(
                        person,
                        skillList,
                        favouriteList,
                        personSkillList,
                        personFavouriteList
                );
                currentPage.setMustacheModel(data);
                return currentPage;
            }
            case "addHobbiesButtonUpdate": {
                // String hobbyBoxText = pageData.get("hobbiesTextBox").toString();
                // String userInput = pageData.get("hobbiesTextInput").toString();
                // update.addChildUpdate("hobbiesTextBox", "innerHTML", hobbyBoxText+userInput);
                // update.addChildUpdate("hobbiesTextBox", "innerHTML", hobbyBoxText+userInput);
            }
            case "setUniMajor": {
                if (pageData.get("uniMajorDropdown").toString() != emptySelection)
                    person.uniMajor = pageData.get("uniMajorDropdown").toString();
                break;
            }
            case "setUniEduLevel": {
                if (pageData.get("uniEduLevelDropdown").toString() != emptySelection)
                    person.uniEduLevel = pageData.get("uniEduLevelDropdown").toString();
                break;
            }
            case "setUniEntryYear": {
                if (pageData.get("uniEntryYearDropdown").toString() != emptySelection)
                    person.uniEntryYear = Integer.parseInt(pageData.get("uniEntryYearDropdown").toString());
                break;
            }
            case "nextButtonOfLoginPage": {
                String studentId = pageData.get("studentNumberLoginInput").toString();
                String phoneNumber = pageData.get("phoneNumberLoginInput").toString();
                boolean isValid = true;
                if (studentId.length() != 8) {
                    update.addChildUpdate("studentNumberError", "text","شماره دانشجویی باید دقیقا 8 رقم باشد.");
                    isValid = false;
                }
                if (phoneNumber.length() == 0) {
                    update.addChildUpdate("phoneNumberError", "text", "شماره موبایل باید معتبر باشد.");
                    isValid = false;
                }
                if (isValid) {
                    Person loginResponse = DbOperation.retrievePerson(studentId, phoneNumber, connection);
                    if (loginResponse == null) {
                        currentPage = new LoginPage();
                    } else {
                        person = loginResponse;
                        filterUniMajor = person.uniMajor;
                        filterUniEduLevel = person.uniEduLevel;
                        filterUniEntryYear = person.uniEntryYear;
                        skillList = DbOperation.getSkillList(connection);
                        favouriteList = DbOperation.getFavouriteList(connection);
                        personSkillList = DbOperation.getPersonSkillList(person.id, connection);
                        personFavouriteList = DbOperation.getPersonFavouriteList(person.id, connection);

                        makeNewFilters();

                        currentPage = new ProfilePage();
                        ProfilePageData data = new ProfilePageData(
                                person,
                                skillList,
                                favouriteList,
                                personSkillList,
                                personFavouriteList
                        );
                        currentPage.setMustacheModel(data);
                    }
                    return currentPage;
                }
                return update;
            }
            case "filterfavSelect":{
                if(pageData.get("filterfavDropdownn").toString()!=emptySelection) {
                    filterSelectedFav = pageData.get("filterfavDropdownn").toString();
                }

                break;
            }
            case "filteraddFav":{
                if(!filterFavouriteList.contains(filterSelectedFav))
                    filterFavouriteList.add(filterSelectedFav);
                String filterNewFav = " ";
                for(int i = 0; i < filterFavouriteList.size() ; i++) {
                    if (i != 0) {
                        filterNewFav += " , ";
                    }
                    filterNewFav+=filterFavouriteList.get(i);
                }
                filterNewFav+=" ";
                update.addChildUpdate("filterFavouriteList","text",filterNewFav);
                break;
            }
            case "filterremoveFav":{
                filterFavouriteList.remove(filterSelectedFav);
                String filterNewFav = " ";
                for(int i = 0; i < filterFavouriteList.size() ; i++) {
                    if (i != 0) {
                        filterNewFav += " , ";
                    }
                    filterNewFav+=filterFavouriteList.get(i);
                }
                filterNewFav+=" ";
                update.addChildUpdate("filterFavouriteList","text",filterNewFav);
                break;

            }
            case "skilladdFav":{
                if(!filterSkillList.contains(filterSelectedSkill))
                    filterSkillList.add(filterSelectedSkill);
                String filterNewSkill=" ";
                for(int i = 0 ; i < filterSkillList.size() ; i++){
                    if(i != 0){
                        filterNewSkill += " , ";
                    }
                    filterNewSkill += filterSkillList.get(i);
                }
                filterNewSkill+=" ";
                update.addChildUpdate("filterSkillListt","text",filterNewSkill);
                break;
            }
            case "filterSkillSelect":{
                if(pageData.get("filterSkillDropdownn").toString()!=emptySelection) {
                    filterSelectedSkill = pageData.get("filterSkillDropdownn").toString();
                }
                break;
            }
            case "skillremoveFav":{
                filterSkillList.remove(filterSelectedSkill);
                String filterNewSkill=" ";
                for(int i = 0 ; i < filterSkillList.size() ; i++){
                    if(i != 0){
                        filterNewSkill+=" , ";
                    }
                    filterNewSkill+=filterSkillList.get(i);
                }
                filterNewSkill+=" ";
                update.addChildUpdate("filterSkillListt","text",filterNewSkill);
                break;
            }
            case "favSelect": {
                if (pageData.get("favDropdown").toString() != emptySelection)
                    selectedFav = pageData.get("favDropdown").toString();
                break;
            }
            case "skillSelect": {
                if (pageData.get("skillDropdown").toString() != emptySelection)
                    selectedSkill = pageData.get("skillDropdown").toString();
                break;
            }
            case "addFav": {
                boolean response = DbOperation.insertNewFavouriteForSpecificPerson(person.id, selectedFav, connection);
                if (response) {
                    personFavouriteList = DbOperation.getPersonFavouriteList(person.id, connection);
                    makeNewFilters();
                    String personFavouriteListString = " ";
                    for (int i = 0; i < personFavouriteList.size(); i++) {
                        personFavouriteListString += personFavouriteList.get(i).favName + " \n ";
                    }
                    update.addChildUpdate("personFavouriteList", "text", personFavouriteListString);
                }
                break;
            }
            case "removeFav": {
                boolean response = DbOperation.removeFavouriteForSpecificPerson(person.id, selectedFav, connection);
                if (response) {
                    personFavouriteList = DbOperation.getPersonFavouriteList(person.id, connection);
                    makeNewFilters();
                    String personFavouriteListString = " ";
                    for (int i = 0; i < personFavouriteList.size(); i++) {
                        personFavouriteListString += personFavouriteList.get(i).favName + " \n ";
                    }
                    update.addChildUpdate("personFavouriteList", "text", personFavouriteListString);
                }
                break;
            }
            case "addSkill": {
                boolean response = DbOperation.insertNewSkillForSpecificPerson(person.id, selectedSkill, connection);
                if (response) {
                    personSkillList = DbOperation.getPersonSkillList(person.id, connection);
                    makeNewFilters();
                    String personSkillListString = " ";
                    for (int i = 0; i < personSkillList.size(); i++) {
                        personSkillListString += personSkillList.get(i).skillName + " \n ";
                    }
                    update.addChildUpdate("personSkillList", "text", personSkillListString);
                }
                break;
            }
            case "removeSkill": {
                boolean response = DbOperation.removeSkillForSpecificPerson(person.id, selectedSkill, connection);
                if (response) {
                    personSkillList = DbOperation.getPersonSkillList(person.id, connection);
                    makeNewFilters();
                    String personSkillListString = " ";
                    for (int i = 0; i < personSkillList.size(); i++) {
                        personSkillListString += personSkillList.get(i).skillName + " \n ";
                    }
                    update.addChildUpdate("personSkillList", "text", personSkillListString);
                }
                break;
            }
            case "changeUniMajorFilter": {
                if (pageData.get("uniMajorFilterDropdown").toString() != emptySelection)
                    filterUniMajor = pageData.get("uniMajorFilterDropdown").toString();
                break;
            }
            case "changeUniEduLevelFilter": {
                if (pageData.get("uniEduLevelFilterDropdown").toString() != emptySelection)
                    filterUniEduLevel = pageData.get("uniEduLevelFilterDropdown").toString();
                break;
            }
            case "changeUniEntryYearFilter": {
                if (pageData.get("uniEntryYearFilterDropdown").toString() != emptySelection)
                    filterUniEntryYear = Integer.parseInt(pageData.get("uniEntryYearFilterDropdown").toString());
                break;
            }
            case "dislikePerson": {
                DbOperation.setRelationShip(person.id, personLinkedList.get(next).id, false, connection);
                next++;
                if (next == personLinkedList.size()) {
                    currentPage = new NotFoundPage();
                    return currentPage;
                } else {
                    Person currentPerson = personLinkedList.get(next);
                    LinkedList<Favourite> currentFavouriteList = DbOperation.getPersonFavouriteList(currentPerson.id, connection);
                    LinkedList<Skill> currentSkillList = DbOperation.getPersonSkillList(currentPerson.id, connection);
                    HomePageData data = new HomePageData(currentPerson, makeFavouriteString(currentFavouriteList), makeSkillString(currentSkillList));
                    currentPage.setMustacheModel(data);
                    break;
                }
            }
            case "likePerson": {
                DbOperation.setRelationShip(person.id, personLinkedList.get(next).id, true, connection);
                next++;
                if (next == personLinkedList.size()) {
                    currentPage = new NotFoundPage();
                    return currentPage;
                } else {
                    Person currentPerson = personLinkedList.get(next);
                    LinkedList<Favourite> currentFavouriteList = DbOperation.getPersonFavouriteList(currentPerson.id, connection);
                    LinkedList<Skill> currentSkillList = DbOperation.getPersonSkillList(currentPerson.id, connection);
                    HomePageData data = new HomePageData(currentPerson, makeFavouriteString(currentFavouriteList), makeSkillString(currentSkillList));
                    currentPage.setMustacheModel(data);
                    return currentPage;
                }
            }
            default: {
                return update;
            }
        }
        return update;
    }

}