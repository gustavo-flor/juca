# Juca API

A monolith service referencing [Caju](https://caju.com.br), to put in practice "credit transaction" management.

---

## Getting Started

### Dependencies

- Docker

### Running

- Run docker compose

```shell
docker compose up -d --build
```

- That's it!

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

### Add Credit

If you want to add money to an account wallet, that's your endpoint.

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

### Find Wallets by Account ID

Endpoint to check the values of each benefit available on an account.

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

### Transact

Endpoint to handle the card transaction.

- **Method**: `POST`
- **URL**: `/v1/transactions`
- **Request Headers**:
  - X-Request-Duration
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
