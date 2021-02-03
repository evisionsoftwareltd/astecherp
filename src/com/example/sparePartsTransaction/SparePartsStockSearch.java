package com.example.sparePartsTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.crosstabs.fill.calculation.HeaderCell;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.loader.custom.Return;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class SparePartsStockSearch extends Window
{
	private SessionBean sessionBean;

	private HorizontalLayout hLayout = new HorizontalLayout();
	
	AbsoluteLayout mainLayout=new AbsoluteLayout();
	private boolean tick = false,isFind=false;
	//private Label lblType = new Label("Type:");
	//private static final String[] stType = new String[] {"PO","JO"};
	private ComboBox cmbType = new ComboBox();
	private ComboBox cmbParentType,cmbCategoryName,cmbSubCategoryName,cmbProductName;
	private CheckBox chkAllCategoryName,chkAllSubCategoryName,chkAllProductName;
	
	private Label lbl = new Label("Form:");
	
	private PopupDateField dFromDate = new PopupDateField();
	private PopupDateField dToDate = new PopupDateField();
	private Label lblFrom = new Label("Form Date:");
	private Label lblTo = new Label("To Date:");
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormate = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dcformate=new SimpleDateFormat("#0.00");
	private CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");
	//private CommonButton button = new CommonButton("", "Save", "", "", "Refresh", "", "", "", "", "Exit");
	private Table table = new Table();
	private Label lblParentType = new Label("Parent Type: ");
	private Label lblCategoryName = new Label("Category Name: ");
	private Label lblSubCategoryName = new Label("Sub Category Name: ");
	private Label lblProductName = new Label("Product Name: ");

	
	private ComboBox cmbPartyName;

	private ArrayList<Label> tbsl = new ArrayList<Label>();
	//private ArrayList<CheckBox> tbselect = new ArrayList<CheckBox>();
	private ArrayList<Label> tbItemName = new ArrayList<Label>();
	private ArrayList<Label> tbItemCode = new ArrayList<Label>();
	private ArrayList<Label> tbOpeningQty = new ArrayList<Label>();
	private ArrayList<Label> tbTotalReceiveQty = new ArrayList<Label>();
	private ArrayList<Label> tbTotalIssueQty = new ArrayList<Label>();
	private ArrayList<Label> tbClosingQty = new ArrayList<Label>();
	private ArrayList<Label> tbClosingRate = new ArrayList<Label>();
	private ArrayList<Label> tbClosingamount = new ArrayList<Label>();
	private ArrayList<NativeButton> tbBtnReport = new ArrayList<NativeButton>();
	private TextField txtCancelId=new TextField();
	private ArrayList<Component> allComp = new ArrayList<Component>();
	//private ReportDate reportTime = new ReportDate();
	private boolean isUpdate = false;
	private int reasonIndex = 0;

	public SparePartsStockSearch(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("STOCK SEARCH :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("1050px");
		this.setHeight("800px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setResizable(false);
		this.setStyleName("cwindow");
		this.setContent(buildMainLayout());
		//btnIni(true);
		tableInitialise();
		cmbParenTypeDataLoad();
		cmbParentType.setValue("Spare Parts");
		cmbParentType.setEnabled(false);
		//partyNameData();
		setEventAction();
		//authenticationCheck();
		cmbCategoryLoad();
		focusEnter();
	}
	
	/*private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}*/

	public void setEventAction()
	{
		/*dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
					tableClear();
			}
		});

		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
					tableClear();
			}
		});*/
	/*	cmbParentType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbParentType.getValue()!=null)
				{
					tableClear();
					cmbCategoryLoad();
				}
				else{
					cmbCategoryName.setValue(null);
					cmbCategoryName.removeAllItems();
					tableClear();
				}
			}
		});*/
		
		cmbCategoryName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbCategoryName.getValue()!=null)
				{
					tableClear();
					subCateGoryDataAdd(cmbCategoryName.getValue().toString());
				}
				else{
					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.removeAllItems();
					tableClear();
				}
			}
		});
		chkAllCategoryName.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkAllCategoryName.booleanValue()){
					cmbCategoryName.setValue(null);
					cmbCategoryName.setEnabled(false);
					subCateGoryDataAdd("%");
				}
				else{
					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.removeAllItems();
					cmbCategoryName.setValue(null);
					cmbCategoryName.setEnabled(true);
				}
			}
		});
		
	
		
		cmbSubCategoryName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSubCategoryName.getValue()!=null)
				{
					tableClear();
					if(chkAllCategoryName.booleanValue()){
						productNameLoad("%");
					}
					else{
						productNameLoad(cmbCategoryName.getValue().toString());
					}
				}
				else{
					cmbProductName.setValue(null);
					cmbProductName.removeAllItems();
					tableClear();
				}
			}
		});
		chkAllSubCategoryName.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkAllSubCategoryName.booleanValue()){
					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.setEnabled(false);
					if(chkAllCategoryName.booleanValue()){
						productNameLoad("%");
					}
					else{
						productNameLoad(cmbCategoryName.getValue().toString());
					}
				}
				else{
					cmbProductName.removeAllItems();
					cmbSubCategoryName.setValue(null);
					cmbSubCategoryName.setEnabled(true);
				}
			}
		});

		chkAllProductName.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(chkAllProductName.booleanValue()){
					cmbProductName.setValue(null);
					cmbProductName.setEnabled(false);
				}
				else{
					cmbProductName.setValue(null);
					cmbProductName.setEnabled(true);
				}
			}
		});
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(checkValidation()){
					findButtonEvent();
				}
			}				
		});
	}

	private boolean checkValidation() {
		if(cmbParentType.getValue()!=null){
			if(cmbCategoryName.getValue()!=null||chkAllCategoryName.booleanValue()){
				if(cmbSubCategoryName.getValue()!=null||chkAllSubCategoryName.booleanValue()){
					if(cmbProductName.getValue()!=null||chkAllProductName.booleanValue()){
						return true;
					}
					else{
						showNotification("Warning!","Please Select Product Name.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Warning!","Please Select Sub Category Name.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Warning!","Please Select Category Name.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Warning!","Please Select Parent Type.",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	public void changeTableHeader()
	{
		if(cmbType.getValue().toString().equals("PO"))
		{
			table.setColumnHeader("PO No", "PO No");
			table.setColumnHeader("PO Date", "PO Date");
			table.setColumnHeader("PO Qty", "PO Qty");
		}
		else
		{
			table.setColumnHeader("PO No", "JO No");
			table.setColumnHeader("PO Date", "JO Date");
			table.setColumnHeader("PO Qty", "JO Qty");
		}
	}
	public void productNameLoad(String Id)
	{
		cmbProductName.removeAllItems();
		try {
			String sql="select vRawItemCode,vRawItemName from tbRawItemInfo where vGroupId like '"+Id+"' and vflag='New'  "
					+ "order by vRawItemName ";
			
			Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex){
			showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void subCateGoryDataAdd(String catId)
	{
		cmbSubCategoryName.removeAllItems();
		try {
			
			String sql="select  iSubCategoryID,vSubCategoryName from  tbRawItemSubCategory where Group_Id like '"+catId+"' and vCategoryType like '%Spare Parts%' and vflag='New'  ";
			Iterator<?>iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbSubCategoryName.addItem(element[0].toString());
				cmbSubCategoryName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex){
			showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void cmbCategoryLoad(){
		cmbCategoryName.removeAllItems();
		String sql="select  iCategoryCode,vCategoryName from  tbRawItemCategory where vCategoryType like '%Spare Parts%' and vflag='New'";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbCategoryName.addItem(element[0]);
			cmbCategoryName.setItemCaption(element[0], element[1].toString());
		}
	}
	
	private void cmbParenTypeDataLoad(){
		cmbParentType.removeAllItems();
		String sql="select distinct 0,vCategoryType  from tbRawItemCategory where vCategoryType like '%Spare Parts%' and vflag='New'";
		Iterator<?>iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbParentType.addItem(element[1]);
			cmbParentType.setItemCaption(element[1], element[1].toString());
		}
	}
	
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				session.close();
			}
		}
		return iter;
	}
	
	public void partyNameData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		try
		{
			cmbPartyName.removeAllItems();
			cmbPartyName.addItem("%");
			cmbPartyName.setItemCaption("%","All");
			String sql="";
			if(isFind==false){
				sql ="select distinct supplierId,(select supplierName from tbSupplierInfo where supplierId like tbRawPurchaseOrderInfo.supplierId) supplierName from tbRawPurchaseOrderInfo where vStatus like 'Active' order by supplierName";}
			else{
				sql ="select distinct vPartyId,vPartyName from tbPurchaseOrderCancelInventory order by vPartyName";}
			List<?> lst = session.createSQLQuery(sql).list();
			Iterator<?> iter = lst.iterator();
			if(queryValueCheck(sql)){
				while (iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();
					System.out.println("cmbLoad");
					cmbPartyName.addItem(element[0].toString());
					cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
		finally{
			if(session!=null){
				session.close();
			}
		}
	}
	private boolean queryValueCheck(String sql)
	{
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

/*	public boolean tableSelectCheck()
	{
		boolean ret = false;
		for(int i=0; i<tbpoNo.size(); i++)
		{
			if(!tbpoNo.get(i).getValue().toString().isEmpty())
			{
				if(tbselect.get(i).booleanValue())
				{
					ret = true;
				}
			}
		}
		return ret;
	}*/
/*	private boolean tableReasonCheck()
	{
		boolean ret = true;
		for(int i=0; i<tbpoNo.size(); i++)
		{
			if(!tbpoNo.get(i).getValue().toString().isEmpty())
			{
				if(tbselect.get(i).booleanValue())
				{
					if(tbReason.get(i).getValue().toString().isEmpty())
					{
						reasonIndex = i;
						ret = false;
						break;
					}
				}
			}
		}
		return ret;
	}*/

	private void findButtonEvent()
	{
		try
		{
			String CategoryName="%",SubCategoryName="%",ProductName="%";
			if(cmbCategoryName.getValue()!=null){
				CategoryName=cmbCategoryName.getValue().toString();
			}
			if(cmbSubCategoryName.getValue()!=null){
				SubCategoryName=cmbSubCategoryName.getValue().toString();
			}
			if(cmbProductName.getValue()!=null){
				ProductName=cmbProductName.getValue().toString();
			}
			String sql = "Select productName,openingQty,totalReceivedQty,totalIssueQty,closingQty,closingRate,closingAmount,productId from dbo.[funStockSearchEntry]('"+dateF.format(dFromDate.getValue())+"','"+dateF.format(dToDate.getValue())+"','"+cmbParentType.getValue().toString()+"','"+CategoryName+"','"+SubCategoryName+"','"+ProductName+"')";
			Iterator<?>iter=dbService(sql);
			if(iter.hasNext())
			{
				int i=0;
				Iterator<?>iter1=dbService(sql);
				while(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					if(i == tbItemName.size())
					{
						tableRowAdd(i);
					}
					tbItemName.get(i).setValue(element[0].toString());
					tbOpeningQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[1].toString())));
					tbTotalReceiveQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[2].toString())));
					tbTotalIssueQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
					tbClosingQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[4].toString())));
					tbClosingRate.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[5].toString())));
					tbClosingamount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[6].toString())));
					
					tbItemCode.get(i).setValue(element[7].toString());
					i++;
				}
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);					
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"Error!",Notification.TYPE_ERROR_MESSAGE);					
		}
	}
	public void tableInitialise()
	{
		for(int i=0; i<22; i++)
		{
			tableRowAdd(i);
		}
	}
	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(dFromDate);
		allComp.add(dToDate);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	public void tableRowAdd(final int ar)
	{
		tbsl.add(ar, new Label());
		tbsl.get(ar).setWidth("100%");
		tbsl.get(ar).setImmediate(true);
		tbsl.get(ar).setValue(ar+1);
		
		tbItemName.add(ar, new Label());
		tbItemName.get(ar).setWidth("100%");
		tbItemName.get(ar).setImmediate(true);
		
		
		tbItemCode.add(ar, new Label());
		tbItemCode.get(ar).setWidth("100%");
		tbItemCode.get(ar).setImmediate(true);

		tbOpeningQty.add(ar, new Label());
		tbOpeningQty.get(ar).setWidth("100%");
		tbOpeningQty.get(ar).setImmediate(true);

		tbTotalReceiveQty.add(ar, new Label());
		tbTotalReceiveQty.get(ar).setWidth("100%");
		tbTotalReceiveQty.get(ar).setImmediate(true);

		tbTotalIssueQty.add(ar, new Label());
		tbTotalIssueQty.get(ar).setWidth("100%");
		tbTotalIssueQty.get(ar).setImmediate(true);

		tbClosingQty.add(ar, new Label());
		tbClosingQty.get(ar).setWidth("100%");
		tbClosingQty.get(ar).setImmediate(true);
		
		tbClosingRate.add(ar, new Label());
		tbClosingRate.get(ar).setWidth("100%");
		tbClosingRate.get(ar).setImmediate(true);
		
		tbClosingamount.add(ar, new Label());
		tbClosingamount.get(ar).setWidth("100%");
		tbClosingamount.get(ar).setImmediate(true);

		tbBtnReport.add(ar, new NativeButton());
		tbBtnReport.get(ar).setWidth("100%");
		tbBtnReport.get(ar).setImmediate(true);
		tbBtnReport.get(ar).setIcon(new ThemeResource("../icons/preview.png"));
		tbBtnReport.get(ar).setDescription("Click to view details");
		tbBtnReport.get(ar).setStyleName(BaseTheme.BUTTON_LINK);
		tbBtnReport.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!tbItemName.get(ar).getValue().toString().equals(""))
				{
					detailsAction(ar);
				}
				else
				{
					showNotification("Warning!","No PO no found.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		table.addItem(new Object[]{tbsl.get(ar),tbItemName.get(ar),tbOpeningQty.get(ar),tbTotalReceiveQty.get(ar),tbTotalIssueQty.get(ar),
				tbClosingQty.get(ar),tbClosingRate.get(ar),tbClosingamount.get(ar),tbBtnReport.get(ar),tbItemCode.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout() {
		
		mainLayout=new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		compInit();
		compAdd();
		return mainLayout;
	}
	
	private void compInit()
	{
		cmbType.setImmediate(true);
		cmbType.setStyleName("horizontal");
		cmbType.setDescription("PO = Purchase Order, JO = Job Order");

		cmbParentType = new ComboBox();
		cmbParentType.setImmediate(true);
		cmbParentType.setWidth("250px");
		cmbParentType.setHeight("-1px");
		cmbParentType.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbParentType.setNewItemsAllowed(false);
		
		cmbCategoryName = new ComboBox();
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setWidth("250px");
		cmbCategoryName.setHeight("-1px");
		cmbCategoryName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbCategoryName.setNewItemsAllowed(false);
		
		cmbSubCategoryName = new ComboBox();
		cmbSubCategoryName.setImmediate(true);
		cmbSubCategoryName.setWidth("250px");
		cmbSubCategoryName.setHeight("-1px");
		cmbSubCategoryName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbSubCategoryName.setNewItemsAllowed(false);
		
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("250px");
		cmbProductName.setHeight("-1px");
		cmbProductName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbProductName.setNewItemsAllowed(false);
		
		chkAllCategoryName=new CheckBox("All");
		chkAllCategoryName.setImmediate(true);
		
		chkAllSubCategoryName=new CheckBox("All");
		chkAllSubCategoryName.setImmediate(true);
		
		chkAllProductName=new CheckBox("All");
		chkAllProductName.setImmediate(true);
		
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setWidth("110px");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);

		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setInvalidAllowed(false);
		dToDate.setImmediate(true);

		cButton.btnFind.setHeight("80px");
		cButton.btnFind.setWidth("80px");
		
		table.setWidth("100%");
		table.setHeight("80%");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, null);	
		table.setColumnWidth("SL#", 18);

		table.addContainerProperty("Item Name", Label.class, null);
		table.setColumnWidth("Item Name",200);

		table.addContainerProperty("Opening Qty", Label.class, null);
		table.setColumnWidth("Opening Qty",200);

		table.addContainerProperty("Total Receive Qty Received,LR,LIR,IR", Label.class, null);		
		table.setColumnWidth("Total Receive Qty Received,LR,LIR,IR", 110);

		table.addContainerProperty("Totol Issue Qty ISSUE,LI,LRR", Label.class, null);
		table.setColumnWidth("Totol Issue Qty ISSUE,LI,LRR", 110);

		table.addContainerProperty("Closing Qty", Label.class, null);
		table.setColumnWidth("Closing Qty",75);
		
		table.addContainerProperty("Closing Rate", Label.class, null);
		table.setColumnWidth("Closing Rate",75);
		
		table.addContainerProperty("Closing Amount", Label.class, null);
		table.setColumnWidth("Closing Amount",75);
	

		table.addContainerProperty("Report", NativeButton.class, null);
		table.setColumnWidth("Report", 40);
		
		table.addContainerProperty("Item Code", Label.class, null);
		table.setColumnWidth("Item Code",80);
		table.setColumnCollapsed("Item Code", true);
	
		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		
		table.setStyleName("wordwrap-headers");
	}

	private void compAdd()
	{
		cmbType.setWidth("80px");
		
		// Group headers by joining the cells
		//First Raw
		mainLayout.addComponent(lblParentType,"top:20px;left:20px;");
		mainLayout.addComponent(cmbParentType,"top:18px;left:140px;");
		
		mainLayout.addComponent(lblCategoryName,"top:50px;left:20px;");
		mainLayout.addComponent(cmbCategoryName,"top:48px;left:140px;");
		mainLayout.addComponent(chkAllCategoryName,"top:50px;left:390px;");
		
		mainLayout.addComponent(lblSubCategoryName,"top:80px;left:20px;");
		mainLayout.addComponent(cmbSubCategoryName,"top:78px;left:140px;");
		mainLayout.addComponent(chkAllSubCategoryName,"top:80px;left:390px;");
		//First Raw end
		mainLayout.addComponent(lblProductName,"top:20px;left:450px;");
		mainLayout.addComponent(cmbProductName,"top:18px;left:560px;");
		
		mainLayout.addComponent(chkAllProductName,"top:20px;left:815px;");
		
		mainLayout.addComponent(lblFrom,"top:50px;left:450px;");
		mainLayout.addComponent(dFromDate,"top:48px;left:560px;");
		
		mainLayout.addComponent(lblTo,"top:80px;left:450px;");
		mainLayout.addComponent(dToDate,"top:78px;left:560px;");

		mainLayout.addComponent(cButton.btnFind,"top:23px;left:870px;");
		
		mainLayout.addComponent(table,"bottom:10px;left:0px;");
		
		//mainLayout.addComponent(button,"top:10px;left:20px;");

	}

	private void formClear()
	{
		cmbType.setValue("Spare Parts");
		cmbPartyName.setValue(null);
		cButton.btnFind.setEnabled(true);
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbItemName.size(); i++)
		{
			tbItemName.get(i).setValue("");
			tbItemCode.get(i).setValue("");
			tbOpeningQty.get(i).setValue("");
			tbTotalReceiveQty.get(i).setValue(null);
			tbTotalIssueQty.get(i).setValue(null);
			tbClosingQty.get(i).setValue("");
			tbClosingRate.get(i).setValue("");
			tbClosingamount.get(i).setValue("");
		}
	}

	private void detailsAction(int i)
	{
		/*String query=null;
		String activeFlag = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", "Phone: "+sessionBean.getCompanyPhone()+"   Fax:  "+sessionBean.getCompanyFax()+",   E-mail:  "+sessionBean.getCompanyEmail());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo",sessionBean.getCompanyLogo());
			hm.put("user", sessionBean.getUserName());
			query="Select * from vwPurchaseOrder  where CONVERT (date,poDate) = '"+dateF.format(tbPoDate.get(i).getValue())+"' and supplierId='"+cmbPartyName.getValue()+"'";
			System.out.println(query);
			hm.put("sql", query);
			Window win = new ReportViewerNew(hm,"report/raw/rptRawPurchaseOrder.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
			win.setCaption("Report : Purchase Order");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp){
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}*/
		////////////////
		
		
		String query=null;
		/*if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(pdffromdate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionbean.getFiscalOpenDate()))  && 
				Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(pdftodate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionbean.getFiscalCloseDate())))*/
		{
			Transaction tx=null;

			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String query1="exec PrcRawStockReportDate '"+dateF.format(dFromDate.getValue())+"','"+dateF.format(dToDate.getValue())+"','"+tbItemCode.get(i).getValue()+"'";
				session.createSQLQuery(query1).executeUpdate();
				tx.commit();
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification(exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			try
			{
				HashMap hm = new HashMap();
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("PhoneNumber", "Phone: "+sessionBean.getCompanyAddress());
				hm.put("username", sessionBean.getUserName());
				hm.put("userIp",sessionBean.getUserIp());
				hm.put("Phone", sessionBean.getCompanyContact());

				if(tbItemName.get(i).getValue()!=null)
				{
					query="select * from dbo.[funRawMaterialStockFlownew]('"+tbItemCode.get(i).getValue()+"')";
					System.out.println(query);
					hm.put("sql", query);

					hm.put("productname", tbItemName.get(i).getValue().toString());

					hm.put("fromdate",dFormate.format(dFromDate.getValue()));
					hm.put("todate",dFormate.format(dToDate.getValue())) ;
					Session session=SessionFactoryUtil.getInstance().getCurrentSession();
					tx=session.beginTransaction();
					List lst=session.createSQLQuery(query).list();
					if(!lst.isEmpty())
					{
						Window win = new ReportViewerNew(hm,"report/raw/RptProductWiseStockRegister.jasper",
								this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
								this.getWindow().getApplication().getURL()+"VAADIN/applet",1);
						win.setCaption("Report : Goods Receive Note (GRN)");
						this.getParent().getWindow().addWindow(win);
					}
					else
					{
						this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
		
				{
					getParent().showNotification("Please Select Product Name!!!", Notification.TYPE_WARNING_MESSAGE);		
				}
			}
			catch(Exception exp){
				this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
		}
		/*else{

			this.getParent().showNotification("Warning","Please Select vaild Date.",Notification.TYPE_WARNING_MESSAGE);
		}*/
		
	}

}