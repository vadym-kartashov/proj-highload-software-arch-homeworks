package org.vkartashov.collections;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.IOException;
import java.nio.file.*;
import java.util.Random;

import static org.junit.Assert.*;

public class BalancedBinarySearchTreeTest {


    private BalancedBinarySearchTree<Integer> tree;

    @Before
    public void setUp() {
        tree = new BalancedBinarySearchTree<>();
    }

    @Test
    public void testInsertion() {
        // Insert some elements into the tree.
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);

        // Check that the tree contains the inserted elements.
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(3));
        assertTrue(tree.contains(7));

        // Insert more elements into the tree.
        tree.insert(1);
        tree.insert(4);
        tree.insert(6);
        tree.insert(8);

        // Check that the tree is still balanced.
        assertTrue(tree.isBalanced());
    }

    @Test
    public void testFinding() {
        // Insert some elements into the tree.
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);

        // Find an element in the tree.
        Integer foundElement = tree.find(5);

        // Check that the found element is correct.
        assertEquals(Integer.valueOf(5), foundElement);

        // Find an element that is not in the tree.
        Integer notFoundElement = tree.find(10);

        // Check that the not found element is null.
        assertNull(notFoundElement);
    }

    @Test
    public void testDeletion() {
        // Insert some elements into the tree.
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);

        // Delete an element from the tree.
        tree.delete(3);

        // Check that the tree no longer contains the deleted element.
        assertFalse(tree.contains(3));

        // Check that the tree is still balanced.
        assertTrue(tree.isBalanced());

        // Delete more elements from the tree.
        tree.delete(5);
        tree.delete(7);

        // Check that the tree is empty.
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testIsBalanced() {
        // Insert some elements into the tree.
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);

        // Check that the tree is balanced.
        assertTrue(tree.isBalanced());

        // Insert more elements into the tree to make it unbalanced.
        tree.insert(1);
        tree.insert(2);
        tree.insert(4);

        // Check that the tree is no longer balanced.
        assertFalse(tree.isBalanced());

        // Balance the tree.
        tree.balance();

        // Check that the tree is balanced again.
        assertTrue(tree.isBalanced());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i+=50) {
            prepareTest(i);
        }
    }

    private static void prepareTest(int numberOfElements) {
        // Generate four integer datasets - Sorted, Reverse sorted, Shuffled, Random for 100 records
        int[] sortedDataset = new int[numberOfElements];
        int[] reverseSortedDataset = new int[numberOfElements];
        int[] shuffledDataset = new int[numberOfElements];
        int[] randomDataset = new int[numberOfElements];

        for (int i = 0; i < numberOfElements; i++) {
            sortedDataset[i] = i + 1;
            reverseSortedDataset[i] = numberOfElements - i;
            shuffledDataset[i] = i + 1;
            randomDataset[i] = (int) (Math.random() * numberOfElements) + 1;
        }

        int randomElement = numberOfElements/4;

        // Shuffle the shuffled dataset
        Random random = new Random(1L);
        for (int i = 0; i < shuffledDataset.length; i++) {
            int j = random.nextInt(shuffledDataset.length);
            int temp = shuffledDataset[i];
            shuffledDataset[i] = shuffledDataset[j];
            shuffledDataset[j] = temp;
        }

        measureComplexity("Sorted", sortedDataset, randomElement);
        measureComplexity("ReverseSorted", reverseSortedDataset, randomElement);
        measureComplexity("Shuffled", shuffledDataset, randomElement);
        measureComplexity("Random", randomDataset, randomElement);
    }

    private static void measureComplexity(String key, int[] dataset, int randomElement) {
        BalancedBinarySearchTree<Integer> tree = new BalancedBinarySearchTree<>();
        for (int element : dataset) {
            tree.insert(element);
        }
        tree.balance();
        long startTime = System.nanoTime();
        Integer found = tree.find(randomElement);
        long endTime = System.nanoTime();
        long timeTaken = endTime - startTime;
        try {
            String fileName = "outputAverageBalanced.csv";
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, key + "," + dataset.length + "," + timeTaken + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}