#
# SPDX-License-Identifier: MIT
# Copyright (c) 2020, 2022 Tano Systems LLC. All rights reserved.
#
PR_append = ".tano6"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/patches:${THISDIR}/${PN}/files:"

SRC_URI += "\
	file://wireguard_watchdog \
	file://wireguard.sh \
"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_install_append () {
	install -dm 0755 ${D}${bindir}
	install -m 0755 ${WORKDIR}/wireguard_watchdog ${D}${bindir}/

	install -dm 0755 ${D}${nonarch_base_libdir}/netifd/proto
	install -m 0755 ${WORKDIR}/wireguard.sh ${D}${nonarch_base_libdir}/netifd/proto/
}

FILES_${PN} += " \
	${sysconfdir} \
	${bindir} \
	${nonarch_base_libdir} \
"

DEPENDS += "${@oe.utils.conditional('TANOWRT_WIREGUARD_IN_KERNEL', '1', '', 'wireguard-module', d)}"
RDEPENDS_${PN} += "ubus jsonpath kernel-module-wireguard"
RDEPENDS_${PN}_remove = "wireguard-module"
