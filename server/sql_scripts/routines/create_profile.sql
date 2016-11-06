DROP FUNCTION IF EXISTS create_profile;

DELIMITER $$
CREATE FUNCTION create_profile (first_name VARCHAR(64), last_name VARCHAR(64), username VARCHAR(64), password VARCHAR(64))
  RETURNS BOOLEAN
BEGIN
  DECLARE generated_id INT;
  IF username_in_use(username) THEN
    RETURN FALSE;
  ELSE 
    INSERT INTO profiles (first_name, last_name, username, password) 
      VALUES (first_name, last_name, username, password);
      
    SELECT LAST_INSERT_ID() INTO generated_id;
    
    INSERT INTO sessions (user_id, token, created) 
      VALUES (generated_id, '', NOW());
    RETURN TRUE;
  END IF;
END

  