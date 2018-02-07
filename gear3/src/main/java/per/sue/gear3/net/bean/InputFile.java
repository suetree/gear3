package per.sue.gear3.net.bean;

import java.io.File;

/*
* 文件名：
* 描 述：
* 作 者：苏昭强
* 时 间：2016/1/18
*/
public class InputFile {

    private String key;

    private File file;

    public InputFile(String key, File file) {
        this.key = key;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
