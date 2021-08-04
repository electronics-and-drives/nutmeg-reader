# nutmeg-reader

Python wrapper for the Java [nutmeg-reader](https://github.com/electronics-and-drives/nutmeg-reader).

## Installation

Since this package is built upon the java nutmeg-reader, it only works after
the main package is installed. Other dependencies are listed in
`requrements.txt`.

```
$ pip install . --use-feature=in-tree-build 
```

## Usage

### CLI

The nutmeg-reader python module comes with 2 CLI applications.

1. `nut2hdf`: Convert nutmeg to [HDF5](https://hdfgroup.org/)
2. `nut2csv`: Convert nutmeg to [CSV](https://en.wikipedia.org/wiki/Comma-separated_values)

Other file formats may be supported in the future.

For usage see the individual help

```bash
$ nut2hdf -h
$ nut2csv --help
```

#### Example

Converting a nutmeg file to a **single** HDF5 with the `-s` flag:

```bash
$ nut2hdf -s ../../test/resources/rc2/nutbin.raw

$ h5ls -r ../../test/resources/rc2/nutbin.hdf
/                        Group
/ac                      Group
/ac/I                    Dataset {51}
/ac/O                    Dataset {51}
/ac/R1:1                 Dataset {51}
/ac/V0:p                 Dataset {51}
/ac/freq                 Dataset {51}
/dc1                     Group
/dc1/I                   Dataset {1}
/dc1/O                   Dataset {1}
/dc1/R1:1                Dataset {1}
/dc1/R1:pwr              Dataset {1}
/dc1/V0:p                Dataset {1}
/dc1/dummy               Dataset {1}
/dc2                     Group
/dc2/I                   Dataset {51}
/dc2/O                   Dataset {51}
/dc2/R1:1                Dataset {51}
/dc2/R1:pwr              Dataset {51}
/dc2/V0:p                Dataset {51}
/dc2/VI                  Dataset {51}
/tran                    Group
/tran/I                  Dataset {56}
/tran/O                  Dataset {56}
/tran/R1:1               Dataset {56}
/tran/R1:pwr             Dataset {56}
/tran/V0:p               Dataset {56}
/tran/time               Dataset {56}
```

Or to multiple files, on for each plot:

```bash
$ nut2hdf -s ../../test/resources/rc2/nutbin.raw

$ tree ../../test/resources/rc2/
../../test/resources/rc2/
├── ...
├── nutbin_ac.hdf
├── nutbin_dc1.hdf
├── nutbin_dc2.hdf
├── nutbin.raw
└── nutbin_tran.hdf

$ h5ls -r ../../test/resources/rc2/nutbin_tran.hdf
/                        Group
/I                       Dataset {56}
/O                       Dataset {56}
/R1:1                    Dataset {56}
/R1:pwr                  Dataset {56}
/V0:p                    Dataset {56}
/time                    Dataset {56}
```

Similarly, `nut2csv` produces CSV files, however it is impossible to store all
plots in a single file.

### API

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
plots = reader.getPlots()

# Get nutmeg plot from list
plot = plots[0]

# Get the name of a plot
plot.getPlotname()

# Get number of points from plot
plot.getNoOfPoints()

# Get number of variables from plot
plot.getNoOfVariables()

# Get set of all waves from plot
waves = list(plot.getWaves())

# Check if wave with name 'I' is part of plot
plot.containsWave('I')

# Get unit of wave with name 'I'
plot.getUnit('I')

# Get the waveform (be careful about complex values!)
if plot.isComplex():
    wave = [complex(p.getReal(), p.getImaginary()) for p in plot.getWave('I')]
else:
    wave = list(plot.getWave('I'))
```

#### The simple way

Import the convenience function

```python
from nutmeg_reader import read_nutmeg
```

which can read both ascii and binary Nutmeg files. The downside being, that it
ignores meta information like the plot name, the unit etc. But it's simpler and
returns a [pandas DataFrame](https://pandas.pydata.org/docs/reference/api/pandas.DataFrame.html).

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
