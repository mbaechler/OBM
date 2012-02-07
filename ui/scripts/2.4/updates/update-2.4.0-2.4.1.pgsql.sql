UPDATE ObmInfo SET obminfo_value = '2.4.1-pre' WHERE obminfo_name = 'db_version';

DROP TABLE IF EXISTS opush_invitation_mapping;

UPDATE event SET event_privacy=0 WHERE event_privacy IS NULL;
ALTER TABLE event ALTER event_privacy SET DEFAULT 0;
ALTER TABLE event ALTER event_privacy SET NOT NULL;

UPDATE ObmInfo SET obminfo_value = '2.4.1' WHERE obminfo_name = 'db_version';
