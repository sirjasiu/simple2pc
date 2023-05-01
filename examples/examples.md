# Examples

All examples below are called against kong proxy running under http://localhost:8000 (see [docker compose definition](../docker-compose.yml)) 

## Happy path

Create an account
```http request
POST http://localhost:8000/api/v1/accounts
Content-Type: application/json

{
  "name": "johny"
}
```
Expect 
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 0
}
```
Make deposit
```http request
POST http://localhost:8000/api/v1/accounts/:accountId/actions
Content-Type: application/json

{
  "action": "deposit",
  "funds": 120
}
```
Expect 
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 120
}
```
Create an offer
```http request
POST http://localhost:8000/api/v1/offers
Content-Type: application/json

{
"name": "chocolate",
"price": 100
}
```
Expect
```json
{
  "name": "chocolate",
  "id": ":offerId",
  "price": 100,
  "buyerId": null,
  "reservation": false
}
```
Purchase the offer
```http request
POST http://localhost:8000/api/v1/offers/:offerId/actions
Content-Type: application/json

{
  "action": "purchase",
  "price": 100,
  "buyerId": ":accountId"
}
```
Expected result 200

Check account funds
```http request
GET http://localhost:8000/api/v1/accounts/:accountId
```
Expect
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 20
}
```
Check offer
```http request
GET http://localhost:8000/api/v1/offers/:offerId
```
Expect
```json
{
  "name": "chocolate",
  "id": ":offerId",
  "price": 100,
  "buyerId": ":accountId",
  "reservation": false
}
```

## Insufficient funds

Here we try to buy product by the account with insufficient funds.

Create an account
```http request
POST http://localhost:8000/api/v1/accounts
Content-Type: application/json

{
  "name": "johny"
}
```
Expect
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 0
}
```
Make deposit
```http request
POST http://localhost:8000/api/v1/accounts/:accountId/actions
Content-Type: application/json

{
  "action": "deposit",
  "funds": 80
}
```
Expect
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 80
}
```
Create an offer
```http request
POST http://localhost:8000/api/v1/offers
Content-Type: application/json

{
"name": "chocolate",
"price": 100
}
```
Expect
```json
{
  "name": "chocolate",
  "id": ":offerId",
  "price": 100,
  "buyerId": null,
  "reservation": false
}
```
Purchase the offer
```http request
POST http://localhost:8000/api/v1/offers/:offerId/actions
Content-Type: application/json

{
  "action": "purchase",
  "price": 100,
  "buyerId": ":accountId"
}
```
**Expected result 400**

Check account funds
```http request
GET http://localhost:8000/api/v1/accounts/:accountId
```
Expect
```json
{
  "name": "johny",
  "id": ":accountId",
  "funds": 80
}
```
Check offer
```http request
GET http://localhost:8000/api/v1/offers/:offerId
```
Expect
```json
{
  "name": "chocolate",
  "id": ":offerId",
  "price": 100,
  "buyerId": "null",
  "reservation": false
}
```

Offer was not bought (and reservation was canceled), users funds where not used