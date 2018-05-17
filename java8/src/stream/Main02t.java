package stream;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02t {
    public static void main(String[] args) {
        int[] values = {3, 10, 6, 1, 4, 8, 2, 5, 9, 7};
        int[] empty = new int[0];
        List<Integer> list = new ArrayList<>();
        List<Integer> emptyList = new ArrayList<>();

// terminal
// forEach, average, max, min, count, average, reduce, collect, findFirst, findAny, anyMatch, allMatch

// forEach
        IntStream.of(values).forEach(value -> System.out.printf("%d ", value));  // consumer used
        System.out.println();

        IntConsumer intConsumer = (value) -> System.out.printf("%d.", value);     // explicit usage
        IntStream.of(values).forEach(intConsumer);
        System.out.println();

        IntConsumer consumerA = (value) -> {
            value++;
            list.add(value);
        };
        IntConsumer consumerB = list::add;
        Consumer<Integer> consumerP = (value) -> System.out.printf("%d ", value);

        IntStream.of(values).forEach(consumerA);            // add value to list
        IntStream.of(values).forEach(consumerB);            // add value to list

        System.out.println("Terminal Reduction Operations");

        System.out.print("int[]                : ");
        IntStream.of(values).forEach(v -> System.out.printf("%d ", v));
        System.out.println();

        long[] longs = new long[values.length];
        IntStream.range(0, values.length).forEach(i -> longs[i] = (long) values[i]);
        System.out.print("long[]               : ");
        LongStream.of(longs).forEach(v -> System.out.printf("%d ", v));
        System.out.println();

// terminal reduction
// count,min,max,sum,average, summaryStatistics, reduce
//integer
        System.out.printf("int[]         count  : %d \n", IntStream.of(values).count());
        System.out.printf("int[]          min   : %d \n", IntStream.of(values).min().getAsInt());
        System.out.printf("int[]          max   : %d \n", IntStream.of(values).max().getAsInt());
        System.out.printf("int[]          sum   : %d \n", IntStream.of(values).sum());
        System.out.printf("int[]      average   : %.1f \n", IntStream.of(values).average().getAsDouble());   // no check
        System.out.printf("int[]      average   : %.1f \n", IntStream.of(values).average().orElse(0));  // 0 for empty
        System.out.printf("int[]      average   : %.1f \n", IntStream.of(values).average().orElseGet(() -> 0));// 0 for empty
        System.out.printf("int[]      reduce add: %d \n", IntStream.of(values).reduce((v1, v2) -> v1 + v2).getAsInt());
        System.out.printf("int[]      reduce add: %d \n", IntStream.of(values).reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("int[]      reduce mul: %d \n", IntStream.of(values).reduce((v1, v2) -> v1 * v2).getAsInt());
        System.out.printf("int[]      reduce mul: %d \n", IntStream.of(values).reduce(1, (v1, v2) -> v1 * v2));

        IntBinaryOperator intBinAdd = (v1, v2) -> v1 + v2;
        IntBinaryOperator intBinMul = (v1, v2) -> v1 * v2;
        System.out.printf("int[]      reduce add: %d \n", IntStream.of(values).reduce(intBinAdd).getAsInt());
        System.out.printf("int[]      reduce mul: %d \n", IntStream.of(values).reduce(1, intBinMul));

        System.out.printf("int[]empty   count   : %d \n", IntStream.of(empty).count());
        System.out.printf("int[]empty average   : %.1f \n", IntStream.of(empty).average().orElse(0));
        System.out.printf("int[]empty average   : %.1f \n", IntStream.of(empty).average().orElseGet(() -> 0));
        System.out.printf("int[]empty   count   : %d \n", IntStream.of(empty).count());

        System.out.printf("int[]empty reduce add: %d \n", IntStream.of(empty).reduce((v1, v2) -> v1 + v2).orElse(0));
        System.out.printf("int[]empty reduce add: %d \n", IntStream.of(empty).reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("int[]empty reduce mul: %d \n", IntStream.of(empty).reduce((v1, v2) -> v1 * v2).orElse(1));
        System.out.printf("int[]empty reduce mul: %d \n", IntStream.of(empty).reduce(1, (v1, v2) -> v1 * v2));


//summary statistics
        IntSummaryStatistics ss = IntStream.of(values).summaryStatistics();
        System.out.println("summary statistics   : " + ss.getAverage() + " " + ss.getCount() + " " + ss.getMax() + " " + ss.getMin() + " " + ss.getSum());
//optional
        IntStream.of(values).average().ifPresent((e) -> System.out.println("optional ifPresented : " + e));
        OptionalDouble oD = IntStream.of(values).average();
        if (oD.isPresent()) System.out.println("optional isPresented : " + oD.getAsDouble());
//reduce
        System.out.printf("reduce int[]         : %d \n", IntStream.of(values).reduce((v1, v2) -> v1 + v2).getAsInt());
        System.out.printf("reduce int[] empty   : %d \n", IntStream.of(empty).reduce(0, (v1, v2) -> v1 + v2));
        System.out.printf("reduce int[] empty   : %d \n", IntStream.of(empty).reduce((v1, v2) -> v1 + v2).orElse(0));
        System.out.printf("reduce int[] empty   : %d \n", IntStream.of(empty).reduce((v1, v2) -> v1 + v2).orElseGet(() -> 0));

// mutable reduction
        System.out.println("Terminal Mutable Reduction:");
// collect
//        Arrays.stream(new int[10]).collect(Collectors.toList());

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
//
//        IntFunction<String> intF = v->String.valueOf(v)+"..";
//        IntFunction<String> intFS =String::valueOf;
//        Function<Integer,String> func = v->String.valueOf(v)+"..";
//        Function<Integer,String> funcS = String::valueOf;
//
////        System.out.println("int[]: "+IntStream.of(values).map(v->v).collect(Collectors.joining("_")));
//        System.out.println("int[]: " + IntStream.of(values).mapToObj(String::valueOf).collect(Collectors.joining("_")));
//        System.out.println("list : " + list.stream().map(Object::toString).collect(Collectors.joining("_")));
//
////function
//        System.out.println("int[]: " + IntStream.of(values).mapToObj(intF).collect(Collectors.joining()));
//        System.out.println("int[]: " + IntStream.of(values).mapToObj(intFS).collect(Collectors.joining("_")));
//        System.out.println("list: " + list.stream().map(func).collect(Collectors.joining()));
//        System.out.println("list: " + list.stream().map(funcS).collect(Collectors.joining("_")));
//
//
//        list.stream().forEach(consumerP);          // print every value
//        System.out.println();
//        System.out.println();

    }
}
