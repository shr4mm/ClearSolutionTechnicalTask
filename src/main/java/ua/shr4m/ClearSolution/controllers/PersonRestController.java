package ua.shr4m.ClearSolution.controllers;




import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ua.shr4m.ClearSolution.models.Person;
import ua.shr4m.ClearSolution.services.PersonService;
import ua.shr4m.ClearSolution.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/persons")
public class PersonRestController {
    private final PersonService personService;

    @Autowired
    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {
        Person createdPerson = personService.createPerson(person);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id,@Valid @RequestBody Person person) {
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Person>> searchPersonsByDateOfBirthRange(@RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate, @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {
        List<Person> persons = personService.findPersonsByDateOfBirthRange(fromDate, toDate);
        return ResponseEntity.ok(persons);
    }
    @ExceptionHandler(PersonNotFoundException.class)
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
              "Person not found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PersonWrongAge.class)
    private ResponseEntity<PersonErrorResponse> handleWrongAgeException(PersonWrongAge e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person's age is below the legal age", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonNotAllFields.class)
    private ResponseEntity<PersonErrorResponse> handleNotAllFieldsException(PersonNotAllFields e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Not all required fields are provided", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonDateException.class)
    public ResponseEntity<PersonErrorResponse> handlePersonDateException(PersonDateException e) {
        PersonErrorResponse response = new PersonErrorResponse("Person's date of birth is invalid", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
