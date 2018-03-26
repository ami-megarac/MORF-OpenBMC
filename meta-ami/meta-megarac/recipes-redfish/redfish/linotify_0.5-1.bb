DESCRIPTION = "Inotify bindings for Lua."
LICENSE = "MIT"
HOMEPAGE = "http://hoelz.ro/projects/linotify"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "luajit lua5.1 inotify-tools"

SRC_URI = "git://github.com/hoelzro/linotify.git;protocol=git \
		   file://disable-strip.patch \
		   "

SRCREV = "a56913e9c0922befb65227a00cf69c2e8052de1a"
S = "${WORKDIR}/git/"

inherit pkgconfig

TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OEMAKE = " \ 
	DESTDIR='${D}' \
	"

do_install() {
        oe_runmake install
}


FILES_${PN} += "${prefix}"