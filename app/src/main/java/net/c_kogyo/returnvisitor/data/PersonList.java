package net.c_kogyo.returnvisitor.data;

import net.c_kogyo.returnvisitor.data.DataList;

/**
 * Created by SeijiShii on 2016/07/28.
 */

public abstract class PersonList extends DataList<Person> {

    PersonList() {
        super(Person.class);
    }


}
