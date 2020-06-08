# Copyright (C) 2020 Anton Kikin <a.kikin@tano-systems.com>

PR = "tano1"
SUMMARY = "WireGuard VPN support packages"
DESCRIPTION = "The set of packages required for a WireGuard VPN support"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

do_package[vardeps] += "TANOWRT_LUCI_ENABLE"

RDEPENDS_${PN} = "\
	${@oe.utils.conditional('TANOWRT_LUCI_ENABLE', '1', 'luci-proto-wireguard', '', d)} \
	wireguard-module \
	wireguard-tools \
"