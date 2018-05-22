package io._add;

import java.util.stream.Stream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Check {
    public static void main(String[] args) {
        String[] strings = {
                "data_file.zip","D:/temp/file.zip","text.txt","D:/temp/m.txt","./_temp/cards.zip"
        };
        String format = "%n%s%n------------------------%n";
        System.out.printf(format,"IO Stream:");

        String s = "data_file.zip";
        String wildCard = "*.zip";

        String  regex = wildCard.replaceAll("\\*",".*");
        System.out.println(regex);

        System.out.println(s.replaceAll(regex,"&"));

        Stream.of(strings).map(v->v.replaceAll(regex,"&"))
                .forEach(v-> System.out.printf("%s%n",v));





    }
}
