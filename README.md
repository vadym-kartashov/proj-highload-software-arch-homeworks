# HLA 9 Homework - SQL Databases

40M Users SQL script is generated with [generate_sql_script.sh](./generate_sql_script.sh)
In order to be propagated into MariaDB upon deployment it should be moved to [dbinit](./dbinit) folder
To run Java app with testing routine ./mvnw spring-boot:run


## Test results Part 1 - Index types

Dates are generated within range 1950-01-01 and 2021-12-31. <br/>
There are two key dates picked in order to select particular percent of total records: 1950-01-30 and 1975-12-31
For convenience User table is created with 3 date fields, one per each index option.
Queries that are used here: <br/>
1) 1% - less than one percent of records is expected to be returned with this query ``` "SELECT COUNT(*) FROM users WHERE date_of_birth <= '1950-01-30'" ```
2) 50% #1 - a bit more than 50% of records expected to be queried ``` "SELECT COUNT(*) FROM users WHERE date_of_birth <= '1975-12-31'" ```
3) 50% #2 - a bit less than 50% is expected to be queried ``` "SELECT COUNT(*) FROM users WHERE date_of_birth > '1975-12-31'" ```
4) 99% ``` "SELECT COUNT(*) FROM users WHERE date_of_birth > '1950-01-30'" ```

Test approach here is following:
1) Update docker-compose.yml with corresponding buffer pool size and log file size
2) Start docker-compose with 40 million records SQL script provided within MariaDB container
3) Execute prepared Select criteria against fields with different index types

| Selection criteria | Index structure | Count of selected records to total in (%) | Buffer pool size 134 MB & Log file size 100 MB | Buffer pool size 8 GB & Log file size 100 MB | Buffer pool size 134 MB & Log file size 2 GB | Buffer pool size 8 GB & Log file size 2 GB |
|--------------------|-----------------|-------------------------------------------|------------------------------------------------|----------------------------------------------|----------------------------------------------|--------------------------------------------|
| **1%**             | No index        | 0.164%                                    | 5059 ms                                        | 5059 ms                                      | 10536 ms                                     | 4762 ms                                    |
| **50% #1**         | No index        | 51.01%                                    | 3280 ms                                        | 3280 ms                                      | 10022 ms                                     | 3253 ms                                    |
| **50% #2**         | No index        | 48.98%                                    | 3248 ms                                        | 3248 ms                                      | 10205 ms                                     | 3221 ms                                    |
| **99%**            | No index        | 99.84%                                    | 3149 ms                                        | 3149 ms                                      | 9655 ms                                      | 3054 ms                                    |
| **1%**             | B-tree          | 0.164%                                    | 23 ms                                          | 23 ms                                        | 14 ms                                        | 13 ms                                      |
| **50% #1**         | B-tree          | 51.01%                                    | 2966 ms                                        | 2966 ms                                      | 3611 ms                                      | 2824 ms                                    |
| **50% #2**         | B-tree          | 48.98%                                    | 2595 ms                                        | 2595 ms                                      | 3376 ms                                      | 2650 ms                                    |
| **99%**            | B-tree          | 99.84%                                    | 3401 ms                                        | 3401 ms                                      | 6270 ms                                      | 3338 ms                                    |
| **1%**             | Hash            | 0.164%                                    | 8 ms                                           | 8 ms                                         | 13 ms                                        | 8 ms                                       |
| **50% #1**         | Hash            | 51.01%                                    | 1885 ms                                        | 1885 ms                                      | 3627 ms                                      | 1886 ms                                    |
| **50% #2**         | Hash            | 48.98%                                    | 1766 ms                                        | 1766 ms                                      | 3410 ms                                      | 1735 ms                                    |
| **99% index**      | Hash            | 99.84%                                    | 3312 ms                                        | 3312 ms                                      | 6719 ms                                      | 3325 ms                                    |

## Test results Part 2 - Data flushing strategies

Test approach here is following:
1) Update docker-compose.yml with corresponding buffer pool size and log file size
2) Start docker-compose with 40 million records SQL script provided within MariaDB container
3) Update innodb_flush_log_at_trx_commit into the required value
4) Run insert of 1 000 000 records with concurrency of 10 threads

| innodb_flush_log_at_trx_commit                | Buffer pool size 134 MB & Log file size 100 MB | Buffer pool size 8 GB & Log file size 100 MB | Buffer pool size 134 MB & Log file size 2 GB | Buffer pool size 8 GB & Log file size 2 GB |
|-----------------------------------------------|------------------------------------------------|----------------------------------------------|----------------------------------------------|--------------------------------------------|
| **Flush Each Second (0)**                     | 77357 ms                                       | 25263 ms                                     | 74722 ms                                     | 23676 ms                                   |
| **Flush Each Commit (1)**                     | 104844 ms                                      | 29377 ms                                     | 99990 ms                                     | 24275 ms                                   |
| **Log Each Commit and Flush Each Second (2)** | 100491 ms                                      | 26159 ms                                     | 94469 ms                                     | 22749 ms                                   |