package com.example.sparePartsTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class WastageEntryWriteOff extends Window {

	private static final String cbutton = null;

	SessionBean sessionBean;

	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");

	private FormLayout leftFrmLayout = new FormLayout();
	private FormLayout rightFrmLayout = new FormLayout();
	private HorizontalLayout hriLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout vLayout = new VerticalLayout();
	private HorizontalLayout spaceLayout = new HorizontalLayout();
	private HorizontalLayout lowerHorizontalLayout = new HorizontalLayout();
	ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextRead invoiceNo = new TextRead("Wastage Id :");

	private HashMap hRate = new HashMap();
	private HashMap hStock = new HashMap();
	private HashMap hItemCode = new HashMap();
	private HashMap hUnit = new HashMap();
	Double Total,Amount,wRate;

	private TextField txtInvoiceId=new TextField();

	double totalsum = 0.0;

	private PopupDateField dateField = new PopupDateField("Date:");

	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total : ");
	private TextField amountWordsField = new TextField();

	private TextRead totalField = new TextRead();

	private Table table = new Table();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> txtunit = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtStockQty = new ArrayList<TextRead>();
	private ArrayList<AmountField> txtqty = new ArrayList<AmountField>();
	private ArrayList<TextField>  txtremarks = new ArrayList<TextField>();
	private ArrayList<TextRead> VariableTotal = new ArrayList<TextRead>();
	private ArrayList<TextRead> txtTempQty = new ArrayList<TextRead>();

	private Label label = new Label();
	private Label l1 = new Label();

	private Label lbLine=new Label("___________________________________________________________________________________________________________");

	boolean isUpdate=false;
	boolean isFind=false;
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DecimalFormat df = new DecimalFormat("#0.00");

	public WastageEntryWriteOff(SessionBean sessionBean)
	{

		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setWidth("800px");
		this.setCaption("WASTAGE ENTRY (RAW) :: "+sessionBean.getCompany());

		cButton.btnNew.focus();

		invoiceNo.setWidth("100px");
		leftFrmLayout.addComponent(invoiceNo);


		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY); 
		dateField.setWidth("120px");
		rightFrmLayout.addComponent(dateField);

		hriLayout.setSpacing(true);
		hriLayout.addComponent(leftFrmLayout);
		l1.setWidth("26px");
		hriLayout.addComponent(l1);
		hriLayout.addComponent(rightFrmLayout);

		table.setWidth("100%");
		table.setHeight("220px");

		table.addContainerProperty("Product Name", ComboBox.class , new ComboBox());
		table.setColumnWidth("Product Name",340);

		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",65);

		table.addContainerProperty("Stock", TextRead.class , new TextRead());
		table.setColumnWidth("Stock",65);

		table.addContainerProperty("Qty", TextField.class , new TextField());
		table.setColumnWidth("Qty",80);

		table.addContainerProperty("Remarks", TextField.class , new TextField());
		table.setColumnWidth("Remarks",120);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();

		btnLayout.addComponent(cButton);

		label.setHeight("25px");

		spaceLayout.setWidth("400px");
		lowerHorizontalLayout.addComponent(spaceLayout);
		totalField.setWidth("150px");

		mainLayout.addComponent(hriLayout);
		mainLayout.addComponent(table);
		mainLayout.setSpacing(true);
		mainLayout.addComponent(lowerHorizontalLayout);
		mainLayout.addComponent(label);
		mainLayout.addComponent(lbLine);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		btnIni(true);
		componentIni(true);
		setEventAction();

		this.addComponent(mainLayout);
		cButton.btnNew.focus();
		focusEnter();

	}



	private void focusEnter()
	{

		for(int i=0;i<txtqty.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(txtqty.get(i));
			allComp.add(txtremarks.get(i));	
		}

		allComp.add(cButton.btnSave);

		new com.common.share.FocusMoveByEnter(this,allComp);
	}



	public void setEventAction(){

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
				autoIssueNo();

			}


		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//isUpdate = false;
				saveButtonEvent();

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				updateButtonEvent();
			}
		});

		cButton.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				deleteButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				refreshButtonEvent();
			}
		});
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

	}

	private void autoIssueNo()
	{



		Transaction tx;
		try
		{
			System.out.println("Ready to do");

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select isnull(max(InvoiceNo),0)+1 from tbRawWastageDetails ").list().iterator();

			if(iter.hasNext())
			{
				invoiceNo.setValue(iter.next());

			}	
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void findButtonEvent()
	{
		isFind=true;
		Window win=new FindWindowSP(sessionBean,txtInvoiceId,"rawWastageEntry");
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) 
			{
				if(txtInvoiceId.getValue().toString().length()>0)
					findInitialise();
			}
		});


		this.getParent().addWindow(win);

	}

	private void findInitialise(){

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List led = session.createSQLQuery("select InvoiceNo,Date from tbRawWastageDetails where InvoiceNo='"+txtInvoiceId.getValue().toString().trim()+"'").list();
			txtClear();
			if(led.iterator().hasNext()){

				Object[] element = (Object[]) led.iterator().next();
				invoiceNo.setValue(element[0]);
				dateField.setValue(element[1]);		
			}
			List list=session.createSQLQuery("select ProductID,Qty,Remarks from tbRawWastageDetails  where InvoiceNo='"+txtInvoiceId.getValue().toString().trim()+"'").list();


			int i=0;

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element=(Object[]) iter.next();
				cmbProduct.get(i).setValue(element[0].toString());
				txtTempQty.get(i).setValue(element[1].toString());
				txtqty.get(i).setValue(element[1]);
				txtremarks.get(i).setValue(element[2].toString());


				i++;
			}
		}catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}

	}


	private void refreshButtonEvent() {

		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void deleteButtonEvent(){


		if(invoiceNo.getValue()!= null)
		{


			this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
					new YesNoDialog.Callback() {
				public void onDialogResult(boolean yes) {
					if(yes){
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						Transaction tx= session.beginTransaction();
						if(deleteData(session,tx)){
							tx.commit();
							txtClear();
							getParent().showNotification("All information delete Successfully");
						}
						else{
							tx.rollback();
							getParent().showNotification(
									"Delete Failed",
									"There are no data for delete.",
									Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
			}));
		}
		else
			this.getParent().showNotification(
					"Delete Failed",
					"There are no data for delete.",
					Notification.TYPE_WARNING_MESSAGE);
	}

	public boolean deleteData(Session session,Transaction tx)
	{
		String sql = null;


		try{
			sql = "delete tbRawWastageDetails   " +
					"where InvoiceNo='"+invoiceNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			System.out.println(sql);
			return true;

		}
		catch(Exception exp)
		{

			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}


	}

	private void updateButtonEvent()
	{

		if(invoiceNo.getValue()!= null)
		{
			isUpdate = true;
			isFind=false;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}


	private void saveButtonEvent() {

		if(!invoiceNo.getValue().toString().trim().isEmpty()){

			if(isUpdate)
			{
				this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
						new YesNoDialog.Callback() {
					public void onDialogResult(boolean yes) {
						if(yes){

							updateData();

							isFind=false;	
							isUpdate=false;
						}
					}
				}));
			}else{
				this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
						new YesNoDialog.Callback() {
					public void onDialogResult(boolean yes) 
					{
						if(yes)
						{
							Transaction tx;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							//if(nullCheck())
							insertData(session,tx);
						}
					}

				}));
			}







		}
		else
			this.getParent().showNotification("Warning :","Please Select Product .", Notification.TYPE_WARNING_MESSAGE);	
	}

	/*
	private boolean nullCheck(){

		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		Double  stockQty = 0.0;

		try{




		for (int i=0;i<txtStockQty.size();i++)
		{
			System.out.println("Not Fine");

			String productId =cmbProduct.get(i).getValue().toString().trim();

			Iterator iterator = session.createSQLQuery("select  closingAmount from [funRawMaterialsStock]('"+dateFormat.format(dateField.getValue())+" 23:59:59','"+productId+"')") .list().iterator();

			if(iterator.hasNext())
			{

				stockQty=Double.parseDouble(iterator.next().toString());
			}


			System.out.println(stockQty);
			Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
			System.out.println(cmbProduct.get(i).getValue());

			if (temp != null)
			{

				System.out.println("Help me");
					if(stockQty>=Integer.parseInt(txtqty.get(i).getValue().toString())){

						return true;
					}
					else{
						getParent().showNotification("Warnning","Stock Qty can not be exceed Qty. Stock Qty "+stockQty,
								Notification.TYPE_WARNING_MESSAGE);
						return false;
					}
			}
		}
		}
		catch(Exception ex){

			System.out.print(ex);
		}
		return false;
	}*/

	private void updateData(){
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String sql = null;

		System.out.println("Hi");
		try 
		{
			for (int i = 0; i < cmbProduct.size(); i++)
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				System.out.println(cmbProduct.get(i).getValue());
				if (temp != null && !cmbProduct.get(i).getValue().equals(("x#"+i)))
				{
					String productId =cmbProduct.get(i).getValue().toString().trim();

					System.out.println("not Done");

					String productId1 =cmbProduct.get(i).getValue().toString();

					String Sql= "select isnull(sum(Qty),0) as p , " 
							+"  isnull(sum(Qty * Rate),0) as q from tbRawPurchaseInfo ci, "
							+"   tbRawPurchaseDetails cd where ci.ReceiptNo = cd.ReceiptNo    "
							+"  and productid = '"+productId1+"' ";



					Iterator iter=session.createSQLQuery(Sql).list().iterator();
					System.out.print(Sql);

					String stRate = "",stQty = "";
					if(iter.hasNext()){
						System.out.println("not Done 21");

						Object[] element=(Object[]) iter.next();

						stQty=element[0].toString();
						stRate=element[1].toString();

						System.out.println("gfdhdhdhg");

					}
					if(stQty.equals(null))
						stQty="0.0";
					if(stRate.equals(null))
						stRate="0.0";

					System.out.println(Double.parseDouble(stRate)+" "+Double.parseDouble(stQty));

					if(!stQty.equals("0.0"))
						wRate=Double.parseDouble(stRate)/Double.parseDouble(stQty);
					else
						wRate=0.0;

					Amount=Double.parseDouble(txtqty.get(i).getValue().toString().trim()); 
					Total =Amount*wRate;


					sql =  "update tbRawWastageDetails set " +
							" Date = '" + dateFormat.format(dateField.getValue()) + "'," +
							" Qty = '"+Float.parseFloat(txtqty.get(i).getValue().toString().trim())+"'," +
							"  Amount = '"+Total+"'," +
							" Remarks = '"+txtremarks.get(i).getValue().toString()+"' where" +
							" ProductID = '"+productId1.trim()+"' and" +
							" InvoiceNo = '"+invoiceNo.getValue().toString().trim()+"'";


					System.out.println(sql);


					session.createSQLQuery(sql).executeUpdate();

				}
			}

			tx.commit();
			this.getParent().showNotification("All information Updated successfully.");

			componentIni(true);
			btnIni(true);
			txtClear();
		}catch(Exception ex){
			tx.rollback();
			this.getParent().showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}


	}

	private void insertData(Session session,Transaction tx)
	{

		try
		{
			System.out.println("ok");
			for (int i = 0; i<txtStockQty.size();i++)
			{

				System.out.println("nok");
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());
				System.out.println(cmbProduct.get(i).getValue());
				if (temp != null )
				{  
					System.out.println("not Done");

					String productId =cmbProduct.get(i).getValue().toString();
					//productId = productId.substring(0, productId.indexOf('#'));

					String Sql= "select isnull(sum(Qty),0) as p , " 
							+"  isnull(sum(Qty * Rate),0) as q from tbRawPurchaseInfo ci, "
							+"   tbRawPurchaseDetails cd where ci.ReceiptNo = cd.ReceiptNo       "
							+"  and productid = '"+productId+"' ";

					/*Iterator iter =session.createSQLQuery("select isnull(sum(Qty),0) as p , " +
							"isnull(sum(Qty * Rate),0) as q from tbRawPurchaseInfo ci," +
							" tbRawPurchaseDetails cd where ci.ReceiptNo = cd.ReceiptNo " +
							"and productid = '"+productId+"'").list().iterator();*/

					Iterator iter=session.createSQLQuery(Sql).list().iterator();
					System.out.print(Sql);

					String stRate = "",stQty = "";
					if(iter.hasNext()){
						System.out.println("not Done 21");

						Object[] element=(Object[]) iter.next();

						stQty=element[0].toString();
						stRate=element[1].toString();

						System.out.println("gfdhdhdhg");

					}
					if(stQty.equals(null))
						stQty="0.0";
					if(stRate.equals(null))
						stRate="0.0";

					System.out.println(Double.parseDouble(stRate)+" "+Double.parseDouble(stQty));

					//Double wRate,Amount;
					if(!stQty.equals("0.0"))
						wRate=Double.parseDouble(stRate)/Double.parseDouble(stQty);
					else
						wRate=0.0;

					Amount=Double.parseDouble(txtqty.get(i).getValue().toString().trim()); 
					Total =Amount*wRate;


					session.createSQLQuery("insert tbRawWastageDetails " +
							"(InvoiceNo,ProductID,Date,Qty,Rate, amount,Remarks)" +
							" values('"+invoiceNo.getValue().toString().trim()+"','"+productId.trim()+"'," +
							"'" + dateFormat.format(dateField.getValue()) + "'," +
							"'"+txtqty.get(i).getValue().toString().trim()+"','"+wRate+"'," +
							" '"+Total+"' ,  "  +                                                            
							"'"+txtremarks.get(i).getValue().toString()+"')").executeUpdate();

				}
			}

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			componentIni(true);
			btnIni(true);
			txtClear();
			isFind=false;	
			isUpdate=false;


		}catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}

	}

	private void newButtonEvent() {

		System.out.println("not Done");

		componentIni(false);
		btnIni(false);
		txtClear();

	}

	public void txtClear()
	{

		invoiceNo.setValue("");
		for(int i=0;i<txtunit.size();i++)
		{
			cmbProduct.get(i).setValue(null);
			txtStockQty.get(i).setValue("");
			txtunit.get(i).setValue("");
			txtqty.get(i).setValue("");
			txtremarks.get(i).setValue("");

		}

	}

	private void componentIni(boolean b) 
	{

		lbLine.setEnabled(!b);
		hriLayout.setEnabled(!b);
		table.setEnabled(!b);
		lowerHorizontalLayout.setEnabled(!b);

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

	public void tableInitialise()
	{

		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private boolean doubleEntryCheck(String caption,int row)
	{

		for(int i=0;i<txtqty.size();i++){

			if(i!=row  && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue()))  )
			{
				return false;
			}

		}
		return true;
	}

	public void proComboChange(String head,int r)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String sql=null;
		try{
			Iterator iterator = session.createSQLQuery("select unit,closingQty,productId from [funRawMaterialsStock]('"+dateFormat.format(dateField.getValue())+" 23:59:59','"+head+"')") .list().iterator();
			if(iterator.hasNext()){
				Object[] element=(Object[]) iterator.next();
				txtunit.get(r).setValue(element[0].toString());
				txtStockQty.get(r).setValue(df.format(Double.parseDouble(element[1].toString())));
				hStock.put(element[2].toString(),element[1].toString());
			}
		}catch(Exception ex){

		}
	}


	public void tableRowAdd(final int ar)
	{

		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try
		{
			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");

			cmbProduct.get(ar).setImmediate(true);

			cmbProduct.get(ar).setNullSelectionAllowed(true);
			List lst = session.createSQLQuery("Select vRawItemName, vRawItemCode, vUnitName from  tbRawItemInfo WHERE vRawItemCode in (SELECT ProductID FROM tbRawIssueDetails) order by vRawItemName").list();
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProduct.get(ar).addItem(element[1]);
				cmbProduct.get(ar).setItemCaption(element[1], (String) element[0]);
				hUnit.put(element[1], element[2]);
				
			}
			
			cmbProduct.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{


					if(cmbProduct.get(ar).getValue()!=null)

					{
						boolean fla=(doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()),ar));

						if (cmbProduct.get(ar).getValue() != null && fla )
						{
							int a;
							String head=cmbProduct.get(ar).getValue().toString();
							System.out.println("ProductId: "+head);
							proComboChange(head,ar);

							txtqty.get(ar).focus();

							//tableColumnAction(head,ar);
							if((ar+1)==hRate.size() )
							{
								tableRowAdd(hRate.size());
							}
						}
						else
						{	
							Object checkNull=(Object)cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue());
							System.out.print(checkNull);
							if(!checkNull.equals("")){
								cmbProduct.get(ar).setValue(null);//("x#"+ar,"");
								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							}

						}


					}


				}
			});


			txtunit.add(ar,new TextRead(""));
			txtunit.get(ar).setWidth("100%");

			txtStockQty.add(ar,new TextRead(""));
			txtStockQty.get(ar).setWidth("100%");

			txtTempQty.add(ar,new TextRead(""));
			txtTempQty.get(ar).setWidth("100%");


			txtqty.add( ar , new AmountField());
			txtqty.get(ar).setWidth("100%");
			txtqty.get(ar).setImmediate(true);
			txtqty.get(ar).setTextChangeEventMode(TextChangeEventMode.LAZY);
			txtqty.get(ar).setTextChangeTimeout(100);
			txtqty.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {


					if(!txtqty.get(ar).getValue().toString().isEmpty())
					{
						Double	stkQty= txtStockQty.get(ar).getValue().toString().isEmpty()?0.00: Double.parseDouble(txtStockQty.get(ar).getValue().toString());
						Double qty=   txtqty.get(ar).getValue().toString().isEmpty()?0.00: Double.parseDouble(txtqty.get(ar).getValue().toString().trim());
						Double tempqty= txtTempQty.get(ar).getValue().toString().isEmpty()?0.00:Double.parseDouble(txtTempQty.get(ar).getValue().toString().trim());


						if(stkQty<Math.abs(qty-tempqty))
						{
							showNotification("Qty Can not be Exceded Stock Qty",Notification.TYPE_WARNING_MESSAGE);
							txtqty.get(ar).setValue(0);

						}  


					} 


				}
			});

			txtremarks.add(ar,new TextField(""));
			txtremarks.get(ar).setWidth("100%");
			txtremarks.get(ar).setHeight("-1.0px");
			txtremarks.get(ar).setImmediate(true);

			table.addItem(new Object[]{cmbProduct.get(ar),txtunit.get(ar),txtStockQty.get(ar),txtqty.get(ar),txtremarks.get(ar) },ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

}
