SELECT CREATE_PROFILE('Ned', 'Read', 'needarb', 'cat');
SELECT CREATE_PROFILE('Ben', 'Mathers', 'bamther', 'dog');

SELECT CREATE_CAMPAIGN(1, 'Tobacco', TRUE);
SELECT CREATE_CAMPAIGN(1, 'Alcohol', FALSE);
SELECT CREATE_CAMPAIGN(1, 'Diet', TRUE);


CALL get_user_campaigns(1, false);
CALL get_user_campaigns(1, true);

SELECT MAKE_FRIENDS(1, 2);

CALL get_user_friends(1);
CALL get_user_friends(2);

CALL login('bamther', 'dog');

SELECT 
    IS_VALID_AUTHENTICATION(2,
            '05885b84-a3ce-11e6-bf44-28d244160283');
SELECT IS_VALID_AUTHENTICATION(1, '');

SELECT 
    *
FROM
    sessions;

SELECT USERNAME_IN_USE('needarb')

