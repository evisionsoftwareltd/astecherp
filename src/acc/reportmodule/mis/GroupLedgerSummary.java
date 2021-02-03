package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public class GroupLedgerSummary extends Window 
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	ArrayList<Component> comp = new ArrayList<Component>();
	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");
	//private ComboBox costCentre = new ComboBox("Cost Centre:");
	private NativeSelect primaryCat = new NativeSelect("Primary Category:");
	private NativeSelect mainCat = new NativeSelect("Main Category:");
	private NativeSelect groupList = new NativeSelect("Group List:");
	private NativeSelect subGroupList = new NativeSelect("Sub-Group List:");
	private String lcw = "120px";
	private String cw = "230px";

	private static final List<String>areatype  = Arrays.asList(new String[] {"Pdf", "Other"});
	private OptionGroup subjectType = new OptionGroup("",areatype);

	private String type;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	public GroupLedgerSummary(SessionBean sessionBean,String type)
	{
		this.type = type;
		this.sessionBean = sessionBean;
		this.setWidth("450px");
		this.setResizable(false);		
		if(type.equalsIgnoreCase("asOnDate"))
		{
			System.out.println("GROUP ACCOUNT SUMMARY (AS ON DATE) :: "+this.sessionBean.getCompany());
			this.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) :: "+this.sessionBean.getCompany());
			//costCentre.setVisible(false);
			comp.add(toDate);
			comp.add(primaryCat);
			comp.add(mainCat);
			comp.add(groupList);
			comp.add(subGroupList);
			comp.add(subjectType);
			comp.add(button.btnPreview);
			toDate.focus();
		}
		else if(type.equalsIgnoreCase("dateRange"))
		{
			System.out.println("GROUP ACCOUNT SUMMARY (DATE Range) :: "+this.sessionBean.getCompany());
			this.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) :: "+this.sessionBean.getCompany());
			formLayout.addComponent(fromDate);
			fromDate.setWidth(lcw);
			fromDate.setValue(sessionBean.getFiscalOpenDate());
			fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
			fromDate.setDateFormat("dd-MM-yy");
			fromDate.setInvalidAllowed(false);
			fromDate.setImmediate(true);
			//costCentre.setVisible(false);
			comp.add(fromDate);
			comp.add(toDate);
			comp.add(primaryCat);
			comp.add(mainCat);
			comp.add(groupList);
			comp.add(subGroupList);
			comp.add(subjectType);
			comp.add(button.btnPreview);
			fromDate.focus();
		}
		else
		{
			this.setCaption("MONTH WISE GROUP SUMMARY :: "+this.sessionBean.getCompany());			
			comp.add(toDate);
			//comp.add(costCentre);
			comp.add(primaryCat);
			comp.add(mainCat);
			comp.add(groupList);
			comp.add(subGroupList);
			comp.add(button.btnPreview);
			toDate.focus();
		}

		formLayout.addComponent(toDate);
		/*formLayout.addComponent(costCentre);
		costCentre.setWidth(cw);*/
		formLayout.addComponent(primaryCat);
		primaryCat.setImmediate(true);
		primaryCat.setWidth(cw);
		primaryCat.setImmediate(true);
		/*primaryCat.addItem("1");
		primaryCat.setItemCaption("1", "All");
		primaryCat.addItem("2");
		primaryCat.setItemCaption("2", "Assets");
		primaryCat.addItem("3");
		primaryCat.setItemCaption("3", "Liabilities");*/
		primaryCat.addItem("4");
		primaryCat.setItemCaption("4", "Income");
		primaryCat.addItem("5");
		primaryCat.setItemCaption("5", "Expenses");
		primaryCat.setImmediate(true);
		primaryCat.setNullSelectionAllowed(false);

		formLayout.addComponent(mainCat);
		mainCat.setImmediate(true);
		mainCat.setWidth(cw);
		mainCat.setImmediate(true);
		mainCat.setImmediate(true);		

		formLayout.addComponent(groupList);
		groupList.setImmediate(true);
		groupList.setWidth(cw);
		groupList.setImmediate(true);


		formLayout.addComponent(subGroupList);
		subGroupList.setImmediate(true);
		subGroupList.setWidth(cw);
		subGroupList.setImmediate(true);

		formLayout.addComponent(subjectType);
		subjectType.setImmediate(true);
		subjectType.setStyleName("horizontal");
		subjectType.select("Pdf");

		toDate.setWidth(lcw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		primaryCat.setValue("1");
		new FocusMoveByEnter(this, comp);
		costCenterInitialise();
	}
	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//	System.out.println("Sa");
				preBtnAction(event);
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
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				primaryCatSelect();
			}
		});
		mainCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				mainCatSelect();
			}
		});
		groupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				groupListSelect();
			}
		});
	}

	private void primaryCatSelect()
	{
		mainCat.removeAllItems();
		groupList.removeAllItems();
		subGroupList.removeAllItems();
		mainCatInitialise();
	}

	private void groupListSelect()
	{
		if(groupList.getValue()!=null)
		{
			subGroupList.removeAllItems();
			subGroupListInitialise();
		}
	}

	private void mainCatSelect()
	{
		if(mainCat.getValue()!=null)
		{
			groupList.removeAllItems();
			subGroupList.removeAllItems();
			grouListInitialise();
		}
	} 

	private void mainCatInitialise()
	{
		String sql = "";
		if(primaryCat.getValue().toString().equalsIgnoreCase("1"))
		{
			//sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup where Head_Id <>'A4' order by substring(headId,1,1),slNo";
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup  order by substring(headId,1,1),slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("2"))
		{
			//	sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE Head_Id <>'A4' and substring(headId,1,1) = 'A' order by slNo";
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE  substring(headId,1,1) = 'A' order by slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("3"))
		{
			//sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE Head_Id <>'A4' and substring(headId,1,1) = 'L' order by slNo";
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE  substring(headId,1,1) = 'L' order by slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("4"))
		{
			//sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE Head_Id <>'A4' and substring(headId,1,1) = 'I' order by slNo";
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE  substring(headId,1,1) = 'I' order by slNo";
		}
		else
		{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			//sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE Head_Id <>'A4' and substring(headId,1,1) = 'E' order by slNo";
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE  substring(headId,1,1) = 'E' order by slNo";
		}
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			mainCat.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				mainCat.addItem(element[0].toString());
				mainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
			mainCat.setNullSelectionAllowed(false);
			mainCat.setValue("");
		}
		catch(Exception exp)
		{
			//System.out.println(sql);
			//System.out.println(exp);
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void grouListInitialise()
	{
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
			subGroupList.setNullSelectionAllowed(false);
			subGroupList.setValue("");
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void preBtnAction(ClickEvent event)
	{

		//		if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate())))
		//		{

		if(mainCat.getValue().toString().trim().length()>0)
			if(type.equalsIgnoreCase("dateRange"))
			{
				if (chkDate())
					showReport();
			}
			else
			{
				showReport();	
			}
		else
			showNotification("","Please select main category.",Notification.TYPE_WARNING_MESSAGE);

		//		}
		//		else
		//		{
		//			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//		}	
	}

	private void showReport()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			//hm.put("fromDate", dt);
			tx.commit();

			HashMap hm = new HashMap();
			String groupName,createFrom;
			//hm.put("fromTo", dateRpt.format(fromDate.getValue())+" To "+dateRpt.format(toDate.getValue()));
			//hm.put("fromDate", dateFormatter.format(fromDate.getValue()));
			if(type.equalsIgnoreCase("dateRange"))
			{
				hm.put("fromDate", fromDate.getValue());
			}			
			else
			{
				hm.put("fromDate", dt);
			}

			hm.put("toDate", toDate.getValue());

			hm.put("comName",sessionBean.getCompany());

			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			//	System.out.println(costCentre.getValue().toString());
			//System.out.println("-3");

			hm.put("costCentre","All");//costCentre.getItemCaption(costCentre.getValue()+""));
			hm.put("costId","%");//costCentre.getValue()+"");

			//			if(costCentre.getValue().toString().equals(-1))
			//			{
			//				System.out.println("-3");
			//				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
			//				hm.put("costId","%");
			//				
			//				System.out.println(costCentre.getItemCaption(costCentre.getValue()));
			//				System.out.println("-1");
			//			}
			//			else
			//			{
			//				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
			//				hm.put("costId",costCentre.getValue()+"");
			//				System.out.println(costCentre.getItemCaption(costCentre.getValue()));
			//				System.out.println("-2");
			//			}

			String head = mainCat.getValue().toString();
			groupName = mainCat.getItemCaption(mainCat.getValue().toString());
			//			System.out.println(groupName);
			//	System.out.println(groupList.getItemCaption(groupList.getValue().toString()));//.trim().length());
			//System.out.println(groupList.getValue().toString());
			//	System.out.println(!groupList.getItemCaption(groupList.getValue().toString().trim()).equals(""));
			System.out.println("Mu");
			if(groupList.getValue()!=null)//().toString()==null
			{
				//				System.out.println("7");
				head = head+"-"+groupList.getValue().toString();
				groupName = groupList.getItemCaption(groupList.getValue().toString());
				System.out.println(groupName);
			}
			else
			{
				//				System.out.println("5");
				//				head = head+"-"+groupList.getValue().toString();
				//				groupName = groupList.getItemCaption(groupList.getValue().toString());

			}
			//if(subGroupList.getValue().toString().trim().length()>0)
			//			System.out.println("6");
			if(subGroupList.getValue()!=null && !subGroupList.getValue().toString().equals(""))
			{
				head = head+"-"+subGroupList.getValue().toString();
				System.out.println(head);
				groupName = subGroupList.getItemCaption(subGroupList.getValue().toString());
				System.out.println(groupName);
			}
			else 
			{

			}
			//			head = head+"-"+groupList.getValue().toString();
			//			groupName = groupList.getItemCaption(groupList.getValue().toString());
			//			head = head+"-"+subGroupList.getValue().toString();
			//			groupName = subGroupList.getItemCaption(subGroupList.getValue().toString());

			hm.put("head",head+"%");
			//hm.put("head",head);
			System.out.println(head);
			//hm.put("withNarration", !new Boolean(withoutChk.getValue().toString()));
			System.out.println("Sa");
			createFrom = "";
			if(head.startsWith("A"))
				createFrom = "Assets";
			else if(head.startsWith("L"))
				createFrom = "Liabilities";
			else if(head.startsWith("I"))
				createFrom = "Income";
			else if(head.startsWith("E"))
				createFrom = "Expenses";
			sessionBean.setAsOnDate(toDate.getValue());
			hm.put("groupName", groupName);
			hm.put("createFrom", createFrom);
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("url", this.getWindow().getApplication().getURL()+"");

			Object b=this.getWindow().getApplication().getContext().getBaseDirectory();

			sessionBean.setUrl(getWindow().getApplication().getURL());

			sessionBean.setP(b);

			if(type.equalsIgnoreCase("asOnDate"))
			{
				if(subjectType.getValue().toString().equals("Other"))
				{
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupAsOnDate.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
					
					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) REPORT :: "+sessionBean.getCompany());
				}
				else
				{
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupAsOnDate.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
					
					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (AS ON DATE) REPORT :: "+sessionBean.getCompany());
				}
			}
			else if(type.equalsIgnoreCase("dateRange"))
			{
				if(subjectType.getValue().toString().equals("Other"))
				{
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupOnDateRange.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) REPORT :: "+sessionBean.getCompany());
				}
				else
				{
					Window	win = new ReportViewer(hm,"report/account/mis/ledgerGroupOnDateRange.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

					win.setStyleName("cwindow");
					this.getParent().getWindow().addWindow(win);
					win.setCaption("GROUP ACCOUNT SUMMARY (DATE RANGE) REPORT :: "+sessionBean.getCompany());
				}
			}
			else
			{
				Window win = new ReportViewer(hm,"report/account/mis/monthlyGroupSummary.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				win.setCaption("GROUP WISE LEDGER SUMMARY :: "+sessionBean.getCompany());
			}

		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCenterInitialise()
	{/*
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();


			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
				costCentre.setValue(element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}*/
	}
	private boolean chkDate()
	{
		//		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		//		{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
		//System.out.println(f);
		if (f.equals("1"))	
			//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
			//					&&
			//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
		{
			return true;
		}
		else
		{
			this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
		//		}
		//		else
		//		{
		//			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
		//			return false;
		//		}
	}
}
