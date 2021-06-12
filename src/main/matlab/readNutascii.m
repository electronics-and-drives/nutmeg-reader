function plots = readNutascii (file)

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