package per.sue.gear3.net.progress;

/*
* 文件名：
* 描 述：请求体进度回调接口，比如用于文件上传中
* 作 者：苏昭强
* 时 间：2016/4/21
*/
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten, long contentLength, boolean done);
}
