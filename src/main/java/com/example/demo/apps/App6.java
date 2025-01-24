package com.example.demo.apps;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App6 {
    public static void main(String... args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> bl = IntStream.range(1, 11).boxed().collect(Collectors.toList());
        int[] b = bl.stream().mapToInt(Integer::intValue).toArray();
        int key = 5;
        System.out.println(Arrays.toString(b));
        System.out.println("Index of " + key + " is: " + indexOf(b, key));

        Collections.shuffle(bl);
        b = bl.stream().mapToInt(Integer::intValue).toArray();
        System.out.println(bl);
        System.out.println("Index of " + key + " is: " + linearSearch(b, key));

        var d = LocalDate.of(2024, 1, 1)
                .with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(d);
        Thread.ofVirtual().start(() -> System.out.println("VT"));
        Person[] pa = getPersons(10, false);
        System.out.println(Arrays.toString(pa));
        Person toFind = new Person("Person 4", 21);
        System.out.println("POSITION: " + binarySearch(pa, toFind));
        toFind.setAge(33);
        System.out.println("POSITION: " + binarySearch(pa, toFind));
    }

    private static int linearSearch(int[] a, int key) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == key) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOf(int[] a, int key) {
        int lo = 0;
        int hi = a.length - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            if (a[mid] == key) {
                return mid;
            }

            if (a[mid] < key) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }

    public static int binarySearch(Person[] array, Person target) {
        int left = 0;
        int right = array.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int comparison = array[mid].compareTo(target);

            if (comparison == 0) {
                return mid; // Target found
            } else if (comparison < 0) {
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }

        return -1; // Target not found
    }

    private  static Person[] getPersons(int n, boolean shuffle) {
        Person[] persons = new Person[n];

        for (int i = 0; i < persons.length; i++) {
            persons[i] = new Person("Person " + (i + 1));
            persons[i].setAge(18 + i);
        }
        List<Person> l = Arrays.asList(persons);
        if (shuffle) {
            Collections.shuffle(l);
        }
        return l.toArray(l.toArray(Person[]::new));
    }
}

@Data
class Person implements Comparable<Person> {
    private String name;
    private int age;

    public Person() {}
    public Person(String name) {
        this.name = name;
    }
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person o) {
        return Integer.compare(this.age, o.age);
    }
}
