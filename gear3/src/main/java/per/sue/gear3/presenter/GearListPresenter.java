package per.sue.gear3.presenter;

import android.content.Context;


import per.sue.gear3.exception.GearException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by SUE on 2016/7/20 0020.
 */
public   class GearListPresenter<T> extends AbsPresenter  {

    protected boolean isRefresh;
    private boolean isWithLoad;
    protected int pageNum;
    protected Subscription subscription;
    protected ListResultView<T> listResultView;
    private Observable observable;

    private Runnable questBeforeRunable;


    public void setListResultView(ListResultView<T> listResultView) {
        this.listResultView = listResultView;
    }

    public void refreshWithLoading(){
        isRefresh = true;
        isWithLoad =  true;
        pageNum = 0;
       // listResultView.showLoading();
        query();
    }

    public void refresh(){
        isRefresh = true;
        isWithLoad = false;
        pageNum = 0;
        query();
    }

    public void loadMore(){
        isRefresh = false;
        isWithLoad = false;
        pageNum ++;
        query();
    }

    public void cancelRequest(){
        if(null != subscription)subscription.unsubscribe();
    }

    public void query() {
        if(null != questBeforeRunable){
            questBeforeRunable.run();
        }
        if(null == observable )return;
        if(null != subscription)subscription.unsubscribe();
        subscription  = getObservable().subscribe(new Subscriber<T>() {
            @Override
            public void onCompleted() {
                //listResultView .onCompleted();
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(e instanceof GearException){
                    GearException gearException = (GearException)e;
                    listResultView.onError(gearException.getCode(), e.getMessage());
                }else{
                    listResultView.onError(-1, e.getMessage());
                }

                if(isWithLoad){
                  //  listResultView.onLoadFailed();
                }
            }

            @Override
            public void onNext(T recommendVideos) {
                if(null != listResultView){
                    if(isRefresh){
                        listResultView .onSuccessRefresh(recommendVideos);
                    }else{
                        listResultView.onSuccessLoadModre(recommendVideos);
                    }

                }

            }
        });

        addSubscription(subscription);
    }


    public int getPageNum() {
        return pageNum;
    }

    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    public Observable getObservable() {
        return observable;
    }

    public void setQuestBeforeRunable(Runnable questRunable) {
        this.questBeforeRunable = questRunable;
    }

    public interface  ListResultView<T>  {
         void onSuccessRefresh(T result);
         void onSuccessLoadModre(T result);

          void onError(int code, String message);
    }

    public class  AbsListResultView<T> implements ListResultView<T> {


        @Override
        public void onSuccessRefresh(T result) {

        }

        @Override
        public void onSuccessLoadModre(T result) {

        }

        @Override
        public void onError(int code, String message) {

        }
    }

    public boolean isWithLoad() {
        return isWithLoad;
    }

    public boolean isRefresh() {
        return isRefresh;
    }
}
