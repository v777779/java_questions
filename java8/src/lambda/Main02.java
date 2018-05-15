package lambda;

import java.util.Comparator;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 02-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 {


    private static Comparator<String> c = (o1, o2) -> {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        return o1.compareTo(o2);
    };

    private static Comparator<String> c2 = String::compareTo;

    private static class Apple {
        enum Color {
            RED("red", 1), GREEN("green", 2), BLUE("blue", 3);
            private String color;
            private int order;

            Color(String color, int order) {
                this.color = color;
                this.order = order;
            }
        }

        Enum<Color> color;
        Integer weight;


        public Apple() {
            this.color = Color.GREEN;
            this.weight = 1;
        }

        public Integer getWeight() {
            return weight;
        }

    }

    // demo
    interface IFunc {
        int method(String s);

    }

    interface IFunc2 {
        boolean method(Apple a);

    }

    interface IFunc3 {
        void method(int x, int y);
    }

    interface IFunc4 {
        int method();
    }

    interface IFunc5 {
        int method(Apple a1, Apple a2);
    }

    interface IFunc6 {
        void method();
    }

    interface IFunc7 {
        String method();
    }

    interface IFunc8 {
        String method(Integer value);
    }

    interface IFunc9 {
        String method(String s);
    }

    public static void main(String[] args) {
// comparators
        Comparator<Apple> aC = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
        Comparator<Apple> aN = Comparator.comparingInt(Apple::getWeight);

// demo
        IFunc d = (s) -> s.length();
        IFunc2 d2 = (a) -> a.getWeight() > 150;
        IFunc3 d3 = (x, y) -> {
            System.out.println("Result:");
            System.out.println(x + " " + y);
        };
        IFunc4 d4 = () -> 42;
        IFunc5 d5 = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());

        System.out.println("Demo:");
        System.out.println("demo :" + d.method("string"));
        System.out.println("demo2:" + d2.method(new Apple()));
        System.out.println("demo3:");
        d3.method(1, 2);
        System.out.println("demo4:" + d4.method());
        System.out.println("demo5:" + d5.method(new Apple(), new Apple()));

        System.out.println("Comparators Lambda:");
        System.out.println(c.compare("s4", "s2"));
        System.out.println(c2.compare("s4", "s2"));
// check
        IFunc6 c6 = () -> System.out.println("Check value");
        IFunc7 c7 = () -> "Raoul";
        IFunc7 c72 = () -> {
            return "Mario";
        };
        IFunc8 c8 = (i) -> {
            return "Alan" + i;
        };
        IFunc9 c9 = (s) -> {return "IronMan";};

        System.out.println("Check:");
        System.out.print("c6 :");
        c6.method();
        System.out.println("c7 :" + c7.method());
        System.out.println("c72:" + c72.method());

        System.out.println("c8 :" + c8.method(1));
        System.out.println("c9 :" + c9.method("1"));


    }
}
