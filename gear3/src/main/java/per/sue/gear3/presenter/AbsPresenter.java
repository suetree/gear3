package per.sue.gear3.presenter;

import java.util.ArrayList;

import per.sue.gear3.utils.GearLog;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/4/22
*/
public class AbsPresenter implements Presenter {

    protected final String TAG = getClass().getSimpleName();

    protected ArrayList<Subscription> subscriptionArrayList = new ArrayList<>();

    public void cancelRequest(){
        if(null != subscriptionArrayList){
            for(Subscription subscription : subscriptionArrayList){
                if(null != subscription && !subscription.isUnsubscribed() )
                subscription.unsubscribe();
            }
        }
        subscriptionArrayList = new ArrayList<>();
    }

    public void addSubscription(Subscription subscription){
        subscriptionArrayList.add(subscription);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }


    protected  Observable.Transformer schedulersTransformer() {
        return new Observable.Transformer() {

            @Override
            public Object call(Object observable) {
                return ((Observable)  observable)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @Override
    public void destroy() {
        GearLog.e(this.getClass().getSimpleName(), "destroy()");
        cancelRequest();
    }
}
