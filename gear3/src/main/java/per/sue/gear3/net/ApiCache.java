package per.sue.gear3.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import per.sue.gear3.utils.GearLog;
import rx.Observable;
import rx.Subscriber;
import rx.observables.SyncOnSubscribe;

/**
 * Created by sue on 2017/1/20.
 */
public class ApiCache {
    private static ApiCache ourInstance = new ApiCache();


    private static final String TAG = "ApiCache";
    private HashMap<String, Object> dataCacheMap = new HashMap<>();

    public static ApiCache getInstance() {
        return ourInstance;
    }

    private ApiCache() {
    }

    public <T> Observable<T> cache(final String key, final  Observable<T> observable){

        if(dataCacheMap.containsKey(key)){
          return  Observable.create(new Observable.OnSubscribe<T>() {
              @Override
              public void call(Subscriber<? super T> subscriber) {
                  GearLog.e(TAG, "load data from cache key =" + key );
                  subscriber.onNext((T)dataCacheMap.get(key));
              }
            });
        }else{
            return  Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call( final Subscriber<? super T> subscriber) {
                    observable.subscribe(new Subscriber<T>() {
                        @Override
                        public void onCompleted() {
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onNext(T t) {
                            dataCacheMap.put(key, t);
                            subscriber.onNext((T)dataCacheMap.get(key));
                        }
                    });
                }
            });

        }

    }
}
