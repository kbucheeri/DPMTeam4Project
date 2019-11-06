import matplotlib.pyplot as plt
import numpy as np
import csv
import sys

file='line.csv'
fname = open(file,'rt')
plt.plotfile(fname, ('yval',  'sdiff', 'lineardiff'), subplots=True)
plt.show()