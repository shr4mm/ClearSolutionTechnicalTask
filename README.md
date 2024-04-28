1.1 An email field has been added to the Person class
1.2 The name field has been added to the Person class
1.3 The surname field has been added to the Person class
1.4 The dateOfBirth field has been added to the Person class
1.5 The address field has been added to the Person class. But since the field is not required, it is not in the main constructor
1.6 The phoneNumber field has been added to the Person class. But since the field is not required, it is not in the main constructor
2.1 Added the ability to create a new user over 18 years old, otherwise an error will be thrown; this restriction is also taken from the properties file (application.properties).
To create a user you need to send a POST request to the api/persons address.
2.2-2.3 Added the ability to update the user. One method captures two jobs. As many user rows will be updated as will be sent by the PUT request.
To update a user you need to send a PUT request to the address api/persons/{id}
2.4 Added the ability to delete a user by sending a DELETE request to api/persons/{id}
2.5 Added the ability to search for users by date of birth. Added check that "FROM" is less than "TO", otherwise an error will be thrown
To find a list of people by date of birth, you need to make a GET request to api/persons/search?from=#-01&to=#
3. PersonRestController and PersonService are covered by unit tests.
4. Added error handling for REST applications that send JSON messages in the format
{
message:
timestamp:
};
5. All API responses are provided in JSON format
6. For this REST application I used a Postgres database and linked it to my application using JPA


