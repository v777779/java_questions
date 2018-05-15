package lambda;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    private interface ILambda {
        void method(int x, int y);

    }

    private interface ILambda2 {
        int method(int x, int y);

    }

    private static int sum(int[] a, ILambda2 iL2) {
        return a[0] + iL2.method(a[1], a[2]);
    }

    public static void main(String[] args) {

// lambda operation
        System.out.println("Lambda Operation:");
        ILambda iL = new ILambda() {
            @Override
            public void method(int x, int y) {
                System.out.println(x + " " + y);
            }
        };
        ILambda iL1 = (x, y) -> System.out.println(x + " " + y);

        iL.method(1, 2);
        iL1.method(1, 2);

// lambda return value
        System.out.println("Lambda Return Value:");
        ILambda2 iL2 = (x, y) -> {
            int n = x + y;
            n = n + 5;
            return 2 * n;
        };

        int z = iL2.method(1, 2);

        int z2 = sum(new int[]{1, 2, 3}, (x, y) -> x * y);
        System.out.println("z:" + z + " z2:" + z2);


    }


}
