## AMI Open Redfish Makefile for installation.

PREFIX ?= /usr/local
INSTALL_MODULE= $(PREFIX)/sync-agent
INSTALL_BIN= $(PREFIX)/bin
APP_DIR= git
OUTPUT_DIR= output

all: clean build

build:
	# remove closed source library
	rm $(APP_DIR)/libs/event.lua
	cd $(APP_DIR); \
	for f in `find . -name "*.lua"`; do \
		mkdir -p ../$(OUTPUT_DIR)/`dirname "$$f"`; \
		luajit -b -s "$$f" ../$(OUTPUT_DIR)/"$$f"; \
	done

clean:
	rm -rf $(OUTPUT_DIR)

uninstall:
	@echo "==== Uninstalling sync agent. ===="
	rm -rf $(INSTALL_MODULE)
	rm -rf $(INSTALL_BIN)
	@echo "==== sync agent uninstalled.===="

install:
	@echo "==== Installing sync agent. ===="
	install -d $(INSTALL_MODULE)
	install -d $(INSTALL_MODULE)/oem
	install -d $(INSTALL_MODULE)/extensions
	install -d $(INSTALL_MODULE)/extensions/ldbus
	install -d $(INSTALL_BIN)

	cp ./ldbus_sensors.lua $(INSTALL_MODULE)/extensions/ldbus
	cp ./datetime.lua $(INSTALL_MODULE)/extensions/ldbus
	cp ./init-agent.lua $(INSTALL_MODULE)/extensions/ldbus
	cp ./redfish_group_map.lua $(INSTALL_MODULE)/extensions/ldbus
	cp ./redfish_intermediate_map.lua $(INSTALL_MODULE)/extensions/ldbus

	cp -R $(OUTPUT_DIR)/* $(INSTALL_MODULE)
	install -m 0755 $(APP_DIR)/sync-agent $(INSTALL_BIN)/sync-agent

	@echo "==== Successfully installed sync agent to $(PREFIX) ===="
