package com.iftm.client.tests.repositories;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalClients;
	private long countClientByIncome;
	private String existingName;
	private String existingNameCaseSensitive;
	private String emptyName;
	private Calendar birthdayDate;
	private String newName;
	private Double newIncome;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalClients = 12L;
		countClientByIncome = 5L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
		
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Client client = ClientFactory.createClient();
		client.setId(null);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);		
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		
		Double income = 4000.0;
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		
		Page<Client> result = repository.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	@Test
	public void findByNameShouldReturnExistingName() {
		List<Client> listName = repository.findByNameIgnoreCase(existingName);
		Assertions.assertFalse(listName.isEmpty());
	}


	@Test
	public void findByNameShouldReturnExistingNameIgnoringCase() {
		boolean result;
		List<Client> listName = repository.findByNameIgnoreCase(existingNameCaseSensitive);

		if (listName.isEmpty()) {
			result = false;
		} else {
			result = true;
		}

		Assertions.assertTrue(result);
	}

	
	@Test
	public void findByNameShouldReturnAllNamesWithoudPuttingTheName() {
		List<Client> listName = repository.findByNameIgnoreCase(emptyName);
		List<Client> allNames = repository.findAll();

		Assertions.assertEquals(allNames, listName);
	}

	@Test
	public void findByBirthDateShouldReturnAllNamesThatHaveBirthdayOnTheInformedDate() {
		Date newDate = birthdayDate.getTime();
		List<Client> allNames = repository.findByBirthDateOrYear(newDate.toInstant());

		Assertions.assertFalse(allNames.isEmpty());

		birthdayDate.add(Calendar.YEAR, 1);
		newDate = birthdayDate.getTime();

		allNames = repository.findByBirthDateOrYear(newDate.toInstant());

		Assertions.assertTrue(allNames.isEmpty());

	}

	@Test
	public void clientNameShouldUpdateWhenPassAnExistingId() {
		Client entity = repository.getOne(existingId);
		entity.setName(newName);

		Assertions.assertEquals(newName, entity.getName());
	}

	@Test
	public void updateShouldReturnADefaultMessageWhenUnableToFindTheInformedId() {
		try {	
			Client entity = repository.getOne(nonExistingId);
			entity.setName(newName);
			entity = repository.save(entity);
		} catch (EntityNotFoundException e) {
			String actual = e.getMessage();
			Assertions.assertEquals("Unable to find com.iftm.client.entities.Client with id " + Long.MAX_VALUE, actual);
		}
	}
	
	@Test
	public void clientIncomeShouldUpdateWhenPassAnExistingId() {
		Client entity = repository.getOne(existingId);
		entity.setIncome(newIncome);

		Assertions.assertEquals(newIncome, entity.getIncome());
	}

}
