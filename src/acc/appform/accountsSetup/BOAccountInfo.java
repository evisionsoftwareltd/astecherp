package acc.appform.accountsSetup;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BOAccountInfo extends VerticalLayout{
	private FormLayout formLayout = new FormLayout();
	public PopupDateField date = new PopupDateField("Date:");
	
	private HorizontalLayout hl1 = new HorizontalLayout();
	private HorizontalLayout hl1S1 = new HorizontalLayout();
	private HorizontalLayout hl2 = new HorizontalLayout();
	private HorizontalLayout hl2S1 = new HorizontalLayout();
	
	public CheckBox regular = new CheckBox("Regular");
	public CheckBox omnibus = new CheckBox("Omnibus");
	public CheckBox clearing = new CheckBox("Clearing");
	
	public CheckBox individual = new CheckBox("Individual");
	public CheckBox company = new CheckBox("Company");
	public CheckBox jointHolder = new CheckBox("Joint Holder");
	
	public TextField participantName = new TextField("Name of CDBL Participant:");
	public TextField participantId = new TextField("CDBL Participant ID:");
	public TextField boId = new TextField("BO ID:");
	public PopupDateField openDate = new PopupDateField("Date of Account Opened:");
	public TextField branchName = new TextField("Branch Name:");
	
	
	
	public String commWidth = "220px";
	public BOAccountInfo(){
		
		formLayout.addComponent(date);
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setImmediate(true);
		//date.setLocale(new Locale("en", "GB"));
		
		
		//formLayout.addComponent(citySelect);
		hl1.setSpacing(true);
		hl1S1.setWidth("67px");
		hl1.addComponent(hl1S1);
		hl1.addComponent(new Label("BO Category:"));
		hl1.addComponent(regular);
		hl1.addComponent(omnibus);
		hl1.addComponent(clearing);
		
		hl2.setSpacing(true);
		hl2S1.setWidth("90px");
		hl2.addComponent(hl2S1);
		hl2.addComponent(new Label("BO Type:"));
		hl2.addComponent(individual);
		hl2.addComponent(company);
		hl2.addComponent(jointHolder);
		
		formLayout.addComponent(participantName);
		participantName.setWidth("250px");
		
		formLayout.addComponent(participantId);
		formLayout.addComponent(boId);
		
		formLayout.addComponent(openDate);
		openDate.setValue(new java.util.Date());
		openDate.setResolution(PopupDateField.RESOLUTION_DAY);
		openDate.setDateFormat("dd-MM-yy");
		openDate.setImmediate(true);
		//openDate.setLocale(new Locale("en", "GB"));
		
		formLayout.addComponent(branchName);
		branchName.setWidth("250px");
		
		this.addComponent(hl1);
		this.addComponent(hl2);
		this.addComponent(formLayout);
	}

}
