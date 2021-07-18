package edlab.eda.reader.nutmeg;

import java.util.List;
import java.util.Set;

public class NutmegTest {
  public static void main(String[] args) {

    // Create a new reader
    NutReader reader = NutReader
        .getNutasciiReader("./src/test/resources/rc1/spectre/nutascii.raw");

    // Read and parse the nutascii
    reader.read().parse();

    // Get all plots from the reader
    List<NutmegPlot> plots = reader.getPlots();
    
    // Get nutmeg plot from list
    NutmegPlot nutmegPlot = plots.get(0);

    // Get name of plot
    nutmegPlot.getPlotname();

    // Get number of points from plot
    nutmegPlot.getNoOfPoints();

    // Get number of variables from plot
    nutmegPlot.getNoOfWaves();
    
    // Get reference waveform
    nutmegPlot.getRefWave();
    
    // Get set of all waves from plot
    Set<String> waves = nutmegPlot.getWaves();

    // Check if wave with name "I" is part of plot
    nutmegPlot.containsWave("I");

    // Get unit of wave with name "I"
    nutmegPlot.getUnit("I");

    if (nutmegPlot instanceof NutmegRealPlot) {

      // Cast plot to real plot
      NutmegRealPlot nutmegRealPlot = (NutmegRealPlot) nutmegPlot;

      // Get wave of wave with name "I"
      @SuppressWarnings("unused")
      double[] wave = nutmegRealPlot.getWave("I");
    }
  }
}