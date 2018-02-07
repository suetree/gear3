package per.sue.gear3.net.exception;

/**
 * Created by sue on 2016/12/1.
 */
public class GearThrowable extends Throwable {

    private int code;
    private String message;

    public GearThrowable( String message) {
        super(message);
        this.message = message;
    }


    public GearThrowable( int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
