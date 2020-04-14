# This file Copyright (C) 2018-2019 Anton Kikin <a.kikin@tano-systems.com>

PR_append = ".tano0.${INC_PR}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/files:${THISDIR}/${PN}/patches-1.30:${THISDIR}/${PN}/fragments:"

# Patches
SRC_URI_append = "\
	file://110-no_static_libgcc.patch \
	file://130-mconf_missing_sigwinch.patch \
	file://200-udhcpc_reduce_msgs.patch \
	file://201-udhcpc_changed_ifindex.patch \
	file://210-add_netmsg_util.patch \
	file://220-add_lock_util.patch \
	file://230-add_nslookup_lede.patch \
	file://240-telnetd_intr.patch \
	file://250-date-k-flag.patch \
	file://270-libbb_make_unicode_printable.patch \
	file://301-ip-link-fix-netlink-msg-size.patch \
	file://520-loginutils-handle-crypt-failures.patch \
	file://530-ip-use-rtnl_send_check-on-flush-commands.patch \
"

require busybox-openwrt.inc
