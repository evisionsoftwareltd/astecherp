package com.example.rawMaterialTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.DepoInformation;

import com.common.share.ImmediateUploadExample;
import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.CommonButtonNew;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExampleNew;
import com.common.share.MessageBox;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.YesNoDialog;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.rawMaterialSetup.RawItemCategory;
//import com.example.hrmSetup.Department;
//import com.example.hrmSetup.Section;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import database.hibernate.TbCompanyInfo;

public class RequsitionForHeadOffice extends Window 
{
	SessionBean sessionBean;
	private Label lblDepartment;
	private Label lblUser;
	private Label lblSectionIncharge;
	private Label lblReqNo;
	private Label lblSectionReqNo;
	private Label lblDate;
	private Label lblDeliveryDate;
	private Label lbl;
	private Label lblexpdeldate;
	private Label lblItemType;


	private TextField txtSectionIncharge;
	private ComboBox cmbDepartment;
	private TextRead txtuser;
	private TextRead txtreqno;
	private TextField txtSecReqno ;
	private PopupDateField dateField,deliveryDate,expdeldate;
	private ImmediateUploadExampleNew reqAttached;
	private ComboBox cmbItemType;
	private CheckBox chkAll;

	public NativeButton btnDepartment;

	private Table table = new Table();
	private ArrayList<Label> txtsl = new ArrayList<Label>();
	private ArrayList<ComboBox> cmbProduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> stockQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> unit = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> reqqty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> lastpurchaseqty = new ArrayList<AmountCommaSeperator>();
	private ArrayList<PopupDateField> lastPdate = new ArrayList<PopupDateField>();
	private ArrayList<ComboBox> cmbsupplier = new ArrayList<ComboBox>();
	private ArrayList<AmountCommaSeperator> rate = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> eRate = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> amount = new ArrayList<TextRead>();

	private Label lbLine = new Label("______________________________________________________________________________________________________________________________________________________________________________________");
	private CommonButtonNew cButton = new CommonButtonNew("New", "Save", "Edit","", "Refresh", "Find", "", "Exit", "", "Preview");

	boolean isUpdate = false,isFind=false;
	String filePathTmpReq= "";
	String imageLoc= "0";
	private TextField txtReqId = new TextField();
	private DecimalFormat df = new DecimalFormat("#0.00");
	SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private Formatter fmt;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Component> allComp = new ArrayList<Component>();

	private AbsoluteLayout mainLayout;

	public RequsitionForHeadOffice(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("REQUISITION ENTRY :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentData();
		cmbItemTypeData();
		setEventAction();
		btnIni(true);
		componentIni(true);
		focusEnter();
	}

	private void cmbItemTypeData() {

		cmbItemType.removeAllItems();
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			//List list = session.createSQLQuery("select 0,vCategoryType from tbRawItemCategory where vCategoryType not like '%Spare Parts%'").list();
			List list = session.createSQLQuery("select 0,vCategoryType from tbRawItemCategory").list();
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbItemType.addItem(element[1]);
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbDepartment);
		allComp.add(txtSectionIncharge);
		allComp.add(cmbItemType);
		allComp.add(txtSecReqno);
		allComp.add(dateField);

		for(int i=0;i<cmbProduct.size();i++)
		{
			allComp.add(cmbProduct.get(i));
			allComp.add(reqqty.get(i));
			allComp.add(eRate.get(i));
			allComp.add(lastpurchaseqty.get(i));
			allComp.add(rate.get(i));
			allComp.add(lastPdate.get(i));
			allComp.add(cmbsupplier.get(i));
			allComp.add(amount.get(i));
		}

		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnUpdate);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnUpdate.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPrev.setEnabled(t);
	}

	private void componentIni(boolean b) 
	{
		cmbDepartment.setEnabled(!b);
		btnDepartment.setEnabled(!b);
		chkAll.setValue(false);
		txtuser.setEnabled(!b);
		txtSectionIncharge.setEnabled(!b);
		txtreqno.setEnabled(!b);
		txtSecReqno.setEnabled(!b);
		dateField.setEnabled(!b);
		deliveryDate.setEnabled(!b);
		cmbItemType.setEnabled(!b);
		chkAll.setEnabled(!b);
		reqAttached.setEnabled(!b);
		lbLine.setEnabled(!b);
		table.setEnabled(!b);
	}

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("1250px");
		setHeight("650px");

		// lblDepartment
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Store :");
		mainLayout.addComponent(lblDepartment, "top:22.0px; left:30.0px;");

		// cmbDepartment
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("220px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setNewItemsAllowed(false);
		cmbDepartment.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:22.0px;left:138.0px;");

		// lblUser
		lblUser = new Label();
		lblUser.setImmediate(false);
		lblUser.setWidth("-1px");
		lblUser.setHeight("-1px");
		lblUser.setValue("User :");
		mainLayout.addComponent(lblUser, "top:47.0px; left:30.0px;");

		// txtUser
		txtuser = new TextRead();
		txtuser.setImmediate(false);
		txtuser.setWidth("130px");
		txtuser.setHeight("22px");
		mainLayout.addComponent(txtuser, "top:47.0px;left:139.0px;");

		// lblSectionIncharge
		lblSectionIncharge = new Label();
		lblSectionIncharge.setImmediate(false);
		lblSectionIncharge.setWidth("-1px");
		lblSectionIncharge.setHeight("-1px");
		lblSectionIncharge.setValue("Store Incharge :");
		mainLayout.addComponent(lblSectionIncharge, "top:71.0px; left:30.0px;");

		// txtSectionIncharge
		txtSectionIncharge =  new TextField();
		txtSectionIncharge.setImmediate(false);
		txtSectionIncharge.setWidth("131px");
		txtSectionIncharge.setHeight("-1px");
		mainLayout.addComponent(txtSectionIncharge, "top:71.0px;left:138.0px;");

		// lblSectionIncharge
		lblItemType = new Label();
		lblItemType.setImmediate(false);
		lblItemType.setWidth("-1px");
		lblItemType.setHeight("-1px");
		lblItemType.setValue("Item Type :");
		mainLayout.addComponent(lblItemType, "top:97.0px; left:30.0px;");

		cmbItemType = new ComboBox();
		cmbItemType.setImmediate(true);
		cmbItemType.setWidth("220px");
		cmbItemType.setHeight("-1px");
		cmbItemType.setNullSelectionAllowed(true);
		cmbItemType.setNewItemsAllowed(false);
		cmbItemType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbItemType, "top:97.0px;left:138.0px;");

		chkAll=new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setValue(false);
		mainLayout.addComponent(chkAll, "top:95.0px;left:370.0px;");

		// btnDepartment
		btnDepartment = new NativeButton();
		btnDepartment.setImmediate(false);
		btnDepartment.setIcon(new ThemeResource("../icons/add.png"));
		btnDepartment.setImmediate(true);
		btnDepartment.setWidth("28px");
		btnDepartment.setHeight("24px");
		mainLayout.addComponent(btnDepartment, "top:22.0px;left:360.0px;");

		// lblReqNo
		lblReqNo =  new Label();
		lblReqNo.setImmediate(false);
		lblReqNo.setWidth("-1px");
		lblReqNo.setHeight("-1px");
		lblReqNo.setValue("Req. No :");
		mainLayout.addComponent(lblReqNo, "top:22.0px; left:420.0px;");

		// txtreqno
		txtreqno =  new TextRead();
		txtreqno.setWidth("131px");
		txtreqno.setHeight("24px");
		mainLayout.addComponent(txtreqno, "top:22.0px;left:520.0px;");

		// lblSectionReqNo
		lblSectionReqNo =  new Label();
		lblSectionReqNo.setImmediate(false);
		lblSectionReqNo.setWidth("-1px");
		lblSectionReqNo.setHeight("-1px");
		lblSectionReqNo.setValue("Store Req. No :");
		mainLayout.addComponent(lblSectionReqNo, "top:47.0px; left:420.0px;");

		// txtSecReqno
		txtSecReqno = new TextField();
		txtSecReqno.setWidth("132px");
		txtSecReqno.setHeight("-1px");
		mainLayout.addComponent(txtSecReqno, "top:47.0px;left:520.0px;");

		// lblDate
		lblDate =  new Label();
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:71.0px;  left:420.0px;");

		// dateField
		dateField = new PopupDateField();
		dateField.setDateFormat("dd-MM-yyyy");
		dateField.setValue(new java.util.Date());
		dateField.setResolution(PopupDateField.RESOLUTION_DAY);
		dateField.setWidth("110px");
		dateField.setHeight("-1px");
		mainLayout.addComponent(dateField, "top:71.5px;left:520.0px;");

		// lblDeliveryDate
				lblDeliveryDate =  new Label();
				lblDeliveryDate.setImmediate(false);
				lblDeliveryDate.setWidth("-1px");
				lblDeliveryDate.setHeight("-1px");
				lblDeliveryDate.setValue("Delivery Date :");
				mainLayout.addComponent(lblDeliveryDate, "top:97.0px;  left:420.0px;");

				// dateField
				deliveryDate = new PopupDateField();
				deliveryDate.setDateFormat("dd-MM-yyyy");
				deliveryDate.setValue(new java.util.Date());
				deliveryDate.setResolution(PopupDateField.RESOLUTION_DAY);
				deliveryDate.setWidth("110px");
				deliveryDate.setHeight("-1px");
				mainLayout.addComponent(deliveryDate, "top:97.5px;left:520.0px;");

		// reqAttached
		reqAttached = new ImmediateUploadExampleNew("");
		reqAttached.setWidth("-1px");
		reqAttached.setHeight("-1px");
		reqAttached.setStyleName("uploadReq");
		mainLayout.addComponent(reqAttached, "top:10.5px;left:679.0px;");

		// table
		table.setWidth("99%");
		table.setHeight("330px");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 10);

		table.addContainerProperty("Product Name", ComboBox.class,new ComboBox());
		table.setColumnWidth("Product Name", 330);

		table.addContainerProperty("Stock Qty", TextRead.class, new TextRead());
		table.setColumnWidth("Stock Qty", 60);

		table.addContainerProperty("Unit", TextRead.class, new TextRead());
		table.setColumnWidth("Unit", 60);

		table.addContainerProperty("Req Qty", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Req Qty", 60);

		table.addContainerProperty("E Rate", AmountCommaSeperator.class,new AmountCommaSeperator());
		table.setColumnWidth("E Rate", 60);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Amount", 100);

		table.addContainerProperty("L P Qty", AmountCommaSeperator.class,new AmountCommaSeperator());
		table.setColumnWidth("L P Qty", 60);

		table.addContainerProperty("Rate", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Rate", 60);

		table.addContainerProperty("Date", PopupDateField.class,new PopupDateField());
		table.setColumnWidth("Date", 105);

		table.addContainerProperty("Supplier Name", ComboBox.class,new ComboBox());
		table.setColumnWidth("Supplier Name", 160);

		table.setColumnCollapsingAllowed(true);
		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_RIGHT, Table.ALIGN_CENTER, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER });

		mainLayout.addComponent(table, "top:155.0px; left:10.0px;");
		tableInitialise();

		mainLayout.addComponent(lbLine, "top:500.0px; left:10.0px;");
		mainLayout.addComponent(cButton, "top:540.0px; left:270.0px;");

		return mainLayout;
	}

	private void cmbDepartmentData() 
	{
		cmbDepartment.removeAllItems();
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery("select vDepoId,vDepoName from tbDepoInformation").list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	private void cmbProductAdd(){

		String type="";
		if(chkAll.booleanValue()){
			type="%";
		}
		else if(cmbItemType.getValue()!=null){
			type=cmbItemType.getValue().toString();
		}
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select vRawItemCode,vRawItemName,subString(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName))as category,"
					+" vSubSubCategoryName from tbRawItemInfo where vCategoryType like '"+type+"' order by category,vSubSubCategoryName";

			List list = session.createSQLQuery(sql).list();

			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String name = "";
				Object[] element = (Object[]) iter.next();
				for(int a=0;a<=cmbProduct.size()-1;a++){
					cmbProduct.get(a).addItem(element[0]);
					
					 if(element[2].toString().isEmpty())
						{
							name=element[1].toString()	;
						}
						else
						{
							 name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
						}
//					String name=element[1].toString()+"( "+element[2].toString()+"-"+element[3].toString()+" )";
					cmbProduct.get(a).setItemCaption(element[0], name);
				}
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
	}
	private void reportShow(){
		String sectionId,masterReq,storeReq;
		sectionId=cmbDepartment.getValue().toString().trim();
		storeReq=txtSecReqno.getValue().toString().trim();
		System.out.print(sectionId);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(new Date())+" "+"23:59:59";

		if(sectionId=="All")
		{
			sectionId="%";
		}

		if(storeReq=="All")
		{
			storeReq="%";
		}

		System.out.println(sectionId);
		String query=null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());

			System.out.println("URL: "+getApplication().getURL());
			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());

			query="select * from funcRawRequisition ('"+date+"','"+sectionId+"','"+storeReq+"') ";
			System.out.println(query);
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptRequisitionForm.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",0);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
	public void setEventAction()
	{
		cButton.btnPrev.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				if(isFind&&cmbDepartment.getValue()!=null){
					reportShow();
				}
				else{
					showNotification("Nothing to Preview",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
				cmbDepartment.focus();
				txtreqno.setValue(autoreqNo());

				isUpdate = false;

				txtuser.setValue(sessionBean.getUserName());
			}
		});
		cmbDepartment.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbDepartment.getValue()!=null){
					txtSecReqno.setValue(autoSecreqNo());
					txtSectionIncharge.focus();
				}
			}
		});
		chkAll.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(chkAll.booleanValue()){

					cmbItemType.setEnabled(false);
					cmbItemType.setValue(null);
					for(int a=0;a<cmbProduct.size();a++){
						cmbProduct.get(a).removeAllItems();
					}

					cmbProductAdd();
				}
				else{
					cmbItemType.setEnabled(true);
				}
			}
		});
		cmbItemType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbItemType.getValue()!=null){

					for(int a=0;a<cmbProduct.size();a++){
						cmbProduct.get(a).removeAllItems();
					}
					cmbProductAdd();
				}
			}
		});
		cButton.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable())
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(!txtSectionIncharge.getValue().toString().trim().isEmpty())
						{
							if(!checkReqQty().toString().equalsIgnoreCase("yes"))
							{
								if(!checkeRate().toString().equalsIgnoreCase("yes"))
								{	
									if(nullCheck())
									{
										saveButtonEvent();
										reqAttached.actionCheck = false;
									}else{
										showNotification("Warning,","please Provide All Fields",Notification.TYPE_WARNING_MESSAGE);
									}
								}else{
									showNotification("Warning,","please Provide E. Rate",Notification.TYPE_WARNING_MESSAGE);
								}

							}else{
								showNotification("Warning,","please Provide Req. Qty",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning,","please Provide Section Incharge Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning,","please select Department",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning,","You are not permitted to save data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnUpdate.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
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

		cButton.btnDelete.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isDeleteable())
				{
					deleteButtonEvent();
				}
				else
				{
					showNotification("Warning,","You are not permitted to delete data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		btnDepartment.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				System.out.println("Designation");
				departmentLink();
			}
		});
		reqAttached.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePatRequisition(0,"temp");
				System.out.println("Done");
			}
		});

		reqAttached.nbDOBPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();
					System.out.println(link+"  "+link.endsWith("uptd/"));

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", sessionBean.report)+filePathTmpReq;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());

					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}

				if(isUpdate)
				{
					if(!imageLoc.equalsIgnoreCase("0"))
					{
						if(!reqAttached.actionCheck)
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								System.out.println("Into "+link);
								System.out.println("ImageLocation: "+imageLoc);
								System.out.println("SubString "+imageLoc.substring(22));
								link = link.replaceAll("uptd/", imageLoc.substring(22, imageLoc.length()));
								System.out.println("LINK : " +link);
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
					}
					else
					{
						if(!reqAttached.actionCheck)
						{
							getParent().showNotification("There is no Requisition Form",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}

					if(reqAttached.actionCheck)
					{
						String link = getApplication().getURL().toString();
						System.out.println(link+"  "+link.endsWith("uptd/"));

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", sessionBean.report)+filePathTmpReq;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());

						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});  
	}
	private void departmentLink(){
		Window win = new DepoInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbDepartmentData();
				System.out.println("Category Form");
			}
		});
		this.getParent().addWindow(win);
	}
	private String imagePatRequisition(int flag, String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String ReqImage = null;

		System.out.println("Base Path: "+basePath);
		System.out.println("Base Path: o  "+getWindow().getApplication().getContext().getBaseDirectory());

		if(flag==0)
		{
			// image move		
			if(reqAttached.fileName.trim().length()>0)
			{
				System.out.println("hello : "+reqAttached.fileName.trim());
				if(reqAttached.fileExtension.equalsIgnoreCase(".jpg") || reqAttached.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try
					{
						String path = str;
						fileMove(basePath+reqAttached.fileName.trim(), SessionBean.imagePathTmp+path+reqAttached.fileExtension);
						ReqImage = SessionBean.imagePathTmp+path+reqAttached.fileExtension;
						filePathTmpReq = path+reqAttached.fileExtension;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}

		if(flag==1)
		{
			// image move		
			if(reqAttached.fileName.trim().length()>0)
			{
				System.out.println("reqAttached.fileName : "+reqAttached.fileName.trim());
				if(reqAttached.fileExtension.equalsIgnoreCase(".jpg") || reqAttached.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try {
						String path =str;
						fileMove(basePath+reqAttached.fileName.trim(),SessionBean.Requisition+path+reqAttached.fileExtension);
						ReqImage = SessionBean.Requisition+path+reqAttached.fileExtension;
						filePathTmpReq = path+reqAttached.fileExtension;
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp)
		{

		}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}
	private void findButtonEvent() 
	{
		Window win = new FindWindow(sessionBean, txtReqId,"reqFormforheadoffice");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtReqId.getValue().toString().length() > 0)
				{
					System.out.println(txtReqId.getValue().toString());
					txtClear();
					findInitialise();
					isFind=true;
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private String autoSecreqNo()
	{
		String autoCode = "";

		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max(sectionReqNo),0)+1 from tbRawRequisitionInfo where sectionId='"+cmbDepartment.getValue().toString().replaceAll("#", "")+"' ";
			System.out.println(query);
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		System.out.println("Auto Code: "+autoCode);
		return autoCode;
	}
	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);

		txtClear();
	}

	private void deleteButtonEvent() 
	{
		if (cmbDepartment.getValue() != null) 
		{
			this.getParent().addWindow(
					new YesNoDialog("", "Do you want to update information?",
							new YesNoDialog.Callback() 
					{	
						public void onDialogResult(boolean yes) 
						{
							if (yes) 
							{
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								Transaction tx = session.beginTransaction();

								if (deleteData(session, tx)) 
								{
									tx.commit();
									txtClear();
									getParent().showNotification("All information delete Successfully");
								}
								else 
								{
									tx.rollback();
									getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
								}
							}
						}
					}));
		} 
		else
		{
			this.getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void updateButtonEvent() 
	{
		if (cmbDepartment.getValue() != null) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			if(chkAll.booleanValue()){
				cmbItemType.setEnabled(false);
			}
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent() 
	{
		if (!cmbDepartment.getValue().toString().trim().isEmpty()) 
		{
			if (isUpdate) 
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if (buttonType == ButtonType.YES) 
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
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

							isUpdate = false;

							txtClear();
							componentIni(true);
							btnIni(true);
							mb.close();
						}
					}
				});
			} 
			else
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener() 
				{
					public void buttonClicked(ButtonType buttonType) 
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							reqAttached.status.setValue("");
							txtClear();
							componentIni(true);
							btnIni(true);
							mb.close();
						}
					}
				});
			}
		} 
		else
		{
			this.getParent().showNotification("Warning :","Please Select Product .",Notification.TYPE_WARNING_MESSAGE);
		}
	}


	private void insertData()
	{
		String imgPatReq = imagePatRequisition(1,txtreqno.getValue().toString())==null?imageLoc:imagePatRequisition(1,txtreqno.getValue().toString());
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		try
		{
			String reqId = autoreqNo();
			String secReqId = autoSecreqNo();
			String name=cmbDepartment.getItemCaption(cmbDepartment.getValue());
			StringTokenizer token=new StringTokenizer(name,"(");

			String sql="insert into tbRawRequisitionInfo values('"+cmbDepartment.getValue()+"','"+reqId+"','"+secReqId+"'," +
					"'"+token.nextToken()+"','"+txtSectionIncharge.getValue().toString().trim()+"','"+dateFormat.format(deliveryDate.getValue())+"'," +
					"'','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+imgPatReq+"',1)";

			System.out.println("Mezbah: "+sql);
			for (int i = 0; i < cmbProduct.size(); i++)
			{

				if (!reqqty.get(i).getValue().toString().isEmpty())
				{
					String query = "insert tbRawRequisitionDetails values('"+reqId+"','"+secReqId+"'," +
							" '"+cmbProduct.get(i).getValue().toString()+"'," +
							" '"+cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue())+"'," +
							" '"+reqqty.get(i).getValue().toString().replaceAll(",", "").trim()+"'," +
							" '"+(lastpurchaseqty.get(i).getValue().toString().isEmpty() ? "0.0" : lastpurchaseqty.get(i).getValue().toString().replaceAll(",", "").trim() )+"'," +
							" '"+dateF.format(lastPdate.get(i).getValue())+"'," +
							" '"+(cmbsupplier.get(i).getValue()==null ? "" : cmbsupplier.get(i).getValue().toString().replaceAll("#", "") )+"'," +
							" '"+(cmbsupplier.get(i).getValue()==null ? "" : cmbsupplier.get(i).getItemCaption(cmbsupplier.get(i).getValue()) )+"'," +
							" '"+(rate.get(i).getValue().toString().isEmpty()? "0.0" : rate.get(i).getValue().toString().replaceAll("#", "").trim() )+"'," +
							" '"+eRate.get(i).getValue().toString().replaceAll("#", "").trim()+"'," +
							" '"+amount.get(i).getValue().toString().replaceFirst(",", "")+"'," +
							" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,1)" ;
					System.out.println("Mezbah: "+query);
					session.createSQLQuery(query).executeUpdate();
					if(i==0){
						session.createSQLQuery(sql).executeUpdate();
					}
				}
			}
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			System.out.println(exp);
		}
	}

	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			session.createSQLQuery("delete tbRawRequisitionInfo where reqNo='"+txtReqId.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbRawRequisitionheadofficeInfo where reqNo='"+txtReqId.getValue()+ "' ");
			session.createSQLQuery("delete tbRawRequisitionDetails where reqNo='"+txtReqId.getValue()+ "' ").executeUpdate();
			System.out.println("delete tbRawRequisitionheadofficeDetails where reqNo='"+txtReqId.getValue()+ "' ");

			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}

	}

	private boolean nullCheck() 
	{
		if (cmbDepartment.getValue() != null)
		{
			for (int i = 0; i < cmbProduct.size(); i++) 
			{
				Object temp = cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue());

				System.out.println(cmbProduct.get(i).getValue());

				if (temp != null && !cmbProduct.get(i).getValue().equals(("x#" + i))) 
				{
					if (!amount.get(i).getValue().toString().trim().isEmpty()) 
					{
						return true;
					} 
					else
					{
						this.getParent().showNotification("Warning :","Please Enter Valid Issue Qty .",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		}
		else
		{
			this.getParent().showNotification("Warning :","Please Select Section To .",Notification.TYPE_WARNING_MESSAGE);
		}

		return false;
	}

	private String checkeRate()
	{
		String str = "";
		for(int i=0;i<cmbsupplier.size();i++)
		{
			if(!stockQty.get(i).getValue().toString().isEmpty())
			{
				if(eRate.get(i).getValue().toString().equals(""))
				{
					str = "yes";
					break;
				}else{
					str = "no";
				}
			}
		}

		return str;
	}

	private String checkReqQty()
	{
		String str = "";
		for(int i=0;i<cmbsupplier.size();i++)
		{
			if(!stockQty.get(i).getValue().toString().isEmpty())
			{
				if(reqqty.get(i).getValue().toString().equals(""))
				{
					str = "yes";
					break;
				}else{
					str = "no";
				}
			}
		}

		return str;
	}


	private String checkSupplier()
	{
		String str = "";
		for(int i=0;i<cmbsupplier.size();i++)
		{
			if(!stockQty.get(i).getValue().toString().isEmpty())
			{
				if(cmbsupplier.get(i).getValue()==null)
				{
					System.out.println(cmbsupplier.get(i).getValue());
					str = "yes";
					break;
				}else{
					str = "no";
				}
			}
		}
		return str;
	}

	private void newButtonEvent()
	{
		componentIni(false);
		btnIni(false);
		txtClear();

	}

	public void txtClear()
	{
		cmbDepartment.setValue(null);
		txtuser.setValue("");
		dateField.setValue(new java.util.Date());
		txtreqno.setValue("");
		txtSectionIncharge.setValue("");
		txtSecReqno.setValue("");

		for (int i = 0; i < unit.size(); i++)
		{
			cmbProduct.get(i).setValue(null);
			unit.get(i).setValue("");
			stockQty.get(i).setValue("");
			reqqty.get(i).setValue("");
			lastpurchaseqty.get(i).setValue("");
			lastPdate.get(i).setValue(new java.util.Date());
			cmbsupplier.get(i).setValue(null);
			rate.get(i).setValue("");
			eRate.get(i).setValue("");
			amount.get(i).setValue("");
		}
	}

	public void tableInitialise() 
	{
		for (int i = 0; i<10; i++) 
		{
			tableRowAdd(i);
		}
	}


	public void tableRowAdd(final int ar) 
	{
		String sql="";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		try 
		{
			txtsl.add(ar, new Label());
			txtsl.get(ar).setWidth("100%");
			txtsl.get(ar).setValue(ar + 1);

			cmbProduct.add(ar, new ComboBox());
			cmbProduct.get(ar).setWidth("100%");
			cmbProduct.get(ar).setImmediate(true);
			cmbProduct.get(ar).setNullSelectionAllowed(true);
			cmbProduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			stockQty.add(ar, new TextRead(""));
			stockQty.get(ar).setWidth("100%");

			unit.add(ar, new TextRead(""));
			unit.get(ar).setWidth("100%");

			reqqty.add(ar, new AmountCommaSeperator(""));
			reqqty.get(ar).setWidth("100%");
			reqqty.get(ar).setImmediate(true);

			eRate.add(ar, new AmountCommaSeperator(""));
			eRate.get(ar).setWidth("100%");
			eRate.get(ar).setImmediate(true);

			amount.add(ar, new TextRead(1));
			amount.get(ar).setWidth("100%");
			amount.get(ar).setImmediate(true);

			reqqty.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) {
					if(!reqqty.get(ar).getValue().toString().isEmpty()){
						if(cmbProduct.get(ar).getValue()!=null){
							if(!eRate.get(ar).getValue().toString().isEmpty()){
								String requiredQty;
								double tamount,unitPrice;
								System.out.println("HSHSHSHSH");

								requiredQty=reqqty.get(ar).getValue().toString().trim().replaceAll(",", "");

								String tempPrice=eRate.get(ar).getValue().toString().replaceAll(",", "");

								System.out.println(tempPrice);
								unitPrice=Double.parseDouble(tempPrice);
								System.out.println("column Action");
								tamount=unitPrice*(Double.parseDouble(requiredQty));
								fmt = new Formatter();
								amount.get(ar).setValue(fmt.format("%.2f",tamount));
							}
						}
					}
				}
			});


			eRate.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(!reqqty.get(ar).getValue().toString().isEmpty()){
						if(cmbProduct.get(ar).getValue()!=null){
							if(!eRate.get(ar).getValue().toString().isEmpty()){
								String requiredQty;
								double tamount,unitPrice;
								System.out.println("HSHSHSHSH");

								requiredQty=reqqty.get(ar).getValue().toString().trim().replaceAll(",", "");

								String tempPrice=eRate.get(ar).getValue().toString().replaceAll(",", "");

								System.out.println(tempPrice);
								unitPrice=Double.parseDouble(tempPrice);
								System.out.println("column Action");
								tamount=unitPrice*(Double.parseDouble(requiredQty));
								fmt = new Formatter();
								amount.get(ar).setValue(fmt.format("%.2f",tamount));
							}
						}else{
							reqqty.get(ar).setValue("");
							eRate.get(ar).setValue("");
						}
					}
				}
			});
			lastpurchaseqty.add(ar, new AmountCommaSeperator(""));
			lastpurchaseqty.get(ar).setWidth("100%");
			lastpurchaseqty.get(ar).setEnabled(false);

			rate.add(ar, new AmountCommaSeperator(""));
			rate.get(ar).setWidth("100%");
			rate.get(ar).setEnabled(false);

			lastPdate.add(ar, new PopupDateField(""));
			lastPdate.get(ar).setDateFormat("dd-MM-yyyy");
			lastPdate.get(ar).setValue(new java.util.Date());
			lastPdate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
			lastPdate.get(ar).setWidth("100%");
			lastPdate.get(ar).setEnabled(false);

			cmbsupplier.add(ar, new ComboBox(""));
			cmbsupplier.get(ar).setWidth("100%");
			cmbsupplier.get(ar).setNullSelectionAllowed(true);
			cmbsupplier.get(ar).setNewItemsAllowed(false);
			cmbsupplier.get(ar).setEnabled(false);

			String query="select supplierId,supplierName from tbSupplierInfo";
			List list= session.createSQLQuery(query).list();
			cmbsupplier.get(ar).removeAllItems();


			for(Iterator iter= list.iterator(); iter.hasNext();)
			{

				Object [] element=(Object[])iter.next();
				cmbsupplier.get(ar).addItem(element[0]);
				cmbsupplier.get(ar).setItemCaption(element[0], element[1].toString());


			}

			cmbProduct.get(ar).addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					if(cmbProduct.get(ar).getValue()!=null){

						boolean fla = (doubleEntryCheck(cmbProduct.get(ar).getItemCaption(cmbProduct.get(ar).getValue()), ar));

						int temp=cmbProduct.size();

						if(ar==temp-1)
						{

							tableRowAdd(temp);
							cmbProductAdd();
						}

						if(fla){
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							Transaction tx = session.beginTransaction();
							try{
								String sql="select unit,closingqty from funProductStock('"+dateF.format(dateField.getValue())+"','"+cmbProduct.get(ar).getValue()+"')";
								List list=session.createSQLQuery(sql).list();
								for(Iterator iter=list.iterator();iter.hasNext();){

									Object[] element=(Object[]) iter.next();

									unit.get(ar).setValue(element[0]);
									stockQty.get(ar).setValue(df.format(element[1]) );
									reqqty.get(ar).focus();
								}
								String sql1="select  a.ReceiptNo,a.Date,b.Qty,b.Rate,CAST(c.supplierId as varchar(120))supplierId, c.supplierName  from tbRawPurchaseInfo a "
										+"inner join "
										+"tbRawPurchaseDetails b "
										+"on a.ReceiptNo=b.ReceiptNo inner join tbSupplierInfo c on a.SupplierId=c.supplierId "
										+"where b.ProductID like '"+cmbProduct.get(ar).getValue()+"' and a.Date =(select  MAX(a.Date) from tbRawPurchaseInfo a "
										+"inner join "
										+"tbRawPurchaseDetails b "
										+"on a.ReceiptNo=b.ReceiptNo "
										+"where  a.Date<'"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+" "+"23:59:59"+"' and b.ProductID like '"+cmbProduct.get(ar).getValue()+"')  and a.SupplierId not like '%AL%'"
										+"UNION ALL " 								
										+"select  a.ReceiptNo,a.Date,b.Qty,b.Rate,c.Ledger_Id,c.Ledger_Name  from tbRawPurchaseInfo a "
										+"inner join "
										+"tbRawPurchaseDetails b "
										+"on a.ReceiptNo=b.ReceiptNo inner join tbLedger c on a.SupplierId=c.Ledger_Id "
										+"where b.ProductID like '"+cmbProduct.get(ar).getValue()+"' and a.Date =(select  MAX(a.Date) from tbRawPurchaseInfo a "
										+"inner join "
										+"tbRawPurchaseDetails b "
										+"on a.ReceiptNo=b.ReceiptNo "
										+"where  a.Date<'"+new SimpleDateFormat("yyyy-MM-dd").format(dateField.getValue())+" "+"23:59:59"+"' and b.ProductID like '"+cmbProduct.get(ar).getValue()+"') and a.SupplierId like '%AL%' ";					
								
								List list1=session.createSQLQuery(sql1).list();
								for(Iterator iter =list1.iterator();iter.hasNext();){
									Object element[]=(Object[]) iter.next();
									lastpurchaseqty.get(ar).setValue(element[2]);
									rate.get(ar).setValue(element[3]);
									lastPdate.get(ar).setValue(element[1]);
									cmbsupplier.get(ar).removeAllItems();
									cmbsupplier.get(ar).addItem(element[4]);
									cmbsupplier.get(ar).setItemCaption(element[4], element[5].toString());
									cmbsupplier.get(ar).setValue(element[4]);
									System.out.println(element[4]);
								}
							}
							catch(Exception exp){
								getParent().showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
							}
						}
						else{
							cmbProduct.get(ar).setValue(null);
							getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
							stockQty.get(ar).setValue("");
							rate.get(ar).setValue("");
							stockQty.get(ar).setValue("");
							amount.get(ar).setValue("");
						}
					}
					else{
						stockQty.get(ar).setValue("");
						unit.get(ar).setValue("");
						reqqty.get(ar).setValue("");
						eRate.get(ar).setValue("");
						amount.get(ar).setValue("");
						lastpurchaseqty.get(ar).setValue("");
						lastPdate.get(ar).setValue(new java.util.Date());
						cmbsupplier.get(ar).setValue(null);
					}
				}
			});
			table.addItem(new Object[] { txtsl.get(ar), cmbProduct.get(ar),stockQty.get(ar), unit.get(ar), reqqty.get(ar),eRate.get(ar),amount.get(ar),lastpurchaseqty.get(ar),rate.get(ar), lastPdate.get(ar),cmbsupplier.get(ar)}, ar);

		} 
		catch (Exception exp) 
		{
			System.out.println(exp);
		}
	}



	private void findInitialise() 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery("select * from tbRawRequisitionInfo where reqNo='"+txtReqId.getValue()+ "'").list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();
				cmbDepartment.setValue(element[1].toString());
				txtuser.setValue(element[8]);
				txtSectionIncharge.setValue(element[5]);
				deliveryDate.setValue(element[6]);
				txtreqno.setValue(element[2]);
				txtSecReqno.setValue(element[3]);
				dateField.setValue((element[10]));
				chkAll.setValue(true);
				cmbItemType.setEnabled(false);

				if(!element[11].toString().equals("0")){
					imageLoc = element[11].toString();
					reqAttached.status.setValue("Req No: "+imageLoc.substring(46));
				}
			}
			List list = session.createSQLQuery("Select * from tbRawRequisitionDetails  where reqNo='"+txtReqId.getValue()+ "'").list();
			int i = 0;
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbProduct.get(i).setValue(element[3]);
				reqqty.get(i).setValue(df.format(element[5]));
				lastpurchaseqty.get(i).setValue(df.format(element[6]));
				lastPdate.get(i).setValue((element[7]));
				cmbsupplier.get(i).setValue(element[8]);
				rate.get(i).setValue(df.format(element[10]));
				eRate.get(i).setValue(df.format(element[11]));
				amount.get(i).setValue(df.format(element[12]));

				i++;
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private String autoreqNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max(reqNo),0)+1 from tbRawRequisitionInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	private boolean doubleEntryCheck(String caption, int row) 
	{
		for (int i = 0; i < stockQty.size(); i++) 
		{
			if (i != row && caption.equals(cmbProduct.get(i).getItemCaption(cmbProduct.get(i).getValue()))) 
			{
				return false;
			}
		}
		return true;
	}
}
