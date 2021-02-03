package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptBankStatementCHO extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private PopupDateField dDate;
	private PopupDateField dMonthYear;
	private ComboBox cmbBankName;
	private ComboBox cmbBranchName;
	private TextField txtAccountNo;
	private TextField txtReferrenceNo;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dDayFormat = new SimpleDateFormat("dd");
	private SimpleDateFormat dMonthFormat1 = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

	private CommaSeparator commaSeparator=new CommaSeparator();

	ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private ReportDate reportTime = new ReportDate();

	private OptionGroup rptOtGroup;
	private static final List<String> rptType=Arrays.asList(new String[]{"Forwarding Letter","Salary Statement"});
	private OptionGroup radioAccountGroup;
	private static final List<String> accountType=Arrays.asList(new String[]{"Bank Account","Mobile Account"});

	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other","Excel"});

	public RptBankStatementCHO(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("BANK ADVICE WITH FORWARDING LETTER CHO:: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbBankNameAddData();
		cmbBranchNameAddData();
		setEventAction();
		focusMove();
	}

	public void cmbBankNameAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select id,bankName from tbBankName";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBankName.addItem(element[0]);
				cmbBankName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbBankNameAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void cmbBranchNameAddData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select id,branchName from tbBankBranch";
			List <?> list=session.createSQLQuery(query).list();
			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBranchName.addItem(element[0]);
				cmbBranchName.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbBranchNameAddData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{

		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dDate.getValue()!=null)
				{
					if(dMonthYear.getValue()!=null)
					{
						if(cmbBankName.getValue()!=null)
						{
							if(cmbBranchName.getValue()!=null)
							{
								if(txtAccountNo.getValue().toString().length()>0)
								{
									Session session=SessionFactoryUtil.getInstance().openSession();
									session.beginTransaction();
									try
									{

										String query="select DATEDIFF(MM,'"+dFormat.format(dMonthYear.getValue())+"','"+dFormat.format(dDate.getValue())+"')";
										int diffMonth=Integer.parseInt(session.createSQLQuery(query).list().iterator().next().toString());
										if(diffMonth<=0 && Integer.parseInt(dDayFormat.format(dDate.getValue()))<25)
										{
											showNotification("Warnig","Select Valid Date!!!",Notification.TYPE_WARNING_MESSAGE);
										}
										else
										{
											reportShow();
										}
									}
									catch(Exception exp)
									{
										showNotification("dDate.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
									}
									finally{session.close();}
								}
								else
								{
									showNotification("Warning", "Select Account No!!!", Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Branch Name!!!",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","Select Bank Name!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Select Month!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Select Date!!!",Notification.TYPE_WARNING_MESSAGE);
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

	private void reportPreview(String accountFlag)
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query="";
		String testquery="";
		String rptName="";
		String amount="";
		String inWords="";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(rptOtGroup.getValue()=="Salary Statement")
			{
				query="select ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId,din.designationName," +
						"ts.vBankAccountNo,ts.vDepartmentID,ts.vDepartmentName,ein.vSectionId,sein.SectionName,ein.bankName,(round((ts.Gross+AttBonus),0)-round((ts.perDay*ts.absentDay),0)-round((advanceSalary+incomeTax+Insurance+mRevenueStamp),0)+adjust-Less) as " +
						"totalSalary,(round(itotalOTHour*otRate+(otRate/60*itotalOTMin),0)) as OtAmt,round(fridayAmount,0) fridayAmount,0 deduction, " +
						"ISNULL(ts.mDearnessAllowance,0) mDearnessAllowance,ISNULL(ts.mFireAllowance,0) mFireAllowance from tbEmployeeInfo ein inner join tbDesignationInfo " +
						"din on ein.vDesignationId=din.designationId inner join tbSectionInfo sein " +
						"on ein.vSectionId=sein.vSectionId inner join tbSalary ts on ein.vEmployeeId=ts.autoEmployeeID where ts.vMonthName='"+dMonthFormat1.format(dMonthYear.getValue())+"' " +
						"and ts.year='"+dYearFormat.format(dMonthYear.getValue())+"' and ein.accountNo!='' and vMobileBankFlag = '"+accountFlag+"' and ts.vDepartmentName='CHO' order by ts.vDepartmentName,sein.SectionName,ein.employeeCode";
				System.out.println("omg"+query);
				rptName="rptBankStatement.jasper";

				testquery="select * from tbSalary ts inner join tbEmployeeInfo ein on ts.autoEmployeeID = ein.vEmployeeID where ts.vMonthName='"+dMonthFormat1.format(dMonthYear.getValue())+"' " +
						"and ts.year='"+dYearFormat.format(dMonthYear.getValue())+"' and ein.vMobileBankFlag = '"+accountFlag+"'";


				if(queryValueCheck(testquery))
				{
					if(RadioBtnGroup.getValue()=="Excel")
					{
						String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
						String fname = "BankStatement.xls";
						String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;

						String strColName[]={"SL#","Employee ID","Employee Name","Designation","Department Name","Section Name",
								"Bank Name","Account No","Salary Amt.","OT Amt.","Friday Allow.","Dearness Allow.",
								"Fire Allow.","Deduction","Payable Amt.","Remarks"};

						String Header="For Month of : "+dMonthFormat1.format(dMonthYear.getValue())+","+dYearFormat.format(dMonthYear.getValue());

						String detailQuery[]=new String[1];
						String [] groupItem=new String[0];
						Object [][] GroupElement=new Object[0][];
						String [] GroupColName=new String[0];
						String signatureOption [] = {"Prepared By","Asst. Manager (Accounts)","Head of Accounts/Finance","Approved By"};

						detailQuery[0]="select ein.employeeCode,ein.vEmployeeName,din.designationName,ts.vDepartmentName,sein.SectionName," +
								"'"+cmbBankName.getItemCaption(cmbBankName.getValue())+"' as bankName,ts.vBankAccountNo," +
								"cast((round((ts.Gross+AttBonus),0)-round((ts.perDay*ts.absentDay),0)-round((advanceSalary+incomeTax+Insurance+mRevenueStamp),0)+adjust-Less) as float) as " +
								"totalSalary,cast(round(itotalOTHour*otRate+(otRate/60*iTotalOTMin),0) as float) as OtAmt,cast(round(fridayAmount,0) as float) fridayAmount, " +
								"cast(ISNULL(ts.mDearnessAllowance,0) as float) mDearnessAllowance,cast(ISNULL(ts.mFireAllowance,0) as float) mFireAllowance,0 deduction, " +
								"cast(round(round((ts.Gross+AttBonus),0)-round((ts.perDay*ts.absentDay),0)-round((advanceSalary+incomeTax+Insurance+mRevenueStamp),0)+adjust-Less+round(itotalOTHour*otRate+(otRate/60*iTotalOTMin),0)+fridayAmount+ISNULL(ts.mDearnessAllowance,0)+ISNULL(ts.mFireAllowance,0),0) as float) as payableAmt, "+
								"'' remarks from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationId=din.designationId " +
								"inner join tbSectionInfo sein on ein.vSectionId=sein.vSectionId inner join tbSalary ts on " +
								"ein.vEmployeeId=ts.autoEmployeeID where ts.vMonthName='"+dMonthFormat1.format(dMonthYear.getValue())+"' and ts.year='"+dYearFormat.format(dMonthYear.getValue())+"' and ein.accountNo!='' and vMobileBankFlag = '"+accountFlag+"' and ts.vDepartmentName='CHO'  order by ts.vDepartmentName,sein.SectionName,ein.employeeCode";

						new GenerateExcelReport(sessionBean, loc, url, fname, "Employee Information","Bank Advice",
								Header, strColName, 2,groupItem, GroupColName, GroupElement, 1, detailQuery, 8, 14,"A4",
								"LanScape", signatureOption);

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
						hm.put("Date",dDayFormat.format(dDate.getValue())+"th "+dMonthFormat1.format(dDate.getValue())+", "+dYearFormat.format(dDate.getValue()));
						hm.put("month", dMonthFormat1.format(dMonthYear.getValue())+", "+dYearFormat.format(dMonthYear.getValue()));
						hm.put("BankName",cmbBankName.getItemCaption(cmbBankName.getValue()));
						hm.put("BranchName", cmbBranchName.getItemCaption(cmbBranchName.getValue()));
						hm.put("SysDate",reportTime.getTime);
						hm.put("logo", sessionBean.getCompanyLogo());
						hm.put("sql", query);

						Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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

			else
			{	
				String query1="select sum(round(round((ts.Gross+AttBonus),0)-round((ts.perDay*ts.absentDay),0)-round((advanceSalary+incomeTax+Insurance+mRevenueStamp),0)+adjust-Less+round(itotalOTHour*otRate+(otRate/60*iTotalOTMin),0)+round(fridayAmount,0)+ISNULL(ts.mDearnessAllowance,0)+ISNULL(ts.mFireAllowance,0),0))as amount," +
						"dbo.number(sum(round(round((ts.Gross+AttBonus),0)-round((ts.perDay*ts.absentDay),0)-round((advanceSalary+incomeTax+Insurance+mRevenueStamp),0)+adjust-Less+round(itotalOTHour*otRate+(otRate/60*iTotalOTMin),0)+round(fridayAmount,0)+ISNULL(ts.mDearnessAllowance,0)+ISNULL(ts.mFireAllowance,0),0))) as inWords " +
						"from tbSalary ts inner join tbEmployeeInfo ein on ts.autoEmployeeID=ein.vEmployeeId where ts.vMonthName='"+dMonthFormat1.format(dMonthYear.getValue())+"' and ts.year='"+dYearFormat.format(dMonthYear.getValue())+"' "+
						" and ein.accountNo!='' and vMobileBankFlag = '"+accountFlag+"'";

				testquery="select * from tbSalary ts inner join tbEmployeeInfo ein on ts.autoEmployeeID = ein.vEmployeeID where ts.vMonthName='"+dMonthFormat1.format(dMonthYear.getValue())+"' " +
						"and ts.year='"+dYearFormat.format(dMonthYear.getValue())+"' and ein.vMobileBankFlag = '"+accountFlag+"'";

				if(queryValueCheck(testquery))
				{
					List <?> lst=session.createSQLQuery(query1).list();
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						amount=commaSeparator.setComma(Double.parseDouble(element[0].toString()));
						inWords=element[1].toString();
					}
				}

				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
				hm.put("Date",dDayFormat.format(dDate.getValue())+"th "+dMonthFormat1.format(dDate.getValue())+", "+dYearFormat.format(dDate.getValue()));
				hm.put("month", dMonthFormat1.format(dMonthYear.getValue())+", "+dYearFormat.format(dMonthYear.getValue()));
				hm.put("BankName",cmbBankName.getItemCaption(cmbBankName.getValue()));
				hm.put("BranchName", cmbBranchName.getItemCaption(cmbBranchName.getValue()));
				hm.put("SysDate",reportTime.getTime);
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("amount", amount);
				hm.put("InWords", inWords);
				hm.put("AccountNo", txtAccountNo.getValue());

				if(!txtReferrenceNo.getValue().toString().isEmpty())
					hm.put("refNo", txtReferrenceNo.getValue().toString());

				else
					hm.put("refNo", "");

				if(accountFlag=="Bank A/C")
					rptName="rptBankForwardingLetter.jasper";
				else
					rptName="rptMobileBankAdvise.jasper";

				if(amount.length()>0 && inWords.length()>0)
				{
					Window win = new ReportViewer(hm,"report/account/hrmModule/"+rptName,
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
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportShow()
	{
		if(radioAccountGroup.getValue()=="Bank Account")
			reportPreview("Bank A/C");
		else
			reportPreview("Mobile A/C");
	}

	private boolean queryValueCheck(String sql)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
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
		allComp.add(dDate);
		allComp.add(dMonthYear);
		allComp.add(cmbBankName);
		allComp.add(cmbBranchName);
		allComp.add(txtAccountNo);
		allComp.add(txtReferrenceNo);
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
		setHeight("380px");

		radioAccountGroup = new OptionGroup("",accountType);
		radioAccountGroup.setImmediate(true);
		radioAccountGroup.setStyleName("horizontal");
		radioAccountGroup.setValue("Bank Account");
		mainLayout.addComponent(radioAccountGroup, "top:30.0px;left:130.0px;");

		rptOtGroup = new OptionGroup("",rptType);
		rptOtGroup.setImmediate(true);
		rptOtGroup.setStyleName("horizontal");
		rptOtGroup.setValue("Forwarding Letter");
		mainLayout.addComponent(rptOtGroup, "top:60.0px;left:130.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date : "), "top:90.0px; left:30.0px;");
		mainLayout.addComponent(dDate, "top:88.0px; left:130.0px;");

		// dMonthYear
		dMonthYear = new PopupDateField();
		dMonthYear.setImmediate(true);
		dMonthYear.setWidth("140px");
		dMonthYear.setHeight("-1px");
		dMonthYear.setDateFormat("MMMMM-yyyy");
		dMonthYear.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonthYear.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Month : "), "top:120.0px; left:30.0px;");
		mainLayout.addComponent(dMonthYear, "top:118.0px; left:130.0px;");

		cmbBankName=new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setWidth("220px");
		cmbBankName.setHeight("-1px");
		mainLayout.addComponent(new Label("Bank Name : "), "top:150.0px; left:30.0px;");
		mainLayout.addComponent(cmbBankName, "top:148.0px; left:130.0px;");

		cmbBranchName=new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setWidth("220px");
		cmbBranchName.setHeight("-1px");
		cmbBranchName.setNewItemsAllowed(true);
		mainLayout.addComponent(new Label("Branch Name : "), "top:180.0px; left:30.0px;");
		mainLayout.addComponent(cmbBranchName, "top:178.0px; left:130.0px;");

		txtAccountNo=new TextField();
		txtAccountNo.setImmediate(true);
		txtAccountNo.setWidth("160px");
		txtAccountNo.setHeight("-1px");
		mainLayout.addComponent(new Label("Account No : "), "top:210.0px; left:30.0px;");
		mainLayout.addComponent(txtAccountNo, "top:208.0px; left:130.0px;");

		txtReferrenceNo=new TextField();
		txtReferrenceNo.setImmediate(true);
		txtReferrenceNo.setWidth("160px");
		txtReferrenceNo.setHeight("-1px");
		mainLayout.addComponent(new Label("Referrence No : "), "top:240.0px; left:30.0px;");
		mainLayout.addComponent(txtReferrenceNo, "top:238.0px; left:130.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:270.0px;left:130.0px;");

		mainLayout.addComponent(new Label("_________________________________________________________________________________________"), "top:290.0px;right:20.0px;left:20.0px;");		
		mainLayout.addComponent(cButton,"top:310.opx; left:140.0px");
		return mainLayout;
	}
}