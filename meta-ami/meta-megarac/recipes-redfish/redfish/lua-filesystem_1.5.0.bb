DESCRIPTION = "LuaFileSystem is a Lua library with set of functions related to file systems operations."
LICENSE = "MIT"
HOMEPAGE = "http://keplerproject.org/luafilesystem"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r1"

DEPENDS += "lua5.1"

SRC_URI = "git://github.com/keplerproject/luafilesystem.git;protocol=git \
           file://makefile.patch "

SRCREV = "ae5a05deec8a3737bd6972213b5495108b6566cc"
S = "${WORKDIR}/git/"

TARGET_CC_ARCH += "${LDFLAGS}"

TARGET_CFLAGS += "-fPIC"
CFLAGS += "-fPIC"
CFLAGS += "-I${STAGING_INCDIR}/lua5.1"

EXTRA_OEMAKE = "PREFIX='${D}/${prefix}' CFLAGS='${CFLAGS}' MYFLAGS='${CFLAGS} ${LDFLAGS}'"

do_install() {
        oe_runmake install PREFIX=${D}/${prefix}
        #install -d ${D}/${docdir}/${PN}-${PV}
        #install -m 0644 doc/us/* ${D}/${docdir}/${PN}-${PV}
        rm -rf ${D}/${prefix}/src
}

LUA_LIB_DIR =  "${libdir}/lua/5.1"

#PACKAGES = "${PN} ${PN}-dbg"
#FILES_${PN}-dbg = "${LUA_LIB_DIR}/.debug/lfs.so"
FILES_${PN} = "${LUA_LIB_DIR}/lfs.so"