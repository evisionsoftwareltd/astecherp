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
	import com.vaadin.ui.Button;
	import com.vaadin.ui.CheckBox;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.OptionGroup;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;


public class RptLoanRegisterCHO extends Window {
	
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;


		private ComboBox cmbDepartmentName;
		private CheckBox chkDepartmentAll;

		private Label lblSectionName;
		private ComboBox cmbSectionName;
		private CheckBox chkSectionAll;

		private Label lblAsOnDate;
		private PopupDateField dAsOnDate;

		private CommonButton cButton = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

		private Label lblLoanStatus;
		OptionGroup radioLoanStatus;
		private static final List<String>loanStatus  = Arrays.asList(new String[] {"Active", "All" });

		private SimpleDateFormat dDbFormat = new SimpleDateFormat("yyyy-MM-dd");
		private ReportDate reportTime = new ReportDate();

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

		public RptLoanRegisterCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("LOAN REGISTER CHO:: "+sessionBean.getCompany());
			this.setWidth("500px");
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			cmbAddDepartmentName();
			setEventAction();
		}

		private void setEventAction()
		{
			cmbDepartmentName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSectionName.removeAllItems();
					if(cmbDepartmentName.getValue()!=null)
					{
						cmbAddSectionName();
					}
				}
			});

/*			chkDepartmentAll.addListener(new Listener()
			{
				public void componentEvent(Event event)
				{
					cmbSectionName.removeAllItems();
					if(chkDepartmentAll.booleanValue()==true)
					{
						cmbDepartmentName.setEnabled(false);
						cmbDepartmentName.setValue(null);
						cmbAddSectionName();
					}
					else
					{
						cmbDepartmentName.setEnabled(true);
					}
				}
			});*/


			chkSectionAll.addListener(new Listener()
			{
				public void componentEvent(Event event)
				{
					if(chkSectionAll.booleanValue()==true)
					{
						cmbSectionName.setEnabled(false);
						cmbSectionName.setValue(null);
					}
					else
					{
						cmbSectionName.setEnabled(true);
					}
				}
			});

			cButton.btnPreview.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbDepartmentName.getValue()!=null || chkDepartmentAll.booleanValue())
					{
						if(cmbSectionName.getValue()!=null || chkSectionAll.booleanValue())
						{
							if(dAsOnDate.getValue()!=null)
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
							showNotification("Warning","Select Section Name",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Department Name",Notification.TYPE_WARNING_MESSAGE);
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
		}

		private AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setMargin(false);

			// top-level component properties
			setWidth("420px");
			setHeight("240px");

			// cmbSectionName
			cmbDepartmentName = new ComboBox();
			cmbDepartmentName.setImmediate(true);
			cmbDepartmentName.setWidth("220px");
			cmbDepartmentName.setHeight("-1px");
			cmbDepartmentName.setNullSelectionAllowed(true);
			mainLayout.addComponent(new Label("Department Name : "), "top:10.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartmentName, "top:08.0px; left:135.0px;");

			// chkSectionAll
	/*		chkDepartmentAll = new CheckBox("All");
			chkDepartmentAll.setImmediate(true);
			chkDepartmentAll.setHeight("-1px");
			chkDepartmentAll.setWidth("-1px");
			mainLayout.addComponent(chkDepartmentAll, "top:10.0px; left:360.0px;");*/

			// lblSectionName
			lblSectionName = new Label("Section Name :");
			lblSectionName.setImmediate(false);
			lblSectionName.setWidth("100.0%");
			lblSectionName.setHeight("-1px");
			mainLayout.addComponent(lblSectionName,"top:40.0px; left:20.0px;");

			// cmbSectionName
			cmbSectionName = new ComboBox();
			cmbSectionName.setImmediate(true);
			cmbSectionName.setWidth("220px");
			cmbSectionName.setHeight("-1px");
			cmbSectionName.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbSectionName, "top:38.0px; left:135.0px;");

			// chkSectionAll
			chkSectionAll = new CheckBox("All");
			chkSectionAll.setImmediate(true);
			chkSectionAll.setHeight("-1px");
			chkSectionAll.setWidth("-1px");
			mainLayout.addComponent(chkSectionAll, "top:40.0px; left:360.0px;");

			// lblLoanStatus
			lblLoanStatus = new Label("Loan Status :");
			lblLoanStatus.setImmediate(false);
			lblLoanStatus.setWidth("100.0%");
			lblLoanStatus.setHeight("-1px");
			mainLayout.addComponent(lblLoanStatus,"top:70.0px; left:20.0px;");

			radioLoanStatus= new OptionGroup("",loanStatus);
			radioLoanStatus.setImmediate(true);
			radioLoanStatus.setWidth("-1px");
			radioLoanStatus.setHeight("-1px");
			radioLoanStatus.setStyleName("horizontal");
			radioLoanStatus.setImmediate(true);
			radioLoanStatus.select("Active");
			mainLayout.addComponent(radioLoanStatus, "top:69.0px; left:135.0px;");

			// lblAsOnDate
			lblAsOnDate = new Label("As on Date :");
			lblAsOnDate.setImmediate(false);
			lblAsOnDate.setWidth("100.0%");
			lblAsOnDate.setHeight("-1px");
			mainLayout.addComponent(lblAsOnDate,"top:100.0px; left:20.0px;");

			// dAsOnDate
			dAsOnDate = new PopupDateField();
			dAsOnDate.setImmediate(true);
			dAsOnDate.setWidth("110px");
			dAsOnDate.setHeight("-1px");
			dAsOnDate.setDateFormat("dd-MM-yyyy");
			dAsOnDate.setValue(new java.util.Date());
			dAsOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dAsOnDate, "top:98.0px; left:135.0px;");

			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:130.0px;left:135.0px;");
			mainLayout.addComponent(cButton,"top:160.opx; left:130.0px");
			return mainLayout;
		}

		private void cmbAddDepartmentName()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = " select la.vDepartmentId,dept.vDepartmentName from tbLoanApplication la inner join " +
						"tbDepartmentInfo dept on la.vDepartmentId=dept.vDepartmentId where dept.vDepartmentName='CHO' order by dept.vDepartmentName ";
				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbDepartmentName.addItem(element[0]);
					cmbDepartmentName.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddDepartmentName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}	

		private void cmbAddSectionName()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String querySection = " select la.vSectionId,sein.SectionName from tbLoanApplication la inner join " +
						"tbSectionInfo sein on la.vSectionId=sein.vSectionId where (sein.vSectionID='SEC-41' or sein.vSectionID='SEC-42') order by sein.SectionName ";
				List <?> list = session.createSQLQuery(querySection).list();	
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	

					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception exp)
			{
				showNotification("cmbAddSectionName",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}	

		private void reportpreview()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String query = "";
			String section = "";

			if(chkSectionAll.booleanValue()==true)
			{
				section= "%";
			}
			else
			{
				section= cmbSectionName.getValue().toString();
			}

			try
			{
				if(radioLoanStatus.getValue().toString().equals("Active"))
				{
					query = " select ei.employeeCode,ei.vEmployeeName,dept.vDepartmentName,si.SectionName,di.designationName,ei.dJoiningDate," +
							" la.dApplicationDate,la.mGrossAmount,la.mGrossAmount-(select ISNULL(sum(mRecoveryAmount),0)  from tbLoanRecoveryInfo where vEmployeeId like ei.employeeCode and dRecoveryDate<='"+dDbFormat.format(dAsOnDate.getValue())+"'  and la.vLoanNo=tbLoanRecoveryInfo.vLoanNo ) as balance  from tbLoanApplication as la" +
							" inner join tbEmployeeInfo as ei on la.vAutoEmployeeId=ei.vEmployeeId inner join tbDepartmentInfo " +
							" dept on dept.vDepartmentID=la.vDepartmentId inner join tbSectionInfo as si" +
							" on ei.vSectionId=si.vSectionId inner join tbDesignationInfo as di on ei.vDesignationId=di.designationId" +
							" where la.dApplicationDate<='"+dDbFormat.format(dAsOnDate.getValue())+"' " +
							" and la.vDepartmentID like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
							"and la.vSectionId like '"+section+"' and la.iSanctionStatus='1' and la.mLoanBalance>0 ";
					
					System.out.println("lon Query is :"+query);
				}
				else
				{
					query = " select ei.employeeCode,ei.vEmployeeName,dept.vDepartmentName,si.SectionName,di.designationName,ei.dJoiningDate," +
							" la.dApplicationDate,la.mGrossAmount,la.mGrossAmount-(select ISNULL(sum(mRecoveryAmount),0)  from tbLoanRecoveryInfo where vEmployeeId like ei.employeeCode and dRecoveryDate<='"+dDbFormat.format(dAsOnDate.getValue())+"' and la.vLoanNo=tbLoanRecoveryInfo.vLoanNo ) as balance  from tbLoanApplication as la" +
							" inner join tbEmployeeInfo as ei on la.vAutoEmployeeId=ei.vEmployeeId inner join tbDepartmentInfo " +
							" dept on dept.vDepartmentID=la.vDepartmentId inner join tbSectionInfo as si" +
							" on ei.vSectionId=si.vSectionId inner join tbDesignationInfo as di on ei.vDesignationId=di.designationId" +
							" where la.dApplicationDate<='"+dDbFormat.format(dAsOnDate.getValue())+"' " +
							" and la.vDepartmentID like '"+(cmbDepartmentName.getValue()!=null?cmbDepartmentName.getValue().toString():"%")+"' " +
							"and la.vSectionId like '"+section+"' and la.iSanctionStatus='1' ";
				}

				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("asOnDate", dAsOnDate.getValue());
					hm.put("userName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptLoanRegister.jasper",
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
