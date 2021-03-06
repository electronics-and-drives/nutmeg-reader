package edlab.eda.reader.nutmeg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.text.translate.CharSequenceTranslator;

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

  private NutbinReader(final String file) {
    super(file, new DefaultTranslator());
  }

  private NutbinReader(final String file,
      final CharSequenceTranslator translator) {
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

    final NutReader nutReader = new NutbinReader(file);

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

    final NutReader nutReader = new NutbinReader(file, translator);

    if (nutReader.getFile() == null) {
      return null;
    } else {
      return nutReader;
    }
  }

  @Override
  public NutReader read() {
    try {
      this.data = Files.readAllBytes(this.getFile().toPath());
      return this;
    } catch (final IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public NutReader parse() {

    int idx = 0;
    this.plots = new LinkedList<>();

    final int[] plotNameIdx = { -1, -1 };
    final int[] flagIdx = { -1, -1 };
    final int[] noOfVarsIdx = { -1, -1 };
    final int[] noOfPointsIdx = { -1, -1 };

    int binaryStartIdx;

    int eol;

    String str;
    String plotname;

    int noOfPoints = 0;
    int noOfVars = 0;

    int realNoOfVars;

    String[] varNames = null;
    String[] varUnit = null;

    double[] realWave;
    Complex[] complexWave;

    double re, im;

    String[] line;

    NutmegRealPlot nutmegRealPlot;
    NutmegComplexPlot nutmegComplexPlot;

    HashMap<String, String> units;
    HashMap<String, double[]> realWaves;
    HashMap<String, Complex[]> complexWaves;

    final ByteBuffer buffer = ByteBuffer.allocate(BYTES_PER_NUM);

    FLAG flag = FLAG.NONE;

    while ((idx = getNextPos(this.data, idx, PLOT_ID)) > 0) {

      plotNameIdx[START] = idx;
      idx = jmpToNewline(this.data, idx);
      plotNameIdx[STOP] = idx - 1;

      idx = flagIdx[START] = getNextPos(this.data, idx + 1, FLAGS_ID);
      idx = jmpToNewline(this.data, idx);
      flagIdx[STOP] = idx - 1;

      idx = noOfVarsIdx[START] = getNextPos(this.data, idx + 1, NO_OF_VAR_ID);
      idx = jmpToNewline(this.data, idx);
      noOfVarsIdx[STOP] = idx - 1;

      idx = noOfPointsIdx[START] = getNextPos(this.data, idx + 1,
          NO_OF_POINTS_ID);
      idx = jmpToNewline(this.data, idx);
      noOfPointsIdx[STOP] = idx - 1;

      if ((flagIdx[START] > 0) & (noOfVarsIdx[START] > 0)
          & (noOfPointsIdx[START] > 0)) {

        flag = FLAG.NONE;

        plotname = new String(this.data, plotNameIdx[START],
            (plotNameIdx[STOP] - plotNameIdx[START]) + 1);

        str = new String(this.data, flagIdx[START],
            (flagIdx[STOP] - flagIdx[START]) + 1);
        str = str.trim();

        if (str.equals(REAL_ID)) {
          flag = FLAG.REAL;
        } else if (str.equals(COMPLEX_ID)) {
          flag = FLAG.COMPLEX;
        }

        if (flag != FLAG.NONE) {

          try {
            str = new String(this.data, noOfVarsIdx[START],
                (noOfVarsIdx[STOP] - noOfVarsIdx[START]) + 1);
            str = str.trim();
            noOfVars = Integer.parseInt(str);

            str = new String(this.data, noOfPointsIdx[START],
                (noOfPointsIdx[STOP] - noOfPointsIdx[START]) + 1);
            str = str.trim();
            noOfPoints = Integer.parseInt(str);
          } catch (final Exception e) {
            flag = FLAG.NONE;
          }
        }

        if (flag != FLAG.NONE) {

          idx = getNextPos(this.data, idx + 1, VARS_ID);

          varNames = new String[noOfVars];
          varUnit = new String[noOfVars];

          for (int i = 0; i < noOfVars; i++) {

            eol = jmpToNewline(this.data, idx);

            if (eol == idx) {
              flag = FLAG.NONE;
              break;
            }

            str = new String(this.data, idx, (eol - idx) + 1);
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

          binaryStartIdx = getNextPos(this.data, idx, VALS_BINARY_ID);

          if (noOfPoints == 0) {
            noOfPoints = 1;
          }

          realNoOfVars = noOfVars;

          if (flag == FLAG.REAL) {

            units = new HashMap<>();
            realWaves = new HashMap<>();

            for (int i = 0; i < noOfVars; i++) {

              realWave = new double[noOfPoints];

              for (int j = 0; j < noOfPoints; j++) {

                buffer.clear();
                buffer.put(this.data,
                    (binaryStartIdx + ((i + (j * noOfVars)) * BYTES_PER_NUM))
                        - 1,
                    BYTES_PER_NUM);

                realWave[j] = buffer.getDouble(0);
              }

              if (realWaves.containsKey(varNames[i])) {
                realNoOfVars--;
              } else {

                realWaves.put(this.translator.translate(varNames[i]), realWave);
                units.put(varNames[i], varUnit[i]);
              }
            }

            nutmegRealPlot = NutmegRealPlot.make(plotname, realNoOfVars,
                noOfPoints, varNames[0], units, realWaves);

            if (nutmegRealPlot != null) {
              this.plots.add(nutmegRealPlot);
            }

            idx += (1 * noOfPoints * noOfVars * BYTES_PER_NUM) + 1;

          } else {

            units = new HashMap<>();
            complexWaves = new HashMap<>();

            for (int i = 0; i < noOfVars; i++) {

              complexWave = new Complex[noOfPoints];

              for (int j = 0; j < noOfPoints; j++) {

                buffer.clear();
                buffer.put(this.data,
                    (binaryStartIdx
                        + (2 * (i + (j * noOfVars)) * BYTES_PER_NUM)) - 1,
                    BYTES_PER_NUM);

                re = buffer.getDouble(0);

                buffer.clear();
                buffer.put(this.data,
                    (binaryStartIdx + (2 * (i + (j * noOfVars)) * BYTES_PER_NUM)
                        + BYTES_PER_NUM) - 1,
                    BYTES_PER_NUM);

                im = buffer.getDouble(0);

                complexWave[j] = new Complex(re, im);
              }

              if (complexWaves.containsKey(varNames[i])) {
                realNoOfVars--;
              } else {
                complexWaves.put(this.translator.translate(varNames[i]),
                    complexWave);
                units.put(varNames[i], varUnit[i]);
              }
            }

            nutmegComplexPlot = NutmegComplexPlot.make(plotname, realNoOfVars,
                noOfPoints, varNames[0], units, complexWaves);

            if (nutmegComplexPlot != null) {
              this.plots.add(nutmegComplexPlot);
            }

            idx += (2 * noOfPoints * noOfVars * BYTES_PER_NUM) + 1;
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
  private static int jmpToNewline(final byte[] data, final int start) {

    int i = start;

    while ((data[i] != NEWLINE) && (i < data.length)) {
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
  private static int getNextPos(final byte[] data, final int start,
      final byte[] pattern) {

    int retval = -1;

    int i = start;
    int j;

    boolean patternFound = false;

    while (!patternFound && (i < (data.length - pattern.length))) {

      patternFound = true;

      j = 0;

      while (patternFound && (j < pattern.length)) {

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