package com.example.CostingTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ConvertionCostHeadInfo extends Window
{
	SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblHeadId, lblHeadName, lblIsActive;

	private TextRead txtHeadId;
	private TextField txtHeadName;

	private OptionGroup RadioBtnGroup;
	private static final List<String> type = Arrays.asList(new String[]{"Yes","No"});

	private boolean isUpdate = false;
	private boolean isFind = true;

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtCategoryID = new TextField();
	ArrayList<Component> allComp = new ArrayList<Component>();

	public ConvertionCostHeadInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("CONVERSION COST HEAD INFO :: "+this.sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);
		setEventAction();
		focusEnter();
		authenticationCheck();
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

	private void focusEnter()
	{
		allComp.add(txtHeadId);
		allComp.add(txtHeadName);
		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	private void HeadNameAlreadryExistCheck()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select headName from tbConversionCostHeadInfo where headName='"+txtHeadName.getValue().toString().trim()+"'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty() && !isFind)
			{
				txtHeadName.setValue("");
				showNotification("Warning!","Head name is already exists.", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("HeadNameAlreadryExistCheck", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	public void setEventAction()
	{
		txtHeadName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtHeadName.getValue().toString().isEmpty())
				{
					HeadNameAlreadryExistCheck();
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind = false;
				updateButtonEvent();
				txtHeadName.focus();
			}
		});

		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtHeadName.getValue().toString().isEmpty())
				{
					saveButtonEvent();
				}
				else
				{
					txtHeadName.focus();
					showNotification("Warning!","Please provide head name.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
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

	private void findButtonEvent() 
	{
		Window win = new ConvertionCostHeadFind(sessionBean, txtCategoryID);
		win.setStyleName("cwindow");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtCategoryID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtCategoryID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtCategoryId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		isFind = true;
		try 
		{
			String sql = "Select headId,headName,isActive from tbConversionCostHeadInfo Where headId = '"+txtCategoryId+"'";
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();
				txtHeadId.setValue(element[0]);
				txtHeadName.setValue(element[1]);
				RadioBtnGroup.setValue(element[2].toString().equalsIgnoreCase("1")?"Yes":"No");
			}
		}
		catch (Exception exp) 
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void refreshButtonEvent()
	{
		isFind = false;
		isUpdate = false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void saveButtonEvent() 
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(deleteData())
						{
							insertData();
							txtClear();
							componentIni(true);
							btnIni(true);
							button.btnNew.focus();
							isUpdate = false;
							showNotification("All Information update successfully.");
						}
					}
				}
			});		
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						btnIni(true);							
						componentIni(true);
						txtClear();
						button.btnNew.focus();
						showNotification("All Information Save successfully.");
					}
				}
			});		
		}
	}

	private boolean deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "delete from tbConversionCostHeadInfo where headId like '"+txtHeadId.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String insertQuery = "Insert Into tbConversionCostHeadInfo(headId,headName,isActive,userName,userIp,entryTime) values ("+
					" '"+headId()+"',"+
					" '"+txtHeadName.getValue()+"',"+
					" '"+(RadioBtnGroup.getValue().toString().equals("Yes")?"1":"0")+"',"+
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
			session.createSQLQuery(insertQuery).executeUpdate();
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void updateButtonEvent()
	{
		if(!txtHeadId.getValue().toString().isEmpty())
		{
			isUpdate = true;
			isFind = false;
			btnIni(false);
			componentIni(false);
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void newButtonEvent()
	{
		isFind = false;
		componentIni(false);
		btnIni(false);
		txtClear();
		txtHeadId.setValue(headId());
		txtHeadName.focus();
	}

	public String headId()
	{
		String ret = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "SELECT ISNULL((MAX(CAST(SUBSTRING(headId,3,50) AS INT))+1),1)  FROM tbConversionCostHeadInfo";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				ret = "H-"+iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	public void txtClear()
	{
		txtHeadName.setValue("");
		txtHeadId.setValue("");	
	}

	private void componentIni(boolean b) 
	{	
		txtHeadId.setEnabled(!b);
		txtHeadName.setEnabled(!b);
		RadioBtnGroup.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnFind.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("540px");
		setHeight("230px");

		lblHeadId = new Label("Head ID :");
		lblHeadId.setImmediate(false);
		lblHeadId.setWidth("-1px");
		lblHeadId.setHeight("-1px");

		txtHeadId = new TextRead();
		txtHeadId.setImmediate(false);
		txtHeadId.setWidth("100px");
		txtHeadId.setHeight("23px");		

		lblHeadName = new Label("Head Name :");
		lblHeadName.setImmediate(false);
		lblHeadName.setWidth("-1px");
		lblHeadName.setHeight("-1px");	

		txtHeadName = new TextField();
		txtHeadName.setImmediate(false);
		txtHeadName.setWidth("280px");
		txtHeadName.setHeight("-1px");

		lblIsActive = new Label("Active :");
		lblIsActive.setImmediate(false);
		lblIsActive.setWidth("-1px");
		lblIsActive.setHeight("-1px");	

		RadioBtnGroup = new OptionGroup("",type);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("Yes");

		mainLayout.addComponent(lblHeadId, "top:30.0px;left:45.0px;");
		mainLayout.addComponent(txtHeadId, "top:28.0px;left:160.0px;");

		mainLayout.addComponent(lblHeadName, "top:60.0px;left:45.0px;");
		mainLayout.addComponent(txtHeadName, "top:58.0px;left:160.0px;");

		mainLayout.addComponent(lblIsActive, "top:90.0px;left:45.0px;");
		mainLayout.addComponent(RadioBtnGroup, "top:90.0px;left:160.0px;");

		mainLayout.addComponent(button, "top:140.0px;left:12.0px;");

		return mainLayout;
	}
}