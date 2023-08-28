package com.example.PiB;

import com.example.PiB.Repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/pet")
public class MainController {
    private PetRepository petRepo;

    public MainController(PetRepository petRepository){
        this.petRepo = petRepository;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pet> findById(@PathVariable Long id, Principal principal){
        Pet pet = findPet(id, principal);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Pet>> findAll(Pageable pageable, Principal principal) {
        Page<Pet> page = petRepo.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "petName"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createPet(@RequestBody Pet newPetRequest, UriComponentsBuilder ucb, Principal principal) {
        Pet petWithOwner = new Pet(null, newPetRequest.getPetName(), principal.getName());
        Pet savedPet = petRepo.save(petWithOwner);
        URI locationOfNewCashCard = ucb
                .path("pet/{id}")
                .buildAndExpand(savedPet.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> putPet(@PathVariable Long id, @RequestBody Pet updatePet, Principal principal){
        Pet pet = findPet(id, principal);
        if(pet != null){
            Pet updatedPet = new Pet(pet.getId(), updatePet.getPetName(), principal.getName());
            petRepo.save(updatedPet);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deletePet(@PathVariable Long id, Principal principal){
        if (petRepo.existsByIdAndOwner(id, principal.getName())) {
            petRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Pet findPet(Long id, Principal principal) {
        return petRepo.findByIdAndOwner(id, principal.getName());
    }
}
