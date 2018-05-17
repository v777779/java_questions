package java_08;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 16-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01A {

    private interface IUser {
        static boolean valid(String s) {
            return s != null && !s.isEmpty();
        }

        static boolean valid(int i) {
            return i < 0;
        }

        default void print(String s) {
            if (!valid(s)) s = "Message: empty";
            System.out.println(s);
        }

        String print(Integer i);
    }

    private static class User implements IUser {

        @Override
        public String print(Integer i) {
            IUser.super.print("startInteger");
            print("Send Message");

            if (IUser.valid(i)) i = 0;
            return String.valueOf(i + 1);
        }
    }

    public static void main(String[] args) {
// answer additions
        String format = "%n%s%n-------------------%n";
        System.out.printf(format, "Answer Additions:");
        System.out.printf(format, "Lambda:");
        List<String> names = Arrays.asList("Bob", "James", "Red", "Mike", "John", "Jimmy", "Steve");
        names = names.stream()
                .sorted((s1, s2) -> s2.compareTo(s1))
                .collect(Collectors.toList());
        System.out.println(names);

        Collections.sort(names, String::compareTo);
        System.out.println(names);

        List<String> list = new ArrayList<>();
        names.stream()
                .map(v -> v.toLowerCase())
                .forEach(v -> list.add(v));

        names.stream()
                .map(String::toUpperCase)
                .forEach(list::add);
        System.out.println(list);

        Map<Character, List<String>> tMap = list.stream()
                .collect(Collectors.groupingBy(v -> v.toLowerCase().charAt(0),
                        () -> new TreeMap(), Collectors.toList()));

        Map<Character, List<String>> tMap2 = list.stream()
                .collect(Collectors.groupingBy(v -> v.toLowerCase().charAt(0), TreeMap::new, Collectors.toList()));


        System.out.println(tMap);
        System.out.println(tMap2);

// binary operator
        System.out.printf(format, "BinaryOperator:");
        BinaryOperator<Integer> b = (v1, v2) -> v1 + v2;
        System.out.print("b:" + b.apply(1, 2) + " ");

        BiPredicate<Integer, String> p = (i, s) -> i > Integer.parseInt(s);
        System.out.print("p:" + p.test(2, "1") + " ");

        DoubleBinaryOperator db = (v1, v2) -> v1 + v2;
        System.out.print("db:" + db.applyAsDouble(1.1, 2.1) + " ");
        DoubleConsumer dc = (v) -> System.out.printf("dc: %.1f ", (v));
        dc.accept(2.2);
        DoubleFunction<String> df = v -> String.format("%.1f ", v);
        System.out.print("df:" + df.apply(4.3));
        DoubleToIntFunction dfInt = v -> (int) v;
        System.out.print("dfInt:" + dfInt.applyAsInt(5.5) + " ");

        DoubleUnaryOperator du = v -> v + 1;
        System.out.print("du:" + du.applyAsDouble(6.4) + " ");

        System.out.println();
// consumer
        List<String> listS = new ArrayList<>();
        ObjDoubleConsumer<String> odc = (v1, v2) -> list.add(String.format("%s", v1 + String.valueOf(v2)));
        odc.accept("value:", 1.35);
        System.out.println(list);

        ToDoubleBiFunction<String, Long> tdbf = (s1, v1) -> Long.parseLong(s1) + v1 + 0.35;
        System.out.println("tdbf:" + tdbf.applyAsDouble("120", 12L));

// StringJoiner
        StringJoiner stringJoiner = new StringJoiner(",", "{", "}");
        System.out.println("stringJoiner: " + stringJoiner);
        stringJoiner.setEmptyValue("[]");
        System.out.println("stringJoiner: " + stringJoiner);
        stringJoiner.add("value");
        stringJoiner.add("add");
        System.out.println("stringJoiner: " + stringJoiner);

// javadoc example
        StringJoiner sj = new StringJoiner(":", "[", "]");
        sj.add("George").add("Sally").add("Fred");
        String desiredString = sj.toString();
        System.out.println(sj.toString());

//        A StringJoiner may be employed to create formatted output from a Stream using Collectors.joining(CharSequence). For example:

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        String commaSeparatedNumbers = numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
// default interface
        IUser iUser = (v) -> (String.valueOf(v + 1));
        iUser.print("Message:" + iUser.print(2));

        User user = new User();
        user.print("Message:" + user.print(2));
        user.print("");

// stream
        System.out.printf(format, "Random stream:");
        Random rnd = new Random();
// without boundaries
        rnd.ints().limit(10).forEach(v -> System.out.printf("%d ", v));
        System.out.println();
// with boundaries
        IntStream.range(0, 10)
                .forEach(v -> System.out.printf("%d ", (int) (Math.random() * 100)));
        System.out.println();
        IntStream.range(0, 10).forEach(v -> System.out.printf("%d ", rnd.nextInt(100)));
        System.out.println();
        rnd.ints(10, 0, 100).forEach(v -> System.out.printf("%d ", v));
        System.out.println();

//        SecureRandom sRnd = new SecureRandom();
//        sRnd.doubles(50, 100).limit(10).forEach(v -> System.out.printf("%.1f ", v));
//        System.out.println();
// map
        System.out.printf(format, "Map:");
        rnd.ints(10, -10, 10)
                .map(v -> v * v)
                .distinct()
                .sorted()
                .forEach(v -> System.out.printf("%d ", v));

        System.out.println();
        List<Integer> listI = new ArrayList<>();
        rnd.ints(10, -10, 10)
                .sorted()
                .map(v -> {
                    listI.add(v);
                    return v * v;
                })
                .distinct()
                .boxed()
                .sorted(Comparator.comparingInt(v -> (Integer) v))
                .forEach(v -> System.out.printf("%d ", v));
        System.out.println();
        listI.forEach(v -> System.out.printf("%d ", v));
        System.out.println();

// empty strings
        System.out.printf(format, "Stream empty strings:");
        listS = Arrays.asList("bob", "mike", "", "steve", "", "susan", "john", "", "tim", "", "");
        long count = listS.stream().filter(String::isEmpty).count();
        System.out.println("count: " + count);
// sorted ints
        System.out.printf(format, "Stream random int sorted:");
        rnd.ints(10, 0, 100)
                .forEachOrdered(v -> System.out.printf("%02d ", v));
        System.out.println();

        rnd.ints(0, 100)
                .limit(10)
                .sorted()
                .forEach(v -> System.out.printf("%02d ", v));
        System.out.println();

//parallel
        System.out.printf(format, "Stream parallel:");
        List<Integer> listP = rnd.ints(100, 0, 100)
                .boxed()
                .collect(Collectors.toList());
        listP.stream().parallel()
                .limit(10)
                .forEachOrdered(v -> System.out.printf("%02d ", v));
        System.out.println();

        listP.parallelStream()
                .limit(10)
                .sorted()
                .forEach(v -> System.out.printf("%02d ", v));
        System.out.println();

        long count2 = listS.parallelStream().filter(String::isEmpty).count();
        long count3 = listS.stream().parallel().filter(String::isEmpty).count();
        System.out.println("count2:" + count2 + " count3:" + count3);
// statistics
        System.out.printf(format, "Statistics:");

        System.out.println("listI:"+listI);
        int[] values = listI.stream().mapToInt(v->v).toArray();

        int maxInteger = listI.stream().max(Integer::compareTo).orElse(0);
        int minInteger = listI.stream().min(Integer::compareTo).orElse(0);
        int sumInteger = listI.stream().reduce(0, (v1, v2) -> v1 + v2);
        double averageInteger = (double) sumInteger / listI.size();

        System.out.println("min: " + minInteger + " max: " + maxInteger +
                " sum:" + sumInteger + " avg:" + averageInteger);

        IntSummaryStatistics statistics = listI.stream().mapToInt(v -> v).summaryStatistics();
        int max = listI.stream().mapToInt(v -> v).max().orElse(0);
        int min = listI.stream().mapToInt(v -> v).min().orElse(0);
        int sum = listI.stream().mapToInt(v -> v).sum();
        double average = listI.stream().mapToInt(v -> v).average().orElse(0);
        System.out.println("min: " + min + " max: " + max +
                " sum:" + sum + " avg:" + average);
        System.out.println("min: " + statistics.getMin() + " max: " + statistics.getMax() +
                " sum:" + statistics.getSum() + " avg:" + statistics.getAverage());

        statistics = IntStream.of(values).summaryStatistics();
        max = IntStream.of(values).max().orElse(0);
        min = IntStream.of(values).min().orElse(0);
        sum = IntStream.of(values).sum();
        average = IntStream.of(values).average().orElse(0);
        System.out.println("min: " + min + " max: " + max +
                " sum:" + sum + " avg:" + average);
        System.out.println("min: " + statistics.getMin() + " max: " + statistics.getMax() +
                " sum:" + statistics.getSum() + " avg:" + statistics.getAverage());

// Optional
        OptionalInt optInt = IntStream.of(values).max();
        OptionalDouble optDbl = listI.stream().mapToInt(v -> v).average();

        System.out.println("oInt:"+optInt.isPresent()+" value:"+optInt.orElseGet(()->-1));  // supplier
        System.out.println("oDbl:"+optDbl.isPresent()+" value:"+optDbl.orElse(0));     // supplier

    }


}
