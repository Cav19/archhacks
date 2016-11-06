DROP FUNCTION IF EXISTS send_message;

DELIMITER $$
CREATE FUNCTION send_message (fromId INT, toId INT, message VARCHAR(256))
  RETURNS BOOLEAN
BEGIN
  IF NOT user_exists(fromId) THEN
    RETURN FALSE;
  ELSEIF NOT user_exists(toId) THEN
    RETURN FALSE;
  ELSEIF fromId = toId THEN
    RETURN FALSE;
  ELSE   
    INSERT INTO messages (sender_id, reciever_id, message)
      VALUES (fromId, toId, message);
    RETURN TRUE;
  END IF;
END$$
DELIMITER ;
    
    