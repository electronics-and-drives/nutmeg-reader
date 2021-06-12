package edlab.eda.parsers.nutmeg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class NutmegPlot {

  protected int noOfVariables;
  private int noOfPoints;
  private String plotname;
  private Map<String, String> unit;

  public NutmegPlot() {
    this.unit = new HashMap<String, String>();
  }

  public NutmegPlot(String plotname, int noOfVariables, int noOfPoints) {

    this.unit = new HashMap<String, String>(noOfVariables);
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

  public void addUnit(String name, String unit) {
    this.unit.put(name, unit);
  }

  public String getUnit(String name) {
    return this.unit.get(name);
  }

  public String getPlotname() {
    return plotname;
  }

  public abstract boolean hasKey(String key);

  public abstract Set<String> getKeys();
}