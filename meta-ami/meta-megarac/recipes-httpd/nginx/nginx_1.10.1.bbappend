FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://nginx.conf \
			file://ca.pem \
			file://server.pem \
		"

inherit obmc-phosphor-systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "nginx.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install_append() {
    install -m 0644 ${WORKDIR}/ca.pem ${D}${sysconfdir}/nginx/ca.pem
    install -m 0644 ${WORKDIR}/server.pem ${D}${sysconfdir}/nginx/server.pem
}

CONFFILES_${PN} += " \
    ${sysconfdir}/nginx/ca.pem \
    ${sysconfdir}/nginx/server.pem \
    "