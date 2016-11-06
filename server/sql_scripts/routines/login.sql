DROP PROCEDURE IF EXISTS login;

DELIMITER $$
CREATE PROCEDURE login (IN username VARCHAR(64), IN password VARCHAR(63))
BEGIN
  DECLARE user_id INT;
  DECLARE token VARCHAR(36);
  SET token = NULL;
  
  SELECT id INTO user_id
    FROM profiles prof
    WHERE prof.username = username
      AND prof.password = password
    LIMIT 1;
  
  IF user_id IS NULL THEN 
    SELECT token;
  ELSE
    SET token = uuid();
    UPDATE sessions
      SET sessions.token = token,
          sessions.created = NOW()
      WHERE sessions.user_id = user_id;
    SELECT user_id, token;
  END IF;
END$$
DELIMITER ;
