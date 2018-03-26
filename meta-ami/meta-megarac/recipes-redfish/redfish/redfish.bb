SUMMARY = "MORF REST Server"
SECTION = "redfish"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "9fc9b28282ec1768d776e035e5a53f0e03a2c171"
SRC_URI = "git://github.com/ami-megarac/MORF-REST-Server.git \
		   file://redfish-server.service \
		   file://Makefile \
		   "

inherit obmc-phosphor-systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "redfish-server.service"
SYSTEMD_AUTO_ENABLE = "enable"

S = "${WORKDIR}/git"

BIN_DIR = "/usr/bin"

DEPENDS += " luajit-native turbolua redis luaposix33 bit32 lua-filesystem nginx sync-agent"

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
	${BIN_DIR}/redfish-server \
	"
