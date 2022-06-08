package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;

/**
 * Plot consisting of complex waveforms.
 */
public final class NutmegComplexPlot extends NutmegPlot {

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
}