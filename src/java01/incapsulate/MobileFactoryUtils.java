package java01.incapsulate;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public final class MobileFactoryUtils {
    public static MobileFactory newInstance(){
        return new MobileFactory();
    }

    private MobileFactoryUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}
