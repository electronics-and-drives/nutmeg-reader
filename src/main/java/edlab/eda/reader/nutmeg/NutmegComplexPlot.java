package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;

/**
 * Plot consisting of complex waveforms.
 */
public final class NutmegComplexPlot extends NutmegPlot {

  public static final String VCVS_COMPLEX = "Re, ComplexRe, ComplexIm";
  public static final String VCVS_XYY = "X, Y, Y";

  private final Map<String, Complex[]> waves;

  private NutmegComplexPlot(final String plotname, final int noOfVariables,
      final int noOfPoints, final String refWave,
      final Map<String, String> units, final Map<String, Complex[]> waves) {
    super(plotname, noOfVariables, noOfPoints, refWave, units);
    this.waves = waves;
  }

  /**
   * The method checks all parameters for consistency. When the parameters are
   * valid, a {@link edlab.eda.reader.nutmeg.NutmegComplexPlot
   * NutmegComplexPlot} is returned.
   * 
   * @param plotname      Name of the plot
   * @param noOfVariables Number of variables
   * @param noOfPoints    Number of points
   * @param refWave       Name of reference waveform
   * @param units         Units
   * @param waves         Waves
   * @return nutmegRealPlot
   */
  public static NutmegComplexPlot make(final String plotname,
      final int noOfVariables, final int noOfPoints, final String refWave,
      final Map<String, String> units, final Map<String, Complex[]> waves) {

    final NutmegComplexPlot plot = new NutmegComplexPlot(plotname,
        noOfVariables, noOfPoints, refWave, units, waves);

    if ((noOfVariables != units.size()) || (noOfVariables != waves.size())) {
      return null;
    }

    for (final String wave : units.keySet()) {
      if (units.get(wave) == null) {
        return null;
      }
    }

    for (final String wave : waves.keySet()) {
      if (waves.get(wave) == null) {
        return null;
      }
    }

    for (final String wave : waves.keySet()) {
      if (!units.containsKey(wave)) {
        return null;
      }
    }

    for (final String wave : waves.keySet()) {
      if (waves.get(wave).length != noOfPoints) {
        return null;
      }
    }

    return plot;
  }

  /**
   * Returns a waveform with a given name.
   * 
   * @param wave - name of the wave
   * @return wave - Wave as a double array if existing, null otherwise.
   */
  public Complex[] getWave(final String wave) {
    return this.waves.get(wave);
  }

  @Override
  public boolean containsWave(final String wave) {
    return this.waves.containsKey(wave);
  }

  @Override
  public Set<String> getWaves() {
    return this.waves.keySet();
  }

  @Override
  public boolean isComplex() {
    return true;
  }

  @Override
  public boolean isReal() {
    return false;
  }

  @Override
  public String toVirtuosoCommaSeperatedValues() {

    final StringBuilder builder = new StringBuilder(VCVS_VERSION)
        .append(VCVS_NEWLINE);

    boolean first = true;

    for (final String waveName : this.getWaves()) {

      if (!waveName.equals(this.getRefWave())) {

        if (first) {
          first = false;
        } else {
          builder.append(VCVS_SEP);
        }

        builder.append(VCVS_DELIM).append(waveName);
      }
    }

    builder.append(VCVS_NEWLINE);

    first = true;

    for (int i = 0; i < (this.getNoOfWaves() - 1); i++) {

      if (first) {
        first = false;
      } else {
        builder.append(VCVS_SEP);
      }

      builder.append(VCVS_DELIM).append(VCVS_XYY);
    }

    builder.append(VCVS_NEWLINE);

    first = true;

    for (int i = 0; i < (this.getNoOfWaves() - 1); i++) {

      if (first) {
        first = false;
      } else {
        builder.append(VCVS_SEP);
      }

      builder.append(VCVS_DELIM).append(VCVS_COMPLEX);
    }

    builder.append(VCVS_NEWLINE);
    first = true;

    for (int i = 0; i < (this.getNoOfWaves() - 1); i++) {

      if (first) {
        first = false;
      } else {
        builder.append(VCVS_SEP);
      }

      builder.append(VCVS_DELIM).append(this.getRefWave()).append(" ")
          .append(VCVS_SEP).append("-");
    }

    builder.append(VCVS_NEWLINE);
    first = true;

    for (final String waveName : this.getWaves()) {

      if (!waveName.equals(this.getRefWave())) {

        if (first) {
          first = false;
        } else {
          builder.append(VCVS_SEP);
        }

        builder.append(VCVS_DELIM).append(this.getUnit(this.getRefWave()))
            .append(" ").append(VCVS_SEP).append(this.getUnit(waveName));
      }
    }

    for (int i = 0; i < this.getNoOfPoints(); i++) {

      builder.append(VCVS_NEWLINE);
      first = true;

      for (final String waveName : this.getWaves()) {

        if (!waveName.equals(this.getRefWave())) {

          if (first) {
            first = false;
          } else {
            builder.append(VCVS_SEP);
          }

          builder.append(this.getWave(this.getRefWave())[i].getReal())
              .append(VCVS_SEP).append(this.getWave(waveName)[i].getReal())
              .append(VCVS_SEP)
              .append(this.getWave(waveName)[i].getImaginary());
        }
      }
    }

    return builder.toString();
  }
}