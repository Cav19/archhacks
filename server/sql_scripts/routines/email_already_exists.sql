DROP FUNCTION IF EXISTS email_already_registered;

DELIMITER $$
CREATE FUNCTION email_already_registered(user_email VARCHAR(256))
  RETURNS BOOLEAN
BEGIN
  DECLARE row_count INT;
  SELECT COUNT(*) INTO row_count 
       FROM profiles
       WHERE email = user_email;
  IF row_count > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END$$
DELIMITER ;