from nutmeg_reader import NutmegReader


nutreader = NutmegReader()

obj = nutreader.readNutbin('/home/ynk/workspace/nutmeg-reader/src/test/resources/rc2/nutbin.raw').toArray()

[ plt.getPlotname() for plt in obj ]

plot = obj[1]
        
{ wn: np.array([w.getReal() + 1j * w.getImaginary() for w in wf]) \
      if 'complex' in wf.toString() else \
      np.array(wf)
  for wn, wf in zip( plot.getWaves().toArray()
                   , [ plot.getWave(w) 
                       for w in plot.getWaves().toArray() ]) }

i = plots["I"]

wave = nutReader.getPlots().get(0).getWave('I')
