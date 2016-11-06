DROP PROCEDURE IF EXISTS get_user_campaigns;

DELIMITER $$
CREATE PROCEDURE get_user_campaigns (IN user_id INT, IN is_self BOOLEAN)
BEGIN
  IF is_self THEN
    SELECT id, campaign_type, is_hidden 
      FROM campaigns
      WHERE owner_id = user_id;
  ELSE
    SELECT id, campaign_type, is_hidden
      FROM campaigns
      WHERE owner_id = user_id AND is_hidden = FALSE;
  END IF;
END$$

DELIMITER ;
  