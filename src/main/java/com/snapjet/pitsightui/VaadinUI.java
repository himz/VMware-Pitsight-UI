package com.snapjet.pitsightui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

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
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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
	
	private final String PITSIGHT_JSON_FILE_LOC ="/Users/pandeyh/pitsight/pitsight.json";
	private final String COMMAND_FILE_LOC ="/Users/pandeyh/pitsight/testFile.txt";
	

	private final CustomerRepository repo;

	private final CustomerEditor editor;

	final Grid<Customer> grid;

	final TextField filter;

	private final Button addNewBtn;
	
	private final VerticalLayout verticalLayout;
	
	private final Panel panel;
	GridLayout mainLayout;

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
		if(mainLayout.getComponentCount() > 0) {
			mainLayout.removeAllComponents();
		}
		for (Snapshots snapshot : snapshotList) {


	    		Label full = new Label();
	    		String snapshotText = snapshot.getId() ;
	    		full.setCaption(snapshotText);
	    		
			HorizontalLayout hlPitId = new HorizontalLayout();
	    		Label capPitId = new Label("Point In Time:");
	    		capPitId.addStyleName("h1");
	    		Label txtPitId = new Label("Snapshot " + snapshot.getId());
	    		txtPitId.addStyleName("h1");
	    		hlPitId.addComponent(capPitId); hlPitId.addComponent(txtPitId);
	    		
	    		
			HorizontalLayout hlTimeStamp = new HorizontalLayout();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("PST"));
			long epoch = Long.parseLong(snapshot.getTimestamp()); 
			//Long dateLong=Long.parseLong(sdf.format(epoch*1000));
		
	    		Label capTimeStamp = new Label("Time Stamp: ");	    		capTimeStamp.addStyleName("h3");
	    		Label txtTimeStamp = new Label(sdf.format(epoch*1000)); txtTimeStamp.addStyleName("h3");
	    		hlTimeStamp.addComponent(capTimeStamp); hlTimeStamp.addComponent(txtTimeStamp);
	    		
			HorizontalLayout hlProcessList = new HorizontalLayout();
	    		Label capProcessList = new Label("Processes in the System: "); capProcessList.addStyleName("h3");
	    		Label txtProcessList = new Label(snapshot.getProcessList().toString());txtProcessList.addStyleName("h3");
	    		hlProcessList.addComponent(capProcessList); hlProcessList.addComponent(txtProcessList);
	    		
			HorizontalLayout hlLoggedUserList = new HorizontalLayout();
	    		Label capLoggedUserList = new Label("Logged Users In the System: ");capLoggedUserList.addStyleName("h3");
	    		Label txtLoggedUserList = new Label(snapshot.getLoggedUsersList().toString());txtLoggedUserList.addStyleName("h3");
	    		hlLoggedUserList.addComponent(capLoggedUserList); hlLoggedUserList.addComponent(txtLoggedUserList);
	    		
			HorizontalLayout hlTopFilesList = new HorizontalLayout();
	    		Label capTopFilesList = new Label("Top Files by Size on the System: ");capTopFilesList.addStyleName("h3");
	    		Label txtTopFilesList = new Label(snapshot.getTopTenFilesList().toString()); txtTopFilesList.addStyleName("h3");
	    		hlTopFilesList.addComponent(capTopFilesList); hlTopFilesList.addComponent(txtTopFilesList);
	    		
	    		VerticalLayout miniSnapLayout = new VerticalLayout();
	    		miniSnapLayout.addComponent(hlPitId);
	    		miniSnapLayout.addComponent(hlTimeStamp);
	    		miniSnapLayout.addComponent(hlProcessList);
	    		miniSnapLayout.addComponent(hlLoggedUserList);
	    		miniSnapLayout.addComponent(hlTopFilesList);
	    		
	    		Button bSnap = new Button("Restore VM from Given PIT Snapshot");
	    		bSnap.addClickListener(new ClickListener() {

	                private static final long serialVersionUID = 5625402155456539565L;

	                @Override
	                public void buttonClick(ClickEvent event) {
	                	writeToFile(snapshot.getId(), COMMAND_FILE_LOC);	                  
	                }
	            });
	    		miniSnapLayout.addComponent(bSnap);
	    		
	    		
	    		mainLayout.addComponent(miniSnapLayout);
		}
	}
	
	@Override
	protected void init(VaadinRequest request) {
		// build layout
		VerticalLayout leftLayout = new VerticalLayout();
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		//mainLayout = new VerticalLayout(grid, editor);
		//mainLayout = new VerticalLayout();
		Panel p = new Panel();
		mainLayout = new GridLayout(2,100);
		p.setSizeFull();
		mainLayout.addStyleName("v-scrollable");
		p.setContent(mainLayout);
		HorizontalSplitPanel mainSplitter = new HorizontalSplitPanel(leftLayout, p);
		mainSplitter.setSizeFull();
		mainSplitter.setSplitPosition(20);
		setContent(mainSplitter);
		RootObject root = getRootObject(new File(PITSIGHT_JSON_FILE_LOC));
		System.out.println(root);
		
		//leftLayout.addComponent(actions);g
		List<Vm> vms = root.getVm();
		for (Vm vm : vms) {
			System.out.println(vm);
			System.out.println(vm.getName());
			if(vm != null && vm.getName() != null) {
				Button b1 = new Button(vm.getName());
				b1.setCaption(vm.getName() + "  (Policy RPO: " + vm.getPolicyRpo() + " Mins)");
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
		//mainLayout.addComponent(verticalLayout);
		

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

	public void writeToFile(String content, String fileName) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
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
