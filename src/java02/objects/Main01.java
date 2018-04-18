package java02.objects;

import java.util.IdentityHashMap;
import java.util.Locale;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 13-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 implements Cloneable {
    private int i;
    private long l;
    private float f;
    private double d;
    private String s;
    private Main01 a;
    private Main01 b;

    public Main01(int i, long l, float f, double d, String s, Main01 a, Main01 b) {
        this.i = i;
        this.l = l;
        this.f = f;
        this.d = d;
        this.s = s;
        this.a = a;
        this.b = b;
    }

    @Override
    protected Main01 clone() throws CloneNotSupportedException {
        return (Main01) super.clone();
    }

    private int floatCompare(float f1, float f2) {
        if (f1 < f2) return -1;
        if (f1 > f2) return 1;
        int thisBits = Float.floatToIntBits(f1);
        int anotherBits = Float.floatToIntBits(f2);
        return (thisBits == anotherBits ? 0 :
                (thisBits < anotherBits ? -1 : 1));
    }

    private int doubleCompare(double d1, double d2) {
        if (d1 < d2) return -1;
        if (d1 > d2) return 1;
        long thisBits = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);
        return (thisBits == anotherBits ? 0 :
                (thisBits < anotherBits ? -1 : 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Main01)) return false;

        Main01 main01 = (Main01) o;

        if (i != main01.i) return false;
        if (l != main01.l) return false;

        if (floatCompare(main01.f, f) != 0) return false;
        if (doubleCompare(main01.d, d) != 0) return false;
        return s != null ? s.equals(main01.s) : main01.s == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = i;
        result = 31 * result + (int) (l ^ (l >>> 32));
        result = 31 * result + (f != +0.0f ? Float.floatToIntBits(f) : 0);
        temp = Double.doubleToLongBits(d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (s != null ? s.hashCode() : 0);
        return result;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Object object = new Object();

        object.equals(object);
//        object.hashCode();
//        object.toString();
//        object.getClass();
//        object.notify();

        Object o1 = new Object();
        Object o2 = o1;
        Object o3 = o1;

        Main01 m1 = new Main01(1, 200, 2.35f, 1.765, "m object", null, null);
        Main01 m2 = m1;
        Main01 m3 = (Main01) m1.clone();  // shallow copy
        Main01 m4 = new Main01(2, 201, 12.35f, 21.765, "n object", m2, m3);

        System.out.println(m1.equals(m4));
        try {
            String s1 = null;
            String s2 = null;
            System.out.println(s1.equals(s2));
        } catch (NullPointerException e) {
            System.out.println("s1==null s2==null s1.equals(s2): " + e.toString());
        }
        Main01 m5 = new Main01(1, 200, 2.35f, 1.765, "m object", m1, m4);

        System.out.println("Same objects m1==m2        :" + (m1 == m2));
        System.out.println("Cloned objects m1==m3      :" + (m1 == m3));
        System.out.println("Cloned objects m1.eq(m3)   :" + (m1.equals(m3)));
        System.out.println("Different objects m1.eq(m5):" + m1.equals(m5));

        System.identityHashCode(o1);
        IdentityHashMap<String, String> map = new IdentityHashMap<>();
        map.hashCode();  // based on SystemIdentityHashCode();

        System.out.println("Shallow copy");
        m1 = new Main01(1, 200, 2.35f, 1.765, "m object", null, null);
        m1.a = new Main01(2, 3, 4, 5, "a", null, null);
        m3 = m1.clone();
        System.out.println("Cloned objects m1==m3      :" + (m1 == m3));

        System.out.println("m1: " + m1 + " m1.a:" + m1.a);
        System.out.println("m3: " + m3 + " m3.a:" + m3.a);
        m1.i = 2;
        m1.s = "s";
        m1.a.i = 5;
        System.out.println("m1.i=2 m1.s=s m1.a.i= 5");
        System.out.println("m1: " + m1 + " m1.a:" + m1.a);
        System.out.println("m3: " + m3 + " m3.a:" + m3.a);  // повторяет m1.a то есть shallow copy


    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Main01A i: %3d l: %3d f: %5.2f d: %7.2f %15s", i, l, f, d, s);
    }
}
