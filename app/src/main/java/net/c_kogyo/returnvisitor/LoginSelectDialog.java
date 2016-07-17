package net.c_kogyo.returnvisitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by SeijiShii on 2016/07/16.
 */

public class LoginSelectDialog extends DialogFragment {

    private static Context mContext;
    private static MapActivity.OnGoogleSignInClickListener mOnGoogleSignInListener;
    private static MapActivity.OnFBLoinClickListener mOnFBLoinClickListener;
    private static MapActivity.EmailLoginClickListener mEmailLoginClickListener;

    private View view;


    public static LoginSelectDialog newInstance(Context context,
                                                MapActivity.OnGoogleSignInClickListener onGoogleSignInListener,
                                                MapActivity.OnFBLoinClickListener onFBLoinClickListener,
                                                MapActivity.EmailLoginClickListener emailLoginClickListener) {

        mContext = context;
        mOnGoogleSignInListener = onGoogleSignInListener;
        mOnFBLoinClickListener = onFBLoinClickListener;
        mEmailLoginClickListener = emailLoginClickListener;

        return new LoginSelectDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        FacebookSdk.sdkInitialize(mContext);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        view = LayoutInflater.from(mContext).inflate(R.layout.login_select_dialog, null);

        createEmailText();
        createPasswordText();
        createLoginButton();
        createGoogleLoginButton();
        createFBLoginButton();

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

    private EditText emailText;
    private void createEmailText() {

        emailText = (EditText) view.findViewById(R.id.email_text);

    }

    private EditText passwordText;
    private void createPasswordText() {
        passwordText = (EditText) view.findViewById(R.id.password_text);
    }

    private void createLoginButton() {

        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEmailLoginClickListener.onClick(emailText.getText().toString(), passwordText.getText().toString());
                dismiss();

            }
        });
    }

    private void createGoogleLoginButton() {

        Button gLoginButton = (Button) view.findViewById(R.id.google_login_button);
        gLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOnGoogleSignInListener.onClick();
                dismiss();
            }
        });
    }


    private void createFBLoginButton() {

        Button fbLoginButton = (Button) view.findViewById(R.id.fb_login_button);

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOnFBLoinClickListener.onClick();
                dismiss();
            }
        });

//        CallbackManager callbackManager = CallbackManager.Factory.create();
//        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
//        fbLoginButton.setReadPermissions("email", "public_profile");
//        fbLoginButton.registerCallback(callbackManager, mFacebookCallback);

    }


}

