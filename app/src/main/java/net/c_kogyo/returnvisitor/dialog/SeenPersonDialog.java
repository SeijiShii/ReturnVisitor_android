package net.c_kogyo.returnvisitor.dialog;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.PersonCell;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/23.
 */

public class SeenPersonDialog extends DialogFragment {

    private Context mContext;
    private static Visit mVisit;
    private static OnOkClickListener mListener;
    private ArrayList<String> createdPersonIds;

    private View.OnClickListener seenCellOnClickListener, suggCellOnClickListener;

    private ArrayList<String> suggestedPersonIds;

    public static SeenPersonDialog getInstance(Visit visit, OnOkClickListener listener) {

        mListener = listener;
        mVisit = visit;

        return new SeenPersonDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();

        createdPersonIds = new ArrayList<>();

        initSuggestedIds();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.seen_person_dialog);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.seen_person_dialog, null);
        builder.setView(v);

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onOkClick();
            }
        });

        builder.setNegativeButton(R.string.cancel_text, null);

        initSeenPersonContainer();
        initNewPersonButton();
        initSuggestedPersonContainer();

        return builder.create();
    }

    private void initNewPersonButton() {

        Button addPersonButton = (Button) v.findViewById(R.id.add_person_button);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PersonDialog.getInstance(null, new PersonDialog.OnOkClickListener() {
                    @Override
                    public void onClick(Person person) {

                        // 新規作成したということは「会えた」ということ
                        mVisit.addPersonId(person.getId());

                        // 提案リストに追加
                        createdPersonIds.add(person.getId());
                        initSuggestedIds();

                        addPersonToContainer(person.getId());
                    }
                }).show(getFragmentManager(), null);
            }
        });
    }

    private void initSuggestedIds() {

        // 過去のあえた人たちのid
        suggestedPersonIds = RVData.getInstance().placeList.getHistoricalPersonIds(mVisit.getPlaceId());
        // 今回作成された人を追加
        suggestedPersonIds.addAll(createdPersonIds);

    }

    private LinearLayout seenPersonContainer;
    private void initSeenPersonContainer() {

        seenCellOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PersonCell cell1 = (PersonCell) view;
                String personId = cell1.getPerson().getId();

                mVisit.getPersonIds().remove(personId);
                cell1.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O, true, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        seenPersonContainer.removeView(cell1);

                        String personId = cell1.getPerson().getId();
                        addToSuggestedContainer(personId, BaseAnimateView.InitialHeightCondition.FROM_0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }, 5);
            }
        };

        suggCellOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PersonCell cell1 = (PersonCell) view;
                String personId = cell1.getPerson().getId();

                cell1.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O, true, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        suggestedPersonContainer.removeView(cell1);

                        String personId = cell1.getPerson().getId();
                        addPersonToContainer(personId);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }, 5);
            }
        };

        seenPersonContainer = (LinearLayout) v.findViewById(R.id.seen_person_container);

        for ( String id : mVisit.getPersonIds() ) {

            Person person = RVData.getInstance().personList.getById(id);
            if (person != null) {

                PersonCell cell = new PersonCell(mContext, person, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT);
                seenPersonContainer.addView(cell);
                cell.setOnClickListener(seenCellOnClickListener);
            }
        }
    }

    private void addPersonToContainer(String personId) {

        Person person = RVData.getInstance().personList.getById(personId);
        if ( person == null ) return;

        mVisit.getPersonIds().add(personId);

        PersonCell cell = new PersonCell(mContext, person, BaseAnimateView.InitialHeightCondition.FROM_0);
        seenPersonContainer.addView(cell);
        cell.setOnClickListener(seenCellOnClickListener);
    }

    private LinearLayout suggestedPersonContainer;
    private void initSuggestedPersonContainer() {

        suggestedPersonContainer = (LinearLayout) v.findViewById(R.id.suggested_persons_container);

        // TODO 提案リストにある人を追加する
        for ( String id : suggestedPersonIds ) {

            addToSuggestedContainer(id, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT);
        }
    }

    private void addToSuggestedContainer(String personId, BaseAnimateView.InitialHeightCondition initCondition) {

        // 会えた人に含まれるなら描画しない
        if ( mVisit.getPersonIds().contains(personId)) return;

        Person person = RVData.getInstance().personList.getById(personId);
        if ( person != null ) {

            PersonCell cell = new PersonCell(mContext, person, initCondition);
            suggestedPersonContainer.addView(cell);
            cell.setOnClickListener(suggCellOnClickListener);
        }

    }

    public interface OnOkClickListener {

        void onOkClick();
    }
}
