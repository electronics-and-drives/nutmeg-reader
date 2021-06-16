function plots = readNutascii (file)
% readNutbin : Reads the contents of a Nutmeg file in binary syntax.
%
%  plots = readNutbin(file) reads the contents of 'file'.
%   
%  The function returns an array with the individual plots in the file.
%   
%  plot1 = plots(1) accesses the first plot in the file
%  plot2 = plots(2) accesses the second plot in the file
%
%  Every plot has several properties:
%   name = plot1.name                references the name of the plot
%   numOfWaves = plot1.numOfWaves    references the number of waves
%   numOfPoints = plot1.numOfPoints  references the number of points
%
%  The name of a waveform can be accessed with:
%   name_1 = plot1.waveNames{1}  retrives the name of the first waveform
%   name_2 = plot1.waveNames{2}  retrives the name of the second waveform
%
%  The unit of a waveform can be accessed with:
%   unit_1 = plot1.waveUnits{1}  retrives the unit of the first waveform 
%   unit_2 = plot1.waveUnits{2}  retrives the unit of the second waveform
%
%  The wave of a waveform can be accessed with:
%   wave_1 = plot1.waveData(:,1)  retrives the wave of the first waveform
%   wave_2 = plot1.waveData(:,2)  retrives the wave of the second waveform
%   
%  The entry plot1.waveData has the dimensions  
%    plot1.numOfPoints x plot1.numOfWaves
%
  if exist(file)==2
  
    obj=javaMethod('getNutasciiReader', ...
                   'edlab.eda.reader.nutmeg.NutReader',...
                   file);
                   
    plots = parseNutmeg(obj);
    
  else

    printf("File=%s does not exist",file);
    plots=[];

  end
end
