package per.sue.gear3.presenter;

import android.content.Context;

import rx.Observable;

/**
 * Created by SUE on 2016/8/3 0003.
 */
public class SelectListPresenter<T> extends GearListPresenter<T> {


    private Observable<T> observable;

    public SelectListPresenter(Context context, ListResultView<T> listResultView ) {
       setListResultView(listResultView);
    }



   /* @Override
    public Observable<T> getObservable() {
        return observable;
    }*/
}
