package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main07 {
    public static void main(String[] args) {
        Integer i1 = -128;   // no autoboxing for int  -INF..129 > value  ||   127 < value
        Integer i2 = -128;
        System.out.println(i1 == i2);

        Integer i3 = 127;   // autoboxing to int  for -128..127
        Integer i4 = 127;
        System.out.println(i3 == i4);
    }
}
