Feature: Testing different requests on the book application

  @SMOKE
  Scenario: Verify that the API starts with an empty store.
    When User list return empty with response message as "No Content" and response code 204

  Scenario: Verify that you can create a new book via PUT.
    When User add a book to store
    Then User saw success message as "The book should have been successfully added." with response code 201
    Then GET should return same book

  Scenario: Verify that you cannot create a duplicate book.
    When User add a book to store
    Then User saw error message as "Another book with similar title and author already exists." with response code 409

  Scenario: Verify that title and author cannot be empty.
    When User try to add a book to store with empty title and author
    Then User saw error message as "title and author cannot be empty." with response code 400

  Scenario: Verify that the id field is read−only.
    When User try to add a book to store with id
    Then User saw error message as "Bad Request" with response code 400
