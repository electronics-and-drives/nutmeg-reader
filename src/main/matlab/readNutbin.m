function plots = readNutbin (file)
% readNutbin  Reads the contents of a Nutmeg file in binary syntax.
%
%   plots = readNutbin(file) reads the contents of 'file'.
%
  if exist(file)==2
  
    obj=javaMethod('getNutbinReader', ...
                   'edlab.eda.reader.nutmeg.NutReader',...
                   file);
   
    plots = parseNutmeg(obj);
    
  else

    printf("File=%s does not exist",file);
    plots=[];

  end
end