from nutmeg_reader import NutReader

def test_nutbin():
    bin_file    = '../../test/resources/rc2/nutbin.raw'
    bin_reader  = NutReader.getNutbinReader(bin_file)
    bin_reader.read().parse();
    bin_plots   = list(bin_reader.getPlots())

    assert(len(bin_plots) == 4)

    assert(bin_plots[0].getNoOfPoints() == 1)
    assert(bin_plots[1].getNoOfPoints() == 51)
    assert(bin_plots[2].getNoOfPoints() == 51)
    assert(bin_plots[3].getNoOfPoints() == 56)

    assert(bin_plots[0].getNoOfWaves() == 6)
    assert(bin_plots[1].getNoOfWaves() == 6)
    assert(bin_plots[2].getNoOfWaves() == 5)
    assert(bin_plots[3].getNoOfWaves() == 6)

def test_ascii():
    asc_file    = '../../test/resources/rc2/nutascii.raw'
    asc_reader  = NutReader.getNutasciiReader(asc_file)
    asc_reader.read().parse();
    asc_plots   = list(asc_reader.getPlots())

    assert(len(asc_plots) == 4)

    assert(asc_plots[0].getNoOfPoints() == 1)
    assert(asc_plots[1].getNoOfPoints() == 51)
    assert(asc_plots[2].getNoOfPoints() == 51)
    assert(asc_plots[3].getNoOfPoints() == 56)

    assert(asc_plots[0].getNoOfWaves() == 6)
    assert(asc_plots[1].getNoOfWaves() == 6)
    assert(asc_plots[2].getNoOfWaves() == 5)
    assert(asc_plots[3].getNoOfWaves() == 6)
