package com.reportform.hrmModule;



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


@SuppressWarnings("serial")
public class RptEditEmployeeInformationWithUser extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private ComboBox cmbUser;
	private PopupDateField dSalaryMonth;

	ArrayList<Component> allComp = new ArrayList<Component>();


	//	private List<?> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});
	private static final String CHO="'DEPT10'";
	public RptEditEmployeeInformationWithUser(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("USER BY EDITED EMPLOYEE INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbUserAddData();
		setEventAction();
		focusMove();
	}

	public void cmbUserAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{				
			/*String query ="select 0,vUserName from tbUDEmployeeInfo ";*/
			
			
			String query="select userId,name from tbLogin where name in (select vUserName from  tbUDEmployeeInfo)";
			
			System.out.println("User Data Load "+query);
			
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			
			{
				Object[] element = (Object[]) iter.next();
				cmbUser.addItem(element[0]);
				cmbUser.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbUserAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		cmbUser.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				if(cmbUser.getValue()!=null)
				{
					cmbUserAddData();
				}
			}
		});

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
						if(cmbUser.getValue()!=null)
						{
							reportShow();

						}
						else
						{
							showNotification("Select User Name",Notification.TYPE_WARNING_MESSAGE);
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
		try
		{ 
		
		/*query="select employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo " +
					"din where din.designationId=ein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus," +
					"iStatus,mMonthlySalary,vUserName,dDateTime,vPcIp,mHouseRent,OtStatus," +
					"mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund," +
					"bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee," +
					"'Present' as vUDFlag from tbEmployeeInfo ein where vDepartmentId='"+cmbUser.getValue()+"' and  vEmployeeId like'"+employee+"' union all " +
					"select employeeCode,vProximityId,vEmployeeName,(select din.designationName from tbDesignationInfo din where " +
					"din.designationId=uein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus," +
					"iStatus,mMonthlySalary,vUserName,dDateTime,vPcIp,mHouseRent,OtStatus," +
					"mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund," +
					"bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee," +
					"vUDFlag from tbUDEmployeeInfo uein where vDepartmentId='"+cmbUser.getValue()+"' and vEmployeeId like '"+employee+"' order by employeeCode,vEmployeeName,vUDFlag,dDateTime ";
*/
/*	
	query="select distinct ein.vDepartmentId,sec.vDepartmentName,ein.vSectionId,sec.SectionName,employeeCode,"
			+ "vProximityId,vEmployeeName,(select dg.designationName from tbDesignationInfo dg where"
			+ " dg.designationId=ein.vDesignationId) vDesignationName,vGender,vEmployeeType,vStatus,"
			+ "iStatus,mMonthlySalary,ein.vUserName,dDateTime,vPcIp,mHouseRent,OtStatus,mMedicalAllowance,"
			+ "mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund,bankId,bankName,bankBranchId,"
			+ "branchName,accountNo,FridayStatus,FridayLunchFee,'Present' as vUDFlag from tbEmployeeInfo"
			+ " ein inner join tbSectionInfo sec  on ein.vDepartmentId=sec.vDepartmentId where "
			+ "ein.vUserName = '"+cmbUser.getItemCaption(cmbUser.getValue())+"' and ein.vDepartmentId like '%' and ein.vSectionId like'%'"
			+ " and vEmployeeId like'%' union all select distinct uein.vDepartmentId,sien.vDepartmentName,"
			+ "uein.vSectionId,sien.SectionName,employeeCode,vProximityId,vEmployeeName,"
			+ "(select desg.designationName from tbDesignationInfo desg where desg.designationId=uein.vDesignationId)"
			+ "vDesignationName,vGender,vEmployeeType,vStatus,iStatus,mMonthlySalary,uein.vUserName,dDateTime,vPcIp,"
			+ "mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund,"
			+ "bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,vUDFlag from tbUDEmployeeInfo"
			+ " uein inner join tbSectionInfo sien on uein.vDepartmentId=sien.vDepartmentId "
			+ "where uein.vUserName = '"+cmbUser.getItemCaption(cmbUser.getValue())+"' and uein.vDepartmentId like'%' and uein.vSectionId like '%' "
			+ "and vEmployeeId like '%' order by vDepartmentID,employeeCode,vEmployeeName,vUDFlag,dDateTime,vSectionId";
			
			*/
			query="select tuei.vDepartmentId,tuei.vSectionId,tuei.vDesignationId,employeeCode,vProximityId,vEmployeeName,vGender,"
					+ "vEmployeeType,vStatus,iStatus,mMonthlySalary,tuei.vUserName,dDateTime,vPcIp,mHouseRent,"
					+ "OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mOthersAllowance,mProvidentFund,"
					+ "bankId,bankName,bankBranchId,branchName,accountNo,FridayStatus,FridayLunchFee,vUDFlag,"
					+ "tsi.SectionName,tdi.vDepartmentName,desg.designationName from tbUDEmployeeInfo tuei inner join "
					+ "tbSectionInfo tsi on tsi.vSectionID=tuei.vSectionId inner join tbDepartmentInfo tdi on"
					+ " tdi.vDepartmentId=tuei.vDepartmentId inner join tbDesignationInfo desg on "
					+ "tuei.vDesignationId =desg.designationId where tuei.vUserName= '"+cmbUser.getItemCaption(cmbUser.getValue())+"'"
					+ " and tuei.vDepartmentId "
					+ "like '%' and tuei.vSectionId like '%' and vEmployeeId like '%' and tdi.vDepartmentID!="+CHO+" order by vDepartmentID,vDesignationId,"
					+ "employeeCode,vEmployeeName,vUDFlag,dDateTime,vSectionId";
			
			
			System.out.println("ReportView "+query);

			if(queryValueCheck(query))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("User",cmbUser.getItemCaption(cmbUser.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptEditEmployeeInformationByUser.jasper",
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
	//	allComp.add(cmbSection);
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

		// cmbUser
		cmbUser = new ComboBox();
		cmbUser.setImmediate(true);
		cmbUser.setWidth("220px");
		cmbUser.setHeight("-1px");
		cmbUser.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("User Name : "), "top:30.0px; left:40.0px;");
		mainLayout.addComponent(cmbUser, "top:29.0px; left:120.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:80.0px;left:140.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:170.opx; left:140.0px");
		return mainLayout;
	}
}