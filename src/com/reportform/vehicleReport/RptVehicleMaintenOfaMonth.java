package com.reportform.vehicleReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

public class RptVehicleMaintenOfaMonth extends Window 
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	private static final String[] strType = new String[]{"Regular","Maintenance","Servicing"};
	
	private Label lblFromDate;
	private Label lblMainten;
	
	private ComboBox cmbMainTenType;
		
	private CheckBox chkVehicleMaintenType;
	
	private PopupDateField dMonth;
	
	private String vehicleMainten="";
	private String fromDate="";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
	private SimpleDateFormat dateFormatPerameter = new SimpleDateFormat("yyyy-MM");
	
	//private String Calendar = "";
	
	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptVehicleMaintenOfaMonth(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DAY WISE VEHICLE MAINTENANCE :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainlayout();
		setContent(mainLayout);
		
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
		
		chkVehicleMaintenType.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				allVehicleMainten();
			}
		});
	}
		
	private void allVehicleMainten()
	{
		if(chkVehicleMaintenType.getValue().equals(true))
		{
			cmbMainTenType.setEnabled(false);
			cmbMainTenType.setValue(null);
		}
		else
		{
			cmbMainTenType.setEnabled(true);
		}
	}
	
	private void focusMove()
	{
		allComp.add(dMonth);
		allComp.add(cmbMainTenType);
		allComp.add(cButton.btnPreview);
		

		new FocusMoveByEnter(this,allComp);
	}
	
	private void formValidation()
	{
		if(cmbMainTenType.getValue()!=null || chkVehicleMaintenType.booleanValue()==true)
		{
			readyReport();
		}
		else
		{
			getParent().showNotification("Warning", "Select Maintentype");
		}
	}
	
	private void readyReport()
	{
		if(chkVehicleMaintenType.booleanValue()==true)
		{
			vehicleMainten = "%";
		}
		else
		{
			vehicleMainten = cmbMainTenType.getValue().toString().replaceAll("#", "");
		}
		
		//generateReport();
		getDaysNumber_Upper();
	}
	
	private void getDaysNumber_Upper()
	{
		int year = Integer.parseInt(dateFormatYear.format(dMonth.getValue())); 
		int monthIndex = Integer.parseInt(dateFormatMonth.format(dMonth.getValue()))-1;
		
		Calendar c = new GregorianCalendar(year,monthIndex,1); 
		int numDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		fromDate = dateFormatPerameter.format(dMonth.getValue())+"-0" + 1 + " 00:00:00";
		
//		System.out.println("From Date: "+fromDate+" To:"+toDate);
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

//			query="select * from funDayWiseVehicleStatement('"+fromDate+"','"+toDate+"','"+unitId+"','"+subUnitId+"','"+vehicleMainten+"')";
			
			//System.out.println("Day Wise Report query : "+query);
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
			
				Window win = new ReportViewer(hm,"report/account/vehicleModule/rptDayWiseVehicleMainten.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",false);
			
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
				
				System.out.println("Day Wise Report query : "+query);
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
		setHeight("250px");
				
		lblFromDate=new Label("Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:82px; left:90px;");
		
		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setHeight("-1px");
		dMonth.setWidth("110px");
		dMonth.setDateFormat("MMM-yyyy");
		dMonth.setValue(new java.util.Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dMonth, "top:80px; left:130px;");
		
		
		lblMainten  = new Label("Maintenance Type :");
		lblMainten.setImmediate(true);
		lblMainten.setWidth("120px");
		lblMainten.setHeight("-1px");
		mainLayout.addComponent(lblMainten, "top:107px; left:19px;");
		
		cmbMainTenType = new ComboBox();
		cmbMainTenType.setImmediate(true);
		cmbMainTenType.setWidth("150px");
		cmbMainTenType.setHeight("-1px");
		mainLayout.addComponent(cmbMainTenType,"top:107px; left:130px;");
		for (int i = 0; i < strType.length; i++)
		{
			cmbMainTenType.addItem(strType[i]);
			cmbMainTenType.setItemCaption(strType[i], strType[i].toString());
        }
		
		chkVehicleMaintenType = new CheckBox("All");
		chkVehicleMaintenType.setImmediate(true);
		mainLayout.addComponent(chkVehicleMaintenType, "top:110px; left:280px;");
		
		mainLayout.addComponent(cButton, "top:165px; left:125px; ");
		
		return mainLayout;
	}
}
