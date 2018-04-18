package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02NestedStatic extends Main01 {

    public static int publicStaticA = 1;
    private static int privateStaticA = 2;
    public int publicA = 3;
    private int privateA = 4;

    public void show() {
        System.out.println("\nHelloWorld show():");
        System.out.println("publicStaticParentA:"+publicStaticParentA);
        System.out.println("protectedStaticParentA:"+protectedStaticParentA);
        System.out.println("publicStaticA:"+publicStaticA);
        System.out.println("privateStaticA:"+privateStaticA);

        System.out.println("publicParentA:"+publicParentA);
        System.out.println("protectedParentA:"+protectedParentA);
    }

    public static class Child02{
        private static int m = 5;
        public static void staticShow() {
            System.out.println("Child Static:");
            System.out.println("publicStaticParentA:"+publicStaticParentA);
            System.out.println("protectedStaticParentA:"+protectedStaticParentA);
            System.out.println("publicStaticA:"+publicStaticA);
            System.out.println("privateStaticA:"+privateStaticA);
            System.out.println("Main02DaemonFinally object:");
            Main02NestedStatic m2 = new Main02NestedStatic();

            System.out.println("protectedStaticParentA:"+m2.publicParentA);
            System.out.println("protectedParentA:"+m2.protectedParentA);
            System.out.println("publicStaticA:"+m2.privateA);
            System.out.println("Methods:");
            parentStaticShow();
            m2.parentMemberShow();
            m2.show();

            System.out.println("Child02 object:");  // не работает
            Child02 c2 = new Child02();
//            System.out.println("protectedStaticParentA:"+c2.publicParentA);
//            System.out.println("protectedParentA:"+c2.protectedParentA);
//            System.out.println("publicStaticA:"+c2.privateA);

        }

        public void show() {
            System.out.println("\nChild Member:");
            System.out.println("publicStaticParentA:"+publicStaticParentA);
            System.out.println("protectedStaticParentA:"+protectedStaticParentA);
            System.out.println("publicStaticA:"+publicStaticA);
            System.out.println("privateStaticA:"+privateStaticA);
        }

    }



    public static void main(String[] args) {
        Child02.staticShow();


    }

}