package java_03.softhashmap;

import java.lang.ref.ReferenceQueue;

public class GhostReferenceTest {
    private static final int NUMBER_OF_REFERENCES = 10;

    private static class LargeObject {
        private final byte[] space = new byte[ 1024 * 1024];
        private final int id;

        public LargeObject(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

//        @Override
//        protected void finalize() throws Throwable {
//            super.finalize();
//            System.out.println("finilaze:" + id);
//        }
    }

    public static void main(String[] args) {
        final ReferenceQueue queue = new ReferenceQueue();
        new Thread() {
            {
                setDaemon(true);
                start();
            }

            public void run() {
                try {
                    while (true) {
                        GhostReference ref = (GhostReference) queue.remove();
                        LargeObject obj = (LargeObject) ref.getReferent();
                        System.out.println("GHOST " + obj.getId());
                        ref.clear();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
        for (int i = 0; i < NUMBER_OF_REFERENCES; i++) {
            System.out.println("NEW   " + i);
            // We do not need to keep strong reference to the actual
            // reference anymore, and we also do not need a reverse
            // lookup anymore
            new GhostReference(new LargeObject(i), queue);
        }
        byte[][] buf = new byte[10][];
        System.out.println("Allocating until OOME...");
        try {
            for (int i = 0; i < buf.length; i++) {
                buf[i] = new byte[1024 * 1024 * 1024];

            }
        } catch (OutOfMemoryError e) {
            System.out.println(e);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}