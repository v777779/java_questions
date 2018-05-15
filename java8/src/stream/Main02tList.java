package stream;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02tList {
    public static void main(String[] args) {
        Integer[] values = {3, 10, 6, 1, 4, 8, 2, 5, 9, 7};
        int[] empty = new int[0];
        List<Integer> list = new ArrayList<>();
        List<Integer> emptyList = new ArrayList<>();
// list
        System.out.println("Terminal Operations");
        Stream.of(values).forEach(list::add);

// terminal
        Consumer<Integer> consumerP = (value) -> System.out.printf("%d ", value);
        Function<Integer, String> func = v -> String.valueOf(v) + "..";
        Function<Integer, String> funcS = String::valueOf;

//foreach
        System.out.print("forEach stream       : ");
        list.stream().forEach(consumerP);          // print every value
        System.out.println();

        System.out.print("forEach              : ");
        list.forEach(consumerP);          // print every value
        System.out.println();


// terminal reduction
// count,min,max,sum,average, summaryStatistics, reduce
        System.out.println("Terminal Reduction Operations");
//list
        System.out.println("List:");
//count
        System.out.printf("List<Integer> count  : %d \n", list.stream().count());
//min
        System.out.printf("List<Integer> min    : %d \n", list.stream().min(Integer::compareTo).get());
        System.out.printf("List<Integer> min    : %d \n", list.stream().min(Integer::compareTo).orElse(0));
        System.out.printf("List<Integer> min    : %d \n", list.stream().min(Integer::compareTo).orElseGet(() -> null));
//max
        System.out.printf("List<Integer> max    : %d \n", list.stream().max(Integer::compareTo).get());
        System.out.printf("List<Integer> max    : %d \n", list.stream().max(Integer::compareTo).orElse(0));
        System.out.printf("List<Integer> max    : %d \n", list.stream().max(Integer::compareTo).orElseGet(() -> null));
//sum
        System.out.printf("List<Integer> sum    : %d \n", list.stream().reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("List<Integer> sum    : %d \n", list.stream().reduce((v1, v2) -> v1 + v2).orElse(0));
        System.out.printf("List<Integer> sum    : %d \n", list.stream().reduce((v1, v2) -> v1 + v2).orElseGet(() -> 0));
//average
        System.out.printf("List<Integer> average: %.2f \n", (double) list.stream()
                .reduce(0, (v1, v2) -> v1 + v2) / list.stream().count());

        System.out.printf("List<Integer> average: %.2f \n", (double) list.stream()
                .reduce((v1, v2) -> v1 + v2).orElse(0) / list.stream().count());

        System.out.printf("List<Integer> average: %.2f \n",
                (double) (list.stream().reduce((v1, v2) -> v1 + v2).orElseGet(() -> 0)) /
                        (list.size() == 0 ? 1 : list.size())
        );
//average mapToInt
        System.out.printf("List<> mapTo  average: %.2f \n",
                list.stream()
                        .mapToInt(value -> value)    // ToIntFunction{}  not Function.Identity()
                        .average().orElse(0));

//statistics mapToInt
        System.out.printf("%nList<> mapTo summary: %n");
        IntSummaryStatistics ss = list.stream().mapToInt(value -> value).summaryStatistics();
        System.out.printf("count: %d min: %d max: %d sum: %d  average: %.1f %n",
                ss.getCount(), ss.getMin(), ss.getMax(), ss.getSum(), ss.getAverage());

//optional
        OptionalDouble oD2 = OptionalDouble.of((double) (list.stream().reduce((v1, v2) -> v1 + v2).orElseGet(() -> 0)) / list.size());

        oD2.ifPresent(e -> System.out.printf("average Optional ifPresented: %.1f\n", e));
        if (oD2.isPresent()) System.out.printf("average Optional isPresented: %.1f\n", oD2.getAsDouble());

//reduce
        BinaryOperator<Integer> bAdd = (v1, v2) -> v1 + v2;
        BinaryOperator<Integer> bMul = (v1, v2) -> v1 * v2;

        System.out.printf("List<> reduce add    : %d \n", list.stream().limit(10).reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("List<> reduce add    : %d \n", list.stream().limit(10).reduce((v1, v2) -> v1 + v2).get());
        System.out.printf("List<> reduce add    : %d \n", list.stream().limit(10).reduce(bAdd).orElse(0));
        System.out.printf("List<> reduce add    : %d \n", list.stream().limit(10).reduce(bAdd).orElseGet(() -> 0));

        System.out.printf("List<> reduce mul    : %d \n", list.stream().limit(10).reduce(1, (v1, v2) -> v1 * v2));
        System.out.printf("List<> reduce mul    : %d \n", list.stream().limit(10).reduce((v1, v2) -> v1 * v2).get());
        System.out.printf("List<> reduce mul    : %d \n", list.stream().limit(10).reduce(bMul).orElse(0));
        System.out.printf("List<> reduce mul    : %d \n", list.stream().limit(10).reduce(bMul).orElseGet(() -> 0));

// empty
        System.out.printf("List<> empty  count  : %d \n", emptyList.stream().min(Integer::compareTo).orElse(0));
        System.out.printf("List<> empty  count  : %d \n", emptyList.stream().min(Integer::compareTo).orElseGet(() -> 0));

        System.out.printf("List<> empty  min    : %d \n", emptyList.stream().min(Integer::compareTo).orElse(0));
        System.out.printf("List<> empty  min    : %d \n", emptyList.stream().min(Integer::compareTo).orElseGet(() -> 0));

        System.out.printf("List<> empty  max    : %d \n", emptyList.stream().max(Integer::compareTo).orElse(0));
        System.out.printf("List<> empty  max    : %d \n", emptyList.stream().max(Integer::compareTo).orElseGet(() -> 0));

        System.out.printf("List<> empty average : %.2f \n", (double) emptyList.stream()
                .reduce((v1, v2) -> v1 + v2).orElse(0) / emptyList.stream().count());

        System.out.printf("List<> empty average : %.2f \n",
                (double) (emptyList.stream().reduce(0, bAdd)) /
                        (emptyList.size() == 0 ? 1 : emptyList.size())
        );

        System.out.printf("List<> empty average : %.2f \n",
                (double) (emptyList.stream().reduce(bAdd).orElse(0)) /
                        (emptyList.size() == 0 ? 1 : emptyList.size())
        );

        System.out.printf("List<> empty average : %.2f \n",
                (double) (emptyList.stream().reduce(bAdd).orElseGet(() -> 0)) /
                        (emptyList.size() == 0 ? 1 : emptyList.size())
        );

        System.out.printf("List<> empty reduce  : %d %n", emptyList.stream().reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("List<> empty reduce  : %d %n", emptyList.stream().reduce(bAdd).orElse(0));
        System.out.printf("List<> empty reduce  : %d %n", emptyList.stream().reduce(bAdd).orElseGet(() -> 0));

// mutable reduction
        System.out.println("\nTerminal Mutable Reduction:");
        System.out.printf("Integer[]: %s %n", Arrays.asList(values));  // /n new %n

// collect
        System.out.println("List<> collect sorted: " + Arrays.stream(values)
                .sorted()
                .collect(Collectors.toList())
        );

        System.out.println("List<> collect filter: " + Stream.of(values)
                .filter(v -> v > 4)
                .sorted()
                .collect(Collectors.toList()));

        List<Integer> g4 = Stream.of(values).filter(v -> v > 4).collect(Collectors.toList());
        System.out.println("List<> g4 unsorted   : " + g4);
        System.out.println("List<> g4 sorted     : " + g4.stream().sorted().collect(Collectors.toList()));

        System.out.println("\nList<String>:");
        String[] strings = {"Red", "orange", "Yellow", "green", "Blue", "indigo", "Violet"};

        List<String> listS = Stream.of(strings).collect(Collectors.toList());
        System.out.printf("strings in uppercase     : %s%n",
                Stream.of(strings).map(v -> v.toUpperCase()).collect(Collectors.toList()));
        System.out.printf("strings in uppercase     : %s%n",
                Stream.of(strings).map(String::toUpperCase).collect(Collectors.toList()));

        System.out.printf("strings greater m sorted : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted((v1, v2) -> v1.compareToIgnoreCase(v2))
                        .collect(Collectors.toList()));

        System.out.printf("strings greater m sorted : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList()));

        System.out.printf("strings greater m asc    : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList()));

        System.out.printf("strings greater m desc   : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted((v1, v2) -> v2.compareToIgnoreCase(v1))
                        .collect(Collectors.toList()));


        System.out.printf("strings greater m asc    : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList()));

        System.out.printf("strings greater m desc   : %s%n",
                Stream.of(strings)
                        .filter(v -> v.compareToIgnoreCase("m") > 0)
                        .sorted(String.CASE_INSENSITIVE_ORDER.reversed())
                        .collect(Collectors.toList()));

//collector extension
        System.out.println("\nCollect extension:");
        Collector<Integer, ?, List<Integer>> c2 = Collector2.toList(values);  // ? any type
        System.out.println("List<> user collect2 : " + Stream.of(values)
                .filter(v -> v < 4)
                .sorted()
                .collect(c2));  // returns List<Integer>


//toArray
// search
//findFirst
//findAny
//anyMatch
//allMatch


//// print
//        System.out.print("list sorted: ");
//        list.stream().sorted().forEach(consumerP);          // print every value
//        System.out.println();
//// collect
//        System.out.println("list : " + list.stream().map(Object::toString).collect(Collectors.joining("_")));
//        System.out.println("list: " + list.stream().map(func).collect(Collectors.joining()));
//        System.out.println("list: " + list.stream().map(funcS).collect(Collectors.joining("_")));


    }

    private static class Collector2<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        Collector2(Supplier<A> supplier,
                   BiConsumer<A, T> accumulator,
                   BinaryOperator<A> combiner,
                   Function<A, R> finisher,
                   Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        Collector2(Supplier<A> supplier,
                   BiConsumer<A, T> accumulator,
                   BinaryOperator<A> combiner,
                   Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        private static <I, R> Function<I, R> castingIdentity() {
            return i -> (R) i;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

        private static Collector<Integer, ? extends List<Integer>, List<Integer>> toList(Integer[] values) {
            Supplier<List<Integer>> sA = () -> new ArrayList<>();    // создание List<>
            BiConsumer<List<Integer>, Integer> cT = (v, d) -> v.add(d);     // добавление элемента
            BinaryOperator<List<Integer>> bA = (v, w) -> {                  // добавление группы элементов
                v.addAll(w);                                                //        R apply(T t, U u);
                return v;
            };
            final Set<Characteristics> CH_ID
                    = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

            Collector<Integer, List<Integer>, List<Integer>> c2 = new Collector2<>(sA, cT, bA, CH_ID);
            return c2;

        }

        private static Collector<Integer, ? extends List<Integer>, List<Integer>> toList2(Integer[] values) {
            final Set<Characteristics> CH_ID
                    = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

            Collector<Integer, ? extends List<Integer>, List<Integer>> c2 = new Collector2<>(
                    ArrayList::new, List::add, (v, w) -> {
                v.addAll(w);
                return v;
            }, CH_ID);
            return c2;

        }

    }
}
