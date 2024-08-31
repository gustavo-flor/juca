package com.github.gustavoflor.juca.entrypoint.web.v1

import com.github.gustavoflor.juca.entrypoint.web.v1.request.CreditRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

object Endpoints {
    object AccountController {
        fun create(): ValidatableResponse = RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post("/v1/accounts")
            .then()

        fun findById(id: Long): ValidatableResponse = RestAssured.given()
            .accept(ContentType.JSON)
            .get("/v1/accounts/{id}", id)
            .then()
    }

    object CreditController {
        fun create(request: CreditRequest): ValidatableResponse = RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(request)
            .post("/v1/credits")
            .then()

        fun create(): ValidatableResponse = RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post("/v1/credits")
            .then()
    }

    object TransactionController {
        fun transact(request: TransactRequest, timeoutDuration: String? = null): ValidatableResponse = RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header(ApiHeaders.TIMEOUT_DURATION, timeoutDuration ?: "")
            .body(request)
            .post("/v1/transactions")
            .then()
    }
}
