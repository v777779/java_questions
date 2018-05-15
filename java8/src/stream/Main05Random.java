package stream;


import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main05Random {
    public static void main(String[] args) {
// stream multithreading
// immutable
// lambda
// internal iteration
        Random random = new Random();
        int[]values = random.ints(60_000_000, 1, 7).toArray();
        System.out.printf("%-6s%s%n", "Face", "Frequency");
        Instant start = Instant.now();
        Map<Integer, Long> map =
                        IntStream.of(values)  // 1..6 (1inclusive 7 exclusive)
                        .boxed()                                                               // to stream<Integer>
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); //Map<Integer,Long>

        map.forEach((key, value) -> {
            System.out.printf("%-6d%d  %.2f%% %n", key, value, 100 * (value - 1e7) / 1e7);
        });
        Instant middle = Instant.now();

        System.out.printf("%-6s%s%n", "Face", "Frequency");
        Map<Integer, Long> map2 =
                        IntStream.of(values).parallel()
                        .boxed()                                                               // to stream<Integer>
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); //Map<Integer,Long>

        map2.forEach((key, value) -> {
            System.out.printf("%-6d%d  %.2f%% %n", key, value, 100 * (value - 1e7) / 1e7);
        });
        Instant end = Instant.now();

        System.out.printf("Sequential: %d%n",Duration.between(start,middle).toMillis());
        System.out.printf("Parallel  : %d%n",Duration.between(middle,end).toMillis());

    }
}
