package java01.incapsulate;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MobileFactory implements IMobileFactory {
    private HashMap<String, MobilePhone> map;

    public MobileFactory() {
        this.map = new HashMap<>();
    }

    @Override
    public MobilePhone save(MobilePhone item) {
        Objects.requireNonNull(item);
        if (item.getId() == null) {
            map.put(item.getId(), item);
            return item;
        }
        final String id = UUID.randomUUID().toString();
        final MobilePhone saveItem = MobilePhone.newInstance(id,
                item.getBrand(), item.getModel(), item.getColor(), item.getPrice());
        map.put(id, saveItem);
        return saveItem;
    }

    @Override
    public List<MobilePhone> getBrand(String brand) {
        Objects.requireNonNull(brand);
        return map.values().stream()
                .filter(p -> p.getBrand().toLowerCase().equals(brand.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotal(String brand, String model) {
        return map.values().stream()
                .filter(p -> p.getBrand().toLowerCase().equals(brand.toLowerCase()))
                .filter(p -> p.getModel().toLowerCase().equals(model.toLowerCase()))
                .mapToDouble(MobilePhone::getPrice)
                .sum();
    }

    private static void printBrandPhones(MobileFactory factory, String brand) {
        System.out.println("All " + brand + " phones:");
        factory.getBrand(brand)
                .forEach(System.out::println);
        System.out.println("--------------------------------");
    }

    private static void printBrandModelPhonesTotalPrice(MobileFactory factory, String brand, String model) {
        System.out.println("Total " + brand + " " + model + " phones price: " + factory.getTotal(brand, model));
    }

    public static void main(String[] args) {
        final MobileFactory factory = MobileFactoryUtils.newInstance();
        factory.save(MobilePhone.newInstance("Samsung", "Galaxy 4", "Black", 7700));
        factory.save(MobilePhone.newInstance("Samsung", "Galaxy A", "Green", 6600));
        factory.save(MobilePhone.newInstance("Samsung", "Galaxy A", "Silver", 6900));
        factory.save(MobilePhone.newInstance("Apple", "iPhone 5s", "White", 5500));
        factory.save(MobilePhone.newInstance("Apple", "iPhone 6", "Red", 4444));
        factory.save(MobilePhone.newInstance("Apple", "iPhone 6", "Gray", 5400));

        printBrandPhones(factory, "Samsung");
        printBrandPhones(factory, "Apple");

        printBrandModelPhonesTotalPrice(factory, "Samsung", "Galaxy A");
        printBrandModelPhonesTotalPrice(factory, "Apple", "iPhone 6");
    }



}
