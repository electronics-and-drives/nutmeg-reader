package edlab.eda.adam.parsers.nutmeg;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edlab.eda.parsers.nutmeg.NutReader;
import edlab.eda.parsers.nutmeg.NutmegComplexPlot;
import edlab.eda.parsers.nutmeg.NutmegPlot;
import edlab.eda.parsers.nutmeg.NutmegRealPlot;

class NutmegComparisonTest {
  private static final double ERROR = 1e-14;

  @Test
  void test() {

    NutmegRealPlot ascciRealPlot;
    NutmegRealPlot binRealPlot;
    NutmegComplexPlot ascciComplexPlot;
    NutmegComplexPlot binComplexPlot;

    NutReader asciiReader = NutReader
        .getNutasciiReader("./src/test/java/resources/rc/nutascii.raw");
    asciiReader.read().parse();

    NutReader binReader = NutReader
        .getNutbinReader("./src/test/java/resources/rc/nutbin.raw");
    binReader.read().parse();

    List<NutmegPlot> asciiPlots = asciiReader.getPlots();
    List<NutmegPlot> binPlots = binReader.getPlots();

    if (asciiPlots.size() != binPlots.size()) {
      fail("Unequal number of plots");
    }

    double a, b, c, d;

    for (int i = 0; i < Math.max(asciiPlots.size(), binPlots.size()); i++) {

      if (asciiPlots.get(i) instanceof NutmegRealPlot) {

        ascciRealPlot = (NutmegRealPlot) asciiPlots.get(i);
        binRealPlot = (NutmegRealPlot) binPlots.get(i);

        for (String var : ascciRealPlot.getKeys()) {
          for (int j = 0; j < ascciRealPlot.getNoOfPoints(); j++) {

            a = ascciRealPlot.getData(var)[j];
            b = binRealPlot.getData(var)[j];
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if (c / d >= ERROR) {
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

        for (String var : ascciComplexPlot.getKeys()) {
          for (int j = 0; j < ascciComplexPlot.getNoOfPoints(); j++) {

            a = ascciComplexPlot.getData(var)[j].getReal();
            b = binComplexPlot.getData(var)[j].getReal();
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if (c / d >= ERROR) {
              fail("Error in plot=\"" + ascciComplexPlot.getPlotname()
                  + "\", wave=\"" + var + "\" and index=" + j
                  + ".\nRelative error between ascii.real=" + a
                  + " and binary.real=" + b + "is greater than " + ERROR);
            }

            a = ascciComplexPlot.getData(var)[j].getImaginary();
            b = binComplexPlot.getData(var)[j].getImaginary();
            c = Math.abs(a - b);
            d = Math.max(Math.max(Math.abs(a), Math.abs(b)), 1.0);

            if (c / d >= ERROR) {
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
