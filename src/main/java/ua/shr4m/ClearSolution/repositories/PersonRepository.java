package ua.shr4m.ClearSolution.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.shr4m.ClearSolution.models.Person;

import java.util.Date;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByDateOfBirthBetween(Date fromDate, Date toDate);
}
