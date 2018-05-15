package stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04File {
    public static void main(String[] args) {
        String path = Main04File.class.getResource("Chapter2Paragraph.txt").getFile();
        Path p = Paths.get(path.replaceFirst("/", ""));

        Pattern pattern = Pattern.compile("\\s+");
        String regex = "(?!')\\p{P}|\\p{Z}}";             // look except ' (?!'), look all
//          regex = "(?='|,|!)\\p{P}";                      // look only ' or , or !
//          regex = "(?!'|,|!)\\p{P}";                      // look except ' or , or !
//          regex = "(?!'|!|,|\\)|\\()\\p{P}";              // look except ' ! , ) (
//          \\p{P}  искать любую пунктуацию
//          \\{Z}   искать любые пробелы
//          regex = "(?!')\\p{P}|(?! )\\p{Z}}";             // look except ' (?!') среди пунктуации и пробелов
//        String regex = "(?!')\\p{P}|(?=\\s\\s)\\p{Z}";      // look except ' (?!'), look more than 1 spaces

        try {
            Map<String, Long> map =
                    Files.lines(Paths.get("data/Ch2P.txt"))  // file to stream of lines
                    .map(line -> line.replaceAll(regex, ""))
                    .flatMap(line -> pattern.splitAsStream(line))
                    .collect(Collectors.groupingBy(String::toLowerCase,
                            TreeMap::new, Collectors.counting()));      // map of words and count
// files in project folder
//            Files.lines(Paths.get("data/Ch2P.txt"))
//                    .map(line -> line.replaceAll(regex, ""))
//                    .forEach(System.out::println);                      // stream of lines
//            Files.lines(Paths.get("data/Ch2P.txt"))
//                    .map(line -> line.replaceAll(regex, ""))
//                    .flatMap(line->pattern.splitAsStream(line))         // split over pattern
//                    .forEach(System.out::println);                      // stream of strings


            Map<Character, List<Map.Entry<String, Long>>> eMap =
                    map.entrySet()
                            .stream()
                    .collect(Collectors.groupingBy(e -> e.getKey().charAt(0),
                            TreeMap::new, Collectors.toList()));
             eMap.forEach((letter, list)-> {

                 System.out.printf("%n%C%n",letter);
                 list.stream().forEach(e->
                         System.out.printf("%12s: %d%n",e.getKey(),e.getValue()));

             });



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
