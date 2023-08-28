package com.example.PiB.Repository;
import com.example.PiB.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PetRepository extends CrudRepository<Pet,Long>, PagingAndSortingRepository<Pet,Long> {
    Pet findByIdAndOwner(Long id, String owner);
    Page<Pet> findByOwner(String owner, PageRequest petName);
    boolean existsByIdAndOwner(Long id, String owner);
}
