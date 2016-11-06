make_friends(user_id_one INT, user_id_two INT)
	-> BOOLEAN of success (FALSE if already exists)
are_friends(user_id_one INT, user_id_two INT)
	-> BOOLEAN of friendship
create_campaign(user_id INT, campaign_type ENUM('Tobacco', 'Alcohol', 'Diet'), is_hidden BOOLEAN)
	-> BOOLEAN of success
campaign_exists_for_user(user_id INT, campaign_id INT)
	-> BOOLEAN of existance
create_profile(first_name VARCHAR(64), last_name VARCHAR(64), username VARCHAR(64), password VARCHAR(64)) 
	-> BOOLEAN of success