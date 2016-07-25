package net.c_kogyo.returnvisitor.dialog;

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
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.Person;
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

    private ArrayList<String> pastPersonIds;
    private ArrayList<String> createdPersonIds;

    public static SeenPersonDialog getInstance(Visit visit, OnOkClickListener listener) {

        mListener = listener;
        mVisit = visit;

        return new SeenPersonDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();

        initPastPersonIds();
        createdPersonIds = new ArrayList<>();

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
                        createdPersonIds.add(person.getId());
                        addPersonToContainer(person);
                    }
                }).show(getFragmentManager(), null);
            }
        });
    }

    private void initPastPersonIds() {

        pastPersonIds = new ArrayList<>();
    }

    private LinearLayout seenPersonContainer;
    private void initSeenPersonContainer() {

        seenPersonContainer = (LinearLayout) v.findViewById(R.id.seen_person_container);
    }

    private void addPersonToContainer(Person person) {

        seenPersonContainer.addView(new PersonCell(mContext, person, BaseAnimateView.InitialHeightCondition.FROM_0));
    }

    private LinearLayout suggestedPersonContainer;
    private void initSuggestedPersonContainer() {

        suggestedPersonContainer = (LinearLayout) v.findViewById(R.id.suggested_persons_container);

        // TODO 過去にこの場所で会えた人を追加する
        for ( String id : pastPersonIds ) {

        }

        // TODO 今回作成した人が「会えた人リスト」に追加されていないならここに追加する
        for (String id : createdPersonIds) {
            if (!mVisit.getPersonIds().contains(id)) {

            }
        }
    }

    public interface OnOkClickListener {

        void onOkClick();
    }
}
