# Juca API

A monolith service referencing [Caju](https://caju.com.br), to put in practice "credit transaction" management.

---

## Getting Started

### Dependencies

- [Docker 🐳](https://www.docker.com/)

### Running

- Run docker compose:

```shell
docker compose up -d --build
```

- That's it! Your application is available on [localhost:8080/api](http://localhost:8080/api).

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
    "wallets": [
      {
        "balance": 0,
        "merchantCategory": "MEAL"
      },
      {
        "balance": 0,
        "merchantCategory": "FOOD"
      },
      {
        "balance": 0,
        "merchantCategory": "CASH"
      }
    ]
  }
  ```

#### Response Properties

| name                        | description                                              |
|-----------------------------|----------------------------------------------------------|
| id                          | **Int**<br/>New account identifier.                      |
| wallets[?].balance          | **String**<br/>New wallet balance. Usually it will be 0. |
| wallets[?].merchantCategory | **String**<br/>Wallet benefit category.                  |

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
    "balance": 200.00
  }
  ```

#### Request Properties

| name                                              | description                                                                                                                              |
|---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| accountId<span style="color: red">*</span>        | **Int**<br/>Account identifier, it'll be used to search the wallet to be updated. It should be a positive integer value.                 |
| amount<span style="color: red">*</span>           | **Float**<br/>Value to be credited on wallet. It should be a positive float value, with at most 14 integer digits and 2 fraction digits. |
| merchantCategory<span style="color: red">*</span> | **String**<br/>Category to identify the benefit to receive the credit.                                                                   |

#### Response Properties

| name    | description                                        |
|---------|----------------------------------------------------|
| balance | <strong>Float</strong><br/>Updated wallet balance. |

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
    "wallets": [
      {
        "balance": 0,
        "merchantCategory": "MEAL"
      },
      {
        "balance": 200,
        "merchantCategory": "FOOD"
      },
      {
        "balance": 0,
        "merchantCategory": "CASH"
      }
    ]
  }
  ```

#### Request Parameters

| name                                | description                                                                  |
|-------------------------------------|------------------------------------------------------------------------------|
| id<span style="color: red">*</span> | **Int** (Query)<br/>Account identifier, it'll be used to search the wallets. |

#### Response Properties

| name                        | description                             |
|-----------------------------|-----------------------------------------|
| id                          | **Int**<br/>Account identifier.         |
| wallets[?].balance          | **String**<br/>Wallet balance.          |
| wallets[?].merchantCategory | **String**<br/>Wallet benefit category. |

### Transact

Endpoint to handle the card transaction.

- **Host**: `localhost:8080/api`
- **Method**: `POST`
- **URL**: `/v1/transactions`
- **Request Headers**:
  - X-Max-Request-Duration
- **Request Body**:
  ```json
  {
    "externalId": "123",
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

| name                                                    | description                                                                                                      |
|---------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| X-Max-Request-Duration<span style="color: red">*</span> | **Int** (Header)<br/>Max request duration (in milliseconds), a timeout exception will be thrown if it's reached. |

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
        "externalId": "123",
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

| name                                | description                                                                  |
|-------------------------------------|------------------------------------------------------------------------------|
| id<span style="color: red">*</span> | **Int** (Query)<br/>Account identifier, it'll be used to search the wallets. |

