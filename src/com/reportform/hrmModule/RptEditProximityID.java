package com.reportform.hrmModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptEditProximityID extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSectionName;

	private ComboBox cmbDepartmentName;
	private CheckBox chkDepartmentAll;

	private ComboBox cmbSectionName;
	private CheckBox chkSectionAll;

	private ComboBox cmbEmployeeID;
	private CheckBox chkEmployeeIDAll;

	private Label lblEmployeeName;
	
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private static final String CHO="'DEPT10'";
	public RptEditProximityID(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT PROXIMITY ID :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentDataAdd();
		setEventAction();
	}

	private void setEventAction()
	{
		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
				{
					cmbSectionDataAdd();
				}
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartmentName.setValue(null);
					cmbDepartmentName.setEnabled(false);
					cmbSectionDataAdd();
				}
				else
				{
					cmbDepartmentName.setEnabled(true);
				}
			}
		});

		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeID.removeAllItems();
				if(cmbSectionName.getValue()!=null)
				{
					addCmbEmployeeData(cmbSectionName.getValue().toString());
				}
				else
					showNotification("Warning", "Please select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkSectionAll.booleanValue())
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					addCmbEmployeeData("%");
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});

		chkEmployeeIDAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkEmployeeIDAll.booleanValue())
				{
					cmbEmployeeID.setValue(null);
					cmbEmployeeID.setEnabled(false);
				}
				else
				{
					cmbEmployeeID.setEnabled(true);
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmployeeID.getValue()!=null || chkEmployeeIDAll.booleanValue())
					{
						reportpreview();
					}
					else
					{
						showNotification("Warning","Select Employee ID",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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
		
		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeID.removeAllItems();
				addCmbEmployeeData((cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%"));
			}
		});
	}

	private void addCmbEmployeeData(String Section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vAutoEmployeeId,vEmployeeID from tbEditProximityID where vDepartmentID like " +
					"'"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and vSectionId like " +
					"'"+Section+"' and vDepartmentID!="+CHO+" order by vEmployeeID";
			lblEmployeeName.setValue("Employee ID :");
			
			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vAutoEmployeeId,vEmployeeName from tbEditProximityID where vDepartmentID like " +
						"'"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and vDepartmentID!="+CHO+" order by vEmployeeID";
				lblEmployeeName.setValue("Employee Name :");
			}
			
			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vAutoEmployeeId,vProximityID from tbEditProximityID where vDepartmentID like " +
						"'"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and vSectionId like " +
						"'"+Section+"' and vDepartmentID!="+CHO+" order by vEmployeeID";
				lblEmployeeName.setValue("Proximity ID :");
			}
			
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbEmployeeID.addItem(element[0]);
					cmbEmployeeID.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning", "No Employee Name Found!!!",Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("addCmbEmployeeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("460px");
		setHeight("270px");

		// cmbSectionName
		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setWidth("260px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setNullSelectionAllowed(true);
		cmbDepartmentName.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:30.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:28.0px; left:135.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainLayout.addComponent(chkDepartmentAll, "top:30.0px; left:400.0px;");

		// lblSectionName
		lblSectionName = new Label("Section Name:");
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		mainLayout.addComponent(lblSectionName,"top:60.0px; left:20.0px;");

		// cmbSectionName
		cmbSectionName = new ComboBox();
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(cmbSectionName, "top:58.0px; left:135.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:60.0px; left:400.0px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:90.0px; left:50.0px;");

		lblEmployeeName = new Label("Employee ID : ");
		mainLayout.addComponent(lblEmployeeName, "top:120.0px;left:20.0px;"); 
		
		// cmbEmployeeID
		cmbEmployeeID=new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("260px");
		cmbEmployeeID.setHeight("-1px");
		cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeID, "top:118.0px;left:135.0px;");

		chkEmployeeIDAll = new CheckBox("All");
		chkEmployeeIDAll.setImmediate(true);
		mainLayout.addComponent(chkEmployeeIDAll, "top:120.0px; left:400.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:150.0px;left:135.0px;");

		mainLayout.addComponent(new Label("_____________________________________________________________________________"), "top:170.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:190.opx; left:130.0px");
		return mainLayout;
	}

	private void cmbDepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = "select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEditProximityID ein on dept.vDepartmentId=ein.vDepartmentId where ein.vDepartmentId!="+CHO+" order by dept.vDepartmentName";
			List <?> list = session.createSQLQuery(str).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String str = "select distinct ein.vSectionId,sein.vDepartmentName,sein.SectionName from tbSectionInfo " +
					"sein inner join tbEditProximityID ein on sein.vSectionId=ein.vSectionId where ein.vDepartmentID " +
					"like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and ein.vDepartmentId!="+CHO+" order " +
					"by sein.vDepartmentName,sein.SectionName";
			List <?> list = session.createSQLQuery(str).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportpreview()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String employee = "";
		String Section = "";
		if(chkEmployeeIDAll.booleanValue())
		{
			employee = "%";
		}
		else
		{
			employee = cmbEmployeeID.getValue().toString();
		}
		if(chkSectionAll.booleanValue())
		{
			Section = "%";
		}
		else
		{
			Section = cmbSectionName.getValue().toString();
		}

		try
		{
			String query="select UT.vEmployeeId,UT.employeeCode,UT.vProximityId,UT.vEmployeeName,UT.vStatus,"+
					"UT.vDepartmentID,sein.vDepartmentName,UT.vSectionId,sein.SectionName,UT.vUserName,UT.userIp," +
					"UT.entryTime from (select vEmployeeId,employeeCode,vProximityId,vEmployeeName,'Current' vStatus," +
					"vDepartmentId,vSectionId,vUserName,userIp,entryTime from tbEmployeeInfo where vEmployeeId in (select "+
					"vAutoEmployeeID from tbEditProximityID where vEmployeeID like '"+employee+"') and vSectionId in "+
					"(select vSectionID from tbEditProximityID where vDepartmentID like " +
					"'"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and vDepartmentID!="+CHO+"  and vSectionID " +
					"like '"+Section+"') UNION ALL select vAutoEmployeeID,vEmployeeId,vProximityId,vEmployeeName,'Previous' vStatus," +
					"vDepartmentID,vSectionId,vUserName,vUserIP,dEntryTime from tbEditProximityID where vAutoEmployeeID like '"+employee+"' and "+
					"vSectionId like '"+Section+"') UT inner join tbSectionInfo sein on UT.vSectionId=sein.vSectionId where UT.vDepartmentID like " +
					"'"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' and UT.vDepartmentID!="+CHO+" Order "+
					"by UT.vEmployeeId,UT.vStatus,UT.entryTime";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeProximityID.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

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
			showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			Iterator <?> iter = session.createSQLQuery(sql).list().iterator();
			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}
}
