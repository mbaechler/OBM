#!/bin/bash

test $# -eq 4 || {
    echo "usage: $0 db user password lang"
    exit 1
}

db=$1
user=$2
pw=$3
obm_lang=$4

echo "*** Database creation"

echo "  Delete old database if exists"
mysql -u $user -p$pw -e "DROP DATABASE IF EXISTS $db"

echo "  Create new $db database"
mysql -u $user -p$pw -e "CREATE DATABASE $db CHARACTER SET utf8 COLLATE utf8_general_ci"

echo "  Create new $db database model"
mysql -u $user -p$pw $db < create_obmdb_2.3.mysql.sql
test $? -eq 0 || {
    echo "error running mysql script"
    exit 1
}
echo "*** Database filling"

# Default data insertion
mysql --default-character-set='UTF8' -u $user -p$pw $db < obmdb_default_values_2.3.sql
test $? -eq 0 || {
    echo "error running mysql script"
    exit 1
}

# Dictionnary data insertion
echo "  Dictionnary data insertion"
mysql --default-character-set='UTF8' -u $user -p$pw $db < data-$obm_lang/obmdb_ref_2.3.sql
test $? -eq 0 || {
    echo "error running mysql script"
    exit 1
}

# Company Naf Code data insertion
echo "  Company Naf Code data insertion"
mysql --default-character-set='UTF8' -u $user -p$pw $db < data-$obm_lang/obmdb_nafcode_2.3.sql
test $? -eq 0 || {
    echo "error running mysql script"
    exit 1
}

# Preferences data insertion & Update default lang to .ini value
echo "  Default preferences data insertion"
mysql --default-character-set='UTF8' -u $user -p$pw $db < obmdb_prefs_values_2.3.sql
test $? -eq 0 || {
    echo "error running mysql script"
    exit 1
}

echo "UPDATE UserObmPref set userobmpref_value='$obm_lang' where userobmpref_option='set_lang'" | mysql -u $user -p$pw $db 
