# nutmeg-reader

Python wrapper for the Java [nutmeg-reader](https://github.com/electronics-and-drives/nutmeg-reader).

## Installation

Since this package is built upon the java nutmeg-reader, it only works after
the main package is installed.

```
$ pip install .
```

## Usage

Import the main class

```python
from nutmeg_reader import NutmegReader
```

then create a new object.

```python
nutreader = NutmegReader()
nutreader = NutmegReader(class_path="some/other/path/to/nutmeg.jar")
```

optionally one can pass a custom `class_path`, otherwise the `MAVEN_HOME`
environment variable will be used or the default maven path at
`$HOME/.m2/repository`.

## Tests

```
$ cd tests
$ pytest
```
