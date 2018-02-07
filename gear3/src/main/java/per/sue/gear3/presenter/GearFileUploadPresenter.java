package per.sue.gear3.presenter;
import java.io.File;
import java.util.concurrent.TimeUnit;

import per.sue.gear3.net.progress.ProgressRequestListener;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by SUE on 2016/7/8 0008.
 */
public class GearFileUploadPresenter extends AbsPresenter {
    private static final String TAG = "LoginPresenter";

    private View view;
    private IFileUploadApi iFileUploadApi;

    public GearFileUploadPresenter(IFileUploadApi iFileUploadApi) {
        this.iFileUploadApi = iFileUploadApi;
    }


    public void setView(View view) {
        this.view = view;
    }

    public void uploadFile(File file, ProgressRequestListener progressRequestListener){
        this.iFileUploadApi.uploadFile(file, progressRequestListener)
                .delaySubscription(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null != view){
                    view.onErrorUploadFile(1, e.getMessage());
                }
            }

            @Override
            public void onNext(String result) {
                if(null != view){
                    view.onSuccessUploadFile(result);
                }

            }
        });
    }

    public interface IFileUploadApi{
        Observable<String> uploadFile(File file, ProgressRequestListener progressRequestListener);
    }

    public interface View  {
        void onErrorUploadFile(int code, String message);
        void onSuccessUploadFile(String result);
    }
}
