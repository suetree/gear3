package per.sue.gear3.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by SUE on 2016/7/7 0007.
 */
public interface GearView {
    public abstract int getLayoutResId();
    public abstract void onInitialize(@Nullable Bundle savedInstanceState);
    public Activity getActivity();
    Context getContext();


}
