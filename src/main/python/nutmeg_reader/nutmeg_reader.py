import os
import errno

import jpype
import jpype.imports
from jpype.types import *

import numpy as np
import pandas as pd

__version__ = '0.0.1'

if 'MAVEN_HOME' in os.environ:
    MAVEN_HOME = os.environ['MAVEN_HOME']
else:
    MAVEN_HOME = f'{os.environ["HOME"]}/.m2/repository'

CLASS_PATH = f'{MAVEN_HOME}/edlab/eda/reader/nutmeg/{__version__}/nutmeg-{__version__}-jar-with-dependencies.jar'

class NutmegPlot:
    '''
    Every plot has properties:
        name = plot1.name                   references the name of the plot
        numOfWaves = plot1.num_of_waves     references the number of waves
        numOfPoints = plot1.num_of_point    references the number of points

    The name of a waveform can be accessed with:
        name_1 = plot1.wave_names[0]    retrives the name of the first waveform
        name_2 = plot1.wave_names[1]    retrives the name of the second waveform

    The unit of a waveform can be accessed with:
        unit_1 = plot1.wave_units[name_1]   retrives the unit of the first
                                            waveform 
        unit_2 = plot1.wave_units[name_2]   retrives the unit of the second 
                                            waveform

    The wave of a waveform can be accessed with:
        wave_1 = plot1.wave_data[name_1].values  retrives the wave of the first
                                                 waveform as numpy array
        wave_2 = plot1.wave_data[name_2].values  retrives the wave of the second 
                                                 waveform as numpy array
     
        The entry plot1.wave_data is a dictionary with a numpy array for each
        wave name.
    '''
    def __init__(self, plot):
        '''
        Constructor for the NutmegPlot class. Takes a NutmegReader Plot object
        and converts it to something useful in python.
        '''
        self.plot         = plot
        self.is_complex   = plot.isComplex()
        self.name         = plot.getPlotname()
        self.num_of_waves = plot.getNoOfWaves()
        self.num_of_point = plot.getNoOfPoints()

        self.wave_names = [ str(wav.toString())
                            for wav in plot.getWaves().toArray() ]
        self.wave_units = { wn: self.plot.getUnit(wn)
                            for wn in self.wave_names }

        self.wave_data  = { wn: np.array([ w.getReal() + 1j * w.getImaginary() 
                                          for w in wf ]) \
                                if self.is_complex else np.array(wf)
                            for wn, wf in [ (w, plot.getWave(w)) 
                                            for w in self.wave_names ] }

class NutmegReader:
    nut_reader = None

    def __init__(self, class_path=CLASS_PATH):
        '''
        Constructor of the NutmegReader class. Takes an optional class path and
        starts the jvm.
        '''
        if jpype.isJVMStarted():
            jpype.startJVM(classpath=[class_path])
            from edlab.eda.reader.nutmeg import NutReader
            nut_reader = NutReader

    def __del__(self):
        '''
        Destructor will shutdown the JVM and set `nut_reader = None`.
        '''
        nut_reader = None
        if jpype.isJVMStarted():
            jpype.shutdownJVM()

    def __read_nutmeg(self, file_name, nut_type):
        '''
        Private reader function for handling ascii and bin types.
        See documentation for `readNutascii` and/or `readNutbin`.
        '''
        if os.path.isfile(file_name):   # Check if file exists
            if nut_type == 'ascii':
                reader = nut_reader.getNutasciiReader(file_name)
            else:                       # Raise error of file doesn't exist
                reader = nut_reader.getNutbinReader(file_name)

            # Read and parse the file
            reader.read()
            reader.parse()

            # Extract the plots as array
            plots = [NutmegPlot(plot) for plot in reader.getPlots().toArray()]
        else:
            raise FileNotFoundError( errno.ENOENT
                                   , os.strerror(errno.ENOENT)
                                   , file_name)
        return plots

    def read_nutascii(self, file_name: str):
        ''' 
        read_nutascii : Reads the contents of a Nutmeg file in ascii syntax.

            `plots = read_nutascii(file_name)` reads the contents of 'file_name'.
         
        The function returns an array with the individual plots in the file as
        `NutmegPlot` objects.
         
        plot1 = plots[0] accesses the first plot in the file
        plot2 = plots[1] accesses the second plot in the file
        ...
        '''
        return self.__read_nutmeg(file_name, 'ascii')

    def read_nutbin(self, file_name: str):
        ''' 
        read_nutabin : Reads the contents of a Nutmeg file in binary syntax.

            `plots = read_nutbin(file_name)` reads the contents of 'file_name'.
         
        The function returns an array with the individual plots in the file.
         
        plot1 = plots[0] accesses the first plot in the file
        plot2 = plots[1] accesses the second plot in the file
        ...
        '''
        return self.__read_nutmeg(file_name, 'bin')
