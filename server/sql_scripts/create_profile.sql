DROP FUNCTION IF EXISTS create_profile;

DELIMITER $$
CREATE FUNCTION create_profile (first_name VARCHAR(64), last_name VARCHAR(64), username VARCHAR(64), password VARCHAR(64))
  RETURNS BOOLEAN
BEGIN
  DECLARE user_id INT;
  IF username_in_use(username) THEN
    RETURN FALSE;
  ELSE 
    INSERT INTO profiles (first_name, last_name, username, password) 
      VALUES (first_name, last_name, username, password);
    SET user_id = get_user_id(email);
    INSERT INTO sessions (user_id, token, created)
      VALUES (user_id, uuid(), now());
    RETURN TRUE;
  END IF;
END

  