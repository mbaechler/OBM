BEGIN;

CREATE TABLE dav_event_mapping ( 
    id serial NOT NULL,
    owner_id integer NOT NULL,
    event_client_id character varying NOT NULL,
    event_client_id_hash bytea NOT NULL,
    event_ext_id character varying(300) NOT NULL,
    event_ext_id_hash bytea NOT NULL,
    CONSTRAINT dav_event_mapping_pkey PRIMARY KEY (id),
    CONSTRAINT dav_event_mapping_owner_id_event_ext_id_hash_unique UNIQUE (owner_id, event_ext_id_hash),
    CONSTRAINT dav_event_mapping_owner_id_event_client_id_hash_unique UNIQUE (owner_id, event_client_id_hash),
    CONSTRAINT dav_event_mapping_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES userobm(userobm_id) ON DELETE CASCADE
);

COMMIT;
