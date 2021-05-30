package ir.sample.app.kikoja.models;

import java.util.LinkedList;

public class ProfilePageData {
    public Person person;
    public LinkedList<Skill> skillList;
    public LinkedList<Favourite> favouriteList;
    public LinkedList<Skill> personSkillList;
    public LinkedList<Favourite> personFavouriteList;

    public ProfilePageData (Person person,
                            LinkedList<Skill> skillList,
                            LinkedList<Favourite> favouriteList,
                            LinkedList<Skill> personSkillList,
                            LinkedList<Favourite> personFavouriteList
    ) {
        this.person = person;
        this.skillList = skillList;
        this.favouriteList = favouriteList;
        this.personSkillList = personSkillList;
        this.personFavouriteList = personFavouriteList;
    }
}