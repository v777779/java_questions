package java01.abstracts;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public interface IMain {

    static void print() {
        System.out.println("IMain print()");
    }

    default void voice() {
        System.out.println("IMain voice()");
    }

    default void light() {
        System.out.println("IMain light()");
    }
}
