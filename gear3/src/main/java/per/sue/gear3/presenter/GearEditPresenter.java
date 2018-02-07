package per.sue.gear3.presenter;

import android.app.Application;
import android.content.Context;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by SUE on 2016/7/21 0021.
 */
public class GearEditPresenter<T, E> extends AbsPresenter {

    private Context context;
    private GearEditResultView<T, E> resultView;
    private Observable<T> queryobservable;
    private Observable<E> submitObservable;

    public GearEditPresenter(Context context, GearEditResultView<T, E> resultView) {
        this.context = context;
        this.resultView = resultView;
    }



    public void requestQuery(){
        if(null != queryobservable){
            queryobservable.subscribe(new Subscriber<T>() {
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
                    resultView.onSuccessQuery(t);
                }
            });
        }
    }

    public void requestSubmit(){
        if(null != submitObservable){
            submitObservable.subscribe(new Subscriber<E>() {
                @Override
                public void onCompleted() {
                    resultView.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    resultView.onError(1, e.getMessage());
                }

                @Override
                public void onNext(E t) {
                    resultView.onSuccessSubmit(t);
                }
            });
        }
    }

    public GearEditPresenter setQueryObservable(Observable<T> queryobservable) {
        this.queryobservable = queryobservable;
        return this;
    }

    public GearEditPresenter setSubmitObservable(Observable<E> submitObservable) {
        this.submitObservable = submitObservable;
        return this;
    }

    public interface  GearEditResultView<T, E> extends GearResultView{

        void onSuccessQuery(T result);
        void onSuccessSubmit(E result);
        void onFaildedQuery(int code, String message);
        void onFaildedSubmit(int code, String message);
    }
}
