package kz.astanait.daa;

public class DynamicArray {
    private int[] elements = new int[10];
    private int size;
    private final OperationMetrics metrics;

    public DynamicArray(OperationMetrics metrics) {
        this.metrics = metrics;
    }

    public int size() {
        return size;
    }

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        ensureCapacity();

        for (int i = size; i > index; i--) {
            metrics.countStep();
            elements[i] = elements[i - 1];
            metrics.countMove();
        }

        elements[index] = value;
        size++;
    }

    public int get(int index) {
        checkIndex(index);
        metrics.countStep();
        return elements[index];
    }

    public int remove(int index) {
        checkIndex(index);
        metrics.countStep();
        int removedValue = elements[index];

        for (int i = index; i < size - 1; i++) {
            metrics.countStep();
            elements[i] = elements[i + 1];
            metrics.countMove();
        }

        size--;
        return removedValue;
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            metrics.countStep();
            metrics.countComparison();

            if (elements[i] == value) {
                return true;
            }
        }

        return false;
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        int[] largerArray = new int[elements.length * 2];

        for (int i = 0; i < size; i++) {
            metrics.countStep();
            largerArray[i] = elements[i];
            metrics.countMove();
        }

        elements = largerArray;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }
}