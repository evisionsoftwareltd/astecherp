package acc.appform.DoSalesModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SalesInvoice extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table challanTable = new Table();
	private Table table = new Table();

	private TextField txtInvoiceNoFind = new TextField();

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblPartyAddress;
	private TextRead txtPartyAddress;

	private Label lblPartyMobile;
	private TextRead txtPartyMobile;

	private Label lblSalesBillNo;
	private TextRead txtSalesBillNo;

	private Label lblSalesVoucherNo;
	private TextRead txtSalesVoucherNo;

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblTotalAmount;
	private TextRead txtTotalAmount;

	private Label lblVat;
	private AmountField txtVat;
	private AmountField txtVatAmount;

	private Label lblNetAmount;
	private TextRead txtNetAmount;

	private Label lblRemarks;
	private TextField txtRemarks;

	String partyLedger = "";

	//Table Value
	private ArrayList<CheckBox> tbchkChallan = new ArrayList<CheckBox>();
	private ArrayList<Label> tblblDelChaNo = new ArrayList<Label>();
	private ArrayList<Label> tblblChaNo = new ArrayList<Label>();
	private ArrayList<Label> tblblChaDate = new ArrayList<Label>();
	private ArrayList<Label> tblblChaQty = new ArrayList<Label>();

	//Table Value
	private ArrayList<Label> tblblChallan = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanMerge = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanDate = new ArrayList<Label>();
	private ArrayList<Label> tblblProCode = new ArrayList<Label>();
	private ArrayList<Label> tblblProLedger = new ArrayList<Label>();
	private ArrayList<Label> tblblItemName = new ArrayList<Label>();
	private ArrayList<Label> tblblItemUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tbtxtChallanQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtAmount = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtRate = new ArrayList<TextRead>();
	private ArrayList<TextField> tbtxtRemarks = new ArrayList<TextField>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete","Refresh","Find","","Preview","","Exit");

	private Boolean isUpdate = false;
	private Boolean isFind = false;

	double totalAmount = 0;

	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat decimal = new DecimalFormat("#0");
	private DecimalFormat dfRate = new DecimalFormat("#0.000");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat recDateFormat = new SimpleDateFormat("MM/yy");
	private SimpleDateFormat bdFormat = new SimpleDateFormat("dd-MM-yy");

	int maxId = 0;
	private String ChallanNO = "";
	private String BillNO = "";
	private String ChallanNOAll = "";

	private String InsertProductLedger = "";
	private String InsertProductRemarks = "";

	public SalesInvoice(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setCaption("SALES INVOICE :: " + sessionBean.getCompany());

		buildMainLayout();		
		setContent(mainLayout);

		chaTableinitialise();

		tableinitialise();

		componentIni(true);
		btnIni(true);

		setEventAction();
		focusEnter();
		authenticationCheck();

		cmbPartyData();

		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				componentIni(false);
				btnIni(false);
				txtClear();

				cmbPartyName.focus();
				//selectBillNo();
				//txtSalesVoucherNo.setValue("IV-NO-"+getInvoice());
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				formValidation();
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(isFind)
				{
					isUpdate = true;
					componentIni(false);
					btnIni(false);
					cmbPartyName.focus();
				}
				else
				{
					showNotification("Warning!","Please find data for edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtSalesVoucherNo.getValue().toString().isEmpty())
				{
					if(sessionBean.isAdmin() || sessionBean.isSuperAdmin())
					{
						deleteBtnAction();
					}
					else
					{
						showNotification("Warning!","You are not authorized.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				txtInvoiceNoFind.setValue("");
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				
				txtTableClear();
				if(cmbPartyName.getValue()!=null)
				{
					setPartyInfo();
					clearChallanData();
					//selectBillNo();
					//txtSalesVoucherNo.setValue("IV-NO-"+getInvoice());
					setPartyChallanData();
					if(cmbPartyName.getValue().toString().equalsIgnoreCase("2"))
					{
					txtRemarks.setValue("1. Photocopy of VAT & Delivery Challan Enclosed.									  2. Bank Information : NCC Bank Limited, Account No: 0003-0210029081, Agrabad Branch, Chattogram");
					}
					else
					{
						txtRemarks.setValue("1. Photocopy of VAT & Delivery Challan Enclosed.");
					}
				}
				else
				{
					txtPartyAddress.setValue("");
				}
			}
		});

		txtVat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSumAllAmount();
			}
		});

		button.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBillEvent();
			}
		});
	}

	private void tableFocusEnter()
	{
		int totalItem = 0;
		for(int i=0; i<tblblItemName.size(); i++)
		{
			if(!tblblProCode.get(i).getValue().toString().isEmpty())
			{totalItem++;}
		}
		table.setColumnFooter("Item Code", "Total Item = "+totalItem);
	}

	public boolean checkExistChallan(String ChallanNo)
	{
		boolean ret = true;
		for(int i=0; i<tblblChallan.size(); i++)
		{
			if(ChallanNo.equals(tblblChallan.get(i).getValue().toString()))
			{
				ret = false;
				break;
			}
		}
		return ret;
	}

	private void findButtonEvent() 
	{
		Window win = new SalesInvoiceFind(sessionBean, txtInvoiceNoFind,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtInvoiceNoFind.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtInvoiceNoFind.getValue().toString());
					isFind=true;
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String BillNo) 
	{
		Object[] element = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String sql = " SELECT sd.vPartyId,sd.vBillNo,sd.vVoucherNo,sd.dInvoiceDate,sd.vDeliveryChallanNo," +
					" fsi.vChallanNo,fsi.vChallanNoMerge,fsi.dChallanDate,fsi.vProductId,fsi.vLedgerId," +
					" fsi.vProductName,fsi.vProductUnit,fsi.mChallanQty/*,fsi.mDealerPrice*/,0,fsi.mAmount," +
					" sd.mCommission,sd.mVat,sd.mTruckFare,sd.vRemarks,sd.vBillSerial from" +
					" [funSalesInvoice] ((select REPLACE(vDeliveryChallanNo,'''','') from" +
					" tbSalesInvoiceInfo where vBillNo = '"+BillNo+"')) fsi inner join tbSalesInvoiceInfo" +
					" sd on fsi.vChallanNo in (select * from dbo.Split(Replace(sd.vDeliveryChallanNo,'''','')))" +
					" order by fsi.vChallanNo ";
			List<?> led = session.createSQLQuery(sql).list();

			int i = 0;
			for (Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				element = (Object[]) iter.next();

				if(i==0)
				{
					cmbPartyName.setValue(element[0]);

					txtSalesBillNo.setValue(element[1].toString());
					txtSalesVoucherNo.setValue(element[2].toString());
					dDate.setValue(element[3]);

					clearChallanData();
					setPartyChallanDataFind(element[4].toString());

					ChallanNOAll = element[4].toString();

					maxId = Integer.parseInt(element[19].toString());

					txtTableClear();
				}

				//Table info
				tblblChallan.get(i).setValue(element[5].toString());
				tblblChallanMerge.get(i).setValue(element[6].toString());
				tblblChallanDate.get(i).setValue(element[7].toString());
				tblblProCode.get(i).setValue(element[8].toString());
				tblblProLedger.get(i).setValue(element[9].toString());
				tblblItemName.get(i).setValue(element[10].toString());
				tblblItemUnit.get(i).setValue(element[11].toString());
				tbtxtChallanQty.get(i).setValue(decimal.format(element[12]));
				tbtxtRate.get(i).setValue(dfRate.format(element[13]));
				tbtxtAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[14].toString())));

				if(tblblProCode.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				i++;
			}

			sumAmount();
			txtVat.setValue(df.format(element[16]));
			txtRemarks.setValue(element[18].toString());
		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setPartyChallanDataFind(String ChallanNoAll)
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" select distinct dcd.vChallanNo,dci.dChallanDate,SUM(dcd.mChallanQty)" +
					" mChallanQty FROM tbDeliveryChallanInfo dci inner join tbDeliveryChallanDetails" +
					" dcd on dci.vChallanNo=dcd.vChallanNo where dci.vChallanNo in ("+ChallanNoAll+")" +
					" group by dcd.vChallanNo,dci.dChallanDate ").list();
			int i = 0;
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tbchkChallan.get(i).setValue(true);
				tblblChaNo.get(i).setValue(element[0].toString());
				tblblChaDate.get(i).setValue(bdFormat.format(element[1]));
				tblblChaQty.get(i).setValue(decimal.format(element[2]));
				if(i==tblblChallan.size()-1)
				{
					chaTableRowAdd(i+1);
				}
				i++;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void cmbPartyData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" Select partyCode,partyName from tbPartyInfo where isActive='1' ORDER by partyName ").list();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setPartyInfo()
	{
		txtPartyAddress.setValue("");
		txtPartyMobile.setValue("");
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" Select partyCode,address,mobile,ledgerCode from tbPartyInfo where partyCode='"+cmbPartyName.getValue().toString()+"' ").list();
			for(Iterator<?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				txtPartyAddress.setValue(element[1].toString());
				txtPartyMobile.setValue(element[2].toString());
				partyLedger = element[3].toString();
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void setPartyChallanData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" select distinct dcd.vChallanNo,CONVERT(date,dci.dChallanDate) dcDate,SUM(dcd.mChallanQty)" +
					" mChallanQty,ISNULL(dci.vDelChallanNo,'')vDelChallanNo FROM tbDeliveryChallanInfo dci inner join tbDeliveryChallanDetails" +
					" dcd on dci.vChallanNo = dcd.vChallanNo where dci.vPartyId = '"+cmbPartyName.getValue().toString()+"'" +
					" and dci.status = '1' group by dcd.vChallanNo,dci.dChallanDate,ISNULL(dci.vDelChallanNo,'')" +
					" order by CONVERT(date,dci.dChallanDate),dcd.vChallanNo ").list();
			int i = 0;
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tblblChaNo.get(i).setValue(element[0].toString());
				tblblChaDate.get(i).setValue(bdFormat.format(element[1]));
				tblblChaQty.get(i).setValue(decimal.format(element[2]));
				tblblDelChaNo.get(i).setValue(element[3].toString());

				if(i==tblblChaNo.size()-1)
				{
					chaTableRowAdd(i+1);
				}
				i++;
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(!dDate.getValue().toString().equals(""))
			{
				if(validTableSelect())
				{
					saveButtonEvent();
				}
				else
				{
					showNotification("Warning!","Select Challan No",Notification.TYPE_WARNING_MESSAGE);
					tbchkChallan.get(0).focus();
				}
			}
			else
			{
				showNotification("Warning!","Enter valid date",Notification.TYPE_WARNING_MESSAGE);
				dDate.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select party name",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	public boolean validTableSelect()
	{
		boolean ret = false;
		for(int i=0; i<tblblChallan.size(); i++)
		{
			if(Double.parseDouble(tbtxtRate.get(i).getValue().toString().isEmpty()?"0.0":tbtxtRate.get(i).getValue().toString().replaceAll(",", "").toString())>0)
			{
				ret = true;
				break;
			}
		}
		return ret;
	}

	private void saveButtonEvent()
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
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						if(deleteData(session,tx))
						{
							if(insertData(session,tx)){
								showNotification("All information update successfully.");
								isUpdate=false;
								isFind=false;
								btnIni(true);
								componentIni(true);
								previewBillEvent();
								button.btnNew.focus();
							}
						}


					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						selectBillNo();
						txtSalesVoucherNo.setValue("IV-NO-"+getInvoice());
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						if(insertData( session,tx)){
							showNotification("All information saved successfully.");
							btnIni(true);
							componentIni(true);
							previewBillEvent();
							button.btnNew.focus();
						}
					}
				}
			});
		}
	}
	private void deleteBtnAction()
	{
		MessageBox mb = new MessageBox(this.getParent().getWindow(),"Are you sure?", MessageBox.Icon.QUESTION,"Do you want to delete  information?",
				new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener() 
		{
			public void buttonClicked(ButtonType buttonType) 
			{
				if(buttonType == ButtonType.YES)
				{
					Session session = SessionFactoryUtil.getInstance().openSession();
					Transaction tx = session.beginTransaction();
					if(deleteData(session,tx)){
						if(tx!=null){
							tx.commit();
						}
						if(session!=null){
							session.close();
						}
					}
					isUpdate = false;			
					componentIni(true);
					btnIni(true);
					txtClear();
					showNotification("Successfully","Delete data",Notification.TYPE_HUMANIZED_MESSAGE);


				}
			}
		});
	}

	private String selectVoucher()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dDate.getValue())+"') as voucher").list().iterator().next().toString();
		return voucher;
	}

	private String getInvoice()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String invoice = session.createSQLQuery(" Select cast(isnull(max(cast(replace(Voucher_No, 'IV-NO-', '')as int))+1, 1)as varchar) from Voucher"+selectVoucher()+" where Voucher_No like 'IV-NO-%' ").list().iterator().next().toString();
		return invoice;
	}

	public void selectBillNo()
	{
		String BillNo = "";

		String partyId = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" select iAutoId,vReceiptPrefix from tbReceiptPrefixInfo where" +
					" iAutoId in ('4') ").list();

			/*Iterator<?> iterMax = session.createSQLQuery(" SELECT ISNULL((MAX(CAST(SUBSTRING(vBillSerial,CHARINDEX('-'," +
					" vBillSerial,9)+1,50) AS INT))+1),1) FROM tbSalesInvoiceInfo ").list().iterator();*/
			/*Iterator<?> iterMax = session.createSQLQuery(" select ISNULL(MAX(CAST(vBillSerial as int)),0)+1 from tbSalesInvoiceInfo" +
					" where vPartyId= '"+cmbPartyName.getValue().toString()+"' ").list().iterator();*/

			Iterator<?> iterMax = session.createSQLQuery(" select ISNULL(MAX(CAST(vBillSerial as int)),0)+1 from tbSalesInvoiceInfo").list().iterator();

			if(iterMax.hasNext())
				maxId = Integer.parseInt(iterMax.next().toString());

			if(cmbPartyName.getValue()!=null)
			{
				Iterator<?> partyMax = session.createSQLQuery(" select isnull(max(cast(REVERSE(subString(REVERSE(vBillNo),0," +
						"CHARINDEX('/',REVERSE(vBillNo))))as int)),'0')+1 from tbSalesInvoiceInfo where vPartyId" +
						" = '"+cmbPartyName.getValue().toString()+"'  ").list().iterator();



				if(partyMax.hasNext())
					partyId = "/"+Integer.valueOf(partyMax.next().toString()).toString();
			}

			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				if(element[0].toString().equals("4"))
					BillNo = element[1].toString();
			}
		}
		catch (Exception e)
		{

		}

		txtSalesBillNo.setValue(BillNo+"-"+recDateFormat.format(new Date())+"-"+maxId+partyId);

		BillNO = txtSalesBillNo.getValue().toString();
	}

	private void sumAmount()
	{
		double totalItem = 0;
		double totalQty = 0;
		totalAmount=0;

		for(int i = 0; i<tbtxtAmount.size(); i++)
		{
			if(!tbtxtAmount.get(i).getValue().toString().equals(""))
			{
				if(Double.parseDouble(tbtxtAmount.get(i).getValue().toString().replaceAll(",", ""))>0)
				{
					totalQty = totalQty + Double.parseDouble(tbtxtChallanQty.get(i).getValue().toString().replaceAll(",", ""));
					totalAmount = totalAmount + Double.parseDouble(tbtxtAmount.get(i).getValue().toString().replaceAll(",", ""));

					totalItem++;
				}
			}
		}

		table.setColumnFooter("Challan No", "Total Item=");
		table.setColumnFooter("Item Name", decimal.format(totalItem)+"");

		table.setColumnFooter("Unit", "Ttl Qty=");
		table.setColumnFooter("Qty", new CommaSeparator().setComma(totalQty)+"");

		table.setColumnFooter("Rate", "Ttl Amt=");
		table.setColumnFooter("Amount", (new CommaSeparator().setComma(totalAmount))+"");
	}

	public void totalSumAllAmount()
	{
		txtTotalAmount.setValue((new CommaSeparator().setComma(totalAmount)));

		double totalAmount = Double.parseDouble(txtTotalAmount.getValue().toString().isEmpty()?"0":txtTotalAmount.getValue().toString().replaceAll(",", ""));

		double totalVat = Double.parseDouble(txtVat.getValue().toString().isEmpty()?"0":txtVat.getValue().toString().replaceAll(",", ""));

		txtVatAmount.setValue((new CommaSeparator().setComma((totalVat*totalAmount)/100)));

		double vatAmount = Double.parseDouble(txtVatAmount.getValue().toString().isEmpty()?"0":txtVatAmount.getValue().toString().replaceAll(",", ""));

		txtNetAmount.setValue(new CommaSeparator().setComma(vatAmount+totalAmount));
	}

	public void setChallanData()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> list = session.createSQLQuery(" SELECT * from [funSalesInvoice] ('"+ChallanNOAll.replaceAll("'", "")+"') order by vChallanNo ").list();
			int i = 0;
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tblblChallan.get(i).setValue(element[5].toString());
				tblblChallanMerge.get(i).setValue(element[15].toString());
				tblblChallanDate.get(i).setValue(element[6].toString());
				tblblProCode.get(i).setValue(element[7].toString());
				tblblProLedger.get(i).setValue(element[14].toString());
				tblblItemName.get(i).setValue(element[8].toString());
				tblblItemUnit.get(i).setValue(element[9].toString());
				tbtxtChallanQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[10].toString())));
				tbtxtRate.get(i).setValue(dfRate.format(element[11]));
				tbtxtAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[12].toString())));

				if(i==tblblChallan.size()-1)
				{
					tableRowAdd(i+1);
				}
				i++;
			}
			tableFocusEnter();
		}
		catch(Exception ex)
		{

		}
	}

	private boolean insertData(Session session,Transaction tx)
	{
		String voucherNo = "";
		String voucher = "";
		String udFlag = "";
		String partyNarration = "";
		String vatNarration = "";
		if(isUpdate)
		{
			BillNO = txtSalesBillNo.getValue().toString();
			voucherNo = txtSalesVoucherNo.getValue().toString();
		}
		else
		{
			selectBillNo();
			voucherNo=("IV-NO-"+getInvoice());
		}

		voucher = "voucher"+selectVoucher();
		partyNarration = "Sales Against Bill No: "+BillNO+" ";

		try
		{
			String InsertInfo =" INSERT into tbSalesInvoiceInfo values(" +
					//" '"+BillNO.trim()+"'," +
					" '"+txtSalesBillNo.getValue().toString()+"'," +
					" '"+maxId+"'," +
					" '"+dFormat.format(dDate.getValue()).trim()+"'," +
					" '"+voucherNo.trim()+"'," +
					" '"+cmbPartyName.getValue().toString().trim()+"'," +
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString().trim())+"'," +
					" '"+(txtPartyAddress.getValue().toString().trim().isEmpty()?"":txtPartyAddress.getValue().toString().trim())+"'," +
					" '"+(txtPartyMobile.getValue().toString().trim().isEmpty()?"":txtPartyMobile.getValue().toString().trim())+"'," +
					" '"+partyLedger.trim()+"'," +
					" '"+ChallanNOAll.replaceAll("'", "''").trim()+"'," +
					" '"+totalAmount+"'," +
					" '0'," +
					" '0'," +
					" '"+(txtTotalAmount.getValue().toString().trim().isEmpty()?"0":txtTotalAmount.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtVat.getValue().toString().trim().isEmpty()?"0":txtVat.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtVatAmount.getValue().toString().trim().isEmpty()?"0":txtVatAmount.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '0'," +
					" '"+(txtNetAmount.getValue().toString().isEmpty()?"0":txtNetAmount.getValue().toString().replace(",", "").trim())+"'," +
					" '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString().trim())+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'1') ";

			//System.out.println("InsertInfo : "+InsertInfo);
			session.createSQLQuery(InsertInfo).executeUpdate();

			String partyInvoice = " INSERT into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype," +
					" TransactionWith,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill," +
					" attachChequeBill,audit_by,approve_by) values(" +
					" '"+voucherNo+"'," +
					" '"+dFormat.format(dDate.getValue()).trim()+"'," +
					" '"+partyLedger.trim()+"'," +
					" '"+partyNarration+"'," +
					" '"+txtNetAmount.getValue().toString().replace(",", "").trim()+"'," +
					" '0.00'," +
					" 'ils'," +
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
					" 'U-3'," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '0'," +
					" '0'," +
					" ''," +
					" '')";

			//System.out.println("partyInvoice : "+partyInvoice);
			session.createSQLQuery(partyInvoice).executeUpdate();

			if(Double.parseDouble("0"+txtVatAmount.getValue().toString().replaceAll(",", ""))>0)
			{
				vatNarration = "Sales Against Bill No: "+BillNO+", VAT: "+txtVat.getValue().toString().replace(",", "")+"% of "+txtTotalAmount.getValue().toString().replace(",", "");
				String vatInvoice = " INSERT into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype," +
						" TransactionWith,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill," +
						" attachChequeBill,audit_by,approve_by) values(" +
						" '"+voucherNo+"'," +
						" '"+dFormat.format(dDate.getValue()).trim()+"'," +
						" 'AL127'," +
						" '"+vatNarration+"'," +
						" '0.00'," +
						" '"+txtVatAmount.getValue().toString().replace(",", "")+"'," +
						" 'ils'," +
						" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
						" 'U-3'," +
						" '1'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '')";

				//System.out.println("vatInvoice : "+vatInvoice);
				session.createSQLQuery(vatInvoice).executeUpdate();
			}

			List<?> list = session.createSQLQuery("select dci.vChallanNo,dci.dChallanDate,dcd.vProductId,dcd.vProductName,dcd.vProductUnit,"+
					" dcd.mChallanQty,dcd.mProductRate,(dcd.mChallanQty*dcd.mProductRate) mAmount from tbDeliveryChallanDetails dcd"+ 
					" inner join tbDeliveryChallanInfo dci on dcd.vChallanNo=dci.vChallanNo where dcd.vChallanNo in ("+ChallanNOAll.trim()+") ").list();

			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				getChallanDetails(element[1].toString(),element[5].toString());

				String sql = "insert into tbSalesInvoiceDetails values (" +
						//" '"+BillNO.trim()+"'," +
						" '"+txtSalesBillNo.getValue().toString()+"'," +
						" '"+dFormat.format(dDate.getValue()).trim()+"'," +
						" '"+voucherNo.trim()+"'," +
						" '"+element[0].toString()+"'," +
						" '"+element[1]+"'," +
						" '"+InsertProductLedger+"'," +
						" '"+element[2].toString()+"'," +
						" '"+element[3].toString().replaceAll("'", "''")+"'," +
						" '"+element[4].toString()+"'," +
						" '"+element[5].toString()+"'," +
						" '"+element[6].toString()+"'," +
						" '"+element[7].toString()+"'," +
						" '"+InsertProductRemarks+"' )";
				//System.out.println("InsertDetails: "+sql);
				session.createSQLQuery(sql).executeUpdate();
			}

			for(int i = 0; i<tblblChallan.size(); i++)
			{
				if(!tblblChallan.get(i).getValue().equals("") && Double.parseDouble((tbtxtRate.get(i).getValue().toString().isEmpty()?"0.0":tbtxtRate.get(i).getValue().toString().replace(",", "")))>0)
				{
					String productNarration= "Sales Against Bill No: "+BillNO+" ";

					String productInvoice = " INSERT into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype," +
							" TransactionWith,costId,chqClear,userId,userIp,entryTime,auditapproveflag,companyId,attachBill," +
							" attachChequeBill,audit_by,approve_by) values(" +
							" '"+voucherNo+"'," +
							" '"+dFormat.format(dDate.getValue()).trim()+"'," +
							" '"+tblblProLedger.get(i).getValue().toString().trim()+"'," +
							" '"+productNarration+"'," +						
							" '0.00'," +
							" '"+tbtxtAmount.get(i).getValue().toString().replace(",", "").trim()+"'," +
							" 'ils'," +
							" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
							" 'U-3'," +
							" '1'," +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '2'," +
							" '"+sessionBean.getCompanyId()+"',"+
							" '0'," +
							" '0'," +
							" ''," +
							" '')";

					//System.out.println("productInvoice : "+productInvoice);
					session.createSQLQuery(productInvoice).executeUpdate();

					if(!isUpdate)
					{ udFlag = "New"; }
					else
					{ udFlag = "Update"; }

					String udInvoice = "Insert into tbUdSalesInvoice values ( " +
							" '"+BillNO.trim()+"'," +
							" '"+dFormat.format(dDate.getValue()).trim()+"'," +
							" '"+cmbPartyName.getValue().toString().trim()+"'," +
							" '"+tblblChallan.get(i).getValue().toString().trim()+"'," +
							" '"+tblblProCode.get(i).getValue().toString().trim()+"'," +
							" '"+tbtxtChallanQty.get(i).getValue().toString().trim().replace(",", "")+"'," +
							" '"+tbtxtRate.get(i).getValue().toString().trim().replace(",", "")+"'," +
							" '"+totalAmount+"'," +
							" '0'," +
							" '"+(txtVat.getValue().toString().trim().isEmpty()?"0":txtVat.getValue().toString().replaceAll(",", "").trim())+"'," +
							" '0'," +
							" '"+(txtNetAmount.getValue().toString().isEmpty()?"0":txtNetAmount.getValue().toString().replace(",", "").trim())+"'," +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+udFlag+"') ";

					//System.out.println("udInvoice: "+udInvoice);
					session.createSQLQuery(udInvoice).executeUpdate();
				}
			}

			for(int i=0;i<tblblChaNo.size();i++)
			{
				if(!tblblChaNo.get(i).getValue().toString().isEmpty())
				{
					if(tbchkChallan.get(i).booleanValue())
					{
						String updateStatus = " Update tbDeliveryChallanInfo set status = '0' where " +
								"vChallanNo = '"+tblblChaNo.get(i).getValue().toString().trim()+"' ";
						session.createSQLQuery(updateStatus).executeUpdate();
					}
				}
			}	
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			if(tx!=null){
				tx.rollback();
			}
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}

	private void getChallanDetails(String Challan,String ProductId)
	{
		for(int i=0; i<tblblChallan.size(); i++)
		{
			if(!tblblChallan.get(i).getValue().toString().isEmpty())
			{
				if(tblblProCode.get(i).getValue().toString().equalsIgnoreCase(ProductId))
				{
					InsertProductLedger = tblblProLedger.get(i).getValue().toString();
					InsertProductRemarks = tbtxtRemarks.get(i).getValue().toString();
				}
			}
		}
	}

	private boolean deleteData(Session session,Transaction tx)
	{
		String voucher = "voucher"+selectVoucher();
		try
		{
			String delFormSql = " delete from tbSalesInvoiceInfo where vBillNo='"+txtSalesBillNo.getValue().toString().trim()+"' ";
			String delTableSql = " delete from tbSalesInvoiceDetails where vBillNo='"+txtSalesBillNo.getValue().toString().trim()+"' ";
			String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtSalesVoucherNo.getValue().toString().trim()+"' ";
			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			session.createSQLQuery(delVoucherSql).executeUpdate();
			return true;
		}
		catch(Exception exp)
		{
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void validSelect(int ar)
	{
		for(int i=0; i<tbchkChallan.size(); i++)
		{
			if(ar!=i)
			{
				tbchkChallan.get(i).setValue(false);
			}
		}
	}

	private void getChallanNo()
	{
		for(int i=0; i<tbchkChallan.size(); i++)
		{
			if(tbchkChallan.get(i).booleanValue()==true)
			{
				ChallanNO = "'"+tblblChaNo.get(i).getValue().toString()+"'"+","+ChallanNO;

				ChallanNOAll = ChallanNO.substring(0, ChallanNO.length()-1);
			}
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		this.setWidth("990px");
		this.setHeight("570px");
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:20.0px; left:20.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbPartyName, "top:16.0px; left:100.0px;");

		lblPartyAddress = new Label("Address :");
		lblPartyAddress.setWidth("-1px");
		lblPartyAddress.setHeight("-1px");
		mainLayout.addComponent(lblPartyAddress, "top:45.0px; left:20.0px;");

		txtPartyAddress = new TextRead();
		txtPartyAddress.setImmediate(false);
		txtPartyAddress.setWidth("250px");
		txtPartyAddress.setHeight("47px");
		mainLayout.addComponent(txtPartyAddress, "top:42.0px; left:101.0px;");

		lblPartyMobile = new Label("Mobile :");
		lblPartyMobile.setWidth("-1px");
		lblPartyMobile.setHeight("-1px");
		mainLayout.addComponent(lblPartyMobile, "top:95.0px; left:20.0px;");

		txtPartyMobile = new TextRead();
		txtPartyMobile.setImmediate(false);
		txtPartyMobile.setWidth("250px");
		txtPartyMobile.setHeight("22px");
		mainLayout.addComponent(txtPartyMobile, "top:92.0px; left:101.0px;");

		lblSalesBillNo = new Label("Bill No :");
		lblSalesBillNo.setWidth("-1px");
		lblSalesBillNo.setHeight("-1px");
		mainLayout.addComponent(lblSalesBillNo, "top:20.0px; left:380.0px;");

		txtSalesBillNo = new TextRead();
		txtSalesBillNo.setImmediate(false);
		txtSalesBillNo.setWidth("150px");
		txtSalesBillNo.setHeight("22px");
		mainLayout.addComponent(txtSalesBillNo, "top:18.0px; left:460.0px;");

		lblSalesVoucherNo = new Label("Voucher No :");
		lblSalesVoucherNo.setWidth("-1px");
		lblSalesVoucherNo.setHeight("-1px");
		mainLayout.addComponent(lblSalesVoucherNo, "top:45.0px; left:380.0px;");

		txtSalesVoucherNo = new TextRead();
		txtSalesVoucherNo.setImmediate(false);
		txtSalesVoucherNo.setWidth("150px");
		txtSalesVoucherNo.setHeight("22px");
		mainLayout.addComponent(txtSalesVoucherNo, "top:44.0px; left:460.0px;");

		lblDate = new Label("Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:72.0px; left:380.0px;");

		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:70.0px; left:460.0px;");

		challanTable.setWidth("330px");
		challanTable.setHeight("120px");
		challanTable.setColumnCollapsingAllowed(true);

		challanTable.addContainerProperty("Select", CheckBox.class, new CheckBox());
		challanTable.setColumnWidth("Select", 38);

		challanTable.addContainerProperty("Challan No", Label.class, new Label());
		challanTable.setColumnWidth("Challan No", 125);

		challanTable.addContainerProperty("Sys Challan No", Label.class, new Label());
		challanTable.setColumnWidth("Sys Challan No", 125);

		challanTable.addContainerProperty("Date", Label.class, new Label());
		challanTable.setColumnWidth("Date", 50);

		challanTable.addContainerProperty("Qty", Label.class, new Label());
		challanTable.setColumnWidth("Qty", 40);

		challanTable.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_RIGHT});
		challanTable.setColumnCollapsingAllowed(true);
		challanTable.setColumnCollapsed("Sys Challan No", true);

		mainLayout.addComponent(challanTable, "top:3.0px; left:640.0px;");

		table.setWidth("980px");
		table.setHeight("230px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Challan No", Label.class, new Label());
		table.setColumnWidth("Challan No", 100);

		table.addContainerProperty("Challan Merge", Label.class, new Label());
		table.setColumnWidth("Challan Merge", 100);
		table.setColumnCollapsed("Challan Merge", true);

		table.addContainerProperty("Challan Date", Label.class, new Label());
		table.setColumnWidth("Challan Date", 50);
		table.setColumnCollapsed("Challan Date", true);

		table.addContainerProperty("Item Code", Label.class, new Label());
		table.setColumnWidth("Item Code", 60);

		table.addContainerProperty("Item Ledger", Label.class, new Label());
		table.setColumnWidth("Item Ledger", 30);
		table.setColumnCollapsed("Item Ledger", true);

		table.addContainerProperty("Item Name", Label.class, new Label());
		table.setColumnWidth("Item Name", 375);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 50);

		table.addContainerProperty("Challan Qty", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Challan Qty", 90);

		table.addContainerProperty("Rate", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rate", 60);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount", 110);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 75);
		table.setColumnCollapsed("Item Code", true);

		mainLayout.addComponent(table,"top:130.0px; left:5.0px; ");

		lblTotalAmount = new Label("Total Amount :");
		lblTotalAmount.setWidth("-1px");
		lblTotalAmount.setHeight("-1px");
		mainLayout.addComponent(lblTotalAmount,"top:395.0px; left:680.0px;");

		txtTotalAmount = new TextRead(1);
		txtTotalAmount.setImmediate(true);
		txtTotalAmount.setWidth("150px");
		txtTotalAmount.setHeight("-1px");
		mainLayout.addComponent(txtTotalAmount, "top:393.0px; right:50.0px;");

		Label lblTotalTk = new Label("TK.");
		lblTotalTk.setHeight("-1px");
		lblTotalTk.setWidth("-1px");
		mainLayout.addComponent(lblTotalTk, "top:395.0px; right:25.0px;");

		lblVat = new Label("VAT (%) :");
		lblVat.setWidth("-1px");
		lblVat.setHeight("-1px");
		mainLayout.addComponent(lblVat,"top:420.0px; left:680.0px;");

		txtVat = new AmountField();
		txtVat.setImmediate(true);
		txtVat.setValue("15");
		txtVat.setWidth("40px");
		txtVat.setHeight("-1px");
		mainLayout.addComponent(txtVat, "top:418.0px; right:160.0px;");

		Label lblVatTk = new Label("TK.");
		lblVatTk.setHeight("-1px");
		lblVatTk.setWidth("-1px");
		mainLayout.addComponent(lblVatTk, "top:420.0px; right:25.0px;");

		txtVatAmount = new AmountField();
		txtVatAmount.setImmediate(true);
		txtVatAmount.setWidth("100px");
		txtVatAmount.setHeight("-1px");
		mainLayout.addComponent(txtVatAmount, "top:418.0px; right:50.0px;");

		Label lblTtlTk = new Label("TK.");
		lblTtlTk.setHeight("-1px");
		lblTtlTk.setWidth("-1px");
		mainLayout.addComponent(lblTtlTk, "top:420.0px; right:25.0px;");

		lblNetAmount = new Label("Net Amount :");
		lblNetAmount.setWidth("-1px");
		lblNetAmount.setHeight("-1px");
		mainLayout.addComponent(lblNetAmount,"top:445.0px; left:680.0px;");

		txtNetAmount = new TextRead(1);
		txtNetAmount.setImmediate(true);
		txtNetAmount.setWidth("150px");
		txtNetAmount.setHeight("-1px");
		mainLayout.addComponent(txtNetAmount, "top:443.0px; right:50.0px;");

		lblRemarks = new Label("Remarks :");
		lblRemarks.setWidth("-1px");
		lblRemarks.setHeight("-1px");
		mainLayout.addComponent(lblRemarks,"top:370.0px; left:20.0px;");

		txtRemarks = new TextField();
		txtRemarks.setImmediate(false);
		txtRemarks.setWidth("580px");
		txtRemarks.setHeight("123px");
		txtRemarks.setValue("1. Photocopy of VAT & Delivery Challan Enclosed.");
		mainLayout.addComponent(txtRemarks, "top:368.0px; left:85.0px;");

		mainLayout.addComponent(button,"top:495.0px; left:180.0px");
		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0; i<7; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ar)
	{
		tblblChallan.add(ar,new Label());
		tblblChallan.get(ar).setWidth("100%");
		tblblChallan.get(ar).setHeight("16px");
		tblblChallan.get(ar).setImmediate(true);

		tblblChallanMerge.add(ar,new Label());
		tblblChallanMerge.get(ar).setWidth("100%");
		tblblChallanMerge.get(ar).setImmediate(true);

		tblblChallanDate.add(ar,new Label());
		tblblChallanDate.get(ar).setWidth("100%");
		tblblChallanDate.get(ar).setImmediate(true);

		tblblProCode.add(ar, new Label());
		tblblProCode.get(ar).setWidth("100%");
		tblblProCode.get(ar).setImmediate(true);

		tblblProLedger.add(ar, new Label());
		tblblProLedger.get(ar).setWidth("100%");
		tblblProLedger.get(ar).setImmediate(true);

		tblblItemName.add(ar, new Label());
		tblblItemName.get(ar).setWidth("100%");
		tblblItemName.get(ar).setImmediate(true);

		tblblItemUnit.add(ar, new Label());
		tblblItemUnit.get(ar).setWidth("100%");
		tblblItemUnit.get(ar).setImmediate(true);

		tbtxtChallanQty.add(ar, new TextRead(1));
		tbtxtChallanQty.get(ar).setWidth("100%");
		tbtxtChallanQty.get(ar).setImmediate(true);

		tbtxtRate.add(ar, new TextRead(1));
		tbtxtRate.get(ar).setWidth("100%");
		tbtxtRate.get(ar).setImmediate(true);

		tbtxtAmount.add(ar, new TextRead(1));
		tbtxtAmount.get(ar).setWidth("100%");
		tbtxtAmount.get(ar).setImmediate(true);

		tbtxtRemarks.add(ar, new TextField());
		tbtxtRemarks.get(ar).setWidth("100%");
		tbtxtRemarks.get(ar).setImmediate(true);
		table.setFooterVisible(true);

		table.addItem(new Object[]{tblblChallan.get(ar),tblblChallanMerge.get(ar),tblblChallanDate.get(ar),
				tblblProCode.get(ar),tblblProLedger.get(ar),tblblItemName.get(ar),tblblItemUnit.get(ar),
				tbtxtChallanQty.get(ar),tbtxtRate.get(ar),tbtxtAmount.get(ar),tbtxtRemarks.get(ar)},ar);
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(dDate);

		for(int i=0; i<tblblChallan.size(); i++)
		{
			allComp.add(tbtxtRemarks.get(i));
		}

		allComp.add(txtVat);
		allComp.add(txtRemarks);

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		cmbPartyName.setEnabled(!b);
		txtPartyAddress.setEnabled(!b);
		txtPartyMobile.setEnabled(!b);

		dDate.setEnabled(!b);

		txtSalesVoucherNo.setEnabled(!b);
		txtSalesBillNo.setEnabled(!b);
		table.setEnabled(!b);
		challanTable.setEnabled(!b);

		txtRemarks.setEnabled(!b);

		txtTotalAmount.setEnabled(!b);
		txtVat.setEnabled(!b);
		txtVatAmount.setEnabled(!b);
		txtNetAmount.setEnabled(!b);
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);

		button.btnRefresh.setEnabled(!t);

		button.btnPreview.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtClear()
	{
		isFind = false;
		isUpdate = false;

		cmbPartyName.setValue(null);
		txtPartyAddress.setValue("");
		txtPartyMobile.setValue("");

		txtSalesVoucherNo.setValue("");
		txtSalesBillNo.setValue("");

		clearChallanData();

		txtTableClear();

		txtTotalAmount.setValue("");
		txtNetAmount.setValue("");
		txtRemarks.setValue("1. Photocopy of VAT & Delivery Challan Enclosed.");
		table.setColumnFooter("Item Code", "");
		table.setColumnFooter("Qty", "");
		table.setColumnFooter("Amount", "");

		totalAmount = 0;
	}

	private void clearChallanData()
	{
		for(int i=0;i<tblblChaNo.size();i++)
		{
			tbchkChallan.get(i).setValue(false);
			tblblDelChaNo.get(i).setValue("");
			tblblChaNo.get(i).setValue("");
			tblblChaDate.get(i).setValue("");
			tblblChaQty.get(i).setValue("");
		}
	}

	private void txtTableClear()
	{
		for(int i=0;i<tblblChallan.size();i++)
		{
			tblblChallan.get(i).setValue("");
			tblblChallanDate.get(i).setValue("");
			tblblChallanMerge.get(i).setValue("");
			tblblProCode.get(i).setValue("");
			tblblItemName.get(i).setValue("");
			tblblItemUnit.get(i).setValue("");
			tbtxtChallanQty.get(i).setValue("");
			tbtxtRate.get(i).setValue("");
			tbtxtAmount.get(i).setValue("");
			tbtxtRemarks.get(i).setValue("");
		}
	}

	private void chaTableinitialise()
	{
		for(int i=0; i<5; i++)
		{
			chaTableRowAdd(i);
		}
	}

	private void chaTableRowAdd(final int ar)
	{
		tbchkChallan.add(ar,new CheckBox());
		tbchkChallan.get(ar).setWidth("100%");
		tbchkChallan.get(ar).setHeight("15px");
		tbchkChallan.get(ar).setImmediate(true);
		tbchkChallan.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				validSelect(ar);
				ChallanNO = "";
				ChallanNOAll = "";
				txtTableClear();
				getChallanNo();
				if(ChallanNOAll!="")
				{setChallanData();}

				sumAmount();
				totalSumAllAmount();
			}
		});

		tblblDelChaNo.add(ar,new Label());
		tblblDelChaNo.get(ar).setWidth("100%");
		tblblDelChaNo.get(ar).setHeight("15px");
		tblblDelChaNo.get(ar).setImmediate(true);

		tblblChaNo.add(ar,new Label());
		tblblChaNo.get(ar).setWidth("100%");
		tblblChaNo.get(ar).setHeight("15px");
		tblblChaNo.get(ar).setImmediate(true);

		tblblChaDate.add(ar, new Label());
		tblblChaDate.get(ar).setWidth("100%");
		tblblChaDate.get(ar).setHeight("15px");
		tblblChaDate.get(ar).setImmediate(true);

		tblblChaQty.add(ar, new Label());
		tblblChaQty.get(ar).setWidth("100%");
		tblblChaQty.get(ar).setImmediate(true);

		challanTable.addItem(new Object[]{tbchkChallan.get(ar),tblblDelChaNo.get(ar),tblblChaNo.get(ar),
				tblblChaDate.get(ar),tblblChaQty.get(ar)},ar);
	}

	private void previewBillEvent()
	{
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();
			String queryBill = "SELECT * from funSalesInvoiceReport('"+cmbPartyName.getValue()+"','"+(txtSalesBillNo.getValue().toString())+"'," +
					" '"+dFormat.format(dDate.getValue())+"','"+dFormat.format(dDate.getValue())+"')";
			System.out.println("query is:"+queryBill);
			hm.put("path", "./report/account/DoSales/");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());

			hm.put("sql", queryBill);

			Window win = new ReportViewer(hm,"report/account/DoSales/rptInvoiceBill.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
}