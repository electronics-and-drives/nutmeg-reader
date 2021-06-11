package edlab.eda.adam.parsers.nutmeg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class NutReader {

  protected LinkedList<NutmegPlot> plots;
  protected File file;

  protected static enum FLAG {
    REAL, COMPLEX, NONE
  };

  protected static final String REAL_ID = "real";
  protected static final String COMPLEX_ID = "complex";

  public NutReader(String file) {

    this.file = new File(file);

    if (this.file.exists()) {

      if (this.file.canRead()) {

        this.plots = new LinkedList<NutmegPlot>();
      } else {
        System.err.println("File " + file + " not readable");
        this.file = null;
      }
    } else {
      System.err.println("File " + file + " does not exist");
      this.file = null;
    }
  }

  public abstract NutReader read();

  public abstract NutReader parse();

  public List<NutmegPlot> getPlots() {
    return plots;
  }
}