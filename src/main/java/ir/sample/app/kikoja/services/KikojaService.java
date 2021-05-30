package ir.sample.app.kikoja.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.sample.app.kikoja.database.DatabaseManager;
import ir.sample.app.kikoja.database.DbOperation;
import ir.sample.app.kikoja.models.Favourite;
import ir.sample.app.kikoja.models.Person;
// import required models here
import ir.sample.app.kikoja.models.ProfilePageData;
import ir.sample.app.kikoja.models.Skill;
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
    private String selectedFav;
    private String selectedSkill;
    private View currentPage;

    public KikojaService(String channelName) {
        super(channelName);
    }

    // used by kernel when user opens the application: do not change
    @Override
    public String getServiceName() {
        // format: app:[user_name]:[application_name]
        return "app:kikoja:KiKoja";
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
                break;
            }
            case "getFavouritePage": {
                currentPage = new FavouritePersonsPage();
                break;
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
                currentPage = new HomePage();
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
                    person.uniMajor = pageData.get("uniEduLevelDropdown").toString();
                break;
            }
            case "setUniEntryYear": {
                if (pageData.get("uniEntryYearDropdown").toString() != emptySelection)
                    person.uniMajor = pageData.get("uniEntryYearDropdown").toString();
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
                        skillList = DbOperation.getSkillList(connection);
                        favouriteList = DbOperation.getFavouriteList(connection);
                        personSkillList = DbOperation.getPersonSkillList(person.id, connection);
                        personFavouriteList = DbOperation.getPersonFavouriteList(person.id, connection);

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
                    String personSkillListString = " ";
                    for (int i = 0; i < personSkillList.size(); i++) {
                        personSkillListString += personSkillList.get(i).skillName + " \n ";
                    }
                    update.addChildUpdate("personSkillList", "text", personSkillListString);
                }
                break;
            }
            default: {
                return update;
            }
        }
        return update;
    }

}