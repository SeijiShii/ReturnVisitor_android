package net.c_kogyo.returnvisitor.dialog;

import android.app.DialogFragment;
import android.content.Context;

import net.c_kogyo.returnvisitor.data.Person;

/**
 * Created by SeijiShii on 2016/07/23.
 */

public class PersonDialog extends DialogFragment {

    private static Person mPerson;

    public static PersonDialog getInstance(Person person) {

        mPerson = person;


        return new PersonDialog();

    }


}
