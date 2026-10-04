# Assignment 2 - Data Structures

Student: Dauletkyzy Bayan | Group: SE-2523

Primitive int implementations of DynamicArray, MyLinkedList and MinHeap. The structures use no standard collection implementations. ArrayList and PriorityQueue are used only as test oracles.

## Requirements

JDK 17 and Maven. Run commands from the directory containing pom.xml.

## Build and run all tests

```sh
mvn clean test
```

Six JUnit 5 tests cover empty/single-element structures, invalid indices, duplicates, boundary operations, 2,000 randomized sequence operations against ArrayList, heap operations against PriorityQueue, heap order after each mutation, sorted extraction and operation counters.

## Run the benchmark

```sh
mvn compile exec:java
```

This overwrites results/results.csv. Four workloads use n = 100, 1000, 10000, 100000 and Random(42). Each case has two warm-up runs followed by five measured runs; the CSV stores the median-time run and that run's counters. Prefilling and query generation are excluded from W1-W3 timing. W4 includes both insertion and extraction; validation is after timing. W3 uses the fixed original index n/2 in its middle variant.

## Metrics

Steps count array-cell reads or advances along next links. Moves count shifted/copied/repositioned array elements or updates to head, tail and next links. New array element placement is not a shift. Comparisons count comparisons between stored values, including comparisons with a search query; index/size checks are excluded. Validation helper isValidHeap intentionally does not change counters.

## Results and plots

- results/results.csv: 36 measured cases from the student's run.
- results/plots/W1.png through W4.png: time and three counters for each workload.
- REPORT.pdf: five-page report with complexity, proofs, measurements and discussion.
- DEFENSE.md: short explanations and exercises for preparation.

Optional graph regeneration requires Python and matplotlib, only for plotting:

```sh
python plot_results.py
```

Regenerate plots after a new benchmark run so the report and graphs match the CSV. Timings vary with hardware, JVM warm-up and system load.

## Git and submission

The ZIP contains source and documents, without .git metadata. It does not prove the required Git workflow. Verify the real repository's main branch, feature/array, feature/list, feature/heap, feature/metrics, meaningful commits and tag v1.0 before submission. Do not fabricate a past development history.

Submit DAA_Assignment2_Bayan_Dauletkyzy_SE-2523.zip and the actual GitHub repository link in Moodle. The defense is in Week 5. Optional JOL and Floyd buildHeap bonuses are not implemented.
