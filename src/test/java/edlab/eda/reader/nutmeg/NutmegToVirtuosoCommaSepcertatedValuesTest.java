package edlab.eda.reader.nutmeg;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

class NutmegToVirtuosoCommaSepcertatedValuesTest {

  @Test
  void test() throws IOException {

    final String path = "./src/test/resources/rc1/spectre/nutascii.raw";

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

    for (NutmegPlot plot : plots) {   
      plot.toVirtuosoCommaSeperatedValues();
    }
  }
}