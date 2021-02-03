package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.common.share.SessionBean;
import com.common.share.TextRead;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class StoreReceivedApproveMoulding extends Window {

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;
	
	private NativeButton btnApproved = new NativeButton("Approved");
	private NativeButton btnRefresh = new NativeButton("Refresh");
	private NativeButton btnExit = new NativeButton("Exit");
	
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df=new DecimalFormat("#0.00");
	int count=0;
	
	ArrayList<Label>tbSl = new ArrayList<Label>();
	ArrayList<CheckBox>tbChk=new ArrayList<CheckBox>();
	ArrayList<CheckBox>tbChkIsFg=new ArrayList<CheckBox>();
	ArrayList<Label>tbProductId=new ArrayList<Label>();
	ArrayList<Label>tbProductName=new ArrayList<Label>();
	ArrayList<TextRead>tbUnit=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbRequiredBag=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbRequiredKg=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbReceivedBag=new ArrayList<TextRead>(1);
	ArrayList<TextRead>tbReceivedKg=new ArrayList<TextRead>(1);
	Table table=new Table();
	
	public StoreReceivedApproveMoulding(SessionBean sesionBean){
		this.sessionBean=sesionBean;
		this.setResizable(false);
		this.setCaption("STORE RECEIVED APPROVE [MOULDING] :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		setEventAction();
		
	}

	private void tableInitialize() {
		
	}

	private void setEventAction() {
		
	}
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("880px");
		setHeight("650px");
		
		return mainLayout;
	}
}
