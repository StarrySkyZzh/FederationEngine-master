INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)
VALUES (1, 'Paul', 32, 'California', '20000.00' );

UPDATE company
   SET id=100, name='paul', age='32'
WHERE id=1;