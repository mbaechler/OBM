package OBM::Ldap::ldapContactEngine;

$VERSION = '1.0';

use OBM::Ldap::ldapEngine;
@ISA = ('OBM::Ldap::ldapEngine');

$debug = 1;

use 5.006_001;
use strict;


sub new {
    my $class = shift;

    my $self = bless { }, $class;

    $self = $self->SUPER::new();

    $self->{'dbEntitiesId'} = [];
    $self->{'ldapEntries'} = [];

    return $self;
}


sub DESTROY {
    my $self = shift;

    $self->SUPER::DESTROY();
    $self->{'dbEntitiesId'} = undef;
    $self->{'ldapEntries'} = undef;
}


sub setEntitiesIds {
    my $self = shift;
    my( $entitiesIdsList ) = @_;

    if( ref($entitiesIdsList) ne 'HASH' ) {
        $self->_log( 'liste d\'ID de contact incorrecte', 4 );
        return 1;
    }

    $self->{'dbEntitiesId'} = $entitiesIdsList;

    return 0;
}


sub deleteLdapContacts {
    my $self = shift;
    my( $domainEntity ) = @_;

    if( ref($domainEntity) ne 'OBM::Entities::obmDomain' ) {
        $self->_log( 'entité incorrecte, la mise à jour est possible uniquement pour les domaines', 4 );
        return 1;
    }
    $self->{'currentEntity'} = $domainEntity;

    if( $self->_getLdapContacts() ) {
        $self->_log( 'probleme lors de l\'obtention des contacts depuis l\'annuaire LDAP', 3 );
        return 1;
    }

    if( $self->_deleteLdapContacts() ) {
        $self->_log( 'probleme lors de la suppression des contacts dans l\'annuaire LDAP', 3 );
        return 1;
    }

    return 0;
}


sub _getLdapContacts {
    my $self = shift;
    my $ldapServers = $self->{'ldapservers'};
    my $entity = $self->{'currentEntity'};

    require OBM::Entities::obmContact;
    my $currentEntityDNs = $entity->getDnPrefix( OBM::Entities::obmContact->new( $entity ) );

    # Get LDAP server conn for this entity
    my $ldapServerConn;
    if( !($ldapServerConn = $ldapServers->getLdapServerConn($entity->getLdapServerId())) ) {
        $self->_log( 'problème avec le serveur LDAP de l\'entité : '.$entity->getDescription(), 1 );
        return 1;
    }

    $self->_log( 'obtention des entités de la branche LDAP \''.$currentEntityDNs->[0].'\'', 4 );

    my $result = $ldapServerConn->search(
        base => $currentEntityDNs->[0],
        scope => 'one',
        filter => '(objectclass=*)',
        attrs => ['obmUID']
    );

    if( ($result->code != 32) && ($result->is_error()) ) {
        $self->_log( 'problème lors de la recherche LDAP \''.$result->code.'\', '.$result->error, 1 );
        return 0;
    }


    my @ldapEntries = $result->entries();
    $self->{'ldapEntries'} = \@ldapEntries;

    return 0;
}


# Delete LDAP contact whose aren't in OBM DB
sub _deleteLdapContacts {
    my $self = shift;
    my $ldapServers = $self->{'ldapservers'};
    my $entity = $self->{'currentEntity'};

    require OBM::Entities::obmContact;
    my $currentEntityDNs = $entity->getDnPrefix(OBM::Entities::obmContact->new($entity));

    # Get LDAP server conn for this entity
    my $ldapServerConn;
    if( !($ldapServerConn = $ldapServers->getLdapServerConn($entity->getLdapServerId())) ) {
        $self->_log( 'problème avec le serveur LDAP de l\'entité : '.$entity->getDescription(), 1 );
        return 1;
    }

    $self->_log( 'suppression des entités de la branche \''.$currentEntityDNs->[0].'\' non présentes en BD', 4 );

    my $ldapEntities = $self->{'ldapEntries'};
    my $dbEntities = $self->{'dbEntitiesId'};

    for( my $i=0; $i<=$#{$ldapEntities}; $i++ ) {
        my $ldapEntity = $ldapEntities->[$i];
        my $obmUid = $ldapEntity->get_value('obmUID');

        if( !exists($dbEntities->{$obmUid}) ) {
            my $result = $ldapServerConn->delete( $ldapEntity->dn() );

            if( $result->is_error() ) {
                $self->_log('erreur LDAP à la suppression de l\'entité d\'ID OBM \''.$obmUid.'\', DN '.$ldapEntity->dn().' : '.$result->code().' - '.$result->error(), 1);
            }else {
                $self->_log('suppression de l\'entité d\'ID OBM \''.$obmUid.'\', DN '.$ldapEntity->dn(), 3);
            }
        }
    }

    return 0;
}


sub getLastUpdateDate {
    my $self = shift;
    my($entity) = @_;
    my $ldapServers = $self->{'ldapservers'};

    # Get LDAP server conn for this entity
    my $ldapServerConn;
    if( !($ldapServerConn = $ldapServers->getLdapServerConn($entity->getLdapServerId())) ) {
        $self->_log( 'problème avec le serveur LDAP de l\'entité : '.$entity->getDescription(), 2 );
        return undef;
    }

    $self->_log( 'obtention de la configuration du service contact', 4 );
    my $dn = $entity->getDnPrefix();

    my $result = $ldapServerConn->search(
        base => $dn->[0],
        scope => 'base',
        filter => '(objectclass=obmContactService)',
        attrs => ['lastUpdate']
    );

    if( ($result->code != 32) && ($result->is_error()) ) {
        $self->_log( 'problème lors de la recherche LDAP \''.$result->code.'\', '.$result->error, 1 );
        return undef;
    }


    my @ldapEntries = $result->entries();
    if($#ldapEntries != 0) {
        $self->_log( 'pas de date de dernière mise à jour. Mise à jour globale', 2 );
        return undef;
    }

    my $lastUpdate = $ldapEntries[0]->get_value('lastUpdate', asref => 1);
    if($#$lastUpdate != 0) {
        $self->_log( 'date de dernière mise à jour incorrecte. Mise à jour globale', 2 );
        return undef;
    }

    return $lastUpdate->[0];
}
