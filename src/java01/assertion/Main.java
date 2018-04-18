package java01.assertion;

import java.io.Serializable;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main implements Serializable{
    public static void main(String[] args) {
        System.out.println("To Activate Assert use switch -ea for JVM");
        boolean flag = false;
        System.out.println("flag value:"+flag);
        assert flag: "flag is not true";
    }
}
