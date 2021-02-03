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

@SuppressWarnings("serial")
public class GroupCreate extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "", "", "", "","");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout leftVerLayout = new VerticalLayout();
	private FormLayout leftFormLayout = new FormLayout();
	private FormLayout rigthFormLayout = new FormLayout();
	private VerticalLayout rigthVerLayout = new VerticalLayout();
	private VerticalLayout space = new VerticalLayout();
	private GridLayout grid = new GridLayout(1,1);
	private GridLayout gridT = new GridLayout(1,1);
	private Label selected = new Label("");

	private OptionGroup group = new OptionGroup();
	private ComboBox primaryCat = new ComboBox("Primary Category:");
	private ListSelect groupList = new ListSelect("Group List:");
	private TextField groupName = new TextField("Group Name:");

	private String slTitle = "";
	private boolean isUpdate = false;

	public GroupCreate(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("MAIN GROUP :: "+this.sessionBean.getCompany());
		this.setWidth("860px");
		this.setResizable(false);

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
		
		group.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				priCatInitialise();
				groupList.removeAllItems();
				selected.setValue("");
				initialise();
			}
		});

		leftVerLayout.addComponent(group);
		leftVerLayout.addComponent(new Label(""));

		leftVerLayout.addComponent(primaryCat);
		primaryCat.setImmediate(true);
		primaryCat.setWidth("300px");
		primaryCat.addListener(new ValueChangeListener()
		{
			@Override
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
		
		groupList.setImmediate(true);
		groupList.setWidth("300px");
		leftVerLayout.addComponent(groupList);
		groupList.addListener(new ValueChangeListener()
		{
			@Override
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
		
		leftVerLayout.setSpacing(true);
		leftVerLayout.setMargin(true);
		horLayout.addComponent(leftVerLayout);

		horLayout.addComponent(rigthVerLayout);
		rigthFormLayout.setWidth("300px");

		groupName.setWidth("320px");
		rigthFormLayout.addComponent(groupName);
		rigthFormLayout.setMargin(true);
		gridT.addComponent(selected);
		gridT.setMargin(true);
		rigthVerLayout.addComponent(gridT);
		rigthVerLayout.setComponentAlignment(gridT, Alignment.MIDDLE_CENTER);
		rigthVerLayout.addComponent(space);
		space.setHeight("80px");
		rigthVerLayout.addComponent(rigthFormLayout);

		VerticalLayout space1 = new VerticalLayout();
		space1.setHeight("75px");
		rigthVerLayout.addComponent(space1);
		rigthVerLayout.addComponent(btnLayout);
		btnLayout.addComponent(button);

		grid.addComponent(horLayout,0,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		this.addComponent(mainLayout);
		setButtonAction();
		priCatInitialise();
		initialise();
	}
	
	private void groupListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT groupId,groupName FROM TbMainGroup WHERE headId = '"+primaryCat.getValue()+"'").list();
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
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void priCatInitialise()
	{
		String sql = "";
		
		if(group.getValue().toString().equalsIgnoreCase("1"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup order by substring(headId,1,1),slNo";
			primaryCat.setCaption("Primary Category:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("2"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'A' order by slNo";
			primaryCat.setCaption("Asset Category:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("3"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'L' order by slNo";
			primaryCat.setCaption("Liabilities Category:");
		}
		else if(group.getValue().toString().equalsIgnoreCase("4"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'I' order by slNo";
			primaryCat.setCaption("Income Category:");
		}
		else
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'E' order by slNo";
			primaryCat.setCaption("Expenses Category:");
		}
		
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			try
			{
				primaryCat.removeAllItems();
			}catch(Exception e){}

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
			//System.out.println(sql);
			//System.out.println(exp);
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
						deleteData();
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
			Iterator iter = session.createSQLQuery("SELECT Group_Id FROM tbSub_Group WHERE Group_Id ='"+groupList.getValue()+"'").list().iterator();

			if(iter.hasNext())
			{
				this.getParent().showNotification(
						"",
						"Unable to delete this group because already sub group create for this group.",
						Notification.TYPE_ERROR_MESSAGE);
				return true;
			}
			else
			{
				iter = session.createSQLQuery(" SELECT * FROM tbLedger WHERE substring(Create_From,4,4) = '"+groupList.getValue()+"'").list().iterator();
				if(iter.hasNext())
				{
					this.getParent().showNotification(
							"",
							"Unable to delete this group because already ledger create for this group.",
							Notification.TYPE_ERROR_MESSAGE);
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return true;
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
				this.getParent().showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
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
	
	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String sql = "UPDATE tbMain_Group SET Group_Name = '"+groupName.getValue()+"' WHERE Group_Id = '"+groupList.getValue()+"'";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				this.getParent().showNotification("All Information update successfully.");
				groupList.setItemCaption(groupList.getValue().toString(), groupName.getValue().toString());
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void deleteData()
	{
		if(sessionBean.isDeleteable())
		{
			if(groupName.getValue().toString().trim().length()>0){
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();
					String sql = "Delete FROM tbMain_Group WHERE Group_Id = '"+groupList.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();
					tx.commit();
					this.getParent().showNotification("Desired Information delete successfully.");
					slInitialise(true);
					groupName.setValue("");
					groupList.removeItem(groupList.getValue());
					isUpdate = false;
				}catch(Exception exp){
					this.getParent().showNotification(
							"Error",
							exp+"",
							Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}else{
				this.getParent().showNotification(
						"Delete Failed",
						"There are no data for delete.",
						Notification.TYPE_WARNING_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for delete.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void insertData(){
		if(sessionBean.isSubmitable()){
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String sql = "INSERT INTO TbMain_Group(Group_Id,Group_Name,Head_Id,userId,userIp,entryTime) VALUES('G'+ISNULL((SELECT cast((max(substring(Group_Id,2,len(Group_Id)-1))+1) as VARCHAR) FROM TbMain_Group),101) ,'"+
				groupName.getValue()+"','"+primaryCat.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				this.getParent().showNotification("All Information save successfully.");
				groupListInitialise();
			}catch(Exception exp){

				this.getParent().showNotification(
						"Error",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for save.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}	
}
