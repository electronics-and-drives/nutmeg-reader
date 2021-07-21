import os
import errno
import warnings

import jpype
import jpype.imports
from jpype.types import *

import numpy as np
import pandas as pd

__version__ = '0.0.1'

def __get_maven_home():
    mvn_command  = 'mvn help:evaluate -Dexpression=settings.localRepository -B'
    maven_output = os.popen(mvn_command).read()
    maven_home   = list(filter( lambda l: (not l.startswith('[')) and l
                              , maven_output.split(sep='\n')))[0]
    return maven_home

def __default_class_path():
    maven_home = __get_maven_home()
    class_path = f'{maven_home}/edlab/eda/reader/nutmeg/{__version__}/nutmeg-{__version__}-jar-with-dependencies.jar'
    return class_path

DEFAULT_CLASS_PATH = __default_class_path()

class NutmegPlot:
    '''
    Every plot has properties:
        name = plot1.name                   references the name of the plot
        numOfWaves = plot1.num_of_waves     references the number of waves
        numOfPoints = plot1.num_of_points    references the number of points

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
        self.plot          = plot
        self.is_complex    = plot.isComplex()
        self.name          = plot.getPlotname()
        self.num_of_waves  = plot.getNoOfWaves()
        self.num_of_points = plot.getNoOfPoints()

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
    _nut_reader = None
    _class_path = None

    def __init__(self, class_path=DEFAULT_CLASS_PATH):
        '''
        Constructor of the NutmegReader class. Takes an optional class path and
        starts the jvm.
        Where `class_path` should point to the nutmeg-reader `.jar`. If the
        nutmeg-reader was installed following the instructions this should be
        unecessary.
        
        **NOTE:** If a jpype has already a JVM running, the class_path arg will
        be ignored until all objects are `del`ed and the JVM is killed.
        '''
        ## Check if given nutmeg jar exists
        if not os.path.isfile(class_path):
            raise FileNotFoundError( errno.ENOENT
                                   , os.strerror(errno.ENOENT)
                                   , class_path )

        ## Set class path or warn about running JVM
        if NutmegReader._class_path is None:
            NutmegReader._class_path = class_path
        elif jpype.isJVMStarted() and (NutmegReader._class_path != class_path):
            w1 = f'JVM is already running with a different class path {NutmegReader._class_path}.\n'
            w2 = f'Close the current JVM to start with a different class path.\n'
            warnings.warn( f'\n{w1}\n{w2}\n'
                         , RuntimeWarning ) 

        ## Start JVM if not running and set static variable
        if not jpype.isJVMStarted():
            jpype.startJVM(classpath=[NutmegReader._class_path])
            from edlab.eda.reader.nutmeg import NutReader
            NutmegReader._nut_reader = NutReader

    def __del__(self):
        '''
        Destructor will shutdown the JVM and set `_nut_reader = None`.
        '''
        NutmegReader._nut_reader = None
        if jpype.isJVMStarted():
            jpype.shutdownJVM()

    def __read_nutmeg(self, file_name, nut_type):
        '''
        Private reader function for handling ascii and bin types.
        See documentation for `readNutascii` and/or `readNutbin`.
        '''
        if os.path.isfile(file_name):   # Check if file exists
            if nut_type == 'ascii':
                reader = NutmegReader._nut_reader.getNutasciiReader(file_name)
            else:                       # Raise error of file doesn't exist
                reader = NutmegReader._nut_reader.getNutbinReader(file_name)

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
