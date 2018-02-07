package per.sue.gear3.net;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import per.sue.gear3.net.bean.InputFile;
import per.sue.gear3.net.progress.ProgressRequestListener;


/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/4/29
*/
public class ApiConnectionFactory {

    private   Context context;
    private static ApiConnectionFactory apiConnectionFactory = new ApiConnectionFactory();
    public Map<String, String> heads = new HashMap<>();
    public static ArrayList<String> mobileParamKey = new ArrayList<>();


    public static ApiConnectionFactory getInstance() {
        return apiConnectionFactory;
    }

    public void  initialize(Context context){
        this.context = context.getApplicationContext();
    }

    public void  initialize(Context context,  Map<String, String> heads){
        this.context = context.getApplicationContext();
        this.heads = heads;
    }

    public void addHead(String key, String value){
        heads.put(key, value);
    }


    public Context getContext() {
        return context;
    }

    public  static ApiConnection createGET(String url){
        ApiConnection apiConnection =   new  ApiConnection.Builder().url(url).builder(ApiConnectionFactory.getInstance().getContext());
        return  apiConnection;
    }

    public  static ApiConnection createGET(String url, Map<String, String> params){
        url += "?k=" + System.currentTimeMillis();
        for(String key : params.keySet() ){
            url =  new StringBuilder(url).append("&").append(key).append("=").append(params.get(key)).toString();
        }
        ApiConnection apiConnection =   new  ApiConnection.Builder().url(url).builder(ApiConnectionFactory.getInstance().getContext());
        apiConnection.setHeads(ApiConnectionFactory.getInstance().heads);
        return  apiConnection;
    }

    public static ApiConnection createPOST(String url, Map<String, String> params){

        return  create(url, params, null, null);
    }

    public static ApiConnection createPOSTFile(String url, Map<String, String> params, ArrayList<InputFile> files){
        return  create(url, params, files, null);
    }


    public static  ApiConnection createPOSTFile(String url, Map<String, String> params, ArrayList<InputFile> files, ProgressRequestListener progressRequestListener){
        return  create(url, params, files,  progressRequestListener);
    }

    public static ApiConnection create(String url, Map<String, String> params, ArrayList<InputFile> files, ProgressRequestListener progressRequestListener){

        if(null == ApiConnectionFactory.getInstance().getContext()){
            throw new IllegalStateException("ApiConnectionFactory has not initialize ");
        }
        filterSpecharsForMobile(params);

        ApiConnection apiConnection =  new ApiConnection.Builder().url(url)
                .post(params)
                .files(files)
                .progressRequestListener(progressRequestListener)
                .builder(ApiConnectionFactory.getInstance().getContext());

        apiConnection.setHeads(getInstance().heads);
        return  apiConnection;
    }

    public static ApiConnection createForJsonBody(String url,String json){

        if(null == ApiConnectionFactory.getInstance().getContext()){
            throw new IllegalStateException("ApiConnectionFactory has not initialize ");
        }

        ApiConnection apiConnection =  new ApiConnection.Builder().url(url)
                .post(json)
                .builder(ApiConnectionFactory.getInstance().getContext());

        apiConnection.setHeads(getInstance().heads);
        return apiConnection;
    }



    public static void  filterSpecharsForMobile(Map<String, String> params){
        if(null != mobileParamKey){
            for(String key : mobileParamKey){
                if(null != params && params.containsKey(key)){
                    String mobile = params.get(key);
                    params.put(key, filterSpecharsForMobile(mobile));
                }
            }
        }
    }

    public static String  filterSpecharsForMobile(String mobile){
        if(!TextUtils.isEmpty(mobile) && ( mobile.contains("+86") ||mobile.contains("(+86)"))){
            mobile = mobile.replace("+86", "");
            mobile = mobile.replace("(+86)", "");

        }
        return mobile;
    }
}
