package com.example.sparePartsTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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

public class IssueReturnFindSP extends Window{

	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	private PopupDateField fromDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private Label lblFrom=new Label("Form Date:");
	private Label lblTo=new Label("To Date:");
	private TextField txtd= new TextField();
	private NativeButton btnFind=new NativeButton("Find");
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	private SessionBean sessionBean;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");

	private String px="150px";
	private String vocherType;
	private String receiptId="";

	private ArrayList<Label> lblAutoID = new ArrayList<Label>();

	private ArrayList<Label> lbIssueNo = new ArrayList<Label>();
	private ArrayList<Label> lbIssueTo = new ArrayList<Label>();

	//private TextField txtReceiptId= new TextField();

	private TextField Asaud=new TextField();


	private DecimalFormat df = new DecimalFormat("#0.00");


	private String frmName;

	public IssueReturnFindSP(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{


		this.sessionBean=sessionBean;
		this.txtd=txtReceiptId;
		this.setCaption("Find Window");
		this.center();
		this.setWidth("550px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();	
		setEventAction();

	}
	public void tableInitialise()
	{

		for(int i=0;i<50;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblAutoID.add(ar,new Label());
		lblAutoID.get(ar).setWidth("100%");
		table.addItem(new Object[]{lblAutoID.get(ar)},ar);
	}
	public void setEventAction(){

		btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}


		});

		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick())
				{
					receiptId=lblAutoID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("value is"+receiptId);
					txtd.setValue(receiptId);
					System.out.print("our Desire value is"+Asaud.getValue().toString());
					windowClose();

				}
			}
		});

	}

	private void windowClose()
	{

		this.close();
	}

	private void findButtonEvent()
	{

		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = null;

				String query= "select  transactionNo,issueNo,challanNo,CONVERT(date,transactionDate,105) as Date,returnForm,b.SectionName  from tbIssueReturnInfo a "
						      +"inner join "
						      +"tbSectionInfo b on a.returnForm=b.AutoID where CONVERT(date,transactionDate,105) between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'  ";
				
				

				System.out.println(query);

				lst= session.createSQLQuery(query).list();


			int i=1;
			if(!lst.isEmpty())
				for (Iterator iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					

						if(i==lblAutoID.size()){

							tableRowAdd(i);
						}
						table.addItem(new Object[] {element[3] ,element[0],element[1],element[2],element[5]}, new Integer(i));
						lblAutoID.get(i).setValue(element[0].toString().trim());
					

					i++;
				}
			else
				getParent().showNotification("Warning: ","There are no Data.");



		}
		catch(Exception exp){
			System.out.println(exp);
		}
		/*}
	else{

		getParent().showNotification("Warning: ","Please Select valid Date.");
	}*/


	}
	private void compInit(){


		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);  
		fromDate.setWidth(px);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		toDate.setWidth(px);


		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		

			table.addContainerProperty("Date", Label.class, null);
			table.addContainerProperty("Return No", Label.class, new Label());
			table.addContainerProperty("Issue No",Label.class, new Label());
			table.addContainerProperty("Challan No", Label.class, null);
			table.addContainerProperty("Return From", Label.class, null);
			             
		

	}
	private void compAdd(){

		hLayout.setSpacing(true);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(toDate);
		hLayout.addComponent(btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}


}
