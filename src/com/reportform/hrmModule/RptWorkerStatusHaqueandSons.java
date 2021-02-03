package com.reportform.hrmModule;

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
	import com.vaadin.ui.Window.Notification;

public class RptWorkerStatusHaqueandSons extends Window {


		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblSection;
		private Label lblSalaryMonth;

		private Label lblEmpType;
		private ComboBox cmbEmpType;

		private ComboBox cmbDepartment;
		private ComboBox cmbSection;
		private PopupDateField dSalaryMonth;

		private CheckBox chkalldepartment;
		private CheckBox chkallsection;


		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		private SimpleDateFormat dMonthFormat = new SimpleDateFormat("MMMMM-yyyy");
		private SimpleDateFormat dSalaryMonthFormat = new SimpleDateFormat("MMMMM");
		private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");
		private SimpleDateFormat dateFormet=new SimpleDateFormat("MM/dd/yyyy");

		ArrayList<Component> allComp = new ArrayList<Component>();

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptWorkerStatusHaqueandSons(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("WORKER STATUS :: "+sessionBean.getCompany());
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
				String query="select distinct vDepartmentId,vDepartmentName,iAutoId  from tbDepartmentInfo where vDepartmentId like 'DEPT3' or vDepartmentId like 'DEPT5' ORDER by iAutoId";
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
				String query="select distinct vSectionID,SectionName from tbSectioninfo where " +
						"vDepartmentID='"+cmbDepartment.getValue()+"' order by vSectionID";
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

			dSalaryMonth.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbDepartment.removeAllItems();
					if(dSalaryMonth.getValue()!=null)
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
						cmbSectionAddData();
					}
				}
			});



			chkalldepartment.addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event)
				{
					if(chkalldepartment.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
					}

					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			});


			chkallsection.addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event)
				{
					if(chkallsection.booleanValue())
					{
						cmbSection.setValue(null);
						cmbSection.setEnabled(false);
					}

					else
					{
						cmbSection.setEnabled(true);
					}
				}
			});


			cButton.btnPreview.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbDepartment.getValue()!=null || chkalldepartment.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkallsection.booleanValue())
						{
							if(dSalaryMonth.getValue()!=null)
							{
									reportShow();
							}
							else
							{
								showNotification("Select Month",Notification.TYPE_WARNING_MESSAGE);
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

		/*private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String query="";
			String rptName="";
			try
			{
				query="select distinct vDepartmentName,Section,empType, Sum(Gross) Gross,SUM(basicSalary)Basic," +
						"SUM(houseRent)houserent,SUM(Conveyance)conveyance,SUM(Medical)medical," +
						"SUM(AttBonus)AttBonus,SUM(FridayAllowance)FridayAllowance,SUM(advanceSalary)AdvanceSalary," +
						"sum(incomeTax)Incometax,SUM(Insurance)Insurance,SUM(ProvidentFund)ProvidentFund," +
						" SUM(Adjust)Adjust,SUM(Less)Less from tbSalary where SectionID like '%' and " +
						"vDepartmentID Like '%' and empType='Permanent' group by empType,vDepartmentName,Section";

				if(queryValueCheck(query))
				{
					if(RadioBtnGroup.getValue()=="Excel")
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

						String fname = "Monthly_Salary.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

						File inFile; 
						String header [] = new String [0]; 
						String reportName = "Employee Salary("+cmbEmpType.getValue().toString()+")      DepartmentName:"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"      Section Name:"+cmbSection.getItemCaption(cmbSection.getValue())+"    Salary For:"+dMonthFormat.format(dSalaryMonth.getValue());
						String detailQuery[]=new String[1];
						String GroupQuery[]=new String[1];
						String signatureOption [] = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
						int rowWidth=0;
						if(cmbEmpType.getValue().toString().equalsIgnoreCase("Casual") || cmbEmpType.getValue().toString().equalsIgnoreCase("Temporary"))
						{	
							inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/RptMonthlySalaryCasual.xls");
							detailQuery[0]="select empId,empName,designation,cast(Gross as float) Gross,joinDate,present,absentDay," +
									"cast(perDay as int) perDay,cast((Gross-round(absentDay*perDay,0)) as float ) salaryAmt,FridayAllowance,cast((iExtraOT-iLessOT) as float) as extraOT," +
									"convert(varchar,itotalOTHour)+':'+convert(varchar,iTotalOTMin) totalOT,cast(otRate as varchar) otRate,cast(itotalOTHour*otRate+(otRate/60*iTotalOTMin) as float) otAmt," +
									"cast(AttBonus as float) AttBonus,cast(advanceSalary as float) advanceSalary," +
									"cast(totalDeduction+mRevenueStamp as float) totalDeduction,cast((Gross-round(absentDay*perDay,0))+" +
									"(itotalOTHour*otRate+(otRate/60*iTotalOTMin))+AttBonus-advanceSalary-mRevenueStamp as float) totalSalary,cast((Adjust-Less) as float) " +
									"adjustment,cast((round(Gross+AttBonus,0)-round(absentDay*perDay,0))+round(itotalOTHour*otRate+(otRate/60*iTotalOTMin),0)-round(advanceSalary+mRevenueStamp,0)+" +
									"(Adjust-Less)  as float) netPayable,'' as signature,ISNULL((select 'Bank' from tbEmployeeInfo ein where " +
									"ein.vEmployeeId=autoEmployeeID and ein.accountNo!=''),'') Remarks from tbSalary " +
									"where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
									"year=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentID='"+cmbDepartment.getValue().toString()+"' and "+
									"SectionId='"+cmbSection.getValue().toString()+"' and " +
									"empType='"+cmbEmpType.getValue().toString()+"' order by Section,empID";
							rowWidth=23;
							new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Monthly_Salary_Casual", 
									reportName, 2, GroupQuery, 2, detailQuery, rowWidth,6,signatureOption);
						}

						else
						{
							inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/RptMonthlySalaryPermanent.xls");
							detailQuery[0]="select empId,empName,designation,cast(Gross as float) Gross,joinDate,present,absentDay,leaveDay," +
									"cast(basicSalary as float) basicSalary,cast(houseRent as float) houseRent,cast(Conveyance as float) Conveyance," +
									"cast(Medical as float) Medical,cast(AttBonus as float) AttBonus,cast(FridayAllowance as float) FridayAllowance,cast(round(Gross+AttBonus,0) as float) as subTotal," +
									"cast(round(absentDay*perDay,0) as float) as absAmt,cast(advanceSalary as float) advanceSalary,cast(incomeTax as float) incomeTax," +
									"cast(insurance as float) insurance,cast((round(absentDay*perDay,0)+advanceSalary+incomeTax+insurance+" +
									"mRevenueStamp) as float) totalDeduction,cast((Adjust-Less) as float) adjustment," +
									"cast((round(Gross+AttBonus,0)-(round(absentDay*perDay,0)+round(advanceSalary+incomeTax+insurance+" +
									"mRevenueStamp,0))+Adjust-Less) as float) netPayable,'' as signature,ISNULL((select 'Bank' from tbEmployeeInfo ein where " +
									"ein.vEmployeeId=autoEmployeeID and ein.accountNo!=''),'') Remarks from tbSalary " +
									"where vMonthName=DATENAME(MM,'"+dFormat.format(dSalaryMonth.getValue())+"') and " +
									"year=YEAR('"+dFormat.format(dSalaryMonth.getValue())+"') and vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
									"SectionId='"+cmbSection.getValue().toString()+"' and " +
									"empType='"+cmbEmpType.getValue().toString()+"' order by Section,empID";
							rowWidth=25;
							new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Monthly_Salary_Permanent", 
									reportName, 2, GroupQuery, 2, detailQuery, rowWidth,7,signatureOption);
						}

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
						hm.put("Department",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
						hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
						hm.put("SysDate",reportTime.getTime);
						hm.put("logo", sessionBean.getCompanyLogo());
						hm.put("sql", query);

						Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
		}*/

		private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String query=null;
			String Department="";
			String section="";
			String rptName="";
			try
			{

				if(chkalldepartment.booleanValue())
				{
					Department="%";
				}

				else
				{
					Department=cmbDepartment.getValue().toString();
				}

				if(chkallsection.booleanValue())
				{
					section="%";
				}

				else
				{
					section=cmbSection.getValue().toString();
				}


			
					query= " select * "
							+ "from [dbo].[funWorkerStatusNew]( '"+dateFormet.format(dSalaryMonth.getValue())+"','"+Department+"','"+section+"') ";
					rptName="RptWorkerStatus.jasper";
				
				System.out.println("query");

				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("Department",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
					hm.put("month",dMonthFormat.format(dSalaryMonth.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
			setHeight("270px");


			// lblSalaryMonth
			lblSalaryMonth = new Label("Month :");
			lblSalaryMonth.setImmediate(false);
			lblSalaryMonth.setWidth("100.0%");
			lblSalaryMonth.setHeight("-1px");
			mainLayout.addComponent(lblSalaryMonth,"top:10.0px; left:20.0px;");

			// dSalaryMonth
			dSalaryMonth = new PopupDateField();
			dSalaryMonth.setImmediate(true);
			dSalaryMonth.setWidth("140px");
			dSalaryMonth.setHeight("-1px");
			dSalaryMonth.setDateFormat("dd/MM/yyyy");
			dSalaryMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
			dSalaryMonth.setValue(new java.util.Date());
			mainLayout.addComponent(dSalaryMonth, "top:08.0px; left:130.0px;");


			// cmbSection
			cmbDepartment = new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("260px");
			cmbDepartment.setHeight("-1px");
			cmbDepartment.setNullSelectionAllowed(true);
			mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartment, "top:38.0px; left:130.0px;");


			chkalldepartment=new CheckBox("All");
			chkalldepartment.setImmediate(true);
			mainLayout.addComponent(chkalldepartment, "top:40.0px;left:390.0px;");


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


			chkallsection=new CheckBox("All");
			chkallsection.setImmediate(true);
			mainLayout.addComponent(chkallsection, "top:70.0px;left:390.0px;");


/*			//lblEmpType
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
			mainLayout.addComponent(cmbEmpType, "top:98.0px;left:130.0px;");*/

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
