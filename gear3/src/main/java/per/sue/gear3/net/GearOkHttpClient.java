package per.sue.gear3.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by sure on 2017/8/7.
 */

public class GearOkHttpClient {
    private static final OkHttpClient ourInstance = builderOkHttpClient();

    public  static int timeout = 15000;//ms

    public static synchronized OkHttpClient getInstance() {
        return ourInstance;
    }

    private static OkHttpClient builderOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);

        return builder.build();
    }




}
