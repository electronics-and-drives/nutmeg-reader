package edlab.eda.reader.nutmeg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.text.translate.CharSequenceTranslator;

/**
 * Reader for a Nutmeg waveform file
 */
public abstract class NutReader {

  /**
   * List of plots
   */
  protected LinkedList<NutmegPlot> plots;

  /**
   * Nutmeg file
   */
  private File file;

  /**
   * Translator for wave names
   */
  protected final CharSequenceTranslator translator;

  /**
   * Flag that indicates whether a waveform is real or complex.
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
     * Undefined waveform.
     */
    NONE
  }

  protected static final String REAL_ID = "real";
  protected static final String COMPLEX_ID = "complex";

  /**
   * Create a new {@link edlab.eda.reader.nutmeg.NutReader NutReader}
   * 
   * @param file       Nutmeg waveform file
   * @param translator Translator for wave names
   */
  protected NutReader(final String file,
      final CharSequenceTranslator translator) {
    
    this.file = new File(file);

    if (this.file.exists()) {

      if (this.file.canRead()) {

        this.plots = new LinkedList<>();
      } else {
        System.err.println("File " + file + " not readable");
        this.file = null;
      }
    } else {
      System.err.println("File " + file + " does not exist");
      this.file = null;
    }

    this.translator = translator;
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
    return this.plots;
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in ASCII syntax.
   * 
   * @param file Path to waveform file
   * @return nutReader Reader for the corresponding waveform <code>null</code>
   *         when the file is not existing
   */
  public static NutReader getNutasciiReader(final String file) {
    return NutasciiReader.getNutReader(file);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in ASCII syntax.
   * 
   * @param file       Path to waveform file
   * @param translator Translator for wave names
   * @return nutReader Reader for the corresponding waveform <code>null</code>
   *         when the file is not existing
   */
  public static NutReader getNutasciiReader(final String file,
      final CharSequenceTranslator translator) {
    return NutasciiReader.getNutReader(file, translator);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file Path to waveform file
   * @return nutReader - Reader for the corresponding waveform,
   *         <code>null</code> when the file is not existing
   */
  public static NutReader getNutbinReader(final String file) {
    return NutbinReader.getNutReader(file);
  }

  /**
   * Creates a {@link edlab.eda.reader.nutmeg.NutReader NutReader} of a nutmeg
   * waveform file in binary syntax.
   * 
   * @param file       Path to waveform file
   * @param translator Translator for wave names
   * @return nutReader - Reader for the corresponding waveform,
   *         <code>null</code> when the file is not existing
   */
  public static NutReader getNutbinReader(final String file,
      final CharSequenceTranslator translator) {
    return NutbinReader.getNutReader(file, translator);
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