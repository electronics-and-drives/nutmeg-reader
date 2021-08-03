import os
import argparse
from pkg_resources import get_distribution
from .nutmeg_reader import nut2hdf, nut2csv

__name__    = 'nutmeg_reader'
__version__ = get_distribution(__name__).version

def __convert_nutmeg(file_type: str) -> int:
    parser = argparse.ArgumentParser(description=f'Convert Nutmeg to {file_type}.')

    parser.add_argument( '--version', action="version"
                       , version=f'{__name__}: {__version__}'
                       , )

    parser.add_argument( 'files', type=str, nargs='+'
                       , help='Path to Nutmeg input file'
                       , )

    parser.add_argument( '-o', '--override', action='store_true'
                       , help='Override target files if they exist.')

    if file_type == 'HDF':
        parser.add_argument( '-s', '--single', action='store_true'
                           , help='Put all plots into a single HDF file')

    args = parser.parse_args()

    for file_name in args.files:
        if file_type == 'HDF':
            nut2hdf(file_name, args.single, args.override)
        elif file_type == 'CSV':
            nut2csv(file_name, args.override)

    return 0

def nut2hdf() -> int:
    '''
    nut2hdf: Entry point for converting Nutmeg to HDF5.
        Returns exit code.
    '''
    return __convert_nutmeg('HDF')

def nut2csv() -> int:
    '''
    nut2csv: Entry point for converting Nutmeg to CSV.
        Returns exit code.
    '''
    return __convert_nutmeg('CSV')

def main():
    return 0

if __name__ == '__main__':
    sys.exit(main())
