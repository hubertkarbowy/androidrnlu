package pl.hubertkarbowy.androidrnlu.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 * Created by hubert on 15.04.18.
 */

public class UserInteraction {

    public static void alertView( Context ctxt, String msg ) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctxt).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
