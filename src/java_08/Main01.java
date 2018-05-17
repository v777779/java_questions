package java_08;

import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static org.junit.Assert.assertEquals;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 16-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {

    private interface CustomInterface {
        default String next(int i) {
            return "default:" + String.valueOf(i + 1);
        }

        String apply(int value);
    }

    public static void main(String[] args) throws InterruptedException {
//       lambda	полноценная поддержка lambda выражений
//       default	метод в интерфейсах
//       ссылки	на методы	(s->s.toLowerCase()) >> (String::toLowerCase)
//       functional	функциональный интерфейс
//       stream	потоки для работы с коллекциями
//       Data API	для работы с датами
//       Nashorn	Javascript support на Java


// lambda
        CustomInterface c = (v) -> String.valueOf(v);
        System.out.println(c.apply(1));
// default
        System.out.println(c.next(1));
// ссылка на метод
        CustomInterface c2 = String::valueOf;
        System.out.println(c2.apply(2));
//functional
        Function<Integer, String> f = (v) -> String.valueOf(v + 1);
        System.out.println(f.apply(2));

//stream
        System.out.println("stream:");
        int[] values = {1, 5, 7, 9, 3, 4, 6, 2, 10, 8};
        Comparator<Integer> cL = Long::compare;
        Function<Integer, Integer> fi = Function.identity();

        List<String> list =
                Arrays.stream(values)
                        .filter(v -> v % 2 == 0)            // predicate
                        .sorted()                           // sort in ascending order
//                .boxed()
                        .mapToObj(v -> (long) v)               // box to Long
                        .sorted(Comparator.reverseOrder())
//                .sorted((v1,v2)->Integer.compare(v2,v1)) // reverseOrder
//                .sorted(Comparator.reverseOrder())
//                .sorted((Comparator.comparingInt(v->(Integer)v)).reversed())
//                .sorted(Comparator.comparingLong(v->(Long)v).reversed())
                        .map(String::valueOf)                                               // перевести в String
                        .map(v -> ((Integer.parseInt(v) > 9) ? "ab" + v : "abc" + v))       // value1, value2...
                        .collect(Collectors.toList());      // превратить в List<String>

        list.stream()
                .flatMap(s -> Arrays.stream(s.split("(?<=\\G..)")))  // расшить на строки по два символа
                .forEach(v -> System.out.println(v + " "));
// Data API
        String format = "%n%s%n-------------------%n";
        System.out.printf(format, "Java8 Date API:");
// java.util.Date, java.util.SimpleDateFormatter  NOT ThreadSafe
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        System.out.println(localDate + " " + localTime);



        System.out.println("nano         :"+localTime.isSupported(ChronoUnit.NANOS));
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("localDateTime: "+localDateTime);
// zones
//        https://stackoverflow.com/questions/43057690/java-stream-collect-every-n-elements
        AtomicInteger index = new AtomicInteger(0);
        ZoneId.getAvailableZoneIds().stream()
                .collect(Collectors.groupingBy(v -> index.getAndIncrement() / 3)) // map N collected generated
                .values().stream()
                .limit(2)
                .forEach(v -> System.out.printf("%s%n",                           // List<String>(3)
                        v.stream()
                                .map(m -> String.format("%-50s", m))              // String
                                .collect(Collectors.joining(">"))         // String +">"+String+">"+String
                ));

// LocalDateTime
        System.out.printf(format, "Date and Time:");
        Map<Integer, Temporal> map = new TreeMap<>();
        LocalDateTime timePoint = LocalDateTime.now();              // The current date and time

        map.put(1, timePoint);
        map.put(2, LocalDate.of(2012, Month.DECEMBER, 12));      // from values
        map.put(3, LocalDate.ofEpochDay(150));                                  // middle of 1970
        map.put(4, LocalTime.of(17, 18));                           // the train I took home today
        map.put(5, LocalTime.parse("10:15:30"));                                // From a String


        /* You can use direct manipulation methods,or pass a value and field pair */
        LocalDateTime thePast = timePoint.withDayOfMonth(10).withYear(2010);
        LocalDateTime yetAnother = thePast.plusWeeks(3).plus(3, ChronoUnit.WEEKS);

        map.put(6, thePast);        // 2010-M-10
        map.put(7, yetAnother);     // thePast+6 weeks

        map.values().forEach(System.out::println);

//adjusters
        System.out.printf(format, "Adjusters:");
        LocalDateTime foo = timePoint.with(lastDayOfMonth());
        LocalDateTime bar = timePoint.with(previousOrSame(DayOfWeek.WEDNESDAY));

        map.put(8, foo);
        map.put(9, bar);
        map.values().stream().skip(7).forEach(System.out::println);

        System.out.printf(format, "LocalTime.now() adjuster:");
// Using value classes as adjusters
        Thread.sleep(500);
        map.put(10, timePoint.with(LocalTime.now()));
        map.values().stream().skip(9).forEach(System.out::println);

        System.out.printf(format, "Truncation:");
        map.clear();
        LocalTime time = LocalTime.now();

        LocalTime truncatedTime = time.truncatedTo(ChronoUnit.SECONDS);
        map.put(1, time);
        map.put(2, truncatedTime);
        map.values().forEach(System.out::println);

//zones
        System.out.printf(format, "Time Zones:");
        map.clear();
        // You can specify the zone id when creating a zoned date time
        ZoneId id = ZoneId.of("Europe/Paris");
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);
        ZonedDateTime zoned = ZonedDateTime.of(dateTime, id);

        ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowZoned = ZonedDateTime.of(dateTime, moscowZoneId);

        map.put(1, zoned);
        map.put(2, moscowZoned);

        assertEquals(id, ZoneId.from(zoned));
        assertEquals(moscowZoneId, ZoneId.from(moscowZoned));

        ZonedDateTime parsed = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        map.put(3, parsed);
        map.values().forEach(System.out::println);

// zoneOffset
        System.out.printf(format, "Zone Offset:");
        map.clear();

        OffsetTime time2 = OffsetTime.now().truncatedTo(ChronoUnit.SECONDS);
        ZoneOffset offset = ZoneOffset.ofHoursMinutes(4, 30);

        // changes offset, while keeping the same point on the timeline
        OffsetTime sameTimeDifferentOffset = time2.withOffsetSameInstant(offset);
        // changes the offset, and updates the point on the timeline
        OffsetTime changeTimeWithNewOffset = time2.withOffsetSameLocal(offset);

        // Can also create new object with altered fields as before
        OffsetTime timeWithChangeOffset = changeTimeWithNewOffset.withHour(3).plusSeconds(2);

        System.out.println("time2          : " + time2);
        System.out.println("offset         : " + offset);
        System.out.println("sameInstant    : " + sameTimeDifferentOffset);
        System.out.println("sameLocal      : " + changeTimeWithNewOffset);
        System.out.println("timeWithChange : " + timeWithChangeOffset);

// period
        System.out.printf(format, "Period:");
        // 3 years, 2 months, 1 day
        Period period = Period.of(3, 2, 1);

        // You can modify the values of dates using periods
        LocalDate oldDate = LocalDate.now();
        LocalDate newDate = oldDate.plus(period);

        ZonedDateTime oldDateTime = ZonedDateTime.of(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), ZoneId.of("Europe/Kaliningrad"));
        ZonedDateTime newDateTime = oldDateTime.minus(period);
        // Components of a Period are represented by ChronoUnit values
        assertEquals(1, period.get(ChronoUnit.DAYS));

        System.out.println("Period     : " + period);
        System.out.println("oldDate    : " + oldDate);
        System.out.println("newDate    : " + newDate);
        System.out.println("oldDateTime: " + oldDateTime);
        System.out.println("newDateTime: " + oldDateTime);

// durations
        System.out.printf(format, "Duration:");
        // A duration of 3 seconds and 5 nanoseconds
        Duration duration = Duration.ofSeconds(3, 5);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minus(1, ChronoUnit.DAYS);
        Duration oneDay = Duration.between(yesterday, today);
        System.out.println("today      : " + today);
        System.out.println("yesterday  : " + yesterday);
        System.out.println("Duration ms: " + oneDay.toMillis());
        System.out.println("Duration s : " + oneDay.get(ChronoUnit.SECONDS));

        System.out.printf(format, "Chronologies:");

        ChronoLocalDate cDate = ChronoLocalDate.from(LocalDateTime.now());
        ChronoLocalDateTime cDateTime = LocalDateTime.now();
        ChronoZonedDateTime cZoned = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Paris"));

        System.out.println("cDate    : " + cDate);
        System.out.println("cDateTime: " + cDateTime);
        System.out.println("cZoned   : " + cZoned);


        System.out.printf(format, "MonthDay");
        MonthDay monthDay = MonthDay.from(LocalDateTime.now());
        YearMonth yearMonth = YearMonth.from(LocalDateTime.now());

        System.out.println("monthDay  :" + monthDay);
        System.out.println("month     :" + monthDay.getMonth());
        System.out.println("month     :" + monthDay.getMonth().get(ChronoField.MONTH_OF_YEAR));
        System.out.println("day       :" + monthDay.getDayOfMonth());
        System.out.println("yearMonth :" + yearMonth);
        System.out.println("year      :" + yearMonth.getYear());
        System.out.println("month     :" + yearMonth.getMonth());
        System.out.println("month     :" + yearMonth.getMonth().get(ChronoField.MONTH_OF_YEAR));



    }
}
