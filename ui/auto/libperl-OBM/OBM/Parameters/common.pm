#########################################################################
# OBM           - File : OBM::Parameters::common.pm (Perl Module)	    #
#               - Desc : Librairie Perl pour OBM                        #
#               Definition des constantes et des parametres communs     #
#########################################################################
package OBM::Parameters::common;

require Exporter;
use Config::IniFiles;
use FindBin qw($Bin);


@ISA = qw(Exporter);
@EXPORT_const = qw($Bin $logLevel $logFile $sieveSrv $backupRoot $documentRoot $documentDefaultPath $ldapServerId $ldapDescription $ldapAdminLogin $ldapServer $ldapRoot $sambaOldSidMapping $cyrusAdminLogin $cyrusDomainPartition $ldapAllMainMailAddress $obmModules $userMailboxDefaultFolders $shareMailboxDefaultFolders $baseHomeDir $defaultCharSet $sambaRidBase $minUID $minGID $MAILBOXENTITY $MAILSHAREENTITY $USERCONSUMER);
@EXPORT_dir = qw($automateOBM $templateOBM $tmpOBM);
@EXPORT_command = qw($recode $sambaNTPass $sambaLMPass);
@EXPORT_db = qw($userDb $userPasswd $dbName $db $dbType);
@EXPORT = (@EXPORT_const, @EXPORT_db, @EXPORT_command, @EXPORT_dir);
@EXPORT_OK = qw();

# Necessaire pour le bon fonctionnement du package
$debug=1;

sub trim {
    my( $string ) = @_;

    $string =~ s/^\s+//;
    $string =~ s/\s+$//;

    return $string;
}


# Détainte la variable '$Bin'
if( (-d $Bin) && ($Bin =~ /^([\p{Alphabetic}0-9\/_\-\s\.]+)$/) ) {
    $Bin = $1;
}else {
    print STDERR "Syntaxe incorrecte du chemin d'acces aux scripts de l'automate. Le chemin d'acces ne peut contenir que les caracteres : [A-Za-z0-9/_-. ]\n";
    exit 1;
}


# Lecture du fichier ini
my $obmConfIni = '/etc/obm/obm_conf.ini';
if( ! -r $obmConfIni ) {
    $obmConfIni = $Bin.'/../conf/obm_conf.ini';
    if( ! -r $obmConfIni ) {
        print STDERR "Le fichier de configuration 'obm_conf.ini' n'existe pas ou n'est pas lisible\n";
        exit 1;
    }
}


$cfgFile = Config::IniFiles->new( -file => $obmConfIni );

# Initialisation du moteur de random
# srand( time ^ $$ ^ unpack "%L*", `ps auwx | gzip` );

# racine relative pour les scripts Perl
$racineOBM = $Bin."/..";

# Definition des bases de donnees
$userDb = $cfgFile->val( 'global', 'user' );
$userPasswd = $cfgFile->val( 'global', 'password' );
if( defined( $userPasswd ) && $userPasswd =~ /^"(.*)"$/ ) {
    $userPasswd = $1;
}else {
    $userPasswd = undef;
}

# La base de travail
#
# La base des mises à jours
$dbName = $cfgFile->val( 'global', 'db' );
$dbHost = $cfgFile->val( 'global', 'host' );
$dbType = lc( $cfgFile->val( 'global', 'dbtype' ));

# La racine du backup
$backupRoot = $cfgFile->val( 'global', 'backupRoot' );
if( !defined( $backupRoot ) ) {
    $backupRoot = '/var/lib/obm/backup';
}else {
    $backupRoot =~ s/^"//;
    $backupRoot =~ s/"$//;
}

# La racine du repertoire document
$documentRoot = $cfgFile->val( 'global', 'documentRoot' );
if( !defined( $documentRoot ) ) {
    $documentRoot = '/var/lib/obm/documents';
}else {
    $documentRoot =~ s/^"//;
    $documentRoot =~ s/"$//;
}

# Path des documents
$documentDefaultPath = $cfgFile->val( 'global', 'documentDefaultPath' );
if( !defined( $documentDefaultPath ) ) {
    $documentDefaultPath = '/';
}else {
    $documentDefaultPath =~ s/^"//;
    $documentDefaultPath =~ s/"$//;
}

# definition du niveau de log
$logLevel = $cfgFile->val( 'automate', 'logLevel' );
if( !defined($logLevel) || ($logLevel !~ /^[0-9]+$/) ) {
    $logLevel = 2;
}elsif( $logLevel > 5 ) {
    $logLevel = 5;
}elsif( $logLevel < 0 ) {
    $logLevel = 0;
}

# Log file
$logFile = '/var/log/obm-services/obm-services.log';

# La descrption de l'annuaire LDAP
$ldapServerId = 0;
$ldapDescription = 'Annuaire LDAP OBM';
# Le login de l'administrateur LDAP
$ldapAdminLogin = 'ldapadmin';
# Le serveur LDAP
$ldapServer = $cfgFile->val( 'automate', 'ldapServer' );
if( !defined($ldapServer) ) {
    $ldapServer = 'ldap://127.0.0.1/';
}
# TLS LDAP conn
$ldapTls = $cfgFile->val( 'automate', 'ldapTls' );
if( $ldapServer =~ /^ldaps:/ ) {
    $ldapTls = 'none';
}
if( !defined($ldapTls) || ($ldapTls !~ /^(none|may|encrypt)$/) ) {
    $ldapTls = 'may';
}

# Racine LDAP de l'arbre gérée pas OBM-Ldap
# exemple : 'aliasource,local' place l'arbre LDAP d'OBM-Ldap sous le DN: 'dc=aliasource,dc=local' 
$ldapRoot = $cfgFile->val( 'automate', 'ldapRoot' );
if( !defined($ldapRoot) ) {
    $ldapRoot = "local";
}

# Le mapping des UID<->SID
$sambaOldSidMapping = $cfgFile->val( 'automate', 'oldSidMapping' );
if( defined($sambaOldSidMapping) && lc($sambaOldSidMapping) eq "true" ) {
    $sambaOldSidMapping = 1;
}else {
    $sambaOldSidMapping = 0;
}

# Le login de l'administrateur Cyrus
$cyrusAdminLogin = 'cyrus';

# Gestion d'une partition cyrus par domaine
$cyrusDomainPartition = $cfgFile->val( 'automate', 'cyrusPartition' );
if( defined( $cyrusDomainPartition ) && lc($cyrusDomainPartition) eq "true" ) {
    $cyrusDomainPartition = 1;
}else {
    $cyrusDomainPartition = 0;
}

# Publication des adresses principales des utilisateurs n'ayant pas le droit
# mail activé
$ldapAllMainMailAddress = $cfgFile->val( 'automate', 'ldapAllMainMailAddress' );
if( defined($ldapAllMainMailAddress) && lc($ldapAllMainMailAddress) eq "true" ) {
    $ldapAllMainMailAddress = 1;
}else {
    $ldapAllMainMailAddress = 0;
}

# Les modules OBM actifs
$obmModules = {
    ldap => 0,
    mail => 0,
    samba => 0,
    web => 0,
    contact => 0
};

$obmModule = trim( $cfgFile->val( 'global', 'obm-ldap' ) );
if( defined( $obmModule ) && lc($obmModule) eq 'true' ) {
    $obmModules->{'ldap'} = 1;
}else {
    $obmModules->{'ldap'} = 0;
}

$obmModule = trim( $cfgFile->val( 'global', 'obm-mail' ) );
if( defined( $obmModule ) && lc($obmModule) eq 'true' ) {
    $obmModules->{'ldap'} = 1;
    $obmModules->{'mail'} = 1;
}else {
    $obmModules->{'mail'} = 0;
}

$obmModule = trim( $cfgFile->val( 'global', 'obm-samba' ) );
if( defined( $obmModule ) && lc($obmModule) eq 'true' ) {
    $obmModules->{'ldap'} = 1;
    $obmModules->{'samba'} = 1;
}else {
    $obmModules->{'samba'} = 0;
}

$obmModule = trim( $cfgFile->val( 'global', 'obm-web' ) );
if( defined( $obmModule ) && lc($obmModule) eq 'true' ) {
    $obmModules->{'ldap'} = 1;
    $obmModules->{'web'} = 1;
}else {
    $obmModules->{'web'} = 0;
}

$obmModule = trim( $cfgFile->val( 'global', 'obm-contact' ) );
if( defined( $obmModule ) && lc($obmModule) eq 'true' ) {
    $obmModules->{'contact'} = 1;
}else {
    $obmModules->{'contact'} = 0;
}

# Creation de repertoires a la creation de l'utilisateur
$userMailboxDefaultFolders = $cfgFile->val( 'automate', 'userMailboxDefaultFolders' );
if( defined( $userMailboxDefaultFolders ) && $userMailboxDefaultFolders =~ /^['"](.*)['"]$/ ) {
    $userMailboxDefaultFolders = $1;
}else {
    $userMailboxDefaultFolders = undef;
}

# Creation de repertoires a la creation de partage
$shareMailboxDefaultFolders = $cfgFile->val( 'automate', 'shareMailboxDefaultFolders' );
if( defined( $shareMailboxDefaultFolders ) && $shareMailboxDefaultFolders =~ /^['"](.*)['"]$/ ) {
    $shareMailboxDefaultFolders = $1;
}else {
    $shareMailboxDefaultFolders = undef;
}

# Le repertoire pere des repertoires personnels
# Ne pas mettre le '/' de la fin du chemin
$baseHomeDir = "/home";

# Definition des fichiers modeles
#
# Le repertoire contenant les modeles
$templateOBM = $racineOBM . "/template";

# Definitions des fichiers temporaires.
#
# Le repertoire temporaire
$tmpOBM = "/tmp";


#
# Definition des divers programmes utiles.
#
# Utilitaire de recodage des caracteres de latin1->UTF8
# Préciser l'encodage du système (apache)
#$defaultCharSet = "ISO-8859-1";
$defaultCharSet = "UTF8";
$recode = "/usr/bin/recode l1..utf8";

#
# Definitions des parametres Samba
#
# Base de calcul du RID
$sambaRidBase = 1000;
#
# UID et GID mini
$minUID = 1000;
$minGID = 1000;


#
# Les scripts de l'automate
#
# Le repertoire contenant les scripts de l'automate
$automateOBM = $racineOBM . "/auto";

#
# ACL : Definition des entites et des consomateurs
$MAILBOXENTITY="mailbox";
$MAILSHAREENTITY="mailshare";
$USERCONSUMER="user";
