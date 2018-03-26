DESCRIPTION = "hello-world-mod tests the module.bbclass mechanism."
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

inherit module

PR = "r0"
PV = "0.1"

SRC_URI = "file://Makefile \
           file://hello.c \
          "

S = "${WORKDIR}"
