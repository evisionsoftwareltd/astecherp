package com.reportform.vehicleReport;

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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RptVehicleInfo extends Window 
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	private Label lblVehicleRegNo;
	
	private ComboBox cmbVehicleRegistration;
	
	private CheckBox chkVehicleReg;
	
	private String vehicleRegNo="";
	
	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptVehicleInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("VEHICLE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainlayout();
		setContent(mainLayout);
		
		focusMove();
		
		loadVehicleReg();
		
		setBtnAction();
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
		});		
	}
	
	private void focusMove()
	{
		allComp.add(cmbVehicleRegistration);
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
			showNotification("Warning","Select Vehicle Reg");
			cmbVehicleRegistration.focus();
		}
	}
	
	private void loadVehicleReg()
	{
		Transaction tx;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try
		{
			tx = session.beginTransaction();
			
			String query = " select vehicleId,regNumber from tbVehicleInfo order by vehicleId ";
			System.out.println("Route Query: "+query);
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
			System.out.print(ex);
		}
	}
	
	private void readyReport()
	{
		if(chkVehicleReg.booleanValue()==true)
		{
			vehicleRegNo = "%";
		}
		else
		{			
			if(cmbVehicleRegistration.getValue()!=null)
			{
				vehicleRegNo=cmbVehicleRegistration.getValue().toString().replaceAll("#", "");
			}
			else
			{
				vehicleRegNo="%";
			}
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
			hm.put("companyname", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFaxEmail", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
//			hm.put("logo", sessionBean.getCompanyLogo());

			query=" SELECT * FROM tbVehicleInfo where vehicleId like '"+vehicleRegNo+"' order by vehicleId ";
			
			System.out.println("Report query : "+query);
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
			
				Window win = new ReportViewer(hm,"report/account/vehicleModule/rptVehicleInfo.jasper",
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
		setWidth("400px");
		setHeight("220px");
		
		lblVehicleRegNo  = new Label("Vehicle Reg. No :");
		lblVehicleRegNo.setImmediate(true);
		lblVehicleRegNo.setWidth("100px");
		lblVehicleRegNo.setHeight("-1px");
		mainLayout.addComponent(lblVehicleRegNo, "top:40px; left:30px;");
		
		cmbVehicleRegistration = new ComboBox();
		cmbVehicleRegistration.setImmediate(true);
		cmbVehicleRegistration.setWidth("200px");
		cmbVehicleRegistration.setHeight("-1px");
		mainLayout.addComponent(cmbVehicleRegistration,"top:37px; left:130px;");
		
		chkVehicleReg = new CheckBox("All");
		chkVehicleReg.setImmediate(true);
		mainLayout.addComponent(chkVehicleReg, "top:37px; left:335px;");
		
		mainLayout.addComponent(cButton, "top:135px; left:120px; ");
		
		return mainLayout;
	}
}
