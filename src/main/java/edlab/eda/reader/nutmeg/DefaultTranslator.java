package edlab.eda.reader.nutmeg;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.text.translate.CharSequenceTranslator;

/**
 * Default translator for waveform names
 */
final class DefaultTranslator extends CharSequenceTranslator {

  @Override
  public int translate(final CharSequence input, final int index,
      final Writer out) throws IOException {
    return 0;
  }
}