package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int QUEUE_CAPACITY = 100;
    private static final int NUM_TEXTS = 10000;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    private static boolean stopAnalysis = false;

    public static void main(String[] args) {
        Thread generatorThread = new Thread(() -> {
            for (int i = 0; i < NUM_TEXTS; i++) {
                String text = generateText("abc", 100000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopAnalysis = true;
        });

        Thread analyzerThreadA = new Thread(() -> analyze('a', queueA));
        Thread analyzerThreadB = new Thread(() -> analyze('b', queueB));
        Thread analyzerThreadC = new Thread(() -> analyze('c', queueC));

        generatorThread.start();
        analyzerThreadA.start();
        analyzerThreadB.start();
        analyzerThreadC.start();

        try {
            generatorThread.join();
            analyzerThreadA.join();
            analyzerThreadB.join();
            analyzerThreadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Analysis completed.");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void analyze(char target, BlockingQueue<String> queue) {
        int maxCount = 0;
        String maxText = "";

        try {
            while (!stopAnalysis) {
                String text = queue.take();
                int count = 0;
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) == target) {
                        count++;
                    }
                }
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Analyzer for '" + target + "' stopped. Max count: " + maxCount);
    }
}



