from nutmeg_reader import NutmegReader

nutreader = NutmegReader()

def test_nutbin():
    bin_plots = nutreader.read_nutbin('../../../test/resources/rc2/nutbin.raw')
    assert(len(bin_plots) == 4)

def test_ascii():
    asc_plots = nutreader.read_nutascii('../../../test/resources/rc2/nutascii.raw')
    assert(len(bin_plots) == 4)


