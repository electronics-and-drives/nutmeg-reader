function plots = readNutbin (file)

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