package edlab.eda.reader.nutmeg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Reader for a Nutmeg waveform file.
 *
 */
public abstract class NutReader {

  /**
   * List of plots
   */
  protected LinkedList<NutmegPlot> plots;
  private File file;

  /**
   * Flag that indicated whether a waveform is real or complex.
   *
   */
  protected static enum FLAG {
    /**
     * Waveform is real-valued.
     */
    REAL,
    /**
     * Waveform is complex-valued.
     */
    COMPLEX,
    /**
     * Unified waveform.
     */
    NONE
  };

  protected static final String REAL_ID = "real";
  protected static final String COMPLEX_ID = "complex";

  /**
   * Create a new {@link edlab.eda.reader.nutmeg.NutReader NutReader}
   * 
   * @param file - nutmeg waveform file
   */
  protected NutReader(String file) {
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

  /**
   * Returns the path to the waveform file.
   * 
   * @return file - path to waveform file
   */
  public File getFile() {
    return this.file;
  }

  /**
   * Return a list of all plots.
   * 
   * @return list - list of all plots
   */
  public List<NutmegPlot> getPlots() {
    return plots;
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in ASCII syntax.
   * 
   * @param file - path to waveform file
   * @return nutReader - Reader for the corresponding waveform / null when the
   *         file is not existing
   */
  public static NutReader getNutasciiReader(String file) {
    return NutasciiReader.getNutReader(file);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file - path to waveform file
   * @return nutReader - Reader for the corresponding waveform, null when the
   *         file is not existing
   */
  public static NutReader getNutbinReader(String file) {
    return NutbinReader.getNutReader(file);
  }

  /**
   * Reads a waveform file into memory.
   * 
   * @return nutReader - Reader when reading of the file was successful, null
   *         otherwise.
   */
  public abstract NutReader read();

  /**
   * Parses a waveform file.
   * 
   * @return nutReader - Reader when reading of the file was successful, null
   *         otherwise.
   */
  public abstract NutReader parse();

}