package com.snapjet.pitsightui;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import model.RootObject;
import model.Snapshots;
import model.Vm;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@Theme("valo")
@Title("Pitsight")
@SpringUI
@PreserveOnRefresh
public class VaadinUI extends UI {

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;
	
	private final VerticalLayout verticalLayout;
	
	private final Panel panel;
	VerticalLayout mainLayout;

	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Customer.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
		this.verticalLayout = new VerticalLayout();
		this.panel = new Panel();
	}

	public void createSnapshotLayout(List<Snapshots> snapshotList) {
		mainLayout.removeAllComponents();
		for (Snapshots snapshot : snapshotList) {
	    		Button bSnap = new Button(snapshot.getId());
	    		Label ta = new Label();
	    		String snapshotText = snapshot.getId() + "\n" +snapshot.getTimestamp() + "\n" +snapshot.getProcessList() + "\n" + snapshot.getLoggedUsersList() + "\n" + snapshot.getTopTenFilesList();
	    		ta.setCaption(snapshotText);
	    		VerticalLayout miniSnapLayout = new VerticalLayout();
	    		miniSnapLayout.addComponent(ta);
	    		miniSnapLayout.addComponent(bSnap);
	    		mainLayout.addComponent(miniSnapLayout);
		}
	}
	
	@Override
	protected void init(VaadinRequest request) {
		// build layout
		VerticalLayout leftLayout = new VerticalLayout();
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		mainLayout = new VerticalLayout(grid, editor);
		
		HorizontalSplitPanel mainSplitter = new HorizontalSplitPanel(leftLayout, mainLayout);
		mainSplitter.setSizeFull();
		mainSplitter.setSplitPosition(20);
		setContent(mainSplitter);
		RootObject root = getRootObject(new File("/Users/pandeyh/pitsight/pitsight.json"));
		System.out.println(root);
		
		leftLayout.addComponent(actions);
		leftLayout.addComponent(new Button("New Button1"));
		leftLayout.addComponent(new Button("New Button2"));
		List<Vm> vms = root.getVm();
		for (Vm vm : vms) {
			System.out.println(vm);
			System.out.println(vm.getName());
			if(vm != null && vm.getName() != null) {
				Button b1 = new Button(vm.getName());
				b1.addClickListener(new ClickListener() {

	                private static final long serialVersionUID = 5625402155456539564L;

	                @Override
	                public void buttonClick(ClickEvent event) {
	                    /*mainLayout.removeAllComponents();*/
	                    List<Snapshots> snapshotList = vm.getSnapshots();
	                    /*	                    for (Snapshots snapshot : snapshotList) {
	                    		Button bSnap = new Button(snapshot.getId());
	                    		mainLayout.addComponent(bSnap);
	                    }*/
	                	createSnapshotLayout(snapshotList);
	                    
	                }
	            });
				leftLayout.addComponent(b1);
			}
			
		}
		verticalLayout.addComponent(new Button("New Button3"));
		mainLayout.addComponent(verticalLayout);
		

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("id", "firstName", "lastName");

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.LAZY);
		filter.addValueChangeListener(e -> listCustomers(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editCustomer(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listCustomers(filter.getValue());
		});

		// Initialize listing
		listCustomers(null);
	}

	// tag::listCustomers[]
	void listCustomers(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
		}
	}
	// end::listCustomers[]

	public RootObject getRootObject(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert JSON string from file to Object
			RootObject root = mapper.readValue(file, RootObject.class);
			System.out.println(root);
			return root;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			return null;
		
	}
}
