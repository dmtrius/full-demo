package com.example.demo.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App19 {
    void main() {
        // List of participants
        List<String> participants = Arrays.asList("A", "B", "C", "D", "E", "F", "G");

        // Define couples (both directions)
        Map<String, String> couples = new HashMap<>();
        couples.put("A", "B");
        couples.put("B", "A");
        couples.put("C", "D");
        couples.put("D", "C");
        couples.put("F", "G");
        couples.put("G", "F");

        // Generate a valid assignment
        Map<String, String> assignments = generateAssignments(participants, couples);

        // Print the assignments
        for (Map.Entry<String, String> entry : assignments.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static Map<String, String> generateAssignments(List<String> participants, Map<String, String> couples) {
        Map<String, String> assignments = new HashMap<>();
        List<String> availableParticipants = new ArrayList<>(participants);

        // Shuffle the list to randomize assignments
        Collections.shuffle(availableParticipants);

        for (String participant : participants) {
            for (String assignee : availableParticipants) {
                // Check if the assignee is not the participant themselves and not their partner
                if (!assignee.equals(participant)
                        && !assignee.equals(couples.get(participant))) {
                    assignments.put(participant, assignee);
                    availableParticipants.remove(assignee);
                    break;
                }
            }
        }

        return assignments;
    }
}
