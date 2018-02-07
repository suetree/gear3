package per.sue.gear3.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import per.sue.gear3.R;
import per.sue.gear3.utils.GearLog;
import per.sue.gear3.utils.StatusBarCompat;
import per.sue.gear3.widget.PageStatusLayout;


/**
 * Created by SUE on 2016/7/8 0008.
 */
public abstract class GearActivity extends AppCompatActivity implements  GearView{

    public  final String TAG =  getClass().getSimpleName();
    private View mContentView;
    private PageStatusLayout gearBaseFrameLayout;
    private Toolbar gearBaseToolbar;
    protected ViewGroup gearBaseContainLayout;
    private  long loadTimes;
    protected   GearViewHelper gearViewHelper;

    private ViewStub gearNormalViewStub;
    private ViewStub gearDrawerLayoutViewStub;

    protected DrawerLayout gearDrawerLayout;
    protected FrameLayout gearLeftFrameLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    private AnimationDrawable mAnimationDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GearLog.i(TAG, " onCreate start loadTimes = " + (loadTimes = System.currentTimeMillis()));
        if(!isCustomLayout()){
            setContentView(R.layout.activity_gear);
            initComponentViews();
        }else{
            setContentView(getLayoutResId());
        }
        ButterKnife.bind(this);
        gearViewHelper = new GearViewHelper(this, getSupportFragmentManager());
    }

    private void initComponentViews() {
        gearBaseContainLayout =  (ViewGroup)findViewById(R.id.gearBaseContainLayout);
        gearBaseToolbar = (Toolbar) findViewById(R.id.gearToolbar);
        initToolbar();

        gearNormalViewStub =  (ViewStub)findViewById(R.id.gearNormalViewStub);
        gearDrawerLayoutViewStub =  (ViewStub)findViewById(R.id.gearDrawerLayoutViewStub);
        if(loadDrawerLayout()){
            gearDrawerLayout = (DrawerLayout)gearDrawerLayoutViewStub.inflate();
            //gearDrawerLayout = (DrawerLayout)view.findViewById(R.id.gearDrawerLayout);
            gearLeftFrameLayout = (FrameLayout)gearDrawerLayout.findViewById(R.id.gearLeftFrameLayout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, gearDrawerLayout, gearBaseToolbar, R.string.drawer_open, R.string.drawer_close);
            actionBarDrawerToggle.syncState();
            gearDrawerLayout.addDrawerListener(actionBarDrawerToggle);
            gearBaseFrameLayout = (PageStatusLayout)gearDrawerLayout. findViewById(R.id.gearFrameLayout);

        }else{
            gearBaseFrameLayout = (PageStatusLayout)gearNormalViewStub.inflate();
             //= (PageStatusLayout)view. findViewById(R.id.gearFrameLayout);
        }

        mContentView = getLayoutInflater().inflate(getLayoutResId(), null);

        gearBaseFrameLayout.addView(mContentView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        gearBaseFrameLayout.showContent();
        StatusBarCompat.compat(this);
    }
    private void initToolbar() {
        setSupportActionBar(gearBaseToolbar);
        gearBaseToolbar.setPopupTheme(R.style.PopupMenu);
        gearBaseToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackView());
        gearBaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitleBar(getTitle().toString());
    }

    public boolean showBackView(){
        return false;
    }

    public void setTitleBar(int resId) {
        setTitleBar(getResources().getString(resId));
    }

    public void setTitleBar(String title) {
        gearBaseToolbar.setTitle(title);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onInitialize(savedInstanceState);
        GearLog.i(TAG, "onPostCreate  end loadTimes = " + (  System.currentTimeMillis() - loadTimes));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GearLog.e(TAG, "onDestroy " );
        ButterKnife.unbind(this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    @Override
    public Context getContext() {
        return this;
    }

    public ViewGroup getBaseContainLayout() {
        return gearBaseContainLayout;
    }

    public boolean isCustomLayout(){
        return false;
    }

    public boolean loadDrawerLayout(){
        return  false;
    }

    public Toolbar getGearBaseToolbar() {
        return gearBaseToolbar;
    }

    /**
     * 监听点击外面关闭小键盘
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText || v instanceof Button)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
