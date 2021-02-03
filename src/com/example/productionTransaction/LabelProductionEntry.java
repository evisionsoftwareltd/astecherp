package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class LabelProductionEntry extends Window 
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
	private Label lbljoborderNo;

	private Label lblLine;

	// ComboBox
	private ComboBox cmbProductionStep;
	private ComboBox cmbIssueNo;
	private ComboBox cmbRawItem;
	private ComboBox cmbjoborderNO;

	// TextRead
	private TextRead txtProductionNo;
	private TextRead txtIssue;   
	private TextRead txtShiftA;
	private TextRead txtShiftB;
	private TextRead txtTotal;
	private TextRead txtWastage;


	private TextField txtProdNo = new TextField();

	
	private PopupDateField dProductionDate;


	private DecimalFormat decFormat = new DecimalFormat("#0.000");
	private DecimalFormat df = new DecimalFormat("#0");

	// dateFormats
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");

	// boolean Values
	boolean isUpdate = false;
	boolean isFind = false;

	// table
	private Table table,tableRM;


	private ArrayList<Label> lblSl = new ArrayList<Label>();

	//	private ArrayList<CheckBox> chkFG=new ArrayList<CheckBox>();

	private ArrayList<ComboBox> cmbFinishedProduct = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> cmbRM = new ArrayList<ComboBox>();

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

	// Button
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");
	
	String[]a1={"Not Sample","Sample Not Finished","Sample Finished"};
	private List<String> lst1=Arrays.asList(a1[0],a1[1],a1[2]);
	private OptionGroup optiongroup1=new OptionGroup();

	public LabelProductionEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("Label Production Entry :: "+sessionBean.getCompany());
		this.setResizable(false);
		setContent(buildMainLayout());
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
		setWidth("1250px");
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
		
		lbljoborderNo = new Label("Job Order No :");
		lbljoborderNo.setImmediate(false);
		lbljoborderNo.setWidth("-1px");
		lbljoborderNo.setHeight("-1px");

		// cmbProductionStep
		cmbjoborderNO = new ComboBox();
		cmbjoborderNO.setImmediate(true);
		cmbjoborderNO.setNullSelectionAllowed(false);

		// lblIssueNo
		lblIssueNo = new Label("R/M Issue No :");
		lblIssueNo.setImmediate(false);
		lblIssueNo.setWidth("-1px");
		lblIssueNo.setHeight("-1px");

	
		cmbIssueNo = new ComboBox();
		cmbIssueNo.setImmediate(true);
		
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

		optiongroup1=new OptionGroup("", lst1);
		optiongroup1.select(a1[0].toString());
		optiongroup1.setStyleName("vertical");
		
	
		table = new Table();
		table.setColumnCollapsingAllowed(true);
		table.setFooterVisible(true);
		table.setWidth("1225px");
		table.setHeight("220px");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 10);
		
		table.addContainerProperty("Raw Material", ComboBox.class, new ComboBox());
		table.setColumnWidth("Raw Material", 180);

		table.addContainerProperty("Finished Product", ComboBox.class, new ComboBox());
		table.setColumnWidth("Finished Product", 180);

		table.addContainerProperty("Issue sqm ", TextRead.class, new TextRead());
		table.setColumnWidth("Issue sqm ", 55);

		table.addContainerProperty("Remain sqm ", TextRead.class, new TextRead());
		table.setColumnWidth("Remain sqm ", 55);

		table.addContainerProperty(" pcs", TextRead.class, new TextRead());
		table.setColumnWidth(" pcs", 55);

		// Shift A
		table.addContainerProperty("sqm", AmountField.class, new AmountField());
		table.setColumnWidth("sqm", 55);

		table.addContainerProperty("pcs", AmountField.class, new AmountField());
		table.setColumnWidth("pcs", 55);

		// Shift B
		table.addContainerProperty(" sqm", AmountField.class, new AmountField());
		table.setColumnWidth(" sqm", 55);

		table.addContainerProperty("pcs ", AmountField.class, new AmountField());
		table.setColumnWidth("pcs ", 55);

		// Total(A+B)
		table.addContainerProperty("  sqm", TextRead.class, new TextRead());
		table.setColumnWidth("  sqm", 55);

		table.addContainerProperty("pcs  ", TextRead.class, new TextRead());
		table.setColumnWidth("pcs  ", 55);

		// wastage
		table.addContainerProperty("   sqm", AmountField.class, new AmountField());
		table.setColumnWidth("   sqm", 55);

		table.addContainerProperty("%", AmountField.class, new AmountField());
		table.setColumnWidth("%", 55);

		table.addContainerProperty("Total", AmountField.class, new AmountField());
		table.setColumnWidth("Total", 55);

		// adding components to mainLayout (distance: 30px)
		mainLayout.addComponent(lblProductionNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtProductionNo, "top: 18px; left: 130px;");

		mainLayout.addComponent(lblProductionDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dProductionDate, "top: 48px; left: 130px;");

		mainLayout.addComponent(lblProductionStep, "top: 80px; left: 20px;");
		mainLayout.addComponent(cmbProductionStep, "top: 78px; left: 130px;");
		
		mainLayout.addComponent(lbljoborderNo, "top: 110px; left: 20px;");
		mainLayout.addComponent(cmbjoborderNO, "top: 108px; left: 130px;");

		
		mainLayout.addComponent(optiongroup1, "top: 150px; left: 120px;");

		mainLayout.addComponent(txtIssue, "top: 230px; left: 390px;");
		mainLayout.addComponent(lblIssue, "top: 230px; left: 490px;");

		mainLayout.addComponent(txtShiftA, "top: 230px; left: 609px;");
		mainLayout.addComponent(lblShiftA, "top: 230px; left: 664px;");

		mainLayout.addComponent(txtShiftB, "top: 230px; left: 755px;");
		mainLayout.addComponent(lblShiftB, "top: 230px; left: 808px;");

		mainLayout.addComponent(txtTotal, "top: 230px; left: 901px;");
		mainLayout.addComponent(lblTotal, "top: 230px; left: 961px;");

		mainLayout.addComponent(txtWastage, "top: 230px; left: 1047px;");
		mainLayout.addComponent(lblWastage, "top: 230px; left: 1116px;");

		mainLayout.addComponent(table, "top: 250px; left: 10px;");

		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:470.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:500.0px; left:270.0px;");


		return mainLayout;
	}


	private void joborderDataLoad()
	{
		cmbjoborderNO.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
 
			sql = "select distinct 0, JobOrderNo  from tbLabelIssueInfo where  IssueTo like '"+cmbProductionStep.getValue().toString()+"' and JobOrderNo is not null  ";
			System.out.print("cmbProductionStepData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbjoborderNO.addItem(element[1].toString());
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}
	
	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(dProductionDate);
		focusComp.add(cmbProductionStep);
		focusComp.add(cmbIssueNo);

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

		lblIssueNo.setEnabled(!b);
		cmbIssueNo.setEnabled(!b);

		table.setEnabled(!b);

		lblIssue.setEnabled(!b);
		txtIssue.setEnabled(!b);

		lblShiftA.setEnabled(!b);
		txtShiftA.setEnabled(!b);

		lblShiftB.setEnabled(!b);
		txtShiftB.setEnabled(!b);

		lblTotal.setEnabled(!b);
		txtTotal.setEnabled(!b);

		lblWastage.setEnabled(!b);
		txtWastage.setEnabled(!b);
		
		lbljoborderNo.setEnabled(!b);
		cmbjoborderNO.setEnabled(!b);
		optiongroup1.setEnabled(!b);
	}

	private void txtClear()
	{
		txtProductionNo.setValue("");
		dProductionDate.setValue(new java.util.Date());
		cmbProductionStep.setValue(null);
		cmbjoborderNO.setValue(null);
		cmbIssueNo.setValue(null);
		optiongroup1.select(a1[0].toString());
	}

	private void tableClear()
	{
		for(int i = 0; i < cmbFinishedProduct.size(); i++)
		{
			cmbFinishedProduct.get(i).removeAllItems();

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
					
					if(cmbjoborderNO.getValue()!=null)
					{
						if(RmCHeck())
						{
							if(FgCheck())
							{
								if(nullCheck())
								{
									saveButtonEvent();
									cButton.btnNew.focus();
									isFind = false;
									isUpdate = false;
								}	
							}
							else
							{
								showNotification("Please Select Finished Goods", Notification.TYPE_WARNING_MESSAGE);	
							}		
						}
						else	
						{
							showNotification("Please Select Raw Material", Notification.TYPE_WARNING_MESSAGE); 	
						}		
					}
					else
					{
						showNotification("Please Select Job Order No", Notification.TYPE_WARNING_MESSAGE);
						cmbjoborderNO.focus();	
					}							
				}
				else
				{
					showNotification("Please Select Production Step", Notification.TYPE_WARNING_MESSAGE);
					cmbProductionStep.focus();
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
					tableClear();
					joborderDataLoad();
				}
			}
		});
		

		cmbjoborderNO.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbjoborderNO.getValue()!=null)
				{
					for(int i=0;i<cmbFinishedProduct.size();i++)
					{
						cmbFinishedProductData(i);
						RawMaterialDataAdd(i);
					}	
				}
				
				else
				{
					 for(int i=0;i<cmbFinishedProduct.size();i++)
					 {
						 cmbFinishedProduct.get(i).removeAllItems();
						 cmbRM.get(i).removeAllItems();
					 }
				}
			}
		});

	}

	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}


	private void findButtonEvent() 
	{
		Window win = new LabelProductionFindWindow(sessionBean, txtProdNo);
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

			String sql1= "select ProductionNo,ProductionDate,Stepid,joborderNo,sampleFlag from tbLabelProductionInfo "
					    + " where ProductionNo like '"+txtProdNo+"' ";
			
			
			System.out.println(sql1);
			List list=session.createSQLQuery(sql1).list();
			for(Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object element[] = (Object[])iter.next();
				txtProductionNo.setValue(element[0]);
				dProductionDate.setValue(element[1]);
				cmbProductionStep.setValue(element[2].toString());
				cmbjoborderNO.setValue(element[3].toString());
				
				if(element[4].toString().equalsIgnoreCase("Sample Not Finished")){
					optiongroup1.select(a1[1]);
				}
				else if(element[4].toString().equalsIgnoreCase("Sample Finished")){
					optiongroup1.select(a1[2]);
				}
				else{
					optiongroup1.select(a1[0]);
				}
				
				
				
				System.out.println("Hello world");

			}

			String sql2 = "";
			sql2 = "Select * from tbLabelProductionDetails  where ProductionNo='"+txtProdNo+"'";
			System.out.println("Check By Mezbah: "+sql2);
			List list1=session.createSQLQuery(sql2).list();

			int i = 0;

			for (Iterator iter2 = list1.iterator(); iter2.hasNext();)
			{
				Object element[] = (Object[])iter2.next();
				
				cmbRM.get(i).setValue(element[19].toString());

				cmbFinishedProduct.get(i).setValue(element[3]);
				txtIssuesqm.get(i).setValue(decFormat.format(element[4]).toString());
				//				txtIssueRemainsqm.get(i).setValue(element[].toString());
				txtIssuepcs.get(i).setValue(decFormat.format(element[5]).toString());
				afShiftAsqm.get(i).setValue(decFormat.format(element[6]).toString());
				afShiftApcs.get(i).setValue(decFormat.format(element[7]).toString());
				afShiftBsqm.get(i).setValue(decFormat.format(element[8]).toString());
				afShiftBpcs.get(i).setValue(decFormat.format(element[9]).toString());
				txtTotalsqm.get(i).setValue(decFormat.format(element[10]).toString());
				txtTotalpcs.get(i).setValue(decFormat.format(element[11]).toString());
				afWastagesqm.get(i).setValue(decFormat.format(element[12]).toString());
				afWastagePercent.get(i).setValue(decFormat.format(element[14]).toString());
				afWastageTotal.get(i).setValue((element[13]).toString());

				i++;
				tableRowAdd(i+1);
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error is"+exp,Notification.TYPE_WARNING_MESSAGE);
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
			

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "";
		

			sql = 	"select distinct fgCode,(select vProductName from tbFinishedProductInfo where vProductId like fgCode) as  productName  "  
					+"from tbLabelIssueInfo where joborderNo like '"+cmbjoborderNO.getValue().toString()+"' ";

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

	

	private void RawMaterialDataAdd(int ar)
	{
		cmbRM.get(ar).removeAllItems();
		Transaction tx= null;
		try
		{
			

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "";
			
			sql=    "select distinct  b.RmCode,(select vRawItemName from tbRawItemInfo where vRawItemCode like RmCode )as RmName "  
					   + "from tbLabelIssueInfo a inner  join  tbLabelIssueDetails b on a.jobNo=b.jobNo  "
						+" where a.joborderNo like '"+cmbjoborderNO.getValue().toString()+"' ";
		
			List lst = session.createSQLQuery(sql).list();
			System.out.println(sql);

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbRM.get(ar).addItem(element[0].toString());
				cmbRM.get(ar).setItemCaption(element[0].toString() , element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	


	private void cmbRawItemData()
	{
		cmbRawItem.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

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

	}

	private void cmbIssueNoData()
	{
		cmbIssueNo.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			if(!isFind)
			{
				Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session1.beginTransaction();
				session1.createSQLQuery("exec PrcLabelIssueNoUpdate ''").executeUpdate();
				tx.commit();
				System.out.print("Every Thing ");
			}
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			
			sql ="select distinct 0,jobNo from tbLabelIssueInfo where issueTo like '"+cmbProductionStep.getValue().toString()+"' and flag='1' and joborderNo like '"+cmbjoborderNO.getValue().toString()+"' ";
			System.out.println("cmbIssuNoData: "+sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbIssueNo.addItem(element[1].toString());
				cmbIssueNo.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}

	}

	private void cmbProductionStepData()
	{
		cmbProductionStep.removeAllItems();
		String sql = "";

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql = "select StepId, StepName from tbProductionStep where productionTypeId like 'PT-2'";
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

	private void tableInitialise()
	{
		for(int i = 0; i < 5; i++)
		{
			tableRowAdd(i);
		}
		
		
	}

	private double totlaSqmCalc(int ar){

		double shiftAsqm = 0;
		double shiftBsqm = 0;

		shiftAsqm = Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0":afShiftAsqm.get(ar).getValue().toString());
		shiftBsqm = Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?"0.0":afShiftBsqm.get(ar).getValue().toString());

		double totalSqm = 0;
		totalSqm = shiftAsqm + shiftBsqm;

		return totalSqm;
	}

	private void tableRowAdd(final int ar)
	{
		try
		{
			lblSl.add(ar, new Label());
			lblSl.get(ar).setWidth("100%");
			lblSl.get(ar).setValue(ar+1);

			
			
			cmbRM.add(ar, new ComboBox());
			cmbRM.get(ar).setWidth("100%");
			cmbRM.get(ar).setImmediate(true);
			
			cmbRM.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbRM.get(ar).getValue()!=null)
					{

								if(doubleCheck(ar) )
								{
									
									if(cmbFinishedProduct.get(ar).getValue()!=null)
									{
										Session session = SessionFactoryUtil.getInstance().getCurrentSession();
										Transaction tx = session.beginTransaction();
										try
										{
											String sql="select issueQty,leftQty from funcLabelProductionLeftQty " +
													"('"+cmbjoborderNO.getValue()+"','"+cmbProductionStep.getValue()+"'," +
													"'"+cmbFinishedProduct.get(ar).getValue()+"','"+cmbRM.get(ar).getValue().toString()+"') order by rawItemId";
					
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
											totalItem();
										}
										catch(Exception ex)
										{
											System.out.println(ex);
										}

										if(cmbFinishedProduct.size()-1==ar)
										{	
											tableRowAdd(ar+1);
												
										}	
									}
								
								}
								else 
								{
									if (cmbRM.get(ar).getValue()!=null) 
									{
										cmbRM.get(ar).setValue(null);
										getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
									}
								}	
						
					}
					else
					{
						txtIssuesqm.get(ar).setValue("");
						txtIssueRemainsqm.get(ar).setValue("");
						totalItem();
					}
				}
			});
			
			

			cmbFinishedProduct.add(ar, new ComboBox());
			cmbFinishedProduct.get(ar).setWidth("100%");
			cmbFinishedProduct.get(ar).setImmediate(true);

			cmbFinishedProduct.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{

						if(cmbFinishedProduct.get(ar).getValue()!=null)
						{
								if(doubleCheck(ar) )
								{
									
									if(cmbRM.get(ar).getValue()!=null)
									{
										Session session = SessionFactoryUtil.getInstance().getCurrentSession();
										Transaction tx = session.beginTransaction();
										try
										{
										 
											
											String sql="select issueQty,leftQty from funcLabelProductionLeftQty " +
													"('"+cmbjoborderNO.getValue()+"','"+cmbProductionStep.getValue()+"'," +
													"'"+cmbFinishedProduct.get(ar).getValue()+"','"+cmbRM.get(ar).getValue().toString()+"') order by rawItemId";
					
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
											totalItem();

										}
										catch(Exception ex)
										{
											System.out.println(ex);
										}

										if(cmbFinishedProduct.size()-1==ar)
										{	
											tableRowAdd(ar+1);
												
										}	
									}
								
								}
								else 
								{
									
										cmbFinishedProduct.get(ar).setValue(null);
										getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
									
								}	
						}
						else
						{
						}
					}
					else
					{
						txtIssuesqm.get(ar).setValue("");
						txtIssueRemainsqm.get(ar).setValue("");
						totalItem();
						
					}
				}
			});

			txtIssuesqm.add(ar, new TextRead(1));
			txtIssuesqm.get(ar).setWidth("100%");
			txtIssuesqm.get(ar).setImmediate(true);

			txtIssuesqm.get(ar).addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event) {

					if(!txtIssuesqm.get(ar).getValue().toString().isEmpty())
					{
						List std=perTubeCalc( ar);
						if(std.iterator().hasNext()){
							Object[] element = (Object[]) std.iterator().next();
							double target=Double.parseDouble((String) txtIssuesqm.get(ar).getValue())*Double.parseDouble(element[1].toString());
							System.out.println("Target: "+target);
							txtIssuepcs.get(ar).setValue(df.format(target));
							System.out.println("Target after: "+target);
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

			
			afShiftAsqm.get(ar).addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event) 
				{

					if(cmbFinishedProduct.get(ar).getValue()!=null){
						if(!afShiftAsqm.get(ar).getValue().toString().isEmpty())
						{

							double isSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());
							double totalSqm=totlaSqmCalc(ar);
							if(!isFind){
								if(isSqm>=totalSqm)
								{

									List std=perTubeCalc( ar);
									if(std.iterator().hasNext())
									{
										Object[] element = (Object[]) std.iterator().next();
										double target=Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0":
											afShiftAsqm.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());

										afShiftApcs.get(ar).setValue(df.format(target));
										txtTotalsqm.get(ar).setValue(totalSqm);

										double totalPcs = 0;
										totalPcs=totalSqm*Double.parseDouble(element[1].toString());
										txtTotalpcs.get(ar).setValue(df.format(totalPcs));

									}
								}
								else{

										afShiftAsqm.get(ar).setValue("");
										showNotification("Shift A Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);	
									}
							}
							else{
								List std=perTubeCalc( ar);
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(afShiftAsqm.get(ar).getValue().toString().isEmpty()?"0.0":
										afShiftAsqm.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());

									afShiftApcs.get(ar).setValue(df.format(target));
									txtTotalsqm.get(ar).setValue(totalSqm);

									double totalPcs = 0;
									totalPcs=totalSqm*Double.parseDouble(element[1].toString());
									txtTotalpcs.get(ar).setValue(df.format(totalPcs));

								}
							}
						}
					}
				}
			});

			afShiftApcs.add(ar, new AmountField());
			afShiftApcs.get(ar).setWidth("100%");
			afShiftApcs.get(ar).setImmediate(true);

			afShiftApcs.get(ar).addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(!afShiftApcs.get(ar).getValue().toString().isEmpty())
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(afShiftApcs.get(ar).getValue().toString().isEmpty()?"0.0":afShiftApcs.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
								System.out.println("Target: "+target);
								afShiftAsqm.get(ar).setValue(decFormat.format(target));
							}
						}
					}
				}
			});

			afShiftBsqm.add(ar, new AmountField());
			afShiftBsqm.get(ar).setWidth("100%");
			afShiftBsqm.get(ar).setImmediate(true);


			afShiftBsqm.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null){
						if(!afShiftBsqm.get(ar).getValue().toString().isEmpty())
						{

							double isSqm=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());
							double totalSqm=totlaSqmCalc(ar);
							if(!isFind){
								if(isSqm>=totalSqm)
								{

									List std=perTubeCalc( ar);
									if(std.iterator().hasNext())
									{
										Object[] element = (Object[]) std.iterator().next();
										double target=Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?"0.0":
											afShiftBsqm.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());

										afShiftBpcs.get(ar).setValue(df.format(target));
										txtTotalsqm.get(ar).setValue(totalSqm);

										double totalPcs = 0;
										totalPcs=totalSqm*Double.parseDouble(element[1].toString());
										txtTotalpcs.get(ar).setValue(df.format(totalPcs));

										if(totalSqm>isSqm)
										{
											showNotification("Total Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
											afShiftAsqm.get(ar).setValue("");
											afShiftAsqm.get(ar).focus();
										}
									}
								}
								else{
										afShiftBsqm.get(ar).setValue("");
										showNotification("Shift B Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);	

								}
							}
							else{
								List std=perTubeCalc( ar);
								if(std.iterator().hasNext())
								{
									Object[] element = (Object[]) std.iterator().next();
									double target=Double.parseDouble(afShiftBsqm.get(ar).getValue().toString().isEmpty()?
											"0.0":afShiftBsqm.get(ar).getValue().toString())*Double.parseDouble(element[1].toString());
									afShiftBpcs.get(ar).setValue(df.format(target));
									txtTotalsqm.get(ar).setValue(totalSqm);

									double totalPcs = 0;
									totalPcs=totalSqm*Double.parseDouble(element[1].toString());
									txtTotalpcs.get(ar).setValue(df.format(totalPcs));

									/*if(totalSqm>isSqm)
									{
										showNotification("Total Qty exceed Issue Left Qty",Notification.TYPE_HUMANIZED_MESSAGE);
										afShiftAsqm.get(ar).setValue("");
										afShiftAsqm.get(ar).focus();
									}*/
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
			});

			txtTotalsqm.add(ar, new TextRead(1));
			txtTotalsqm.get(ar).setWidth("100%");
			txtTotalsqm.get(ar).setImmediate(true);

			txtTotalpcs.add(ar, new TextRead(1));
			txtTotalpcs.get(ar).setWidth("100%");
			txtTotalpcs.get(ar).setImmediate(true);

			afWastagesqm.add(ar, new AmountField());
			afWastagesqm.get(ar).setWidth("100%");
			afWastagesqm.get(ar).setImmediate(true);

			afWastagesqm.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(!afWastagesqm.get(ar).getValue().toString().isEmpty())
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext()){
								Object[] element = (Object[]) std.iterator().next();

								double Issuesqm = 0;
								double Wastagesqm = 0;

								Issuesqm = Double.parseDouble(txtIssuesqm.get(ar).toString().isEmpty()?"0.0":txtIssuesqm.get(ar).toString());
								double issueRemain=Double.parseDouble(txtIssueRemainsqm.get(ar).toString().isEmpty()?"0.0":txtIssueRemainsqm.get(ar).toString());;
								Wastagesqm = Double.parseDouble(afWastagesqm.get(ar).getValue().toString().isEmpty()?"0.0":afWastagesqm.get(ar).getValue().toString());

								double shiftA= Double.parseDouble(afShiftAsqm.get(ar).toString().isEmpty()?"0.0":afShiftAsqm.get(ar).toString());
								double shiftB= Double.parseDouble(afShiftBsqm.get(ar).toString().isEmpty()?"0.0":afShiftBsqm.get(ar).toString());
								double total=shiftA+shiftB+Wastagesqm;
								
								if(!isFind){
									if(total>issueRemain)
									{
											showNotification("Total and Wastage qty Exceed Issue Remain Qty",Notification.TYPE_WARNING_MESSAGE);
											afWastagesqm.get(ar).setValue("");	
									}
									else{
										double wastagePercent = 0;
										wastagePercent = (100 * Wastagesqm)/Issuesqm;

										afWastagePercent.get(ar).setValue(decFormat.format(wastagePercent));

										double wastageTotal=Wastagesqm*Double.parseDouble(element[1].toString());
										afWastageTotal.get(ar).setValue(df.format(wastageTotal));
									}
								}
								else{
									double wastagePercent = 0;
									wastagePercent = (100 * Wastagesqm)/Issuesqm;

									afWastagePercent.get(ar).setValue(decFormat.format(wastagePercent));

									double wastageTotal=Wastagesqm*Double.parseDouble(element[1].toString());
									afWastageTotal.get(ar).setValue(df.format(wastageTotal));
								}
							}
						}
					}
				}
			});

			afWastagePercent.add(ar, new AmountField());
			afWastagePercent.get(ar).setWidth("100%");
			afWastagePercent.get(ar).setImmediate(true);

			afWastageTotal.add(ar, new AmountField());
			afWastageTotal.get(ar).setWidth("100%");
			afWastageTotal.get(ar).setImmediate(true);

			afWastageTotal.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if(cmbFinishedProduct.get(ar).getValue()!=null)
					{
						if(!afWastageTotal.get(ar).getValue().toString().isEmpty())
						{
							List std=perTubeCalc( ar);
							if(std.iterator().hasNext())
							{
								Object[] element = (Object[]) std.iterator().next();
								double target=Double.parseDouble(afWastageTotal.get(ar).getValue().toString().isEmpty()?"0.0":afWastageTotal.get(ar).getValue().toString())/Double.parseDouble(element[1].toString());
								System.out.println("Target: "+target);
								afWastagesqm.get(ar).setValue(decFormat.format(target));
							}
						}
					}
				}
			});


			table.addItem(new Object[] {lblSl.get(ar), cmbRM.get(ar),cmbFinishedProduct.get(ar), txtIssuesqm.get(ar),txtIssueRemainsqm.get(ar), txtIssuepcs.get(ar), afShiftAsqm.get(ar), afShiftApcs.get(ar), afShiftBsqm.get(ar), afShiftBpcs.get(ar), txtTotalsqm.get(ar), txtTotalpcs.get(ar), afWastagesqm.get(ar), afWastagePercent.get(ar), afWastageTotal.get(ar)}, ar);

		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	

/*	public boolean doubleCheck(int ar)
	{
		String value=cmbFinishedProduct.get(ar).getValue().toString();
		for(int x=0;x<cmbFinishedProduct.size();x++){
			if(cmbFinishedProduct.get(x).getValue()!=null){
				if(x!=ar&&value.equalsIgnoreCase(cmbFinishedProduct.get(x).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}*/
	
	public boolean doubleCheck(int ar)
	{
		String Finish="";
		String Raw="";
		
		if(cmbFinishedProduct.get(ar).getValue()!=null)
		{
			Finish=	cmbFinishedProduct.get(ar).getValue().toString();
		}
		
		if(cmbRM.get(ar).getValue()!=null)
		{
			 Raw=cmbRM.get(ar).getValue().toString();	
		}
		String Cation=Finish+Raw;
		
		
		for(int i=0;i<cmbFinishedProduct.size();i++)
		{
			
			if(cmbFinishedProduct.get(i).getValue()!=null && cmbRM.get(i).getValue()!=null)
			{
				String fg=cmbFinishedProduct.get(i).getValue().toString();
				String rm=cmbRM.get(i).getValue().toString();
				String compareCaption=fg+rm;
				
				if(Cation.equalsIgnoreCase(compareCaption) && ar!=i)
				{
					return false;
				}
				
			}
		}
		
		return true;
		
	}
	
	
	

	private void totalItem()
	{
		int totalItem = 0;

		for(int i = 0; i < cmbFinishedProduct.size(); i++)
		{
			if(cmbFinishedProduct.get(i).getValue()!=null)
			{
				totalItem++;
			}

			table.setColumnFooter("Finished Product", "Total Item = "+totalItem);
		}
	}


	public List perTubeCalc(int ar)
	{
		Transaction tx = null;
		String sql = "";
		List std = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String productId = null;
			
			sql="select 0, isnull(perSqm,0)as qty  from tbFinishedProductDetails  where fgId like '"+cmbFinishedProduct.get(ar).getValue().toString()+"' and rawItemCode like '"+cmbRM.get(ar).getValue().toString()+"'";
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

							if (deleteData(session, tx))
							{
								insertData("Update");
							}
							else 
							{
								tx.rollback();
							}
							componentIni(true);
							btnIni(true);
							tableClear();
							txtClear();
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
							insertData("New");
							componentIni(true);
							btnIni(true);
							tableClear();
							txtClear();							
						}
					}
				});
			}
		
	}

	private void insertData(String Type)
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String productionNo = "";
		String name="";
		String sampleFlag="";
		int sampleFinFlag=0;

		if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Not Finished")||optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished")){
			sampleFlag=optiongroup1.getValue().toString();
			System.out.println("SampleFlag: "+sampleFlag);
		}
		
		try
		{
			
				productionNo = txtProductionNo.getValue().toString();
			

			String sql =" insert into tbLabelProductionInfo" +
					" (ProductionNo, ProductionDate,  rawItemCode, userIp, userName, EntryTime,Stepid,joborderNo,sampleFlag)" +
					" values" +
					" ('"+productionNo+"'," +
					"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"'," +
					"''," +
					"'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"CURRENT_TIMESTAMP" +
					",'"+cmbProductionStep.getValue()+"','"+cmbjoborderNO.getValue().toString()+"','"+sampleFlag+"' )" ;
			System.out.println(sql);
			
			String sqlUpdate =" insert into tbUdLabelProductionInfo" +
					" (ProductionNo, ProductionDate,  rawItemCode, userIp, userName, EntryTime,Stepid,joborderNo,sampleFlag,type)" +
					" values" +
					" ('"+productionNo+"'," +
					"'"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"'," +
					"''," +
					"'"+sessionBean.getUserIp()+"'," +
					"'"+sessionBean.getUserName()+"'," +
					"CURRENT_TIMESTAMP" +
					",'"+cmbProductionStep.getValue()+"','"+cmbjoborderNO.getValue().toString()+"','"+sampleFlag+"','"+Type+"' )" ;
			System.out.println(sqlUpdate);
			
			
			for (int i = 0; i < cmbFinishedProduct.size(); i++)
			{
				System.out.println("for");
				if (!afShiftAsqm.get(i).getValue().toString().isEmpty())
				{
					System.out.println("if");
					
					if(optiongroup1.getValue().toString().equalsIgnoreCase("Sample Finished"))
					{
						sampleFinFlag=1;
					}

					String query = 	" insert into tbLabelProductionDetails" +
							" (ProductionNo, FinishedProduct, IssueSqm, IssueQty, ShiftASqm, ShiftAQty, ShiftBSqm, ShiftBQty, TotalSqm, TotalQty, WastageSqm, WastageQty, WastagePercent, UserIp, UserName, EntryTime,sampleFinFlag,rawItemCode)" +
							" values" +
							" ( "
							+"'"+txtProductionNo.getValue().toString()+"'," +
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
							"CURRENT_TIMESTAMP" +
							",'"+sampleFinFlag+"','"+cmbRM.get(i).getValue().toString()+"' )" ;
					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();
					
					String queryUpdate = 	" insert into tbUdLabelProductionDetails" +
							" (ProductionNo, FinishedProduct, IssueSqm, IssueQty, ShiftASqm, ShiftAQty, ShiftBSqm, ShiftBQty, TotalSqm, TotalQty, WastageSqm, WastageQty, WastagePercent, UserIp, UserName, EntryTime,sampleFinFlag,rawItemCode)" +
							" values" +
							" ( "
							+"'"+txtProductionNo.getValue().toString()+"'," +
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
							"CURRENT_TIMESTAMP" +
							",'"+sampleFinFlag+"','"+cmbRM.get(i).getValue().toString()+"' )" ;
					System.out.println(query);
					session.createSQLQuery(queryUpdate).executeUpdate();
					
					
					if(i==0)
					{
						session.createSQLQuery(sql).executeUpdate();
						session.createSQLQuery(sqlUpdate).executeUpdate();
					}
					
					if(cmbProductionStep.getValue().toString().equalsIgnoreCase("LevelSTP-2")||sampleFinFlag==1)
					{
						String sqlFG="insert into tbLabelFinishProduct values ('','"+productionNo+"'" +
								",'"+cmbProductionStep.getValue()+"','"+cmbFinishedProduct.get(i).getValue().toString()+"'," +
								"'"+txtTotalsqm.get(i).getValue().toString()+"','"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP," +
								"'"+cmbjoborderNO.getValue().toString()+"','"+sampleFinFlag+"','"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"')";
						System.out.println(sqlFG);
						session.createSQLQuery(sqlFG).executeUpdate();
						
						String sqlFGUpdate="insert into tbUDLabelFinishProduct values ('','"+productionNo+"'" +
								",'"+cmbProductionStep.getValue()+"','"+cmbFinishedProduct.get(i).getValue().toString()+"'," +
								"'"+txtTotalsqm.get(i).getValue().toString()+"','"+txtTotalpcs.get(i).getValue().toString()+"'," +
								"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserName()+"',CURRENT_TIMESTAMP," +
								"'"+cmbjoborderNO.getValue().toString()+"','"+sampleFinFlag+"','"+dFormatSql.format(dProductionDate.getValue())+" "+getTime()+"','"+Type+"')";
						System.out.println(sqlFG);
						session.createSQLQuery(sqlFG).executeUpdate();
					}
				}
			}

			tx.commit();
			this.getParent().showNotification("All information is saved successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			System.out.println(exp);
		}
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
		if (cmbjoborderNO.getValue() != null)
		{
			for (int i = 0; i < cmbFinishedProduct.size(); i++) 
			{
				Object temp = cmbFinishedProduct.get(i).getItemCaption(cmbFinishedProduct.get(i).getValue());

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
						afShiftAsqm.get(i).focus();
					}
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Job Order No .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}
	
	
	
	private boolean RmCHeck()
	{
		
		
		for(int i=0;i<cmbFinishedProduct.size();i++)
		{
		  if(cmbRM.get(i).getValue()!=null)
		  {
			 return true;  
		  }
		}
		return false;
		
	}
	
	
	private boolean FgCheck()
	{
		
		
		for(int i=0;i<cmbFinishedProduct.size();i++)
		{
		  if(cmbFinishedProduct.get(i).getValue()!=null)
		  {
			 return true;  
		  }
		}
		return false;
		
	}
	
	

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+"' ").executeUpdate();
			System.out.println("delete tbLabelProductionInfo where ProductionNo='"+txtProductionNo.getValue()+ "' ");

			session.createSQLQuery("delete tbLabelProductionDetails where ProductionNo='"+txtProductionNo.getValue()+"' ").executeUpdate();
			System.out.println("delete tbLabelProductionDetails where ProductionNo='"+txtProductionNo.getValue()+"' ");
			session.createSQLQuery(" delete from tbLabelFinishProduct where ProductionNo like '"+txtProductionNo.getValue().toString()+"' ").executeUpdate();
			
			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
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

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(ProductionNo, '', '')as int))+1, 1)as varchar) from tbLabelProductionInfo").list().iterator();

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
