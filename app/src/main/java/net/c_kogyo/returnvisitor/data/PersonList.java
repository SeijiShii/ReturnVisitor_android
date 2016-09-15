package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import net.c_kogyo.returnvisitor.data.DataList;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

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

    public ArrayList<String> getSearchedPersonIds(String searchString, Context context) {

        String[] words = searchString.split(" ");

        ArrayList<String> personIds = new ArrayList<>();

        for (Person person : list) {

            for (String word : words) {
                if (StringUtils.containsIgnoreCase(person.toStringForSearch(context), word)) {
                    if (!personIds.contains(person.getId())) {
                        personIds.add(person.getId());
                    }
                }
            }
        }
        return personIds;
    }

}
