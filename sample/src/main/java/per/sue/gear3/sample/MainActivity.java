package per.sue.gear3.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;


import butterknife.Bind;
import butterknife.ButterKnife;
import per.sue.gear3.ui.GearActivity;
import per.sue.gear3.widget.recycler.SpacesItemDecoration;

/**
 * Created by sure on 2018/2/8.
 */

public class MainActivity extends GearActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    MenuAdapter menuAdapter;
    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onInitialize(@Nullable Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(8));
        menuAdapter = new MenuAdapter(getContext());
        ArrayList<MainMenu> list = new ArrayList<>();
        list.add(new MainMenu("导航"));
        list.add(new MainMenu("菜单"));
        menuAdapter.setList(list);
        recyclerView.setAdapter(menuAdapter);
    }

}
