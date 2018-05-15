package java_03.enums;


import java.util.EnumSet;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 25-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02Enum3<E> implements IA<E> {
    @Override
    public E get() {
        return null;
    }

    interface A<T> {
        T getValue();
    }

    private enum B2 implements A<Integer> {
        B21,B22,B23;

        @Override
        public Integer getValue() {
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
            this.value= 0;
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

    public static void main(String[] args) {
        System.out.println("Enum:"+B.A1+" "+B.A2);
        EnumSet<B> enumSet = EnumSet.of(B.A1,B.A2);
        System.out.println("EnumSet:"+enumSet);

    }
}
