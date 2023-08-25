package com.example.PiB.Repository;
import com.example.PiB.Pet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PetRepository extends CrudRepository<Pet,Long>, PagingAndSortingRepository<Pet,Long> {
}
