package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RptCancelChequeList extends Window {


	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblUser;
	private ComboBox cmbUser;

	private Label lblFromDate; 
	private PopupDateField dFromDate;

	private Label lblToDate; 
	private PopupDateField dToDate;

	private CheckBox chkAll;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateF = new SimpleDateFormat("dd-MM-yyyy");

	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");

	public RptCancelChequeList(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("CANCEL CHEQUE LIST :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		btnAction();

		addComboData();
	}

	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnChk();
			}
		});


		chkAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				checkBoxAction();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void previewBtnChk()
	{
		if(cmbUser.getValue()!=null || chkAll.booleanValue()==true)
		{
			previewBtnAction();
		}
		else
		{
			showNotification("Warning!","Select User Name",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void checkBoxAction()
	{
		if(chkAll.booleanValue()==true)
		{
			cmbUser.setValue(null);
			cmbUser.setEnabled(false);
		}
		else
		{
			cmbUser.setEnabled(true);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

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

	private void addComboData()
	{
		cmbUser.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select userId,name from tbLogin where name not like 'Admin' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbUser.addItem(element[0].toString());
				cmbUser.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void previewBtnAction()
	{
		String queryAll = null;
		String all= "";
		String userName="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();

			if(chkAll.booleanValue()==true)
			{
				all = "%";
				userName = "All";
			}
			else
			{
				all = cmbUser.getValue().toString();
				userName = cmbUser.getItemCaption(cmbUser.getValue().toString());
			}

			queryAll =  " select l.Ledger_Name,cb.bookNo,cb.date,cb.folioNo from tbChequeBook cb left join tbLedger " +
						" l on cb.ledgerId=l.Ledger_Id inner join tbLogin lo on lo.userId=cb.userId where date between '"+dateFormat.format(dFromDate.getValue())+"' and" +
						" '"+dateFormat.format(dToDate.getValue())+"' and cb.userId like '"+all+"' and status = 'CANCEL'" +
						"  ";

			System.out.println("Report Query: "+queryAll);

			hm.put("comName", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax", sessionBean.getCompanyContact());

			hm.put("userName", userName);
			hm.put("userNameIp", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("dateFrom", dateF.format(dFromDate.getValue()));
			hm.put("dateTo", dateF.format(dToDate.getValue()));
			hm.put("logo", sessionBean.getCompanyLogo());

			if(queryValueCheck(queryAll))
			{
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/account/mis/rptCancelChequeList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

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
			System.out.println(exp);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("420px");
		setHeight("200px");

		// lblFromDate
		lblFromDate = new Label();
		lblFromDate.setImmediate(true);
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		lblFromDate.setValue("From : ");
		mainLayout.addComponent(lblFromDate, "top:20.0px; left:60.0px;");

		//dFromDate
		dFromDate=new PopupDateField();
		dFromDate.setWidth("110px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:18.0px; left:130.0px;");

		// lblFromDate
		lblToDate = new Label();
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		lblToDate.setValue("To : ");
		mainLayout.addComponent(lblToDate, "top:48.0px; left:60.0px;");

		//dToDate
		dToDate=new PopupDateField();
		dToDate.setWidth("110px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:45.0px; left:130.0px;");

		// lblProductName
		lblUser = new Label();
		lblUser.setImmediate(true);
		lblUser.setWidth("-1px");
		lblUser.setHeight("-1px");
		lblUser.setValue("User : ");
		mainLayout.addComponent(lblUser, "top:75; left:60.0px;");

		cmbUser = new ComboBox();
		cmbUser.setImmediate(true);
		cmbUser.setEnabled(true);
		cmbUser.setHeight("-1px");
		cmbUser.setWidth("200px");
		cmbUser.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbUser, "top:73.0px; left:130.0px;");

		//chkAll
		chkAll = new CheckBox("All");
		chkAll.setImmediate(true);
		chkAll.setWidth("-1px");
		chkAll.setHeight("-1px");
		mainLayout.addComponent(chkAll, "top:75.0px; left:335.0px;");

		//CButton
		mainLayout.addComponent(button, "top:110.0px; left:130.0px;");
		return mainLayout;
	}

}
