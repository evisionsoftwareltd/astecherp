package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.vaadin.data.Property.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.util.HashMap;

@SuppressWarnings("serial")
public class DebitNoteApprove extends Window
{
	SessionBean sessionBean;
	AbsoluteLayout mainLayout = new AbsoluteLayout();

	private Label lblBank;
	private ComboBox cmbBankHead;

	private Table tableDebitNote = new Table();
	private ArrayList<Label> tblblsl = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbVoucherNo = new ArrayList<Label>();
	private ArrayList<Label> tbRefNo = new ArrayList<Label>();
	private ArrayList<Label> tbPartyLedger = new ArrayList<Label>();
	private ArrayList<Label> tbPayStatus = new ArrayList<Label>();
	private ArrayList<Label> tbPayMode = new ArrayList<Label>();
	private ArrayList<Label> tbPayAginst = new ArrayList<Label>();
	private ArrayList<Label> tbAmount = new ArrayList<Label>();
	private ArrayList<Label> tbPreparedBy = new ArrayList<Label>();
	private ArrayList<Label> tbPreparedTime = new ArrayList<Label>();
	private ArrayList<CheckBox> tbAudit = new ArrayList<CheckBox>();
	private ArrayList<CheckBox> tbApproved = new ArrayList<CheckBox>();
	private ArrayList<NativeButton> tbDetailsPrint = new ArrayList<NativeButton>();
	private ArrayList<NativeButton> tbCQPrint = new ArrayList<NativeButton>();
	private ArrayList<CheckBox> tbCQPrinted = new ArrayList<CheckBox>();

	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");

	public DebitNoteApprove(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DEBIT NOTE :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		tableInitialise();
		setEventAction();
		bankCashHeadIni();
		cmbBankHead.setValue("%");
	}

	private void setEventAction()
	{
		cmbBankHead.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbBankHead.getValue()!= null)
				{
					setTableData();
				}
				else
				{
					showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
					tableClear();
				}
			}
		});
	}

	private void bankCashHeadIni()
	{
		cmbBankHead.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct vLedgerIdBankCash,vLedgerName from tbDebitNote order by vLedgerIdBankCash"; 
			List<?> list = session.createSQLQuery(sql).list();

			if(!list.isEmpty())
			{
				cmbBankHead.addItem("%");
				cmbBankHead.setItemCaption("%", "All");
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbBankHead.addItem(element[0].toString());
					cmbBankHead.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setTableData()
	{
		tableClear();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();

		try
		{
			String query = "select dDate,vVoucherNo,vReferenceNo,vSupplierName,vPaymentStatus,vModeOfPayment,"
					+ " vPaymentAgainst,mAmount,vUserName,dEntryTime,iApproveFlag from tbDebitNote where (iPrint = 0 "
					+ " or iApproveFlag != 2) "
					+ " and vLedgerIdBankCash like '"+cmbBankHead.getValue().toString()+"' order by vReferenceNo";
			List<?> list = session.createSQLQuery(query).list();
			int i = 0;
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				tbDate.get(i).setReadOnly(false);
				tbDate.get(i).setValue(element[0]);
				tbDate.get(i).setReadOnly(true);
				tbVoucherNo.get(i).setValue(element[1].toString());
				tbRefNo.get(i).setValue(element[2].toString());
				tbPartyLedger.get(i).setValue(element[3].toString());
				tbPayStatus.get(i).setValue(element[4].toString());

				tbPayAginst.get(i).setValue(element[5].toString());
				tbPayMode.get(i).setValue(element[6].toString());

				tbAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[7].toString())));
				tbPreparedBy.get(i).setValue(element[8].toString());
				tbPreparedTime.get(i).setValue(df.format(element[9]));

				if(element[10].toString().equals("0"))
				{
					tbAudit.get(i).setEnabled(true);
					tbApproved.get(i).setEnabled(false);
					tbCQPrinted.get(i).setEnabled(false);
					tbCQPrint.get(i).setEnabled(false);
				}
				else if(element[10].toString().equals("1"))
				{
					tbAudit.get(i).setEnabled(false);
					tbApproved.get(i).setEnabled(true);
					tbCQPrinted.get(i).setEnabled(false);
					tbCQPrint.get(i).setEnabled(false);
				}
				else if(element[10].toString().equals("2"))
				{
					tbAudit.get(i).setEnabled(false);
					tbApproved.get(i).setEnabled(false);
					tbCQPrinted.get(i).setEnabled(true);
					tbCQPrint.get(i).setEnabled(true);
				}

				if(tbRefNo.size()<=i)
				{
					tableRowAdd(i);
				}
				i++;
			}
			if(i == 0)
			{
				showNotification("Warning!","No data found.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnAction(final int flag, final int index, final int auditApp)
	{
		String type = "";
		if(flag==1)
		{type = "audit";}
		if(flag==2)
		{type = "approve";}
		if(flag==3)
		{type = "clear print";}
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", 
				MessageBox.Icon.QUESTION, "Do you want to "+type+" information?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
				new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					if(flag == 1)
					{auditData(index);}
					if(flag == 2)
					{approveData(index);}
					if(flag == 3)
					{printedData(index);}

					tableClear();
					setTableData();
					showNotification("Successful!","Information saved successfully.",Notification.TYPE_HUMANIZED_MESSAGE);
				}
				else
				{
					if(auditApp == 1)
					{tbAudit.get(index).setValue(false);}
					if(auditApp == 2)
					{tbApproved.get(index).setValue(false);}
					if(auditApp == 3)
					{tbCQPrinted.get(index).setValue(false);}
				}
			}
		});
	}

	private void auditData(final int index)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(tbDate.get(index).getValue())+"')";
			String fsl = session.createSQLQuery(sql).list().iterator().next().toString();
			String voucher =  "Voucher"+fsl;

			String queryInfo = " update tbDebitNote set "
					+ "iApproveFlag = 1, "
					+ "vAuditBy = '"+sessionBean.getUserName()+"', "
					+ "vAuditIp = '"+sessionBean.getUserIp()+"', "
					+ "dAuditTime = CURRENT_TIMESTAMP "
					+ "where vReferenceNo = '"+tbRefNo.get(index).getValue().toString()+"' ";
			session.createSQLQuery(queryInfo).executeUpdate();

			String queryVoucher = " update "+voucher+" set "
					+ "auditapproveflag = 1, "
					+ "audit_by = '"+sessionBean.getUserId()+"'"
					+ "where Voucher_No = '"+tbVoucherNo.get(index).getValue().toString()+"' ";
			session.createSQLQuery(queryVoucher).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void approveData(final int index)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String sql = "Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(tbDate.get(index).getValue())+"')";
			String fsl = session.createSQLQuery(sql).list().iterator().next().toString();
			String voucher =  "Voucher"+fsl;

			String queryInfo = " update tbDebitNote set "
					+ "iApproveFlag = 2, "
					+ "vApproveBy = '"+sessionBean.getUserName()+"', "
					+ "vApproveIp = '"+sessionBean.getUserIp()+"', "
					+ "dApproveTime = CURRENT_TIMESTAMP "
					+ "where vReferenceNo = '"+tbRefNo.get(index).getValue().toString()+"' ";
			session.createSQLQuery(queryInfo).executeUpdate();

			String queryVoucher = " update "+voucher+" set "
					+ "auditapproveflag = 2, "
					+ "approve_by = '"+sessionBean.getUserId()+"' "
					+ "where Voucher_No = '"+tbVoucherNo.get(index).getValue().toString()+"' ";
			session.createSQLQuery(queryVoucher).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void printedData(final int index)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String queryInfo = " update tbDebitNote set iPrint = 1"
					+ " where vReferenceNo = '"+tbRefNo.get(index).getValue().toString()+"' ";
			session.createSQLQuery(queryInfo).executeUpdate();
			tx.commit();
		}
		catch(Exception ex)
		{
			tx.rollback();
			showNotification("Warning!",""+ex,Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean checkAuthenAudit()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select vUserId from tbAuthoritySegregation where"
					+ " vUserId = '"+sessionBean.getUserId()+"' and vModuleId = '5' ";
			Iterator<?> iterMax = session.createSQLQuery(sql).list().iterator();
			if(iterMax.hasNext())
			{
				return true;
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private boolean checkAuthenApprove()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select vUserId from tbAuthoritySegregation where"
					+ " vUserId = '"+sessionBean.getUserId()+"' and vModuleId = '6' ";
			Iterator<?> iterMax = session.createSQLQuery(sql).list().iterator();
			if(iterMax.hasNext())
			{
				return true;
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private boolean checkAuthenCheque()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select vUserId from tbAuthoritySegregation where"
					+ " vUserId = '"+sessionBean.getUserId()+"' and vModuleId = '7' ";
			Iterator<?> iterMax = session.createSQLQuery(sql).list().iterator();
			if(iterMax.hasNext())
			{
				return true;
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}

	private void tableClear()
	{
		for(int i = 0; i < tbRefNo.size(); i++)
		{
			tbDate.get(i).setReadOnly(false);
			tbDate.get(i).setValue(null);
			tbDate.get(i).setReadOnly(true);
			tbRefNo.get(i).setValue("");
			tbVoucherNo.get(i).setValue("");
			tbPartyLedger.get(i).setValue("");
			tbPayStatus.get(i).setValue("");
			tbPayMode.get(i).setValue("");
			tbPayAginst.get(i).setValue("");
			tbAmount.get(i).setValue("");
			tbPreparedBy.get(i).setValue("");
			tbPreparedTime.get(i).setValue("");
			tbApproved.get(i).setValue(false);
			tbAudit.get(i).setValue(false);
			tbAudit.get(i).setEnabled(true);
			tbApproved.get(i).setEnabled(true);
			tbCQPrinted.get(i).setValue(false);
			tbCQPrinted.get(i).setEnabled(true);
			tbCQPrint.get(i).setEnabled(true);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		setWidth("1182px");
		setHeight("680px");

		lblBank = new Label("Cash/Bank Head : ");
		mainLayout.addComponent(lblBank, "top:20.0px; left:10.00px;");

		cmbBankHead = new ComboBox();
		cmbBankHead.setImmediate(true);
		cmbBankHead.setWidth("310px");
		cmbBankHead.setHeight("-1px");
		mainLayout.addComponent(cmbBankHead, "top:18.0px; left:140.0px;");

		tableDebitNote.setWidth("100%");
		tableDebitNote.setHeight("540px");
		tableDebitNote.setFooterVisible(true);
		tableDebitNote.setColumnCollapsingAllowed(true);

		tableDebitNote.addContainerProperty("SL#", Label.class , new Label());
		tableDebitNote.setColumnWidth("SL#",20);

		tableDebitNote.addContainerProperty("Date", PopupDateField.class , new PopupDateField());
		tableDebitNote.setColumnWidth("Date",82);

		tableDebitNote.addContainerProperty("Voucher No", Label.class , new Label());
		tableDebitNote.setColumnWidth("Voucher No",75);

		tableDebitNote.addContainerProperty("Ref. No", Label.class , new Label());
		tableDebitNote.setColumnWidth("Ref. No",80);
		tableDebitNote.setColumnCollapsed("Ref. No", true);

		tableDebitNote.addContainerProperty("Party/Ledger Name", Label.class , new Label());
		tableDebitNote.setColumnWidth("Party/Ledger Name",220);

		tableDebitNote.addContainerProperty("Pay. Sta.", Label.class , new Label());
		tableDebitNote.setColumnWidth("Pay. Sta.",35);

		tableDebitNote.addContainerProperty("Pay. Agai.", Label.class , new Label());
		tableDebitNote.setColumnWidth("Pay. Agai.",50);

		tableDebitNote.addContainerProperty("Pay. Mode", Label.class , new Label());
		tableDebitNote.setColumnWidth("Pay. Mode",50);

		tableDebitNote.addContainerProperty("Amount", Label.class , new Label(), null, null, Table.ALIGN_RIGHT);
		tableDebitNote.setColumnWidth("Amount",90);

		tableDebitNote.addContainerProperty("Prepared By", Label.class , new Label());
		tableDebitNote.setColumnWidth("Prepared By",70);

		tableDebitNote.addContainerProperty("Prepared Time", Label.class , new Label());
		tableDebitNote.setColumnWidth("Prepared Time",120);

		tableDebitNote.addContainerProperty("Audit", CheckBox.class , new CheckBox(),  null, null, Table.ALIGN_CENTER);
		tableDebitNote.setColumnWidth("Audit",25);

		tableDebitNote.addContainerProperty("App", CheckBox.class , new CheckBox(),  null, null, Table.ALIGN_CENTER);
		tableDebitNote.setColumnWidth("App",25);

		tableDebitNote.addContainerProperty("Details", NativeButton.class , new NativeButton());
		tableDebitNote.setColumnWidth("Details",30);

		tableDebitNote.addContainerProperty("CQ Print", NativeButton.class , new NativeButton());
		tableDebitNote.setColumnWidth("CQ Print",30);

		tableDebitNote.addContainerProperty("Printed", CheckBox.class , new CheckBox(),  null, null, Table.ALIGN_CENTER);
		tableDebitNote.setColumnWidth("Printed",25);

		mainLayout.addComponent(tableDebitNote, "top: 50px; left: 0px;");

		return mainLayout;
	}

	private void tableInitialise()
	{
		for(int i=0;i<15;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			tblblsl.add(ar,new Label());
			tblblsl.get(ar).setWidth("20px");
			tblblsl.get(ar).setValue(ar+1);

			tbDate.add(ar,new PopupDateField());
			tbDate.get(ar).setWidth("100%");
			tbDate.get(ar).setImmediate(true);
			tbDate.get(ar).setReadOnly(true);
			tbDate.get(ar).setDateFormat("dd-MM-yyyy");

			tbVoucherNo.add(ar,new Label());
			tbVoucherNo.get(ar).setWidth("100%");
			tbVoucherNo.get(ar).setImmediate(true);

			tbRefNo.add(ar,new Label());
			tbRefNo.get(ar).setWidth("100%");
			tbRefNo.get(ar).setImmediate(true);

			tbPartyLedger.add(ar,new Label());
			tbPartyLedger.get(ar).setWidth("100%");
			tbPartyLedger.get(ar).setImmediate(true);

			tbPayStatus.add(ar,new Label());
			tbPayStatus.get(ar).setWidth("100%");
			tbPayStatus.get(ar).setImmediate(true);

			tbPayMode.add(ar,new Label());
			tbPayMode.get(ar).setWidth("100%");
			tbPayMode.get(ar).setImmediate(true);

			tbPayAginst.add(ar,new Label());
			tbPayAginst.get(ar).setWidth("100%");
			tbPayAginst.get(ar).setImmediate(true);

			tbAmount.add(ar,new Label());
			tbAmount.get(ar).setWidth("100%");
			tbAmount.get(ar).setImmediate(true);

			tbPreparedBy.add(ar,new Label());
			tbPreparedBy.get(ar).setWidth("100%");
			tbPreparedBy.get(ar).setImmediate(true);

			tbPreparedTime.add(ar,new Label());
			tbPreparedTime.get(ar).setWidth("100%");
			tbPreparedTime.get(ar).setImmediate(true);

			tbAudit.add(ar,new CheckBox());
			tbAudit.get(ar).setWidth("100%");
			tbAudit.get(ar).setImmediate(true);
			tbAudit.get(ar).setDescription("Click here to audit.");
			tbAudit.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(tbAudit.get(ar).booleanValue() && !tbRefNo.get(ar).getValue().toString().isEmpty())
					{
						if(checkAuthenAudit())
						{
							saveBtnAction(1, ar, 1);
						}
						else
						{
							showNotification("Warning!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
							tbAudit.get(ar).setValue(false);
						}
					}
					else
					{
						tbAudit.get(ar).setValue(false);
					}
				}
			});

			tbApproved.add(ar,new CheckBox());
			tbApproved.get(ar).setWidth("100%");
			tbApproved.get(ar).setImmediate(true);
			tbApproved.get(ar).setDescription("Click here to approve.");
			tbApproved.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(tbApproved.get(ar).booleanValue() && !tbRefNo.get(ar).getValue().toString().isEmpty())
					{
						if(checkAuthenApprove())
						{
							saveBtnAction(2, ar, 2);
						}
						else
						{
							showNotification("Warning!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
							tbApproved.get(ar).setValue(false);
						}
					}
					else
					{
						tbApproved.get(ar).setValue(false);
					}
				}
			});

			tbDetailsPrint.add(ar,new NativeButton());
			tbDetailsPrint.get(ar).setWidth("100%");
			tbDetailsPrint.get(ar).setImmediate(true);
			tbDetailsPrint.get(ar).setDescription("Click to view details");
			tbDetailsPrint.get(ar).setIcon(new ThemeResource("../icons/preview.png"));
			tbDetailsPrint.get(ar).addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					if(!tbRefNo.get(ar).getValue().toString().isEmpty())
					{
						reportView(ar,0);
					}
				}
			});

			tbCQPrint.add(ar,new NativeButton());
			tbCQPrint.get(ar).setWidth("100%");
			tbCQPrint.get(ar).setImmediate(true);
			tbCQPrint.get(ar).setIcon(new ThemeResource("../icons/print.png"));
			tbCQPrint.get(ar).setDescription("Click to print cheque");
			tbCQPrint.get(ar).addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					if(!tbRefNo.get(ar).getValue().toString().isEmpty())
					{
						if(checkAuthenCheque())
						{
							reportView(ar,1);
						}
						else
						{
							showNotification("Warning!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
							tbCQPrinted.get(ar).setValue(false);
						}
					}
				}
			});

			tbCQPrinted.add(ar,new CheckBox());
			tbCQPrinted.get(ar).setWidth("100%");
			tbCQPrinted.get(ar).setImmediate(true);
			tbCQPrinted.get(ar).setDescription("Click here if Cheque Print completed.");
			tbCQPrinted.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(tbCQPrinted.get(ar).booleanValue() && !tbRefNo.get(ar).getValue().toString().isEmpty())
					{
						if(checkAuthenCheque())
						{
							saveBtnAction(3, ar, 3);
						}
						else
						{
							showNotification("Warning!","You aren't authorized.",Notification.TYPE_WARNING_MESSAGE);
							tbCQPrinted.get(ar).setValue(false);
						}
					}
					else
					{
						tbCQPrinted.get(ar).setValue(false);
					}
				}
			});

			tableDebitNote.addItem(new Object[]{tblblsl.get(ar), tbDate.get(ar), tbVoucherNo.get(ar),
					tbRefNo.get(ar), tbPartyLedger.get(ar), tbPayStatus.get(ar),  tbPayMode.get(ar),
					tbPayAginst.get(ar), tbAmount.get(ar), tbPreparedBy.get(ar), tbPreparedTime.get(ar),tbAudit.get(ar),
					tbApproved.get(ar), tbDetailsPrint.get(ar),tbCQPrint.get(ar),tbCQPrinted.get(ar)},ar);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void reportView(int ar, int flag)
	{
		ReportDate reportTime = new ReportDate();
		String query = "", jasper = "";
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("Date",reportTime.getTime);

			if(flag==0)
			{
				query = "select vSupplierId,vSupplierName,(select address from tbSupplierInfo si" +
						" where dn.vSupplierId = si.supplierId )adress,dLastPaydate,mAmount,vReferenceNo,mCurrentBalance," +
						" dDate,vPoNo,dPoDate,mPoAmount,vLedgerName,vChequeNo,dChequeDate,vPaymentAgainst,vPaymentStatus," +
						" vPreparedBy,vApproveBy from tbDebitNote dn where vReferenceNo = '"+tbRefNo.get(ar).getValue().toString()+"' ";
				jasper = "report/account/voucher/rptDebitNote.jasper";
			}
			if(flag==1)
			{
				query = "select dDate,vSupplierName vNarration,mAmount,dbo.number(mAmount) inword from tbDebitNote where"
						+ " vReferenceNo = '"+tbRefNo.get(ar).getValue().toString()+"'";
				jasper = "report/account/Cheque.jasper";
			}

			hm.put("urlLink", this.getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", ""));
			hm.put("sql", query);
			Window win = new ReportViewer(hm,jasper,
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false); 
			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}