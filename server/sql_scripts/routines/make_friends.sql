DROP FUNCTION IF EXISTS make_friends;

DELIMITER $$
CREATE FUNCTION make_friends (id_one INT, id_two INT) 
	RETURNS BOOLEAN
BEGIN
	IF are_friends(id_one, id_two) THEN
		RETURN FALSE;
	ELSE 
		INSERT INTO friendships (person_one_id, person_two_id) 
		  VALUES (id_one, id_two);
		RETURN TRUE;
	END IF;
END$$
DELIMITER ;
