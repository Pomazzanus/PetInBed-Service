package com.example.PiB;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/animal")
public class MainController {
    @GetMapping("/{petId}")
    public ResponseEntity<Pet> findById(){
        Pet doggo = new Pet(1L, "Antoha");
        return ResponseEntity.ok(doggo);
    }

    @GetMapping("/")
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
}
