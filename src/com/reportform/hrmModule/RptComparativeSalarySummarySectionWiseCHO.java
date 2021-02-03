package com.reportform.hrmModule;

import java.io.File;
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

@SuppressWarnings("serial")
public class RptComparativeSalarySummarySectionWiseCHO extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private PopupDateField dMonth;
	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox DepartmentAll;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox sectionAll;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptComparativeSalarySummarySectionWiseCHO(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("COMPARATIVE SALARY SUMMARY :: "+sessionBean.getCompany());
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
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					addSectionName();
				}
			}
		});

/*		DepartmentAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				cmbSection.removeAllItems();
				if(DepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					addSectionName();
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});*/

		sectionAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(sectionAll.booleanValue())
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
				String section = "";
				if(cmbSection.getValue()!=null || sectionAll.booleanValue()==true)
				{
					if(sectionAll.booleanValue()==true)
					{section = "%";}
					else
					{section = cmbSection.getValue().toString();}

					reportShow(section);
				}
				else
				{
					showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
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
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="SELECT vDepartmentID,vDepartmentName from tbSalary where " +
					"Month(dDate) = Month('"+dateFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate) = Year('"+dateFormat.format(dMonth.getValue())+"') and vDepartmentName='CHO' order by vDepartmentName ";
			
			System.out.println("OMG"+query);
			List <?> list = session.createSQLQuery(query).list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("addDepartmentName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void addSectionName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(" SELECT SectionID,vDepartmentName,Section from tbSalary " +
					"where vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue().toString():"%")+"' " +
					"and Month(dDate) = Month('"+dateFormat.format(dMonth.getValue())+"') and " +
					"YEAR(dDate) = Year('"+dateFormat.format(dMonth.getValue())+"') order by vDepartmentName,Section").list();

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("addSectionName",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow(Object sectionName)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("SysDate",reportTime.getTime);
			hm.put("logo", sessionBean.getCompanyLogo());

			query = "select vDepartmentName,vSectionName,vMonthName,vYear,iNoOfEmployee,mProbisionarySalary,mPermanentSalary," +
					"mCasualSalary,vPreMonthName,iPreYear,iPreNoOfEmployee,mPreProbisionarySalary,mPrePermanentSalary,mPreCasualSalary " +
					"from funMonthlyComparativeSalarySummary('"+dateFormat.format(dMonth.getValue())+"','"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"'," +
					"'"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"') where vDepartmentName='CHO' order by vDepartmentName,vSectionName";
			if(queryValueCheck(query))
			{
				if(RadioBtnGroup.getValue()=="Excel")
				{
					String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";

					String fname = "Comparetive_Salary.xls";
					String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

					File inFile; 
					String header [] = new String[1];
					header[0] = monthFormat.format(dMonth.getValue())+", "+yearFormat.format(dMonth.getValue()); 
					String reportName = "COMPARATIVE SALARY SUMMARY";
					String detailQuery[]=new String[1];
					String GroupQuery[]=new String[1];
					String signatureOption [] = new String [0];
					int rowWidth=0;

					inFile=new File("D://Tomcat 7.0/webapps/report/astecherp/hrmReportExl/RptComparativeSalary.xls");
					detailQuery[0]="select vDepartmentName,vSectionName,CAST(iNoOfEmployee as float) iNoOfEmployee,CAST(mProbisionarySalary as float) mProbisionarySalary," +
							"CAST(mPermanentSalary as float) mPermanentSalary,CAST(mCasualSalary as float) mCasualSalary," +
							"CAST(mProbisionarySalary+mPermanentSalary+mCasualSalary as float) Total,CAST(iPreNoOfEmployee as float) iPreNoOfEmployee," +
							"CAST(mPreProbisionarySalary as float) mPreProbisionarySalary,CAST(mPrePermanentSalary as float) mPrePermanentSalary," +
							"CAST(mPreCasualSalary as float) mPreCasualSalary,CAST(mPreProbisionarySalary+mPrePermanentSalary+mPreCasualSalary as float) preTotal," +
							"CAST((mProbisionarySalary+mPermanentSalary+mCasualSalary)-(mPreProbisionarySalary+mPrePermanentSalary+mPreCasualSalary) as float) as diff " +
							"from funMonthlyComparativeSalarySummary ('"+dateFormat.format(dMonth.getValue())+"','"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"'," +
							"'"+(cmbSection.getValue()!=null?cmbSection.getValue():"%")+"') order by vDepartmentName,vSectionName";
							rowWidth=13;

							new SalaryExcelReport(sessionBean, loc, url, fname, header, inFile, "Comparative_Salary", 
									reportName, 2, GroupQuery, 1, detailQuery, rowWidth,8,signatureOption);

							Window window = new Window();
							getApplication().addWindow(window);
							getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);
				}
				else
				{
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptComparativeSalarySummary.jasper",
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
			showNotification("reportShow"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
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
		setWidth("430px");
		setHeight("250px");

		dMonth = new PopupDateField();
		dMonth.setImmediate(true);
		dMonth.setWidth("140px");
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setValue(new Date());
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(new Label("Month : "), "top:30.0px; left:30.0px;");
		mainLayout.addComponent(dMonth, "top:28.0px; left:150.0px;");

		// lblCategory
		lblDepartment = new Label();
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("100.0%");
		lblDepartment.setHeight("-1px");
		lblDepartment.setValue("Department Name :");
		mainLayout.addComponent(lblDepartment,"top:60.0px; left:30.0px;");

		// cmbSection
		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(false);
		cmbDepartment.setWidth("200px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:58.0px; left:150.0px;");

/*		//sectionAll
		DepartmentAll = new CheckBox("All");
		DepartmentAll.setHeight("-1px");
		DepartmentAll.setWidth("-1px");
		DepartmentAll.setImmediate(true);
		mainLayout.addComponent(DepartmentAll, "top:60.0px; left:356.0px;");*/

		// lblCategory
		lblSection = new Label();
		lblSection.setImmediate(false);
		lblSection.setWidth("100.0%");
		lblSection.setHeight("-1px");
		lblSection.setValue("Section Name :");
		mainLayout.addComponent(lblSection,"top:90.0px; left:30.0px;");

		// cmbSection
		cmbSection = new ComboBox();
		cmbSection.setImmediate(false);
		cmbSection.setWidth("200px");
		cmbSection.setHeight("-1px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:88.0px; left:150.0px;");

		//sectionAll
		sectionAll = new CheckBox("All");
		sectionAll.setHeight("-1px");
		sectionAll.setWidth("-1px");
		sectionAll.setImmediate(true);
		mainLayout.addComponent(sectionAll, "top:90.0px; left:356.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:120.0px;left:150.0px;");

		mainLayout.addComponent(cButton,"top:150.opx; left:140.0px");
		return mainLayout;
	}
}
