
## Details on how to ship on Production.

I've mentioned some of the points that can be considered to ship the service to production. There can be additional
configurations and setups that can be done based on the requirements.

These are the steps I would take to ship the service to production and for it to be able to serve larger number of requests.
Some of the changes can be made in the code, for example the image processing logic can be moved to a different service.
Other suggestions are related to infrastructure, database, security, monitoring, and other production aspects.

Typically, a MVP Production ready service would have the below functionalities.
  * CRUD operations for Post
  * CRUD operations for Comment
  * Add replies to Comment.
  * Add images to comments - gifs, etc
  * React to posts,comments - like, dislike, etc
  * Share posts with other users
  * Like count, comment count, share count on posts
  * Like counts on Comments
  * Later on, we can add support to multiple images and videos to posts.

### Image Processing Logic
* The Image processing logic can be part of a different service altogether, that can be scaled independently.
    * We can implement event-driver architecture using Kafka Topics/AWS SNS/AWS SQS to communicate between services.
    * The flow would be as below
        * User invokes /post
        * The ContentServer app creates the post entity, uploads original image to S3 and publishes on a Kafka Topic A.
        * The ImageProcessing service listens to the Kafka Topic A, gets the file urls from the topic, processes the image and uploads to S3.
        * After completion, the ImageProcessing service publishes the processed image url to another Kafka Topic B, on which the ContentServer has subscribed
        * The ContentServer receives the event and updates the post entity with the processed image url.
        * The post can then be shown to the user with the processed image. For this, the ContentServer can send an event to the FeedService, that adds the post to the user feed.
* We can also use a third party service like Cloudinary to process the images.
* We can integrate a CDN to cache the images and serve them faster to the user.

### Security
* We need to integrate TLS/SSL to upgrade the server to HTTPS for secure communication.
* We can integrate use a Load Balancer to distribute the load across multiple instances of the service.
* We can use an API Gateway to manage the APIs and route the requests to the appropriate service.
  It can be configured to have Bot Protection, Rate Limiting, etc. We can use AWS API Gateway.
* To setup an authentication logic in the service to validate user request we can use spring security filter chains.
    * We can integrate a SSO service like Okta to manage the authentication.
    * For username/password based authentication, we can use signed JWT tokens. The password can be hashed using a library like Bcrypt(adds salt + hash) for secure storage in db.
        * Everytime a user makes a request, we can check the JWT token in the request header and validate it.
        * For the first time, the user can login using username/password and get a JWT token. The token can be stored in the browser's local storage.
        * For subsequent requests, the token can be sent in the header.
        * The token can have an expiry time, after which the user needs to login again.
        * If user wants to invalidate token, we can use a blacklist to store the token and check the token in the blacklist before validating.
    * We can use request matchers to decide which endpoints need authentication and which don't.
    * We can implement CSRF tokens to prevent CSRF attacks.
    * We can also implement OAuth2.0 with OpenID, along with 2FA.
* We can use AWS Cognito for authentication, AWS WAF for Web Application Firewall, AWS Shield for DDoS Protection.

### Testing
* We'll have to write unit tests for the service and make sure to cover all use cases.
  I've covered some basic user cases in the tests.
    * We can use JUnit and Mockito for writing unit tests. We can also use SpringBootTest for UTs
* We can use Postman for API testing.
* We can use JMeter/Vegeta for load testing.
* We can integrate SonarQube in our CI/CR for code quality and coverage checks during PRs.
* We can use Gatling for performance testing.
* We can setup Karate for Integration testing of APIs end to end. We can create a new module inside this repo to have all integration tests

### Scalability, Availability and Performance

* We can use Kubernetes for scaling the service. We can use Horizontal Pod Autoscaler to scale the pods based on CPU/Memory usage.
* We can Global and local load balancers to distribute the load across multiple instances in multiple regions.
* We can shard our database to handle more requests. We can use MongoDB sharding for this along with consistent hashing to distribute the data across shards.
* We can brainstorm data access patterns and create db indexes for faster access.
* We can also use managed databases like AWS DynamoDB for scaling as can support trillions of requests per day.
* We can implement our web server using Non blocking I/O frameworks like SpringBoot Webflux and netty.
* If we're using Java/Kotlin, we can use ForkJoinPool and Virtual threads for parallel processing. Similar concepts from other languages can be used.
* We can use AWS CloudFront for CDN to cache the images and serve them faster to the user.
* We can implement event-driven flow for async cross service communication.

### Deployment and CI/CD
* We should maintain different configurations for different environments like Dev, QA, Staging, Prod.
* We can integrate vault into our helm charts to populate sensitive creds during deployment. It can be used as a init container
  when the service deployment starts.
* We can use Jenkins/TravisCI/CircleCI for CI/CD. I've created just a sample Jenkinsfile for the pipeline.
* We can use Docker for containerization. I've added a simple Dockerfile to create an image.
* We can use Kubernetes for orchestration.
* We can use Helm for package management. I've added a sample helm chart that will deploy the service.
* We can use Terraform for to create and deploy resources.
* We can use AWS S3 for storing images data.
* We can use AWS EKS for Kubernetes for deployment and AWS ECR for storing docker images. We can implement a blue-green deployment strategy to minimise downtime.
* We can use AWS Lambda for serverless functions.
* We can use AWS Route 53 for DNS.
* We can use AWS SNS/SQS for message queuing and event driven flow.
* We can use AWS DynamoDB/MongoDB for NoSQL database.
* We can use AWS Elasticache for caching.
* We can use AWS VPC for networking.
* We can use AWS IAM for access management.

### Monitoring
* We can use Prometheus for monitoring service metrics
* We can use Grafana for visualization and tracking service metrics like number of API requests, JVM heap usage, etc
* Grafana also supports log integration which can be used view logs from different services
* We can setup tracing using Jaeger/OpenTelemetry to track the request flow across services.

### Logging and Exception Handling
* We can use Grafana logs/LogDNA/New Relic for logging.
* We can setup different logg levels for different services.
* SpringBoot allows to setup centralised loggers for different classes and packages using AspectJ.
* We need to setup robust exception handling to reduces the chances of service failure. I've setup some exception handlers in the exceptions package.
* We can track errors on tools like Grafana and setup alerts for critical errors.

### Backup and Recovery
* We can have the database replicated across multiple regions for backup and availability.
* We can have a circuit breaker implementation in the service to handle failures and fallback services to handle failing requests.
  I've implemented circuit breakers and rate limiters using Resilience4J in the service.

### Disaster Recovery
* We can restore the data from the backup in case of a disaster.
* We can have a DR plan in place to handle disasters. For example, if there is AWS outage in a region, we can bring our services up in another region.
* We should have DR mock drills every few months to make sure the DR plan works.

### Cost Optimization
* We can use AWS Cost Explorer to track the costs.
* We can schedule jobs to cleanup the original uploaded images after a certain period of time.
* We can batch multiple requests to AWS S3 to save costs.

### Compliance
* Since we're storing a lot of user data, we need to be GDPR compliant. We can use AWS services like AWS KMS for encryption.

### Documentation
* We can use OpenAPI for API documentation. I've added a sample api.yaml file for the /post endpoint.
* Any new code that we add should have proper documentation. This can be part of the Story/Task that is assigned to the developer.
* There should be clear documentation on how to setup the service, how to run the tests, how to deploy the service, any features being added/deprecated etc.

### Training
* For complex designs, we can have a training session for the other team to understand the design and the architecture.
* We can have sessions with Tech Leads/Architects/CTO to brainstorm innovative designs and architectures.

### Support
* We can have a support channel where teams can raise issues and get them resolved.
* We should have clear RCA for any issues that occur in production.
* We can have an agreement on support time SLAs.

### API SLAs
* We can have defined SLAs for the APIs. For example, the /post API should respond in less than 50 ms.
* Any API that doesn't meet the SLA should be investigated and fixed.
* We can use Prometheus/Grafana to track the API response times.