CREATE TABLE `dav_event_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `event_client_id` varchar(10000) NOT NULL,
  `event_client_id_hash` BINARY(20) NOT NULL,
  `event_ext_id` varchar(300) NOT NULL,
  `event_ext_id_hash` BINARY(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `dav_event_mapping_owner_id_key` (`owner_id`),
  CONSTRAINT `dav_event_mapping_owner_id_fkey` FOREIGN KEY (`owner_id`) REFERENCES `UserObm` (`userobm_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE KEY `dav_event_mapping_ext_id_hash_key` (`owner_id`,`event_ext_id_hash`),
  UNIQUE KEY `dav_event_mapping_client_id_hash_key` (`owner_id`,`event_client_id_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

