#!/bin/sh

set -e 
set -x

CURDIR=$1
maven_home=${HOME}
MVN_BIN="/usr/bin/mvn -Dmaven.test.skip -Duser.home=${maven_home}"
POM_FILE="pom.xml"

FUNAMBOL_BUILD_DEB_DIR="${CURDIR}/debian/obm-funambol-core"
PROJECT_NAME="funambol-connector"

FUNAMBOL_VERSION="10.0.3"
SHARE_INSTALL_DIR="${FUNAMBOL_BUILD_DEB_DIR}/usr/share"
FUNAMBOL_INSTALL_DIR="${SHARE_INSTALL_DIR}/funambol-${FUNAMBOL_VERSION}"

#funambol
echo "Funambol preparing distribution..."

cp -r obm-funambol/funambol-${FUNAMBOL_VERSION} ${FUNAMBOL_INSTALL_DIR}

echo "Funambol distribution done."

#OBM-FUNAMBOL

# clean project
${MVN_BIN} clean
if [ $? -ne 0 ]; then
  echo "FATAL: mvn clean"
  exit 1
fi

# build project
${MVN_BIN} -f ${PROJECT_NAME}/pom.xml install
if [ $? -ne 0 ]; then
  echo "FATAL: mvn package"
  exit 1
fi

JAR_OBM_FUNAMBOL=`find ${PROJECT_NAME}/target -name obm-funambol*.jar`
cp ${JAR_OBM_FUNAMBOL} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_OBM_SYNC_CLIENT=`find ${PROJECT_NAME}/target/dependencies -name client-*.jar`
cp ${JAR_OBM_SYNC_CLIENT} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_OBM_SYNC_COMMON=`find ${PROJECT_NAME}/target/dependencies -name common-*.jar`
cp ${JAR_OBM_SYNC_COMMON} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_OBM_UTILS=`find ${PROJECT_NAME}/target/dependencies -name utils-*.jar`
cp ${JAR_OBM_UTILS} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_OBM_CONFIGURATION=`find ${PROJECT_NAME}/target/dependencies -name configuration-*.jar`
cp ${JAR_OBM_CONFIGURATION} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_OBM_LOCATOR=`find ${PROJECT_NAME}/target/dependencies -name locator-*.jar`
cp ${JAR_OBM_LOCATOR} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

JAR_GUAVA=`find ${PROJECT_NAME}/target/dependencies -name guava-*.jar`
cp ${JAR_GUAVA} ${FUNAMBOL_INSTALL_DIR}/funambol/WEB-INF/lib

# build s4j
${MVN_BIN} -f obm-funambol/pom.xml funambol:s4j
if [ $? -ne 0 ]; then
  echo "FATAL: mvn package"
  exit 1
fi

S4J=`find ${PROJECT_NAME}/target -name *.s4j`
cp ${S4J} ${FUNAMBOL_INSTALL_DIR}/ds-server/modules

