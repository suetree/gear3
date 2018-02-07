package per.sue.gear3.net;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import per.sue.gear3.exception.GearException;
import per.sue.gear3.net.exception.ParseException;
import per.sue.gear3.net.parser.Parser;
import per.sue.gear3.utils.GearLog;
import rx.Subscriber;

/**
 * Created by sure on 2017/4/30.
 */

public class StringObserver extends APIObserver {

    private static final String TAG = "StringObserver";

    public StringObserver(ApiConnection apiConnection, Parser parser) {
        super(apiConnection, parser);
    }

    @Override
    public void dealSubscriber(int code, Subscriber subscriber, String json, Parser parser) throws IOException, JSONException, ParseException {
        GearLog.e(TAG, " json result: " + json);
        if(code == 200){
            subscriber.onNext(  parser.parse(json));
            subscriber.onCompleted();
        }else{
            subscriber.onError( new GearException( code ,  json));
            subscriber.onCompleted();
        }
    }
}
