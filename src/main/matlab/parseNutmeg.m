function plots = parseNutmeg (obj)
  
  plots=[];

  if obj.equals(obj.read())

    if obj.equals(obj.parse())

      for i=0:obj.getPlots().size()-1

        plotObj = obj.getPlots().get(i);

        plot = struct();

        plot.waveNames = [];
        plot.waveData = [];
        plot.waveUnits = [];
        plot.name = string(plotObj.getPlotname());
        plot.numOfWaves = plotObj.getNoOfWaves();
        plot.numOfPoints = plotObj.getNoOfPoints();

        wavesArrayObj=plotObj.getWaves().toArray();

        for j = 1:numel(wavesArrayObj)

          name = string(wavesArrayObj(j));

          plot.waveNames = [plot.waveNames name];

          plot.waveUnits = [plot.waveUnits string(plotObj.getUnit(name))];


          if strcmp(class(plotObj),'edlab.eda.reader.nutmeg.NutmegRealPlot')
            plot.waveData = [plot.waveData plotObj.getWave(name)];
          end

          if strcmp(class(plotObj),'edlab.eda.reader.nutmeg.NutmegComplexPlot')

            complexWaveArray= plotObj.getWave(name);
            wave=[];

            for k=1:numel(complexWaveArray)

              wave(k) = complex(complexWaveArray(k).getReal(),...
                                complexWaveArray(k).getImaginary());
            end

            plot.waveData = [plot.waveData wave'];

          end
        end

        plots=[plots plot];

      end
    end
  end
end