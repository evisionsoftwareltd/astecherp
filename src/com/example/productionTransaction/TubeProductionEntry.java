package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

//import org.apache.bcel.generic.POP;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.productionSetup.ProductionFindWindow;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class TubeProductionEntry extends Window 
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Labels
	private Label lblProductionNo;
	private Label lblProductionDate;
	private Label lblProductionStep;
	private Label lblIssueNo;
	private Label lblRawItem;

	private Label lblIssue;
	private Label lblShiftA;
	private Label lblShiftB;
	private Label lblTotal;
	private Label lblWastage;

	private Label lblLine;
	private Label lbljoborderNo;

	// ComboBox
	private ComboBox cmbProductionStep;
	//private ComboBox cmbIssueNo;
	private ComboBox cmbRawItem;
	private ComboBox cmbjoborder;

	// TextRead
	private TextRead txtProductionNo;
	private TextRead txtIssue;
	private TextRead txtShiftA;
	private TextRead txtShiftB;
	private TextRead txtTotal;
	private TextRead txtWastage;

	// TextField
	private TextField txtProdNo = new TextField();

	// popupdatefields 
	private PopupDateField dProductionDate;

	// AmountField

	// AmountCommaSeperator

	// decimalFormats
	private DecimalFormat decFormat = new DecimalFormat("#0.000");
	private DecimalFormat df = new DecimalFormat("#0");
	private DecimalFormat dFormat = new DecimalFormat("#0.00");
	
	// dateFormats
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");

	// boolean Values
	boolean isUpdate = false;
	boolean isFind = false;

	// table
	private Table table,tableRM;

	//ArrayList TableRM
	private ArrayList<Label> lblSlRM = new ArrayList<Label>();
	private ArrayList<CheckBox> chkRM = new ArrayList<CheckBox>();
	private ArrayList<Label> lblRm = new ArrayList<Label>();
	private ArrayList<Label> lblId = new ArrayList<Label>();

	// ArrayList
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<CheckBox> chkFG = new ArrayList<CheckBox>();
	private ArrayList<ComboBox> cmbFinishedProduct = new ArrayList<ComboBox>();

	private ArrayList<TextRead> txtIssuesqm = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtIssueRemainsqm = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtIssuepcs = new ArrayList<TextRead>();

	private ArrayList<AmountField> afShiftAsqm = new ArrayList<AmountField>();
	private ArrayList<AmountField> afShiftApcs = new ArrayList<AmountField>();

	private ArrayList<AmountField> afShiftBsqm = new ArrayList<AmountField>();
	private ArrayList<AmountField> afShiftBpcs = new ArrayList<AmountField>();

	private ArrayList<TextRead> txtTotalsqm = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtTotalpcs = new ArrayList<TextRead>();

	private ArrayList<AmountField> afWastagesqm = new ArrayList<AmountField>();
	private ArrayList<AmountField> afWastagePercent = new ArrayList<AmountField>();
	private ArrayList<AmountField> afWastageTotal = new ArrayList<AmountField>();
	private OptionGroup optiongroup=new OptionGroup();
	private OptionGroup optiongroup1=new OptionGroup();
	
	String[]a={"Top Sealing","Bottom Sealing"};
	private List<String> lst=Arrays.asList(a[0],a[1]);
	
	String[]a1={"Not Sample","Sample Not Finished","Sample Finished"};
	private List<String> lst1=Arrays.asList(a1[0],a1[1],a1[2]);

	// Button
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");

	public TubeProductionEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("Tube Production Entry :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		eventAction();
		cmbProductionStepData();
		tableInitialise();
		btnIni(true);
		componentIni(true);
		txtClear();
		tableClear();
		focusMove();

		cButton.btnNew.focus();
	}
	
	
	private AbsoluteLayout buildMainLayout()
	{
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("1220px");
		setHeight("600px");

		// lblProductionNo
		lblProductionNo = new Label("Production No :");
		lblProductionNo.setImmediate(false);
		lblProductionNo.setWidth("-1px");
		lblProductionNo.setHeight("-1px");

		// txtProductionNo
		txtProductionNo = new TextRead();
		txtProductionNo.setImmediate(true);
		txtProductionNo.setWidth("80px");
		txtProductionNo.setHeight("22px");

		// lblProductionDate
		lblProductionDate = new Label("Production Date :");
		lblProductionDate.setImmediate(false);
		lblProductionDate.setWidth("-1px");
		lblProductionDate.setHeight("-1px");

		// dProductionDate
		dProductionDate = new PopupDateField();
		dProductionDate.setImmediate(false);
		dProductionDate.setWidth("-1px");
		dProductionDate.setHeight("-1px");
		dProductionDate.setDateFormat("dd-MM-yyyy");
		dProductionDate.setValue(new java.util.Date());
		dProductionDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblProductionSteop
		lblProductionStep = new Label("Production Step :");
		lblProductionStep.setImmediate(false);
		lblProductionStep.setWidth("-1px");
		lblProductionStep.setHeight("-1px");

		// cmbProductionStep
		cmbProductionStep = new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setNullSelectionAllowed(false);
		//cmbProductionStep.setWidth("-1px");
		//cmbProductionStep.setHeight("-1px");

		// lblIssueNo
		lblIssueNo = new Label("Job No :");
		lblIssueNo.setImmediate(false);
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");

		// cmbIssueNo
		//cmbIssueNo = new ComboBox();
		//cmbIssueNo.setImmediate(true);
		
		lbljoborderNo = new Label("Job Order No :");
		lbljoborderNo.setImmediate(false);
		lbljoborderNo.setWidth("-1px");
		lbljoborderNo.setHeight("-1px");

		// cmbIssueNo
		cmbjoborder = new ComboBox();
		cmbjoborder.setImmediate(true);
		
		optiongroup=new OptionGroup("", lst);
		optiongroup.select(a[0].toString());
		optiongroup.setStyleName("horizontal");
		
		
		optiongroup1=new OptionGroup("", lst1);
		optiongroup1.select(a1[0].toString());
		optiongroup1.setStyleName("vertical");
		//cmbIssueNo.setWidth("-1px");
		//cmbIssueNo.setHeight("-1px");

		/*// lblFinishedProduct
		lblRawItem = new Label("R/M Name :");
		lblRawItem.setImmediate(false);
		lblRawItem.setWidth("-1px");
		lblRawItem.setHeight("-1px");

		// cmbRawItem
		cmbRawItem = new ComboBox();
		cmbRawItem.setWidth("350px");
		cmbRawItem.setHeight("-1px");
		cmbRawItem.setImmediate(true);*/
		//cmbRawItem.setWidth("-1px");
		//cmbRawItem.setHeight("-1px");

		// txtIssue
		txtIssue = new TextRead();
		txtIssue.setImmediate(true);
		txtIssue.setWidth("217px");
		txtIssue.setHeight("20px");
		txtIssue.setStyleName("txtcolor");

		// txtShiftA
		txtShiftA = new TextRead();
		txtShiftA.setImmediate(true);
		txtShiftA.setWidth("144px");
		txtShiftA.setHeight("20px");
		txtShiftA.setStyleName("txtcolor");

		// txtShiftB
		txtShiftB = new TextRead();
		txtShiftB.setImmediate(true);
		txtShiftB.setWidth("144px");
		txtShiftB.setHeight("20px");
		txtShiftB.setStyleName("txtcolor");

		// txtTotal
		txtTotal = new TextRead();
		txtTotal.setImmediate(true);
		txtTotal.setWidth("144px");
		txtTotal.setHeight("20px");
		txtTotal.setStyleName("txtcolor");

		// txtWastage
		txtWastage = new TextRead();
		txtWastage.setImmediate(true);
		txtWastage.setWidth("207px");
		txtWastage.setHeight("20px");
		txtWastage.setStyleName("txtcolor");

		// lblIssue
		lblIssue = new Label("<b><font color='#fff'>ISSUE</font></b>", Label.CONTENT_XHTML);
		lblIssue.setImmediate(false);
		lblIssue.setWidth("-1px");
		lblIssue.setHeight("-1px");

		// lblShiftA
		lblShiftA = new Label("<b><font color='#fff'>SHIFT A</font></b>", Label.CONTENT_XHTML);
		lblShiftA.setImmediate(false);
		lblShiftA.setWidth("-1px");
		lblShiftA.setHeight("-1px");

		// lblShiftB
		lblShiftB = new Label("<b><font color='#fff'>SHIFT B</font></b>", Label.CONTENT_XHTML);
		lblShiftB.setImmediate(false);
		lblShiftB.setWidth("-1px");
		lblShiftB.setHeight("-1px");

		// lblTotal
		lblTotal = new Label("<b><font color='#fff'>TOTAL</font></b>", Label.CONTENT_XHTML);
		lblTotal.setImmediate(false);
		lblTotal.setWidth("-1px");
		lblTotal.setHeight("-1px");

		// lblWastage
		lblWastage = new Label("<b><font color='#fff'>WASTAGE</font></b>", Label.CONTENT_XHTML);
		lblWastage.setImmediate(false);
		lblWastage.setWidth("-1px");
		lblWastage.setHeight("-1px");

		//tableRM
		tableRM=new Table();
		tableRM.setWidth("473px");
		tableRM.setHeight("150px");
		tableRM.setColumnCollapsingAllowed(true);

		tableRM.addContainerProperty("SL", Label.class, new Label());
		tableRM.setColumnWidth("SL", 20);

		tableRM.addContainerProperty("Check", CheckBox.class, new CheckBox());
		tableRM.setColumnWidth("Check", 40);

		tableRM.addContainerProperty("Raw Material", Label.class, new Label());
		tableRM.setColumnWidth("Raw Material", 350);

		tableRM.addContainerProperty("ID", Label.class, new Label());
		tableRM.setColumnWidth("ID", 100);
		tableRM.setColumnCollapsed("ID", true);

		// table
		table = new Table();
		table.setColumnCollapsingAllowed(true);
		table.setFooterVisible(true);
		table.setWidth("1185px");
		table.setHeight("250px");

//		table.addContainerProperty("SL", Label.class, new Label());
//		table.setColumnWidth("SL", 10);

		table.addContainerProperty("CHK", CheckBox.class, new CheckBox());
		table.setColumnWidth("CHK", 18);

		table.addContainerProperty("FINISHED PRODUCT", ComboBox.class, new ComboBox());
		table.setColumnWidth("FINISHED PRODUCT", 260);

		table.addContainerProperty("Issue sqm ", TextRead.class, new TextRead());
		table.setColumnWidth("Issue sqm ", 60);

		table.addContainerProperty("Remain sqm ", TextRead.class, new TextRead());
		table.setColumnWidth("Remain sqm ", 60);

		table.addContainerProperty(" pcs", TextRead.class, new TextRead());
		table.setColumnWidth(" pcs", 60);

		// Shift A
		table.addContainerProperty("sqm", AmountField.class, new AmountField());
		table.setColumnWidth("sqm", 60);

		table.addContainerProperty("pcs", AmountField.class, new AmountField());
		table.setColumnWidth("pcs", 60);

		// Shift B
		table.addContainerProperty(" sqm", AmountField.class, new AmountField());
		table.setColumnWidth(" sqm", 60);

		table.addContainerProperty("pcs ", AmountField.class, new AmountField());
		table.setColumnWidth("pcs ", 60);

		// Total(A+B)
		table.addContainerProperty("  sqm", TextRead.class, new TextRead());
		table.setColumnWidth("  sqm", 60);

		table.addContainerProperty("pcs  ", TextRead.class, new TextRead());
		table.setColumnWidth("pcs  ", 60);

		// wastage
		table.addContainerProperty("   sqm", AmountField.class, new AmountField());
		table.setColumnWidth("   sqm", 60);

		table.addContainerProperty("%", AmountField.class, new AmountField());
		table.setColumnWidth("%", 60);

		table.addContainerProperty("Total", AmountField.class, new AmountField());
		table.setColumnWidth("Total", 60);

		// adding components to mainLayout (distance: 30px)
		mainLayout.addComponent(lblProductionNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtProductionNo, "top: 18px; left: 130px;");

		mainLayout.addComponent(lblProductionDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dProductionDate, "top: 48px; left: 130px;");

		mainLayout.addComponent(lblProductionStep, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbProductionStep, "top: 78px; left: 130px;");
		
		
		mainLayout.addComponent(lbljoborderNo, "top: 110px; left: 20px;");
		mainLayout.addComponent(cmbjoborder, "top: 108px; left: 130px;");
		
		//mainLayout.addComponent(lblIssueNo, "top: 140px; left: 20px;");
		//mainLayout.addComponent(cmbIssueNo, "top: 138px; left: 130px;");
		
		mainLayout.addComponent(optiongroup, "top: 164px; left: 130px;");
		optiongroup.setVisible(false);

		//mainLayout.addComponent(lblRawItem, "top: 50px; left: 340px;");
		//mainLayout.addComponent(cmbRawItem, "top: 48px; left: 430px;");
		mainLayout.addComponent(tableRM,"top: 20px; left: 350px");
		tableRM.setEditable(false);
		
		mainLayout.addComponent(optiongroup1, "top: 100px; left: 840px;");
		//optiongroup.setVisible(false);

		mainLayout.addComponent(txtIssue, "top: 200px; left: 314px;");
		mainLayout.addComponent(lblIssue, "top: 200px; left: 407px;");

		mainLayout.addComponent(txtShiftA, "top: 200px; left: 533px;");
		mainLayout.addComponent(lblShiftA, "top: 200px; left: 583px;");

		mainLayout.addComponent(txtShiftB, "top: 200px; left: 679px;");
		mainLayout.addComponent(lblShiftB, "top: 200px; left: 727px;");

		mainLayout.addComponent(txtTotal, "top: 200px; left: 825px;");
		mainLayout.addComponent(lblTotal, "top: 200px; left: 879px;");

		mainLayout.addComponent(txtWastage, "top: 200px; left: 971px;");
		mainLayout.addComponent(lblWastage, "top: 200px; left: 1036px;");

		mainLayout.addComponent(table, "top: 220px; left: 10px;");

		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:485.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:510.0px; left:300.0px;");

		return mainLayout;
	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(dProductionDate);
		focusComp.add(cmbProductionStep);
		//focusComp.add(cmbIssueNo);

		for(int i = 0; i < cmbFinishedProduct.size(); i++)
		{
			focusComp.add(cmbFinishedProduct.get(i));

			focusComp.add(afShiftAsqm.get(i));
			focusComp.add(afShiftApcs.get(i));

			focusComp.add(afShiftBsqm.get(i));
			focusComp.add(afShiftBpcs.get(i));

			focusComp.add(afWastagesqm.get(i));
			focusComp.add(afWastagePercent.get(i));
			focusComp.add(afWastageTotal.get(i));
		}

		focusComp.add(cButton.btnNew);
		focusComp.add(cButton.btnEdit);
		focusComp.add(cButton.btnSave);
		focusComp.add(cButton.btnRefresh);
		focusComp.add(cButton.btnDelete);
		focusComp.add(cButton.btnFind);

		new FocusMoveByEnter(this, focusComp);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private void componentIni(boolean b)
	{
		lblProductionNo.setEnabled(!b);
		txtProductionNo.setEnabled(!b);

		lblProductionDate.setEnabled(!b);
		dProductionDate.setEnabled(!b);

		lblProductionStep.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);

		//lblIssueNo.setEnabled(!b);
		//cmbIssueNo.setEnabled(!b);
		
		lbljoborderNo.setEnabled(!b);
		cmbjoborder.setEnabled(!b);

		//lblRawItem.setEnabled(!b);
		//cmbRawItem.setEnabled(!b);

		tableRM.setEnabled(!b);
		table.setEnabled(!b);

		lblIssue.setEnabled(!b);
		txtIssue.setEnabled(!b);

		lblShiftA.setEnabled(!b);
		txtShiftA.setEnabled(!b);

		lblShiftB.setEnabled(!b);
		txtShiftB.setEnabled(!b);

		lblTotal.setEnabled(!b);
		txtTotal.setEnabled(!b);
		optiongroup.setEnabled(!b);

		lblWastage.setEnabled(!b);
		txtWastage.setEnabled(!b);
		
		optiongroup1.setEnabled(!b);
	}

	private void txtClear()
	{
		txtProductionNo.setValue("");
		dProductionDate.setValue(new java.util.Date());
		cmbProductionStep.setValue(null);
		cmbjoborder.setValue(null);
		//cmbIssueNo.setValue(null);
		optiongroup.setVisible(false);
		//cmbRawItem.setValue(null);
		optiongroup1.select(a1[0].toString());
		
	}

	private void tableClear()
	{
		for(int i = 0; i < cmbFinishedProduct.size(); i++)
		{
			cmbFinishedProduct.get(i).setValue(null);

			txtIssuesqm.get(i).setValue("");
			txtIssueRemainsqm.get(i).setValue("");
			txtIssuepcs.get(i).setValue("");

			afShiftAsqm.get(i).setValue("");
			afShiftApcs.get(i).setValue("");

			afShiftBsqm.get(i).setValue("");
			afShiftBpcs.get(i).setValue("");

			txtTotalsqm.get(i).setValue("");
			txtTotalpcs.get(i).setValue("");

			afWastagesqm.get(i).setValue("");
			afWastagePercent.get(i).setValue("");
			afWastageTotal.get(i).setValue("");
		}
	}
	private void tableClearRM(){

		for(int a=0;a<lblSlRM.size();a++){
			lblId.get(a).setValue("");
			chkRM.get(a).setValue(false);
			lblSlRM.get(a).setValue("");
			lblRm.get(a).setValue("");
		}
	}

	private void eventAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				txtProductionNo.setValue(autoProductionNo());
			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				cButton.btnNew.focus();
			}
		});

		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbProductionStep.getValue()!=null)
				{
					if(cmbjoborder.getValue()!=null)
					{
						saveButtonEvent();
						cButton.btnNew.focus();
						isFind = false;
						isUpdate = false;
					}
				}
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbProductionStep.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductionStep.getValue()!=null)
				{
					//tableRM.removeAllItems();
					tableClear();
					joborderDataLoad();
					if(cmbProductionStep.getItemCaption(cmbProductionStep.getValue()).equalsIgnoreCase("Sealing"))
					{
					  optiongroup.setVisible(true);	
					}
					else
					{
						optiongroup.setVisible(false);		
					}
					
					//cmbIssueNoData();
					
					for(int i = 0; i < cmbFinishedProduct.size(); i++)
					{
						chkFGListener(i);
					}
				}
			}
		});
		
		cmbjoborder.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableClear();
				tableClearRM();//cmbIssueactionStart
				if(cmbjoborder.getValue()!=null)
				{
					
					//cmbIssueNoData();
					
					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						cmbFinishedProductData(i);
					}
					
					//cmbIssue Action Start
					tableRawItem();
					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						//cmbFinishedProductData(i);
						txtIssuesqm.get(i).setValue("");
						txtIssueRemainsqm.get(i).setValue("");
						txtIssuepcs.get(i).setValue("");

						afShiftAsqm.get(i).setValue("");
						afShiftApcs.get(i).setValue("");

						afShiftBsqm.get(i).setValue("");
						afShiftBpcs.get(i).setValue("");

						txtTotalsqm.get(i).setValue("");
						txtTotalpcs.get(i).setValue("");

						afWastagesqm.get(i).setValue("");
						afWastagePercent.get(i).setValue("");
						afWastageTotal.get(i).setValue("");
					}
					//cmbIssue Action End	
				}
				else
				{
				 // cmbIssueNo.removeAllItems();
					
				  for(int i=0;i<cmbFinishedProduct.size();i++)
				  {
					 cmbFinishedProduct.get(i).removeAllItems();  
				  }
				}
				
			}
		});
		
		
		
		/*cmbIssueNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				tableClearRM();
				if(cmbIssueNo.getValue()!=null){
					tableRawItem();
					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						//cmbFinishedProductData(i);
						txtIssuesqm.get(i).setValue("");
						txtIssueRemainsqm.get(i).setValue("");
						txtIssuepcs.get(i).setValue("");

						afShiftAsqm.get(i).setValue("");
						afShiftApcs.get(i).setValue("");

						afShiftBsqm.get(i).setValue("");
						afShiftBpcs.get(i).setValue("");

						txtTotalsqm.get(i).setValue("");
						txtTotalpcs.get(i).setValue("");

						afWastagesqm.get(i).setValue("");
						afWastagePercent.get(i).setValue("");
						afWastageTotal.get(i).setValue("");
					}	
				}
				
			}
		});*/

		/*cmbIssueNo.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbIssueNo.getValue()!=null)
				{
					//tableRM.removeAllItems();
					tableClearRM();
					tableRawItem();

					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						cmbFinishedProductData(i);
						txtIssuesqm.get(i).setValue("");
						txtIssueRemainsqm.get(i).setValue("");
						txtIssuepcs.get(i).setValue("");

						afShiftAsqm.get(i).setValue("");
						afShiftApcs.get(i).setValue("");

						afShiftBsqm.get(i).setValue("");
						afShiftBpcs.get(i).setValue("");

						txtTotalsqm.get(i).setValue("");
						txtTotalpcs.get(i).setValue("");

						afWastagesqm.get(i).setValue("");
						afWastagePercent.get(i).setValue("");
						afWastageTotal.get(i).setValue("");
					}	
				}
				else
				{
					tableRM.removeAllItems();
					//tableClearRM();
				}
			}
		});*/

		/*cmbRawItem.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbRawItem.getValue()!=null)
				{
					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						//						tableClear();
						//						cmbFinishedProduct.get(i).removeAllItems();
						cmbFinishedProductData(i);
					}
				}
			}
		});*/
	}
	
	private void chkFGListener(final int i)
	{
		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("TubeSTP-1"))
		{			
				chkFG.get(i).setEnabled(false);
				chkFG.get(i).setValue(false);			
		}
		
		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("TubeSTP-2"))
		{			
				chkFG.get(i).setEnabled(false);
				chkFG.get(i).setValue(false);			
		}
		
		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("TubeSTP-3"))
		{			
				chkFG.get(i).setEnabled(true);
				chkFG.get(i).setValue(true);			
		}

		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("TubeSTP-3"))
		{			
				chkFG.get(i).setEnabled(true);
				chkFG.get(i).setValue(true);			
		}
		
		if(cmbProductionStep.getValue().toString().equalsIgnoreCase("TubeSTP-4"))
		{			
				chkFG.get(i).setEnabled(false);
				chkFG.get(i).setValue(true);			
		}
	}

	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}

	private void tableRawItem()
	{
		Transaction tx = null;
		String sql=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql="select distinct c.vRawItemCode,c.vRawItemName,subString(c.vSubGroupName,CHARINDEX('-',c.vSubGroupName)+1,LEN(c.vSubGroupName))as category,c.vSubSubCategoryName from tbTubeIssueDetails a inner join tbTubeIssueInfo b"+
					" on a.IssueNo=b.IssueNo inner join tbRawItemInfo c"+
					" on c.vRawItemCode=a.RMitemCode where b.jobOrderNo='"+cmbjoborder.getValue()+"'";
			System.out.print("tableRawItem: "+sql);

			List list = session.createSQLQuery(sql).list();
			int a=0;
			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
				//tableRowAddRM(element[0],name,a);
				lblSlRM.get(a).setValue(a+1);
				lblRm.get(a).setValue(name);
				lblId.get(a).setValue(element[0]);
				a++;
			}
			for(int x=a;x<lblSlRM.size();x++){
				lblSlRM.get(x).setEnabled(false);
				chkRM.get(x).setEnabled(false);
				lblRm.get(x).setEnabled(false);
				lblId.get(x).setEnabled(false);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void tableRowAddRM(final int ar)
	{

		lblSlRM.add(ar, new Label());
		lblSlRM.get(ar).setWidth("-1");;
		lblSlRM.get(ar).setHeight("-1");
		lblSlRM.get(ar).setValue(ar+1);

		chkRM.add(ar,new CheckBox());
		chkRM.get(ar).setImmediate(true);
		chkRM.get(ar).setWidth("-1");
		chkRM.get(ar).setHeight("-1");
		chkRM.get(ar).setValue(true);
		//chkRM.get(ar).setEnabled(false);
		chkRM.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(chkRM.get(ar).booleanValue()){
					for(int a=0;a<lblSlRM.size();a++){
						if(a!=ar){
							chkRM.get(a).setValue(false);
						}
					}
				}
			}
		});

		lblRm.add(ar, new Label());
		lblRm.get(ar).setWidth("-1");
		lblRm.get(ar).setHeight("-1");
	//	lblRm.get(ar).setValue(name);

		lblId.add(ar, new Label());
		lblId.get(ar).setWidth("-1");
		lblId.get(ar).setHeight("-1");
		//lblId.get(ar).setValue(id);

		tableRM.addItem(new Object[]{lblSlRM.get(ar),chkRM.get(ar),lblRm.get(ar),lblId.get(ar)},ar);

	}

	private void findButtonEvent() 
	{
		Window win = new TubeProductionFindWindow(sessionBean, txtProdNo);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtProdNo.getValue().toString().length() > 0)
				{
					txtClear();
					isFind = true;
					findInitialise(txtProdNo.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtProdNo) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			//String sql1 = "select info.ProductionNo, ProductionDate, issueInfo.IssueTo, info.IssueNo, rawItemCode,info.joborderNo,info.Sealingflag from tbTubeProductionInfo as info left join (select IssueNo, IssueTo from tbTubeIssueInfo) as issueInfo on issueInfo.IssueNo = info.IssueNo where ProductionNo='"+txtProdNo+"' ";
			
			String sql1="select ProductionNo,ProductionDate,Stepid,IssueNo,rawItemCode,jobOrderNo,sealingFlag,isnull(sampleFlag,'')" +
					"from tbTubeProductionInfo  where ProductionNo='"+txtProdNo+"'";
			System.out.println(sql1);
			List list=session.createSQLQuery(sql1).list();
			for(Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object element[] = (Object[])iter.next();
				txtProductionNo.setValue(element[0]);
				dProductionDate.setValue(element[1]);
				cmbProductionStep.setValue(element[2].toString());
				cmbjoborder.setValue(element[5].toString());
				//cmbIssueNo.setValue(element[3].toString());
				
				if(optiongroup.isVisible())
				{
					optiongroup.select(element[6].toString());	
				}
				if(element[7].toString().equalsIgnoreCase("Sample Not Finished")){
					optiongroup1.select(a1[1]);
				}
				else if(element[7].toString().equalsIgnoreCase("Sample Finished")){
					optiongroup1.select(a1[2]);
				}
				else{
					optiongroup1.select(a1[0]);
				}
				for(int a=0;a<lblId.size();a++){
					if(!lblId.get(a).getValue().toString().isEmpty()){
						if(lblId.get(a).getValue().toString().equalsIgnoreCase(element[4].toString())){
							chkRM.get(a).setValue(true);
						}
					}
				}

			}

			String sql2 = "";
			sql2 = "Select * from tbTubeProductionDetails  where ProductionNo='"+txtProdNo+"'";
			System.out.println(sql2);
			List list1=session.createSQLQuery(sql2).list();

			int i = 0;

			for (Iterator iter2 = list1.iterator(); iter2.hasNext();)
			{
				Object element[] = (Object[])iter2.next();

				cmbFinishedProduct.get(i).setValue(element[3].toString());
				txtIssuesqm.get(i).setValue(decFormat.format(element[4]).toString());
				
				if(element[18].equals(0))
				{
					chkFG.get(i).setValue(true);
				  	
				}
				else
				{
					chkFG.get(i).setValue(false);	
				}
				
				//				txtIssueRemainsqm.get(i).setValue(element[].toString());
				txtIssuepcs.get(i).setValue(df.format(element[5]).toString());
				afShiftAsqm.get(i).setValue(decFormat.format(element[6]).toString());
				afShiftApcs.get(i).setValue(df.format(element[7]).toString());
				afShiftBsqm.get(i).setValue(decFormat.format(element[8]).toString());
				afShiftBpcs.get(i).setValue(df.format(element[9]).toString());
				txtTotalsqm.get(i).setValue(decFormat.format(element[10]).toString());
				txtTotalpcs.get(i).setValue(df.format(element[11]).toString());
				afWastagesqm.get(i).setValue(decFormat.format(element[12]).toString());
				afWastagePercent.get(i).setValue(dFormat.format(element[14]).toString());
				afWastageTotal.get(i).setValue(df.format(element[13]).toString());

				i++;
				tableRowAdd(i+1);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error1", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		tableClear();

		dProductionDate.focus();
		isFind = false;
	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableClear();
		isFind = false;
		isUpdate = false;
	}

	private void cmbFinishedProductData(int ar)
	{
		cmbFinishedProduct.get(ar).removeAllItems();
		Transaction tx= null;
		try
		{
			String idname=itemId();

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "";
			
			sql= "select distinct b.fgId,(select  distinct vProductName from tbFinishedProductInfo where vProductId like b.fgId ) from tbJobOrderInfo a inner join tbJobOrderDetails b "
				 +"on a.orderNo=b.orderNo and a.orderNo like '"+cmbjoborder.getValue().toString()+"' ";
			

			List lst = session.createSQLQuery(sql).list();
			System.out.println(sql);

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbFinishedProduct.get(ar).addItem(element[0].toString());
				cmbFinishedProduct.get(ar).setItemCaption(element[0].toString() , element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private String itemId(){
		String idname = null;
		System.out.println("Mezbah Boss "+idname);
		if(lblSlRM.size()!=0)
		{
			String id[]=new String[lblSlRM.size()];

			for(int a=0;a<lblSlRM.size();a++)
			{
				id[a]=lblId.get(a).getValue().toString();
				if(a==0)
				{
					idname="'"+id[a]+"'";
				}
				else if(!id[a].isEmpty())
				{
					idname=idname+","+"'"+id[a]+"'";
				}
			}
			System.out.println("Mezbah "+idname);
			return idname;
		}
		return "";
	}

	/*private void cmbRawItemData()
	{
		cmbRawItem.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = 	" select issueDet.RMitemCode, rawInfo.vRawItemName  from tbTubeIssueDetails as issueDet" +
					" left join" +
					" (select vRawItemCode, vRawItemName from tbRawItemInfo) as rawInfo" +
					" on rawInfo.vRawItemCode = issueDet.RMitemCode" +
					" where IssueNo = '"+cmbIssueNo.getValue().toString()+"'";

			sql="select c.vRawItemCode,c.vRawItemName,subString(c.vSubGroupName,CHARINDEX('-',c.vSubGroupName)+1,LEN(c.vSubGroupName))as category,c.vSubSubCategoryName from tbTubeIssueDetails a inner join tbTubeIssueInfo b"+
					" on a.IssueNo=b.IssueNo inner join tbRawItemInfo c"+
					" on c.vRawItemCode=a.RMitemCode where a.IssueNo=1";
			System.out.print("cmbRawItemData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbRawItem.addItem(element[0].toString());
				String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
				cmbRawItem.setItemCaption(element[0].toString(), name);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}*/

	/*private void cmbIssueNoData()
	{
		//cmbIssueNo.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			if(!isFind)
			{
				Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session1.beginTransaction();
				session1.createSQLQuery("exec PrcIssueNoUpdate '"+cmbProductionStep.getValue()+"'").executeUpdate();
				tx.commit();
			}
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(!isFind)
			{
				sql = " select 0, IssueNo from tbTubeIssueInfo where JobOrderNo like '"+cmbjoborder.getValue().toString()+"' and IssueTo like '"+cmbProductionStep.getValue().toString()+"' and Flag='1' ";
			}
			else
			{
				sql = " select 0, IssueNo from tbTubeIssueInfo where JobOrderNo like '"+cmbjoborder.getValue().toString()+"' and IssueTo like '"+cmbProductionStep.getValue().toString()+"'  ";
			}
			System.out.println("cmbIssuNoData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbIssueNo.addItem(element[1].toString());
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}*/

	private void cmbProductionStepData()
	{
		cmbProductionStep.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = "select StepId, StepName from tbProductionStep where productionTypeId like 'PT-1'";
			System.out.print("cmbProductionStepData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbProductionStep.addItem(element[0].toString());
				cmbProductionStep.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}
	private void joborderDataLoad()
	{
		cmbjoborder.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
 
			sql = "select distinct 0, JobOrderNo  from tbTubeIssueInfo where  IssueTo like '"+cmbProductionStep.getValue().toString()+"' and JobOrderNo is not null  ";
			System.out.print("cmbProductionStepData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbjoborder.addItem(element[1].toString());
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}
	
	

	private void tableInitialise()
	{
		for(int i = 0; i < 6; i++)
		{
			tableRowAdd(i);
			tableRowAddRM(i);
		}
	}

	private double totlaSqmCalc(int ar){

		double shiftAsqm = 0;
		double shiftBsqm = 0;
		double wastageSqm=0;

		shiftAsqm = Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0":afShiftAsqm.get(ar).getValue().toString());
		shiftBsqm = Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBsqm.get(ar).getValue().toString());
		//wastageSqm =Double.parseDouble(afWastagesqm.get(ar).getValue().toString().isEmpty()?"0.0":afWastagesqm.get(ar).getValue().toString());

		double totalSqm = 0;
		totalSqm = shiftAsqm + shiftBsqm;

		//txtTotalsqm.get(ar).setValue(totalSqm);
		System.out.println("ShiftAsqm: "+shiftAsqm);
		System.out.println("ShiftBsqm: "+shiftBsqm);

		return totalSqm;
	}
	private boolean tablechk(){
		for(int a=0;a<lblRm.size();a++){
			if(chkRM.get(a).booleanValue()){
				return true;
			}
		}
		return false;
	}
	private void tableRowAdd(final int ar)
	{
		try
		{
//			lblSl.add(ar, new Label());
//			lblSl.get(ar).setWidth("100%");
//			lblSl.get(ar).setValue(ar+1);

			chkFG.add(ar,new CheckBox());
			chkFG.get(ar).setImmediate(true);
			chkFG.get(ar).setWidth("-1");
			chkFG.get(ar).setHeight("-1");
			chkFG.get(ar).setValue(false);
			chkFG.get(ar).setEnabled(false);

			cmbFinishedProduct.add(ar, new ComboBox());
			cmbFinishedProduct.get(ar).setWidth("100%");
			cmbFinishedProduct.get(ar).setImmediate(true);
			cmbFinishedProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			cmbFinishedProduct.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(doubleEntryCheck(ar) )
						{
							if(tablechk()){
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								Transaction tx = session.beginTransaction();
								try
								{
									/*String sql = "select issueDet.IssueQty,0 from tbTubeIssueInfo as issueInfo" +
											" left join" +
											" (select IssueNo, RMitemCode, IssueQty from tbTubeIssueDetails) as issueDet" +
											" on issueDet.IssueNo = issueInfo.IssueNo" +
											" where issueDet.RMitemCode = '"+cmbRawItem.getValue().toString()+"' and issueInfo.FinishedGood = '"+cmbFinishedProduct.get(ar).getValue().toString()+"'";*/

									/*String sql="select b.IssueQty,a.leftQty from tbTubeIssueInfo a inner join tbTubeIssueDetails b "
											+" on a.IssueNo=b.IssueNo where a.FinishedGood ='"+cmbFinishedProduct.get(ar).getValue()+"' and a.jobOrderNo='"+cmbjoborder.getValue()+"'";*/
									String rawId=null;
									for(int a=0;a<lblRm.size();a++){
										if(chkRM.get(a).booleanValue()){
											rawId=lblId.get(a).getValue().toString();
											break;
										}
									}
									
									String sql="select issueQty,leftQty from funcProductionLeftQty " +
											"('"+cmbjoborder.getValue()+"','"+cmbProductionStep.getValue()+"'," +
											"'"+cmbFinishedProduct.get(ar).getValue()+"','"+rawId+"') order by rawItemId";
									
									List list = session.createSQLQuery(sql).list();
									int cmbflag=0;
									System.out.print(sql);

									if(list.iterator().hasNext())
									{
										Object[] element = (Object[]) list.iterator().next();
										cmbflag=1;
										txtIssuesqm.get(ar).setValue(decFormat.format(element[0]));
										txtIssueRemainsqm.get(ar).setValue(decFormat.format(element[1]));
									}

									else
									{
										System.out.println("From Else");
										txtIssuesqm.get(ar).setValue("0.0");
										txtIssueRemainsqm.get(ar).setValue("0.0");
									}

								}

								catch(Exception ex)
								{
									System.out.println(ex);
								}

								if(cmbFinishedProduct.size()-1==ar)
								{	
									tableRowAdd(ar+1);
									chkFGListener(ar+1);
									cmbFinishedProductData(ar+1);
									//								cmbItemNameAdd(ar+1);
									//								cmbItemName.get(ar+1).focus();
								}
							}
							else{
								cmbFinishedProduct.get(ar).setValue(null);
								getParent().showNotification("Warning :","Please Select Raw Materail .",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else 
						{
								cmbFinishedProduct.get(ar).setValue(null);
								getParent().showNotification("Warning :","Same Item Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
								
							
						}
						totalItemFG();
					}
					else
					{
						totalItemFG();
					}
				}
			});

			txtIssuesqm.add(ar, new TextRead(1));
			txtIssuesqm.get(ar).setWidth("100%");
			txtIssuesqm.get(ar).setImmediate(true);

			txtIssuesqm.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(!txtIssuesqm.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();
							double target=Double.parseDouble((String) txtIssuesqm.get(ar).getValue())*
									(Double.parseDouble(element[1].toString()));
							txtIssuepcs.get(ar).setValue(df.format(target));
						}
					}
				}
			});

			txtIssueRemainsqm.add(ar,new TextRead(1));
			txtIssueRemainsqm.get(ar).setWidth("100%");
			txtIssueRemainsqm.get(ar).setImmediate(true);

			txtIssuepcs.add(ar, new TextRead(1));
			txtIssuepcs.get(ar).setWidth("100%");
			txtIssuepcs.get(ar).setImmediate(true);

			afShiftAsqm.add(ar, new AmountField());
			afShiftAsqm.get(ar).setWidth("100%");
			afShiftAsqm.get(ar).setImmediate(true);
			/*
			afShiftAsqm.get(ar).addListener(new TextChangeListener() {

				public void textChange(TextChangeEvent event) {

					List std=perTubeCalc( ar);
					if(std.iterator().hasNext()){
						Object[] element = (Object[]) std.iterator().next();
						double target=Double.parseDouble(event.getText().toString().isEmpty()?"0.0":event.getText().toString())*Double.parseDouble(element[1].toString());
						System.out.println("Target: "+target);
						afShiftApcs.get(ar).setValue(decFormat.format(target));
						System.out.println("Target after: "+target);

						double shiftAsqm = 0;
						double shiftBsqm = 0;

						shiftAsqm = Double.parseDouble(afShiftAsqm.get(ar).toString().isEmpty()?"0.0":afShiftAsqm.get(ar).toString());
						shiftBsqm = Double.parseDouble(event.getText().toString().isEmpty()?"0.0":event.getText().toString());

						double totalSqm = 0;
						totalSqm = shiftAsqm + shiftBsqm;

						txtTotalsqm.get(ar).setValue(totalSqm);

						double totalPcs = 0;
						totalPcs=totalSqm*Double.parseDouble(element[1].toString());
						txtTotalpcs.get(ar).setValue(decFormat.format(totalPcs));
					}
					double ASqm=Double.parseDouble(afShiftAsqm.get(ar).toString().isEmpty()?"0.0":afShiftAsqm.get(ar).toString());
					double isSqm=Double.parseDouble(txtIssuesqm.get(ar).toString().isEmpty()?"0.0":txtIssuesqm.get(ar).toString());
					if(isSqm>ASqm){
						System.out.println("Shift A: "+ASqm);
						System.out.println("Shift B: "+isSqm);
					}
					else{
						afShiftAsqm.get(ar).setValue("");
						showNotification("Shift A Qty exceed Issue Qty",Notification.TYPE_HUMANIZED_MESSAGE);
					}
				}
			});*/

			afShiftAsqm.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afShiftAsqm.get(ar).getValue().toString().isEmpty())
					{
						if(!afShiftAsqm.get(ar).getValue().toString().isEmpty())
						{
							double isSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());
							double totalSqm=totlaSqmCalc(ar);

							System.out.println("Total Sqm is: "+totalSqm);

							if(isSqm>=totalSqm)
							{
								List std=perTubeCalc( ar);

								if(std.iterator().hasNext())
								{
									System.out.println("Rabiul Hasan");
									Object[] element = (Object[]) std.iterator().next();

									double shiftASqmQty=Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0"
											:afShiftAsqm.get(ar).getValue().toString())*(Double.parseDouble(element[1].toString()));

									afShiftApcs.get(ar).setValue(df.format(shiftASqmQty));

									txtTotalsqm.get(ar).setValue(totalSqm);

									double totalPcs = 0;
									totalPcs=totalSqm*(Double.parseDouble(element[1].toString()));
									txtTotalpcs.get(ar).setValue(df.format(totalPcs));

									if(!isFind)
									{
										if(totalSqm>isSqm)
										{
											showNotification("Total Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
											afShiftAsqm.get(ar).setValue("");
											afShiftAsqm.get(ar).focus();
										}
									}
								}
							}
							else
							{
								if(!isFind)
								{
									afShiftAsqm.get(ar).setValue("");
									showNotification("Shift A Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
								}
							}
						}
					}
				}
			});

			/*afShiftAsqm.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afShiftAsqm.get(ar).getValue().toString().isEmpty())
					{
						double issueRemainSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0"
								:txtIssueRemainsqm.get(ar).toString());
						double totalSqm=totlaSqmCalc(ar);
						if(issueRemainSqm>totalSqm)
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double aShiftASqm=Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0"
										:afShiftAsqm.get(ar).getValue().toString());
								double shiftAPcs=aShiftASqm*(1/Double.parseDouble(element[1].toString()));
								afShiftApcs.get(ar).setValue(shiftAPcs);
								txtTotalsqm.get(ar).setValue(decFormat.format(totalSqm));

								double totalPcs = 0;
								totalPcs=totalSqm*(1/Double.parseDouble(element[1].toString()));
								txtTotalpcs.get(ar).setValue(decFormat.format(totalPcs));
							}		
						}
						else
						{
							showNotification("Shift A Sqm Qty Exceed Remain Sqm Qty",Notification.TYPE_WARNING_MESSAGE);
							afShiftAsqm.get(ar).setValue("");
							afShiftAsqm.get(ar).focus();
						}
					}
				}
			});*/

			afShiftApcs.add(ar, new AmountField());
			afShiftApcs.get(ar).setWidth("100%");
			afShiftApcs.get(ar).setImmediate(true);

			afShiftApcs.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afShiftApcs.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);

						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();
							//double target=Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());
							double target=Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
							System.out.println("Target: "+target);
							afShiftAsqm.get(ar).setValue(decFormat.format(target));
						}
					}
				}
			});

			afShiftBsqm.add(ar, new AmountField());
			afShiftBsqm.get(ar).setWidth("100%");
			afShiftBsqm.get(ar).setImmediate(true);

			afShiftBsqm.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afShiftBsqm.get(ar).getValue().toString().isEmpty())
					{
						double issueRemainSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());
						double totalSqm=totlaSqmCalc(ar);

						if(issueRemainSqm>totalSqm)
						{
							List std=perTubeCalc( ar);

							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double aShiftASqm=Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?"0.0"
										:afShiftBsqm.get(ar).getValue().toString());
								double shiftAPcs=aShiftASqm*(Double.parseDouble(element[1].toString()));
								afShiftBpcs.get(ar).setValue(df.format(shiftAPcs));
								txtTotalsqm.get(ar).setValue(decFormat.format(totalSqm));

								double totalPcs = 0;
								totalPcs=totalSqm*(Double.parseDouble(element[1].toString()));
								txtTotalpcs.get(ar).setValue(df.format(totalPcs));
							}		
						}
						else
						{
							if(!isFind)
							{
								showNotification("Shift A Sqm Qty Exceed Remain Sqm Qty",Notification.TYPE_WARNING_MESSAGE);
								afShiftBsqm.get(ar).setValue("");
								afShiftBsqm.get(ar).focus();
							}
						}
					}
				}
			});

			afShiftBpcs.add(ar, new AmountField());
			afShiftBpcs.get(ar).setWidth("100%");
			afShiftBpcs.get(ar).setImmediate(true);

			afShiftBpcs.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afShiftBpcs.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();
							//double target=Double.parseDouble(afShiftBpcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBpcs.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());
							double target=Double.parseDouble(afShiftBpcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBpcs.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
							System.out.println("Target: "+target);
							afShiftBsqm.get(ar).setValue(decFormat.format(target));
						}
					}
				}
			});

			/*afShiftBsqm.get(ar).addListener(new TextChangeListener() {

				public void textChange(TextChangeEvent event) {

					List std=perTubeCalc( ar);
					if(std.iterator().hasNext()){
						Object[] element = (Object[]) std.iterator().next();
						double target=Double.parseDouble(event.getText().toString().isEmpty()?"0.0":event.getText().toString())*Double.parseDouble(element[1].toString());
						System.out.println("Target: "+target);
						afShiftBpcs.get(ar).setValue(decFormat.format(target));
						System.out.println("Target after: "+target);

						double shiftAsqm = 0;
						double shiftBsqm = 0;

						shiftAsqm = Double.parseDouble(afShiftAsqm.get(ar).toString().isEmpty()?"0.0":afShiftAsqm.get(ar).toString());
						shiftBsqm = Double.parseDouble(event.getText().toString().isEmpty()?"0.0":event.getText().toString());

						double totalSqm = 0;
						totalSqm = shiftAsqm + shiftBsqm;

						txtTotalsqm.get(ar).setValue(totalSqm);

						double totalPcs = 0;
						totalPcs=totalSqm*Double.parseDouble(element[1].toString());
						txtTotalpcs.get(ar).setValue(decFormat.format(totalPcs));
					}
				}
			});*/
			/*afShiftBsqm.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null){
						if(!afShiftBsqm.get(ar).getValue().toString().isEmpty())
						{

							double isSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());
							double totalSqm=totlaSqmCalc(ar);
							if(isSqm>=totalSqm)
							{

								List std=perTubeCalc( ar);
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?"0.0":
										afShiftBsqm.get(ar).getValue().toString())*(1*Double.parseDouble(element[1].toString()));
									System.out.println("Target: "+target);
									afShiftBpcs.get(ar).setValue(df.format(target));
									System.out.println("Target after: "+target);
									txtTotalsqm.get(ar).setValue(totalSqm);

									double totalPcs = 0;
									totalPcs=totalSqm*Double.parseDouble(element[1].toString());
									txtTotalpcs.get(ar).setValue(df.format(totalPcs));

									if(!isFind){
										if(totalSqm>isSqm)
										{
											showNotification("Total Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
											afShiftAsqm.get(ar).setValue("");
											afShiftAsqm.get(ar).focus();
										}
									}
								}
							}
							else{
								if(!isFind)
								{
									afShiftBsqm.get(ar).setValue("");
									showNotification("Shift B Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
								}
							}
						}
					}
				}
			});

			afShiftBpcs.add(ar, new AmountField());
			afShiftBpcs.get(ar).setWidth("100%");
			afShiftBpcs.get(ar).setImmediate(true);

			afShiftBpcs.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(!afShiftBpcs.get(ar).getValue().toString().isEmpty())
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(afShiftBpcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBpcs.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
								System.out.println("Target: "+target);
								afShiftBsqm.get(ar).setValue(decFormat.format(target));
							}
						}
					}
				}
			});*/

			txtTotalsqm.add(ar, new TextRead(1));
			txtTotalsqm.get(ar).setWidth("100%");
			txtTotalsqm.get(ar).setImmediate(true);

			txtTotalpcs.add(ar, new TextRead(1));
			txtTotalpcs.get(ar).setWidth("100%");
			txtTotalpcs.get(ar).setImmediate(true);

			afWastagesqm.add(ar, new AmountField());
			afWastagesqm.get(ar).setWidth("100%");
			afWastagesqm.get(ar).setImmediate(true);

			/*afWastagesqm.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afWastagesqm.get(ar).getValue().toString().isEmpty())
					{

						List std=perTubeCalc( ar);
						if(std.iterator().hasNext()){
							Object[] element = (Object[]) std.iterator().next();

							double Issuesqm = 0;
							double Wastagesqm = 0;

							Issuesqm = Double.parseDouble(txtIssuesqm.get(ar).toString().isEmpty()?"0.0":
								txtIssuesqm.get(ar).toString());
							double issueRemain=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":
								txtIssueRemainsqm.get(ar).toString());;
								Wastagesqm = Double.parseDouble(afWastagesqm.get(ar).getValue().toString().isEmpty()?"0.0":
									afWastagesqm.get(ar).getValue().toString());

								double shiftA= Double.parseDouble(afShiftAsqm.get(ar).toString().isEmpty()?"0.0":
									afShiftAsqm.get(ar).toString());
								double shiftB= Double.parseDouble(afShiftBsqm.get(ar).toString().isEmpty()?"0.0":
									afShiftBsqm.get(ar).toString());
								double total=shiftA+shiftB+Wastagesqm;
								double wastagePercent = 0;
								wastagePercent = (100 * Wastagesqm)/Issuesqm;

								afWastagePercent.get(ar).setValue(decFormat.format(wastagePercent));

								double wastageTotal=Wastagesqm*Double.parseDouble(element[1].toString());
								afWastageTotal.get(ar).setValue(df.format(wastageTotal));
								if(!isFind){
									if(total>issueRemain){
										showNotification("Total and Wastage qty Exceed Issue Remain Qty",Notification.TYPE_WARNING_MESSAGE);
										afWastagesqm.get(ar).setValue("");
									}
									else{
										double wastagePercent = 0;
										wastagePercent = (100 * Wastagesqm)/Issuesqm;

										afWastagePercent.get(ar).setValue(decFormat.format(wastagePercent));

										double wastageTotal=Wastagesqm*(1*Double.parseDouble(element[1].toString()));
										afWastageTotal.get(ar).setValue(df.format(wastageTotal));
									}
								}
						}
					}
				}
			});*/

			afWastagePercent.add(ar, new AmountField());
			afWastagePercent.get(ar).setWidth("100%");
			afWastagePercent.get(ar).setImmediate(true);

			afWastageTotal.add(ar, new AmountField());
			afWastageTotal.get(ar).setWidth("100%");
			afWastageTotal.get(ar).setImmediate(true);

			/*afWastageTotal.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(!afWastageTotal.get(ar).getValue().toString().isEmpty())
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(afWastageTotal.get(ar).getValue().toString().isEmpty()?"0.0":
									afWastageTotal.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());
								System.out.println("Target: "+target);
								afWastagesqm.get(ar).setValue(decFormat.format(target));
							}
						}
					}
				}
			});*/
			afWastagesqm.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afWastagesqm.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();

							double Issuesqm = Double.parseDouble(txtIssuesqm.get(ar).toString().isEmpty()?"0.0":txtIssuesqm.get(ar).toString());
							double wastageSqm=Double.parseDouble(afWastagesqm.get(ar).getValue().toString().isEmpty()?"0.0":afWastagesqm.get(ar).getValue().toString());
							double wastageQty=wastageSqm*(1*Double.parseDouble(element[1].toString()));
							double wastagePercent = (100 * wastageSqm)/Issuesqm;
							afWastagePercent.get(ar).setValue(dFormat.format(wastagePercent));
							afWastageTotal.get(ar).setValue(df.format(wastageQty));
						}
					}
				}
			});
			afWastageTotal.get(ar).addListener(new ValueChangeListener() {
				
				public void valueChange(ValueChangeEvent event) {
				
					
					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afWastageTotal.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();
							double perTube=Double.parseDouble(element[1].toString());
							double total=Double.parseDouble(afWastageTotal.get(ar).getValue().toString().isEmpty()?"1":afWastageTotal.
									get(ar).getValue().toString());
							double sqm=total/perTube;
							System.out.println(sqm);
							afWastagesqm.get(ar).setValue(decFormat.format(sqm));
						}
					}
					
				}
			});
			
			
			/*afWastageTotal.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null&&!afWastageTotal.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext())
						{
							Object[] element = (Object[]) std.iterator().next();
							double wastageSqm=Double.parseDouble(afWastageTotal.get(ar).getValue().toString().isEmpty()?"0.00":
								afWastageTotal.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
							System.out.println("Wastage Total: "+wastageSqm);
							afWastagesqm.get(ar).setValue(decFormat.format(wastageSqm));
						}
					}
				}
			});*/

			table.addItem(new Object[] {/*lblSl.get(ar),*/ chkFG.get(ar),cmbFinishedProduct.get(ar), txtIssuesqm.get(ar),txtIssueRemainsqm.get(ar), txtIssuepcs.get(ar), afShiftAsqm.get(ar), afShiftApcs.get(ar), afShiftBsqm.get(ar), afShiftBpcs.get(ar), txtTotalsqm.get(ar), txtTotalpcs.get(ar), afWastagesqm.get(ar), afWastagePercent.get(ar), afWastageTotal.get(ar)}, ar);

		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private void totalItemFG()
	{
		int totalItem = 0;

		for(int i = 0; i < cmbFinishedProduct.size(); i++)
		{
			if(cmbFinishedProduct.get(i).getValue()!=null)
			{
				totalItem++;
			}

			table.setColumnFooter("FINISHED PRODUCT", "Total Product = "+totalItem);
		}
	}

/*	public List perTubeCalc(int ar)
	{
		Transaction tx = null;
		String sql = "";
		String productId = "";
		List std = null;
		String sql1="";

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			if(cmbRawItem.getValue()!=null)
			{
				productId = cmbRawItem.getValue().toString();
			}
			String sql1="select 0,a.mTubePer from tbStandardFinishedInfo a inner join tbStandardFinishedDetails b "
					+" on a.vProductId=b.vProductId where b.vRawItemCode in( "+itemId()+") " +
					"and b.vProductId like '"+cmbFinishedProduct.get(ar).getValue().toString().trim()+"'";

			String sql1="select 0,b.Qty,a.declarationDate from tbFinishedGoodsStandardInfo a "+
					"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo "+
					"where a.fGCode like '"+cmbFinishedProduct.get(ar).getValue()+"' and b.RawItemCode in ( "+itemId()+") and  "+
					"a.declarationDate=(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where a.fGCode='"+cmbFinishedProduct.get(ar).getValue()+"')";
			sql1= "select  ISNULL(perSqm,0)  from tbFinishedProductDetails where fgId like '"+cmbFinishedProduct.get(ar).getValue().toString()+"' and rawItemCode like '"+lblId.get(0).getValue().toString()+"' ";
			
		
			//System.out.println(sql1);
			std=session.createSQLQuery(sql1).list();

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("From perTubeCalc: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return std;
	}*/
	
	
	public List perTubeCalc(int ar){
		Transaction tx = null;
		String sql = "";
		List std = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String productId = null;// = lblId.get(0).getValue().toString();
			for(int a=0;a<lblRm.size();a++){
				if(chkRM.get(a).booleanValue()){
					productId = lblId.get(a).getValue().toString();
					break;
				}
			}

			sql="select 0, isnull(perSqm,0)as qty  from tbFinishedProductDetails  where fgId like '"+cmbFinishedProduct.get(ar).getValue().toString()+"' and rawItemCode like '"+productId+"'";
			System.out.println("sql is"+sql);

			std=session.createSQLQuery(sql).list();

		}
		catch(Exception exp){
			this.getParent().showNotification("From perTubeCalc: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		return std;
	}
	
	

	private void saveButtonEvent() 
	{
		if (!cmbjoborder.getValue().toString().trim().isEmpty()) 
		{
			if (isUpdate) 
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if (buttonType == ButtonType.YES) 
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							Transaction tx = null;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();

							tx = session.beginTransaction();

							if (deleteData(session, tx) && nullCheck())
							{
								insertData();
							}
							else 
							{
								tx.rollback();
							}
						    txtClear();
							componentIni(true);
							btnIni(true);
							tableClear();
							txtClear();
							isUpdate=false;
						}
					}
				});
			} 
			else
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							//							txtClear();
							componentIni(true);
							btnIni(true);
							tableClear();
							txtClear();	
							isUpdate=false;
						}
					}
				});
			}
		} 
		else
		{
			this.getParent().showNotification("Warning :","Please Select Product .",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void insertData()
	{
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String productionNo = "";
		String name="";
		int flag;
		String SealingFlag="";
		String sampleFlag="";
		int sampleFinFlag = 0;
		try
		{
			/*if(!isUpdate)
			{
				productionNo = autoProductionNo();
			}
			else
			{
				productionNo = txtProductionNo.getValue().toString();
			}*/
			productionNo = txtProductionNo.getValue().toString();
			
			if(optiongroup.isVisible())
			{
				SealingFlag=optiongroup.getValue().toString();	
			}
			else
			{
				SealingFlag="";	
			}
			if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Not Finished")||optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished")){
				sampleFlag=optiongroup1.getValue().toString();
				System.out.println("SampleFlag: "+sampleFlag);
			}
			String sql =" insert into tbTubeProductionInfo" +
					" (ProductionNo, ProductionDate, IssueNo, rawItemCode, rawItemName, userIp, userName, EntryTime,Stepid,joborderNo,Sealingflag,sampleFlag)" +
					" values" +
					" ('"+productionNo+"'," +
					"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"'," +
					"''," +
					"'"+itemId().replace("'", "")+"'," +
					"' "+name+"'," +
					"'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"'," + 
					"CURRENT_TIMESTAMP" +
					",'"+cmbProductionStep.getValue()+"','"+cmbjoborder.getValue().toString()+"','"+SealingFlag+"','"+sampleFlag+"' )" ;
			System.out.println(sql);

			for (int i = 0; i < cmbFinishedProduct.size(); i++)
			{
				System.out.println("for");
				if (!txtTotalsqm.get(i).getValue().toString().isEmpty()||!afWastagesqm.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");
					
					if(!chkFG.get(i).booleanValue())
					{
					  flag=1;	
					}
					else
					{
						flag=0;	
					}
					if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished")){
						sampleFinFlag=1;
						flag=0;
					}
					/*else if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Not Finished")){
						sampleFinFlag=0;
						flag=1;
					}*/
					
						String query = 	" insert into tbTubeProductionDetails" +
								" (IssueNo, ProductionNo, FinishedProduct, IssueSqm, IssueQty, ShiftASqm, ShiftAQty, ShiftBSqm, ShiftBQty, TotalSqm, TotalQty, WastageSqm, WastageQty, WastagePercent, UserIp, UserName, EntryTime,flag,sampleFinFlag)" +
								" values" +
								" (' '," +
								"'"+txtProductionNo.getValue().toString()+"'," +
								"'"+cmbFinishedProduct.get(i).getValue().toString()+"'," +
								"'"+txtIssuesqm.get(i).getValue().toString()+"'," +
								"'"+txtIssuepcs.get(i).getValue().toString()+"'," +
								"'"+afShiftAsqm.get(i).getValue().toString()+"'," +
								"'"+afShiftApcs.get(i).getValue().toString()+"'," +
								"'"+afShiftBsqm.get(i).getValue().toString()+"'," +
								"'"+afShiftBpcs.get(i).getValue().toString()+"'," +
								"'"+txtTotalsqm.get(i).getValue().toString()+"'," +
								"'"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+afWastagesqm.get(i).getValue().toString()+"'," +
								"'"+afWastageTotal.get(i).getValue().toString()+"'," +
								"'"+afWastagePercent.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"'," +
								"'"+sessionBean.getUserName()+"'," +
								"CURRENT_TIMESTAMP,'"+flag+"','"+sampleFinFlag+"' " +
								" )" ;
						System.out.println(query);
						session.createSQLQuery(query).executeUpdate();	
					

					if(flag==0)
					{
						String sqlFG="insert into tbTubeFinishProduct values (' ','"+productionNo+"'" +
								",'"+cmbProductionStep.getValue()+"','"+cmbFinishedProduct.get(i).getValue().toString()+"'," +
								"'"+txtTotalsqm.get(i).getValue().toString()+"','"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"','"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"','"+cmbjoborder.getValue().toString()+"','"+sampleFinFlag+"')";
						System.out.println(sqlFG);
						session.createSQLQuery(sqlFG).executeUpdate();
					}
					
					if(i==0)
					{
						session.createSQLQuery(sql).executeUpdate();
					}
				}
			}

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private boolean doubleEntryCheck( int ar)
	{
		String captyion=cmbFinishedProduct.get(ar).getItemCaption(cmbFinishedProduct.get(ar).getValue().toString());
		
		for(int i=0;i<cmbFinishedProduct.size();i++)
		{
			if(cmbFinishedProduct.get(i).getValue()!=null)
			{
				if(ar!=i && captyion.equals(cmbFinishedProduct.get(i).getItemCaption(cmbFinishedProduct.get(i).getValue().toString())))
				{
				  return false;	
				}
			}
		}
		
		
		return true;
		
	}

	private Date getTime()
	{
		Date time = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;

		try
		{
			String sql = "";

			sql = "select convert(time, CURRENT_TIMESTAMP)";
			System.out.println("time sql"+sql);

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if(iter.hasNext())
			{
				time = (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("getTime error: "+ex, Notification.TYPE_ERROR_MESSAGE);
		}

		return time;
	}

	private boolean nullCheck() 
	{
		if (cmbjoborder.getValue() != null)
		{
			for (int i = 0; i < cmbFinishedProduct.size(); i++) 
			{
				
				if(cmbFinishedProduct.get(i).getValue()!=null)
				{
					 return true;
				}
				
				
				
				/*Object temp = cmbFinishedProduct.get(i).getItemCaption(cmbFinishedProduct.get(i).getValue());

				System.out.println(cmbFinishedProduct.get(i).getValue());

				if (temp != null && !cmbFinishedProduct.get(i).getValue().equals(("x#" + i))) 
				{
					if (!afShiftAsqm.get(i).getValue().toString().trim().isEmpty()) 
					{
						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid Qty .",Notification.TYPE_WARNING_MESSAGE);
					}
				}*/
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Section To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbTubeProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbTubeProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete tbTubeProductionDetails where ProductionNo='"+txtProductionNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbTubeProductionDetails where ProductionNo='"+txtProductionNo.getValue()+ "' ");
			
			session.createSQLQuery("delete from tbTubeFinishProduct where ProductionNo like '"+txtProductionNo.getValue()+"' ").executeUpdate();

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		}
	}

	private String autoProductionNo()
	{
		String autoNo=null;
		Transaction tx;

		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(ProductionNo, '', '')as int))+1, 1)as varchar) from tbTubeProductionInfo").list().iterator();

			if(iter.hasNext())
			{
				autoNo=iter.next().toString().trim();
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}

		return autoNo;
	}
}
