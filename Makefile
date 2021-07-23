# Small installer for Java and/or Python

define mvnpath
	$(1) := $(shell mvn -q -Dexec.executable=echo -Dexec.args="\$${settings.localRepository}" --non-recursive exec:exec)
endef

define jarpath
	$(eval $(call mvnpath, mvn_path))
	groupId=$(shell mvn -q -Dexec.executable=echo -Dexec.args="\$${project.groupId}" --non-recursive exec:exec | tr '.' '/')
	artifactId=$(shell mvn -q -Dexec.executable=echo -Dexec.args="\$${project.artifactId}" --non-recursive exec:exec)
	version=$(shell mvn -q -Dexec.executable=echo -Dexec.args="\$${project.version}" --non-recursive exec:exec)
	descriptorRef="jar-with-dependencies"
	$(1) := "$(mvn_path)/$$(groupId)/$$(artifactId)/$$(version)/$$(artifactId)-$$(version)-$$(descriptorRef).jar"
endef

all: java python

python: java
	cd ./src/main/python && pip install . --use-feature=in-tree-build

java:
	mvn install

mvn-path:
	$(eval $(call mvnpath, mvn_path))
	@echo ${mvn_path}

jar-path:
	$(eval $(call jarpath, jar_path))
	@echo ${jar_path}
