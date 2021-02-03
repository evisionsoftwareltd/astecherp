package acc.appform.accountsSetup;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TransferLedger extends Window
{
	private SessionBean sessionBean;

	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout leftVerLayout = new VerticalLayout();
	private VerticalLayout midVerLayout = new VerticalLayout();
	private VerticalLayout rightVerLayout = new VerticalLayout();
	private VerticalLayout space = new VerticalLayout();
	private GridLayout grid = new GridLayout(1,1);

	private NativeSelect fromPrimaryCat = new NativeSelect("Primary Category:");
	private NativeSelect fromMainCat = new NativeSelect("Main Category:");
	private NativeSelect fromGroupList = new NativeSelect("Group List:");
	private NativeSelect fromSubGroupList = new NativeSelect("Sub-Group List:");
	private ListSelect fromLedgerList = new ListSelect("Ledger List:");	
	private TextField ledgerName = new TextField("Ledger Name:");
	private NativeButton transferBtn = new NativeButton("Transfer");
	private NativeButton cancelBtn = new NativeButton("Cancel");
	private NativeSelect toPrimaryCat = new NativeSelect("Primary Category:");
	private NativeSelect toMainCat = new NativeSelect("Main Category:");
	private NativeSelect toGroupList = new NativeSelect("Group List:");
	private NativeSelect toSubGroupList = new NativeSelect("Sub-Group List:");
	private ListSelect toLedgerList = new ListSelect("Ledger List:");

	private String cw = "200px";

	public TransferLedger(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEDGER TRANSFER INFO :: "+this.sessionBean.getCompany());
		this.setWidth("750px");
		this.setResizable(false);

		leftVerLayout.addComponent(new Label("<B>Transfer From:</B>",Label.CONTENT_XHTML));
		leftVerLayout.addComponent(fromPrimaryCat);
		leftVerLayout.addComponent(fromMainCat);
		leftVerLayout.addComponent(fromGroupList);
		leftVerLayout.addComponent(fromSubGroupList);
		leftVerLayout.addComponent(fromLedgerList);

		fromPrimaryCat.setWidth(cw);
		fromPrimaryCat.addItem("1");
		fromPrimaryCat.setItemCaption("1", "All");
		fromPrimaryCat.addItem("2");
		fromPrimaryCat.setItemCaption("2", "Assets");
		fromPrimaryCat.addItem("3");
		fromPrimaryCat.setItemCaption("3", "Liabilities");
		fromPrimaryCat.addItem("4");
		fromPrimaryCat.setItemCaption("4", "Income");
		fromPrimaryCat.addItem("5");
		fromPrimaryCat.setItemCaption("5", "Expenses");
		fromPrimaryCat.setImmediate(true);

		fromPrimaryCat.setNullSelectionAllowed(false);
		
		fromPrimaryCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				fromPrimaryCatSelect();
			}
		});
		
		fromMainCat.setWidth(cw);
		fromMainCat.setImmediate(true);
		
		fromMainCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				fromMainCatSelect();
			}
		});

		fromGroupList.setWidth(cw);
		fromGroupList.setImmediate(true);
		
		fromGroupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				fromGroupListSelect();
			}
		});

		fromSubGroupList.setWidth(cw);
		fromSubGroupList.setImmediate(true);
		
		fromSubGroupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				fromSubGroupListSelect();
			}
		});

		fromLedgerList.setWidth(cw);
		fromLedgerList.setImmediate(true);
		
		fromLedgerList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				fromLedgerListSelect();
			}
		});

		leftVerLayout.addComponent(fromLedgerList);
		leftVerLayout.setSpacing(true);
		leftVerLayout.setMargin(true);
		horLayout.addComponent(leftVerLayout);
		
		btnLayout.addComponent(transferBtn);
		btnLayout.addComponent(cancelBtn);
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		
		transferBtn.setWidth("80px");
		transferBtn.setHeight("28px");
		cancelBtn.setWidth("80px");
		cancelBtn.setHeight("28px");

		midVerLayout.setMargin(true);
		midVerLayout.addComponent(space);
		space.setHeight("120px");
		midVerLayout.addComponent(ledgerName);
		ledgerName.setWidth("200px");
		VerticalLayout space1 = new VerticalLayout();
		space1.setHeight("50px");
		midVerLayout.addComponent(space1);
		midVerLayout.addComponent(btnLayout);
		horLayout.addComponent(midVerLayout);

		rightVerLayout.addComponent(new Label("<B>Transfer To:</B>",Label.CONTENT_XHTML));
		rightVerLayout.addComponent(toPrimaryCat);
		rightVerLayout.addComponent(toMainCat);
		rightVerLayout.addComponent(toGroupList);
		rightVerLayout.addComponent(toSubGroupList);
		rightVerLayout.addComponent(toLedgerList);

		rightVerLayout.setSpacing(true);
		rightVerLayout.setMargin(true);

		toPrimaryCat.setWidth(cw);
		toPrimaryCat.addItem("1");
		toPrimaryCat.setItemCaption("1", "All");
		toPrimaryCat.addItem("2");
		toPrimaryCat.setItemCaption("2", "Assets");
		toPrimaryCat.addItem("3");
		toPrimaryCat.setItemCaption("3", "Liabilities");
		toPrimaryCat.addItem("4");
		toPrimaryCat.setItemCaption("4", "Income");
		toPrimaryCat.addItem("5");
		toPrimaryCat.setItemCaption("5", "Expenses");
		toPrimaryCat.setImmediate(true);

		toPrimaryCat.setNullSelectionAllowed(false);
		
		toPrimaryCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				toPrimaryCatSelect();
			}
		});
		
		toMainCat.setWidth(cw);
		toMainCat.setImmediate(true);
		
		toMainCat.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				toMainCatSelect();
			}
		});
		
		toGroupList.setWidth(cw);
		toGroupList.setImmediate(true);
		
		toGroupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				toGroupListSelect();
			}
		});
		
		toSubGroupList.setWidth(cw);
		toSubGroupList.setImmediate(true);
		
		toSubGroupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				toSubGroupListSelect();
			}
		});
		
		toLedgerList.setWidth(cw);
		toLedgerList.setImmediate(true);
		
		toLedgerList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				toLedgerListSelect();
			}
		});
		
		horLayout.addComponent(rightVerLayout);

		grid.addComponent(horLayout,0,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		this.addComponent(mainLayout);
		setButtonAction();

		fromPrimaryCat.setValue("1");
		toPrimaryCat.setValue("1");
		ledgerName.setEnabled(false);
		toPrimaryCat.setEnabled(false);
	}
	
	private void fromPrimaryCatSelect()
	{		
		fromMainCat.removeAllItems();
		fromGroupList.removeAllItems();
		fromSubGroupList.removeAllItems();
		fromLedgerList.removeAllItems();
		fromMainCatInitialise();
	}
	
	private void toPrimaryCatSelect()
	{
		toMainCat.removeAllItems();
		toGroupList.removeAllItems();
		toSubGroupList.removeAllItems();
		toLedgerList.removeAllItems();
		toMainCatInitialise();
	}
	
	private void fromMainCatSelect()
	{
		if(fromMainCat.getValue()!=null)
		{
			fromGroupList.removeAllItems();
			fromSubGroupList.removeAllItems();
			fromLedgerList.removeAllItems();
			fromGrouListInitialise();
			fromLedgerInitialise();
		}
	}
	
	private void toMainCatSelect()
	{
		if(toMainCat.getValue()!=null)
		{
			toGroupList.removeAllItems();
			toSubGroupList.removeAllItems();
			toLedgerList.removeAllItems();
			toGrouListInitialise();
			toLedgerInitialise();
		}
	}
	
	private void fromGroupListSelect()
	{
		if(fromGroupList.getValue()!=null)
		{
			fromSubGroupList.removeAllItems();
			fromLedgerList.removeAllItems();
			fromSubGroupListInitialise();
			fromLedgerInitialise();
		}
	}
	
	private void toGroupListSelect()
	{
		if(toGroupList.getValue()!=null)
		{
			toSubGroupList.removeAllItems();
			toLedgerList.removeAllItems();
			toSubGroupListInitialise();
			toLedgerInitialise();
		}
	}
	
	private void fromSubGroupListSelect()
	{
		if(fromSubGroupList.getValue()!=null)
		{
			fromLedgerList.removeAllItems();
			fromLedgerInitialise();
		}
	}
	
	private void toSubGroupListSelect()
	{
		if(toSubGroupList.getValue()!=null)
		{
			toLedgerList.removeAllItems();
			toLedgerInitialise();
		}
	}
	
	private void fromLedgerListSelect()
	{
		if(fromLedgerList.getValue()!=null)
		{
			ledgerName.setValue(fromLedgerList.getItemCaption(fromLedgerList.getValue()));
		}
		else
		{
			ledgerName.setValue("");
		}
	}
	
	private void toLedgerListSelect()
	{

	}
	
	private void fromMainCatInitialise()
	{
		String sql = "";
		toPrimaryCat.setValue(fromPrimaryCat.getValue());
		if(fromPrimaryCat.getValue().toString().equalsIgnoreCase("1")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup order by substring(headId,1,1),slNo";
			fromPrimaryCat.setCaption("Primary Category:");
		}
		else if(fromPrimaryCat.getValue().toString().equalsIgnoreCase("2")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'A' order by slNo";
			fromPrimaryCat.setCaption("Asset Category:");
		}
		else if(fromPrimaryCat.getValue().toString().equalsIgnoreCase("3")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'L' order by slNo";
			fromPrimaryCat.setCaption("Liabilities Category:");
		}
		else if(fromPrimaryCat.getValue().toString().equalsIgnoreCase("4")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'I' order by slNo";
			fromPrimaryCat.setCaption("Income Category:");
		}
		else{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'E' order by slNo";
			fromPrimaryCat.setCaption("Expenses Category:");
		}
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			fromMainCat.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				fromMainCat.addItem(element[0].toString());
				fromMainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
			fromMainCat.setNullSelectionAllowed(false);
			fromMainCat.setValue("");
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error1",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void toMainCatInitialise()
	{
		String sql = "";
		if(toPrimaryCat.getValue().toString().equalsIgnoreCase("1")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup order by substring(headId,1,1),slNo";
			toPrimaryCat.setCaption("Primary Category:");
		}
		else if(toPrimaryCat.getValue().toString().equalsIgnoreCase("2")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'A' order by slNo";
			toPrimaryCat.setCaption("Asset Category:");
		}
		else if(toPrimaryCat.getValue().toString().equalsIgnoreCase("3")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'L' order by slNo";
			toPrimaryCat.setCaption("Liabilities Category:");
		}
		else if(toPrimaryCat.getValue().toString().equalsIgnoreCase("4")){
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'I' order by slNo";
			toPrimaryCat.setCaption("Income Category:");
		}
		else{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'E' order by slNo";
			toPrimaryCat.setCaption("Expenses Category:");
		}
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			toMainCat.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				toMainCat.addItem(element[0].toString());
				toMainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
			toMainCat.setNullSelectionAllowed(false);
			toMainCat.setValue("");
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error1",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void fromGrouListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT groupId,groupName FROM TbMainGroup WHERE headId = '"+fromMainCat.getValue()+"'")
			.list();
			fromGroupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				fromGroupList.addItem(element[0].toString());
				fromGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			fromGroupList.setNullSelectionAllowed(false);
			fromGroupList.setValue("");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void toGrouListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT groupId,groupName FROM TbMainGroup WHERE headId = '"+toMainCat.getValue()+"'")
			.list();
			toGroupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				toGroupList.addItem(element[0].toString());
				toGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			
			toGroupList.setNullSelectionAllowed(false);
			toGroupList.setValue("");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void fromSubGroupListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+fromGroupList.getValue()+"'")
			.list();
			fromSubGroupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				fromSubGroupList.addItem(element[0].toString());
				fromSubGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			fromSubGroupList.setNullSelectionAllowed(false);
			fromSubGroupList.setValue("");
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void toSubGroupListInitialise(){
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+toGroupList.getValue()+"'")
			.list();
			toSubGroupList.addItem("");
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				toSubGroupList.addItem(element[0].toString());
				toSubGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			toSubGroupList.setNullSelectionAllowed(false);
			toSubGroupList.setValue("");
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void fromLedgerInitialise(){
		String sql = "";
		if(!fromSubGroupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+fromSubGroupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		else if(!fromGroupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+fromGroupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		else
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+fromMainCat.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				fromLedgerList.addItem(element[0].toString());
				fromLedgerList.setItemCaption(element[0].toString(), element[1].toString());
			}
			fromLedgerList.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void toLedgerInitialise()
	{
		String sql = "";
		if(!toSubGroupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+toSubGroupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		else if(!toGroupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+toGroupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		else
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+toMainCat.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')";
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				toLedgerList.addItem(element[0].toString());
				toLedgerList.setItemCaption(element[0].toString(), element[1].toString());
			}
			toLedgerList.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void setButtonAction()
	{
		transferBtn.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				transferBtnAction(event);
			}
		});
		
		cancelBtn.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				ledgerName.setValue("");
			}
		});
	}
	
	private void transferBtnAction(ClickEvent e)
	{
		if(ledgerName.getValue().toString().trim().length()==0)
			this.getParent().showNotification("","Please select the ledger. You want to transfer.",Notification.TYPE_WARNING_MESSAGE);
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to transfer ledger?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{			
						ledgerTransfer();
					}
				}
			});
		}
	}
	private void ledgerTransfer()
	{
		if(sessionBean.isUpdateable())
		{
			if(toMainCat.getValue()!= null && !toMainCat.getValue().equals(""))
			{
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();

					String parentId = "";
					String createFrom = "";

					if(!toSubGroupList.getValue().toString().equals(""))
					{
						parentId = toSubGroupList.getValue().toString();
						createFrom = toMainCat.getValue()+"-"+toGroupList.getValue()+"-"+toSubGroupList.getValue();
					}
					else if(!toGroupList.getValue().toString().equals(""))
					{
						parentId = toGroupList.getValue().toString();
						createFrom = toMainCat.getValue()+"-"+toGroupList.getValue();
					}
					else
					{
						parentId = toMainCat.getValue().toString();
						createFrom = toMainCat.getValue().toString();
					}

					String sql = "UPDATE tbLedger SET Parent_Id = '"+parentId+"',Create_From = '"+createFrom+"', companyId = '"+ sessionBean.getCompanyId() +"' WHERE Ledger_Id = '"+fromLedgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
					session.createSQLQuery(sql).executeUpdate();
					tx.commit();

					toLedgerList.addItem(fromLedgerList.getValue().toString());
					toLedgerList.setItemCaption(fromLedgerList.getValue().toString(), fromLedgerList.getItemCaption(fromLedgerList.getValue()));
					fromLedgerList.removeItem(fromLedgerList.getValue());

					this.getParent().showNotification("Ledger transfer successfully.");

				}catch(Exception exp){
					this.getParent().showNotification(
							"Error",
							exp+"",
							Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}else{
				this.getParent().showNotification(
						"",
						"Please provide the Category Name under which you want to transfer.",
						Notification.TYPE_WARNING_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for ledger trnasfer.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
