DESCRIPTION = "A Lua library to access dbus."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/daurnimator/ldbus"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "luajit lua5.1 dbus"

SRC_URI = "git://github.com/daurnimator/ldbus.git;protocol=git"

SRCREV = "f4a1464e915a2313c80fb40c5c40b0bee7583677"
S = "${WORKDIR}/git/"
COMPAT53 = "${S}vendor/compat-5.3/c-api"

inherit pkgconfig

TARGET_CC_ARCH += "${LDFLAGS}"

TARGET_CFLAGS += "-fpic"

CFLAGS += "-Wall -Wextra --pedantic -Wno-long-long"
CFLAGS += "`pkg-config --cflags dbus-1`"
CFLAGS += "-fPIC"
CFLAGS += "-I${STAGING_INCDIR}/lua5.1 -I${COMPAT53}"

LUA_LIBDIR =  "${libdir}/lua/5.1"

EXTRA_OEMAKE = "PREFIX='${D}/${prefix}' CFLAGS='${CFLAGS}' MYFLAGS='${CFLAGS} ${LDFLAGS}' LUA_LIBDIR='${D}${LUA_LIBDIR}'"

do_install() {
		cd ${S}/src
        oe_runmake install
}


FILES_${PN} += "${LUA_LIBDIR}/ldbus.so"