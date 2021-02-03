package com.reportform.vehicleReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class RptMonthWiseVehicleMainten extends Window 
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	private static final String[] strType = new String[]{"Regular","Maintenance","Servicing"};
	
	private Label lblFromDate;
	private Label lblVehicleRegNo;
	
	private ComboBox cmbVehicleRegistration;
	
	private CheckBox chkVehicleReg;
	
	private PopupDateField dMonth;
	
	private String vehicleRegNo="";

	private SimpleDateFormat dateMonth = new SimpleDateFormat("MM");
	private SimpleDateFormat dateYear = new SimpleDateFormat("yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("MMM-yyyy");
	
	String month;
	String year;
	
	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptMonthWiseVehicleMainten(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("MONTH WISE VEHICLE MAINTENANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainlayout();
		setContent(mainLayout);
		
		loadVehicleRegistration();
		
		setBtnAction();
		focusMove();
	}
	
	private void setBtnAction()
	{
		cButton.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});
		
		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		
		chkVehicleReg.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				allVehicleReg();
			}
		});		
	}
	
	private void allVehicleReg()
	{
		if(chkVehicleReg.getValue().equals(true))
		{
			cmbVehicleRegistration.setEnabled(false);
			cmbVehicleRegistration.setValue(null);
		}
		else
		{
			cmbVehicleRegistration.setEnabled(true);
		}
	}
		
	private void focusMove()
	{
		allComp.add(cmbVehicleRegistration);
		allComp.add(dMonth);
		allComp.add(cButton.btnPreview);
		
		new FocusMoveByEnter(this,allComp);
	}
	
	private void formValidation()
	{
		if(cmbVehicleRegistration.getValue()!=null || chkVehicleReg.booleanValue()==true)
		{
			readyReport();					
		}
		else
		{
			getParent().showNotification("Warning", "Select Vehicle Registration No");
		}
	}
	
	private void loadVehicleRegistration()
	{
		cmbVehicleRegistration.removeAllItems();		
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		
			tx = session.beginTransaction();
			
			String query="select vehicleId,regNumber from tbVehicleInfo order by vehicleId";
			
			System.out.println("Vehicle Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbVehicleRegistration.addItem(element[0]+"#");
				cmbVehicleRegistration.setItemCaption(element[0]+"#", element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print("Hi"+ex);
		}
	}
	
	private void readyReport()
	{
		month=dateMonth.format(dMonth.getValue());
		year=dateYear.format(dMonth.getValue());
		
		if(chkVehicleReg.booleanValue()==true)
		{
			vehicleRegNo = "%";
		}
		else
		{
			vehicleRegNo=cmbVehicleRegistration.getValue().toString().replace("#", "");
		}
		generateReport();
	}
	
	private void generateReport()
	{
		String query=null;
		
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFaxEmail", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
//			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("dMonth", dMonth.getValue());

			query = "Select * from funMonthlyVehicleStatement('"+month+"','"+year+"','"+vehicleRegNo+"') ";
			
			System.out.println("Report query : "+query);
			
			if(queryValueCheck(query))
			{	
				hm.put("sql", query);
			
				Window win = new ReportViewer(hm,"report/account/vehicleModule/rptMonthlyVehicleMainten.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",false);
			
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
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
	
	private AbsoluteLayout buildMainlayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("420px");
		setHeight("220px");

		lblVehicleRegNo  = new Label("Vehicle Reg. No :");
		lblVehicleRegNo.setImmediate(true);
		lblVehicleRegNo.setWidth("100px");
		lblVehicleRegNo.setHeight("-1px");
		mainLayout.addComponent(lblVehicleRegNo, "top:30px; left:28px;");
		
		cmbVehicleRegistration = new ComboBox();
		cmbVehicleRegistration.setImmediate(true);
		cmbVehicleRegistration.setWidth("200px");
		cmbVehicleRegistration.setHeight("-1px");
		mainLayout.addComponent(cmbVehicleRegistration,"top:27px; left:130px;");
		
		chkVehicleReg = new CheckBox("All");
		chkVehicleReg.setImmediate(true);
		mainLayout.addComponent(chkVehicleReg, "top:28px; left:335px;");
		
		lblFromDate=new Label("Month :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:75px; left:28px;");
		
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setHeight("-1px");
		dMonth.setWidth("110px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:72px; left:130px;");
		
		mainLayout.addComponent(cButton, "top:140px; left:125px; ");
		
		return mainLayout;
	}
}
