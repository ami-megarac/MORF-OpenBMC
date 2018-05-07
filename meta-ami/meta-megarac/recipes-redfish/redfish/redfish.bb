SUMMARY = "MORF REST Server"
SECTION = "redfish"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "${AUTOREV}"
SRC_URI = "git://git@megaracgit.ami.com/clarkk/MORF-REST-Server.git;protocol=ssh \
		   file://redfish-server.service \
		   file://redfish-db-init.service \
		   "

S = "${WORKDIR}/git"

DEPENDS += " luajit-native turbolua redis luaposix bit32 lua-filesystem nginx sync-agent"

EXTRA_OEMAKE = "\
	'PREFIX=${D}/usr/local' \
	'GIT_DIR=${S}' \
	'OUTPUT_DIR=${WORKDIR}/output' \
	"

do_compile() {
	oe_runmake build
}

do_install() {
	oe_runmake install
}

FILES_${PN} += "${prefix}"

inherit obmc-phosphor-systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "redfish-server.service redfish-db-init.service"
SYSTEMD_AUTO_ENABLE = "enable"
