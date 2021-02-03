package acc.appform.hrmModule;

import java.io.File;
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
import com.common.share.SalaryExcelReport;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
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
public class NewEmployeeList extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblDate;

	private Label lblEmpType;
	private ComboBox cmbEmpType;

	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dFromDate,dToDate;
	

	private CheckBox chkDepartmentAll = new CheckBox("All");
	private CheckBox chkSectionAll = new CheckBox("All");
	private CheckBox chkEmployeeTypeAll = new CheckBox("All");
	
	private SimpleDateFormat dFormat1 = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"/*,"Excel"*/});
	private static final String CHO="'DEPT10'";
	public NewEmployeeList(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("NEW EMPLOYEE LIST :: "+sessionBean.getCompany());
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
			String query="select distinct vDepartmentId,"
					+ "(select vDepartmentName from tbDepartmentInfo where vDepartmentId=a.vDepartmentId) as vDepartmentName "
					+ "from tbEmployeeInfo a "
					+ "where CONVERT(date,a.dJoiningDate,105) = '"+dFormat.format(dFromDate.getValue())+"' and a.vDepartmentId!="+CHO+" ";
			
			
			/*String query="select distinct vDepartmentId,vDepartmentName from tbSalary where " +
					"Month(dFromDate) = Month('"+dFormat.format(dFromDate.getValue())+"') and vDepartmentName !='CHO' order by vDepartmentName";
			*/
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
		String dept="%";
		if(chkDepartmentAll.booleanValue()!=true){
			dept=cmbDepartment.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,"
					+ "(select SectionName from tbSectionInfo where vSectionID=a.vSectionId) as SectionName   "
					+ "from tbEmployeeInfo a "
					+ "where CONVERT(date,a.dJoiningDate,105) = '"+dFormat.format(dFromDate.getValue())+"'  "
					+ "AND a.vDepartmentId like '"+dept+"'  and a.vDepartmentId!="+CHO+" ";
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

	public void setEventAction()
	{

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dFromDate.getValue()!=null)
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
					cmbSection.removeAllItems();
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
		chkEmployeeTypeAll.addListener(new ClickListener()
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
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dFromDate.getValue()!=null)
				{
					if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							
							if(cmbEmpType.getValue()!=null || chkEmployeeTypeAll.booleanValue())
							{
								reportShow();
							}
							else
							{
								showNotification("Select Employee Type",Notification.TYPE_WARNING_MESSAGE);
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
		if(chkEmployeeTypeAll.booleanValue()!=true){
			empType=cmbEmpType.getValue().toString();
		}

		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;

		try
		{
			query="select a.vEmployeeId,a.employeeCode,a.vProximityId,a.vEmployeeName,c.designationName,b.vDepartmentName,"
					+ "b.SectionName,a.mOthersAllowance,a.vEmployeeType,a.OtStatus,a.FridayStatus "
					+ "from tbEmployeeInfo a "
					+ "inner join tbSectionInfo b on a.vSectionId=b.vSectionID "
					+ "inner join tbDesignationInfo c on a.vDesignationId=c.designationId "
					+ "where CONVERT(date,a.dJoiningDate,105) = '"+dFormat.format(dFromDate.getValue())+"' "
					+ "and a.vDepartmentId like '"+dept+"' "
					+ "and a.vSectionId like '"+section+"' "
					+ "and a.vEmployeeType like '"+empType+"' and a.vDepartmentId!="+CHO+" "
					+ "order by a.vDepartmentId,a.vSectionId,a.vEmployeeType";
			
			
			//"+dFormat.format(dFromDate.getValue())+"
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
				hm.put("dDate",dFormat1.format(dFromDate.getValue()));
				hm.put("sql", query);
				
				
	
				Window win = new ReportViewer(hm,"report/account/hrmModule/rptNewEmployeeList.jasper",
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
		allComp.add(dFromDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmpType);
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
		mainLayout.addComponent(lblDate,"top:10.0px; left:20.0px;");

		// dFromDate
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setHeight("-1px");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		mainLayout.addComponent(dFromDate, "top:08.0px; left:130.0px;");
		
		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setHeight("-1px");
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		//mainLayout.addComponent(new Label("To :"),"top:10.0px; left:250px;");
		//mainLayout.addComponent(dToDate, "top:08.0px; left:280px;");
		
		/*dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setHeight("-1px");
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		mainLayout.addComponent(dFromDate, "top:265.0px;left:128.0px;");*/


		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");

		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		mainLayout.addComponent(chkDepartmentAll,"top:40.0px; left:395px;");

		// lblSection
		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection,"top:70.0px; left:20.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:130.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll,"top:70.0px; left:395px;");

		//lblEmpType
		lblEmpType=new Label("Employee Type : ");
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
		mainLayout.addComponent(chkEmployeeTypeAll,"top:100.0px; left:330px;");

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