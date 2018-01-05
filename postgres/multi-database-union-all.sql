--------------------------------------------------
--Purpose:
--Joining multiple databases which contain the
--same or similar information you would like to
--join.
--------------------------------------------------
CREATE OR REPLACE FUNCTION get_new_customers() RETURNS void
AS $$
BEGIN
 
DROP TABLE IF EXISTS pg_temp.temp_table_name;
 
PERFORM dblink_connect('CONN-NAME1','host=127.0.0.1 user=USER password=PASS dbname=NAME');
PERFORM dblink_connect('CONN-NAME2','host=127.0.0.1 user=USER password=PASS dbname=NAME');
  
  CREATE TEMPORARY TABLE temp_table_name as (
    SELECT *
  FROM dblink('CONN-NAME1',
    'SELECT 
      id,
      account_number,
      create_date
     FROM customer 
  ') AS t (
  id BIGINT             ,
  account_number BIGINT ,
  create_date TIMESTAMP
  )
  
  UNION ALL
  
  SELECT *
  FROM dblink('CONN-NAME2',
    'SELECT 
      id,
      account_number,
      create_date
     FROM customer 
  ') AS t (
  id BIGINT             ,
  account_number BIGINT ,
  create_date TIMESTAMP
  ));

PERFORM dblink_disconnect('CONN-NAME1');
PERFORM dblink_disconnect('CONN-NAME2');

END;
$$ LANGUAGE plpgsql;
