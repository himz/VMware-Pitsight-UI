package com.snapjet.pitsightui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.*;

import com.snapjet.pitsightui.Customer;
import com.snapjet.pitsightui.CustomerEditor;
import com.snapjet.pitsightui.CustomerRepository;
import com.snapjet.pitsightui.VaadinUI;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.boot.VaadinAutoConfiguration;


import model.RootObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VaadinUITests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VaadinUITests {

	@Autowired CustomerRepository repository;

	VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);

	CustomerEditor editor;

	VaadinUI vaadinUI;

	@Before
	public void setup() {
		this.editor = new CustomerEditor(this.repository);
		this.vaadinUI = new VaadinUI(this.repository, editor);
	}

	@Test
	public void shouldInitializeTheGridWithCustomerRepositoryData() {
		int customerCount = (int) this.repository.count();

		vaadinUI.init(this.vaadinRequest);

		then(vaadinUI.grid.getColumns()).hasSize(3);
		then(getCustomersInGrid()).hasSize(customerCount);
	}

	private List<Customer> getCustomersInGrid() {
		ListDataProvider<Customer> ldp = (ListDataProvider) vaadinUI.grid.getDataProvider();
		return new ArrayList<>(ldp.getItems());
	}

	@Test
	public void shouldFillOutTheGridWithNewData() {
		int initialCustomerCount = (int) this.repository.count();
		this.vaadinUI.init(this.vaadinRequest);
		customerDataWasFilled(editor, "Marcin", "Grzejszczak");

		this.editor.save.click();

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);

		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Marcin", "Grzejszczak");

	}

	@Test
	public void shouldFilterOutTheGridWithTheProvidedLastName() {
		this.vaadinUI.init(this.vaadinRequest);
		this.repository.save(new Customer("Josh", "Long"));

		vaadinUI.listCustomers("Long");

		then(getCustomersInGrid()).hasSize(1);
		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Josh", "Long");
	}

	@Test
	public void shouldInitializeWithInvisibleEditor() {
		this.vaadinUI.init(this.vaadinRequest);

		then(this.editor.isVisible()).isFalse();
	}

	@Test
	public void shouldMakeEditorVisible() {
		this.vaadinUI.init(this.vaadinRequest);
		Customer first = getCustomersInGrid().get(0);
		this.vaadinUI.grid.select(first);

		then(this.editor.isVisible()).isTrue();
	}
	
	@Test
	public void testGetRootObject() throws Exception {
		this.vaadinUI.init(this.vaadinRequest);
		File newFile = new File("/Users/pandeyh/pitsight/pitsight.json");
		StringBuilder result = new StringBuilder("");
        Scanner scanner = new Scanner(newFile);

    		while (scanner.hasNextLine()) {
    			String line = scanner.nextLine();
    			result.append(line).append("\n");
    		}
    		System.out.println(result);
    		scanner.close();
		RootObject root = vaadinUI.getRootObject(newFile);
		System.out.println(root);
	}
	@Test
	public void testHomeJson() throws Exception {
		System.out.println("**************************************************************************************");
		System.out.println("**************************************************************************************");
		final File folder = new File("/Users/pandeyh/pitsight");
		listFilesForFolder(folder);
		System.out.println("**************************************************************************************");
		System.out.println("**************************************************************************************");
		
	}
	public void listFilesForFolder(final File folder) throws Exception {
		StringBuilder result = new StringBuilder("");
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            System.out.println(fileEntry.toString());
	            // JSON Parsing
	            JSONParser parser = new JSONParser();
	            
	     


	            
	            
	            
	            try (Scanner scanner = new Scanner(fileEntry)) {

	        		while (scanner.hasNextLine()) {
	        			String line = scanner.nextLine();
	        			result.append(line).append("\n");
	        		}

	        		scanner.close();

	        	} catch (IOException e) {
	        		e.printStackTrace();
	        	}
	            System.out.println(result);
	            
	            
                Object obj = parser.parse(result.toString());

                JSONObject jsonObject = (JSONObject) obj;
                
                System.out.println(jsonObject);
                JSONArray vms = jsonObject.getJSONArray("VM");
                System.out.println(vms);

                long age = (Long) jsonObject.get("snapshot");
                System.out.println(age);

                // loop array
                JSONArray msg = (JSONArray) jsonObject.get("messages");
                
            
	        }
	    }
	}


	private void customerDataWasFilled(CustomerEditor editor, String firstName,
			String lastName) {
		this.editor.firstName.setValue(firstName);
		this.editor.lastName.setValue(lastName);
		editor.editCustomer(new Customer(firstName, lastName));
	}

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	static class Config {

		@Autowired
		CustomerRepository repository;

		@PostConstruct
		public void initializeData() {
			this.repository.save(new Customer("Jack", "Bauer"));
			this.repository.save(new Customer("Chloe", "O'Brian"));
			this.repository.save(new Customer("Kim", "Bauer"));
			this.repository.save(new Customer("David", "Palmer"));
			this.repository.save(new Customer("Michelle", "Dessler"));
		}
	}
}
