DROP FUNCTION IF EXISTS get_user_id;
DELIMITER $$

CREATE FUNCTION get_user_id(username VARCHAR(64))
  RETURNS INT
BEGIN
  DECLARE user_id INT;
  SELECT id INTO user_id
    FROM profiles prof
    WHERE prof.username = username;
  RETURN user_id;
END$$

DELIMITER ;