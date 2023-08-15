package com.example.PiB;

public class Pet {
    private Long id;
    private String petName;

    public Pet(Long id, String petName) {
        this.id = id;
        this.petName = petName;
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
}
