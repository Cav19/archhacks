DROP PROCEDURE IF EXISTS get_user_friends;

DELIMITER $$
CREATE PROCEDURE get_user_friends(user_id INT)
  BEGIN
    SELECT id, first_name, last_name, username
    FROM profiles friend_prof 
      INNER JOIN (SELECT person_two_id friend_id
                    FROM friendships
                    WHERE person_one_id = user_id
                  UNION 
                  SELECT person_one_id friend_id
                    FROM friendships
                    WHERE person_two_id = user_id) friend_ids
        ON friend_prof.id = friend_ids.friend_id;	
  END$$
DELIMITER ;