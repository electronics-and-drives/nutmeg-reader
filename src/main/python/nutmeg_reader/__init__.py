import os
from pkg_resources import get_distribution

__name__    = 'nutmeg_reader'
__version__ = get_distribution(__name__).version

def __get_maven_home():
    mvn_command  = 'mvn help:evaluate -Dexpression=settings.localRepository -B 2> /dev/null'
    maven_output = os.popen(mvn_command).read()
    maven_home   = list(filter( lambda l: (not l.startswith('[')) and l
                              , maven_output.split(sep='\n')))[0]
    return maven_home

def __default_class_path():
    maven_home = __get_maven_home()
    class_path = f'{maven_home}/edlab/eda/reader.nutmeg/{__version__}/reader.nutmeg-{__version__}-jar-with-dependencies.jar'
    return class_path

if 'CLASSPATH' not in os.environ.keys():
    cp = __default_class_path()
    import jnius_config
    jnius_config.set_classpath(cp)

from .nutmeg_reader import NutReader, read_nutmeg
