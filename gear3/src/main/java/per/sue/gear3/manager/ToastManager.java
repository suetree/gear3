package per.sue.gear3.manager;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sure on 2017/11/11.
 */

public class ToastManager {
    private static final ToastManager ourInstance = new ToastManager();

    private Context context;

    public static ToastManager instance() {
        return ourInstance;
    }

    private ToastManager() {
    }

    public void initialize(Context ctx){
        if(null != ctx){
            context = ctx.getApplicationContext();
        }
    }

    public void error( String message){
        toast(message, context, Toast.LENGTH_SHORT).show();
    }

    public void success( String message){
        toast(message, context, Toast.LENGTH_SHORT).show();
    }

    public  Toast toast(String message, Context context, int length) {
        return Toast.makeText(context, message, length);
    }
}
