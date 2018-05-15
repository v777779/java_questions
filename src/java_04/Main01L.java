package java_04;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01L {

    private static abstract class Validator {

// Predicate version
        private static final Map<Class, Predicate> map;
        static {
            map = new HashMap<>();
            map.put(Integer.class, (Predicate<Integer>) v -> v > 10);
            map.put(String.class, (Predicate<String>) v -> v.length() > 10);
            map.put(Double.class, (Predicate<Double>) v -> Double.compare((Double) v, 10) > 0);
        }

        private static Predicate p = v ->
                Optional.ofNullable(map.get(v.getClass()))
                        .orElseThrow(()-> new IllegalArgumentException())
                        .test(v);

// Function version
        private static Function<Object, Boolean> f = v -> {
            if (v instanceof Integer)
                return (Integer) v > 10;
            if (v instanceof String)
                return ((String) v).length() > 10;
            throw new IllegalArgumentException();
        };

        private static Function<Object,Boolean> b = ((Function<Object,Boolean>)v1->{
            if (v1 instanceof Integer)
                return (Integer) v1 > 10;
            if (v1 instanceof String)
                return ((String) v1).length() > 10;

            throw new IllegalArgumentException();

        });

// Simple version
        private static Predicate validate = v ->((Function<Object,Boolean>)v1->{
            if (v1 instanceof Integer)
                return (Integer) v1 > 10;
            if (v1 instanceof String)
                return ((String) v1).length() > 10;
            throw new IllegalArgumentException();
        }).apply(v);


        private static Predicate validate2 = v ->{
            if (v instanceof Integer)
                return (Integer) v > 10;
            if (v instanceof String)
                return ((String) v).length() > 10;
            throw new IllegalArgumentException();
        };


        public static void main(String[] args) {
            int n = 25;
            String s = "string length value more than 10";

            System.out.println(Validator.p.test(n));
            System.out.println(Validator.p.test(s));

            System.out.println((Validator.validate).test(n));
            System.out.println(Validator.validate.test(s));

            System.out.println((Validator.validate2).test(n));
            System.out.println(Validator.validate2.test(s));
            System.out.println((Validator.validate).test(1.0f));
            System.out.println(Validator.validate.test(1L));


        }
    }


    public static void main(String[] args) {

        Validator.main(args);

    }
}
