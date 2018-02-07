package per.sue.gear3.selector;

import android.content.Intent;

/**
 * Created by sure on 2017/12/4.
 */

public interface IGearImageSelector {


    void selectorImage();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
