package kz.astanait.daa;

public class MinHeap {
    private int[] elements = new int[10];
    private int size;
    private final OperationMetrics metrics;

    public MinHeap(OperationMetrics metrics) {
        this.metrics = metrics;
    }

    public int size() {
        return size;
    }

    public void insert(int value) {
        ensureCapacity();
        elements[size] = value;
        int current = size;
        size++;

        while (current > 0) {
            int parent = (current - 1) / 2;
            int currentValue = read(current);
            int parentValue = read(parent);
            metrics.countComparison();

            if (parentValue <= currentValue) {
                break;
            }

            swap(current, parent);
            current = parent;
        }
    }

    public int peekMin() {
        checkNotEmpty();
        return read(0);
    }

    public int extractMin() {
        checkNotEmpty();
        int minimum = read(0);
        size--;

        if (size == 0) {
            return minimum;
        }

        elements[0] = read(size);
        metrics.countMove();
        int current = 0;

        while (current < size / 2) {
            int left = 2 * current + 1;
            int right = left + 1;
            int smallerChild = left;

            if (right < size) {
                int leftValue = read(left);
                int rightValue = read(right);
                metrics.countComparison();

                if (rightValue < leftValue) {
                    smallerChild = right;
                }
            }

            int currentValue = read(current);
            int childValue = read(smallerChild);
            metrics.countComparison();

            if (currentValue <= childValue) {
                break;
            }

            swap(current, smallerChild);
            current = smallerChild;
        }

        return minimum;
    }

    public boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;

            if (elements[parent] > elements[child]) {
                return false;
            }
        }

        return true;
    }

    private int read(int index) {
        metrics.countStep();
        return elements[index];
    }

    private void swap(int first, int second) {
        int firstValue = read(first);
        int secondValue = read(second);
        elements[first] = secondValue;
        metrics.countMove();
        elements[second] = firstValue;
        metrics.countMove();
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        int[] largerArray = new int[elements.length * 2];

        for (int i = 0; i < size; i++) {
            largerArray[i] = read(i);
            metrics.countMove();
        }

        elements = largerArray;
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
    }
}