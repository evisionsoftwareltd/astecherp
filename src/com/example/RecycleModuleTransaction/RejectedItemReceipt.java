package com.example.RecycleModuleTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.CommonButtonNew;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.ImmediateUploadExampleNew;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import database.hibernate.TbSubGroup;

public class RejectedItemReceipt extends Window 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SessionBean sessionBean;
	private Label lblReceiptNo;
	private Label lblDate;
	private Label lblChallan;
	private Label lblChallanDate;
	private TextRead txtreceiptNo;
	private TextRead txtFindreceiptNo=new TextRead();
	private TextField txtchallanNo;
	private PopupDateField dateField;
	private PopupDateField dChallanDate;
	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> lblUnit = new ArrayList<TextRead>();
	private ArrayList<TextRead> lblcolor = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> QtyKg = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> QtyPcs = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> wastageQtyKg = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> wastageQtyPcs = new ArrayList<AmountCommaSeperator>();
	//private ArrayList<AmountCommaSeperator> Qty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField> Remarks = new ArrayList<TextField>();
	private ArrayList<ComboBox> cmbStore = new ArrayList<ComboBox>();
	
	
	private HashMap productUnit=new HashMap();
	double totalsum = 0.0;
	private Formatter fmt = new Formatter();
	private TextRead txttotalField = new TextRead();

	boolean isUpdate=false,isFind=false;
	String udFlag;
	private HashMap supplierAddress=new HashMap();

	String strFlag;
	private Label lbLine;
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dformate = new DecimalFormat("#0");

	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");


	private AbsoluteLayout mainLayout;
	HashMap hMap=new HashMap();

	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private HashMap hmproduct=new HashMap();
	private HashMap hmqty= new HashMap();

	private Label lblCommon;

	public RejectedItemReceipt(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("REJECTED ITEM RECEIPT ::"+sessionBean.getCompany());
		setWidth("1100px");
		setHeight("625px");
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		btnIni(true);
		componentIni(true);
		txtClear();
		FocusMove();
	}
	public void setEventAction()
	{
		cButton.btnPreview.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(isFind&&!txtreceiptNo.getValue().toString().isEmpty()){
					//reportShow();
				}
				else
				{
					showNotification("Nothing To Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});



		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				autoReciptNo();
				udFlag="New";
				isUpdate=false;
				isFind=false;
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					saveButtonEvent();
				}
				else{
					showNotification("Warning","You are not permitted to save date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				udFlag="Update";
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
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

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		
	}



	public void dataload(int i )
	{
		try{
			cmbProduct.get(i).removeAllItems();
			String sql="select vCode,vItemName from tbThirdPartyItemInfo where vCategoryType='' ";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbProduct.get(i).addItem(element[0].toString());
				cmbProduct.get(i).setItemCaption(element[0].toString(),element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void FocusMove()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(dateField);
		allComp.add(dChallanDate);
		allComp.add(txtchallanNo);

		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(QtyKg.get(i));	
			allComp.add(QtyPcs.get(i));	
			allComp.add(wastageQtyKg.get(i));	
			allComp.add(wastageQtyPcs.get(i));
			allComp.add(cmbStore.get(i));
			allComp.add(Remarks.get(i));
		}
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);

		new FocusMoveByEnter(this,allComp);
	}
	private void componentIni(boolean b) 
	{
		txtreceiptNo.setEnabled(!b);
		dateField.setEnabled(!b);
		txtchallanNo.setEnabled(!b);
		dChallanDate.setEnabled(!b);
		lbLine.setEnabled(!b);

		for(int i=0;i<cmbProduct.size();i++){
			cmbProduct.get(i).setEnabled(!b);
			lblUnit.get(i).setEnabled(!b);
			QtyKg.get(i).setEnabled(!b);
			QtyPcs.get(i).setEnabled(!b);
			lblcolor.get(i).setEnabled(!b);
			wastageQtyKg.get(i).setEnabled(!b);
			wastageQtyPcs.get(i).setEnabled(!b);
			cmbStore.get(i).setEnabled(!b);
			Remarks.get(i).setEnabled(!b);

		}
	}


	public void txtClear()
	{
		try
		{	

			txtreceiptNo.setValue("");
			txtchallanNo.setValue("");
			tableclear();
			//table.setColumnFooter("Amount", "Total:"+0.0);
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

	}
	public void tableClear(){
		for(int i=0;i<lblUnit.size();i++)
		{
			cmbProduct.get(i).setValue(null);
			lblUnit.get(i).setValue("");
			QtyKg.get(i).setValue("");
			
			QtyPcs.get(i).setValue("");
			wastageQtyKg.get(i).setValue("");
			wastageQtyPcs.get(i).setValue("");
			
			
			Remarks.get(i).setValue("");
			QtyKg.get(i).setValue("");
		}
	}


	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);	
		txtClear();	
		isUpdate=false;
	}

	private Iterator<?> dbService(String sql){

		System.out.println(sql);
		Session session=null;
		Iterator<?> iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		// top-level component properties


		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:50.0px;left:50.0px;");

		// dateField
		dateField = new PopupDateField();
		dateField.setImmediate(true);
		dateField.setWidth("107px");
		dateField.setHeight("-1px");
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dateField, "top:50.0px;left:120.0px;");

		// lblChallanDate
		lblChallanDate = new Label();
		lblChallanDate.setImmediate(false);
		lblChallanDate.setWidth("-1px");
		lblChallanDate.setHeight("-1px");
		lblChallanDate.setValue("Challan Date :");
		mainLayout.addComponent(lblChallanDate, "top:80.0px;left:50.0px;");

		// dChallanDate
		dChallanDate = new PopupDateField();
		dChallanDate.setImmediate(true);
		dChallanDate.setWidth("107px");
		dChallanDate.setHeight("-1px");
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChallanDate, "top:80.0px;left:120.0px;");





		// lblReceiptNo
		lblReceiptNo = new Label();
		lblReceiptNo.setImmediate(false);
		lblReceiptNo.setWidth("-1px");
		lblReceiptNo.setHeight("-1px");
		lblReceiptNo.setValue("Receipt No :");
		mainLayout.addComponent(lblReceiptNo, "top:50.0px;left:300.0px;");

		// txtreceiptNo
		txtreceiptNo = new TextRead(1);
		txtreceiptNo.setImmediate(true);
		txtreceiptNo.setWidth("100px");
		txtreceiptNo.setHeight("-1px");
		mainLayout.addComponent(txtreceiptNo, "top:50.0px;left:420.0px;");







		// lblChallan
		lblChallan = new Label();
		lblChallan.setImmediate(false);
		lblChallan.setWidth("-1px");
		lblChallan.setHeight("-1px");
		lblChallan.setValue("Challan No :");
		mainLayout.addComponent(lblChallan, "top:80.0px;left:300.0px;");

		// txtchallanNo
		txtchallanNo = new TextField();
		txtchallanNo.setImmediate(true);
		txtchallanNo.setWidth("100px");
		txtchallanNo.setHeight("-1px");
		mainLayout.addComponent(txtchallanNo, "top:80.0px;left:420.0px;");



		////////////////

		// table
		table = new Table();
		table.setWidth("99%");
		table.setHeight("350px");
		table.setFooterVisible(true);

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product Name", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Product Name",250);

		table.addContainerProperty("Unit", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Unit",40);
		
		table.addContainerProperty("Color", TextRead.class , new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Color",40);

		table.addContainerProperty("Qty(kg)", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(kg)",75);
		
		table.addContainerProperty("Qty(Pcs)", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty(Pcs)",75);
		
		table.addContainerProperty("WastageQty(Kg)", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("WastageQty(Kg)",80);
		
		table.addContainerProperty("WastageQty(Pcs)", AmountCommaSeperator.class , new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("WastageQty(Pcs)",80);
		
		table.addContainerProperty("Store", ComboBox.class , new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Store",150);

		table.addContainerProperty("Remarks", TextField.class , new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Remarks",100);


		tableInitialise();
		mainLayout.addComponent(table, "top:130.0px;left:5.0px;");

		lbLine = new Label("_____________________________________________________________________________________________________________________________________________________________________________________________");
		mainLayout.addComponent(lbLine, "top:495.0px;left:0.0px;");

		mainLayout.addComponent(cButton, "top:530.0px;left:213.0px;");

		return mainLayout;
	}

	public void tableInitialise(){
		for(int i=0;i<10;i++){
			tableRowAdd(i);
		}
	}

	public void ProductDataLoad(String id,int ar)
	{
		String sql=    "select unit,color from tbSemiFgInfo where semiFgCode='"+id+"' "
				       +"union all "
				        +"select '',color from tbSemiFgSubInformation where semiFgSubId='"+id+"' ";
		 
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext())
		{
			Object[]element=(Object[]) iter.next();
			lblUnit.get(ar).setValue(element[0].toString());
			lblcolor.get(ar).setValue(element[1].toString());
		}
	}

	public void tableRowAdd(final int ar)
	{
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx=session.beginTransaction();
		String query="";
		System.out.println("Value of tableRowadd: "+ar);
		try{
			tbLblSl.add(ar,new Label());
			tbLblSl.get(ar).setWidth("20px");
			tbLblSl.get(ar).setValue(ar + 1);
			tbLblSl.get(ar).setImmediate(true);

			cmbProduct.add(ar,new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(false);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			String sql="select semiFgCode,semiFgName from tbSemiFgInfo "
					  +"union "
					  +"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation " ;
			
			Iterator<?>iter=session.createSQLQuery(sql).list().iterator();
			while(iter.hasNext())
			{
			   Object[]element=(Object[]) iter.next();	
			   cmbProduct.get(ar).addItem(element[0]);
			   cmbProduct.get(ar).setItemCaption(element[0], element[1].toString());
			}
			

			cmbProduct.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(cmbProduct.get(ar).getValue()!=null)
					{
						ProductDataLoad(cmbProduct.get(ar).getValue().toString(),ar);
					}

				}
			});



			lblUnit.add(ar,new TextRead(""));
			lblUnit.get(ar).setWidth("100%");
			
			lblcolor.add(ar,new TextRead(""));
			lblcolor.get(ar).setWidth("100%");

			QtyKg.add( ar , new AmountCommaSeperator());
			QtyKg.get(ar).setWidth("90%");
			QtyKg.get(ar).setImmediate(true);
			
			
			QtyPcs.add( ar , new AmountCommaSeperator());
			QtyPcs.get(ar).setWidth("90%");
			QtyPcs.get(ar).setImmediate(true);
			
			
			wastageQtyKg.add( ar , new AmountCommaSeperator());
			wastageQtyKg.get(ar).setWidth("90%");
			wastageQtyKg.get(ar).setImmediate(true);
			
			wastageQtyPcs.add( ar , new AmountCommaSeperator());
			wastageQtyPcs.get(ar).setWidth("90%");
			wastageQtyPcs.get(ar).setImmediate(true);
			
			
			cmbStore.add(ar,new ComboBox());
			cmbStore.get(ar).setWidth("100%");
			cmbStore.get(ar).setImmediate(true);
			cmbStore.get(ar).setNullSelectionAllowed(false);
			cmbStore.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			
			String sqlStore="select vDepoId,vDepoName from tbDepoInformation";
			
			Iterator<?>iterstr=session.createSQLQuery(sqlStore).list().iterator();
			while(iterstr.hasNext())
			{
			   Object[]element=(Object[]) iterstr.next();	
			   cmbStore.get(ar).addItem(element[0]);
			   cmbStore.get(ar).setItemCaption(element[0], element[1].toString());
			}


			Remarks.add(ar,new TextField());
			Remarks.get(ar).setWidth("90%");
			Remarks.get(ar).setImmediate(true);



			table.addItem(new Object[]{tbLblSl.get(ar),cmbProduct.get(ar),lblUnit.get(ar),lblcolor.get(ar),QtyKg.get(ar),QtyPcs.get(ar),wastageQtyKg.get(ar),wastageQtyPcs.get(ar),cmbStore.get(ar) ,Remarks.get(ar)},ar);
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void tableclear()
	{
		for(int i=0;i<lblUnit.size();i++){
			cmbProduct.get(i).setValue(null);
			lblUnit.get(i).setValue("");
			lblcolor.get(i).setValue("");
			QtyKg.get(i).setValue("");
			QtyPcs.get(i).setValue("");
			wastageQtyKg.get(i).setValue("");
			wastageQtyPcs.get(i).setValue("");
			cmbStore.get(i).setValue(null);
			Remarks.get(i).setValue("");
			
		}
	}
/*	private void reportShow()
	{	
		int type=1;
		String query=null;
		Transaction tx=null;
		String ReceiptNo="";
		String parentype="";
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());

			hm.put("user", sessionBean.getUserName());

			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());



			query="select Info.vPartyName,Info.vPartyAddress,Info.vChallanNo,Info.dChallanDate, (select vSourceName from tbSourceInfo where info.vSource = iSourceID) vSourceName,Info.vReceiptNo," +
					"Info.dReceiptDate, details.vProductName,details.vUnit,details.mQty,details.mRate,details.mAmount " +
					"from tb3rdPartyReceiptInformation Info inner join tb3rdPartyReceiptDetails details " +
					" on Info.vReceiptNo = details.vReceiptNo " +
					"where Info.vPartyId like '"+parentype+"'  and Info.vReceiptNo like '1'";



			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				Window win = new ReportViewerNew(hm,"report/raw/rptThirdPartyItemReceipt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else
			{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
*/



	private String autoReciptNo(){
		String autoId=null;
		try{
			String sql="select ISNULL(MAX(iReceiptNo),0)+1  from tbRejectedReceiptInfo";
			Iterator<?> iter=dbService(sql);
			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtreceiptNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}

	private void refreshButtonEvent() 
	{
		isFind=false;
		isUpdate=false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}


	private String autocodegenerate(String autocode, int length)
	{

		while(autocode.length()<8)
		{
			autocode="0"+autocode;
		}

		return autocode;


	}

	private void updateButtonEvent(){

		if(!txtreceiptNo.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		}
		else
			this.getParent().showNotification(
					"Update Failed",
					"There are no data for update.",
					Notification.TYPE_WARNING_MESSAGE);
	}
	private boolean doubleEntryCheck(String caption,int row){

		for(int i=0;i<cmbProduct.size();i++){

			if(i!=row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue()))){

				return false;
			}
		}
		return true;
	}


	@SuppressWarnings("unused")


	private boolean tableDataCheck(){

		for(int a=0;a<cmbProduct.size();a++){
			if(cmbProduct.get(a).getValue()!=null && cmbStore.get(a).getValue()!=null  ){
				if(!QtyKg.get(a).getValue().toString().isEmpty() && !QtyPcs.get(a).toString().isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	private void executeProcedure() {
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="exec PrcPOleftQty ''";
			session.createSQLQuery(query).executeUpdate();
			tx.commit();

		} 
		catch (Exception ex) 
		{
			tx.rollback();
			showNotification("From Procedure"+ex,Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void saveButtonEvent() 
	{


		if(!txtreceiptNo.getValue().toString().trim().isEmpty())
		{
			if(!txtchallanNo.getValue().toString().trim().isEmpty())
			{
				if(tableDataCheck()){
					if(isUpdate)
					{
						final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								if(buttonType == ButtonType.YES)
								{
									mb.buttonLayout.getComponent(0).setEnabled(false);
									Transaction tx=null;
									Session session = SessionFactoryUtil.getInstance().openSession();
									tx = session.beginTransaction();
									if(deleteData(session,tx))
									{
										insertData(session,tx);
									}
									else{
										tx.rollback();
									}
									isUpdate=false;
									mb.close();
								}
							}
						});	

					}
					else
					{
						final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								if(buttonType == ButtonType.YES)
								{
									mb.buttonLayout.getComponent(0).setEnabled(false);
									Transaction tx=null;
									Session session = SessionFactoryUtil.getInstance().getCurrentSession();
									tx = session.beginTransaction();
									insertData(session,tx);
									isUpdate=false;
									executeProcedure();
									mb.close();
								}
							}

						});	

					}
				}
				else
				{
					this.getParent().showNotification("Warning :","Please provide all data in table.", Notification.TYPE_WARNING_MESSAGE);
				}	
			}
			else{
				this.getParent().showNotification("Warning :","Please Enter Challan No.", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
			this.getParent().showNotification("Warning :","Please Enter Receipt No..", Notification.TYPE_WARNING_MESSAGE);



	}
	public boolean deleteData(Session session,Transaction tx){
		try{
			String sqlinfo="delete from tbRejectedReceiptInfo where iReceiptNo='"+txtreceiptNo.getValue().toString()+"' ";
			String sqlDetails="delete from tbRejectedItemReceipDetails where iReceiptNo='"+txtreceiptNo.getValue().toString()+"' ";
			
			
			session.createSQLQuery(sqlinfo).executeUpdate();
			session.createSQLQuery(sqlDetails).executeUpdate();

			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
	}


		private void insertData(Session session,Transaction tx) 
	{
		
		String PartyLedger="";
		String ReceiptNo="";
		String transectionId="",partyId="",partyIdName="";
		String sqlInfo="",sqlDetails="",sqlUdInfo="",sqlUdDetails="",  vocherId="";

		String productId="";
		String ProductName="";


		
		try
		{
			if(!isUpdate)
			{
			  txtreceiptNo.setValue(autoReciptNo());	
			}
			

			sqlInfo="insert into tbRejectedReceiptInfo(iReceiptNo,dReceiptDate,vchallanNo,dChallanDate,vUserIp, "
					+"vUserId,dEntryTime) values('"+txtreceiptNo.getValue().toString()+"','"+datef.format(dateField.getValue())+"',"
					+ " '"+txtchallanNo.getValue().toString()+"','"+datef.format(dChallanDate.getValue())+"','"+sessionBean.getUserIp()+"',"
					+ " '"+sessionBean.getUserId()+"',getdate() ) ";
			System.out.println(sqlInfo);
			session.createSQLQuery(sqlInfo).executeUpdate();
		

			for (int i = 0; i < cmbProduct.size(); i++)
			{
			
				if (cmbProduct.get(i).getValue()!= null && (!QtyKg.get(i).getValue().toString().isEmpty() || !QtyPcs.get(i).getValue().toString().isEmpty()) )
				{

					


					sqlDetails="insert into tbRejectedItemReceipDetails(iReceiptNo,vProductId,vProductName,vUnitName,mQtyKg, "
							   +"mQtyPcs,mWastageQtyKg,mWastageQtyPcs,vRemarks,vUserIp,vuserId,dEntryTime,storeId)"
							   + " values('"+txtreceiptNo.getValue()+"','"+cmbProduct.get(i).getValue()+"',"
							   	+ " '"+cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())+"',"
							   	+ " '"+lblUnit.get(i).getValue()+"','"+QtyKg.get(i).getValue().toString().replaceAll(",", "")+"',"
							   	+ " '"+QtyPcs.get(i).getValue()+"','"+wastageQtyKg.get(i).getValue()+"','"+wastageQtyPcs.get(i).getValue()+"',"
							   	+ " '"+Remarks.get(i).getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',getdate(),'"+cmbStore.get(i).getValue()+"' )  ";


					System.out.println(sqlDetails);
					session.createSQLQuery(sqlDetails).executeUpdate();

					/*if(isUpdate){
						sqlUdDetails="insert into tbUdThirdPartyItemReceiptDetails "
								+ "(iTransectionId, vReceiptNo, vChallanNo, vProductId, vProductName, vUnit, mQty, vRemarks,vUdFlag,mRate,mAmount)"
								+ " values('"+transectionId+"','"+txtreceiptNo.getValue().toString()+"',"
								+ " '"+txtchallanNo.getValue().toString()+"','"+productId+"','"+ProductName+"',"
								+ "'"+unit.get(i).getValue()+"', '"+Qty.get(i).getValue()+"', '"+Remarks.get(i).getValue()+"',"
								+ " 'New',0,0) ";
						System.out.println(sqlUdDetails);
						session.createSQLQuery(sqlUdDetails).executeUpdate();
					}*/
				}
			}
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception exp){
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private String Option(){
		strFlag="debit";
		if(strFlag.equalsIgnoreCase("debit"))
			strFlag= "RDP";
		else
			strFlag= "RCP";

		return strFlag;
	}

	private void findButtonEvent()
	{
		Window win=new RejectedItemRcvFind(sessionBean,txtFindreceiptNo);
		win.addListener(new Window.CloseListener() {
			public void windowClose(CloseEvent e) {
				if(txtFindreceiptNo.getValue().toString().length()>0){
					txtClear();
					findInitialise(txtFindreceiptNo.getValue().toString());
					isFind=true;
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String receiptNo){
		try
		{
			String imgcap="";

			String sql=
					"select a.iReceiptNo,a.dReceiptDate,a.vchallanNo,a.dChallanDate, "
					+"b.vProductId,b.vProductName,b.vUnitName,b.mQtyKg, "
					+"b.mQtyPcs,b.mWastageQtyKg,b.mWastageQtyPcs, "
					+"b.vRemarks,b.storeId from tbRejectedReceiptInfo a inner join tbRejectedItemReceipDetails b "
					+"on a.iReceiptNo=b.iReceiptNo where a.iReceiptNo='"+receiptNo+"' ";

			Iterator<?> iter=dbService(sql);
			int i=0;
			while(iter.hasNext()){
				Object[] element = (Object[]) iter.next();
				
				if(i==0)
				{
					txtreceiptNo.setValue(element[0]);
					dateField.setValue(element[1]);
					txtchallanNo.setValue(element[2]);
					dChallanDate.setValue(element[3]);

				}
				cmbProduct.get(i).setValue(element[4]);
				lblUnit.get(i).setValue(element[6]);
				QtyKg.get(i).setValue( new CommaSeparator().setComma(Double.parseDouble(element[7].toString()) ));
				QtyPcs.get(i).setValue( new CommaSeparator().setComma(Double.parseDouble(element[8].toString()) ));
				
				wastageQtyKg.get(i).setValue( new CommaSeparator().setComma(Double.parseDouble(element[9].toString()) ));
				wastageQtyPcs.get(i).setValue( new CommaSeparator().setComma(Double.parseDouble(element[10].toString()) ));
				cmbStore.get(i).setValue(element[12].toString());
				Remarks.get(i).setValue(element[11]);
				i++;

			}
		}
		catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}


}
