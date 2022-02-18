package edlab.eda.reader.nutmeg;

import java.util.List;
import java.util.Set;

public class NutmegTest2 {
  public static void main(final String[] args) {

    // Create a new reader
    final NutReader reader = NutReader
        .getNutbinReader("./src/test/resources/rc1/ngspice/nutbin.raw");

    // Read and parse the nutascii
    reader.read().parse();

    // Get all plots from the reader
    final List<NutmegPlot> plots = reader.getPlots();
    System.out.println(plots.size());
    
    // Get nutmeg plot from list
    final NutmegPlot nutmegPlot = plots.get(0);

    // Get name of plot
    System.out.println(nutmegPlot.getPlotname());

    // Get number of points from plot
    System.out.println(nutmegPlot.getNoOfPoints());

    // Get number of variables from plot
    System.out.println(nutmegPlot.getNoOfWaves());
    
    // Get reference waveform
    nutmegPlot.getRefWave();
    
    // Get set of all waves from plot
    @SuppressWarnings("unused")
    final
    Set<String> waves = nutmegPlot.getWaves();

    // Check if wave with name "I" is part of plot
    System.out.println(nutmegPlot.containsWave("v(i)"));

    // Get unit of wave with name "I"
    System.out.println(nutmegPlot.getUnit("v(i)"));

    if (nutmegPlot instanceof NutmegRealPlot) {

      // Cast plot to real plot
      final NutmegRealPlot nutmegRealPlot = (NutmegRealPlot) nutmegPlot;

      // Get wave of wave with name "I"
      @SuppressWarnings("unused")
      final
      double[] wave = nutmegRealPlot.getWave("I");
    }
  }
}