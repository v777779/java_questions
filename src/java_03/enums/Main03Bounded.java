package java_03.enums;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 25-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03Bounded {

    interface A<T> {
        T getValue();
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

            return "value";
        }
    }

    private static class A2 extends A1 implements A<A1> {
        String s;
        int n;

        private A2(String s, int n) {
            this.s = s;
            this.n = n;
        }

        @Override
        public A1 getValue() {
            return value;
        }

        @Override
        public String toString() {
//            return "A2[" + s + "," + n + "," + value + ']';         // Stack Overflow
            if (value == null)
                return "A2[" + s + "," + n + "," + "null" + ']';
            else
                return "A2[" + s + "," + n + "," + value.show() + ']';
        }
    }


    public static void main(String[] args) {

        System.out.println("\nGeneric bounded:");
        A2 a2 = new A2("a2", 12);
        A2 a21 = new A2("a21", 521);
        a2.setValue(a21);

        System.out.println("a2         :" + a2);
        System.out.println("a2.getValue:" + a2.getValue());


    }
}
