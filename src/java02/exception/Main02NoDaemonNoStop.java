package java02.exception;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main02NoDaemonNoStop {
    private static AtomicBoolean isExit = new AtomicBoolean(false);

    private static Runnable runnable = new Runnable() {
        private int counter = 100;
        @Override
        public void run() {
            try {
                while (counter-- > 0) {
                    System.out.println("Is alive");
                    Thread.sleep(10);
                    // throw new RuntimeException();
                    if(isExit.get()) return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Finally child.Thread This will never be executed.");
            }
        }
    };

    public static void main(String[] args) throws InterruptedException {
// daemon and exit
        Thread daemon = new Thread(runnable);
        daemon.setDaemon(false);
        daemon.start();
        Thread.sleep(100);
        isExit.set(false);                   // atomic for multithreading  instead of daemon.stop()
        Thread.sleep(100);
        System.out.println("Last non-daemon thread exits.");
    }
}