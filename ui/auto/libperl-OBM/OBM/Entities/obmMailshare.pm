package OBM::Entities::obmMailshare;

$VERSION = "1.0";

$debug = 1;

use 5.006_001;
require Exporter;
use strict;

use OBM::Entities::commonEntities qw(getType setDelete getDelete getArchive getLdapObjectclass isLinks getEntityId makeEntityEmail getMailboxDefaultFolders getHostIpById);
use OBM::Tools::commonMethods qw(_log dump);
use OBM::Parameters::common;
require OBM::Parameters::ldapConf;
require OBM::Tools::obmDbHandler;
require OBM::Ldap::utils;
require OBM::toolBox;


sub new {
    my $self = shift;
    my( $links, $deleted, $mailShareId ) = @_;

    my %obmMailshareAttr = (
        type => undef,
        entityRightType => undef,
        links => undef,
        toDelete => undef,
        archive => undef,
        sieve => undef,
        objectId => undef,
        domainId => undef,
        mailShareDbDesc => undef,   # Pure description BD
        properties => undef,        # Propriétés calculées
        mailShareLinks => undef,    # Les relations avec d'autres entités
        objectclass => undef,
        dnPrefix => undef,
        dnValue => undef
    );


    if( !defined( $links ) || !defined($deleted) || !defined($mailShareId) ) {
        $self->_log( 'Usage: PACKAGE->new(LINKS, MAILSHAREID)', 1 );
        return undef;
    }elsif( $mailShareId !~ /^\d+$/ ) {
        $self->_log( 'identifiant de BAL partagee incorrect', 2 );
        return undef;
    }else {
        $obmMailshareAttr{"objectId"} = $mailShareId;

    }


    $obmMailshareAttr{'links'} = $links;
    $obmMailshareAttr{'toDelete'} = $deleted;

    $obmMailshareAttr{'type'} = $OBM::Parameters::ldapConf::MAILSHARE;
    $obmMailshareAttr{'entityRightType'} = 'MailShare';
    $obmMailshareAttr{'archive'} = 0;
    $obmMailshareAttr{'sieve'} = 0;

    # Définition de la représentation LDAP de ce type
    $obmMailshareAttr{'objectclass'} = $OBM::Parameters::ldapConf::attributeDef->{$obmMailshareAttr{"type"}}->{objectclass};
    $obmMailshareAttr{'dnPrefix'} = $OBM::Parameters::ldapConf::attributeDef->{$obmMailshareAttr{"type"}}->{dn_prefix};
    $obmMailshareAttr{'dnValue'} = $OBM::Parameters::ldapConf::attributeDef->{$obmMailshareAttr{"type"}}->{dn_value};

    bless( \%obmMailshareAttr, $self );
}


sub getEntity {
    my $self = shift;
    my( $domainDesc ) = @_;

    my $mailShareId = $self->{'objectId'};
    if( !defined($mailShareId) ) {
        $self->_log( 'aucun identifiant de partage de messagerie defini', 3 );
        return 0;
    }


    my $dbHandler = OBM::Tools::obmDbHandler->instance();
    if( !defined($dbHandler) ) {
        $self->_log( 'connecteur a la base de donnee invalide', 3 );
        return 0;
    }

    if( !defined($domainDesc->{"domain_id"}) || ($domainDesc->{"domain_id"} !~ /^\d+$/) ) {
        $self->_log( 'description de domaine OBM incorrecte', 3 );
        return 0;
    }else {
        # On positionne l'identifiant du domaine de l'entité
        $self->{"domainId"} = $domainDesc->{"domain_id"};
    }


    my $mailShareTable = "MailShare";
    my $mailServerTable = "MailServer";
    if( $self->getDelete() ) {
        $mailShareTable = "P_".$mailShareTable;
        $mailServerTable = "P_".$mailServerTable;
    }

    my $query = "SELECT COUNT(*) FROM ".$mailShareTable." LEFT JOIN ".$mailServerTable." ON mailshare_mail_server_id=mailserver_id WHERE mailshare_id=".$mailShareId;
    my $queryResult;
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        return 0;
    }

    my( $numRows ) = $queryResult->fetchrow_array();
    $queryResult->finish();

    if( $numRows == 0 ) {
        $self->_log( 'pas de BAL partagee d\'identifiant : '.$mailShareId, 3 );
        return 0;
    }elsif( $numRows > 1 ) {
        $self->_log( 'plusieurs BAL partagees d\'identifiant : '.$mailShareId." ???", 3 );
        return 0;
    }


    # Obtention de la description BD de l'utilisateur
    $query = "SELECT * FROM ".$mailShareTable." WHERE mailshare_id=".$mailShareId;
    # On execute la requete
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        return 0;
    }

    # On range les resultats dans la structure de donnees des resultats
    my $dbMailShareDesc = $queryResult->fetchrow_hashref();
    $queryResult->finish();

    # On stocke la description BD utile pour la MAJ des tables
    $self->{"mailShareDbDesc"} = $dbMailShareDesc;


    # La requete a exécuter - obtention des informations sur le répertoire
    # partagé
    $query = "SELECT * FROM ".$mailShareTable." LEFT JOIN ".$mailServerTable." ON mailshare_mail_server_id=mailserver_id WHERE mailshare_id=".$mailShareId;
    # On exécute la requête
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        return 0;
    }

    # On range les résultats dans la structure de données des résultats
    $dbMailShareDesc = $queryResult->fetchrow_hashref();
    $queryResult->finish();

    if( $self->getDelete() ) {
        $self->_log( 'suppression de la BAL partagee : \''.$dbMailShareDesc->{'mailshare_name'}.'\', domaine \''.$domainDesc->{'domain_label'}.'\'', 1 );
    
    }else {
        $self->_log( 'gestion de la BAL partagee : \''.$dbMailShareDesc->{'mailshare_name'}.'\', domaine \''.$domainDesc->{'domain_label'}.'\'', 1 );

    }

    # On range les resultats dans la structure de données des resultats
    $self->{"properties"}->{"mailshare_name"} = $dbMailShareDesc->{"mailshare_name"};
    $self->{"properties"}->{"mailshare_description"} = $dbMailShareDesc->{"mailshare_description"};
    $self->{"properties"}->{"mailshare_domain"} = $domainDesc->{"domain_label"};


    # Gestion du droit de messagerie
    my $localServerIp;
    SWITCH: {
        if( !$dbMailShareDesc->{"mailshare_email"} ) {
            $self->{"properties"}->{"mailshare_mailperms"} = 0;
            last SWITCH;
        }

        $localServerIp = $self->getHostIpById( $dbMailShareDesc->{"mailserver_host_id"} );
        if( !defined($localServerIp) ) {
            $self->_log( 'droit mail du repertoire partage : \''.$dbMailShareDesc->{'mailshare_name'}.'\' - annule, serveur inconnu !', 2 );
            $self->{"properties"}->{"mailshare_mailperms"} = 0;
            last SWITCH;
        }

        # Gestion des adresses de la boîte à lettres partagée
        my $return = $self->makeEntityEmail( $dbMailShareDesc->{"mailshare_email"}, $domainDesc->{"domain_name"}, $domainDesc->{"domain_alias"} );
        if( $return == 0 ) {
            $self->_log( 'droit mail du repertoire partage : \''.$dbMailShareDesc->{'mailshare_name'}.'\' - annule, pas d\'adresses mails valides', 2 );
            $self->{"properties"}->{"mailshare_mailperms"} = 0;
            last SWITCH;
        }

        $self->{"properties"}->{"mailshare_mailperms"} = 1;
    }

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        # Gestion de la BAL destination
        #   valeur dans LDAP
        $self->{"properties"}->{"mailshare_mailbox"} = "+".$dbMailShareDesc->{"mailshare_name"}."@".$domainDesc->{"domain_name"};
        #   nom de la BAL Cyrus
        $self->{"properties"}->{"mailshare_mailbox_name"} = $dbMailShareDesc->{"mailshare_name"};
        if( !$singleNameSpace ) {
            $self->{"properties"}->{"mailshare_mailbox_name"} .= "@".$domainDesc->{"domain_name"};
        }

        # Partition Cyrus associée à cette BAL
        if( $OBM::Parameters::common::cyrusDomainPartition ) {
            $self->{"properties"}->{"mailShare_partition"} = $domainDesc->{"domain_dn"};
            $self->{"properties"}->{"mailShare_partition"} =~ s/\./_/g;
            $self->{"properties"}->{"mailShare_partition"} =~ s/-/_/g;
        }

        # Gestion du quota
        $self->{"properties"}->{"mailshare_mailbox_quota"} = $dbMailShareDesc->{"mailshare_quota"}*1024;

        # Gestion des sous-répertoires de la BAL a créer
        if( defined($shareMailboxDefaultFolders) ) {
            foreach my $folderTree ( split( ',', $shareMailboxDefaultFolders ) ) {
                if( $folderTree !~ /(^[",]$)|(^$)/ ) {
                    my $folderName = $dbMailShareDesc->{"mailshare_name"};
                    foreach my $folder ( split( '/', $folderTree ) ) {
                        $folder =~ s/^\s+//;

                        $folderName .= '/'.$folder;
                        if( !$singleNameSpace ) {
                            push( @{$self->{"properties"}->{mailbox_folders}}, $folderName.'@'.$domainDesc->{domain_name} );
                        }else {
                            push( @{$self->{"properties"}->{mailbox_folders}}, $folderName );
                        }
                    }
                }
            }
        }

        # On ajoute le serveur de mail associé
        $self->{"properties"}->{"mailShare_mailLocalServer"} = "lmtp:".$localServerIp.":24";

        # Gestion du serveur de mail
        $self->{"properties"}->{"mailShare_server"} = $dbMailShareDesc->{"mailserver_host_id"};
    }


    # On positionne l'identifiant du domaine de l'entité
    $self->{"domainId"} = $domainDesc->{"domain_id"};

    # Si nous ne sommes pas en mode incrémental, on charge aussi les liens de
    # cette entité
    if( $self->isLinks() ) {
        $self->getEntityLinks( $domainDesc );
    }


    return 1;
}


sub updateDbEntity {
    my $self = shift;

    my $dbHandler = OBM::Tools::obmDbHandler->instance();
    if( !defined($dbHandler) ) {
        return 0;
    }

    my $dbEntry = $self->{"mailShareDbDesc"};
    if( !defined($dbEntry) ) {
        return 0;
    }

    $self->_log( 'MAJ de la boite a lettre partagee '.$self->getEntityDescription().' dans les tables de production', 1 );


    # Champs de la BD qui ne sont pas mis à jour car champs références
    my $exceptFields = '^(mailshare_id)$';

    # MAJ de l'entité dans la table de production
    my @updateFields;
    my @whereFields;
    while( my( $columnName, $columnValue ) = each(%{$dbEntry}) ) {
        if( $columnName =~ /$exceptFields/ ) {
            push( @whereFields, $columnName."=".$dbHandler->quote($columnValue) );
        }else {
            push( @updateFields, $columnName."=".$dbHandler->quote($columnValue) );
        }
    }

    my $query = 'UPDATE P_MailShare SET '.join( ', ', @updateFields ).' WHERE '.join( ' AND ', @whereFields );
    my $queryResult;
    my $result = $dbHandler->execQuery( $query, \$queryResult );

    if( !defined($result) ) {
        $self->_log( 'probleme a la mise a jour de la boite a lettres partagee', 2 );
        return 0;

    }elsif( $result == 0 ) {
        my @fields;
        my @fieldsValues;
        while( my( $columnName, $columnValue ) = each(%{$dbEntry}) ) {
            push( @fields, $columnName );
            push( @fieldsValues, $dbHandler->quote($columnValue) );
        }

        $query = 'INSERT INTO P_MailShare ('.join( ', ', @fields ).') VALUES ('.join( ', ', @fieldsValues ).')';
        $result = $dbHandler->execQuery( $query, \$queryResult );
        if( !defined($result) ) {
            $self->_log( 'probleme a la mise a jour de la boite a lettre partagee', 2 );
            return 0;

        }elsif( $result != 1 ) {
            $self->_log( 'probleme a la mise a jour de la boite a lettre partagee : boite a lettre partagee inseree '.$result.' fois dans les tables de production !', 2 );
            return 0;
        }
    }

    $self->_log( 'MAJ des tables de production reussie', 2 );

    return 1;
}


sub updateDbEntityLinks {
    my $self = shift;
    my $queryResult;

    my $dbHandler = OBM::Tools::obmDbHandler->instance();
    if( !defined($dbHandler) ) {
        return 0;
    }

    $self->_log( 'MAJ des liens de la boite a lettre partagee '.$self->getEntityDescription().' dans les tables de production', 1 );

    # On supprime les liens actuels de la table de production
    my $query = "DELETE FROM P_EntityRight WHERE entityright_entity_id=".$self->{"objectId"}." AND entityright_entity='".$self->{"entityRightType"}."'";
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        return 0;
    }


    # On copie les nouveaux droits
    $query = "INSERT INTO P_EntityRight SELECT * FROM EntityRight WHERE entityright_entity='".$self->{"entityRightType"}."' AND entityright_entity_id=".$self->{"objectId"};
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        return 0;
    }


    return 1;
}


sub getEntityLinks {
    my $self = shift;
    my( $domainDesc ) = @_;

    $self->_getEntityMailShareAcl( $domainDesc );

    # On précise que les liens de l'entité sont aussi à mettre à jour.
    $self->{"links"} = 1;

    return 1;
}


sub getEntityDescription {
    my $self = shift;
    my $entry = $self->{"properties"};
    my $description = "";


    if( defined($entry->{mailshare_name}) ) {
        $description .= "identifiant '".$entry->{mailshare_name}."'";
    }

    if( defined($entry->{mailshare_domain}) ) {
        $description .= ", domaine '".$entry->{mailshare_domain}."'";
    }

    if( ($description ne "") && defined($self->{type}) ) {
        $description .= ", type '".$self->{type}."'";
    }

    if( $description ne "" ) {
        return $description;
    }

    if( defined($self->{objectId}) ) {
        $description .= "ID BD '".$self->{objectId}."'";
    }

    if( defined($self->{type}) ) {
        $description .= ",type '".$self->{type}."'";
    }

    return $description;
}


sub _getEntityMailShareAcl {
    my $self = shift;
    my( $domainDesc ) = @_;
    my $mailShareId = $self->{'objectId'};

    if( !$self->{'properties'}->{'mailshare_mailperms'} ) {
        $self->{'properties'}->{'user_mailshare_acl'} = undef;

    }else {
        my $entityType = $self->{'entityRightType'};
        my %rightDef;


        my $userObmTable = 'UserObm';
        my $entityRightTable = 'EntityRight';
        if( $self->getDelete() ) {
            $userObmTable = 'P_'.$userObmTable;
            $entityRightTable = 'P_'.$entityRightTable;
        }

        $rightDef{'read'}->{'compute'} = 1;
        $rightDef{'read'}->{'sqlQuery'} = 'SELECT i.userobm_id, i.userobm_login FROM '.$userObmTable.' i, '.$entityRightTable.' j WHERE i.userobm_id=j.entityright_consumer_id AND j.entityright_write=0 AND j.entityright_read=1 AND j.entityright_entity_id='.$mailShareId.' AND j.entityright_entity=\''.$entityType.'\'';

        $rightDef{'writeonly'}->{'compute'} = 1;
        $rightDef{'writeonly'}->{'sqlQuery'} = 'SELECT i.userobm_id, i.userobm_login FROM '.$userObmTable.' i, '.$entityRightTable.' j WHERE i.userobm_id=j.entityright_consumer_id AND j.entityright_write=1 AND j.entityright_read=0 AND j.entityright_entity_id='.$mailShareId.' AND j.entityright_entity=\''.$entityType.'\'';

        $rightDef{'write'}->{'compute'} = 1;
        $rightDef{'write'}->{'sqlQuery'} = 'SELECT i.userobm_id, i.userobm_login FROM '.$userObmTable.' i, '.$entityRightTable.' j WHERE i.userobm_id=j.entityright_consumer_id AND j.entityright_write=1 AND j.entityright_read=1 AND j.entityright_entity_id='.$mailShareId.' AND j.entityright_entity=\''.$entityType.'\'';

        $rightDef{'admin'}->{'compute'} = 1;
        $rightDef{'admin'}->{'sqlQuery'} = 'SELECT i.userobm_id, i.userobm_login FROM '.$userObmTable.' i, '.$entityRightTable.' j WHERE i.userobm_id=j.entityright_consumer_id AND j.entityright_admin=1 AND j.entityright_entity_id='.$mailShareId.' AND j.entityright_entity=\''.$entityType.'\'';

        $rightDef{'public'}->{'compute'} = 0;
        $rightDef{'public'}->{'sqlQuery'} = 'SELECT entityright_read, entityright_write FROM '.$entityRightTable.' WHERE entityright_entity_id='.$mailShareId.' AND entityright_entity=\''.$entityType.'\' AND entityright_consumer_id=0';

        # On recupere la definition des ACL
        $self->{'properties'}->{'user_mailshare_acl'} = &OBM::toolBox::getEntityRight( $domainDesc, \%rightDef, $mailShareId );
    }

    return 1;
}


sub getLdapDnPrefix {
    my $self = shift;
    my $dnPrefix = undef;

    if( defined($self->{"dnPrefix"}) && defined($self->{"properties"}->{$self->{"dnValue"}}) ) {
        $dnPrefix = $self->{"dnPrefix"}."=".$self->{"properties"}->{$self->{"dnValue"}};
    }

    return $dnPrefix;
}


sub createLdapEntry {
    my $self = shift;
    my ( $ldapEntry ) = @_;
    my $entry = $self->{'properties'};

    # Les parametres necessaires
    if( $entry->{'mailshare_name'} ) {
        $ldapEntry->add(
            objectClass => $self->{'objectclass'},
            cn => $entry->{'mailshare_name'}
        );

    }else {
        return 0;
    }

    if( $entry->{'mailshare_mailbox'} ) {
        $ldapEntry->add( mailBox => $entry->{'mailshare_mailbox'} );
    }

    if( $entry->{'mailshare_description'} ) {
        $ldapEntry->add( description => $entry->{'mailshare_description'} );
    }

    # Le serveur de BAL local
    if( $entry->{'mailShare_mailLocalServer'} ) {
        $ldapEntry->add( mailBoxServer => $entry->{'mailShare_mailLocalServer'} );
    }

    # Les adresses mails
    if( $entry->{'email'} ) {
        $ldapEntry->add( mail => $entry->{'email'} );
    }

    # Les adresses mails secondaires
    if( $entry->{'emailAlias'} ) {
        $ldapEntry->add( mailAlias => $entry->{'emailAlias'} );
    }

    # L'acces mail
    if( $entry->{'mailshare_mailperms'} ) {
        $ldapEntry->add( mailAccess => 'PERMIT' );
    }else {
        $ldapEntry->add( mailAccess => 'REJECT' );
    }

    # Le domaine
    if( $entry->{'mailshare_domain'} ) {
        $ldapEntry->add( obmDomain => $entry->{'mailshare_domain'} );
    }


    return 1;
}


sub updateLdapEntryDn {
    my $self = shift;
    my( $ldapEntry ) = @_;
    my $update = 0;


    if( !defined($ldapEntry) ) {
        return 0;
    }


    return $update;
}


sub updateLdapEntry {
    my $self = shift;
    my( $ldapEntry, $objectclassDesc ) = @_;
    my $entry = $self->{"properties"};

    require OBM::Entities::entitiesUpdateState;
    my $update = OBM::Entities::entitiesUpdateState->new();


    if( !defined($ldapEntry) ) {
        return undef;
    }


    # Le nom de la BAL
    if( &OBM::Ldap::utils::modifyAttr( $entry->{"mailshare_mailbox"}, $ldapEntry, "mailbox" ) ) {
        $update->setUpdate();
    }

    # La description
    if( &OBM::Ldap::utils::modifyAttr( $entry->{"mailshare_description"}, $ldapEntry, "description" ) ) {
        $update->setUpdate();
    }

    # Le cas des alias mails
    if( &OBM::Ldap::utils::modifyAttrList( $entry->{"email"}, $ldapEntry, "mail" ) ) {
        $update->setUpdate();
    }

    # Le cas des alias mails secondaires
    if( &OBM::Ldap::utils::modifyAttrList( $entry->{"emailAlias"}, $ldapEntry, "mailAlias" ) ) {
        $update->setUpdate();
    }

    # L'acces au mail
    if( $entry->{"mailshare_mailperms"} && (&OBM::Ldap::utils::modifyAttr( "PERMIT", $ldapEntry, "mailAccess" )) ) {
        $update->setUpdate();

    }elsif( !$entry->{"mailshare_mailperms"} && (&OBM::Ldap::utils::modifyAttr( "REJECT", $ldapEntry, "mailAccess" )) ) {
        $update->setUpdate();

    }

    # Le serveur de BAL local
    if( &OBM::Ldap::utils::modifyAttr( $entry->{"mailShare_mailLocalServer"}, $ldapEntry, "mailBoxServer" ) ) {
        $update->setUpdate();
    }

    # Le domaine
    if( &OBM::Ldap::utils::modifyAttr( $entry->{"mailshare_domain"}, $ldapEntry, "obmDomain" ) ) {
        $update->setUpdate();
    }


    if( $self->isLinks() ) {
        if( $self->updateLdapEntryLinks( $ldapEntry ) ) {
            $update->setUpdate();
        }
    }


    return $update;
}


sub updateLdapEntryLinks {
    my $self = shift;
    my( $ldapEntry ) = @_;
    my $update = 0;

    
    if( !defined($ldapEntry) ) {
        return 0;
    }


    return $update;
}


sub getMailServerId {
    my $self = shift;
    my $mailServerId = undef;

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        $mailServerId = $self->{"properties"}->{"mailShare_server"};
    }

    return $mailServerId;
}


sub getMailboxPrefix {
    my $self = shift;

    return "";
}


sub getMailboxName {
    my $self = shift;
    my $mailShareName = undef;

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        $mailShareName = $self->{"properties"}->{"mailshare_mailbox_name"};
    }

    return $mailShareName;
}


sub getMailboxPartition {
    my $self = shift;
    my $mailSharePartition = undef;

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        $mailSharePartition = $self->{"properties"}->{"mailShare_partition"};
    }

    return $mailSharePartition;
}


sub getMailboxSieve {
    my $self = shift;

    return $self->{"sieve"};
}


sub getMailboxQuota {
    my $self = shift;
    my $mailShareQuota = undef;

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        $mailShareQuota = $self->{"properties"}->{"mailshare_mailbox_quota"};
    }

    return $mailShareQuota;
}


sub getMailboxAcl {
    my $self = shift;
    my $mailShareAcl = undef;

    if( $self->{"properties"}->{"mailshare_mailperms"} ) {
        $mailShareAcl = $self->{"properties"}->{"user_mailshare_acl"};
    }

    return $mailShareAcl;
}
