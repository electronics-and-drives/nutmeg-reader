import os
import errno

import jpype
import jpype.imports
from jpype.types import *

import numpy as np

if 'MAVEN_HOME' in os.environ:
    MAVEN_HOME = os.environ['MAVEN_HOME']
else:
    MAVEN_HOME = f'{os.environ["HOME"]}/.m2/repository'

VERSION = '0.0.1'
CLASS_PATH = f'{MAVEN_HOME}/edlab/eda/reader/nutmeg/{VERSION}/nutmeg-{VERSION}-jar-with-dependencies.jar'

class NutmegReader:
    def __init__(self, class_path=CLASS_PATH):
        jpype.startJVM(classpath=[class_path])
        from edlab.eda.reader.nutmeg import NutReader

    def __readNutmeg(self, file_name, nut_type):
        '''
        Private reader function for handling ascii and bin types.
        See documentation for `readNutascii` and/or `readNutbin`.
        '''
        if os.path.isfile(file_name):
            
            if nut_type == 'ascii':
                nutReader = NutReader.getNutasciiReader(file_name)
            else:
                nutReader = NutReader.getNutbinReader(file_name)

            nutReader.read()
            nutReader.parse()
            plots = nutReader.getPlots()
        else:
            raise FileNotFoundError( errno.ENOENT
                                   , os.strerror(errno.ENOENT)
                                   , file_name)
        return plots

    def readNutascii(self, file_name: str):
        ''' 
        readNutascii : Reads the contents of a Nutmeg file in ascii syntax.

            `plots = readNutascii(file_name)` reads the contents of 'file_name'.
         
        The function returns an array with the individual plots in the file.
         
        plot1 = plots[0] accesses the first plot in the file
        plot2 = plots[1] accesses the second plot in the file
        ...

        Every plot has properties:
            name = plot1.name                   references the name of the plot
            numOfWaves = plot1.num_of_waves     references the number of waves
            numOfPoints = plot1.num_of_point    references the number of points

        The name of a waveform can be accessed with:
            name_1 = plot1.waveNames[0]     retrives the name of the first waveform
            name_2 = plot1.waveNames[1]     retrives the name of the second waveform

        The unit of a waveform can be accessed with:
            unit_1 = plot1.waveUnits[0]     retrives the unit of the first waveform 
            unit_2 = plot1.waveUnits[1]     retrives the unit of the second waveform

        The wave of a waveform can be accessed with:
            wave_1 = plot1.waveData[:,0]  retrives the wave of the first waveform
            wave_2 = plot1.waveData[:,1]  retrives the wave of the second waveform
         
        The entry plot1.waveData has the shape  
            (plot1.numOfPoints, plot1.numOfWaves)
        '''
        return self.__readNutmeg(file_name, 'ascii')

    def readNutbin(self, file_name: str):
        ''' 
        readNutbin : Reads the contents of a Nutmeg file in binary syntax.

            `plots = readNutbin(file_name)` reads the contents of 'file_name'.
         
        The function returns an array with the individual plots in the file.
         
        plot1 = plots[0] accesses the first plot in the file
        plot2 = plots[1] accesses the second plot in the file
        ...

        Every plot has properties:
            name = plot1.name                   references the name of the plot
            numOfWaves = plot1.num_of_waves     references the number of waves
            numOfPoints = plot1.num_of_point    references the number of points

        The name of a waveform can be accessed with:
            name_1 = plot1.waveNames[0]     retrives the name of the first waveform
            name_2 = plot1.waveNames[1]     retrives the name of the second waveform

        The unit of a waveform can be accessed with:
            unit_1 = plot1.waveUnits[0]     retrives the unit of the first waveform 
            unit_2 = plot1.waveUnits[1]     retrives the unit of the second waveform

        The wave of a waveform can be accessed with:
            wave_1 = plot1.waveData[:,0]  retrives the wave of the first waveform
            wave_2 = plot1.waveData[:,1]  retrives the wave of the second waveform
         
        The entry plot1.waveData has the shape  
            (plot1.numOfPoints, plot1.numOfWaves)
        '''
        return self.__readNutmeg(file_name, 'bin')
