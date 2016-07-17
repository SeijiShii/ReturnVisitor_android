package net.c_kogyo.returnvisitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SeijiShii on 2016/07/16.
 */

public class LoginSelectDialog extends DialogFragment {

    private static Context mContext;

    public static LoginSelectDialog newInstance(Context context) {

        mContext = context;

        return new LoginSelectDialog();
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.login_select_dialog, null);
//
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.login_select_dialog, null);

        builder.setTitle(R.string.select_login);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dismiss();
            }
        });

        return builder.create();
    }
}

