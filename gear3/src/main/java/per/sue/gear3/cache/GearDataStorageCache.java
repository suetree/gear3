package per.sue.gear3.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by sure on 2017/11/12.
 */

public class GearDataStorageCache {
    private static final GearDataStorageCache ourInstance = new GearDataStorageCache();

    public static GearDataStorageCache instance() {
        return ourInstance;
    }

    private GearDataStorageCache() {
    }


    public void  initialize(Context context, String spName){
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public <T>  T getObject(String key, Class<T> cls){
        String json  = this.sharedPreferences.getString(key, null) ;
        return new Gson().fromJson(json, cls);
    }


    public void storeObject (String key, Object obj ){
        this.editor.putString(key, new Gson().toJson(obj));
        this.editor.commit();
    }
}
