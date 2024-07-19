package org.vkartashov;

import org.vkartashov.collections.BalancedBinarySearchTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PerformanceMeasurementUtil {

    public static void measureComplexity(String key, int datasetSize, String fileName, Runnable runnable) {
        long startTime = System.nanoTime();
        runnable.run();
        long endTime = System.nanoTime();
        long timeTaken = endTime - startTime;
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, key + "," + datasetSize + "," + timeTaken + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
