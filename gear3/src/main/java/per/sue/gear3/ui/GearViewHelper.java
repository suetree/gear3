package per.sue.gear3.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by sure on 2017/11/11.
 */

public class GearViewHelper {
    protected ProgressDialog progressDialog;;
    private FragmentManager fragmentManager;
    private Activity activity;

    public GearViewHelper(Activity activity, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    public void addFragment(int containerViewId, Fragment fragment ) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    public void addFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }

    public void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    public  void showProgressDialog(String tip) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
        progressDialog.setMessage(tip);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
