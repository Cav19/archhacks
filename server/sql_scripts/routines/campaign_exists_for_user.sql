DROP FUNCTION IF EXISTS campaign_exists_for_user;

DELIMITER $$
CREATE FUNCTION campaign_exists_for_user (user_id INT, campaign_id INT)
  RETURNS BOOLEAN
BEGIN
  DECLARE row_count INT;
  SELECT COUNT(*) INTO row_count
    FROM campaigns
    WHERE id = campaign_id
      AND owner_id = user_id;
    
  IF row_count > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END$$
DELIMITER ;