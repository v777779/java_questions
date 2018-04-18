package java02.annotation;

import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
@CUserAnnotation(name = "Main01A name", info = "Main01A info")
public class Main01A<T> {
    @Native
    @UserAnnotation
    public Main01A main01;


    public int main02;



    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @CUserAnnotationStart
    public void startMethod() {

    }

    @CUserAnnotationStop
    public void stopMethod() {

    }

    // ok
    @SafeVarargs
    private final void variables(T... args) {
        System.out.println("member method");
    }

    @SafeVarargs
    private static void variables(List<String>... lists) {
        System.out.println(lists[0].get(0));
        System.out.println(lists[1].get(0));
    }

    // warning
    @SafeVarargs
    private final void variables2(String... args) {
        System.out.println("member method");
    }


    private static void variables2(List<String>... lists) {
        System.out.println(lists[0].get(0));
        System.out.println(lists[1].get(0));
    }

    public static Main01A getInstance() {
        return new Main01A();
    }

    @SuppressWarnings("unchecked")              // suppress -Xlint:"unchecked"
    public static void main(String[] args) {

        Main01A<String> main01 = new Main01A();
        System.out.println("Annotations");


        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.add("Hi");
        list2.add("Low");
        main01.variables("1", "2", "3");
        variables(list1, list2);

        main01.variables2("1", "2", "3");

        variables2(list1, list2);

    }
}
