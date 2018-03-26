FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " file://redis-server.service "

SYSTEMD_PACKAGES = "${PN}"

SYSTEMD_SERVICE_${PN} = "redis-server.service"

inherit obmc-phosphor-systemd

do_install_append() {
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${WORKDIR}/redis-server.service ${D}${sysconfdir}/systemd/system
}

