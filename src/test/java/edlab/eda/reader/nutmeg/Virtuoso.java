package edlab.eda.reader.nutmeg;

import java.util.List;

public class Virtuoso {

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    final NutReader reader = NutReader
        .getNutasciiReader("./src/test/resources/rc1/spectre/nutascii.raw");

    reader.read().parse();

    List<NutmegPlot> plots = reader.getPlots();

    System.err.println(plots.get(2).toVirtuosoCommaSeperatedValues());
  }
}
