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


public class RptDateWiseEmployeeAttendanceEdit extends Window {
	
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		ArrayList<Component> allComp = new ArrayList<Component>();

		private ComboBox cmbsection;
		private ComboBox cmbDepartment;
		
		private Label lblEmployee;
		private CheckBox chkempall;
		
		private Label lblMonth;
		private PopupDateField dMonth;

		private OptionGroup opgEmployee;
		private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private ReportDate reportTime = new ReportDate();
		
		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

		private OptionGroup RadioBtnGroup;
		private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
		private static final String CHO="'DEPT10'";
		public RptDateWiseEmployeeAttendanceEdit(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("DATE WISE EMPLOYEE ATTENDANCE EDIT :: "+sessionBean.getCompany());
			this.setResizable(false);
			buildMainLayout();
			setContent(mainLayout);
			addDepartmentName();
			setEventAction();
			focusMove();
		}

		public void setEventAction()
		{
			dMonth.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbDepartment.removeAllItems();
					if(dMonth.getValue()!=null)
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
						addEmployeeType();
					}
				}
			});
			
			
			chkempall.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkempall.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
						addEmployeeType();
					}
					else
						cmbDepartment.setEnabled(true);
				}
			});
			
			cButton.btnPreview.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					
					if(dMonth.getValue()!=null ) 
					{
						if(cmbDepartment.getValue()!=null || chkempall.booleanValue() ) 
						{
							reportShow();
						}
						else
						{
							showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Date",Notification.TYPE_WARNING_MESSAGE);
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
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
						" where vEditFlag ='Edited' and vDepartmentId!="+CHO+" and dDate='"+dFormat.format(dMonth.getValue())+"' ";
				
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
		
		public void addEmployeeType()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct 0, vEmployeeType from tbEmployeeInfo ein inner join tbEmployeeMainAttendance mat on mat.vEmployeeID=ein.vEmployeeId  " +
						" where vEditFlag ='Edited' and ein.vDepartmentId!="+CHO+" and dDate='"+dFormat.format(dMonth.getValue())+"' ";
				
				Iterator <?> itr=session.createSQLQuery(query).list().iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbsection.addItem(element[1]);
					/*cmbsection.addItem(element[0]);
					cmbsection.setItemCaption(element[0], element[1].toString());*/
				}
			}
			
			catch(Exception exp)
			{
				showNotification("addEmployeeType : ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}


		private void reportShow()
		{
			ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
			String department="";
			try
			{	
				
				if(cmbDepartment.getValue()!=null)
					department=cmbDepartment.getValue().toString();
				else
				{
					department="%";
				}
				String query = "select ema.dDate,ema.employeeCode,ema.vEmployeeName,vDepartmentName,vSectionName,dInTimeOne,dOutTimeOne,vEditFlag from tbEmployeeMainAttendance ema inner join tbEmployeeInfo ei on ema.vEmployeeID=ei.vEmployeeId" +
						" where vEditFlag ='Edited' and ema.dDate='"+dFormat.format(dMonth.getValue())+"' and ema.vDepartmentId like '"+department+"' and ema.vDepartmentId!="+CHO+" and ei.vEmployeeType='"+cmbsection.getValue()+"'   order by iDesignationSerial";
				System.out.println(query);
				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionBean.getCompany());
					hm.put("address", sessionBean.getCompanyAddress());
					hm.put("phone", sessionBean.getCompanyContact());
					hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("month",new SimpleDateFormat("MMMMMM").format(dMonth.getValue()));
					hm.put("year",new SimpleDateFormat("yy").format(dMonth.getValue()));
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptDateWiseEmployeeAttendenceEdit.jasper",
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
			setHeight("240px");
			
			lblMonth = new Label("Date :");
			lblMonth.setImmediate(false);
			lblMonth.setWidth("-1px");
			lblMonth.setHeight("-1px");
			mainLayout.addComponent(lblMonth, "top:30.0px; left:30.0px;");

			dMonth = new PopupDateField();
			dMonth.setImmediate(true);
			dMonth.setWidth("140px");
			dMonth.setDateFormat("dd-MM-yyyy");
			dMonth.setValue(new java.util.Date());
			dMonth.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dMonth, "top:28.0px; left:130.0px;");

			cmbDepartment=new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("260.0px");
			cmbDepartment.setHeight("24.0px");
			mainLayout.addComponent(new Label("Department :"), "top:60.0px; left:30.0px;");
			mainLayout.addComponent(cmbDepartment, "top:58.0px;left:130.0px;");

			//sectionAll
			chkempall = new CheckBox("All");
			chkempall.setHeight("-1px");
			chkempall.setWidth("-1px");
			chkempall.setImmediate(true);
			mainLayout.addComponent(chkempall, "top:60.0px; left:396.0px;");
			
			cmbsection=new ComboBox();
			cmbsection.setImmediate(true);
			cmbsection.setWidth("260.0px");
			cmbsection.setHeight("24.0px");
			mainLayout.addComponent(new Label("Employee Type :"), "top:90.0px; left:30.0px;");
			mainLayout.addComponent(cmbsection, "top:88.0px;left:130.0px;");
			
			// optionGroup
			RadioBtnGroup = new OptionGroup("",type1);
			RadioBtnGroup.setImmediate(true);
			RadioBtnGroup.setStyleName("horizontal");
			RadioBtnGroup.setValue("PDF");
			mainLayout.addComponent(RadioBtnGroup, "top:140.0px;left:180.0px;");
			
			mainLayout.addComponent(new Label("______________________________________________________________________________"), "top:150.0px; left:20.0px; right:20.0px;");
			mainLayout.addComponent(cButton,"top:170.opx; left:150.0px");
			return mainLayout;
		}

}
