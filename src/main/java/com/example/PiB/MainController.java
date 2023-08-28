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
import java.util.Optional;

@RestController
@RequestMapping("/pet")
public class MainController {
    private PetRepository petRepo;

    public MainController(PetRepository petRepository){
        this.petRepo = petRepository;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pet> findById(@PathVariable Long id, Principal principal){
        Optional<Pet> petOptional = Optional.ofNullable(petRepo.findByIdAndOwner(id, principal.getName()));
        if (petOptional.isPresent()) {
            return ResponseEntity.ok(petOptional.get());
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
}
