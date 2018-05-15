package stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {
        int[] values = { 3,10,6,1,4,8,2,5,9,7 };

        IntStream.of(values).forEach(value -> System.out.printf("%d ",value));  // consumer used
        System.out.println();

        IntConsumer intConsumer = (value)-> System.out.printf("%d.",value);     // explicit usage
        IntStream.of(values).forEach(intConsumer);
        System.out.println();


        List<Integer> list = new ArrayList<>();
        IntStream.of(values).forEach((e)->list.add(e));     // standard
        IntStream.of(values).forEach(list::add);            // shortcut
        System.out.println(list);

// intermediate
// filter
        Consumer<Integer> consumer = (value)-> System.out.printf("%d.",value); // used Consumer<T>
        Predicate<Integer> predicate = (value)-> value > 5;
        list.stream().filter(predicate).forEach(consumer);
        System.out.println();
        list.stream().filter((value)-> value > 5).forEach((value)-> System.out.printf("%d,",value));
        System.out.println();
// distinct
        list.stream().distinct().forEach(consumer);
        System.out.println();
// limit
        list.stream().limit(7).forEach(consumer);
        System.out.println();
// map
        List<String> listS = new ArrayList<>();
        Function<Integer,String> functionS = (value)-> String.valueOf(value);
        Consumer<String> consumerS = (value)-> listS.add(value);
        list.stream().map(functionS).forEach(consumerS);
        System.out.println(listS);
        list.stream().map(String::valueOf).forEach(listS::add);
        System.out.println(listS);
// sorted
        Comparator<Integer> cInt = Integer::compare;
        Comparator<Integer> rInt = (i1,i2)->Integer.compare(i2,i1);

        Comparator<String> cStr = String::compareTo;
        Comparator<String> cStrN = (s1,s2)->Integer.compare(Integer.valueOf(s1),Integer.valueOf(s2));
        Comparator<String> cStrNS = Comparator.comparingInt(Integer::valueOf);
        Comparator<String> rStr = (s1,s2)-> s2.compareTo(s1);
        Comparator<String> rStrN = (s1,s2)->Integer.compare(Integer.valueOf(s2),Integer.valueOf(s1));

        System.out.println("sorted()");
        System.out.println("Integer:");
        list.stream().limit(10).sorted().forEach(consumer);
        System.out.println();
        list.stream().limit(10).sorted(rInt).forEach(consumer);
        System.out.println();
        list.stream().limit(10).sorted(Comparator.reverseOrder()).forEach(consumer);
        System.out.println();
        System.out.println("String:");
        Consumer<String> consumerP = (s)->System.out.print(s+" ");
        listS.stream().limit(10).sorted().forEach(consumerP);
        System.out.println();
        listS.stream().limit(10).sorted(rStr).forEach(consumerP);
        System.out.println();
        listS.stream().limit(10).sorted(Comparator.reverseOrder()).forEach(consumerP);
        System.out.println();
        System.out.println("String as Integer:");
        listS.stream().limit(10).sorted(cStrN).forEach(consumerP);
        System.out.println();
        listS.stream().limit(10).sorted(cStrNS).forEach(consumerP);
        System.out.println();
        listS.stream().limit(10).sorted(rStrN).forEach(consumerP);
        System.out.println();
    }
}
