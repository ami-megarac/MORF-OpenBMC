#
# This file was derived from the 'Hello World!' example recipe in the
# Yocto Project Development Manual.
#

SUMMARY = "lua-bit32"
SECTION = "lua-bit32"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

HOMEPAGE = "http://lua.org/work/"

SRC_URI[md5sum] = "de79cc3fc3883ba98823afa45915ef9f"
SRC_URI = "https://raw.githubusercontent.com/hishamhm/lua-compat-5.2/bitlib-5.2.2/lbitlib.c"

S = "${WORKDIR}"

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

DEPENDS += "lua-native lua5.1"

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
# "

EXTRA_OEMAKEINST = "\
    'DESTDIR=${D}/usr/local' \
    'PREFIX=${D}/usr/local' \
    'INSTALL_LIB=${D}${libdir}' \
    'INSTALL_BIN=${D}${bindir}' \
    'INSTALL_MAN=${D}${mandir}/man1' \
"

CFLAGS += "-I${STAGING_INCDIR}/lua5.1"


do_compile() {
	     ${CC} -c -Wall -Werror -fpic ${CFLAGS} lbitlib.c -o bit32.o
         ${CC} -shared -o bit32.so bit32.o
}

do_install() {
	     #oe_runmake ${EXTRA_OEMAKEINST} install
         mkdir -p ${D}/usr/local/lib/lua/5.1/
         cp -R ${S}/bit32.so ${D}/usr/local/lib/lua/5.1/
}


FILES_${PN} += "${prefix}"