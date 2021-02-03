package acc.reportmodule.mis;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class SalesPositionRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout verLayout = new VerticalLayout();
	private GridLayout grid = new GridLayout(2,1);
	private InlineDateField Month = new InlineDateField("Date :");
	private VerticalLayout space = new VerticalLayout();
	private Label lbl = new Label();
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtf = new SimpleDateFormat("MMMM, yyyy");

	public SalesPositionRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SALES POSITIONS :: "+ this.sessionBean.getCompany());
		this.setWidth("400px");
		this.setHeight("220px");
		this.setResizable(false);
		formLayout.addComponent(Month);
		lbl.setHeight("30px");
		formLayout.addComponent(lbl);
		formLayout.addComponent(button);
		formLayout.setSpacing(true);

		Month.setValue(new java.util.Date());
		Month.setDateFormat("dd-MM-yy");
		Month.setResolution(InlineDateField.RESOLUTION_MONTH);
		Month.setImmediate(true);

		verLayout.addComponent(space);
		verLayout.setSpacing(true);
		space.setHeight("42px");
		space.setSpacing(true);

		grid.addComponent(formLayout,0,0);
		grid.addComponent(verLayout,1,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		this.addComponent(mainLayout);		
		setButtonAction();		
	}

	private void setButtonAction()
	{
		button.btnPreview.addListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				showReport();
			}
		});

		button.btnExit.addListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});			
	}
	private void showReport() 
	{
		try
		{
			HashMap hm = new HashMap();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String sql ="";
			String path = "";
			
			System.out.println(dateformat.format(Month.getValue()));
			sql = "select * from dbo.funSalesPosition('"+ dateformat.format(Month.getValue()) +"', '"+ sessionBean.getCompanyId() +"')  order by consignee";
			//System.out.print(sql);
			
			hm.put("sql",sql);
			hm.put("date","For the month of : "+ dtf.format(Month.getValue()).toString());
			hm.put("companyname",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());

			
					Window win = new ReportPdf(hm,"report/account/rptSalesStatement.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			
			win.setCaption("Comparative Sales Statement :: "+ sessionBean.getCompany());
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
