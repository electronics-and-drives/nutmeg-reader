function plots = readNutascii (file)
% readNutbin  Reads the contents of a Nutmeg file in ASCII syntax.
%
%   plots = readNutbin(file) reads the contents of 'file'.
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