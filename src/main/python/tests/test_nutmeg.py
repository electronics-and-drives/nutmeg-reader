from nutmeg_reader import NutmegReader

nutreader = NutmegReader()

def test_nutbin():
    bin_plots = nutreader.read_nutbin('../../../test/resources/rc2/nutbin.raw')

    assert(len(bin_plots) == 4)

    assert(bin_plots[0].num_of_points == 1)
    assert(bin_plots[1].num_of_points == 51)
    assert(bin_plots[2].num_of_points == 51)
    assert(bin_plots[3].num_of_points == 56)

    assert(bin_plots[0].num_of_waves == 6)
    assert(bin_plots[1].num_of_waves == 6)
    assert(bin_plots[2].num_of_waves == 5)
    assert(bin_plots[3].num_of_waves == 6)

def test_ascii():
    asc_plots = nutreader.read_nutascii('../../../test/resources/rc2/nutascii.raw')

    assert(len(asc_plots) == 4)

    assert(asc_plots[0].num_of_points == 1)
    assert(asc_plots[1].num_of_points == 51)
    assert(asc_plots[2].num_of_points == 51)
    assert(asc_plots[3].num_of_points == 56)

    assert(bin_plots[0].num_of_waves == 6)
    assert(bin_plots[1].num_of_waves == 6)
    assert(bin_plots[2].num_of_waves == 5)
    assert(bin_plots[3].num_of_waves == 6)
