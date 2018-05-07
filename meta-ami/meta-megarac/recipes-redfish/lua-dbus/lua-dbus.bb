DESCRIPTION = "Convenient dbus api in lua."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/dodo/lua-dbus"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "luajit lua5.1 ldbus"

SRC_URI = "git://github.com/dodo/lua-dbus.git;protocol=git \
		   file://ldbus_fixes.patch \
		   "

SRCREV = "cdef26d09aa61d7f1f175675040383f6ae0becbb"
S = "${WORKDIR}/git/"

LUA_MODULEDIR = "${D}/usr/local/share/lua/5.1"
LUAJIT_VERSION = "2.0.4"
LUAJIT_MODULEDIR = "${D}/usr/local/share/luajit-${LUAJIT_VERSION}"

do_install() {
	install -d ${LUA_MODULEDIR}/lua-dbus/awesome
	install -d ${LUAJIT_MODULEDIR}/lua-dbus/awesome

	cp ${S}/awesome/init.lua ${LUA_MODULEDIR}/lua-dbus/awesome
	cp ${S}/awesome/init.lua ${LUAJIT_MODULEDIR}/lua-dbus/awesome

	cp ${S}/awesome/dbus.lua ${LUA_MODULEDIR}/lua-dbus/awesome
	cp ${S}/awesome/dbus.lua ${LUAJIT_MODULEDIR}/lua-dbus/awesome

	cp ${S}/init.lua ${LUA_MODULEDIR}/lua-dbus
	cp ${S}/init.lua ${LUAJIT_MODULEDIR}/lua-dbus

	cp ${S}/interface.lua ${LUA_MODULEDIR}/lua-dbus
	cp ${S}/interface.lua ${LUAJIT_MODULEDIR}/lua-dbus
}


FILES_${PN} += "${prefix}"