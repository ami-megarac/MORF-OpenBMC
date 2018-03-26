DESCRIPTION = "batterybacked-mod tests the module.bbclass mechanism."
LICENSE = "CLOSED"

inherit module

PR = "r0"
PV = "0.1"

SRC_URI = "file://Makefile \
           file://ast_batterybacked.c \
           file://ast_batterybacked.h \
           file://ast_batterybacked_ioctl.h \
          "

S = "${WORKDIR}"
