package com.example.PiB;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Objects;

public class Pet {
    @Id
    @Column("ID")
    private Long id;
    @Column("PET_NAME")
    private String petName;
    @Column("OWNER")
    private String owner;

    public Pet(Long id, String petName, String owner) {
        this.id = id;
        this.petName = petName;
        this.owner = owner;
    }
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id && Objects.equals(petName, pet.petName) && Objects.equals(owner, pet.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petName, owner);
    }
}
