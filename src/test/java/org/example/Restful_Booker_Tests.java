package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Restful_Booker_Tests {

    public static String authToken = "";
    public static int bookingId;

    @BeforeTest
    public void setUp(){
        RestAssured.baseURI="https://restful-booker.herokuapp.com";
        RestAssured.useRelaxedHTTPSValidation();
    }


    @Test(priority = 0)
    public void pingCheckTest(){

        Response response = RestAssured.given()
                .when()
                .get("/ping")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==201);
        Assert.assertTrue(response.getContentType().contains("text/plain"));
        System.out.println(response.asString());
        Assert.assertTrue(response.asString().equals("Created"));


    }

    @Test(priority = 1)
    public void createAuthTest(){

        String requestBody = "{\r\n    \"username\" : \"admin\",\r\n    \"password\" : \"password123\"\r\n}";

        Response response = RestAssured.given()
                .header("Content-Type","application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/auth")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        System.out.println(response.asString());
        JsonPath jsonPath = new JsonPath(response.asString());
        System.out.println(jsonPath.getString("token"));
        authToken=jsonPath.getString("token");
        System.out.println(authToken);



    }

    @Test(priority = 2)
    public void createBooking(){

        String requestBody = "{\r\n    \"firstname\" : \"Abinash\",\r\n    \"lastname\" : \"Rath\",\r\n    \"totalprice\" : 700,\r\n    \"depositpaid\" : true,\r\n    \"bookingdates\" : {\r\n        \"checkin\" : \"2023-03-20\",\r\n        \"checkout\" : \"2023-03-21\"\r\n    },\r\n    \"additionalneeds\" : \"Breakfast\"\r\n}";
        Response response = RestAssured.given()
                .header("Content-Type","application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/booking")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        System.out.println(response.asString());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        bookingId=jsonObject.getInt("bookingid");
        Assert.assertTrue(jsonObject.getJSONObject("booking").get("firstname").toString().equals("Abinash"));


    }

    @Test(priority = 3)
    public void updateBooking(){

        String requestBody = "{\r\n    \"firstname\" : \"Asish\",\r\n    \"lastname\" : \"Rath\",\r\n    \"totalprice\" : 700,\r\n    \"depositpaid\" : true,\r\n    \"bookingdates\" : {\r\n        \"checkin\" : \"2023-03-20\",\r\n        \"checkout\" : \"2023-03-21\"\r\n    },\r\n    \"additionalneeds\" : \"Breakfast\"\r\n}";
        Response response = RestAssured.given()
                .header("Content-Type","application/json")
                .and()
                .header("Cookie","token="+authToken)
                .and()
                .body(requestBody)
                .when()
                .put("/booking/"+bookingId)
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        System.out.println(response.asString());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        Assert.assertTrue(jsonObject.get("firstname").toString().equals("Asish"));


    }

    @Test(priority = 4)
    public void partialUpdateBooking(){

        String requestBody = "{\r\n    \"firstname\" : \"Aarna\",\r\n    \"lastname\" : \"Rath\"\r\n}";
        Response response = RestAssured.given()
                .header("Content-Type","application/json")
                .and()
                .header("Cookie","token="+authToken)
                .and()
                .body(requestBody)
                .when()
                .patch("/booking/"+bookingId)
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        System.out.println(response.asString());
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        Assert.assertTrue(jsonObject.get("firstname").toString().equals("Aarna"));


    }

    @Test(priority = 5)
    public void getBookingByIdTest(){

        Response response = RestAssured.given()
                .when()
                .get("/booking/"+bookingId)
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        Assert.assertTrue(jsonObject.get("firstname").toString().equals("Aarna"));

    }


    @Test(priority = 6)
    public void getBookingByFirstNameAndLastNameTest(){

        Response response = RestAssured.given()
                .queryParam("firstname","Aarna")
                .and()
                .queryParam("lastname","Rath")
                .when()
                .get("/booking")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        JSONArray jsonArray = new JSONArray(response.getBody().asString());
        Assert.assertTrue(jsonArray.getJSONObject(0).getInt("bookingid")==bookingId);


    }


    @Test(priority = 7)
    public void getBookingByFirstNameTest(){

        Response response = RestAssured.given()
                .queryParam("firstname","Aarna")
                .when()
                .get("/booking")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        JSONArray jsonArray = new JSONArray(response.getBody().asString());
        Assert.assertTrue(jsonArray.getJSONObject(0).getInt("bookingid")==bookingId);
    }


    @Test(priority = 8)
    public void getBookingByLastNameTest(){

        Response response = RestAssured.given()
                .queryParam("lastname","Rath")
                .when()
                .get("/booking")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        JSONArray jsonArray = new JSONArray(response.getBody().asString());
        Assert.assertTrue(jsonArray.getJSONObject(0).getInt("bookingid")==bookingId);

    }


    @Test(priority = 9)
    public void getAllBookingsTest(){

        Response response = RestAssured.given()
                .when()
                .get("/booking")
                .then()
                .extract().response();

        Assert.assertTrue(response.getStatusCode()==200);
        Assert.assertTrue(response.getContentType().contains("application/json"));
        JSONArray jsonObject = new JSONArray(response.getBody().asString());
        Assert.assertTrue(jsonObject.toString().contains(String.valueOf(bookingId)));

    }


    @Test(priority = 10)
    public void deleteBooking(){

         Response response = RestAssured.given()
                .header("Content-Type","application/json")
                .and()
                .header("Cookie","token="+authToken)
                .when()
                .delete("/booking/"+bookingId)
                .then()
                .extract().response();
        System.out.println(response.getStatusCode());
        Assert.assertTrue(response.getStatusCode()==201);
        Assert.assertTrue(response.getContentType().contains("text/plain"));
        Assert.assertTrue(response.asString().equals("Created"));

    }



}
