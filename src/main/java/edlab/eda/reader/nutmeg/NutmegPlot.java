package edlab.eda.reader.nutmeg;

import java.util.Map;
import java.util.Set;

/**
 * Plot consisting of waveforms.
 *
 */
public abstract class NutmegPlot {

  private int noOfWaves;
  private int noOfPoints;
  private String plotname;
  private Map<String, String> units;

  protected NutmegPlot(String plotname, int noOfVariables, int noOfPoints,
      Map<String, String> units) {

    this.units = units;
    this.plotname = plotname;
    this.noOfWaves = noOfVariables;
    this.noOfPoints = noOfPoints;

  }

  /**
   * Returns the number of waves in the plot.
   * 
   * @return number of waves.
   */
  public int getNoOfWaves() {
    return noOfWaves;
  }

  /**
   * Get number of points (length of wave).
   * 
   * @return length of wave
   */
  public int getNoOfPoints() {
    return noOfPoints;
  }

  /**
   * Returns the unit of a given wave.
   * 
   * @param wave - name of the wave
   * @return unit - unit of the wave
   */
  public String getUnit(String wave) {
    return this.units.get(wave);
  }

  /**
   * Getter for the plotname
   * 
   * @return plotname
   */
  public String getPlotname() {
    return plotname;
  }

  /**
   * Check if the plot contains a wave with a given name.
   * 
   * @param wave - name of the wave
   * @return True if a plot with the given name is contained in the plot.
   */
  public abstract boolean containsWave(String wave);

  /**
   * Returns a set of all names of waves in the plot.
   * 
   * @return Set of all names of waves.
   */
  public abstract Set<String> getWaves();
}