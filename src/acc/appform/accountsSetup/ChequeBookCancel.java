package acc.appform.accountsSetup;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class ChequeBookCancel extends Window 
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private boolean isUpdate = false;

	private Label lblBankAccount;
	private Label lblBookNo;
	private Label lblDate;

	private ComboBox cmbBankAccount;

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private ComboBox cmbFolioNo;
	private Label lblReason;
	private TextField txtReason;
	/*private Label lblFolioTo;
	private TextField txtFolioTo;*/

	String rId;
	TextRead id = new TextRead();

	boolean check = false; 

	private DateField date;

	private NativeButton btnbankHead;
	
	private String findChequeNo = "";

	CommonButton button = new CommonButton("New", "Save", "", "", "Refresh", "0", "", "", "", "Exit");//Find

	public ChequeBookCancel(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("CHEQUE BOOK CANCELLATION :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		button.btnNew.focus();

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();

		buttonShortCut();

		bankHeadIni();

		btnIni(true);
		txtEnable(false);

		Component allComp[] = {cmbBankAccount,cmbFolioNo,date,txtReason,button.btnNew,button.btnEdit,
				button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};

		new FocusMoveByEnter(this,allComp);

		setButtonShortCut();
	}

	private void buttonActionAdd()
	{
		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				cmbFolioNo.removeAllItems();
				newBtnAction();
			}
		});

		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				/*if(!updateCheck())
				{*/
				if(cmbBankAccount.getValue()!=null)
				{
					if(!id.getValue().toString().isEmpty())
					{
						isUpdate = true;
						updateBtnAction();
					}
					else
					{
						showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
				}
				/*			}
				else
				{
					showNotification("Warning","Edit Not permitted",Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
		});

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbBankAccount.getValue()!=null)
				{
					if(cmbFolioNo.getValue()!=null)
					{
						if(!txtReason.getValue().toString().isEmpty())
						{
							check = false;
							if(!duplicateCheck())
							{
								saveBtnAction();
							}
							else if(!check && duplicateCheck())
							{
								showNotification("Warning","Transaction already Exits",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning!","Provide reason of cancellation",Notification.TYPE_WARNING_MESSAGE);
							txtReason.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide Folio No",Notification.TYPE_WARNING_MESSAGE);
						cmbFolioNo.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Bank AC Name",Notification.TYPE_WARNING_MESSAGE);
					cmbBankAccount.focus();
				}
			}
		});

		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				txtEnable(false);
				txtClear();
			}
		});
		/*
		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!updateCheck())
				{
				if(cmbBankAccount.getValue()!=null)
				{
					deleteBtnAction();
				}
				else
				{
					showNotification("Warning","There are no data",Notification.TYPE_WARNING_MESSAGE);
				}
								}
				else
				{
					showNotification("Warning","Delete Not permitted",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		 */
		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = true;
				findBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		btnbankHead.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				bankHeadLink();
			}
		});

		cmbBankAccount.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbBankAccount.getValue()!=null)
				{
					if(isUpdate)
					{
						addChequeNo();
					}
					else
					{
						addChequeNoAll();
					}
				}
			}
		});
	}

	private void addChequeNo()
	{
		cmbFolioNo.removeAllItems();
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " Select * from tbChequeBook where ledgerId = '"+cmbBankAccount.getValue().toString()+"'" +
					" and (status = 'NO' or status='YES') ";

			System.out.println(sql);

			List bh = session.createSQLQuery(sql).list();

			for (Iterator iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbFolioNo.addItem(element[5].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void addChequeNoAll()
	{
		cmbFolioNo.removeAllItems();
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " Select * from tbChequeBook where ledgerId = '"+cmbBankAccount.getValue().toString()+"'" +
					" and (status = 'NO' or status='YES' or status='CANCEL') ";

			System.out.println(sql);

			List bh = session.createSQLQuery(sql).list();

			for (Iterator iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbFolioNo.addItem(element[5].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public boolean duplicateCheck() 
	{/*	
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			int sub = Integer.parseInt(folioTo.subtract(folioFrom).toString());

			if(!folioTo.subtract(folioFrom).toString().contains("-"))
			{
				for(int i = 0;i<=sub;i++)
				{
					BigInteger val = folioFrom.add(BigInteger.valueOf(i));

					String query = " Select Cheque_No from vwChequeDetails where Cheque_No = '"+val+"' ";
					Iterator iter = session.createSQLQuery(query).list().iterator();

					if (iter.hasNext())
					{
						return true;
					}
				}
			}
			else
			{
				showNotification("Warning","Check Folio No");
				check  = true;
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		check = false;*/
		return false;
	}

	private boolean updateCheck()
	{
		boolean autoCode = false;

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select COUNT(id) as a from tbChequeBook where id = '"+id.getValue()+"' and (status = 'YES' or status = 'CANCEL') ";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				if(Integer.parseInt(iter.next().toString())==0)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void deleteBtnAction()
	{
		MessageBox mb = new MessageBox(this.getParent().getWindow(),
				"Are you sure?", MessageBox.Icon.QUESTION,
				"Do you want to delete  information?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,
						"Yes"), new MessageBox.ButtonConfig(
								MessageBox.ButtonType.NO, "No"));

		mb.show(new EventListener() 
		{
			public void buttonClicked(ButtonType buttonType) 
			{
				if (buttonType == ButtonType.YES) 
				{	
					Transaction tx = null;
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();

					tx = session.beginTransaction();

					deleteData(session,tx);

					tx.commit();

					isUpdate = false;

					txtEnable(false);
					btnIni(true);
					txtClear();

					showNotification("Successfully","Delete data",Notification.TYPE_HUMANIZED_MESSAGE);
				}
			}
		});
	}

	private void updateBtnAction()
	{
		btnIni(false);
		txtEnable(true);
	}

	private void findBtnAction()
	{
		Window win = new FindChequeCancel(sessionBean,id);

		win.setStyleName("cwindow");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(id.getValue().toString().length()>0)
				{
					System.out.println("From find : "+id.getValue());

					isUpdate = true;
					txtClear();

					findSetValue();
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findSetValue()
	{
		try
		{
			Transaction tx = null;

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select vBankLedgerId,vFolioNo,dCancelDate,vReason from tbChequeCancel where iAutoId = '"+id.getValue().toString()+"' ";

			System.out.println(query);

			List led = session.createSQLQuery(query).list();

			for(Iterator iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBankAccount.setValue(element[0]);
				cmbFolioNo.setValue(element[1]);
				date.setValue(element[2]);
				txtReason.setValue(element[3].toString());
				
				findChequeNo = element[1].toString();
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						isUpdate = true;

						updateData();

						txtEnable(false);
						btnIni(true);
						txtClear();
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
					}
				}
			});
		}
	}

	private boolean deleteData(Session session,Transaction tx)
	{
		try
		{
			String sql = "" ;

			sql = " delete tbChequeBook where id = '"+id.getValue()+"' ";

			System.out.println(sql);

			session.createSQLQuery(sql).executeUpdate();

			return true;
		}
		catch(Exception exp)
		{
			getWindow().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	public String autoId() 
	{
		String autoCode = "";

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select cast(isnull(max(cast(replace(id, '', '')as int))+1, 1)as varchar) from tbChequeBook ";

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

	private void insertData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " update tbChequeBook set status = 'CANCEL', date = '"+dtfYMD.format(date.getValue())+"'" +
					" where folioNo = '"+cmbFolioNo.getValue().toString()+"' ";

			session.createSQLQuery(sql).executeUpdate();

			String sqlInsert = " insert into tbChequeCancel values (" +
					" '"+cmbBankAccount.getValue().toString()+"'," +
					" '"+cmbBankAccount.getItemCaption(cmbBankAccount.getValue())+"'," +
					" (select bookNo from tbChequeBook where ledgerId = '"+cmbBankAccount.getValue().toString()+"'" +
					" and folioNo='"+cmbFolioNo.getValue().toString()+"' )," +
					" '"+cmbFolioNo.getValue().toString()+"'," +
					" '"+dtfYMD.format(date.getValue())+"'," +
					" '"+txtReason.getValue().toString()+"'," +
					" '"+(isUpdate?"Update":"New")+"'," +
					" '"+sessionBean.getUserName()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP)";

			session.createSQLQuery(sqlInsert).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information save successfully.");

			txtEnable(false);
			btnIni(true);
			txtClear();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Errora",exp+"",Notification.TYPE_ERROR_MESSAGE);
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

			String sql = " update tbChequeBook set status = 'CANCEL', date = '"+dtfYMD.format(date.getValue())+"'" +
					" where folioNo = '"+cmbFolioNo.getValue().toString()+"' ";

			session.createSQLQuery(sql).executeUpdate();

			String sqlInsert = " insert into tbChequeCancel values (" +
					" '"+cmbBankAccount.getValue().toString()+"'," +
					" '"+cmbBankAccount.getItemCaption(cmbBankAccount.getValue())+"'," +
					" (select bookNo from tbChequeBook where ledgerId = '"+cmbBankAccount.getValue().toString()+"'" +
					" and folioNo='"+cmbFolioNo.getValue().toString()+"' )," +
					" '"+cmbFolioNo.getValue().toString()+"'," +
					" '"+dtfYMD.format(date.getValue())+"'," +
					" '"+txtReason.getValue().toString()+"'," +
					" '"+(isUpdate?"Update":"New")+"'," +
					" '"+sessionBean.getUserName()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" CURRENT_TIMESTAMP)";

			session.createSQLQuery(sqlInsert).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information save successfully.");

			txtEnable(false);
			btnIni(true);
			txtClear();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Errora",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void bankHeadLink()
	{
		Window win = new LedgerCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				bankHeadIni();
			}
		});

		this.getParent().addWindow(win);
	}

	private void newBtnAction()
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
	}

	private void txtClear()
	{
		cmbBankAccount.setValue(null);
		cmbFolioNo.setValue(null);
		date.setValue(new java.util.Date());
		txtReason.setValue("");
		isUpdate = false;
		
		findChequeNo = "";
	}

	private void bankHeadIni()
	{
		cmbBankAccount.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();

			for (Iterator iter = bh.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				cmbBankAccount.addItem(element[0]);
				cmbBankAccount.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void buttonShortCut()
	{
		button.btnNew.setClickShortcut(KeyCode.N, ModifierKey.ALT);
		button.btnEdit.setClickShortcut(KeyCode.U, ModifierKey.ALT);
		button.btnSave.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		button.btnRefresh.setClickShortcut(KeyCode.C, ModifierKey.ALT);
		button.btnDelete.setClickShortcut(KeyCode.D, ModifierKey.ALT);
		button.btnFind.setClickShortcut(KeyCode.F, ModifierKey.ALT);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
		button.btnPreview.setEnabled(t);
		button.btnChequePrint.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{
		cmbBankAccount.setEnabled(t);
		cmbFolioNo.setEnabled(t);
		date.setEnabled(t);
		btnbankHead.setEnabled(t);
		txtReason.setEnabled(t);
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(button.btnNew, KeyCode.N, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnEdit, KeyCode.U, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnSave, KeyCode.S, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnRefresh, KeyCode.C, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnDelete, KeyCode.D, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnFind, KeyCode.F, ModifierKey.ALT,ModifierKey.SHIFT));
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
		setWidth("620px");
		setHeight("250px");

		// lblBankAccount
		lblBankAccount = new Label();
		lblBankAccount.setImmediate(true);
		lblBankAccount.setWidth("-1px");
		lblBankAccount.setHeight("-1px");
		lblBankAccount.setValue("Bank AC Name :");
		mainLayout.addComponent(lblBankAccount, "top:22.0px;left:100.0px;");

		// cmbBankAccount
		cmbBankAccount = new ComboBox();
		cmbBankAccount.setImmediate(true);
		cmbBankAccount.setWidth("-1px");
		cmbBankAccount.setHeight("-1px");
		cmbBankAccount.setWidth("270px");
		cmbBankAccount.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbBankAccount, "top:22.0px;left:210.0px;");

		// btnbankHead
		btnbankHead = new NativeButton();
		btnbankHead.setCaption("");
		btnbankHead.setImmediate(true);
		btnbankHead.setWidth("28px");
		btnbankHead.setHeight("24px");
		btnbankHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnbankHead,"top:22.0px;left:485.0px;");

		// lblBookNo
		lblBookNo = new Label("Folio No :");
		lblBookNo.setImmediate(true);
		lblBookNo.setWidth("-1px");
		lblBookNo.setHeight("-1px");
		mainLayout.addComponent(lblBookNo, "top:47.0px;left:100.0px;");

		// cmbFolioNo
		cmbFolioNo = new ComboBox();
		cmbFolioNo.setImmediate(true);
		cmbFolioNo.setWidth("130px");
		cmbFolioNo.setHeight("-1px");
		mainLayout.addComponent(cmbFolioNo, "top:47.0px;left:210.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:72.0px;left:100.0px;");

		// date
		date = new DateField();
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date,"top:72.0px;left:210.0px;");

		// lblReason
		lblReason = new Label("Reason :");
		lblReason.setImmediate(true);
		lblReason.setWidth("-1px");
		lblReason.setHeight("-1px");
		mainLayout.addComponent(lblReason, "top:97.0px;left:100.0px;");

		// txtReason
		txtReason = new TextField();
		txtReason.setImmediate(true);
		txtReason.setWidth("270px");
		txtReason.setHeight("44px");
		txtReason.setSecret(false);
		mainLayout.addComponent(txtReason, "top:97.0px;left:210.0px;");

		/*// lblFolioTo
		lblFolioTo = new Label();
		lblFolioTo.setImmediate(true);
		lblFolioTo.setWidth("-1px");
		lblFolioTo.setHeight("-1px");
		lblFolioTo.setValue("Folio To :");
		mainLayout.addComponent(lblFolioTo, "top:122.0px;left:100.0px;");

		// txtFolioTo
		txtFolioTo = new TextField();
		txtFolioTo.setImmediate(true);
		txtFolioTo.setWidth("180px");
		txtFolioTo.setHeight("-1px");
		txtFolioTo.setSecret(false);
		mainLayout.addComponent(txtFolioTo, "top:122.0px;left:210.0px;");*/

		// common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:160.0px;left:100.0px;");

		return mainLayout;
	}

}
