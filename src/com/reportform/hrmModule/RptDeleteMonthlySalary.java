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
	import com.vaadin.ui.Button;
	import com.vaadin.ui.CheckBox;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.OptionGroup;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RptDeleteMonthlySalary extends Window {
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;
		private Label lblSalaryYear;
		private Label lblUserID;
		
		private ComboBox cmbSalaryYear;
		private ComboBox cmbSalaryMonth;
		private ComboBox cmbUserID;
		private CheckBox chkDepartmentAll;
		private CheckBox chkSectionAll;

		ArrayList<Component> allComp = new ArrayList<Component>();

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptDeleteMonthlySalary(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("DELETE MONTHLY SALARY:: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			cmbYearAddData();
			setEventAction();
			focusMove();
		}

		public void cmbYearAddData()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct 0,year from tbUDSalary";
				List <?> list=session.createSQLQuery(query).list();	
				
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[])iter.next();
					cmbSalaryYear.addItem(element[1]);
					cmbSalaryYear.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbYearAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		public void cmbMonthAddData(String year)
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct 0,vMonthName from tbUDSalary  where year='"+year+"'";
				List <?> list=session.createSQLQuery(query).list();
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSalaryMonth.addItem(element[1]);
					cmbSalaryMonth.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp){
				showNotification("cmbMonthAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
		
		public void cmbUserIDAddData(String month)
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct 0,userId from tbUDSalary where vMonthName='"+month+"' and year='"+cmbSalaryYear.getValue().toString()+"'";
				List <?> list=session.createSQLQuery(query).list();
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbUserID.addItem(element[1]);
					cmbUserID.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp){
				showNotification("cmbUserIDAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			
		}

		public void setEventAction()
		{
			cmbSalaryYear.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSalaryMonth.removeAllItems();
					if(cmbSalaryYear.getValue()!=null)
					{
						cmbMonthAddData(cmbSalaryYear.getValue().toString());
					}
				}
			});

			cmbSalaryMonth.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbUserID.removeAllItems();
					if(cmbSalaryMonth.getValue()!=null)
					{
						cmbUserIDAddData(cmbSalaryMonth.getValue().toString());
					}
				}
			});


			cButton.btnPreview.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbSalaryYear.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSalaryMonth.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(cmbUserID.getValue()!=null)
							{
								reportShow();
							}
							else
							{
								showNotification("Select User",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Select Salary Month",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Select Salary Year",Notification.TYPE_WARNING_MESSAGE);
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
			try
			{
				String query="select entryTime,userIP,empId,empName from" +
						" tbUDSalary where year='"+cmbSalaryYear.getValue().toString()+"' and" +
						" vMonthName='"+cmbSalaryMonth.getValue().toString()+"' and userId='"+cmbUserID.getValue().toString()+"'" +
								"and vDepartmentID!='DEPT10' ";

				System.out.println(query);
				
				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptDeleteMonthlySalary.jasper",
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
			allComp.add(cmbSalaryYear);
			allComp.add(cmbSalaryMonth);
			allComp.add(cmbUserID);
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
			setHeight("270px");


			// lblSalaryMonth
			lblSalaryYear = new Label("Year :");
			lblSalaryYear.setImmediate(false);
			lblSalaryYear.setWidth("100.0%");
			lblSalaryYear.setHeight("-1px");
			mainLayout.addComponent(lblSalaryYear,"top:30.0px; left:20.0px;");

			// dSalaryMonth
			cmbSalaryYear = new ComboBox();
			cmbSalaryYear.setImmediate(true);
			cmbSalaryYear.setWidth("140px");
			cmbSalaryYear.setHeight("-1px");
			//cmbSalaryMonth.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbSalaryYear, "top:28.0px; left:130.0px;");
			
			cmbSalaryMonth = new ComboBox();
			cmbSalaryMonth.setImmediate(true);
			cmbSalaryMonth.setWidth("140px");
			cmbSalaryMonth.setHeight("-1px");
			//cmbSalaryMonth.setNullSelectionAllowed(true);
			mainLayout.addComponent(new Label("Month : "),  "top:60.0px; left:20.0px;");
			mainLayout.addComponent(cmbSalaryMonth, "top:58.0px; left:130.0px;");

		/*	chkDepartmentAll=new CheckBox("All");
			chkDepartmentAll.setImmediate(true);
			mainLayout.addComponent(chkDepartmentAll, "top:60.0px; left:400.0px;");*/

			// lblSection
			lblUserID = new Label("UserID :");
			lblUserID.setImmediate(false);
			lblUserID.setWidth("100.0%");
			lblUserID.setHeight("-1px");
			mainLayout.addComponent(lblUserID,"top:90.0px; left:20.0px;");

			// cmbSection
			cmbUserID = new ComboBox();
			cmbUserID.setImmediate(true);
			cmbUserID.setWidth("260px");
			cmbUserID.setHeight("-1px");
			cmbUserID.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbUserID, "top:88.0px; left:130.0px;");

		/*	chkSectionAll=new CheckBox("All");
			chkSectionAll.setImmediate(true);
			mainLayout.addComponent(chkSectionAll, "top:90.0px; left:400.0px;");*/

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:130.0px;");

			mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:140.0px;right:20.0px;left:20.0px;");		
			mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");
			return mainLayout;
		}
	}
