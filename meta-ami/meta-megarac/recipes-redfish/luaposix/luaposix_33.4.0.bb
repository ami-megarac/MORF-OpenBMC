# Copyright (C) 2016 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Lua bindings for POSIX"

DESCRIPTION = "A library binding various POSIX APIs.\
    POSIX is the IEEE Portable Operating System Interface standard.\
    luaposix is based on lposix.\
"
HOMEPAGE = "http://luaposix.github.io/luaposix/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7dd2aad04bb7ca212e69127ba8d58f9f"
SECTION = "libs"

SRCREV = "0e56d7ed756509eb6a4806bd31c088e14b74a853"
SRC_URI = "git://github.com/luaposix/luaposix \
		   file://mkdir_fix.patch \
		   "

S = "${WORKDIR}/git"

B = "${S}"

inherit autotools-brokensep pkgconfig binconfig

DEPENDS += "help2man-native  lua5.1 bit32"

TARGET_CC_ARCH += "${LDFLAGS}" 

#export LUA = "lua5.1"
#CFLAGS += "-I${STAGING_INCDIR}/lua5.1"

# EXTRA_OEMAKE = "\
#     Q= E='@:' \
#     \
#     CCOPT= CCOPT_x86= CFLAGS= LDFLAGS= TARGET_STRIP='@:' \
#     \
#     'TARGET_SYS=${LUA_TARGET_OS}' \
#     \
#     'CC=${CC}' \
#     'TARGET_AR=${AR} rcus' \
#     'TARGET_CFLAGS=${CFLAGS}' \
#     'TARGET_LDFLAGS=${LDFLAGS}' \
#     'TARGET_SHLDFLAGS=${LDFLAGS}' \
#     'HOST_CC=${BUILD_CC}' \
#     'HOST_CFLAGS=${BUILD_CFLAGS}' \
#     'HOST_LDFLAGS=${BUILD_LDFLAGS}' \
#     \
#     'PREFIX=${prefix}' \
#     'MULTILIB=${baselib}' \
# 	'LUAV=5.1' \
# "

CFLAGS += "-I${STAGING_INCDIR}/lua5.1"
EXTRA_OEMAKE += "PLAT=linux LUAINC_linux_base=${STAGING_INCDIR} LUAPREFIX_linux=${prefix} DESTDIR=${D} \
CC_linux='${CC}' LD_linux='${CC}' LUAV=5.1"

do_configure() {
	#cd ${S}
	#luarocks --local install ldoc
	#autoconf ./configure.ac
	${S}/bootstrap --skip-rock-checks
	oe_runconf
	#./configure
}

EXTRA_OEMAKEINST = "\
	PLAT=linux luadir=${datadir}/lua/5.1 luaexecdir=${libdir}/lua/5.1 \
    'DESTDIR=${D}' \
    'INSTALL_BIN=${D}${bindir}' \
    'INSTALL_INC=${D}${includedir}/luajit-$(MAJVER).$(MINVER)' \
    'INSTALL_MAN=${D}${mandir}/man1' \
	'LUAV=5.1' \
"

do_install() {
	oe_runmake ${EXTRA_OEMAKEINST} install
	#cp -R ${D}/usre/chandru/Projects/openbmc/openbmc/build/tmp/sysroots/x86_64-linux/usr/* ${D}/usr/
	#rm -rf ${D}/usre
}

FILES_${PN} += "${prefix}"