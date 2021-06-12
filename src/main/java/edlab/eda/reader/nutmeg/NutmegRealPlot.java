package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

public class NutmegRealPlot extends NutmegPlot {

  private Map<String, double[]> waves;

  private NutmegRealPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units, Map<String, double[]> waves) {
    super(plotname, noOfVariables, noOfPoints, units);
    this.waves = waves;
  }

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

  public double[] getWave(String wave) {
    return this.waves.get(wave);
  }
}