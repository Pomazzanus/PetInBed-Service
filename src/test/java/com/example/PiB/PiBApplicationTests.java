package com.example.PiB;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PiBApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnAPetWhenDataIsSaved() {
		ResponseEntity<String> response = restTemplate.getForEntity("/pet/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);
		String petName = documentContext.read("$.petName");
		assertThat(petName).isEqualTo("Antonio");
		String owner = documentContext.read("$.owner");
		assertThat(owner).isEqualTo("Pomazzanus");
	}

	@Test
	void shouldNotReturnAPetWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate.getForEntity("/pet/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@DirtiesContext
	@Test
	void shouldCreateANewPet() {
		Pet newPet = new Pet(null, "Dominic", "Pomazzanus");
		ResponseEntity<Void> createResponse = restTemplate.postForEntity("/pet", newPet, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		String petName = documentContext.read("$.petName");
		String owner = documentContext.read("$.owner");
		assertThat(id).isNotNull();
		assertThat(petName).isEqualTo("Dominic");
		assertThat(owner).isEqualTo("Pomazzanus");
	}
	@Test
	void shouldReturnAllPetsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate.getForEntity("/pet", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int petCount = documentContext.read("$.length()");
		assertThat(petCount).isEqualTo(5);
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101, 102, 103);
		JSONArray petNames = documentContext.read("$..petName");
		assertThat(petNames).containsExactlyInAnyOrder("Antonio", "Valerio", "Egorio", "Vladimio", "Eugenio");
	}

	@Test
	void shouldReturnAPageOfPets() {
		ResponseEntity<String> response = restTemplate.getForEntity("/pet?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfPets() {
		ResponseEntity<String> response = restTemplate.getForEntity("/pet?page=0&size=1&sort=petName,asc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);
		String petName = documentContext.read("$[0].petName");
		assertThat(petName).isEqualTo("Antonio");
	}
}
