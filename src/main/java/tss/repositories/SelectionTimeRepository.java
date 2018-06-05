package tss.repositories;

import org.springframework.data.repository.CrudRepository;
import tss.entities.SelectionTimeEntity;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * @author ljh-A3
 */
public interface SelectionTimeRepository extends CrudRepository<SelectionTimeEntity, Integer> {
    Optional<SelectionTimeEntity> findByStartAndEnd(Timestamp start, Timestamp end);
    List<SelectionTimeEntity> findAll();


    Optional<SelectionTimeEntity> findById(Long id);
    //List<SelectionTimeEntity> findBy

    boolean existsById(Long id);
}