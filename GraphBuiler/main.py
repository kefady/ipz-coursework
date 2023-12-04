import re
import matplotlib.pyplot as plt


def build_graph(value_name='diskProcessingTime', down_sample_factor=1):
    file_paths = [
        '../src/logs/responses_fcfs.txt',
        '../src/logs/responses_sstf.txt',
        '../src/logs/responses_clook.txt'
    ]

    algorithm_names = ['FCFS', 'SSTF', 'CLOOK']

    plt.figure(figsize=(16, 5), dpi=100)

    for file_path, algorithm_name in zip(file_paths, algorithm_names):
        with open(file_path, 'r') as file:
            log_data = file.read()

        values = [int(match.group(1)) for match in re.finditer(rf'{value_name}=(\d+)', log_data)]

        values = values[::down_sample_factor]

        x_values = list(range(0, len(values) * down_sample_factor, down_sample_factor))
        y_values = values

        plt.plot(x_values, y_values, label=f'{algorithm_name}')

    plt.xlabel('Request Number')
    plt.ylabel(label)
    plt.title(f"Графіки значень '{label[0:-5]}' для різних алгоритмів")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()


def build_separate_graph(value_name='diskProcessingTime', down_sample_factor=1):
    file_paths = [
        '../src/logs/responses_fcfs.txt',
        '../src/logs/responses_sstf.txt',
        '../src/logs/responses_clook.txt'
    ]

    algorithm_names = ['FCFS', 'SSTF', 'CLOOK']
    colors = ['blue', 'orange', 'green']

    fig, axs = plt.subplots(3, 1, figsize=(16, 15), dpi=100)

    i = 0
    for ax, file_path, algorithm_name in zip(axs, file_paths, algorithm_names):
        with open(file_path, 'r') as file:
            log_data = file.read()

        values = [int(match.group(1)) for match in re.finditer(rf'{value_name}=(\d+)', log_data)]

        values = values[::down_sample_factor]

        x_values = list(range(0, len(values) * down_sample_factor, down_sample_factor))
        y_values = values

        ax.plot(x_values, y_values, label=f'{algorithm_name}', color=colors[i])
        ax.set_ylabel(label)

        ax.set_xlabel('Request Number')
        ax.legend()
        ax.grid(True)

        ax.set_title(f"Графік значень '{label[0:-5]}' для алгоритму {algorithm_name}")
        i += 1

    plt.tight_layout()
    plt.show()


label = 'Disk Processing Time (ms)'

build_separate_graph('diskProcessingTime', down_sample_factor=1000)
build_graph('diskProcessingTime', down_sample_factor=1000)
