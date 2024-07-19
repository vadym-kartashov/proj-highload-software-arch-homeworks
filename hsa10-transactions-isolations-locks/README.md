# hla10-locks

## Isolations & locks 

Set up percona and postgres and create an InnoDB table <br/>
By changing isolation levels and making parallel queries, reproduce the main problems of parallel access: <br/>
1) lost update <br/>
2) dirty read <br/>
3) non-repeatable read <br/>
4) phantom read <br/>

### Project setup
Docker compose file has been prepared to set up MariaDB and PostgreSQL. 
Tests were done through python script [sql-scripts-automation](./scripts/sql-scripts-automation.py)

### Dirty read
A "dirty read" in SQL refers to the scenario where a transaction reads data that has been modified but not yet committed by another transaction. This means that the data being read is in a temporary, uncommitted state and could potentially be rolled back. If the data is rolled back after being read by the first transaction, then the first transaction has essentially read data that "never existed" from a logical perspective.

| TX1                                             | TX2                                         |
|-------------------------------------------------|---------------------------------------------|
| START TRANSACTION;                              |                                             |
| UPDATE accounts SET balance = 200 WHERE id = 1  |                                             |
|                                                 | START TRANSACTION;                          |
|                                                 | SELECT balance FROM accounts WHERE id = 1   |        
|                                                 | COMMIT;                                     |
| ROLLBACK;                                       |                                             |

| Isolation level  | MariaDB | PostgreSQL |
|------------------|---------|------------|
| READ UNCOMMITTED | Y       | N          |
| READ COMMITTED   | N       | N          |
| REPEATABLE READ  | N       | N          |
| SERIALIZABLE     | N       | N          |

### Lost update

The Lost Update problem occurs when two or more transactions select the same row and then update it based on the value originally selected. Each transaction is unaware of the other transactions, so the last update overwrites updates made by the other transactions.</br>
In case of PostgreSQL for REPEATABLE_READ and SERIALIZABLE an exception has been encountered - An error occurred: could not serialize access due to concurrent update. 

| TX1                                                 | TX2                                                  |
|-----------------------------------------------------|------------------------------------------------------|
| START TRANSACTION;                                  |                                                      |
| SELECT balance FROM accounts WHERE id = 1           |                                                      |
|                                                     | START TRANSACTION;                                   |
|                                                     | SELECT balance FROM accounts WHERE id = 1;           |
|                                                     | UUPDATE accounts SET balance = 100 + 20 WHERE id = 1 |
|                                                     | COMMIT;                                              |
| UPDATE accounts SET balance = 100 + 10 WHERE id = 1 |                                                      |
| COMMIT;                                             |                                                      |

| Isolation level  | MariaDB | PostgreSQL |
|------------------|---------|------------|
| READ UNCOMMITTED | Y       | Y          |
| READ COMMITTED   | Y       | Y          |
| REPEATABLE READ  | Y       | E          |
| SERIALIZABLE     | N       | E          |
### Non-repeatable read
A "non-repeatable read" refers to a situation in database transactions where a value read by a transaction is modified by another transaction before the first transaction is complete. This means that if the first transaction tries to read the value again, it may get a different result, making the read "non-repeatable."

| TX1                                         | TX2                                             |
|---------------------------------------------|-------------------------------------------------|
| START TRANSACTION;                          |                                                 |
| SELECT balance FROM accounts WHERE id = 1;  |                                                 |
|                                             | START TRANSACTION;                              |
|                                             | UPDATE accounts SET balance = 250 WHERE id = 1  |
|                                             | COMMIT;                                         |
| SELECT balance FROM accounts WHERE id = 1;  |                                                 |
| COMMIT;                                     |                                                 |

| Isolation level  | MariaDB | PostgreSQL |
|------------------|---------|------------|
| READ UNCOMMITTED | Y       | Y          |
| READ COMMITTED   | Y       | Y          |
| REPEATABLE READ  | N       | N          |
| SERIALIZABLE     | N       | N          |
### Phantom read
A "phantom read" occurs in the context of database transactions when a row that wasn't previously in the result set of a query appears in a subsequent result set, or a previously existing row disappears from a subsequent result set, due to inserts, deletes, or updates made by another transaction. Essentially, new rows emerge or existing rows vanish, like "phantoms," between consecutive reads within the same transaction.

| TX1                                         | TX2                                              |
|---------------------------------------------|--------------------------------------------------|
| START TRANSACTION;                          |                                                  |
| SELECT * FROM accounts WHERE balance < 150; |                                                  |
|                                             | START TRANSACTION;                               |
|                                             | UPDATE accounts SET balance = 200 where id = 1   |
|                                             | COMMIT;                                          |
| SELECT * FROM accounts WHERE balance < 150; |                                                  |
| COMMIT;                                     |                                                  |

| Isolation level  | MariaDB | PostgreSQL |
|------------------|---------|------------|
| READ UNCOMMITTED | Y       | Y          |
| READ COMMITTED   | Y       | Y          |
| REPEATABLE READ  | N       | N          |
| SERIALIZABLE     | N       | N          |
