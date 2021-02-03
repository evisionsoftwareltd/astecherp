package com.reportform.hrmModule;



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
public class RptEditEmployeeInformationCHO extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblEmployee;
	
	private ComboBox cmbEmployee;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dSalaryMonth;
	private CheckBox chkallemp;

	private OptionGroup RadioBtnStatus;
	private static final List<String> status=Arrays.asList(new String[]{"Active","Left","All"});
	ArrayList<Component> allComp = new ArrayList<Component>();
	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	public RptEditEmployeeInformationCHO(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("EDIT EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
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
			String query="select distinct  f.vDepartmentId,e.vDepartmentName from " +
					"tbDepartmentInfo e inner join tbudEmployeeInfo " +
					"f on e.vDepartmentId=f.vDepartmentId where e.vDepartmentName='CHO' order by vDepartmentName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct  d.vSectionID,s.SectionName from tbSectionInfo " +
					"s inner join tbUDEmployeeInfo d on s.vSectionID=d.vSectionId where " +
					"d.vDepartmentId='"+cmbDepartment.getValue()+"' order by s.SectionName ";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp){
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void EmployeeDataAdd(String Section)
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
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,employeeCode from tbEmployeeInfo " +
					"where vDepartmentID = '"+cmbDepartment.getValue()+"' and iStatus like '"+status+"' " +
					"and  vSectionId = '"+cmbSection.getValue()+"' and vDepartmentID!='DEPT10'" +
					"order by employeeCode";
			lblEmployee.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo " +
						"where vDepartmentID = '"+cmbDepartment.getValue()+"' and iStatus like '"+status+"'" +
						"and vSectionId = '"+cmbSection.getValue()+"' and vDepartmentID!='DEPT10' " +
						"order by vEmployeeName";
				lblEmployee.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbEmployeeInfo " +
						"where vDepartmentID = '"+cmbDepartment.getValue()+"' and iStatus like '"+status+"'" +
						"and vSectionId = '"+cmbSection.getValue()+"' and vDepartmentID!='DEPT10' " +
						"order by vProximityID";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);
		}
		catch (Exception exp)
		{
			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeDataAdd(cmbSection.getValue().toString());
				}
			}
		});
		RadioBtnStatus.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					EmployeeDataAdd(cmbSection.getValue().toString());
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				EmployeeDataAdd(cmbSection.getValue().toString());
			}
		});

		chkallemp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkallemp.booleanValue())
				{
					cmbEmployee.setValue(null);
					cmbEmployee.setEnabled(false);
					EmployeeDataAdd(cmbSection.getValue().toString());	
				}
				else
					cmbEmployee.setEnabled(true);
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						if(cmbEmployee.getValue()!=null || chkallemp.booleanValue())
						{
							reportShow();

						}
						else
						{
							showNotification("Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Select Section Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Select Department Name",Notification.TYPE_WARNING_MESSAGE);
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
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String employee="";
		try
		{ 
			if(cmbEmployee.getValue()!=null)
				employee=cmbEmployee.getValue().toString();
			else
			{
				employee="%";
			}

			query="select employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo " +
					"din where din.designationId=ein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus," +
					"iStatus,mMonthlySalary,vUserName,dDateTime,vPcIp,mHouseRent,OtStatus," +
					"mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund," +
					"bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee," +
					"'Present' as vUDFlag from tbEmployeeInfo ein where vDepartmentId='"+cmbDepartment.getValue()+"' and vSectionId='"+cmbSection.getValue()+"' and  vEmployeeId like'"+employee+"' union all " +
					"select employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo din where " +
					"din.designationId=uein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus," +
					"iStatus,mMonthlySalary,vUserName,dDateTime,vPcIp,mHouseRent,OtStatus," +
					"mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund," +
					"bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee," +
					"vUDFlag from tbUDEmployeeInfo uein where vDepartmentId='"+cmbDepartment.getValue()+"' and vSectionId='"+cmbSection.getValue()+"' and  vEmployeeId like '"+employee+"' order by vEmployeeName,vUDFlag,employeeCode,dDateTime ";

			System.out.println("OMG"+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("section",cmbSection.getItemCaption(cmbSection.getValue()));
				hm.put("Department",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeInformation.jasper",
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
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
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

	private void focusMove()
	{
		allComp.add(cmbSection);
		allComp.add(dSalaryMonth);
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
		setWidth("450px");
		setHeight("300px");


		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:10.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:08.0px; left:130.0px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:40.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:38.0px; left:130.0px;");
		
		RadioBtnStatus = new OptionGroup("",status);
		RadioBtnStatus.setImmediate(true);
		RadioBtnStatus.setStyleName("horizontal");
		RadioBtnStatus.setValue("Active");
		mainLayout.addComponent(RadioBtnStatus, "top:70.0px;left:40.0px;");
		
		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:100.0px; left:50.0px;");

		//lblEmpType
		lblEmployee=new Label("Employee Name : ");
		mainLayout.addComponent(lblEmployee, "top:130.0px;left:20.0px;");

		//cmbEmpType
		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("260px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:128.0px; left:130.0px;");

		chkallemp=new CheckBox("All");
		chkallemp.setImmediate(true);
		mainLayout.addComponent(chkallemp, "top:130.0px;left:390.0px;");

		
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:160.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:180.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:200.opx; left:175.0px");
		return mainLayout;
	}
}