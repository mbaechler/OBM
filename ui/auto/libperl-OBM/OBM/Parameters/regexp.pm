#########################################################################
# OBM       - file : OBM::Parameters::regexp (Perl Module)              #
#           - Desc : librairie des expressions rationnelles Perl pour   #
#                    OBM                                                #
#########################################################################
package OBM::Parameters::regexp;

require Exporter;
require OBM::Parameters::common;


@ISA = qw(Exporter);
@EXPORT_regexp = qw(
    $regexp_id
    $regexp_domain
    $regexp_email
    $regexp_email_left
    $regexp_email_right
    $regexp_rootLdap
    $regexp_login
    $regexp_passwd
    $regexp_ip
    $regexp_server_id
    $regexp_uid
    $regexp_hostname
    $regexp_groupname
    $regexp_mailsharename
    );
@EXPORT = (@EXPORT_regexp);


# Regexp generic
$regexp_id = '^[0-9]+$';

# Domain regexp
$regexp_domain = '^([a-z0-9-]+\.)+[a-z]{2,6}$';

# Email
$regexp_email = '^[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@([a-z0-9-]+\.)+[a-z]{2,6}$';
$regexp_email_left = '^[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*$';
$regexp_email_right = $regexp_domain;

# LDAP root
$regexp_rootLdap = "^dc=(.+),dc=.+\$";

# Login regexp
$regexp_login = "^([a-z0-9][a-z0-9-._]{0,256})\$";
if( $OBM::Parameters::common::obmModules->{'samba'} ) {
    $regexp_login = "^([a-z0-9][a-z0-9-._]{0,31})\$";
}

# Passwd regexp
$regexp_passwd = '^[-\$\\\&~#\{\(\[\|_`\^@\);\]+=\}%!:\/\.,?<>"\w0-9]{4,20}$';
if( $OBM::Parameters::common::obmModules->{'samba'} ) {
    $regexp_passwd = '^[-\$\\\&~#\{\(\[\|_`\^@\);\]+=\}%!:\/\.,?<>"\w0-9]{4,12}$';
}

# Les adresses IP
$regexp_ip = '^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$';

# Server regexp
$regexp_server_id = '^[0-9]+$';

# User regexp
$regexp_uid = '^[0-9]+$';

# Host
$regexp_hostname = '^[A-Za-z0-9][A-Za-z0-9-]{0,30}[A-Za-z0-9]$';

# Group
$regexp_groupname = '^[\W\w0-9]([\W\w0-9-._ ]{0,252}[\W\w0-9]){0,1}$';

# Mailshare
$regexp_mailsharename = $regexp_login;
