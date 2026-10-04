package kz.astanait.daa;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DataStructuresTest {

    @Test
    void resettingMetricsDoesNotChangeHeap() {
        OperationMetrics metrics = new OperationMetrics();
        MinHeap heap = new MinHeap(metrics);

        heap.insert(2);
        heap.insert(1);

        assertTrue(metrics.getSteps() > 0);
        assertTrue(metrics.getMoves() > 0);
        assertTrue(metrics.getComparisons() > 0);

        metrics.reset();

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());
        assertEquals(2, heap.size());
        assertEquals(1, heap.peekMin());
    }

    @Test
    void heapHandlesOnlyLeftChildAfterExtraction() {
        MinHeap heap = new MinHeap(new OperationMetrics());

        heap.insert(1);
        heap.insert(2);
        heap.insert(3);

        assertEquals(1, heap.extractMin());
        assertTrue(heap.isValidHeap());
        assertEquals(2, heap.peekMin());

        assertEquals(2, heap.extractMin());
        assertTrue(heap.isValidHeap());

        assertEquals(3, heap.extractMin());
        assertEquals(0, heap.size());
    }

    @Test
    void listCanBeReusedAfterRemovingAllTailElements() {
        MyLinkedList list = new MyLinkedList(new OperationMetrics());

        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(30, list.remove(2));
        assertEquals(20, list.remove(1));
        assertEquals(10, list.remove(0));
        assertEquals(0, list.size());

        list.add(40);
        list.add(50);

        assertEquals(2, list.size());
        assertEquals(40, list.get(0));
        assertEquals(50, list.get(1));
    }
    @Test
    void arrayPreservesValuesAfterGrowth() {
        DynamicArray array = new DynamicArray(new OperationMetrics());

        for (int i = 0; i < 100; i++) {
            array.add(i);
        }

        array.add(0, -1);

        assertEquals(101, array.size());
        assertEquals(-1, array.get(0));

        for (int i = 0; i < 100; i++) {
            assertEquals(i, array.get(i + 1));
        }
    }
    @Test
    void emptyStructuresAndInvalidIndices() {
        DynamicArray array = new DynamicArray(new OperationMetrics());
        MyLinkedList list = new MyLinkedList(new OperationMetrics());
        MinHeap heap = new MinHeap(new OperationMetrics());

        assertEquals(0, array.size());
        assertEquals(0, list.size());
        assertEquals(0, heap.size());
        assertFalse(array.contains(5));
        assertFalse(list.contains(5));

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 5));
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());

        array.add(5);
        list.add(5);

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 5));
    }

    @Test
    void singleElementAndReuse() {
        DynamicArray array = new DynamicArray(new OperationMetrics());
        MyLinkedList list = new MyLinkedList(new OperationMetrics());

        array.add(7);
        list.add(7);

        assertEquals(7, array.get(0));
        assertEquals(7, list.get(0));
        assertTrue(array.contains(7));
        assertTrue(list.contains(7));
        assertEquals(7, array.remove(0));
        assertEquals(7, list.remove(0));
        assertEquals(0, array.size());
        assertEquals(0, list.size());

        array.add(9);
        list.add(9);

        assertEquals(9, array.get(0));
        assertEquals(9, list.get(0));
    }

    @Test
    void firstLastAndDuplicates() {
        DynamicArray array = new DynamicArray(new OperationMetrics());
        MyLinkedList list = new MyLinkedList(new OperationMetrics());

        array.add(20);
        list.add(20);
        array.add(0, 10);
        list.add(0, 10);
        array.add(2, 20);
        list.add(2, 20);

        assertEquals(20, array.remove(2));
        assertEquals(20, list.remove(2));
        assertTrue(array.contains(20));
        assertTrue(list.contains(20));
        assertEquals(10, array.remove(0));
        assertEquals(10, list.remove(0));

        array.add(30);
        list.add(30);

        assertEquals(20, array.get(0));
        assertEquals(20, list.get(0));
        assertEquals(30, array.get(1));
        assertEquals(30, list.get(1));
    }

    @Test
    void randomOperationsMatchArrayList() {
        DynamicArray array = new DynamicArray(new OperationMetrics());
        MyLinkedList list = new MyLinkedList(new OperationMetrics());
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int operation = 0; operation < 2000; operation++) {
            int choice = random.nextInt(5);
            int value = random.nextInt(100) - 50;

            if (expected.isEmpty() || choice == 0) {
                expected.add(value);
                array.add(value);
                list.add(value);
            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);
                expected.add(index, value);
                array.add(index, value);
                list.add(index, value);
            } else if (choice == 2) {
                int index = random.nextInt(expected.size());
                int removed = expected.remove(index);
                assertEquals(removed, array.remove(index));
                assertEquals(removed, list.remove(index));
            } else if (choice == 3) {
                int index = random.nextInt(expected.size());
                int expectedValue = expected.get(index);
                assertEquals(expectedValue, array.get(index));
                assertEquals(expectedValue, list.get(index));
            } else {
                assertEquals(expected.contains(value), array.contains(value));
                assertEquals(expected.contains(value), list.contains(value));
            }

            assertEquals(expected.size(), array.size());
            assertEquals(expected.size(), list.size());

            for (int index = 0; index < expected.size(); index++) {
                int expectedValue = expected.get(index);
                assertEquals(expectedValue, array.get(index));
                assertEquals(expectedValue, list.get(index));
            }
        }
    }

    @Test
    void heapMatchesPriorityQueueAndSortedExtraction() {
        MinHeap heap = new MinHeap(new OperationMetrics());
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        heap.insert(7);
        assertTrue(heap.isValidHeap());
        assertEquals(7, heap.peekMin());
        assertEquals(7, heap.extractMin());
        assertTrue(heap.isValidHeap());

        for (int operation = 0; operation < 2000; operation++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(100) - 50;
                expected.add(value);
                heap.insert(value);
            } else {
                int minimum = expected.remove();
                assertEquals(minimum, heap.extractMin());
            }

            assertTrue(heap.isValidHeap());
            assertEquals(expected.size(), heap.size());

            if (!expected.isEmpty()) {
                int minimum = expected.peek();
                assertEquals(minimum, heap.peekMin());
            }
        }

        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt(100) - 50;
            expected.add(value);
            heap.insert(value);
            assertTrue(heap.isValidHeap());
        }

        int previous = Integer.MIN_VALUE;

        while (!expected.isEmpty()) {
            int minimum = expected.remove();
            int actual = heap.extractMin();
            assertEquals(minimum, actual);
            assertTrue(actual >= previous);
            assertTrue(heap.isValidHeap());
            previous = actual;
        }

        assertEquals(0, heap.size());
    }

    @Test
    void countersMeasureActualOperations() {
        OperationMetrics arrayMetrics = new OperationMetrics();
        DynamicArray array = new DynamicArray(arrayMetrics);
        OperationMetrics listMetrics = new OperationMetrics();
        MyLinkedList list = new MyLinkedList(listMetrics);

        for (int value = 0; value < 5; value++) {
            array.add(value);
            list.add(value);
        }

        arrayMetrics.reset();
        listMetrics.reset();
        array.get(4);
        list.get(4);

        assertEquals(1L, arrayMetrics.getSteps());
        assertEquals(4L, listMetrics.getSteps());

        arrayMetrics.reset();
        listMetrics.reset();
        array.add(0, 99);
        list.add(0, 99);

        assertEquals(5L, arrayMetrics.getMoves());
        assertEquals(2L, listMetrics.getMoves());

        arrayMetrics.reset();
        assertFalse(array.contains(-1));
        assertEquals(6L, arrayMetrics.getSteps());
        assertEquals(6L, arrayMetrics.getComparisons());

        arrayMetrics.reset();
        assertEquals(0L, arrayMetrics.getSteps());
        assertEquals(0L, arrayMetrics.getMoves());
        assertEquals(0L, arrayMetrics.getComparisons());
    }
}