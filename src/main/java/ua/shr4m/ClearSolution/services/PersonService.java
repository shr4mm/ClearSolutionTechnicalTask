package ua.shr4m.ClearSolution.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.shr4m.ClearSolution.config.AppConfig;
import ua.shr4m.ClearSolution.models.Person;
import ua.shr4m.ClearSolution.repositories.PersonRepository;
import ua.shr4m.ClearSolution.util.PersonDateException;
import ua.shr4m.ClearSolution.util.PersonNotAllFields;
import ua.shr4m.ClearSolution.util.PersonNotFoundException;
import ua.shr4m.ClearSolution.util.PersonWrongAge;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final AppConfig appConfig;
    @Autowired
    public PersonService(PersonRepository personRepository, AppConfig appConfig) {
        this.personRepository = personRepository;
        this.appConfig = appConfig;
    }


    public Person createPerson(Person person) {
        int legalAge = appConfig.getLegalAge();
        if (calculateAge(person.getDateOfBirth()) < legalAge) {
            throw new PersonWrongAge();
        }
        if (person.getEmail().isEmpty() || person.getName().isEmpty()
                || person.getSurname().isEmpty() || person.getDateOfBirth() == null
        ){
            throw new PersonNotAllFields();
        }
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person updatedPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException());


        if (updatedPerson.getEmail() != null) {
            existingPerson.setEmail(updatedPerson.getEmail());
        }
        if (updatedPerson.getName() != null) {
            existingPerson.setName(updatedPerson.getName());
        }
        if (updatedPerson.getSurname() != null) {
            existingPerson.setSurname(updatedPerson.getSurname());
        }
        if (updatedPerson.getDateOfBirth() != null) {
            existingPerson.setDateOfBirth(updatedPerson.getDateOfBirth());
        }
        if (updatedPerson.getAddress() != null) {
            existingPerson.setAddress(updatedPerson.getAddress());
        }
        if (updatedPerson.getPhoneNumber() != null) {
            existingPerson.setPhoneNumber(updatedPerson.getPhoneNumber());
        }

        return personRepository.save(existingPerson);
    }

    public void deletePerson(Long id) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException());
        personRepository.deleteById(id);
    }

    public List<Person> findPersonsByDateOfBirthRange(Date fromDate, Date toDate) {
        if (fromDate.after(toDate)) {
            throw new PersonDateException();
        }
        List <Person> personList = personRepository.findByDateOfBirthBetween(fromDate, toDate);
        if (personList.isEmpty()){
            throw new PersonNotFoundException();
        }
        return personList;
    }

    private int calculateAge(Date birthDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(localBirthDate, currentDate);
        int age = period.getYears();
        if (currentDate.isBefore(localBirthDate.plusYears(age))) {
            age--;
        }
        return age;
    }
}
