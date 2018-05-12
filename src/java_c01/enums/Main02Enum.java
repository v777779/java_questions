package java_c01.enums;


import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02Enum {
// enum static final by default

    private enum eValue implements Runnable {
        JAVA("Java") {
        }, SCALA("Scala"), KOTLIN("Kotlin", 12) {
            @Override
            public void show() {
                System.out.println("KOTLIN show():"+getS());
            }

            @Override
            public String toString() {
                return "ENUM[" + this.getS() + ", " + getN() + ']';
            }
        }, CPP("Cpp", 51), PASCAL {};

        private final String s;
        private final int n;

        private eValue() {
            s = null;
            n = 0;
        }

        private eValue(String s) {
            this.s = s;
            this.n = 0;
        }

        private eValue(String s, int n) {
            this.s = s;
            this.n = n;
        }

        public void show() {
            System.out.println(s);
        }

        public String getS() {
            return s;
        }

        public int getN() {
            return n;
        }


        @Override
        public void run() {
            System.out.println("Enum:"+s+" "+n+" runnable");
        }

        @Override
        public String toString() {
            return "Enum[" + s + "]";
        }


    }


    public static void main(String[] args) {
        System.out.println("Enum:");
        System.out.println(eValue.JAVA);
        System.out.println(eValue.KOTLIN);
        eValue.JAVA.show();
        eValue.KOTLIN.show();

        new Thread(eValue.KOTLIN).start();
        System.out.println("\nEnumSet:");
        EnumSet<eValue> set = EnumSet.allOf(eValue.class); // all values
        EnumSet<eValue> subSet = EnumSet.of(eValue.JAVA,eValue.KOTLIN,eValue.SCALA); // all values

        System.out.println(set);
        System.out.println(subSet);

// Enum methods
        System.out.println("\nEnum methods:");
        System.out.println("toString:"+eValue.JAVA.toString());
        System.out.println("name():"+eValue.JAVA.name());
        System.out.println("valueOf:"+eValue.valueOf("KOTLIN"));
        System.out.println("ordinal:"+eValue.KOTLIN.ordinal());
        System.out.println("values:"+ Arrays.toString(eValue.values()));
        System.out.println("compareTo:"+eValue.KOTLIN.compareTo(eValue.SCALA));
        System.out.println("compareTo:"+eValue.KOTLIN.equals(eValue.SCALA));

// BitSet
        System.out.println("\nBitSet:");
        BitSet bitSet = new BitSet(16);
        bitSet.set(12);
        System.out.println(Arrays.toString(bitSet.toByteArray()));

// EnumSet
        System.out.println("\nBitSet:");
        EnumSet<eValue> eSet = EnumSet.allOf(eValue.class); // all values
        System.out.println("\nEnumSet iterator():");
        Iterator<eValue> eIt = eSet.iterator();
        while(eIt.hasNext()) {
            eValue e = eIt.next();
            System.out.print(e.name()+":"+eValue.valueOf(e.name())+" ");
        }
        System.out.println("\nEnumSet forEach():");
        for (eValue value : eSet) {
            System.out.print(value.name()+":"+eValue.valueOf(value.name())+" ");
        }
    }
}
