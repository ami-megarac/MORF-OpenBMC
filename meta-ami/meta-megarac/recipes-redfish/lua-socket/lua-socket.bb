DESCRIPTION = "Networking extension library for Lua"
HOMEPAGE = "https://github.com/diegonehab/luasocket"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab6706baf6d39a6b0fa2613a3b0831e7"

DEPENDS += " lua5.1"

SRCREV = "d1ec29be7f982db75864155dd61a058902e1cae2"
SRC_URI = "git://github.com/diegonehab/luasocket.git \
           file://0001-allow-overrides-for-DESTDIR-CC_linux-LD_linux-LDFLAG.patch \
           file://0002-serial-LUASOCKET_API-fix.patch \
           file://0003-build-unix-and-serial-with-compatibility-fixes.patch \
          "

TARGET_CC_ARCH += "${LDFLAGS}"
TARGET_CFLAGS += "-fpic"
CFLAGS += "-fpic"

S = "${WORKDIR}/git"

CFLAGS += "-I${STAGING_INCDIR}/lua5.1"
EXTRA_OEMAKE += "PLAT=linux CFLAGS+='-I${STAGING_INCDIR}/lua5.1' LUAINC_linux_base=${STAGING_INCDIR} LUAPREFIX_linux=${prefix} DESTDIR=${D} \
CC_linux='${CC}' LD_linux='${CC}' LUAV=5.1"

do_install() {
	oe_runmake install-unix
}

FILES_SOLIBSDEV = ""

FILES_${PN}  += "${libdir}/* ${datadir}/lua/5.*/"
FILES_${PN}-dbg  += "${libdir}/lua/5.*/.debug ${libdir}/lua/5.*/*/.debug"
