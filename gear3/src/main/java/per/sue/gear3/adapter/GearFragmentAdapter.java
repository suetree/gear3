package per.sue.gear3.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by SUE on 2016/7/21 0021.
 */
public class GearFragmentAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "GearFragmentAdapter";

    private List<GearFragmentModel> list;

    public GearFragmentAdapter(FragmentManager fm, List<GearFragmentModel> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e(TAG, "getItem()" + "position=" + position);
        Fragment fragment = null;

        try {
            fragment   = (Fragment) this.list.get(position).cls.newInstance();
            fragment.setArguments(this.list.get(position).bundle);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }



    public int getCount() {
        return this.list.size();
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return this.list.get(position).title;
    }


    public static class GearFragmentModel{

        public Class cls;
        public String title;
        public int imageResId;
        public Bundle bundle;

        public GearFragmentModel(Class cls,Bundle bundle, String title) {
           this(cls, bundle, title, 0);
        }

        public GearFragmentModel(Class cls,Bundle bundle, String title, int imageResId) {
            this.cls = cls;
            this.bundle = bundle;
            this.title = title;
            this.imageResId = imageResId;
        }
    }


}
