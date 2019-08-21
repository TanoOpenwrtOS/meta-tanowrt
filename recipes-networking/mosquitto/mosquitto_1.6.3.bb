#
PR = "tano0.${INC_PR}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/patches:${THISDIR}/${PN}/files:"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=62ddc846179e908dc0c8efec4a42ef20 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
                    file://epl-v10;md5=8d383c379e91d20ba18a52c3e7d3a979 \
                    file://notice.html;md5=a00d6f9ab542be7babc2d8b80d5d2a4c \
"

SRC_URI = "http://mosquitto.org/files/source/mosquitto-${PV}.tar.gz"
SRC_URI[md5sum] = "bd19cf124459ee26040912786ef177f7"
SRC_URI[sha256sum] = "9ef5cc75f4fe31d7bf50654ddf4728ad9e1ae2e5609a4b42ecbbcb4a209ed17e"

require mosquitto.inc
