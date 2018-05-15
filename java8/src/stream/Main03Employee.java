package stream;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03Employee {
    public static void main(String[] args) {

        Employee[] employees = {
                new Employee("Jason", "Red", 5000, "IT"),
                new Employee("Ashley", "Green", 7600, "IT"),
                new Employee("Matthew", "Indigo", 3587.5, "Sales"),
                new Employee("James", "Indigo", 4700.77, "Marketing"),
                new Employee("Luke", "Indigo", 6200, "IT"),
                new Employee("Jason", "Blue", 3200, "Sales"),
                new Employee("Wendy", "Brown", 4236.4, "Marketing")
        };
        List<Employee> list = Arrays.asList(employees);
//        List<Employee> list = Stream.of(employees).collect(Collectors.toList());

        System.out.println("Employees List:");
        list.forEach(System.out::println);

        System.out.println("\nEmployees sorted by salary:");
        Predicate<Employee> fTSP = e -> e.getSalary() >= 4000 && e.getSalary() <= 6000;
        list.stream()
                .filter(fTSP)
                .sorted((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary()))
                .forEach(System.out::println);
        System.out.println();
        list.stream()
                .filter(fTSP)
                .sorted(Comparator.comparingDouble(e -> e.getSalary()))
                .forEach(System.out::println);
        System.out.println();
        list.stream()
                .filter(fTSP)
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .forEach(System.out::println);
// findFirst()
        System.out.printf("%nEmployees 4000..6000 first: %n%s %n",
                list.stream()
                        .filter(fTSP)
                        .findFirst().orElse(null));

        System.out.printf("%nEmployees 4000..6000 any : %n%s %n",
                list.stream()
                        .filter(fTSP)
                        .findAny().orElse(null));

        System.out.printf("%nEmployees 4000..6000 match: %n%s %n",
                list.stream()
                        .filter(fTSP)
                        .anyMatch(e->e.getLastName().startsWith("I")));

        System.out.printf("%nEmployees 4000..6000 match: %n%s %n",
                list.stream()
                        .filter(fTSP)
                        .allMatch(e->e.getLastName().startsWith("I")));

// multiple sort
        Function<Employee, String> eFirstName = e -> e.getFirstName();
        Function<Employee, String> eLastName = e -> e.getLastName();

        Function<Employee, String> eFirstNameS = Employee::getFirstName;
        Function<Employee, String> eLastNameS = Employee::getLastName;

        Comparator<Employee> cLastThenFirst = Comparator.comparing(eLastName).thenComparing(eFirstName);
        System.out.printf("%nEmployees sorted last then first  asc: %n");
        list.stream().sorted(cLastThenFirst).forEach(System.out::println);

        System.out.printf("%nEmployees sorted last then first desc: %n");
        list.stream().sorted(cLastThenFirst.reversed()).forEach(System.out::println);
// distinct
        System.out.printf("%nEmployees distinct last names asc: %n");
        list.stream().map(Employee::getLastName).distinct().sorted().forEach(System.out::println);
// distinct map
        System.out.printf("%nEmployees distinct map asc: %n");
        Map<String, List<Employee>> fMap =
                list.stream().collect(Collectors.groupingBy(e -> e.getLastName()));

        list.stream().map(Employee::getLastName)
                .distinct()
                .sorted()
                .forEach(s -> {
                    System.out.println(fMap.get(s).stream().findFirst().get());
                });
//
// sorted by distinct
        System.out.printf("%nEmployees distinct last names asc: %n");
        list.stream().map(Employee::getLastName).distinct().sorted().forEach(System.out::println);

        System.out.printf("%nEmployees last first names asc: %n");
        list.stream().sorted(cLastThenFirst).forEach(System.out::println);
// group
        System.out.printf("%nEmployees by department: %n");
        Map<String, List<Employee>> dMap =
                list.stream().collect(Collectors.groupingBy(e -> e.getDepartment()));

        dMap.forEach((key, value) -> {
            System.out.println(key);
            value.forEach(e -> System.out.printf("     %s%n", e));
        });
// group sorted by key
        System.out.printf("%nEmployees sorted by department then salary: %n");
        Map<String, List<Employee>> dMapS =
                list.stream().collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        TreeMap::new,
                        Collectors.toList()));

        dMapS.forEach((key, value) -> {
            System.out.println(key);
            value.stream().sorted(Comparator.comparing(Employee::getSalary))
                    .forEach(e -> System.out.printf("     %s%n", e));
        });

// group and count  Collectors.groupBy(downstream) sorted
        System.out.printf("%nEmployees count by department: %n");
        Map<String, Long> cMap =
                list.stream().collect(
                        Collectors.groupingBy(
                                e -> e.getDepartment(),
                                () -> new TreeMap<String, Long>(),
                                Collectors.counting()));

        cMap.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        System.out.printf("%nEmployees count by department sorted: %n");
        Map<String, Long> eMap = list.stream().collect(
                Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.counting()));
        eMap.forEach((key, value) -> {
            System.out.printf("%-10s: %d%n", key, value);
        });

        System.out.printf("%nEmployees count by department unsorted: %n");
        Map<String, Long> gMap = list.stream().collect(
                Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        gMap.forEach((key, value) -> {
            System.out.printf("%-10s: %d%n", key, value);
        });

// group sum, average
        System.out.printf("%nEmployees sum salaries: %.1f",
                list.stream().mapToDouble(e -> e.getSalary()).sum());
        System.out.printf("%nEmployees sum salaries: %.1f",
                list.stream().mapToDouble(Employee::getSalary).reduce(0, (v1, v2) -> v1 + v2));

        System.out.printf("%nEmployees average salary: %.1f%n",
                list.stream().mapToDouble(Employee::getSalary).average().orElse(0));

        System.out.printf("%nEmployees average salary by dept%n");
        list.stream().collect(
                Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.toList()))
                .forEach((k, v) -> System.out.printf("%-10s: %.1f%n", k, v.stream().map(Employee::getSalary)
                        .mapToDouble(s->s).average().orElse(0)));


    }


}
