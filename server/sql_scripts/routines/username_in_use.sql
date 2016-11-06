DROP FUNCTION IF EXISTS username_in_use;

DELIMITER $$
CREATE FUNCTION username_in_use(username VARCHAR(256))
  RETURNS BOOLEAN
BEGIN
  DECLARE row_count INT;
  SELECT COUNT(*) INTO row_count 
       FROM profiles prof
       WHERE prof.username = username;
  IF row_count > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END$$
DELIMITER ;