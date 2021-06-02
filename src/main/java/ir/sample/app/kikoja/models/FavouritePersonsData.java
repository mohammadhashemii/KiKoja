package ir.sample.app.kikoja.models;

import java.util.LinkedList;

public class FavouritePersonsData {
    LinkedList<Person> favouritePersons;
    public FavouritePersonsData (
            LinkedList<Person> favouritePersons
    ) {
        this.favouritePersons = favouritePersons;
    }
}
