package java01.incapsulate;

import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public interface IMobileFactory {
    MobilePhone save(MobilePhone item);
    List<MobilePhone> getBrand(String brand);
    double getTotal(String brand, String model);
}
