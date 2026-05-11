package com.example.demo.apps;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import static java.lang.IO.println;

public class App47 {
    void main() throws Exception {
        // 1. Multiple Each Item in a List by 2
        var list = IntStream.range(1, 10).map(i -> i * 2);
        IO.println(list);

        // 2. Sum a List of Numbers
        var sum = IntStream.range(1, 1000).sum();
        IO.println(sum);

        // 3. Verify if Exists in a String
        List<String> wordList = Arrays.asList("java", "jdk", "spring", "maven");
        String tweet = "This is an example tweet talking about java and maven.";
        var words = wordList.stream().anyMatch(tweet::contains);
        IO.println(words);

        // 4. Read in a File
        var url = getClass().getClassLoader().getResource("data.txt");
        String fileText = new String(Files.readAllBytes(Paths.get(url.toURI())));
        List<String> fileLines = Files.readAllLines(Paths.get(url.toURI()));
        IO.println(fileText);
        IO.println(fileLines);

        // 5. Happy Birthday to You!
        IntStream.rangeClosed(1, 4).mapToObj(
                i -> MessageFormat.format("Happy Birthday {0}",
                        (i == 3) ? "dear NAME" : "to You")).forEach(IO::println);

        // 6. Filter list of numbers
        Map<Boolean, List<Integer>> passedFailedMap
                = Stream.of(49, 58, 76, 82, 88, 90)
                .collect(Collectors.partitioningBy(i -> i > 60));
        IO.println(passedFailedMap);

        // 7. Fetch and Parse an XML web service
        Document parse = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(
                        URI.create("https://doi.crossref.org/search/doi?pid=email@address.com&doi=10.1577/H02-043&format=unixsd")
                                .toURL().openStream());
        IO.println(parse.toString());

        // 8. Find minimum (or maximum) in a List
        var min = IntStream.of(14, 35, -7, 46, 98).min();
        IO.println(min.orElse(Integer.MIN_VALUE));
        var comp = Stream.of(14, 35, -7, 46, 98).min(Integer::compare);
        IO.println(comp.orElse(Integer.MIN_VALUE));
        var reduce = Stream.of(14, 35, -7, 46, 98).reduce(Integer::min);
        IO.println(reduce.orElse(Integer.MIN_VALUE));
        var min2 = Collections.min(Arrays.asList(14, 35, -7, 46, 98));
        IO.println(min2);

        // 9. Parallel Processing
        List<String> dataList = List.of("1", "2", "3");
        var result = dataList.parallelStream().map(App47::processItem);
        result.forEach(IO::println);

        // 10. Sieve of Eratosthenes
        List<Integer> nums = IntStream.rangeClosed(2, 100).boxed().collect(Collectors.toCollection(LinkedList::new));
        IntStream.rangeClosed(2, Double.valueOf(Math.sqrt(nums.getLast())).intValue()).forEach(n -> nums.removeIf(i -> i % n == 0 && n != i));
        nums.forEach(IO::println);

        // Bonus: FizzBuzz
        IntStream.rangeClosed(1, 100)
                .forEach(i -> println(
                                (i % 3 == 0)
                                        ? ((i % 5 == 0) ? "FizzBuzz" : "Fizz")
                                        : ((i % 5 == 0) ? "Buzz" : i)
                        )
                );
    }

    private static Object processItem(String line) {
        // Some heavy work task
        try {
            TimeUnit.MICROSECONDS.sleep(1);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
        IO.println(line);
        return null;
    }
}
