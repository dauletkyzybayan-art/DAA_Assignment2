package kz.astanait.daa;

public class MyLinkedList {
    private static class Node {
        private final int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final OperationMetrics metrics;

    public MyLinkedList(OperationMetrics metrics) {
        this.metrics = metrics;
    }

    public int size() {
        return size;
    }

    public void add(int value) {
        Node newNode = new Node(value);

        if (size == 0) {
            head = newNode;
            metrics.countMove();
        } else {
            tail.next = newNode;
            metrics.countMove();
        }

        tail = newNode;
        metrics.countMove();
        size++;
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        if (index == size) {
            add(value);
            return;
        }

        Node newNode = new Node(value);

        if (index == 0) {
            newNode.next = head;
            metrics.countMove();
            head = newNode;
            metrics.countMove();
        } else {
            Node previous = nodeAt(index - 1);
            newNode.next = previous.next;
            metrics.countMove();
            previous.next = newNode;
            metrics.countMove();
        }

        size++;
    }

    public int get(int index) {
        checkIndex(index);
        return nodeAt(index).value;
    }

    public int remove(int index) {
        checkIndex(index);
        Node removedNode;

        if (index == 0) {
            removedNode = head;
            head = head.next;
            metrics.countStep();
            metrics.countMove();

            if (size == 1) {
                tail = null;
                metrics.countMove();
            }
        } else {
            Node previous = nodeAt(index - 1);
            removedNode = previous.next;
            metrics.countStep();
            previous.next = removedNode.next;
            metrics.countMove();

            if (removedNode == tail) {
                tail = previous;
                metrics.countMove();
            }
        }

        size--;
        return removedNode.value;
    }

    public boolean contains(int value) {
        Node current = head;

        while (current != null) {
            metrics.countComparison();

            if (current.value == value) {
                return true;
            }

            current = current.next;
            metrics.countStep();
        }

        return false;
    }

    private Node nodeAt(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.countStep();
        }

        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }
}