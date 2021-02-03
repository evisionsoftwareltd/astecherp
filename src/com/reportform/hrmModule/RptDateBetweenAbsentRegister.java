package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptDateBetweenAbsentRegister extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblFromDate;

	private Label lblEmpID;
	private ComboBox cmbEmpID;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dFrom;
	private PopupDateField dTo;

	private CheckBox chkallemp;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dNFormat = new SimpleDateFormat("dd-MM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private static final String CHO="'DEPT10'";
	public RptDateBetweenAbsentRegister(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE DATE BETWEEN ABSENT REGISTER :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
		focusMove();
		
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct e.vDepartmentID, d.vDepartmentName from tbEmployeeInfo as e inner join"
					+ " tbDepartmentInfo as d on d.vDepartmentId=e.vDepartmentId where e.vDepartmentId!="+CHO+" order by vDepartmentName";

			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}


	public void cmbSectionAddData(String DepartmentID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct e.vSectionId, d.SectionName from tbEmployeeInfo as e inner join"
					+ " tbSectionInfo as d on d.vSectionID=e.vSectionId where d.vDepartmentID='"+DepartmentID+"' and d.vDepartmentId!="+CHO+" ";

			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();){

				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void EmployeeDataAdd()
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		
		String sql="";

		if(opgEmployee.getValue().toString()=="Employee ID")
		{
			lblEmpID.setValue("Employee ID");
			sql="select vEmployeeID,employeeCode from tbEmployeeInfo where "
					+ "vDepartmentId ='"+cmbDepartment.getValue()+"' and vDepartmentId!="+CHO+" and vSectionId='"+cmbSection.getValue()+"' and iStatus like '"+status+"' ";
		}

		else if(opgEmployee.getValue().toString()=="Employee Name")
		{
			lblEmpID.setValue("Employee Name");
			sql="select vEmployeeID,vEmployeeName from tbEmployeeInfo where "
					+ "vDepartmentId ='"+cmbDepartment.getValue()+"' and vDepartmentId!="+CHO+" and vSectionId='"+cmbSection.getValue()+"' and iStatus like '"+status+"'";
		}

		else if(opgEmployee.getValue()=="Proximity ID")
		{
			lblEmpID.setValue("Proximity ID");
			sql="select vEmployeeID,vProximityId from tbEmployeeInfo where "
					+ "vDepartmentId ='"+cmbDepartment.getValue()+"' and vDepartmentId!="+CHO+" and vSectionId='"+cmbSection.getValue()+"' and iStatus like '"+status+"'";
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		try
		{
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbEmpID.addItem(element[0]);
					cmbEmpID.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("EmployeeDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	public void setEventAction()
	{
		dFrom.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dFrom.getValue()!=null)
				{
					cmbDepartmentAddData();
				}
			}
		});

		dTo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dTo.getValue()!=null)
				{
					cmbDepartmentAddData();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData(cmbDepartment.getValue().toString());
				}
			}
		});

	cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(cmbDepartment.getValue()!=null )
				{
					if(cmbSection.getValue()!=null)
					{
						EmployeeDataAdd();
					}
				}
			}
		});


		chkallemp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpID.removeAllItems();
				if(chkallemp.booleanValue())
				{
					cmbEmpID.setValue(null);
					cmbEmpID.setEnabled(false);
				}
				else
					cmbEmpID.setEnabled(true);
			}
		});
		
		 RadioBtnStatus.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event)
			{ 
				cmbEmpID.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						EmployeeDataAdd();
					}
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						EmployeeDataAdd();
					}
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dFrom.getValue()!=null && dTo.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbSection.getValue()!=null)
						{
							if(cmbEmpID.getValue()!=null || chkallemp.booleanValue())
							{
								reportShow();
							}
							else
							{
								showNotification("Warning","Select "+lblEmpID.getValue(),Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);						
					}

				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void reportShow()
	{
		String status="%";
		if(RadioBtnStatus.getValue().equals("Active"))
		{
			status="1";
		}
		else if(RadioBtnStatus.getValue().equals("Left"))
		{
			status="0";
		}
		else
		{
			status="%";
		}
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

		try
		{
			String query="select vDepartmentName,vSectionName,vEmployeeCode,fn.vEmployeeName,fn.vProximityID,vAbsentDate,"
					+ "iMaxAbsentFrequency from funDateBetweenEmployeeAbsent('"+dFormat.format(dFrom.getValue())+"',"
					+ "'"+dFormat.format(dTo.getValue())+"','"+(cmbEmpID.getValue()!=null?cmbEmpID.getValue().toString():"%")+"',"
					+ "'"+cmbDepartment.getValue().toString()+"','"+cmbSection.getValue().toString()+"') fn inner join tbEmployeeInfo ein on fn.vEmployeeID=ein.vEmployeeId where  ein.iStatus like '"+status+"' and ein.vDepartmentId!="+CHO+"  order by vEmployeeCode";

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("fromDate", dNFormat.format(dFrom.getValue()));
				hm.put("toDate", dNFormat.format(dTo.getValue()));
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDateBetweenAbsentRegister.jasper",
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
		catch(Exception exp){
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
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
		finally
		{
			session.close();
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(dFrom);
		allComp.add(dTo);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmpID);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("475px");
		setHeight("320px");

		// lblFromDate
		lblFromDate = new Label("From Date :");
		lblFromDate.setImmediate(false);
		lblFromDate.setWidth("100.0%");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:10.0px; left:30.0px;");

		// dFrom
		dFrom = new PopupDateField();
		dFrom.setImmediate(true);
		dFrom.setWidth("110px");
		dFrom.setHeight("-1px");
		dFrom.setDateFormat("dd-MM-yyyy");
		dFrom.setResolution(PopupDateField.RESOLUTION_DAY);
		dFrom.setValue(new java.util.Date());
		mainLayout.addComponent(dFrom, "top:08.0px; left:140.0px;");

		dTo=new PopupDateField();
		dTo.setImmediate(true);
		dTo.setWidth("110px");
		dTo.setHeight("-1px");
		dTo.setDateFormat("dd-MM-yyyy");
		dTo.setResolution(PopupDateField.RESOLUTION_DAY);
		dTo.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("To Date :"), "top:40.0px; left:30.0px;");
		mainLayout.addComponent(dTo, "top:38.0px; left:140.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:70.0px; left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:140.0px;");

		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:100.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:98.0px; left:140.0px;");
		
		RadioBtnStatus =new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:130.0px;left:30.0px;");
		
		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:160.0px;left:30.0px;");

		//lblEmpID
		lblEmpID=new Label("Employee ID");
		mainLayout.addComponent(lblEmpID, "top:190.0px;left:30.0px;");
		mainLayout.addComponent(new Label(" : "), "top:190.0px;left:125.0px;");

		//cmbEmpID
		cmbEmpID=new ComboBox();
		cmbEmpID.setImmediate(true);
		cmbEmpID.setWidth("260px");
		cmbEmpID.setHeight("-1px");
		cmbEmpID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmpID, "top:188.0px;left:140.0px;");

		chkallemp=new CheckBox("All");
		chkallemp.setImmediate(true);
		mainLayout.addComponent(chkallemp, "top:190.0px;left:415.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:220.0px;left:140.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________"), "top:240.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:250.opx; left:160.0px");
		return mainLayout;
	}
}