package com.example.demo.apps;

import java.util.HashMap;
import java.util.Map;

public class App10 {
    public static void main(String[] args) {
        char[] code = {'1', '2', 3};
        Key key = new Key(code, "first");

        Map<Key, String> map = new HashMap<>();
        map.put(key, "value");

        code[0] = 'x';
        System.out.println(map.get(key)); // and what would happen here?
        // null

//        key.setName("other");
        System.out.println(map.get(key)); // and what would happen here?
        // null
    }

    static final class Key {
        private final char[] code;
        private final String name;

        public Key(char[] code, String name) {
            this.code = code.clone();
            this.name = name;
        }

        public char[] getCode() {
            return code.clone();
        }

        public String getName() {
            return name;
        }
//        public void setCode(char[] code) { this.code = code; }
//        public void setName(String name) { this.name = name; }

        @Override
        public int hashCode() {
            return code.hashCode() + name.hashCode();
        }
    }
}

// We have a map of islands in the sea which is represented as 2d array, where land is marked as 1 and sea is marked as 0. Island can consist of several adjustent vertically and horizontally tiles. Write a class to count islands on the map.
//count = 0;
// 1, 0 horz
// (1,1), (1,2)
// (0,1 1,1) vert
// count++
//{
//        {0, 1, 0, 0},
//        {0, 1, 1, 0},
//        {0, 0, 0, 0},
//        {0, 0, 0, 1}
//        }
//int[][] map2 = {
//        {0, 1, 0, 0, 0},
//        {1, 1, 0, 0, 0},
//        {0, 0, 1, 0, 1},
//        {0, 0, 0, 1, 1}
//}