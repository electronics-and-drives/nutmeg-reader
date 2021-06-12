# nutmeg-parser

Reader for the Nutmeg Waveform Format in Java.
Parsers for both ASCII and Binary waveforms are provided.

## Installation

Clone this repository:

```bash
$ git clone https://github.com/electronics-and-drives/nutmeg-reader.git
```

`cd nutmeg-reader` into the directory and install the maven package:

```bash
$ mvn install
```

Add the dependency to your project

```xml
<dependency>
  <groupId>edlab.eda.reader</groupId>
  <artifactId>nutmeg</artifactId>
  <version>0.0.1</version>
</dependency>
```

Import the corresponding package to your code
```java
import edlab.eda.reader.nutmeg.*;
```

## API

The [JavaDoc](https://electronics-and-drives.github.io/nutmeg-reader/)
is stored on the Github-Pages (branch *gh-pages*).

## Example

The below shown example shows how a 

```java
// Create a new reader
NutReader reader = NutReader
    .getNutasciiReader("./src/test/java/resources/rc/nutascii.raw");

// Read and parse the nutascii
reader.read().parse();

// Get all plots from the reader
List<NutmegPlot> plots = reader.getPlots();

// Get nutmeg plot from list
NutmegPlot nutmegPlot = plots.get(0);

// Get name of plot
nutmegPlot.getPlotname();

// Get number of points from plot
nutmegPlot.getNoOfPoints();

// Get number of variables from plot
nutmegPlot.getNoOfVariables();

// Get set of all waves from plot
Set<String> waves = nutmegPlot.getWaves();

// Check if wave with name "I" is part of plot
nutmegPlot.containsWave("I");

// Get unit of wave with name "I"
nutmegPlot.getUnit("I");

if (nutmegPlot instanceof NutmegRealPlot) {

  // Cast plot to real plot
  NutmegRealPlot nutmegRealPlot = (NutmegRealPlot) nutmegPlot;

  // Get wave of wave with name "I"
  double[] wave = nutmegRealPlot.getWave("I");
}
```

## TODO

- [X] Test the reader for waveforms generated with Cadence Spectre
- [ ] Test the reader for waveforms generated with Ngspice

## License

Copyright (C) 2021, Electronics & Drives Lab

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see 
[https://www.gnu.org/licenses/](https://www.gnu.org/licenses).