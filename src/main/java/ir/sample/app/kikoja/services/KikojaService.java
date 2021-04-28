package ir.sample.app.kikoja.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
// import required models here
import ir.sample.app.kikoja.views.*;
import org.json.simple.JSONObject;
// import org.json.simple.JSONObject;
// import java.sql.Connection;

public class KikojaService extends APSService {

    private final int FIRST_NAME_MAX_LENGTH = 10, FIRST_NAME_MIN_LENGTH = 1, LAST_NAME_MAX_LENGTH = 10,
            LAST_NAME_MIN_LENGTH = 1;

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
            case "startingPage": {
                view = new StartingPage();
                break;
            }
            case "registerPageOne": {
                view = new RegisterPage_1();
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
                // success case
                if ((firstNameInputLength > FIRST_NAME_MIN_LENGTH) && (firstNameInputLength < FIRST_NAME_MAX_LENGTH)
                        && (lastNameInputLength > LAST_NAME_MIN_LENGTH) && (lastNameInputLength < LAST_NAME_MAX_LENGTH)) {
                    // lunch the second registering page if succeed
                    return new RegisterPage_2();
                }
                // fail case
                else {
                    if ((firstNameInputLength < FIRST_NAME_MIN_LENGTH) || (firstNameInputLength > FIRST_NAME_MAX_LENGTH)) {
                        update.addChildUpdate("firstNameRegisterInput", "background", "red");
                        WarningMessage = "کاراکتر باشد" + FIRST_NAME_MAX_LENGTH + " و حداکثر" + FIRST_NAME_MIN_LENGTH
                                + "طول نام باید حداقل";
                        update.addChildUpdate("firstNameRegisterInput", "innerhtml", WarningMessage);
                    }
                    if ((lastNameInputLength < LAST_NAME_MIN_LENGTH) || (lastNameInputLength > LAST_NAME_MAX_LENGTH)) {
                        update.addChildUpdate("lastNameRegisterInput", "background", "red");
                        WarningMessage = "کاراکتر باشد" + LAST_NAME_MAX_LENGTH + " و حداکثر" + LAST_NAME_MIN_LENGTH
                                + "طول نام خانوادگی باید حداقل";
                        update.addChildUpdate("lastNameRegisterInput", "innerhtml", WarningMessage);
                    }
                }
                return update;
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
            default:{
                return update;
            }
        }

    }

}