package acc.appform.hrmModule;

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
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DepartmentInformation extends Window 
{
	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtFindId = new TextField();
	boolean isUpdate = false;
	boolean isNew = false;
	private AbsoluteLayout mainLayout;
	private Label lblCommon;
	private TextRead txtDepartmentID;
	private TextField txtDepartmentName;
	private TextField txtPartyIdBack = new TextField();

	SessionBean sessionBean;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private boolean isFind = false;

	public DepartmentInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("DEPARTMENT INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		txtInit(true);
		btnIni(true);
		focusEnter();
		btnAction();
		authenticationCheck();
		setButtonShortCut();
		cButton.btnNew.focus();
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(cButton.btnSave, KeyCode.S, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnNew, KeyCode.N, ModifierKey.ALT));
		this.addAction(new ClickShortcut(cButton.btnRefresh, KeyCode.R, ModifierKey.ALT));
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{cButton.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{cButton.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{cButton.btnDelete.setVisible(false);}
	}

	public void btnAction()
	{
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				txtInit(false);
				btnIni(false);
				txtClear();
				txtDepartmentID.setValue(selectHeadId());
				txtDepartmentName.focus();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateButtonEvent();
			}
		});

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				refreshButtonEvent();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				txtFindId.setValue("");
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		txtDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtDepartmentName.getValue().toString().trim().isEmpty())
				{
					if(duplicateDepartmentName())
					{
						showNotification("Warning!","Department already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtDepartmentName.setValue("");
						txtDepartmentName.focus();
					}
				}
			}
		});
	}

	private void formValidation()
	{
		if(!txtDepartmentName.getValue().toString().isEmpty())
		{
			saveButtonEvent();
		}
		else
		{
			showNotification("Warning!","Provide Department Name.", Notification.TYPE_WARNING_MESSAGE);
			txtDepartmentName.focus();
		}
	}

	private boolean duplicateDepartmentName()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select vDepartmentName from tbDepartmentInfo where vDepartmentName" +
					" like '"+txtDepartmentName.getValue().toString().trim()+"'";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext() && !isFind) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}
		return false;
	}

	private void findButtonEvent() 
	{
		Window win = new DepartmentFindWindow(sessionBean, txtFindId, "DEPARTMENT");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtFindId.getValue().toString().length() > 0)
				{
					txtClear();
					txtPartyIdBack.setValue(txtFindId.getValue().toString());
					findInitialise(txtFindId.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);	
	}

	private String selectHeadId()
	{
		String DepartmentId = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select isnull(max(cast(SUBSTRING(vDepartmentId,5,LEN(vDepartmentId)) as int)),0)+1 from tbDepartmentInfo ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				DepartmentId = "DEPT"+iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}

		return DepartmentId;
	}

	private void findInitialise(String DepartmentId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vDepartmentId,vDepartmentName from tbDepartmentInfo Where" +
					" vDepartmentId = '"+DepartmentId+"'";
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext())
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDepartmentID.setValue(element[0].toString());
				txtDepartmentName.setValue(element[1].toString());
			}
			isFind = false;
		}
		catch (Exception exp) 
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
		txtClear();
		isNew=false;
	}

	private void updateButtonEvent()
	{
		if(!txtDepartmentID.getValue().toString().trim().isEmpty())
		{
			btnIni(false);
			txtInit(false);
			isUpdate = true;
			isFind = false;
		}
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(isUpdate)
						{
							updateData();

							btnIni(true);
							txtInit(true);
							txtClear();
							cButton.btnNew.focus();
						}
						isUpdate=false;
						isFind = false;
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();		
						btnIni(true);
						txtInit(true);
						txtClear();

						cButton.btnNew.focus();
					}
				}
			});
		}
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String Insert = " INSERT into tbDepartmentInfo (vDepartmentId,vDepartmentName, " +
					" vUserName,vUserIp,dEntryTime) values(" +
					" '"+selectHeadId()+"'," +
					" '"+txtDepartmentName.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
			session.createSQLQuery(Insert).executeUpdate();

			tx.commit();
			showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			showNotification("insertData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	public boolean updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{

			String udInsertQuery = "insert into tbUDDepartmentInfo (vDepartmentId,vDepartmentName," +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vDepartmentId,vDepartmentName," +
					"'OLD',vUserName,vUserIp,dEntryTime from tbDepartmentInfo where " +
					"vDepartmentId = '"+txtDepartmentID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();

			// Main table update
			String updateUnit = "UPDATE tbDepartmentInfo set" +
					" vDepartmentName = '"+txtDepartmentName.getValue().toString().trim()+"'," +
					" vUserName = '"+sessionBean.getUserName()+"'," +
					" vUserIp = '"+sessionBean.getUserIp()+"'," +
					" dEntryTime = GETDATE()" +
					" where vDepartmentId = '"+txtFindId.getValue().toString()+"' ";

			session.createSQLQuery(updateUnit).executeUpdate();

			udInsertQuery = "insert into tbUDDepartmentInfo (vDepartmentId,vDepartmentName," +
					"vUDFlag,vUserName,vUserIp,dEntryTime) select vDepartmentId,vDepartmentName," +
					"'UPDATE',vUserName,vUserIp,dEntryTime from tbDepartmentInfo where " +
					"vDepartmentId = '"+txtDepartmentID.getValue().toString().trim()+"'";
			session.createSQLQuery(udInsertQuery).executeUpdate();

			txtFindId.setValue("");

			showNotification("All information update successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("updateData",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally
		{
			session.close();
		}
		return true;
	}

	private void focusEnter()
	{
		allComp.add(txtDepartmentName);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		txtDepartmentID.setValue("");
		txtDepartmentName.setValue("");
	}

	public void txtInit(boolean t)
	{
		txtDepartmentID.setEnabled(!t);
		txtDepartmentName.setEnabled(!t);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("550px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		lblCommon = new Label("Department ID:");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:40.0px; left:50.0px;");

		txtDepartmentID = new TextRead();
		txtDepartmentID.setImmediate(true);
		txtDepartmentID.setWidth("60px");
		txtDepartmentID.setHeight("24px");
		mainLayout.addComponent(txtDepartmentID, "top:38.0px;left:180.5px;");

		lblCommon = new Label("Department Name :");
		mainLayout.addComponent(lblCommon, "top:80.0px; left:50.0px;");

		txtDepartmentName = new TextField();
		txtDepartmentName.setImmediate(true);
		txtDepartmentName.setWidth("320px");
		txtDepartmentName.setHeight("-1px");
		mainLayout.addComponent(txtDepartmentName, "top:78.0px; left:180.0px;");

		mainLayout.addComponent(cButton,"top:150px; left:18px;");
		return mainLayout;
	}
}
