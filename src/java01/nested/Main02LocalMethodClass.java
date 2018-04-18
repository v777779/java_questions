package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02LocalMethodClass extends Main01{
    public static int publicStaticA = 1;
    private static int privateStaticA = 2;
    public int publicA = 3;
    private int privateA = 4;

    private static void staticShow() {
        System.out.println("Static show method");
    }

    private void memberShow() {
        System.out.println("Member show method");
    }


    private void show(int n) {

        class Child02L {
            private static final int CONST = 1;


            private void show(int m) {
                System.out.println("\nChild Member:");
                System.out.println("Parent:");
                System.out.println("publicStaticParentA:"+publicStaticParentA);
                System.out.println("protectedStaticParentA:"+protectedStaticParentA);
                System.out.println("protectedParentA:"+protectedParentA);

                System.out.println("Local:");
                System.out.println("publicStaticA:"+publicStaticA);
                System.out.println("privateStaticA:"+privateStaticA);
                System.out.println("publicA:"+publicA);
                System.out.println("privateA:"+privateA);

                System.out.println("Methods:");
                Main01.parentStaticShow();
                Main02LocalMethodClass.staticShow();
                parentMemberShow();
                memberShow();
                System.out.println("Vairables:");
                System.out.println(n);
                System.out.println(m);
                System.out.println("Static final CONST:"+CONST);
            }
        }


        new Child02L().show(1);

    }

    public static void main(String[] args) {
        new Main02LocalMethodClass().show(2);
    }
}
