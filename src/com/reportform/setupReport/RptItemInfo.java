package com.reportform.setupReport;

import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class RptItemInfo extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	
	private Label lblProductName;
	
	private ComboBox cmbProductName;
	
	private String[] Item  = new String[]{"All Item in English","All Item in Bangla"};
	
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	
	public RptItemInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("ITEM INFORMATION :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		
		buildMainLayout();
		setContent(mainLayout);
		btnAction();
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
		if(cmbProductName.getValue()!=null)
		{
			System.out.println("Go: "+cmbProductName.getValue().toString());
			if(cmbProductName.getValue().toString().equals("All Item in English"))
			{
				previewBtnActionEng();
			}
			else if(cmbProductName.getValue().toString().equals("All Item in Bangla"))
			{
				previewBtnActionBan();
			}
		}
		else
		{
			showNotification("Warning!","Select Item type");
		}
	}
	
	private void previewBtnActionEng()
	{
		String queryAll = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			
			queryAll = " SELECT * from tbProductInfo order by productCode ";
			
			System.out.println("Report Query: "+queryAll);
			
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("companyId", sessionBean.getCompanyId());
			hm.put("company", sessionBean.getCompany());
			hm.put("UserIp", sessionBean.getUserIp());
			hm.put("UserName", sessionBean.getUserName());
			
			hm.put("sql", queryAll);
			
			Window win = new ReportViewer(hm,"report/account/adminReport/rptProductInfo.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",false);
			
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	private void previewBtnActionBan()
	{
		String queryAll = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();
			
			queryAll = " SELECT * from tbProductInfo order by productCode ";
			
			System.out.println("Report Query: "+queryAll);
			
//			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("companyId", sessionBean.getCompanyId());
//			hm.put("company", sessionBean.getCompany());
			hm.put("UserIp", sessionBean.getUserIp());
			hm.put("UserName", sessionBean.getUserName());
			
			hm.put("sql", queryAll);
			
			Window win = new ReportViewer(hm,"report/account/adminReport/rptProductInfoBangla.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",false);
			
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
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
		
		// lblProductName
		lblProductName = new Label();
		lblProductName.setImmediate(true);
		lblProductName.setWidth("-1px");
		lblProductName.setHeight("-1px");
		lblProductName.setValue("Item Type : ");
		mainLayout.addComponent(lblProductName, "top:45.0px; left:60.0px;");
		
		// cmbProductName
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
//		cmbProductName.setEnabled(false);
		cmbProductName.setHeight("-1px");
		cmbProductName.setWidth("200px");
		cmbProductName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbProductName, "top:42.0px; left:145.0px;");
		for (int i = 0; i < Item.length; i++){
			cmbProductName.addItem(Item[i]);
        }
						
		//CButton
		mainLayout.addComponent(button, "top:100.0px; left:130.0px;");
		
		return mainLayout;
	}
}
