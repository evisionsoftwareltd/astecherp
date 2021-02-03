package acc.appform.accountsSetup;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class fiscalYearClosing extends Window
{
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout btnLayoutFirst = new HorizontalLayout();
	private HorizontalLayout btnLayoutSecond = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();
	private SessionBean sessionBean;

	private Label lblCaption1 = new Label(
			
			"Here you see the current working fiscal year.");

	private Label lblCaption2 = new Label("In order to close current fiscal year and start new" 
			+ "\ntransactions for the next working fiscal year you"
			+ "\nshould execute following two steps.",Label.CONTENT_PREFORMATTED);

	private Label lblFirst = new Label("# 1st Step : ");
	private NativeButton btnFirstStep = new NativeButton("Create New Fiscal Year");
	private Label lblCaption3 = new Label("In 1st step new fiscal year be created and" 
			+ "\nyou can start data entry for new fiscal Year."
			+ "\nBut all ledger balance will remain zero.",Label.CONTENT_PREFORMATTED);

	private Label lblFiscalYear = new Label("");

	private Label lblSecond = new Label("# Final Step : ");
	private NativeButton btnFinalStep = new NativeButton("Final Balance Transfer");

	private Label lblCaption4 = new Label("In final step balance will be transfered from" 
			+ "\nprevious fiscal year to current fiscal year.",Label.CONTENT_PREFORMATTED);

	public fiscalYearClosing(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("Fiscal Year Closing :: "+sessionBean.getCompany());
		this.setWidth("500px");
		space.setWidth("35px");
		this.setResizable(false);
		lblCaption2.addStyleName("backred");

		formLayout.addComponent(lblCaption1);
		formLayout.addComponent(space);
		btnLayout.addComponent(lblFiscalYear);
		btnLayout.setWidth("500px");
		formLayout.addComponent(btnLayout);
		formLayout.addComponent(space);
		formLayout.addComponent(lblCaption2);

		btnLayoutFirst.addComponent(lblFirst);
		btnLayoutFirst.addComponent(btnFirstStep);

		btnLayoutFirst.setWidth("500px");
		btnLayoutFirst.setSpacing(true);
		btnLayoutFirst.setMargin(true);

		formLayout.addComponent(btnLayoutFirst);
		formLayout.addComponent(lblCaption3);

		btnLayoutSecond.addComponent(lblSecond);

		btnLayoutSecond.setWidth("500px");
		btnLayoutSecond.setSpacing(true);
		btnLayoutSecond.setMargin(true);
		btnLayoutSecond.addComponent(lblSecond);
		btnLayoutSecond.addComponent(btnFinalStep);
		formLayout.addComponent(btnLayoutSecond);
		formLayout.addComponent(lblCaption4);

		formLayout.setMargin(true);

		mainLayout.setMargin(true);
		mainLayout.addComponent(formLayout);
		addComponent(mainLayout);

		buttonAction();
		currentFiscalinitialize();
		previousFiscalinitialize();
	}

	private void buttonAction()
	{
		btnFirstStep.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				message(1);
			}
		});

		btnFinalStep.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				message(2);
			}
		});
	}

	private void currentFiscalinitialize()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select Op_date, Cl_Date from tbfiscal_year where running_flag = 1";
			List<?> group = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = group.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				sessionBean.setFiscalOpenDate(element[0]);
				sessionBean.setFiscalCloseDate(element[1]);
				Object strDate = new SimpleDateFormat("dd-MM-yyyy").format(element[0]);
				Object dateStr = new SimpleDateFormat("dd-MM-yyyy").format(element[1]);
				lblFiscalYear.setCaption("Current Fiscal Year : " + strDate +" to "+dateStr);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void previousFiscalinitialize()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sqlFirst = "select Running_Flag from tbFiscal_Year where CONVERT(date,CURRENT_TIMESTAMP) between" +
					" Op_Date and Cl_Date and Running_Flag = 1";
			Iterator<?> iterFirst = session.createSQLQuery(sqlFirst).list().iterator();
			if(iterFirst.hasNext())
			{
				btnFirstStep.setEnabled(false);
			}
			else
			{
				btnFirstStep.setEnabled(true);
			}

			String sqlFinal = "select isClosed from tbFiscal_Year where DATEADD(yyyy,-1,CONVERT(date,CURRENT_TIMESTAMP))" +
					" between Op_Date and Cl_Date and isClosed = 0 and Running_Flag = 0";
			Iterator<?> iterFinal = session.createSQLQuery(sqlFinal).list().iterator();
			if(iterFinal.hasNext())
			{
				btnFinalStep.setEnabled(true);
			}
			else
			{
				btnFinalStep.setEnabled(false);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void message(final int flag)
	{
		String msg = (flag == 1?"Do you want to create new fiscal year.":"Do you want to transfer balance of previous fiscal year.");
		MessageBox mb = new MessageBox(getParent(), "Are you sure?".toUpperCase(),
				MessageBox.Icon.QUESTION, msg,
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
				new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.setStyleName("cwindow");
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(flag == 1)
					{
						firstStepAction();
					}
					if(flag == 2)
					{
						finalStepAction();
					}
				}
			}
		});
	}

	private void firstStepAction()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			session.createSQLQuery("exec prcYearClosing1stStep '"+sessionBean.getCompanyId()+"'").executeUpdate();
			tx.commit();
			showNotification("New fiscal year create successfully.");
			previousFiscalinitialize();
			currentFiscalinitialize();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error to create table",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void finalStepAction()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			session.createSQLQuery("exec prcYearClosing2ndStep '"+sessionBean.getCompanyId()+"'").executeUpdate();
			tx.commit();
			showNotification("Balance transfer process completed successfully.");
			previousFiscalinitialize();
			currentFiscalinitialize();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error to balance transfer",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
}