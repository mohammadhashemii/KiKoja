package ir.sample.app.kikoja.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.sample.app.kikoja.models.Person;
// import required models here
import ir.sample.app.kikoja.views.*;
import org.json.simple.JSONObject;
// import org.json.simple.JSONObject;
// import java.sql.Connection;

public class KikojaService extends APSService {

    private final int FIRST_NAME_MAX_LENGTH = 15, FIRST_NAME_MIN_LENGTH = 1, LAST_NAME_MAX_LENGTH = 15,
            LAST_NAME_MIN_LENGTH = 1;
    private final String emptySelection = "انتخاب کنید";

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
        View view = new StartingPage();
        // enter the command_name which entered in onclick second attribute here to
        // specify desired on create command
        // use pageData to get data from xml page elements using
        // their id :: pageData.get([element_id]).[desired_method]();
        // use view classes to create new page and return view class to open
        switch (command) {
            case "loginPage": {
                view = new LoginPage();
                break;
            }
            case "startingPage": {
                view = new StartingPage();
                break;
            }
            case "registerPageOne": {
                view = new RegisterPage_1();
                break;
            }
            case "loginRegisterPage": {
                view = new LoginRegisterPage();
                break;
            }
            case "getHomePage": {
                view = new HomePage();
                break;
            }
            case "getFavouritePage": {
                view = new FavouritePersonsPage();
                break;
            }
            case "getProfilePage": {
                view = new ProfilePage();
                break;
            }
            case "getMoreInfoPage": {
                view = new MoreInfoPage();
                break;
            }
            case "getNotFoundPage": {
                view = new NotFoundPage();
                break;
            }
            case "getChangeFilterPage": {
                view = new ChangeFilterPage();
                break;
            }
        }
        return view;
    }

    // use this method to update current page
    @Override
    public ir.appsan.sdk.Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData,
            String userId) {
        // new person data
        Person newPerson = new Person();
        // assign the next page to view variable and return it to change page
        View view = new StartingPage();
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
                    newPerson.id = pageData.get("studentNumberRegisterInput").toString();
                    newPerson.firstName = pageData.get("firstNameRegisterInput").toString();
                    newPerson.lastName = pageData.get("lastNameRegisterInput").toString();
                    // REVIEW test section ALL PASSED!
//                    System.out.println("[id]=" + newPerson.id);
//                    System.out.println("[first_name]=" + newPerson.firstName);
//                    System.out.println("[last_name]=" + newPerson.lastName);
                    return new RegisterPage_2();
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
                }
                return update;
            }
            case "nextButtonOfSecondRegisterPage": {
                String uniMajor = pageData.get("uniMajorDropdown").toString();
                String uniEduLevel = pageData.get("uniEduLevelDropdown").toString();
                String uniEntryYear = pageData.get("uniEntryYearDropdown").toString();
                if (!uniMajor.equals(emptySelection) && !uniEduLevel.equals(emptySelection) && !uniEntryYear.equals(emptySelection))
                    return new RegisterPage_3();
                else {
                    if (uniMajor.equals(emptySelection))
                        update.addChildUpdate("uniMajorError", "text", "باید رشته تحصیلی را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniMajorError", "text", "");
                    if (uniEduLevel.equals(emptySelection))
                        update.addChildUpdate("uniEduLevelError", "text", "باید مقطع تحصیلی را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniEduLevelError", "text", "");
                    if (uniEntryYear.equals(emptySelection))
                        update.addChildUpdate("uniEntryYearError", "text", "باید سال ورود را انتخاب کنید.");
                    else
                        update.addChildUpdate("uniEntryYearError", "text", "");
                }
                return update;
            }
            case "nextButtonOfThirdRegisterPage":{
                System.out.println(pageData.get("emailRegisterInput").toString());
                if(pageData.get("emailRegisterInput").toString().equals(""))
                    update.addChildUpdate("emailError", "text", "باید ایمیل معتبر وارد کنید.");
                if(pageData.get("phoneNumberRegisterInput").toString().equals(""))
                    update.addChildUpdate("emailError", "text", "باید شماره تلفن معتبر وارد کنید.");
                if(!pageData.get("emailRegisterInput").toString().equals("") && !pageData.get("phoneNumberRegisterInput").toString().equals("")) {
                    newPerson.email = pageData.get("emailRegisterInput").toString();
                    newPerson.phoneNumber = pageData.get("phoneNumberRegisterInput").toString();
                }
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
                return new HomePage();
            }
            case "addHobbiesButtonUpdate": {
                // String hobbyBoxText = pageData.get("hobbiesTextBox").toString();
                // String userInput = pageData.get("hobbiesTextInput").toString();
                // update.addChildUpdate("hobbiesTextBox", "innerHTML", hobbyBoxText+userInput);
                // update.addChildUpdate("hobbiesTextBox", "innerHTML", hobbyBoxText+userInput);
            }
            case "setUniMajor": {
                if (pageData.get("uniMajorDropdown").toString() != emptySelection)
                    newPerson.uniMajor = pageData.get("uniMajorDropdown").toString();
            }
            case "setUniEduLevel": {
                if (pageData.get("uniEduLevelDropdown").toString() != emptySelection)
                    newPerson.uniMajor = pageData.get("uniEduLevelDropdown").toString();
            }
            case "setUniEntryYear": {
                if (pageData.get("uniEntryYearDropdown").toString() != emptySelection)
                    newPerson.uniMajor = pageData.get("uniEntryYearDropdown").toString();
            }
            default: {
                return update;
            }
        }

    }

}