## AMI Open Redfish Makefile for installation.

PREFIX ?= /usr/local
INSTALL_MODULE= $(PREFIX)/redfish
INSTALL_BIN= $(PREFIX)/bin
GIT_DIR= git
APP_DIR=$(GIT_DIR)/app
OUTPUT_DIR= output

all: clean build

build:
	cd $(APP_DIR); \
	for f in `find . -name "*.lua"`; do \
		mkdir -p ../../$(OUTPUT_DIR)/`dirname "$$f"`; \
		luajit -b -s "$$f" ../../$(OUTPUT_DIR)/"$$f"; \
	done

clean:
	rm -rf $(OUTPUT_DIR)

uninstall:
	@echo "==== Uninstalling Redfish server. ===="
	rm -rf $(INSTALL_MODULE)
	rm -rf $(INSTALL_BIN)
	@echo "==== Redfish server uninstalled.===="

install:
	@echo "==== Installing Redfish server. ===="
	install -d $(INSTALL_MODULE)
	install -d $(INSTALL_MODULE)/oem
	install -d $(INSTALL_MODULE)/extensions/constants
	install -d $(INSTALL_MODULE)/extensions/routes
	install -d $(INSTALL_BIN)

	cp -R $(OUTPUT_DIR)/* $(INSTALL_MODULE)
	cp -R $(GIT_DIR)/db_init $(INSTALL_MODULE)/db_init
	install -m 0755 $(GIT_DIR)/redfish-server $(INSTALL_BIN)/redfish-server

	@echo "==== Successfully installed Redfish server to $(PREFIX) ===="
