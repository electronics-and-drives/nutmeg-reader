package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

/**
 * Plot consisting of real waveforms
 */
public final class NutmegRealPlot extends NutmegPlot {

  private final Map<String, double[]> waves;

  private NutmegRealPlot(final String plotname, final int noOfVariables,
      final int noOfPoints, final String refWave,
      final Map<String, String> units, final Map<String, double[]> waves) {
    super(plotname, noOfVariables, noOfPoints, refWave, units);
    this.waves = waves;
  }

  /**
   * The method checks all parameters for consistency. When the parameters are
   * valid, a {@link edlab.eda.reader.nutmeg.NutmegRealPlot NutmegRealPlot} is
   * returned.
   * 
   * @param plotname      Name of the plot
   * @param noOfVariables Number of variables
   * @param noOfPoints    Number of points
   * @param refWave       Name of reference waveform
   * @param units         Units
   * @param waves         Waves
   * @return nutmegRealPlot
   */
  public static NutmegRealPlot make(final String plotname,
      final int noOfVariables, final int noOfPoints, final String refWave,
      final Map<String, String> units, final Map<String, double[]> waves) {

    final NutmegRealPlot plot = new NutmegRealPlot(plotname, noOfVariables,
        noOfPoints, refWave, units, waves);

    if (noOfVariables != units.size()) {
      System.out.println(noOfVariables + "-" + units.size());
      return null;
    }

    if (noOfVariables != waves.size()) {
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

  @Override
  public boolean containsWave(final String wave) {
    return this.waves.containsKey(wave);
  }

  @Override
  public Set<String> getWaves() {
    return this.waves.keySet();
  }

  /**
   * Returns a waveform with a given name.
   * 
   * @param wave name of the wave
   * @return wave as a double array if existing, <code>null</code> otherwise.
   */
  public double[] getWave(final String wave) {
    return this.waves.get(wave);
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public boolean isReal() {
    return true;
  }
}