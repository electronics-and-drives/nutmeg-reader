package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;

/**
 * Plot consisting of complex waveforms.
 *
 */
public class NutmegComplexPlot extends NutmegPlot {

  private Map<String, Complex[]> waves;

  private NutmegComplexPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units, Map<String, Complex[]> waves) {
    super(plotname, noOfVariables, noOfPoints, units);
    this.waves = waves;
  }

  /**
   * The method checks all parameters for consistency. When the parameters are
   * valid, a {@link edlab.eda.reader.nutmeg.NutmegComplexPlot NutmegComplexPlot} is
   * returned.
   * 
   * @param plotname      - Name of the plot
   * @param noOfVariables - Number of variables
   * @param noOfPoints    - Number of points
   * @param units         - Units
   * @param waves         - Waves
   * @return nutmegRealPlot - Plot
   */
  public static NutmegComplexPlot make(String plotname, int noOfVariables,
      int noOfPoints, Map<String, String> units, Map<String, Complex[]> waves) {

    NutmegComplexPlot plot = new NutmegComplexPlot(plotname, noOfVariables,
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

  /**
   * Returns a waveform with a given name.
   * 
   * @param wave - name of the wave
   * @return wave - Wave as a double array if existing, null otherwise.
   */
  public Complex[] getWave(String wave) {
    return this.waves.get(wave);
  }

  @Override
  public boolean containsWave(String wave) {
    return this.waves.containsKey(wave);
  }

  @Override
  public Set<String> getWaves() {
    return this.waves.keySet();
  }
}