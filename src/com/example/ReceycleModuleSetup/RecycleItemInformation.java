package com.example.ReceycleModuleSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FileUpload;
import com.common.share.FileUploadItemInfo;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.rawMaterialSetup.RawItemCategory;
import com.example.rawMaterialSetup.RawItemInfoFind;
import com.example.rawMaterialSetup.RawItemSubCategory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;

public class RecycleItemInformation extends Window 
{

	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dDate;


	private Label lblItemCode;
	private TextRead txtItemCode;


	private Label lblItemName;
	private TextField txtItemName;


	private Label lblcategory;
	private ComboBox cmbCategory;	


	private Label lblsubcategory;
	private ComboBox cmbsubcategory;



	private Label lblUnit;
	private TextField txtUnit;


	private ComboBox cmbBaseProduct;
	private Label lblbaseProduct;


	private DecimalFormat df = new DecimalFormat("#0.00");

	String RawItemId="";
	String LedgerId="";
	private TextRead ledgerCode = new TextRead();

	boolean isUpdate=false;
	boolean isFind=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
	private SimpleDateFormat dateF=new SimpleDateFormat("yyyy-MM-dd");

	private TextField txtmodelNo=new TextField();
	private Label lblModelNo=new Label();

	private TextField txtcolor=new TextField();
	private Label lblcolor=new Label();

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	String FindItemName="";

	public RecycleItemInformation(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("RECYCLED ITEM INFORMATION :: "+sessionBean.getCompany());
		setContent(buildMainLayout());
		btnIni(true);
		componentIni(true);
		CategoryDataLoad();
		BaseProductLoad();
		setEventAction();
		focusEnter();
		authenticationCheck();

	}	


	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}
		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}
		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	


	public void setEventAction()
	{


		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;
				newButtonEvent();
				
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				formValidation();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});



		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				findButtonEvent();
			}
		});

		cmbCategory.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbCategory.getValue()!=null)
				{
					subcategoryData();
				}
				else
				{
					cmbsubcategory.removeAllItems();
				}
			}
		});
		 

		txtItemName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				Transaction tx = null;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				try
				{
					if(!txtItemName.getValue().toString().isEmpty())
					{
						String query = "select * from tbRecycleItemInfo where vItemName like '"+txtItemName.getValue().toString()+"'";
						Iterator iter = session.createSQLQuery(query).list().iterator();
						if (iter.hasNext())
						{
							if(!FindItemName.equalsIgnoreCase(txtItemName.getValue().toString()))
							{
								showNotification("Item Name Already Exists :"+txtItemName.getValue().toString());
								txtItemName.setValue("");
							}
							else
							{
								System.out.println("This Is Update");	
							}
						}	
					}
				}
				catch(Exception ex)
				{
					System.out.print(ex);	
				}

			}
		});

		;


	}

	private String selectItemCode()
	{
		String itemcode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select'RRM-'+ CAST(ISNULL(MAX(CAST(SUBSTRING(vItemCode,5,LEN(vItemCode)) as int) ),0)+1 as varchar(120))     from tbRecycleItemInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				itemcode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return itemcode;
	}

	public void CategoryDataLoad()
	{
		cmbCategory.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select iCategoryCode,vCategoryName from tbRawItemCategory").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategory.addItem(element[0].toString());
				cmbCategory.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void BaseProductLoad()
	{
		cmbBaseProduct.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String query= "select semiFgCode,semiFgName from tbSemiFgInfo "
					+"union "
					+"select semiFgSubId,semiFgSubName from tbSemiFgSubInformation ";

			List list=session.createSQLQuery(query).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBaseProduct.addItem(element[0].toString());
				cmbBaseProduct.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void subcategoryData()
	{
		cmbsubcategory.removeAllItems();
		//	int i=0;
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			
			String sql= "select iSubCategoryID,vSubCategoryName from tbRawItemSubCategory where Group_Id='"+cmbCategory.getValue()+"' ";
			
			List list=session.createSQLQuery(sql).list();

			//	i=1;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbsubcategory.addItem(element[0].toString());
				cmbsubcategory.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		
	}



	private void updateButtonEvent()
	{
		if(!txtItemName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(sessionBean.isSubmitable())
		{
			if(!txtItemCode.getValue().toString().isEmpty())
			{
				if(!txtItemName.getValue().toString().isEmpty())
				{
					if(cmbCategory.getValue()!=null)
					{
						/*if(cmbBaseProduct.getValue()!=null)
						{*/
							if(!txtUnit.getValue().toString().isEmpty())
							{

								saveButtonEvent();		


							}
							else
							{
								getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
								txtUnit.focus();
							}	
						/*}
						else
						{
							getParent().showNotification("Warning","Please Select Base Product",Notification.TYPE_WARNING_MESSAGE);
							cmbBaseProduct.focus();	
						}*/
						
						
					}
					else
					{
						getParent().showNotification("Warning","Please select category",Notification.TYPE_WARNING_MESSAGE);
						cmbCategory.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtItemName.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent() 
	{
		Window win = new RecycleItemFind(sessionBean, txtItemID,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

		private void findInitialise(String txtItemId) 
	{
		Transaction tx = null;
		String sql = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql= "select vItemCode,vItemName,iCategoryId,vCategoryName,iSubCategoryId,vSubCategoryName, "
				 +"vBaseProductId,vBaseProductName,vModelNo,vUnitName,vColor,vUserIp,vUserId,dEntryTime,vLedgerCode from tbRecycleItemInfo "
				 +"where vItemCode='"+txtItemId+"' ";

			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtItemCode.setValue(element[0]);
				FindItemName=element[1].toString();
				txtItemName.setValue(element[1]);
				
				cmbCategory.setValue(element[2].toString());
				cmbsubcategory.setValue(element[4].toString());
				
				cmbBaseProduct.setValue(element[6]);
				
				txtmodelNo.setValue(element[8]);
				txtUnit.setValue(element[9]);
				txtcolor.setValue(element[10]);
			
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	
	
		private void saveButtonEvent()
	{
		
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						updateData();
						isUpdate = false;
						isFind=false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						mb.close();
					}
				}
			});																	
		}
		else
		{									
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
						isUpdate = false;
						isFind=false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						mb.close();
					}
				}
			});
		}
	}
	 


	public String AutoledgerId() 
	{
		String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select cast(isnull(max(cast(replace(Ledger_Id, 'AL', '')as int))+1, 1)as varchar)" +
					" from tbLedger where Ledger_Id like 'AL%' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "AL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return ledgerId;
	}

	
	private void insertData()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String ModelNo="";
		String categoryId="";
		String categoryName="";
		
		String subcategoryId="";
		String subcategoryName="";
		
		String baseProductId="";
		String baseProductName="";
		String color="";
		String unitName="";
		String ItemName="";
		
		
		
		txtItemCode.setValue(selectItemCode());


		if(!txtmodelNo.getValue().toString().isEmpty())
		{
			ModelNo=txtmodelNo.getValue().toString();
		}
		
		if(!txtcolor.getValue().toString().isEmpty())
		{
			color=txtcolor.getValue().toString();
		}
		
		if(!txtUnit.getValue().toString().isEmpty())
		{
			unitName=txtUnit.getValue().toString();
		}
		if(!txtItemName.getValue().toString().isEmpty())
		{
			ItemName=txtItemName.getValue().toString();
		}
		
		if(cmbCategory.getValue()!=null)
		{
		  categoryId=cmbCategory.getValue().toString();
		  categoryName=cmbCategory.getItemCaption(cmbCategory.getValue());
		}

		if(cmbsubcategory.getValue()!=null)
		{
			subcategoryId=cmbsubcategory.getValue().toString();
			subcategoryName=cmbsubcategory.getItemCaption(cmbsubcategory.getValue());
		}
		
		
		if(cmbBaseProduct.getValue()!=null)
		{
			baseProductId=cmbBaseProduct.getValue().toString();
			baseProductName=cmbBaseProduct.getItemCaption(cmbBaseProduct.getValue());
		}
		
		String ledgerId=AutoledgerId();
		
		try
		{
			String sql= "insert into tbRecycleItemInfo (vItemCode,vItemName,iCategoryId,vCategoryName,iSubCategoryId,vSubCategoryName, "
					    +"vBaseProductId,vBaseProductName,vModelNo,vUnitName,vColor,vUserIp,vUserId,dEntryTime,vLedgerCode,dDate) "
					    +"values('"+txtItemCode.getValue().toString()+"','"+ItemName+"','"+categoryId+"','"+categoryName+"',"
					    + " '"+subcategoryId+"','"+subcategoryName+"','"+baseProductId+"','"+baseProductName+"',"
					    + " '"+ModelNo+"','"+unitName+"','"+color+"','"+sessionBean.getUserIp()+"',"
					    + "  '"+sessionBean.getUserId()+"',getdate(),'"+ledgerId+"','"+dateF.format(dDate.getValue())+"'  ) ";


			System.out.println("Insertquery : "+sql);
			session.createSQLQuery(sql).executeUpdate();
			
			
			String parentId="G548";
			String createForm="A5-G548";
			
			String InsertLedger="INSERT into tbLedger values(" +
					" '"+ledgerId+"', " +
					" '"+txtItemName.getValue().toString().trim().replaceAll("'","~")+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("InsertLedger : "+InsertLedger);
			session.createSQLQuery(InsertLedger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+ledgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("LedgerOpen : "+LedgerOpen);
			session.createSQLQuery(LedgerOpen).executeUpdate();
			


			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	
	public String ledgerId() 
	{
		String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select cast(isnull(max(cast(replace(Ledger_Id, 'AL', '')as int))+1, 1)as varchar)" +
					" from tbLedger where Ledger_Id like 'AL%' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "AL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return ledgerId;
	}
	

		private boolean updateData()
	{

			String ModelNo="";
			String categoryId="";
			String categoryName="";
			
			String subcategoryId="";
			String subcategoryName="";
			
			String baseProductId="";
			String baseProductName="";
			String color="";
			String unitName="";
			String ItemName="";
			
			
			if(!txtmodelNo.getValue().toString().isEmpty())
			{
				ModelNo=txtmodelNo.getValue().toString();
			}
			
			if(!txtcolor.getValue().toString().isEmpty())
			{
				color=txtcolor.getValue().toString();
			}
			
			if(!txtUnit.getValue().toString().isEmpty())
			{
				unitName=txtUnit.getValue().toString();
			}
			if(!txtItemName.getValue().toString().isEmpty())
			{
				ItemName=txtItemName.getValue().toString();
			}
			
			if(cmbCategory.getValue()!=null)
			{
			  categoryId=cmbCategory.getValue().toString();
			  categoryName=cmbCategory.getItemCaption(cmbCategory.getValue());
			}

			if(cmbsubcategory.getValue()!=null)
			{
				subcategoryId=cmbsubcategory.getValue().toString();
				subcategoryName=cmbsubcategory.getItemCaption(cmbsubcategory.getValue());
			}
			
			
			if(cmbBaseProduct.getValue()!=null)
			{
				baseProductId=cmbBaseProduct.getValue().toString();
				baseProductName=cmbBaseProduct.getItemCaption(cmbBaseProduct.getValue());
			}

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			
			String updateRecycleItem=
					"update tbRecycleItemInfo set vItemName='"+ItemName+"',iCategoryId='"+categoryId+"',vCategoryName='"+categoryName+"', "
					+"iSubCategoryId='"+subcategoryId+"',vSubCategoryName='"+subcategoryName+"', vBaseProductId='"+baseProductId+"', "
					+"vBaseProductName='"+baseProductName+"',vModelNo='"+ModelNo+"',vUnitName='"+unitName+"',vColor='"+color+"', "
					+"vUserIp='"+sessionBean.getUserIp()+"',vUserId='"+sessionBean.getUserId()+"',dEntryTime=getdate() where vItemCode='"+txtItemCode.getValue().toString()+"' ";

			
			System.out.println(updateRecycleItem);
			session.createSQLQuery(updateRecycleItem).executeUpdate();
			
			
			String UpdateLedger = "UPDATE tbLedger set" +
					" Ledger_Name = '"+ItemName+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Ledger_Id='"+ledgerCode.getValue()+"'";

			//System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();
			
			

			this.getParent().showNotification("All information update successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		txtItemCode.setValue(selectItemCode());
		txtItemName.focus();

	}

	private void focusEnter()
	{
		allComp.add(dDate);
		allComp.add(txtItemName);
		
		allComp.add(cmbCategory);
		allComp.add(cmbsubcategory);
		allComp.add(cmbBaseProduct);
		allComp.add(txtUnit);
		allComp.add(txtmodelNo);
		allComp.add(txtcolor);
		
		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		dDate.setValue(new Date());
		txtItemCode.setValue("");
		txtItemName.setValue("");
		cmbCategory.setValue(null);
		cmbsubcategory.setValue(null);
		cmbBaseProduct.setValue(null);
		txtmodelNo.setValue("");
		txtUnit.setValue("");
		txtcolor.setValue("");
		FindItemName="";
		
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);		
	}

	private void componentIni(boolean b) 
	{
		dDate.setEnabled(!b);
		txtItemCode.setEnabled(!b);
		txtItemName.setEnabled(!b);
		cmbCategory.setEnabled(!b);
		cmbsubcategory.setEnabled(!b);
		cmbBaseProduct.setEnabled(!b);
		txtUnit.setEnabled(!b);
		txtmodelNo.setEnabled(!b);
		txtcolor.setEnabled(!b);

	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("600px");
		setHeight("420px");


		lblDate = new Label("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:50.0px;");

		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:20.0px; left:190.0px;");



		// lblRawItemCode
		lblItemCode = new Label("Item Code: ");
		lblItemCode.setImmediate(true);
		lblItemCode.setWidth("100.0%");
		lblItemCode.setHeight("18px");
		mainLayout.addComponent(lblItemCode,"top:50.0px;left:50.0px;");

		// txtRawItemCode
		txtItemCode = new TextRead();
		txtItemCode.setImmediate(false);
		txtItemCode.setWidth("100px");
		txtItemCode.setHeight("23px");
		mainLayout.addComponent(txtItemCode, "top:50.0px;left:190.0px;");



		// lblRawItemName
		lblItemName = new Label("Item Name: ");
		lblItemName.setImmediate(false);
		lblItemName.setWidth("-1px");
		lblItemName.setHeight("-1px");
		mainLayout.addComponent(lblItemName, " top:80.0px;left:50.0px;");

		// txtRawItemName
		txtItemName = new TextField();
		txtItemName.setImmediate(false);
		txtItemName.setWidth("318px");
		txtItemName.setHeight("-1px");
		txtItemName.setSecret(false);
		mainLayout.addComponent(txtItemName, "top:80.0px;left:190.0px;");


		// lblGroup
		lblcategory = new Label("Category: ");
		lblcategory.setImmediate(false);
		lblcategory.setWidth("-1px");
		lblcategory.setHeight("-1px");
		mainLayout.addComponent(lblcategory, "top:110.0px;left:50.0px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("318px");
		cmbCategory.setHeight("24px");
		cmbCategory.setNullSelectionAllowed(true);
		cmbCategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbCategory, "top:110.0px;left:190.0px;");



		// lblSubGroup
		lblsubcategory = new Label("Sub-Category: ");
		lblsubcategory.setImmediate(false);
		lblsubcategory.setWidth("-1px");
		lblsubcategory.setHeight("-1px");
		mainLayout.addComponent(lblsubcategory, "top:140.0px;left:50.0px;");

		// cmbSubGroup
		cmbsubcategory = new ComboBox();
		cmbsubcategory.setImmediate(false);
		cmbsubcategory.setWidth("318px");
		cmbsubcategory.setHeight("-1px");
		cmbsubcategory.setNullSelectionAllowed(true);
		cmbsubcategory.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbsubcategory, "top:140.0px;left:190.0px;");



		lblbaseProduct = new Label("Base Product: ");
		lblbaseProduct.setImmediate(false);
		lblbaseProduct.setWidth("-1px");
		lblbaseProduct.setHeight("-1px");
		mainLayout.addComponent(lblbaseProduct, "top:170.0px;left:50.0px;");

		cmbBaseProduct = new ComboBox();
		cmbBaseProduct.setImmediate(true);
		cmbBaseProduct.setWidth("318px");
		cmbBaseProduct.setHeight("24px");
		cmbBaseProduct.setNullSelectionAllowed(true);
		cmbBaseProduct.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbBaseProduct, "top:170.0px;left:190.0px;");


		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:200.0px;left:50.0px;");

		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);
		mainLayout.addComponent(txtUnit, "top:200.0px;left:190.0px;");



		//lblModel
		lblModelNo = new Label("Grade :");
		lblModelNo.setImmediate(false);
		lblModelNo.setWidth("-1px");
		lblModelNo.setHeight("-1px");
		mainLayout.addComponent(lblModelNo, "top:230.0px;left:50.0px;");

		// txtmodelNo
		txtmodelNo = new TextField();
		txtmodelNo.setImmediate(false);
		txtmodelNo.setWidth("100px");
		txtmodelNo.setHeight("-1px");
		txtmodelNo.setSecret(false);
		mainLayout.addComponent(txtmodelNo, "top:230.0px;left:190.0px;");


		//lblModel
		lblcolor = new Label("Color :");
		lblcolor.setImmediate(false);
		lblcolor.setWidth("-1px");
		lblcolor.setHeight("-1px");
		mainLayout.addComponent(lblcolor, "top:260.0px;left:50.0px;");

		// txtmodelNo
		txtcolor = new TextField();
		txtcolor.setImmediate(false);
		txtcolor.setWidth("100px");
		txtcolor.setHeight("-1px");
		txtcolor.setSecret(false);
		mainLayout.addComponent(txtcolor, "top:260.0px;left:190.0px;");



		mainLayout.addComponent(button, "top:320.0px;left:55.0px;");

		return mainLayout;
	}
}
