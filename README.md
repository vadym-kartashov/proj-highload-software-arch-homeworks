## Highload architecture 3 homework

### Requirements
Java 17 (to build java sample app)

### Building project and deploying to docker:
Run 'build_and_deploy.sh' script in root directory of project

### Running load on system:
Run 'siege_it.sh 10 1 0' script in root directory of project,
Where 10 is number of concurrent workers, 1 load time in minutes, 0 time between requrests

### Result on local environment
(Load has been generated between 15:17 and 15:27)
* ctop output while running siege ![ctop_output.png](ctop_output.png)
* Telegraph Influx dashboard ![Telegraf Influx dashboard.png](Telegraf%20Influx%20dashboard.png)
* Elasticsearch dashboard ![Elasticsearch dashboard.png](Elasticsearch%20dashboard.png)
* Mongo dashboard ![Mongo dashboard.png](Mongo%20dashboard.png)

### Stress test results (Homework #5)

Run with N concurrent workers (where N in (10,25,50,100)), 5 minute load time, 0 seconds between requests
CMD: ./siege_it.sh $N 5 0

Since CPU has 8 cores, having more concurrent workers  will bring performance degradation. 
Table with results:

| Metric                       | Concurrency 10 | Concurrency 25 | Concurrency 50 | Concurrency 100  |
|------------------------------|----------------|----------------|----------------|------------------|
| Transactions (hits)          | 75950          | 53426          | 54675          | 48914            |
| Availability (%)             | 100.00         | 100.00         | 100.00         | 100.00           |
| Elapsed time (secs)          | 300.32         | 300.41         | 300.10         | 301.00           |
| Data transferred (MB)        | 9.62           | 6.76           | 6.92           | 6.19             |
| Response time (secs)         | 0.04           | 0.14           | 0.27           | 0.61             |
| Transaction rate (trans/sec) | 252.90         | 177.84         | 182.19         | 162.50           |
| Throughput (MB/sec)          | 0.03           | 0.02           | 0.02           | 0.02             |
| Successful transactions      | 75950          | 53426          | 54675          | 48914            |
| Failed transactions          | 0              | 0              | 0              | 0                |
| Longest transaction (sec)    | 3.44           | 4.73           | 6.89           | 9.28             |
| Shortest transaction (sec)   | 0.00           | 0.01           | 0.02           | 0.03             |