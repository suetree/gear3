package per.sue.gear3.net.exception;

import per.sue.gear3.exception.GearException;

/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/4/28
*/
public class NetworkConnectionException extends GearException {

    public NetworkConnectionException(String detailMessage) {
        super(detailMessage);
    }


    public NetworkConnectionException( int code, String message) {
        super(code, message);
    }


}
