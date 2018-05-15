package examples;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 {
    private static final String[] STRING_SOURCE = {
            "-rwxr-xr-x",             // 755
            "-rw-r--r--"
    };

    private static String getDigits3(String s) {
// array n
        String s2 = Arrays.stream(s.chars()
                .skip(s.length() - 9)
                .map(n -> {
                    if (n == '-') return 0;
                    if (n == 'r' || n == 'w' | n == 'x') return 1;
                    return n;
                })
                .mapToObj(Integer::toString)
                .collect(joining())
                .split("(?<=\\G...)"))
                .map(v -> Integer.parseInt(v, 2) + "")
                .collect(Collectors.joining());

        return s2;
    }
    private static String getDigits2(String s) {
        if (!s.matches(".*([r-][w-][x-]){3}$")) {
            throw new IllegalArgumentException("Achtung!");
        }

        char[] chars = s.substring(s.length() - 9)
                .replaceAll("[rwx]", "1")
                .replaceAll("-", "0").toCharArray();

        return IntStream.range(0, 3)
                .map(i -> (chars[i * 3] - '0') * 4 + (chars[i * 3 + 1] - '0') * 2 + chars[i * 3 + 2] - '0')
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    private static String getDigits(String s) {
        if (!s.matches(".*([r-][w-][x-]){3}$")) {
            throw new IllegalArgumentException("Achtung!");
        }

        Function<String, String> mapper = rwx -> String.valueOf(
                Integer.parseInt(rwx
                        .replaceAll("[rwx]", "1")
                        .replaceAll("-", "0"), 2));

        return  Arrays.stream(s.substring(s.length() - 9)
                .split("(?<=\\G...)"))
                .map(mapper)
                .collect(joining());

    }

    public static void main(String[] args) {
// regex (abc){3} >>  abcabcabc
        String s = STRING_SOURCE[0];

// original
        if (!s.matches(".*([r-][w-][x-]){3}$")) {
            throw new IllegalArgumentException("Achtung!");
        }

        Function<String, String> mapper = rwx -> String.valueOf(
                Integer.parseInt(rwx
                        .replaceAll("[rwx]", "1")
                        .replaceAll("-", "0"), 2));

        String s4 = Arrays.stream(s.substring(s.length() - 9)
                .split("(?<=\\G...)"))
                .map(mapper)
                .collect(joining());

// array n
        s = STRING_SOURCE[0];
        String s2 = Arrays.stream(s.chars()
                .skip(s.length() - 9)
                .map(n -> {
                    if (n == '-') return 0;
                    if (n == 'r' || n == 'w' | n == 'x') return 1;
                    return n;
                })
                .mapToObj(Integer::toString)
                .collect(joining())
                .split("(?<=\\G...)"))
                .map(v -> Integer.parseInt(v, 2) + "")
                .collect(Collectors.joining());

        System.out.println(s2);

// array original reversed
        s = STRING_SOURCE[0];
        s2 = Arrays.stream(
                s.substring(s.length() - 9)
                        .replaceAll("[rwx]", "1")
                        .replaceAll("-", "0")
                        .split("(?<=\\G...)"))
                .map(v -> Integer.parseInt(v, 2) + "")
                .collect(Collectors.joining());
        System.out.println(s);

// sequential
        s = STRING_SOURCE[0];
        char[] chars = s.substring(s.length() - 9)
                .replaceAll("[rwx]", "1")
                .replaceAll("-", "0").toCharArray();

        String s8 = IntStream.range(0, 3)
                .map(i -> (chars[i * 3] - '0') * 4 + (chars[i * 3 + 1] - '0') * 2 + chars[i * 3 + 2] - '0')
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        System.out.println(s8);


        System.out.println("Start measurement:");
        String[] strings = new String[1000];
        String[] strings2 = new String[1000];

        Instant start = Instant.now();
        for (int i = 0; i < 1000; i++) {
            strings[i] = getDigits(STRING_SOURCE[0]);
        }
        Instant middle = Instant.now();
        for (int i = 0; i < 1000; i++) {
            strings2[i] = getDigits(STRING_SOURCE[0]);
        }
        Instant end = Instant.now();
        System.out.println("Eof size:"+strings.length);
        System.out.println("Eof size:"+strings2.length);
        System.out.println("strings: "+Duration.between(start,middle).toMillis());
        System.out.println("strings2: "+Duration.between(middle,end).toMillis());

    }


}
