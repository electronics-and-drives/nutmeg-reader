package edlab.eda.reader.nutmeg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plot consisting of waveforms.
 *
 */
public abstract class NutmegPlot {

  private final int noOfWaves;
  private final int noOfPoints;
  private final String plotname;
  private final String refWave;
  private final Map<String, String> units;

  protected NutmegPlot(final String plotname, final int noOfVariables, final int noOfPoints,
      final String refWave, final Map<String, String> units) {
    this.units = units;
    this.plotname = plotname;
    this.noOfWaves = noOfVariables;
    this.refWave = refWave;
    this.noOfPoints = noOfPoints;
  }

  /**
   * Returns the number of waves in the plot.
   * 
   * @return number of waves.
   */
  public int getNoOfWaves() {
    return this.noOfWaves;
  }

  /**
   * Get number of points (length of wave).
   * 
   * @return length of wave
   */
  public int getNoOfPoints() {
    return this.noOfPoints;
  }

  /**
   * Returns the unit of a given wave.
   * 
   * @param wave - name of the wave
   * @return unit - unit of the wave
   */
  public String getUnit(final String wave) {
    return this.units.get(wave);
  }

  /**
   * Getter for the plotname
   * 
   * @return plotname
   */
  public String getPlotname() {
    return this.plotname;
  }

  /**
   * Getter for the reference waveform
   * 
   * @return refWave
   */
  public String getRefWave() {
    return this.refWave;
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

  /**
   * Returns if the plot contains complex waves
   * 
   * @return True if plot contains complex waves
   */
  public abstract boolean isComplex();

  /**
   * Returns if the plot contains real waves
   * 
   * @return True if plot contains real waves
   */
  public abstract boolean isReal();

  /**
   * Create a map from a list of {@link NutmegPlot}. The key corresponds to the
   * plotname and the value to the {@link NutmegPlot}
   * 
   * @param plots list of plots
   * @return map
   */
  public static Map<String, NutmegPlot> getPlotMap(
      final List<NutmegPlot> plots) {

    final Map<String, NutmegPlot> retval = new HashMap<>();

    if (plots != null) {

      for (final NutmegPlot nutmegPlot : plots) {
        retval.put(nutmegPlot.getPlotname(), nutmegPlot);
      }
    }
    return retval;
  }
}