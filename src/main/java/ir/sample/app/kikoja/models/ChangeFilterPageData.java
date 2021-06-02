package ir.sample.app.kikoja.models;

import java.util.LinkedList;

public class ChangeFilterPageData {
    public Person person;
    public LinkedList<Skill> skillList;
    public LinkedList<Favourite> favouriteList;
    String favouriteString;
    String skillString;

    public ChangeFilterPageData (Person person,
                            LinkedList<Skill> skillList,
                            LinkedList<Favourite> favouriteList,
                            String favouriteString,
                            String skillString
    ) {
        this.person = person;
        this.skillList = skillList;
        this.favouriteList = favouriteList;
        this.favouriteString = favouriteString;
        this.skillString = skillString;
    }
}