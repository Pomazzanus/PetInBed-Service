package com.example.PiB;

//import com.example.PiB.Repository.PetRepository;
import com.example.PiB.Repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class MainController {
    private PetRepository petRepo;

    public MainController(PetRepository petRepository){
        this.petRepo = petRepository;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pet> findById(@PathVariable Long id){
        Optional<Pet> petOptional = petRepo.findById(id);
        if (petOptional.isPresent()) {
            return ResponseEntity.ok(petOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createPet(@RequestBody Pet newPetRequest, UriComponentsBuilder ucb) {
        Pet savedPet = petRepo.save(newPetRequest);
        URI locationOfNewCashCard = ucb
                .path("pet/{id}")
                .buildAndExpand(savedPet.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    /*@GetMapping("/")
    public ResponseEntity<List<Pet>> getAll(){
        Pet doggo = new Pet(1L, "Antoha");
        Pet catto = new Pet(2L, "Artemka");
        Pet doggonio = new Pet(3L, "Alyoshka");
        Pet cattonio = new Pet(4L, "Andreika");
        List<Pet> pets = new ArrayList<Pet>();
        pets.add(doggo);
        pets.add(catto);
        pets.add(doggonio);
        pets.add(cattonio);
        return ResponseEntity.ok(pets);
    }
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody Pet newPetRequest, UriComponentsBuilder ucb) {
        Pet savedPet = PetRepository.save(newPetRequest);
        URI locationOfNewPet = ucb
                .path("Pet/{petId}")
                .buildAndExpand(savedPet.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewPet).build();
    } */
}
