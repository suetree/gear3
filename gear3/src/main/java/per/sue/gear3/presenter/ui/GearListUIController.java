package per.sue.gear3.presenter.ui;

import android.widget.ListView;

import per.sue.gear3.R;
import per.sue.gear3.adapter.GearListAdapter;
import per.sue.gear3.bean.ListPage;
import per.sue.gear3.presenter.GearListPresenter;
import per.sue.gear3.widget.refresh.GearSwipeRefreshView;
import per.sue.gear3.widget.PageStatusLayout;


/**
 * Created by sure on 2017/8/22.
 */

public class GearListUIController<T> {
    PageStatusLayout pageStatusLayout;
    GearSwipeRefreshView refreshLayout;
    ListView listView;
    GearListAdapter<T> arrayListAdapter;
    GearListPresenter<ListPage<T>> gearListPresenter;

    private boolean enableLoadMore;

    private  OnLastResultListener onLastResultListener;


    public GearListUIController(PageStatusLayout pageStatusLayout, GearSwipeRefreshView refreshLayout, ListView listView, GearListAdapter<T> arrayListAdapter, GearListPresenter<ListPage<T>> gearListPresenter) {
        this.pageStatusLayout = pageStatusLayout;
        this.refreshLayout = refreshLayout;
        this.listView = listView;
        this.arrayListAdapter = arrayListAdapter;
        this.gearListPresenter = gearListPresenter;

        this.enableLoadMore = true;

        initView();
        initPresenter();
    }

    private  void initView(){
        refreshLayout.setCanLoadMore(this.enableLoadMore);
        listView.setAdapter(arrayListAdapter);


        // 设置下拉进度的背景颜色，默认就是白色的
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        //refreshLayout.setItemCount(10);
        // 设置下拉进度的主题颜色
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        refreshLayout.setOnRefreshListener(new GearSwipeRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(GearSwipeRefreshView gearSwipeRefreshView) {
                gearListPresenter.refresh();
            }

            @Override
            public void onLoadMore(GearSwipeRefreshView gearSwipeRefreshView) {
                gearListPresenter.loadMore();
            }
        });
    }

    private void initPresenter(){
        this.gearListPresenter.setListResultView(new GearListPresenter.ListResultView<ListPage<T>>() {
            @Override
            public void onSuccessRefresh(ListPage<T> result) {
                if(null != pageStatusLayout) pageStatusLayout.showContent();
                if(null != result && result.content.size() > 0){
                    arrayListAdapter.setList(result.content);

                }else{
                    if(null != pageStatusLayout){
                        pageStatusLayout.showEmpty();
                    }
                }

                if(null != refreshLayout){
                    if(enableLoadMore){
                        if(null != result && !result.last){
                            refreshLayout.setCanLoadMore(true);
                        }else{
                            refreshLayout.setCanLoadMore(false);
                        }
                    }

                    refreshLayout.setRefreshing(false);
                    refreshLayout.setLoadMoreing(false);
                }

                if(null != onLastResultListener){
                    onLastResultListener.onResultChange(result);
                }
            }

            @Override
            public void onSuccessLoadModre(ListPage<T> result) {
                if(null != result && result.content.size() > 0){
                    arrayListAdapter.addList(result.content);
                }

                if(null != refreshLayout){
                    if(enableLoadMore){
                        if(null != result && !result.last){
                            refreshLayout.setCanLoadMore(true);
                        }else{
                            refreshLayout.setCanLoadMore(false);
                        }
                    }
                    refreshLayout.setRefreshing(false);
                    refreshLayout.setLoadMoreing(false);
                }

                if(null != onLastResultListener){
                    onLastResultListener.onResultChange(result);
                }
            }

            @Override
            public void onError(int code, String message) {
                if(null != refreshLayout){
                    refreshLayout.setRefreshing(false);
                    refreshLayout.setLoadMoreing(false);
                }

                if(gearListPresenter.isRefresh()){
                    if(null != pageStatusLayout){
                        pageStatusLayout.showFailed(message, null);
                    }
                }

            }
        });
    }


    public void loadData(){
        pageStatusLayout.showLoading();
        gearListPresenter.refresh();
    }

    public void setOnLastResultListener(OnLastResultListener onLastResultListener) {
        this.onLastResultListener = onLastResultListener;
    }

    public interface  OnLastResultListener<T>{

        void onResultChange(ListPage<T> lastResult);
    }


    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
    }
}
