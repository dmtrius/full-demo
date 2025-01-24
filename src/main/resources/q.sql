                ||  Transactions  ||                                                 || Clients ||
| client_id	 | year |	month    |	day |	time        |	amount  |                | client_id | client_name |
---------------------------------------------------------------                ---------------------------
|     1      | 2024 | January  | 3	  | 05:22:00 PM	| -123.17 |                |    1      | John Snow   |
|     1      | 2024	| January  | 22	  | 10:05:00 AM	| -66     |                |    2      | Rob Stark   |
|     1   	 | 2024	| February | 1	  | 11:10:00 AM	| 3500    |                |    3      | Sansa Stark |
|     1      | 2024	| February | 17	  | 04:00:00 PM	| -599    |                |    4      | Ariya Stark |
|     1      | 2024	| March	   | 1	  | 11:00:00 AM	| 3500    |
|     1      | 2024	| March	   | 2	  | 07:00:00 PM	| -2000   |
|     1      | 2024	| March	   | 31	  | 06:17:00 PM	| -2500   |
|     2      | 2024	| January	 | 3	  | 07:22:00 AM	| -17     |
|     2   	 | 2024	| January	 | 26	  | 04:17:00 PM	| -127    |
|     2   	 | 2024	| February	 | 1	  | 11:01:00 AM	| 2500    |


-- 1. Retrieve all unique clients ID, NAME who has transactions in January
-- 2. Count number of transaction for each month by John Snow
-- 3. Show months where balance of transactions is positive for John snow

-- select t.month from transactions t
--  join left clients c on t.client_id = c.client_id
--  group by t.month
--   having sum(t.amount) > 0
--   and c.name = 'John Snow';

-- select distinct c.client_id, c.client_name from clients c
--     left join transactions t
--         on c.client_id = t.client_id
--     where t.month = "January";

    VALUES (1, 2024, 'January', 3, '05:22:00 PM', -123.17);
    VALUES (1, 2024, 'January', 22, '10:05:00 AM', -66);
    VALUES (1, 2024, 'February', 1,'11:10:00 AM', 3500);
    VALUES (1, 2024, 'February', 17,'04:00:00 PM', -599);
    VALUES (1, 2024, 'March', 1, '11:00:00 AM', 3500);
    VALUES (1, 2024, 'March', 2, '07:00:00 PM', -2000);
    VALUES (1, 2024, 'March', 31, '06:17:00 PM', -2500);
    VALUES (2, 2024, 'January', 3, '07:22:00 AM', -17);
    VALUES (2, 2024, 'January', 26, '04:17:00 PM', -127);
    VALUES (2, 2024, 'February', 1, '11:01:00 AM', 2500);