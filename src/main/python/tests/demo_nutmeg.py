import pandas as pd
from matplotlib import pyplot as plt
from nutmeg_reader import NutReader, read_nutmeg

## The slightly longer and painful way with all the information:

file    = '../../../test/resources/rc2/nutascii.raw'
reader  = NutReader.getNutasciiReader(file)

reader.read().parse();

plots       = reader.getPlots()
tran_plot   = plots[3]

tran_data   = pd.DataFrame({ w: list(tran_plot.getWave(w))
                             for w in list(tran_plot.getWaves()) })

fig, axs = plt.subplots(1,1, figsize=(8,8))
axs.plot(tran_data['time'], tran_data['O'])
axs.set_title(tran_plot.getPlotname())
axs.set_xlabel(f'time ({tran_plot.getUnit("time")})')
axs.set_ylabel(f'O ({tran_plot.getUnit("O")})')
plt.show()

## Or with the `read_nutmeg` convenience function, but less information:

bin_plots = read_nutmeg('../../../test/resources/rc2/nutbin.raw')
tran_data = bin_plots['tran']

fig, axs = plt.subplots(1,1, figsize=(8,8))
axs.plot(tran_data['time'], tran_data['O'])
axs.set_title('tran')
axs.set_xlabel(f'time (s)')
axs.set_ylabel(f'O (V)')
plt.show()
