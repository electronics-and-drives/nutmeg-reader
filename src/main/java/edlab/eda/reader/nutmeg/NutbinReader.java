package edlab.eda.reader.nutmeg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.math3.complex.Complex;

/**
 * Reader for a Nutmeg waveform file in binary syntax.
 *
 */
public class NutbinReader extends NutReader {

  private byte[] data;

  private static final byte[] PLOT_ID = { 'P', 'l', 'o', 't', 'n', 'a', 'm',
      'e', ':' };
  private static final byte[] FLAGS_ID = { 'F', 'l', 'a', 'g', 's', ':' };
  private static final byte[] NO_OF_VAR_ID = { 'N', 'o', '.', ' ', 'V', 'a',
      'r', 'i', 'a', 'b', 'l', 'e', 's', ':' };
  private static final byte[] NO_OF_POINTS_ID = { 'N', 'o', '.', ' ', 'P', 'o',
      'i', 'n', 't', 's' };
  private static final byte[] VARS_ID = { 'V', 'a', 'r', 'i', 'a', 'b', 'l',
      'e', 's', ':' };

  private static final byte[] VALS_BINARY_ID = { 'B', 'i', 'n', 'a', 'r', 'y',
      ':', '\n' };

  private static final byte NEWLINE = '\n';

  private static final int BYTES_PER_NUM = 8;

  private static final int START = 0;
  private static final int STOP = 1;

  private NutbinReader(String file) {
    super(file);
  }

  /*
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file - path to waveform file
   * 
   * @return nutReader - Reader for the corresponding waveform, null when the
   * file is not existing.
   */
  public static NutReader getNutReader(String file) {

    NutReader nutReader = new NutbinReader(file);

    if (nutReader.getFile() == null) {
      return null;
    } else {
      return nutReader;
    }
  }

  @Override
  public NutReader read() {
    try {
      this.data = Files.readAllBytes(getFile().toPath());
      return this;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public NutReader parse() {

    int idx = 0;
    this.plots = new LinkedList<NutmegPlot>();

    int[] plotNameIdx = { -1, -1 };
    int[] flagIdx = { -1, -1 };
    int[] noOfVarsIdx = { -1, -1 };
    int[] noOfPointsIdx = { -1, -1 };

    int binaryStartIdx;

    int eol;

    String str;
    String plotname;

    int noOfPoints = 0;
    int noOfVars = 0;

    String[] varNames = null;
    String[] varUnit = null;

    double[] realWave;
    Complex[] complexWave;

    Complex complex;

    String[] line;

    NutmegRealPlot nutmegRealPlot;
    NutmegComplexPlot nutmegComplexPlot;

    HashMap<String, String> units;
    HashMap<String, double[]> realWaves;
    HashMap<String, Complex[]> complexWaves;

    FLAG flag = FLAG.NONE;

    while ((idx = getNextPos(data, idx, PLOT_ID)) > 0) {

      plotNameIdx[START] = idx;
      idx = jmpToNewline(data, idx);
      plotNameIdx[STOP] = idx - 1;

      idx = flagIdx[START] = getNextPos(data, idx + 1, FLAGS_ID);
      idx = jmpToNewline(data, idx);
      flagIdx[STOP] = idx - 1;

      idx = noOfVarsIdx[START] = getNextPos(data, idx + 1, NO_OF_VAR_ID);
      idx = jmpToNewline(data, idx);
      noOfVarsIdx[STOP] = idx - 1;

      idx = noOfPointsIdx[START] = getNextPos(data, idx + 1, NO_OF_POINTS_ID);
      idx = jmpToNewline(data, idx);
      noOfPointsIdx[STOP] = idx - 1;

      if (flagIdx[START] > 0 & noOfVarsIdx[START] > 0
          & noOfPointsIdx[START] > 0) {

        flag = FLAG.NONE;

        plotname = new String(data, plotNameIdx[START],
            plotNameIdx[STOP] - plotNameIdx[START] + 1);

        str = new String(data, flagIdx[START],
            flagIdx[STOP] - flagIdx[START] + 1);
        str = str.trim();

        if (str.equals(REAL_ID)) {
          flag = FLAG.REAL;
        } else if (str.equals(COMPLEX_ID)) {
          flag = FLAG.COMPLEX;
        }

        if (flag != FLAG.NONE) {

          try {
            str = new String(data, noOfVarsIdx[START],
                noOfVarsIdx[STOP] - noOfVarsIdx[START] + 1);
            str = str.trim();
            noOfVars = Integer.parseInt(str);

            str = new String(data, noOfPointsIdx[START],
                noOfPointsIdx[STOP] - noOfPointsIdx[START] + 1);
            str = str.trim();
            noOfPoints = Integer.parseInt(str);
          } catch (Exception e) {
            flag = FLAG.NONE;
          }
        }

        if (flag != FLAG.NONE) {

          idx = getNextPos(data, idx + 1, VARS_ID);

          varNames = new String[noOfVars];
          varUnit = new String[noOfVars];

          for (int i = 0; i < noOfVars; i++) {

            eol = jmpToNewline(data, idx);

            if (eol == idx) {
              flag = FLAG.NONE;
              break;
            }

            str = new String(data, idx, eol - idx + 1);
            str = str.trim();

            line = str.split("\\s");

            if (line.length > 2) {
              varNames[i] = line[1];
              varUnit[i] = line[2];
            } else {
              flag = FLAG.NONE;
              break;
            }

            idx = eol + 1;
          }
        }

        if (flag != FLAG.NONE) {

          binaryStartIdx = getNextPos(data, idx, VALS_BINARY_ID);

          if (noOfPoints == 0) {
            noOfPoints = 1;
          }

          if (flag == FLAG.REAL) {

            units = new HashMap<String, String>();
            realWaves = new HashMap<String, double[]>();

            for (int i = 0; i < noOfVars; i++) {

              realWave = new double[noOfPoints];

              units.put(varNames[i], varUnit[i]);

              for (int j = 0; j < noOfPoints; j++) {
                realWave[j] = ByteBuffer.wrap(data,
                    binaryStartIdx + (i + j * noOfVars) * BYTES_PER_NUM - 1,
                    BYTES_PER_NUM).getDouble();
              }

              realWaves.put(varNames[i], realWave);
            }

            nutmegRealPlot = NutmegRealPlot.make(plotname, noOfVars, noOfPoints,
                units, realWaves);

            if (nutmegRealPlot != null) {
              plots.add(nutmegRealPlot);
            }

            idx += 1 * noOfPoints * noOfVars * BYTES_PER_NUM + 1;

          } else {

            units = new HashMap<String, String>();
            complexWaves = new HashMap<String, Complex[]>();

            for (int i = 0; i < noOfVars; i++) {

              complexWave = new Complex[noOfPoints];

              units.put(varNames[i], varUnit[i]);

              for (int j = 0; j < noOfPoints; j++) {
                complex = new Complex(
                    ByteBuffer.wrap(data,
                        binaryStartIdx + 2 * (i + j * noOfVars) * BYTES_PER_NUM
                            - 1,
                        BYTES_PER_NUM).getDouble(),
                    ByteBuffer
                        .wrap(data,
                            binaryStartIdx
                                + 2 * (i + j * noOfVars) * BYTES_PER_NUM
                                + BYTES_PER_NUM - 1,
                            BYTES_PER_NUM)
                        .getDouble());
                complexWave[j] = complex;
              }

              complexWaves.put(varNames[i], complexWave);
            }

            nutmegComplexPlot = NutmegComplexPlot.make(plotname, noOfVars,
                noOfPoints, units, complexWaves);

            if (nutmegComplexPlot != null) {
              plots.add(nutmegComplexPlot);
            }

            idx += 2 * noOfPoints * noOfVars * BYTES_PER_NUM + 1;
          }
        }
      }
    }
    return this;
  }

  /**
   * Find the next occurrence of a newline character in a byte array starting
   * from index start.
   * 
   * @param data  - array to be searched
   * @param start - index from which on the data array is searched.
   * @return - index in data where the pattern starts
   */
  private static int jmpToNewline(byte[] data, int start) {

    int i = start;

    while (data[i] != NEWLINE && i < data.length) {
      i++;
    }

    return i;
  }

  /**
   * Finds the next occurrence of pattern in data starting from index start.
   * 
   * @param data    - array with data
   * @param start   - index from which on the data array is searched.
   * @param pattern - array with pattern to be searched.
   * @return index - index in data where the pattern starts
   */
  private static int getNextPos(byte[] data, int start, byte[] pattern) {

    int retval = -1;

    int i = start;
    int j;

    boolean patternFound = false;

    while (!patternFound && i < data.length - pattern.length) {

      patternFound = true;

      j = 0;

      while (patternFound && j < pattern.length) {

        if (data[i + j] != pattern[j]) {
          patternFound = false;
        }

        j++;
      }

      if (patternFound) {
        retval = i + pattern.length + 1;
      }

      i++;
    }

    return retval;
  }
}
