package OBM::Postfix::smtpInEngine;

$VERSION = '1.0';

use OBM::Log::log;
@ISA = ('OBM::Log::log');

$debug = 1;

use 5.006_001;
require Exporter;
use strict;


sub new {
    my $class = shift;

    my $self = bless { }, $class;

    # Count entities for which correct update need postfix maps regeneration
    $self->{'entitiesUpdate'} = 0;

    # Save entities which need postfix maps update, but are in error state and
    # cancel postfix maps regeneration
    $self->{'entitiesUpdateErrorDesc'} = ();

    # Which domains SMTP-in to update
    $self->{'smtpInDomainId'} = {};

    return $self;
}


sub DESTROY {
    my $self = shift;

    $self->_log( 'suppression de l\'objet', 5 );
}


sub update {
    my $self = shift;
    my( $entity ) = @_;

    if( !defined($entity) ) {
        $self->_log( 'entité non définie', 1 );
        return 1;
    }elsif( !ref($entity) ) {
        $self->_log( 'entité incorrecte', 1 );
        return 1;
    }

 
    # If entity update error
    if( !$entity->getUpdated() ) {
        $self->_log( 'entité '.$entity->getDescription().' en erreur de mise à jour', 1 );
        push( @{$self->{'entitiesUpdateErrorDesc'}}, $entity->getDescription() );
        return 0;
    }

    # If entity is not updated (but only links)
    if( !$entity->smtpInUpdateMap() ) {
        $self->_log( 'entité '.$entity->getDescription().' mise à jour mais pas d\'impact sur les maps postfix', 3 );
        return 0;
    }

    $self->{'entitiesUpdate'}++;
    $self->{'smtpInDomainId'}->{$entity->getDomainId()} = '';

    return 0;
}


sub updateByDomainId {
    my $self = shift;
    my( $ids ) = @_;

    if( ref($ids) ne 'ARRAY' ) {
        $self->_log( 'listes d\'identifiant de domaines incorrecte', 1 );
        return 1;
    }

    $self->{'entitiesUpdate'} = 1;

    require OBM::Parameters::regexp;
    for( my $i=0; $i<=$#$ids; $i++ ) {
        if( $ids->[$i] =~ /$OBM::Parameters::regexp::regexp_id/ ) {
            $self->{'smtpInDomainId'}->{$ids->[$i]} = '';
        }
    }

    return 0;
}


sub updateMaps {
    my $self = shift;

    if( $#{$self->{'entitiesUpdateErrorDesc'}} >= 0 ) {
        $self->_log( 'au moins une entité nécessitant la régénération des maps SMTP-in est en erreur de traitement', 0 );
        $self->_log( 'génération des maps SMTP-in annulée', 1 );

        for( my $i=0; $i<=$#{$self->{'entitiesUpdateErrorDesc'}}; $i++ ) {
            $self->_log( 'erreur: '.$self->{'entitiesUpdateErrorDesc'}->[$i], 1 );
        }

        return 1;
    }

    if( !$self->{'entitiesUpdate'} ) {
        $self->_log( 'pas d\'entité mise à jour, nécessitant la régénération des maps SMTP-in', 3 );
        return 0;
    }

    if( $self->_updateSmtpInMaps() ) {
        $self->_log( 'problème à la mise à jour des maps SMTP-in', 1 );
        return 2;
    }

    return 0;
}


sub _updateSmtpInMaps {
    my $self = shift;

    my @smtpInDomainId = keys(%{$self->{'smtpInDomainId'}});
    if( $#smtpInDomainId < 0 ) {
        $self->_log( 'pas d\'ID de domaine à mettre à jour. Pas de régénération des maps SMTP-in', 3 );
        return 0;
    }

    require OBM::Tools::obmDbHandler;
    my $dbHandler = OBM::Tools::obmDbHandler->instance();

    $self->_log( 'obtention des serveurs SMTP-in à mettre à jour', 3 );
    my $query = 'SELECT host_id,
                        host_name,
                        host_ip,
                        host_fqdn
                 FROM ServiceProperty
                 INNER JOIN DomainEntity ON serviceproperty_entity_id=domainentity_entity_id AND serviceproperty_property=\'smtp_in\'
                 INNER JOIN Host ON host_id='.$dbHandler->castAsInteger('serviceproperty_value').' 
                 WHERE domainentity_domain_id IN ('.join( ', ', @smtpInDomainId ).')';

    my $sth;
    if( !defined( $dbHandler->execQuery( $query, \$sth ) ) ) {
        $self->_log( 'obtention du serveur SMTP-in impossible', 1 );
        return 1;
    }

    my $smtpInDesc = 0;
    while( my $srvDesc = $sth->fetchrow_hashref() ) {
        $smtpInDesc++;

        require OBM::Postfix::smtpInServer;
        my $srv = OBM::Postfix::smtpInServer->new( $srvDesc );

        if( !defined($srv) ) {
            $self->_log( 'problème d\'initialisation d\'un serveur SMTP-in', 1 );
            $sth->finish();
            return 1;
        }

        if( $srv->update() ) {
            $self->_log( 'problème à la mise à jour d\'un serveur SMTP-in', 1 );
            $sth->finish();
            return 1;
        }
    }

    if( $smtpInDesc == 0 ) {
        $self->_log( 'pas de serveur SMTP-in à mettre à jour', 3 );
    }

    return 0;
}
