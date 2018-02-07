package per.sue.gear3.presenter;


/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/4/28
*/
public interface GearResultView<T>  {

    void onSuccess(T result);
    void onError(int code, String message);
    void onCompleted();

}
