package per.sue.gear3.bean;

import java.util.ArrayList;

/**
 * Created by sure on 2017/7/2.
 */

public class ListPage<T> {

    public boolean last;
    public int totalPages;
    public int number;
    public int totalElements;
    public int size;
    public boolean  first;
    public ArrayList<T> content;
}
