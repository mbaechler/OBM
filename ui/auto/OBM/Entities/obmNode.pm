package OBM::Entities::obmNode;

$VERSION = "1.0";

$debug = 1;

use 5.006_001;
require Exporter;
require overload;
use Carp;
use strict;

use OBM::Parameters::common;
use OBM::Parameters::ldapConf;
require OBM::Ldap::utils;
use Unicode::MapUTF8 qw(to_utf8 from_utf8 utf8_supported_charset);
use strict;


my %ldapEngineAttr = (
    type => undef,
    typeDesc => undef,
    incremental => undef,
    archive => undef,
    nodeId => undef,
    domainId => undef,
    nodeDesc => undef
);


sub new {
    my( $obj, $incremental ) = @_;
    $obj = ref($obj) || $obj;

    if( $incremental ) {
        $ldapEngineAttr{"incremental"} = 1;
    }else {
        $ldapEngineAttr{"incremental"} = 0;
    }

    $ldapEngineAttr{"type"} = $NODE;
    $ldapEngineAttr{"typeDesc"} = $attributeDef->{$ldapEngineAttr{"type"}};

    my $self = \%ldapEngineAttr;
    bless( $self, $obj );

    return $self;
}


sub getEntity {
    my $self = shift;
    my( $name, $description, $domainDesc ) = @_;

    if( !defined($domainDesc->{"domain_id"}) || ($domainDesc->{"domain_id"} !~ /^\d+$/) ) {
        &OBM::toolBox::write_log( "obmNode: description de domaine OBM incorrecte", "W" );
        return 0;
    }else {
        # On positionne l'identifiant du domaine de l'entité
        $self->{"domainId"} = $domainDesc->{"domain_id"};
    }

    if( defined($name) ) {
        $self->{"nodeDesc"}->{"name"} = $name;
    }else {
        &OBM::toolBox::write_log( "obmNode: nom de noeud invalide", "W" );
        return 0;
    }

    $self->{"nodeDesc"}->{"description"} = $description;

    return 1;
}


sub getLdapDnPrefix {
    my $self = shift;
    my $dnPrefix = undef;

    if( defined($self->{"typeDesc"}->{"dn_prefix"}) && defined($self->{"nodeDesc"}->{$self->{"typeDesc"}->{"dn_value"}}) ) {
        $dnPrefix = $self->{"typeDesc"}->{"dn_prefix"}."=".$self->{"nodeDesc"}->{$self->{"typeDesc"}->{"dn_value"}};
    }

    return $dnPrefix;
}


sub createLdapEntry {
    my $self = shift;
    my( $ldapEntry ) = @_;
    my $entry = $self->{"nodeDesc"};

    #
    # On construit la nouvelle entree
    if( $entry->{"name"} ) {
        $ldapEntry->add(
            objectClass => $self->{"typeDesc"}->{"objectclass"},
            ou => to_utf8({ -string => $entry->{"name"}, -charset => $defaultCharSet })
        );
                
    }else {
        return 0;
    }

    # La description
    if( $entry->{"description"} ) {
        $ldapEntry->add( description => to_utf8({ -string => $entry->{"description"}, -charset => $defaultCharSet }) );
    }

    return 1;
}


sub updateLdapEntry {
    my $self = shift;
    my( $ldapEntry ) = @_;
    my $entry = $self->{"nodeDesc"};
    my $update = 0;

    if( &OBM::Ldap::utils::modifyAttr( $entry->{"description"}, $ldapEntry, "description" ) ) {
        $update = 1;
    }

    return $update;
}


sub dump {
    my $self = shift;
    my @desc;

    push( @desc, $self );
    
    require Data::Dumper;
    print Data::Dumper->Dump( \@desc );

    return 1;
}
