import setuptools
 
package_name = 'nutmeg-reader'

with open('README.md', 'r') as fh:
    long_description = fh.read()

with open('requirements.txt', 'r') as req:
    requirements = req.read().splitlines()
 
setuptools.setup( name                          = package_name
                , version                       = '0.0.1'
                , author                        = 'FIXME'
                , author_email                  = 'FIXME'
                , description                   = 'FIXME'
                , long_description              = long_description
                , long_description_content_type = 'text/markdown'
                , url                           = 'FIXME'
                , packages                      = setuptools.find_packages()
                , classifiers                   = [ 'Development Status :: 2 :: Pre-Alpha'
                                                  , 'Programming Language :: Python :: 3'
                                                  , 'Operating System :: POSIX :: Linux' ]
                , python_requires               = '>=3.8'
                , install_requires              = requirements
                , entry_points                  = { 'console_scripts': [ 'FIXME' ]}
                , package_data                  = { '': ['*.hy', '__pycache__/*']}
                , )
