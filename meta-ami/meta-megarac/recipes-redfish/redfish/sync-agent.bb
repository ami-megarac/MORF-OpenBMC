SUMMARY = "MORF Sync Agent"
SECTION = "redfish"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "5715d992508c230f2f3e9e6eb978eaa4d430cc5d"
SRC_URI = "git://github.com/ami-megarac/MORF-SyncAgent.git \
		   file://sync-agent.service \
		   file://Makefile \
		   file://init-agent.lua \
		   file://ldbus_sensors.lua \
		   file://datetime.lua \
		   file://redfish_group_map.lua \
		   file://redfish_intermediate_map.lua \
		   "

inherit obmc-phosphor-systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "sync-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

S = "${WORKDIR}/git"

BIN_DIR = "/usr/bin"

DEPENDS += " luajit-native redis luaposix33 bit32 lua-filesystem lua-dbus linotify"

B = "${WORKDIR}"

EXTRA_OEMAKE = "\
	'PREFIX=${D}/usr/local' \
	"

do_compile() {
	oe_runmake build
}

do_install() {
	oe_runmake install
}


FILES_${PN} += " \
	${prefix} \
	${BIN_DIR} \
	${BIN_DIR}/sync-agent \
	"
