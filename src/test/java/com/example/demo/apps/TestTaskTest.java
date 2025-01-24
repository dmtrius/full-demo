package com.example.demo.apps;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TestTaskTest {

    @Order(1)
    @Test
    @Disabled
    void testTask() {
        TestTask task = new TestTask();
        int[] a = {1, 2, 3, -2, 5, -1};
        int expected = 2;
        int actual = task.pairsCalculation(a);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "2, ont grgr",
            "4, Pol bene knjnnj njnjn",
            "5, efe feefe Pol1 bene2 jjkjkj"
    })
    void testWordCount(int count, String sentence) {
        WordCount wordCount = new WordCount();
        int actual = wordCount.count(sentence);
        assertEquals(count, actual);
    }

    @TestFactory
    Iterator<DynamicTest> pozTest() {
        return Arrays.asList(
                dynamicTest("neg",
                        () -> assertFalse(predicate.check(-1))),
                dynamicTest("zero",
                        () -> assertFalse(predicate.check(0))),
                dynamicTest("pos",
                        () -> assertTrue(predicate.check(1)))
        ).iterator();
    }

    private final PozNumPredicate predicate = new PozNumPredicate();

}

class PozNumPredicate {
    public boolean check(int num) {
        return num > 0;
    }
}

class WordCount {
    public int count(String sentence) {
        return (int)Arrays.stream(sentence.split("\\s")).count();
    }
}