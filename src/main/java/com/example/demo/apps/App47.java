package com.example.demo.apps;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import static java.lang.IO.println;

public class App47 {
    void main() throws Exception {
        // 1. Multiple Each Item in a List by 2
        IntStream.range(1, 10).map(i -> i * 2);

        // 2. Sum a List of Numbers
        IntStream.range(1, 1000).sum();

        // 3. Verify if Exists in a String
        List<String> wordList = Arrays.asList("java", "jdk", "spring", "maven");
        String tweet = "This is an example tweet talking about java and maven.";
        wordList.stream().anyMatch(tweet::contains);

        // 4. Read in a File
        String fileText = new String(Files.readAllBytes(Paths.get("data.txt")));
        List<String> fileLines = Files.readAllLines(Paths.get("data.txt"));

        // 5. Happy Birthday to You!
        IntStream.rangeClosed(1, 4).mapToObj(
                i -> MessageFormat.format("Happy Birthday {0}",
                        (i == 3) ? "dear NAME" : "to You")).forEach(IO::println);

        // 6. Filter list of numbers
        Map<Boolean, List<Integer>> passedFailedMap
                = Stream.of(49, 58, 76, 82, 88, 90)
                .collect(Collectors.partitioningBy(i -> i > 60));

        // 7. Fetch and Parse an XML web service
        Document parse = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new URL("http://www.omdbapi.com/?i=tt0121765&plot=short&r=xml").openStream());


        // 8. Find minimum (or maximum) in a List
        IntStream.of(14, 35, -7, 46, 98).min();
        Arrays.asList(14, 35, -7, 46, 98).stream().min(Integer::compare);
        Arrays.asList(14, 35, -7, 46, 98).stream().reduce(Integer::min);
        Collections.min(Arrays.asList(14, 35, -7, 46, 98));

        // 9. Parallel Processing
        List<String> dataList = null;
        dataList.parallelStream().map(line -> processItem(line));

        // 10. Sieve of Eratosthenes
        List<Integer> nums = new LinkedList<>(IntStream.rangeClosed(2, 1000).boxed().collect(Collectors.toList()));
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
        return null;
    }
}
