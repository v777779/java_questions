package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main06 {
    private int GetValue() {
        return (true ? null : 0);
    }
    public static void main(String[] args) {
        Main06 obj = new Main06();
        obj.GetValue();
    }
}
