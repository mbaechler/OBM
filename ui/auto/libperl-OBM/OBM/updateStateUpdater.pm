package OBM::updateStateUpdater;

$VERSION = '1.0';

use OBM::Log::log;
@ISA = ('OBM::Log::log');

$debug = 1;

use 5.006_001;
require Exporter;
use strict;

use OBM::Parameters::regexp;


sub new {
    my $class = shift;
    my( $domainId ) = @_;

    my $self = bless { }, $class;

    if( !defined($domainId) || ref($domainId) || ($domainId !~ /$regexp_id/) ) {
        $self->_log( 'un et un seul identifiant de domaine doit être indiqué', 1 );
        return undef;
    }

    $self->{'domainId'} = $domainId;

    return $self;
}


sub DESTROY {
    my $self = shift;

    $self->_log( 'suppression de l\'objet', 5 );
}


sub update {
    my $self = shift;

    require OBM::Tools::obmDbHandler;
    my $dbHandler;
    if( !($dbHandler = OBM::Tools::obmDbHandler->instance()) ) {
        $self->_log( 'connexion à la base de données impossible', 1 );
        return 1;
    }

    $self->_log( 'mise à jour du flag de mise à jour en attente pour le domaine d\'identifiant '.$self->{'domainId'}, 3 );
    my $query = 'UPDATE DomainPropertyValue
                    SET domainpropertyvalue_value=0
                    WHERE domainpropertyvalue_property_key=\'update_state\'
                    AND domainpropertyvalue_domain_id='.$self->{'domainId'};

    my $queryResult;
    if( !defined($dbHandler->execQuery( $query, \$queryResult )) ) {
        $self->_log( 'problème lors de la mise à jour du flag de mise à jour en attente', 1 );
        return 1;
    }

    return 0;
}
