package examples;

import java.util.function.Predicate;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01P {

    private static Predicate validator = v -> {
        if (v instanceof Integer)
            return (Integer) v > 10;
        if (v instanceof String)
            return ((String) v).length() > 10;
        throw new IllegalArgumentException();
    };

    public static void main(String[] args) {
        Integer i = 11;
        int j = 1;
        String s = "123";
        long l = 11;


        System.out.println(validator.test(i));
        System.out.println(validator.test(j));
        System.out.println(validator.test(s));
        System.out.println(validator.test(l));


    }
}
