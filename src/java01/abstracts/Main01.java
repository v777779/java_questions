package java01.abstracts;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
abstract public class Main01 implements IMain {
    private static int s;
    private int n;

    public Main01(int n) {
        this.n = n;
    }

    protected abstract Object show() throws IOException, Error, Throwable;


}
