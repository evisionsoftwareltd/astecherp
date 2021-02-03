package acc.appform.accountsSetup;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
import com.common.share.TextRead;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;

@SuppressWarnings("serial")
public class ChequeBookEntry extends Window 
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private boolean isUpdate = false;

	private Label lblBankAccount;
	private Label lblBookNo;
	private Label lblDate;
	private Label lblFolioFrom;
	private Label lblFolioTo;

	private ComboBox cmbBankAccount;

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private TextRead txtBookNo;
	private AmountField txtFolioFrom;
	private AmountField txtFolioTo;

	String rId;
	TextRead id = new TextRead();

	private DateField date;

	private NativeButton btnbankHead;

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");

	public ChequeBookEntry(SessionBean sessionBean) 
	{
		this.sessionBean = sessionBean;
		this.setCaption("CHEQUE BOOK ENTRY :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		button.btnNew.focus();

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();

		buttonShortCut();

		bankHeadIni();

		btnIni(true);
		txtEnable(false);

		Component allComp[] = {cmbBankAccount,txtBookNo,date,txtFolioFrom,txtFolioTo,button.btnNew,
				button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,allComp);

		setButtonShortCut();
	}

	private void buttonActionAdd()
	{
		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
			}
		});

		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!updateCheck())
				{
					if(cmbBankAccount.getValue()!=null)
					{
						if(!id.getValue().toString().isEmpty())
						{
							isUpdate = true;
							updateBtnAction();
						}
						else
						{
							showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning!","There are no Data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Cheque book is in using.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbBankAccount.getValue()!=null)
				{
					if(!txtBookNo.getValue().toString().isEmpty())
					{
						if(!txtFolioFrom.getValue().toString().isEmpty())
						{
							if(!txtFolioTo.getValue().toString().isEmpty())
							{
								saveBtnAction();
							}
							else
							{
								showNotification("Warning!","Provide Folio To.",Notification.TYPE_WARNING_MESSAGE);
								txtFolioTo.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Folio From.",Notification.TYPE_WARNING_MESSAGE);
							txtFolioFrom.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide Cheque Book No.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Select Bank AC Name.",Notification.TYPE_WARNING_MESSAGE);
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
						showNotification("Warning!","There are no data",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Cheque book is in using.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction();
				isUpdate = false;
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
				addChequeBookSl();
			}
		});
	}

	private void addChequeBookSl()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		txtBookNo.setValue("");
		try
		{
			if(cmbBankAccount.getValue()!=null)
			{
				String query = "select ISNULL(MAX(CAST(bookNo as int)),0)+1 maxId from tbChequeBook where ledgerId"
						+ " = '"+(cmbBankAccount.getValue()!=null?cmbBankAccount.getValue().toString():"")+"'";
				Iterator<?> iter = session.createSQLQuery(query).list().iterator();
				if(iter.hasNext())
				{
					txtBookNo.setValue(charIncrease(iter.next().toString(),4));
				}
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
	}

	private String charIncrease(String id, int length)
	{
		String ret = id;
		while(ret.length()!=length)
		{
			ret = "0"+ret;
		}
		return ret;
	}

	private boolean updateCheck()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " Select COUNT(id) as a from tbChequeBook where id = '"+id.getValue()+"' and"
					+ " (status = 'YES' or status = 'CANCEL') ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
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
		finally{session.close();}
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
					deleteData();
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
		Window win = new FindChequeBookEntry(sessionBean,id);

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

					findsetValue();
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findsetValue()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " Select distinct id,ledgerId,bookNo,date, "+
					" (Select replace(convert(varchar,min(convert(money,folioNo))),'.00','') from tbChequeBook as a where a.id = '"+id.getValue()+"') as folioFrom, "+
					" (Select replace(convert(varchar,max(convert(money,folioNo))),'.00','') from tbChequeBook as a where a.id = '"+id.getValue()+"') as folioTo "+
					" from tbChequeBook where id = '"+id.getValue()+"' group by id,ledgerId,bookNo,date,folioNo ";
			List<?> led = session.createSQLQuery(query).list();
			for(Iterator<?> iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBankAccount.setValue(element[1]);
				txtBookNo.setValue(element[2]);
				date.setValue(element[3]);
				txtFolioFrom.setValue(element[4]);
				txtFolioTo.setValue(element[5]);
			}	
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
						if(deleteData())
						{
							insertData();
						}
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

	private boolean deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = " delete tbChequeBook where id = '"+id.getValue()+"' ";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	public String autoId() 
	{
		String autoCode = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query = " Select cast(isnull(max(cast(replace(id, '', '')as int))+1, 1)as varchar) from tbChequeBook ";
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return autoCode;
	}

	private void insertData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			if(!isUpdate)
			{rId = autoId();}
			else
			{rId = id.getValue().toString();}

			BigInteger folioFrom = new BigInteger(txtFolioFrom.getValue().toString());
			BigInteger folioTo = new BigInteger(txtFolioTo.getValue().toString());
			int sub = Integer.parseInt(folioTo.subtract(folioFrom).toString());
			for(int i = 0; i<=sub; i++)
			{
				BigInteger val = folioFrom.add(BigInteger.valueOf(i));
				String sql = " Insert into tbChequeBook  values " +
						" ('"+rId+"','"+cmbBankAccount.getValue().toString().replaceAll("#", "")+"', " +
						" '"+txtBookNo.getValue().toString().trim()+"', '"+dtfYMD.format(date.getValue())+"', " +
						" '"+val+"', 'NO', " +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP )";
				session.createSQLQuery(sql).executeUpdate();
			}
			tx.commit();
			txtEnable(false);
			btnIni(true);
			txtClear();
			showNotification("All information save successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Errora",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
		txtBookNo.setValue("");
		date.setValue(new java.util.Date());
		txtFolioFrom.setValue("");
		txtFolioTo.setValue("");
	}

	private void bankHeadIni()
	{
		cmbBankAccount.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,"
					+ " 1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A8','L8') AND companyId in ('0',"
					+ " '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
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
		finally{session.close();}
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
		txtBookNo.setEnabled(t);
		date.setEnabled(t);
		txtFolioFrom.setEnabled(t);
		txtFolioTo.setEnabled(t);
		btnbankHead.setEnabled(t);
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
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		setWidth("620px");
		setHeight("260px");

		lblBankAccount = new Label();
		lblBankAccount.setImmediate(true);
		lblBankAccount.setWidth("-1px");
		lblBankAccount.setHeight("-1px");
		lblBankAccount.setValue("Bank AC Name :");
		mainLayout.addComponent(lblBankAccount, "top:22.0px;left:100.0px;");

		cmbBankAccount = new ComboBox();
		cmbBankAccount.setImmediate(true);
		cmbBankAccount.setWidth("-1px");
		cmbBankAccount.setHeight("-1px");
		cmbBankAccount.setWidth("270px");
		cmbBankAccount.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbBankAccount, "top:22.0px;left:210.0px;");

		btnbankHead = new NativeButton();
		btnbankHead.setCaption("");
		btnbankHead.setImmediate(true);
		btnbankHead.setWidth("28px");
		btnbankHead.setHeight("24px");
		btnbankHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnbankHead,"top:22.0px;left:485.0px;");

		lblBookNo = new Label();
		lblBookNo.setImmediate(true);
		lblBookNo.setWidth("-1px");
		lblBookNo.setHeight("-1px");
		lblBookNo.setValue("Cheque Book No :");
		mainLayout.addComponent(lblBookNo, "top:47.0px;left:100.0px;");

		txtBookNo = new TextRead();
		txtBookNo.setImmediate(true);
		txtBookNo.setWidth("100px");
		txtBookNo.setHeight("23px");
		mainLayout.addComponent(txtBookNo, "top:47.0px;left:210.0px;");

		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:72.0px;left:100.0px;");

		date = new DateField();
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date,"top:72.0px;left:210.0px;");

		lblFolioFrom = new Label();
		lblFolioFrom.setImmediate(true);
		lblFolioFrom.setWidth("-1px");
		lblFolioFrom.setHeight("-1px");
		lblFolioFrom.setValue("Folio From :");
		mainLayout.addComponent(lblFolioFrom, "top:97.0px;left:100.0px;");

		txtFolioFrom = new AmountField();
		txtFolioFrom.setImmediate(true);
		txtFolioFrom.setWidth("180px");
		txtFolioFrom.setHeight("-1px");
		mainLayout.addComponent(txtFolioFrom, "top:97.0px;left:210.0px;");

		lblFolioTo = new Label();
		lblFolioTo.setImmediate(true);
		lblFolioTo.setWidth("-1px");
		lblFolioTo.setHeight("-1px");
		lblFolioTo.setValue("Folio To :");
		mainLayout.addComponent(lblFolioTo, "top:122.0px;left:100.0px;");

		txtFolioTo = new AmountField();
		txtFolioTo.setImmediate(true);
		txtFolioTo.setWidth("180px");
		txtFolioTo.setHeight("-1px");
		mainLayout.addComponent(txtFolioTo, "top:122.0px;left:210.0px;");

		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:170.0px;left:10.0px;");
		return mainLayout;
	}
}