package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

@SuppressWarnings("serial")
public class RptIncrementTypeProposal extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblYear;
	private Label lblSection;
	private Label lblIncrementType;

	private PopupDateField dYear;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbIncrementType;

	private CheckBox chkSectionAll;

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	//private static final String[] category = new String[] { "Permanent", "Temporary", "Provisionary", "Casual"};

	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptIncrementTypeProposal(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("INDIVIDUAL INCREMENT TYPE LIST:: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbDepartmentAddData();
		cmbSectionAddData();
		cmbIncrementAddData();
		setEventAction();
		focusMove();
	}

	public void cmbDepartmentAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbSalaryIncrement where "
					+ " YEAR(dDate) = '"+yearFormat.format(dYear.getValue())+"' and vDepartmentName != 'CHO' order by vDepartmentName";
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
			String query="select distinct vSectionId,vSectionName from tbSalaryIncrement where "
					+ " YEAR(dDate) = '"+yearFormat.format(dYear.getValue())+"' and vDepartmentId = '"+cmbDepartment.getValue()+"' and vSectionName != 'CHO' order by vSectionName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbSectionAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	public void cmbIncrementAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();

		String Sec =" ";
		
		if(cmbSection.getValue()!=null)
		{
			Sec=cmbSection.getValue().toString();
		}
		else
		{
			Sec="%";
		}
		
		try
		{
			
		String query=" select distinct vIncrementId,vIncrementType from tbSalaryIncrement "
			 + "where YEAR(dDate) = '"+yearFormat.format(dYear.getValue())+"'  and "
			 + "vDepartmentId ='"+cmbDepartment.getValue()+"' and vSectionId like '"+Sec+"' order "
			 + "by vIncrementType";			
		
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbIncrementType.addItem(element[0]);
				cmbIncrementType.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("cmbIncrementAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	
	public void setEventAction()
	{
		dYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dYear.getValue()!=null)
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
		
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				//cmbSection.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbIncrementAddData();
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkSectionAll.booleanValue())
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
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbIncrementType.getValue()!=null)
						{
							reportShow();
						}
						else
						{
							showNotification("Select Category Name",Notification.TYPE_WARNING_MESSAGE);
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		
		String Section =" ";
		
		if(cmbSection.getValue()!=null)
		{
			Section=cmbSection.getValue().toString();
		}
		else
		{
			Section="%";
		}
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
/*			String query = "select sinc.employeeCode,sinc.vEmployeeName,ein.vGender,sinc.vDesignationName,sinc.vDepartmentName,sinc.vSectionName,"
					+ " ein.dJoiningDate,DateDiff(DD,ein.dInterviewDate,sinc.dDate)/365 dYear,DateDiff(DD,ein.dInterviewDate,sinc.dDate)%365/30 dMonth,"
					+ " sinc.vEmployeeType,sinc.mBasic,sinc.mGross,sinc.vIncrementPercentage,sinc.vIncrementType,sinc.mIncrementAmount,sinc.mNewGross from tbSalaryIncrement sinc "
					+ " inner join tbEmployeeInfo ein on ein.vEmployeeId = sinc.vEmployeeId where YEAR(sinc.dDate) = '"+yearFormat.format(dYear.getValue())+"' "
					+ " and sinc.vDepartmentId ='"+cmbDepartment.getValue()+"' and sinc.vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
					+ " and sinc.vIncrementType ='"+cmbIncrementType.getValue()+"' order by sinc.vDepartmentName,sinc.vSectionName,sinc.employeeCode";*/

			
			String query = "select sinc.employeeCode,sinc.vEmployeeName,ein.vGender,sinc.vDesignationName,sinc.vDepartmentName,sinc.vSectionName,"
					+ " ein.dJoiningDate,DateDiff(DD,ein.dInterviewDate,sinc.dDate)/365 dYear,DateDiff(DD,ein.dInterviewDate,sinc.dDate)%365/30 dMonth,"
					+ " sinc.vEmployeeType,sinc.mBasic,sinc.mGross,sinc.vIncrementPercentage,sinc.vIncrementType,sinc.mIncrementAmount,sinc.mNewGross from tbSalaryIncrement sinc "
					+ " inner join tbEmployeeInfo ein on ein.vEmployeeId = sinc.vEmployeeId where YEAR(sinc.dDate) = '"+yearFormat.format(dYear.getValue())+"' "
					+ " and sinc.vDepartmentId ='"+cmbDepartment.getValue()+"' and sinc.vSectionId like '"+Section+"' "
					+ " and sinc.vIncrementId ='"+cmbIncrementType.getValue()+"' order by sinc.vDepartmentName,sinc.vSectionName,sinc.employeeCode";

			System.out.println("suma"+query);
			
			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
					String fname = "salaryIncrement.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					String strColName[]={"SL#","Employee ID","Employee Name","Gender","Designation","Date of Joining","Employee Type","Present Salary (Gross)","Length of Service","Declared Percentage","Increment Amount","Net Salary After Gross","Remarks"};
					String Header="";

					String Groupuery="SELECT distinct vDepartmentID,vDepartmentName,vSectionID,vSectionName from tbSalaryIncrement "
							+ " where YEAR(dDate) = '"+yearFormat.format(dYear.getValue())+"' and vDepartmentId = '"+cmbDepartment.getValue()+"' "
							+ " and vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"' "
							+ " and vIncrementType  = '"+cmbIncrementType.getValue()+"' order by vDepartmentName,vSectionName";

					System.out.println("Ruma"+query);
					
					List <?> lst1=session.createSQLQuery(Groupuery).list();

					String detailQuery[]=new String[lst1.size()];
					String [] groupItem=new String[lst1.size()];
					Object [][] GroupElement=new Object[lst1.size()][];
					String [] GroupColName=new String[0];
					String [] signatureOption = new String [0];
					int countInd=0;

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element = (Object[]) itr1.next();
						groupItem[countInd]="Department Name : "+element[1].toString()+"     Section Name : "+element[3].toString();
						GroupElement[countInd]=new Object [] {(Object)"Department Name : ",element[1],(Object)"Section Name : ",element[3]};

						detailQuery[countInd]="select sinc.employeeCode,sinc.vEmployeeName,ein.vGender,mBasic,sinc.vDesignationName,ein.dJoiningDate,"
								+ " sinc.vEmployeeType,sinc.vIncrementType,cast(sinc.mGross as float) mGross,CONVERT(varchar,DateDiff(DD,ein.dInterviewDate,sinc.dDate)/365) +'y '+CONVERT(varchar,DateDiff(DD,ein.dInterviewDate,sinc.dDate)%365/30)+'m' vLengthOfService,"
								+ " cast(sinc.vIncrementPercentage as float) vIncrementPercentage,cast(sinc.mIncrementAmount as float) mIncrementAmount,cast(sinc.mNewGross as float) mNewGross,'' vRemarks from tbSalaryIncrement sinc "
								+ " inner join tbEmployeeInfo ein on ein.vEmployeeId = sinc.vEmployeeId where YEAR(sinc.dDate) = '"+yearFormat.format(dYear.getValue())+"' "
								+ " and sinc.vDepartmentId = '"+element[0].toString()+"' and sinc.vSectionId like '"+element[2].toString()+"' "
								+ " and sinc.vIncrementType  = '"+cmbIncrementType.getValue()+"' order by sinc.employeeCode";
						countInd++;
					}

					new GenerateExcelReport(sessionBean, loc, url, fname, "Increment Proposal", "INCREMENT PROPOSAL"+yearFormat.format(dYear.getValue()),
							Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4",
							"Landscape",signatureOption);

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
					hm.put("year", yearFormat.format(dYear.getValue()));
					hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
					hm.put("Department",cmbDepartment.getItemCaption(cmbDepartment.getValue()));
					hm.put("SysDate",reportTime.getTime);
					hm.put("logo", sessionBean.getCompanyLogo());
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/RptIncrementTypeProposal.jasper",
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
		finally{session.close();}
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
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
		allComp.add(cmbSection);
		allComp.add(cmbIncrementType);
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
		setHeight("250px");

		lblYear = new Label("Year :");
		lblYear.setWidth("100.0%");
		lblYear.setHeight("-1px");
		mainLayout.addComponent(lblYear,"top:20.0px; left:20.0px;");

		dYear = new PopupDateField();
		dYear.setImmediate(true);
		dYear.setWidth("110.0px");
		dYear.setValue(new Date());
		dYear.setDateFormat("yyyy");
		dYear.setResolution(PopupDateField.RESOLUTION_YEAR);
		mainLayout.addComponent(dYear, "top:18.0px; left:130.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		mainLayout.addComponent(new Label("Department Name : "), "top:45.0px; left:20.0px;");
		mainLayout.addComponent(cmbDepartment, "top:43.0px; left:130.0px;");

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

		// chkCategoryAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setHeight("-1px");
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:395.0px;");

		// lblCategory
		lblIncrementType = new Label("Increment Type :");
		lblIncrementType.setImmediate(false);
		lblIncrementType.setWidth("100.0%");
		lblIncrementType.setHeight("-1px");
		mainLayout.addComponent(lblIncrementType,"top:95.0px; left:20.0px;");

		// cmbCategory
		cmbIncrementType = new ComboBox();
		cmbIncrementType.setImmediate(true);
		cmbIncrementType.setWidth("220px");
		cmbIncrementType.setHeight("-1px");
		cmbIncrementType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbIncrementType, "top:93.0px; left:130.0px;");
	/*	for(int i=0; i<category.length; i++)
		{cmbIncrementType.addItem(category[i]);}
*/
		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:125.0px;left:130.0px;");

		mainLayout.addComponent(new Label("___________________________________________________________________"), "top:145.0px;left:20.0px;right:20.0px;");
		mainLayout.addComponent(cButton,"top:165.opx; left:140.0px");
		return mainLayout;
	}
}
