package java01;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 {
    private interface Basic1 {
        String getCount();
    }

    private interface Basic2 {
        String getCount();
    }

    private interface Interface1 extends Basic1 {
        String getInfo();
        String s = "Interface1";
    }
    private interface Interface2  extends Basic2{
        String getInfo();
        String s = "Interface2";
    }
    private interface Interface3 {
        String getInfo();
        String s = "Interface3";
    }

    private static class Child implements Interface1,Interface2,Interface3 {

        @Override
        public String getInfo() {
            return "getInfo";
        }

        @Override
        public String getCount() {
            return "getCount";
        }

    }

    public static void main(String[] args) {
        Child child = new Child();

        System.out.println(child.getInfo()+" "+child.getCount());
        System.out.println(((Interface1)child).s+" "+Interface2.s);


    }


}
