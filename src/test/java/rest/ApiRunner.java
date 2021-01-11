package rest;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.core.util.EnvironmentVariables;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rest.model.Book;

import java.util.List;

@RunWith(SerenityRunner.class)
public class ApiRunner {

    // tag::setup[]
    private String theRestApiBaseUrl;
    private EnvironmentVariables environmentVariables; // <1>
    private Actor sam; // <2>

    @Before
    public void configureBaseUrl() {
        theRestApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl") // <3>
                .orElse("http://localhost:5000/api");

        sam = Actor.named("Sam the supervisor").whoCan(CallAnApi.at(theRestApiBaseUrl)); // <4>
    }
    // end::setup[]

    @Test
    @When("User list return empty with response message as {string} and response code {int}")
    public void fetch_every_book(String message, int code) {

        // tag::fetch_every_user[]
        sam.attemptsTo(
                Get.resource("/books") // <1>
        );

        sam.should(
                seeThatResponse("all the expected users should be returned",
                        response -> response.body("data.author", hasItems("John Smith", "Jane Archer"))) // <2>
        );
        // end::fetch_every_user[]

        // tag::fetch_every_user_data[]
        List<String> titles = SerenityRest.lastResponse().path("data.title"); // <1>
        assertThat(titles).contains("SRE 101", "DevOps is a lie");
        // end::fetch_every_user_data[]

        // tag::fetch_every_user_object[]
        List<Book> books = SerenityRest.lastResponse()
                .jsonPath()
                .getList("data", Book.class); // <1>

        assertThat(books).hasSize(0);
        // end::fetch_every_user_object[]

        sam.should(
                seeThatResponse(message,
                        response -> response.statusCode(code))
        );

    }

    // tag::add_a_book[]
    @Test
    @When("User add a book to store")
    public void add_a_book() {
        sam.attemptsTo(
                Post.to("/books")
                        .with(request -> request.header("Content-Type", "application/json") // <1>
                                .body("{\"author\": \"John Smith\",\"title\": \"SRE 101\"}") // <2>
                        )
        );
    }
    // end::add_a_book[]

    // tag::empty_fields[]
    @Test
    @When("User try to add a book to store with empty title and author")
    public void empty_fields() {
        sam.attemptsTo(
                Post.to("/books")
                        .with(request -> request.header("Content-Type", "application/json") // <1>
                                .body("{\"author\": \" \",\"title\": \" \"}") // <2>
                        )
        );
    }
    // end::empty_fields[]

    // tag::add_a_book_with_id[]
    @Test
    @When("User try to add a book to store with id")
    public void add_a_book_with_id() {
        sam.attemptsTo(
                Post.to("/books")
                        .with(request -> request.header("Content-Type", "application/json") // <1>
                                .body("{\"id\": \"i1\",\"author\": \"John Smith\",\"title\": \"SRE 101\"}") // <2>
                        )
        );
    }
    // end::add_a_book_with_id[]

    // tag::success_message[]
    @Test
    @Then("User saw success message as {string} with response code {int}")
    public void success_message(String message, int code) {
        sam.should(
                seeThatResponse(message,
                        response -> response.statusCode(code))
        );
    }
    // end::success_message[]

    // tag::return_book_just_added[]
    @Test
    @Then("GET should return same book")
    public void return_book_just_added() {
        sam.attemptsTo(
                Get.resource("/books")
        );

        sam.should(
                seeThatResponse("books should be returned",
                        response -> response.statusCode(200)
                                .body("data.title", hasItems("SRE 101")))
        );
    }
    // end::return_book_just_added[]

    // tag::error_message[]
    @Test
    @Then("User saw error message as {string} with response code {int}")
    public void error_message(String message, int code) {
        sam.should(
                seeThatResponse(message,
                        response -> response.statusCode(code))
        );
    }
    // end::error_message[]

}
