package edlab.eda.reader.nutmeg;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.text.translate.CharSequenceTranslator;

public class DefaultTranslator extends CharSequenceTranslator {

  @Override
  public int translate(CharSequence input, int index, Writer out)
      throws IOException {
    return 0;
  }
}
