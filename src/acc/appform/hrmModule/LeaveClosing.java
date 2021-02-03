package acc.appform.hrmModule;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeaveClosing extends Window
{
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private ProgressIndicator PI;
	private Worker1 worker1;

	private Label lblLeave;
	private NativeButton btnGenarate;
	private int start = 0;

	public LeaveClosing(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("LEAVE CLOSING :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		setEventAction();
	}

	private void setEventAction()
	{
		btnGenarate.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(start == 1)
				{
					proceedValidation();
				}
				else
				{
					showNotification("Warning!","Before procced closing leave balance please confirm again",Notification.TYPE_WARNING_MESSAGE);
				}
				start = 1;
			}
		});
	}

	private void proceedValidation()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String check = " select * from tbLeaveBalanceNew where YEAR(currentYear) = YEAR(CURRENT_TIMESTAMP) ";
			List <?> chkList = session.createSQLQuery(check).list();

			if(chkList.isEmpty())
			{
				proceedAction();
			}
			else
			{
				showNotification("Warning!","Leave closing already proccded",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp)
		{

		}
		finally{session.close();}
	}

	private void proceedAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to procced leave balance closing?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					worker1 = new Worker1();
					worker1.start();
					PI.setEnabled(true);
					PI.setValue(0f);
					btnGenarate.setEnabled(false);
				}
			}
		});
	}

	private void proccedClosing()
	{
		String insertQuery = " ";
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			insertQuery = " EXEC prcLeaveClosing '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"' ";
			session.createSQLQuery(insertQuery).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("350px");
		mainLayout.setHeight("170px");
		mainLayout.setMargin(false);

		// lblLeave
		lblLeave = new Label("<html><b>Procced Leave Closing</b></html>",Label.CONTENT_XHTML);
		lblLeave.setImmediate(false);
		lblLeave.setWidth("-1px");
		lblLeave.setHeight("-1px");
		lblLeave.setStyleName("lblColor");
		mainLayout.addComponent(lblLeave, "top:40.0px; left:85.0px;");

		// btnGenarate
		btnGenarate=new NativeButton("Proceed");
		btnGenarate.setImmediate(true);
		btnGenarate.setWidth("100px");
		btnGenarate.setHeight("32px");
		btnGenarate.setIcon(new ThemeResource("../icons/generate.png"));
		mainLayout.addComponent(btnGenarate,"top:70.0px; left:85.0px;");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"top:78.0px; left:200.0px;");

		return mainLayout;
	}

	public class Worker1 extends Thread 
	{
		int current = 1;
		public final static int MAX = 10;
		public void run() 
		{
			for (; current <= MAX; current++) 
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				synchronized (getApplication()) 
				{
					prosessed();
				}
			}
			proccedClosing();
			showNotification("Procced Successfully");
		}
		public int getCurrent() 
		{
			return current;
		}
	}
	public void prosessed() 
	{
		int i = worker1.getCurrent();
		if (i == Worker1.MAX)
		{
			PI.setEnabled(false);
			btnGenarate.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
}
