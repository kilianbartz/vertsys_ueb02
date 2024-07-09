package de.unitrier.s4kibart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CountOcc {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\s4kibart\\IdeaProjects\\vertsys_ueb02\\1000_6sek.log";
        int sendingCount = 0;
        int receivedCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sendingCount += boyerMooreCount(line, "Sending");
                receivedCount += boyerMooreCount(line, "Received");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        System.out.println("Number of 'Sending' occurrences: " + sendingCount);
        System.out.println("Number of 'Received' occurrences: " + receivedCount);
    }

    private static int boyerMooreCount(String text, String pattern) {
        int count = 0;
        int[] lastOccurrence = computeLastOccurrence(pattern);
        int i = 0;
        while (i <= text.length() - pattern.length()) {
            int j = pattern.length() - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }
            if (j < 0) {
                count++;
                i++;
            } else {
                i += Math.max(1, j - lastOccurrence[text.charAt(i + j)]);
            }
        }
        return count;
    }

    private static int[] computeLastOccurrence(String pattern) {
        int[] lastOccurrence = new int[256];
        for (int i = 0; i < 256; i++) {
            lastOccurrence[i] = -1;
        }
        for (int i = 0; i < pattern.length(); i++) {
            lastOccurrence[pattern.charAt(i)] = i;
        }
        return lastOccurrence;
    }
}