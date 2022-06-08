package edlab.eda.reader.nutmeg;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

public class NutasciiTest {

  private static final String[] PLOTNAMES = { "DC Analysis `dc1'",
      "DC Analysis `dc2': VI = (0 -> 10)",
      "AC Analysis `ac': freq = (1 Hz -> 1 GHz)",
      "Transient Analysis `tran': time = (0 s -> 5 ns)" };
  private static final int[] NUM_OF_POINTS = { 1, 51, 51, 56 };
  private static final int[] NUM_OF_WAVES = { 5, 5, 5, 5 };

  @Test
  void test() {
    this.readWave("./src/test/resources/rc1/spectre/nutascii.raw");
  }

  private void readWave(final String path) {
    // Create a new reader
    final NutReader reader = NutReader
        .getNutasciiReader("./src/test/resources/rc1/spectre/nutascii.raw");

    if (reader.read() == null) {
      fail("Unable to read " + path);
    }

    if (reader.parse() == null) {
      fail("Unable to parse " + path);
    }

    final List<NutmegPlot> plots = reader.getPlots();

    if (plots.size() != 4) {
      fail("Number of plots mismatch");
    }

    NutmegPlot nutmegPlot;

    for (int i = 0; i < PLOTNAMES.length; i++) {

      nutmegPlot = plots.get(i);

      if (!PLOTNAMES[i].equals(nutmegPlot.getPlotname())) {
        fail("Plotname: " + PLOTNAMES[i] + " mismatch with "
            + nutmegPlot.getPlotname());
      }

      if (NUM_OF_WAVES[i] != nutmegPlot.getNoOfWaves()) {
        fail("Num of waves: " + NUM_OF_WAVES[i] + " mismatch with "
            + nutmegPlot.getNoOfWaves());
      }

      if (NUM_OF_POINTS[i] != nutmegPlot.getNoOfPoints()) {
        fail("Num of points: " + NUM_OF_POINTS[i] + " mismatch with "
            + nutmegPlot.getNoOfPoints());
      }
    }
  }
}