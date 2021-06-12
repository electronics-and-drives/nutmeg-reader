package edlab.eda.reader.nutmeg;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.math3.complex.Complex;

/**
 * Reader for a Nutmeg waveform file in ASCII syntax.
 *
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

  private NutasciiReader(String file) {
    super(file);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file - path to waveform file
   * @return nutReader - Reader for the corresponding waveform, null when the
   *         file is not existing.
   */
  public static NutReader getNutReader(String file) {

    NutReader nutReader = new NutasciiReader(file);

    if (nutReader.getFile() == null) {
      return null;
    } else {
      return nutReader;
    }
  }

  @Override
  public NutReader read() {

    try {

      Scanner scn = new Scanner(getFile());
      scn.useDelimiter("\\Z");
      data = scn.next();
      scn.close();

      return this;

    } catch (FileNotFoundException e) {
      System.err.println("Unable to open file " + getFile().getAbsolutePath()
          + ":\n" + e.toString());

      return null;
    }

  }

  @Override
  public NutReader parse() {

    this.scanner = new Scanner(data);
    this.scannerOpen = true;
    this.plots = new LinkedList<NutmegPlot>();

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
    while ((plotname = getNextPlot()) != null) {

      flag = getFlag();

      if (!flag.equals(FLAG.NONE)) {

        noOfVariables = getNumberOfVariables();
        noOfPoints = getNumberOfPoints();

        if (noOfVariables > 0 && noOfPoints > 0) {
          variables = getVariables(noOfVariables);

          if (flag.equals(FLAG.REAL)) {

            units = new HashMap<String, String>();
            realWaves = new HashMap<String, double[]>();

            realVals = readDoubleValues(noOfPoints, noOfVariables);

            for (int i = 0; i < variables.length; i++) {
              units.put(variables[i][0], variables[i][1]);
              realWaves.put(variables[i][0], realVals[i]);
            }

            nutmegRealPlot = NutmegRealPlot.make(plotname, noOfVariables,
                noOfPoints, units, realWaves);

            if (nutmegRealPlot != null) {
              this.plots.addLast(nutmegRealPlot);
            }

          } else if (flag.equals(FLAG.COMPLEX)) {

            units = new HashMap<String, String>();
            complexWaves = new HashMap<String, Complex[]>();

            complexVals = readComplexValues(noOfPoints, noOfVariables);

            for (int i = 0; i < variables.length; i++) {
              units.put(variables[i][0], variables[i][1]);
              complexWaves.put(variables[i][0], complexVals[i]);
            }

            nutmegComplexPlot = NutmegComplexPlot.make(plotname, noOfVariables,
                noOfPoints, units, complexWaves);

            if (nutmegComplexPlot != null) {
              plots.add(nutmegComplexPlot);
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

    if (scannerOpen) {

      String line;

      while (scanner.hasNextLine()) {

        line = scanner.nextLine();

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

    if (scannerOpen && scanner.hasNextLine()) {

      String line = scanner.nextLine();

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

    if (scannerOpen && scanner.hasNextLine()) {
      String line = scanner.nextLine();

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

    if (scannerOpen && scanner.hasNextLine()) {

      String line = scanner.nextLine();

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
  private String[][] getVariables(int noOfvariables) {

    String[] refracturedLine;
    String[][] variables = new String[noOfvariables][2];

    for (int i = 0; i < variables.length; i++) {

      if (scannerOpen && scanner.hasNextLine()) {

        String line = scanner.nextLine();

        if (i == 0) {

          if (line.startsWith(VARS_ID)) {

            line = line.substring(VARS_ID.length(), line.length()).trim();

          } else {
            return null;
          }
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
  private Complex[][] readComplexValues(int noOfPoints, int noOfVariables) {

    Complex[][] vals = new Complex[noOfVariables][noOfPoints];

    String[] splitComplexNo;

    int idx;

    if (scannerOpen && scanner.hasNextLine()) {

      String line = scanner.nextLine();

      if (line.startsWith(VALS_ASCII_ID)) {

        String[] strArr = readAscii(noOfPoints * (noOfVariables + 1));

        if (strArr != null) {

          for (int i = 0; i < noOfPoints; i++) {
            for (int j = 0; j < noOfVariables; j++) {

              idx = i * (noOfVariables + 1) + j + 1;

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
  private double[][] readDoubleValues(int noOfPoints, int noOfVariables) {

    double[][] vals = new double[noOfVariables][noOfPoints];
    int idx;

    if (scannerOpen && scanner.hasNextLine()) {

      String line = scanner.nextLine();

      if (line.startsWith(VALS_ASCII_ID)) {

        String[] strArr = readAscii(noOfPoints * (noOfVariables + 1));

        if (strArr != null) {

          for (int i = 0; i < noOfPoints; i++) {
            for (int j = 0; j < noOfVariables; j++) {

              idx = i * (noOfVariables + 1) + j + 1;
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
  private String[] readAscii(int noOfElems) {

    String[] result = new String[noOfElems];

    for (int i = 0; i < result.length; i++) {

      if (scannerOpen && scanner.hasNext()) {

        result[i] = scanner.next();

      } else {
        this.scanner.close();
        this.scannerOpen = false;
        return null;
      }
    }
    return result;
  }
}