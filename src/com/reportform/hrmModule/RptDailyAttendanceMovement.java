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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptDailyAttendanceMovement extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblSectionName;
	private ComboBox cmbSectionName;
	private ComboBox cmbDepartmentName;

	private Label lblMonth;
	private PopupDateField dDate;

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dRptFormat = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});
	private static final String CHO="'DEPT10'";
	public RptDailyAttendanceMovement(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("SECTION WISE DAILY MOVEMENT :: "+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		addDepartmentName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartmentName.removeAllItems();
				if(dDate.getValue()!=null)
					addDepartmentName();
			}
		});
		
		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
					addSectionName();
			}
		});
		
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				String section = "";
				if(cmbSectionName.getValue()!=null)
				{
					section = cmbSectionName.getValue().toString();
					reportShow(section);
				}
				else
				{
					showNotification("Warning","Select Section!!!",Notification.TYPE_WARNING_MESSAGE);
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

	public void addDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance where " +
					"dDate='"+dFormat.format(dDate.getValue())+"' and vDepartmentId!="+CHO+" order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("adDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionId,vSectionName from tbEmployeeMainAttendance where " +
					"dDate='"+dFormat.format(dDate.getValue())+"' and vDepartmentId!="+CHO+" and vDepartmentID='"+cmbDepartmentName.getValue()+"' " +
					"order by vSectionName";
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow(Object Section)
	{
		String DepartmentID="";
		
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		
		try
		{
			String query = "SELECT attendanceDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,shiftId,shiftName," +
					"vEmployeeType,vDesignationID,vDesignationName,iDesignationSerial,vDepartmentID,vDepartmentName," +
					"vSectionID,vSectionName,firstEntry,secondEntry,thirdEntry,fourthEntry,fifthEntry,sixthEntry," +
					"seventhEntry,eighthEntry,ninethEntry,tenthEntry from " +
					"funDailyMovement('"+dFormat.format(dDate.getValue())+"','%','"+cmbDepartmentName.getValue()+"','"+Section+"') where vDepartmentId!="+CHO+" " +
					"order by vSectionName,vEmployeeCode";

			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "SectionWiseDailyMovement_"+dRptFormat.format(dDate.getValue()).toString()+".xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name","1st Entry","2nd Entry","3rd Entry",
							"4th Entry","5th Entry","6th Entry","7th Entry","8th Entry","9th Entry"};

					String Header="Date : "+dRptFormat.format(dDate.getValue());

					String query1="SELECT distinct vDepartmentID,vDepartmentName,vSectionID,vSectionName from " +
					"funDailyMovement('"+dFormat.format(dDate.getValue())+"','%','"+cmbDepartmentName.getValue()+"','"+Section+"') where vDepartmentId!="+CHO+" " +
					"order by vSectionName";

					List <?> lst1=session.createSQLQuery(query1).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"Section Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"",(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3]};
						
						detailQuery[countInd]="SELECT vEmployeeCode,vProximityID,vEmployeeName,vDesignationName," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,firstEntry)),1,8),'')firstEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,secondEntry)),1,8),'')secondEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,thirdEntry)),1,8),'')thirdEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,fourthEntry)),1,8),'')fourthEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,fifthEntry)),1,8),'')fifthEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,sixthEntry)),1,8),'')sixthEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,seventhEntry)),1,8),'')seventhEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,eighthEntry)),1,8),'')eighthEntry," +
								"ISNULL(subString(convert(varchar(50),CONVERT(time,ninethEntry)),1,8),'')ninethEntry " +
								"from funDailyMovement('"+dFormat.format(dDate.getValue())+"','%','"+element[0].toString()+"','"+element[2].toString()+"') " +
								" where vDepartmentId!="+CHO+" order by vSectionName,vEmployeeCode";
						
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Section Wise Daily Movement", "Section Wise Daily Movement",
							Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0,"Legal","LandScape",signatureOption);

					Window window = new Window();
					getApplication().addWindow(window);
					getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);
	
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyAttendenceMovement.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);
	
					win.setCaption("Project Report");
					this.getParent().getWindow().addWindow(win);
				}
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

	private void focusMove()
	{
		allComp.add(dDate);
		allComp.add(cmbDepartmentName);
		allComp.add(cmbSectionName);
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
		setWidth("460px");
		setHeight("260px");

		lblMonth = new Label("Date :");
		lblMonth.setImmediate(false);
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px; left:40.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:08.0px; left:150.0px;");
		
		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setWidth("260px");
		cmbDepartmentName.setHeight("-1px");
		cmbDepartmentName.setNullSelectionAllowed(true);
		cmbDepartmentName.setImmediate(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:40.0px;");
		mainLayout.addComponent(cmbDepartmentName, "top:38.0px; left:150.0px;");
		
		lblSectionName = new Label();
		lblSectionName.setImmediate(false);
		lblSectionName.setWidth("100.0%");
		lblSectionName.setHeight("-1px");
		lblSectionName.setValue("Section Name :");
		mainLayout.addComponent(lblSectionName,"top:70.0px; left:40.0px;");

		// cmbEmployee
		cmbSectionName = new ComboBox();
		cmbSectionName.setWidth("260px");
		cmbSectionName.setHeight("-1px");
		cmbSectionName.setNullSelectionAllowed(true);
		cmbSectionName.setImmediate(true);
		mainLayout.addComponent(cmbSectionName, "top:68.0px; left:150.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:100.0px;left:150.0px;");

		mainLayout.addComponent(new Label("___________________________________________________________________________________________"), "top:120.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:150.0px; left:140.0px");
		return mainLayout;
	}
}
