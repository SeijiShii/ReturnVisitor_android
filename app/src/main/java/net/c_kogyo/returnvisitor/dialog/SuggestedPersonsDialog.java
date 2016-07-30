package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private static Place mPlace;
    private static OnSeenPersonAddedListener mListener;

    public static SuggestedPersonsDialog getInstance(Place place, OnSeenPersonAddedListener listener) {

        mPlace = place;
        mListener = listener;

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
        refreshSuggestedPersons();
        initAddPersonButton();

        return builder.create();
    }

    private void initSearchText() {

        EditText searchText = (EditText) v.findViewById(R.id.search_text);
    }


    private ListView personList;
    private void initPersonList() {

        personList = (ListView) v.findViewById(R.id.persons_list);

    }


    private void refreshSuggestedPersons() {

        ArrayList<Person> persons = RVData.getInstance().personList.getPersons(mPlace.getPersonIds());

        personList.setAdapter(new SuggestedPersonAdapter(persons));
    }

    private void initAddPersonButton() {

        RelativeLayout addPersonButton = (RelativeLayout) v.findViewById(R.id.add_person_button);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PersonDialog.getInstance(null, new PersonDialog.OnOkClickListener() {
                    @Override
                    public void onClick(Person person) {

                        mListener.onAdded(person.getId());
                        dismiss();
                    }
                }).show(getFragmentManager(), null);
            }
        });
    }

    // 会えた人が追加される状況は二つ
    // SuggestListのアイテムがクリックされる
    // PersonDialogで新しい人を作って帰ってくる
    public interface OnSeenPersonAddedListener {

        void onAdded(String personId);
    }

    class SuggestedPersonAdapter extends BaseAdapter {

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
                view = new PersonCell(getActivity(), (Person)getItem(i), BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT);
            } else {
                ((PersonCell) view).setPerson((Person) getItem(i));
            }

            return view;
        }
    }

}
