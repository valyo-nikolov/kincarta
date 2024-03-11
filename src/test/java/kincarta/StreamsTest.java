package kincarta;

import org.example.Employee;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.*;

public class StreamsTest {
    private static Employee[] arrayOfEmps = {
            new Employee("Jeff Bezos", 100000.0),
            new Employee("Bill Gates", 200000.0),
            new Employee("Mark Zuckerberg", 300000.0)
    };

    @Test
    public void testForEach() {
        Stream.of(arrayOfEmps).forEach(e -> {
            System.out.println(e.getId());
            System.out.println(e.getName());
            System.out.println(e.getSalary());
            e.setSalary(round(e.getSalary()*1.1));
            System.out.println(e.getSalary());
            System.out.println();
        });
    }

    @Test
    public void testMap() {
        Integer[] empIds = {1,2,3};

        List<Integer> integers = Stream.of(empIds)
                .map(e -> multiplyExact(e, e))
                .collect(Collectors.toList());

        integers.stream().forEach(integer -> {
            System.out.println(integer);
        });

    }

    @Test
    public void testCollect() {
        Employee[] employees = arrayOfEmps;

        List<String> employeeNameList = Stream.of(employees)
                .filter(e ->
                    e.getSalary() < 250000.0
                )
                .map(e -> e.getName())
                .collect(Collectors.toList());

        employeeNameList.stream().forEach(e -> {
            System.out.println(e);
        });
    }

    @Test
    public void testFilter() {
        Employee[] employees = arrayOfEmps;

        List<String> employeeNameList = Stream.of(employees)
                .filter(e ->
                        e.getSalary() < 250000.0
                )
                .map(e -> e.getName())
                .collect(Collectors.toList());

        employeeNameList.stream().forEach(e -> {
            System.out.println(e);
        });
    }

    @Test
    public void testFindFirst() {
        Employee[] employees = arrayOfEmps;

        String employeeName = Stream.of(employees)
                .filter(e ->
                        e.getSalary() < 25000.0
                )
                .map(e -> e.getName()).findFirst().orElse("John Doe");

        System.out.println(employeeName);
    }

    @Test
    public void testToArray() {
        List<Employee> empList = Arrays.asList(arrayOfEmps);

        Employee[] employees = empList.stream().toArray(Employee[]::new);
        Assert.assertEquals(empList.toArray(), employees);
    }

    @Test
    public void testFlatMap() {
        List<List<String>> namesNested = Arrays.asList(
                Arrays.asList("Jeff", "Bezos"),
                Arrays.asList("Bill", "Gates"),
                Arrays.asList("Mark", "Zuckerberg"));
        List<String> namesFlatStream = namesNested.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Assert.assertEquals(namesFlatStream.size(), namesNested.size()*2);

        namesFlatStream.stream().forEach(name -> {
            System.out.println(name);
        });
    }

    @Test
    public void testPeak() {
        List<Employee> empList = Arrays.asList(arrayOfEmps);

        empList.stream()
                .peek(e -> System.out.println(e.getName()))
                .peek(System.out::println)
                .collect(Collectors.toList());

    }

    @Test
    public void testSorted() {
        List<Employee> empList = Arrays.asList(arrayOfEmps);

        empList.forEach(e -> {
            System.out.println(e.getName());
        });

        List<Employee> employeesSorted = empList.stream()
                .sorted((e1, e2) -> e1.getName().compareTo(e2.getName()))
                .collect(Collectors.toList());

        employeesSorted.stream().forEach(e -> {
            System.out.println(e.getName());
        });
    }

    @Test
    public void testMin() {
        List<Employee> empList = Arrays.asList(arrayOfEmps);

        Employee firstEmp = empList.stream()
                .min((e1, e2) -> e1.getId() - e2.getId())
                .orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(firstEmp.getId(), 1);
    }

    @Test
    public void testMax() {
        List<Employee> empList = Arrays.asList(arrayOfEmps);

        Employee maxSalEmp = empList.stream()
                .max(Comparator.comparing(Employee::getSalary))
                .orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(maxSalEmp.getSalary(), 300000);
    }

    @Test
    public void testDistinct() {
        List<Integer> intList = Arrays.asList(2, 5, 3, 2, 4, 3);
        List<Integer> distinctIntList = intList.stream().distinct().collect(Collectors.toList());

        Assert.assertEquals(distinctIntList, Arrays.asList(2, 5, 3, 4));
    }

    @Test
    public void testAllMatchAnyMatchNoneMatch() {
        List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);

        boolean allEven = intList.stream().allMatch(i -> i % 2 == 0);
        boolean oneEven = intList.stream().anyMatch(i -> i % 2 == 0);
        boolean noneMultipleOfThree = intList.stream().noneMatch(i -> i % 3 == 0);

        Assert.assertEquals(allEven, false);
        Assert.assertEquals(oneEven, true);
        Assert.assertEquals(noneMultipleOfThree, false);
    }

}
