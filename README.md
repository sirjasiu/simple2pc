# simple2pc
Simple 2 phase commit implementation with springBoot microservices and KongApiGtw as a coordinator

# Goal

The goal here is to demonstrate how Kong Api GTW can be used as a simple coordinator for distributed 2 phase commit 
implementation. See https://en.wikipedia.org/wiki/Two-phase_commit_protocol for details.

# Design

The solution consist of two elements:
* kong api gtw plugin
* java simple 2 phase commit implementation based on Job-like interface.

## Plugin

For specified route `simple2pc` plugin is applied in kongApiGtw. Configuration of this plugin defines the list of urls 
where the call should be propagated. 

In the first phase plugin propagates raw messages to every of mentioned endpoints and 
expects `202` (ACCEPTED) response for VOTE:YES state and any other for VOTE:NO, e.g. after bad request, service error 
etc.

The plugin also expects `location` header among response headers, so it could send the second phase request with  
*commit* or *abort* message against this url. In both cases PATCH method is executed with the following body:
```json
{
  "state": "{STATE}"
}
```
where state can be either `committed` (for *commit* message) or `aborted` (for *abort*).

If all responses from the first phase result in 202 the `commit` message is send. If at least on response indicated 
that the service was voting:no, `abort` is sent to all endpoints - if the service didn't respond with message that 
could point to a correct second phase endpoint, it is omitted. 

> NOTE: Handing errors in the second phase is beyond this simple implementation.

## Java microservices

Spring boot microservices are used here as a reference implementation to demonstrate usage of the simple2pc plugin.

Microservices use _common-jobs_ library which provides necessary functionality for preparing and then consequent 
committing or aborting of the prepared operation. Mentioned sequence of operations should be implemented by interface 
JobHandler which delivers necessary operations: 
```java
public interface JobHandler<T extends Serializable> {
    
    void prepare(T data);

    void commit(T data);

    void abort(T data);
}
```
and can be executed by port `PrepareJob` (see https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)). 
Mentioned port executes `prepare` method from corresponding handler, stores the data for the expected second phase's 
call and returns the id of job which can be either committed or aborted. Job is available via 
```http request
PATCH {service_url}/api/v1/jobs/{jobId}
```
> NOTE: _common-jobs_ is autoconfigurable spring module, it delivers necessary services and controllers to the 
> spring boot application it is used in. It also delivers its own flyway db migration in the simple2pc schema (see 
> **io.medness.simple2pc.infrastructure.MultiModuleFlywayMigrationStrategy** for details)

# Example

In the example implemented in the following project we are trying to use 2pc in the distributed purchase operation.
Solution consist of two separate microservices:
* account
* offer.

## Account

In the account service simple account implementation is delivered, with account and funds it holds. Account can be 
created, fetched and two main operation on its funds can be executed: deposit and withdraw, e.g.
```http request
POST {serverUrl}/api/v1/accounts 
Content-Type: application/json
{
   "name": "John Smith"
}
```
```http request
POST {serverUrl}/api/v1/accounts/{accountId}/actions
Content-Type: application/json
{
   "action": "deposit",
   "funds": 120.0
}
```
```http request
POST {serverUrl}/api/v1/accounts/{accountId}/actions
Content-Type: application/json
{
   "action": "withdraw",
   "funds": 20.0
}
```
```http request
GET {serverUrl}/api/v1/accounts/{accountId}
```
result after those operation should be as follows:
```json
{
  "name": "John Smith",
  "id": {{accountId}},
  "funds": 100.0
}
```

Account service also delivers additional, 2pc action api:
```http request
POST {serverUrl}/api/v1/accounts/{accountId}/prepare-actions
Content-Type: application/json
{
   "action": "withdraw",
   "funds": 20.0
}
```
This operation if allowed ends up with 202 status code and delivers url to job that can be either committed or aborted.
> NOTE: For the simplicity of the example in the prepare phase funds are not blocked, only validation if account 
> exists and if it has enough funds is checked. In commit those funds are lowered. Abort does nothing.

## Offer

Offer service holds offers with their prices. Offer can be purchased and it can also be reserved. The second 
mechanism is used for the 2pc mechanism. In the first phase offer is reserved for the account, during the second 
phase it is either purchased by the same account - in commit, or the reservation is canceled - in abort. 
```http request
POST {serverUrl}/api/v1/offers 
Content-Type: application/json
{
   "name": "Chocolate",
   "price: 1.50
}
```
```http request
POST {serverUrl}/api/v1/offers/{offerId}/actions
Content-Type: application/json
{
   "action": "puchase",
   "accountId": {accountId}
   "price": 1.50
}
```
```http request
GET {serverUrl}/api/v1/offers/{offerId}
```
result after those operation should be as follows:
```json
{
  "name": "Chocolate",
  "price: 1.50,
  "id": {offerId},
  "accountId": {accountId},
  "reservation": false
}
```

Offer service also delivers additional, 2pc action api:
```http request
POST {serverUrl}/api/v1/offers/{oferId}/prepare-actions
Content-Type: application/json
{
   "action": "purchase",
   "price": 1.5,
   "accountId": {accountId}
}
```
This operation if allowed ends up with 202 status code and delivers url to job that can be either committed or aborted.


## Execution
First build the project and all necessary docker images using build.sh script:
```shell
./build.sh
```

Then execute docker-compose (it should use default docker-compose.yml file):
```shell
docker-compose up -d
```

> NOTE: **docker-compose-dev.yml** is for development purpose, it sets up databases for both account and offer 
> services, you can run it with _docker-compose -f docker-compose-dev.yml up -d_ 

## Checklist
- [x] common-jobs
- [x] account
- [ ] offer
- [X] docker images build
- [ ] kong plugin
- [X] execution