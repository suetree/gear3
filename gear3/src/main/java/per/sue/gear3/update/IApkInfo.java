package per.sue.gear3.update;

import java.io.Serializable;

/**
 * Created by sure on 2017/6/25.
 */

public interface IApkInfo extends Serializable{
    public static final String APK_DOWNLOAD_URL = "url";
    String versions();
    String describe();
    String date();
    String downloadUrl();
}
