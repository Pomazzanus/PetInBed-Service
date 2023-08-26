package com.example.PiB;

import org.assertj.core.util.Arrays;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@JsonTest
public class PiBJsonTests {
    @Autowired
    private JacksonTester<Pet> json;

    @Autowired
    private JacksonTester<Pet[]> jsonList;
    @Autowired
    private ResourceLoader resourceLoader;
    private Pet[] petArr;

    @BeforeEach
    void setUp() {
        petArr = Arrays.array(
                new Pet(99L, "Antonio", "Pomazzanus"),
                new Pet(100L, "Valerio", "Pomazzanus"),
                new Pet(101L, "Egorio", "Pomazzanus"),
                new Pet(102L, "Vladimio", "Pomazzanus"),
                new Pet(103L, "Eugenio", "Pomazzanus"));
    }

    @Test
    void PetListSerializationTest() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:example/Pet/list.json");
        assertThat(jsonList.write(petArr)).isStrictlyEqualToJson(resource);
    }

    @Test
    void PetListDeserializationTest() throws IOException {
        String expected="""
                [
                   {"id": 99, "petName": "Antonio", "owner": "Pomazzanus" },
                   {"id": 100, "petName": "Valerio", "owner": "Pomazzanus" },
                   {"id": 101, "petName": "Egorio", "owner": "Pomazzanus" },
                   {"id": 102, "petName": "Vladimio", "owner": "Pomazzanus" },
                   {"id": 103, "petName": "Eugenio", "owner": "Pomazzanus" }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(petArr);
    }

    @Test
    public void PetSerializationTest() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:example/Pet/single.json");
        Pet pet = petArr[0];
        assertThat(json.write(pet)).isStrictlyEqualToJson(resource);
        assertThat(json.write(pet)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(pet)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(pet)).hasJsonPathStringValue("@.petName");
        assertThat(json.write(pet)).extractingJsonPathStringValue("@.petName")
                .isEqualTo("Antonio");
    }

    @Test
    public void PetDeserializationTest() throws IOException, JSONException {
        String expected = """
                {
                    "id": 99,
                    "petName": "Antonio",
                    "owner": "Pomazzanus"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new Pet(99L,"Antonio", "Pomazzanus"));
        assertThat(json.parseObject(expected).getId()).isEqualTo(99L);
        assertThat(json.parseObject(expected).getPetName()).isEqualTo("Antonio");
    }
}
