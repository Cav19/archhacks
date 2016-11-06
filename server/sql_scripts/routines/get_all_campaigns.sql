DROP PROCEDURE IF EXISTS get_all_campaigns;

DELIMITER $$
CREATE PROCEDURE get_all_campaigns(list_of_owners TEXT) 
BEGIN
  SELECT id, owner_id, campaign_type
  FROM campaigns
  WHERE FIND_IN_SET(owner_id, list_of_owners)
    AND is_hidden = TRUE;
END$$
DELIMITER ;