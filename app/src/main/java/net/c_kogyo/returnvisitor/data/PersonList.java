package net.c_kogyo.returnvisitor.data;

import net.c_kogyo.returnvisitor.data.DataList;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/28.
 */

public abstract class PersonList extends DataList<Person> {

    PersonList() {
        super(Person.class);
    }

    public ArrayList<Person> getPersons(ArrayList<String> personIds) {

        ArrayList<Person> persons = new ArrayList<>();
        for ( String  id : personIds) {
            persons.add(getById(id));
        }
        return persons;
    }

}
