package per.sue.gear3.net.parser;


import per.sue.gear3.net.exception.ParseException;

/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2015/12/22
*/
public class StringParser implements Parser<String> {
    @Override
    public String parse(String jsonString) throws ParseException {
        return jsonString;
    }
}
