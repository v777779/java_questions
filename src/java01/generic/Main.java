package java01.generic;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 30-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main {
    private static class Shape<T extends String> {
        String get(T t) {
            return "shape";
        }
//        String get(String t){  // не работает
//            return "shape";
//        }

        static void print() {
            System.out.println("This is shape");
        }

        void show() {
            System.out.println("This is shape");
        }

    }

    private static class Circle<T extends String> extends Shape {
        String get(String t) {
            return "cube";
        }

//        String get(T t){ // не работает
//            return "cube";
//        }


        static void print() {
            System.out.println("This is circle");
        }

        void show() {
            System.out.println("This is circle");
        }
    }

    private static class Cube<T> extends Shape {
        String get(String t) {
            return "cube";
        }

        static void print() {
            System.out.println("This is cube");
        }

        void show() {
            System.out.println("This is cube");
        }
    }


    public static void main(String[] args) {
        Shape[] shapes = {
                new Shape(), new Circle(), new Cube()
        };

        for (Shape shape : shapes) {
            shape.print(); // полиморфизм НЕ работает static
            shape.show();  // полиморфизм работает object
        }

    }
}
