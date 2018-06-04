package nio.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 04-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainRegex {
//    private static final int[] PATTERN_FLAGS = {
//            Pattern.CASE_INSENSITIVE,
//            Pattern.DOTALL,
//            Pattern.CANON_EQ,
//            Pattern.MULTILINE,
//            Pattern.COMMENTS,
//            Pattern.LITERAL,
//            Pattern.UNICODE_CASE,
//            Pattern.UNICODE_CHARACTER_CLASS,
//            Pattern.UNIX_LINES
//
//    };

    public static void main(String regex, String s) {
        System.out.printf(FORMAT, "Regex group:");
        main(new String[]{regex, s});
    }

    public static void main(String regex, String s, int flags) {
        System.out.printf(FORMAT, "Regex group flags:");
        main(new String[]{regex, s, String.valueOf(flags)});
    }
    public static void main(String[] args) {

        int flags = 0;
        if (args.length < 2) {
            System.out.printf(FORMAT, "Usage: MainRegex.main(new String[]{regex, input}");
            return;
        }

        if (args.length == 3) {
            try {
                flags = Integer.parseInt(args[2]);

            } catch (NumberFormatException e) {
                System.out.printf("parse flags: failed%n");
            }
        }

        try {
            System.out.printf("regex: %s%n", args[0]);
            System.out.printf("input: %s%n", args[1]);
            Pattern p = Pattern.compile(args[0]);
            if (flags != 0) {
                p = Pattern.compile(args[0], flags);
            }
            Matcher m = p.matcher(args[1]);
            while (m.find()) {
                System.out.printf("group[%s] start:%d end:%d%n", m.group(), m.start(), m.end());
            }

        } catch (PatternSyntaxException e) {
            System.out.printf(FORMAT, "PatternSyntaxException:");
            System.out.printf("regex message:%s%n" + e.getMessage());
            System.out.printf("description  :%s%n" + e.getDescription());
            System.out.printf("index        :%s%n" + e.getIndex());
            System.out.printf("pattern      :%s%n" + e.getPattern());

        }
    }
}
