package acc.appform.accountsSetup;

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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class SubGroupCreate extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "", "", "", "","");	

	private String slTitle = "";
	private boolean isUpdate = false;

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private OptionGroup group;

	private NativeButton btnGroup; 

	private Label lblPrimaryCat;
	private Label lblGroupList;
	private Label lblGroupName;
	private Label selected = new Label("");

	private ComboBox primaryCat;

	private ListSelect groupList;

	private TextField groupName;

	public SubGroupCreate(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SUB-GROUP :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		setButtonAction();
		priCatInitialise();
		initialise();
	}

	private void setButtonAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				groupName.setValue("");
				slInitialise(false);
				groupName.setEnabled(true);
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction(event);
			}
		});
		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction(event);
				slInitialise(true);
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				groupName.setValue("");
				slInitialise(true);
				groupName.setEnabled(false);
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteBtnAction(event);
			}
		});

		group.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				priCatInitialise();
				groupList.removeAllItems();
				selected.setValue("");
				initialise();
			}
		});

		primaryCat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				String t = "";
				if(primaryCat.getValue().toString().startsWith("A"))
					t = "Assests";
				else if(primaryCat.getValue().toString().startsWith("L"))
					t = "Liabilities";
				else if(primaryCat.getValue().toString().startsWith("I"))
					t = "Income";
				else if(primaryCat.getValue().toString().startsWith("E"))
					t = "Expenses";
				selected.setValue("Selected:"+t+" \\ "+primaryCat.getItemCaption(primaryCat.getValue()));
				
				groupListInitialise();
				slInitialise(true);
				
				slTitle = selected.getValue().toString();
			}
		});

		groupList.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(groupList.getValue()!=null)
				{
					selected.setValue(slTitle+" \\ "+groupList.getItemCaption(groupList.getValue()));
					groupName.setValue(groupList.getItemCaption(groupList.getValue()));
				}
				else
				{
					groupName.setValue("");
				}
			}
		});
		
		btnGroup.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				groupLink();
			}
		});
	}
	
	public void groupLink()
	{
		Window win = new GroupCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				priCatInitialise();
			}
		});
		this.getParent().addWindow(win);
	}

	private void priCatInitialise()
	{
		String sql = "";

		if(group.getValue().toString().equalsIgnoreCase("1"))
		{
			sql = "SELECT groupId,groupName FROM TbMainGroup ORDER BY headId,groupId";
			lblPrimaryCat.setValue("Group:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("2"))
		{
			sql = "SELECT groupId,groupName FROM TbMainGroup WHERE substring(headId,1,1) = 'A' order by groupId";
			lblPrimaryCat.setValue("Asset Group:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("3"))
		{
			sql = "SELECT groupId,groupName FROM TbMainGroup WHERE substring(headId,1,1) = 'L' order by groupId";
			lblPrimaryCat.setValue("Liabilities Group:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("4"))
		{
			sql = "SELECT groupId,groupName FROM TbMainGroup WHERE substring(headId,1,1) = 'I' order by groupId";
			lblPrimaryCat.setValue("Income Group:");
		}
		else
		{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			sql = "SELECT groupId,groupName FROM TbMainGroup WHERE substring(headId,1,1) = 'E' order by groupId";
			lblPrimaryCat.setValue("Expenses Group:");
		}
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			List group = session.createQuery(sql).list();
			
			try
			{
				primaryCat.removeAllItems();}catch(Exception e){}

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				
				primaryCat.addItem(element[0].toString());
				primaryCat.setItemCaption(element[0].toString(), element[1].toString());
			}
			primaryCat.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			System.out.println(sql);
			System.out.println(exp);
			this.getParent().showNotification(
					"Error1",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveBtnAction(ClickEvent e)
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
						button.btnSave.setEnabled(false);
						updateData();
						button.btnNew.focus();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						button.btnSave.setEnabled(false);
						insertData();
						button.btnNew.focus();
					}
				}
			});
		}
	}

	private void updateBtnAction(ClickEvent e)
	{
		if(sessionBean.isUpdateable())
		{
			if(groupName.getValue().toString().trim().length()>0)
			{
				slInitialise(false);
				groupName.setEnabled(true);
				isUpdate = true;
			}
			else
			{
				this.getParent().showNotification(
						"Edit Failed",
						"There are no data for Edit.",
						Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification
			(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String sql = "UPDATE tbSub_Group SET Sub_Group_Name = '"+groupName.getValue()+"' WHERE Sub_Group_Id = '"+groupList.getValue()+"'";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				this.getParent().showNotification("All Information update successfully.");
				groupList.setItemCaption(groupList.getValue().toString(), groupName.getValue().toString());
			}
			catch(Exception exp)
			{
				this.getParent().showNotification(
						"Error",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void initialise()
	{
		isUpdate = false;
		groupName.setValue("");
		groupName.setEnabled(false);
		button.btnNew.setEnabled(false);
		button.btnEdit.setEnabled(false);
		button.btnSave.setEnabled(false);
		button.btnRefresh.setEnabled(false);
		button.btnDelete.setEnabled(false);
	}

	private void slInitialise(boolean t)
	{
		isUpdate = false;;
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		group.setEnabled(t);
		primaryCat.setEnabled(t);
		groupList.setEnabled(t);
	}

	private void deleteBtnAction(ClickEvent e)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(!isthereData())
					{
						deleteData();
					}
				}
			}
		});
	}

	private boolean isthereData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			
			Transaction tx = session.beginTransaction();
			Iterator iter = session.createSQLQuery("SELECT Parent_Id FROM tbLedger WHERE Parent_Id = '"+groupList.getValue()+"'").list().iterator();

			if(iter.hasNext())
			{
				this.getParent().showNotification(
						"",
						"Unable to delete this group because already ledger create for this sub-group.",
						Notification.TYPE_ERROR_MESSAGE);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			return true;
		}
	}

	private void deleteData()
	{
		if(sessionBean.isDeleteable())
		{
			if(groupName.getValue().toString().trim().length()>0)
			{
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					
					tx = session.beginTransaction();
					String sql = "Delete FROM tbSub_Group WHERE Sub_Group_Id = '"+groupList.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();
					tx.commit();
					
					this.getParent().showNotification("Desired Information delete successfully.");
					
					slInitialise(true);
					groupName.setValue("");
					groupList.removeItem(groupList.getValue());
					isUpdate = false;
				}
				catch(Exception exp)
				{
					this.getParent().showNotification(
							"Error",
							exp+"",
							Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}
			else
			{
				this.getParent().showNotification(
						"Delete Failed",
						"There are no data for delete.",
						Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for delete.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void groupListInitialise()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+primaryCat.getValue()+"'").list();
			groupList.removeAllItems();
			
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				
				groupList.addItem(element[0].toString());
				groupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			groupList.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				
				String sql = "INSERT INTO TbSub_Group(Sub_Group_Id,Sub_Group_Name,Group_Id,userId,userIp,entryTime) VALUES('S'+ISNULL((SELECT cast((max(substring(Sub_Group_Id,2,len(Sub_Group_Id)-1))+1) as VARCHAR) FROM TbSub_Group),101) ,'"+
						groupName.getValue()+"','"+primaryCat.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
				
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				
				this.getParent().showNotification("All Information save successfully.");
				groupListInitialise();
			}
			catch(Exception exp)
			{
				this.getParent().showNotification(
						"Error",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for save.",
					Notification.TYPE_ERROR_MESSAGE);
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
		setWidth("860px");
		setHeight("500px");

		// group
		group  = new OptionGroup();
		group.setImmediate(true);
		group.addItem("1");
		group.setItemCaption("1", "All");
		group.addItem("2");
		group.setItemCaption("2", "Assets");
		group.addItem("3");
		group.setItemCaption("3", "Liabilities");
		group.addItem("4");
		group.setItemCaption("4", "Income");
		group.addItem("5");
		group.setItemCaption("5", "Expenses");
		group.setImmediate(true);
		group.setValue("1");
		mainLayout.addComponent(group, "top:22.0px;left:20.0px;");

		// lblPrimaryCat
		lblPrimaryCat = new Label();
		lblPrimaryCat.setImmediate(true);
		lblPrimaryCat.setWidth("-1px");
		lblPrimaryCat.setHeight("-1px");
		lblPrimaryCat.setValue("Group :");
		mainLayout.addComponent(lblPrimaryCat, "top:130.0px;left:20.0px;");

		// primaryCat
		primaryCat = new ComboBox();
		primaryCat.setImmediate(true);
		primaryCat.setHeight("-1px");
		primaryCat.setWidth("300px");
		primaryCat.setNullSelectionAllowed(false);
		mainLayout.addComponent(primaryCat, "top:150.0px;left:20.0px;");

		// btnGroup
		btnGroup = new NativeButton();
		btnGroup.setCaption("");
		btnGroup.setImmediate(true);
		btnGroup.setWidth("28px");
		btnGroup.setHeight("24px");
		btnGroup.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnGroup,"top:150.0px;left:325.0px;");

		// lblGroupList
		lblGroupList = new Label();
		lblGroupList.setImmediate(true);
		lblGroupList.setWidth("-1px");
		lblGroupList.setHeight("-1px");
		lblGroupList.setValue("Sub-Group List:");
		mainLayout.addComponent(lblGroupList, "top:180.0px;left:20.0px;");

		// groupList
		groupList = new ListSelect();
		groupList.setImmediate(true);
		groupList.setHeight("-1px");
		groupList.setWidth("300px");
		groupList.setNullSelectionAllowed(false);
		mainLayout.addComponent(groupList, "top:200.0px;left:20.0px;");

		// lblGroupName
		lblGroupName = new Label();
		lblGroupName.setImmediate(true);
		lblGroupName.setWidth("-1px");
		lblGroupName.setHeight("-1px");
		lblGroupName.setValue("Sub-Group Name:");
		mainLayout.addComponent(lblGroupName, "top:180.0px;left:360.0px;");

		// groupName
		groupName  = new TextField();
		groupName.setImmediate(true);
		groupName.setHeight("-1px");
		groupName.setWidth("300px");
		mainLayout.addComponent(groupName, "top:180.0px;left:470.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:270.0px;left:350.0px;");

		return mainLayout;
	}
}
