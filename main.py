import csv
import sys
from typing import Iterable

from data import Data, Value

import matplotlib.pyplot as plt
import numpy as np

class Entries:
    def __init__(self) -> None:
        self.table = {}
    
    def add(self, key: str, data: Data) -> None:
        if key not in self.table:
            self.table[key] = []
        self.table[key].append(data) # assumes calls to add are sorted by time
    
    def get_value(self, key: str, time: float) -> Value:
        # could binary search if calls to add are sorted by time
        last = None
        for point in self.table[key]:
            if (last is None or time > last.time) and time < point.time:
                return point.value
            last = point
        return self.table[key][-1].value

    def get_values(self, key: str) -> list[Data]:
        return self.table[key]
    
    def get_timespan(self, key: str) -> tuple[float, float]:
        values = self.get_values(key)
        return values[0].time, values[-1].time

    def get_timeseries(self, key: str, t = None):
        if t is None:
            t = np.arange(0, data.get_timespan(key)[1], 0.02)
        v = np.zeros(t.size, dtype=t.dtype)

        for index, time in enumerate(t): 
            v[index] = float(data.get_value(key, time)) # converts bools into floats

        return t, v

def get_data(file: Iterable[str]) -> Entries:
    reader = csv.reader(file)
    next(reader) # skip first line as header, can also check if each line is equal

    data = Entries()
    for values in reader:
        time, key, value = values
        data.add(key, Data(float(time), value))

    return data

def plot_data(title: str, data: Entries, keys: list[str]):     
    fig, ax = plt.subplots()

    for key in keys:
        ax.plot(*data.get_timeseries(key))

    ax.set(
        xlabel='time (s)',
        ylabel=', '.join(keys),
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
    plot_data(sys.argv[1], data, [
        'NT:/SmartDashboard/Shooter/Target RPM',
        # 'NT:/limelight/tx',
        # 'NT:/limelight/ty'
    ])

    # for item in data.items():
    #     print(item)
