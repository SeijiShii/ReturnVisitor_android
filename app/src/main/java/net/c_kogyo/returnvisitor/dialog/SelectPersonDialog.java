package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.activity.PersonActivity;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.PersonCell;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/29.
 */

public class SelectPersonDialog extends DialogFragment{

    private static Visit mVisit; // すでにVisitに追加されている人を除外するため
    private static Place mPlace; // この場所で今までに出会った人を取得するため
    private static OnPersonSelectedListener mSelectedListener;

    public static SelectPersonDialog getInstance(Visit visit,
                                                 Place place,
                                                 OnPersonSelectedListener selectedListener) {

        mVisit = visit;
        mPlace = place;
        mSelectedListener = selectedListener;

        return new SelectPersonDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        v = LayoutInflater.from(getActivity()).inflate(R.layout.select_persons_dialog, null);
        builder.setView(v);

        initSearchText();
        initPersonContainer();
        refreshSuggestedPersons(null);
        initAddPersonButton();

        builder.setTitle(R.string.select_seen_person_dialog);
        builder.setMessage(R.string.select_seen_person_message);

        builder.setNegativeButton(R.string.cancel_text, null);

        return builder.create();
    }

    private void initSearchText() {

        EditText searchText = (EditText) v.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                refreshSuggestedPersons(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    private ListView personList;
//    private void initPersonList() {
//
//        personList = (ListView) v.findViewById(R.id.persons_list);
//        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mSelectedListener.onSelected(persons.get(i).getId());
//                dismiss();
//            }
//        });
//
//    }

    private LinearLayout personContainer;
    private void initPersonContainer() {

        personContainer = (LinearLayout) v.findViewById(R.id.person_container);

    }

    private ArrayList<Person> persons;

    /**
     *
     * @param searchString nullまたは空白ならPlaceの過去履歴の人をセット
     */
    private void refreshSuggestedPersons(String searchString) {

        ArrayList<String> personIds = new ArrayList<>();

        if (searchString == null || isStringAllBlank(searchString)) {

            if (mPlace != null) {

                personIds = mPlace.getPersonIds();

            }

        } else {
            personIds = RVData.getInstance().personList.getSearchedPersonIds(searchString, getActivity());

        }
        personIds.removeAll(mVisit.getPersonIds());
        persons = RVData.getInstance().personList.getList(personIds);
        setPersonCells();
    }

    private boolean isStringAllBlank(String string) {

        return string.trim().length() <= 0;

    }

    private void initAddPersonButton() {

        RelativeLayout addPersonButton = (RelativeLayout) v.findViewById(R.id.add_person_button);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newPersonIntent = new Intent(getActivity(), PersonActivity.class);
                newPersonIntent.putExtra(Constants.REQUEST_CODE, Constants.PersonCode.ADD_PERSON_REQUEST_CODE);
                getActivity().startActivityForResult(newPersonIntent, Constants.PersonCode.ADD_PERSON_REQUEST_CODE);

                dismiss();

            }
        });
    }

    private void setPersonCells() {

        personContainer.removeAllViews();

        for ( Person person : persons ) {

            final PersonCell cell = new PersonCell(getActivity(), person, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT, null);
            personContainer.addView(cell);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedListener.onSelected(cell.getPerson().getId());
                    dismiss();
                }
            });

            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra(Person.PERSON, cell.getPerson().getId());
                    intent.putExtra(Constants.REQUEST_CODE, Constants.PersonCode.EDIT_PERSON_REQUEST_CODE);
                    getActivity().startActivityForResult(intent, Constants.PersonCode.EDIT_PERSON_REQUEST_CODE);

                    dismiss();

                    return true;
                }
            });

            // タッチ時に半透明にする　振り分けや伝播の仕方を間違えると他のリスナが呼ばれない
            cell.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    cell.setAlpha(0.5f);
                    return false;
                }
            });

        }

    }

    public interface OnPersonSelectedListener {

        void onSelected(String personId);
    }

}
