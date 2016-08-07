package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
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

    public PersonCell(Context context,
                      Person person,
                      InitialHeightCondition initCondition,
                      PostRemoveAnimationListener listener) {
        super(context, initCondition, R.layout.person_cell);

        mContext = context;
        mPerson = person;

        initRaterMark();
        initPersonText();
        setPerson(null);
        initRemoveButton(listener);

        initTagContainer();
    }

    private ImageView raterMark;
    private void initRaterMark() {

        raterMark = (ImageView) getViewById(R.id.rater_mark);

    }

    private TextView personText;
    private void initPersonText() {

        personText = (TextView) getViewById(R.id.person_text);

    }

    private TagContainer tagContainer;
    private void initTagContainer() {

        tagContainer = (TagContainer) getViewById(R.id.tag_container);
        tagContainer.setTagIds(mPerson.getTagIds(), false);
    }

    @Override
    public int getViewHeight() {

        if (tagContainer == null) return 50;

        return 50 + tagContainer.getViewHeight();
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

    private void initRemoveButton(final PostRemoveAnimationListener listener) {

        Button removeButton = (Button) getViewById(R.id.remove_button);

        if (listener == null) {
            removeButton.setVisibility(INVISIBLE);
        } else {
            removeButton.setVisibility(VISIBLE);
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    PersonCell.this.changeViewHeight(AnimateCondition.FROM_HEIGHT_TO_O,
                            true,
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                    listener.postAnimation(PersonCell.this);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            },
                    5);
                }
            });
        }
    }

    public interface PostRemoveAnimationListener {
        void postAnimation(PersonCell cell);
    }

    //TODO PersonSeenDialogを開いたとき、PersonCellにタグが反映されない不具合が生じている
}
