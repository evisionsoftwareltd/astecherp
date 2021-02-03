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


public class RptDailyPunchReportCHO extends Window {
	


		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblSection;
		private Label lblEmployee;
		private Label lblDate;
		private ComboBox cmbEmployee;

		private ComboBox cmbDepartment;
		private ComboBox cmbSection;
		private PopupDateField dDate;
		private CheckBox chkallemp;


		ArrayList<Component> allComp = new ArrayList<Component>();
		private OptionGroup opgEmployee;
		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();
		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

		public RptDailyPunchReportCHO(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("DAILY PUNCH REPORT CHO :: "+sessionBean.getCompany());
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
				String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
						"where dDate=('"+dFormat.format(dDate.getValue())+"') and vDepartmentId like 'DEPT10' order by vDepartmentName";
				System.out.println("OH"+query);

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
				String query="select distinct vSectionID,vSectionName from tbEmployeeMainAttendance " +
						"where dDate=('"+dFormat.format(dDate.getValue())+"') and vDepartmentID='"+cmbDepartment.getValue()+"' order by vSectionName";
				System.out.println("GO"+query);

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
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select vEmployeeId,employeeCode from tbEmployeeMainAttendance where vDepartmentID = '"+cmbDepartment.getValue()+"' " +
						"and vSectionId = '"+cmbSection.getValue()+"' and dDate=('"+dFormat.format(dDate.getValue())+"') " +
						"order by employeeCode";
				lblEmployee.setValue("Employee ID :");

				if(opgEmployee.getValue()=="Employee Name")
				{
					query = "select vEmployeeId,vEmployeeName from tbEmployeeMainAttendance where vDepartmentID = '"+cmbDepartment.getValue()+"' " +
							"and vSectionId = '"+cmbSection.getValue()+"' and dDate=('"+dFormat.format(dDate.getValue())+"') " +
							"order by employeeCode";
					lblEmployee.setValue("Employee Name :");
				}

				else if(opgEmployee.getValue()=="Proximity ID")
				{
					query = "select vEmployeeId,vProximityID from tbEmployeeMainAttendance where vDepartmentID = '"+cmbDepartment.getValue()+"' " +
							"and vSectionId = '"+cmbSection.getValue()+"' and dDate=('"+dFormat.format(dDate.getValue())+"') " +
							"order by employeeCode";
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
			dDate.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbDepartment.removeAllItems();
					if(dDate.getValue()!=null)
						cmbDepartmentAddData();
				}
			});

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
					if(dDate.getValue()!=null)
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
				ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
				String query=null;
				String employee="";
				Session session = SessionFactoryUtil.getInstance().openSession();
				session.beginTransaction();
				try
				{ 
					if(cmbEmployee.getValue()!=null)
						employee=cmbEmployee.getValue().toString();
					else
					{
						employee="%";
					}

					query="select vEmployeeCode,vProximityID,vEmployeeName,vDepartmentName,vSectionName,vShiftName,vDesignationName,dinTimeOne," +
							"dOutTimeOne from funDailyEmployeeAttendance('"+dFormat.format(dDate.getValue())+"','"+dFormat.format(dDate.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"') order by vEmployeeCode ";

					System.out.println("OMG"+query);
					
					if(queryValueCheck(query))
					{
						if(RadioBtnGroup.getValue()=="Excel")
						{
							String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes" +"/temp/attendanceFolder";
							String fname = "Daily_Salary_Report.xls";
							String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

							String strColName[]={"SL#","Employee ID","Proximity ID","Employee Name","Designation Name",
									"Shift Name","In Date","In Time","Out Date","Out Time"};

							String Header="Department:"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"        Section:"+cmbSection.getItemCaption(cmbSection.getValue())+" ";

							String detailQuery[]=new String[1];
							String [] groupItem=new String[0];
							Object [][] GroupElement=new Object[0][];
							String [] GroupColName=new String[0];
							String [] signatureOption = {""};

							detailQuery[0]="select vEmployeeCode,vProximityID,vEmployeeName,vDesignationName,vShiftName," +
									"convert(Date,dinTimeOne)inDate,ISNULL(subString(convert(varchar(50),CONVERT(time,dInTimeOne)),1,8),'') inTime ," +
									"convert(Date,dOutTimeOne)outDate,ISNULL(subString(convert(varchar(50),CONVERT(time,dOutTimeOne)),1,8),'') outTime " +
									"from funDailyEmployeeAttendance('"+dFormat.format(dDate.getValue())+"','"+dFormat.format(dDate.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"') order by vEmployeeCode ";

							new GenerateExcelReport(sessionBean, loc, url, fname, "DAILY PUNCH REPORT", "DAILY PUNCH REPORT",
									Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0, "", "",signatureOption);

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
						hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
						hm.put("SysDate",reportTime.getTime);
						hm.put("logo", sessionBean.getCompanyLogo());
						hm.put("sql", query);

						Window win = new ReportViewer(hm,"report/account/hrmModule/RptDailyPunch.jasper",
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
			catch(Exception exp){showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
			finally{session.close();}
		}

	/*if(queryValueCheck(query))
	{
		if(RadioBtnGroup.getValue()=="Excel")
		{
			String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes" +"/temp/attendanceFolder";
			String fname = "Daily_Salary_Report.xls";
			String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

			String strColName[]={"SL#","Employee ID","Employee Name","Designation",
					"Gross","Per Day Salary","OT Hours","OT Amount","Payable Amount"};

			String Header="Department:"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"        Section:"+cmbSection.getItemCaption(cmbSection.getValue())+" ";

			String detailQuery[]=new String[1];
			String [] groupItem=new String[0];
			Object [][] GroupElement=new Object[0][];
			String [] GroupColName=new String[0];
			String [] signatureOption = {""};

			detailQuery[0]="SELECT vEmployeeCode,vEmployeeName,vDesignationName,cast(mGross as float) Gross," +
					"cast(Round((mGross/(itotalDay-iholiday)),0)as float) paidAmount," +
					"cast(dOtHour as varchar(120))+':'+CAST(dOTMin as varchar(120))OThours," +
					"cast(Round(((mOTRate*dOtHour)+(mOTRate/60)*dOTMin ),0)as float) OTamount," +
					"cast ((mGross/(itotalDay-iholiday))+Round((((mOTRate*dOtHour)+(mOTRate/60)*dOTMin )),0)as float)payableamount" +
					" from funDailySalarySheet1('"+dFormat.format(dMonth.getValue())+"','"+dFormat.format(dMonth.getValue())+"','"+employee+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"')";

			new GenerateExcelReport(sessionBean, loc, url, fname, "DAILY SALARY REPORT", "DAILY SALARY REPORT",
					Header, strColName, 2,groupItem, GroupColName, GroupElement, 2, detailQuery, 0, 0, "", "",signatureOption);

			Window window = new Window();
			getApplication().addWindow(window);
			getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);

		}*/

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

			lblDate = new Label("Date :");
			lblDate.setImmediate(false);
			lblDate.setWidth("-1px");
			lblDate.setHeight("-1px");
			mainLayout.addComponent(lblDate, "top:10.0px; left:20.0px;");


			dDate = new PopupDateField();
			dDate.setImmediate(true);
			dDate.setWidth("110px");
			dDate.setDateFormat("yyyy-MM-dd");
			dDate.setValue(new java.util.Date());
			dDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dDate, "top:08.0px; left:130.0px;");


			// cmbSection
			cmbDepartment = new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("260px");
			cmbDepartment.setHeight("-1px");
			cmbDepartment.setNullSelectionAllowed(true);
			mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");

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
