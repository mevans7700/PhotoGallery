package com.evansappwriter.photogallery.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.util.Keys;
import com.evansappwriter.photogallery.util.Utils;


/**
 * Created by markevans on 7/8/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BASEACTIVITY";

    public SharedPreferences sharedPrefs;

    private static Handler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic);

        if (sharedPrefs == null) {
            sharedPrefs = getSharedPreferences(Keys.PREFS_NAME, Context.MODE_PRIVATE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Utils.setStrictMode(true);
    }

    /********************************/
    // DialogFragment Manager
    /********************************/

    /**
     * Used to post runnables to the main thread.
     *
     * @return the activity handler
     */
    public Handler getHandler() {
        if (sHandler == null) {
            sHandler = new Handler();
        }
        return sHandler;
    }

    public void showProgress(String message) {
        Bundle args = new Bundle();
        args.putString(Keys.DIALOG_MESSAGE_KEY, message);
        showDialogFragment(Keys.DIALOG_GENERAL_LOADING, args);
    }

    /**
     * show a general error with title, message and data customizable
     *
     * @param title   the title of the error popup
     * @param message the body of the error
     * @param data
     */
    protected void showError(String title, String message, Bundle data) {
        Bundle args = new Bundle();
        args.putString(Keys.DIALOG_TITLE_KEY, title);
        args.putString(Keys.DIALOG_MESSAGE_KEY, message);
        args.putBundle(Keys.DIALOG_DATA_KEY, data);
        showDialogFragment(Keys.DIALOG_GENERAL_ERROR, args);
    }

    /**
     * show a general message with title, message and data customizable
     *
     * @param title   the title of the message popup
     * @param message the body of the message
     * @param data
     */
    protected void showMessage(String title, String message, Bundle data) {
        Bundle args = new Bundle();
        args.putString(Keys.DIALOG_TITLE_KEY, title);
        args.putString(Keys.DIALOG_MESSAGE_KEY, message);
        args.putBundle(Keys.DIALOG_DATA_KEY, data);
        showDialogFragment(Keys.DIALOG_GENERAL_MESSAGE, args);
    }


    protected void showDialogFragment(final int id, final Bundle args) {
        showDialogFragment(id, args, false);
    }

    protected void showDialogFragment(final int id, final Bundle args, final boolean cancelable) {
        if (!isFinishing()) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    DialogFragment dialog;
                    switch (id) {
                        case Keys.DIALOG_GENERAL_LOADING:
                        case Keys.DIALOG_GENERAL_MESSAGE:
                        case Keys.DIALOG_GENERAL_ERROR:
                        default:
                            dialog = AlertDialogFragment.newInstance(id, args);
                    }
                    dialog.setCancelable(cancelable);
                    dialog.show(getSupportFragmentManager(), "dialog_" + id);
                }
            });
        }
    }

    protected void showDialogFragment(final int id, final Bundle args, final boolean cancelable, final AlertDialogFragment.OnDialogDoneListener l) {
        if (!isFinishing()) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    AlertDialogFragment dialog = AlertDialogFragment.newInstance(id, args);
                    dialog.setCancelable(cancelable);
                    dialog.setListener(l);
                    dialog.show(getSupportFragmentManager(), "dialog_" + id);
                }
            });
        }
    }

    private void showDialogFragment(final int id, final Bundle args, final boolean cancelable, final Fragment f) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    AlertDialogFragment dialog = AlertDialogFragment.newInstance(id, args);
                    dialog.setCancelable(cancelable);
                    dialog.setTargetFragment(f, 1);
                    dialog.show(getSupportFragmentManager(), "dialog_" + id);
                }
            }
        });
    }

    protected void dismissDialogFragment(final int id) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialogFragment dialog = (AlertDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog_" + id);
                if (dialog != null) {
                    dialog.dismissAllowingStateLoss();
                }
            }
        });
    }

    public void dismissProgress() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialogFragment popup = (AlertDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog_" + Keys.DIALOG_GENERAL_LOADING);
                if (popup != null) {
                    popup.dismissAllowingStateLoss();
                }
            }
        });
    }
}
