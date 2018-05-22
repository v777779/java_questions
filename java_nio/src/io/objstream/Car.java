package io.objstream;

public class Car extends Base {
    static final String[] NAMES = {
            "Porsche", "Bentley", "Jaguar", "Lamborghini", "Bugatti", "Maserati", "Ferrari"
    };
    static final int AGE_MIN = 0;
    static final int AGE_MAX = 10;
    static final double PRICE_MIN = 499.99;
    static final double PRICE_MAX = 499999.99;

    private String model;
    private int age;
    private double price;

    public Car(String model, int age, double price) {
        this.model = model;
        this.age = age;
        this.price = price;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getName() {
        return model;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("< %-8s %2dy $%-9.2f >", model, age, price);
    }

}

