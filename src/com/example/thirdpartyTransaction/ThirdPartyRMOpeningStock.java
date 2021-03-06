package com.example.thirdpartyTransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.AmountField;
import com.common.share.BtUpload;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.text.DecimalFormat;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyRMOpeningStock extends Window 
{
	private CommonButtonNew button = new CommonButtonNew( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "Exit","","");
	private VerticalLayout btnLayout = new VerticalLayout();

	@AutoGenerated
	private AbsoluteLayout mainLayout;

	@AutoGenerated
	private InlineDateField dOpeningYear;
	@AutoGenerated
	private Label lblOpeningYear;

	@AutoGenerated
	private Label lblProductName;
	@AutoGenerated
	private ComboBox cmbProductName;
	private ComboBox cmbgroupName;
	private ComboBox cmbsubgroupName;

	private Label lblgroupName;
	private Label lblsubgroupName;

	@AutoGenerated
	private Label lblProductId;
	@AutoGenerated
	private TextRead trProductId;

	private Label lblStore;
	private ComboBox cmbStore;

	@AutoGenerated
	private Label lblUnit;
	@AutoGenerated
	private TextRead trUnit;

	private Label lblcategoryType;
	private ComboBox cmbcategorytype;
	
	private Label lblPartyName;
	private ComboBox cmbPartyName;

	@AutoGenerated
	private Label lblQuantity;
	@AutoGenerated
	private AmountField amtQuantity;

	

	@AutoGenerated
	private Label lblline;

	boolean isUpdate=false;
	int index;

	private TextRead txtCategoryID = new TextRead();
	private TextRead txtSubCategoryID = new TextRead();

	private TextField txtProductID = new TextField();
	private TextField txtOpeningYear = new TextField();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dYFormat = new SimpleDateFormat("yyyy");
	ArrayList<Component> allComp = new ArrayList<Component>();

	private DecimalFormat df = new DecimalFormat("#0.00");

	private Formatter fmt = new Formatter();

	private SessionBean sessionBean;
	

	public ThirdPartyRMOpeningStock(SessionBean sessionBean)
	{
		buildMainLayout();
		this.sessionBean = sessionBean;
		this.setCaption("OPENING STOCK INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.setContent(mainLayout);
		buttonLayoutAdd();
		btnIni(true);
		componentIni(true);
		updateBtnFileldED(true);
		setEventAction();
		categorytypeload();	
		cmbStoreload();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(dOpeningYear);
		allComp.add(cmbcategorytype);
		allComp.add(cmbgroupName);
		allComp.add(cmbsubgroupName);
		allComp.add(cmbPartyName);
		allComp.add(cmbProductName);
		allComp.add(cmbStore);
		allComp.add(amtQuantity);
		

		allComp.add(button.btnNew);
		allComp.add(button.btnUpdate);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnDelete);
		allComp.add(button.btnFind);

		new FocusMoveByEnter(this,allComp);
	}
	
	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				focusEnter();
				cmbcategorytype.focus();
				newButtonEvent();
				categorytypeload();
			}
		});

		button.btnUpdate.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable()){
					updateButtonEvent();
				}else{
					getParent().showNotification("You are not Permitted to Update", Notification.TYPE_WARNING_MESSAGE);	
				}
			}
		});

		button.btnDelete.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) 
			{
				
				txtClear();
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isSubmitable()){
					saveButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Save",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
				
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event){ 
				refreshButtonEvent();
				
			}
		});

		cmbgroupName.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbgroupName.getValue()!=null)
				{
					partyLoad();
					subCateGoryDataAdd();
				}

			}
		});


		cmbsubgroupName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbsubgroupName.getValue()!=null)
				{
					partyLoad();
					
				}
			}
		});
		
		
		cmbPartyName.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
			  if(cmbPartyName.getValue()!=null && cmbgroupName.getValue()!=null)
			  {
				 productLoad(); 
			  }
				
			}
		});
		

		
		cmbcategorytype.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbcategorytype.getValue()!=null)
				{
					cmbCategoryName();
				}

			}
		});

		cmbProductName.addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event)
			{
				prodataAction();

			}
		});


		

	}

	private void updateButtonEvent()
	{
		if(cmbProductName.getValue()!= null)
		{
			isUpdate = true;
			btnIni(false);
			updateinit(true);
		}
		else
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
	}

	private void findButtonEvent() 
	{
		Window win = new ThirdPartyOPenigStockFind(sessionBean, txtProductID,txtOpeningYear);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtProductID.getValue().toString().length() > 0)
				{
					txtClear();
					cmbProductName.removeAllItems();
					findInitialise(txtProductID.getValue().toString(),txtOpeningYear.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtProductId,String txtOpeningyear) 
	{


		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			
			String sql=  "select a.openingYear, b.vCategoryType,b.vGroupId, b.vSubGroupId ,a.productId,b.vItemName ,a.qty,a.rate ,a.amount,a.storeId,a.partyId from tbThirdPartyOpening a " 
					     +"inner join "
					     +"tbThirdPartyItemInfo b "
					     +"on a.productId=b.vCode "
					     +" where a.productId like '"+txtProductId+"' and  YEAR(a.openingYear) like '"+txtOpeningyear+"' "; 

			
			System.out.println("query is"+sql);

			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object element[]=(Object[]) iter.next();

				dOpeningYear.setValue(element[0]);
				cmbcategorytype.setValue(element[1].toString());
				cmbgroupName.setValue(element[2]);
				cmbsubgroupName.setValue(element[3]);
				cmbPartyName.setValue(element[10].toString());
				cmbProductName.addItem(element[4].toString());
				cmbProductName.setItemCaption(element[4].toString(), element[5].toString());
				cmbProductName.setValue(element[4]);
				trProductId.setValue(element[4]);
				fmt=new Formatter();
				amtQuantity.setValue(fmt.format("%.2f",Double.parseDouble(element[6].toString().trim())));
				fmt=new Formatter();
				fmt=new Formatter();
				cmbStore.setValue(element[9].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void saveButtonEvent()
	{
		System.out.print(cmbProductName.getValue());

		if(cmbcategorytype.getValue()!=null)
		{	
			if(cmbgroupName.getValue()!=null)
			{
				if(cmbProductName.getValue()!=null || isUpdate)
				{
					if(cmbStore.getValue()!=null)
					{
						System.out.print("ok");
						if(!trUnit.getValue().toString().trim().isEmpty())
						{
							if(!amtQuantity.getValue().toString().trim().isEmpty())
							{
								
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
													updateData();
													isUpdate=false;
													componentIni(true);
													updateBtnFileldED(true);
													txtClear();
													btnIni(true);
													button.btnNew.focus();
													mb.close();
												
												
											}
										}
									});	
								}
								else
								{
									final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
									mb.show(new EventListener()
									{
										public void buttonClicked(ButtonType buttonType)
										{
											if(buttonType == ButtonType.YES)
											{	
												
													mb.buttonLayout.getComponent(0).setEnabled(false);
													insertData();
													btnIni(true);
													componentIni(true);
													txtClear();
													mb.close();
												
											}
										}
									});	
								}
							}
							else{
								this.getParent().showNotification("Warning !","Enter Quantity.", Notification.TYPE_WARNING_MESSAGE);
								amtQuantity.focus();
							}
						}
						else{
							this.getParent().showNotification("Warning !","Enter Unit.", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						this.getParent().showNotification("Warning !","Select Store Name.", Notification.TYPE_WARNING_MESSAGE);
						cmbStore.focus();
					}  	
				}
				else{
					this.getParent().showNotification("Warning !","Select Product Name.", Notification.TYPE_WARNING_MESSAGE);
					cmbProductName.focus();
				}  	
			}
			else
			{
				showNotification("Please Select Category Name",Notification.TYPE_WARNING_MESSAGE);
			}	
		}
		else
		{
			showNotification("Please Select Item Type",Notification.TYPE_WARNING_MESSAGE);
		}

	}


	public void updateData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
			WebBrowser webBrowser = context.getBrowser();
			sessionBean.setUserIp(webBrowser.getAddress());    

			InetAddress inetAddress =InetAddress.getByName(webBrowser.getAddress().toString());//get the host Inet using ip

			String updateQOS = "Update tbThirdPartyOpening set Qty = '"+amtQuantity.getValue().toString().trim()+"', " +
					"Rate = '0',vUserName = '"+sessionBean.getUserName()+"', storeId='"+cmbStore.getValue()+"', " +
					"vUserIP = '"+inetAddress.getHostName()+"'," +
					"dtEntryTime = CURRENT_TIMESTAMP, amount='0'," +
					"openingYear = '"+dateFormat.format(dOpeningYear.getValue())+"' WHERE ProductID = '"+cmbProductName.getValue()+"'";

		
			session.createSQLQuery(updateQOS).executeUpdate();

			
			tx.commit();
			this.getParent().showNotification("All information update successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}


	private void insertData()
	{
		Transaction tx = null;
		
	
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
			WebBrowser webBrowser = context.getBrowser();
			sessionBean.setUserIp(webBrowser.getAddress());    

			InetAddress inetAddress =InetAddress.getByName(webBrowser.getAddress().toString());//get the host Inet using ip
			System.out.println ("Host Name: "+ inetAddress.getHostName());//display the host

			String insertProductOpening = "Insert Into tbThirdPartyOpening(ProductId, Qty, Rate,vUserName,vUserIP,dtEntryTime,amount,openingYear,storeId,partyId) " +
					" values('"+cmbProductName.getValue()+"','"+amtQuantity.getValue().toString().trim()+"'," +
					" '0'," +
					" '"+sessionBean.getUserName()+"','"+inetAddress.getHostName()+"',CURRENT_TIMESTAMP,'0'," +
					" '"+dateFormat.format(dOpeningYear.getValue())+"','"+cmbStore.getValue()+"','"+cmbPartyName.getValue()+"')";

			
			System.out.println("insertProductInfo : "+insertProductOpening);

			session.createSQLQuery(insertProductOpening).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information save successfully.");

		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	
	private void refreshButtonEvent()
	{
		componentIni(true);
		updateBtnFileldED(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		updateBtnFileldED(false);
		btnIni(false);
		txtClear();
		isUpdate = false;
	}

	public void txtClear()
	{
		cmbPartyName.setValue(null);
		cmbProductName.setValue(null);
		trProductId.setValue("");
		cmbStore.setValue(null);
		txtCategoryID.setValue("");
		cmbgroupName.setValue(null);
		cmbsubgroupName.setValue(null);
		trUnit.setValue("");
		amtQuantity.setValue("");
		
		cmbcategorytype.setValue(null);
	}



	public void subCateGoryDataAdd()
	{
		cmbsubgroupName.removeAllItems();
		Transaction tx = null;
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String squery=" select vSubGroupId,vSubGroupName from tbThirdPartyItemInfo where vGroupId='"+cmbgroupName.getValue()+"' ";
			System.out.println("squery : "+squery);
			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbsubgroupName.addItem(element[0].toString());
				cmbsubgroupName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}



	public void partyLoad()
	{
		cmbPartyName.removeAllItems();
		Transaction tx = null;
		String groupid="";
		String subgroupid="";
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			if(cmbgroupName.getValue()!=null)
			{
				groupid=cmbgroupName.getValue().toString();

			}
			
			if(cmbsubgroupName.getValue()!=null)
			{
				subgroupid=cmbsubgroupName.getValue().toString();
			}
			else
			{
				subgroupid="%";	
			}

			String squery="select vPartyId,vPartyName  from tbThirdPartyItemInfo where vGroupId='"+cmbgroupName.getValue()+"' and vSubGroupId='"+subgroupid+"'   ";
			System.out.println("squery : "+squery);
			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0]);
				cmbPartyName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}



	public void cmbCategoryName()
	{
		cmbgroupName.removeAllItems();
		Transaction tx = null;
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String squery="select vGroupId,vGroupName from tbThirdPartyItemInfo where vCategoryType='"+cmbcategorytype.getValue()+"' ";
			System.out.println("squery : "+squery);
			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbgroupName.addItem(element[0].toString());
				cmbgroupName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void productLoad()
	{
		cmbProductName.removeAllItems();
		
		String groupId="";
		String subGroupId="";
		String partyId="";
		
		if(cmbgroupName.getValue()!=null)
		{
			groupId=cmbgroupName.getValue().toString();	
		}
		
		if(cmbsubgroupName.getValue()!=null)
		{
			subGroupId=cmbsubgroupName.getValue().toString();
		}
		else
		{
			subGroupId="%";	
		}
		
		partyId=cmbPartyName.getValue().toString();
		
		Transaction tx = null;
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			
			String query="select vCode,vItemName from tbThirdPartyItemInfo where vGroupId like '"+groupId+"' and vSubGroupId like '"+subGroupId+"'  "
					     +"and vPartyId like '"+partyId+"' and vCode not in  "
					     +"(select productId from tbThirdPartyOpening where YEAR(openingYear)='"+dYFormat.format(dOpeningYear.getValue())+"' "
					     +") "; 
			
			List list = session.createSQLQuery(query).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	

	public void prodataAction()
	{
		Transaction tx = null;
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			
			String squery="select vCode,vUnitName from tbThirdPartyItemInfo where vCode='"+cmbProductName.getValue()+"' ";
			
			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				trProductId.setValue(element[0].toString());
				trUnit.setValue(element[1].toString());

			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void categorytypeload()
	{
		cmbcategorytype.removeAllItems();
		Transaction tx = null;
		try {
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String squery="select 0,vCategoryType from tbThirdPartyItemInfo";
			System.out.println("squery : "+squery);
			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbcategorytype.addItem(element[1]);
				cmbcategorytype.setItemCaption(element[1], element[1].toString());
			}
		}
		catch (Exception ex){
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbStoreload()
	{
		cmbStore.removeAllItems();

		Transaction tx = null;

		try 
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String squery= "select vDepoId, vDepoName from tbDepoInformation order by vDepoName";
			System.out.println("squery : "+squery);

			List list = session.createSQLQuery(squery).list();

			for (Iterator iter = list.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbStore.addItem(element[0].toString());
				cmbStore.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void updateBtnFileldED(boolean b)
	{
		lblQuantity.setEnabled(!b);
		amtQuantity.setEnabled(!b);

		lblline.setEnabled(!b);
	}

	private void componentIni(boolean b) 
	{
		lblOpeningYear.setEnabled(!b);
		dOpeningYear.setEnabled(!b);

		lblProductName.setEnabled(!b);
		cmbProductName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);

		lblProductId.setEnabled(!b);
		trProductId.setEnabled(!b);
		lblStore.setEnabled(!b);
		cmbStore.setEnabled(!b);
		lblgroupName.setEnabled(!b);
		cmbgroupName.setEnabled(!b);
		lblsubgroupName.setEnabled(!b);
		cmbsubgroupName.setEnabled(!b);
		cmbcategorytype.setEnabled(!b);
		lblcategoryType.setEnabled(!b);
		lblUnit.setEnabled(!b);
		trUnit.setEnabled(!b);

		lblQuantity.setEnabled(!b);
		amtQuantity.setEnabled(!b);

		lblline.setEnabled(!b);
	}

	private void updateinit(boolean b) 
	{
		lblOpeningYear.setEnabled(b);
		dOpeningYear.setEnabled(b);

		lblProductName.setEnabled(!b);
		cmbProductName.setEnabled(!b);

		lblProductId.setEnabled(!b);
		trProductId.setEnabled(!b);

		lblStore.setEnabled(!b);
		cmbStore.setEnabled(!b);

		lblgroupName.setEnabled(!b);
		cmbgroupName.setEnabled(!b);

		lblsubgroupName.setEnabled(!b);
		cmbsubgroupName.setEnabled(!b);
		cmbcategorytype.setEnabled(!b);

		lblUnit.setEnabled(!b);
		trUnit.setEnabled(!b);

		lblQuantity.setEnabled(b);
		amtQuantity.setEnabled(b);

		

		lblline.setEnabled(!b);
	}





	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnUpdate.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("566px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("570px");
		setHeight("454px");

		// lblOpeningYear
		lblOpeningYear = new Label();
		lblOpeningYear.setImmediate(false);
		lblOpeningYear.setWidth("-1px");
		lblOpeningYear.setHeight("-1px");
		lblOpeningYear.setValue("Opening Year :");


		// dOpeningYear
		dOpeningYear = new InlineDateField();
		dOpeningYear.setImmediate(true);
		dOpeningYear.setDateFormat("yyyy");
		dOpeningYear.setWidth("-1px");
		dOpeningYear.setHeight("-1px");
		dOpeningYear.setInvalidAllowed(false);
		dOpeningYear.setResolution(6);


		lblcategoryType = new Label();
		lblcategoryType.setImmediate(false);
		lblcategoryType.setWidth("-1px");
		lblcategoryType.setHeight("-1px");
		lblcategoryType.setValue("Item Type :");


		// category Type
		cmbcategorytype = new ComboBox();
		cmbcategorytype.setImmediate(true);
		cmbcategorytype.setNullSelectionAllowed(false);
		cmbcategorytype.setNewItemsAllowed(false);
		cmbcategorytype.setWidth("280px");
		cmbcategorytype.setHeight("-1px");
		cmbcategorytype.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblgroupName = new Label();
		lblgroupName.setImmediate(false);
		lblgroupName.setWidth("-1px");
		lblgroupName.setHeight("-1px");
		lblgroupName.setValue("Category Name:");


		cmbgroupName = new ComboBox();
		cmbgroupName.setImmediate(true);
		cmbgroupName.setNullSelectionAllowed(false);
		cmbgroupName.setNewItemsAllowed(false);
		cmbgroupName.setWidth("280px");
		cmbgroupName.setHeight("-1px");
		cmbgroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblsubgroupName = new Label();
		lblsubgroupName.setImmediate(false);
		lblsubgroupName.setWidth("-1px");
		lblsubgroupName.setHeight("-1px");
		lblsubgroupName.setValue("SubCategory Name:");



		cmbsubgroupName = new ComboBox();
		cmbsubgroupName.setImmediate(true);
		cmbsubgroupName.setNullSelectionAllowed(false);
		cmbsubgroupName.setNewItemsAllowed(false);
		cmbsubgroupName.setWidth("280px");
		cmbsubgroupName.setHeight("-1px");
		cmbsubgroupName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		// lblProductList
		lblProductName = new Label();
		lblProductName.setImmediate(false);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Product Name:");


		// comProductName
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setNullSelectionAllowed(false);
		cmbProductName.setNewItemsAllowed(false);
		cmbProductName.setWidth("300px");
		cmbProductName.setHeight("-1px");
		cmbProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		// lblProductId
		lblProductId = new Label();
		lblProductId.setImmediate(false);
		lblProductId.setWidth("-1px");
		lblProductId.setHeight("-1px");
		lblProductId.setValue("Product ID:");


		// trProductId
		trProductId = new TextRead();
		trProductId.setImmediate(false);
		trProductId.setWidth("100px");
		trProductId.setHeight("22px");

		// lblStore
		lblStore = new Label();
		lblStore.setImmediate(false);
		lblStore.setWidth("-1px");
		lblStore.setHeight("-1px");
		lblStore.setValue("Store:");

		// cmbStore
		cmbStore = new ComboBox();
		cmbStore.setImmediate(true);
		cmbStore.setNullSelectionAllowed(false);
		cmbStore.setNewItemsAllowed(false);
		cmbStore.setWidth("280px");
		cmbStore.setHeight("-1px");
		cmbStore.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		// lblStore
		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name:");

		// cmbStore
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setNullSelectionAllowed(false);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setWidth("280px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		
		// lblUnit
		lblUnit = new Label();
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		lblUnit.setValue("Unit :");


		// trUnit
		trUnit = new TextRead();
		trUnit.setImmediate(false);
		trUnit.setWidth("100px");
		trUnit.setHeight("22px");


		// lblQuantity
		lblQuantity = new Label();
		lblQuantity.setImmediate(false);
		lblQuantity.setWidth("-1px");
		lblQuantity.setHeight("-1px");
		lblQuantity.setValue("Quantity :");


		// amtQuantity
		amtQuantity = new AmountField();
		amtQuantity.setImmediate(true);
		amtQuantity.setWidth("102px");
		amtQuantity.setHeight("22px");


		


		lblline = new Label();
		lblline.setImmediate(false);
		lblline.setWidth("-1");
		lblline.setHeight("-1");
		lblline.setValue("________________________________________________________________________");

		// adding components to mainLayout

		mainLayout.addComponent(lblOpeningYear, "top:32.0px;right:380.0px;");
		mainLayout.addComponent(dOpeningYear, "top:31.0px;left:194.0px;");

		mainLayout.addComponent(lblcategoryType, "top:60.0px;right:380.0px;");
		mainLayout.addComponent(cmbcategorytype, "top:57.0px;left:194.0px;");

		mainLayout.addComponent(lblgroupName, "top:86.0px;right:380.0px;");
		mainLayout.addComponent(cmbgroupName, "top:83.0px;left:194.0px;");

		mainLayout.addComponent(lblsubgroupName, "top:112.0px;right:380.0px;");
		mainLayout.addComponent(cmbsubgroupName, "top:109.0px;left:194.0px;");

		mainLayout.addComponent(lblPartyName, "top:138.0px;right:380.0px;");
		mainLayout.addComponent(cmbPartyName, "top:135.0px;left:194.0px;");

		mainLayout.addComponent(lblProductName, "top:164.0px;right:380.0px;");
		mainLayout.addComponent(cmbProductName, "top:161.0px;left:194.0px;");

		mainLayout.addComponent(lblProductId, "top:190.0px;right:380.0px;");
		mainLayout.addComponent(trProductId, "top:187.0px;left:195.0px;");

		mainLayout.addComponent(lblStore, "top:216.0px;right:380.0px;");
		mainLayout.addComponent(cmbStore, "top:213.0px;left:195.0px;");

		mainLayout.addComponent(lblUnit, "top:242.0px;right:380.0px;");
		mainLayout.addComponent(trUnit, "top:239.0px;left:193.5px;");

		mainLayout.addComponent(lblQuantity,"top:268.0px;right:380.0px;");
		mainLayout.addComponent(amtQuantity,"top:265.0px;left:193.5px;");

		
		mainLayout.addComponent(lblline,"top:330.0px; left:18.0px;");

		return mainLayout;
	}

	private void buttonLayoutAdd()
	{
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout,"top:360px;left:15px;");
		
	}

}
