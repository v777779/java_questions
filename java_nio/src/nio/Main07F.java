package nio;

import nio.formatter.Employee;
import nio.formatter.StockName;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main07F {

    private interface IAppend {
        IAppend append(String s);
    }

    private static class StringAppend implements IAppend {  // сделано чтобы возвращать данный класс
        public IAppend append(String s) {
            return this;
        }

        ;
    }


    public static void main(String[] args) {

// formatter
        System.out.printf(FORMAT, "Formatter:");


        IAppend a = new IAppend() {                     // прямая реализация, lambda тут не сработает
            @Override
            public IAppend append(String s) {
                return this;
            }
        };

        IAppend as = (v) -> new StringAppend().append(v); // через реализацию класса

        Formatter f = new Formatter();

        f.format(Locale.ENGLISH, "%d %x %c %f %s%n", 123, 123, 'X', 0.1, "Hello World!");
        System.out.printf("%s", f);
        System.out.printf("%n");
        f.format(Locale.ENGLISH, "%1$10.2f %2$05d %2$d %3$3.5s%n", 98.375, 1200, "StringLength");
        System.out.printf("%s", f);
        System.out.printf("%n");

        f.format(Locale.ENGLISH, "%1$12.1f [%2$2.5s] [%2$6.5s] [%3$2.5s] [%3$6.5s] [%3$6.8s]%n", 98.375, "ab", "abcdefg");
        System.out.printf("%s", f);
        System.out.printf("%n");

        f.format(Locale.ENGLISH, "String width.precision [8.4] abcdef:[%8.4s]%n", "abcdefg");
        f.format("index: %1$d %2$.1f %2$12.3f%n", 20, 212.3457534);

        f.flush();
        System.out.printf("%s", f);
        System.out.printf("%n");
        f.close();

        System.out.printf(FORMAT, "Formatter String Precision:");

        System.out.printf("[10.2s]  ABC             :[%10.2s]%n", "ABC");
        System.out.printf("[10.12s]ABC             :[%10.12s]%n", "ABC");
        System.out.printf("[10.2s] ABCDEFGHIJKLMNOP:[%10.2s]%n", "ABCDEFGHIJKLMNOP");
        System.out.printf("[10.12s]ABCDEFGHIJKLMNOP:[%10.12s]%n", "ABCDEFGHIJKLMNOP");
//sb
        System.out.printf(FORMAT, "Format and StringBuilder:");

        StringBuilder sb = new StringBuilder(); // Appendable
        f = new Formatter(sb, Locale.ENGLISH);
        f.format("%4$2s %3$2s %2$2s %1$2s%n", "a", "b", "c", "d");
        f.flush();

        System.out.printf("formatter    :%s", f);
        System.out.printf("formatter    :%s", f);
        System.out.printf("StringBuilder:%s", sb);
        f.close();


// locale
        System.out.printf(FORMAT, "Format and Locale:");
        sb = new StringBuilder();
        f = new Formatter(sb, Locale.ENGLISH);
        f.format(Locale.FRANCE, "e = %+10.4f%n", Math.E);
        f.format("Amount gained since last statement: $%(,.2f%n", 6217.58);
        f.format("Amount lost since last statement  : $%(,.2f%n", -6217.58);
        f.flush();
        System.out.printf("StringBuilder:%s%n", sb);
        f.close();

// date time
        System.out.printf(FORMAT, "Format and Date and Time:");
        LocalDateTime local = LocalDateTime.now();
        Calendar c = Calendar.getInstance();
        sb = new StringBuilder();
        f = new Formatter(sb, Locale.ENGLISH);

        f.format("Locale Date Time: %1$td,%1$tB,%1$tY %1$tH:%1$tm:%1$tS%n", local);
        f.format("Calendar Date Time: %1$td,%1$tB,%1$tY %1$tH:%1$tm:%1$tS%n", c);
        c = new GregorianCalendar(1995, Calendar.MAY, 23);
        f.format("Gregorian Date Time: %1$td,%1$tB,%1$tY %1$tH:%1$tm:%1$tS%n", c);
        ZonedDateTime zd = ZonedDateTime.of(local, ZoneId.of("GMT+3"));
        f.format("Zone Date Time: %1$td,%1$tB,%1$tY %1$tH:%1$tm:%1$tS %1$tZ %1$tz %n", zd);
        try {
            f.format("Local Date Time: %1$td,%1$tB,%1$tY %1$tH:%1$tm:%1$tS %1$tZ %1$tz %n", local);
        } catch (IllegalFormatConversionException e) {
            f.format("<Exception>%n%s%n", e);
        }
        f.format("Zone Date Time: %1$tD %1$tT  %1$tF %1$tR%n", zd);
        f.format("Zone Date Time: %1$tD %1$tr%n", zd);
        f.format("Zone Date Time: %1$tc%n", zd);

        f.flush();
        System.out.printf("%s%n", sb);
        f.close();

// Formattable
        System.out.printf(FORMAT, "Formattable StockName:");
        StockName stockName = new StockName("HUGE", "Huge Fruit, Inc.",
                "Fruite Titanesque, Inc");

        System.out.printf("%s%n", stockName);            // formatTo !!!
        System.out.printf("%s%n", stockName.toString()); // toString !!!

        f = new Formatter();
        f.format("%s%n", stockName);
        f.format("%s%n", stockName.toString());
        f.format("%#s%n", stockName);
        f.format("%-10.8s%n", stockName);
        f.format("%.12s%n", stockName);
        f.format(Locale.FRANCE, "%25s%n", stockName);


        f.flush();
        System.out.printf("%s%n", f);
        f.close();

        System.out.printf(FORMAT, "Formattable StockName:");
        f = new Formatter();

        Employee emp = new Employee("John Doe", 1000);
        f.format(Locale.ENGLISH, "[%s]%n", emp);
        f.format(Locale.FRENCH, "[%s]%n", emp);
        f.format("[%S]%n", emp);
        f.format("[%10.3s]%n", emp);
        f.format("[%-10.3s]%n", emp);
        f.format("[%#s]%n", emp);

        f.flush();
        System.out.printf("%s%n", f);
        f.close();

        System.out.printf(FORMAT, "Formatter Question:");

        sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("%d", 123);
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%x", 123);
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%c", 'X');
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%f", 0.1);
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%s%n", "Hello, World");
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%10.2f", 98.375);
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%05d", 123);
        System.out.println(formatter.toString());
        sb.setLength(0);

        formatter.format("%1$d %1$d", 123);
        System.out.println(formatter.toString());
        sb.setLength(0);
        try {
            formatter.format("%d %d", 123);
            System.out.println(formatter.toString());
        } catch (IllegalFormatException e) {
            System.out.printf("Exception:%s%n", e);
        }
        formatter.close();
        System.out.printf("Formatter finished...%n");

    }


}
