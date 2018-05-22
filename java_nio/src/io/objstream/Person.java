package io.objstream;

public class Person extends Base {
    static final String[] NAMES = {
            "Bob", "James", "John", "Mike", "Stanley", "Tom", "Willey"
    };
    static final int AGE_MIN = 18;
    static final int AGE_MAX = 98;
    static final double HEIGHT_MIN = 158.99;
    static final double HEIGHT_MAX = 198.99;
    static final double WEIGHT_MIN = 58.99;
    static final double WEIGHT_MAX = 108.99;

    private String name;
    private int age;
    private double height;
    private double weight;

    public Person(String name, int age, double height, double weight) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%-10s: %2dy %.0fcm %.1fKg", name, age, height, weight);
    }

}