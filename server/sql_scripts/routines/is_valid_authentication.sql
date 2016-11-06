DROP FUNCTION IF EXISTS is_valid_authentication;

DELIMITER $$
CREATE FUNCTION is_valid_authentication(user_id INT, token VARCHAR(36))
  RETURNS BOOLEAN
BEGIN
  DECLARE row_count INT;
  SELECT COUNT(*) INTO row_count
    FROM profiles profs
      INNER JOIN sessions sess
        ON profs.id = sess.user_id
    WHERE profs.id = user_id
      AND sess.token = token
      AND NOT sess.token = '';
  
  IF row_count > 0 THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END $$
DELIMITER ;