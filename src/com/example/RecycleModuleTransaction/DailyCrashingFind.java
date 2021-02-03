package com.example.RecycleModuleTransaction;

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
import com.common.share.TextRead;
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

public class DailyCrashingFind extends Window
{
	private AbsoluteLayout mainLayout;
	private TextRead txtTransectionId;
	private Table table=new Table();
	public String transectionId = "";
	

	private  Label lblfromDate= new Label();
	private PopupDateField dFdate= new PopupDateField();
	
	private Label lblToDate= new Label();
	private PopupDateField dTdate= new PopupDateField();
	
	TextField txtReceipt=new TextField();
	private ArrayList<Label> tblblReceiptNo = new ArrayList<Label>();
	private ArrayList<Label> tblblReceiptDate = new ArrayList<Label>();
	private ArrayList<Label> tblblRejectedItem = new ArrayList<Label>();
	private ArrayList<Label> lblRecycledItem= new ArrayList<Label>();
	private ArrayList<Label> lblunit = new ArrayList<Label>();
	private ArrayList<Label> lblproductionQty = new ArrayList<Label>();

	private NativeButton findButton = new NativeButton("Find");
	

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private SimpleDateFormat DateF=new SimpleDateFormat("yyyy-MM-dd");
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dformate = new DecimalFormat("#0");
	
	private Label lblCountProduct=new Label();
	
	public DailyCrashingFind(SessionBean sessionBean,TextRead txtTransectionId)
	{
		this.txtTransectionId = txtTransectionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND DAILY CRASHING :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("800px");
		this.setHeight("600px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		setContent(buildMainLayout());
		setEventAction();
		
	}
	
	public void setEventAction()
	{
		

		
		
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
				
					tableclear();
					tableDataAdding();
				
				
			}
		});
		
		
	}

	private void tableclear()
	{
		for(int i=0; i<tblblReceiptNo.size(); i++)
		{
			tblblReceiptNo.get(i).setValue("");
			tblblReceiptDate.get(i).setValue("");
			lblRecycledItem.get(i).setValue("");
			lblRecycledItem.get(i).setValue("");
			lblunit.get(i).setValue("");
			lblproductionQty.get(i).setValue("");
		
		}
	}
	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		try
		{
			
			
			String partyId="";
			
			
			
			query=  "select a.iTransactionNo,CONVERT(varchar(120),a.dTransactionDate,110)trnsactionDate, "
					+"b.RejectedItemName,b.ReceycledItemName,b.vUnit, "
					+"b.mProducedQtyKg from tbDailyCrashingInfo a inner join tbDailyCrashingDetails b "
					+"on a.iTransactionNo=b.iTransactionNo where CONVERT(date,a.dTransactionDate,105) "
					+"between  '"+DateF.format(dFdate.getValue())+"' and '"+DateF.format(dTdate.getValue())+"' ";
			
			
			System.out.println("find query is:"+query);
			tableclear();
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblReceiptNo.get(i).setValue(element[0].toString());
					tblblReceiptDate.get(i).setValue(element[1]);
					tblblRejectedItem.get(i).setValue(element[2]);
					lblRecycledItem.get(i).setValue(element[3].toString());
					lblunit.get(i).setValue(element[4].toString());
					lblproductionQty.get(i).setValue(df.format(Double.parseDouble(element[5].toString())) );
					
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
		catch (Exception ex) 
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		
		finally
		{
			session.close();
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	
	private AbsoluteLayout buildMainLayout()
	{		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		
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

	
		table.addContainerProperty("Receipt No", Label.class, new Label());
		table.setColumnWidth("Receipt No",100);
		table.setColumnAlignment("Receipt No", table.ALIGN_CENTER);
	
		table.addContainerProperty("Receipt Date", Label.class, new Label());
		table.setColumnWidth("Receipt Date",100);
		table.setColumnAlignment("Receipt Date", table.ALIGN_CENTER);
		
		table.addContainerProperty("Rejected Product", Label.class, new Label());
		table.setColumnWidth("Rejected Product",100);
		table.setColumnAlignment("Rejected Product", table.ALIGN_CENTER);

		table.addContainerProperty("Recycled Product", Label.class, new Label());
		table.setColumnWidth("Recycled Product",100);
		table.setColumnAlignment("Recycled Product", table.ALIGN_CENTER);
		
		table.addContainerProperty("unit", Label.class, new Label());
		table.setColumnWidth("unit",100);
		table.setColumnAlignment("unit", table.ALIGN_CENTER);
		
		table.addContainerProperty("Production Qty", Label.class, new Label());
		table.setColumnWidth("Production Qty",100);
		table.setColumnAlignment("Production Qty", table.ALIGN_CENTER);

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
		
		tblblRejectedItem.add(ar, new Label(""));
		tblblRejectedItem.get(ar).setWidth("100%");
		tblblRejectedItem.get(ar).setImmediate(true);
		tblblRejectedItem.get(ar).setHeight("15px");

		lblRecycledItem.add(ar, new Label(""));
		lblRecycledItem.get(ar).setWidth("100%");
		lblRecycledItem.get(ar).setImmediate(true);
		lblRecycledItem.get(ar).setHeight("15px");

		lblunit .add(ar, new Label(""));
		lblunit.get(ar).setWidth("100%");
		lblunit.get(ar).setImmediate(true);
		lblunit.get(ar).setHeight("15px");
		
		lblproductionQty .add(ar, new Label(""));
		lblproductionQty.get(ar).setWidth("100%");
		lblproductionQty.get(ar).setImmediate(true);
		lblproductionQty.get(ar).setHeight("15px");
		
	
		
		table.addItem(new Object[]{ tblblReceiptNo.get(ar),tblblReceiptDate.get(ar),tblblRejectedItem.get(ar),
				lblRecycledItem.get(ar),lblunit.get(ar),lblproductionQty.get(ar)},ar);
		
		
	}
	
}