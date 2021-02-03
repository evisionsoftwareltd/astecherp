package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import database.hibernate.TbAuthentication;

public class MoneyReceipt extends Window
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AbsoluteLayout mainLayout;

	private Label lblReceipt;
	private static final List<String> receiptType = Arrays.asList(new String[] {"Cash","Cheque","Online Deposit","PO/DD/TT"});
	public OptionGroup receiptGroup = new OptionGroup("",receiptType);

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblMrNo;
	private TextRead txtMrNo;

	private Label lblVoucherNo;
	private TextRead txtVoucherNo;

	private Label lblOfficeLocation;
	private ComboBox cmbOfficeLocation;

	private Label lblReceivedFrom;
	private static final List<String> partyType = Arrays.asList(new String[] {"Party","Non-Party"});
	public OptionGroup partyOg = new OptionGroup("",partyType);

	private Label lblAccountHead;
	private ComboBox cmbAccountHead;

	private Label lblAddress;
	private TextField txtAddress;

	private Label lblChequeNo;
	private TextField txtChequeNo;

	private Label lblChequeDate;
	private PopupDateField dChequeDate;

	private Label lblBankName;
	private ComboBox cmbBankName;

	private Label lblBranchName;
	private ComboBox cmbBranchName;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat recDateFormat = new SimpleDateFormat("MM/yy");

	private Table table= new Table();

	private CommonButton cButton = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");

	private ArrayList<ComboBox> tbCmbParticular = new ArrayList<ComboBox>();
	private ArrayList<AmountCommaSeperator> tbTxtAmount = new ArrayList<AmountCommaSeperator>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SessionBean sessionBean;

	private TextField txtMrFind = new TextField();

	private Boolean isUpdate= false;
	private Boolean isFind= false;

	int actionParty = 0;

	String maxId = "";

	public MoneyReceipt(SessionBean sessionBean) 
	{
		this.setResizable(false);
		this.sessionBean = sessionBean;
		this.setCaption("MONEY RECEIPT :: "+ sessionBean.getCompany());

		buildmainLayout();
		setContent(mainLayout);

		tableinitialise();

		componentIni(true);
		btnIni(true);

		setEventAction();

		cmbDepositeBankNameData();
		cmbDepositeBranchNameData();

		addOfficeInformation();

		focusEnter();
		authenticationCheck();

		receiptGroup.setValue("Cash");
		cButton.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(false);
		}
	}

	public void setEventAction()
	{
		cButton.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				componentIni(false);
				btnIni(false);
				txtClear();
				selectReceiptNo();
				//actionParty=0;
				cmbOfficeLocation.focus();
			}
		});

		cButton.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				txtMrFind.setValue("");
				findButtonEvent();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(isFind)
				{
					isUpdate = true;
					componentIni(false);
					btnIni(false);
				}
				else
				{
					showNotification("Warning!","Please find data for edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
				//actionParty=0;
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		partyOg.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				addPartyLedger();
				actionParty=1;
			}
		});

		receiptGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				txtClear();
				enableComponent();
				cmbParticularIni(receiptGroup.getValue().toString());
				cButton.btnNew.focus();
			}
		});

		cmbAccountHead.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbAccountHead.getValue()!=null)
				{
					setPartyAddress();
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewMrEvent();
			}
		});

		cButton.btnDelete.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				deleteEventAction();
			}
		});
	}

	private void addOfficeInformation()
	{
		cmbOfficeLocation.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" select vDepoId,vDepoName from tbDepoInformation order by iAutoId ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOfficeLocation.addItem(element[0]);
				cmbOfficeLocation.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbDepositeBankNameData()
	{
		cmbBankName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "SELECT id,bankName FROM tbBankName" ;
			System.out.println(sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBankName.addItem(element[1]);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

	private void cmbDepositeBranchNameData()
	{
		cmbBranchName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "select id,branchName from tbBankBranch" ;
			System.out.println(sql);

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBranchName.addItem(element[1]);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

	public void cmbParticularIni(String str)
	{
		tbCmbParticular.get(0).removeAllItems();
		String s = "";

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(str.equalsIgnoreCase("cash"))
			{
				s = "CR";
			}
			else if(str.equalsIgnoreCase("cheque"))
			{
				s = "BR";
			}

			List group = session.createSQLQuery("SELECT * FROM tbNarrationList where VoucherType = '"+s+"' ").list();

			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();

				tbCmbParticular.get(0).addItem(element[1]);
			}
		}
		catch(Exception exp)
		{			
			System.out.println(exp);
		}
	}

	private void addPartyLedger()
	{
		if(partyOg.getValue()!=null)
		{
			cmbAccountHead.removeAllItems();
			txtAddress.setValue("");
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();

				String sql="";

				if(partyOg.getValue().toString().equalsIgnoreCase("Party"))
				{
					sql = " select ledgerCode,partyName from tbPartyInfo order by autoId ";
				}
				if(partyOg.getValue().toString().equalsIgnoreCase("Non-Party"))
				{
					sql = " select Ledger_Id,Ledger_Name from tbLedger where SUBSTRING(Create_From,1,2) in ('A2','A4','A8','L1','L10','L11','L5','L8') ";
				}

				List list=session.createSQLQuery(sql).list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbAccountHead.addItem(element[0]);
					cmbAccountHead.setItemCaption(element[0], element[1].toString());
				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	private void setPartyAddress()
	{
		txtAddress.setValue("");
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iterMax = session.createSQLQuery(" select isnull(address,'') from tbPartyInfo where ledgerCode = '"+cmbAccountHead.getValue().toString()+"' ").list().iterator();

			if(iterMax.hasNext())
				txtAddress.setValue(iterMax.next().toString());
		}
		catch (Exception e)
		{
			this.getParent().showNotification("Error ",e+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void findButtonEvent()
	{
		Window win = new MoneyReceiptFind(sessionBean, txtMrFind,receiptGroup.getValue().toString());
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtMrFind.getValue().toString().length() > 0)
				{
					txtClear();
					System.out.println("Find "+txtMrFind.getValue().toString());
					findInitialise(txtMrFind.getValue().toString());
					isFind=true;
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String VoucherNo)
	{
		Object[] element = null;
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = " SELECT * FROM tbMoneyReceipt where vVoucherNo='"+VoucherNo+"' ";

			List led = session.createSQLQuery(sql).list();

			for (Iterator iter = led.iterator();iter.hasNext();)
			{
				element = (Object[]) iter.next();

				receiptGroup.setValue(element[1].toString());
				dDate.setValue(element[2]);
				txtMrNo.setValue(element[3].toString());
				txtVoucherNo.setValue(element[4].toString());

				maxId = element[5].toString();

				cmbOfficeLocation.setValue(element[6]);
				partyOg.setValue(element[8].toString());
				cmbAccountHead.setValue(element[9]);
				txtChequeNo.setValue(element[12].toString());
				dChequeDate.setValue(element[13]);
				cmbBankName.setValue(element[14].toString());
				cmbBranchName.setValue(element[15].toString());

				tbCmbParticular.get(0).addItem((element[16].toString())); //if particular is not same as database particular
				tbCmbParticular.get(0).setValue(element[16].toString());

				tbTxtAmount.get(0).setValue(new CommaSeparator().setComma(Double.parseDouble(element[17].toString())));
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void formValidation()
	{
		if(!txtMrNo.getValue().toString().isEmpty() && !txtVoucherNo.getValue().toString().isEmpty())
		{
			if(cmbOfficeLocation.getValue()!=null)
			{
				if(actionParty==1)
				{
					if(cmbAccountHead.getValue()!=null)
					{
						if(receiptGroup.getValue().toString().equalsIgnoreCase("Cash"))
						{
							tableValidation();
						}
						if(receiptGroup.getValue().toString().equalsIgnoreCase("Cheque"))
						{
							chaqueValidation();
						}
						if(receiptGroup.getValue().toString().equalsIgnoreCase("Online Deposit"))
						{
							onlineValidation();
						}
						if(receiptGroup.getValue().toString().equalsIgnoreCase("PO/DD/TT"))
						{
							poValidation();
						}
					}
					else
					{
						showNotification("Warning!","Select Party/Non-Party ledger",Notification.TYPE_WARNING_MESSAGE);
						cmbAccountHead.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Received from",Notification.TYPE_WARNING_MESSAGE);
					partyOg.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Office/Depo",Notification.TYPE_WARNING_MESSAGE);
				cmbOfficeLocation.focus();
			}
		}
		else
		{
			showNotification("Warning!","Provide Money receipt & Voucher no",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void chaqueValidation()
	{
		if(!txtChequeNo.getValue().toString().isEmpty())
		{
			if(cmbBankName.getValue()!=null)
			{
				if(cmbBranchName.getValue()!=null)
				{
					tableValidation();
				}
				else
				{
					showNotification("Warning!","Select Branch Name",Notification.TYPE_WARNING_MESSAGE);
					cmbBranchName.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Bank Name",Notification.TYPE_WARNING_MESSAGE);
				cmbBankName.focus();
			}
		}
		else
		{
			showNotification("Warning!","Provide Cheque No",Notification.TYPE_WARNING_MESSAGE);
			txtChequeNo.focus();
		}
	}

	private void onlineValidation()
	{
		if(cmbBankName.getValue()!=null)
		{
			if(cmbBranchName.getValue()!=null)
			{
				tableValidation();
			}
			else
			{
				showNotification("Warning!","Select Branch Name",Notification.TYPE_WARNING_MESSAGE);
				cmbBranchName.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Bank Name",Notification.TYPE_WARNING_MESSAGE);
			cmbBankName.focus();
		}
	}

	private void poValidation()
	{
		if(cmbBankName.getValue()!=null)
		{
			if(cmbBranchName.getValue()!=null)
			{
				tableValidation();
			}
			else
			{
				showNotification("Warning!","Select Branch Name",Notification.TYPE_WARNING_MESSAGE);
				cmbBranchName.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Bank Name",Notification.TYPE_WARNING_MESSAGE);
			cmbBankName.focus();
		}
	}

	public void selectReceiptNo()
	{
		String MrPrefix = "";

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			// where MR prefix is fixed
			/*Iterator iterPrefix = session.createSQLQuery(" select vReceiptPrefix from tbReceiptPrefixInfo where" +
					" iAutoId in ('5') ").list().iterator();*/

			Iterator iterMax = session.createSQLQuery(" SELECT ISNULL((MAX(CAST(SUBSTRING(vMrSerial,CHARINDEX('-'," +
					" vMrSerial,9)+1,50) AS INT))+1),1) FROM tbMoneyReceipt ").list().iterator();

			/*if(iterPrefix.hasNext())
				MrPrefix = iterPrefix.next().toString();*/

			if(iterMax.hasNext())
				maxId = iterMax.next().toString();
		}
		catch (Exception e)
		{

		}

		txtMrNo.setValue("MR-"+recDateFormat.format(new Date())+"-"+maxId);
		txtVoucherNo.setValue(autoVoucher(receiptGroup.getValue().toString()));
	}

	private void tableValidation()
	{
		if(tbCmbParticular.get(0).getValue()!=null)
		{
			if(!tbTxtAmount.get(0).getValue().isEmpty())
			{
				saveEventAction();
			}
			else
			{
				showNotification("Warning!","Provide Amount",Notification.TYPE_WARNING_MESSAGE);
				tbTxtAmount.get(0).focus();
			}
		}
		else
		{
			showNotification("Warning!","Select Particular",Notification.TYPE_WARNING_MESSAGE);
			tbCmbParticular.get(0).focus();
		}
	}

	private void deleteEventAction()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{						
					deleteData();

					btnIni(true);
					componentIni(true);

					cButton.btnNew.focus();
				}
			}
		});
	}

	private void saveEventAction()
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

						btnIni(true);
						componentIni(true);
						isUpdate = false;
						isFind = false;
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			if(checkChequeNoExist())
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							btnIni(true);
							componentIni(true);
							previewMrEvent();
							cButton.btnNew.focus();

						}
					}
				});
			}
			else
			{
				showNotification("Warning!","Cheque No Already Exist!!!",Notification.TYPE_WARNING_MESSAGE);
				txtChequeNo.focus();
			}
		}
	}

	private boolean checkChequeNoExist()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		Iterator iterPrefix = session.createSQLQuery(" select Cheque_No from ChequeDetails1 where Cheque_No = '"+(txtChequeNo.getValue().toString().isEmpty()?"No cheque":txtChequeNo.getValue().toString().trim())+"' ").list().iterator();

		if(iterPrefix.hasNext())
			return false;
		else
			return true;
	}

	private void insertData()
	{
		String voucher = "";
		String cheque = "";

		String voucherNo = "";
		String MRNo = "";

		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		if(isUpdate)
		{
			MRNo = txtMrNo.getValue().toString();
			voucherNo = txtVoucherNo.getValue().toString();
		}
		else
		{
			selectReceiptNo();
			MRNo = txtMrNo.getValue().toString();
			voucherNo = autoVoucher(receiptGroup.getValue().toString());
		}

		String number = selectVoucher();

		voucher = "voucher"+number;
		cheque = "chequedetails"+number;
		Object time = getDatetime();

		try
		{
			String InsertInfo = " INSERT into tbMoneyReceipt values( " +
					" '"+receiptGroup.getValue().toString()+"', " +
					" '"+dFormat.format(dDate.getValue()).trim()+" "+(time)+"'," +
					" '"+MRNo+"', " +
					" '"+voucherNo+"', " +
					" '"+maxId+"', " +
					" '"+cmbOfficeLocation.getValue().toString()+"', " +
					" '"+cmbOfficeLocation.getItemCaption(cmbOfficeLocation.getValue().toString())+"', " +
					" '"+partyOg.getValue().toString()+"', " +
					" '"+cmbAccountHead.getValue().toString()+"', " +
					" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString())+"', " +
					" '"+(txtAddress.getValue().toString().isEmpty()?"":txtAddress.getValue().toString())+"', " +
					" '"+(txtChequeNo.getValue().toString().isEmpty()?"":txtChequeNo.getValue().toString())+"', " +
					" '"+dFormat.format(dChequeDate.getValue()).trim()+" "+(time)+"'," +
					" '"+(cmbBankName.getValue()==null?"":cmbBankName.getValue().toString())+"', " +
					" '"+(cmbBranchName.getValue()==null?"":cmbBranchName.getValue().toString())+"', " +
					" '"+tbCmbParticular.get(0).getValue().toString()+"', " +
					" '"+tbTxtAmount.get(0).getValue().toString().replaceAll(",", "")+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP " +
					")";

			//System.out.println("InsertInfo: "+InsertInfo);
			session.createSQLQuery(InsertInfo).executeUpdate();

			if(receiptGroup.getValue().toString().equalsIgnoreCase("Cash"))
			{
				String voucherCredit = " INSERT into "+voucher+" values(" +
						" '"+voucherNo+"'," +
						" '"+dFormat.format(dDate.getValue()).trim()+"'," +
						" '"+(!receiptGroup.getValue().toString().equalsIgnoreCase("Cash")?"temp":"AL101")+"'," +
						" '"+tbCmbParticular.get(0).getValue().toString()+"'," +
						" '0.00'," +
						" '"+tbTxtAmount.get(0).getValue().toString().replace(",", "").trim()+"'," +
						" '"+(!receiptGroup.getValue().toString().equalsIgnoreCase("Cash")?"cba":"cca")+"'," +
						" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString())+"'," +
						" 'U-3'," +
						" '"+(receiptGroup.getValue().toString().equalsIgnoreCase("Cheque")?"0":"1")+"'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '')";

				//System.out.println("voucherCredit: "+voucherCredit);
				session.createSQLQuery(voucherCredit).executeUpdate();

				String voucherDebit = " INSERT into "+voucher+" values(" +
						" '"+voucherNo+"'," +
						" '"+dFormat.format(dDate.getValue()).trim()+"'," +
						" '"+cmbAccountHead.getValue().toString()+"'," +
						" '"+tbCmbParticular.get(0).getValue().toString()+"'," +
						" '"+tbTxtAmount.get(0).getValue().toString().replace(",", "").trim()+"'," +
						" '0.00'," +
						" '"+(!receiptGroup.getValue().toString().equalsIgnoreCase("Cash")?"cba":"cca")+"'," +
						" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString())+"'," +
						" 'U-3'," +
						" '"+(receiptGroup.getValue().toString().equalsIgnoreCase("Cheque")?"0":"1")+"'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '')";

				//System.out.println("voucherDebit: "+voucherDebit);
				session.createSQLQuery(voucherDebit).executeUpdate();
			}

			if(!receiptGroup.getValue().toString().equalsIgnoreCase("Cash"))
			{
				String chequeDetails = " INSERT INTO "+cheque+" ( Cheque_No, Cheque_Date, Voucher_No, Bank_Id, BankName," +
						" BranchName, MrNo, companyId ,payNo, receivedFrom, userId, userIp, entryTime) values ( " +
						" '"+txtChequeNo.getValue().toString().trim()+"'," +
						" '"+dFormat.format(dChequeDate.getValue())+"'," +
						" '"+voucherNo+"'," +
						" ''," +
						" '"+(cmbBankName.getValue()==null?"":cmbBankName.getValue().toString())+"'," +
						" '"+(cmbBranchName.getValue()==null?"":cmbBranchName.getValue().toString())+"'," +
						" '"+MRNo+"'," +
						" '1'," +
						" '0'," +
						" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString().trim())+"'," +
						" '"+sessionBean.getUserId()+"', " +
						" '"+sessionBean.getUserIp()+"', " +
						" CURRENT_TIMESTAMP " +
						" ) ";

				//System.out.println("chequeDetails "+chequeDetails);
				session.createSQLQuery(chequeDetails).executeUpdate();
			}
			String InsertUdInfo = " INSERT into tbUdMoneyReceipt values( " +
					" '"+receiptGroup.getValue().toString()+"', " +
					" '"+dFormat.format(dDate.getValue()).trim()+" "+(time)+"'," +
					" '"+MRNo+"', " +
					" '"+voucherNo+"', " +
					" '"+maxId+"', " +
					" '"+cmbOfficeLocation.getValue().toString()+"', " +
					" '"+cmbOfficeLocation.getItemCaption(cmbOfficeLocation.getValue().toString())+"', " +
					" '"+partyOg.getValue().toString()+"', " +
					" '"+cmbAccountHead.getValue().toString()+"', " +
					" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString())+"', " +
					" '"+(txtAddress.getValue().toString().isEmpty()?"":txtAddress.getValue().toString())+"', " +
					" '"+(txtChequeNo.getValue().toString().isEmpty()?"":txtChequeNo.getValue().toString())+"', " +
					" '"+dFormat.format(dChequeDate.getValue()).trim()+" "+(time)+"'," +
					" '"+(cmbBankName.getValue()==null?"":cmbBankName.getValue().toString())+"'," +
					" '"+(cmbBranchName.getValue()==null?"":cmbBranchName.getValue().toString())+"'," +
					" '"+tbCmbParticular.get(0).getValue().toString()+"', " +
					" '"+tbTxtAmount.get(0).getValue().toString().replaceAll(",", "")+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, " +
					" '"+(isUpdate?"Update":"New")+"' " +
					")";

			//System.out.println("InsertUdInfo: "+InsertUdInfo);
			session.createSQLQuery(InsertUdInfo).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		Object time = getDatetime();
		String voucher = "voucher"+selectVoucher();
		String cheque = "chequedetails"+selectVoucher();

		try
		{
			String InsertUdInfo = " INSERT into tbUdMoneyReceipt values( " +
					" '"+receiptGroup.getValue().toString()+"', " +
					" '"+dFormat.format(dDate.getValue()).trim()+" "+(time)+"'," +
					" '"+txtMrNo.getValue().toString().trim()+"', " +
					" '"+txtVoucherNo.getValue().toString().trim()+"', " +
					" '"+maxId+"', " +
					" '"+cmbOfficeLocation.getValue().toString()+"', " +
					" '"+cmbOfficeLocation.getItemCaption(cmbOfficeLocation.getValue().toString())+"', " +
					" '"+partyOg.getValue().toString()+"', " +
					" '"+cmbAccountHead.getValue().toString()+"', " +
					" '"+cmbAccountHead.getItemCaption(cmbAccountHead.getValue().toString())+"', " +
					" '"+(txtAddress.getValue().toString().isEmpty()?"":txtAddress.getValue().toString())+"', " +
					" '"+(txtChequeNo.getValue().toString().isEmpty()?"":txtChequeNo.getValue().toString())+"', " +
					" '"+dFormat.format(dChequeDate.getValue()).trim()+" "+(time)+"'," +
					" '"+(cmbBankName.getValue()==null?"":cmbBankName.getValue().toString())+"'," +
					" '"+(cmbBranchName.getValue()==null?"":cmbBranchName.getValue().toString())+"'," +
					" '"+tbCmbParticular.get(0).getValue().toString()+"', " +
					" '"+tbTxtAmount.get(0).getValue().toString().replaceAll(",", "")+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, " +
					" 'Delete' " +
					")";

			//System.out.println("InsertUdInfo: "+InsertUdInfo);
			session.createSQLQuery(InsertUdInfo).executeUpdate();

			String delFormSql = " delete from tbMoneyReceipt where vVoucherNo='"+txtVoucherNo.getValue().toString().trim()+"' ";
			String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtVoucherNo.getValue().toString().trim()+"' ";
			String delChequeSql = " delete from "+cheque+" where Voucher_No='"+txtVoucherNo.getValue().toString().trim()+"' ";

			//System.out.println("delFormSql: "+delFormSql);
			//System.out.println("delVoucherSql: "+delVoucherSql);
			//System.out.println("delChequeSql: "+delVoucherSql);
			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delVoucherSql).executeUpdate();
			session.createSQLQuery(delChequeSql).executeUpdate();

			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private String autoVoucher(String str)
	{
		String voucherNo = "";

		if(str.equalsIgnoreCase("cash"))
		{
			if(!txtMrNo.getValue().toString().isEmpty())
			{
				voucherNo = "CR-CH-"+txtVoucherNo("cash");
			}
			else
			{
				showNotification("Warning","There is no MR No ",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		else// if(str.equalsIgnoreCase("cheque"))
		{
			if(!txtMrNo.getValue().toString().isEmpty())
			{
				voucherNo = "CR-BK-"+txtVoucherNo("cheque");
			}
			else
			{
				showNotification("Warning","There is no MR No ",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		return voucherNo;
	}

	private String txtVoucherNo(String str)
	{	
		String autoCode = "";
		String voucher = "voucher"+selectVoucher();

		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "";

			if(str.equalsIgnoreCase("cash"))
			{
				query = " Select cast(isnull(max(cast(replace(substring(Voucher_No,7,len(Voucher_No)), '', '')as int))+1, 1)as varchar) from "+voucher+" where Voucher_No like 'CR-CH-%' ";
			}
			else if(str.equalsIgnoreCase("cheque"))
			{
				query = " Select cast(isnull(max(cast(replace(substring(Voucher_No,7,len(Voucher_No)), '', '')as int))+1, 1)as varchar) from "+voucher+" where Voucher_No like 'CR-BK-%' ";
			}

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

	private String selectVoucher()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dDate.getValue())+"') as voucher").list().iterator().next().toString();

		return voucher;
	}

	private void enableComponent()
	{
		if(receiptGroup.getValue()!=null)
		{
			if(receiptGroup.getValue().toString().equalsIgnoreCase("Cash"))
			{
				lblChequeNo.setVisible(false);
				txtChequeNo.setVisible(false);
				dChequeDate.setVisible(false);
				lblChequeDate.setVisible(false);
				cmbBankName.setVisible(false);
				lblBankName.setVisible(false);
				cmbBranchName.setVisible(false);
				lblBranchName.setVisible(false);
			}
			else if(receiptGroup.getValue().toString().equalsIgnoreCase("Online Deposit"))
			{
				lblChequeNo.setVisible(false);
				txtChequeNo.setVisible(false);
				dChequeDate.setVisible(false);
				lblChequeDate.setVisible(false);
			}
			else
			{
				lblChequeNo.setVisible(true);
				txtChequeNo.setVisible(true);
				dChequeDate.setVisible(true);
				lblChequeDate.setVisible(true);
				cmbBankName.setVisible(true);
				lblBankName.setVisible(true);
				cmbBranchName.setVisible(true);
				lblBranchName.setVisible(true);
				if(receiptGroup.getValue().toString().equalsIgnoreCase("PO/DD/TT"))
				{
					lblChequeNo.setValue("PO/DD/TT No :");
					lblChequeDate.setValue("Date :");
				}
				else
				{
					lblChequeNo.setValue("Cheque No :");
					lblChequeDate.setValue("Cheque Date :");
				}
			}
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbOfficeLocation);

		allComp.add(partyOg);
		allComp.add(cmbAccountHead);
		allComp.add(txtAddress);

		allComp.add(txtChequeNo);
		allComp.add(cmbBankName);
		allComp.add(cmbBranchName);

		allComp.add(tbCmbParticular.get(0));
		allComp.add(tbTxtAmount.get(0));

		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		isFind = false;
		isUpdate = false;

		dDate.setValue(new java.util.Date());
		txtMrNo.setValue("");
		txtVoucherNo.setValue("");

		cmbOfficeLocation.setValue(null);
		cmbAccountHead.setValue(null);
		txtAddress.setValue("");

		txtChequeNo.setValue("");
		dChequeDate.setValue(new java.util.Date());

		cmbBankName.setValue(null);
		cmbBranchName.setValue(null);

		for(int i=0; i<1; i++)
		{
			tbCmbParticular.get(i).setValue(null);
			tbTxtAmount.get(i).setValue("");
		}
	}

	private void componentIni(boolean b) 
	{
		receiptGroup.setEnabled(b);

		dDate.setEnabled(!b);
		txtMrNo.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);

		cmbOfficeLocation.setEnabled(!b);
		partyOg.setEnabled(!b);
		cmbAccountHead.setEnabled(!b);
		txtAddress.setEnabled(!b);
		txtChequeNo.setEnabled(!b);
		dChequeDate.setEnabled(!b);
		cmbBankName.setEnabled(!b);
		cmbBranchName.setEnabled(!b);

		table.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnCancel.setEnabled(t);
		cButton.btnPreview.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildmainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("800px");
		setHeight("460px");
		this.setResizable(false);

		lblReceipt = new Label("Receipt Type :");
		lblReceipt.setHeight("-1px");
		lblReceipt.setWidth("-1px");
		mainLayout.addComponent(lblReceipt, "top:20.0px; left:30.0px");

		receiptGroup.setImmediate(true);
		receiptGroup.setStyleName("horizontal");

		mainLayout.addComponent(receiptGroup, "top:18.0px; left:110.0px");

		lblDate = new Label("Date :");
		lblDate.setHeight("-1px");
		lblDate.setWidth("-1px");
		mainLayout.addComponent(lblDate, "top:60.0px; left:30.0px");

		//dDate
		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setHeight("-1px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(dDate,"top:57.0px; left:130.0px");

		// lblMrNo
		lblMrNo = new Label("MR No :");
		lblMrNo.setImmediate(false);
		lblMrNo.setWidth("-1px");
		lblMrNo.setHeight("-1px");
		mainLayout.addComponent(lblMrNo, "top:85.0px;left:30.0px;");

		// txtMRno
		txtMrNo  = new TextRead();
		txtMrNo.setImmediate(false);
		txtMrNo.setWidth("110px");
		txtMrNo.setHeight("21px");
		mainLayout.addComponent(txtMrNo, "top:83.0px;left:131.0px;");

		// lblVoucherNo
		lblVoucherNo = new Label("Voucher No :");
		lblVoucherNo.setImmediate(false);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("22px");
		mainLayout.addComponent(lblVoucherNo, "top:110.0px;left:30.0px;");

		// txtVoucher
		txtVoucherNo = new TextRead();
		txtVoucherNo.setImmediate(false);
		txtVoucherNo.setHeight("21px");
		txtVoucherNo.setWidth("110px");
		mainLayout.addComponent(txtVoucherNo, "top:108.0px;left:131.0px;");

		//lblOfficeLocation
		lblOfficeLocation = new Label("Office/Depo :");
		lblOfficeLocation.setImmediate(true);
		lblOfficeLocation.setHeight("-1px");
		lblOfficeLocation.setWidth("-1px");
		mainLayout.addComponent(lblOfficeLocation, "top:135.0px;left:30.0px;");

		// cmbOfficeLocation
		cmbOfficeLocation = new ComboBox();
		cmbOfficeLocation.setImmediate(true);
		cmbOfficeLocation.setNewItemsAllowed(false);
		cmbOfficeLocation.setNullSelectionAllowed(true);
		cmbOfficeLocation.setWidth("230px");
		mainLayout.addComponent(cmbOfficeLocation, "top:133.0px;left:130px;");

		//lblReceivedFrom
		lblReceivedFrom = new Label("Received From :");
		lblReceivedFrom.setImmediate(false);
		lblReceivedFrom.setWidth("-1px");
		lblReceivedFrom.setHeight("-1px");
		mainLayout.addComponent(lblReceivedFrom, "top:170.0px;left:30.0px;");

		//partyOg
		partyOg.setStyleName("horizontal");
		partyOg.setImmediate(true);
		mainLayout.addComponent(partyOg, "top:168.0px;left:130px;");

		// lblAccountHead
		lblAccountHead = new Label("Accounts Head :");
		lblAccountHead.setImmediate(false);
		lblAccountHead.setWidth("-1px");
		lblAccountHead.setHeight("-1px");
		mainLayout.addComponent(lblAccountHead, "top:195.0px;left:30.0px;");

		// cmbAccountHead
		cmbAccountHead = new ComboBox();
		cmbAccountHead.setImmediate(true);
		cmbAccountHead.setNewItemsAllowed(false);
		cmbAccountHead.setNullSelectionAllowed(true);
		cmbAccountHead.setWidth("320px");
		mainLayout.addComponent(cmbAccountHead, "top:193.0px;left:130.0px;");

		// lblAddress
		lblAddress = new Label("Address :");
		lblAddress.setImmediate(false);
		lblAddress.setWidth("230px");
		lblAddress.setHeight("28px");
		mainLayout.addComponent(lblAddress, "top:220.0px;left:30.0px;");

		// txtAddress
		txtAddress = new TextField();
		txtAddress.setImmediate(false);
		txtAddress.setWidth("230px");
		txtAddress.setHeight("44px");
		mainLayout.addComponent(txtAddress, "top:218.0px;left:130.0px;");

		// lblChequeNo
		lblChequeNo = new Label("Cheque No :");
		lblChequeNo.setImmediate(false);
		lblChequeNo.setWidth("-1px");
		lblChequeNo.setHeight("-1px");
		mainLayout.addComponent(lblChequeNo, "top:60.0px;left:460.0px;");

		// txtChequeNo
		txtChequeNo = new TextField();
		txtChequeNo.setImmediate(false);
		txtChequeNo.setWidth("200px");
		txtChequeNo.setHeight("-1px");
		mainLayout.addComponent(txtChequeNo, "top:58.0px;left:560.0px;");

		// lblChequeDate
		lblChequeDate = new Label("Cheque Date :");
		lblChequeDate.setImmediate(false);
		lblChequeDate.setWidth("-1px");
		lblChequeDate.setHeight("-1px");
		mainLayout.addComponent(lblChequeDate, "top:90.0px;left:460.0px;");

		// dChequeDate
		dChequeDate = new PopupDateField();
		dChequeDate.setDateFormat("dd-MM-yyyy");
		dChequeDate.setValue(new Date());
		dChequeDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChequeDate, "top:88.0px;left:560.0px;");

		// lblBankName
		lblBankName = new Label("Bank Name :");
		lblBankName.setImmediate(false);
		lblBankName.setWidth("-1px");
		lblBankName.setHeight("-1px");
		mainLayout.addComponent(lblBankName, "top:135.0px;left:460.0px;");

		// cmbBankName
		cmbBankName = new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setWidth("200px");
		cmbBankName.setHeight("-1px");
		cmbBankName.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbBankName, "top:133.0px;left:560.0px;");

		// lblBranchName
		lblBranchName = new Label("Branch Name :");
		lblBranchName.setImmediate(false);
		lblBranchName.setWidth("-1px");
		lblBranchName.setHeight("-1px");
		mainLayout.addComponent(lblBranchName, "top:165.0px;left:460.0px;");

		// cmbBranchName
		cmbBranchName = new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setWidth("200px");
		cmbBranchName.setHeight("-1px");
		cmbBranchName.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbBranchName, "top:163.0px;left:560.0px;");

		table.setWidth("610px");
		table.setHeight("70px");
		table.setColumnCollapsingAllowed(true);
		table.setImmediate(true);

		table.addContainerProperty("PARTUCULAR", ComboBox.class, new ComboBox());
		table.setColumnWidth("PARTUCULAR", 450);

		table.addContainerProperty("AMOUNT", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("AMOUNT", 120);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER, Table.ALIGN_CENTER});
		mainLayout.addComponent(table,"top:280.0px; left:95.0px; ");

		mainLayout.addComponent(cButton, "top:370.0px;left:60.0px;");

		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0; i<1; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		tbCmbParticular.add(ar,new ComboBox());
		tbCmbParticular.get(ar).setImmediate(true);
		tbCmbParticular.get(ar).setWidth("100%");
		tbCmbParticular.get(ar).setHeight("25px");
		tbCmbParticular.get(ar).setNewItemsAllowed(true);

		tbTxtAmount.add(ar,new AmountCommaSeperator());
		tbTxtAmount.get(ar).setWidth("100%");
		tbTxtAmount.get(ar).setHeight("25px");

		table.addItem(new Object[]{tbCmbParticular.get(ar),tbTxtAmount.get(ar)},ar);
	}

	private Date getDatetime()
	{
		Date dateTime = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			String query= "select CONVERT(time ,CURRENT_TIMESTAMP)";//"select CURRENT_TIMESTAMP";
			Iterator iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				dateTime= (Date) iter.next();
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error3",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return dateTime;
	}

	private void previewMrEvent()
	{
		String query = null;
		String subReportQueryBill = null;
		try
		{
			HashMap hm = new HashMap();

			query = " select vReceiptType,vMrNo,dDate,vLedgerName vCustomer,vAddress,mAmount,dbo.number(mAmount) inWord," +
					" vChequeNo,dChequeDate,vBankName,vBranchName,vPartucular,name,vOfficeName,current_timestamp datetime" +
					" from tbMoneyReceipt tbm inner join tbLogin tlg on tbm.vUserId=tlg.userId" +
					" where vMrNo = '"+txtMrNo.getValue().toString()+"' ";

			System.out.println("Report Query: "+query);

			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());

			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/rptMoneyReceipt.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",false);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}
}
