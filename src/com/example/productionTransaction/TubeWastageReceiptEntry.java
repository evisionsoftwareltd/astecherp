package com.example.productionTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class TubeWastageReceiptEntry extends Window 
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Panel searchPanel;

	// Labels
	private Label lblJobNo;
	private Label lblJobDate;
	private Label lblChallanNo;
	private Label lblFrom;
	private Label lblTo;
	private Label lblRawItemName;
	private Label lblUnit;
	private Label lblSectionStock;
	private Label lblFloorStock;
	private Label lblPrintedStock;
	//private Label lblTargetQty;
	private Label lblFinishedGoods;
	private Label lblIssueQty;
	private Label lblIssueTarget;
	private Label lblRemarks;

	private Label lblLine;

	private Label lblFromDate;
	private Label lblToDate;

	// ComboBox
	private ComboBox cmbFrom;
	private ComboBox cmbTo;
	private ComboBox cmbRawItemName;

	// TextRead
	private TextRead txtJobNo;
	private TextRead txtUnit;
	private TextRead txtSectionStock;
	private TextRead txtFloorStock;
	private TextRead txtPrintedStock;
	//private TextRead txtStockQty;

	// TextField
	private TextField txtChallanNo;
	private TextField findJobNo;

	// TextArea
	private TextArea txtRemarks;

	// popupdatefields
	private PopupDateField dJobDate;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;

	// AmountField
	private AmountField txtIssueQty;
	//private AmountField txtReceivedQty;

	// AmountCommaSeperator

	// decimalFormats
	private DecimalFormat decFormat = new DecimalFormat("#0.00");
	private DecimalFormat df = new DecimalFormat("#0");

	// dateFormats
	private SimpleDateFormat dFormatSql = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dF = new SimpleDateFormat("dd-MM-yyyy");

	// boolean Values
	boolean isUpdate = false;
	boolean isFind = false;

	// table
	private Table tableFG;

	//ArrayList tableFG
	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbRawItem = new ArrayList<ComboBox>();
	private ArrayList<TextRead> txtItemUnit  = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtStockQty  = new ArrayList<TextRead>();
	private ArrayList<AmountField> txtReceivedQty  = new ArrayList<AmountField>();

	Table tableFind = new Table();
	ArrayList <Label> lblFindJobNo=new ArrayList<Label>();
	ArrayList <Label> lblFindJobDate=new ArrayList<Label>();

	private NativeButton findButton = new NativeButton("Search");

	// Button
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "Preview", "", "Exit");

	public TubeWastageReceiptEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("WASTAGE RECEIPT ENTRY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		eventAction();
		//		cmbFromData();
		//		cmbRawItemNameData();
		//		tableInitialise();
		

		txtClear();
		tableClear();
		tableFindClear();
		
		btnIni(true);
		componentIni(true);
		searchPanelIni(true);

		focusMove();
		//
		cButton.btnNew.focus();

	}

	private AbsoluteLayout buildMainLayout()
	{
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("960px");
		setHeight("410px");

		// lblJobNo
		lblJobNo = new Label("Wastage Job No :");
		lblJobNo.setImmediate(false);
		lblJobNo.setWidth("-1px");
		lblJobNo.setHeight("-1px");

		// txtJobNo
		txtJobNo = new TextRead();
		txtJobNo.setImmediate(false);
		txtJobNo.setWidth("80px");
		txtJobNo.setHeight("23px");

		// lblJobDate
		lblJobDate = new Label("Date :");
		lblJobDate.setImmediate(false);
		lblJobDate.setWidth("-1px");
		lblJobDate.setHeight("-1px");

		// dJobDate
		dJobDate = new PopupDateField();
		dJobDate.setImmediate(false);
		dJobDate.setWidth("-1px");
		dJobDate.setHeight("-1px");
		dJobDate.setDateFormat("dd-MM-yyyy");
		dJobDate.setValue(new java.util.Date());
		dJobDate.setResolution(PopupDateField.RESOLUTION_DAY);

		//tableFG
		tableFG=new Table();
		tableFG.setWidth("630px");
		tableFG.setHeight("160px");
		tableFG.setColumnCollapsingAllowed(true);
		tableFG.setImmediate(true);

		tableFG.addContainerProperty("SL", Label.class, new Label());
		tableFG.setColumnWidth("SL", 20);
		tableFG.setColumnAlignment("SL", tableFG.ALIGN_CENTER);

		tableFG.addContainerProperty("Name", ComboBox.class, new ComboBox());
		tableFG.setColumnWidth("Name", 280);

		tableFG.addContainerProperty("Unit", TextRead.class, new TextRead());
		tableFG.setColumnWidth("Unit", 80);
		tableFG.setColumnAlignment("Unit", tableFG.ALIGN_CENTER);

		tableFG.addContainerProperty("Stock Qty", TextRead.class, new TextRead());
		tableFG.setColumnWidth("Stock Qty", 80);
		tableFG.setColumnAlignment("Stock Qty", tableFG.ALIGN_RIGHT);

		tableFG.addContainerProperty("Receive Qty", AmountField.class, new AmountField());
		tableFG.setColumnWidth("Receive Qty", 80);
		tableFG.setColumnAlignment("Receive Qty", tableFG.ALIGN_RIGHT);

		tableFG.setEditable(false);

		//searchPanel
		searchPanel = new Panel();
		searchPanel.setImmediate(true);
		searchPanel.setWidth("250px");
		searchPanel.setHeight("300px");
		searchPanel.setStyleName("radius");

		// lblFromDate
		lblFromDate = new Label("From Date :");		
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");

		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(false);
		dFromDate.setWidth("-1px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// lblToDate
		lblToDate = new Label("To Date :");
		lblToDate.setImmediate(false);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");

		// dToDate
		dToDate = new PopupDateField();
		dToDate.setImmediate(false);
		dToDate.setWidth("-1px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);

		// findButton
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setIcon(new ThemeResource("../icons/find.png"));

		// tableFind
		tableFind.setSelectable(true);
		tableFind.setWidth("240px");
		tableFind.setHeight("200px");

		tableFind.addContainerProperty("JOB NO", Label.class, new Label());
		tableFind.setColumnWidth("JOB NO", 50);
		tableFind.setColumnAlignment("JOB NO", tableFind.ALIGN_CENTER);

		tableFind.addContainerProperty("JOB DATE", Label.class, new Label());
		tableFind.setColumnWidth("JOB DATE", 110);
		tableFind.setColumnAlignment("JOB DATE", tableFind.ALIGN_CENTER);

		tableFind.setImmediate(true); // react at once when something is selected
		tableFind.setColumnReorderingAllowed(true);
		tableFind.setColumnCollapsingAllowed(true);		

		// adding components to mainLayout (distance: 30px)
		mainLayout.addComponent(lblJobNo, "top: 20px; left: 20px;");
		mainLayout.addComponent(txtJobNo, "top: 18px; left: 140px;");

		mainLayout.addComponent(lblJobDate, "top: 50px; left: 20px;");
		mainLayout.addComponent(dJobDate, "top: 48px; left: 140px;");

		mainLayout.addComponent(tableFG,"top: 100px; left: 20px");

		mainLayout.addComponent(searchPanel,"top:5px;left:690px;");

		mainLayout.addComponent(lblFromDate,"top:10px;left:720px;");
		mainLayout.addComponent(dFromDate,"top:8px;left:810px;");

		mainLayout.addComponent(lblToDate,"top:40px;left:720px;");
		mainLayout.addComponent(dToDate,"top:38px;left:810px;");

		mainLayout.addComponent(findButton, "top:70px;left:820.0px;");

		mainLayout.addComponent(tableFind, "top:100px;left:695.0px;");

		lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:300.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:330.0px; left:140.0px;");

		return mainLayout;
	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(dJobDate);

		for(int i = 0; i < cmbRawItem.size(); i++)
		{
			focusComp.add(cmbRawItem.get(i));
			focusComp.add(txtReceivedQty.get(i));
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

	private void searchPanelIni(boolean b)
	{
		searchPanel.setEnabled(!b);
		lblFromDate.setEnabled(!b);
		dFromDate.setEnabled(!b);
		lblToDate.setEnabled(!b);
		dToDate.setEnabled(!b);
		tableFind.setEnabled(!b);
		findButton.setEnabled(!b);
	}

	private void componentIni(boolean b)
	{
		lblJobNo.setEnabled(!b);
		txtJobNo.setEnabled(!b);

		lblJobDate.setEnabled(!b);
		dJobDate.setEnabled(!b);

		tableFG.setEnabled(!b);
	}

	private void txtClear()
	{
		txtJobNo.setValue("");
		dJobDate.setValue(new java.util.Date());
	}

	private void tableClear()
	{
		for(int i = 0; i < cmbRawItem.size(); i++)
		{
			cmbRawItem.get(i).removeAllItems();
			txtItemUnit.get(i).setValue("");
			txtStockQty.get(i).setValue("");
			txtReceivedQty.get(i).setValue("");

		}
	}

	private void tableFindClear()
	{
		for(int i = 0; i < lblFindJobNo.size(); i++)
		{
			lblFindJobNo.get(i).setValue("");
			lblFindJobDate.get(i).setValue("");

		}
	}

	private void eventAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				txtJobNo.setValue(autoJobNo());
				dJobDate.focus();
				tableFG.removeAllItems();
				tableInitialise();
				for(int i=0;i<lblSl.size();i++)
				{
					System.out.println("I am Not Fine");
					tableDataAdding(i);	
				}
			}
		});
		cButton.btnEdit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				editButtonEvent();
				dJobDate.focus();

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
				saveButtonEvent();							
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				searchPanelIni(false);

				//				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		tableFind.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(!event.isDoubleClick())
				{
					try
					{
						tableFG.removeAllItems();
						tableInitialise();
						for(int i=0;i<lblSl.size();i++)
						{
							System.out.println("I am Not Fine");
							tableDataAdding(i);	
						}
						findInitialise(lblFindJobNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());	
					}

					catch(Exception ex)
					{
						showNotification("Error is Here"+ex);	
					}

				}
			}
		});

		findButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				tableFindDataLoad();
			}
		});

	}

	private void tableFindDataLoad(){
		tableFind.removeAllItems();
		Transaction tx=null;
		String query=null;
		try{
			query=	" select jobNo, jobDate from tbTubeWastageInfo" +
					" where CONVERT(date, jobDate, 105) between '"+dFormatSql.format(dFromDate.getValue())+"' and '"+dFormatSql.format(dToDate.getValue())+"'" +
					" order by convert(int, jobNo)";
			System.out.println("tableDataLoad: "+query);
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(query).list();
			int a=list.size();
			Iterator iter=list.iterator();
			if(list.size()>0)
			{
				int i = 0;
				while(a>0){
					if(iter.hasNext()){
						Object element[]=(Object[]) iter.next();
						String issNo=element[0].toString();
						String issDate=dF.format(element[1]);
						tableRowAdd(i,issNo,issDate);
					}
					a--;
					i++;
				}
			}
			else
			{
				this.getParent().showNotification("There is no Data!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("TableDataLoad: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableRowAdd(int ar,String issNo,String issDate){

		lblFindJobNo.add(ar,new Label());
		lblFindJobNo.get(ar).setWidth("100%");
		lblFindJobNo.get(ar).setImmediate(true);
		lblFindJobNo.get(ar).setValue(issNo);

		lblFindJobDate.add(ar,new Label());
		lblFindJobDate.get(ar).setWidth("100%");
		lblFindJobDate.get(ar).setImmediate(true);
		lblFindJobDate.get(ar).setValue(issDate);

		tableFind.addItem(new Object[]{lblFindJobNo.get(ar),lblFindJobDate.get(ar)},ar);
	}

	private void editButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		isUpdate = true;
	}

	private void tableRowAddRM(final int ar)
	{

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("-1");;
		lblSl.get(ar).setHeight("-1");
		lblSl.get(ar).setValue(ar+1);

		cmbRawItem.add(ar, new ComboBox());
		cmbRawItem.get(ar).setImmediate(true);
		cmbRawItem.get(ar).setWidth("100%");
		cmbRawItem.get(ar).setHeight("-1");
		cmbRawItem.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbRawItem.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbRawItem.get(ar).getValue()!=null)
				{
					if(doubleCheck(ar))
					{
						System.out.println("Done is Done");

						Transaction tx=null;
						Session session=SessionFactoryUtil.getInstance().getCurrentSession();
						String sql=null;
						try
						{
							//							Double mul1= Double.parseDouble(txtSectionStock.getValue().toString().isEmpty()?"0":txtSectionStock.getValue().toString());
							//							Double mul2=Double.parseDouble(txtIssueQty.getValue().toString().isEmpty()?"0":txtIssueQty.getValue().toString()) ;
							/*sql=" select  0,mTubePer*'"+mul1+"' as targetqty,mTubePer*'"+mul2+"' as targetIssueqty   from tbStandardFinishedInfo a "
									+" inner join tbStandardFinishedDetails b on a.vProductId=b.vProductId "
									+" where a.vProductId like '"+cmbRawItem.get(ar).getValue()+"' and b.vRawItemCode like '"+cmbRawItemName.getValue().toString()+"' ";*/

							sql = "select vUnitName, 0 from tbRawItemInfo where vRawItemCode like '"+cmbRawItem.get(ar).getValue()+"'";
							System.out.println("Our Desire sql is"+sql);
							tx=session.beginTransaction();
							List lst=session.createSQLQuery(sql).list();
							Iterator iter= lst.iterator();

							if(iter.hasNext())
							{
								Object[] element=(Object[]) iter.next();
								txtItemUnit.get(ar).setValue(element[0].toString());
							}
							
							String sql2=null;
							sql2 = "select * from dbo.funcWastageStock('"+cmbRawItem.get(ar).getValue()+"','"+dateF.format(dJobDate.getValue())+"' )";
							System.out.println("Our Desire sql is"+sql2);
							
							List lst2=session.createSQLQuery(sql2).list();
							Iterator iter2= lst2.iterator();

							if(iter2.hasNext())
							{
								Object[] element=(Object[]) iter2.next();
								txtStockQty.get(ar).setValue(decFormat.format(element[7]));
							}

						}

						catch(Exception ex)
						{
							showNotification("Error Is Here"+ex,Notification.TYPE_WARNING_MESSAGE);	
						}

						if(ar==lblSl.size()-1)
						{
							int a=txtItemUnit.size();
							tableRowAddRM(txtItemUnit.size());
							tableDataAdding(a);

							System.out.println("Try To Get The Best Result");
						}
					}
					else{
						showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
						cmbRawItem.get(ar).setValue(null);
						cmbRawItem.get(ar).focus();
						System.out.println("Not Ok");
					}

				}

			}

		});

		txtItemUnit.add(ar, new TextRead());
		txtItemUnit.get(ar).setImmediate(true);
		txtItemUnit.get(ar).setWidth("100%");

		txtStockQty.add(ar, new TextRead(1));
		txtStockQty.get(ar).setImmediate(true);
		txtStockQty.get(ar).setWidth("100%");

		txtReceivedQty.add(ar, new AmountField());
		txtReceivedQty.get(ar).setImmediate(true);
		txtReceivedQty.get(ar).setWidth("100%");

		tableFG.addItem(new Object[]{lblSl.get(ar),cmbRawItem.get(ar),txtItemUnit.get(ar),txtStockQty.get(ar),txtReceivedQty.get(ar)},ar);

	}

	public boolean doubleCheck(int ar){
		String value=cmbRawItem.get(ar).getValue().toString();
		for(int x=0;x<cmbRawItem.size();x++){
			if(cmbRawItem.get(x).getValue()!=null){
				if(x!=ar&&value.equalsIgnoreCase(cmbRawItem.get(x).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}

	private void findButtonEvent() 
	{
		Window win = new LabelIssueFindWindow(sessionBean, findJobNo);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				txtClear();
				isFind = true;
				findInitialise(findJobNo.getValue().toString());

			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String findJobNo) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql1 = "select jobNo, jobDate from tbWastageInfo where jobNo='"+findJobNo+ "'";
			System.out.println(sql1);
			List list=session.createSQLQuery(sql1).list();
			for(Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object element[] = (Object[])iter.next();
				txtJobNo.setValue(element[0].toString());
				dJobDate.setValue(element[1]);
				System.out.println("Hello world");

			}

			String sql2 = "";
			sql2 = "select itemCode, unit, stockQty, receivedQty  from tbTubeWastageDetails where jobNo='"+findJobNo+ "'";
			System.out.println(sql2);
			List list1=session.createSQLQuery(sql2).list();

			int i = 0;

			for (Iterator iter2 = list1.iterator(); iter2.hasNext();)
			{
				Object element[] = (Object[])iter2.next();

				cmbRawItem.get(i).setValue(element[0].toString());
				txtReceivedQty.get(i).setValue(decFormat.format(element[3]).toString());
				i++;
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
		tableFindClear();
		searchPanelIni(true);

		//		dProductionDate.focus();
		isFind = false;
	}

	private void refreshButtonEvent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		tableClear();
		tableFindClear();
		isFind = false;
		isUpdate = false;
		searchPanelIni(true);
	}

	private void tableDataAdding(int i)
	{
		//		tableclear();
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			System.out.println("Index Is"+i);

			String query ="select vRawItemCode, vRawItemName, vUnitName from tbRawItemInfo where vGroupId in ('G174','G175','G178')"; 
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			//cmbRawItem.get(i).removeAllItems();
			//txtStockQty.get(i).setValue("");
			//txtReceivedQty.get(i).setValue("");
			for (Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbRawItem.get(i).addItem(element[0].toString());
				cmbRawItem.get(i).setItemCaption(element[0].toString(), element[1].toString());

				System.out.println("Product Name: "+element[1].toString());

			}

		}
		catch (Exception ex) {
			//this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}	

	private void cmbToData() {
		cmbTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like 'Level Production' and a.StepName like 'Printing' ";

			}
			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName not like 'Printing' and StepId like '%Level%' ";

			}


			System.out.println("To: "+query);


			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbTo.addItem(element[0]);
				cmbTo.setItemCaption(element[0], (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void cmbRawItemNameData() 
	{
		cmbRawItemName.removeAllItems();

		Transaction tx=null;
		String query=null;

		try
		{

			query= " select distinct vRawItemCode, vRawItemName from tbStandardFinishedDetails order by vRawItemName ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				cmbRawItemName.addItem(element[0].toString());
				cmbRawItemName.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}

		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void tableInitialise()
	{
		for(int i = 0; i < 4; i++)
		{
			tableRowAddRM(i);
		}
	}

	private void saveButtonEvent() 
	{

		if(itemcheck())
		{
			if (isUpdate) 
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if (buttonType == ButtonType.YES) 
						{
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

							//							isUpdate = false;
							componentIni(true);
							btnIni(true);
							tableClear();
							tableFindClear();
							txtClear();
							searchPanelIni(true);
							isFind = false;
							isUpdate = false;
						}
					}
				});
			} 
			else
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if(buttonType == ButtonType.YES)
						{
							insertData();
							componentIni(true);
							btnIni(true);
							tableClear();
							tableFindClear();
							txtClear();
							searchPanelIni(true);
							isFind = false;
							isUpdate = false;
						}
					}
				});
			}	 
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Product Name.",Notification.TYPE_WARNING_MESSAGE);

		}
	}

	private void insertData()
	{

		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String itemType = "";
		String jobNo = "";
		String name="";
		String query1="";
		Double totalIssue=0.00;
		Double ttlIssue=0.00;

		try
		{
			if(!isUpdate)
			{
				jobNo = autoJobNo();
			}
			else
			{
				jobNo = txtJobNo.getValue().toString();
			}
			/*totalIssue= Double.parseDouble(txtIssueQty.getValue().toString());
			if(isFind)
			{
				query1=	 "select 0, isnull (SUM(a.issueQty),0) from tbLabelIssueInfo a "
				         +"inner join "
				         +"tbLabelIssueDetails b "
				         +"on "
				         +"a.jobNo=b.jobNo where a.jobNo like '"+txtJobNo+"' ";	
				List lst=session.createSQLQuery(query1).list();
				Iterator iter=lst.iterator();
				if(iter.hasNext())
				{
					Object[] element=(Object[]) iter.next();
					ttlIssue=(Double) element[1];
				}

				totalIssue=totalIssue-ttlIssue;
			}*/

			String sql =" insert into tbTubeWastageInfo" +
					" (jobNo, jobDate, userId, userIp, entryTime)" +
					" values" +
					" ('"+jobNo+"'," +
					" '"+dFormatSql.format(dJobDate.getValue())+" "+getTime()+"'," +
					" '"+sessionBean.getUserId()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP)" ;
			System.out.println(sql);
			
			String tbUdTubeWastageInfo =" insert into tbUdTubeWastageInfo" +
					" (jobNo, jobDate, userId, userIp, entryTime,vUdFlag)" +
					" values" +
					" ('"+jobNo+"'," +
					" '"+dFormatSql.format(dJobDate.getValue())+" "+getTime()+"'," +
					" '"+sessionBean.getUserId()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP,'New')" ;
			System.out.println(tbUdTubeWastageInfo);
			
			for (int i = 0; i < cmbRawItem.size(); i++)
			{
				System.out.println("for");
				if (cmbRawItem.get(i).getValue()!=null)
				{
					System.out.println("if");

					String query = 	"insert into tbTubeWastageDetails" +
							" (jobNo, itemCode, unit, stockQty, receivedQty)" +
							" values(" +
							" '"+jobNo+"'," +
							" '"+cmbRawItem.get(i).getValue()+"'," +
							" '"+txtItemUnit.get(i).getValue().toString()+"', " +
							"  '"+txtStockQty.get(i).getValue().toString()+"',"+
							"  '"+txtReceivedQty.get(i).getValue().toString()+"')";
					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();
					
					String tbTubeWastageDetails = 	"insert into tbUdTubeWastageDetails" +
							" (jobNo, itemCode, unit, stockQty, receivedQty,vUdFlag)" +
							" values(" +
							" '"+jobNo+"'," +
							" '"+cmbRawItem.get(i).getValue()+"'," +
							" '"+txtItemUnit.get(i).getValue().toString()+"', " +
							"  '"+txtStockQty.get(i).getValue().toString()+"',"+
							"  '"+txtReceivedQty.get(i).getValue().toString()+"','New')";
					System.out.println(tbTubeWastageDetails);
					session.createSQLQuery(tbTubeWastageDetails).executeUpdate();
					if(i==0){
						session.createSQLQuery(sql).executeUpdate();
						session.createSQLQuery(tbUdTubeWastageInfo).executeUpdate();
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

		for (int i = 0; i < cmbRawItem.size(); i++) 
		{
			Object temp = cmbRawItem.get(i).getItemCaption(cmbRawItem.get(i).getValue());

			System.out.println(cmbRawItem.get(i).getValue());

			if (temp != null && !cmbRawItem.get(i).getValue().equals(("x#" + i))) 
			{
				if (cmbRawItem.get(i).getValue()!=null) 
				{
					return true;
				} 
				else
				{
					this.getParent().showNotification("Warning :","Please Enter Valid Qty .",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}


		return false;
	}

	private boolean itemcheck() 
	{
		for(int i=0;i<cmbRawItem.size();i++)
		{
			if(cmbRawItem.get(i).getValue()!=null)
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
			String tbTubeWastageInfo="insert into tbUdTubeWastageInfo select jobNo, jobDate, userId, "
			+ "userIp, entryTime,'Update' vUdFlag from tbTubeWastageInfo where jobNo='"+txtJobNo.getValue()+ "'";
			
			String tbTubeWastageDetails="insert into tbUdTubeWastageDetails "
					+ "select jobNo, itemCode, unit, stockQty, receivedQty,'Update' vUdFlag "
					+ "from tbTubeWastageDetails where jobNo='"+txtJobNo.getValue()+"'";
			
			System.out.println(tbTubeWastageInfo);
			System.out.println(tbTubeWastageDetails);
			
			
			session.createSQLQuery(tbTubeWastageInfo).executeUpdate();
			session.createSQLQuery(tbTubeWastageDetails).executeUpdate();
			
			session.createSQLQuery("delete tbTubeWastageInfo where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbTubeWastageInfo where jobNo='"+txtJobNo.getValue()+ "' ");
			session.createSQLQuery("delete tbTubeWastageDetails where jobNo='"+txtJobNo.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbTubeWastageDetails where jobNo='"+txtJobNo.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private String autoJobNo()
	{
		String autoNo=null;
		Transaction tx;

		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select cast(isnull(max(cast(replace(jobNo, '', '')as int))+1, 1)as varchar) from tbTubeWastageInfo").list().iterator();

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
