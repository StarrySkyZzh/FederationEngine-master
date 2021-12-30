-- Function: notify_trigger()

-- DROP FUNCTION notify_trigger();

CREATE OR REPLACE FUNCTION notify_trigger()
  RETURNS trigger AS
$BODY$
DECLARE
BEGIN
  IF TG_OP = 'UPDATE' 
  THEN
       PERFORM pg_notify('mymessage', TG_OP || ',' || TG_TABLE_NAME || ',old:' || OLD || ',new:' || NEW);
  ELSEIF TG_OP = 'INSERT'
  THEN PERFORM pg_notify('mymessage', TG_OP || ',' || TG_TABLE_NAME || ',new:' || NEW);
  ELSE
       PERFORM pg_notify('mymessage', TG_OP || ',' || TG_TABLE_NAME || ',old:' || OLD);
  END IF;
  RETURN NULL;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION notify_trigger()
  OWNER TO unisa;

