## hla22-sharding Sharding

1) Create 3 docker containers: postgresql-b, postgresql-b1, postgresql-b2
2) Setup horizontal/vertical sharding as it’s described in this lesson and with alternative tool ( citus, pgpool-|| etc )
3) Insert 1 000 000 rows into books
4) Measure performance for reads and writes
5) Do the same without sharding
6) Compare performance of 3 cases ( without sharding, FDW, and approach of your choice )

## Setup

All three cases are described under [docker-compose](./docker-compose.yml) file. Test script is implemented in python and
 located under [main.py](./main.py). Initialization scripts are located under [sql](./sql) folder. 
                
## Results
Since sharding is implemented on one machine, the case without sharding has best results.
Citus performed best for data insertion, FWD performed better for data query.

| SQL Query                                           | Default (seconds) | FWD (seconds) | Citus (seconds) |
|-----------------------------------------------------|-------------------|---------------|-----------------|
| INSERT INTO books ... VALUES %s 1000000             | 65.6767           | 253.9452      | 155.6153        |
| INSERT INTO books ... VALUES %s 100000              | 6.4998            | 25.0667       | 15.9034         |
| INSERT INTO books ... VALUES %s 10000               | 0.7658            | 2.6221        | 1.9319          |
| INSERT INTO books ... VALUES %s 100                 | 0.0297            | 0.1889        | 0.3602          |
| INSERT INTO books ... VALUES %s 10                  | 0.0250            | 0.1751        | 0.3318          |
| SELECT * FROM books                                 | 3.7315            | 8.1600        | 15.5012         |
| SELECT * FROM books WHERE category_id = 1           | 1.2703            | 4.4132        | 5.4098          |
| SELECT * FROM books WHERE category_id = 2           | 1.2589            | 4.3428        | 5.3696          |
| SELECT * FROM books WHERE category_id = 3           | 1.2783            | 0.1798        | 5.3648          |
| SELECT * FROM books WHERE category_id IN (1,2)      | 2.4866            | 8.3426        | 10.3705         |
| SELECT * FROM books WHERE category_id IN (2,3)      | 2.4147            | 4.2923        | 10.3325         |
