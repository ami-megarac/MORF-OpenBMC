#
# This file was derived from the 'Hello World!' example recipe in the
# Yocto Project Development Manual.
#

SUMMARY = "Turbo lua framework"
SECTION = "turbolua"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

HOMEPAGE = "http://www.turbolua.org"

SRCREV = "f43bffa558742548b4bba1f39ef2771d97d7041c"
SRC_URI = "git://github.com/kernelsauce/turbo.git \
           file://soname_fix.patch \
           "

S = "${WORKDIR}/git"

inherit pkgconfig binconfig

BBCLASSEXTEND = "native"

BUILD_CC_ARCH_append_powerpc = ' -m32'
BUILD_CC_ARCH_append_x86 = ' -m32'
BUILD_CC_ARCH_append_arm = ' -m32'

LUA_TARGET_OS = "Unknown"
LUA_TARGET_OS_darwin = "Darwin"
LUA_TARGET_OS_linux = "Linux"
LUA_TARGET_OS_linux-gnueabi = "Linux"
LUA_TARGET_OS_mingw32 = "Windows"

TARGET_CC_ARCH += "${LDFLAGS}" 

PACKAGECONFIG ??= "zlib openssl"

PACKAGECONFIG[zlib] = "--shared-zlib,,zlib"
PACKAGECONFIG[openssl] = "--shared-openssl,,openssl"

TARGET_CFLAGS += "-fpic"
CFLAGS += "-fpic"

EXTRA_OEMAKE = "\
    Q= E='@:' \
    \
    CCOPT= CCOPT_x86= \
    \
    'TARGET_SYS=${LUA_TARGET_OS}' \
    \
    'CC=${CC}' \
    'TARGET_AR=${AR} rcus' \
    'TARGET_CFLAGS=${CFLAGS}' \
    'TARGET_LDFLAGS=${LDFLAGS}' \
    'TARGET_SHLDFLAGS=${LDFLAGS}' \
    'HOST_CC=${BUILD_CC}' \
    'HOST_CFLAGS=${BUILD_CFLAGS}' \
    'HOST_LDFLAGS=${BUILD_LDFLAGS}' \
    \
    'PREFIX=${prefix}' \
    'MULTILIB=${baselib}' \
"

DEPENDS += "openssl luajit lua-socket"

EXTRA_OEMAKEINST = "\
    'DESTDIR=${D}/usr/local' \
    'PREFIX=${D}/usr/local' \
    'INSTALL_LIB=${D}${libdir}' \
    'INSTALL_BIN=${D}${bindir}' \
    'INSTALL_MAN=${D}${mandir}/man1' \
"

do_compile() {
	     #${CC} helloworld.c -o helloworld
		 oe_runmake
}

do_install() {
	     oe_runmake ${EXTRA_OEMAKEINST} install
}


FILES_${PN} += "${prefix}"