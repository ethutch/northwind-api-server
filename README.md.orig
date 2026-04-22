# Northwind API Server

# Purpose
There are already more examples of Spring Java services than the world really needs.  So the goal of this one is different from most.  Rather than another bare-bones example of how to do the most trivial things in the shortest possible example this project aims to be a real world example that showcases best practices and Enterprise considerations.

To this end it includes (at least some) real log messages, error checking, JavaDoc and a full OpenAPI implementaiton, both static: openApi.json and Dynamic: operational web interface.  In short all the stuff everyone omits "to save space".  Here you will find not a project to present a single concept quickly and easilly but an exhaustive and opinionated application that hopes to teach Enterprise grade reasoning.

## Functional goals
The overarching goal of these applications is to demonstrate an end to end flow from web requests to data warehouse.  Since we are constrained by the chosen data model - and the lack of any real world analysis goals one could observe that the information quality of the data warehouse is somewhat light.  But we are not attempting to produce a system of record to sell as fiat acompli.  We are attempting to demonstrate important design patterns.

## The Big Picture
What will ultimately be demonstrated here is the implementation of basic CRUD functions for Customers and Orders.  OpenAPI (Swagger) documentation will be generated through the annotation of Controller Interfaces.  This is strongly recommended as a near zero effort way to generate a sandbox for front end developers.

When changes are made to the Postgres database the new data will be written to an outbox table.  This is the only table that is not part of the standard schema.  This is a critical part of why the application was written.  It demonstrates the "at least once" delivery principal so vitally important in distributed systems.

A Python program is also supplied that will poll the outbox table.  Polling was a conscious choice.  Another obvious design would use Change Data Capture (CDC).  CDC may become the only reasonable design for ultra-high volume databases.  However, it is not the no-brainer choice some may believe.  CDC in Postgres will use a Replication Slot and database logs will not roll off if the consumer falls behind.  Thus rather than an outbox table growing to consume database space - a well monitored consumable - we can be faced with Postgres itself crashing due to exhausting log space.

The point is that this was a carefully considered architectural choice.  This is the sort of choice that determines the ultimate success of a backend system.


## Basic Design
At the most basic level this code implements a Java Spring Boot 3 API endpoint application.  It is suitable for a user facing interface e.g. Angular web app / mobile app or as a functional endpoint for other programs.

It offers basic CRUD functionality across the Customer and Order Domain objects.  These functions are exposed as ReST endpoints and secured with Spring Boot 3 Security.  Admittedly that security today exists as an in-memory map of two users.  But this can be trivially extended within the Spring Framework to any AIM provider desired.

Importantly the **two** security filter chains show how different security needs can be met with different chains.  The first shows how to expose Spring Actuator Health endpoint to unrestricted access - this is essential for health aware systems like Kubernetes.  It also shows basic (user/pass) security on the more sensitive Actuator endpoints e.g. Info.

And finally a completely separate Spring Security Filter Chain shows the real "front door" JWT based access that secures everything else.

Along the way we see examples of Lazy / Eager loading of associated RDBMS entities using JPA annotations.  Primary Key assignment from a Sequence and many other uses of Spring and Java that highlight what a real application really does.

As a major design feature the application originally published changes to the database to Kafka where another application, this time written in Python, consumes the events and writes them to a MongoDB Data Warehouse.

That was fine and those classes are still in the codebase as an example of publishing to Kafka.  But really for an application claiming to show how to do things correctly this was a fairly big design flaw.  

So in V2 direct Kafka access was removed and the application instead writes database updates to an outbox table.  This is a solid Enterprise pattern and solves the problem of database changes that get committed but message broker messages do not get published.

Another repository will be provided with a simple Python program to read the outbox table and publish the Kafka events.  It is worth noting that because the Python Apicurio support is significantly less mature we have chosen here to serialize the already encoded payload into the outbox.  The java code is using Apicurio to retrieve the correct Protbuf definition and then save that to outbox.  This means the Python literally just has to read the payload, publish it, and update the outbox status.  This is a real world pragmatic choice based on what frameworks are available and their level of maturity.

## Data Model
To have a non-trivial data model the Microsoft Northwind Schema has been adopted.  Only a small portion of an application to manage ths domain has been implemented.  But the part that is implemented is intended to be a true best practice on which production quality code can be based.

It can be observed that this data model leaves much to be desired as data models go. But it is universally well know so with the barest minimum of modifications we will use it as is.

The microsoft provided DDL is in the resources directory here.  There is also an AuditFields-DDL.sql file that adds the 4 fields we must have to demonstrate Spring / Hibernate change tracking.

## Java Packages 
The most important design choice is true domain **and** service packages (layers).  Even though one of these layers is often quite sparse collapsing them is not a good design choice. They serve distinct and important architectural purposes.  

Even if it seems pedantic at first as applications mature and new features are added maintainers will come to appreciate having a properly structured home in which new features can naturally live.

Some of these are painfully obvious but each will be at least mentioned here so everyone is on the same page.
#### Config
The config package is familiar to all Spring developers.  These are the classes that define and initialize the Spring Application Context.  This is all the startup plumbing that makes all the Spring magic possible.

#### Controller
Here, as is almost always the best practice we are embracing the Model View Controller (MVC) pattern.  Controller packages define the external endpoints that users of the application hit.  Importantly this is really where security happens.  **Any** missing authorization annotations here are the quintessential red flag!

Beyond this however the controller methods should be almost painfully short.  Don't overburden these endpoints with creeping functionality.

Also as an anti-pattern too often seen: do not call one Controller method from another one.  If there is a valid reason to have so much common logic that this seems like a good idea, extract that logic into a third method.  Endpoints should be just that. Endpoints.

#### Service
The service layer is responsible for transaction control and orchestration of operations.

This is a big job and if it does this well that is a great accomplishment.  And as mentioned this is the place to orchestrate all your complex operations.  What it is not is the place **FOR** these operations. 


#### Declarative Transactions
Service classes are where all the @Transactional annotations belong.

Keep in mind that in Spring Declarative Transactions (the only kind you should be using), transaction control must be in a different class than the actual manipulation of the data.  This is because Spring must have an opportunity to proxy the data manipulation and wrap the transactioning logic around it.  If the called method is in the same class as the @Transaction caller the optimizer may restructure this code such that no proxy is possible.

#### Domain
The domain layer has two big tasks.
1. It is responsible for all business functions.  All the classes that implement the domain functions satisfying the business requirements go here.  Use as many packages and classes as needed.  This is the layer that provides the business value.
2. Only the Domain knows about the physical data model.  As we will see the incoming request and the outgoing response have different representations at different levels.  But the domain owns the physical implementation of the data.  This of course means that when (not if) the physical data model changes, those changes are confined exclusively to the domain layer.

#### Model
This is the Entity ORM classes as is Spring best practice.

#### Repository
No surprise here.  The Spring Repository Interfaces.

#### Security
These are the classes that configure the Security Filter Chains etc.

#### Mapper
Simple but critical transformation classes that handle the transformation of objects as they flow through the system e.g. Request -> Domain -> Response.

#### Util
The ever present helper classes that don't fit anywhere else.



### Data Transfer / Value Objects
The goal is to confine knowledge of physical data representation to only those classes that absolutely must deal with it.

Many opinions exist on the proper way to move data between layers of the system.  In fact if you don't have an opinion you probably haven't yet been bitten by a bad choice.  In this application we pass the raw request into the Service layer.  

On the down side we've moved the external representation beyond the controller layer but that is a conscious choice to allow direct access to the full unimproved request from the proper layer to orchestrate significant functions.  

This is a judgement call and depends mostly on your application.  Consider the complexity of the edits and the frequency of the API changing.

If for example complex edits are required and the API is considered very static then the Service layer is an easy choice.  Conversely if the API changes often and / or the edits are trivial then you can remove complexity from the Service layer by standardizing your input in the Controller.

The most important thing is to be consistent.  If some controller endpoints pass request bodies and some pass a standardized value object it makes the separation inconsistent.  Inconsistency is the enemy. 

It is worth noting that annotation based edits will take place at the controller level when the wire data is deserialized.  But these are by definition constrained to simple things like "is this a valid date".  Complex edits and cross validation is often best handled by the Service layer.

Because the Domain should be sanitized of any API concerns, the Service layer will normalize the request payload into the standard Value Object format before calling the Domain.  The domain will return this VO abstract all the way to the Controller for a response.  As new versions of the API evolve the Controller becomes most aware of which entry point was used and can therefore best ensure the outbound response is formatted correctly.

The end to end data flow then follows.
1. Request comes in to the Dispatcher Servlet
2. Static validation occurs e.g. @NotEmpty @Min etc
3. The request object in instantiated and the controller endpoint is invoked
4. The request object is passed to the Service layer
5. The Service layer applies complex edits as needed
6. The Service layer instatiates the intermediate value object from the request
7. The domain manipulates the physical data model based on the passed command and value object.
8. The domain returns a new value object to the Service.  This is intitially at least the same class type as was input.  Maintaining only one intermediate value object for each domain object is highly desirable and you should consider **addative** changes to the value object. 
9. The service returns the VO to the controller.  This normal exit of the service method will commit the database transaction.
10. The controller returns the VO which complies with the OpenApi specification of what return looks like.  Note that over time the internal VO may also drift from the external response body, especially as multiple versions of the API become supported.


## Logging
People often ask "What should I log."  My short answer to that is: when the system is down at 3 in the morning, what are the things you would like to know in order to fix the problem.

That may sound obvious, but it is sound advice.  Other things to consider, logging should
1. Give visibility into performance.  We don't want to wait until something breaks to find out the system is not performant.  There are lots of tools to examine expensive queries etc.  But there is no reason to leave every bit of this up to your frameworks.  Always keep in mind "What will I want to know later."
2. Be ever mindful of information overload.  Do not fall into the trap of documenting every assignment statement.  If you have tricky logic that you believe can benefit from incredibly detailed logging utilize the **TRACE** level such that important diagnostics can be gathered without the overkill of logs for things that are not broken right now.
3. Log errors.  Sure there will be the never-helpful NullPointerException.  But ask what can I do here to add context so this error will be meaningful to someone that has never seen the code before.
4. Be mindful of security leakage.  While we want to have as much information as possible about failures do not log things whose exposure would cause the security of the system to be compromised.  Obviously passwords, but another example is a credit card number.  The industry standard is PCI DSS.  When a standard exists don't invent your own.  A quick internet search can save you from failing an audit!

Note that we are using a CorrelationInterceptor to pull a unique request identifier from the incoming request header.  That class is smart enough to hunt down that identifier from several different hosting platforms.  This unique correlation Id then appears in every single log statement produced as the request moves through the system.  It is even persisted into the Kafka update message.  Thus we can track a given request from CloudWatch all the way through to MongoDB and beyond.  If this microservice was part of a composite flow the one and only unique correlation Id will link together every action the system takes in response to any given request.  It is not possible to overstate the importance of this.

One final point worth mentioning is the use of a log ***Marker*** in the PUT endpoint of the CustomerController.  Markers like this are a little known global callout that can make scraping logs much easier.  


## Lower Level Details


### OpenApi - Static and Dynamic
Here we have added the OpenApi annotations to the Controller Interfaces.  This keeps the somewhat verbose annotations out of the program code but puts them front and center in the package hierarchy.

Note that during build time a special Spring Profile is used to pick up a different application-openapi-gen.yml.  This is primarilly needed to deconflict the ever popular port 8080.

We are also using the openaip gradle plugin.  It will consume these annotations and generate the openapi.json file suitable for distribution to consumers of your service.

We are also generating the "swagger" page at /swagger-up/index.html.  This is a wonderful tool for consumers.  However it must be properly secured on the production system.  What "properly secured" means of course is completely dependent on what your service does and who its audence is.


## Build and Deploy
This service can be run locally as any Spring Java applcation can.  However the *charts* directory contains the helm chart and values necessary to create a deployment in kubernetes.  This is really the intended environment.

The application depends on Postgres, Apicurio, and has a latent unused dependency on Kafka.  During development all these services are running as containers under docker.  Another repository here will be provided with docker-compose files to bring up all the ancillary requirements.  But access to any existing services can be used by simple updates to the application.yaml.

### Axion Version Numbers
We have migrated away from constant maintenance of a version number in build.gradle and are using the axion plugin.  It is worth reading up on axion.  But the minimum useful info is condensed here.

Axion will label versions based on a git tag.  That tag is created when you run the gradle release task.  It is important to remember to PUSH the tag to your remote repos.

For any commit ahead of the last axion tag the version will be 

`X.Y.Z-<commit hash>-SNAPSHOT`

Each new build will increment Z.  To change other components of the version number create a release.

### Create a new Release
Since we are now using the Axion Release plugin it numbers versions automatically.  Under normal conditions it looks for it's tag and if it is not on HEAD the release becomes

`x.y.z-<commiHash>-SNAPSHOT`

To increment to a new release commit and push all changes and then run the axion release task

`./gradlew release`

To specify what component of the version numer is incremented use the -P parameter
```
# Minor: 0.4.7 → 0.5.0
./gradlew release -Prelease.incrementer=minorVersion

# Major: 0.4.7 → 1.0.0
./gradlew release -Prelease.incrementer=majorVersion

# Patch (default): 0.4.7 → 0.4.8
./gradlew release -Prelease.incrementer=incrementPatch
```
Or even easier just force it!

`./gradlew release -Prelease.forceVersion=0.5.0`

It is necessary to PUSH the tag after the release runs!

`git push origin <tagname>`


### Recommended deployment
The project also contains a Jenkinsfile.  So if you build a Jenkins pipeline you can point at this file and the deployment just happens.


### Bare Minimum to deploy to Kubernetes
For the absolute minimum effort deployment these 3 command will do the trick.

1. Set the version number in the environment
`VERSION=$(./gradlew -q printVersion) && echo $VERSION`



2. Build the docker image.  Note using buildx gives a vast performance improvement.
`docker buildx build --load -t northwind-api-server:$VERSION .`

3. Install the docker image into kubernetes.  Note here is where the version number becomes so important.  If the version does not change helm will not replace the container.
`helm upgrade --install northwind-api-service-dev . \
--set image.tag=$VERSION \
--wait  --timeout 3m`


4. Not recommended, but if you really hate version numbers that much, this will absolutely force a redeploy every time you build add this to deployment.yaml

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: northwind-api-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: northwind-api-service
  template:
    metadata:
      labels:
        app: northwind-api-service
|      annotations:
|        redeployAt: "{{ now | quote }}"
    spec:
      containers:
        - name: northwind-api
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: IfNotPresent

```


### Other useful commands

#### Monitor kafka publishing
You can monitor messages posted to kafka with this command

`kafka-console-consumer --bootstrap-server localhost:9094 --topic customer-events --max-messages 1 | xxd | head -5`


### Generate encrypted passwords:
`htpasswd -bnBC 10 '' 'passw0rd!' | tr -d ':\n'`

#




