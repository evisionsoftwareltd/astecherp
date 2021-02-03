package acc.appform.accountsSetup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class Narration  extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "", "", "", "","");
	private FormLayout formLayout = new FormLayout();
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();

	private NativeSelect narrationId = new NativeSelect("Narration ID:");
	private NativeSelect voucherType = new NativeSelect("Voucher Type:");
	private TextField narration = new TextField("Narration:");

	private String cw = "230px";

	private boolean isUpdate = false;
	private SessionBean sessionBean;
	//private HashMap vt = new HashMap();
	public Narration(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("NARRATION :: "+sessionBean.getCompany());
		this.setWidth("500px");
		this.setResizable(false);

		formLayout.addComponent(narrationId);
		narrationId.setWidth(cw);
		narrationId.setImmediate(true);

		formLayout.addComponent(voucherType);		
		voucherType.setWidth(cw);
		voucherType.setImmediate(true);
		voucherType.addItem("");
		voucherType.addItem("CP");
		voucherType.setItemCaption("CP", "Cash Payment Voucher");
		voucherType.addItem("CR");
		voucherType.setItemCaption("CR", "Cash Receipt Voucher");

		voucherType.addItem("BP");
		voucherType.setItemCaption("BP", "Bank Payment Voucher");
		voucherType.addItem("BR");
		voucherType.setItemCaption("BR", "Bank Receipt Voucher");

		voucherType.addItem("JV");
		voucherType.setItemCaption("JV", "Journal Voucher");
		voucherType.addItem("CE");
		voucherType.setItemCaption("CE", "Contra Entry");
		voucherType.setNullSelectionAllowed(false);
		voucherType.setValue("");

		formLayout.addComponent(narration);
		narration.setWidth(cw);
		narration.setRows(2);

		space.setWidth("35px");
		//btnLayout.addComponent(space);
		btnLayout.addComponent(button);
		btnLayout.setWidth("500px");
		btnLayout.setSpacing(true);
		formLayout.setMargin(true);
		//btnLayout.setMargin(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(formLayout);
		mainLayout.addComponent(btnLayout);
		addComponent(mainLayout);
		buttonAction();
		initialise(true);
		button.btnNew.focus();
		narrationIdIni();

	}

	private void buttonAction()
	{
		narrationId.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				narrationIdSelect();
			}
		});

		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction(event);
			}
		});


		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction(event);
			}
		});


		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction(event);
			}
		});


		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteBtnAction(event);
			}
		});


		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				clearBtnAction(event);
			}
		});
	}

	private void narrationIdSelect(){
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			voucherType.setValue("");
			narration.setValue("");
			List group = session.createSQLQuery("SELECT voucherType,narration FROM TbNarrationList WHERE narrationId = '"+narrationId.getValue()+"'").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();

				voucherType.setValue(element[0]);
				narration.setValue(element[1]);
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void newBtnAction(ClickEvent e)
	{
		initialise(false);
		isUpdate = false;
		narrationId.setValue(null);
		narration.setValue("");		
		voucherType.setValue("");
	}
	
	private void saveBtnAction(ClickEvent e)
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update address/Office"+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{

						//saveBtn.setEnabled(false);
						updateData();
					}
				}
			});
		}
		else
		{
			if(voucherType.getValue() != null && narration.getValue() != "")
			{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						//saveBtn.setEnabled(false);
						insertData();
					}
				}
			});
			}
			else
				this.getParent().showNotification("Warning :","Please Select/Enter Voucher Type and Narration.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void updateData(){
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			//System.out.println("UPDATE tbNarrationList SET Narration = '"+narration.getValue()+"' WHERE NarrationId = '"+narrationId.getValue()+"'");
			session.createSQLQuery("UPDATE tbNarrationList SET Narration = '"+narration.getValue()+"' WHERE NarrationId = '"+narrationId.getValue()+"'").executeUpdate();
			tx.commit();
			this.getParent().showNotification("Desired Information update successfully.");
			initialise(true);
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void insertData(){
		if(sessionBean.isSubmitable()){
			if(!voucherType.getValue().toString().equals("")){
				if(narration.getValue().toString().trim().length()>0){
					Transaction tx = null;
					try
					{
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						tx = session.beginTransaction();
						String sql = "INSERT tbNarrationList(NarrationId,Narration,VoucherType) VALUES('"+voucherType.getValue()+"-'+"+
						"CAST((SELECT ISNULL(MAX(CAST(SUBSTRING(NarrationId,4,50) AS int)+1),1) FROM tbNarrationList WHERE VoucherType = '"+voucherType.getValue()+"') AS VARCHAR),'"+narration.getValue()+
						"','"+voucherType.getValue()+"')"; 
						session.createSQLQuery(sql).executeUpdate();
						tx.commit();
						this.getParent().showNotification("All Information save successfully.");
						initialise(true);
						narrationIdIni();
					}
					catch(Exception exp)
					{
						showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
					}
				}
				else
				{
					this.getParent().showNotification("","Please provide the narration.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				this.getParent().showNotification("","Please provide the voucher type.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateBtnAction(ClickEvent e)
	{
		if(sessionBean.isAdmin())
		{
			System.out.println(narrationId.getValue());
			if(narrationId.getValue() != null)
			{
				isUpdate = true;
				initialise(false);
				voucherType.setEnabled(false);
			}
			else
				this.getParent().showNotification("Warning :","Please Select Narration ID.",Notification.TYPE_WARNING_MESSAGE);
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteBtnAction(ClickEvent e)
	{
		if(narrationId.getValue() != null)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						deleteData();
					}
				}
			});
		}
		else
			this.getParent().showNotification("Warning :","Please Select Narration ID.",Notification.TYPE_WARNING_MESSAGE);
	}

	private void deleteData()
	{
		if(sessionBean.isAdmin())
		{
			try
			{
				Transaction tx = null;
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				session.createSQLQuery("DELETE FROM tbNarrationList WHERE NarrationId = '"+narrationId.getValue()+"'").executeUpdate();
				tx.commit();
				this.getParent().showNotification("Desired Information deleted successfully.");
				narrationIdIni();
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void clearBtnAction(ClickEvent e)
	{
		initialise(true);
		isUpdate = false;
		narrationId.setValue(null);
		narration.setValue("");
		voucherType.setValue(null);
	}

	private void initialise(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnDelete.setEnabled(t);

		narrationId.setEnabled(t);
		voucherType.setEnabled(!t);
		narration.setEnabled(!t);
	}

	private void narrationIdIni()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT narrationId FROM TbNarrationList ORDER BY slNo").list();

			narrationId.removeAllItems();			
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object element = iter.next();
				narrationId.addItem(element);
			}
			//narrationId.setNullSelectionAllowed(false);
			narrationId.setValue("");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}