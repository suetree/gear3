package per.sue.gear3;

import android.content.Context;

import per.sue.gear3.cache.GearDataStorageCache;
import per.sue.gear3.manager.ToastManager;

/**
 * Created by sure on 2017/11/12.
 */

public class GearConfig {
    private static final GearConfig ourInstance = new GearConfig();

    public static GearConfig instance() {
        return ourInstance;
    }

    private GearConfig() {
    }

    private Context context;

    public  void initialize(Context context){
        this.context = context;
        ToastManager.instance().initialize(context);
        GearDataStorageCache.instance().initialize(context, context.getPackageName() );
    }

}
