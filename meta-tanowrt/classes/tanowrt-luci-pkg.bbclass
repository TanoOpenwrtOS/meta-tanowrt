#
# SPDX-License-Identifier: MIT
#
# LuCI package class
#
# Copyright (c) 2018, 2020-2021 Tano Systems LLC. All rights reserved.
# Anton Kikin <a.kikin@tano-systems.com>
#

inherit tanowrt-luci

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

RDEPENDS_${PN} += "luci-base"

FILES_${PN} += "${LUCI_INSTALL_LUASRC_PATH}"
FILES_${PN} += "${LUCI_INSTALL_HTDOCS_PATH}"
FILES_${PN} += "${LUCI_INSTALL_ROOT_PATH}"

LUCI_PKG_SRC ?= "${S}"

LUCI_PKG_EXECUTABLE ?= "\
	${D}${LUCI_INSTALL_ROOT_PATH}${sysconfdir}/uci-defaults/* \
	${D}${LUCI_INSTALL_ROOT_PATH}${sysconfdir}/init.d/* \
	${D}/usr/libexec/rpcd/* \
	${D}/usr/libexec/* \
	${D}/usr/bin/* \
"

install_files() {
	DIRSRC="$1"
	DIRDST="$2"

	cd ${DIRSRC}
	find * -type f -exec install -Dm 644 "{}" "${DIRDST}/{}" \;
	find * -type l -exec cp -d "{}" "${DIRDST}/{}" \;
}

do_install_append() {
	# Install luasrc
	if [ -d "${LUCI_PKG_SRC}/luasrc" ]; then
		install_files "${LUCI_PKG_SRC}/luasrc" "${D}${LUCI_INSTALL_LUASRC_PATH}"
	fi

	# Install htdocs
	if [ -d "${LUCI_PKG_SRC}/htdocs" ]; then
		install_files "${LUCI_PKG_SRC}/htdocs" "${D}${LUCI_INSTALL_HTDOCS_PATH}"
	fi

	# Install root files
	if [ -d "${LUCI_PKG_SRC}/root" ]; then
		install_files "${LUCI_PKG_SRC}/root" "${D}${LUCI_INSTALL_ROOT_PATH}"
	fi

	# Executable files
	for p in "${LUCI_PKG_EXECUTABLE}"; do
		for f in ${p}; do
			if [ -e "$f" ]; then
				chmod 0755 "$f"
			fi
		done
	done
}
