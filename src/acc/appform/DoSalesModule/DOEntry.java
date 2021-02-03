package acc.appform.DoSalesModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DOEntry extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "", "","","","","","","Exit");
	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblReceiptType;
	private ComboBox cmbReceiptType;

	private Label lblReceiptPrefix;
	private TextField txtReceiptPrefix;

	private SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();	

	public DOEntry(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("PREFIX FORM  :: "+sessionBean.getCompany());
		this.setWidth("580px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		cmbAddData();
		txtInit(true);
		btnInit(true);
		btnAction();
		focusEnter();

		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}

	public void btnAction()
	{
		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		cmbReceiptType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)		
			{
				if(cmbReceiptType.getValue()!=null)
				{
				   txtReceiptPrefix.setValue(SetReceiptPrefix());
				}
			}
		});


		button.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbReceiptType.getValue()!=null)
				{
					if(!txtReceiptPrefix.getValue().toString().isEmpty())
					{
						if((txtReceiptPrefix.getValue().toString().length())<=5)
						{
							saveButtonEvent();
						}
						else
						{
							showNotification("Warning!","Prefix should not greater than 5 character",Notification.TYPE_WARNING_MESSAGE);
							txtReceiptPrefix.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide Receipt Prefix  ",Notification.TYPE_WARNING_MESSAGE);
						txtReceiptPrefix.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Receipt Type",Notification.TYPE_WARNING_MESSAGE);
					cmbReceiptType.focus();
				}
			}
		});
	}

	private void newButtonEvent() 
	{
		txtInit(false);
		btnInit(false);
		txtClear();
	}

	private void saveButtonEvent()
	{

		MessageBox mb = new MessageBox(getParent(), "", 
				MessageBox.Icon.QUESTION, "Do you want to Update ?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
				new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new MessageBox.EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					System.out.print("OK");
					updateData();
					btnInit(true);
					txtInit(true);
					txtClear();
				}
			}
		});
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String updateData = "UPDATE tbReceiptPrefixInfo set" +
					" vReceiptPrefix  ='"+txtReceiptPrefix.getValue().toString().trim()+"' " +
					" where iAutoId='"+cmbReceiptType.getValue()+"' ";
			session.createSQLQuery(updateData).executeUpdate();

			showNotification("All information update successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void cmbAddData()
	{
		cmbReceiptType.removeAllItems();
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery("select iAutoID,vReceiptName from tbReceiptPrefixInfo order by iAutoId").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbReceiptType.addItem(element[0]);
				cmbReceiptType.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	

	public String SetReceiptPrefix()
	{
		String ReceiptPrefix="";
		txtReceiptPrefix.setValue("");
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query = "select vReceiptPrefix from tbReceiptPrefixInfo where" +
					" iAutoID='"+cmbReceiptType.getValue()+"'";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				ReceiptPrefix = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return ReceiptPrefix;
	}

	private void txtClear() 
	{
		button.btnNew.focus();
		txtReceiptPrefix.setValue("");
		cmbReceiptType.setValue(null);
	}

	private void focusEnter()
	{
		allComp.add(cmbReceiptType);
		allComp.add(txtReceiptPrefix);
		allComp.add(button.btnSave);
		allComp.add(button.btnNew);
		new FocusMoveByEnter(this,allComp);
	}

	private void btnInit(boolean t) 
	{
		button.btnNew.setEnabled(t);
		button.btnSave.setEnabled(!t);
	}

	private void txtInit(boolean t)
	{
		txtReceiptPrefix.setEnabled(!t);
		cmbReceiptType.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("430px");
		setHeight("200px");

		lblReceiptType= new Label("Receipt Type :");
		lblReceiptType.setImmediate(false);
		lblReceiptType.setWidth("-1px");
		lblReceiptType.setHeight("-1px");
		mainLayout.addComponent(lblReceiptType, "top:20.0px;left:50.0px;");

		cmbReceiptType = new ComboBox();
		cmbReceiptType.setImmediate(true);
		cmbReceiptType.setWidth("200px");
		cmbReceiptType.setHeight("-1px");
		mainLayout.addComponent(cmbReceiptType, "top:18.0px;left:170.0px;");

		lblReceiptPrefix= new Label("Receipt Prefix :");
		lblReceiptPrefix.setImmediate(false);
		lblReceiptPrefix.setWidth("-1px");
		lblReceiptPrefix.setHeight("-1px");
		mainLayout.addComponent(lblReceiptPrefix,"top:50.0px;left:50.0px;");

		txtReceiptPrefix= new TextField();
		txtReceiptPrefix.setImmediate(false);
		txtReceiptPrefix.setWidth("100px");
		txtReceiptPrefix.setHeight("-1px");
		mainLayout.addComponent(txtReceiptPrefix, "top:48.0px;left:170.0px;");

		mainLayout.addComponent(button, "top:100.0px; left:80.0px ");		
		return mainLayout;
	}
}