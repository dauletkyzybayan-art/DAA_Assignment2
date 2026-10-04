package kz.astanait.daa;

public class OperationMetrics {
    private long steps;
    private long moves;
    private long comparisons;

    public void countStep() {
        steps++;
    }

    public void countMove() {
        moves++;
    }

    public void countComparison() {
        comparisons++;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}