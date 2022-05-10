import csv
import sys
from typing import Iterable

from data import Data

import matplotlib.pyplot as plt
import numpy as np

# class Entries:
#     def __init__(self):
#         self.table = {}
Entries = dict[str, Data]

def get_value_at_time(time: float, points: list[Data]):
    last = None # TODO: bin search
    for point in points:
        if (last is None or time > last.time) and time < point.time:
            return point.value
        last = point

    return points[-1].value


def get_data(file: Iterable[str]) -> Entries:
    reader = csv.reader(file)
    next(reader) # skip first line as header, can also check if each line is equal

    data = {}
    for values in reader:
        time, key, value = values
        
        if key not in data:
            data[key] = []
        
        data[key].append(Data(float(time), value))

    return data

def plot_data(title: str, data: Entries, key: str): # TODO: be able to plot multiple keys
    fig, ax = plt.subplots()

    lasttime = data[key][-1].time
    t = np.arange(0, lasttime, 0.02)
    v = np.zeros((t.size), dtype=t.dtype)

    for index, time in enumerate(t): 
        v[index] = get_value_at_time(time, data[key])

    ax.plot(t, v)

    ax.set(
        xlabel='time (s)',
        ylabel=', '.join([key]),
        title=title
    )
    ax.grid()

    plt.show()

if __name__ == "__main__":
    if len(sys.argv) > 1: 
        infile = open(sys.argv[1])
    else:
        print(f"python {sys.argv[0]} <FRC list csv>")
        exit(1)
    
    data = get_data(infile)
    plot_data(sys.argv[1], data, 'NT:/SmartDashboard/Shooter/Target RPM')

    # for item in data.items():
    #     print(item)
