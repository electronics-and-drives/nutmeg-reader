package edlab.eda.adam.parsers.nutmeg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NutmegRealPlot extends NutmegPlot {

  private Map<String, double[]> values;

  public NutmegRealPlot() {
    this.values = new HashMap<String, double[]>(noOfVariables);
  }

  public NutmegRealPlot(String plotname, int noOfVariables, int noOfPoints) {
    super(plotname, noOfVariables, noOfPoints);
    this.values = new HashMap<String, double[]>(noOfVariables);
  }

  public void addData(String name, double[] values) {
    this.values.put(name, values);
  }

  public double[] getData(String name) {
    return this.values.get(name);
  }

  @Override
  public boolean hasKey(String key) {
    return values.containsKey(key);
  }

  @Override
  public Set<String> getKeys() {
    return this.values.keySet();
  }
}