import csv
from pathlib import Path
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

root = Path(__file__).resolve().parent
with (root / 'results/results.csv').open(newline='') as file:
    rows = list(csv.DictReader(file))
assert len(rows) == 36
out = root / 'results/plots'
out.mkdir(parents=True, exist_ok=True)
names = {'W1': 'Random access: 10,000 gets', 'W2': 'Search: 1,000 queries', 'W3': '1,000 inserts + 1,000 removals', 'W4': 'Heap: n inserts + n extractions'}
colors = {'DynamicArray': '#2563eb', 'MyLinkedList': '#d97706', 'MinHeap': '#15803d'}
for workload, title in names.items():
    selected = [row for row in rows if row['workload'] == workload]
    groups = sorted({(row['structure'], row['variant']) for row in selected})
    fig, axes = plt.subplots(2, 2, figsize=(12, 8), constrained_layout=True)
    for ax, metric, label in zip(axes.flat, ['time_ms', 'steps', 'moves', 'comparisons'], ['Median time (ms)', 'Steps (count)', 'Moves (count)', 'Comparisons (count)']):
        for structure, variant in groups:
            data = sorted([row for row in selected if row['structure'] == structure and row['variant'] == variant], key=lambda row: int(row['n']))
            legend = structure if variant == '-' else f'{structure}: {variant}'
            ax.plot([int(row['n']) for row in data], [float(row[metric]) for row in data], marker='o', linestyle='--' if variant == 'middle' else '-', color=colors[structure], label=legend)
        ax.set_xscale('log')
        ax.set_yscale('log' if metric == 'time_ms' else 'symlog', **({} if metric == 'time_ms' else {'linthresh': 1}))
        ax.set_xticks([100, 1000, 10000, 100000], ['100', '1,000', '10,000', '100,000'])
        if metric != 'time_ms':
            maximum = max(float(row[metric]) for row in selected)
            ax.set_ylim(0, max(1, maximum * 2))
        ax.set_xlabel('Initial number of elements (n)')
        ax.set_ylabel(label)
        ax.grid(True, alpha=0.25)
        ax.legend(fontsize=8)
    fig.suptitle(f'{workload} — {title}\nTime: logarithmic axis; counts: symmetric log axis (includes zero)', fontsize=13)
    fig.savefig(out / f'{workload}.png', dpi=170)
    plt.close(fig)
print('Generated W1–W4; 36 CSV rows verified')
