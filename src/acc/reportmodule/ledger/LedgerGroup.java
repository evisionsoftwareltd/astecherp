package acc.reportmodule.ledger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;

public class LedgerGroup extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private FormLayout formLayoutCost = new FormLayout();
	private FormLayout formButton = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	
	private HorizontalLayout h1Layout = new HorizontalLayout();
	private HorizontalLayout h2Layout = new HorizontalLayout();
	private HorizontalLayout hMainLayout = new HorizontalLayout();

	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");

	private ComboBox costCentre = new ComboBox("Cost Centre:"); 
	private ComboBox primaryCat = new ComboBox("Primary Category:");
	private ComboBox mainCat = new ComboBox("Main Category:");
	private ComboBox groupList = new ComboBox("Group List:");
	private ComboBox subGroupList = new ComboBox("Sub-Group List:");

	private CheckBox withoutChk = new CheckBox("Without Narration:");
	private CheckBox chkAll = new CheckBox("All");

	//private NativeButton preBtn = new NativeButton("Preview");
	//private NativeButton exitBtn = new NativeButton("Exit");

	private String lcw = "130px";
	private String cw = "230px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	
	private Label lblHeight = new Label();
	
	public LedgerGroup(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("480px");
		this.setResizable(false);
		this.setCaption("LEDGER (GROUP) :: "+this.sessionBean.getCompany());

		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		costCentre.setImmediate(true);
		costCentre.setNullSelectionAllowed(false);
	
		formLayout.addComponent(costCentre);
		
		chkAll.setImmediate(true);
		chkAll.setValue(true);
		costCentre.setEnabled(false);

		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.getValue().equals(true))
				{
					costCentre.setEnabled(false);
					costCentre.setValue(null);
				}
				else
				{
					costCentre.setEnabled(true);
				}
			}
		});
		
		lblHeight.setHeight("55px");
		formButton.addComponent(lblHeight);
		formButton.addComponent(chkAll);
		
		
/*		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);*/

		
		costCentre.setWidth(cw);
		formLayout.addComponent(primaryCat);
		primaryCat.setImmediate(true);
		primaryCat.setWidth(cw);
		primaryCat.setImmediate(true);
		primaryCat.addItem("1");
		primaryCat.setItemCaption("1", "All");
		primaryCat.addItem("2");
		primaryCat.setItemCaption("2", "Assets");
		primaryCat.addItem("3");
		primaryCat.setItemCaption("3", "Liabilities");
		primaryCat.addItem("4");
		primaryCat.setItemCaption("4", "Income");
		primaryCat.addItem("5");
		primaryCat.setItemCaption("5", "Expenses");
		primaryCat.setImmediate(true);
		primaryCat.setNullSelectionAllowed(false);
		primaryCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				primaryCatSelect();
			}
		});

		formLayout.addComponent(mainCat);
		mainCat.setImmediate(true);
		mainCat.setWidth(cw);
		mainCat.setImmediate(true);
		mainCat.setImmediate(true);
		mainCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				mainCatSelect();
			}
		});
		
		/*chkAll.setImmediate(true);
		chkAll.setValue(true);
		costCentre.setEnabled(false);

		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAll.getValue().equals(true))
				{
					costCentre.setEnabled(false);
					costCentre.setValue(null);
				}
				else
				{
					costCentre.setEnabled(true);
				}
			}
		});
		
		lblHeight.setHeight("55px");
		formButton.addComponent(lblHeight);
		formButton.addComponent(chkAll);
		
		
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		h1Layout.addComponent(formLayoutCost);
		h2Layout.addComponent(formButton);
		
		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		formLayout.addComponent(hMainLayout);*/
		
		formLayout.addComponent(groupList);
		groupList.setImmediate(true);
		groupList.setWidth(cw);
		groupList.setImmediate(true);
		groupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				groupListSelect();
			}
		});

		formLayout.addComponent(subGroupList);
		subGroupList.setImmediate(true);
		subGroupList.setWidth(cw);
		subGroupList.setImmediate(true);
		/*subGroupList.addListener(new ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				subGroupListSelect();
			}
		});*/
		//ledgerInitialise();

		formLayout.addComponent(withoutChk);
		withoutChk.setImmediate(true);

		fromDate.setWidth(lcw);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setWidth(lcw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		//		btnL.addComponent(exitBtn);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(btnL);
		
		
		h1Layout.addComponent(formLayout);
		h2Layout.addComponent(formButton);
		
		hMainLayout.addComponent(h1Layout);
		hMainLayout.addComponent(h2Layout);
		//formLayout.addComponent(hMainLayout);
		
		mainLayout.addComponent(hMainLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		Component comp[] = {fromDate, toDate, costCentre, primaryCat, mainCat, groupList, subGroupList, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		primaryCat.setValue("1");
		costCenterInitialise();
		costCentre.setValue("-1");
		fromDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
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
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup order by substring(headId,1,1),slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("2"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'A' order by slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("3"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'L' order by slNo";
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("4"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'I' order by slNo";
		}
		else
		{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'E' order by slNo";
		}
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
//			mainCat.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				mainCat.addItem(element[0].toString());
				mainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
//			mainCat.setNullSelectionAllowed(false);
//			mainCat.setValue("");
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
//			groupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				groupList.addItem(element[0].toString());
				groupList.setItemCaption(element[0].toString(), element[1].toString());
			}
//			groupList.setNullSelectionAllowed(false);
//			groupList.setValue("");
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void subGroupListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+groupList.getValue()+"'").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				subGroupList.addItem(element[0].toString());
				subGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void preBtnAction(ClickEvent event)
	{
		try
		{
//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//				&&
//			(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//		{
//			if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
//			{
			if (chkDate())
				if(mainCat.getValue() != null)
					showReport();
//				else
//					showNotification("","Please select main category.",Notification.TYPE_WARNING_MESSAGE);
//			}
//			else
//			{
//				showNotification("","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
//			}
//		}
//		else
//		{
//			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}
		}
		catch(Exception ex)
		{
			showNotification(ex.toString());
		}
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
				tx.commit();
				
			HashMap hm = new HashMap();
			hm.put("fromTo", dtfDMY.format(fromDate.getValue())+" To "+dtfDMY.format(toDate.getValue()));
			hm.put("fromDate", dtfYMD.format(fromDate.getValue()));
			hm.put("toDate", dtfYMD.format(toDate.getValue()));			
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());			
			hm.put("phoneFax",sessionBean.getCompanyContact());
			
			hm.put("logo", sessionBean.getCompanyLogo());


/*			if(costCentre.getValue().toString().equals("-1"))
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue().toString()));
				hm.put("costId","%");
			}
			else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue().toString()));
				hm.put("costId",costCentre.getValue().toString());
			}*/
			
			if(costCentre.getValue()==null)
			{
				//hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costCentre","All");
				hm.put("costId","%");
			}
			else
			{
				hm.put("costCentre",costCentre.getItemCaption(costCentre.getValue()+""));
				hm.put("costId",costCentre.getValue()+"");
			}
			
			String head = mainCat.getValue().toString();
			if(groupList.getValue() != null)
				head = head+"-"+groupList.getValue().toString();
			if(subGroupList.getValue() != null)
				head = head+"-"+subGroupList.getValue().toString();
			System.out.println("h");
			
			
//			if(groupList.getValue().toString().trim().length()>0)
//				head = head+"-"+groupList.getValue().toString();
//			if(subGroupList.getValue().toString().trim().length()>0)
//				head = head+"-"+subGroupList.getValue().toString();
//			
			hm.put("head",head+"%");
			System.out.println(head);
			
	//		hm.put("ledgerId",head+"%");
			hm.put("withNarration", !new Boolean(withoutChk.getValue().toString()));
			hm.put("SUBREPORT_DIR","report/account/book/");
			
			Window win = new ReportPdf(hm,"report/account/book/LedgerGroup.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);

			this.getParent().getWindow().addWindow(win);
			win.setStyleName("cwindow");
			win.setCaption("LEDGER(GROUP) :: "+sessionBean.getCompany());
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCenterInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();

//			costCentre.addItem("-1");
//			costCentre.setItemCaption("-1", "All");

			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
//			costCentre.setValue("-1");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
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
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}
