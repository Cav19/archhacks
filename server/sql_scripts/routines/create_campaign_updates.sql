DROP FUNCTION IF EXISTS add_campaign_update_time;

DELIMITER $$
CREATE FUNCTION add_campaign_update_time (user_id INT, campaign_id INT, update_time DATETIME)
  RETURNS BOOLEAN
  BEGIN
    IF campaign_exists_for_user(user_id, campaign_id) THEN
      INSERT INTO campaign_updates (campaign_id, update_time) 
        VALUES (campaign_id, update_time);
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END$$
DELIMITER ;
  