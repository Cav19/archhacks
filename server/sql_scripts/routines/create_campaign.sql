DROP FUNCTION IF EXISTS create_campaign;

DELIMITER $$
CREATE FUNCTION create_campaign (user_id INT, campaign_type ENUM('Tobacco', 'Alcohol', 'Diet'), is_hidden BOOLEAN)
	RETURNS BOOLEAN
BEGIN
	IF user_exists(user_id) THEN
		INSERT INTO campaigns (owner_id, campaign_type, is_hidden) 
			VALUES (user_id, campaign_type, is_hidden);
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
END$$
DELIMITER ;