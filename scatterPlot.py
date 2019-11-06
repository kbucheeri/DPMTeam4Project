import csv

import numpy as np

import matplotlib.pyplot as plt
import pandas as pd
wine = pd.read_csv('localization.csv')


w = 4
h = 3
d = 70
plt.figure(figsize=(w, h), dpi=d)
iris_data = np.genfromtxt(
    "localization.csv", names=True,
    dtype="float", delimiter=",")

plt.plot(iris_data["angle"], iris_data["dist"], "o")
plt.show()
#plt.savefig("out.png")