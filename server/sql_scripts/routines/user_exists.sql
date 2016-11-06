DROP FUNCTION IF EXISTS user_exists;

DELIMITER $$
CREATE FUNCTION user_exists (user_id INT)
  RETURNS BOOLEAN
BEGIN
  DECLARE row_count INT;
  SELECT COUNT(*) INTO row_count
    FROM profiles
    WHERE id = user_id;
    
  IF row_count > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END$$
DELIMITER ;