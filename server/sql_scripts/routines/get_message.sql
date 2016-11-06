DROP PROCEDURE IF EXISTS get_message;

DELIMITER $$
CREATE PROCEDURE get_message (IN to_id INT) 
BEGIN
  DECLARE message_id INT;
  DECLARE sf_name VARCHAR(64);
  DECLARE sl_name VARCHAR(64);
  DECLARE s_id INT;
  DECLARE msg VARCHAR(256);
  SELECT m.id, sender_id, message, first_name, last_name 
    INTO message_id, s_id, msg, sf_name, sl_name
    FROM messages m 
      INNER JOIN profiles p
        ON m.sender_id = p.id
    WHERE m.reciever_id = to_id
    LIMIT 1;
  
  IF NOT message_id = NULL THEN
    DELETE FROM messages 
      WHERE id = message_id;
  END IF;
  
  SELECT sf_name, sl_name, msg;
END$$

DELIMITER 