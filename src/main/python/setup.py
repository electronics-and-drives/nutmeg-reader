import setuptools
 
package_name = 'nutmeg_reader'

with open('README.md', 'r') as fh:
    long_description = fh.read()

with open('requirements.txt', 'r') as req:
    requirements = req.read().splitlines()
 
setuptools.setup( name                          = package_name
                , version                       = '1.0.1'
                , author                        = 'Yannick Uhlmann, Matthias Schweikardt'
                , author_email                  = 'yannick.uhlmann@reutlingen-university.de'
                , description                   = 'Nutmeg reader for python based on Java implementation.'
                , long_description              = long_description
                , long_description_content_type = 'text/markdown'
                , url                           = 'https://github.com/electronics-and-drives/nutmeg-reader'
                , packages                      = setuptools.find_packages()
                , classifiers                   = [ 'Development Status :: 2 :: Pre-Alpha'
                                                  , 'Programming Language :: Python :: 3'
                                                  , 'Operating System :: POSIX :: Linux' ]
                , python_requires               = '>=3.8'
                , install_requires              = requirements
                , entry_points                  = { 'console_scripts': [ 'nut2hdf = nutmeg_reader.__main__:pct' 
                                                                       , 'nut2csv = nutmeg_reader.__main__:prc' ]}
                , )
