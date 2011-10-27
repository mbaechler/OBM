#!/bin/bash
set -e
set -x

CURDIR=$1
BUILD_DEB_DIR="${CURDIR}/debian"
BUILD_DEB_DIR_STORAGE="${BUILD_DEB_DIR}/obm-funambol-storage"
INSTALL_DIR_SQL="${BUILD_DEB_DIR_STORAGE}/usr/share/dbconfig-common/data/obm-funambol-storage/install"
UPGRADE_DIR_SQL="${BUILD_DEB_DIR_STORAGE}/usr/share/dbconfig-common/data/obm-funambol-storage/upgrade"
SHARE_DIR="${BUILD_DEB_DIR_STORAGE}/usr/share/obm-funambol-storage/debian"

cp debian/misc/db/postgresql/init_engine.sql ${INSTALL_DIR_SQL}/pgsql
cp debian/misc/db/mysql/init_engine.sql ${INSTALL_DIR_SQL}/mysql
