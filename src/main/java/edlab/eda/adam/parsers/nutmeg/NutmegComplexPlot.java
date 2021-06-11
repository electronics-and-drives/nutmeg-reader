package edlab.eda.adam.parsers.nutmeg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;

public class NutmegComplexPlot extends NutmegPlot {

  public Map<String, Complex[]> values;

  public NutmegComplexPlot() {
    this.values = new HashMap<String, Complex[]>();
  }

  public NutmegComplexPlot(String plotname, int noOfVariables, int noOfPoints) {
    super(plotname, noOfVariables, noOfPoints);
    this.values = new HashMap<String, Complex[]>(noOfVariables);
  }

  public void addData(String name, Complex[] values) {
    this.values.put(name, values);
  }

  public Complex[] getData(String name) {
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