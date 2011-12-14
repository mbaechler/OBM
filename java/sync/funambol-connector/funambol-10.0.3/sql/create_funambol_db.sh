#!/bin/sh

DIR=`dirname $0`

. ${DIR}/servers.env
. ${DIR}/db_management.lib

create_db || {
    echo "Could not create db"
    exit 1
}
my_userscript ${dir}/funambol_dump.sql && echo "[OK]"

echo "funambol db install finished"
exit 0