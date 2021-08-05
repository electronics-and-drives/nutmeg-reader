import os
import re
import errno
import magic
import shutil
import random
import string
import warnings
import importlib

from typing import Union
from pkg_resources import get_distribution

import h5py as h5
import pandas as pd

import jpype
import jpype.imports
from jpype.types import *

## Package Meta information ###################################################

__name__    = 'nutmeg_reader'
__version__ = get_distribution(__name__).version

## Module internal functions ##################################################

def __get_maven_home() -> Union[str, None]:
    '''
    Get the default maven home directory.
    '''
    maven_home = None

    mvn_command  = 'mvn help:evaluate ' \
                 + '-Dexpression=settings.localRepository ' \
                 + '-B 2> /dev/null'

    if shutil.which('mvn') is not None:
        maven_output = os.popen(mvn_command).read()
        maven_home   = list(filter( lambda l: (not l.startswith('[')) and l
                                  , maven_output.split(sep='\n')))[0]
    else:
        raise ChildProcessError( errno.ECHILD
                               , os.strerror(errno.ECHILD)
                               , 'sh: command not found: mvn' )

    return maven_home

def __default_class_path() -> Union[str, None]:
    '''
    Get the expected class path for the `nutmeg-reader` jar, including all
    dependencies.
    '''
    maven_home = __get_maven_home()
    path = f'{maven_home}/edlab/eda/reader.nutmeg/{__version__}/'
    clss = f'reader.nutmeg-{__version__}-jar-with-dependencies.jar'
    class_path = path + clss

    if not (os.path.isfile(class_path) and os.access(class_path, os.R_OK)):
        raise FileNotFoundError( errno.ENOENT
                               , os.strerror(errno.ENOENT)
                               , class_path )
        class_path = None

    return class_path

## Starting and Handling the JVM ##############################################

if not jpype.isJVMStarted():
    jpype.startJVM()

if importlib.util.find_spec('edlab') is None:
    jpype.addClassPath(__default_class_path())

## Import Java Classes ########################################################

global NutReader, NutmegRealPlot, NutmegComplexPlot
from edlab.eda.reader.nutmeg import NutReader, NutmegRealPlot, NutmegComplexPlot

## Module private functions ###################################################

def _analysis_type(plot_name: str) -> str:
    '''
    Extract the analys type from a given plot name.
    '''
    analysis_pattern = "`(.*?)'"
    analysis_match   = re.search(analysis_pattern, plot_name)
    analysis_type    = analysis_match.group(1) if analysis_match is not None \
                  else 'dummy_' + ''.join(random.sample( string.ascii_letters, 5))

    return analysis_type

def _file_names( file_name: str , plots: list[str]
               , extension: str ) -> list[str]:
    '''
    Construct individual file names for plots contained in a single Nutmeg file.
    '''
    fb = f'{os.path.splitext(os.path.abspath(file_name))[0]}'
    return [ f'{fb}_{p}.{extension}' for p in plots ]

def _data_frame(plot: Union[NutmegRealPlot, NutmegComplexPlot]) -> pd.DataFrame:
    '''
    Turn a single NutmegRealPlot or NutmegComplexPlot object into a 
    pandas Data Frame.
    '''
    c = lambda p: complex(p.getReal(), p.getImaginary())
    d = { w: plot.getWave(w) if not plot.isComplex() \
                             else [c(p) for p in plot.getWave(w)]
          for w in list(plot.getWaves()) }
    return pd.DataFrame(d)

## Module functions ###########################################################

def read_nutmeg(file_name: str) -> dict[str, pd.DataFrame]:
    '''
    read_nutmeg: 
        Reads the contents of a Nutmeg file in binary or ascii format and
        returns a dictionary with all the plots contained within as pandas
        DataFrames.
        Arguments:
            file_name: Path to nutmeg file.
        Returns:
            Dictionary with plot names as keys and pandas DataFrames as values
            for each plot.
    '''
    if not (os.path.isfile(file_name) and os.access(file_name, os.R_OK)):
        raise FileNotFoundError( errno.ENOENT
                               , os.strerror(errno.ENOENT)
                               , file_name )

    nutmeg_format = magic.from_file(file_name)

    if nutmeg_format == 'data':
        reader = NutReader.getNutbinReader(file_name) 
    elif nutmeg_format == 'ASCII text':
        reader = NutReader.getNutasciiReader(file_name)
    else:
        raise IOError( errno.EBFONT
                     , os.strerror(errno.EBFONT)
                     , file_name )
             
    reader.read().parse()

    return { _analysis_type(str(plot.getPlotname())): _data_frame(plot)
             for plot in reader.getPlots() }

def nut2hdf( file_name: str, single: bool = False
           , override: bool = False ) -> Union[str, list[str]]:
    '''
    nut2hdf: 
        Takes path to nutmeg file, reads it and writes the contents to either a
        single HDF5 with groups per plot, or individual HDF5s per plot.
        Arguments:
            file_name: Path to nutmeg format raw file.
            [override]: Override if file already exists. (default = False).
            [verbose]: Optional verbose output. (default = False )
        Returns:
            Path(s) to HDF5 file(s).
    '''
    nutmeg_data = read_nutmeg(file_name)

    if single:
        hdf_files = [f'{os.path.splitext(os.path.abspath(file_name))[0]}.hdf']
    else:
        hdf_files = _file_names(file_name, list(nutmeg_data.keys()), 'hdf') 

    if any([os.path.isfile(f) for f in hdf_files]) and not override:
        raise IOError(errno.EEXIST, os.strerror(errno.EEXIST), hdf_files)
    
    if single:
        with h5.File(hdf_files[0], 'w') as hdf_file:
            for plot, data in nutmeg_data.items():
                group = hdf_file.create_group(plot)
                for col in data:
                    group[col] = data[col].to_numpy()
    else:
        for file, data in zip(hdf_files, nutmeg_data.values()):
            with h5.File(file, 'w') as hdf_file:
                for col in data:
                    hdf_file[col] = data[col].to_numpy()

    return hdf_files[0] if single else hdf_files

def nut2csv(file_name: str, override: bool = False) -> str:
    '''
    nut2csv: 
        Takes path to nutmeg file, reads it and writes the contents to CSV
        files, for each plot.
        Arguments:
            file_name: Path to nutmeg format raw file.
            [verbose]: Optional verbose output.
        Returns:
            Paths to CSV files.
    '''
    nutmeg_data = read_nutmeg(file_name)
    csv_files = _file_names(file_name, list(nutmeg_data.keys()), 'csv')
    
    if any([os.path.isfile(f) for f in csv_files]) and not override:
        raise IOError(errno.EEXIST, os.strerror(errno.EEXIST), csv_files)

    for file, data in zip(csv_files, nutmeg_data.values()):
        data.to_csv(file)
    
    return csv_files
