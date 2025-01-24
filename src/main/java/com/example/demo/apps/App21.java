package com.example.demo.apps;

public class App21 {
    void main() {
        String input = "HeLLoWoRLd";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char current = input.charAt(i);
//            if (Character.isUpperCase(current)) {
//                output.append(Character.toLowerCase(current));
//            } else if (Character.isLowerCase(current)) {
//                output.append(Character.toUpperCase(current));
//            } else {
//                output.append(current);
//            }
            if ((short)input.charAt(i) >= (short)'A' && (short)input.charAt(i) <= (short)'Z') {
                output.append(Character.toLowerCase(input.charAt(i)));
            } else {
                output.append(Character.toUpperCase(input.charAt(i)));
            }
//            if ((short)input.charAt(i) >= 65 && (short)input.charAt(i) <= 90) {
//                output.append(Character.toLowerCase(input.charAt(i)));
//            } else {
//                output.append(Character.toUpperCase(input.charAt(i)));
//            }
        }
        System.out.println(input);
        System.out.println(output);
    }
}
