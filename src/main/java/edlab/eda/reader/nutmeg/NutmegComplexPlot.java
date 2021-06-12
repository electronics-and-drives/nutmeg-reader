package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;

public class NutmegComplexPlot extends NutmegPlot {

  public Map<String, Complex[]> waves;

  private NutmegComplexPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units, Map<String, Complex[]> waves) {
    super(plotname, noOfVariables, noOfPoints, units);
    this.waves = waves;
  }

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