package per.sue.gear3.net.parser;/*
* 描 述：
* 作 者：ld
* 时 间：2015/12/26
* 版 权：比格科技有限公司
*/


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Gear2Parser<T> implements Parser {
    private Type objectType ;

    public Gear2Parser(  Type objectType) {
        this.objectType = objectType;
    }


    @Override
    public T parse(String jsonString){
        return  new Gson().fromJson(jsonString, objectType);

    }
}
