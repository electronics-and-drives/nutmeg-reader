package edlab.eda.reader.nutmeg;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.text.translate.CharSequenceTranslator;

/**
 * Reader for a Nutmeg waveform file in ASCII syntax.
 */
public class NutasciiReader extends NutReader {

  @SuppressWarnings("unused")
  private static final String TITLE_ID = "Title:";
  @SuppressWarnings("unused")
  private static final String DATE_ID = "Date:";
  private static final String PLOT_ID = "Plotname:";
  private static final String FLAGS_ID = "Flags:";
  private static final String NO_OF_VAR_ID = "No. Variables:";
  private static final String NO_OF_POINTS_ID = "No. Points:";
  private static final String VARS_ID = "Variables:";

  private static final String VALS_ASCII_ID = "Values:";

  private String data;

  private Scanner scanner = null;
  private boolean scannerOpen = false;

  private NutasciiReader(final String file) {
    super(file, new DefaultTranslator());
  }

  private NutasciiReader(final String file, final CharSequenceTranslator translator) {
    super(file, translator);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file Path to waveform file
   * @return nutReader Reader for the corresponding waveform, <code>null</code>
   *         when the file is not existing.
   */
  public static NutReader getNutReader(final String file) {

    final NutReader nutReader = new NutasciiReader(file);

    if (nutReader.getFile() == null) {
      return null;
    } else {
      return nutReader;
    }
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file       Path to waveform file
   * @param translator Translator for wave names
   * @return nutReader Reader for the corresponding waveform, <code>null</code>
   *         when the file is not existing.
   */
  public static NutReader getNutReader(final String file,
      final CharSequenceTranslator translator) {

    final NutReader nutReader = new NutasciiReader(file, translator);

    if (nutReader.getFile() == null) {
      return null;
    } else {
      return nutReader;
    }
  }

  @Override
  public NutReader read() {

    try {

      final Scanner scn = new Scanner(this.getFile());
      scn.useDelimiter("\\Z");
      this.data = scn.next();
      scn.close();

      return this;

    } catch (final FileNotFoundException e) {
      System.err.println("Unable to open file " + this.getFile().getAbsolutePath()
          + ":\n" + e.toString());

      return null;
    }
  }

  @Override
  public NutReader parse() {

    this.scanner = new Scanner(this.data);
    this.scannerOpen = true;
    this.plots = new LinkedList<>();

    String plotname;
    FLAG flag;
    int noOfPoints;
    int noOfVariables;
    String[][] variables = null;

    Complex[][] complexVals;
    double[][] realVals;

    NutmegRealPlot nutmegRealPlot;
    NutmegComplexPlot nutmegComplexPlot;

    HashMap<String, String> units;
    HashMap<String, double[]> realWaves;
    HashMap<String, Complex[]> complexWaves;

    // Iterate until no new plots are in file
    while ((plotname = this.getNextPlot()) != null) {

      flag = this.getFlag();

      if (!flag.equals(FLAG.NONE)) {

        noOfVariables = this.getNumberOfVariables();
        noOfPoints = this.getNumberOfPoints();

        if (noOfPoints == 0) {
          noOfPoints = 1;
        }

        if ((noOfVariables > 0) && (noOfPoints > 0)) {
          variables = this.getVariables(noOfVariables);

          if (flag.equals(FLAG.REAL)) {

            units = new HashMap<>();
            realWaves = new HashMap<>();

            realVals = this.readDoubleValues(noOfPoints, noOfVariables);

            for (int i = 0; i < variables.length; i++) {

              if (realWaves.containsKey(variables[i][0])) {
                noOfVariables--;
              } else {

                units.put(variables[i][0], variables[i][1]);
                realWaves.put(this.translator.translate(variables[i][0]),
                    realVals[i]);
              }
            }

            nutmegRealPlot = NutmegRealPlot.make(plotname, noOfVariables,
                noOfPoints, variables[0][0], units, realWaves);

            if (nutmegRealPlot != null) {
              this.plots.addLast(nutmegRealPlot);
            }

          } else if (flag.equals(FLAG.COMPLEX)) {

            units = new HashMap<>();
            complexWaves = new HashMap<>();

            complexVals = this.readComplexValues(noOfPoints, noOfVariables);

            for (int i = 0; i < variables.length; i++) {

              if (complexWaves.containsKey(variables[i][0])) {
                noOfVariables--;
              } else {
                units.put(variables[i][0], variables[i][1]);
                complexWaves.put(this.translator.translate(variables[i][0]),
                    complexVals[i]);
              }
            }

            nutmegComplexPlot = NutmegComplexPlot.make(plotname, noOfVariables,
                noOfPoints, variables[0][0], units, complexWaves);

            if (nutmegComplexPlot != null) {
              this.plots.add(nutmegComplexPlot);
            }
          }
        }
      }
    }

    this.scanner.close();
    this.scannerOpen = false;

    return this;
  }

  /**
   * Returns next plotname in the stream.
   * 
   * @return plotname
   */
  private String getNextPlot() {

    if (this.scannerOpen) {

      String line;

      while (this.scanner.hasNextLine()) {

        line = this.scanner.nextLine();

        if (line.startsWith(PLOT_ID)) {
          return line.substring(PLOT_ID.length(), line.length()).trim();
        }
      }

      this.scannerOpen = false;
      return null;

    } else {
      return null;
    }
  }

  /**
   * Returns whether next plot in stream is complex or real.
   * 
   * @return flag
   */
  private FLAG getFlag() {

    if (this.scannerOpen && this.scanner.hasNextLine()) {

      String line = this.scanner.nextLine();

      if (line.startsWith(FLAGS_ID)) {

        line = line.substring(FLAGS_ID.length(), line.length()).trim();

        if (line.equals(COMPLEX_ID)) {
          return FLAG.COMPLEX;
        } else if (line.equals(REAL_ID)) {
          return FLAG.REAL;
        } else {
          return FLAG.NONE;
        }
      } else {
        return FLAG.NONE;
      }
    } else {
      this.scannerOpen = false;
      this.scanner.close();
      return FLAG.NONE;
    }
  }

  /**
   * Finds the number of variables of the current plot in stream.
   * 
   * @return Number of Variables
   */
  private int getNumberOfVariables() {

    if (this.scannerOpen && this.scanner.hasNextLine()) {
      String line = this.scanner.nextLine();

      if (line.startsWith(NO_OF_VAR_ID)) {

        line = line.substring(NO_OF_VAR_ID.length(), line.length()).trim();

        return Integer.parseInt(line);
      } else {
        return -1;
      }
    } else {
      this.scannerOpen = false;
      return -1;
    }
  }

  /**
   * Finds the number of points of the current plot in stream.
   * 
   * @return Number of Points
   */
  private int getNumberOfPoints() {

    if (this.scannerOpen && this.scanner.hasNextLine()) {

      String line = this.scanner.nextLine();

      if (line.startsWith(NO_OF_POINTS_ID)) {

        line = line.substring(NO_OF_POINTS_ID.length(), line.length()).trim();

        return Integer.parseInt(line);
      } else {
        return -1;
      }
    } else {
      this.scannerOpen = false;
      return -1;
    }
  }

  /**
   * Read variables and units from steam.
   * 
   * @param noOfVariables - number of variables to be read.
   * @return array of variable names and units.
   */
  private String[][] getVariables(final int noOfvariables) {

    String[] refracturedLine;
    final String[][] variables = new String[noOfvariables][2];

    for (int i = 0; i < variables.length; i++) {

      if (this.scannerOpen && this.scanner.hasNextLine()) {

        String line = this.scanner.nextLine();

        if (i == 0) {

          if (line.startsWith(VARS_ID)) {
            line = line.substring(VARS_ID.length(), line.length()).trim();

          } else {
            return null;
          }
        }

        if (line.length() == 0) {
          line = this.scanner.nextLine();
        }

        refracturedLine = line.trim().split("\\s");

        if (refracturedLine.length >= 3) {

          variables[i][0] = refracturedLine[1];
          variables[i][1] = refracturedLine[2];

        } else {
          return null;
        }

      } else {
        this.scanner.close();
        this.scannerOpen = false;
        return null;
      }
    }

    return variables;
  }

  /**
   * Read complex values from stream.
   * 
   * @param noOfPoints    - number of points to be read.
   * @param noOfVariables - number of variables to be read.
   * @return double array with values.
   */
  private Complex[][] readComplexValues(final int noOfPoints, final int noOfVariables) {

    final Complex[][] vals = new Complex[noOfVariables][noOfPoints];

    String[] splitComplexNo;

    int idx;

    if (this.scannerOpen && this.scanner.hasNextLine()) {

      final String line = this.scanner.nextLine();

      if (line.startsWith(VALS_ASCII_ID)) {

        final String[] strArr = this.readAscii(noOfPoints * (noOfVariables + 1));

        if (strArr != null) {

          for (int i = 0; i < noOfPoints; i++) {
            for (int j = 0; j < noOfVariables; j++) {

              idx = (i * (noOfVariables + 1)) + j + 1;

              splitComplexNo = strArr[idx].split(",");

              if (splitComplexNo.length == 2) {

                vals[j][i] = new Complex(Double.parseDouble(splitComplexNo[0]),
                    Double.parseDouble(splitComplexNo[1]));

              } else {
                return null;
              }
            }
          }
          return vals;
        } else {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Read real values from stream.
   * 
   * @param noOfPoints    - number of points to be read.
   * @param noOfVariables - number of variables to be read.
   * @return double array with values.
   */
  private double[][] readDoubleValues(final int noOfPoints, final int noOfVariables) {

    final double[][] vals = new double[noOfVariables][noOfPoints];
    int idx;

    if (this.scannerOpen && this.scanner.hasNextLine()) {

      final String line = this.scanner.nextLine();

      if (line.startsWith(VALS_ASCII_ID)) {

        final String[] strArr = this.readAscii(noOfPoints * (noOfVariables + 1));

        if (strArr != null) {

          for (int i = 0; i < noOfPoints; i++) {
            for (int j = 0; j < noOfVariables; j++) {

              idx = (i * (noOfVariables + 1)) + j + 1;
              vals[j][i] = Double.parseDouble(strArr[idx]);
            }
          }
        } else {
          return null;
        }
        return vals;
      }

      return null;
    } else {
      this.scanner.close();
      this.scannerOpen = false;

      return null;
    }
  }

  /**
   * Read string elements from the stream
   * 
   * @param noOfElems - number of elements to be read from stream.
   * @return array of strings.
   */
  private String[] readAscii(final int noOfElems) {

    final String[] result = new String[noOfElems];

    for (int i = 0; i < result.length; i++) {

      if (this.scannerOpen && this.scanner.hasNext()) {

        result[i] = this.scanner.next();

      } else {
        this.scanner.close();
        this.scannerOpen = false;
        return null;
      }
    }
    return result;
  }
}