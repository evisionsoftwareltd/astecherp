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

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class ReturnInvoice extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table challanTable= new Table();
	private Table table= new Table();

	private TextField txtReturnINvoiceNoFind = new TextField();

	private Label lblPartyName;
	private ComboBox cmbPartyName;

	private Label lblPartyAddress;
	private TextRead txtPartyAddress;

	private Label lblPartyMobile;
	private TextRead txtPartyMobile;

	private Label lblReturnBill;
	private TextRead txtReturnBill;

	private Label lblReturnVoucher;
	private TextRead txtReturnVoucher;

	private Label lblSalesIBillNo;
	private ComboBox cmbSalesBillNo;

	private Label lblDate;
	private PopupDateField dDate;

	/*private Label lblCommission;
	private AmountField txtCommission;
	private TextRead txtCommissionAmnt;*/

	private Label lblTotalAmount;
	private TextRead txtTotalAmount;

	/*private Label lblTruckFare;
	private AmountCommaSeperator txtTruckFare;*/

	private Label lblVat;
	private AmountField txtVat;
	private TextRead txtVatAmount;

	private Label lblNetAmount;
	private TextRead txtNetAmount;

	private Label lblRemarks;
	private TextField txtRemarks;
	String SalesBillDate="";

	String partyLedger="";
	String maxId  = "0";
	String VoucherNo  = "";

	private Object doDate = null;

	//Table Value
	private ArrayList<CheckBox> tbchkChallan = new ArrayList<CheckBox>();
	private ArrayList<Label> tblblChaNo = new ArrayList<Label>();
	private ArrayList<Label> tblblChaDate = new ArrayList<Label>();
	private ArrayList<Label> tblblChaQty = new ArrayList<Label>();

	//Table Value
	private ArrayList<Label> tblblChallan = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanDate = new ArrayList<Label>();
	private ArrayList<Label> tblblProCode = new ArrayList<Label>();
	private ArrayList<Label> tblblProLedger = new ArrayList<Label>();
	private ArrayList<Label> tblblItemName = new ArrayList<Label>();
	private ArrayList<Label> tblblItemUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tbtxtSalesQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtReturnQty = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxBalQty = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbtxQty = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbtxtAmount = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbtxtRate = new ArrayList<TextRead>();
	private ArrayList<TextField> tbtxtRemarks = new ArrayList<TextField>();

	private ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat recDateFormat = new SimpleDateFormat("MM/yy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","Preview","","Exit");
	
	private Boolean isUpdate= false;
	private Boolean isFind= false;

	private String findInvoiceNo = "";

	private int DoStatus=0;
	private int findDoNo=0;

	double totalAmount = 0;

	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat decimal = new DecimalFormat("#0");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat bdFormat = new SimpleDateFormat("dd-MM-yy");

	String challanNo = "";
	String gatepassNo = "";
	String RBillNo="";	
	private String ChallanNO = "";
	private String ChallanNOAll = "";

	private String GatePassNO = "";
	private String GatePassNOAll = "";

	public ReturnInvoice(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("RETURN INVOICE :: " + sessionBean.getCompany());

		buildMainLayout();		
		setContent(mainLayout);

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
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
		}
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
				selectReceiptNo();
				cmbPartyName.focus();
				txtReturnVoucher.setValue("IV-NO-"+getInvoice());
				txtReturnBill.setValue(RBillNo);

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

		button.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBillEvent();
			}
		});
		
		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				txtClear();
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
				cmbSalesBillNo.removeAllItems();
				txtClearData();
				if(cmbPartyName.getValue()!=null)
				{
					setPartyInfo();
					cmbInvoiceData();
				}
				else
				{
					txtPartyAddress.setValue("");
				}
			}
		});

		cmbSalesBillNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSalesBillNo.getValue()!=null)
				{
					setChallanData();
					txtNetAmount.setValue(0.00);
				}
			}
		});

		/*txtCommission.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSumAllAmount();
			}
		});*/

		txtVat.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSumAllAmount();
			}
		});

		/*txtTruckFare.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				totalSumAllAmount();
			}
		});*/
	}

	private void tableFocusEnter()
	{
		System.out.println("Size "+tblblItemName.size());

		int totalItem = 0;
		for(int i=0; i<tblblItemName.size(); i++)
		{
			allComp.add(tbtxtSalesQty.get(i));
			allComp.add(tbtxtRemarks.get(i));

			if(!tblblProCode.get(i).getValue().toString().isEmpty())
			{totalItem++;}
		}
		new FocusMoveByEnter(this,allComp);
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

		Window win = new ReturnInvoiceFind(sessionBean, txtReturnINvoiceNoFind,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtReturnINvoiceNoFind.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtReturnINvoiceNoFind.getValue().toString());
					if(cmbPartyName.getValue()!=null)
					{isFind=true;}
				}

			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String ReturnBillNo) 
	{		
		txtClear();
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select ri.vPartyId,ri.dReturnBillDate,ri.vReturnBillNo,ri.vSalesBillNo," +
					" ri.mPreTotal,ri.mCommission,ri.mCommissionAmt,ri.mPostTotal,ri.mVat,ri.mVatAmount," +
					" ri.mTruckFare,ri.mNetTotal,ri.vRemarks,rd.vChallanNo,rd.dChallanDate,rd.vProductId," +
					" rd.vProductLegderId,rd.vProductName,rd.vProductUnit,rd.mReturnQty,rd.mRate,rd.mAmount," +
					" rd.vRemarks,rd.dChallanDate,ri.vReturnVoucherNo from tbReturnInvoiceInfo ri inner join tbReturnInvoiceDetails" +
					" rd on ri.vReturnBillNo=rd.vReturnBillNo where ri.vReturnBillNo='"+ReturnBillNo+"'";

			List led = session.createSQLQuery(sql).list();

			int i = 0;
			for (Iterator iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(i==0)
				{
					//txtCommission.setValue(df.format(element[5]));
					cmbPartyName.setValue(element[0].toString());
					dDate.setValue(element[1]);
					txtReturnBill.setValue(element[2].toString());
					cmbSalesBillNo.setValue(element[3].toString());
					txtVat.setValue(df.format(element[8]));
					txtVatAmount.setValue(df.format(element[9]));
					//txtTruckFare.setValue(df.format(element[10]));
					txtRemarks.setValue(element[12].toString());
					txtReturnVoucher.setValue(element[24].toString());
					tableClearData();
				}

				tblblChallan.get(i).setValue(element[13].toString());
				tblblChallanDate.get(i).setValue(element[14].toString());
				tblblProCode.get(i).setValue(element[15].toString());
				tblblProLedger.get(i).setValue(element[16].toString());
				tblblItemName.get(i).setValue(element[17].toString());
				tblblItemUnit.get(i).setValue(element[18].toString());
				tbtxtSalesQty.get(i).setValue(decimal.format(getSalesQty(element[3].toString(),element[15].toString())));
				tbtxtReturnQty.get(i).setValue(decimal.format(getReturnQty(element[2].toString(),element[3].toString(),element[15].toString())));
				tbtxBalQty.get(i).setValue(decimal.format(Double.parseDouble(tbtxtSalesQty.get(i).getValue().toString())
						-Double.parseDouble(tbtxtReturnQty.get(i).getValue().toString())));
				tbtxtRate.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[20].toString())));
				tbtxQty.get(i).setValue(decimal.format(element[19]));
				tbtxtRemarks.get(i).setValue(element[22].toString());

				i++;

				tableFocusEnter();

				if(i==tblblChallan.size()-1)
				{
					tableRowAdd(i+1);
				}

				sumAmount();
				totalSumAllAmount();
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}


	private void tableClearData()
	{
		for(int i=0;i<tblblChallan.size();i++)
		{
			tblblChallan.get(i).setValue("");
			tblblProCode.get(i).setValue("");
			tblblItemName.get(i).setValue("");
			tblblItemUnit.get(i).setValue("");
			tbtxtSalesQty.get(i).setValue("");
			tbtxtReturnQty.get(i).setValue("");
			tbtxBalQty.get(i).setValue("");
			tbtxQty.get(i).setValue("");
			tbtxtAmount.get(i).setValue("");
			tbtxtRate.get(i).setValue("");
			tbtxtRemarks.get(i).setValue("");
		}
	}

	private void cmbPartyData()
	{
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" Select partyCode,partyName from tbPartyInfo where isActive='1' ORDER by partyCode ").list();

			System.out.println("sql is :"+" Select partyCode,partyName from tbPartyInfo where isActive='1' ORDER by partyCode ");
			for(Iterator iter=list.iterator();iter.hasNext();)
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

	private void cmbInvoiceData()
	{
		cmbSalesBillNo.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" Select iAutoId, vBillNo,vVoucherNo from tbSalesInvoiceInfo where vPartyId='"+cmbPartyName.getValue().toString()+"' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalesBillNo.addItem(element[1].toString());
				cmbSalesBillNo.setItemCaption(element[1].toString(), element[1].toString());
				VoucherNo= (element[2].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void setPartyInfo()
	{
		txtPartyAddress.setValue("");
		txtPartyMobile.setValue("");
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" Select partyCode,address,mobile,ledgerCode from tbPartyInfo where partyCode='"+cmbPartyName.getValue().toString()+"' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				txtPartyAddress.setValue(element[1].toString());
				txtPartyMobile.setValue(element[2].toString());
				partyLedger=element[3].toString();
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private Double getSalesQty(String SalesBillNo,String productId)
	{
		double Salesqty = 0;
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " select mChallanQty from tbSalesInvoiceDetails where vBillNo = '"+SalesBillNo+"' and vProductId = '"+productId+"' ";
			System.out.println("productQtySql "+sql);
			List lst = session.createSQLQuery(sql).list();
			Iterator iter = lst.iterator();

			if(iter.hasNext())
			{
				Salesqty = Double.parseDouble(iter.next().toString());
			}
		}
		catch(Exception e)
		{
			showNotification("Error "+e,Notification.TYPE_ERROR_MESSAGE);
		}

		return Salesqty;
	}

	public Double getReturnQty(String ReturnSalesInvoiceNo,String SalesBillNo,String ProId)
	{
		double findQty = 0;

		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = " select isnull(SUM(mReturnQty),0) asd from tbReturnInvoiceDetails" +
					" where vReturnBillNo != '"+ReturnSalesInvoiceNo+"' and vProductId = '"+ProId+"' and vSalesBillNo='"+SalesBillNo+"'";

			List lst = session.createSQLQuery(sql).list();
			Iterator iter = lst.iterator();

			if(iter.hasNext())
			{
				findQty = Double.parseDouble(iter.next().toString());
			}
		}
		catch(Exception e)
		{
			showNotification("Error "+e,Notification.TYPE_ERROR_MESSAGE);
		}

		return findQty;
	}

	private void formValidation()
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(cmbSalesBillNo.getValue() !=null)
			{
				if(validTableSelect())
				{
					if(!txtRemarks.getValue().toString().isEmpty())
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning!","You Should Mention the Causes Why Product has been Returned",Notification.TYPE_WARNING_MESSAGE);

					}
				}
				else
				{
					showNotification("Warning!","You Can Not Save Data With Out Return Information",Notification.TYPE_WARNING_MESSAGE);
					tbchkChallan.get(0).focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Sales Invoice No",Notification.TYPE_WARNING_MESSAGE);
				cmbSalesBillNo.focus();
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
								btnIni(true);
								componentIni(true);
								isUpdate = false;
								isFind = false;
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
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						if(insertData(session,tx)){
							showNotification("All information Save successfully.");
							btnIni(true);
							componentIni(true);
							button.btnNew.focus();
						}
					}
				}
			});
		}
	}

	private String selectVoucher()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dDate.getValue())+"') as voucher").list().iterator().next().toString();

		return voucher;
	}

	private String getInvoice()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String invoice = session.createSQLQuery(" Select cast(isnull(max(cast(replace(Voucher_No, 'IV-NO-', '')as int))+1, 1)as varchar) from Voucher"+selectVoucher()+" where Voucher_No like 'IV-NO-%' ").list().iterator().next().toString();

		return invoice;
	}

	private void sumAmount()
	{
		double totalItem = 0;
		double totalQty = 0;
		totalAmount=0;

		for(int i = 0; i<tbtxtRate.size(); i++)
		{
			if(!tbtxtRate.get(i).getValue().toString().isEmpty())
			{
				if(!tbtxQty.get(i).getValue().toString().isEmpty())
				{
					if(Double.parseDouble(tbtxtRate.get(i).getValue().toString().replaceAll(",", ""))>0)
					{
						totalQty = totalQty + Double.parseDouble(tbtxQty.get(i).getValue().toString().replaceAll(",", ""));
						totalAmount = totalAmount + Double.parseDouble(tbtxtAmount.get(i).getValue().toString().replaceAll(",", ""));
						totalItem++;
					}
				}
			}
		}

		table.setColumnFooter("Challan No", "Ttl Item=");
		table.setColumnFooter("Item Code", decimal.format(totalItem)+"");

		table.setColumnFooter("Unit", "Ttl Qty=");
		table.setColumnFooter("Qty", decimal.format(totalQty)+"");

		table.setColumnFooter("Rate", "Ttl Amt=");
		table.setColumnFooter("Amount", (new CommaSeparator().setComma(totalAmount))+"");
	}

	public void totalSumAllAmount()
	{
		//double commission = Double.parseDouble(txtCommission.getValue().toString().isEmpty()?"0":txtCommission.getValue().toString());

		//txtCommissionAmnt.setValue((new CommaSeparator().setComma((commission*Double.parseDouble(totalAmount+"".replaceAll(",", "")))/100)));
		txtTotalAmount.setValue((new CommaSeparator().setComma(totalAmount-((totalAmount)/100))));

		double totalAmount = Double.parseDouble(txtTotalAmount.getValue().toString().isEmpty()?"0":txtTotalAmount.getValue().toString().replaceAll(",", ""));

		double totalVat = Double.parseDouble(txtVat.getValue().toString().isEmpty()?"0":txtVat.getValue().toString().replaceAll(",", ""));

		txtVatAmount.setValue((new CommaSeparator().setComma((totalVat*totalAmount)/100)));

		double vatAmount = Double.parseDouble(txtVatAmount.getValue().toString().isEmpty()?"0":txtVatAmount.getValue().toString().replaceAll(",", ""));

	//	double truckFare = Double.parseDouble(txtTruckFare.getValue().toString().isEmpty()?"0":txtTruckFare.getValue().toString().replaceAll(",", ""));

		txtNetAmount.setValue(new CommaSeparator().setComma(vatAmount+totalAmount));
	}

	public void setChallanData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
 
			List list = session.createSQLQuery("  select sd.vChallanNo,sd.dBillDate,si.mPreTotal," +
					"si.mCommission,si.mCommissionAmt,si.mPostTotal,si.mVat,si.mVatAmount,si.mTruckFare," +
					"si.mNetTotal,si.vRemarks,sd.vProductId,sd.vProductLegderId,sd.vProductName,sd.vProductUnit,sd.mChallanQty," +
					"sd.mRate,sd.mAmount,sd.vRemarks,sd.dChallanDate from tbSalesInvoiceInfo si inner join tbSalesInvoiceDetails " +
					"sd on si.vBillNo=sd.vBillNo where si.vBillNo='"+cmbSalesBillNo.getValue()+"' " +
					"order by sd.iAutoId ").list();

			int i = 0;

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				if(i == 0)
				{
					//txtCommission.setValue(df.format(element[3]));
					SalesBillDate=(element[1].toString());
					txtVat.setValue(df.format(element[6]));
					txtVatAmount.setValue(df.format(element[7]));
					//txtTruckFare.setValue(df.format(element[8]));
					txtNetAmount.setValue(0.00);
				}

				tblblChallan.get(i).setValue(element[0].toString());
				tblblChallanDate.get(i).setValue(element[19].toString());
				tblblProCode.get(i).setValue(element[11].toString());
				tblblProLedger.get(i).setValue(element[12].toString());
				tblblItemName.get(i).setValue(element[13].toString());
				tblblItemUnit.get(i).setValue(element[14].toString());
				tbtxtSalesQty.get(i).setValue(decimal.format(element[15]));
				tbtxtReturnQty.get(i).setValue(decimal.format(getBalQty(cmbSalesBillNo.getValue().toString(),element[11].toString())));
				tbtxBalQty.get(i).setValue(decimal.format(Double.parseDouble(tbtxtSalesQty.get(i).getValue().toString())
						-Double.parseDouble(tbtxtReturnQty.get(i).getValue().toString())));
				tbtxtRate.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[16].toString())));
				i++;
			}

			tableFocusEnter();

			if(i==tblblChallan.size()-1)
			{
				tableRowAdd(i+1);
			}

			sumAmount();
			totalSumAllAmount();
		}
		catch(Exception ex)
		{

		}
	}

	public void selectReceiptNo()
	{
		String RB = "";
		String partyId = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			List list = session.createSQLQuery(" select iAutoId,vReceiptPrefix from tbReceiptPrefixInfo where" +
					" iAutoId in ('5') ").list();

			Iterator iterMax = session.createSQLQuery(" SELECT ISNULL((MAX(CAST(SUBSTRING(vReturnBillSerial,CHARINDEX('-'," +
					" vReturnBillSerial,9)+1,50) AS INT))+1),1) FROM tbReturnInvoiceInfo ").list().iterator();

			if(iterMax.hasNext())
				maxId = (iterMax.next().toString());

			for(Iterator iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();

				RB= element[1].toString();
			}
		}
		catch (Exception e)
		{

		}

		txtReturnBill.setValue(RB+"-"+recDateFormat.format(new Date())+"-"+maxId);
		RBillNo = RB+"-"+recDateFormat.format(new Date())+"-"+maxId;
		System.out.println("RBillNo"+RBillNo);
	}

	private String autoDoNo() 
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max(vReturnBillSerial),0)+1 from tbReturnInvoiceInfo";

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
	private double getBalQty(String InvoiceNo,String productId)
	{
		double balQty=0;
		Transaction tx = null;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "select isnull(SUM(mReturnQty),0) asd from tbReturnInvoiceDetails where vSalesBillNo = '"+InvoiceNo+"' and vProductId = '"+productId+"' ";

			List lst = session.createSQLQuery(sql).list();
			Iterator iter = lst.iterator();

			if(iter.hasNext())
			{
				balQty = Double.parseDouble(iter.next().toString());
				System.out.println("balQty" +balQty);
			}
		}
		catch(Exception e)
		{
			showNotification("Error "+e,Notification.TYPE_ERROR_MESSAGE);
		}

		return balQty;

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

	private boolean insertData(Session session,Transaction tx)
	{
		

		String SalesBillNo = "";
		String ReturnVoucherNo = "";
		String voucher = "";
		String autoCode="";

		String partyNarration = "";
		String commissionNarration = "";
		String vatNarration = "";
		String truckFareNarration = "";

		if(isUpdate)
		{
			RBillNo = txtReturnBill.getValue().toString();
			ReturnVoucherNo = txtReturnVoucher.getValue().toString();
			autoCode=autoDoNo();
		}
		else
		{
			ReturnVoucherNo=("IV-NO-"+getInvoice());
			autoCode=maxId;
			RBillNo=txtReturnBill.getValue().toString();

		}

		voucher = "voucher"+selectVoucher();

		partyNarration = "Invoice: "+ReturnVoucherNo+", Date: "+dFormat.format(dDate.getValue())+", ChallanNo: "+ChallanNOAll.replaceAll("'", "").trim()+", "+txtNetAmount.getValue().toString().replace(",", "");

		try
		{
			String InsertInfo =" INSERT into tbReturnInvoiceInfo values(" +
					" '"+RBillNo+"',"+
					" '"+autoCode+"'," +
					" '"+ReturnVoucherNo.trim()+"'," +
					" '"+dFormat.format(dDate.getValue()).trim()+" "+(getDatetime())+"'," +
					" '"+cmbSalesBillNo.getValue().toString()+"'," +
					" '"+VoucherNo+"'," +
					" '"+SalesBillDate+"'," +
					" '"+cmbPartyName.getValue().toString().trim()+"'," +
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString().trim())+"'," +
					" '"+(txtPartyAddress.getValue().toString().trim().isEmpty()?"":txtPartyAddress.getValue().toString().trim())+"'," +
					" '"+(txtPartyMobile.getValue().toString().trim().isEmpty()?"":txtPartyMobile.getValue().toString().trim())+"'," +
					" '"+partyLedger.trim()+"'," +
					" '"+totalAmount+"'," +
					" '0'," +
					" '0'," +
					" '"+(txtTotalAmount.getValue().toString().trim().isEmpty()?"0":txtTotalAmount.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtVat.getValue().toString().trim().isEmpty()?"0":txtVat.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '"+(txtVatAmount.getValue().toString().trim().isEmpty()?"0":txtVatAmount.getValue().toString().replaceAll(",", "").trim())+"'," +
					" '0'," +
					" '"+(txtNetAmount.getValue().toString().isEmpty()?"0":txtNetAmount.getValue().toString().replace(",", "").trim())+"'," +
					" '"+(txtRemarks.getValue().toString().isEmpty()?"":txtRemarks.getValue().toString().trim())+"'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";

			System.out.println("InsertInfo : "+InsertInfo);
			session.createSQLQuery(InsertInfo).executeUpdate();

			String partyInvoice = " INSERT into "+voucher+" values(" +
					" '"+ReturnVoucherNo+"'," +
					" '"+dFormatTime.format(dDate.getValue())+"'," +
					" '"+partyLedger.trim()+"'," +
					" '"+partyNarration+"'," +
					" '0.00'," +
					" '"+txtNetAmount.getValue().toString().replace(",", "").trim()+"'," +
					" 'ils'," +
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
					" ''," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '0'," +
					" '0'," +
					" ''," +
					" '','','')";

			//System.out.println("partyInvoice : "+partyInvoice);
			session.createSQLQuery(partyInvoice).executeUpdate();
			
			System.out.println("Atik");

			/*if(!txtCommission.getValue().toString().isEmpty())
			{
				commissionNarration = "Invoice: "+ReturnVoucherNo+", Date: "+dFormat.format(dDate.getValue())+", Commission: "+txtCommission.getValue().toString().replace(",", "")+" of "+totalAmount;
				String commissionInvoice = " INSERT into "+voucher+" values(" +
						" '"+ReturnVoucherNo+"'," +
						" '"+dFormat.format(dDate.getValue())+"'," +
						" 'TempEL122'," +
						" '"+commissionNarration+"'," +
						" '0.00'," +
						" '"+txtCommissionAmnt.getValue().toString().replace(",", "").trim()+"'," +
						" 'ils'," +
						" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
						" ''," +
						" '1'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '')";

				//System.out.println("commissionInvoice : "+commissionInvoice);
				session.createSQLQuery(commissionInvoice).executeUpdate();
			}*/

			/*if(!txtTruckFare.getValue().toString().isEmpty())
			{
				truckFareNarration = "Invoice: "+ReturnVoucherNo+", Date: "+dFormat.format(dDate.getValue())+", " +
						"Truck Fare: "+txtTruckFare.getValue().toString().replace(",", "")+" of " +
						""+(df.format(Double.parseDouble(txtTotalAmount.getValue().toString().replaceAll(",", ""))+
								Double.parseDouble((txtVatAmount.getValue().toString().replaceAll(",", "").trim().isEmpty())?
										"0":txtVatAmount.getValue().toString().replaceAll(",", "").trim())));

				String truckFareInvoice = " INSERT into "+voucher+" values(" +
						" '"+ReturnVoucherNo+"'," +
						" '"+dFormat.format(dDate.getValue())+"'," +
						" 'TempEL122'," +
						" '"+truckFareNarration+"'," +
						" '0.00'," +
						" '"+txtTruckFare.getValue().toString().replace(",", "").trim()+"'," +
						" 'ils'," +
						" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
						" ''," +
						" '1'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '')";

				//System.out.println("truckFareInvoice : "+truckFareInvoice);
				session.createSQLQuery(truckFareInvoice).executeUpdate();
			}*/

			if(Double.parseDouble("0"+txtVat.getValue().toString())>0)
			{
				vatNarration = "Invoice: "+ReturnVoucherNo+", Date: "+dFormat.format(dDate.getValue())+", VAT: "+txtVat.getValue().toString().replace(",", "")+"% of "+txtTotalAmount.getValue().toString().replace(",", "");
				String vatInvoice = " INSERT into "+voucher+" values(" +
						" '"+ReturnVoucherNo+"'," +
						" '"+dFormat.format(dDate.getValue())+"'," +
						" 'TempAL227'," +
						" '"+vatNarration+"'," +
						" '"+txtVatAmount.getValue().toString().replace(",", "")+"'," +
						" '0.00'," +
						" 'ils'," +
						" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
						" ''," +
						" '1'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" '2'," +
						" '"+sessionBean.getCompanyId()+"',"+
						" '0'," +
						" '0'," +
						" ''," +
						" '','','')";

				//System.out.println("vatInvoice : "+vatInvoice);
				
				System.out.println("vat invoice:"+vatInvoice);
				
				session.createSQLQuery(vatInvoice).executeUpdate();
			}

			for(int i = 0; i<tblblChallan.size(); i++)
			{
				if(!tbtxQty.get(i).getValue().toString().isEmpty()){
					String productNarration= "Invoice: "+ReturnVoucherNo+", Date: "+dFormat.format(dDate.getValue())+", Prod: "+tblblItemName.get(i).getValue().toString().trim().replace("'", "''")+", Qty: "+tbtxtSalesQty.get(i).getValue().toString().replace(",", "")+" "+tblblItemUnit.get(i).getValue().toString().replace(",", "");

					String InsertDetails = "Insert into tbReturnInvoiceDetails values ( " +
							" '"+RBillNo+"',"+
							" '"+ReturnVoucherNo.trim()+"'," +
							" '"+dFormat.format(dDate.getValue()).trim()+" "+(getDatetime())+"'," +
							" '"+cmbSalesBillNo.getValue().toString()+"'," +
							" '"+VoucherNo+"'," +
							" '"+SalesBillDate+"'," +
							" '"+tblblChallan.get(i).getValue().toString().trim()+"'," +
							" '"+tblblChallanDate.get(i).getValue()+"'," +
							" '"+tblblProLedger.get(i).getValue().toString().trim()+"'," +
							" '"+tblblProCode.get(i).getValue().toString().trim()+"'," +
							" '"+tblblItemName.get(i).getValue().toString().trim().replace("'", "''")+"'," +
							" '"+tblblItemUnit.get(i).getValue().toString().trim()+"'," +
							" '"+tbtxtSalesQty.get(i).getValue().toString().trim().replace(",", "")+"',"+
							" '"+tbtxQty.get(i).getValue().toString().trim().replace(",", "")+"'," +
							" '"+tbtxtRate.get(i).getValue().toString().trim().replace(",", "")+"'," +
							" '"+tbtxtAmount.get(i).getValue().toString().replace(",", "").trim()+"'," +
							" '"+(tbtxtRemarks.get(i).getValue().toString().isEmpty()?"":tbtxtRemarks.get(i).getValue().toString().trim())+"' )";	
					
					//System.out.println("InsertDetails: "+InsertDetails);
					session.createSQLQuery(InsertDetails).executeUpdate();

					String productInvoice = " INSERT into "+voucher+" values(" +
							" '"+ReturnVoucherNo+"'," +
							" '"+dFormat.format(dDate.getValue())+"'," +
							" '"+tblblProLedger.get(i).getValue().toString().trim()+"'," +
							" '"+productNarration+"'," +
							" '"+tbtxtAmount.get(i).getValue().toString().replace(",", "").trim()+"'," +
							" '0.00'," +
							" 'ils'," +
							" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue().toString())+"'," +
							" ''," +
							" '1'," +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '2'," +
							" '"+sessionBean.getCompanyId()+"',"+
							" '0'," +
							" '0'," +
							" ''," +
							" '','','')";

					//System.out.println("productInvoice : "+productInvoice);
					session.createSQLQuery(productInvoice).executeUpdate();

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
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}

	private boolean deleteData(Session session,Transaction tx)
	{
		String voucher = "voucher"+selectVoucher();
		try
		{
			String delFormSql = " delete from tbReturnInvoiceInfo where vReturnVoucherNo='"+txtReturnVoucher.getValue().toString().trim()+"' ";
			String delTableSql = " delete from tbReturnInvoiceDetails where vReturnVoucherNo='"+txtReturnVoucher.getValue().toString().trim()+"' ";
			String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtReturnVoucher.getValue().toString().trim()+"' ";

			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			session.createSQLQuery(delVoucherSql).executeUpdate();
			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		return false;
	}

	private void tableinitialise()
	{
		for(int i=0; i<12; i++)
		{
			tableRowAdd(i);
		}
	}
	private void tableRowAdd(final int ar)
	{
		tblblChallan.add(ar,new Label());
		tblblChallan.get(ar).setWidth("100%");
		tblblChallan.get(ar).setHeight("23px");
		tblblChallan.get(ar).setImmediate(true);

		tblblChallanDate.add(ar,new Label());
		tblblChallanDate.get(ar).setWidth("100%");
		tblblChallanDate.get(ar).setHeight("23px");
		tblblChallanDate.get(ar).setImmediate(true);

		tblblProCode.add(ar, new Label());
		tblblProCode.get(ar).setWidth("100%");
		tblblProCode.get(ar).setHeight("23px");
		tblblProCode.get(ar).setImmediate(true);

		tblblProLedger.add(ar, new Label());
		tblblProLedger.get(ar).setWidth("100%");
		tblblProLedger.get(ar).setHeight("23px");
		tblblProLedger.get(ar).setImmediate(true);

		tblblItemName.add(ar, new Label());
		tblblItemName.get(ar).setWidth("100%");
		tblblItemName.get(ar).setImmediate(true);

		tblblItemUnit.add(ar, new Label());
		tblblItemUnit.get(ar).setWidth("100%");
		tblblItemUnit.get(ar).setImmediate(true);

		tbtxtSalesQty.add(ar, new TextRead(1));
		tbtxtSalesQty.get(ar).setWidth("100%");
		tbtxtSalesQty.get(ar).setImmediate(true);

		tbtxtReturnQty.add(ar, new TextRead(1));
		tbtxtReturnQty.get(ar).setWidth("100%");
		tbtxtReturnQty.get(ar).setImmediate(true);

		tbtxBalQty.add(ar, new TextRead(1));
		tbtxBalQty.get(ar).setWidth("100%");
		tbtxBalQty.get(ar).setImmediate(true);

		tbtxQty.add(ar, new AmountField());
		tbtxQty.get(ar).setWidth("100%");
		tbtxQty.get(ar).setImmediate(true);
		tbtxQty.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbtxBalQty.get(ar).getValue().toString().isEmpty())
				{	
					if(Double.parseDouble(tbtxQty.get(ar).getValue().toString().isEmpty()?"0.0":
						tbtxQty.get(ar).getValue().toString().replaceAll(",", "").toString())>
					Double.parseDouble(tbtxBalQty.get(ar).getValue().toString().isEmpty()?"0.0":
						tbtxBalQty.get(ar).getValue().toString().replaceAll(",", "").toString()))
					{
						tbtxQty.get(ar).setValue(0);
						showNotification("Warning!","Exceeded Limit");
					}
				}
				if(!tbtxQty.get(ar).getValue().toString().isEmpty())
				{
					if(!tbtxtRate.get(ar).getValue().toString().isEmpty())
					{
						double requiredQty = Double.parseDouble(tbtxQty.get(ar).getValue().toString().trim().replaceAll(",", ""));
						double tempPrice = Double.parseDouble(tbtxtRate.get(ar).getValue().toString().replaceAll(",", ""));

						tbtxtAmount.get(ar).setValue(new CommaSeparator().setComma(requiredQty*tempPrice));

					}
					sumAmount();
				}
				else
				{
					tbtxtAmount.get(ar).setValue("");
					sumAmount();
				}

				totalSumAllAmount();


			}
		});

		tbtxtRate.add(ar, new TextRead(1));
		tbtxtRate.get(ar).setWidth("100%");
		tbtxtRate.get(ar).setImmediate(true);

		tbtxtAmount.add(ar, new TextRead(1));
		tbtxtAmount.get(ar).setWidth("100%");
		tbtxtAmount.get(ar).setImmediate(true);

		tbtxtRemarks.add(ar, new TextField());
		tbtxtRemarks.get(ar).setWidth("100%");
		tbtxtRemarks.get(ar).setHeight("-1px");
		tbtxtRemarks.get(ar).setImmediate(true);

		table.setFooterVisible(true);

		table.addItem(new Object[]{tblblChallan.get(ar),tblblChallanDate.get(ar),tblblProCode.get(ar),
				tblblProLedger.get(ar),tblblItemName.get(ar),tblblItemUnit.get(ar),tbtxtSalesQty.get(ar),
				tbtxtReturnQty.get(ar),tbtxBalQty.get(ar),tbtxQty.get(ar),tbtxtRate.get(ar),tbtxtAmount.get(ar),tbtxtRemarks.get(ar)},ar);
	}

	private void focusEnter()
	{
		allComp.add(cmbPartyName);
		allComp.add(dDate);
		allComp.add(cmbSalesBillNo);

		for(int i=0;i<tblblChallan.size();i++)
		{
			allComp.add(tbtxQty.get(i));
			allComp.add(tbtxtRemarks.get(i));
		}

		//allComp.add(txtTruckFare);
		allComp.add(txtRemarks);

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		cmbPartyName.setEnabled(!b);
		txtPartyAddress.setEnabled(!b);
		txtPartyMobile.setEnabled(!b);
		txtReturnBill.setEnabled(!b);
		txtReturnVoucher.setEnabled(!b);

		dDate.setEnabled(!b);

		cmbSalesBillNo.setEnabled(!b);
		table.setEnabled(!b);
		challanTable.setEnabled(!b);

		txtRemarks.setEnabled(!b);

		/*txtCommission.setEnabled(!b);
		txtCommission.setReadOnly(false);
		txtCommissionAmnt.setEnabled(!b);*/

		txtTotalAmount.setEnabled(!b);
		txtVat.setEnabled(!b);
		txtVatAmount.setEnabled(!b);
		//txtTruckFare.setEnabled(!b);
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

		dDate.setValue(new java.util.Date());
		cmbSalesBillNo.setValue(null);
		txtReturnBill.setValue("");
		txtReturnVoucher.setValue("");
		txtClearData();

		txtRemarks.setValue("");

		totalAmount=0;
	}

	private void txtClearData()
	{
		for(int i=0;i<tblblChallan.size();i++)
		{
			tblblChallan.get(i).setValue("");
			tblblProCode.get(i).setValue("");
			tblblItemName.get(i).setValue("");
			tblblItemUnit.get(i).setValue("");
			tbtxtSalesQty.get(i).setValue("");
			tbtxtReturnQty.get(i).setValue("");
			tbtxBalQty.get(i).setValue("");
			tbtxQty.get(i).setValue("");
			tbtxtAmount.get(i).setValue("");
			tbtxtRate.get(i).setValue("");
			tbtxtRemarks.get(i).setValue("");
		}

		txtRemarks.setValue("");

		/*txtCommission.setReadOnly(false);
		txtCommission.setValue("");
		txtCommissionAmnt.setValue("");*/

		txtTotalAmount.setValue("");
		txtVat.setValue("");
		txtVatAmount.setValue("");
		txtNetAmount.setValue("");
		//txtTruckFare.setValue("");

		table.setColumnFooter("Item Code", "");
		table.setColumnFooter("Cha Qty", "");
		table.setColumnFooter("Amount", "");

	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1090px");
		mainLayout.setHeight("735px");

		//lblPartyName
		lblPartyName = new Label("Party Name :");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("-1px");
		lblPartyName.setHeight("-1px");
		mainLayout.addComponent(lblPartyName, "top:20.0px; left:20.0px;");

		//cmbPartyName
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("250px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbPartyName, "top:16.0px; left:100.0px;");

		//lblPartyAddress
		lblPartyAddress = new Label("Address :");
		lblPartyAddress.setWidth("-1px");
		lblPartyAddress.setHeight("-1px");
		mainLayout.addComponent(lblPartyAddress, "top:45.0px; left:20.0px;");

		//txtPartyAddress
		txtPartyAddress = new TextRead();
		txtPartyAddress.setImmediate(true);
		txtPartyAddress.setWidth("250px");
		txtPartyAddress.setHeight("47px");
		mainLayout.addComponent(txtPartyAddress, "top:42.0px; left:101.0px;");

		//lblPartyMobile
		lblPartyMobile = new Label("Mobile :");
		lblPartyMobile.setWidth("-1px");
		lblPartyMobile.setHeight("-1px");
		mainLayout.addComponent(lblPartyMobile, "top:95.0px; left:20.0px;");

		//txtPartyMobile
		txtPartyMobile = new TextRead();
		txtPartyMobile.setImmediate(false);
		txtPartyMobile.setWidth("250px");
		txtPartyMobile.setHeight("22px");
		mainLayout.addComponent(txtPartyMobile, "top:92.0px; left:101.0px;");

		//lblDate
		lblDate = new Label("Return Date :");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:400.0px;");

		//dDate
		dDate = new PopupDateField();
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dDate, "top:18.0px; left:520.0px;");

		//lblSalesBillNo
		lblReturnVoucher= new Label("Voucher No :");
		lblReturnVoucher.setWidth("-1px");
		lblReturnVoucher.setHeight("-1px");
		mainLayout.addComponent(lblReturnVoucher, "top:45.0px; left:400.0px;");

		//cmbSalesBillNo
		txtReturnVoucher =new TextRead();
		txtReturnVoucher.setImmediate(true);
		txtReturnVoucher.setWidth("150px");
		txtReturnVoucher.setHeight("22px");
		mainLayout.addComponent(txtReturnVoucher, "top:43.0px; left:520.0px;");

		//lblSalesBillNo
		lblReturnBill= new Label("Return Bill No :");
		lblReturnBill.setWidth("-1px");
		lblReturnBill.setHeight("-1px");
		mainLayout.addComponent(lblReturnBill, "top:70.0px; left:400.0px;");

		//cmbSalesBillNo
		txtReturnBill =new TextRead();
		txtReturnBill.setImmediate(true);
		txtReturnBill.setWidth("150px");
		txtReturnBill.setHeight("22px");
		mainLayout.addComponent(txtReturnBill, "top:68.0px; left:520.0px;");

		//lblSalesBillNo
		lblSalesIBillNo = new Label("Sales Bill No :");
		lblSalesIBillNo.setWidth("-1px");
		lblSalesIBillNo.setHeight("-1px");
		mainLayout.addComponent(lblSalesIBillNo, "top:95.0px; left:400.0px;");

		//cmbSalesBillNo
		cmbSalesBillNo = new ComboBox();
		cmbSalesBillNo.setImmediate(true);
		cmbSalesBillNo.setWidth("150px");
		cmbSalesBillNo.setHeight("22px");
		mainLayout.addComponent(cmbSalesBillNo, "top:93.0px; left:520.0px;");

		//Table
		table.setWidth("1050px");
		table.setHeight("380px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Challan No", Label.class, new Label());
		table.setColumnWidth("Challan No", 100);

		table.addContainerProperty("Challan Date", Label.class, new Label());
		table.setColumnWidth("Challan Date", 50);
		table.setColumnCollapsed("Challan Date", true);

		table.addContainerProperty("Item Code", Label.class, new Label());
		table.setColumnWidth("Item Code", 60);

		table.addContainerProperty("Item Ledger", Label.class, new Label());
		table.setColumnWidth("Item Ledger", 30);
		table.setColumnCollapsed("Item Ledger", true);

		table.addContainerProperty("Item Name", Label.class, new Label());
		table.setColumnWidth("Item Name", 330);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 60);

		table.addContainerProperty("Sal. Qty", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Sal. Qty", 45);

		table.addContainerProperty("Ret.Qty", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Ret.Qty", 50);

		table.addContainerProperty("Bal.Qty", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Bal.Qty", 50);

		table.addContainerProperty("Qty", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Qty", 50);

		table.addContainerProperty("Rate", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Rate", 60);

		table.addContainerProperty("Amount", TextRead.class, new TextRead(1),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount", 80);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 70);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_RIGHT, Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER});

		table.setColumnCollapsed("Item Code", true);
		
		mainLayout.addComponent(table,"top:130.0px; left:20.0px; ");

		/*//lblCommission
		lblCommission = new Label("Commission (%) :");
		lblCommission.setWidth("-1px");
		lblCommission.setHeight("-1px");
		mainLayout.addComponent(lblCommission,"top:520.0px; left:680.0px;");

		//txtTotalAmount
		txtCommission = new AmountField();
		txtCommission.setImmediate(true);
		txtCommission.setWidth("40px");
		txtCommission.setHeight("-1px");
		mainLayout.addComponent(txtCommission, "top:517.0px; right:160.0px;");

		//txtCommissionAmnt
		txtCommissionAmnt = new TextRead(1);
		txtCommissionAmnt.setImmediate(true);
		txtCommissionAmnt.setWidth("100px");
		txtCommissionAmnt.setHeight("-1px");
		mainLayout.addComponent(txtCommissionAmnt, "top:517.0px; right:50.0px;");

		Label lblComTk = new Label("TK.");
		lblComTk.setHeight("-1px");
		lblComTk.setWidth("-1px");
		mainLayout.addComponent(lblComTk, "top:520.0px; right:25.0px;");*/

		//lblTotalAmount
		lblTotalAmount = new Label("Total Amount :");
		lblTotalAmount.setWidth("-1px");
		lblTotalAmount.setHeight("-1px");
		mainLayout.addComponent(lblTotalAmount,"top:545.0px; left:680.0px;");

		//txtTotalAmount
		txtTotalAmount = new TextRead(1);
		txtTotalAmount.setImmediate(true);
		txtTotalAmount.setWidth("150px");
		txtTotalAmount.setHeight("-1px");
		mainLayout.addComponent(txtTotalAmount, "top:543.0px; right:50.0px;");

		Label lblTotalTk = new Label("TK.");
		lblTotalTk.setHeight("-1px");
		lblTotalTk.setWidth("-1px");
		mainLayout.addComponent(lblTotalTk, "top:546.0px; right:25.0px;");

		//lblVat
		lblVat = new Label("VAT (%) :");
		lblVat.setWidth("-1px");
		lblVat.setHeight("-1px");
		mainLayout.addComponent(lblVat,"top:571.0px; left:680.0px;");

		//txtVat
		txtVat = new AmountField();
		txtVat.setImmediate(true);
		txtVat.setWidth("40px");
		txtVat.setHeight("-1px");
		mainLayout.addComponent(txtVat, "top:568.0px; right:160.0px;");

		Label lblVatTk = new Label("TK.");
		lblVatTk.setHeight("-1px");
		lblVatTk.setWidth("-1px");
		mainLayout.addComponent(lblVatTk, "top:571.0px; right:25.0px;");

		//txtVatAmount
		txtVatAmount = new TextRead(1);
		txtVatAmount.setImmediate(true);
		txtVatAmount.setWidth("100px");
		txtVatAmount.setHeight("-1px");
		mainLayout.addComponent(txtVatAmount, "top:568.0px; right:50.0px;");

		/*//lblTruckFare
		lblTruckFare = new Label("Truck Fare :");
		lblTruckFare.setWidth("-1px");
		lblTruckFare.setHeight("-1px");
		mainLayout.addComponent(lblTruckFare,"top:597.0px; left:680.0px;");

		//txtTruckFare
		txtTruckFare = new AmountCommaSeperator();
		txtTruckFare.setImmediate(true);
		txtTruckFare.setWidth("150px");
		txtTruckFare.setHeight("-1px");
		mainLayout.addComponent(txtTruckFare, "top:594.0px; right:50.0px;");*/

		Label lblTtlTk = new Label("TK.");
		lblTtlTk.setHeight("-1px");
		lblTtlTk.setWidth("-1px");
		mainLayout.addComponent(lblTtlTk, "top:597.0px; right:25.0px;");

		//lblNetAmount
		lblNetAmount = new Label("Net Amount :");
		lblNetAmount.setWidth("-1px");
		lblNetAmount.setHeight("-1px");
		mainLayout.addComponent(lblNetAmount,"top:597.0px; left:680.0px;");

		//txtNetAmount
		txtNetAmount = new TextRead(1);
		txtNetAmount.setImmediate(true);
		txtNetAmount.setWidth("150px");
		txtNetAmount.setHeight("-1px");
		mainLayout.addComponent(txtNetAmount, "top:594.0px; right:50.0px;");

	/*	Label lblNetTk = new Label("TK.");
		lblNetTk.setHeight("-1px");
		lblNetTk.setWidth("-1px");
		mainLayout.addComponent(lblNetTk, "top:622.0px; right:25.0px;");*/

		//txtRemarks
		txtRemarks = new TextField("Causes of Return :");
		txtRemarks.setImmediate(false);
		txtRemarks.setWidth("580px");
		txtRemarks.setHeight("123px");
		mainLayout.addComponent(txtRemarks, "top:535.0px; left:65.0px;");

		mainLayout.addComponent(button,"top:680.0px; left:180.0px");

		return mainLayout;
	}

	private void previewBillEvent()
	{
		String queryBill = null;
		String subReportQueryBill = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			HashMap hm = new HashMap();

			queryBill = "  SELECT * from [funReturnInvoiceReport] ('"+txtReturnBill.getValue().toString().trim()+"') ";

			System.out.println("Report Query: "+queryBill);

			try
			{
				tx = session.beginTransaction();
				String query= "select vDeliveryChallanNo,lg.name from tbSalesInvoiceInfo si inner join tbReturnInvoiceInfo ri on si.vBillNo=ri.vSalesBillNo inner join tbLogin lg on ri.userId=lg.userId where  vBillNo = " +
						"(select vSalesBillNo from tbReturnInvoiceInfo where vReturnBillNo = '"+txtReturnBill.getValue().toString()+"')";
				List list = session.createSQLQuery(query).list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					subReportQueryBill = "select dcd.vChallanNo,dci.dChallanDate,dci.vTruckNo,dci.vDriverName," +
							"dci.vDestination,dcd.vProductUnit,SUM(dcd.mChallanQty) mChallanQty,dcd.vDoNo,Convert" +
							"(date,dcd.dDoDate) dDoDate,dci.vDepoName FROM tbDeliveryChallanInfo dci inner join tbDeliveryChallanDetails " +
							"dcd on dci.vChallanNo=dcd.vChallanNo where dci.vChallanNo in ("+element[0]+") group by" +
							" dcd.vChallanNo,dci.dChallanDate, dci.vTruckNo,dci.vDriverName,dcd.vProductUnit," +
							"dci.vDestination,dcd.vDoNo,Convert(date,dcd.dDoDate),dci.vDepoName order by dcd.vChallanNo ";

					hm.put("userSavedBy",element[1].toString());

					System.out.println("subReportQueryBill "+subReportQueryBill);
				}
			}
			catch(Exception ex)
			{
				this.getParent().showNotification("Error3",ex+"",Notification.TYPE_ERROR_MESSAGE);
			}

			hm.put("path", "./report/account/DoSales/");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("subsql", subReportQueryBill);

			if(queryValueCheck(queryBill))
			{
				hm.put("sql", queryBill);

				Window win = new ReportViewer(hm,"report/account/DoSales/rptReturnInvoiceBill.jasper",
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