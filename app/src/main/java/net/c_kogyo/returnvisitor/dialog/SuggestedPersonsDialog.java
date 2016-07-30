package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

public class SuggestedPersonsDialog extends DialogFragment {

    private static Visit mVisit; // すでにVisitに追加されている人を除外するため
    private static Place mPlace; // この場所で今までに出会った人を取得するため
    private static OnNewPersonAddedListener mAddedListener;
    private static OnPersonSelectedListener mSelectedListener;
    private static ArrayList<String> mCreatePersonIds;

    public static SuggestedPersonsDialog getInstance(Visit visit,
                                                     Place place,
                                                     ArrayList<String> createdPersonIds,
                                                     OnNewPersonAddedListener addedListener,
                                                     OnPersonSelectedListener selectedListener) {

        mVisit = visit;
        mPlace = place;
        mAddedListener = addedListener;
        mSelectedListener = selectedListener;
        mCreatePersonIds = new ArrayList<>(createdPersonIds);

        return new SuggestedPersonsDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        v = LayoutInflater.from(getActivity()).inflate(R.layout.suggested_persons_dialog, null);
        builder.setView(v);

        initSearchText();
        initPersonList();
        refreshSuggestedPersons(null);
        initAddPersonButton();

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
            personIds.addAll(mCreatePersonIds);
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

                PersonDialog.getInstance(null, new PersonDialog.OnOkClickListener() {
                    @Override
                    public void onClick(Person person) {

                        mAddedListener.onAdded(person.getId());
                        dismiss();
                    }
                }).show(getFragmentManager(), null);
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
