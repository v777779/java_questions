import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 13-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class java08 {
    public static void main(String[] args) {
        String[] strings = {"public", "static", "void", "main", "String", "args"
        };


        List<String> list = Arrays.stream(strings).sorted(String::compareTo).collect(Collectors.toList());
        list = Arrays.stream(strings)
                .sorted( Comparator.naturalOrder())
                .collect(Collectors.toList());
        System.out.println(list);
    }
}
