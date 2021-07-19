from nutmeg_reader import NutmegReader

nutreader = NutmegReader()

bin_plots = nutreader.read_nutbin('../../../test/resources/rc2/nutbin.raw')

assert(len(plots) == 4)

asc_plots = nutreader.read_nutbin('../../../test/resources/rc2/nutascii.raw')
