package java_03.enums;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 25-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02Enum4 {

    private enum B2 implements Main02Enum3.A<Integer> {
        B21, B22, B23;

        @Override
        public Integer getValue() {
            return null;
        }
    }

    private static class B4<T> implements A<T> {
        private final T s;
        public static final B4<String> A1 = new B4<>("S");
        public static final B4<Integer> A2 = new B4<>(12);

        B4(T s) {
            this.s = s;
        }

        @Override
        public T getValue() {
            return null;
        }
    }

    private enum B implements A<Integer> {
        A1("S") {
            @Override
            public Integer getValue() {
                return super.getValue();
            }
        },
        A2(12) {
            @Override
            public Integer getValue() {
                return super.getValue();
            }
        };

        private final String s;
        private final int value;

        B(String s) {
            this.s = s;
            this.value = 0;
        }

        B(int value) {
            this.s = "";
            this.value = value;
        }


        @Override
        public Integer getValue() {
            return null;
        }
    }

    private static abstract class A1<T extends A1<T>> {
        T value;

        public T getValue() {                           // создание экземпляра закрытого класса
            return value;
        }

        public <E extends T> void setValue(E value) {   // гарантированно проверяет тип checked
            this.value = (T) value;
        }


        public String show() {
            return "A1[" + "value" + ']';
        }
    }

    private static class A2 extends A1 {
        String s;
        int n;

        private A2(String s, int n) {
            this.s = s;
            this.n = n;
        }

        @Override
        public String toString() {
//            return "A2[" + s + "," + n + "," + value + ']';         // Stack Overflow
            return "A2[" + s + "," + n + "," + value.show() + ']';
        }

        public static void main(String[] args) {
            A2 a2 = new A2("s1", 12);
            a2.setValue(a2);
            System.out.println(a2);
        }
    }


    interface A<T> {
        T getValue();
    }

    private enum C {
        C1, C2
    }


    private static class B3<T> implements A<T> {
        private final T s;

        public static final B3<String> A1 = new B3<>("A1");
        public static final B3<String> A2 = new B3<>("A2");
        public static final B3<Integer> A3 = new B3<>(12);

        B3(T s) {
            this.s = s;
        }

        @Override
        public T getValue() {
            return null;
        }

        @Override
        public String toString() {
            return (String) s;
        }
    }

    private enum Color {RED, GREEN, BLUE}


    public static void main(String[] args) {
        System.out.println("Enum B :");

        System.out.println("Enum   :" + B.A1 + " " + B.A2);
        EnumSet<B> enumSet = EnumSet.of(B.A1, B.A2);
        System.out.println("EnumSet:" + enumSet);

        System.out.println("Class B:");
        System.out.println("Class B:" + B3.A1 + " " + B3.A2);


        System.out.println("\nGeneric:");
        A2 a2 = new A2("s1", 12);
        a2.setValue(a2);
        System.out.println(a2);
// Color
        System.out.println(Arrays.toString(Color.values()));


    }
}
