package ir.sample.app.kikoja.models;

public class HomePageData {
    public Person person;
    public String favString;
    public String skillString;

    public HomePageData (
            Person person,
            String favString,
            String skillString
    ) {
        this.person = person;
        this.favString = favString;
        this.skillString = skillString;
    }
}
