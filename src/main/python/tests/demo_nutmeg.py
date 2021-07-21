from nutmeg_reader import NutmegReader
import pandas as pd
from matplotlib import pyplot as plt

nutreader = NutmegReader()

bin_plots = nutreader.read_nutbin('../../../test/resources/rc2/nutbin.raw')

tran_plot = bin_plots[3]

tran_data = pd.DataFrame(tran_plot.wave_data)

fig, axs = plt.subplots(1,1, figsize=(8,8))
axs.plot(tran_data['time'], tran_data['O'])
axs.set_title(tran_plot.name)
axs.set_xlabel(f'time ({tran_plot.wave_units["time"]})')
axs.set_ylabel(f'O ({tran_plot.wave_units["O"]})')
plt.show()
