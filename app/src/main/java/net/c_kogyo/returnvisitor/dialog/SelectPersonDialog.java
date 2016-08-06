package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

public class SelectPersonDialog extends DialogFragment {

    private static Visit mVisit; // すでにVisitに追加されている人を除外するため
    private static Place mPlace; // この場所で今までに出会った人を取得するため
    private static OnPersonSelectedListener mSelectedListener;
    private static ArrayList<String> mCreatePersonIds;

    public static SelectPersonDialog getInstance(Visit visit,
                                                 Place place,
                                                 ArrayList<String> createdPersonIds,
                                                 OnPersonSelectedListener selectedListener) {

        mVisit = visit;
        mPlace = place;
        mSelectedListener = selectedListener;
        mCreatePersonIds = new ArrayList<>(createdPersonIds);

        return new SelectPersonDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        v = LayoutInflater.from(getActivity()).inflate(R.layout.select_persons_dialog, null);
        builder.setView(v);

        initSearchText();
        initPersonList();
        refreshSuggestedPersons(null);
        initAddPersonButton();

        builder.setTitle(R.string.select_seen_person_dialog);
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

    private ListView personList;
    private void initPersonList() {

        personList = (ListView) v.findViewById(R.id.persons_list);
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedListener.onSelected(persons.get(i).getId());
                dismiss();
            }
        });

    }

    private ArrayList<Person> persons;

    /**
     *
     * @param searchString nullまたは空白ならPlaceの過去履歴の人をセット
     */
    private void refreshSuggestedPersons(String searchString) {

        ArrayList<String> personIds;

        if (searchString == null || isStringAllBlank(searchString)) {
            personIds = mPlace.getPersonIds();

            // Merge without duplicates
            for ( String id : mCreatePersonIds ) {
                if (!personIds.contains(id)) {
                    personIds.add(id);
                }
            }

        } else {
            personIds = RVData.getInstance().personList.getSearchedPersonIds(searchString, getActivity());

        }
        personIds.removeAll(mVisit.getPersonIds());

        persons = RVData.getInstance().personList.getPersons(personIds);

        personList.setAdapter(new SuggestedPersonAdapter(persons));
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
                getActivity().startActivityForResult(newPersonIntent, Constants.PersonCode.ADD_PERSON_REQUEST_CODE);

                dismiss();

            }
        });
    }

    // 会えた人が追加される状況は二つ
    // SuggestListのアイテムがクリックされる
    // PersonDialogで新しい人を作って帰ってくる
    public interface OnNewPersonAddedListener {

        // ここで返されたのがあちらでcreatedPersonIdsに加えられるのだけどね。
        void onAdded(String personId);
    }

    public interface OnPersonSelectedListener {

        void onSelected(String personId);
    }

    private class SuggestedPersonAdapter extends BaseAdapter {

        private ArrayList<Person> mPersons;

        SuggestedPersonAdapter(ArrayList<Person> persons) {

            mPersons = persons;
        }

        @Override
        public int getCount() {
            return mPersons.size();
        }

        @Override
        public Object getItem(int i) {
            return mPersons.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = new PersonCell(getActivity(),
                        (Person) getItem(i),
                        BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT,
                        null);
            } else {
                ((PersonCell) view).setPerson((Person) getItem(i));
            }

            return view;
        }
    }

}
