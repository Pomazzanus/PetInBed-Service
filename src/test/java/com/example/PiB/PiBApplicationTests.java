package com.example.PiB;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet/99", String.class);
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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@DirtiesContext
	@Test
	void shouldCreateANewPet() {
		Pet newPet = new Pet(null, "Dominic", null);
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.postForEntity("/pet", newPet, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);
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
	@DirtiesContext
	void shouldUpdateAnExistingPet() {
		Pet petUpdate = new Pet(null, "Dmitrio", null);
		HttpEntity<Pet> request = new HttpEntity<>(petUpdate);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.exchange("/pet/99", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet/99", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		String petName = documentContext.read("$.petName");
		assertThat(id).isEqualTo(99);
		assertThat(petName).isEqualTo("Dmitrio");
	}

	@Test
	void shouldNotUpdateACashCardThatDoesNotExist(){
		Pet petUpdate = new Pet(null, "Dmitrio", null);
		HttpEntity<Pet> request = new HttpEntity<>(petUpdate);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.exchange("/pet/9999999", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldReturnAllPetsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet", String.class);
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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfPets() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet?page=0&size=1&sort=petName,asc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);
		String petName = documentContext.read("$[0].petName");
		assertThat(petName).isEqualTo("Antonio");
	}

	@Test
	void shouldNotReturnAPetWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "abc123")
				.getForEntity("/pet/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("Pomazzanus", "BAD-PASSWORD")
				.getForEntity("/pet/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotPetOwners() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("hank-owns-no-cards", "qrs456")
				.getForEntity("/pet/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet/104", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingPet(){
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.exchange("/pet/99", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.getForEntity("/pet/99", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAPetThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.exchange("/pet/99999", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotAllowDeletionOfPetsTheyDoNotOwn() {
		ResponseEntity<Void> deleteResponse = restTemplate
				.withBasicAuth("Pomazzanus", "abc123")
				.exchange("/pet/104", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("Sunazzamop", "xyz789")
				.getForEntity("/pet/104", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
