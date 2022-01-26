#
# SPDX-License-Identifier: MIT
#
# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Copyright (C) 2018 Daniel Dickinson <cshored@thecshore.com>
# Copyright (C) 2018-2022 Anton Kikin <a.kikin@tano-systems.com>
#

PR = "tano54"

DESCRIPTION = "OpenWrt Network interface configuration daemon"
HOMEPAGE = "http://git.openwrt.org/?p=project/netifd.git;a=summary"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://main.c;beginline=1;endline=13;md5=572cd47ba0e377b26331e67e9f3bc4b3"
SECTION = "base"
DEPENDS = "json-c libubox ubus libnl uci"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/patches:${THISDIR}/${PN}/files:"

SRC_URI = "\
	git://${GIT_OPENWRT_ORG}/project/netifd.git;name=netifd;branch=master \
"

# rootfs files
SRC_URI += "\
	file://rootfs/etc/config/network \
	file://rootfs/etc/config/wireless \
	file://rootfs/etc/hotplug.d/iface/00-netstate \
	file://rootfs/etc/hotplug.d/net/20-smp-packet-steering \
	file://rootfs/etc/init.d/network \
	file://rootfs/etc/uci-defaults/15_migrate-network-config \
	file://rootfs/lib/netifd/proto/dhcp.sh \
	file://rootfs/lib/netifd/dhcp.script \
	file://rootfs/lib/network/config.sh \
	file://rootfs/sbin/devstatus \
	file://rootfs/sbin/ifstatus \
	file://rootfs/sbin/ifup \
	file://rootfs/usr/share/udhcpc/default.script \
	file://rootfs/usr/libexec/netifd-utils/netifd-config-migrate.sh \
	file://rootfs/lib/upgrade/backup-scripts.d/15_migrate-network-config-at-boot \
"

SRC_URI += "\
	file://0001-system-linux-Disable-IFLA_VXLAN_GBP-support-for-kern.patch \
	file://0002-system-linux-add-ethtool_link_mode_bit_indices-for-k.patch \
"

# 02.08.2021
# device: add support for configuring device link speed/duplex
SRCREV_netifd = "1eb0fafaa9865b729509a7d47ecf1f05c2c0595c"

S = "${WORKDIR}/git"

inherit cmake pkgconfig tanowrt-services update-alternatives

TANOWRT_SERVICE_PACKAGES = "netifd"
TANOWRT_SERVICE_SCRIPTS_netifd += "network"
TANOWRT_SERVICE_STATE_netifd-network ?= "enabled"

OECMAKE_C_FLAGS += "-I${STAGING_INCDIR}/libnl3 -Wno-error=cpp"

do_configure_prepend () {
	# replace hardcoded '/lib/' with '${base_libdir}/'
	grep -rnl "/lib/" ${WORKDIR}/rootfs/ | xargs sed -i "s:/lib/:${base_libdir}/:g"
}

do_install_append() {
	install -d ${D}${sysconfdir}/config
	install -m 0644 ${WORKDIR}/rootfs/etc/config/network ${D}${sysconfdir}/config/

	if [ "${@bb.utils.contains('COMBINED_FEATURES', 'wifi', '1', '0', d)}" == "1" ]; then
		install -m 0644 ${WORKDIR}/rootfs/etc/config/wireless ${D}${sysconfdir}/config/
	fi

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/rootfs/etc/init.d/network ${D}${sysconfdir}/init.d/

	install -d ${D}${sysconfdir}/uci-defaults
	install -m 0755 ${WORKDIR}/rootfs/etc/uci-defaults/15_migrate-network-config ${D}${sysconfdir}/uci-defaults/

	install -d ${D}${sysconfdir}/hotplug.d/iface
	install -d ${D}${sysconfdir}/hotplug.d/net
	install -m 0755 ${WORKDIR}/rootfs/etc/hotplug.d/iface/00-netstate ${D}${sysconfdir}/hotplug.d/iface/
	install -m 0755 ${WORKDIR}/rootfs/etc/hotplug.d/net/20-smp-packet-steering ${D}${sysconfdir}/hotplug.d/net/

	install -d ${D}${base_libdir}/netifd/proto
	install -m 0755 ${WORKDIR}/rootfs/lib/netifd/proto/dhcp.sh ${D}${base_libdir}/netifd/proto/
	install -m 0755 ${WORKDIR}/rootfs/lib/netifd/dhcp.script ${D}${base_libdir}/netifd/
	cp -dR --preserve=mode,links ${S}/scripts/* ${D}${base_libdir}/netifd/

	install -d ${D}${base_libdir}/network
	install -m 0755 ${WORKDIR}/rootfs/lib/network/config.sh ${D}${base_libdir}/network/

	install -d ${D}${base_sbindir}
	install -m 0755 ${WORKDIR}/rootfs/sbin/devstatus ${D}${base_sbindir}/
	install -m 0755 ${WORKDIR}/rootfs/sbin/ifstatus ${D}${base_sbindir}/
	install -m 0755 ${WORKDIR}/rootfs/sbin/ifup ${D}${base_sbindir}/
	
	ln -sf ifup ${D}/sbin/ifdown
	ln -sf /usr/sbin/netifd ${D}/sbin/netifd

	install -d ${D}${datadir}/udhcpc
	install -m 0755 ${WORKDIR}/rootfs/usr/share/udhcpc/default.script ${D}${datadir}/udhcpc

	install -d ${D}${libexecdir}/netifd-utils
	install -m 0755 ${WORKDIR}/rootfs/usr/libexec/netifd-utils/netifd-config-migrate.sh \
		${D}${libexecdir}/netifd-utils/

	install -d ${D}${base_libdir}/upgrade/backup-scripts.d
	install -m 0755 ${WORKDIR}/rootfs/lib/upgrade/backup-scripts.d/15_migrate-network-config-at-boot \
		${D}${base_libdir}/upgrade/backup-scripts.d/
}

ALTERNATIVE_${PN} = "ifup ifdown default.script"

ALTERNATIVE_PRIORITY = "40"
ALTERNATIVE_PRIORITY_pkg[default.script] = "60"
ALTERNATIVE_LINK_NAME[ifup] = "${base_sbindir}/ifup"
ALTERNATIVE_LINK_NAME[ifdown] = "${base_sbindir}/ifdown"
ALTERNATIVE_LINK_NAME[default.script] = "/usr/share/udhcpc/default.script"

FILES_${PN} += "\
	${datadir}/udhcpc/default.script* \
	${base_libdir}/netifd/dhcp.script \
	${base_libdir}/netifd/utils.sh \
	${base_libdir}/netifd/netifd-wireless.sh \
	${base_libdir}/netifd/netifd-proto.sh \
	${base_libdir}/netifd/proto/dhcp.sh \
	${base_libdir}/network/config.sh \
	${base_libdir}/upgrade/backup-scripts.d/ \
	${sysconfdir}/config/network \
	${@bb.utils.contains('COMBINED_FEATURES', 'wifi', '${sysconfdir}/config/wireless', '', d)} \
"

CONFFILES_${PN}_append = "\
	${sysconfdir}/config/network \
	${@bb.utils.contains('COMBINED_FEATURES', 'wifi', '${sysconfdir}/config/wireless', '', d)} \
"

RDEPENDS_${PN} += "\
	bridge-utils \
	base-files-scripts-openwrt \
"

inherit kmod/bridge

do_configure[depends] += "virtual/kernel:do_shared_workdir"

#OECMAKE_C_FLAGS += "${@bb.utils.contains('TOOLCHAIN', 'clang', '-Wno-implicit-fallthrough', '', d)}"
