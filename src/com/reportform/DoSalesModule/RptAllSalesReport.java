package com.reportform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.CommonButton;
import com.common.share.PreviewOption;
import com.common.share.ReportDate;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptAllSalesReport extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	// Main Panel
	private Label lblReportName;
	private Panel leftPanel;
	private OptionGroup RadioButtonReportName;
	private static final List<String> groupButtonReportName=Arrays.asList(new String[]{"Party Wise Sales","Product Wise Sales","TSO Wise Sales","Collection"});

	// previewPanel
	private Panel previewPanel;
	private Label lblPreviewOption;

	private Label lblFromDate;
	private Label lblAsonDate;
	private PopupDateField dfromDate;

	private Label lbltoDate;
	private PopupDateField dtoDate;

	private Label lblMonth;
	private PopupDateField dMonth;

	private Label lblYear;
	private PopupDateField dYear;

	private Label lblDivision;
	private ComboBox cmbDivision;
	private CheckBox chkDivision;

	private Label lblArea;
	private ComboBox cmbArea;
	private CheckBox chkArea;

	private Label lblSR;
	private ComboBox cmbSR;
	private CheckBox chkSr;

	private Label lblCat;
	private ComboBox cmbCat;
	private CheckBox chkCat;

	private Label lblOfficeLocation;
	private ComboBox cmbOfficeLocation;
	private CheckBox chkOfficeLocation;

	// start report criteria

	private Label lblReportCriteria;

	// PartyDateBetweenPanel
	private Panel PartyDateBetweenPanel;
	private FormLayout fLayoutPartyDateBetween = new FormLayout();

	// SummaryPanel
	private Panel PartySummaryPanel;
	private FormLayout fLayoutPartySummary = new FormLayout();

	// TargetAchievePanel
	private Panel PartyTargetAchievePanel;
	private FormLayout fLayoutPartyTargetAchieve = new FormLayout();

	private Panel PartyDuePanel;
	private FormLayout fLayoutPartyDue = new FormLayout();

	private Label lblTargetAchieve;

	// end report criteria

	// CategoryPanel
	private Panel CategoryPanel;
	private Label lblCategory;

	private OptionGroup RadioButtonPartyWise;
	private static final List<String> groupButtonParty=Arrays.asList(new String[]{"Date Between","Summary","Target Vs Achievement","Dues"});

	private OptionGroup RadioButtonProductWise;
	private static final List<String> groupButtonProduct=Arrays.asList(new String[]{"Date Between","Summary","Monthly","Yearly"});

	private OptionGroup RadioButtonSrWise;
	private static final List<String> groupButtonSR=Arrays.asList(new String[]{"Monthly Target","Target Vs Achievement(Category)","Target Vs Achievement(TSO)","Yearly Target Vs Achievement"});

	private OptionGroup RadioButtonCollection;
	private static final List<String> groupButtonCollection=Arrays.asList(new String[]{"Date Between","Summary","Daily Details","Monthly Details","Monthly Summary","Yearly",});

	// date Formats
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yformat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dFFiscal = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM-01");
	private SimpleDateFormat m = new SimpleDateFormat("MM");
	private SimpleDateFormat d = new SimpleDateFormat("dd");
	private SimpleDateFormat mFormat = new SimpleDateFormat("MMMM");

	private PreviewOption po = new PreviewOption();

	private ReportDate reportTime = new ReportDate();

	private HorizontalLayout hButtonLayout = new HorizontalLayout();
	private CommonButton cButton = new CommonButton("", "", "", "","","","","Preview","Print","Exit");
	private NativeButton btnPrint = new NativeButton("Print");



	public RptAllSalesReport(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SALES REPORT :: "+sessionBean.getCompany());
		this.setWidth("850px");
		this.setHeight("320px");
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		PartyInvisiblerData(false);
		cmbAddDivisionData();
		setEventAction();
		SetRadio();
	}

	public void setEventAction()
	{
		RadioButtonReportName.addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{ 
				SetRadio();
				ClearData();
				if(RadioButtonReportName.getValue().toString().equals("Party Wise Sales"))
				{
					SetRadio();
					ClearData();
					PartyInvisiblerData(false);
					optionGroupVisible(true,false,false,false);

					RadioButtonPartyWise.addListener(new ValueChangeListener() 
					{
						public void valueChange(ValueChangeEvent event) 
						{
							ClearData();
							PartyInvisiblerData(false);
							if(RadioButtonPartyWise.getValue().toString().equals("Date Between") )

							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,true,true,true,true,false,true,true,false,false,false);
							}

							if(RadioButtonPartyWise.getValue().toString().equals("Summary"))
							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,false,true,false,false,true,true,true,false,false,false);
							}

							if(RadioButtonPartyWise.getValue().toString().equals("Target Vs Achievement"))
							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,false,true,false,false,true,true,true,false,false,false);
							}

							if(RadioButtonPartyWise.getValue().toString().equals("Dues"))
							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,false,true,false,false,true,true,true,false,false,false);
							}
						}
					});
				}

				if(RadioButtonReportName.getValue().toString().equals("Product Wise Sales"))
				{
					PartyInvisiblerData(false);
					optionGroupVisible(false,true,false,false);

					RadioButtonProductWise.addListener(new ValueChangeListener() 
					{

						public void valueChange(ValueChangeEvent event) 
						{
							SetRadio();
							ClearData();
							PartyInvisiblerData(false);
							if(RadioButtonProductWise.getValue().toString().equals("Date Between") )
							{
								PartyInvisiblerData(false);
								ProductWise(true,true,true,true,false,false,false,false,false);
							}

							if(event.getProperty().toString().equals("Summary"))
							{
								ClearData();
								PartyInvisiblerData(false);
								ProductWise(false,true,false,false,true,false,false,false,false);
							}

							if(event.getProperty().toString().equals("Monthly"))
							{
								ClearData();
								PartyInvisiblerData(false);
								ProductWise(false,false,false,false,false,true,true,false,false);
							}

							if(event.getProperty().toString().equals("Yearly"))
							{
								ClearData();
								PartyInvisiblerData(false);
								ProductWise(false,false,false,false,false,false,false,true,true);
							}
						}
					});

				}

				if(RadioButtonReportName.getValue().toString().equals("TSO Wise Sales"))
				{
					SetRadio();
					ClearData();
					PartyInvisiblerData(false);
					optionGroupVisible(false,false,true,false);

					RadioButtonSrWise.addListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) 
						{
							if(event.getProperty().toString().equals("Monthly Target"))
							{
								ClearData();
								PartyInvisiblerData(false);
								SRWise(true,true,false,false,true,true,true,false,false,false,false);
							}

							if(RadioButtonSrWise.getValue().toString().equals("Target Vs Achievement(TSO)") )
							{
								ClearData();
								cmbAddSRData();
								PartyInvisiblerData(false);
								SRWise(true,true,true,true,true,true,true,false,false,false,true);
							}

							if(event.getProperty().toString().equals("Target Vs Achievement(Category)"))
							{
								ClearData();
								cmbAddCategoryData();
								PartyInvisiblerData(false);
								SRWise(true,true,false,false,true,true,true,true,true,true,false);
							}
						}
					});
				}

				if(RadioButtonReportName.getValue().toString().equals("Collection"))
				{
					SetRadio();
					ClearData();
					PartyInvisiblerData(false);
					optionGroupVisible(false,false,false,true);

					RadioButtonCollection.addListener(new ValueChangeListener() 
					{
						public void valueChange(ValueChangeEvent event) 
						{
							ClearData();
							PartyInvisiblerData(false);
							if(RadioButtonCollection.getValue().toString().equals("Date Between") )

							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,true,true,true,true,false,true,true,false,false,false);
							}

							if(RadioButtonCollection.getValue().toString().equals("Summary"))
							{
								ClearData();
								PartyInvisiblerData(false);
								PartyWise(true,true,true,true,false,true,false,false,true,true,true,false,false,false);
							}

							if(event.getProperty().toString().equals("Daily Details"))
							{
								ClearData();
								addOfficeInformation();
								PartyInvisiblerData(false);
								PartyWise(false,false,false,false,true,true,false,false,false,false,false,true,true,true);
							}

							if(event.getProperty().toString().equals("Monthly Details"))
							{
								ClearData();
								addOfficeInformation();
								PartyInvisiblerData(false);
								PartyWiseCollection(true,true,true,true,true,false,false,false,false,false,false,false,false);
							}

							if(event.getProperty().toString().equals("Monthly Summary"))
							{
								ClearData();
								addOfficeInformation();
								PartyInvisiblerData(false);
								PartyWiseCollection(false,false,false,true,true,false,false,true,true,true,true,true,true);
							}

							if(event.getProperty().toString().equals("Yearly"))
							{
								ClearData();
								addOfficeInformation();
								PartyInvisiblerData(false);
								PartyWiseCollection(true,true,true,false,false,true,true,false,false,false,false,false,false);
							}
						}
					});
				}
			}
		});


		cmbDivision.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbArea.removeAllItems();
				cmbArea.setValue(null);
				if(cmbDivision.getValue()!=null)
				{
					cmbAreaAddData();
				}
			}
		});

		chkArea.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkArea.booleanValue()==true)
				{
					cmbArea.setValue(null);
					cmbArea.setEnabled(false);
				}
				else
				{
					cmbArea.setEnabled(true);
				}
			}
		});

		chkDivision.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkDivision.booleanValue()==true)
				{
					chkArea.setEnabled(false);
					chkArea.setValue(true);
					cmbDivision.setValue(null);
					cmbDivision.setEnabled(false);
					cmbArea.setValue(null);
					cmbArea.setEnabled(false);
				}
				else
				{
					cmbArea.setEnabled(true);
					cmbDivision.setEnabled(true);
					chkArea.setEnabled(true);
					chkArea.setValue(false);
				}
			}
		});

		chkCat.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkCat.booleanValue()==true)
				{
					cmbCat.setValue(null);
					cmbCat.setEnabled(false);
				}
				else
				{
					cmbCat.setEnabled(true);
				}
			}
		});

		chkSr.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkSr.booleanValue()==true)
				{
					cmbSR.setValue(null);
					cmbSR.setEnabled(false);
				}
				else
				{
					cmbSR.setEnabled(true);
				}
			}
		});

		chkOfficeLocation.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(chkOfficeLocation.booleanValue()==true)
				{
					cmbOfficeLocation.setValue(null);
					cmbOfficeLocation.setEnabled(false);
				}
				else
				{
					cmbOfficeLocation.setEnabled(true);
				}
			}
		});


		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(RadioButtonReportName.getValue().toString().equals("Party Wise Sales"))
				{
					if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
						if(chkArea.booleanValue()==true || cmbArea.getValue()!=null){
							previewReport();

						}else{
							getParent().showNotification("Warning","Please Select Zone",Notification.TYPE_WARNING_MESSAGE);
							cmbArea.focus();
						}
					}else{
						getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
						cmbDivision.focus();
					}
				}
				if(RadioButtonReportName.getValue().toString().equals("Product Wise Sales"))
				{
					previewReport();
				}

				if(RadioButtonReportName.getValue().toString().equals("TSO Wise Sales"))
				{
					if(RadioButtonSrWise.getValue().toString().equals("Target Vs Achievement(TSO)")){
						if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
							if(cmbSR.getValue() !=null || chkSr.booleanValue()==true )
							{
								previewReport();
							}
							else
							{
								getParent().showNotification("Warning","Please Select TSO Name",Notification.TYPE_WARNING_MESSAGE);
								cmbSR.focus();
							}
						}else{
							getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}
					if(RadioButtonSrWise.getValue().toString().equals("Target Vs Achievement(Category)")){
						if(cmbDivision.getValue() !=null || chkDivision.booleanValue()==true)
						{
							if(cmbCat.getValue() !=null || chkCat.booleanValue()==true)
							{
								previewReport();
							}
							else
							{
								getParent().showNotification("Warning","Please Select Category Name",Notification.TYPE_WARNING_MESSAGE);
								cmbCat.focus();
							}
						}
						else
						{
							getParent().showNotification("Warning","Please Select Division Name",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}

					if(RadioButtonSrWise.getValue().toString().equals("Monthly Target"))
					{
						if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
							previewReport();
						}
						else{
							getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}
				}
				if(RadioButtonReportName.getValue().toString().equals("Collection"))
				{
					if(RadioButtonCollection.getValue().toString().equals("Date Between")){
						if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
							if(cmbArea.getValue() !=null || chkArea.booleanValue()==true )
							{
								previewReport();
							}
							else
							{
								getParent().showNotification("Warning","Please Select Zone",Notification.TYPE_WARNING_MESSAGE);
								cmbArea.focus();
							}
						}else{
							getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}

					if(RadioButtonCollection.getValue().toString().equals("Summary")){
						if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
							if(cmbArea.getValue() !=null || chkArea.booleanValue()==true )
							{
								previewReport();
							}
							else
							{
								getParent().showNotification("Warning","Please Select Zone",Notification.TYPE_WARNING_MESSAGE);
								cmbArea.focus();
							}
						}else{
							getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}

					if(RadioButtonCollection.getValue().toString().equals("Daily Details")){

						if(cmbOfficeLocation.getValue() !=null || chkOfficeLocation.booleanValue()==true )
						{
							previewReport();
						}
						else
						{
							getParent().showNotification("Warning","Please Select Office",Notification.TYPE_WARNING_MESSAGE);
							cmbOfficeLocation.focus();
						}
					}

					if(RadioButtonCollection.getValue().toString().equals("Monthly Details")){

						if(cmbOfficeLocation.getValue() !=null || chkOfficeLocation.booleanValue()==true )
						{
							previewReport();
						}
						else
						{
							getParent().showNotification("Warning","Please Select Office",Notification.TYPE_WARNING_MESSAGE);
							cmbOfficeLocation.focus();
						}
					}

					if(RadioButtonCollection.getValue().toString().equals("Monthly Summary")){

						if(cmbDivision.getValue()!=null || chkDivision.booleanValue()==true ){
							if(cmbArea.getValue() !=null || chkArea.booleanValue()==true )
							{
								previewReport();
							}
							else
							{
								getParent().showNotification("Warning","Please Select Zone",Notification.TYPE_WARNING_MESSAGE);
								cmbArea.focus();
							}
						}else{
							getParent().showNotification("Warning","Please Select Division",Notification.TYPE_WARNING_MESSAGE);
							cmbDivision.focus();
						}
					}

					/*if(RadioButtonCollection.getValue().toString().equals("Yearly")){

						if(cmbOfficeLocation.getValue() !=null || chkOfficeLocation.booleanValue()==true )
						{
							previewReport();
						}
						else
						{
							getParent().showNotification("Warning","Please Select Office",Notification.TYPE_WARNING_MESSAGE);
							cmbOfficeLocation.focus();
						}
					}*/
				}

			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}


	public void cmbAddDivisionData()
	{
		cmbDivision.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vDivisionId,vDivisionName from tbDivisionInfo order by iAutoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDivision.addItem(element[0].toString());
				cmbDivision.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbAddCategoryData()
	{
		cmbCat.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select Group_Id,vCategoryName from tbProductCategory ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCat.addItem(element[0].toString());
				cmbCat.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbAddSRData()
	{
		cmbSR.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select Distinct vEmployeeId,vEmployeeName from tbAreaInfo").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSR.addItem(element[0].toString());
				cmbSR.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbAreaAddData()
	{
		Transaction tx=null;

		if(cmbDivision.getValue()!=null)
		{
			try
			{

				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(" SELECT vAreaId,vAreaName FROM tbAreaInfo WHERE vDivisionId = '"+cmbDivision.getValue().toString()+"' ").list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbArea.addItem(element[0].toString());
					cmbArea.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error:",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}

		}
	}

	private void addOfficeInformation()
	{
		cmbOfficeLocation.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select vDepoId,vDepoName from tbDepoInformation order by iAutoId ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOfficeLocation.addItem(element[0]);
				cmbOfficeLocation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("830px");
		mainLayout.setHeight("280px");
		mainLayout.setMargin(false);


		/*------------------------------Left Select Main Panel---------------------------------------------*/

		leftPanel = new Panel();
		leftPanel.setWidth("150px");
		leftPanel.setHeight("140px");
		leftPanel.setImmediate(true);
		leftPanel.setStyleName("radius");
		mainLayout.addComponent(leftPanel, "top:20px;left:20px;");

		lblReportName = new Label("<font color='#06854E' size='2px'><b>Report Name</b></font>");
		lblReportName.setImmediate(true);
		lblReportName.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblReportName, "top:30px;left:55px;");

		RadioButtonReportName = new OptionGroup("",groupButtonReportName);
		RadioButtonReportName.setImmediate(true);
		RadioButtonReportName.setStyleName("vertical");
		mainLayout.addComponent(RadioButtonReportName,"top:55.0px;left:30.0px");

		/*-------------------------Preview Option Panel-----------------------------------------*/

		previewPanel = new Panel();
		previewPanel.setWidth("150px");
		previewPanel.setHeight("100px");
		previewPanel.setImmediate(true);
		previewPanel.setStyleName("radius");

		po.setImmediate(true);
		previewPanel.addComponent(po);
		mainLayout.addComponent(previewPanel, "top:165px;left:20px;");

		lblPreviewOption = new Label("<font color='#06854E' size='2px'><b>Report Preview</b></font>");
		lblPreviewOption.setImmediate(true);
		lblPreviewOption.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPreviewOption, "top:170px;left:42px;");

		//------------------------------------ For Category Panel-------------------------------------------

		CategoryPanel = new Panel();
		CategoryPanel.setWidth("220px");
		CategoryPanel.setHeight("245px");
		CategoryPanel.setImmediate(true);
		CategoryPanel.setStyleName("radius");
		mainLayout.addComponent(CategoryPanel, "top:20px;left:190px;");

		lblCategory = new Label("<font color='#06854E'><b>Report Category</b></font>");
		lblCategory.setImmediate(true);
		lblCategory.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCategory, "top:30px;left:250px;");

		// ------------------------------- Radio Button ---------------------------------------------

		RadioButtonPartyWise = new OptionGroup("",groupButtonParty);
		RadioButtonPartyWise.setImmediate(true);
		RadioButtonPartyWise.setVisible(false);
		mainLayout.addComponent(RadioButtonPartyWise, "top:55px;left:200px;");

		RadioButtonProductWise = new OptionGroup("",groupButtonProduct);
		RadioButtonProductWise.setImmediate(true);
		RadioButtonProductWise.setVisible(false);
		mainLayout.addComponent(RadioButtonProductWise, "top:55px;left:200px;");

		RadioButtonSrWise = new OptionGroup("",groupButtonSR);
		RadioButtonSrWise.setImmediate(true);
		RadioButtonSrWise.setVisible(false);
		mainLayout.addComponent(RadioButtonSrWise, "top:55px;left:200px;");

		RadioButtonCollection = new OptionGroup("",groupButtonCollection);
		RadioButtonCollection.setImmediate(true);
		RadioButtonCollection.setVisible(false);
		mainLayout.addComponent(RadioButtonCollection, "top:55px;left:200px;");

		/*--------------------------------- For Party Date Between --------------------------------*/

		PartyDateBetweenPanel = new Panel();
		PartyDateBetweenPanel.setImmediate(true);
		PartyDateBetweenPanel.setWidth("400px");
		PartyDateBetweenPanel.setHeight("245px");
		PartyDateBetweenPanel.setStyleName("radius");
		mainLayout.addComponent(PartyDateBetweenPanel, "top:20px;left:430px;");

		lblDivision = new Label("<font color='#9110BB'><b>Division :</b></font>");
		lblDivision.setImmediate(true);
		lblDivision.setContentMode(Label.CONTENT_XHTML);
		lblDivision.setVisible(false);
		mainLayout.addComponent(lblDivision, "top:80px;left:450px;");

		cmbDivision=new ComboBox();
		cmbDivision.setImmediate(true);
		cmbDivision.setWidth("200px");
		cmbDivision.setHeight("-1px");
		cmbDivision.setNullSelectionAllowed(true);
		cmbDivision.setVisible(false);
		mainLayout.addComponent(cmbDivision, "top:78px;left:550px;");

		//CategoryAll
		chkDivision = new CheckBox("All");
		chkDivision.setHeight("-1px");
		chkDivision.setWidth("-1px");
		chkDivision.setImmediate(true);
		chkDivision.setVisible(false);
		mainLayout.addComponent(chkDivision, "top:78px; left:766.0px;");

		lblArea = new Label("<font color='#9110BB'><b>Zone :</b></font>");
		lblArea.setImmediate(true);
		lblArea.setContentMode(Label.CONTENT_XHTML);
		lblArea.setVisible(false);
		mainLayout.addComponent(lblArea, "top:105px;left:450px;");

		cmbArea=new ComboBox();
		cmbArea.setImmediate(true);
		cmbArea.setWidth("200px");
		cmbArea.setHeight("-1px");
		cmbArea.setNullSelectionAllowed(true);
		cmbArea.setVisible(false);
		mainLayout.addComponent(cmbArea, "top:103px;left:550px;");

		chkArea = new CheckBox("All");
		chkArea.setHeight("-1px");
		chkArea.setWidth("-1px");
		chkArea.setImmediate(true);
		chkArea.setVisible(false);
		mainLayout.addComponent(chkArea, "top:103px; left:766.0px;");

		lblFromDate = new Label("<font color='#9110BB'><b>From Date</b></font>");
		lblFromDate.setImmediate(true);
		lblFromDate.setContentMode(Label.CONTENT_XHTML);
		lblFromDate.setVisible(false);
		mainLayout.addComponent(lblFromDate, "top:130px;left:450px;");

		lblAsonDate = new Label("<font color='#9110BB'><b>As on Date</b></font>");
		lblAsonDate.setImmediate(true);
		lblAsonDate.setContentMode(Label.CONTENT_XHTML);
		lblAsonDate.setVisible(false);
		mainLayout.addComponent(lblAsonDate, "top:130px;left:450px;");

		dfromDate = new PopupDateField();
		dfromDate.setImmediate(true);
		dfromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfromDate.setDateFormat("dd-MM-yyyy");
		dfromDate.setValue(new java.util.Date());
		dfromDate.setVisible(false);
		mainLayout.addComponent(dfromDate, "top:128px;left:550px;");

		lblMonth = new Label("<font color='#9110BB'><b>Month :</b></font>");
		lblMonth.setImmediate(true);
		lblMonth.setContentMode(Label.CONTENT_XHTML);
		lblMonth.setVisible(false);
		mainLayout.addComponent(lblMonth, "top:130px;left:450px;");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMM");
		dMonth.setValue(new java.util.Date());
		dMonth.setVisible(false);
		mainLayout.addComponent(dMonth, "top:128px;left:550px;");

		lblYear = new Label("<font color='#9110BB'><b>Year :</b></font>");
		lblYear.setImmediate(true);
		lblYear.setContentMode(Label.CONTENT_XHTML);
		lblYear.setVisible(false);
		mainLayout.addComponent(lblYear, "top:130px;left:450px;");

		dYear = new PopupDateField();
		dYear.setImmediate(true);
		dYear.setResolution(PopupDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setValue(new java.util.Date());
		dYear.setVisible(false);
		mainLayout.addComponent(dYear, "top:128px;left:550px;");

		lbltoDate = new Label("<font color='#9110BB'><b>To Date :</b></font>");
		lbltoDate.setImmediate(true);
		lbltoDate.setContentMode(Label.CONTENT_XHTML);
		lbltoDate.setVisible(false);
		mainLayout.addComponent(lbltoDate, "top:155px;left:450px;");

		dtoDate = new PopupDateField();
		dtoDate.setImmediate(true);
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setValue(new java.util.Date());
		dtoDate.setVisible(false);
		mainLayout.addComponent(dtoDate, "top:153px;left:550px;");

		lblSR = new Label("<font color='#9110BB'><b>TSO Name :</b></font>");
		lblSR.setImmediate(true);
		lblSR.setContentMode(Label.CONTENT_XHTML);
		lblSR.setVisible(false);
		mainLayout.addComponent(lblSR, "top:105px;left:450px;");

		cmbSR=new ComboBox();
		cmbSR.setImmediate(true);
		cmbSR.setWidth("200px");
		cmbSR.setHeight("-1px");
		cmbSR.setNullSelectionAllowed(true);
		cmbSR.setVisible(false);
		mainLayout.addComponent(cmbSR, "top:103px;left:550px;");

		//CategoryAll
		chkSr = new CheckBox("All");
		chkSr.setHeight("-1px");
		chkSr.setWidth("-1px");
		chkSr.setImmediate(true);
		chkSr.setVisible(false);
		mainLayout.addComponent(chkSr, "top:103px; left:766.0px;");

		lblCat = new Label("<font color='#9110BB'><b>Category :</b></font>");
		lblCat.setImmediate(true);
		lblCat.setContentMode(Label.CONTENT_XHTML);
		lblCat.setVisible(false);
		mainLayout.addComponent(lblCat, "top:105px;left:450px;");

		cmbCat=new ComboBox();
		cmbCat.setImmediate(true);
		cmbCat.setWidth("200px");
		cmbCat.setHeight("-1px");
		cmbCat.setNullSelectionAllowed(true);
		cmbCat.setVisible(false);
		mainLayout.addComponent(cmbCat, "top:103px;left:550px;");

		chkCat = new CheckBox("All");
		chkCat.setHeight("-1px");
		chkCat.setWidth("-1px");
		chkCat.setImmediate(true);
		chkCat.setVisible(false);
		mainLayout.addComponent(chkCat, "top:103px; left:766.0px;");

		lblOfficeLocation = new Label("<font color='#9110BB'><b>Office :</b></font>");
		lblOfficeLocation.setImmediate(true);
		lblOfficeLocation.setContentMode(Label.CONTENT_XHTML);
		lblOfficeLocation.setVisible(false);
		mainLayout.addComponent(lblOfficeLocation, "top:80px;left:450px;");

		cmbOfficeLocation=new ComboBox();
		cmbOfficeLocation.setImmediate(true);
		cmbOfficeLocation.setWidth("200px");
		cmbOfficeLocation.setHeight("-1px");
		cmbOfficeLocation.setNullSelectionAllowed(true);
		cmbOfficeLocation.setVisible(false);
		mainLayout.addComponent(cmbOfficeLocation, "top:78px;left:550px;");

		//CategoryAll
		chkOfficeLocation = new CheckBox("All");
		chkOfficeLocation.setHeight("-1px");
		chkOfficeLocation.setWidth("-1px");
		chkOfficeLocation.setImmediate(true);
		chkOfficeLocation.setVisible(false);
		mainLayout.addComponent(chkOfficeLocation, "top:78px; left:766.0px;");

		/*btnPrint.setWidth("90px");
		btnPrint.setHeight("28px");
		btnPrint.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(btnPrint, "top:230px;left:490px;");
*/
		/*---------------------------------------- For hButtonLayout-------------------------------------------*/

		lblReportCriteria = new Label("<font color='#06854E' size='2px'><b>Report Criteria</b></font>");
		lblReportCriteria.setImmediate(true);
		lblReportCriteria.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblReportCriteria, "top:30px;left:580px;");

		mainLayout.addComponent(cButton,"top:230px;left:590px;");

		return mainLayout;
	}

	private void ClearData()
	{
		cmbDivision.setValue(null);
		cmbArea.setValue(null);
		dfromDate.setValue(new java.util.Date());
		dtoDate.setValue(new java.util.Date());
		chkDivision.setValue(false);
		chkArea.setValue(false);
		cmbSR.setValue(null);
		dMonth.setValue(new java.util.Date());
		dYear.setValue(new java.util.Date());
		chkCat.setValue(false);
		cmbCat.setValue(null);
		chkOfficeLocation.setValue(false);
		cmbOfficeLocation.setValue(null);
		chkSr.setValue(false);
	}


	private void PartyInvisiblerData(boolean t)
	{
		cmbDivision.setVisible(t);
		cmbArea.setVisible(t);
		dfromDate.setVisible(t);
		dtoDate.setVisible(t);
		lblArea.setVisible(t);
		lblDivision.setVisible(t);
		lblFromDate.setVisible(t);
		lbltoDate.setVisible(t);
		lblAsonDate.setVisible(t);
		lblYear.setVisible(t);
		dMonth.setVisible(t);
		lblMonth.setVisible(t);
		dYear.setVisible(t);
		chkDivision.setVisible(t);
		chkArea.setVisible(t);
		lblSR.setVisible(t);
		cmbSR.setVisible(t);
		lblCat.setVisible(t);
		cmbCat.setVisible(t);
		chkCat.setVisible(t);
		lblOfficeLocation.setVisible(t);
		cmbOfficeLocation.setVisible(t);
		chkOfficeLocation.setVisible(t);
		chkSr.setVisible(t);
	}

	private void optionGroupVisible(boolean a,boolean b,boolean c,boolean d)

	{
		RadioButtonPartyWise.setVisible(a);
		RadioButtonProductWise.setVisible(b);
		RadioButtonSrWise.setVisible(c);
		RadioButtonCollection.setVisible(d);
	}
	private void SetRadio()

	{
		RadioButtonPartyWise.setValue(false);
		RadioButtonProductWise.setValue(false);
		RadioButtonSrWise.setValue(false);
		RadioButtonCollection.setValue(false);
	}

	private void PartyWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k,boolean l,boolean m,boolean n)

	{
		lblDivision.setVisible(a);
		cmbDivision.setVisible(b);
		lblArea.setVisible(c);
		cmbArea.setVisible(d);
		lblFromDate.setVisible(e);
		dfromDate.setVisible(f);
		lbltoDate.setVisible(g);
		dtoDate.setVisible(h);
		lblAsonDate.setVisible(i);
		chkDivision.setVisible(j);
		chkArea.setVisible(k);
		lblOfficeLocation.setVisible(l);
		cmbOfficeLocation.setVisible(m);
		chkOfficeLocation.setVisible(n);
	}

	private void SRWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k)
	{

		lblMonth.setVisible(a);
		dMonth.setVisible(b);
		lblSR.setVisible(c);
		cmbSR.setVisible(d);
		lblDivision.setVisible(e);
		cmbDivision.setVisible(f);
		chkDivision.setVisible(g);
		lblCat.setVisible(h);
		cmbCat.setVisible(i);
		chkCat.setVisible(j);
		chkSr.setVisible(k);
	}

	private void ProductWise(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i)

	{
		lblFromDate.setVisible(a);
		dfromDate.setVisible(b);
		lbltoDate.setVisible(c);
		dtoDate.setVisible(d);
		lblAsonDate.setVisible(e);
		lblMonth.setVisible(f);
		dMonth.setVisible(g);
		lblYear.setVisible(h);
		dYear.setVisible(i);
	}

	private void PartyWiseCollection(boolean a,boolean b,boolean c,boolean d,boolean e,boolean f,boolean g,boolean h,boolean i,boolean j,boolean k,boolean l,boolean m)

	{
		lblOfficeLocation.setVisible(a);
		cmbOfficeLocation.setVisible(b);
		chkOfficeLocation.setVisible(c);
		lblMonth.setVisible(d);
		dMonth.setVisible(e);
		lblYear.setVisible(f);
		dYear.setVisible(g);
		lblDivision.setVisible(h);
		cmbDivision.setVisible(i);
		lblArea.setVisible(j);
		cmbArea.setVisible(k);
		chkDivision.setVisible(l);
		chkArea.setVisible(m);
	}

	private void previewReport()
	{
		String query=null;
		String report = "";
		String Division ="";
		String Area ="";
		String Category ="";
		String SR ="";
		String Office ="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("UserName", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("FromDate", dfromDate.getValue());
			hm.put("ToDate", dtoDate.getValue());
			hm.put("month", dMonth.getValue());
			hm.put("year", dYear.getValue());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());


			if(RadioButtonReportName.getValue().toString().equals("Party Wise Sales") ){

				if(chkDivision.booleanValue()==true)
				{
					Division="%";
					Area="%";
				}
				else
				{
					Division=cmbDivision.getValue().toString();
					if(chkArea.booleanValue()==true)
					{
						Area="%";
					}
					else
					{
						Area=cmbArea.getValue().toString();
					}
				}
				if(RadioButtonPartyWise.getValue().toString().equals("Date Between"))
				{
					query="select * from [funPartyWiseDetails]('"+dateFormat.format(dfromDate.getValue())+"','"+dateFormat.format(dtoDate.getValue())+"','"+Division+"','"+Area+"') order by vDivId,vAreaId";

					report="report/account/DoSales/rptPartyWiseSalesDetails.jasper";

				}
				if(RadioButtonPartyWise.getValue().toString().equals("Summary"))
				{
					query="select 'Pcs' pcs, * from [funPartyWiseSummary]('"+dateFormat.format(dfromDate.getValue())+"','"+Division+"','"+Area+"') ";
					report="report/account/DoSales/rptPartyWiseSalesSummary.jasper";
				}

				if(RadioButtonPartyWise.getValue().toString().equals("Target Vs Achievement"))
				{


					query="select * from [funPartyWiseTargetAchieve]('"+dateFormat.format(dfromDate.getValue())+"','"+m.format(dfromDate.getValue())+"','"+d.format(dfromDate.getValue())+"','"+Division+"','"+Area+"')";
					report="report/account/DoSales/rptPartyWiseTarget&Achieve.jasper";
				}

				if(RadioButtonPartyWise.getValue().toString().equals("Dues"))
				{
					query="select * from [funPartyWiseDues]('"+dateFormat.format(dfromDate.getValue())+"','"+Division+"','"+Area+"') ";
					report="report/account/DoSales/rptPartyWiseDuesSummary.jasper";
				}
			}

			if(RadioButtonReportName.getValue().toString().equals("Product Wise Sales") ){
				if(RadioButtonProductWise.getValue().toString().equals("Date Between"))
				{
					query="select Distinct sd.vProductId,sd.vProductName,sd.vProductUnit,si.vBillNo,si.dInvoiceDate,lg.name," +
							"sum(sd.mChallanQty) mChallanQty,sd.mRate,sum(sd.mAmount) mAmount from tbSalesInvoiceInfo" +
							" si  inner join tbSalesInvoiceDetails sd on si.vBillNo=sd.vBillNo inner join tbLogin lg" +
							" on lg.userId = si.userId inner join tbPartyInfo pin on si.vPartyId=pin.partyCode inner " +
							"join tbAreaInfo ai on ai.vDivisionId=pin.DivisionId inner join tbEmployeeInfo ei on " +
							"ai.vEmployeeId=ei.vEmployeeId where convert(date,si.dInvoiceDate) between '"+dateFormat.format(dfromDate.getValue())+"' " +
							"and '"+dateFormat.format(dtoDate.getValue())+"' group by sd.vProductId,sd.vProductName,sd.vProductUnit,si.vBillNo,si.dInvoiceDate,lg.name," +
							"sd.mRate order by si.vBillNo";

					System.out.println("Report Query: "+query);
					report="report/account/DoSales/rptProductWiseSalesDetails.jasper";
				}

				if(RadioButtonProductWise.getValue().toString().equals("Summary"))
				{
					query="select  'Pcs' pcs,* from [funProductWiseSummary]('"+dateFormat.format(dfromDate.getValue())+"') ";
					report="report/account/DoSales/rptProductWiseSalesSummary.jasper";
				}

				if(RadioButtonProductWise.getValue().toString().equals("Monthly"))
				{
					query=" select Distinct vProductId,vProductName,vProductUnit,sum(mChallanQty) mChallanQty,sum(mAmount)" +
							" mAmount from tbSalesInvoiceDetails  where  month(dBillDate)= '"+m.format(dMonth.getValue())+"' group by" +
							" vProductId,vProductName,vProductUnit order by vProductId";
					report="report/account/DoSales/rptProductWiseMonthlySalesSummary.jasper";
				}

				if(RadioButtonProductWise.getValue().toString().equals("Yearly"))
				{
					query=" select Distinct vProductId,vProductName,vProductUnit,sum(mChallanQty) mChallanQty,sum(mAmount)" +
							" mAmount from tbSalesInvoiceDetails  where  year(dBillDate)='"+yformat.format(dYear.getValue())+"' group by" +
							" vProductId,vProductName,vProductUnit order by vProductId";
					report="report/account/DoSales/rptProductWiseYearlySalesSummary.jasper";
				}
			}

			if(RadioButtonReportName.getValue().toString().equals("TSO Wise Sales") )
			{
				if(RadioButtonSrWise.getValue().toString().equals("Target Vs Achievement(TSO)"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
					}
					if(chkSr.booleanValue()==true)
					{
						SR="%";
					}
					else
					{
						SR=cmbSR.getValue().toString();
					}
					query="select at.vDivisionId,at.vAreaId,at.vDivisionName,at.vAreaName,at.vAreaIncharge,ei.dJoiningDate, ei.vContact," +
							" (at.vFlQty+vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty) vTargetQty,(at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount) " +
							" vTargetAmount, sum(sd.mAmount) achieveAmount,sum(sd.mChallanQty) achieveQty,((sum(sd.mAmount)*100)/" +
							" (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount)) achieveAmountPercentage,((sum(sd.mChallanQty)*100)/" +
							" (at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty)) achieveQtyPercentage,( (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount)-" +
							" sum(sd.mAmount)) rAmountTarget,((at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty)-sum(sd.mChallanQty)) rQtyTarget," +
							" (100-((sum(sd.mAmount)*100)/ (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount))) rTargetAmountPercentage," +
							" (100-((sum(sd.mChallanQty)*100)/(at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty))) rTargetQtyPercentage," +
							" (select DATEDIff(dd ,((CONVERT(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))+'-'+((CONVERT(varchar(50),month('"+dateFormat.format(dMonth.getValue())+"'))))+'-01')," +
							" ((CONVERT(date,DateAdd(dd, -1, DateAdd(mm, 1,(convert(varchar(50)," +
							" Month(convert(varchar(50),'"+mFormat.format(dMonth.getValue())+"') +' 1 ' + convert(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"'))))+'/01/'+convert(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))))))))- " +
							" DATEDIff(dd ," +
							" ((CONVERT(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))+'-'" +
							"+((CONVERT(varchar(50),month('"+dateFormat.format(dMonth.getValue())+"'))))+'-01'),'"+dateFormat.format(dMonth.getValue())+"'))" +
							" DaysLeft," +
							" at.vAreaIncharge from tbAreaTarget at inner join tbPartyInfo pin on pin.AreaId=at.vAreaId inner join " +
							" tbSalesInvoiceInfo si on pin.PartyCode=si.vPartyId  inner join tbSalesInvoiceDetails sd on " +
							" si.vBillNo=sd.vBillNo inner join tbAreaInfo ai on at.vAreaId=ai.vAreaId  inner join " +
							" tbEmployeeInfo ei on ei.vEmployeeId=ai.vEmployeeId  where ei.vEmployeeId like '"+SR+"' and at.vDivisionId like '"+Division+"' " +
							" and MONTH(at.dMonth)=MONTH('"+dateFormat.format(dMonth.getValue())+"') and year(at.dMonth)=year('"+dateFormat.format(dMonth.getValue())+"') " +
							" group by at.vDivisionName,at.vAreaName,at.vAreaIncharge,ei.dJoiningDate," +
							" ei.vContact,at.vDivisionId,at.vAreaId,at.vFlAmount,at.vPnlAmount,at.vFrmAmount,at.vClAmount," +
							" at.vFClAmount,at.vFlQty,at.vPnlQty,at.vFrmQty,at.vClQty,at.vFClQty";
					System.out.println("Report Query: "+query);
					report="report/account/DoSales/rptSRWiseMonthlyTarget&Achieve.jasper";
				}

				if(RadioButtonSrWise.getValue().toString().equals("Monthly Target"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
					}
					query=" select at.dMonth,at.vDivisionId,at.vDivisionName,at.vAreaId,at.vAreaIncharge," +
							" at.vAreaName,at.vDesignation,at.vFlQty,at.vFlAmount,at.vPnlQty,at.vPnlAmount," +
							" at.vFrmQty,at.vFrmAmount,at.vClQty,at.vClAmount,at.vFClQty,at.vFClAmount," +
							" ei.vContact,ei.dJoiningDate from tbAreaTarget at inner join tbAreaInfo ai " +
							" on ai.vAreaId=at.vAreaId " +
							" inner join tbEmployeeInfo ei on ai.vEmployeeId=ei.vEmployeeId where" +
							" at.vDivisionId like '"+Division+"' and MONTH(at.dMonth)=MONTH('"+dateFormat.format(dMonth.getValue())+"') and " +
							" Year(at.dMonth)=Year('"+dateFormat.format(dMonth.getValue())+"') order by at.vDivisionId  ";
					report="report/account/DoSales/rptCatSRWiseMonthlyTarget.jasper";
				}

				if(RadioButtonSrWise.getValue().toString().equals("Target Vs Achievement(Category)"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
					}
					if(chkCat.booleanValue()==true)
					{
						Category="%";
					}
					else
					{
						Category=cmbCat.getValue().toString();
					}
					query="select at.vDivisionId,at.vAreaId,at.vDivisionName,at.vAreaName,at.vAreaIncharge,fi.vCategoryId,fi.vCategoryName," +
							" fi.vSubCategoryId,fi.vSubCategoryName,ei.dJoiningDate, ei.vContact," +
							" (at.vFlQty+vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty) vTargetQty,(at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount) " +
							" vTargetAmount, sum(sd.mAmount) achieveAmount,sum(sd.mChallanQty) achieveQty,((sum(sd.mAmount)*100)/" +
							" (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount)) achieveAmountPercentage,((sum(sd.mChallanQty)*100)/" +
							" (at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty)) achieveQtyPercentage,( (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount)-" +
							" sum(sd.mAmount)) rAmountTarget,((at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty)-sum(sd.mChallanQty)) rQtyTarget," +
							" (100-((sum(sd.mAmount)*100)/ (at.vFlAmount+at.vPnlAmount+at.vFrmAmount+at.vClAmount+at.vFClAmount))) rTargetAmountPercentage," +
							" (100-((sum(sd.mChallanQty)*100)/(at.vFlQty+at.vPnlQty+at.vFrmQty+at.vClQty+at.vFClQty))) rTargetQtyPercentage," +
							" (select DATEDIff(dd ,((CONVERT(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))+'-'+((CONVERT(varchar(50)," +
							" month('"+dateFormat.format(dMonth.getValue())+"'))))+'-01')," +
							" ((CONVERT(date,DateAdd(dd, -1, DateAdd(mm, 1,(convert(varchar(50)," +
							" Month(convert(varchar(50),'"+mFormat.format(dMonth.getValue())+"') +' 1 ' + convert(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"'))))+" +
							"'/01/'+convert(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))))))))- " +
							" DATEDIff(dd ," +
							" ((CONVERT(varchar(50),year('"+dateFormat.format(dMonth.getValue())+"')))+'-'" +
							" +((CONVERT(varchar(50),month('"+dateFormat.format(dMonth.getValue())+"'))))+'-01'),'"+dateFormat.format(dMonth.getValue())+"'))" +
							" DaysLeft," +
							" at.vAreaIncharge from tbAreaTarget at inner join tbPartyInfo pin on pin.AreaId=at.vAreaId inner join " +
							" tbSalesInvoiceInfo si on pin.PartyCode=si.vPartyId  inner join tbSalesInvoiceDetails sd on " +
							" si.vBillNo=sd.vBillNo inner join tbAreaInfo ai on at.vAreaId=ai.vAreaId  inner join " +
							" tbEmployeeInfo ei on ei.vEmployeeId=ai.vEmployeeId inner join tbFinishedProductInfo fi on " +
							" fi.vProductId=sd.vProductId  where at.vDivisionId like '"+Division+"' and  fi.vCategoryId like '"+Category+"' and " +
							" MONTH(at.dMonth)=MONTH('"+dateFormat.format(dMonth.getValue())+"') and year(at.dMonth)=year('"+dateFormat.format(dMonth.getValue())+"') " +
							" group by at.vDivisionName,at.vAreaName,at.vAreaIncharge,ei.dJoiningDate," +
							" ei.vContact,at.vDivisionId,at.vAreaId,at.vFlAmount,at.vPnlAmount,at.vFrmAmount,at.vClAmount," +
							" at.vFClAmount,at.vFlQty,at.vPnlQty,at.vFrmQty,at.vClQty,at.vFClQty,fi.vCategoryId," +
							" fi.vCategoryName,fi.vSubCategoryId,fi.vSubCategoryName ";
					report="report/account/DoSales/rptCatWiseMonthlyTarget&Achieve.jasper";
				}
			}

			if(RadioButtonReportName.getValue().toString().equals("Collection") ){

				if(RadioButtonCollection.getValue().toString().equals("Date Between"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
						Area="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
						if(chkArea.booleanValue()==true)
						{
							Area="%";
						}
						else
						{
							Area=cmbArea.getValue().toString();
						}
					}
					query="select * from [funPartyWiseReceiptDetails]('"+dateFormat.format(dfromDate.getValue())+"','"+dateFormat.format(dtoDate.getValue())+"','"+Division+"','"+Area+"') order by vDivId,vAreaId";
					report="report/account/DoSales/rptPartyWiseReceiptDetails.jasper";
				}

				if(RadioButtonCollection.getValue().toString().equals("Summary"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
						Area="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
						if(chkArea.booleanValue()==true)
						{
							Area="%";
						}
						else
						{
							Area=cmbArea.getValue().toString();
						}
					}
					query="select * from [funPartyWiseCollectionSummary]('"+dateFormat.format(dfromDate.getValue())+"','"+Division+"','"+Area+"') ";
					report="report/account/DoSales/rptPartyWiseCollectionSummary.jasper";
				}
				if(RadioButtonCollection.getValue().toString().equals("Daily Details"))
				{
					System.out.println("Dailly");
					if(chkOfficeLocation.booleanValue()==true || cmbOfficeLocation.getValue()==null)
					{
						Office="%";
					}
					else
					{
						Office=cmbOfficeLocation.getValue().toString();
					}
					query="select * from [funPartyWiseDailyCollection ]('"+dateFormat.format(dfromDate.getValue())+"','"+Office+"') order by vMrNo ";
					report="report/account/DoSales/rptPartyWiseDailyCollection.jasper";
				}

				if(RadioButtonCollection.getValue().toString().equals("Monthly Details"))
				{
					if(chkOfficeLocation.booleanValue()==true || cmbOfficeLocation.getValue()==null)
					{
						Office="%";
					}
					else
					{
						Office=cmbOfficeLocation.getValue().toString();
					}

					query="select * from [funPartyWiseMonthlyCollection]('"+dateFormat.format(dMonth.getValue())+"','"+Office+"') order by vMrNo ";
					report="report/account/DoSales/rptPartyWiseMonthlyCollectionDetails.jasper";
				}

				if(RadioButtonCollection.getValue().toString().equals("Monthly Summary"))
				{
					if(chkDivision.booleanValue()==true)
					{
						Division="%";
						Area="%";
					}
					else
					{
						Division=cmbDivision.getValue().toString();
						if(chkArea.booleanValue()==true)
						{
							Area="%";
						}
						else
						{
							Area=cmbArea.getValue().toString();
						}
					}

					query="select * from [funPartyWiseMonthlyCollectionSummary]('"+dateFormat.format(dMonth.getValue())+"','"+Division+"','"+Area+"') order by vDivId,vAreaId ";
					report="report/account/DoSales/rptPartyWiseMonthlyCollectionSummary.jasper";
				}
				if(RadioButtonCollection.getValue().toString().equals("Yearly"))
				{
					if(chkOfficeLocation.booleanValue()==true || cmbOfficeLocation.getValue()==null)
					{
						Office="%";
					}
					else
					{
						Office=cmbOfficeLocation.getValue().toString();
					}
					query="select * from [funPartyWiseYearlyCollection]('"+yformat.format(dYear.getValue())+"','"+Office+"') order by vMrNo ";
					report="report/account/DoSales/rptPartyWiseYearlyCollection.jasper";
				}

			}

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,report,
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}	

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
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

}
