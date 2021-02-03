package com.example.thirdpartyTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyRmRcvFind extends Window
{
	private AbsoluteLayout mainLayout;
	private TextField txtTransectionId;
	private Table table=new Table();
	public String transectionId = "";
	
	/*private Label lblDate;
	private PopupDateField dDate;*/
	
	private Label lblPartyId=new Label("Party Id : ");
	private ComboBox cmbPartyName=new ComboBox() ;
	private CheckBox chkAllPartyName=new CheckBox("ALL");
	
	private  Label lblfromDate= new Label();
	private PopupDateField dFdate= new PopupDateField();
	
	private Label lblToDate= new Label();
	private PopupDateField dTdate= new PopupDateField();
	
	TextField txtReceipt=new TextField();
	private ArrayList<Label> tblblReceiptNo = new ArrayList<Label>();
	private ArrayList<Label> tblblReceiptDate = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanNo = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanDate= new ArrayList<Label>();
	private ArrayList<Label> tblblProductName = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<Label> tblblQty= new ArrayList<Label>(); 
	private ArrayList<Label> tblblRate= new ArrayList<Label>(); 
	private ArrayList<Label> tblblAmount= new ArrayList<Label>(); 
	
	private ArrayList<Label> tblblPartyName= new ArrayList<Label>(); 
	
	
	private NativeButton findButton = new NativeButton("Find");
	

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private SimpleDateFormat DateF=new SimpleDateFormat("yyyy-MM-dd");
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dformate = new DecimalFormat("#0");
	
	private Label lblCountProduct=new Label();
	
	public ThirdPartyRmRcvFind(SessionBean sessionBean,TextField txtTransectionId)
	{
		this.txtTransectionId = txtTransectionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND THIRD PARTY ITEM RECEIPT :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("800px");
		this.setHeight("600px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		setContent(buildMainLayout());
		setEventAction();
		cmbPartyNameLoad();
	}
	
	public void setEventAction()
	{
		

		chkAllPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkAllPartyName.booleanValue()==true){
					cmbPartyName.setValue(null);			
					cmbPartyName.setEnabled(false);
					
				}
				else{
					cmbPartyName.setEnabled(true);
				}
			}
		});
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					transectionId = tblblReceiptNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtTransectionId.setValue(transectionId);
					windowClose();
				}
			}
		});
		
		findButton.addListener(new ClickListener() 
		{
			@Override
			public void buttonClick(ClickEvent event) 
			{
				if (cmbPartyName.getValue()!=null  || chkAllPartyName.booleanValue())
				{
					tableclear();
					tableDataAdding();
				}
				
			}
		});
		
		
	}

	private void tableclear()
	{
		for(int i=0; i<tblblReceiptNo.size(); i++)
		{
			tblblReceiptNo.get(i).setValue("");
			tblblReceiptDate.get(i).setValue("");
			tblblChallanNo.get(i).setValue("");
			tblblChallanDate.get(i).setValue("");
			tblblProductName.get(i).setValue("");
			tblblUnit.get(i).setValue("");
			tblblQty.get(i).setValue("");
			
		}
	}
	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			
			String partyId="";
			
			if(cmbPartyName.getValue()!=null)
			{
				partyId=cmbPartyName.getValue().toString();	
			}
			
			if(chkAllPartyName.booleanValue())
			{
				partyId="%";
			}
			
			query=  "select vReceiptNo,dReceiptDate,vChallanNo,dChallanDate,vPartyName from tbThirdPartyItemReceiptInfo  where vPartyId like  '"+partyId+"'  "
					+" and CONVERT(date,dReceiptDate,105) between '"+DateF.format(dFdate.getValue())+"' and '"+DateF.format(dTdate.getValue())+"'  ";
			System.out.println("find query is:"+query);
			
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblReceiptNo.get(i).setValue(element[0].toString());
					tblblReceiptDate.get(i).setValue(dFormat.format(element[1]));
					tblblChallanNo.get(i).setValue(element[2].toString());
					tblblChallanDate.get(i).setValue(dFormat.format(element[3]));
					tblblPartyName.get(i).setValue(element[4].toString());
					if(i==tblblReceiptNo.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	public void cmbPartyNameLoad()
	{
		cmbPartyName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select  distinct vPartyId,vPartyName from tbThirdPartyItemReceiptInfo").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private AbsoluteLayout buildMainLayout()
	{		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		
		lblPartyId = new Label("Party Name: ");
		lblPartyId.setImmediate(true);
		lblPartyId.setWidth("100.0%");
		lblPartyId.setHeight("18px");
		mainLayout.addComponent(lblPartyId,"top:30.0px;left:50.0px;");
		
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("318px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:28.0px;left:150.0px;");
		
		chkAllPartyName=new CheckBox("All");
		chkAllPartyName.setImmediate(true);
		chkAllPartyName.setValue(false);
		mainLayout.addComponent(chkAllPartyName, "top:28.0px;left:500.0px;");
		
		
		lblfromDate = new Label("From Date: ");
		lblfromDate.setImmediate(true);
		lblfromDate.setWidth("100.0%");
		lblfromDate.setHeight("18px");
		mainLayout.addComponent(lblfromDate,"top:60.0px;left:50.0px;");
		
		dFdate = new PopupDateField();
		dFdate.setImmediate(true); 
		dFdate.setWidth("110px");
		dFdate.setHeight("24px");
		dFdate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFdate.setValue(new java.util.Date());
		dFdate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dFdate, "top:58.0px;left:150.0px;");
		
		
		lblToDate = new Label("To Date: ");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("100.0%");
		lblToDate.setHeight("18px");
		mainLayout.addComponent(lblToDate,"top:90.0px;left:50.0px;");
		
		dTdate = new PopupDateField();
		dTdate.setImmediate(true); 
		dTdate.setWidth("110px");
		dTdate.setHeight("24px");
		dTdate.setResolution(PopupDateField.RESOLUTION_DAY);
		dTdate.setValue(new java.util.Date());
		dTdate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dTdate, "top:88.0px;left:150.0px;");
		
		findButton.setWidth("80px");
		findButton.setHeight("28px");
		findButton.setImmediate(true);
		findButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(findButton, "top:118.0px;left:150.0px;");
		
		
		table.setSelectable(true);
		table.setWidth("98%");
		table.setHeight("95%");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",300);
		table.setColumnAlignment("Party Name", table.ALIGN_CENTER);
		
		
		table.addContainerProperty("Receipt No", Label.class, new Label());
		table.setColumnWidth("Receipt No",100);
		table.setColumnAlignment("Receipt No", table.ALIGN_CENTER);
	
		table.addContainerProperty("Receipt Date", Label.class, new Label());
		table.setColumnWidth("Receipt Date",100);
		table.setColumnAlignment("Receipt Date", table.ALIGN_CENTER);
		
		table.addContainerProperty("Challan No", Label.class, new Label());
		table.setColumnWidth("Challan No",100);
		table.setColumnAlignment("Challan No", table.ALIGN_CENTER);

		table.addContainerProperty("Challan Date", Label.class, new Label());
		table.setColumnWidth("Challan Date",100);
		table.setColumnAlignment("Challan Date", table.ALIGN_CENTER);
		
	
		tableInitialise();
		mainLayout.addComponent(table, "top:148px; left:5px;");
		return mainLayout;
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<30;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tblblReceiptNo.add(ar, new Label(""));
		tblblReceiptNo.get(ar).setWidth("100%");
		tblblReceiptNo.get(ar).setImmediate(true);
		tblblReceiptNo.get(ar).setHeight("15px");
		
		tblblReceiptDate.add(ar, new Label(""));
		tblblReceiptDate.get(ar).setWidth("100%");
		tblblReceiptDate.get(ar).setImmediate(true);
		tblblReceiptDate.get(ar).setHeight("15px");
		
		tblblChallanNo.add(ar, new Label(""));
		tblblChallanNo.get(ar).setWidth("100%");
		tblblChallanNo.get(ar).setImmediate(true);
		tblblChallanNo.get(ar).setHeight("15px");

		tblblChallanDate.add(ar, new Label(""));
		tblblChallanDate.get(ar).setWidth("100%");
		tblblChallanDate.get(ar).setImmediate(true);
		tblblChallanDate.get(ar).setHeight("15px");

		tblblProductName .add(ar, new Label(""));
		tblblProductName.get(ar).setWidth("100%");
		tblblProductName.get(ar).setImmediate(true);
		tblblProductName.get(ar).setHeight("15px");
		
		tblblUnit .add(ar, new Label(""));
		tblblUnit.get(ar).setWidth("100%");
		tblblUnit.get(ar).setImmediate(true);
		tblblUnit.get(ar).setHeight("15px");
		
		tblblQty .add(ar, new Label(""));
		tblblQty.get(ar).setWidth("100%");
		tblblQty.get(ar).setImmediate(true);
		tblblQty.get(ar).setHeight("15px");

		tblblPartyName.add(ar, new Label(""));
		tblblPartyName.get(ar).setWidth("100%");
		tblblPartyName.get(ar).setImmediate(true);
		tblblPartyName.get(ar).setHeight("15px");
	
		
		table.addItem(new Object[]{ tblblPartyName.get(ar), tblblReceiptNo.get(ar),tblblReceiptDate.get(ar),tblblChallanNo.get(ar),
				tblblChallanDate.get(ar)},ar);
		
		
	}
	
}