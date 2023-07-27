## Highload architecture 3 homework

### Requirements
Java 17 (to build java sample app)

### Building project and deploying to docker:
Run 'build_and_deploy.sh' script in root directory of project

### Running load on system:
Run 'siege_it.sh' script in root directory of project

### Result on local environment
(Load has been generated between 15:17 and 15:27)
* ctop output while running siege ![ctop_output.png](ctop_output.png)
* Telegraph Influx dashboard ![Telegraf Influx dashboard.png](Telegraf%20Influx%20dashboard.png)
* Elasticsearch dashboard ![Elasticsearch dashboard.png](Elasticsearch%20dashboard.png)
* Mongo dashboard ![Mongo dashboard.png](Mongo%20dashboard.png)