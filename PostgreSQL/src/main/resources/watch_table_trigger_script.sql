-- Trigger: watch_table_trigger on all tables

DO $$
DECLARE
    tables CURSOR FOR
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
        ORDER BY tablename;
BEGIN
    FOR table_record IN tables LOOP
        EXECUTE 'DROP TRIGGER IF EXISTS watch_table_trigger ON ' || table_record.tablename;
        EXECUTE 'CREATE TRIGGER watch_table_trigger' || 
        ' AFTER INSERT OR DELETE OR UPDATE ON ' || table_record.tablename ||
        ' FOR EACH ROW EXECUTE PROCEDURE notify_trigger()';
    END LOOP;
END$$;



