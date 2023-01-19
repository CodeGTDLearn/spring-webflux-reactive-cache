## Spring-WebFlux-Replicaset

### Index
* [WebFlux](#webflux)
* [Application.Yml](#application_yml)
* [Docker](#docker)
* [Docker Secrets](#docker-secrets)
* [Testcontainers](#testcontainers)
* [Tests Junit 5](#tests-junit-5)
* [GitGuardian pre commit-githook](#gitguardian)
* [Reactive Cache](#reactive-cache)

### WebFlux
1. RestControllers

### Application_yml
1. Importation of properties:
    1. [PropertySource](https://www.baeldung.com/configuration-properties-in-spring-boot)
2. Yml filesystem-Format
3. Custom Logging.pattern.console

### Docker
1. Compose
    1. _Specific file:_ **docker-compose.yml**
       1. Profiles:
          1. compose-dev-Replicaset: single-node
             1. [compose-replicaset-singlenode](https://stackoverflow.com/questions/60671005/docker-compose-for-mongodb-replicaset)
          2. compose-dev-Standalone: standalone-db
          3. compose-prod-replicaset: three nodes
    2. Environment:
       1. compose variables
       2. Modular env_files
          1. [Tutorial](https://www.youtube.com/watch?v=1je3VxDF67o)
    3. Running SH-Scripts
2. Dockerfile
    1. _Specific file:_ **Dockerfile**
3. Batch Scripts:
    1. Parametric-scripts (env_variables)
        1. Parametric-scripts IDE execution
    2. Reusing bat-scripts:
        1. ex.: compose-up.bat using clean.bat
4. SH Scripts:
    1. Running
    2. Environment variables
        1. [Loading](https://zwbetz.com/set-environment-variables-in-your-bash-shell-from-a-env-file/)
        2. [Delete](https://www.baeldung.com/linux/delete-shell-env-variable)
5. Docker images safety
   1. Aspects:
      1. Delete the \run\secrets\<all-secrets> does not solve the problem because the historial layers in the docker 
         can contains some credentials
   2. Source:
      1. [Finding leaked credentials in Docker images](https://www.youtube.com/watch?v=SOd_XMIGRqo&t=435s)

### Docker-Secrets
1. Disclaimer:
   1. Docker secrets needs to be supported by the image that will use it.
      1. Examples of images supported:
         1. MySQL offical docker-image
         2. MongoDb official docker-Image
   2. The webapp should support it as well, using the library:
      1. [Spring Boot Docker Secret Starter](https://github.com/rozidan/docker-secret-spring-boot-starter#spring-boot-docker-secret-starter)
      2. Or using a Custom-Implementation
2. How secrets will work?
   1. Problem to solve:
      1. Using simple env_vars(compose-environment) this 'sensitive data'(ex.: password) comes from 'outside'
         1. It is mandatory, exclude those env_vars from VCS (gitignore)
      2. However, the sensitive content(from env_vars) (ex. password) will be visible (docker inspect container);
3. How to solve it?:
   1. Docker-Secrets, can hide the sensitive content, 'blocking' docker-inspect;
   2. How?
      1. Docker-Secrets will show, instead the env_vars_content, the "path-secrets"
      2. HOWEVER, this content will be available.
         1. The 'secret-file' in compose(environment) must have the suffix _FILE
4. Where the Secrets are Storaged in container/service 
   1. Default-Storage Folder:
      1. '/run/secrets', of course, inside the service/worker that is using them
   2. Each SECRET own its file, in the Storage-Folder
   3. [Deletion/removing forbidden:](https://docs.docker.com/engine/swarm/secrets/#advanced-example-use-secrets-with-a-wordpress-service)
   > " After you create a secret,you cannot remove a secret that a service is using. However, 
   you can grant or revoke a running service's access to secrets using docker service update ."
5. Sources:
   1. [secrets-with-docker-compose](https://www.rockyourcode.com/using-docker-secrets-with-docker-compose/)
   2. [secrets-during-development](https://blog.mikesir87.io/2017/05/using-docker-secrets-during-development/)
   3. [docker-secrets](https://docs.docker.com/engine/swarm/secrets/#use-secrets-in-compose)
   4. [earthly.dev](https://earthly.dev/blog/docker-secrets/)
   5. [secured-mongodb-container](https://medium.com/@leonfeng/set-up-a-secured-mongodb-container-e895807054bd)
   6. [Docker Secret in Microservice](https://blogmilind.wordpress.com/2018/03/14/docker-secret-in-microservice/)
6. Docker Obervations - Docker Folder:
   1. Docker\mongo-secrets folder:
      1. Should be tested in MongoDbClient (MongoCompass)
      2. No API
   2. Docker\mongo-Standalone
      1. Should be tested in HttpClient (Postman)
      1. No API

### Testcontainers
* Containers
  * Automatic replicaset allow test transactions
* Compose
  * MongoDb StandAlone

### Tests Junit 5
1. ConsoleLog Border
2. StepVerifier
3. RestAssured:
    1. RestAssuredWebTestClient:
        1. Reactive RestAssured
    2. JsonSchemaValidator - CDD Contracts Driven Development
        1. Validate Responses
4. Spring Expression Language (SpEL) expressions:
   1. EnabledIf + SpEL
      2. [spring-5-enabledIf](https://www.baeldung.com/spring-5-enabledIf)
      3. [junit-5-conditional-test-execution](https://www.baeldung.com/junit-5-conditional-test-execution)

### GitGuardian
   1. Idea:
      1. Concept:
         1. GitGuardian shield (ggshield) is a CLI runs local-environment or in a CI.
         2. The purpouse is to detect >300 types of secrets + potential security 
            vulnerabilities or policy breaks.
      2. In a nutshell:
         1. Scan files searching SECRETS **_"BEFORE"_** commit/push 
            1. Avoiding to send SECRETS to web (ex.: docker-hub, github, etc...)
   2. Scan-time - the scan can be done:
      1. Pre-commit: prevent send the secrets in GitHistory 
      2. Pre-push: needs to clena githistory (not recommended)
      3. [Pre-commit vs Pre-push](https://youtu.be/uc70CE1MXvM)
      4. docker images using GGShield-CLI
   3. GitGuardian - API-KEY:
      1. export:
         1. use export to send it as env-var in terminal-session  
      2. env-var-file:
         1. create env-var-file inside the repository
            1. ADD IT IN GIT-IGNORE!!
   4. PIP:
      1. Installation:
         1. [Install phyton](https://www.python.org/downloads/)
         2. python.exe -m pip install --upgrade pip
      2. PIP is used to install:
         1. pre-commit:
      ```
      pip install pre-commit
      ```
         2. ggshield:
      ``` 
      pip install ggshield
      ```
   5. Enabling GGshield in the Git-Local-Repository 
      1. [Tutorial](https://youtu.be/ySTG2NODQCg)
      
   7. Source:
      1. [Detect secrets with a pre-commit-githook {export}](https://youtu.be/8bDKn3y7Br4)
      2. [Detect secrets with a pre-push-githook {env-var-file}](https://youtu.be/uc70CE1MXvM)

### Reactive Cache
   1. https://www.baeldung.com/spring-webflux-cacheable
   2. https://github.com/eugenp/tutorials/tree/master/spring-5-webflux-2
   3. Reactive Redis:
      * Redis Reactive Cache library brings reactive cache functionality to Spring Boot WebFlux
      * Source: https://github.com/Bryksin/redis-reactive-cache