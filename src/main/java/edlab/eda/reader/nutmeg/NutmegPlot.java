package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

public abstract class NutmegPlot {

  private int noOfVariables;
  private int noOfPoints;
  private String plotname;
  private Map<String, String> units;

  protected NutmegPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units) {

    this.units = units;
    this.plotname = plotname;
    this.noOfVariables = noOfVariables;
    this.noOfPoints = noOfPoints;

  }

  public int getNoOfVariables() {
    return noOfVariables;
  }

  public int getNoOfPoints() {
    return noOfPoints;
  }

  public String getUnit(String wave) {
    return this.units.get(wave);
  }

  public String getPlotname() {
    return plotname;
  }

  public abstract boolean containsWave(String wave);

  public abstract Set<String> getWaves();
}