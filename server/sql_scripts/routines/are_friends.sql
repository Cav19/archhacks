DELIMITER $$
CREATE FUNCTION are_friends (id_one INT, id_two INT) 
	RETURNS BOOLEAN
BEGIN
	DECLARE count_rows SMALL_INT;
	
	SELECT COUNT(*) INTO count_rows
	  FROM friendships
	  WHERE person_one_id = id_one
        AND person_two_id = id_two;
	
	IF count_rows > 0 THEN
	  RETURN TRUE;
	END IF;
	
	SELECT COUNT(*) INTO count_rows
	  FROM friendships
	  WHERE person_one_id = id_one
	    AND person_two_id = id_two;
		
	IF count_rows > 0 THEN
	  RETURN TRUE;
	END IF;
	
	RETURN FALSE;
END$$
DELIMITER ;

