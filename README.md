# simple2pc
Simple 2 phase commit implementation with springBoot microservices and KongApiGtw as a coordinator

# Goal

The goal here is to demonstrate how Kong Api GTW can be used as a simple coordinator for distributed 2 phase commit 
implementation. See https://en.wikipedia.org/wiki/Two-phase_commit_protocol for details.

# Design

The solution consist of two elements:
* kong api gtw plugin
* java simple 2 phase commit implementation based on spring boot.

## Plugin

For specified route `simple2pc` plugin is applied in kongApiGtw. Configuration of this plugin defines the list of urls 
where the call should be propagated. 

In the first phase plugin propagates raw messages to every of mentioned endpoints and 
expects `202` (ACCEPTED) response for VOTE:YES state and any other for VOTE:NO, e.g. after bad request, service error 
etc.

The plugin also expects `location` header among response headers, so it could send the second phase request with *commit* or *abort* message against this url. In both cases PATCH method is executed with the following body:
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

> NOTE: For implementation check [handler.lua](kong/simple2pc/handler.lua) - this implementation also supports url interpolation based on uri captures and request body

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
When 2 phase commit sequence is required proper call to _prepareJob_ facility can be made, e.g.:
```java
prepareJob.prepare("withdraw", new WithdrawJobData(accountId, value));
```
This call executes `prepare` method from corresponding handler, stores the data for the expected second phase's 
call and returns the id of job which can be either committed or aborted. Job is available via 
```http request
PATCH http://example.com/api/v1/jobs/:jobId
```
> NOTE: _common-jobs_ is autoconfigurable spring module, it delivers necessary services and controllers to the 
> spring boot application it is used in. It also delivers its own flyway db migration in the simple2pc schema (see 
> **io.medness.simple2pc.infrastructure.MultiModuleFlywayMigrationStrategy** for details)

# Example

In the example implemented in the following project we are trying to use 2pc in the distributed purchase operation.
Solution consist of two separate microservices:
* account
* offer.

When user wants to puchase a product available in the offer service it needs to send action of purchasing the product and at the same time it must spend necessary funds controlled in the account service. Several cases may occur here:
* happy path - user has necessary funds and offer is available,
* user doesn't has required funds - product should not be sold,
* user wants to buy product for insufficient price - product should not be sold and funds should not be used,
* user wants to buy already bought product - funds should not be used,
* some problems during performing purchase operation may occur.

In all cases except the happy path both user's funds and offer's state should be rolled back.

## Account

In the account service simple account implementation is delivered, with account and funds it holds. Account can be 
created, fetched and two main operation on its funds can be executed: deposit and withdraw, e.g.
```http request
POST http://example.com/api/v1/accounts 
Content-Type: application/json
{
   "name": "John Smith"
}
```
```http request
POST http://example.com/api/v1/accounts/:accountId/actions
Content-Type: application/json
{
   "action": "deposit",
   "funds": 120.0
}
```
```http request
POST http://example.com/api/v1/accounts/:accountId/actions
Content-Type: application/json
{
   "action": "withdraw",
   "funds": 20.0
}
```
```http request
GET http://example.com/api/v1/accounts/:accountId
```
result after those operation should be as follows:
```json
{
  "name": "John Smith",
  "id": ":accountId",
  "funds": 100.0
}
```

Account service also delivers additional, 2pc action api:
```http request
POST http://example.com/api/v1/accounts/:accountId/prepare-actions
Content-Type: application/json
{
   "action": "withdraw",
   "funds": 20.0
}
```
This operation if allowed ends up with 202 status code and delivers url to job that can be either committed or aborted.
> NOTE: For the simplicity of the example in the prepare phase funds are not blocked, only validation if account 
> exists and if it has enough funds is checked. In commit those funds are taken. Abort does nothing.

## Offer

Offer service holds offers with their prices. Offer can be created and purchased.  
```http request
POST http://example.com/api/v1/offers 
Content-Type: application/json
{
   "name": "Chocolate",
   "price": 1.50
}
```
```http request
POST http://example.com/api/v1/offers/:offerId/actions
Content-Type: application/json
{
   "action": "puchase",
   "buyerId": ":accountId"
   "price": 1.50
}
```
```http request
GET http://example.com/api/v1/offers/:offerId
```
result after those operation should be as follows:
```json
{
  "name": "Chocolate",
  "price": 1.50,
  "id": ":offerId",
  "buyerId": ":accountId",
  "reservation": false
}
```

Offer service also delivers additional, 2pc action api. This api delivers reservation functionality for the offer. In the first phase offer is reserved for the account, during the second phase it is either purchased by the same account - in _commit_, or the reservation is canceled - in _abort_.
```http request
POST http://example.com/api/v1/offers/:offerId/prepare-actions
Content-Type: application/json
{
   "action": "purchase",
   "price": 1.5,
   "buyerId": ":accountId"
}
```
This operation if allowed ends up with 202 status code and delivers url to job that can be either committed or aborted.
Result after this operation should be as follows:
```json
{
  "name": "Chocolate",
  "price": 1.50,
  "id": ":offerId",
  "buyerId": ":accountId",
  "reservation": true
}
```

## Kong

Kong's configuration delivers several functionalities necessary for this solution (see [kong/kong.yaml](/kong/kong.yaml)):
1. it exposes `/api/v1/accounts*` and `/api/v1/offers*` endpoints (note that services are not exposed themselves)
2. it forbids access to `*/prepare-actions` endpoints, those are available only in the internal network
3. it provides translation from one method's body to another (in account when we are making purchase action must be 
   changed from _purchase_ to _withdraw_ and _price_ to _funds_) - this mapping is however only exposed internally, 
   for call to host **kong**
4. eventually it overrides the default `/api/v1/offers/:offerId/actions` method and uses **simple2pc** plugin for 
   performing distributed transaction

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
## Examples 

Check [examples](examples/examples.md). 

## Checklist
- [x] common-jobs
- [x] account
- [X] offer
- [X] docker images build
- [X] kong plugin
- [X] execution
- [X] example calls
