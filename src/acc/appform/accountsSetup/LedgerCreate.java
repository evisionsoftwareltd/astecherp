package acc.appform.accountsSetup;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LedgerCreate extends Window
{	
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private Label lblPrimaryCat;
	private Label lblMainCat;
	private Label lblGroupList;
	private Label lblSubGroupList;
	private Label lblLedgerList;
	private Label lblLedgerName;
	private Label lblDebAmt;
	private Label lblCrdAmt;
	private Label lblOpyear;

	private NativeSelect primaryCat;
	private NativeSelect mainCat;
	private NativeSelect groupList;
	private NativeSelect subGroupList;

	private ListSelect ledgerList;

	private TextField ledgerName;
	private TextField opyear;

	private String cww = "270px";
	private String cw = "300px";

	private NativeButton btnGroup;
	private NativeButton btnSubGroup;

	private Label lblLed = new Label("<B><u>Ledger Information</u></B>",Label.CONTENT_XHTML);
	private Label lblOpen = new Label("<B><u>Opening Balance Information</u></B>",Label.CONTENT_XHTML);

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private AmountCommaSeperator debAmt;
	private AmountCommaSeperator crdAmt;

	private CheckBox chkCompany = new CheckBox("All Company");

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "", "", "", "","");

	private CommaSeparator cms = new CommaSeparator();	

	private NumberFormat fmt = new DecimalFormat("#0.00");
	private HashMap hm = new HashMap();
	private boolean isUpdate = false;

	public LedgerCreate(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEDGER :: "+this.sessionBean.getCompany());
		this.setWidth("860px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		listenerMethod();
		setButtonAction();
		mainCatInitialise();
		initialise();
	}

	private void listenerMethod()
	{
		primaryCat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				primaryCatSelect();
			}
		});

		mainCat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				mainCatSelect();				
			}
		});

		groupList.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				groupListSelect();
			}
		});

		subGroupList.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				subGroupListSelect();
			}
		});

		ledgerList.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				ledgerListSelect();
			}
		});
	}

	private void setButtonAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isFixedAsset(0))
				{
					if(!mainCat.getValue().toString().equalsIgnoreCase("") && !mainCat.getValue().toString().equalsIgnoreCase("") &&
							!mainCat.getValue().toString().equalsIgnoreCase("A5") && !mainCat.getValue().toString().equalsIgnoreCase("I1"))
					{
						ledgerName.setValue("");
						chkCompany.setValue(false);
						slInitialise(false);
						ledgerName.setEnabled(true);
						chkCompany.setEnabled(true);
						crdAmt.setValue("");
						crdAmt.setEnabled(true);
						debAmt.setValue("");
						debAmt.setEnabled(true);
						opyear.setValue(dtfYMD.format(sessionBean.getFiscalOpenDate()).substring(0, 4));
						opyear.setEnabled(true);
					}
					else
					{
						showNotification("Warning!", "Ledger create is not available in this head",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				if(!isFixedAsset(1))
				{
					if(!mainCat.getValue().toString().equalsIgnoreCase("") && !mainCat.getValue().toString().equalsIgnoreCase("") &&
							!mainCat.getValue().toString().equalsIgnoreCase("A5") &&  !mainCat.getValue().toString().equalsIgnoreCase("I1"))
					{
						updateBtnAction();
					}
					else
					{
						showNotification("Warning!", "Ledger update is not available in this head",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{				
				if(isValid())
				{
					saveBtnAction();
					slInitialise(true);
				}
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				ledgerName.setValue("");
				slInitialise(true);
				ledgerName.setEnabled(false);
				chkCompany.setValue(false);
				chkCompany.setEnabled(false);
				crdAmt.setValue("");
				crdAmt.setEnabled(false);
				debAmt.setValue("");
				debAmt.setEnabled(false);
				opyear.setValue("");
				opyear.setEnabled(false);
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!mainCat.getValue().toString().equalsIgnoreCase("A6") && !mainCat.getValue().toString().equalsIgnoreCase("L10") &&
						!mainCat.getValue().toString().equalsIgnoreCase("A5") &&  !mainCat.getValue().toString().equalsIgnoreCase("I1"))
				{
					deleteBtnAction();
				}
				else
				{
					showNotification("Warning!", "Ledger delete is not available in this head",Notification.TYPE_WARNING_MESSAGE);
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

		btnSubGroup.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				subGroupLink();
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
				grouListInitialise();
			}
		});

		this.getParent().addWindow(win);
	}

	public void subGroupLink()
	{
		Window win = new SubGroupCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				subGroupListInitialise();
			}
		});

		this.getParent().addWindow(win);
	}

	private void initialise()
	{
		isUpdate = false;
		ledgerName.setValue("");
		ledgerName.setEnabled(false);
		chkCompany.setValue(false);
		chkCompany.setEnabled(false);
		crdAmt.setValue("");
		crdAmt.setEnabled(false);
		debAmt.setValue("");
		debAmt.setEnabled(false);
		opyear.setValue("");
		opyear.setEnabled(false);
		button.btnNew.setEnabled(false);
		button.btnEdit.setEnabled(false);
		button.btnSave.setEnabled(false);
		button.btnRefresh.setEnabled(false);
		button.btnDelete.setEnabled(false);
	}

	private void mainCatInitialise()
	{
		String sql = "";

		if(primaryCat.getValue().toString().equalsIgnoreCase("1"))
		{
			//sql = "SELECT head_Id,head_Name,slNo FROM tbPrimary_Group order by substring(head_Id,1,1),slNo";
			sql="SELECT head_Id,head_Name,slNo FROM tbPrimary_Group order by Head_Name";
			lblPrimaryCat.setValue("Primary Category:");
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("2"))
		{
			sql = "SELECT head_Id,head_Name,slNo FROM tbPrimary_Group WHERE substring(head_Id,1,1) = 'A' order by head_Name";
			lblPrimaryCat.setValue("Asset Category:");
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("3"))
		{
			sql = "SELECT head_Id,head_Name,slNo FROM tbPrimary_Group WHERE substring(head_Id,1,1) = 'L' order by head_Name";
			lblPrimaryCat.setValue("Liabilities Category:");
		}
		else if(primaryCat.getValue().toString().equalsIgnoreCase("4"))
		{
			sql = "SELECT head_Id,head_Name,slNo FROM tbPrimary_Group WHERE substring(head_Id,1,1) = 'I' order by head_Name";
			lblPrimaryCat.setValue("Income Category:");
		}
		else
		{ //if(group.getValue().toString().equalsIgnoreCase("5"))
			sql = "SELECT head_Id,head_Name,slNo FROM tbPrimary_Group WHERE substring(head_Id,1,1) = 'E' order by head_Name";
			lblPrimaryCat.setValue("Expenses Category:");
		}
		try
		{
			Transaction tx = null;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List group = session.createSQLQuery(sql).list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				mainCat.addItem(element[0].toString());
				mainCat.setItemCaption(element[0].toString(), element[1].toString());
			}
			mainCat.setNullSelectionAllowed(true);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void primaryCatSelect()
	{
		mainCat.removeAllItems();
		groupList.removeAllItems();
		subGroupList.removeAllItems();
		ledgerList.removeAllItems();
		mainCatInitialise();
		initialise();
	}

	private void mainCatSelect()
	{
		if(mainCat.getValue()!=null)
		{
			groupList.removeAllItems();
			subGroupList.removeAllItems();
			ledgerList.removeAllItems();

			grouListInitialise();
			ledgerInitialise();

			if(!mainCat.getValue().equals(""))
			{
				slInitialise(true);
				ledgerName.setValue("");
				ledgerName.setEnabled(false);

				chkCompany.setValue(false);
				chkCompany.setEnabled(false);

				crdAmt.setValue("");
				crdAmt.setEnabled(false);

				debAmt.setValue("");
				debAmt.setEnabled(false);

				opyear.setValue("");
				opyear.setEnabled(false);
			}
			else
			{
				initialise();
			}
		}
	}

	private void grouListInitialise()
	{
		try
		{
			Transaction tx = null;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List group = session.createSQLQuery("SELECT group_Id,group_Name FROM TbMain_Group WHERE head_Id = '"+ mainCat.getValue() +"' order by group_Name").list();

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
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void groupListSelect()
	{
		if(groupList.getValue()!=null)
		{
			subGroupList.removeAllItems();
			ledgerList.removeAllItems();
			subGroupListInitialise();
			ledgerInitialise();
		}
	}

	private void subGroupListInitialise()
	{
		try
		{
			Transaction tx = null;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List group = session.createSQLQuery("SELECT sub_Group_Id,sub_Group_Name FROM TbSub_Group WHERE group_Id = '"+ groupList.getValue() +"' order by sub_Group_Name").list();

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
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void ledgerInitialise()
	{
		String sql = "";

		if(subGroupList.getValue() != null)
		{
			sql = "SELECT ledger_Id,ledger_Name,parent_Id, companyId FROM TbLedger WHERE  parent_Id = '"+subGroupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')  order by ledger_Name";
		}
		else if(groupList.getValue() != null)
		{
			sql = "SELECT ledger_Id,ledger_Name,parent_Id, companyId FROM TbLedger WHERE  parent_Id = '"+groupList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')   order by ledger_Name";
		}
		else
		{
			sql = "SELECT ledger_Id,ledger_Name,parent_Id, companyId FROM TbLedger WHERE  parent_Id = '"+mainCat.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')   order by ledger_Name";
		}
		try
		{
			Transaction tx = null;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List group = session.createSQLQuery(sql).list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				hm.put(element[0].toString(), element[3].toString());

				ledgerList.addItem(element[0].toString());
				ledgerList.setItemCaption(element[0].toString(), element[1].toString());
			}
			ledgerList.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void subGroupListSelect()
	{
		if(subGroupList.getValue()!=null)
		{
			ledgerList.removeAllItems();
			ledgerInitialise();
		}
	}

	private void ledgerListSelect()
	{
		if(ledgerList.getValue()!=null)
		{
			ledgerName.setValue(ledgerList.getItemCaption(ledgerList.getValue()));
			debAmt.setValue("");
			crdAmt.setValue("");
			opyear.setValue("");

			Transaction tx = null;

			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				Iterator iter = session.createSQLQuery("SELECT drAmount,crAmount,op_year FROM TbLedger_Op_Balance  WHERE ledger_Id = '"+ledgerList.getValue()+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') and  op_year = '"+dtfYMD.format(sessionBean.getFiscalOpenDate()).substring(0, 4)+"'").list().iterator();

				if(iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();

					chkCompany.setValue(hm.get(ledgerList.getValue().toString()).equals("0")?true:false);
					debAmt.setValue(cms.setComma(Double.valueOf(fmt.format(element[0]))));
					crdAmt.setValue(cms.setComma(Double.valueOf(fmt.format(element[1]))));
					opyear.setValue(element[2].toString());

					//	opyear.setValue(dtfYMD.format(sessionBean.getFiscalOpenDate()).substring(0, 4));
				}
			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally
			{
				tx.commit();
			}
		}
		else
		{
			ledgerName.setValue("");
			chkCompany.setValue(false);
			debAmt.setValue("");
			crdAmt.setValue("");
			opyear.setValue("");
		}
	}

	private boolean isFixedAsset(int i)
	{
		if(mainCat.getValue().toString().equalsIgnoreCase("A1"))
		{			
			if(i==0)
			{
				showNotification("","You can not create ledger for fixed asset.",Notification.TYPE_WARNING_MESSAGE);
			}
			else
			{
				showNotification("","Fixed asset ledger can not be update from here..",Notification.TYPE_WARNING_MESSAGE);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private void slInitialise(boolean t)
	{
		isUpdate = false;;
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		primaryCat.setEnabled(t);
		mainCat.setEnabled(t);
		groupList.setEnabled(t);
		subGroupList.setEnabled(t);
		ledgerList.setEnabled(t);
	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(ledgerName.getValue().toString().trim().length()>0)
			{
				slInitialise(false);
				ledgerName.setEnabled(true);
				chkCompany.setEnabled(true);
				crdAmt.setEnabled(true);
				debAmt.setEnabled(true);
				opyear.setEnabled(true);
				isUpdate = true;
			}
			else
			{
				showNotification("Update Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean isExistsLedgerName(){

		Session session=null;
		Transaction tx=null;
		try
		{
			session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			Iterator iter = session.createSQLQuery("select Ledger_Name from tbLedger where Ledger_Name like '"+ledgerName.getValue()+"'").list().iterator();

			if(iter.hasNext()) 
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			tx.commit();
		}

		return false;
	}
	private boolean isValid()
	{
		if(!ledgerName.getValue().toString().isEmpty())
		{
			if(!mainCat.getValue().toString().equalsIgnoreCase("") && !mainCat.getValue().toString().equalsIgnoreCase("") &&
					!mainCat.getValue().toString().equalsIgnoreCase("A5") &&  !mainCat.getValue().toString().equalsIgnoreCase("I1"))
			{
				if(isUpdate){
					if(Double.valueOf("0"+crdAmt.getValue().toString())<0||Double.valueOf("0"+debAmt.getValue().toString())<0)
					{
						showNotification("","Amount can not be negative.",Notification.TYPE_WARNING_MESSAGE);
						return false;
					}
					else if(Double.valueOf("0"+crdAmt.getValue().toString())>0&&Double.valueOf("0"+debAmt.getValue().toString())>0)
					{
						showNotification("","Credit & Debit both amount can not be greater then zero.",Notification.TYPE_WARNING_MESSAGE);
						return false;
					}
				}
				else{
					if(!isExistsLedgerName()){
						if(Double.valueOf("0"+crdAmt.getValue().toString())<0||Double.valueOf("0"+debAmt.getValue().toString())<0)
						{
							showNotification("","Amount can not be negative.",Notification.TYPE_WARNING_MESSAGE);
							return false;
						}
						else if(Double.valueOf("0"+crdAmt.getValue().toString())>0&&Double.valueOf("0"+debAmt.getValue().toString())>0)
						{
							showNotification("","Credit & Debit both amount can not be greater then zero.",Notification.TYPE_WARNING_MESSAGE);
							return false;
						}
					}
					else{
						showNotification("Warning!","Ledger Name Already Exists",Notification.TYPE_WARNING_MESSAGE);
						ledgerName.focus();
						return false;
					}
				}
			}
			else
			{
				showNotification("Warning!","Ledger create is not available in this head",Notification.TYPE_WARNING_MESSAGE);
				mainCat.focus();
				return false;
			}
		}
		else
		{
			showNotification("Warning!","Provide Ledger Name",Notification.TYPE_WARNING_MESSAGE);
			ledgerName.focus();
			return false;
		}
		return true;
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION,
					"Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
					new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));

			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(!isfiscalClosed())
						{
							button.btnSave.setEnabled(false);
							updateData();
							button.btnNew.focus();
						}
						else
						{
							showNotification("Failed","Fiscal Year is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
						}
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION,
					"Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
					new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));

			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						if(!isfiscalClosed())
						{
							button.btnSave.setEnabled(false);
							insertData();
							button.btnNew.focus();
						}
						else
						{
							showNotification("Failed","Fiscal Year is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
							ledgerInitialise();
							ledgerName.setEnabled(false);
							chkCompany.setEnabled(false);
							crdAmt.setEnabled(false);
							debAmt.setEnabled(false);
							opyear.setEnabled(false);
						}
					}
				}
			});
		}
	}

	private boolean isfiscalClosed()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();

		Iterator iters = session.createSQLQuery("Select Ledger_Id from tbLedger where Creation_year = '"+opyear.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' and Creation_year In (Select year(op_date) from tbFiscal_Year where isclosed = 1)").list().iterator();

		if(iters.hasNext())
		{
			//			showNotification("","Unable to delete this ledger because this ledger is not created in current fiscal year.",Notification.TYPE_ERROR_MESSAGE);
			return true;
		}
		else
			return false;	
	}

	private void insertData()
	{
		if(opyear.getValue().toString().length()==4) 
		{
			if(sessionBean.isSubmitable())
			{
				double d = Double.valueOf("0"+debAmt.getValue().toString());
				double c = Double.valueOf("0"+crdAmt.getValue().toString());

				if((d>=0 && c==0)||(d==0 && c>=0))
				{
					Transaction tx = null;

					try
					{
						Session session = SessionFactoryUtil.getInstance().getCurrentSession();
						tx = session.beginTransaction();

						String parentId = "";
						String createFrom = "";

						if(subGroupList.getValue() != null)
						{
							parentId = subGroupList.getValue().toString();
							createFrom = mainCat.getValue()+"-"+groupList.getValue()+"-"+subGroupList.getValue();
						}
						else if(groupList.getValue() != null)
						{
							parentId = groupList.getValue().toString();
							createFrom = mainCat.getValue()+"-"+groupList.getValue();
						}
						else
						{
							parentId = mainCat.getValue().toString();
							createFrom = mainCat.getValue().toString();
						}

						String chk = chkCompany.getValue().toString() == "true" ?"0":sessionBean.getCompanyId().toString();					
						String sql = "INSERT INTO tbLedger(Ledger_Id,Ledger_Name,Creation_Year,Parent_Id,Create_From,userId,userIp,entryTime, companyid) VALUES((SELECT '"+
								mainCat.getValue().toString().substring(0,1)+"L'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer))+1,101) AS varchar)"+
								" FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = '"+mainCat.getValue().toString().substring(0,1)+
								"'),'"+ledgerName.getValue()+"','"+ opyear.getValue() +"','"+parentId+"','"+createFrom+"','"+
								sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ chk +"')";
						session.createSQLQuery(sql).executeUpdate();

						//	System.out.println(sql);

						session.createSQLQuery("INSERT INTO tbLedger_Op_Balance(Ledger_Id,DrAmount,CrAmount,Op_Year,userId,userIp,entryTime, companyId) VALUES((SELECT '"+
								mainCat.getValue().toString().substring(0,1)+"L'+CAST(ISNULL(max(cast(substring(Ledger_Id,3,len(Ledger_Id)) AS integer)),101) AS varchar)"+
								" FROM tbLedger WHERE SUBSTRING(Ledger_Id,1,1) = '"+mainCat.getValue().toString().substring(0,1)+"'),0"+debAmt.getValue().toString()+",0"+
								crdAmt.getValue().toString()+",'"+opyear.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ chk +"')").executeUpdate();

						tx.commit();

						ledgerInitialise();

						ledgerName.setEnabled(false);
						chkCompany.setEnabled(false);
						crdAmt.setEnabled(false);
						debAmt.setEnabled(false);
						opyear.setEnabled(false);
						showNotification("All Information saved successfully.");
					}
					catch(Exception exp)
					{
						showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
						tx.rollback();
					}
				}
				else
				{
					showNotification("Submit Failed","Balance information is not correct. Please verify the balance information.",Notification.TYPE_ERROR_MESSAGE);
				}
			}
			else
			{
				showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Insert Failed","Please Input Ledger Opening Year",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void  deleteBtnAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			if(opyear.getValue().toString()!="")
			{
				Transaction tx = null;

				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();

					String chk = chkCompany.getValue().toString() == "true" ? "0" : sessionBean.getCompanyId().toString();
					String sql = "UPDATE tbLedger SET Ledger_Name = '"+ ledgerName.getValue() +"', companyId = '"+ chk +"' WHERE Ledger_Id = '"+ ledgerList.getValue() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";

					session.createSQLQuery(sql).executeUpdate();
					session.createSQLQuery("UPDATE tbLedger_Op_Balance SET DrAmount = 0"+debAmt.getValue().toString()+",CrAmount = 0"+crdAmt.getValue().toString()+", companyId = '"+ chk +"'  WHERE Ledger_Id = '"+ ledgerList.getValue() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' and op_year = '"+opyear.getValue()+"'").executeUpdate();

					tx.commit();

					showNotification("All Information update successfully.");

					ledgerInitialise();

					ledgerName.setEnabled(false);
					chkCompany.setEnabled(false);
					crdAmt.setEnabled(false);
					debAmt.setEnabled(false);
					opyear.setEnabled(false);
					//groupList.setItemCaption(groupList.getValue().toString(), groupName.getValue().toString());
				}
				catch(Exception exp)
				{
					showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}
			else
			{
				showNotification("Update Failed","No Balance found for present fiscal year.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean isthereData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iters = session.createSQLQuery("SELECT ledger_id FROM tbledger WHERE Ledger_id = '"+ledgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' and Creation_year <> year('"+sessionBean.getFiscalOpenDate()+"')").list().iterator();

			if(iters.hasNext())
			{
				showNotification("","Unable to delete this ledger because this ledger is not created in current fiscal year.",Notification.TYPE_ERROR_MESSAGE);
				return true;
			}
			else
			{
				//Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				//Transaction tx = session.beginTransaction();
				Iterator iter = session.createSQLQuery("SELECT ledger_id FROM Voucher1 WHERE Ledger_id = '"+ledgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

				if(iter.hasNext())
				{
					showNotification("","Unable to delete this ledger because already data inserted for this ledger.",Notification.TYPE_ERROR_MESSAGE);
					return true;
				}
				else
				{
					List list = session.createSQLQuery("SELECT ISNULL(SUM(DrAmount),0) as drAmt,ISNULL(SUM(CrAmount),0) as crAmt "+
							"FROM tbLedger_Op_Balance WHERE Ledger_Id = '"+ledgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list();

					Object[] element = (Object[]) list.iterator().next();

					if(Double.valueOf(element[0].toString())==0 && Double.valueOf(element[1].toString())==0)
					{
						return false;
					}
					else
					{
						showNotification("","Unable to delete this ledger because already inserted opeing balance for this ledger.",Notification.TYPE_ERROR_MESSAGE);
						return true;	
					}
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return true;
		}
	}

	private void deleteData()
	{
		if(sessionBean.isDeleteable())
		{
			if(ledgerName.getValue().toString().trim().length()>0)
			{
				Transaction tx = null;
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();

					String sql = "Delete FROM tbLedger WHERE Ledger_Id = '"+ledgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";

					session.createSQLQuery(sql).executeUpdate();
					session.createSQLQuery("DELETE FROM tbLedger_Op_Balance WHERE Ledger_Id = '"+ledgerList.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

					tx.commit();
					showNotification("Desired Information delete successfully.");

					slInitialise(true);

					ledgerList.removeItem(ledgerList.getValue());

					ledgerName.setValue("");
					chkCompany.setValue(false);
					crdAmt.setValue("");
					debAmt.setValue("");

					ledgerName.setEnabled(false);
					chkCompany.setEnabled(false);
					crdAmt.setEnabled(false);
					debAmt.setEnabled(false);

					isUpdate = false;
				}
				catch(Exception exp)
				{
					showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}
			}
			else
			{
				showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
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
		setHeight("450px");

		// lblPrimaryCat
		lblPrimaryCat = new Label();
		lblPrimaryCat.setImmediate(true);
		lblPrimaryCat.setWidth("-1px");
		lblPrimaryCat.setHeight("-1px");
		lblPrimaryCat.setValue("Primary Category:");
		mainLayout.addComponent(lblPrimaryCat, "top:20.0px;left:20.0px;");

		// primaryCat
		primaryCat  = new NativeSelect();
		primaryCat.setImmediate(true);
		primaryCat.setWidth("150px");
		primaryCat.addItem("1");
		primaryCat.setItemCaption("1", "All");
		primaryCat.addItem("2");
		primaryCat.setItemCaption("2", "Assets");
		primaryCat.addItem("5");
		primaryCat.setItemCaption("5", "Expenses");
		primaryCat.addItem("4");
		primaryCat.setItemCaption("4", "Income");
		primaryCat.addItem("3");
		primaryCat.setItemCaption("3", "Liabilities");
		
		
		primaryCat.setImmediate(true);
		primaryCat.setValue("1");
		primaryCat.setNullSelectionAllowed(false);
		mainLayout.addComponent(primaryCat, "top:42.0px;left:20.0px;");

		// lblMainCat
		lblMainCat = new Label();
		lblMainCat.setImmediate(true);
		lblMainCat.setWidth("-1px");
		lblMainCat.setHeight("-1px");
		lblMainCat.setValue("Main Category:");
		mainLayout.addComponent(lblMainCat, "top:67.0px;left:20.0px;");

		// mainCat
		mainCat  = new NativeSelect();
		mainCat.setWidth(cww);
		mainCat.setImmediate(true);
		mainLayout.addComponent(mainCat, "top:87.0px;left:20.0px;");

		// lblGroupList
		lblGroupList = new Label();
		lblGroupList.setImmediate(true);
		lblGroupList.setWidth("-1px");
		lblGroupList.setHeight("-1px");
		lblGroupList.setValue("Group List:");
		mainLayout.addComponent(lblGroupList, "top:117.0px;left:20.0px;");

		// groupList
		groupList   = new NativeSelect();
		groupList.setWidth(cww);
		groupList.setImmediate(true);
		mainLayout.addComponent(groupList, "top:137.0px;left:20.0px;");

		// btnGroup
		btnGroup = new NativeButton();
		btnGroup.setCaption("");
		btnGroup.setImmediate(true);
		btnGroup.setWidth("28px");
		btnGroup.setHeight("24px");
		btnGroup.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnGroup,"top:137.0px;left:295.0px;");

		// lblSubGroupList
		lblSubGroupList = new Label();
		lblSubGroupList.setImmediate(true);
		lblSubGroupList.setWidth("-1px");
		lblSubGroupList.setHeight("-1px");
		lblSubGroupList.setValue("Sub-Group List:");
		mainLayout.addComponent(lblSubGroupList, "top:167.0px;left:20.0px;");

		// subGroupList
		subGroupList  = new NativeSelect();
		subGroupList.setWidth(cww);
		subGroupList.setImmediate(true);
		mainLayout.addComponent(subGroupList, "top:187.0px;left:20.0px;");

		// btnSubGroup
		btnSubGroup = new NativeButton();
		btnSubGroup.setCaption("");
		btnSubGroup.setImmediate(true);
		btnSubGroup.setWidth("28px");
		btnSubGroup.setHeight("24px");
		btnSubGroup.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnSubGroup,"top:187.0px;left:295.0px;");

		// lblLedgerList
		lblLedgerList = new Label();
		lblLedgerList.setImmediate(true);
		lblLedgerList.setWidth("-1px");
		lblLedgerList.setHeight("-1px");
		lblLedgerList.setValue("Ledger List:");
		mainLayout.addComponent(lblLedgerList, "top:217.0px;left:20.0px;");

		// ledgerList
		ledgerList = new ListSelect();
		ledgerList.setImmediate(true);
		ledgerList.setHeight("-1px");
		ledgerList.setWidth(cw);
		ledgerList.setNullSelectionAllowed(false);
		mainLayout.addComponent(ledgerList, "top:237.0px;left:20.0px;");

		// lblLed
		lblLed.setImmediate(true);
		lblLed.setWidth("-1px");
		lblLed.setHeight("-1px");
		mainLayout.addComponent(lblLed, "top:112.0px;left:340.0px;");

		// lblLedgerName
		lblLedgerName = new Label();
		lblLedgerName.setImmediate(true);
		lblLedgerName.setWidth("-1px");
		lblLedgerName.setHeight("-1px");
		lblLedgerName.setValue("Ledger Name:");
		mainLayout.addComponent(lblLedgerName, "top:142.0px;left:340.0px;");

		// lblLed
		ledgerName = new TextField();
		ledgerName.setImmediate(true);
		ledgerName.setWidth("300px");
		ledgerName.setHeight("-1px");
		mainLayout.addComponent(ledgerName, "top:142.0px;left:425.0px;");

		// chkCompany
		chkCompany.setImmediate(true);
		chkCompany.setHeight("-1px");
		chkCompany.setWidth("220px");		
		chkCompany.setVisible(false);
		mainLayout.addComponent(chkCompany, "top:150.0px;left:425.0px;");

		// lblOpen
		lblOpen.setImmediate(true);
		lblOpen.setWidth("-1px");
		lblOpen.setHeight("-1px");
		mainLayout.addComponent(lblOpen, "top:222.0px;left:340.0px;");

		// lblDebAmt
		lblDebAmt = new Label();
		lblDebAmt.setImmediate(true);
		lblDebAmt.setWidth("-1px");
		lblDebAmt.setHeight("-1px");
		lblDebAmt.setValue("Debit Amount:");
		mainLayout.addComponent(lblDebAmt, "top:252.0px;left:340.0px;");

		// debAmt
		debAmt = new AmountCommaSeperator();
		debAmt.setImmediate(true);
		debAmt.setWidth("-1px");
		debAmt.setHeight("-1px");
		debAmt.setStyleName("fright");
		mainLayout.addComponent(debAmt, "top:272.0px;left:340.0px;");

		// lblCrdAmt
		lblCrdAmt = new Label();
		lblCrdAmt.setImmediate(true);
		lblCrdAmt.setWidth("-1px");
		lblCrdAmt.setHeight("-1px");
		lblCrdAmt.setValue("Credit Amount:");
		mainLayout.addComponent(lblCrdAmt, "top:252.0px;left:505.0px;");

		// crdAmt
		crdAmt = new AmountCommaSeperator();
		crdAmt.setImmediate(true);
		crdAmt.setWidth("-1px");
		crdAmt.setHeight("-1px");
		crdAmt.setStyleName("fright");
		mainLayout.addComponent(crdAmt, "top:272.0px;left:505.0px;");

		// lblOpyear
		lblOpyear = new Label();
		lblOpyear.setImmediate(true);
		lblOpyear.setWidth("-1px");
		lblOpyear.setHeight("-1px");
		lblOpyear.setValue("Opening Year");
		mainLayout.addComponent(lblOpyear, "top:252.0px;left:670.0px;");

		// crdAmt
		opyear  = new TextField();
		opyear.setImmediate(true);
		opyear.setWidth("-1px");
		opyear.setHeight("-1px");
		opyear.setStyleName("fright");
		mainLayout.addComponent(opyear, "top:272.0px;left:670.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:330.0px;left:350.0px;");

		return mainLayout;
	}
}
