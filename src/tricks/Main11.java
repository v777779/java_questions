package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main11 {
    static int method1(int i) {
        return method2(i *= 11);
    }
    static int method2(int i) {
        return method3(i /= 11);
    }
    static int method3(int i) {
        return method4(i -= 11);
    }
    static int method4(int i) {
        return i += 11;
    }
    public static void main(String[] args) {
        System.out.println(method1(11));
    }
}
