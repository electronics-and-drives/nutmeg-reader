# Small installer for different Targets

all: java python

python: java
	cd ./src/main/python && pip install . --use-feature=in-tree-build

java:
	mvn install
