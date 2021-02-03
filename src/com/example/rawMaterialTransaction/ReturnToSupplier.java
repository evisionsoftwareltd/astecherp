package com.example.rawMaterialTransaction;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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

public class ReturnToSupplier extends Window {
	SessionBean sessionBean;

	private CommonButton cButton = new CommonButton( "",  "Save",  "Edit",  "",  "Refresh",  "", "", "","","Exit");

	private FormLayout leftFrmLayout = new FormLayout();
	private FormLayout rightFrmLayout = new FormLayout();
	private HorizontalLayout hriLayout = new HorizontalLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout vLayout = new VerticalLayout();
	private HorizontalLayout spaceLayout = new HorizontalLayout();
	private HorizontalLayout lowerHorizontalLayout = new HorizontalLayout();



	private TextField txtchallanNo = new TextField();


	private ComboBox cmbreceiptNo = new ComboBox();

	private PopupDateField dateField = new PopupDateField();
	private Label amountWordLabel = new Label("Amount In Words: ");
	private Label totalLabel = new Label("Total : ");
	private TextField txtamountWordsField = new TextField();
	private TextField txtReceiptId=new TextField();
	private TextRead txttotalField = new TextRead();
	private Label lbLine=new Label("__________________________________________________________________________________________________________________");
	private Table table = new Table();
	private ArrayList<Label> lblProduct = new ArrayList<Label>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<TextRead> rate = new ArrayList<TextRead>();
	private ArrayList<TextRead> rqty = new ArrayList<TextRead>();
	private ArrayList<AmountField> returnqty = new ArrayList<AmountField>();
	private ArrayList<AmountField> amount = new ArrayList<AmountField>();

	private AmountField ttoAmt=new AmountField();




	private HashMap hvoucherNo=new HashMap();
	private HashMap hproudId=new HashMap();
	private Formatter fmt = new Formatter();


	private Label label = new Label();
	private Label l1 = new Label();
	double totalsum = 0.0;
	boolean isUpdate=false;
	String strFlag;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public String receiptNo;
	public ReturnToSupplier(SessionBean sessionBean) {

		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setWidth("795px");
		this.setHeight("510px");

		this.setCaption("RETURN TO SUPPLIER ENTRY  :: "+sessionBean.getCompany());



		hriLayout.addComponent(new Label("Receipt No: "));
		cmbreceiptNo.setImmediate(true);
		cmbreceiptNo.setWidth("190px");
		hriLayout.addComponent(cmbreceiptNo);

		hriLayout.addComponent(new Label("Challan No: "));
		txtchallanNo.setWidth("140px");
		hriLayout.addComponent(txtchallanNo);



		dateField.setDateFormat("dd-MM-yyyy");
		hriLayout.addComponent(new Label("Date"));
		dateField.setValue(new java.util.Date());
		dateField.setWidth("100px");

		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		hriLayout.addComponent(dateField);
		hriLayout.setSpacing(true);





		table.setWidth("100%");
		table.setHeight("256px");

		table.addContainerProperty("Product Name", Label.class , new Label());
		table.setColumnWidth("Product Name",268);
		table.addContainerProperty("Unit", TextRead.class , new TextRead());
		table.setColumnWidth("Unit",60);
		table.addContainerProperty("Received Qty", TextRead.class , new TextRead());
		table.setColumnWidth("Received Qty",70);
		table.addContainerProperty("Return Qty", AmountField.class , new AmountField());
		table.setColumnWidth("Return Qty",80);
		table.addContainerProperty("Rate", TextRead.class , new TextRead());
		table.setColumnWidth("Rate",70);

		table.addContainerProperty("Return Amount", AmountField.class , new AmountField());
		table.setColumnWidth("Return Amount",100);

		table.setColumnCollapsingAllowed(true);
		tableInitialise();


		btnLayout.addComponent(cButton);
		label.setHeight("25px");


		lowerHorizontalLayout.setHeight("20px");

		mainLayout.addComponent(hriLayout);
		mainLayout.addComponent(lowerHorizontalLayout);
		mainLayout.addComponent(table);
		mainLayout.setSpacing(true);
		spaceLayout.setSpacing(true);
		spaceLayout.addComponent(new Label("Total Amount"));
		spaceLayout.addComponent(ttoAmt);


		mainLayout.addComponent(spaceLayout);
		mainLayout.setComponentAlignment(spaceLayout, Alignment.BOTTOM_RIGHT);
		mainLayout.addComponent(label);
		mainLayout.addComponent(lbLine);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		btnIni(true);
		componentIni(true);
		cmbreceiptNoData();


		setEventAction();

		this.addComponent(mainLayout);

	}
	private void cmbreceiptNoData(){


		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List lst = session.createSQLQuery("Select  ReceiptNo, ReceiptNo,ChallanNo,VoucherNo,Ledger_Id from tbRawPurchaseInfo r,tbSupplierDetails s where r.SupplierId=s.SupplierID order by r.ReceiptNo").list();

			int i=0;
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbreceiptNo.addItem(element[2].toString());
				cmbreceiptNo.setItemCaption(element[2], (String)element[1]);
				hvoucherNo.put(element[2], element[3]);
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}

	}




	public void tableInitialise(){

		for(int i=0;i<8;i++){
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();


			lblProduct.add(ar,new Label());
			lblProduct.get(ar).setWidth("100%");
			lblProduct.get(ar).setImmediate(true);


			unit.add(ar,new TextRead(""));
			unit.get(ar).setWidth("100%");


			rate.add(ar,new TextRead(1));

			rate.get(ar).setWidth("100%");

			rqty.add( ar , new TextRead(1));
			rqty.get(ar).setWidth("100%");

			returnqty.add( ar , new AmountField());
			returnqty.get(ar).setWidth("100%");


			returnqty.get(ar).addListener(new TextChangeListener() {
				public void textChange(TextChangeEvent event) {
					tableColumnAction(event,ar);
				}
			});
			
			/*returnqty.get(ar).addListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					returnqty.get(ar+1).focus();
				}
			});*/
			amount.add( ar , new AmountField());
			amount.get(ar).setWidth("100%");

			table.addItem(new Object[]{lblProduct.get(ar),unit.get(ar),rqty.get(ar),returnqty.get(ar),rate.get(ar),amount.get(ar)},ar);

		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void tableColumnAction(TextChangeEvent event,int r)
	{



		//returnqty.get(r).setTextChangeEventMode(TextChangeEventMode.LAZY);

		//returnqty.get(r).setTextChangeTimeout(200);


		try
		{


			String tbquntity;
			double tamount,unitPrice;

			if(!event.getText().trim().isEmpty())
			{
				if(Double.parseDouble(rqty.get(r).getValue().toString())>= Double.parseDouble(event.getText().toString().trim())){

					tbquntity= event.getText().trim();

					String tempPrice=rate.get(r).getValue().toString().trim();
					//System.out.println(tempPrice);
					unitPrice=Double.parseDouble(tempPrice);
					System.out.println("column Action");
					tamount=unitPrice*(Double.parseDouble(tbquntity));
					fmt = new Formatter();
					amount.get(r).setValue(fmt.format("%.2f",tamount));
				}
				else
					getParent().showNotification("Warnning","Return Qty can not be exceed Qty. Receive Qty "+Double.parseDouble(rqty.get(r).getValue().toString()),
							Notification.TYPE_WARNING_MESSAGE);
				//returnqty.get(r).setValue("");
			}

			addtionAmnt();				

		}
		catch(Exception exp)
		{
			getParent().showNotification("Errora",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}



	}




	public void setEventAction(){



		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

				if(nullCheck())
					saveButtonEvent();

			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbreceiptNo.getValue()!=null)
					updateButtonEvent();
				else{

					getParent().showNotification(
							"Warning","Please Select Receipt No.",
							Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});



		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
			}
		});

		cmbreceiptNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				txtClear();
				if(cmbreceiptNo.getValue()!=null)
					cmbChangeValue();
				else
					txtchallanNo.setValue("");
			}
		});



	}

	private  void cmbChangeValue(){




		Transaction tx;
		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List lst = session.createSQLQuery("select c.ProductID, ProductName,Unit, Qty, Rate,Ledger_Id from tbRawPurchaseDetails c, tbRawProductInfo p where c.ProductId = p.ProductCode and ReceiptNo = '"+cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+"' order by AutoID").list();
			List lst2 = session.createSQLQuery("select ProductID, Qty from tbRawPurchaseReturnDetails where ReceiptNo ='"+cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+"'").list();

			int i=0;
			for (Iterator iter = lst.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[])iter.next();
				if(i==lblProduct.size()-2){


					tableRowAdd(i+2);

				}
				lblProduct.get(i).setValue(element[1]);
				unit.get(i).setValue(element[2]);
				rqty.get(i).setValue(element[3]);
				fmt = new Formatter();
				rate.get(i).setValue(fmt.format("%.2f",element[4]));
				hproudId.put(element[1], element[0]);

				i++;
			}
			int j=0;
			for (Iterator iter = lst2.iterator(); iter.hasNext();)
			{
				Object[] element=(Object[])iter.next();

				if((hproudId.get(lblProduct.get(j).getValue())).equals(element[0])){

					returnqty.get(j).setValue(element[1]);

					amount.get(j).setValue(Double.parseDouble(element[1].toString())*Double.parseDouble(rate.get(j).getValue().toString()));

				}

				j++;
			}
			addtionAmnt();
			txtchallanNo.setValue(cmbreceiptNo.getValue());


		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void  addtionAmnt(){

		ttoAmt.setImmediate(true);
		totalsum=0.0;
		for(int flag=0;flag<amount.size();flag++)
		{							
			if(amount.get(flag).getValue().toString().trim().length()>0)
			{
				String flagbit = amount.get(flag).getValue().toString();
				totalsum=totalsum+Double.parseDouble(flagbit);//flagbit;
			}
		}
		fmt = new Formatter();
		ttoAmt.setValue(fmt.format("%.2f",totalsum));	
	}

	private void refreshButtonEvent() {

		componentIni(true);
		btnIni(true);
		txtClear();
		cmbreceiptNo.setValue(null);
	}

	private void deleteButtonEvent(){



	}

	private void updateButtonEvent(){

		componentIni(false);
		btnIni(false);


	}


	private void saveButtonEvent() {

		this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
				new YesNoDialog.Callback() {
			public void onDialogResult(boolean yes) {
				if(yes){
					Transaction tx=null;
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();
					if(deleteData(session,tx) ){
						insertData(session,tx);
						cmbreceiptNoData();
					}
					else{
						tx.rollback();
					}
				}
			}
		}));

	}



	private boolean deleteData(Session session,Transaction tx){

		try{
			session.createSQLQuery("delete tbRawPurchaseReturn where ReceiptNo ='"+cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+"'").executeUpdate();
			session.createSQLQuery("delete tbRawPurchaseReturnDetails where ReceiptNo ='"+cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+"'").executeUpdate();
			return true;

		}
		catch(Exception exp){

			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}

	}



	private boolean nullCheck(){

		for (int i = 0; i < lblProduct.size(); i++)
		{
			Object temp = lblProduct.get(i).getValue();
			//System.out.println(cmbProduct.get(i).getValue());
			if (temp != "")
			{
				if(Double.parseDouble(rqty.get(i).getValue().toString())<= Double.parseDouble(returnqty.get(i).getValue().toString().trim().isEmpty()?"0":returnqty.get(i).getValue().toString().trim())){

					getParent().showNotification("Warnning","Return Qty can not be exceed Qty. Receive Qty "+Double.parseDouble(rqty.get(i).getValue().toString()),
							Notification.TYPE_WARNING_MESSAGE);
					return false;

				}
				else
					return true;
			}
		}

		return false;
	}
	private void insertData(Session session,Transaction tx){


		try
		{





			session.createSQLQuery("insert tbRawPurchaseReturn (ReceiptNo,Date,TotalAmount,ChallanNo,VoucherNo) values('" +cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+"','" +dateformat.format(dateField.getValue())+
					"'," +ttoAmt.getValue().toString().trim()+",'" +txtchallanNo.getValue().toString().trim()+ "','" + hvoucherNo.get(cmbreceiptNo.getValue()) + "')").executeUpdate();


			for (int i = 0; i < lblProduct.size(); i++)
			{
				Object temp = lblProduct.get(i).getValue();
				//System.out.println(cmbProduct.get(i).getValue());
				if (temp != "")
				{
					String returnQty=returnqty.get(i).getValue().toString().trim().isEmpty()?"0":returnqty.get(i).getValue().toString().trim();
					session.createSQLQuery("insert tbRawPurchaseReturnDetails (ReceiptNo,ProductID,Qty,Date,Amount) values('" +cmbreceiptNo.getItemCaption(cmbreceiptNo.getValue())+ "','" + hproudId.get(lblProduct.get(i).getValue().toString().trim()) + "','" +returnQty+"','"+dateformat.format(dateField.getValue())+"','"+rate.get(i).getValue().toString().trim()+ "')").executeUpdate();

				}
			}

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
			cmbreceiptNo.setValue(null);


		}catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}
	}

	public void txtClear(){

		try
		{
			txtchallanNo.setValue("");
			ttoAmt.setValue("");

			for(int i=0;i<unit.size();i++){

				lblProduct.get(i).setValue("");
				unit.get(i).setValue("");
				amount.get(i).setValue("");
				rate.get(i).setValue("");
				rqty.get(i).setValue("");
				returnqty.get(i).setValue("");
			}

			txttotalField.setValue("");
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void componentIni(boolean b) {

		lbLine.setEnabled(!b);
		table.setEnabled(!b);
		spaceLayout.setEnabled(!b);
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

}
