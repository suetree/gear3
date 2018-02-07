package per.sue.gear3.net.parser;



import org.json.JSONException;

import per.sue.gear3.net.exception.ParseException;


public interface Parser<T> {
	public abstract T parse(String jsonString) throws ParseException, JSONException, ParseException;
}
