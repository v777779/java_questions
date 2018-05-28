import java.util.Scanner;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("JAVA8 IO Support");
        System.out.println("Java8 NIO Support");

        Scanner in = new Scanner(System.in);
        String s;
        while((s = in.nextLine())!= null){
            System.out.println(s);
        }
    }
}
