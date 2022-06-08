package edlab.eda.reader.nutmeg;

public class Test {

  public static void main(final String[] args) {

    // Create a new reader
    final NutReader reader = NutReader
        .getNutbinReader("./src/test/resources/rc1/spectre/nutbin.raw");

    // Read and parse the nutascii
    reader.read();

    final long start = System.currentTimeMillis();
    reader.parse();

    final long finish = System.currentTimeMillis();

    System.err.println(finish - start);
    
    System.out.println(reader.getPlots().size());

  }
}