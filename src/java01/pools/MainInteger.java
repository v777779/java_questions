package java01.pools;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainInteger {
    public static void main(String[] args) {
        Integer i1 = 127;
        Integer i2 = 127;

        Character c1 = '\u007f';
        Character c2 = '\u007e';
        c2++;

        Byte b1 = 127;
        Byte b2 = 127;

        Short s1 = 126;
        Short s2 = 127;
        s1++;

        final Long l1 = 127L;
        final Long l2 = 127L;

        System.out.println("i1==i2: "+(i1==i2));
        System.out.println("c1==c2: "+(c1==c2));
        System.out.println("b1==b2: "+(b1==b2));
        System.out.println("s1==s2: "+(s1==s2));
        System.out.println("l1==l2: "+(l1==l2));


    }
}
