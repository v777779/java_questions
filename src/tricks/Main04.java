package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04 {
        public static void show(){
            System.out.println("Static method called");
        }

        public static void main(String[] args)  {
            Main04 obj = null;
            obj.show();  // сработает даже для объекта null если метод статический
        }
}
