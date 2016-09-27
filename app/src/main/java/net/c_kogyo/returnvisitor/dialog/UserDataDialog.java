package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;


/**
 * Created by SeijiShii on 2016/09/26.
 */

public class UserDataDialog extends DialogFragment {

    public static UserDataDialog newInstance() {

        Bundle args = new Bundle();

        UserDataDialog fragment = new UserDataDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = LayoutInflater.from(getActivity()).inflate(R.layout.user_data_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setTitle(R.string.user_data);

        initUserNameText();

        builder.setNegativeButton(R.string.cancel_text, null);
        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (userNameText.getText() == null) return;

                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SharedPrefTags.RETURN_VISITOR_SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(Constants.SharedPrefTags.USER_NAME, userNameText.getText().toString());

                editor.apply();
            }
        });

        return builder.create();
    }

    private EditText userNameText;
    private void initUserNameText() {

        userNameText = (EditText) view.findViewById(R.id.user_name_text);

        SharedPreferences pref
                = getActivity().getSharedPreferences(Constants.SharedPrefTags.RETURN_VISITOR_SHARED_PREFS, Context.MODE_PRIVATE);
        String userName = pref.getString(Constants.SharedPrefTags.USER_NAME, null);

        if (userName == null) return;

        userNameText.setText(userName);
    }
}
