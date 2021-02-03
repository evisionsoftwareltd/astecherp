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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptDeductionSummary extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblDate;

	/*private Label lblEmpType;
	private ComboBox cmbEmpType;*/

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dDate;
	

	private CheckBox chkDepartmentAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	//private CheckBox chkEmployeeTypeAll = new CheckBox("All");

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat1 = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MM");
	private SimpleDateFormat FMonthYear = new SimpleDateFormat("MM-yyyy");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"/*,"Excel"*/});

	public RptDeductionSummary(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("DEDUCTION SUMMARY :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbSalary where " +
					"Month(dDate)=Month('"+dateFormat.format(dDate.getValue())+"') " +
					"and year(dDate)='"+FYear.format(dDate.getValue())+"' and vDepartmentName!='CHO' order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();
			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("cmbDepartmentAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbSectionAddData()
	{
		String dept="%";
		if(chkDepartmentAll.booleanValue()!=true){
			dept=cmbDepartment.getValue().toString();
		}
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query="select distinct ts.SectionId,sein.SectionName from tbSectionInfo sein " +
					"inner join tbSalary ts on sein.vSectionID=ts.SectionId where " +
					"ts.vDepartmentID like '"+dept+"' and Month(dDate)=Month('"+dateFormat.format(dDate.getValue())+"') " +
					"and year(dDate)='"+FYear.format(dDate.getValue())+"' and sein.SectionName!='CHO'  order by sein.SectionName";
			List <?> list = session.createSQLQuery(query).list();
			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("cmbSectionAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dDate.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					if(dDate.getValue()!=null)
					{
						cmbDepartmentAddData();
					}
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				chkSectionAll.setValue(false);
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionAddData();
				}
			}
		});
		chkDepartmentAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue()==true)
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					cmbSectionAddData();
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSectionAll.booleanValue()==true)
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					//cmbSectionAddData();
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});
		
		/*chkEmployeeTypeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkEmployeeTypeAll.booleanValue()==true)
				{
					cmbEmpType.setValue(null);
					cmbEmpType.setEnabled(false);
					//cmbSectionAddData();
				}
				else
				{
					cmbEmpType.setEnabled(true);
				}
			}
		});*/

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dDate.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							reportShow();
							
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
				else
				{
					showNotification("Select Date",Notification.TYPE_WARNING_MESSAGE);
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
		String dept="%";
		String section="%";
		String empType="%";

		if(chkDepartmentAll.booleanValue()!=true){
			dept=cmbDepartment.getValue().toString();
		}
		if(chkSectionAll.booleanValue()!=true){
			section=cmbSection.getValue().toString();
		}
		/*if(chkEmployeeTypeAll.booleanValue()!=true){
			empType=cmbEmpType.getValue().toString();
		}*/

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		try
		{
			query="select dDate,empId as employeeCode,empCode as ProximityId,empName,designation,"
					+ "advanceSalary,vDepartmentName,Section,incomeTax,Insurance,Adjust,Less,userId,entryTime "
					+ "from tbSalary "
					+ "where vDepartmentID like '"+dept+"' "
					+ "and SectionID like '"+section+"' "
					+ "and Month(dDate)='"+FMonthName.format(dDate.getValue())+"' "
					+ "and year(dDate)='"+FYear.format(dDate.getValue())+"' "
					+ "order by vDepartmentName,Section";
			
			/*Month(dDate)=Month('"+dateFormat.format(dDate.getValue())+"') " +
					"and year='"+FYear.format(dDate.getValue())+"'"*/
			
			/*query="select a.vEmployeeId,a.vProximityId,a.vEmployeeName,c.designationName,b.vDepartmentName,"
					+ "b.SectionName,a.mOthersAllowance,a.vEmployeeType,a.OtStatus,a.FridayStatus "
					+ "from tbEmployeeInfo a "
					+ "inner join tbSectionInfo b on a.vSectionId=b.vSectionID "
					+ "inner join tbDesignationInfo c on a.vDesignationId=c.designationId "
					+ "where CONVERT(date,a.entryTime,105)='"+dFormat.format(dDate.getValue())+"' "
					+ "and a.vDepartmentId like '"+dept+"' "
					+ "and a.vSectionId like '"+section+"' "
					+ "and a.vEmployeeType like '"+empType+"'"
					+ "order by a.vDepartmentId,a.vSectionId,a.vEmployeeType";*/
			
			
			//"+dFormat.format(dDate.getValue())+"
			System.out.println(query);
			
			if(queryValueCheck(query))
			{
				HashMap <String, Object> hm = new HashMap <String, Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
				hm.put("SysDate",reportTime.getTime);
				hm.put("dDate",FMonthYear.format(dDate.getValue()));
				hm.put("sql", query);
				
	
				Window win = new ReportViewer(hm,"report/account/hrmModule/RptDeductionSummary.jasper",
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
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
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
		allComp.add(dDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		//allComp.add(cmbEmpType);
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
		setHeight("270px");


		// lblDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate,"top:40.0px; left:20.0px;");

		// dDate
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("140px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("MMMMM-yyyy");
		dDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate, "top:38.0px; left:130.0px;");
		
		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:70.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:68.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:70.0px; left:395px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:100.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:98.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll,"top:100.0px; left:395px;");

		//lblEmpType
		/*lblEmpType=new Label("Employee Type : ");
		mainLayout.addComponent(lblEmpType, "top:100.0px;left:20.0px;");

		//cmbEmpType
		cmbEmpType=new ComboBox();
		cmbEmpType.setImmediate(true);
		cmbEmpType.addItem("Permanent");
		cmbEmpType.addItem("Temporary");
		cmbEmpType.addItem("Provisionary");
		cmbEmpType.addItem("Casual");
		cmbEmpType.setWidth("200px");
		cmbEmpType.setHeight("-1px");
		mainLayout.addComponent(cmbEmpType, "top:98.0px;left:130.0px;");

		chkEmployeeTypeAll = new CheckBox("All");
		chkEmployeeTypeAll.setImmediate(true);
		chkEmployeeTypeAll.setWidth("-1px");
		chkEmployeeTypeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeTypeAll,"top:100.0px; left:330px;");*/

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");
		return mainLayout;
	}
}