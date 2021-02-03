package com.reportform.hrmModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
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
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptServiceOfLength extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblSection;
	private Label lblCategory;
	private Label lblEmployeeID;
	private Label lblJobLength;
	private Label lblAsOnDate;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbCategory;
	private ComboBox cmbEmployeeID;
	private ComboBox cmbLengthOfMeasure;

	private OptionGroup opgEmployee;
	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	private CheckBox categoryAll;
	private CheckBox employeeAll;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private PopupDateField dAsOnDate;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private static final String[] category = new String[] {"Permanent", "Temporary", "Provisionary", "Casual"};
	private static final String[] length = new String[] {"Confirmation Date","Joining Date","Interview Date"};

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();
	private OptionGroup opgActive;
	private static final List <String> type2 = Arrays.asList(new String[]{"Active","Inactive","All"});
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final String CHO="'DEPT10'";
	public RptServiceOfLength(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("Length of Service :: "+sessionBean.getCompany());
		this.setResizable(false);;

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		setEventAction();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list=session.createSQLQuery(" SELECT vDepartmentID,vDepartmentName from tbDepartmentInfo where vDepartmentID!="+CHO+" " +
					" order by vDepartmentName ").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
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
			List <?> list=session.createSQLQuery(" SELECT vSectionID,vDepartmentName,SectionName from tbSectionInfo " +
					" where vDepartmentId='"+cmbDepartment.getValue().toString()+"' and vDepartmentID!="+CHO+" order by vDepartmentName,SectionName ").list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbEmployeeIDData(String Section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vEmployeeId,employeeCode from tbEmployeeInfo where vDepartmentID like " +
					"'"+cmbDepartment.getValue()+"' and vSectionId like " +
					"'"+cmbSection.getValue()+"' and vDepartmentID!="+CHO+" order by employeeCode";
			lblEmployeeID.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID like " +
						"'"+cmbDepartment.getValue()+"' and vSectionId like " +
						"'"+cmbSection.getValue()+"' and vDepartmentID!="+CHO+" order by employeeCode";
				lblEmployeeID.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID like " +
						"'"+cmbDepartment.getValue()+"' and vSectionId like " +
						"'"+cmbSection.getValue()+"' and vDepartmentID!="+CHO+" order by employeeCode";
				lblEmployeeID.setValue("Proximity ID :");
			}

			List<?> list=session.createSQLQuery(query).list();	
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployeeID.addItem(element[0]);
				cmbEmployeeID.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeIDData",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
				cmbEmployeeID.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					String Section= cmbSection.getValue().toString();
					cmbEmployeeIDData(Section);
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSection.getValue()!=null)
				{
					if(cmbCategory.getValue()!=null || categoryAll.booleanValue()==true)
					{
						if(cmbEmployeeID.getValue()!=null || employeeAll.booleanValue()==true)
						{
							if(cmbLengthOfMeasure.getValue()!=null)
							{
								if(dAsOnDate.getValue()!=null)
								{
									getAllData();
								}
								else
								{
									showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Measurement",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Category",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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

		categoryAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(categoryAll.booleanValue()==true)
				{	
					cmbCategory.setValue(null);
					cmbCategory.setEnabled(false);
				}
				else
				{
					cmbCategory.setEnabled(true);
				}
			}
		});

		employeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(employeeAll.booleanValue()==true)
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

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeID.removeAllItems();
				if(cmbSection.getValue()!=null)
					cmbEmployeeIDData(cmbSection.getValue().toString());
			}
		});
	}

	private void getAllData()
	{
		String sectionValue = "";
		String categoryValue = "";
		String employeeValue = "";

		if(cmbSection.getValue()!=null)
		{
			sectionValue =cmbSection.getValue().toString();
		}

		if(categoryAll.booleanValue()==true)
		{
			categoryValue = "%";
		}
		else
		{
			categoryValue = cmbCategory.getItemCaption(cmbCategory.getValue());
		}

		if(employeeAll.booleanValue()==true)
		{
			employeeValue = "%";
		}
		else
		{
			employeeValue = cmbEmployeeID.getValue().toString();
		}		
		Object asOnDate=dateFormat.format(dAsOnDate.getValue());
		reportShow(sectionValue,categoryValue,employeeValue,asOnDate);
	}

	private void reportShow(Object sectionValue,Object categoryValue,Object employeeValue,Object asOnDate)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query="";
		String activeFlag = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgActive.getValue()=="Active")
				activeFlag = "1";
			else if(opgActive.getValue()=="Inactive")
				activeFlag = "0";
			else
				activeFlag = "%";   

			if(cmbLengthOfMeasure.getValue().toString().equals("Confirmation Date"))
			{
				query = " SELECT dept.vDepartmentName,b.SectionName," +
						" vEmployeeType,vEmployeeName,employeecode,c.designationName,dJoiningDate,dInterviewDate,convert(date,dConfirmationDate) dConfirmationDate ," +
						" dConfirmationDate confDate,"+
						" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dConfirmationDate)*10000-MONTH(dConfirmationDate)*100-DAY(dConfirmationDate))/10000 as year," +
						" (1200 + (month('"+asOnDate+"')-month(dConfirmationDate))*100+day('"+asOnDate+"')-day(dConfirmationDate))/100 %12 as month," +
						" (sign(day('"+asOnDate+"')-day(dConfirmationDate))+1)/2*(day('"+asOnDate+"')-day(dConfirmationDate))+(sign(day(dConfirmationDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dConfirmationDate),112),6)+'01')))-day(dConfirmationDate)+day('"+asOnDate+"')) as day,"+
						" a.iStatus from tbEmployeeInfo" +
						" as a inner join tbDepartmentInfo as dept on dept.vDepartmentID=a.vDepartmentID inner join " +
						" tbSectionInfo as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as" +
						" c on a.vDesignationId=c.designationId where a.iStatus like '"+activeFlag+"' and a.vDepartmentID='"+cmbDepartment.getValue().toString()+"' " +
						" and a.vSectionId like '"+sectionValue+"' and  vEmployeeType like '"+categoryValue+"' and " +
						" vEmployeeId like '"+employeeValue+"'and isnull(dConfirmationDate,'')!='' and dConfirmationDate<='"+asOnDate+"'  and a.vDepartmentID!="+CHO+" order by vDepartmentName," +
						"SectionName,a.iStatus,vEmployeeType,employeeCode";
			}
			else if(cmbLengthOfMeasure.getValue().toString().equals("Joining Date"))
			{
				query = " SELECT dept.vDepartmentName,b.SectionName," +
						" vEmployeeType,vEmployeeName,employeecode,c.designationName,dJoiningDate,dInterviewDate,convert(date,dConfirmationDate) dConfirmationDate ," +
						" dConfirmationDate confDate,"+
						" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dJoiningDate)*10000-MONTH(dJoiningDate)*100-DAY(dJoiningDate))/10000 as year," +
						" (1200 + (month('"+asOnDate+"')-month(dJoiningDate))*100+day('"+asOnDate+"')-day(dJoiningDate))/100 %12 as month," +
						" (sign(day('"+asOnDate+"')-day(dJoiningDate))+1)/2*(day('"+asOnDate+"')-day(dJoiningDate))+(sign(day(dJoiningDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dJoiningDate),112),6)+'01')))-day(dJoiningDate)+day('"+asOnDate+"')) as day,"+
						" a.iStatus from tbEmployeeInfo" +
						" as a inner join tbDepartmentInfo as dept on dept.vDepartmentID=a.vDepartmentID inner join " +
						" tbSectionInfo as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as" +
						" c on a.vDesignationId=c.designationId where a.iStatus like '"+activeFlag+"' and a.vDepartmentID='"+cmbDepartment.getValue().toString()+"' " +
						" and a.vSectionId like '"+sectionValue+"' and  vEmployeeType like '"+categoryValue+"' and vEmployeeId " +
						" like '"+employeeValue+"' and isnull(dJoiningDate,'')!='' and dJoiningDate<='"+asOnDate+"'  and a.vDepartmentID!="+CHO+" order by vDepartmentName,SectionName,a.iStatus," +
						"vEmployeeType,employeeCode";
				
				
			}
			
			else   
			{
				query = " SELECT dept.vDepartmentName,b.SectionName," +
						" vEmployeeType,vEmployeeName,employeecode,c.designationName,dJoiningDate,dInterviewDate,convert(date,dConfirmationDate) dConfirmationDate ," +
						" dConfirmationDate confDate,"+
						" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dInterviewDate)*10000-MONTH(dInterviewDate)*100-DAY(dInterviewDate))/10000 as year," +
						" (1200 + (month('"+asOnDate+"')-month(dInterviewDate))*100+day('"+asOnDate+"')-day(dInterviewDate))/100 %12 as month," +
						" (sign(day('"+asOnDate+"')-day(dInterviewDate))+1)/2*(day('"+asOnDate+"')-day(dInterviewDate))+(sign(day(dInterviewDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dInterviewDate),112),6)+'01')))-day(dInterviewDate)+day('"+asOnDate+"')) as day,"+
						" a.iStatus from tbEmployeeInfo" +
						" as a inner join tbDepartmentInfo as dept on dept.vDepartmentID=a.vDepartmentID inner join " +
						" tbSectionInfo as b on a.vSectionId=b.vSectionId inner join tbDesignationInfo as" +
						" c on a.vDesignationId=c.designationId where a.iStatus like '"+activeFlag+"' and a.vDepartmentID='"+cmbDepartment.getValue().toString()+"' " +
						" and a.vSectionId like '"+sectionValue+"' and  vEmployeeType like '"+categoryValue+"' and vEmployeeId " +
						" like '"+employeeValue+"' and isnull(dInterviewDate,'')!='' and dInterviewDate<='"+asOnDate+"'  and a.vDepartmentID!="+CHO+" order by vDepartmentName,SectionName,a.iStatus," +
						" vEmployeeType,employeeCode";

			}

			if(queryValueCheck(query))
			{
				try
				{
					if(RadioBtnGroup.getValue()=="Excel")
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
						String fname = "Serviceoflength.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

						String strColName[]={"SL#","Employee ID","Employee Name","Designation","Interview Date","Joining Date","Confirmation Date","Year","Month","Day"};
						String Header="";

						String GroupQuery=" select distinct d.vDepartmentId,d.vDepartmentName,s.vSectionID,s.SectionName,a.vEmployeeType from tbEmployeeInfo " +
								" as a inner join tbSectionInfo as s on s.vSectionID=a.vSectionId inner join tbDepartmentInfo" +
								" as d on d.vDepartmentId=a.vDepartmentId where a.vSectionId like '"+sectionValue+"' and a.vDepartmentId like '"+cmbDepartment.getValue().toString()+"' and" +
								"  a.vEmployeeType like '"+categoryValue+"'  and a.vDepartmentID!="+CHO+" order by d.vDepartmentName,s.SectionName,a.vEmployeeType";

						List <?> lst1=session.createSQLQuery(GroupQuery).list();

						String detailQuery[]=new String[lst1.size()];
						String [] groupItem=new String[lst1.size()];
						Object [][] GroupElement=new Object[lst1.size()][];
						String [] GroupColName=new String[0];
						String [] signatureOption = new String [0];
						int countInd=0;

						for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
						{
							Object [] element = (Object[]) itr1.next();
							groupItem[countInd]="Department Name : "+element[1].toString()+"  Section Name : "+element[3].toString()+"   Category Name : "+element[4].toString();
							GroupElement[countInd]=new Object [] {(Object)"",(Object)"Section Name : ",element[1],(Object)"Category Name : ",element[4]};

							if(cmbLengthOfMeasure.getValue()=="Confirmation Date")
							{
								detailQuery[countInd] = "select a.employeeCode,a.vEmployeeName,b.designationName,a.dInterviewDate,a.dJoiningDate," +
										" SUBSTRING(a.dConfirmationDate,9,2)+'-'+SUBSTRING(dConfirmationDate,6,2)+'-'+SUBSTRING(dConfirmationDate,1,4) ConfDate," +
										" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dConfirmationDate)*10000-MONTH(dConfirmationDate)*100-DAY(dConfirmationDate))/10000 as year," +
										" (1200 + (month('"+asOnDate+"')-month(dConfirmationDate))*100+day('"+asOnDate+"')-day(dConfirmationDate))/100 %12 as month," +
										" (sign(day('"+asOnDate+"')-day(dConfirmationDate))+1)/2*(day('"+asOnDate+"')-day(dConfirmationDate))+(sign(day(dConfirmationDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dConfirmationDate),112),6)+'01')))-day(dConfirmationDate)+day('"+asOnDate+"')) as day "+
										" from tbEmployeeInfo as a inner join tbDesignationInfo " +
										" as b on a.vDesignationId = b.designationId inner join tbDepartmentInfo as d on d.vDepartmentId = a.vDepartmentId  " +
										" where a.iStatus like '"+activeFlag+"' and d.vDepartmentID like '"+element[0].toString()+"' and vSectionId like '"+sectionValue+"' and " +
										" vEmployeeType like '"+element[4].toString()+"' and vEmployeeId like '"+employeeValue+"'" +
										" and (dConfirmationDate<='"+asOnDate+"' and ISNULL(dConfirmationDate,'')!='')  and a.vDepartmentID!="+CHO+" order by " +
										" a.iStatus,a.employeeCode,vEmployeeType,dJoiningDate";	

								System.out.println("Shehab"+detailQuery[countInd]);

							}

							else if(cmbLengthOfMeasure.getValue()=="Joining Date")
							{
								detailQuery[countInd] = " select a.employeeCode,a.vEmployeeName,b.designationName,a.dInterviewDate,a.dJoiningDate," +
										" (case when (isnull(dConfirmationDate,'')!='' and  year(CONVERT(date,dConfirmationDate))!=1900) then SUBSTRING(a.dConfirmationDate,9,2)+'-'+SUBSTRING(dConfirmationDate,6,2)+'-'+SUBSTRING(dConfirmationDate,1,4) else '' end ) dConfirmationDate," +
										" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dJoiningDate)*10000-MONTH(dJoiningDate)*100-DAY(dJoiningDate))/10000 as year," +
										" (1200 + (month('"+asOnDate+"')-month(dJoiningDate))*100+day('"+asOnDate+"')-day(dJoiningDate))/100 %12 as month," +
										" (sign(day('"+asOnDate+"')-day(dJoiningDate))+1)/2*(day('"+asOnDate+"')-day(dJoiningDate))+(sign(day(dJoiningDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dJoiningDate),112),6)+'01')))-day(dJoiningDate)+day('"+asOnDate+"')) as day "+
										" from " +
										" tbEmployeeInfo as a inner join tbDesignationInfo as b on a.vDesignationId = b.designationId inner join tbDepartmentInfo as d on d.vDepartmentId = a.vDepartmentId  where a.iStatus like" +
										" '"+activeFlag+"' and vSectionId like '"+sectionValue+"' and d.vDepartmentID like '"+element[0].toString()+"' and vEmployeeType like  '"+element[4].toString()+"' and vEmployeeId like'"+employeeValue+"' " +
										" and (dJoiningDate<='"+asOnDate+"' and ISNULL(dJoiningDate,'')!='')  and a.vDepartmentID!="+CHO+" order by a.iStatus,a.employeeCode,vEmployeeType," +
										" dJoiningDate";

							}
							else 
							{
								detailQuery[countInd] ="select  a.employeeCode,a.vEmployeeName,b.designationName,a.dInterviewDate,a.dJoiningDate," +
										" (case when (isnull(dConfirmationDate,'')!='' and  year(CONVERT(date,dConfirmationDate))!=1900) then SUBSTRING(a.dConfirmationDate,9,2)+'-'+SUBSTRING(dConfirmationDate,6,2)+'-'+SUBSTRING(dConfirmationDate,1,4) else '' end ) dConfirmationDate," +
										" (YEAR('"+asOnDate+"')*10000 + MONTH('"+asOnDate+"')*100 + DAY('"+asOnDate+"')-YEAR(dInterviewDate)*10000-MONTH(dInterviewDate)*100-DAY(dInterviewDate))/10000 as year," +
										" (1200 + (month('"+asOnDate+"')-month(dInterviewDate))*100+day('"+asOnDate+"')-day(dInterviewDate))/100 %12 as month," +
										" (sign(day('"+asOnDate+"')-day(dInterviewDate))+1)/2*(day('"+asOnDate+"')-day(dInterviewDate))+(sign(day(dInterviewDate)-day('"+asOnDate+"'))+1)/2*((day(dateadd(d,-1,left(convert(varchar(8),dateadd(m,1,dInterviewDate),112),6)+'01')))-day(dInterviewDate)+day('"+asOnDate+"')) as day "+
										" from " +
										" tbEmployeeInfo as a inner join tbDesignationInfo as b on a.vDesignationId = b.designationId inner join tbDepartmentInfo as d on d.vDepartmentId = a.vDepartmentId " +
										" where a.iStatus like '"+activeFlag+"' and vSectionId like '"+sectionValue+"' and d.vDepartmentID like '"+element[0].toString()+"' " +
										" and vEmployeeType like '"+element[4].toString()+"' and vEmployeeId like '"+employeeValue+"'" +
										" and (dInterviewDate<='"+asOnDate+"' and YEAR(dInterviewDate)!='1900')   and a.vDepartmentID!="+CHO+" order by a.iStatus,a.employeeCode,vEmployeeType,dJoiningDate";
							}
							countInd++;
						}
						new GenerateExcelReport(sessionBean, loc, url, fname, "length_of_sertvice_report","LENGTH OF SERVICE",
								Header, strColName, 2,groupItem, GroupColName, GroupElement, 1, detailQuery,0,0,"A4","LandScape",signatureOption);

						Window window = new Window();
						getApplication().addWindow(window);
						getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
					}
					else
					{
						HashMap <String,Object> hm = new HashMap <String,Object> ();
						hm.put("section", cmbSection.getItemCaption(cmbSection.getValue()));
						hm.put("Department", cmbDepartment.getItemCaption(cmbDepartment.getValue()));
						hm.put("company", sessionBean.getCompany());
						hm.put("address", sessionBean.getCompanyAddress());
						hm.put("phone", sessionBean.getCompanyContact());
						hm.put("lengthMeasure", cmbLengthOfMeasure.getItemCaption(cmbLengthOfMeasure.getValue().toString()));
						hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
						hm.put("SysDate",reportTime.getTime);
						hm.put("logo", sessionBean.getCompanyLogo());
						hm.put("date", dAsOnDate.getValue());
						hm.put("sql", query);

						Window win = new ReportViewer(hm,"report/account/hrmModule/RptService_lenghth.jasper",
								this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
								this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

						win.setCaption("Project Report");
						this.getParent().getWindow().addWindow(win);
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				catch(Exception exp)
				{
					showNotification("reportView "+exp,Notification.TYPE_ERROR_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp){showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
		finally{session.close();}
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

	public AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("520px");
		setHeight("370px");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("-1px");
		mainLayout.addComponent(new Label("Department Name : "), "top:20.0px; left:25.0px;");
		mainLayout.addComponent(cmbDepartment, "top:18.0px; left:160.0px;");

		// lblSection
		lblSection = new Label("Section Name : ");
		lblSection.setImmediate(true);
		lblSection.setWidth("100px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:50.0px; left:25.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("-1px");
		mainLayout.addComponent(cmbSection, "top:48.0px; left:160.0px;");

		// lblCategory
		lblCategory = new Label("Category : ");
		lblCategory.setImmediate(true);
		lblCategory.setWidth("100px");
		lblCategory.setHeight("-1px");
		mainLayout.addComponent(lblCategory, "top:80px; left:25px;");

		// cmbCategory
		cmbCategory = new ComboBox();
		cmbCategory.setImmediate(true);
		cmbCategory.setWidth("280px");
		cmbCategory.setHeight("-1px");
		mainLayout.addComponent(cmbCategory, "top:78.0px; left:160.0px;");
		for(int i=0; i<category.length; i++)
		{cmbCategory.addItem(category[i]);}

		// categoryAll
		categoryAll = new CheckBox("All");
		categoryAll.setImmediate(true);
		categoryAll.setWidth("-1px");
		categoryAll.setHeight("-1px");
		mainLayout.addComponent(categoryAll,"top:80px; left:445px;");

		opgEmployee=new OptionGroup("",lstEmployee);
		opgEmployee.select("Employee ID");
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:110.0px; left:50.0px;");

		// lblEmployeeName
		lblEmployeeID = new Label("Employee ID : ");
		lblEmployeeID.setImmediate(true);
		lblEmployeeID.setWidth("100px");
		lblEmployeeID.setHeight("-1px");
		mainLayout.addComponent(lblEmployeeID, "top:140px; left:25px;");

		// cmbEmployeeName
		cmbEmployeeID = new ComboBox();
		cmbEmployeeID.setImmediate(true);
		cmbEmployeeID.setWidth("280px");
		cmbEmployeeID.setHeight("-1px");
		cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployeeID, "top:138.0px; left:160.0px;");

		// employeeAll
		employeeAll = new CheckBox("All");
		employeeAll.setImmediate(true);
		employeeAll.setWidth("-1px");
		employeeAll.setHeight("-1px");
		mainLayout.addComponent(employeeAll,"top:140px; left:445px;");

		// lblJobLength
		lblJobLength = new Label("Length Measure From : ");
		lblJobLength.setImmediate(true);
		lblJobLength.setWidth("150px");
		lblJobLength.setHeight("-1px");
		mainLayout.addComponent(lblJobLength, "top:170px; left:25px;");

		// cmbLengthOfMeasure
		cmbLengthOfMeasure = new ComboBox();
		cmbLengthOfMeasure.setImmediate(true);
		cmbLengthOfMeasure.setWidth("280px");
		cmbLengthOfMeasure.setHeight("-1px");
		mainLayout.addComponent(cmbLengthOfMeasure, "top:168.0px; left:160.0px;");
		for(int i=0; i<length.length; i++)
		{cmbLengthOfMeasure.addItem(length[i]);}

		// lblAsOnDate
		lblAsOnDate = new Label("As on Date : ");
		lblAsOnDate.setImmediate(true);
		lblAsOnDate.setWidth("100px");
		lblAsOnDate.setHeight("-1px");
		mainLayout.addComponent(lblAsOnDate, "top:200px; left:25px;");

		// cmbLengthOfMeasure
		dAsOnDate = new PopupDateField();
		dAsOnDate.setImmediate(true);
		dAsOnDate.setWidth("110px");
		dAsOnDate.setHeight("-1px");
		dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dAsOnDate.setDateFormat("dd-MM-yyyy");
		dAsOnDate.setValue(new java.util.Date());
		mainLayout.addComponent(dAsOnDate, "top:198.0px; left:160.0px;");

		// optionGroup
		opgActive = new OptionGroup("",type2);
		opgActive.setImmediate(true);
		opgActive.setValue("Active");
		opgActive.setStyleName("horizontal");
		opgActive.setValue("PDF");
		mainLayout.addComponent(opgActive, "top:230.0px;left:180.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:260.0px;left:180.0px;");
		mainLayout.addComponent(cButton, "top:290.0px;left:180.0px;");
		return mainLayout;
	}
}
