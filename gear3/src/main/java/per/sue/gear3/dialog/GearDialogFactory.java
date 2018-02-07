package per.sue.gear3.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

/**
 * Created by sure on 2017/8/14.
 */

public class GearDialogFactory {



    public static void showTipDialog(Activity activity, String title, Runnable runnable){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(null != runnable){
                    runnable.run();
                }
            }
        });

        builder.show();
    }

    public static void showConfirmDialog(Activity activity, String title,  String message, Runnable runnable){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(null != runnable){
                    runnable.run();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public static void showConfirmDialog(Activity activity, String title,  String message, Runnable sureRunnable, Runnable cancelRunnable ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(null != sureRunnable){
                    sureRunnable.run();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(null != cancelRunnable){
                    cancelRunnable.run();
                }
            }
        });
        builder.show();

    }

    public static AlertDialog showConfirmDialog(Activity activity, String title, String message,  View view ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if(!TextUtils.isEmpty(message)){
            builder.setMessage(message);
        }
        builder.setView(view);
        return builder.show();

    }

    public static AlertDialog showComtusUIDialog(Activity activity, String title,  View view ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setView(view);
        return builder.show();

    }

}


