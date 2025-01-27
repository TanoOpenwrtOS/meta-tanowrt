#
# SPDX-License-Identifier: MIT
# Copyright (c) 2021-2022 Tano Systems LLC. All rights reserved.
#

PR_append_evb-ksz-sd = ".atmel0"
PR_append_at91-sama5d2-xplained-sd = ".atmel0"
PR_append_at91-sama5d3-xplained-sd = ".atmel0"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/files:"

PREINIT_FILES = ""
PREINIT_FILES_evb-ksz-sd = "file://preinit/79_mount_boot"
PREINIT_FILES_at91-sama5d2-xplained-sd = "file://preinit/79_mount_boot"
PREINIT_FILES_at91-sama5d3-xplained-sd = "file://preinit/79_mount_boot"

SRC_URI_append = "\
	${PREINIT_FILES} \
"

do_install_append() {
	if [ -n "{PREINIT_FILES}" ]; then
		install -d ${D}${base_libdir}/preinit
		for f in ${PREINIT_FILES}; do
			FILEPATH="${f#file://}"
			install -m 0644 ${WORKDIR}/${FILEPATH} ${D}${TANOWRT_PATH_PREINIT}/
		done
	fi
}
