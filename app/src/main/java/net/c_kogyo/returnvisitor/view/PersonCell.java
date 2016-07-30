package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.data.Person;

/**
 * Created by SeijiShii on 2016/07/24.
 */

public class PersonCell extends BaseAnimateView {

    private Person mPerson;
    private Context mContext;

    public PersonCell(Context context, Person person, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.person_cell);

        mContext = context;
        mPerson = person;

        initRaterMark();
        initPersonText();
        setPerson(null);
    }

    private ImageView raterMark;
    private void initRaterMark() {

        raterMark = (ImageView) getViewById(R.id.rater_mark);

    }

    private TextView personText;
    private void initPersonText() {

        personText = (TextView) getViewById(R.id.person_text);

    }

    @Override
    public int getViewHeight() {
        return 50;
    }

    @Override
    public void onUpdateHeight() {

    }

    public Person getPerson() {
        return mPerson;
    }

    public void setPerson(Person person) {

        if (person != null) {
            mPerson = person;
        }

        raterMark.setBackgroundResource(Constants.buttonRes[mPerson.getInterest().num()]);
        personText.setText(mPerson.toString(mContext));

    }


}
