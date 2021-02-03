package acc.appform.DoSalesModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.TextRead;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class FgDeliveryChallanInvoiceDelete extends Window
{
	private SessionBean sessionBean;

	private AbsoluteLayout mainLayout;
	private Panel cPanel=new Panel();
	private boolean tick = false,isFind=false;
	//private Label lblType = new Label("Type:");
	//private static final String[] stType = new String[] {"PO","JO"};
	private ComboBox cmbType = new ComboBox();
	//private PopupDateField dChallanDate = new PopupDateField();

	
	private SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dcformate=new SimpleDateFormat("#0.00");
	private CommonButton button = new CommonButton("", "", "", "Delete", "", "", "", "", "", "Exit");
	private Table table = new Table();
	private Label lblInvoiceNo;
	private ComboBox cmbInvoiceNo;
	private Label lblParty = new Label("Party Name : ");
	private TextRead txtParty;
	private Label lblDaliveryChallan = new Label("Challan No : ");
	private TextRead txtDaliveryChallan;
	private TextRead txtInvoiceNo;
	private Label lblJVNo = new Label("JV No : ");
	private TextRead txtJvNo;
	private Label lblIVNo = new Label("IV NO : ");
	private TextRead txtIVNo;
	private Label lblDate = new Label("Challan Date : ");
	private PopupDateField dChallanDate;
	private Label lblInvoiceDate = new Label("Invoice Date : ");
	private PopupDateField dInvoiceDate;
	

	private ArrayList<Label> tbsl = new ArrayList<Label>();
	private ArrayList<Label> tbProductName = new ArrayList<Label>();
	private ArrayList<Label> tbUnit = new ArrayList<Label>();
	private ArrayList<Label> tbQty = new ArrayList<Label>();
	
	
	private TextField txtCancelId=new TextField();
	private ArrayList<Component> allComp = new ArrayList<Component>();
	private ReportDate reportTime = new ReportDate();
	private boolean isUpdate = false;
	private int reasonIndex = 0;

	public FgDeliveryChallanInvoiceDelete(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("INVOICE DELETE STATEMENT:: "+sessionBean.getCompany());
		this.center();
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		tableInitialise();
		InvoiceDataLoad();
		setEventAction();
		authenticationCheck();
		focusEnter();
		/*IniEnable();*/
	}
	private void IniEnable() {
		txtParty.setEnabled(false);
		dChallanDate.setEnabled(false);
		dInvoiceDate.setEnabled(false);
		txtInvoiceNo.setEnabled(false);
		txtIVNo.setEnabled(false);
		txtDaliveryChallan.setEnabled(false);
		txtJvNo.setEnabled(false);
		table.setEnabled(false);
		
		
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
		cmbInvoiceNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbInvoiceNo.getValue()!=null)
				{
					tableClear();
				}
				
			}
		});
		cmbInvoiceNo.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				findButtonEvent();
			}
		});

		button.btnDelete.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			   DeleteDeliveryChallan();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		
	}

	
	public void InvoiceDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		try
		{
			cmbInvoiceNo.removeAllItems();
			
			
			String sql="";
		
				sql ="select Distinct a.vBillNo,a.vBillNo from tbSalesInvoiceDetails a  "
						+ "where convert(date,a.dChallanDate,105) between '"+sessionBean.getFiscalOpenDate()+"' and CURRENT_TIMESTAMP";
						
				System.out.println("cmbInvoiceLoad : "+sql);
			List<?> lst = session.createSQLQuery(sql).list();
			Iterator<?> iter = lst.iterator();
			if(queryValueCheck(sql)){
				while (iter.hasNext()) 
				{
					Object[] element = (Object[]) iter.next();
					System.out.println("cmbLoad");
					cmbInvoiceNo.addItem(element[0].toString());
					cmbInvoiceNo.setItemCaption(element[0].toString(), element[1].toString());
				}
			}

		}

		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
		finally{
			if(session!=null){
				session.close();
			}
				
			}
	}

	private boolean queryValueCheck(String sql)
	{
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
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


	private void findButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String suppId="";
			if(cmbInvoiceNo.getValue()!=null){
				suppId=cmbInvoiceNo.getValue().toString();
			}
			String sql="select c.vChallanNo,c.dChallanDate,b.vBillNo,b.dInvoiceDate,a.vProductName,"
					+ "a.vProductUnit,a.mChallanQty,b.vPartyName,isnull(b.vVoucherNo,'')IVoucherNo,"
					+ "isnull(c.voucherNo,'')JvoucherNo "
					+ "from tbSalesInvoiceDetails a inner join tbSalesInvoiceInfo b on a.vBillNo=b.vBillNo "
					+ "inner join tbDeliveryChallanInfo c on a.vChallanNo=c.vChallanNo "
					+ "where b.vBillNo like '"+suppId+"'";
			System.out.println(sql);
			List<?> lst = session.createSQLQuery(sql).list();
			int i=0;
			if(!lst.isEmpty())
			{
				tableClear();
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(i == tbProductName.size())
					{
						tableRowAdd(i);
					}
					txtDaliveryChallan.setValue(element[0].toString());
					
					dChallanDate.setValue(element[1]);
					txtInvoiceNo.setValue(element[2]);
					dInvoiceDate.setValue(element[3]);
					tbProductName.get(i).setValue(element[4].toString());
					tbUnit.get(i).setValue(element[5]);
					tbQty.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element[6].toString())));
					
					txtParty.setValue(element[7].toString());
					txtIVNo.setValue(element[8].toString());
					txtJvNo.setValue(element[9].toString());
					i++;
				}
			}
			/*else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);					
			}*/
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		finally{session.close();}
	}
	
	private void DeleteDeliveryChallan()
	{
		final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener() {
			 
			
			public void buttonClicked(ButtonType buttonType) {

				String insertQuery = "";
				String insertQueryInvoice = "";
				String insertIngradiant= "";
				String insertVoucher= "";
				String insertVoucherInvoice = "";
				for(int i = 0; i<tbProductName.size(); i++)
				{
					if(!tbProductName.get(i).getValue().toString().isEmpty())
					{
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						
						
						try
						{
							String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateF.format(dChallanDate.getValue())+"')").list().iterator().next().toString();
							String voucher =  "voucher"+fsl;
							
							insertVoucher="Insert into tbDeleteUpdateVoucher "
									+ "select Voucher_No, Date, Ledger_Id, Narration, DrAmount, CrAmount, vouchertype, TransactionWith,"
									+ "'"+sessionBean.getUserName()+"'vUserName,CURRENT_TIMESTAMP as DUDtime, "
									+ "'Delete' TType,costId, '"+sessionBean.getUserIp()+"'vUserIp, '"+sessionBean.getCompanyId()+"' companyId "
									+ "from "+voucher+" "
									+ "where Voucher_No='"+txtJvNo.toString()+"'";
							System.out.println(insertVoucher);
							session.createSQLQuery(insertVoucher).executeUpdate();
							
							insertQuery = "Insert into tbUdDeliveryChallanInfoDetails "
									+ "select  a.vDoNo, vChallanSerial, dChallanDate, a.vChallanNo, a.vGatePassNo, vPartyId, vPartyName, vPartyAddress, vPartyTinNo, vPartyMobile, "
									+ "vDepoId, vDepoName, vDriverName, vDriverMobile, vTruckNo, vDestination, a.vRemarks, status, vDelChallanNo, vVatChallanNo, a.voucherNo,  "
									+ "totalCostAmount, vDestinationTIN, dDoDate, vProductId, vProductName, vProductUnit, mChallanQty, mProductRate, costAmount, vLabelId,"
									+ "vLabelName, vSizeName, '"+sessionBean.getUserName()+"'vUserName, '"+sessionBean.getUserIp()+"'vUserIp, CURRENT_TIMESTAMP as dEntryTime,"
											+ "'Delete'vFlag from tbDeliveryChallanInfo a inner join tbDeliveryChallanDetails b  "
									+ "on a.vChallanNo=b.vChallanNo where a.vChallanNo='"+txtDaliveryChallan.toString()+"' ";
							System.out.println(insertQuery);
							session.createSQLQuery(insertQuery).executeUpdate();

							insertIngradiant="Insert into tbUdDelliveryIngradiantConsumptionInfoDetails "
									+ "select  a.challanNo, a.challanDate, productId, productName, unit, delliveryQty, rate, Amount, voucherNo, fiscalYearSl, "
									+ "fgId, fgName, ingradiantId, ingradiantName, ratioQty, consumptionQty, consumptionStage, "
									+ " '"+sessionBean.getUserName()+"'vUserName, '"+sessionBean.getUserIp()+"' vUserIp, CURRENT_TIMESTAMP as dEntryTime,'Delete'vFlag "
									+ "from tbDelliveryIngradiantConsumptionInfo a inner join tbDelliveryIngradiantConsumptionDetails b "
									+ "on a.challanNo= b.challanNo where a.challanNo='"+txtDaliveryChallan.toString()+"' ";
							System.out.println("insertIngradiant : "+insertIngradiant);
							session.createSQLQuery(insertIngradiant).executeUpdate();
							
							insertVoucherInvoice="Insert into tbDeleteUpdateVoucher "
									+ "select Voucher_No, Date, Ledger_Id, Narration, DrAmount, CrAmount, vouchertype, TransactionWith,"
									+ "'"+sessionBean.getUserName()+"'vUserName,CURRENT_TIMESTAMP as DUDtime, "
									+ "'Delete' TType,costId, '"+sessionBean.getUserIp()+"'vUserIp, '"+sessionBean.getCompanyId()+"' companyId "
									+ "from "+voucher+" "
									+ "where Voucher_No='"+txtIVNo.toString()+"'";
							System.out.println(insertVoucherInvoice);
							session.createSQLQuery(insertVoucherInvoice).executeUpdate();
							
							insertQueryInvoice = "insert into tbUdSalesInvoiceInfoDetails "
									+ "select  a.vBillNo, vBillSerial, dInvoiceDate, a.vVoucherNo, vPartyId, vPartyName, vPartyAddress, vPartyMobile, "
									+ "vPartyLedgerId, vDeliveryChallanNo, mPreTotal, mCommission,   "
									+ "mCommissionAmt, mPostTotal, mVat, mVatAmount, mTruckFare, mNetTotal, dBillDate, vChallanNo, dChallanDate,  "
									+ "vProductLegderId, vProductId, vProductName, vProductUnit, mChallanQty, mRate,  "
									+ "mAmount, a.vRemarks, iStatus,"
									+ "'"+sessionBean.getUserName()+"' userName,"
									+ "'"+sessionBean.getUserId()+"' userId,"
									+ "'"+sessionBean.getUserIp()+"' userIp,"
									+ "CURRENT_TIMESTAMP as entryTime,'Delete' Flag "
									+ "from tbSalesInvoiceInfo a inner join tbSalesInvoiceDetails b on a.vBillNo= b.vBillNo "
									+ " where a.vBillNo = '"+txtInvoiceNo.toString()+"' ";
							System.out.println(insertQueryInvoice);
							session.createSQLQuery(insertQueryInvoice).executeUpdate();
				
							String 	deleteQueryDeliveryChallanInfo = "Delete from tbDeliveryChallanInfo where vChallanNo='"+txtDaliveryChallan.toString()+"'";
							System.out.println(deleteQueryDeliveryChallanInfo);
							session.createSQLQuery(deleteQueryDeliveryChallanInfo).executeUpdate();
							
							String 	deleteQueryDeliveryChallanDetails = "Delete from tbDeliveryChallanDetails where vChallanNo='"+txtDaliveryChallan.toString()+"'";
							System.out.println(deleteQueryDeliveryChallanDetails);
							session.createSQLQuery(deleteQueryDeliveryChallanDetails).executeUpdate();
							
							String 	deleteQueryIngradiantInfo = "Delete from tbDelliveryIngradiantConsumptionInfo where challanNo='"+txtDaliveryChallan.toString()+"'";
							System.out.println(deleteQueryIngradiantInfo);
							session.createSQLQuery(deleteQueryIngradiantInfo).executeUpdate();
							
							String 	deleteQueryIngradiantDetails = "Delete from tbDelliveryIngradiantConsumptionDetails where challanNo='"+txtDaliveryChallan.toString()+"'";
							System.out.println(deleteQueryIngradiantDetails);
							session.createSQLQuery(deleteQueryIngradiantDetails).executeUpdate();
			
							String 	deleteQueryVoucher = "Delete from "+voucher+" where "
									+ "Voucher_No='"+txtJvNo.toString()+"'";
							System.out.println(deleteQueryVoucher);
							session.createSQLQuery(deleteQueryVoucher).executeUpdate();
						
							String 	deleteQuerySalesInfo = "Delete from tbSalesInvoiceInfo where vBillNo = '"+txtInvoiceNo.toString()+"'";
							System.out.println(deleteQuerySalesInfo);
							session.createSQLQuery(deleteQuerySalesInfo).executeUpdate();
							
							String 	deleteQuerySalesDetails = "Delete from tbSalesInvoiceDetails where vBillNo = '"+txtInvoiceNo.toString()+"'";
							System.out.println(deleteQuerySalesDetails);
							session.createSQLQuery(deleteQuerySalesDetails).executeUpdate();
							
							String 	deleteQueryVoucherSelas = "Delete from "+voucher+" "
									+ "where Voucher_No= '"+txtIVNo.toString()+"'";
							System.out.println(deleteQueryVoucherSelas);
							session.createSQLQuery(deleteQueryVoucherSelas).executeUpdate();
							
							
							tx.commit();
							
							reportPreview();
							
							formClear();
							InvoiceDataLoad();
						}
						
						catch(Exception ex)
						{
							if(tx!=null){
								tx.rollback();
							}
							showNotification("Can't Save", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						
						finally{
							if(session!=null){
								session.close();
							}
						}
					}
				}
				
			}
			
		});
		
	}
	private void reportPreview()
	{
		String InvoiceNo = txtInvoiceNo.getValue().toString();
	
		try
		{
			HashMap<String, Object> hm = new HashMap<String, Object>();

			String query = " select entryTime,dInvoiceDate,vBillNo,vChallanNo,vPartyName, "
					+ " vProductName,vProductUnit,mChallanQty,userName,userIp "
					+ "from tbUdSalesInvoiceInfoDetails "
					+ " where vBillNo like '"+InvoiceNo+"' order by entryTime,iAutoId";
			
			System.out.println("Report Query : "+query);
			
			/*dd-MM-yyyy  hh:mm:ss aaa*/
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("username", sessionBean.getUserName() +"  "+  sessionBean.getUserIp());
			hm.put("fromDate", "From  "+dateF.format(new Date())+"  to  "+dateF.format(new Date()));
			hm.put("toDate", new Date());

			if(queryValueCheck(query))
			{
				hm.put("sql", query);
				Window win = new ReportViewer(hm,"report/account/finishedGoods/RptDeliveryChallanInvoiceDeleteStatement.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning!","There are no data.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	

	public void tableInitialise()
	{
		for(int i=0; i<5; i++)
		{
			tableRowAdd(i);
		} 
	}
	private void btnIni(boolean t)
	{
		button.btnSave.setEnabled(!t);
		button.btnEdit.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void focusEnter()
	{
		allComp.add(cmbInvoiceNo);
	
		//allComp.add(dChallanDate);
		//allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	public void tableRowAdd(final int ar)
	{
		tbsl.add(ar, new Label());
		tbsl.get(ar).setWidth("100%");
		tbsl.get(ar).setImmediate(true);
		tbsl.get(ar).setValue(ar+1);

		

		tbProductName.add(ar, new Label());
		tbProductName.get(ar).setWidth("100%");
		tbProductName.get(ar).setImmediate(true);

		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");
		tbUnit.get(ar).setImmediate(true);

		tbQty.add(ar, new Label());
		tbQty.get(ar).setWidth("100%");
		tbQty.get(ar).setImmediate(true);

	
		
		
		table.addItem(new Object[]{tbsl.get(ar),tbProductName.get(ar),tbUnit.get(ar),
				tbQty.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("700px");
		mainLayout.setHeight("400px");
		
		cPanel.setWidth("680px");
		cPanel.setHeight("130px");
		cPanel.setStyleName("NewWorkPanel");
		mainLayout.addComponent(cPanel, "top:20.0px; left:10.0px");
		
		lblInvoiceNo = new Label("Invoice No: ");
		lblInvoiceNo.setImmediate(false);
		lblInvoiceNo.setWidth("-1px");
		lblInvoiceNo.setHeight("-1px");
		mainLayout.addComponent(lblInvoiceNo, "top:30.0px; left:20.0px");
		
		cmbInvoiceNo = new ComboBox();
		cmbInvoiceNo.setImmediate(true);
		cmbInvoiceNo.setWidth("180px");
		cmbInvoiceNo.setHeight("-1px");
		cmbInvoiceNo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbInvoiceNo, "top:25.0px; left:100.0px");

		lblParty.setImmediate(false);
		lblParty.setWidth("-1px");
		lblParty.setHeight("-1px");
		mainLayout.addComponent(lblParty, "top:30.0px; left:280.0px" );
		
		txtParty = new TextRead();
		txtParty.setImmediate(true);
		txtParty.setWidth("320px");
		txtParty.setHeight("-1px");
		mainLayout.addComponent(txtParty, "top:30.0px; left:360.0px" );
		
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:60.0px; left:280.0px");
		
		dChallanDate= new PopupDateField();
		dChallanDate.setWidth("108px");
		dChallanDate.setHeight("24px");
		dChallanDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dChallanDate.setDateFormat("dd-MM-yyyy");
		dChallanDate.setValue(new java.util.Date());
		dChallanDate.setImmediate(true);
		mainLayout.addComponent(dChallanDate, "top:60.0px; left:360.0px" );
		
		
		lblDaliveryChallan.setImmediate(false);
		lblDaliveryChallan.setWidth("-1px");
		lblDaliveryChallan.setHeight("-1px");
		mainLayout.addComponent(lblDaliveryChallan, "top:90.0px; left:280.0px");
		
		txtDaliveryChallan = new TextRead();
		txtDaliveryChallan.setImmediate(true);
		txtDaliveryChallan.setWidth("150px");
		txtDaliveryChallan.setHeight("-1px");
		mainLayout.addComponent(txtDaliveryChallan,"top:90.0px; left:360.0px");
		
		
		lblJVNo.setImmediate(false);
		lblJVNo.setWidth("-1px");
		lblJVNo.setHeight("-1px");
		mainLayout.addComponent(lblJVNo, "top:120.0px; left:280.0px");
		
		txtJvNo = new TextRead();
		txtJvNo.setImmediate(true);
		txtJvNo.setWidth("150px");
		txtJvNo.setHeight("-1px");
		mainLayout.addComponent(txtJvNo,"top:120.0px; left:360.0px");
		
		lblInvoiceDate.setImmediate(false);
		lblInvoiceDate.setWidth("-1px");
		lblInvoiceDate.setHeight("-1px");
		mainLayout.addComponent(lblInvoiceDate, "top:60.0px; left:20.0px");
		
		dInvoiceDate= new PopupDateField();
		dInvoiceDate.setWidth("108px");
		dInvoiceDate.setHeight("24px");
		dInvoiceDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dInvoiceDate.setDateFormat("dd-MM-yyyy");
		dInvoiceDate.setValue(new java.util.Date());
		dInvoiceDate.setImmediate(true);
		mainLayout.addComponent(dInvoiceDate, "top:60.0px; left:100.0px" );
		
		lblInvoiceNo = new Label("Invoice No : ");
		lblInvoiceNo.setImmediate(false);
		lblInvoiceNo.setWidth("-1px");
		lblInvoiceNo.setHeight("-1px");
		mainLayout.addComponent(lblInvoiceNo, "top:90.0px; left:20.0px");
		
		txtInvoiceNo = new TextRead();
		txtInvoiceNo.setImmediate(true);
		txtInvoiceNo.setWidth("150px");
		txtInvoiceNo.setHeight("-1px");
		mainLayout.addComponent(txtInvoiceNo,"top:90.0px; left:100.0px");
		
		lblIVNo.setImmediate(false);
		lblIVNo.setWidth("-1px");
		lblIVNo.setHeight("-1px");
		mainLayout.addComponent(lblIVNo, "top:120.0px; left:20.0px");
		
		txtIVNo = new TextRead();
		txtIVNo.setImmediate(true);
		txtIVNo.setWidth("150px");
		txtIVNo.setHeight("-1px");
		mainLayout.addComponent(txtIVNo,"top:120.0px; left:100.0px");
		
		table.setWidth("680");
		table.setHeight("200px");

		table.addContainerProperty("SL#", Label.class, null);		
		table.setColumnWidth("SL#", 25);

		table.addContainerProperty("Product Name", Label.class, null);
		table.setColumnWidth("Product Name", 450);

		table.addContainerProperty("Unit", Label.class, null);		
		table.setColumnWidth("Unit", 50);

		table.addContainerProperty("Qty", Label.class, null);
		table.setColumnWidth("Qty", 80);
		

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_RIGHT});

		table.setImmediate(true);
		table.setColumnCollapsingAllowed(true);
		
		mainLayout.addComponent(table, "top:155.0px; left:10.0px");
		mainLayout.addComponent(button, "top:360.0px; left:220.0px");
		
		return mainLayout;
	}
	

	private void formClear()
	{
		cmbInvoiceNo.setValue(null);
		txtParty.setValue("");
		txtDaliveryChallan.setValue("");
		txtJvNo.setValue("");
		dChallanDate.setValue(new java.util.Date());
		txtInvoiceNo.setValue("");
		dInvoiceDate.setValue(new java.util.Date());
		txtIVNo.setValue("");
		
		tableClear();
	}

	private void tableClear()
	{
		for(int i = 0; i<tbProductName.size(); i++)
		{
			
			tbProductName.get(i).setValue("");
			tbProductName.get(i).setValue("");
			tbUnit.get(i).setValue("");
			tbQty.get(i).setValue("");
			
		}
	}

	
}