package per.sue.gear3.net.progress;

/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/4/21
*/
public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
