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
| **1%**             | No index        | 0.164%                                    | 10323 ms                                       | 3311 ms                                      | 9549 ms                                      | 4193 ms                                    |
| **50% #1**         | No index        | 51.01%                                    | 10344 ms                                       | 3240 ms                                      | 10080 ms                                     | 3285 ms                                    |
| **50% #2**         | No index        | 48.98%                                    | 10988 ms                                       | 3194 ms                                      | 11191 ms                                     | 3224 ms                                    |
| **99%**            | No index        | 99.84%                                    | 10800 ms                                       | 3005 ms                                      | 10191 ms                                     | 3036 ms                                    |
| **1%**             | B-tree          | 0.164%                                    | 10 ms                                          | 8 ms                                         | 13 ms                                        | 7 ms                                       |
| **50% #1**         | B-tree          | 51.01%                                    | 2620 ms                                        | 1759 ms                                      | 2926 ms                                      | 1829 ms                                    |
| **50% #2**         | B-tree          | 48.98%                                    | 2585 ms                                        | 1633 ms                                      | 2779 ms                                      | 1706 ms                                    |
| **99%**            | B-tree          | 99.84%                                    | 5241 ms                                        | 3280 ms                                      | 5676 ms                                      | 3312 ms                                    |
| **1%**             | Hash            | 0.164%                                    | 12 ms                                          | 7 ms                                         | 13 ms                                        | 7 ms                                       |
| **50% #1**         | Hash            | 51.01%                                    | 2859 ms                                        | 1838 ms                                      | 2954 ms                                      | 1767 ms                                    |
| **50% #2**         | Hash            | 48.98%                                    | 2654 ms                                        | 1696 ms                                      | 2837 ms                                      | 1682 ms                                    |
| **99% index**      | Hash            | 99.84%                                    | 5347 ms                                        | 3261 ms                                      | 5616 ms                                      | 3301 ms                                    |

## Test results Part 2 - Data flushing strategies

Test approach here is following:
1) Update docker-compose.yml with corresponding buffer pool size and log file size
2) Start docker-compose with 40 million records SQL script provided within MariaDB container
3) Update innodb_flush_log_at_trx_commit into the required value
4) Run insert of 1 000 000 records with concurrency of 10 threads

| innodb_flush_log_at_trx_commit                | Buffer pool size 134 MB & Log file size 100 MB | Buffer pool size 8 GB & Log file size 100 MB | Buffer pool size 134 MB & Log file size 2 GB | Buffer pool size 8 GB & Log file size 2 GB |
|-----------------------------------------------|------------------------------------------------|----------------------------------------------|----------------------------------------------|--------------------------------------------|
| **Flush Each Second (0)**                     | 70573 ms                                       | 22076 ms                                     | 71086 ms                                     | 20818 ms                                   |
| **Flush Each Commit (1)**                     | 96778 ms                                       | 25473 ms                                     | 98185 ms                                     | 23319 ms                                   |
| **Log Each Commit and Flush Each Second (2)** | 81704 ms                                       | 24595 ms                                     | 88561 ms                                     | 21517 ms                                   |
