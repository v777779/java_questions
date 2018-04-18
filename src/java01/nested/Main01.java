package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {

    public static int publicStaticParentA = 1;
    private static int privateStaticParentA = 2;
    public int publicParentA  = 3;


    protected static int protectedStaticParentA = 5;
    protected int protectedParentA = 6;

    protected static void parentStaticShow() {
        System.out.println("Parent Static show method");
    }

    protected void parentMemberShow() {
        System.out.println("Parent Member show method");
    }


}
