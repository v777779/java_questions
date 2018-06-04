package nio;

import nio.regex.MainRegex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main05R {
    public static void main(String[] args) {

// regex
        System.out.printf(FORMAT, "Regex methods:");
        String regex = "abdc";
        String s = "abdc efgh1 abcd true come before efgh come before2 abdc efgh3";
        String regex2 = "abdc.*efgh3$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        String[] ss = p.split(s, 8);
        for (String str : ss) {
            System.out.printf("%s%n", str);
        }
        System.out.printf("matches  :%b  flags:%d%n", Pattern.matches(regex, s), p.flags());
        System.out.printf("matches  :%b%n", Pattern.matches(regex2, s));
        Pattern p2 = Pattern.compile(regex);
        Matcher m2 = p2.matcher(s);
        System.out.printf("matches2 :%b%n", m2.matches());
        System.out.printf("matches2 :%b%n", Pattern.compile(regex2).matcher(s).matches());

        System.out.printf(FORMAT, "Pattern Syntax:");
        try {
//            p = Pattern.compile("abdc { cdba)");

        } catch (PatternSyntaxException e) {
            System.out.printf("getDescription:%s%n", e.getDescription());
            System.out.printf("getIndex      :%s%n", e.getIndex());
            System.out.printf("getMessage    :%s%n", e.getMessage());
            System.out.printf("getPattern    :%s%n", e.getPattern());
        }
        System.out.printf(FORMAT, "Matcher:");
        Matcher m3 = p.matcher(s);

        System.out.printf("m3(abdc)   matches  :%b%n", m3.matches());
        System.out.printf("m3(abdc)   lookingAt:%b%n", m3.lookingAt());
        System.out.printf("m3(abdc) region(0,4):%b%n", m3.region(0, 4).matches());

// regex group
        System.out.printf(FORMAT, "Regex group:");
        regex = "abdc";
        s = "abdc efgh1 abcd true come before efgh come before2 abdc efgh3";
        MainRegex.main(regex, s);
        MainRegex.main("ox", "ox");
        MainRegex.main("box", "ox");
        MainRegex.main("ox", "box");
        MainRegex.main("\\sox", " b ox ");           // spec character standard usage
        MainRegex.main("\\\\sox", " b\\sox ");       // spec character as char
        MainRegex.main("\\Q\\s\\Eox", " b\\sox ");   // spec character as char

// pattern DOTALL
        System.out.printf(FORMAT, "Regex Pattern.DOTALL:");
        regex = "abdc.*";
        s = "abdc efgh1 abcd true. come before\r efgh come before2 abdc efgh3";
        p = Pattern.compile(regex, Pattern.DOTALL);
        String[] strings = p.split(s);
        for (String string : strings) {
            System.out.printf("%s%n", string);
        }
        MainRegex.main(regex, s);
        MainRegex.main(regex, s, Pattern.DOTALL);

// spaces
        System.out.printf(FORMAT, "Regex spaces:");
        MainRegex.main("\\sox", " b ox ");
        MainRegex.main("\\sox", " b ox \nox b\rox box");
//words
        System.out.printf(FORMAT, "Regex words:");
        MainRegex.main("\\wbc fd\\w", "abc fdc");

//group numbers
        System.out.printf(FORMAT, "Regex group numbers:");
        MainRegex.main("abc (bdf) (cfd) \\2 \\1", "abc bdf cfd cfd bdf abc cfd abc bdf cfd cfd bdfa");
        MainRegex.main("(abc) (bdf) (cfd) \\3 \\2", "abc bdf cfd cfd bdf abc cfd abc bdf cfd cfd bdfa");


//boundary numbers
        System.out.printf(FORMAT, "Regex word boundary greedy and not:");
        MainRegex.main("\\bb.*d\\b", "abc bdf cfd cfd bdf abc");
        MainRegex.main("\\bb.*?d\\b", "abc bdf cfd cfd bdf abc");

//boundary zero-length
        System.out.printf(FORMAT, "Regex boundary zero-length:");
        MainRegex.main("\\b\\b", "I think");
        MainRegex.main(".?", "a");


// greedy
        System.out.printf(FORMAT, "Regex word boundary greedy and not:");
        MainRegex.main("a.*b", "abc cdb cdb ccdc ");
        MainRegex.main("a.*?b", "abc cdb cdb ccdc ");
// greedy super
        MainRegex.main("a.*+b", "abc cdb cdb ccdc ");        // empty
        MainRegex.main("a.*+", "abc cdb cdb ccdc ");         // all string up to the end
        MainRegex.main("abc cdb c*+", "abc cdb cccdefgt t"); //

        System.out.printf(FORMAT, "Regex quantifiers demo:");
        MainRegex.main(".*end", "wend rend end");        // greedy
        MainRegex.main(".*?end", "wend rend end");       // least
        MainRegex.main(".*+end", "wend rend end");        // super greedy

        System.out.printf(FORMAT, "Regex Practical demo:");
        regex = "(\\(\\d{3}\\))?\\s*\\d{3}-\\d{4}";
        MainRegex.main(regex, "(800) 555-1212");
        MainRegex.main(regex, "(800)555-1212");
        MainRegex.main(regex, "555-1212");
// \\G
        System.out.printf(FORMAT, "Regex boundary \\G:");
        MainRegex.main("abc.*\\G?d", "abc cdb cdb ccdc ");
        MainRegex.main("abc.*?\\G?d", "abc cdb cdb ccdc ");

// \\G and zero-length
        MainRegex.main(".?\\G", "a");
        MainRegex.main("d\\G*.?g", "dog dag");  // \\G? относится к самом символу \\G либо он есть либо нет
        MainRegex.main("\\G*.?g", "dog dag");  // \\G? относится к самом символу \\G либо он есть либо нет

// regex question
        System.out.printf(FORMAT, "Regex Question:");
        String sText = "Regex   word     boundary  greedy   and   not:   spaces removal string";
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(sText);

        String sResult = matcher.replaceAll(" ");
        System.out.printf("source:%s%n",sText);
        System.out.printf("result:%s%n",sResult);

    }
}
