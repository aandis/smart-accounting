package help.smartbusiness.smartaccounting.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by gamerboy on 30/5/16.
 */
public class YesNoDialog extends DialogFragment {

    public static final String TAG = YesNoDialog.class.getCanonicalName();
    public static final String TITLE = "title";
    public static final String MESSAGE = "msg";

    public static YesNoDialog newInstance(String title, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, msg);
        YesNoDialog dialog = new YesNoDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    public YesNoDialog() {
    }

    private DialogClickListener mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(TITLE, "");
        String message = args.getString(MESSAGE, "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onYesClick();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onNoClick();
                    }
                })
                .create();
    }

    public void setCallback(DialogClickListener callback) {
        this.mCallback = callback;
    }

    public interface DialogClickListener {
        void onYesClick();

        void onNoClick();
    }
}