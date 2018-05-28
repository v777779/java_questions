package io.console;

import java.io.Console;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static util.IOUtils.FORMAT;


/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 25-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainConsole {
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean flagLock = false;

    public static void main(String[] args) {
// Console
        Runnable r = () -> {
            System.out.printf(FORMAT, "Console:");
            Console console = null;
            console = System.console();
            if (console != null) {
                String s;
                console.printf("Type any text and press <Enter>(wait 3 sec or <Enter> to exit):%n: ");
                while ((s = console.readLine()) != null) {
                    console.printf(": %s%n: ", s);
                    if (s.equals("exit")) {
                        console.printf("Press any key to close...");
                        console.readLine();
                        System.exit(0);
                    }
                    if (s.isEmpty()) System.exit(0);
                    synchronized (lock) {
                        flagLock = true;
                    }
                }
            }
        };

        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.execute(r);
        while(true) {
            try{
                Thread.sleep(3000);
                synchronized (lock) {
                    if(flagLock) flagLock = false;
                    else break;
                }

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        exec.shutdownNow();
        System.out.println("shutdown");
        System.exit(0);
    }
}
