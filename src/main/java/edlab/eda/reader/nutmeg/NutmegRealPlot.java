package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

/**
 * Plot consisting of real waveforms
 *
 */
public class NutmegRealPlot extends NutmegPlot {

  private Map<String, double[]> waves;

  private NutmegRealPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units, Map<String, double[]> waves) {
    super(plotname, noOfVariables, noOfPoints, units);
    this.waves = waves;
  }

  /**
   * The method checks all parameters for consistency. When the parameters are
   * valid, a {@link edlab.eda.reader.nutmeg.NutmegRealPlot NutmegRealPlot} is
   * returned.
   * 
   * @param plotname      - Name of the plot
   * @param noOfVariables - Number of variables
   * @param noOfPoints    - Number of points
   * @param units         - Units
   * @param waves         - Waves
   * @return nutmegRealPlot - Plot
   */
  public static NutmegRealPlot make(String plotname, int noOfVariables,
      int noOfPoints, Map<String, String> units, Map<String, double[]> waves) {

    NutmegRealPlot plot = new NutmegRealPlot(plotname, noOfVariables,
        noOfPoints, units, waves);

    if (noOfVariables != units.size()) {
      return null;
    }

    if (noOfVariables != waves.size()) {
      return null;
    }

    for (String wave : units.keySet()) {
      if (units.get(wave) == null) {
        return null;
      }
    }

    for (String wave : waves.keySet()) {
      if (waves.get(wave) == null) {
        return null;
      }
    }

    for (String wave : waves.keySet()) {
      if (!units.containsKey(wave)) {
        return null;
      }
    }

    for (String wave : waves.keySet()) {
      if (waves.get(wave).length != noOfPoints) {
        return null;
      }
    }

    return plot;

  }

  @Override
  public boolean containsWave(String wave) {
    return this.waves.containsKey(wave);
  }

  @Override
  public Set<String> getWaves() {
    return this.waves.keySet();
  }

  /**
   * Returns a waveform with a given name.
   * 
   * @param wave - name of the wave
   * @return wave - Wave as a double array if existing, null otherwise.
   */
  public double[] getWave(String wave) {
    return this.waves.get(wave);
  }
}