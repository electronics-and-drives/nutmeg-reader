package edlab.eda.reader.nutmeg;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

class NutmegComparisonTest {
  private static final double ERROR = 1e-14;

  @Test
  void test() {

    compareWaves("./src/test/resources/rc1/spectre/nutascii.raw",
        "./src/test/resources/rc1/spectre/nutbin.raw");
    compareWaves("./src/test/resources/rc2/nutascii.raw",
        "./src/test/resources/rc2/nutbin.raw");
    
    compareWaves("./src/test/resources/op/nutascii.raw",
        "./src/test/resources/op/nutbin.raw");
  }

  private static void compareWaves(final String nutascii, final String nutbin) {
    NutmegRealPlot ascciRealPlot;
    NutmegRealPlot binRealPlot;
    NutmegComplexPlot ascciComplexPlot;
    NutmegComplexPlot binComplexPlot;

    final NutReader asciiReader = NutReader.getNutasciiReader(nutascii);
    asciiReader.read().parse();

    final NutReader binReader = NutReader.getNutbinReader(nutbin);
    binReader.read().parse();

    final List<NutmegPlot> asciiPlots = asciiReader.getPlots();
    final List<NutmegPlot> binPlots = binReader.getPlots();

    if (asciiPlots.size() != binPlots.size()) {
      System.out.println(asciiPlots.size() + "-" + binPlots.size());
      fail("Unequal number of plots");
    }

    double a, b, c, d;

    for (int i = 0; i < Math.max(asciiPlots.size(), binPlots.size()); i++) {

      if (asciiPlots.get(i) instanceof NutmegRealPlot) {

        ascciRealPlot = (NutmegRealPlot) asciiPlots.get(i);
        binRealPlot = (NutmegRealPlot) binPlots.get(i);

        for (final String var : ascciRealPlot.getWaves()) {
          for (int j = 0; j < ascciRealPlot.getNoOfPoints(); j++) {

            a = ascciRealPlot.getWave(var)[j];
            b = binRealPlot.getWave(var)[j];
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if ((c / d) >= ERROR) {
              fail("Error in plot=\"" + ascciRealPlot.getPlotname()
                  + "\", wave=\"" + var + "\" and index=" + j
                  + ".\nRelative error between ascii=" + a + " and binary=" + b
                  + "is greater than " + ERROR);
            }
          }
        }

      } else {

        ascciComplexPlot = (NutmegComplexPlot) asciiPlots.get(i);
        binComplexPlot = (NutmegComplexPlot) binPlots.get(i);

        for (final String var : ascciComplexPlot.getWaves()) {
          for (int j = 0; j < ascciComplexPlot.getNoOfPoints(); j++) {

            a = ascciComplexPlot.getWave(var)[j].getReal();
            b = binComplexPlot.getWave(var)[j].getReal();
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if ((c / d) >= ERROR) {
              fail("Error in plot=\"" + ascciComplexPlot.getPlotname()
                  + "\", wave=\"" + var + "\" and index=" + j
                  + ".\nRelative error between ascii.real=" + a
                  + " and binary.real=" + b + "is greater than " + ERROR);
            }

            a = ascciComplexPlot.getWave(var)[j].getImaginary();
            b = binComplexPlot.getWave(var)[j].getImaginary();
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if ((c / d) >= ERROR) {
              fail("Error in plot=\"" + ascciComplexPlot.getPlotname()
                  + "\", wave=\"" + var + "\" and index=" + j
                  + ".\nRelative error between ascii.imag=" + a
                  + " and binary.imag=" + b + "is greater than " + ERROR);
            }
          }
        }
      }
    }
  }
}