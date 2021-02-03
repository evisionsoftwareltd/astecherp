package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class ColorInformation extends Window 
{
	private AbsoluteLayout mainLayout;

	private Label lblColorNo;
	private TextRead txtColorNo;

	private Label lblColor;
	private TextField txtColorName;

	private Label lblColorDes;
	private TextArea txtColorDescription;
	private Label lblExistColorName;


	private CommonButton button = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");
	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextField ColorId=new TextField();
	boolean isUpdate=false;
	boolean isFind=false;

	SessionBean sessionBean;
	public ColorInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("COLOR INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		compInit(true);
		btnint(true);

		setEventAction();
		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck(){
		if(!sessionBean.isSubmitable()){
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			button.btnDelete.setVisible(false);
		}
	}

	private void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				newBtnAction();
				System.out.println("New Button");
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				compInit(true);
				btnint(true);
				txtClear();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();	
			}
		});


		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtColorName.getValue().toString().isEmpty())
				{
					saveButtonEvent();
				}
				else
				{
					showNotification("Warning!","Provide Size",Notification.TYPE_WARNING_MESSAGE);
					txtColorName.focus();
				}
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtColorName.focus();
				if(sessionBean.isUpdateable())
				{
					isUpdate = true;
					isFind=true;
					updateButtonEvent();
				}
				else
				{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				findButtonEvent();
				isFind=false;
			}
		});

		txtColorName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind)
				{
					if(!txtColorName.getValue().toString().isEmpty())
					{
						if(duplicateName())
						{
							lblExistColorName.setVisible(true);
							lblExistColorName.setValue("<b><Font Color='#CD0606'>! Already Exist</Font></b>");
							txtColorName.setValue("");
							txtColorName.focus();
						}
						else
						{	
							lblExistColorName.setVisible(false);
						}
					}
				}
			}
		});
	}
	

	private boolean duplicateName()
	{
		String ColorName="";
		
		if(!isUpdate){
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select vColorName from tbColorInformation where vColorName='"+txtColorName.getValue().toString().trim()+"' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		}
		return false;
	}

	private void findButtonEvent()
	{
		Window win = new ColorFindWindow (sessionBean, ColorId,"ColorId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(ColorId.getValue().toString().length() > 0)
				{
					System.out.println(ColorId.getValue().toString());
					txtClear();
					findInitialise(ColorId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}


	private void findInitialise(String ColorId)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select vColorId,vColorName,vColorDescription from tbColorInformation where iAutoId='"+ColorId+"'";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtColorNo.setValue(element[0]);
				txtColorName.setValue(element[1]);
				txtColorDescription.setValue(element[2]);
				//System.out.println("Value is Show :"+element[0]);
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
		if(!txtColorName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnint(false);
			compInit(false);//Enable(true);
			//focusEnter();*/
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
	{

		if(!isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Save ColorInformation?",
					new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
					new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						System.out.print("OK");
						insertData();
						btnint(true);
						btnint(true);
						txtClear();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Update ColorInformation?",
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
						isUpdate=false;
						btnint(true);
						compInit(true);
						txtClear();
					}
				}
			});
		}
	}

	private void insertData()
	{
		Transaction tx= null;
		try
		{
			Session session= SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String insertData="INSERT INTO tbColorInformation values(" +
					" '"+autoIdGenerate()+"', " +
					" '"+txtColorName.getValue().toString().trim()+"', " +
					" '"+txtColorDescription.getValue().toString().trim()+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP )";

			System.out.println("insertData: "+insertData);
			session.createSQLQuery(insertData).executeUpdate();

			showNotification("All information saved successfully");
			tx.commit();
		}
		catch (Exception ex) 
		{
			this.getParent().showNotification("Error",ex+"", Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void updateData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String updateProduct ="UPDATE tbColorInformation set" +
					" vColorName ='"+txtColorName.getValue().toString().trim()+"' ," +
					" vColorDescription ='"+txtColorDescription.getValue().toString().trim()+"' " +
					" where vColorId ='"+ColorId.getValue()+"'";

			System.out.println("UpdateProduct: "+updateProduct);
			session.createSQLQuery(updateProduct).executeUpdate();

			this.getParent().showNotification("All information update successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newBtnAction()
	{
		txtClear();
		compInit(false);
		btnint(false);
		focusEnter();
		txtColorNo.setValue(autoIdGenerate());
		txtColorName.focus();
	}

	private String autoIdGenerate()
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select ISNULL(MAX(vColorId),0)+1 from tbColorInformation";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}

	private void compInit(boolean t)
	{
		txtColorNo.setEnabled(!t);
		txtColorName.setEnabled(!t);
		txtColorDescription.setEnabled(!t);
	}

	private void txtClear()
	{
		txtColorNo.setValue("");
		txtColorName.setValue("");
		txtColorDescription.setValue("");
		lblExistColorName.setValue("");
		lblExistColorName.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(txtColorNo);
		allComp.add(txtColorName);
		allComp.add(txtColorDescription);

		allComp.add(button.btnNew);
		allComp.add(button.btnEdit);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnDelete);
		allComp.add(button.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnint(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);	
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("540px");
		mainLayout.setHeight("180px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("545px");
		setHeight("220px");
		this.setResizable(false);

		//lblColorNo
		lblColorNo = new Label("Color ID :");
		lblColorNo.setImmediate(false);
		lblColorNo.setWidth("100.0%");
		lblColorNo.setHeight("-1px");
		mainLayout.addComponent(lblColorNo,"top:20.0px; left:50.0px;");

		//txtColorNo
		txtColorNo = new TextRead();
		txtColorNo.setImmediate(false);
		txtColorNo.setWidth("80px");
		txtColorNo.setHeight("24px");
		mainLayout.addComponent(txtColorNo,"top:18.0px; left:160.0px;");

		//lblColor
		lblColor = new Label("Color Name :");
		lblColor.setImmediate(false);
		lblColor.setWidth("100%");
		lblColor.setHeight("-1px");
		mainLayout.addComponent(lblColor,"top:50px;left:50px;");

		//txtColorName
		txtColorName = new TextField();
		txtColorName.setImmediate(false);
		txtColorName.setWidth("200px");
		txtColorName.setHeight("-1px");
		txtColorName.setSecret(false);
		mainLayout.addComponent(txtColorName,"top:48.0px; left:160.0px;");
		
		lblExistColorName = new Label();
		lblExistColorName.setWidth("-1px");
		lblExistColorName.setHeight("-1px");
		lblExistColorName.setImmediate(true);
		lblExistColorName.setContentMode(Label.CONTENT_XHTML);
		lblExistColorName.setVisible(false);
		lblExistColorName.setValue("");
		mainLayout.addComponent(lblExistColorName, " top:50px;left:400.0px;");

		//lblColorDes
		lblColorDes = new Label("Color Description :");
		lblColorDes.setImmediate(false);
		lblColorDes.setWidth("100%");
		lblColorDes.setHeight("-1px");
		mainLayout.addComponent(lblColorDes,"top:80px;left:50px;");

		//txtColorDescription
		txtColorDescription = new TextArea();
		txtColorDescription.setImmediate(false);
		txtColorDescription.setWidth("250px");
		txtColorDescription.setHeight("40px");
		mainLayout.addComponent(txtColorDescription,"top:78.0px; left:160.0px;");


		mainLayout.addComponent(button, "top:140.0px;left:25.0px;");
		return mainLayout;
	}

}
