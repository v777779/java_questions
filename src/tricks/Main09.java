package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main09 {
    public static void main(String[] args) {
        Integer i = new Integer(null);  // ошибка на этапе исполнения
//        String s = (StringBuffer)new String(null);      // ошибка на этапе компиляции два конструктора StringBuffer(), StringBuilder()
        StringBuffer s2 = new StringBuffer(null);
        StringBuilder s3 = new StringBuilder(null);
    }
}
