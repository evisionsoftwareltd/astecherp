package com.reportform.hrmModule;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.common.share.SalaryExcelReport;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptLeaveEncashmentCHO extends Window {

		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblYear;
		private Label lblSectionName;
		private Label lblEmployeeName;

		private ComboBox cmbYear;
		private ComboBox cmbSectionName;
		/*private CheckBox chkSectionAll;*/
		private ComboBox cmbEmployeeName;
		private CheckBox chkEmployeeAll;

		private OptionGroup opgEmployee;
		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup ogaictive = new OptionGroup();
		private static final List<String> aictiveType = Arrays.asList(new String[]{"Active","Inactive","All"});
		private String stAIctive = "1";



		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

		private SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yy");

		public RptLeaveEncashmentCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("LEAVE ENCASHMENT CHO :: "+sessionBean.getCompany());
			this.setWidth("500px");
			this.setResizable(false);
			buildMainLayout();
			setContent(mainLayout);
			cmbAddYear();
			setEventAction();
		}

		private void cmbAddYear()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = "select distinct YEAR(dLeaveYear)LeaveYear,YEAR(dLeaveYear)LeaveYear from tbLeaveEncashment ";
				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbYear.addItem(element[0]);
					cmbYear.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddYear",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}


		private void cmbSectionDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String str = "select distinct vSectionID,vSectionName from tbLeaveEncashment where YEAR(dLeaveYear)='"+cmbYear.getValue()+"' "
						+ "and vDepartmentName='CHO' order by vSectionID ";
				List <?> list = session.createSQLQuery(str).list();
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbSectionDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void setEventAction()
		{
			cmbYear.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSectionName.removeAllItems();
					if(cmbYear.getValue()!=null)
					{
						cmbSectionDataAdd();
					}
				}
			});


			cButton.btnPreview.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbYear.getValue()!=null)
					{
						if(cmbSectionName.getValue()!=null ) /*|| chkSectionAll.booleanValue()==true*/
						{
							if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue()==true)
							{
								getAlldata();
							}
						}
						else
						{
							showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}

					}
					else
					{
						showNotification("Warning","Select Register Year",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			cmbSectionName.addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event)
				{
					cmbEmployeeName.removeAllItems();
					if(cmbSectionName.getValue()!=null)
					{
						addCmbEmployeeData();
					}
					else
						showNotification("Warning", "Please select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			});

/*			chkSectionAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSectionName.setEnabled(false);
						cmbSectionName.setValue(null);
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			});*/

		chkEmployeeAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployeeName.setEnabled(false);
						cmbEmployeeName.setValue(null);
					}
					else
					{
						cmbEmployeeName.setEnabled(true);
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

			ogaictive.addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(event.getProperty().toString()=="Active")
					{
						stAIctive = "1";
					}
					else if(event.getProperty().toString()=="Inactive")
					{
						stAIctive = "0";
					}
					else
					{
						stAIctive = "%";
					}
				}
			});

			opgEmployee.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbEmployeeName.removeAllItems();
					addCmbEmployeeData();
				}
			});
		}

		private void addCmbEmployeeData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select vEmployeeID,vEmployeeCode from tbLeaveEncashment where vSectionID = '"+cmbSectionName.getValue()+"' order by vEmployeeID";
				lblEmployeeName.setValue("Employee ID :");

				if(opgEmployee.getValue()=="Employee Name")
				{
					query = "select vEmployeeID,vEmployeeName from tbLeaveEncashment where vSectionID = '"+cmbSectionName.getValue()+"' order by vEmployeeID";
					lblEmployeeName.setValue("Employee Name :");
				}

				else if(opgEmployee.getValue()=="Proximity ID")
				{
					query = "select vEmployeeID,vProximityID from tbLeaveEncashment where vSectionID = '"+cmbSectionName.getValue()+"' order by vEmployeeID";
					lblEmployeeName.setValue("Proximity ID :");
				}

				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						cmbEmployeeName.addItem(element[0]);
						cmbEmployeeName.setItemCaption(element[0], element[1].toString());
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
			setWidth("440px");
			setHeight("280px");

			// lblYear
			lblYear = new Label("Year :");
			lblYear.setImmediate(false);
			lblYear.setWidth("100.0%");
			lblYear.setHeight("-1px");
			mainLayout.addComponent(lblYear,"top:10.0px; left:20.0px;");

			// cmbYear
			cmbYear = new ComboBox();
			cmbYear.setImmediate(false);
			cmbYear.setWidth("100px");
			cmbYear.setHeight("-1px");
			cmbYear.setNullSelectionAllowed(true);
			cmbYear.setImmediate(true);
			mainLayout.addComponent(cmbYear, "top:08.0px; left:130.0px;");


			// lblSectionName
			lblSectionName = new Label("Section Name : ");
			lblSectionName.setImmediate(false);
			lblSectionName.setWidth("100.0%");
			lblSectionName.setHeight("-1px");
			mainLayout.addComponent(lblSectionName,"top:40.0px; left:20.0px;");

			// cmbSectionName
			cmbSectionName = new ComboBox();
			cmbSectionName.setWidth("170px");
			cmbSectionName.setHeight("-1px");
			cmbSectionName.setNullSelectionAllowed(true);
			cmbSectionName.setImmediate(true);
			mainLayout.addComponent(cmbSectionName, "top:38.0px; left:130.0px;");

			// chkSectionAll
  /*		
   *     	chkSectionAll = new CheckBox("All");
			chkSectionAll.setImmediate(true);
			chkSectionAll.setWidth("-1px");
			chkSectionAll.setHeight("-1px");
			mainLayout.addComponent(chkSectionAll, "top:38.0px;left:300.0px;");*/

			opgEmployee=new OptionGroup("",lstEmployee);
			opgEmployee.select("Employee ID");
			opgEmployee.setImmediate(true);
			opgEmployee.setStyleName("horizontal");
			mainLayout.addComponent(opgEmployee, "top:70.0px; left:50.0px;");

			lblEmployeeName = new Label("Employee ID:");
			mainLayout.addComponent(lblEmployeeName, "top:100.0px;left:20.0px;");

			// cmbEmployeeName
			cmbEmployeeName=new ComboBox();
			cmbEmployeeName.setImmediate(true);
			cmbEmployeeName.setWidth("170px");
			cmbEmployeeName.setHeight("-1px");
			cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployeeName, "top:98.0px;left:130.0px;");

			// chkEmployeeAll
		    chkEmployeeAll = new CheckBox("All");
			chkEmployeeAll.setImmediate(true);
			chkEmployeeAll.setWidth("-1px");
			chkEmployeeAll.setHeight("-1px");
			mainLayout.addComponent(chkEmployeeAll, "top:98.0px;left:300.0px;");

			ogaictive = new OptionGroup("",aictiveType);
			ogaictive.setImmediate(true);
			ogaictive.setValue("Active");
			ogaictive.setWidth("250px");
			ogaictive.setHeight("-1px");
			ogaictive.setStyleName("horizontal");
			mainLayout.addComponent(ogaictive, "top:130px; left:130px;");

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:160.0px;left:140.0px;");

			mainLayout.addComponent(new Label("_____________________________________________________________________________"), "top:190.0px;left:20.0px;right:20.0px;");
			mainLayout.addComponent(cButton,"top:210.opx; left:130.0px");
			return mainLayout;
		}

		private void getAlldata()
		{
			String employeeName = "%";
			String sectionName = "%";

			if(cmbSectionName.getValue()!=null)
				sectionName = cmbSectionName.getValue().toString();

			if(cmbEmployeeName.getValue()!=null)
				employeeName = cmbEmployeeName.getValue().toString();


			reportShow();
		}

		/*private void reportpreview(Object sectionName,Object activeValue)
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());

			try
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("year", cmbYear.getValue().toString());
				hm.put("SysDate",reportTime.getTime);
				hm.put("path", "./report/account/hrmModule/");
				hm.put("logo", sessionBean.getCompanyLogo());


				String Query="select le.vDepartmentID,le.vDepartmentName,le.vSectionID,le.vSectionName,le.vEmployeeCode,le.vEmployeeName,le.vDesignation,le.mBasic,le.iClTotalDays,le.iClEnjoyedDays,le.iClBalance,le.mClAllowance,le.iSlTotalDays,"
						+ " le.iSlEnjoyedDays,le.iSlBalance,em.dConfirmationDate,le.mSlAllowance,le.iElTotalDays,le.iElEnjoyedDays,le.iElBalance,le.mElAllowance,le.iTotalLeave,le.iTotalEnjoyed,le.iTotalBalance,le.mTotalAmount"
						+ " from tbLeaveEncashment le inner join tbEmployeeinfo em on em.vEmployeeID=le.vEmployeeID where "
						+ " le.vDepartmentID = '"+cmbDepartmentName.getValue().toString()+"' "
						+ "and le.vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' "
						+ "and le.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"'"
						+ " order by le.vDepartmentID ";

				System.out.println("Report Query" + Query);

				if(queryValueCheck(Query))
				{
					hm.put("sql", Query);
					Window win = new ReportViewer(hm,"report/account/hrmModule/RptLeaveEncashment.jasper",
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
				this.getParent().showNotification("reportpreview",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
		}*/


		/*private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			try
			{
				String Query="select le.vDepartmentID,le.vDepartmentName,le.vSectionID,le.vSectionName,le.vEmployeeCode,le.vEmployeeName,le.vDesignation,le.mBasic,le.iClTotalDays,le.iClEnjoyedDays,le.iClBalance,le.mClAllowance,le.iSlTotalDays,"
						+ " le.iSlEnjoyedDays,le.iSlBalance,em.dConfirmationDate,le.mSlAllowance,le.iElTotalDays,le.iElEnjoyedDays,le.iElBalance,le.mElAllowance,le.iTotalLeave,le.iTotalEnjoyed,le.iTotalBalance,le.mTotalAmount"
						+ " from tbLeaveEncashment le inner join tbEmployeeinfo em on em.vEmployeeID=le.vEmployeeID where "
						+ " le.vDepartmentID = '"+cmbDepartmentName.getValue().toString()+"' "
						+ "and le.vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' "
						+ "and le.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"'"
						+ " order by le.vDepartmentID ";

				if(queryValueCheck(Query))
				{
					if(RadioBtnGroup.getValue()=="Excel")
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

						String fname = "LeaveEncashment.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

						File inFile; 
						String header[]=new String[3];
						String reportName = "LeaveEncashment";
						String detailQuery[]=new String[1];
						String GroupQuery[]=new String[1];
						String signatureOption [] = {"Prepared By HR Officer","Checked By HR Executive","Manager (HR & Admin)","Manager (Accounts & Finance)","Approved By"};
						int rowWidth=0;
						header[0]="Department Name : "+cmbDepartmentName.getItemCaption(cmbDepartmentName.getValue());
						header[1]="Section Name : "+cmbSectionName.getItemCaption(cmbSectionName.getValue());
						header[2]="Month : "+(cmbYear.getValue());

						inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/LeaveEncashment.xls");

						detailQuery[0]=	"select le.vEmployeeCode,le.vEmployeeName,le.vDesignation,le.mBasic,le.iClTotalDays,le.iClEnjoyedDays,le.iClBalance,le.mClAllowance,le.iSlTotalDays,"
						+ " le.iSlEnjoyedDays,le.iSlBalance,em.dConfirmationDate,le.mSlAllowance,le.iElTotalDays,le.iElEnjoyedDays,le.iElBalance,le.mElAllowance,le.iTotalLeave,le.iTotalEnjoyed,le.iTotalBalance,le.mTotalAmount"
						+ " from tbLeaveEncashment le inner join tbEmployeeinfo em on em.vEmployeeID=le.vEmployeeID where "
						+ " le.vDepartmentID = '"+cmbDepartmentName.getValue().toString()+"' "
						+ "and le.vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' "
						+ "and le.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"'"
						+ " order by le.vDepartmentID ";

						rowWidth=10;
						new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "LeaveEncashment", 
								reportName, 2, GroupQuery, 2, detailQuery, rowWidth,9,signatureOption);

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
						hm.put("year", cmbYear.getValue().toString());
						hm.put("SysDate",reportTime.getTime);
						hm.put("path", "./report/account/hrmModule/");
						hm.put("logo", sessionBean.getCompanyLogo());


						if(queryValueCheck(Query))
						{
							hm.put("sql", Query);
							Window win = new ReportViewer(hm,"report/account/hrmModule/RptLeaveEncashment.jasper",
									this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
									this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
									this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

							win.setCaption("Project Report");
							this.getParent().getWindow().addWindow(win);
						}
					}
				}
				else
				{
					showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			catch(Exception exp){
				showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);}
		}*/

		private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String Query="";
			String ActiveValue="";
			try
			{

				if(ogaictive.getValue().toString().equals("Active")){
					ActiveValue = "1";}

				else if(ogaictive.getValue().toString().equals("Inactive")){
					ActiveValue = "0";}

				else{
					ActiveValue = "%";}


				Query="select le.vSectionID,le.vSectionName,le.vEmployeeCode,le.vEmployeeName,le.vDesignation,le.mBasic,le.iClTotalDays,le.iClEnjoyedDays,le.iClBalance,le.mClAllowance,le.iSlTotalDays,"
						+ " le.iSlEnjoyedDays,le.iSlBalance,em.dConfirmationDate,le.mSlAllowance,le.iElTotalDays,le.iElEnjoyedDays,le.iElBalance,le.mElAllowance,le.iTotalLeave,le.iTotalEnjoyed,le.iTotalBalance,le.mTotalAmount"
						+ " from tbLeaveEncashment le inner join tbEmployeeinfo em on em.vEmployeeID=le.vEmployeeID where "
						+ "  "
						+ " le.vSectionID like '"+(cmbSectionName.getValue()!=null?cmbSectionName.getValue().toString():"%")+"' "
						+ "and le.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"'"
						+ " and YEAR(le.dLeaveYear)=YEAR('"+cmbYear.getValue()+"') "
						+ "and em.istatus like '"+ActiveValue+"' order by le.vEmployeeCode";
				//rptName="rptMonthlySalary.jasper";
				System.out.println(Query);

				if(queryValueCheck(Query))
				{
					if(RadioBtnGroup.getValue()=="Excel")
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

						String fname = "LeaveEncashment.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

						File inFile; 
						String header [] = new String [0]; 
						String reportName = "Section Name:"+cmbSectionName.getItemCaption(cmbSectionName.getValue())+"    Leave  Year:"+(cmbYear.getValue());
						String detailQuery[]=new String[1];
						String GroupQuery[]=new String[1];
						String signatureOption [] = {};
						int rowWidth=0;

						inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/LeaveEncashment.xls");
						detailQuery[0]="select le.vEmployeeCode,le.vEmployeeName,le.vDesignation,cast(le.mBasic as float)Basic,em.dConfirmationDate,le.iClTotalDays,le.iClEnjoyedDays,le.iClBalance,cast(le.mClAllowance as float)CLAllowance,le.iSlTotalDays,"
								+ " le.iSlEnjoyedDays,le.iSlBalance,cast(le.mSlAllowance as float)SLAllowance,le.iElTotalDays,le.iElEnjoyedDays,le.iElBalance,cast(le.mElAllowance as float)ELAllowance,le.iTotalLeave,le.iTotalEnjoyed,le.iTotalBalance,cast(le.mTotalAmount as float) TotalAmount"
								+ " from tbLeaveEncashment le inner join tbEmployeeinfo em on em.vEmployeeID=le.vEmployeeID where "
								+ "  "
								+ " le.vSectionID like '"+(cmbSectionName.getValue().toString())+"' "
								+ "and le.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"'"
								+ " and YEAR(le.dLeaveYear)=YEAR('"+cmbYear.getValue()+"') and em.istatus like '"+ActiveValue+"' order by le.vEmployeeCode";
						rowWidth=23;
						new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Leave_Encashment", 
								reportName, 2, GroupQuery, 2, detailQuery, rowWidth,6,signatureOption);


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
						hm.put("year", cmbYear.getValue().toString());
						hm.put("SysDate",reportTime.getTime);
						hm.put("path", "./report/account/hrmModule/");
						hm.put("logo", sessionBean.getCompanyLogo());


						if(queryValueCheck(Query))
						{
							hm.put("sql", Query);
							Window win = new ReportViewer(hm,"report/account/hrmModule/RptLeaveEncashment.jasper",
									this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
									this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
									this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

							win.setCaption("Project Report");
							this.getParent().getWindow().addWindow(win);
						}
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
	
}
