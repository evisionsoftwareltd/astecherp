package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class TestGroupLedgerSummary extends Window
{
	SessionBean sessionBean;

	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblFromDate=new Label("From Date :");
	private DateField fromDate = new DateField();

	private PopupDateField toDate =new PopupDateField();
	private Label lblDate=new Label("To Date :");

	private Label lblPrimaryCat=new Label("Primary Category :");
	private ComboBox primaryCat= new ComboBox();

	private Label lblmainCat=new Label("Main Category : ");
	private ComboBox mainCat=new ComboBox();
	private CheckBox chkMainCatAll = new CheckBox("All");

	private Label lblsubGroupList=new Label("Sub-Group List :");
	private ComboBox groupList=new ComboBox();
	private CheckBox chkGroupAll = new CheckBox("All");

	private Label lblgroupList=new Label("Group List :");
	private ComboBox subGroupList=new ComboBox();
	private CheckBox chkSubGroupAll = new CheckBox("All");

	private static final List<String>areatype  = Arrays.asList(new String[] {"Pdf", "Other"});
	private OptionGroup subjectType = new OptionGroup("",areatype);

	private String type;
	CommonButton button= new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	public TestGroupLedgerSummary(SessionBean sessionBean,String type)
	{
		this.type = type;
		this.sessionBean = sessionBean;
		this.setWidth("450px");
		this.setResizable(false);

		setContent(mainLayout);
		buildMainLayout();

		if(type.equalsIgnoreCase("asOnDate"))
		{
			this.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) :: "+this.sessionBean.getCompany());
			lblFromDate.setVisible(false);
			fromDate.setVisible(false);
			lblDate.setValue("Date :");
		}

		else if(type.equalsIgnoreCase("dateRange"))
		{
			this.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) :: "+this.sessionBean.getCompany());
		}

		setEventAction();
	}

	private void setEventAction()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		primaryCat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				mainCat.removeAllItems();
				groupList.removeAllItems();
				subGroupList.removeAllItems();
				mainCatInitialise();
			}
		});

		mainCat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(mainCat.getValue()!=null)
				{
					groupList.removeAllItems();
					subGroupList.removeAllItems();
					grouListInitialise();
				}
			}
		});

		groupList.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(groupList.getValue()!=null)
				{
					subGroupList.removeAllItems();
					subGroupListInitialise();
				}
			}
		});

		chkMainCatAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkMainCatAll.booleanValue()==true)
				{
					mainCat.setValue(null);
					mainCat.setEnabled(false);
					groupList.removeAllItems();
				}
				else
				{
					mainCat.setEnabled(true);
				}
			}
		});

		chkGroupAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkGroupAll.booleanValue()==true)
				{
					groupList.setValue(null);
					groupList.setEnabled(false);
					subGroupList.removeAllItems();
				}
				else
				{
					groupList.setEnabled(true);
				}
			}
		});

		chkSubGroupAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkSubGroupAll.booleanValue()==true)
				{
					subGroupList.setValue(null);
					subGroupList.setEnabled(false);
				}
				else
				{
					subGroupList.setEnabled(true);
				}
			}
		});
		primaryCat.setValue("A");
	}

	private void mainCatInitialise()
	{
		mainCat.removeAllItems();
		String sql = "";

		sql = " SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE" +
				" substring(headId,1,1) like '"+primaryCat.getValue().toString()+"%"+"' order by slNo";

		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				mainCat.addItem(element[0].toString());
				mainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void grouListInitialise()
	{
		groupList.removeAllItems();
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT groupId,groupName FROM TbMainGroup WHERE headId = '"+mainCat.getValue()+"'").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				groupList.addItem(element[0].toString());
				groupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			groupList.setNullSelectionAllowed(false);			
		}
		catch(Exception exp)
		{
			showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void subGroupListInitialise()
	{
		subGroupList.removeAllItems();
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+groupList.getValue()+"'")
					.list();
			subGroupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				subGroupList.addItem(element[0].toString());
				subGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void preBtnAction()
	{
		if(primaryCat.getValue()!=null)
		{
			if(mainCat.getValue()!=null || chkMainCatAll.booleanValue()==true)
			{
				if(groupList.getValue()!=null || chkGroupAll.booleanValue()==true)
				{
					if(subGroupList.getValue()!=null || chkSubGroupAll.booleanValue()==true)
					{
						if(type.equalsIgnoreCase("dateRange"))
						{
							if (chkDate())
								showReport();
						}
						else
						{
							showReport();	
						}
					}
					else
					{showNotification("Warning!","Select subgroup list.",Notification.TYPE_WARNING_MESSAGE);}
				}
				else
				{showNotification("Warning!","Select group list.",Notification.TYPE_WARNING_MESSAGE);}
			}
			else
			{showNotification("Warning!","Select main category.",Notification.TYPE_WARNING_MESSAGE);}
		}
		else
		{showNotification("Warning!","Select primary category.",Notification.TYPE_WARNING_MESSAGE);}
	}

	private void showReport()
	{
		Object objFromDate = null;
		String head = primaryCat.getValue().toString();
		HashMap hm = new HashMap();
		String groupName="";
		String createFrom,mainCategory,groupListing,subGroupListing;
		String sql = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();

			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();

			tx.commit();

			if(type.equalsIgnoreCase("dateRange"))
			{
				objFromDate = fromDate.getValue();
			}			
			else
			{
				objFromDate = dt;
			}

			sessionBean.setAsOnDate(toDate.getValue());

			if(chkMainCatAll.booleanValue()==true)
			{
				head = head;
				mainCategory = "All";
			}
			else
			{
				head = mainCat.getValue().toString();
				mainCategory = mainCat.getItemCaption(mainCat.getValue().toString());
			}

			if(chkGroupAll.booleanValue()==true)
			{
				head = head+"%";
				groupListing = "All";
			}
			else
			{
				head = head+"-"+groupList.getValue().toString();
				groupListing = groupList.getItemCaption(groupList.getValue().toString());
			}

			if(chkSubGroupAll.booleanValue()==true && chkGroupAll.booleanValue()==true)
			{
				head = head;
				subGroupListing = "All";
			}
			else if(chkSubGroupAll.booleanValue()==true)
			{
				head = head+"%";
				subGroupListing = "All";
			}
			else 
			{
				head = head+"-"+subGroupList.getValue().toString();
				subGroupListing = subGroupList.getItemCaption(subGroupList.getValue().toString());
			}

			if(head.equals("L1%"))
			{
				head = "L1";
			}

			hm.put("toDate", toDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("fromDate", objFromDate);
			hm.put("head",head+"%");
			hm.put("groupName", groupName);
			hm.put("createFrom",  primaryCat.getItemCaption(primaryCat.getValue().toString()));
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("url", this.getWindow().getApplication().getURL()+"");
			hm.put("logo", sessionBean.getCompanyLogo());

			/*hm.put("mainCategory", mainCategory);
			hm.put("groupList", groupListing);
			hm.put("subGroupList", subGroupListing);*/

			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);

			if(type.equalsIgnoreCase("asOnDate"))
			{
				/*sql = " Select A.ledger_id,A.Ledger_Name,SUM(A.opBal) as opBal,SUM(A.drAmt) as drAmt,SUM(A.crAmt) as crAmt" +
						" from (SELECT l.ledger_id, l.Ledger_Name,0 as opBal, SUM(DrAmount) drAmt,SUM(CrAmount) crAmt FROM" +
						" vwVoucher as v inner join tbLedger as l ON v.Ledger_Id = l.Ledger_Id WHERE l.Create_From Like" +
						" '"+head+"%"+"' AND (v.Date <= '"+dtfYMD.format(toDate.getValue())+"') AND (v.chqClear = 1) AND l.companyId = '"+sessionBean.getCompanyId()+"' " +
						" AND  v.companyId = '"+sessionBean.getCompanyId()+"' AND auditapproveflag = 2 group by l.Ledger_Name,l.Ledger_Id" +
						" Union SELECT l.ledger_id,l.Ledger_Name,lo.DrAmount - lo.CrAmount AS opBal, 0 AS drAmt, 0 AS crAmt" +
						" FROM dbo.tbLedger AS l LEFT OUTER JOIN dbo.tbLedger_Op_Balance AS lo ON l.Ledger_Id = lo.Ledger_Id" +
						" WHERE lo.Op_Year = Year('"+dtfYMD.format(objFromDate)+"') and l.Create_From Like '"+head+"%"+"' And" +
						" (l.companyId = '"+sessionBean.getCompanyId()+"') AND (lo.companyId = '"+sessionBean.getCompanyId()+"')) A Group by  A.Ledger_Name, A.ledger_id";*/
				sql = " select * from funLedgerGroupSummary('"+head+"', '"+dtfYMD.format(objFromDate)+"'," +
						" '"+dtfYMD.format(objFromDate)+"', '"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"', '0')" +
						" where opBal!=0 or drAmt!=0 or crAmt!=0 " +
						" order by CAST(SUBSTRING(priGroupId,2,len(priGroupId))as int)," +
						" CAST(SUBSTRING(mainGroupId,2,len(mainGroupId))as int)," +
						" CAST(SUBSTRING(subGroupId,2,len(subGroupId))as int) ";

				hm.put("sql", sql);
				System.out.println("SQL: "+sql);
				if(subjectType.getValue().toString().equals("Other"))
				{
					if(queryValueCheck(sql))
					{
						System.out.println("GOT AS ON DATE");
						Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupAsOnDate.jasper",
								this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
								this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
						this.getParent().getWindow().addWindow(win);
						win.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) REPORT :: "+sessionBean.getCompany());
					}
					else
					{
						showNotification("Warning!","There are no data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					System.out.println("GOT AS ON DATE");
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupAsOnDate.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) REPORT :: "+sessionBean.getCompany());
				}
			}
			else if(type.equalsIgnoreCase("dateRange"))
			{
				sql = " select * from funLedgerGroupSummary('"+head+"', '"+dtfYMD.format(objFromDate)+"'," +
						" '"+dtfYMD.format(objFromDate)+"', '"+dtfYMD.format(toDate.getValue())+"', '"+sessionBean.getCompanyId()+"', '1')" +
						" where opBal!=0 or drAmt!=0 or crAmt!=0 " +
						" order by CAST(SUBSTRING(priGroupId,2,len(priGroupId))as int)," +
						" CAST(SUBSTRING(mainGroupId,2,len(mainGroupId))as int)," +
						" CAST(SUBSTRING(subGroupId,2,len(subGroupId))as int) ";

				System.out.println("SQL1 : "+sql);
				hm.put("sql", sql);
				if(subjectType.getValue().toString().equals("Other"))
				{
					if(queryValueCheck(sql))
					{
						System.out.println("GOT AS ON DATE");
						Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupOnDateRange.jasper",
								this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
								this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
								this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
						this.getParent().getWindow().addWindow(win);
						win.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) REPORT :: "+sessionBean.getCompany());
					}
					else
					{
						showNotification("Warning!","There are no data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupOnDateRange.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) REPORT :: "+sessionBean.getCompany());
				}
			}
			else
			{
				Window win = new ReportPdf(hm,"report/account/mis/monthlyGroupSummary.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",true);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("GROUP WISE LEDGER SUMMARY :: "+sessionBean.getCompany());
			}

		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean chkDate()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();

		if (f.equals("1"))	
		{
			return true;
		}
		else
		{
			this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;
		System.out.println("Query="+sql);
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout.setImmediate(false);
		mainLayout.setWidth("430px");
		mainLayout.setHeight("260px");
		mainLayout.setMargin(false);

		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate,"top:20px;left:30.0px;");

		fromDate.setWidth("110px");
		fromDate.setHeight("-1px");
		fromDate.setValue(new java.util.Date());
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setImmediate(true);
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		mainLayout.addComponent(fromDate,"top:18px;left:140.0px;");

		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate,"top:45px;left:30.0px;");

		toDate.setWidth("110px");
		toDate.setHeight("-1px");
		toDate.setValue(new java.util.Date());
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setImmediate(true);
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(toDate,"top:43px;left:140.0px;");

		lblPrimaryCat.setWidth("-1px");
		lblPrimaryCat.setHeight("-1px");
		mainLayout.addComponent(lblPrimaryCat,"top:70px;left:30.0px;");

		primaryCat.setWidth("220px");
		primaryCat.setHeight("23px");
		/*primaryCat.addItem("%");
		primaryCat.setItemCaption("%", "All");*/
		primaryCat.addItem("A");
		primaryCat.setItemCaption("A", "Assets");
		primaryCat.addItem("L");
		primaryCat.setItemCaption("L", "Liabilities");
		primaryCat.addItem("I");
		primaryCat.setItemCaption("I", "Income");
		primaryCat.addItem("E");
		primaryCat.setItemCaption("E", "Expenses");
		primaryCat.setImmediate(true);
		primaryCat.setNullSelectionAllowed(false);
		mainLayout.addComponent(primaryCat,"top:68px;left:140.0px;");

		lblmainCat.setWidth("-1px");
		lblmainCat.setHeight("-1px");
		mainLayout.addComponent(lblmainCat,"top:95px;left:30.0px;");

		mainCat.setWidth("220px");
		mainCat.setHeight("23px");
		mainCat.setImmediate(true);
		mainCat.setNullSelectionAllowed(true);
		mainLayout.addComponent(mainCat,"top:93px;left:140.0px;");

		chkMainCatAll.setImmediate(true);
		chkMainCatAll.setHeight("-1px");
		chkMainCatAll.setWidth("-1px");
		mainLayout.addComponent(chkMainCatAll, "top:97px;left:365.0px;");

		lblgroupList.setWidth("-1px");
		lblgroupList.setHeight("-1px");
		mainLayout.addComponent(lblgroupList,"top:120px;left:30.0px;");

		groupList.setWidth("220px");
		groupList.setHeight("23px");
		groupList.setImmediate(true);
		groupList.setNullSelectionAllowed(true);
		mainLayout.addComponent( groupList,"top:118px;left:140.0px;");

		chkGroupAll.setHeight("-1px");
		chkGroupAll.setWidth("-1px");
		chkGroupAll.setImmediate(true);
		mainLayout.addComponent(chkGroupAll, "top:122px;left:365.0px;");

		lblsubGroupList.setWidth("-1px");
		lblsubGroupList.setHeight("-1px");
		mainLayout.addComponent(lblsubGroupList,"top:145px;left:30.0px;");

		subGroupList.setWidth("220px");
		subGroupList.setHeight("23px");
		subGroupList.setImmediate(true);
		subGroupList.setNullSelectionAllowed(true);
		mainLayout.addComponent(subGroupList,"top:143px;left:140.0px;");

		chkSubGroupAll.setImmediate(true);
		chkSubGroupAll.setHeight("-1px");
		chkSubGroupAll.setWidth("-1px");
		mainLayout.addComponent(chkSubGroupAll, "top:147px;left:365.0px;");

		subjectType.setImmediate(true);
		subjectType.setStyleName("horizontal");
		subjectType.select("Pdf");
		mainLayout.addComponent(subjectType, "top:175px;left:140.0px;");

		mainLayout.addComponent(button,"top:215px;left:140.0px;");

		return mainLayout;
	}
}
