import cn.hutool.core.util.IdUtil;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //List<String> list = new ArrayList<String>() {
        //    {
        //        add("a");
        //        add("b");
        //        add("c");
        //        add("d");
        //        add("e");
        //        add("f");
        //    }
        //};
        //System.out.println(list.subList(3, list.size()));
        int a = 1;
        int max = 10;
        int c = (a / max) + (a % max == 0 ? 0 : 1);
        System.out.println(c);
    }
}
