DROP TABLE IF EXISTS ProfileModule;
DROP TABLE IF EXISTS ProfileSection;
DROP TABLE IF EXISTS ProfileProperty;
DROP TABLE IF EXISTS ProfilePropertyValue;
DROP TABLE IF EXISTS Profile;

--
-- Table structure for table `Profile`
--

DROP TABLE IF EXISTS Profile;
CREATE TABLE Profile (
  profile_id int(8) NOT NULL auto_increment,
  profile_domain_id int(8) NOT NULL,
  profile_timeupdate timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  profile_timecreate timestamp NOT NULL default '0000-00-00 00:00:00',
  profile_userupdate int(8) default NULL,
  profile_usercreate int(8) default NULL,
  profile_name varchar(64) default NULL,
  PRIMARY KEY  (profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `ProfileModule`
--
DROP TABLE IF EXISTS ProfileModule;
CREATE TABLE ProfileModule (
  profilemodule_id int(8) NOT NULL auto_increment,
  profilemodule_domain_id int(8) NOT NULL,
  profilemodule_profile_id int(8) default NULL,
  profilemodule_module_name varchar(16) NOT NULL default '',
  profilemodule_right int(2) default NULL,
  PRIMARY KEY (profilemodule_id),
  CONSTRAINT profilemodule_profile_id_profile_id_fkey FOREIGN KEY (profilemodule_profile_id) REFERENCES Profile (profile_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `ProfileSection`
--

DROP TABLE IF EXISTS ProfileSection;
CREATE TABLE ProfileSection (
  profilesection_id int(8) NOT NULL auto_increment,
  profilesection_domain_id int(8) NOT NULL,
  profilesection_profile_id int(8) default NULL,
  profilesection_section_name varchar(16) NOT NULL default '',
  profilesection_show tinyint(1) default NULL,
  PRIMARY KEY (profilesection_id),
  CONSTRAINT profilesection_profile_id_profile_id_fkey FOREIGN KEY (profilesection_profile_id) REFERENCES Profile (profile_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `ProfileProperty`
--

DROP TABLE IF EXISTS ProfileProperty;
CREATE TABLE ProfileProperty (
  profileproperty_id int(8) NOT NULL auto_increment,
  profileproperty_type varchar(32) default NULL,
  profileproperty_default varchar(64) default NULL,
  profileproperty_readonly int(1) default '0',
  profileproperty_name varchar(32) NOT NULL default '',
  PRIMARY KEY (profileproperty_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `ProfilePropertyValue`
--

DROP TABLE IF EXISTS ProfilePropertyValue;
CREATE TABLE ProfilePropertyValue (
  profilepropertyvalue_id int(8) NOT NULL auto_increment,
  profilepropertyvalue_profile_id int(8) default NULL,
  profilepropertyvalue_property_id int(8) default NULL,
  profilepropertyvalue_property_value varchar(32) NOT NULL default '',
  PRIMARY KEY (profilepropertyvalue_id),
  CONSTRAINT profilepropertyvalue_profile_id_profile_id_fkey FOREIGN KEY (profilepropertyvalue_profile_id) REFERENCES Profile (profile_id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT profilepropertyvalue_profileproperty_id_profileproperty_id_fkey FOREIGN KEY (profilepropertyvalue_property_id) REFERENCES ProfileProperty (profileproperty_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-------------------------------------------------------------------------------
-- Default Profile properties
-------------------------------------------------------------------------------
DELETE FROM ProfileProperty;
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default, profileproperty_readonly) VALUES ('update_state', 'integer', 1, 1);
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default) VALUES ('level', 'integer', 3);
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default) VALUES ('level_managepeers', 'integer', 0);
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default) VALUES ('access_restriction', 'text', 'ALLOW_ALL');
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default) VALUES ('admin_realm', 'text', '');
INSERT INTO ProfileProperty (profileproperty_name, profileproperty_type, profileproperty_default, profileproperty_readonly) VALUES ('last_public_contact_export', 'timestamp', 0, 1);

