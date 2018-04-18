package java01.pools;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainString {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        String s1 = "Cat";                      // pool S1
        String s2 = new String("Cat");   // pool S1 heap s2
        String s3 = "Cat";
        String s4 = new String("Cat");
        String s5 = s2.intern();
        String s6 = s3.intern();

        System.out.println("s1==s2:"+(s1==s2));
        System.out.println("s1==s3:"+(s1==s3));
        System.out.println("s1==s4:"+(s1==s4));
        System.out.println("s1==s5:"+(s1==s5));
        System.out.println("s1==s6:"+(s1==s6));

        System.out.println("s2==s3:"+(s2==s3));
        System.out.println("s2==s4:"+(s2==s4));
        System.out.println("s2==s5:"+(s2==s5));
        System.out.println("s2==s6:"+(s2==s6));

        System.out.println("s3==s4:"+(s3==s4));
        System.out.println("s3==s5:"+(s3==s5));
        System.out.println("s3==s6:"+(s3==s6));

        System.out.println("s4==s5:"+(s4==s5));
        System.out.println("s4==s6:"+(s4==s6));


    }
}
