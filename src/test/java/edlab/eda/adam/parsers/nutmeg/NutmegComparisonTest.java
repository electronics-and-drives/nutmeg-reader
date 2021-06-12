package edlab.eda.adam.parsers.nutmeg;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class NutmegComparisonTest {
  private static final double ERROR = 1e-6;

  @Test
  void test() {

    NutmegRealPlot ascciRealPlot;
    NutmegRealPlot binRealPlot;
    NutmegComplexPlot ascciComplexPlot;
    NutmegComplexPlot binComplexPlot;

    NutasciiReader asciiReader = new NutasciiReader(
        "./src/test/java/resources/rc/nutascii.raw");
    asciiReader.read().parse();

    NutbinReader binReader = new NutbinReader(
        "./src/test/java/resources/rc/nutbin.raw");
    binReader.read().parse();

    List<NutmegPlot> asciiPlots = asciiReader.getPlots();
    List<NutmegPlot> binPlots = binReader.getPlots();

    if (asciiPlots.size() != binPlots.size()) {
      fail("Unequal number of plots");
    }

    for (int i = 0; i < Math.max(asciiPlots.size(), binPlots.size()); i++) {

      if (asciiPlots.get(i) instanceof NutmegRealPlot) {
        ascciRealPlot = (NutmegRealPlot) asciiPlots.get(i);
        binRealPlot = (NutmegRealPlot) binPlots.get(i);

        for (String var : ascciRealPlot.getKeys()) {
          for (int j = 0; j < ascciRealPlot.getNoOfPoints(); j++) {

            assertEquals(ascciRealPlot.getData(var)[j],
                binRealPlot.getData(var)[j], ERROR);
          }
        }

      } else {

        ascciComplexPlot = (NutmegComplexPlot) asciiPlots.get(i);
        binComplexPlot = (NutmegComplexPlot) binPlots.get(i);

        for (String var : ascciComplexPlot.getKeys()) {
          for (int j = 0; j < ascciComplexPlot.getNoOfPoints(); j++) {
            assertEquals(ascciComplexPlot.getData(var)[j].getReal(),
                binComplexPlot.getData(var)[j].getReal(), ERROR);
            assertEquals(ascciComplexPlot.getData(var)[j].getImaginary(),
                binComplexPlot.getData(var)[j].getImaginary(), ERROR);
            
            
          }
        }
      }
    }
  }

}
