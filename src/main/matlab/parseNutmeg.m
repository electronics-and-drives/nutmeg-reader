function plots = parseNutmeg (obj)
% parseNutmeg  Converts the plots from from the Java Nutmeg-Objects 
%              in MATLAB datastructures.
%  

  plots = [];

  %check if is running on Octave or Matlab
  isOctave = exist("OCTAVE_VERSION", "builtin") > 0;

  if obj.equals(obj.read())

    if obj.equals(obj.parse())

      %iterate over all plots in nutmeg file
      for i=0:obj.getPlots().size()-1

        plotObj = obj.getPlots().get(i);

        plot = {};
        plot.waveData = [];

        if isOctave
          plot.name = plotObj.getPlotname();
        else
          plot.name = string(plotObj.getPlotname());
        end
        
        plot.numOfWaves = plotObj.getNoOfWaves();
        plot.numOfPoints = plotObj.getNoOfPoints();

        wavesArrayObj=plotObj.getWaves().toArray();

        %iterate over all waves in a plot
        for j = 1:numel(wavesArrayObj)
       
          if isOctave
            name = wavesArrayObj(j);
          else
            name = string(wavesArrayObj(j));
          end

          plot.waveNames{j} = name;

          if isOctave
            plot.waveUnits{j} = plotObj.getUnit(name);
          else
            plot.waveUnits{j} = string(plotObj.getUnit(name));
          end

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

        plots = [plots plot];

      end
    else

      if isOctave
        error("Unable to parse nutmeg file");
      else
        fprintf(2,"Unable to parse nutmeg file")
      end
    end
  else
    if isOctave
      error("Unable to read nutmeg file");
    else
      fprintf(2,"Unable to read nutmeg file")
    end
  end
end