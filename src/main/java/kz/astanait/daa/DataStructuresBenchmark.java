package kz.astanait.daa;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class DataStructuresBenchmark {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 5;
    private static volatile long checksum;

    private record Sample(
            double timeMs,
            long steps,
            long moves,
            long comparisons
    ) {
    }

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("results"));

        try (PrintWriter file = new PrintWriter("results/results.csv")) {
            file.println(
                    "workload,variant,structure,n,time_ms,steps,moves,comparisons"
            );

            for (int size : SIZES) {
                int[] input = createInput(size);

                for (int workload = 1; workload <= 3; workload++) {
                    String[] variants = workload == 3
                            ? new String[]{"head", "middle"}
                            : new String[]{"-"};

                    for (String variant : variants) {
                        runCase(workload, variant, "DynamicArray", input, file);
                        runCase(workload, variant, "MyLinkedList", input, file);
                    }
                }

                runCase(4, "-", "MinHeap", input, file);
            }
        }

        System.out.println("Finished: results/results.csv");
        System.out.println("Checksum: " + checksum);
    }

    private static int[] createInput(int size) {
        int[] input = new int[size];
        Random random = new Random(42);

        for (int i = 0; i < size; i++) {
            input[i] = random.nextInt(1_000_000);
        }

        return input;
    }

    private static void runCase(
            int workload,
            String variant,
            String structure,
            int[] input,
            PrintWriter file
    ) {
        for (int run = 0; run < WARMUP_RUNS; run++) {
            measure(workload, variant, structure, input);
        }

        Sample[] samples = new Sample[MEASURED_RUNS];

        for (int run = 0; run < MEASURED_RUNS; run++) {
            samples[run] = measure(workload, variant, structure, input);
        }

        for (int i = 1; i < samples.length; i++) {
            Sample current = samples[i];
            int j = i - 1;

            while (j >= 0 && samples[j].timeMs() > current.timeMs()) {
                samples[j + 1] = samples[j];
                j--;
            }

            samples[j + 1] = current;
        }

        Sample median = samples[MEASURED_RUNS / 2];

        file.printf(
                Locale.US,
                "W%d,%s,%s,%d,%.6f,%d,%d,%d%n",
                workload, variant, structure, input.length,
                median.timeMs(), median.steps(),
                median.moves(), median.comparisons()
        );
        file.flush();

        System.out.printf(
                Locale.US,
                "W%d %s %s n=%d: %.3f ms%n",
                workload, variant, structure,
                input.length, median.timeMs()
        );
    }

    private static Sample measure(
            int workload,
            String variant,
            String structure,
            int[] input
    ) {
        OperationMetrics metrics = new OperationMetrics();

        if (workload == 4) {
            return measureHeap(input, metrics);
        }

        boolean useArray = structure.equals("DynamicArray");
        DynamicArray array = useArray ? new DynamicArray(metrics) : null;
        MyLinkedList list = useArray ? null : new MyLinkedList(metrics);

        for (int value : input) {
            if (useArray) {
                array.add(value);
            } else {
                list.add(value);
            }
        }

        Random random = new Random(42);
        int queryCount = workload == 1 ? 10000 : 1000;
        int[] queries = new int[queryCount];

        if (workload == 1) {
            for (int i = 0; i < queries.length; i++) {
                queries[i] = random.nextInt(input.length);
            }
        } else if (workload == 2) {
            for (int i = 0; i < queries.length; i++) {
                queries[i] = i < 500
                        ? input[random.nextInt(input.length)]
                        : -i - 1;
            }
        }

        metrics.reset();
        long total = 0;
        long start = System.nanoTime();

        if (workload == 1) {
            for (int index : queries) {
                total += useArray ? array.get(index) : list.get(index);
            }
        } else if (workload == 2) {
            for (int value : queries) {
                boolean found = useArray
                        ? array.contains(value)
                        : list.contains(value);

                if (found) {
                    total++;
                }
            }
        } else {
            int index = variant.equals("head") ? 0 : input.length / 2;

            for (int i = 0; i < 1000; i++) {
                if (useArray) {
                    array.add(index, i);
                } else {
                    list.add(index, i);
                }
            }

            for (int i = 0; i < 1000; i++) {
                total += useArray ? array.remove(index) : list.remove(index);
            }
        }

        long elapsed = System.nanoTime() - start;
        checksum = total;

        if (workload == 2 && total != 500) {
            throw new IllegalStateException("Search result is incorrect");
        }

        if (workload == 3) {
            int finalSize = useArray ? array.size() : list.size();

            if (finalSize != input.length || total != 499500) {
                throw new IllegalStateException("Insert/remove result is incorrect");
            }
        }

        return new Sample(
                elapsed / 1_000_000.0,
                metrics.getSteps(),
                metrics.getMoves(),
                metrics.getComparisons()
        );
    }

    private static Sample measureHeap(
            int[] input,
            OperationMetrics metrics
    ) {
        MinHeap heap = new MinHeap(metrics);
        int[] extracted = new int[input.length];
        metrics.reset();
        long start = System.nanoTime();

        for (int value : input) {
            heap.insert(value);
        }

        for (int i = 0; i < extracted.length; i++) {
            extracted[i] = heap.extractMin();
        }

        long elapsed = System.nanoTime() - start;
        long total = 0;

        for (int i = 0; i < extracted.length; i++) {
            if (i > 0 && extracted[i] < extracted[i - 1]) {
                throw new IllegalStateException("Heap output is not sorted");
            }

            total += extracted[i];
        }

        checksum = total;

        return new Sample(
                elapsed / 1_000_000.0,
                metrics.getSteps(),
                metrics.getMoves(),
                metrics.getComparisons()
        );
    }
}
