package per.sue.gear3.net.session;


import android.content.Context;
import android.os.Bundle;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 自动管理Cookies
 */
public class CookiesManager implements CookieJar {

    private Context context;
    private  PersistentCookieStore cookieStore;
    private static CookiesManager cookiesManager  = new CookiesManager();

    private CookiesManager() {

    }

    public static CookiesManager getInstance() {
        return cookiesManager;
    }

    public void initialize(Context context){
        this.context = context.getApplicationContext();
        cookieStore = new PersistentCookieStore( context.getApplicationContext());
    }

    public boolean isInitialized(){
        return context != null;
    }



    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }

    //auth_app_Id=7; expires=Sun, 21-Aug-2016 19:35:37 GMT; Max-Age=604800;
    public String cookie(HttpUrl url){
        StringBuilder cookieBuilder = new StringBuilder();
        if(cookieStore != null){
            List<Cookie> cookies = cookieStore.get(url);
            for(Cookie cookie : cookies){
                if(cookieBuilder.length() > 0){
                    cookieBuilder.append("; ");
                }
                cookieBuilder.append(cookie.toString());
            }
        }
        return cookieBuilder.toString();
    }

    public void reset(){
        cookieStore.removeAll();
    }
}
