package acc.appform.accountsSetup;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class CostInformation extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "","");
	private SessionBean sessionBean;
	private GridLayout titleGrid = new GridLayout(1,1);
	private VerticalLayout mainLayout = new VerticalLayout();

	private HorizontalLayout btnLayout = new HorizontalLayout();
	private FormLayout formLayout = new FormLayout();

	private ComboBox findCostCentre = new ComboBox("Find Cost Centre:");
	private TextField costCentreName = new TextField("Cost Centre Name:");
	private AmountField ttlManpower = new AmountField("Total Man Power:");
	private TextField incharge = new TextField("Centre Incharge:");
	private TextField location = new TextField("Location:");

	private boolean isUpdate = false;
	private String cw = "250px";

	public CostInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("COST INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("570px");
		this.setResizable(false);

		titleGrid.addComponent(new Label("<h3><u>Cost Information</u></h3>",Label.CONTENT_XHTML));
		mainLayout.addComponent(titleGrid);
		mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);

		formLayout.addComponent(findCostCentre); 
		findCostCentre.setImmediate(true);

		findCostCentre.setWidth(cw);
		formLayout.addComponent(costCentreName);
		costCentreName.setWidth(cw);
		formLayout.addComponent(ttlManpower);
		formLayout.addComponent(incharge);
		incharge.setWidth(cw);
		formLayout.addComponent(location);
		location.setWidth(cw);

		formLayout.setMargin(true);
		formLayout.setSpacing(true);
		mainLayout.addComponent(formLayout);

		btnLayout.addComponent(button);		
		mainLayout.addComponent(btnLayout);
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		this.addComponent(mainLayout);
		mainLayout.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
		btnIni(true);
		txtEnable(false);
		setButtonAction();
		Component ob[] = {costCentreName, ttlManpower, incharge, location, button.btnSave, button.btnEdit};
		new FocusMoveByEnter(this,ob);
		findCostCentre.setEnabled(false);
		button.btnNew.focus();
	}

	private void setButtonAction()
	{
		button.btnNew.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newBtnAction();
				findCostCentre.setEnabled(false);
				findCostCentre.removeAllItems();
				txtClear();
				costCentreName.focus();
			}
		});

		button.btnSave.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(nullCheck())
					saveBtnAction();
			}
		});

		button.btnEdit.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateBtnAction();
				findCostCentre.setEnabled(false);
			}
		});

		button.btnDelete.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				deleteBtnAction();
			}
		});

		button.btnRefresh.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				btnIni(true);
				txtEnable(false);
				ttlManpower.setValue("");
				findCostCentre.setEnabled(false);
				findCostCentre.removeAllItems();
			}
		});

		button.btnFind.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction();
			}
		});

		findCostCentre.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(findCostCentre.getValue()!=null)
					setFindData();
			}
		});
	}

	private boolean nullCheck()
	{
		if(costCentreName.getValue().toString().trim().length()>0)
		{
			return true;
		}
		else
		{
			this.getParent().showNotification("","Please Enter Cost Centre Name.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}

	private void deleteBtnAction()
	{
		if(sessionBean.isDeleteable())
		{
			if(!isthereData())
				deleteData();
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void deleteData(){
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			session.createSQLQuery("DELETE FROM tbCostCentre WHERE id = '"+findCostCentre.getValue()+"'").executeUpdate();
			tx.commit();
			this.getParent().showNotification("Desired Information delete successfully.");
			isUpdate = false;
			ttlManpower.setValue("");
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private void newBtnAction()
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		ttlManpower.setValue("");
	}
	private void saveBtnAction(){
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
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
						insertData();
						button.btnNew.focus();
					}
				}
			});
		}
	}
	
	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(findCostCentre.getValue()!=null)
			{
				isUpdate = true;
				txtEnable(true);
				btnIni(false);
			}
			else
			{
				this.getParent().showNotification(
						"Edit Failed",
						"There are no data for Edit.",
						Notification.TYPE_WARNING_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void insertData(){
		if(sessionBean.isSubmitable()){
			Transaction tx = null;
			try{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String sql = "insert into tbCostCentre(id,costCentreName,ttlManpower,centreIncharge,location,userId,userIP,"+
				"insertTime,companyId) values(('U-'+cast((SELECT (isnull(max(cast(substring(id,3,50) as int)),0)+1) FROM tbCostCentre)as varchar)),'"+costCentreName.getValue()+"','"+ttlManpower.getValue()+"','"+incharge.getValue()+"','"+
				location.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+sessionBean.getCompanyId()+"')";
				session.createSQLQuery(sql).executeUpdate();
				tx.commit();
				this.getParent().showNotification("All information save successfully.");
				txtEnable(false);
				btnIni(true);
			}catch(Exception exp){
				tx.rollback();
				this.getParent().showNotification(
						"Error1",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for save.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void updateData(){
		if(sessionBean.isUpdateable()){
			Transaction tx = null;
			try{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String sql = "UPDATE tbCostCentre SET costCentreName = '"+costCentreName.getValue()+"',ttlManpower = '"+ttlManpower.getValue()+"',"+
				"centreIncharge = '"+incharge.getValue()+"',location = '"+location.getValue()+"',userId = '"+sessionBean.getUserId()+"',"+
				"userIP = '"+sessionBean.getUserIp()+"',insertTime = CURRENT_TIMESTAMP,companyId = '"+sessionBean.getCompanyId()+"' WHERE id = '"+findCostCentre.getValue()+"'";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				this.getParent().showNotification("Desired information update successfully.");
				txtEnable(false);
				btnIni(true);
			}catch(Exception exp){
				tx.rollback();
				this.getParent().showNotification(
						"Error",
						exp+"",
						Notification.TYPE_ERROR_MESSAGE);
			}
		}else{
			this.getParent().showNotification(
					"Authentication Failed",
					"You have not proper authentication for update.",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void findBtnAction(){
		findCostCentre.setEnabled(true);
		findInitialise();
	}
	private void findInitialise(){
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			findCostCentre.removeAllItems();

			List group = session.createSQLQuery("SELECT Id,costCentreName FROM tbCostCentre WHERE companyId = '"+sessionBean.getCompanyId()+"' and id like 'U-%' ORDER BY costCentreName").list();
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				findCostCentre.addItem(element[0].toString());
				findCostCentre.setItemCaption(element[0].toString(), element[1].toString());
			}
			findCostCentre.setNullSelectionAllowed(false);

		}
		catch(Exception exp)
		{
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void btnIni(boolean t){
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}
	private void txtEnable(boolean t){
		costCentreName.setEnabled(t);
		ttlManpower.setEnabled(t);
		incharge.setEnabled(t);
		location.setEnabled(t);
	}
	private void txtClear(){
		costCentreName.setValue("");
		ttlManpower.setValue("");
		incharge.setValue("");
		location.setValue("");
	}
	private void setFindData(){

		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			Iterator iter = session.createSQLQuery("SELECT costCentreName,ttlManpower,centreIncharge,location FROM tbCostCentre "+
					"WHERE id = '"+findCostCentre.getValue()+"'").list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				costCentreName.setValue(element[0].toString());
				ttlManpower.setValue(element[1].toString());
				incharge.setValue(element[2].toString());
				location.setValue(element[3].toString());
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean isthereData(){
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			Iterator iter = session.createSQLQuery("SELECT ledger_id FROM vwVoucher WHERE costId = '"+findCostCentre.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

			if(iter.hasNext())
			{
				this.getParent().showNotification(
						"",
						"Unable to delete this cost centre. Already data inserted for this costCentre.",
						Notification.TYPE_ERROR_MESSAGE);
				return true;
			}else{
				return false;
			}
		}catch(Exception exp){
			this.getParent().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
			return true;
		}
	}
}
