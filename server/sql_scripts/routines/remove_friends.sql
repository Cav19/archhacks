DROP FUNCTION IF EXISTS create_remove_friends;

DELIMITER $$
CREATE FUNCTION remove_friends (id_one INT, id_two INT) 
  RETURNS BOOLEAN
BEGIN
  IF are_friends(id_one, id_two) THEN 
    DELETE FROM friendships
    WHERE (person_one_id = id_one AND person_two_id = id_two)
      OR  (person_two_id = id_one AND person_one_id = id_two);
      RETURN TRUE;
  ELSE 
      RETURN FALSE;
  END IF;
END$$
DELIMITER ;

