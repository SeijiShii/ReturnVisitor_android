package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.widget.ImageView;
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
    }

    private ImageView raterMark;
    private void initRaterMark() {

        raterMark = (ImageView) getViewById(R.id.rater_mark);
        raterMark.setBackgroundResource(Constants.buttonRes[mPerson.getInterest().num()]);
    }

    private TextView personText;
    private void initPersonText() {

        personText = (TextView) getViewById(R.id.person_text);
        personText.setText(mPerson.toString(mContext));
    }

    @Override
    public int getViewHeight() {
        return 50;
    }

    @Override
    public void onUpdateHeight() {

    }
}
