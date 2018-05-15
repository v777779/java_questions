package java_04.incap;

import java.util.Arrays;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02A {

    private static void get(String brand, MobilePhone[] phones) {
        System.out.printf("%n%s%n", brand);

        final String lowerCaseBrand = brand.toLowerCase();
        double sum = Arrays.stream(phones)
                .filter(p -> p.getBrand().toLowerCase().equals(lowerCaseBrand))
                .map(p -> {
                    System.out.println(p);
                    return p.getPrice();
                })
                .reduce(0.0, (p1, p2) -> p1 + p2);
        System.out.printf("total: %.2f%n", sum);
    }


    public static void main(String[] args) {
        MobilePhone[] phones = {
                MobilePhone.newInstance(1, "Samsung", "Galaxy4", "Black", 7700.2),
                MobilePhone.newInstance("iPhone", "5s", "White", 5500.2),
                MobilePhone.newInstance("Samsung", "GalaxyA", "Green", 6600),
                MobilePhone.newInstance(2, "iPhone", "X", "Gray", 5400),
                MobilePhone.newInstance("iPhone", "6", "Red", 4847)};
        //массив телефонов
        Arrays.stream(phones).forEach(System.out::println);

        get("Samsung", phones);
        get("IPhone", phones);
        System.out.println();
        try {
            MobilePhone.newInstance(1, null, "model", "color", 1.21);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        try {
            MobilePhone.newInstance(1, "Samsung", null, "Black", 7700.2);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        try {
            MobilePhone.newInstance(-1, "Samsung", "Galaxy4", null, 7700.2);
        } catch (NullPointerException e) {
            System.out.println(e);
        }

    }


}
