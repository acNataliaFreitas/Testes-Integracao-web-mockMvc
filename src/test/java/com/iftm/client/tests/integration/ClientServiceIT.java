package com.iftm.client.tests.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ClientServiceIT {
	
	@Autowired
	private ClientService service;
	
	private long existingId;
	private long nonExistingId;
	private long countClientByIncome;
	private long countTotalClients;
	private PageRequest pageRequest;
	private long existingId2;
	private String existingName, existingCpf;
	private Client client;
	private ClientDTO clientDTO;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;	
		countClientByIncome = 5L;
		countTotalClients = 12L;
		pageRequest = PageRequest.of(0, 6);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExistis() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		
		Double income = 4000.0;
		
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	@Test
	public void findAllShouldReturnAllClients() {
		
		List<ClientDTO> result = service.findAll();
		
		Assertions.assertEquals(countTotalClients, result.size());
	}
	
	@Test
	public void ShouldDeleteObjectWhenIdExistsAndDecrementedCustomersIncluded() {
		service.delete(existingId);
		
		long totalAfterDelete = repository.count();
		
		Assertions.assertEquals(countTotalClients-1, totalAfterDelete);
	}
	
	@Test
	public void findByIdShouldVerifyNameAndCPFWhichMatch() {
		ClientDTO entity = service.findById(existingId2);
	
		Assertions.assertTrue(existingName.equals(entity.getName()));
		Assertions.assertEquals(existingCpf, entity.getCpf());
	}
	
	
	@Test
	public void insertNewClientUsingFactoryDefaultCheckFindAllAndIfItHasBeenIncremented() {
	
		Assertions.assertEquals(countTotalClients, repository.findAll().size());
		service.insert(clientDTO);
		
	
		Assertions.assertEquals(countTotalClients+1, repository.findAll().size());
	}
	

	@Test
	public void UpdateAndVerificationOfDataFromAnExistingCustomer() {
		ClientDTO client = service.findById(existingId);
		ClientDTO clientUpdated = service.update(existingId, clientDTO);
		 
		Assertions.assertEquals(client.getId(), clientUpdated.getId());
		
		Assertions.assertNotEquals(client.getName(), clientUpdated.getName());
		Assertions.assertFalse(client.getCpf().equals(clientUpdated.getCpf()));
		Assertions.assertFalse(client.getChildren() == clientUpdated.getChildren());
		Assertions.assertTrue(client.getIncome() != clientUpdated.getIncome());
		Assertions.assertNotEquals(client.getBirthDate(), clientUpdated.getBirthDate());
							
	}
}
