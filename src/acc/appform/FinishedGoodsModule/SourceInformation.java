package acc.appform.FinishedGoodsModule;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
public class SourceInformation extends Window 
{
	private AbsoluteLayout mainLayout;

	private Label lblSourceId;
	private TextRead txtRSubcategoryId;

	private Label lblSourceName;
	private TextField txtSourceName;

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblline;

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	private TextField txtSubCategoryID = new TextField();

	private SessionBean sessionBean;
	boolean isUpdate=false;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private String findSubGroupId = "";

	public SourceInformation(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("SOURCE NAME INFORMATION :: " + sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		//cmbPartyValueLoad();
		setEventAction();
		authencationCheck();
		cmbPartyValueLoad();
	}

	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(true);
		}
		else
		{
			button.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(true);
		}
		else
		{
			button.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(true);
		}
		else
		{
			button.btnDelete.setVisible(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(txtSourceName);
		allComp.add(cmbPartyName);

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this,allComp);
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				focusEnter();
				txtSourceName.focus();
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable()){
					updateButtonEvent();
					focusEnter();
					txtSourceName.focus();
				}
				else{
					getParent().showNotification("Warning,","You have not Proper Authentication to Edit.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable()){
					if(!txtSourceName.getValue().toString().isEmpty()){
						saveButtonEvent();
					}
					else{
						getParent().showNotification("Warning,","Enter Source Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}else{
					getParent().showNotification("Warning,","You have not Proper Authentication to Save.", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
				cmbPartyValueLoad();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void findButtonEvent() 
	{
		Window win = new SourceInformationFind(sessionBean, txtSubCategoryID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtSubCategoryID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtSubCategoryID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtSourctId) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query="";
			query=" select iSourceID,vSourceName,vPartyID,vPartyName from tbSourceInfo Where iSourceID = '"+txtSourctId+"'  ";

			List led = session.createSQLQuery(query).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtRSubcategoryId.setValue(element[0]);
				txtSourceName.setValue(element[1]);
				cmbPartyName.setValue(element[2]);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private void updateButtonEvent()
	{
		if(!txtSourceName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			txtRSubcategoryId.setEnabled(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		isUpdate=false;
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	public void updateData() 
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String updateQuery = "update  tbSourceInfo set iSourceID='"+txtRSubcategoryId.getValue()+"',"
					+ " vSourceName='"+txtSourceName.getValue()+"', "
					+ "vPartyID='"+cmbPartyName.getValue()+"', vPartyName='"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'"
					+ "where iSourceID like '"+txtRSubcategoryId.getValue().toString()+"'";
			session.createSQLQuery(updateQuery).executeUpdate();
			System.out.println("updateQuery : "+updateQuery);
			
			tx.commit();
			this.getParent().showNotification("Update successfully.");
			//cmbPartyValueLoad();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}	
	}

	private void saveButtonEvent() 
	{

		if(cmbPartyName.getValue()!=null)
		{
			if(isUpdate)
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update product information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							updateData();
							button.btnNew.focus();
							isUpdate=false;
							componentIni(true);
							txtClear();
							btnIni(true);
							mb.close();
						}
					}
				});		
			}
			else
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new MessageBox.EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							btnIni(true);
							 //cmbPartyValueLoad();
							componentIni(true);
							txtClear();
							btnIni(true);
							button.btnNew.focus();
							mb.close();
						}
					}
				});		
			}
		}

		else
		{
			this.getParent().showNotification("Warning :", "Please select Category Name", Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}	


	}

	private void insertData()
	{
		Transaction tx = null;
		String subGroupId = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String insertQuery ="insert into tbSourceInfo (iSourceID, vSourceName, vPartyID, vPartyName, "
					+ "vUserName, vUserIP, dtEntryTime) "
					+ "values('"+txtRSubcategoryId.getValue()+"','"+txtSourceName.getValue()+"',"
					+ " '"+cmbPartyName.getValue().toString()+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"',"
					+ " '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";

			session.createSQLQuery(insertQuery).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information Save successfully.");
			btnIni(true);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		newBtnData(1);
		cmbPartyValueLoad();
		txtRSubcategoryId.setEnabled(false);	
	}

	private Iterator<?> dbService(String sql){

		System.out.println(sql);
		Session session=null;
		Iterator<?> iter=null;
		try {
			session=SessionFactoryUtil.getInstance().openSession();
			iter=session.createSQLQuery(sql).list().iterator();
		} 
		catch (Exception e) {
			showNotification(null,""+e,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
		return iter;
	}

	public void cmbPartyValueLoad()
	{
		cmbPartyName.removeAllItems();
		try
		{
			String sql="select partyCode,partyName from tbPartyInfo where isActive like '1'";
			Iterator<?> iter=dbService(sql);
			while(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void newBtnData(int flag)
	{
		if(txtSourceName.getValue().toString().isEmpty() || flag==1)
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String sql = "Select isnull(max(iSourceID)+1,1) as id from tbSourceInfo";
				Iterator iter = session.createSQLQuery(sql).list().iterator();
				int num = 0;

				if (iter.hasNext())
				{
					num = Integer.parseInt(iter.next().toString());
					txtRSubcategoryId.setValue(num);
				}
			}
			catch(Exception ex)
			{
				this.getParent().showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	public void txtClear()
	{
		txtRSubcategoryId.setValue("");
		txtSourceName.setValue("");
		cmbPartyName.setValue(null);
	}

	private void componentIni(boolean b) 
	{
		lblSourceId.setEnabled(!b);
		txtRSubcategoryId.setEnabled(!b);		

		lblSourceName.setEnabled(!b);
		txtSourceName.setEnabled(!b);

		lblPartyName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);

		lblline.setEnabled(!b);

	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("530px");
		mainLayout.setHeight("230px");
		mainLayout.setMargin(false);

		lblSourceId = new Label();
		lblSourceId.setImmediate(false);
		lblSourceId.setWidth("-1px");
		lblSourceId.setHeight("-1px");
		lblSourceId.setValue("Source Id: ");
		mainLayout.addComponent(lblSourceId, "top:20.0px;left:43.0px;");

		txtRSubcategoryId= new TextRead();
		txtRSubcategoryId.setImmediate(false);
		txtRSubcategoryId.setWidth("101px");
		txtRSubcategoryId.setHeight("24px");
		mainLayout.addComponent(txtRSubcategoryId, "top:18.0px;left:160.0px;");

		lblSourceName = new Label();
		lblSourceName.setImmediate(false);
		lblSourceName.setWidth("-1px");
		lblSourceName.setHeight("-1px");
		lblSourceName.setValue("Source Name :");
		mainLayout.addComponent(lblSourceName, "top:50.0px;left:20.0px;");

		txtSourceName = new TextField();
		txtSourceName.setImmediate(false);
		txtSourceName.setWidth("280px");
		txtSourceName.setHeight("-1px");
		txtSourceName.setSecret(false);
		mainLayout.addComponent(txtSourceName, "top:48.0px;left:160.0px;");

		lblPartyName = new Label();
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		lblPartyName.setValue("Party Name:");
		mainLayout.addComponent(lblPartyName, "top:80.0px;left:45.0px;");

		cmbPartyName= new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("280px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setNewItemsAllowed(false);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:78.0px;left:160.0px;");

		lblline= new Label();
		lblline.setImmediate(false);
		lblline.setWidth("-1px");
		lblline.setHeight("-1px");
		lblline.setValue("_________________________________________________________________________");
		mainLayout.addComponent(lblline, "top:140.0px;left:10.0px;");

		mainLayout.addComponent(button, "top:180.0px;left:12.0px;");

		return mainLayout;
	}

}
