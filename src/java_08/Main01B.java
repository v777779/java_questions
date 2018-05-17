package java_08;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01B {
    public static void main(String[] args) {

// Data API
        String format = "%n%s%n-------------------%n";

        System.out.printf(format, "Java8 Date API:");

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("localDateTime   : "+localDateTime);

        LocalDateTime plusWeek = localDateTime.plus(1,ChronoUnit.WEEKS);
        System.out.println("plusWeek        : "+plusWeek);

        LocalDateTime plusMonth = localDateTime.plusMonths(1);
        System.out.println("plusMonth       : "+plusMonth);

        LocalDateTime plusYear = localDateTime.plus(1,ChronoUnit.YEARS);
//        LocalDateTime plusYear = localDateTime.plusYears(1);
        System.out.println("plusYear        : "+plusYear);

        LocalDateTime plus10Year = localDateTime.plus(1,ChronoUnit.DECADES);
//        LocalDateTime plus10Year = localDateTime.plusYears(10);
        System.out.println("plus10Year      : "+plus10Year);

        LocalDateTime nextTue = localDateTime.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        System.out.println("nextTue         : "+nextTue);

        LocalDateTime secondSat = localDateTime
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY))
                .with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        System.out.println("secondSat       : "+secondSat);


        localDateTime = LocalDateTime.now();
        long localMilli = localDateTime.toInstant(ZoneOffset.ofHoursMinutes(3,0)).toEpochMilli();
        System.out.println("LocalDateTime ms: "+localMilli);


        long timeMilliSeconds = Instant.now().toEpochMilli();
        System.out.println("Instant ms      : "+timeMilliSeconds);

        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime,ZoneId.of("Europe/Moscow"));
        System.out.println("Moscow ms       : "+zonedDateTime.toInstant().toEpochMilli());

        zonedDateTime = ZonedDateTime.of(localDateTime,ZoneId.systemDefault());
        System.out.println("milliseconds    : "+zonedDateTime.toInstant().toEpochMilli());


        Instant instant = Instant.ofEpochMilli(timeMilliSeconds);
        localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("from ms         : "+localDateTime);

        localDateTime = LocalDateTime.ofInstant(instant,ZoneId.systemDefault());
        System.out.println("from ms         : "+localDateTime);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMilliSeconds);
        System.out.println("calendar from ms: "+c.getTime());

    }
}
