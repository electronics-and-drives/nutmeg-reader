# nutmeg-reader

Python wrapper for the Java [nutmeg-reader](https://github.com/electronics-and-drives/nutmeg-reader).

## Installation

Since this package is built upon the java nutmeg-reader, it only works after
the main package is installed.

```
$ pip install . --use-feature=in-tree-build 
```

## Usage

Before importing, make sure the java packe is installed. If the jar with all
dependencies is not installed by maven, make sure the `CLASSPATH` environment
variable is set and points to the jar.

```bash
$ export CLASSPATH=/path/to/jar-with-dependencies.jar
```

Otherwise `nutmeg_reader` will look for it in the default maven install.

#### The Java way

Import the main class

```python
from nutmeg_reader import NutReader
```

from here on it can be used _exactly_ the same as the java class.

```python
# Create a new reader
reader = NutReader.getNutasciiReader('../../test/resources/rc2/nutascii.raw')

# Read and parse the nutascii
reader.read().parse();

# Get all plots from the reader
plots = reader.getPlots().toArray()

# Get nutmeg plot from list
plot = plots.get(0)

# Get the name of a plot
plot.getPlotname()

# Get number of points from plot
plot.getNoOfPoints()

# Get number of variables from plot
plot.getNoOfVariables()

# Get set of all waves from plot
waves = plot.getWaves().toArray()

# Check if wave with name 'I' is part of plot
plot.containsWave('I')

# Get unit of wave with name 'I'
plot.getUnit('I')

# Get the waveform (be careful about complex values!)
if plot.isComplex():
    wave = [complex(p.getReal(), p.getImaginary()) for p in plot.getWave('I')]
else:
    wave = plot.getWave('I')
```

#### The simple way

```python
# Get a dictionary of all plots
plots = read_nutmeg('../../../test/resources/rc2/nutbin.raw')

# Picking a plot returns a pandas DataFrame
plot = bin_plots['tran']

# Get number of points from plot
len(plot.index)

# Get number of variables from plot
len(plot.columns)

# Get set of all waves
plot.columns

# Check if wave with name 'I' is part of plot
'I' in plot.columns

# Get the waveform 
wave = plot['I'].values
```

## Tests

Run this in the root of the python packge:

```
$ pytest
```
