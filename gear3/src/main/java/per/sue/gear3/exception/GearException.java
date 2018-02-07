package per.sue.gear3.exception;

/**
 * Created by sue on 2016/10/27.
 */
public class GearException  extends Exception {


    public final static int ERRORE_CODE_NOT_NET = -1;

    private int code;
    private String message;
    private String json;

    public GearException(  String message) {
        super(message);
        this.message = message;
    }

    public GearException( int code, String message) {
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
