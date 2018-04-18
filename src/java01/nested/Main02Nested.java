package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02Nested extends Main01 {
    public static int publicStaticA = 1;
    private static int privateStaticA = 2;
    public int publicA = 3;
    private int privateA = 4;

    private static void staticShow() {
        System.out.println("Static show method");
    }

    private void show() {
        System.out.println("Member show method");
    }

    public static interface IChild02 {  // interface всегда статический класс

    }

    public class Child02 {
        private static final int CONST = 1;
        private int m = 2;

        public void show() {
            System.out.println("\nChild Member:");
            System.out.println("Parent:");
            System.out.println("publicStaticParentA:" + publicStaticParentA);
            System.out.println("protectedStaticParentA:" + protectedStaticParentA);
            System.out.println("protectedParentA:" + protectedParentA);

            System.out.println("Local:");
            System.out.println("publicStaticA:" + publicStaticA);
            System.out.println("privateStaticA:" + privateStaticA);
            System.out.println("publicA:" + publicA);
            System.out.println("privateA:" + privateA);

            System.out.println("Methods:");
            Main01.parentStaticShow();
            Main02Nested.staticShow();
            parentMemberShow();
            Main02Nested.this.show();
            System.out.println("Static final CONST:" + CONST);

        }

    }

    public static void main(String[] args) {
        Main02Nested.Child02 child02 = new Main02Nested().new Child02();

        child02.show();

    }

}
