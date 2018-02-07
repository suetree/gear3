package per.sue.gear3.presenter;

import android.content.Context;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by SUE on 2016/7/21 0021.
 */
public class GearPresenter<T> extends AbsPresenter {

    private Context context;
    private GearResultView<T> resultView;
    private Observable<T> observable;

    public GearPresenter(Context context, GearResultView<T> resultView) {
        this.context = context;
        this.resultView = resultView;
    }

    public void request(){
        if(null != observable){
            observable.subscribe(new Subscriber<T>() {
                @Override
                public void onCompleted() {
                    resultView.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    resultView.onError(1, e.getMessage());
                }

                @Override
                public void onNext(T t) {
                    resultView.onSuccess(t);
                }
            });
        }
    }

    public void setObservable(Observable<T> observable) {
        this.observable = observable;
    }
}
