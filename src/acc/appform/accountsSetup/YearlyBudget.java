package acc.appform.accountsSetup;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.AmountCommaSeperator;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Formatter;

@SuppressWarnings("serial")
public class YearlyBudget extends Window
{
	private SessionBean sessionBean;
	CommonButton button = new CommonButton("", "Save", "", "Refresh", "", "", "", "", "","Exit");	
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private VerticalLayout leftVerLayout = new VerticalLayout();
	private VerticalLayout rigthVerLayout = new VerticalLayout();
	private GridLayout grid = new GridLayout(1,1);

	private OptionGroup group = new OptionGroup();
	private NativeSelect category = new NativeSelect("Income Category:");
	private NativeSelect groupList = new NativeSelect("Group List:");
	private NativeSelect subGroupList = new NativeSelect("Sub-Group List:");

	private Table table = new Table();
	private ArrayList<Label> sl = new ArrayList<Label>();
	private ArrayList<Label> ledger = new ArrayList<Label>();
	private ArrayList<AmountCommaSeperator> amt = new ArrayList<AmountCommaSeperator>();

	private NativeButton proceedBtn = new NativeButton("Proceed");

	private String cw = "220px";
	private DecimalFormat fmt = new DecimalFormat("#0.00");
	//private Formatter fmt = new Formatter();

	public YearlyBudget(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("Yearly Budget Declare :: "+this.sessionBean.getCompany());
		this.setWidth("760px");
		this.setResizable(false);

		table.setWidth("450px");
		table.setHeight("285px");
		table.addContainerProperty("SL NO", Label.class, new Label());
		table.addContainerProperty("LEDGER", Label.class, new Label());
		table.addContainerProperty("Amount", AmountCommaSeperator.class, new AmountCommaSeperator(),null,null,Table.ALIGN_RIGHT);
		table.setFooterVisible(true);
		tableInitialise();
		group.addItem("1");
		group.setItemCaption("1", "Income");
		group.addItem("2");
		group.setItemCaption("2", "Expenses");
		group.setImmediate(true);
		leftVerLayout.addComponent(group);
		leftVerLayout.addComponent(category);
		category.setWidth(cw);
		category.setImmediate(true);
		category.setWidth(cw);
		category.setImmediate(true);
		groupList.setWidth(cw);
		groupList.setImmediate(true);
		subGroupList.setWidth(cw);
		subGroupList.setImmediate(true);
		leftVerLayout.addComponent(groupList);
		leftVerLayout.addComponent(subGroupList);
		leftVerLayout.addComponent(proceedBtn);
		proceedBtn.setWidth("80px");
		proceedBtn.setHeight("28px");
		leftVerLayout.setSpacing(true);
		leftVerLayout.setMargin(true);
		horLayout.addComponent(leftVerLayout);		
		horLayout.addComponent(rigthVerLayout);
		rigthVerLayout.setWidth("480px");
		rigthVerLayout.addComponent(table);
		rigthVerLayout.addComponent(btnLayout);		
		HorizontalLayout space = new HorizontalLayout();
		space.setWidth("80px");
		btnLayout.addComponent(space);
		btnLayout.addComponent(button);
		btnLayout.setMargin(true);
		btnLayout.setSpacing(true);		
		grid.addComponent(horLayout,0,0);
		mainLayout.addComponent(grid);
		mainLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		this.addComponent(mainLayout);
		setButtonAction();
		group.setValue("1");
	}

	private void setButtonAction()
	{
		group.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				groupSelect();
			}
		});

		category.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				categorySelect();
			}
		});

		groupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				groupListSelect();
			}
		});

		subGroupList.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				subGroupListSelect();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				cancelBtnAction(event);
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				exitBtnAction();
			}
		});

		proceedBtn.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				processBtnAction();
			}
		});
	}

	private void exitBtnAction()
	{
		this.close();
	}

	private void saveBtnAction()
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

	private void saveData()
	{
		if(sessionBean.isSubmitable())
		{
			double d = 0;
			Transaction tx = null;
			String budYear;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				budYear = session.createQuery("SELECT year(opDate) FROM TbFiscalYear WHERE runningFlag = 1").list().iterator().next().toString();

				for(int i = 0;i<ledger.size();i++)
				{
					try
					{
						d = Double.valueOf("0"+amt.get(i).getValue());
					}
					catch(Exception e){}
					if(d > 0)
					{
						session.createSQLQuery("DELETE FROM tbBudget WHERE Ledger_Id = '"+ledger.get(i).getDebugId()+"' AND companyId = '"+ sessionBean.getCompanyId() +"' AND Op_Year = "+budYear).executeUpdate();
						session.createSQLQuery("INSERT INTO tbBudget(Ledger_Id,BudgetAmount,Op_Year,Create_From,userId,userIp,entryTime, companyId) VALUES('"+
								ledger.get(i).getDebugId()+"',"+d+","+budYear+",(SELECT Create_From FROM tbLedger WHERE ledger_id = '"+ledger.get(i).getDebugId()+
								"'),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')").executeUpdate();
					}
				}
				tx.commit();
				this.getParent().showNotification("All Information save successfully.");
				initialBtnPos(false);
				tableInitialise();
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cancelBtnAction(ClickEvent e)
	{		
		tableInitialise();
		initialBtnPos(false);
	}

	private void processBtnAction()
	{
		String sql = "SELECT tbLedger.Ledger_Id,tbLedger.Ledger_Name,isnull(tbBudget.BudgetAmount,0) "+
		"FROM tbLedger LEFT OUTER JOIN tbBudget ON tbLedger.Ledger_Id = tbBudget.Ledger_Id "+
		"AND tbBudget.Op_Year = ";
		
		System.out.println(sql);

		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String budYear = session.createQuery("SELECT year(opDate) FROM TbFiscalYear WHERE runningFlag = 1").list().iterator().next().toString();

			if(subGroupList.getValue() != null)
				sql = sql+budYear+" WHERE tbLedger.companyId in ('0','"+ sessionBean.getCompanyId() +"') AND tbLedger.Parent_Id = '"+subGroupList.getValue()+"' ORDER BY Ledger_Name";
			else if(groupList.getValue()  != null)
				sql = sql+budYear+" WHERE tbLedger.companyId in ('0','"+ sessionBean.getCompanyId() +"') AND tbLedger.Parent_Id = '"+groupList.getValue()+"' ORDER BY Ledger_Name";
			else if(category.getValue()  != null)
				sql = sql+budYear+" WHERE tbLedger.companyId in ('0','"+ sessionBean.getCompanyId() +"') AND SUBSTRING(tbLedger.Create_From,1,2)= '"+category.getValue()+"' ORDER BY Ledger_Name";
			else if(group.getValue().toString().equalsIgnoreCase("1"))
				sql = sql+budYear+" WHERE tbLedger.companyId in ('0','"+ sessionBean.getCompanyId() +"') AND SUBSTRING(tbLedger.Create_From,1,1) = 'I' ORDER BY ledger_Name";
			else
				sql = sql+budYear+" WHERE tbLedger.companyId in ('0','"+ sessionBean.getCompanyId() +"') AND SUBSTRING(tbLedger.Create_From,1,1) = 'E' ORDER BY Ledger_Name";

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			table.removeAllItems();
			int i = 0;
			sl.clear();
			ledger.clear();
			amt.clear();
			for (; iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				sl.add(i,new Label());
				sl.get(i).setValue(i+1);
				sl.get(i).setWidth("10px");
				ledger.add(i,new Label());
				ledger.get(i).setWidth("230px");
				ledger.get(i).setDebugId(element[0].toString());
				ledger.get(i).setValue( element[1].toString());

				amt.add(i,new AmountCommaSeperator());
				amt.get(i).setWidth("110px");
				//amt.get(i).setStyleName("fright");
			
				if(element[2]!=null)
				{
				//	fmt = new Formatter();
					//fmt.format("%.2f", Double.valueOf(element[2].toString()));
					System.out.println(element[2].toString());	
				//	amt.get(i).setValue("0"+fmt.format(element[2].toString()));
					amt.get(i).setValue(fmt.format(Double.valueOf(element[2].toString())));
					//balance.get(t).setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
					System.out.println("Hello1");	
				}
				else
					amt.get(i).setValue("0");
		//		System.out.println("Hello3");
				
				table.addItem(new Object[]{sl.get(i),ledger.get(i),amt.get(i)},i); 
				System.out.println(i);
				i++;
			}
			initialBtnPos(true);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void initialBtnPos(boolean t)
	{
		button.btnSave.setEnabled(t);
		button.btnRefresh.setEnabled(t);
		proceedBtn.setEnabled(!t);

		group.setEnabled(!t);
		category.setEnabled(!t);
		groupList.setEnabled(!t);
		subGroupList.setEnabled(!t);
	}
	private void tableInitialise()
	{	
		try
		{

			table.removeAllItems();
			for(int i=0;i<8;i++){
				sl.add(i,new Label());
				sl.get(i).setValue(i+1);
				sl.get(i).setWidth("10px");

				ledger.add(i,new Label());
				ledger.get(i).setWidth("230px");

				amt.add(i,new AmountCommaSeperator());
			//	amt.get(i).setStyleName("fleft");
				//amt.get(i)
				amt.get(i).setWidth("110px");
				table.addItem(new Object[]{sl.get(i),ledger.get(i),amt.get(i)},i); 
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void groupSelect()
	{
		category.removeAllItems();
		groupList.removeAllItems();
		subGroupList.removeAllItems();
		categoryInitialise();
	}
	private void categorySelect()
	{
		groupList.removeAllItems();
		subGroupList.removeAllItems();
		groupListInitialise();
	}
	private void categoryInitialise()
	{
		String sql = "";
		if(group.getValue().toString().equalsIgnoreCase("1"))
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'I' order by slNo";
			category.setCaption("Income Category:");
		}
		else 
		{
			sql = "SELECT headId,headName,slNo FROM TbPrimaryGroup WHERE substring(headId,1,1) = 'E' order by slNo";
			category.setCaption("Expenses Category:");
		}
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery(sql).list();			
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				category.addItem(element[0].toString());
				category.setItemCaption(element[0].toString(), element[1].toString());
			}
			category.setNullSelectionAllowed(true);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void groupListSelect()
	{
		subGroupList.removeAllItems();
		subGroupListInitialise();
	}

	private void groupListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT groupId,groupName FROM TbMainGroup WHERE headId = '"+category.getValue()+"'").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				groupList.addItem(element[0].toString());
				groupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			groupList.setNullSelectionAllowed(true);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void subGroupListSelect()
	{

	}
	private void subGroupListInitialise()
	{
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createQuery("SELECT subGroupId,subGroupName FROM TbSubGroup WHERE groupId = '"+groupList.getValue()+"'").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				subGroupList.addItem(element[0].toString());
				subGroupList.setItemCaption(element[0].toString(), element[1].toString());
			}
			subGroupList.setNullSelectionAllowed(true);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
/*
 * private void processBtnAction(ClickEvent e){
		String sql = "";
		if(!subGroupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+subGroupList.getValue()+"' ORDER BY Ledger_Name";
		else if(!groupList.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  parentId = '"+groupList.getValue()+"' ORDER BY Ledger_Name";
		else if(!category.getValue().equals(""))
			sql = "SELECT ledgerId,ledgerName,parentId FROM TbLedger WHERE  SUBSTRING(Create_From,1,2)= '"+category.getValue()+"' ORDER BY Ledger_Name";
		else if(group.getValue().toString().equalsIgnoreCase("1"))
			sql = "SELECT ledgerId,ledgerName FROM TbLedger WHERE SUBSTRING(createFrom,1,1) = 'I' ORDER BY ledgerName";
		else
			sql = "SELECT ledgerId,ledgerName FROM TbLedger WHERE SUBSTRING(createFrom,1,1) = 'E' ORDER BY ledgerName";
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String budYear = session.createQuery("SELECT year(opDate) FROM TbFiscalYear WHERE runningFlag = 1").list().iterator().next().toString();
			List group = session.createQuery(sql).list();

			table.removeAllItems();
			int i = 0;
			tLedg = 0;
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				sl[i] = new Label();
				sl[i].setValue(i+1);
				ledger[i] = new Label();
				ledger[i].setWidth("250px");
				ledger[i].setDebugId(element[0].toString());
				ledger[i].setValue( element[1].toString());

				amt[i] = new TextField();
				amt[i].setWidth("85px");
				amt[i].setStyleName("fright");
				amt[i].setValue(getAmt(session,element[0].toString(),budYear));
				table.addItem(new Object[]{sl[i],ledger[i],amt[i]},i); 
				i++;
				tLedg++;
			}
			initialBtnPos(true);
		}catch(Exception exp){
			this.getParent().showNotification(
                    "Error",
                    exp+"",
                    Notification.TYPE_ERROR_MESSAGE);
		}
	}
 */
