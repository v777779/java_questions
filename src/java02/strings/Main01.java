package java02.strings;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {
// concat
        String s1 = "AAA";
        String s2 = "BBB";
        System.out.println(s1 + " " + s2);
        System.out.println("Concatenation:");
        System.out.println(s1.concat(s2));
        StringBuilder sb = new StringBuilder(s1);
        sb.append(s2);
        System.out.println(sb.toString());

// reverse
        System.out.println("Reverse:");
        System.out.println(sb.toString());
        sb.reverse();
        System.out.println(sb.toString());

        char[] chars = sb.toString().toCharArray();
        char[] reverseChars = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            reverseChars[chars.length - i - 1] = chars[i];
        }
        s2 = new String(reverseChars);
        System.out.println(s2);
// removal
        String s3 = "Transaction";
        if (s3 != null) System.out.println(s3.replace("ac", ""));
        if (s3 != null) System.out.println(s3.replaceAll("a.{2}", ""));  // quantifier


    }
}
