package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class DesignationSerialInfo extends Window 
{
	SessionBean sessionBean;

	private AbsoluteLayout mainLayout;
	private Table table = new Table();
	private ArrayList<Label> tbSlNo = new ArrayList<Label>();
	private ArrayList<Label> tbDesignationID = new ArrayList<Label>();
	private ArrayList<Label> tbDesignationName = new ArrayList<Label>();
	private ArrayList<AmountField> tbDesignationSerial = new ArrayList<AmountField>();
	private CommonButton button = new CommonButton("", "Save", "", "", "", "", "", "", "", "Exit");

	public DesignationSerialInfo(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("DESIGNATION SERIAL :: "+sessionBean.getCompany());
		this.setWidth("460px");
		this.setHeight("425px");
		sessionBean.getCompanyId();

		buildMainLayout();
		tableInitialize();
		setContent(mainLayout);
		btnAction();
		tableClear();
		addData();
		focusEnter();
	}

	private void focusEnter()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();

		for(int i=0;i<tbDesignationName.size();i++)
		{
			allComp.add(tbDesignationSerial.get(i));
		}
		new FocusMoveByEnter(this,allComp);
	}

	private void btnAction()
	{
		button.btnSave.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(sessionBean.isUpdateable())
				{
					if(chkTableData())
						saveAction();
					else
						showNotification("Warning", "Please Provide Designation Serial!!!", Notification.TYPE_WARNING_MESSAGE);
				}
				else
				{
					showNotification("Authentication Failed","You have not proper authentication for save.!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});


		button.btnExit.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				close();
			}
		});
	}

	private void saveAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
						saveData();
				}
			}
		});
	}

	private boolean chkTableData()
	{
		for(int tbind=0;tbind<tbDesignationID.size();tbind++)
		{
			if(!tbDesignationID.get(tbind).getValue().toString().isEmpty())
			{
				if(tbDesignationSerial.get(tbind).getValue().toString().isEmpty())
					return false;
			}
		}
		return true;
	}
	
	private void saveData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i=0;i<tbDesignationName.size();i++)
			{
				if(!tbDesignationName.get(i).getValue().toString().trim().isEmpty())
				{
					System.out.println("Still Ok");
					String slNo = tbDesignationSerial.get(i).getValue().toString().isEmpty()?"0":tbDesignationSerial.get(i).getValue().toString().replaceAll(",", "");

					String sql = " Update tbDesignationInfo set designationSerial = '"+slNo+"'  " +
							" where designationId = '"+tbDesignationID.get(i).getValue()+"' ";
					session.createSQLQuery(sql).executeUpdate();
					System.out.println(sql);
				}
			}

			tx.commit();
			showNotification("All information save successfully.");

			tableClear();	
			addData();
		}
		catch(Exception exp)
		{
			showNotification("saveData ",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	private void addData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			String sql = "select designationID,designationName,designationSerial from  tbDesignationInfo order by Cast(designationSerial as int)";
			List <?> list = session.createSQLQuery(sql).list();
			int i = 0;

			tableClear();

			if(!list.isEmpty())
			{
				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					tbDesignationID.get(i).setValue(element[0]);
					tbDesignationName.get(i).setValue(element[1]);
					tbDesignationSerial.get(i).setValue(Integer.parseInt(element[2].toString()));

					if(tbDesignationSerial.size()-1==i)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally
		{
			session.close();
		}
	}

	private void tableClear()
	{
		for(int i=0;i<tbDesignationName.size();i++)
		{
			tbDesignationID.get(i).setValue("");
			tbDesignationName.get(i).setValue("");
			tbDesignationSerial.get(i).setValue("");
		}
	}

	private void tableInitialize()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ar)
	{
		tbSlNo.add(new Label());
		tbSlNo.get(ar).setWidth("100%");
		tbSlNo.get(ar).setHeight("20px");
		tbSlNo.get(ar).setImmediate(true);
		tbSlNo.get(ar).setValue(ar+1);

		tbDesignationID.add(new Label());
		tbDesignationID.get(ar).setWidth("100%");
		tbDesignationID.get(ar).setImmediate(true);

		tbDesignationName.add(new Label());
		tbDesignationName.get(ar).setWidth("100%");
		tbDesignationName.get(ar).setImmediate(true);

		tbDesignationSerial.add(new AmountField());
		tbDesignationSerial.get(ar).setWidth("100%");
		tbDesignationSerial.get(ar).setImmediate(true);
		tbDesignationSerial.get(ar).setStyleName("fright");

		tbDesignationSerial.get(ar).addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbDesignationSerial.get(ar).getValue().toString().trim().isEmpty())
				{
					boolean fla = (doubleEntryCheck(tbDesignationSerial.get(ar).getValue().toString().trim(),ar));
					if (tbDesignationSerial.get(ar).getValue().toString().trim().isEmpty()  && !fla) 
					{
						showNotification("Warning","Same Designation Serial Is Not Applicable!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
		table.addItem(new Object[] {tbSlNo.get(ar),tbDesignationID.get(ar),tbDesignationName.get(ar),tbDesignationSerial.get(ar)}, new Integer(ar));
	}

	private boolean doubleEntryCheck(String caption,int row)
	{
		for(int i=0;i<tbDesignationName.size();i++)
		{
			if(i!=row && caption.equals(tbDesignationSerial.get(i).getValue().toString().trim()))
			{
				tbDesignationSerial.get(row).setValue("");
				return false;
			}	
		}
		return true;
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		table.setWidth("410px");
		table.setHeight("300px");
		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		table.setPageLength(0);
		table.setFooterVisible(false);

		table.addContainerProperty("Sl#", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		table.setColumnWidth("SL#", 30);

		table.addContainerProperty("DESIGNATION ID", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("DESIGNATION ID", 60);

		table.addContainerProperty("Designation Name", Label.class, new Label(),null,null,Table.ALIGN_LEFT);
		table.setColumnWidth("Designation Name", 200);

		table.addContainerProperty("Designation SERIAL", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Designation SERIAL", 110);

		table.setColumnCollapsed("DESIGNATION ID", true);

		mainLayout.addComponent(table,"top:20.0px;left:20.0px;");
		mainLayout.addComponent(button,"top:340.0px;left:150.0px;");
		return mainLayout;
	}
}
