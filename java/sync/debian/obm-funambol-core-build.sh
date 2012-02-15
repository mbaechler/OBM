#!/bin/sh

set -e 
set -x

CURDIR=$1
maven_home=${HOME}
MVN_BIN="/usr/bin/mvn -Dmaven.test.skip -Duser.home=${maven_home}"
MVN_PROJECT_NAME="funambol-connector"

FUNAMBOL_BUILD_DEB_DIR="${CURDIR}/debian/obm-funambol-core"
PROJECT_NAME="funambol-connector"

FUNAMBOL_VERSION="10.0.3"
SHARE_INSTALL_DIR="${FUNAMBOL_BUILD_DEB_DIR}/usr/share"
FUNAMBOL_INSTALL_DIR="${SHARE_INSTALL_DIR}/funambol-${FUNAMBOL_VERSION}"
FUNAMBOL_LIB_DIR="${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib"
FUNAMBOL_CONF_DIR="${FUNAMBOL_INSTALL_DIR}/ds-server/config"

#funambol
echo "Funambol preparing distribution..."

cp -r ${MVN_PROJECT_NAME}/funambol-${FUNAMBOL_VERSION} ${FUNAMBOL_INSTALL_DIR}

echo "Funambol distribution done."

#OBM-FUNAMBOL

# clean project
${MVN_BIN} clean
if [ $? -ne 0 ]; then
  echo "FATAL: mvn clean"
  exit 1
fi

# build project
${MVN_BIN} -f ${MVN_PROJECT_NAME}/pom.xml install
if [ $? -ne 0 ]; then
  echo "FATAL: mvn install"
  exit 1
fi

JAR_OBM_FUNAMBOL=`find ${MVN_PROJECT_NAME}/target -name ${MVN_PROJECT_NAME}*.jar`
cp ${JAR_OBM_FUNAMBOL} ${FUNAMBOL_LIB_DIR}

JAR_DEPENDENCIES=`find ${MVN_PROJECT_NAME}/target/dependencies -name *.jar`
cp ${JAR_DEPENDENCIES} ${FUNAMBOL_LIB_DIR}


cp -r ${MVN_PROJECT_NAME}/target/funambol-configuration/* ${FUNAMBOL_CONF_DIR}
