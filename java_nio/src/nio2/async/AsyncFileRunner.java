package nio2.async;

import nio2.async.files.FileCallback;
import nio2.async.files.FileCallbackLock;
import nio2.async.files.FileCallbackThread;
import nio2.async.files.FileFuture;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class AsyncFileRunner {
    public static void main(String[] args) {
        // async files
        FileFuture.main(args);          // future read
        FileCallback.main(args);        // callback  InternalInt user class attachment
        FileCallbackThread.main(args);  // callback  Thread.interrupt in attachment
        FileCallbackLock.main(args);    // callback  Reentrant.lock

    }
}
