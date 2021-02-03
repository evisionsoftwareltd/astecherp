package com.example.rawMaterialReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import java.text.DateFormat;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class DepartmentWiseIssue extends Window 
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout middleLayout = new FormLayout();

	private VerticalLayout blankLayout = new VerticalLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	
	private HorizontalLayout chklayout=new HorizontalLayout();
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	int type=0;

	private ComboBox cmbDepartment = new ComboBox("Department Name:");
	private PopupDateField fromDate = new PopupDateField("From Date:");
	private PopupDateField toDate = new PopupDateField("To Date:");

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

	private Label lbLine=new Label("______________________________________________________________");

	private SessionBean sessionBean;
	Formatter fmt;

	String identify;
	String dateWidth = "120px";
	String comboWidth = "280px";

	public DepartmentWiseIssue(SessionBean sessionBean2,String str) 
	{
		this.identify = str;		
		this.sessionBean = sessionBean2;
		this.setCaption("DEPARTMENT WISE ISSUE STATEMENT :: "+sessionBean.getCompany());
		this.setWidth("450px");
		//this.setHeight("245px");
		this.setResizable(false);
		addAllComponentInit();
		addAllComponent();
		this.addComponent(mainLayout);
		addDepartmentName();
		allButtonAction();
	}

	private void addAllComponentInit()
	{                                                                                                                                                                                                                                                                                                                                                                                           
		cmbDepartment.setWidth(comboWidth);
		cmbDepartment.setNullSelectionAllowed(false);
		//cmbDepartment.addItem("All");

		fromDate.setWidth(dateWidth);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		toDate.setWidth(dateWidth);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);

		fromDate.setWidth(dateWidth);

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		buttonLayout.setSpacing(true);
		mainLayout.setSpacing(true);

		Component Object[]={cmbDepartment,fromDate,toDate,previewButton};
		new FocusMoveByEnter(this, Object);
		cmbDepartment.focus();
	}

	private void addAllComponent()
	{
		buttonLayout.addComponent(previewButton);
		buttonLayout.addComponent(exitButton);

		middleLayout.addComponent(cmbDepartment);	
		middleLayout.addComponent(fromDate);
		middleLayout.addComponent(toDate);

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		middleLayout.addComponent(chklayout);
		
		mainLayout.addComponent(middleLayout);
		lbLine.setEnabled(false);
		mainLayout.addComponent(lbLine);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
	}

	private void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx;
		tx = session.beginTransaction();

		String query = "Select * from tbsectionInfo";
		System.out.println(query);

		List list = session.createSQLQuery(query).list();		
		for(Iterator iter = list.iterator(); iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();
			cmbDepartment.addItem(element[1]);
			cmbDepartment.setItemCaption(element[1], element[2].toString());
		}
	}


	private void allButtonAction()
	{
		cmbDepartment.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				//cmpEnbleDisable(true);
			}
		});

		previewButton.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				if(cmbDepartment.getValue()!=null){
					reportShow();
				}
				else{
					getParent().showNotification("Select Section Name", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});


		exitButton.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		chkpdf.addListener(new ValueChangeListener()
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				if(chkpdf.booleanValue()==true)
					chkother.setValue(false);
				
				else
					chkother.setValue(true);
				
			}
		});
		
		chkother.addListener(new ValueChangeListener()
		{
			
			public void valueChange(ValueChangeEvent event)
			{
				
				if(chkother.booleanValue()==true)
					chkpdf.setValue(false);
				
				else
					chkpdf.setValue(true);
				
			}
		});
	}

	private void reportShow()
	{
		String query=null;
		String activeFlag = null;
		
		String sectionID = cmbDepartment.getItemCaption(cmbDepartment.getValue());
		
		if(sectionID.equals("All")){
			sectionID="%";
		}else{
			sectionID = cmbDepartment.getValue().toString();
		}
		
		if(chkpdf.booleanValue())
			type=1;
		else
			type=0;
		
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			String FromDate = dateFormat1.format(fromDate.getValue())+" "+"00:00:00";
			String ToDate =  dateFormat1.format(toDate.getValue())+" "+"23:59:59";
			
			System.out.println("From Date : "+FromDate+", To Date : "+ToDate);
			
			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("company", sessionBean.getCompany());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", "Phone: "+sessionBean.getCompanyPhone()+"   Fax:  "+sessionBean.getCompanyFax()+",   E-mail:  "+sessionBean.getCompanyEmail());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			
			hm.put("fDate",  new SimpleDateFormat("dd-MM-yyyy").format(fromDate.getValue()));
			hm.put("tDate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
            //Select distinct ri.sectionReqNo,ri.sectionId, ri.sectionName,ri.reqNo, convert(date,rd.lastpDate,105) as lastpDate,(select COUNT (rr.productId) from tbRawRequisitionDetails as rr where  ri.reqNo=rr.reqNo ) as total from tbRawRequisitionInfo as ri inner join tbRawRequisitionDetails as rd on ri.reqNo=rd.reqNo where ri.sectionId = '3' and convert(date,lastpDate,105) between '2014-02-02' and '2014-03-03'
			query="select * from dbo.funcSectionWiseIssue('"+sectionID+"','"+FromDate+"','"+ToDate+"') order by issueNo";
			
			System.out.println(query);
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/raw/rptSectionWiseIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report : Section Wise Issue Statement");
				this.getParent().getWindow().addWindow(win);
			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}
}