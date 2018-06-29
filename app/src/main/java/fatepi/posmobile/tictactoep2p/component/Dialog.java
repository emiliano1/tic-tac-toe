package fatepi.posmobile.tictactoep2p.component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.concurrent.Callable;

/**
 * Created by magno on 10/12/15.
 */
public class Dialog {

    public static AlertDialog alert(Activity activity, String title, String msg){
        return closeActivityAlert(activity, title, msg, false);
    }

    public static AlertDialog closeActivityAlert(final Activity activity, String title, String msg){
        return closeActivityAlert(activity, title, msg, true);
    }

    public static AlertDialog closeActivityAlert(final Activity activity, String title, String msg, final boolean closeAct){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(msg)
                .setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if(closeAct)
                    activity.finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static ProgressDialog indeterminateProgress(Activity activity, String title, String msg, boolean cancelable) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(activity, title, msg, true);
        ringProgressDialog.setCancelable(cancelable);
        return ringProgressDialog;
    }

    public static ProgressDialog indeterminateProgress(Activity activity, String title, String msg) {
        return Dialog.indeterminateProgress(activity, title, msg, false);
    }

    public static AlertDialog confirm(Context context, String title, String msg, final Callable callSim, final Callable callNao){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(msg)
                .setTitle(title);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if(callSim != null){
                    try {
                        callSim.call();
                    } catch (Exception e) {}
                }
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if(callNao != null){
                    try {
                        callNao.call();
                    } catch (Exception e) {}
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static AlertDialog handleActivityAlert(final Activity activity, String title, String msg, final Callable call){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(msg)
                .setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                try {
                    call.call();
                } catch (Exception e) {
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

}
