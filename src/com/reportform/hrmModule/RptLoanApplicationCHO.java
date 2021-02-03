package com.reportform.hrmModule;

	import java.text.SimpleDateFormat;
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
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.AbstractSelect.Filtering;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.OptionGroup;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;

public class RptLoanApplicationCHO extends Window {
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblEmpName;
		private Label lblAppDate;

		private ComboBox cmbEmpName;
		private ComboBox cmbAppDate;

		private OptionGroup opgEmployee;
		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

		private SimpleDateFormat dFormFormat = new SimpleDateFormat("dd-MM-yyyy");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptLoanApplicationCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("LOAN APPLICATION CHO:: "+sessionBean.getCompany());
			this.setWidth("500px");
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			cmbAddEmployeeName();
			setEventAction();
		}

		private void setEventAction()
		{		
			cmbEmpName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbAppDate.removeAllItems();
					if(cmbEmpName.getValue()!=null)
					{cmbAddApplicationNo(cmbEmpName.getValue().toString());}
				}
			});

			cButton.btnPreview.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbEmpName.getValue()!=null)
					{
						if(cmbAppDate.getValue()!=null)
						{
							reportpreview();
						}
						else
						{
							showNotification("Warning","Select Application Date",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
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
					cmbEmpName.removeAllItems();
					cmbAddEmployeeName();
				}
			});
		}

		private AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setMargin(false);

			// top-level component properties
			setWidth("400px");
			setHeight("220px");

			opgEmployee=new OptionGroup("",lstEmployee);
			opgEmployee.select("Employee ID");
			opgEmployee.setImmediate(true);
			opgEmployee.setStyleName("horizontal");
			mainLayout.addComponent(opgEmployee, "top:20.0px; left:50.0px;");

			// lblEmpName
			lblEmpName = new Label("Employee Name :");
			lblEmpName.setImmediate(false);
			lblEmpName.setWidth("100.0%");
			lblEmpName.setHeight("-1px");
			mainLayout.addComponent(lblEmpName,"top:50.0px; left:30.0px;");

			// cmbEmpName
			cmbEmpName = new ComboBox();
			cmbEmpName.setImmediate(true);
			cmbEmpName.setWidth("220px");
			cmbEmpName.setHeight("-1px");
			cmbEmpName.setNullSelectionAllowed(true);
			cmbEmpName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmpName, "top:48.0px; left:135.0px;");

			// lblAppDate
			lblAppDate = new Label("Application Date :");
			lblAppDate.setImmediate(false);
			lblAppDate.setWidth("100.0%");
			lblAppDate.setHeight("-1px");
			mainLayout.addComponent(lblAppDate,"top:80.0px; left:30.0px;");

			// cmbAppDate
			cmbAppDate = new ComboBox();
			cmbAppDate.setImmediate(true);
			cmbAppDate.setWidth("110px");
			cmbAppDate.setHeight("-1px");
			mainLayout.addComponent(cmbAppDate, "top:78.0px; left:135.0px;");

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:110.0px;left:135.0px;");
			mainLayout.addComponent(cButton,"top:140.opx; left:120.0px");
			return mainLayout;
		}

		private void cmbAddEmployeeName()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = " Select b.vAutoEmployeeId, a.employeeCode from tbEmployeeInfo as a inner join " +
						"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId where b.vDepartmentId='DEPT10' order by b.iAutoId ";
				lblEmpName.setValue("Employee ID :");

				if(opgEmployee.getValue()=="Employee Name")
				{
					querySection = " Select b.vAutoEmployeeId, a.vEmployeeName from tbEmployeeInfo as a inner join " +
							"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId where b.vDepartmentId='DEPT10' order by b.iAutoId ";
					lblEmpName.setValue("Employee Name :");
				}
				else if(opgEmployee.getValue()=="Proximity ID")
				{
					querySection = " Select b.vAutoEmployeeId, a.vProximityID from tbEmployeeInfo as a inner join " +
							"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId where b.vDepartmentId='DEPT10' order by b.iAutoId ";
					lblEmpName.setValue("Proximity ID :");
				}
				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbEmpName.addItem(element[0]);
					cmbEmpName.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddEmployeeName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}	

		private void cmbAddApplicationNo(String empId)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = " select vLoanNo,dApplicationDate from tbLoanApplication where vAutoEmployeeId='"+empId+"' ";
				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	

					cmbAppDate.addItem(element[0]);
					cmbAppDate.setItemCaption(element[0], dFormFormat.format(element[1]));	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddApplicationNo",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void reportpreview()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			try
			{
				String query = " SELECT * from funLoanApplicationInfo ('"+cmbEmpName.getValue().toString()+"','"+(cmbAppDate.getValue())+"') ";
				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptLoanApplication.jasper",
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
