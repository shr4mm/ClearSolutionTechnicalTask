package ua.shr4m.ClearSolution;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ua.shr4m.ClearSolution.controllers.PersonRestController;
import ua.shr4m.ClearSolution.models.Person;
import ua.shr4m.ClearSolution.services.PersonService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonRestController.class)
public class PersonRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Test
    public void testCreatePerson() throws Exception {
        Person person = new Person("john@example.com", "Joe", "Doe", new Date());
        when(personService.createPerson(any(Person.class))).thenReturn(person);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Joe")))
                .andExpect(jsonPath("$.surname", is("Doe")));

        Mockito.verify(personService, Mockito.times(1)).createPerson(any(Person.class));
    }

    @Test
    public void testUpdatePerson() throws Exception {
        Person person = new Person("john@example.com", "John", "Doe", new Date());
        when(personService.updatePerson(anyLong(), any(Person.class))).thenReturn(person);

        mockMvc.perform(put("/api/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")));

        Mockito.verify(personService, Mockito.times(1)).updatePerson(anyLong(), any(Person.class));
    }

    @Test
    public void testDeletePerson() throws Exception {
        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(personService, Mockito.times(1)).deletePerson(anyLong());
    }

    @Test
    public void testSearchPersonsByDateOfBirthRange() throws Exception {
        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
        Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");

        Person person1 = new Person("john@example.com", "John", "Doe", fromDate);
        Person person2 = new Person("jane@example.com", "Jane", "Smith", toDate);
        List<Person> personList = Arrays.asList(person1, person2);

        when(personService.findPersonsByDateOfBirthRange(fromDate, toDate)).thenReturn(personList);

        mockMvc.perform(get("/api/persons/search")
                        .param("from", "1990-01-01")
                        .param("to", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[1].name", is("Jane")));

        Mockito.verify(personService, Mockito.times(1)).findPersonsByDateOfBirthRange(fromDate, toDate);
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
