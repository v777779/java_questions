package java01;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {
        int vByte = (int)(Math.log(Byte.MAX_VALUE+1)/Math.log(2));      // -2^7     2^7-1
        int vShort = (int)(Math.log(Short.MAX_VALUE)/Math.log(2));      // -2^15    2^15-1
        int vInt = (int)(Math.log(Integer.MAX_VALUE)/Math.log(2));      // -2^31    2^31-1
        int vLong = (int)(Math.log10(Long.MAX_VALUE)/Math.log10(2));    // 2^63
        int vFloat = (int)(Math.log10(Float.MAX_VALUE));                // 1E38
        int vDouble = (int)(Math.log10(Double.MAX_VALUE));              // 1E308
        System.out.println("Какова размерность переменных:");
        System.out.println("byte=2^"+vByte);
        System.out.println("short=2^"+vShort);
        System.out.println("integer=2^"+vInt);
        System.out.println("long = 2^"+vLong);
        System.out.println("float = 1E^"+vFloat);
        System.out.println("double = 1E^"+vDouble);

        System.out.println(Integer.MIN_VALUE);


    }

}
