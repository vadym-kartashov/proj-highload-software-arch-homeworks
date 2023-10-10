## hla30-serverless-calculations Image Converter

Create Lambda function that will convert JPEG to BMP, GIF, PNG

## How to run
Requirements: Amazon SAM installed, AWS CLI configured
1. Execute ```sam build```
2. Execute ```sam deploy``` and provide required parameters (input/output bucket names , buckets should not exist). 
 
## Implementation notes

Implementation is done in Java. Convertor implementation - [App](ImageConverterFunction/src/main/java/org/vkartashov/image/App.java) 
Project structure has been generated using AWS toolkit for 
Intellij IDEA. Lambda definition is described under [template](template.yaml) file. However in addition 
it was required to add S3 access permissions for lambda IAM user.
Trigger is not shown on Labmda UI since it has customer definition. However if we are trying to add similar
trigger we are getting error that it is already defined. Conversion of ~ 3000x4000 file
consumes all 512MB of memory. In order to avoid possible recursion - two separate buckets have been
created: one for input, second for output.
### Input bucket
![input-bucket](./img/input-bucket.png)
### Output bucket
![output-bucket](./img/output-bucket.png)
### Log entries
![log-entries](./img/log-entries.png)