import java.sql.SQLException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main {
    public static void main(String[] args) {
        Object a = new Integer[10];
        Object[] b = new Integer[10];
        Object c = new Object[20];
        Object d = new int[10];
        Object e = new String[10];
        Comparable<Integer>[] f = new Integer[10];
        f[0] = 10;
        if((Integer)f[0] == 10) {
            try {
                throw new SQLException();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        if(a.getClass() == Integer[].class) {
            Integer[] a2 = (Integer[])a;
            System.out.print("a:");
            for (int i : a2) {
                System.out.print(i+" ");
            }
            System.out.println();
        }
        if(b.getClass() == Integer[].class) {
            Integer[] b2 = (Integer[])b;
            System.out.print("a:");
            for (int i : b2) {
                System.out.print(i+" ");
            }
            System.out.println();
        }
        if(d instanceof int[]) {
            System.out.print("d:");
            int[] d2 = (int[])d;
            for (int i : d2) {
                System.out.print(i+" ");
            }
            System.out.println();
        }
        int k = 1;

    }
}
