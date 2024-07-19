# hla25-aws-ec2-elb

## Simple Setup

Create 2 micro instances in AWS
Setup application load balancer and assign instances to it
Try Round Robin and sticky sessions load balancing alogrithms

## AWS Configuration
AWS is configured with 2 instances and 2 target groups<br>
#### EC2 instances configuration:
![EC2-instances](./aws-configuration/EC2-instances.png)
#### Target group 1 configuration:
![EC2-targert-group1](./aws-configuration/Target-group-1-configuration.png)
#### Target group 2 configuration:
![EC2-targert-group2](./aws-configuration/Target-group-2-configuration.png)

## AWS Load Balancer with Round Robin algorithm configuration and test
By default all requests are distributed between instances with round robin algorithm
#### Load balancer default configuration:
![ELB-round-robin-configuration](./aws-configuration/ELB-round-robin-configuration.png)
#### Request to load balancer:
![ELB-round-robin-request](./aws-configuration/ELB-round-robin-request.png)
#### Cookies saved after response (No cookies saved since in round robin case there is no need in them):
![ELB-round-robin-cookies](./aws-configuration/ELB-round-robin-cookies.png)

## AWS Load Balancer with sticky sessions configuration
Sticky sessions are configured with 1 minute timeout. AWS load balance is using AWSLBTG cookie in order to identify is request should 'stick' to the same instance. 
#### Load balancer configuration:
![ELB-sticky-configuration](./aws-configuration/ELB-sticky-configuration.png)
#### Request to load balancer:
![ELB-sticky-request](./aws-configuration/ELB-sticky-request.png)
#### Cookies saved after response:
![ELB-sticky-cookies](./aws-configuration/ELB-sticky-cookies.png)