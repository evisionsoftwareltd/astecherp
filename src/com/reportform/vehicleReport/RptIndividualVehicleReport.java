package com.reportform.vehicleReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.ReportViewer;
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
import com.vaadin.ui.Window.Notification;

public class RptIndividualVehicleReport extends Window 
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	
	private Label lblUnitCode;
	private Label lblSubUnitCode;
	
	private ComboBox cmbVehicleNo;
	
	private CheckBox chkVehicleAll;
	
	private String vehicleAll="";
	private String subUnitId="";
	
	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	public RptIndividualVehicleReport(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("INDIVIDUAL VEHICLE REPORT :: "+sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainlayout();
		setContent(mainLayout);
		
		focusMove();
		
		setBtnAction();
		
		loadVehicleRegistration();
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
				
		chkVehicleAll.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(chkVehicleAll.getValue().equals(true))
				{
					cmbVehicleNo.setEnabled(false);
					cmbVehicleNo.setValue(null);
				}
				else
				{
					cmbVehicleNo.setEnabled(true);
				}
			}
		});
	}
	
	private void focusMove()
	{
		allComp.add(cmbVehicleNo);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}
	
	private void formValidation()
	{
		if(cmbVehicleNo.getValue()!=null || chkVehicleAll.booleanValue()==true)
		{
			readyReport();
		}
		else
		{
			getParent().showNotification("Warning!","Select Vehicle Registration No");
		}
	}
	
	private void loadVehicleRegistration()
	{
		cmbVehicleNo.removeAllItems();		
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		
			tx = session.beginTransaction();
			
			String query=" SELECT vehicleId,regNumber from tbVehicleInfo order by autoId ";
			
			System.out.println("Vehicle Query: "+query);

			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbVehicleNo.addItem(element[0]+"#");
				cmbVehicleNo.setItemCaption(element[0]+"#", element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print("Hi"+ex);
		}
	}
	
	private void readyReport()
	{
		if(chkVehicleAll.booleanValue()==true)
		{
			vehicleAll = "%";
		}
		else
		{
			vehicleAll = cmbVehicleNo.getValue().toString().replaceAll("#", "");
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
			hm.put("phoneFaxEmail", " "+sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
//			hm.put("logo", sessionBean.getCompanyLogo());
			
			query=" SELECT * from tbVehicleInfo where vehicleId like '"+vehicleAll+"'  order by vehicleId ";
			
			System.out.println("query : "+query);
			
			if(queryValueCheck(query))
			{
				hm.put("sql", query);
			
				Window win = new ReportViewer(hm,"report/account/vehicleModule/rptIndividualVehicleInfo.jasper",
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
		setHeight("190px");
		
		lblUnitCode = new Label("Vehicle Reg. No. :");
		lblUnitCode.setImmediate(false);
		lblUnitCode.setWidth("100px");
		lblUnitCode.setHeight("-1px");
		mainLayout.addComponent(lblUnitCode, "top:40px; left:30px;");
		
		cmbVehicleNo = new ComboBox();
		cmbVehicleNo.setImmediate(true);
		cmbVehicleNo.setHeight("-1px");
		cmbVehicleNo.setWidth("200px");
		mainLayout.addComponent(cmbVehicleNo, "top:37px; left:130px;");
		
		chkVehicleAll = new CheckBox("All");
		chkVehicleAll.setImmediate(true);		
		mainLayout.addComponent(chkVehicleAll, "top:38px; left:335px;");
		
		mainLayout.addComponent(cButton, "top:105px; left:120px; ");
		
		return mainLayout;
	}
}
