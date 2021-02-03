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
import com.common.share.PreviewOption;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
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

public class ChartOfAccounts extends Window 
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hrLayout = new HorizontalLayout();
	private HorizontalLayout blankLayout = new HorizontalLayout();
	private FormLayout formLayout = new FormLayout();

	private Label lblLine = new Label("_______________________________________________________________");
	private Label lblText = new Label("Please Select The Type of Ledger Then Click on The Preview Button to View The Report");
	private HorizontalLayout btnL = new HorizontalLayout();
	ArrayList<Component> comp = new ArrayList<Component>();

	private static final List<String> options = Arrays.asList(new String[] {
			"All", "Assets", "Liabilities", "Income", "Expenses"});
	OptionGroup optionSelect = new OptionGroup("", options);

	private PreviewOption po = new PreviewOption();

	public ChartOfAccounts(SessionBean sessionBean,String type)
	{
		this.sessionBean = sessionBean;
		this.setWidth("450px");
		this.setResizable(false);		

		this.setCaption("CHART OF ACCOUNTS :: "+this.sessionBean.getCompany());
		
		optionSelect.select("All");
		optionSelect.setImmediate(true);
		blankLayout.setWidth("120px");	
		formLayout.addComponent(optionSelect);
		hrLayout.addComponent(blankLayout);
		hrLayout.addComponent(formLayout);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));

		mainLayout.addComponent(lblText);
		mainLayout.addComponent(hrLayout);
		mainLayout.addComponent(lblLine);
		mainLayout.addComponent(po);
		mainLayout.addComponent(btnL);
		mainLayout.setComponentAlignment(btnL, Alignment.BOTTOM_CENTER);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		new FocusMoveByEnter(this, comp);

	}
	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				String ch = "";
				String selection = "";
				selection = optionSelect.getValue().toString(); 
				if(selection.equals("All")){
					ch = "%";
				}else if(selection.equals("Assets")){
					ch = "A";
				}else if(selection.equals("Liabilities")){
					ch = "L";
				}else if(selection.equals("Income")){
					ch = "I";
				}else{
					ch="E";
				}
				
				viewReport(ch);
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


	private void viewReport(String ch){
		String query="";
		try
		{
			
			HashMap hm = new HashMap();				
			hm.put("comName",sessionBean.getCompany());
			hm.put("address",sessionBean.getCompanyAddress());
			hm.put("companyId",sessionBean.getCompanyId());		
			
			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
				hm.put("logo", sessionBean.getCompanyLogo());
			}
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			session.createSQLQuery("Alter view vwFn_rptChartofAccounts as  SELECT DISTINCT Head_Type, Head_Id, Head_name, Group_Id, Group_name, Sub_Group_Id, Sub_Group_name, Ledger_Id, ledger_name, pos FROM dbo.rptChartofAccounts('"+sessionBean.getCompanyId()+"', '"+ch+"') AS ChartofAccounts ").executeUpdate();
				tx.commit();
				
				
			query = "select * from vwFn_rptChartofAccounts order by head_id,group_name,sub_group_name,Ledger_Name";
			//		"and head_type like '"+ch+"' order by head_id,group_name,sub_group_name,cast(substring(Ledger_Id, 3, 50) as int)";
			System.out.println(query);
			hm.put("sql",query);	
			
/*			Window win = new ReportPdf(hm,"report/account/chartOfAccount.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);*/
			
			
/*			Window	win = new ReportViewer(hm,"report/account/chartOfAccount.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);*/
			Window win = new ReportViewer(hm,"report/account/chartOfAccount.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

			
			win.setCaption("CHART OF ACCOUNT :: "+sessionBean.getCompany());
			this.getParent().getWindow().addWindow(win);
			//showWindow(win,selectedItem,"chartOfAcAcReportMod");
/*			HashMap hm = new HashMap();				
			hm.put("comName",sessionBean.getCompany());
			hm.put("address",sessionBean.getCompanyAddress());
			hm.put("companyId",sessionBean.getCompanyId());
			
			hm.put("logo", sessionBean.getCompanyLogo());
			
			query = "select * from vwChartOfAccount where companyId in ('"+sessionBean.getCompanyId()+"', '0') " +
					"and head_type like '"+ch+"' order by head_id,group_name,sub_group_name,cast(substring(Ledger_Id, 3, 50) as int)";
			System.out.println(query);
			hm.put("sql",query);	
			
			Window win = new ReportPdf(hm,"report/account/chartOfAccount.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			
			Window win = new ReportViewer(hm,"report/account/chartOfAccount.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			
			win.setCaption("CHART OF ACCOUNT :: "+sessionBean.getCompany());
			this.getParent().getWindow().addWindow(win);
			//showWindow(win,selectedItem,"chartOfAcAcReportMod");
*/		}
		catch(Exception exp)
		{
			this.getWindow().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
