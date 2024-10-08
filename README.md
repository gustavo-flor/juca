# Juca API

A monolith service referencing [Caju](https://caju.com.br), to put in practice "credit transaction" management.

---

## Getting Started

### Dependencies

- [Docker 🐳](https://www.docker.com/)

### Running

- Run docker compose (`8080` port should be available):

```shell
docker compose up -d --build
```

- That's it! Your application is available on [localhost:8080/api](http://localhost:8080/api).

### Development

1. Run docker `docker compose -f docker-compose-dev.yml up -d` to up all service dependencies
2. Run `./gradlew build` to build the project
3. Run `gradlew bootRun --args='--spring.profiles.active=dev'` to start the API

## API Docs

- [OpenAPI](http://localhost:8080/api/v3/api-docs)
- [Swagger UI](http://localhost:8080/api/swagger-ui/index.html)

## Use Cases

- [Create account](#create-account)
- [Add Credit](#add-credit)
- [Find Wallets by Account ID](#find-wallets-by-account-id)
- [Transact](#transact)
- [Get Statement](#get-statement)

### Create Account

Endpoint to create an account and wallets.

- **Host**: `localhost:8080/api`
- **Method**: `POST`
- **URL**: `/v1/accounts`
- **Request Body**: `N/A`
- **Response Body**:
  ```json
  {
    "id": 1,
    "wallet": {
      "foodBalance": 0.00,
      "mealBalance": 0.00,
      "cashBalance": 0.00
    }
  }
  ```

#### Response Properties

| name                        | description                                                      |
|-----------------------------|------------------------------------------------------------------|
| id                          | **Int**<br/>New account identifier.                              |
| wallets.foodBalance         | **String**<br/>New wallet food balance. Usually it will be zero. |
| wallets.mealBalance         | **String**<br/>New wallet meal balance. Usually it will be zero. |
| wallets.cashBalance         | **String**<br/>New wallet cash balance. Usually it will be zero. |

### Add Credit

If you want to add money to an account wallet, that's your endpoint.

- **Host**: `localhost:8080/api`
- **Method**: `POST`
- **URL**: `/v1/credits`
- **Request Body**:
  ```json
  {
    "accountId": 1,
    "amount": 200.00,
    "merchantCategory": "FOOD"
  }
  ```
- **Response Body**:
  ```json
  {
    "id": 1,
    "wallet": {
      "foodBalance": 200.00,
      "mealBalance": 0.00,
      "cashBalance": 0.00
    }
  }
  ```

#### Request Properties

| name                                              | description                                                                                                                              |
|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| accountId<span style="color: red">*</span>        | **Int**<br/>Account identifier, it'll be used to search the wallet to be updated. It should be a positive integer value.                 |
| amount<span style="color: red">*</span>           | **Float**<br/>Value to be credited on wallet. It should be a positive float value, with at most 14 integer digits and 2 fraction digits. |
| merchantCategory<span style="color: red">*</span> | **String**<br/>Category to identify the benefit to receive the credit.                                                                   |

#### Response Properties

| name               | description                                             |
|--------------------|---------------------------------------------------------|
| wallet.foodBalance | <strong>Float</strong><br/>Updated wallet food balance. |
| wallet.mealBalance | <strong>Float</strong><br/>Updated wallet meal balance. |
| wallet.cashBalance | <strong>Float</strong><br/>Updated wallet cash balance. |


### Find Wallets by Account ID

Endpoint to check the values of each benefit available on an account.

- **Host**: `localhost:8080/api`
- **Method**: `GET`
- **URL**: `/v1/accounts/{id}`
- **Request Body**: N/A
- **Response Body**:
  ```json
  {
    "id": 1,
    "wallet": {
      "foodBalance": 200.00,
      "mealBalance": 0.00,
      "cashBalance": 0.00
    }
  }
  ```

#### Request Parameters

| name                                | description                                                                  |
|-------------------------------------|------------------------------------------------------------------------------|
| id<span style="color: red">*</span> | **Int** (Query)<br/>Account identifier, it'll be used to search the wallets. |

#### Response Properties

| name                | description                         |
|---------------------|-------------------------------------|
| id                  | **Int**<br/>Account identifier.     |
| wallets.foodBalance | **String**<br/>Wallet food balance. |
| wallets.mealBalance | **String**<br/>Wallet meal balance. |
| wallets.cashBalance | **String**<br/>Wallet cash balance. |

### Transact

Endpoint to handle the card transaction.

- **Host**: `localhost:8080/api`
- **Method**: `POST`
- **URL**: `/v1/transactions`
- **Request Body**:
  ```json
  {
    "externalId": "964df205-d29d-42a1-8cf0-f684afae6ffe",
    "accountId": 1,
    "amount": 50.00,
    "mcc": "5411",
    "merchant": "PADARIA DO ZE               SAO PAULO BR"
  }
  ```
- **Response Body**:
  ```json
  {
    "code": "00"
  }
  ```

#### Request Parameters

| name                                        | description                                                                                                                                                                                                                                             |
|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| externalId<span style="color: red">*</span> | **UUID**<br/>Unique identifier of the transaction, it'll be used to check uniqueness.                                                                                                                                                                   |
| accountId<span style="color: red">*</span>  | **Int**<br/>Account identifier.                                                                                                                                                                                                                         |
| amount<span style="color: red">*</span>     | **Float**<br/>Value to be debited.                                                                                                                                                                                                                      |
| mcc<span style="color: red">*</span>        | **String**<br/>Merchant category code, basically it's a string with exactly 4 digits (0000). It'll be used to find the right benefit. Known values:<br/>- **FOOD**: `5411` and `5412`<br/>- **MEAL**: `5811` and `5812`<br/>- **CASH**: Any other code. |
| merchant<span style="color: red">*</span>   | **String**<br/>Merchant name with address, it's a string with exactly 40 chars, the first 25 chars should have the merchant name and the rest the address.                                                                                              |


#### Response Properties

| name | description                                                                                                                                                |
|------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| code | **String**<br/>Code to represent the result of the request. Available codes:<br/>- **00**: Approved<br/>- **07**: Error<br/>- **51**: Insufficient balance |


### Get Statement

Endpoint to visualize the statement of an account.

- **Method**: `GET`
- **URL**: `/v1/accounts/{id}/statement`
- **Request Body**: N/A
- **Response Body**:
  ```json
  {
    "transactions": [
      {
        "externalId": "964df205-d29d-42a1-8cf0-f684afae6ffe",
        "origin": "PADARIA DO ZE - SAO PAULO BR",
        "accountId": 1,
        "amount": 50.00,
        "type": "DEBIT",
        "merchantCategory": "FOOD",
        "result": "APPROVED",
        "createdAt": "2024-08-31T13:25:39.474374"
      }
    ]
  }
  ```
#### Request Parameters

| name                                | description                                                                       |
|-------------------------------------|-----------------------------------------------------------------------------------|
| id<span style="color: red">*</span> | **Int** (Query)<br/>Account identifier, it'll be used to search the transactions. |

