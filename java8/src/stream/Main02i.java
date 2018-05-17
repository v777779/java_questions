package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02i {
    public static void main(String[] args) {
        int[] values = {3, 10, 6, 1, 4, 8, 2, 5, 9, 7};
        int[] empty = new int[0];
        List<Integer> list = new ArrayList<>();
        IntStream.of(values).forEach(list::add);
        IntStream.of(values).forEach(v->list.add(v));
        List<Integer> emptyList = new ArrayList<>();

// intermediate
// filter, distinct, limit, map, sorted, range

        Consumer<Integer> consumer = (value) -> System.out.printf("%d ", value); // used Consumer<T>

// filter sorted map range
// integer
        IntPredicate intP = v -> v % 2 == 0;
        IntPredicate intP2 = v -> v % 2 != 0;
        IntPredicate intP3 = v -> v > 5;
        IntUnaryOperator intF = v -> v * 10;
        IntConsumer intC = (value) -> System.out.printf("%d ", value); // used Consumer<T>

        System.out.print("\nint[] even sorted  : ");
        IntStream.of(values)
                .filter(v -> v % 2 == 0)                            // intermediate
                .sorted()                                           // intermediate
                .forEach(v -> System.out.printf("%d ", v));         // terminal
        System.out.print("\nint[] even sorted  : ");
//predicate
        IntStream.of(values).filter(intP).sorted().forEach(intC);

        System.out.print("\nint[] odd mul sorted: ");
        IntStream.of(values).filter(v -> v % 2 != 0)
                .sorted()           //
                .map(v -> v * 10)
                .forEach(v -> System.out.printf("%d ", v));
//predicate, function
        System.out.print("\nint[] odd mul sorted: ");
        IntStream.of(values).filter(intP2).sorted().map(intF).forEach(intC);
// predicate and predicate, function
        System.out.print("\nint[] predicates    : ");
        IntStream.of(values).filter(intP.and(intP3)).sorted().map(intF).forEach(intC);  // combine Predicates
// range
        System.out.println("Range:");
        System.out.println("range            :"+IntStream.range(1,10).sum());  // просто сумма элементов
        System.out.println("range            :"+IntStream.rangeClosed(1,10).sum());  // просто сумма элементов

        long[] longs = new long[values.length];
        IntStream.range(0,values.length).forEach(i->longs[i] = values[i]);
        System.out.print("\nlong[] range: ");
        LongStream.of(longs).forEach(v-> System.out.print(v+" "));
        System.out.print("\nint[]  range: ");
        IntStream.range(0,values.length).forEach(i-> System.out.printf("%d ",values[i]));

//list
        System.out.print("\n\nList<Integer> :");
        Predicate<Integer> predicate = v -> v % 2 == 0;
        Predicate<Integer> predicate2 = v -> v % 2 != 0;
        Function<Integer,Integer> function = v -> v * 10;

        System.out.print("\nList<> even sorted   : ");
        list.stream().filter(v -> v % 2 == 0).sorted().forEach(v -> System.out.printf("%d ", v));
// predicate
        System.out.print("\nList<> even sorted   : ");
        list.stream().filter(predicate).sorted().forEach(consumer);

        System.out.print("\nList<> odd mul sorted: ");
        list.stream().filter(v -> v % 2 != 0).sorted().map(v -> v * 10).forEach(v -> System.out.printf("%d ", v));
// predicate2, function
        System.out.print("\nList<> odd mul sorted: ");
        list.stream().filter(predicate2).sorted().map(function).forEach(consumer);

    }
}
