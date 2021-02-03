
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
	import com.vaadin.ui.AbstractSelect.Filtering;
	import com.vaadin.ui.Button.ClickEvent;
	import com.vaadin.ui.Component.Event;
	import com.vaadin.ui.Component.Listener;
	import com.vaadin.ui.Window.Notification;


public class RptAllIncrementListCHO extends Window {

		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		ArrayList<Component> allComp = new ArrayList<Component>();


		private ComboBox cmbIcrementType;
		private ComboBox cmbYear;
		private ComboBox cmbsection;
		private ComboBox cmbDepartment;

		private CheckBox chkDepartmentAll;
		private CheckBox chkSectionAll;

		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();

		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptAllIncrementListCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("INCREMET LIST CHO:: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			addIncrementType();
			setEventAction();
			focusMove();
		}

		public void setEventAction()
		{
			cmbIcrementType.addListener(new ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					cmbYear.removeAllItems();
					if(cmbIcrementType.getValue()!=null)
					{
						addYear();
					}

				}
			});

			cmbYear.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					cmbDepartment.removeAllItems();
					if(cmbYear.getValue()!=null)
					{
						addDepartmentName();
					}
				}
			});

			cmbDepartment.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbsection.removeAllItems();
					if(cmbDepartment.getValue()!=null)
					{
						addSectionName();
					}
				}
			});

			cmbsection.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbsection.getValue()!=null)
					{
						//addemployeeName();
					}
				}
			});

			chkDepartmentAll.addListener(new Listener()
			{
				public void componentEvent(Event event)
				{
					if(chkDepartmentAll.booleanValue()==true)
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

			chkSectionAll.addListener(new Listener()
			{
				public void componentEvent(Event event)
				{
					if(chkSectionAll.booleanValue()==true)
					{
						cmbsection.setValue(null);
						cmbsection.setEnabled(false);
					}
					else
					{
						cmbsection.setEnabled(true);
					}
				}
			});

			cButton.btnPreview.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbIcrementType.getValue()!=null)
					{
						if(cmbYear.getValue()!=null)
						{
							if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
							{
								if(cmbsection.getValue()!=null || chkSectionAll.booleanValue())
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
							showNotification("Select Year",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Select Increment Type",Notification.TYPE_WARNING_MESSAGE);
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

			/*opgEmployee.addListener(new ValueChangeListener()
				{
					public void valueChange(ValueChangeEvent event)
					{
						cmbEmployee.removeAllItems();
						addemployeeName();
					}
				});*/
		}

		public void addIncrementType()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				/*String querySection = "select Datepart(yy,dDate),Datepart(yy,dDate) incrementYear from tbSalaryIncrement ";*/
				String querySection="select distinct vIncrementId, vIncrementType from tbSalaryIncrement";

				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbIcrementType.addItem(element[0]);
					cmbIcrementType.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddYear",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		public void addYear()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = "select Datepart(yy,dDate),Datepart(yy,dDate) incrementYear from tbSalaryIncrement ";


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
		public void addDepartmentName()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct vDepartmentId,vDepartmentName from tbSalaryIncrement " +
						"where YEAR(dDate)='"+cmbYear.getValue().toString() +"' AND vDepartmentName like 'CHO'";
				/*" Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
							"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and" +
									" vDepartmentName!='CHO' order by vDepartmentName";*/

				Iterator <?> itr=session.createSQLQuery(query).list().iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}

			catch(Exception exp)
			{
				showNotification("addDepartmentName : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		public void addSectionName()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select vSectionId,vSectionName from tbSalaryIncrement " +
						"where YEAR(dDate)='"+cmbYear.getValue().toString() +"' and vDepartmentId='"+cmbDepartment.getValue().toString()+"'";
				/*"Month(dDate)=Month('"+dFormat.format(dMonth.getValue())+"') and " +
							"year(dDate)=year('"+dFormat.format(dMonth.getValue())+"') and " +
							"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionName!='CHO' order by vSectionName";*/

				Iterator <?> itr=session.createSQLQuery(query).list().iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbsection.addItem(element[0]);
					cmbsection.setItemCaption(element[0], element[1].toString());
				}
			}

			catch(Exception exp)
			{
				showNotification("addSectionName : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}


		private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String query=null;
			String Department="";
			String section="";
			String rptName="";
			try
			{	

				if(chkDepartmentAll.booleanValue())
				{
					Department="%";
				}

				else
				{
					Department=cmbDepartment.getValue().toString();
			   }
				if(chkSectionAll.booleanValue())
				{
					section="%";
				}

				else
				{
					section=cmbsection.getValue().toString();
				}
				query = "select tsl.vEmployeeId,tsl.employeeCode,tsl.vEmployeeName,tsl.vDesignationName," +
						"ein.dJoiningDate,tsl.vDepartmentName,tsl.vDepartmentId,tsl.vSectionId," +
						"tsl.vSectionName,ein.vGender,tsl.dDate,tsl.vEmployeeType,DateDiff(DD,ein.dInterviewDate,tsl.dDate)/365 dYear," +
						"DateDiff(DD,ein.dInterviewDate,tsl.dDate)%365/30 dMonth,vIncrementType,mNewBasic,mNewGross," +
						"vIncrementPercentage,mIncrementAmount,mBasic,mGross from tbSalaryIncrement tsl inner join tbEmployeeInfo ein on " +
						"ein.vEmployeeId=tsl.vEmployeeId where tsl.vDepartmentId like '"+Department+"' and tsl.vDepartmentId like 'DEPT10'  " +
						"and tsl.vSectionId like '"+section+"' and DATEPART(YY,dDate) like '"+cmbYear.getValue()+"' " +
						"and vIncrementId='"+cmbIcrementType.getValue().toString()+"' " +
						"order by tsl.vDepartmentName,tsl.vSectionName,employeeCode";

				System.out.println(query);
				/*"select * from funMonthlyEmployeeAttendance('"+employeeName+"','"+cmbDepartment.getValue()+"','"+sectionName+"') order by vEmployeeCode,dTxtDate";*/
				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);
					
					Window win = new ReportViewer(hm,"report/account/hrmModule/RptIncrementList.jasper",
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
			setHeight("250px");

			cmbIcrementType=new ComboBox();
			cmbIcrementType.setImmediate(true);
			cmbIcrementType.setWidth("260.0px");
			cmbIcrementType.setHeight("24.0px");
			mainLayout.addComponent(new Label("Increment Type :"), "top:30.0px; left:30.0px;");
			mainLayout.addComponent(cmbIcrementType, "top:28.0px;left:130.0px;");

			cmbYear=new ComboBox();
			cmbYear.setImmediate(true);
			cmbYear.setWidth("100.0px");
			cmbYear.setHeight("24.0px");
			mainLayout.addComponent(new Label("Year :"), "top:60.0px; left:30.0px;");
			mainLayout.addComponent(cmbYear, "top:58.0px;left:130.0px;");

			cmbDepartment=new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("260.0px");
			cmbDepartment.setHeight("24.0px");
			mainLayout.addComponent(new Label("Department :"), "top:90.0px; left:30.0px;");
			mainLayout.addComponent(cmbDepartment, "top:88.0px;left:130.0px;");

	        chkDepartmentAll = new CheckBox("All");
			chkDepartmentAll.setHeight("-1px");
			chkDepartmentAll.setWidth("-1px");
			chkDepartmentAll.setImmediate(true);
			mainLayout.addComponent(chkDepartmentAll, "top:88.0px; left:390.0px;");

			cmbsection=new ComboBox();
			cmbsection.setImmediate(true);
			cmbsection.setWidth("260.0px");
			cmbsection.setHeight("24.0px");
			mainLayout.addComponent(new Label("Section :"), "top:120.0px; left:30.0px;");
			mainLayout.addComponent(cmbsection, "top:118.0px;left:130.0px;");

			chkSectionAll = new CheckBox("All");
			chkSectionAll.setHeight("-1px");
			chkSectionAll.setWidth("-1px");
			chkSectionAll.setImmediate(true);
			mainLayout.addComponent(chkSectionAll, "top:118.0px; left:390.0px;");

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:210.0px;left:200.0px;");

			mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:150.0px; left:20.0px; right:20.0px;");
			mainLayout.addComponent(cButton,"top:170.opx; left:130.0px");
			return mainLayout;
		}
	

}
