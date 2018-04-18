package java02.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {
        testOutOfMemoryError();
        testThreadInterrupted();
        testThreadStop();
        testStackOverflow();
    }

    private static void testThreadStop() {
        try {
            try {
                final Thread thread = Thread.currentThread();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        thread.stop();
                    }
                }).start();
                while(true)
                    Thread.sleep(1000);
            } finally {
                System.out.print("finally called after ");
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    private static void testThreadInterrupted() {
        try {
            try {
                final Thread thread = Thread.currentThread();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        thread.interrupt();
                    }
                }).start();
                while(true)
                    Thread.sleep(1000);
            } finally {
                System.out.print("finally called after ");
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    private static void testOutOfMemoryError() {
        try {
            try {
                List<byte[]> bytes = new ArrayList<byte[]>();
                while(true)
                    bytes.add(new byte[8*1024*1024]);
            } finally {
                System.out.print("finally called after ");
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    private static void testStackOverflow() {
        try {
            try {
                testStackOverflow0();
            } finally {
                System.out.print("finally called after ");
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    private static void testStackOverflow0() {
        testStackOverflow0();
    }
}
