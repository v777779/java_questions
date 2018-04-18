package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03 {
        Main03 b = new Main03();  // бесконечная рекурсия

        public int show(){
            return (true ? null : 0);
        }

        public static void main(String[] args)  {
            Main03 b = new Main03();
            b.show();
        }
}
