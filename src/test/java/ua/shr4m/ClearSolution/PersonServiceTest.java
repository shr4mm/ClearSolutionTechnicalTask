package ua.shr4m.ClearSolution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.shr4m.ClearSolution.config.AppConfig;
import ua.shr4m.ClearSolution.models.Person;
import ua.shr4m.ClearSolution.repositories.PersonRepository;
import ua.shr4m.ClearSolution.services.PersonService;
import ua.shr4m.ClearSolution.util.PersonDateException;
import ua.shr4m.ClearSolution.util.PersonNotAllFields;
import ua.shr4m.ClearSolution.util.PersonNotFoundException;
import ua.shr4m.ClearSolution.util.PersonWrongAge;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private PersonService personService;

    @Test
    public void testCreatePerson_ValidPerson_Success() {

        String email = "john@example.com";
        String name = "John";
        String surname = "Doe";

        Date dateOfBirth = new Date(System.currentTimeMillis() - 20 * 365 * 24 * 60 * 60 * 1000L); // 20 years ago
        Person person = new Person(email, name, surname, dateOfBirth);

        when(appConfig.getLegalAge()).thenReturn(18);
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // Act
        Person createdPerson = personService.createPerson(person);

        // Assert
        assertEquals(email, createdPerson.getEmail());
        assertEquals(name, createdPerson.getName());
        assertEquals(surname, createdPerson.getSurname());
        assertEquals(dateOfBirth, createdPerson.getDateOfBirth());
        verify(personRepository, times(1)).save(person);
    }



    @Test
    public void testCreatePerson_PersonUnderLegalAge_ExceptionThrown() {
        // Arrange
        Person person = new Person("john@example.com", "Joe", "Doe", new Date());
        when(appConfig.getLegalAge()).thenReturn(18);

        // Act & Assert
        assertThrows(PersonWrongAge.class, () -> personService.createPerson(person));
        verifyNoInteractions(personRepository);
    }




    @Test
    public void testUpdatePerson_ValidPerson_Success() {
        // Arrange
        Person existingPerson = new Person( "john@example.com", "Joe", "Doe", new Date());
        Person updatedPerson = new Person("john@example.com", "John", "Doe", new Date());
        when(personRepository.findById(anyLong())).thenReturn(java.util.Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // Act
        Person result = personService.updatePerson(1L, updatedPerson);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPerson.getName(), result.getName());
        assertEquals(updatedPerson.getSurname(), result.getSurname());
        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testUpdatePerson_PersonNotFound_ExceptionThrown() {
        // Arrange
        when(personRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        Person updatedPerson = new Person("john@example.com", "John", "Doe", new Date());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.updatePerson(1L, updatedPerson));
        verify(personRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testDeletePerson_ValidId_Success() {
        // Arrange
        Long id = 1L;
        Person existingPerson = new Person("john@example.com", "Joe", "Doe", new Date());
        when(personRepository.findById(anyLong())).thenReturn(java.util.Optional.of(existingPerson));

        // Act
        personService.deletePerson(id);

        // Assert
        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeletePerson_PersonNotFound_ExceptionThrown() {
        // Arrange
        when(personRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        Long id = 1L;

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.deletePerson(id));
        verify(personRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void testFindPersonsByDateOfBirthRange_ValidRange_Success() {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date(fromDate.getTime() + 1000);
        List<Person> persons = Arrays.asList(
                new Person("john@example.com", "John", "Doe", new Date()),
                new Person("jane@example.com", "Jane", "Smith", new Date(toDate.getTime() - 500))
        );
        when(personRepository.findByDateOfBirthBetween(any(Date.class), any(Date.class))).thenReturn(persons);

        // Act
        List<Person> result = personService.findPersonsByDateOfBirthRange(fromDate, toDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(personRepository, times(1)).findByDateOfBirthBetween(any(Date.class), any(Date.class));
    }

    @Test
    public void testFindPersonsByDateOfBirthRange_InvalidRange_ExceptionThrown() {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date(fromDate.getTime() - 1000);

        // Act & Assert
        assertThrows(PersonDateException.class, () -> personService.findPersonsByDateOfBirthRange(fromDate, toDate));
        verifyNoInteractions(personRepository);
    }

    @Test
    public void testFindPersonsByDateOfBirthRange_NoPersonsFound_ExceptionThrown() {
        // Arrange
        Date fromDate = new Date();
        Date toDate = new Date(fromDate.getTime() + 1000);
        when(personRepository.findByDateOfBirthBetween(any(Date.class), any(Date.class))).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.findPersonsByDateOfBirthRange(fromDate, toDate));
        verify(personRepository, times(1)).findByDateOfBirthBetween(any(Date.class), any(Date.class));
    }
}
