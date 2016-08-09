package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.activity.PlacementActivity;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Placement;
import net.c_kogyo.returnvisitor.data.Visit;

/**
 * Created by SeijiShii on 2016/07/30.
 */

public class PlacementSelectDialog extends DialogFragment {

    static public PlacementSelectDialog getInstance() {

        return new PlacementSelectDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        v = LayoutInflater.from(getActivity()).inflate(R.layout.placement_select_dialog, null);

        builder.setView(v);
        builder.setTitle(R.string.placement_select);
        builder.setNegativeButton(R.string.cancel_text, null);

        initPlacementList();

        return builder.create();
    }

    private void initPlacementList() {

        ListView placementList = (ListView) v.findViewById(R.id.placement_list);
        String[] plcArray = getActivity().getResources().getStringArray(R.array.placement_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, plcArray);
        placementList.setAdapter(adapter);

        placementList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), PlacementActivity.class);
                intent.putExtra(Placement.PLACEMENT_CATEGORY, Placement.Category.getEnum(i).toString());
                intent.putExtra(Placement.PLACEMENT, Constants.PlacementCode.PLACEMENT_REQUEST_CODE);

                getActivity().startActivityForResult(intent, Constants.PlacementCode.PLACEMENT_REQUEST_CODE);

                dismiss();
            }
        });

    }



}
