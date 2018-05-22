package io.objstream;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaseFactory {
    private static final Random rnd = new Random();

    private static Person newPerson() {
        return new Person(Person.NAMES[rnd.nextInt(Person.NAMES.length)],
                rnd.nextInt(Person.AGE_MAX - Person.AGE_MIN) + Person.AGE_MIN,
                rnd.nextDouble() * (Person.HEIGHT_MAX - Person.HEIGHT_MIN) + Person.HEIGHT_MIN,
                rnd.nextDouble() * (Person.WEIGHT_MAX - Person.WEIGHT_MIN) + Person.HEIGHT_MIN);
    }


    private static Car newCar() {
        return new Car(Car.NAMES[rnd.nextInt(Car.NAMES.length)],
                rnd.nextInt(Car.AGE_MAX - Car.AGE_MIN) + Car.AGE_MIN,
                rnd.nextDouble() * (Car.PRICE_MAX - Car.PRICE_MIN) + Car.PRICE_MIN);
    }

    private static List<Person> listPersons(int size) {
        return IntStream.range(0, size).mapToObj(v -> newPerson()).collect(Collectors.toList());
    }

    private static List<Car> listCars(int size) {
        return IntStream.range(0, size).mapToObj(v -> newCar()).collect(Collectors.toList());
    }

    public static List<Base> newList(int size) {
        return IntStream.range(0, size).mapToObj(v -> {
            if (rnd.nextBoolean()) return newPerson();
            else return newCar();
        }).collect(Collectors.toList());
    }

}