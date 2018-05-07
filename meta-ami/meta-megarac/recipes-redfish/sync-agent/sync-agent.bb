SUMMARY = "MORF Sync Agent"
SECTION = "redfish"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRCREV = "${AUTOREV}"
SRC_URI = "git://git@megaracgit.ami.com/clarkk/MORF-SyncAgent.git;protocol=ssh \
		   file://sync-agent.service \
		   file://sync-agent-init.service \
		   file://sync-agent-rffns.service \
		   file://sync-agent-rfgrps.service \
		   "

S = "${WORKDIR}/git"

DEPENDS += " luajit-native redis luaposix bit32 lua-filesystem lua-dbus linotify"

EXTRA_OEMAKE = "\
	'PREFIX=${D}/usr/local' \
	'APP_DIR=${S}/app' \
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
SYSTEMD_SERVICE_${PN} = "sync-agent.service sync-agent-init.service sync-agent-rffns.service sync-agent-rfgrps.service"
SYSTEMD_AUTO_ENABLE = "enable"
