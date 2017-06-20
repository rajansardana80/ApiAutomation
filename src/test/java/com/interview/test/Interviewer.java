package com.interview.test;

import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class Interviewer {
	boolean result1 = true;
	boolean result2 = true;
	public static Response response;
	public static String htmlAsString;
	public static String jsonAsString;
	public static String token;

	@BeforeSuite
	public void configure() {
		RestAssured.baseURI = "https://interviewer-api.herokuapp.com";

	}

	@Test(groups = "demo")
	public void validateInterviewer() {
		given().get("https://interviewer-api.herokuapp.com/").then()
		.statusCode(200).log().all();
		response = given().get("https://interviewer-api.herokuapp.com/").then()
				.contentType(ContentType.HTML).extract().response();

		assertTrue(response.asString().contains("Interviewer says hello!"),
				"The response is not (Interviewer says hello!)");

	}

	@Test(groups = "demo")
	public void validateLogin() {
		given().header("Accept", "application/json")
		.contentType("application/json").post("/login").then()
		.statusCode(200).log().all();
		response = given().header("Accept", "application/json")
				.contentType("application/json").post("/login").then()
				.contentType(ContentType.JSON).extract().response();

		token = response.path("token");

		assertTrue((response.path("token") != null && !token.isEmpty()),
				"Login token is null or empty");

	}

	@Test(groups = "demo", dependsOnMethods = "validateLogin")
	public void validateTransactions() {
		given().header("Accept", "application/json")
		.header("Authorization", "Bearer " + token)
		.get("/transactions").then().statusCode(200).log().all();
		response = given().header("Accept", "application/json")
				.header("Authorization", "Bearer " + token)
				.get("/transactions").then().contentType(ContentType.JSON)
				.extract().response();

		List<String> currency = response.path("currency");
		List<String> id = response.path("id");

		for (String curr : currency) {

			assertEquals(curr, "GBP", "The currency is not GBP");

		}

		for (String transactionId : id) {

			assertTrue(transactionId != "", "The id in response is empty ");

		}

	}

	@Test(groups = "demo", dependsOnMethods = "validateLogin")
	public void validateBalance() {
		given().header("Accept", "application/json")
		.header("Authorization", "Bearer " + token).get("/balance")
		.then().statusCode(200).log().all();
		response = given().header("Accept", "application/json")
				.header("Authorization", "Bearer " + token).get("/balance")
				.then().contentType(ContentType.JSON).extract().response();

		assertTrue(response.path("balance") != "",
				"The balance in response is empty ");
		assertEquals(response.path("currency"), "GBP",
				"The currency is not GBP");

	}

	@Test(groups = "demo", dependsOnMethods = "validateLogin")
	public void validateSpend() {
		given().header("Authorization", "Bearer " + token)
		.contentType("application/json")
		.body("{\"date\":\"2016-12-15T10:44:33Z\",\"description\":\"Some item\",\"amount\":\"5.80\",\"currency\":\"GBP\"}")

		.post("/spend").then().statusCode(204).log().all();

	}

}
