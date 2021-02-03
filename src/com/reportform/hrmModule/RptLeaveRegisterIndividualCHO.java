package com.reportform.hrmModule;

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
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.AbstractSelect.Filtering;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.OptionGroup;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;


public class RptLeaveRegisterIndividualCHO extends Window {
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Label lblYear;
		private Label lblSectionName;
		private Label lblEmployeeName;

		private ComboBox cmbYear;
		private ComboBox cmbDepartmentName;
		private ComboBox cmbSectionName;
		private ComboBox cmbEmployeeName;

		private OptionGroup opgEmployee;
		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup ogactive = new OptionGroup();
		private static final List<String> activeType = Arrays.asList(new String[]{"Active","Inactive","All"});
		private String stAIctive = "1";

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptLeaveRegisterIndividualCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("INDIVIDUAL LEAVE REGISTER CHO:: "+sessionBean.getCompany());
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
				String querySection = " SELECT distinct DATEPART(year, currentYear) as yearId,DATEPART(year, currentYear) as " +
						"balanceYear from tbLeaveBalanceNew ";
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

		private void cmbDepartmentDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String str = " SELECT distinct lbn.vDepartmentID,dept.vDepartmentName from tbLeaveBalanceNew lbn inner join" +
						" tbDepartmentInfo dept on lbn.vDepartmentID=dept.vDepartmentId where " +
						"YEAR(currentYear)='"+cmbYear.getValue().toString()+"' and dept.vDepartmentName='CHO' order by lbn.vDepartmentID ";
				List <?> list = session.createSQLQuery(str).list();
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbDepartmentDataAdd",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbSectionDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String str = " SELECT distinct lbn.vSectionID,sein.vDepartmentName,sein.SectionName from tbLeaveBalanceNew lbn " +
						"inner join tbSectionInfo sein on lbn.vSectionID=sein.vSectionID where " +
						"YEAR(currentYear)='"+cmbYear.getValue().toString()+"' and " +
						"lbn.vDepartmentID='"+cmbDepartmentName.getValue().toString()+"' " +
						"order by sein.SectionName ";
				List <?> list = session.createSQLQuery(str).list();
				for(Iterator <?> iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
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
					cmbDepartmentName.removeAllItems();
					if(cmbYear.getValue()!=null)
					{
						cmbDepartmentDataAdd();
					}
				}
			});

			cmbDepartmentName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSectionName.removeAllItems();
					if(cmbDepartmentName.getValue()!=null)
					{
						cmbSectionDataAdd();
					}
				}
			});

			ogactive.addListener(new ValueChangeListener() 
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

			cButton.btnPreview.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbYear.getValue()!=null)
					{
						if(cmbDepartmentName.getValue()!=null)
						{
							if(cmbSectionName.getValue()!=null)
							{
								if(!stAIctive.equals(""))
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
							showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
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
				String query="select lbn.vAutoEmployeeId,ein.employeeCode from tbLeaveBalanceNew lbn inner join tbEmployeeInfo ein " +
						"on lbn.vAutoEmployeeId=ein.vEmployeeId where lbn.vDepartmentID='"+cmbDepartmentName.getValue().toString()+"' " +
						"and lbn.vSectionId='"+cmbSectionName.getValue()+"' order by lbn.vEmployeeId";
				lblEmployeeName.setValue("Employee ID :");

				if(opgEmployee.getValue()=="Employee Name")
				{
					query = "select lbn.vAutoEmployeeId,ein.vEmployeeName from tbLeaveBalanceNew lbn inner join tbEmployeeInfo ein " +
							"on lbn.vAutoEmployeeId=ein.vEmployeeId where lbn.vDepartmentID='"+cmbDepartmentName.getValue().toString()+"' " +
							"and lbn.vSectionId='"+cmbSectionName.getValue()+"' order by lbn.vEmployeeId";
					lblEmployeeName.setValue("Employee Name :");
				}

				else if(opgEmployee.getValue()=="Proximity ID")
				{
					query = "select lbn.vAutoEmployeeId,ein.vProximityID from tbLeaveBalanceNew lbn inner join tbEmployeeInfo ein " +
							"on lbn.vAutoEmployeeId=ein.vEmployeeId where lbn.vDepartmentID='"+cmbDepartmentName.getValue().toString()+"' " +
							"and lbn.vSectionId='"+cmbSectionName.getValue()+"' order by lbn.vEmployeeId";
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
			setHeight("310px");

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

			cmbDepartmentName = new ComboBox();
			cmbDepartmentName.setWidth("260px");
			cmbDepartmentName.setHeight("-1px");
			cmbDepartmentName.setNullSelectionAllowed(true);
			cmbDepartmentName.setImmediate(true);
			mainLayout.addComponent(new Label("Department Name : "), "top:40.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartmentName, "top:38.0px; left:130.0px;");

			// lblSectionName
			lblSectionName = new Label("Section Name : ");
			lblSectionName.setImmediate(false);
			lblSectionName.setWidth("100.0%");
			lblSectionName.setHeight("-1px");
			mainLayout.addComponent(lblSectionName,"top:70.0px; left:20.0px;");

			// cmbSectionName
			cmbSectionName = new ComboBox();
			cmbSectionName.setWidth("260px");
			cmbSectionName.setHeight("-1px");
			cmbSectionName.setNullSelectionAllowed(true);
			cmbSectionName.setImmediate(true);
			mainLayout.addComponent(cmbSectionName, "top:68.0px; left:130.0px;");

			opgEmployee=new OptionGroup("",lstEmployee);
			opgEmployee.select("Employee ID");
			opgEmployee.setImmediate(true);
			opgEmployee.setStyleName("horizontal");
			mainLayout.addComponent(opgEmployee, "top:100.0px; left:50.0px;");

			lblEmployeeName = new Label("Employee ID:");
			mainLayout.addComponent(lblEmployeeName, "top:130.0px;left:20.0px;");

			// cmbEmployeeName
			cmbEmployeeName=new ComboBox();
			cmbEmployeeName.setImmediate(true);
			cmbEmployeeName.setWidth("260px");
			cmbEmployeeName.setHeight("-1px");
			cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployeeName, "top:128.0px;left:130.0px;");

			// ogactive
			ogactive = new OptionGroup("",activeType);
			ogactive.setImmediate(true);
			ogactive.setValue("Active");
			ogactive.setWidth("250px");
			ogactive.setHeight("-1px");
			ogactive.setStyleName("horizontal");
			mainLayout.addComponent(ogactive, "top:160px; left:130.0px;");

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:190.0px;left:140.0px;");

			mainLayout.addComponent(new Label("_____________________________________________________________________________"), "top:210.0px;left:20.0px;right:20.0px;");
			mainLayout.addComponent(cButton,"top:240.opx; left:130.0px");
			return mainLayout;
		}
		
		private void getAlldata()
		{
			String typeValue = "%";
			String sectionName = "%";

			if(cmbSectionName.getValue()!=null)
				sectionName = cmbSectionName.getValue().toString();

			if(ogactive.getValue().toString().equalsIgnoreCase("Active"))
			{
				typeValue = "1";
			}

			else if(ogactive.getValue().toString().equalsIgnoreCase("Inactive"))
			{
				typeValue = "0";
			}

			else
			{
				typeValue = "%";
			}

			reportpreview(sectionName,typeValue);
		}

		private void reportpreview(Object sectionName,Object activeValue)
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
				String strDetails="";

				String subQuery="select dSenctionFrom,dSenctionTo,iNoOfDays from tbEmployeeLeave where " +
						"vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' " +
						"and YEAR(dSenctionFrom)='"+cmbYear.getValue().toString()+"' and " +
						"YEAR(dSenctionTo)='"+cmbYear.getValue().toString()+"' and vLeaveType = 1 order by dSenctionFrom";

				String subQuerySL="select dSenctionFrom,dSenctionTo,iNoOfDays from tbEmployeeLeave where " +
						"vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' " +
						"and YEAR(dSenctionFrom)='"+cmbYear.getValue().toString()+"' and " +
						"YEAR(dSenctionTo)='"+cmbYear.getValue().toString()+"' and vLeaveType = 2 order by dSenctionFrom";

				String subQueryEL="select dSenctionFrom,dSenctionTo,iNoOfDays from tbEmployeeLeave where " +
						"vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' " +
						"and YEAR(dSenctionFrom)='"+cmbYear.getValue().toString()+"' and " +
						"YEAR(dSenctionTo)='"+cmbYear.getValue().toString()+"' and vLeaveType = 3 order by dSenctionFrom";

				String query="select departmentName,sectionName,autoEmployeeID,EmployeeID,ProximityID,employeeName,designation," +
						"joiningDate,confirmationDate,confDate,contactNo,LeaveType,Opening,ThisYear,Total,Enjoy,Encash,Balance from " +
						"funIndividualLeaveRegister('"+cmbYear.getValue().toString()+"','"+cmbDepartmentName.getValue()+"'," +
						"'"+cmbSectionName.getValue()+"','"+cmbEmployeeName.getValue()+"') where iStatus like '"+activeValue+"'";
				
				System.out.println("Query :" + query);

				if(queryValueCheck(subQuery))
				{
					strDetails="Enjoyed Leave Details :";
					hm.put("subSql", subQuery);
				}
				else
				{
					hm.put("subSql", "");
				}

				if(queryValueCheck(subQuerySL))
				{
					strDetails="Enjoyed Leave Details :";
					hm.put("subSqlSL", subQuerySL);
				}
				else
				{
					hm.put("subSqlSL", "");
				}


				if(queryValueCheck(subQueryEL))
				{
					strDetails="Enjoyed Leave Details :";
					hm.put("subSqlEL", subQueryEL);
				}
				else
				{
					hm.put("subSqlEL", "");
				}

				if(queryValueCheck(query))
				{
					hm.put("pDetails", strDetails);
					hm.put("sql", query);
					Window win = new ReportViewer(hm,"report/account/hrmModule/rptLeaveRegisterIndividual.jasper",
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
