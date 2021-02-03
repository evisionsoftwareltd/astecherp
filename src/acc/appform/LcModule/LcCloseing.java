package acc.appform.LcModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.*;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbPrimaryGroup;

public class LcCloseing extends Window {

	private CommonButton cButton = new CommonButton("New", "Save", "","", "Refresh", "Find", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private boolean isUpdate = false,isFind=false;
	private AbsoluteLayout mainLayout;
	
	//ComboBox cmbMrrNo;
	TextRead txtLedgerBalance,txtVoucherNo,txtLCValueUSD,txtreceiptNo,txtmrrNo;
	ComboBox cmbLCNo;
	PopupDateField dDate;
	
	private ArrayList<Label> sl = new ArrayList<Label>();
	private ArrayList<Label> tbItemId = new ArrayList<Label>();
	private ArrayList<Label> tbLedgerId = new ArrayList<Label>();
	private ArrayList<Label> tbItemName = new ArrayList<Label>();
	private ArrayList<Label> tbUnit = new ArrayList<Label>();
	private ArrayList<TextRead> tbLcQty = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbRcvQty = new ArrayList<AmountField>();
	private ArrayList<AmountField> tbUnitPrice = new ArrayList<AmountField>();
	private ArrayList<TextRead> tbAmount = new ArrayList<TextRead>();
	private ArrayList<AmountField> tbNewUnitPrice= new ArrayList<AmountField>();
	private ArrayList<TextRead> tbNewAmount= new ArrayList<TextRead>();
	private ArrayList<ComboBox> tbCmbStore= new ArrayList<ComboBox>();
	private ArrayList<TextField> tbTxtRemarks= new ArrayList<TextField>();
	Table table=new Table();
	DecimalFormat df=new DecimalFormat("#0.00");
	DecimalFormat df4=new DecimalFormat("#0.0000");
	DecimalFormat df8=new DecimalFormat("#0.00000000");
	
	ArrayList<Component> allComp = new ArrayList<Component>();	
	String fiscaleOpen,fiscalClose;
	CommaSeparator cm=new CommaSeparator();
	SimpleDateFormat dateF=new SimpleDateFormat("yyyy-MM-dd");
	double lcValueBdt=0.00;
	String lcLedger="";
	private TextField txtVoucherNoFind = new TextField();
	
	public LcCloseing(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("MATERIALS RECEIPT(L/C) :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		this.fiscaleOpen=dateF.format(sessionBean.getFiscalOpenDate());
		this.fiscalClose=dateF.format(sessionBean.getFiscalCloseDate());

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		//cmbMrrNoLoad();
		cmbLCNoLoad();
		cButton.btnNew.focus();
		focusEnter();
		authencationCheck();
		eventAction();
	}
	private void lcValueBdtCalc(){
		for(int a=0;a<tbItemId.size();a++){
			if(!tbLedgerId.get(a).getValue().toString().isEmpty()&&!tbNewUnitPrice.get(a).getValue().toString().isEmpty()){
				lcValueBdt=lcValueBdt+Double.parseDouble(tbNewAmount.get(a).getValue().toString().replace(",", ""));
			}
		}
	}
	/*private void cmbMrrNoLoad(){
		cmbMrrNo.removeAllItems();
		String sql="select MrrNo from tbRawPurchaseInfo where transactionType='L / C' and flag=0";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			cmbMrrNo.addItem(iter.next());
		}
	}*/
	
	private void cmbLCNoLoad(){
		cmbLCNo.removeAllItems();
		String sql="select vLedgerId,vLcNo from tbLCOpeningINfo where isActive=1 /*and vLedgerId not in(select  SupplierId from tbRawPurchaseInfo  where transactionType='L / C')*/ order by dLcOpeningDate";
		Iterator<?> iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbLCNo.addItem(element[0]);
			cmbLCNo.setItemCaption(element[0], element[1].toString());
		}
	}
	private void eventAction(){
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = false;
				isUpdate = false;
				newBtnAction();
				cmbLCNoLoad();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				tableClear();
				isUpdate = false;
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
			}
		});
		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				//formValidation();
				if(checkValidation()){
					saveBtnAction();
				}
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				isUpdate=true;
				componentIni(false);
				btnIni(false);
			}
		});
		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		/*cmbMrrNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbMrrNo.getValue()!=null){
					mrrEventDataLoad();
					//txtLCNo.setValue("123");
				}
				else{
					lcLedger="";
					txtLCNo.setValue("");
					txtLedgerBalance.setValue("");
					dDate.setValue(new java.util.Date());
					txtVoucherNo.setValue("");
					txtLCValueUSD.setValue("");
				}
			}
		});*/
		cmbLCNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbLCNo.getValue()!=null){
					tableClear();
					lcInfoLoad();
					lcDetailsLoad();
				}
				else{
					txtLedgerBalance.setValue("");
					txtVoucherNo.setValue("");
					txtLCValueUSD.setValue("");
					dDate.setValue(new java.util.Date());
					tableClear();
				}
			}
		});
		cButton.btnPreview.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtmrrNo.getValue().toString().trim().isEmpty())
				{
					reportView();
				}
				else 
				{
					showNotification("Warning!","There are nothing to preview", Notification.TYPE_WARNING_MESSAGE);	
				}
			}
		});

	}
	private void reportView()
	{
		String query=null;
		String activeFlag = null;
		
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try{
			
			HashMap<String, Object> hm = new HashMap<String, Object>();
			
			query="select * from vwRawPurchaseReceiptLc where SupplierId = '"+cmbLCNo.getValue().toString()+"' or  supplierName  = '"+cmbLCNo.getValue().toString()+"'" +
						  " and MrrNo  like '"+txtmrrNo.getValue().toString().trim()+"' ";	
			
			System.out.println(query);
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("user", sessionBean.getUserName());

			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			hm.put("sql", query);
				Window win = new ReportViewerNew(hm,"report/raw/rptGRN.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
						//this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				
				win.setCaption("Report : Goods Receive Note (GRN)");
				this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}

	private void findButtonEvent()
	{
		Window win = new LcClosWindowNew(sessionBean,txtVoucherNoFind,"LcNo");
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtVoucherNoFind.getValue().toString().length()>0)
				{
					findInitialise(txtVoucherNoFind.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInitialise(String strVoucherNo) 
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			
			String sql = "select a.mrrNo,a.lcNo,b.vLcNo,ISNULL(a.voucherNo,0)voucherNo,a.ledgerBalance,a.LcValueUsd," +
					"(select ReceiptNo from tbRawPurchaseInfo where MrrNo=a.mrrNo)ReceiptNo " +
					"from tbLcCloseInfo a   inner join tbLcOpeningInfo b on a.lcNo=b.vLedgerID  " +
					"where a.mrrNo = '"+strVoucherNo+"'  ";
		//" select a.vProductId,(select vLedgerCode from tbRawItemInfo where vRawItemCode=a.vProductId)ledgerId, b.vLcNo, a.vProductName,a.vProductUnit,a.mQuantity,a.mRate,a.mAmount from tbLcOpeningDetails a  inner join tbLcOpeningInfo b on a.vLcNo=b.vLcNo "
				
			
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for (Iterator<?> iter = led.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				txtmrrNo.setValue(element[0]);
				cmbLCNo.addItem(element[2]);
				//cmbLCNo.setItemCaption(element[1].toString(),element[2].toString());
				cmbLCNo.setValue(element[2]);
				txtLedgerBalance.setValue(cm.setComma(Double.parseDouble("0"+df.format(element[4]))));
				txtLCValueUSD.setValue(cm.setComma(Double.parseDouble("0"+df.format(element[5]))));
				txtVoucherNo.setValue(element[3]);
				txtreceiptNo.setValue(element[6]);
				lcDetailsLoad();
			
			}
			
		/*	for(int a=0;a<tbAmount.size();a++){
				if(!tbItemId.get(a).getValue().toString().isEmpty()){
					
					String sqlRate="select Rate,storeId, Qty from tbRawPurchaseDetails where " +
					" ReceiptNo='"+txtreceiptNo.getValue()+"' and ProductID='"+tbItemId.get(a).getValue()+"'";
					System.out.println(sqlRate);
					Iterator iter1 = session.createSQLQuery(sqlRate).list().iterator();
					if(iter1.hasNext()){
						Object[] element1 = (Object[]) iter1.next();
						tbNewUnitPrice.get(a).setValue(df.format(element1[0]));
						tbCmbStore.get(a).setValue(element1[1].toString());
					}
					String sqlRate="SElect c.Qty,a.rateUsd,a.AmountUsd,a.rateBdt,a.AmountBdt,c.storeId from tbLcCloseDetails a "
							+ "inner join tbRawPurchaseInfo b on a.mrrNo=b.MrrNo inner  join tbRawPurchaseDetails c on "
							+ "c.ReceiptNo=b.ReceiptNo where  a.mrrNo='"+txtmrrNo.getValue()+"' and a.productId='"+tbItemId.get(a).getValue()+"' ";
					System.out.println(sqlRate);
					Iterator iter1 = session.createSQLQuery(sqlRate).list().iterator();
					if(iter1.hasNext()){
						Object[] element1 = (Object[]) iter1.next();
						tbRcvQty.get(a).setValue(df.format(element1[0]));
						tbUnitPrice.get(a).setValue(df.format(element1[1]));
						tbAmount.get(a).setValue(cm.setComma(Double.parseDouble("0"+df.format(element1[2]))));
						tbNewUnitPrice.get(a).setValue(df.format(element1[3]));
						tbNewAmount.get(a).setValue(cm.setComma(Double.parseDouble("0"+df.format(element1[4]))));
						tbCmbStore.get(a).setValue(element1[5].toString());
					}
				}
			}*/

		}
		catch (Exception exp)
		{
			showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String voucher =  "voucher"+selectVoucher();

		try
		{
			String delFormSql = " delete from tbLcCloseInfo where voucherNo='"+txtVoucherNo.getValue().toString()+"' ";
			String delTableSql = " delete from tbLcCloseDetails where voucherNo='"+txtVoucherNo.getValue().toString()+"' ";
			//String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtVoucherNo.getValue().toString()+"' ";

			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			//session.createSQLQuery(delVoucherSql).executeUpdate();

			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		}
	}
	private void saveBtnAction()
	{
		try
		{
			if(sessionBean.isUpdateable())
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
								Transaction tx;
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								tx = session.beginTransaction();

								if(deleteData())
								{
									insertData();
									reportView();
								}
								else
								{
									tx.rollback();		
								}
								//mb.close();
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
							{	mb.buttonLayout.getComponent(0).setEnabled(false);					
								insertData();
								reportView();
								//mb.close();
							}
						}
					});
				}
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private String autoReciptNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("Select isnull(max(cast(ReceiptNo as int) ) ,0)+1  from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtreceiptNo.setValue(autoId);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}
	private void  autoMrrNo(){
		String autoId=null;
		Transaction tx;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			Iterator iter = session.createSQLQuery("select  ISNULL(MAX(CAST(MrrNo as int)),0)+1    from tbRawPurchaseInfo").list().iterator();

			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
				txtmrrNo.setValue(autocodegenerate( autoId,8));
				
				/*if(autoId.length()==1)
				{
					txtmrrNo.setValue("0000000"+autoId);
				}
				else if (autoId.length()==2)
				{
					txtmrrNo.setValue("000000"+autoId);
				}
				else if (autoId.length()==3)
				{
					txtmrrNo.setValue("00000"+autoId);	
				}
				else if (autoId.length()==4)
				{
					txtmrrNo.setValue("0000"+autoId);	
				}
				else if (autoId.length()==5)
				{
					txtmrrNo.setValue("000"+autoId);	
				}
				else if (autoId.length()==6)
				{
					txtmrrNo.setValue("000"+autoId);	
				}
				else if (autoId.length()==7)
				{
					txtmrrNo.setValue("0"+autoId);

				}*/

				System.out.println("Mrr No Is"+txtmrrNo.getValue().toString()	);
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
	}
	
	private String autocodegenerate(String autocode, int length)
	{
		
		while(autocode.length()<8)
		{
			autocode="0"+autocode;
		}
		
		return autocode;
		
		
	}
	
	private void insertData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String query=null;
		String voucherNo = getVoucher();

		String voucher =  "voucher"+selectVoucher();
		String vCrNo = "JV-NO-"+voucherNo;

		System.out.println("Voucher: "+vCrNo);
		String type="New";
		if(isUpdate){
			type="Update";
		}
		
		String id = "";
		String mrr="";
		String udFlag="Update";
		if(!isUpdate)
		{
			id=autoReciptNo();
			txtreceiptNo.setValue(id);	
			autoMrrNo();
			udFlag="New";
		}
		
		lcValueBdtCalc();
		try
		{
			//String Naration = "LC: "+cmbLc.getItemCaption(cmbLc.getValue())+", Date: "+dFormat.format(date.getValue())+", Bank: "+cmbBankAccNo.getItemCaption(cmbBankAccNo.getValue())+", "+totalField.getValue().toString().replace(",", "");
			String Naration="";
			String infoQuery="Insert into tbLcCloseInfo (mrrNo,lcNo,date,ledgerBalance,LcValueUsd,LcValueBdt," +
					"voucherNo,username,userIp,entryTime)values "+
					"('"+txtmrrNo.getValue().toString()+"',"
					+ "'"+cmbLCNo.getValue()+"',"
					+ "'"+dateF.format(dDate.getValue())+"',"
					+ "'"+txtLedgerBalance.getValue()+"',"
					+ "'"+txtLCValueUSD.getValue()+"',"
					+ "'"+df8.format(lcValueBdt)+"',"
					//+ "'"+vCrNo+"',"
					+ "'',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
			System.out.println("Insert : "+infoQuery);
			session.createSQLQuery(infoQuery).executeUpdate();
			
			String infoQueryUd="Insert into tbUdLcCloseInfo (mrrNo,lcNo,date,ledgerBalance,LcValueUsd,LcValueBdt," +
					"voucherNo,username,userIp,entryTime,udFlag)values "+
					"('"+txtmrrNo.getValue().toString()+"',"
					+ "'"+cmbLCNo.getValue()+"',"
					+ "'"+dateF.format(dDate.getValue())+"',"
					+ "'"+txtLedgerBalance.getValue()+"',"
					+ "'"+txtLCValueUSD.getValue()+"',"
					+ "'"+df8.format(lcValueBdt)+"',"
					//+ "'"+vCrNo+"',"
					+ "'',"
					+ "'"+sessionBean.getUserName()+"',"
					+ "'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+type+"')";
			System.out.println("Insert : "+infoQuery);
			session.createSQLQuery(infoQueryUd).executeUpdate();

			String sqlInfo = "Insert into tbRawPurchaseInfo values (" +
					" '"+txtreceiptNo.getValue().toString()+"','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue()) + "', " +
					" '"+cmbLCNo.getValue()+"','"+df8.format(lcValueBdt)+"','', " +
					" '"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue())+"'," +
					" ''," +
					" '','','RDP','0'," +
					" '0','"+sessionBean.getUserName()+ "'," +
					" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" 'Raw Material','"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue()) + "', " +
					"'"+txtmrrNo.getValue().toString()+"', 'With MRR', 'L / C','0'," +
					"'','','','','','','OLD')";	
			session.createSQLQuery(sqlInfo).executeUpdate();
			System.out.println(sqlInfo);

			String sqlUdInfo = "insert into tbUdRawPurchaseInfo values(" +
					" '" +txtreceiptNo.getValue().toString()+ "'," +
					" '" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue()) + "','" + cmbLCNo.getValue() + "'," +
					" '" +df8.format(lcValueBdt)+"','','','RDP'," +
					" '0','0'," +
					" '"+sessionBean.getUserName()+"','"+udFlag+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" 'Raw Material','','','','','','OLD')";
			session.createSQLQuery(sqlUdInfo).executeUpdate();
			System.out.println(sqlUdInfo);
			
			
			for(int i=0; i<sl.size(); i++)
			{
				if(!tbLedgerId.get(i).getValue().toString().isEmpty()&&!tbNewUnitPrice.get(i).getValue().toString().isEmpty()&&tbCmbStore.get(i).getValue()!=null){
					String sqlDetails="insert into tbLcCloseDetails(mrrNo,LCNo,voucherNo,productId,ledgerId,productName,unit,rateUsd,AmountUsd,rateBdt,AmountBdt) "+
							" values('"+txtmrrNo.getValue().toString()+"',"
							+ "'"+cmbLCNo.getValue()+"',"
							+ "'"+vCrNo+"',"
							+ "'"+tbItemId.get(i).getValue()+"',"
							+ "'"+tbLedgerId.get(i).getValue()+"',"
							+ "'"+tbItemName.get(i).getValue()+"',"
							
							+ "'"+tbUnit.get(i).getValue()+"',"
							+ "'"+tbUnitPrice.get(i).getValue()+"',"
							+ "'"+tbAmount.get(i).getValue()+"',"
							+ "'"+tbNewUnitPrice.get(i).getValue()+"',"
							+ "'"+tbNewAmount.get(i).getValue()+"')";
					session.createSQLQuery(sqlDetails).executeUpdate();
					
					/*String productLedger = " INSERT into "+voucher+" values(" +
							" '"+vCrNo+"'," +
							" '"+dateF.format(dDate.getValue())+"'," +
							 "'"+tbLedgerId.get(i).getValue()+"',"+
							" '"+Naration+"'," +						
							" '"+tbNewAmount.get(i).getValue()+"'," +
							" '0.00'," +
							" 'jau'," +
							" '"+cmbLCNo.getValue()+"'," +
							" ' '," +
							" '1'," +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '2'," +
							" '"+sessionBean.getCompanyId()+"',"+
							" '', "+
							" '0'," +
							" ''," +
							" '','','')";

					session.createSQLQuery(productLedger).executeUpdate();*/


					String sqlDetailsUd="insert into tbUdLcCloseDetails(mrrNo,LCNo,voucherNo,productId,ledgerId,productName,unit,rateUsd,AmountUsd,rateBdt,AmountBdt,udFlag) "+
							" values('"+txtmrrNo.getValue().toString()+"',"
							+ "'"+cmbLCNo.getValue()+"',"
							+ "'"+vCrNo+"',"
							+ "'"+tbItemId.get(i).getValue()+"',"
							+ "'"+tbLedgerId.get(i).getValue()+"',"
							+ "'"+tbItemName.get(i).getValue()+"',"
							+ "'"+tbUnit.get(i).getValue()+"',"
							+ "'"+tbUnitPrice.get(i).getValue()+"',"
							+ "'"+tbAmount.get(i).getValue()+"',"
							+ "'"+tbNewUnitPrice.get(i).getValue()+"',"
							+ "'"+tbNewAmount.get(i).getValue()+"','"+type+"')";
					session.createSQLQuery(sqlDetailsUd).executeUpdate();
					
					
					String PurchasesqlDetails = "insert into tbRawPurchaseDetails values(" +
							" '"+txtreceiptNo.getValue().toString()+"','"+tbItemId.get(i).getValue().toString().trim()+"','"+tbLcQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+tbRcvQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '0'," +
							" '"+tbNewUnitPrice.get(i).getValue().toString().replaceAll(",", "")+"','"+tbTxtRemarks.get(i).getValue()+"', " +
							" '"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue()) + "'," +
							" '"+(tbCmbStore.get(i).getValue()!=null?tbCmbStore.get(i).getValue().toString():"")+"'," +
							" '"+(tbCmbStore.get(i).getValue()!=null?tbCmbStore.get(i).getItemCaption(tbCmbStore.get(i).getValue()):"")+"','Raw Material') ";

					session.createSQLQuery(PurchasesqlDetails).executeUpdate();
					System.out.println(PurchasesqlDetails);
					
					String PurchasesqlUdDetails = "insert into tbUdRawPurchaseDetails values(" +
							" '"+txtreceiptNo.getValue().toString()+"','"+tbItemId.get(i).getValue().toString().trim()+"','"+tbLcQty.get(i).getValue().toString().replaceAll(",", "")+"'," +
							" '"+tbRcvQty.get(i).getValue().toString().replaceAll(",", "")+"', '0'," +
							" '"+tbNewUnitPrice.get(i).getValue().toString().replaceAll(",", "")+"','"+sessionBean.getUserName()+"'," +
							" '"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue()) + "'," +
							" '"+(tbCmbStore.get(i).getValue()!=null?tbCmbStore.get(i).getValue().toString():"")+"'," +
							" '"+(tbCmbStore.get(i).getValue()!=null?tbCmbStore.get(i).getItemCaption(tbCmbStore.get(i).getValue()):"")+"') ";


					session.createSQLQuery(PurchasesqlUdDetails).executeUpdate();
					System.out.println(PurchasesqlUdDetails);
					
					/*String lcUpdate="update tbLcOpeningInfo set isActive=0 where vLedgerID='"+cmbLCNo.getValue()+"'";
					session.createSQLQuery(lcUpdate).executeUpdate();
					System.out.println(lcUpdate);*/
				}
			}
			/*String LcLedger = " INSERT into "+voucher+" values(" +
					" '"+vCrNo+"'," +
					" '"+dateF.format(dDate.getValue())+"'," +
					" '"+lcLedger+"'," +
					" '"+Naration+"'," +						
					" '0.00'," +
					" '"+lcValueBdt+"'," +
					" 'jau'," +
					" '"+cmbLCNo.getValue()+"'," +
					" ' '," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '', "+
					
					" '0'," +
					" ''," +
					" '','','')";
			session.createSQLQuery(LcLedger).executeUpdate();*/

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			componentIni(true);
			btnIni(true);
			//txtClear();
		}

		catch(Exception ex)
		{
			this.getParent().showNotification("Error at insert data", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	private String selectVoucher()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dateF.format(dDate.getValue())+"') as voucher").list().iterator().next().toString();

		return voucher;
	}
	private String getVoucher()
	{
		String voucher =  "voucher"+selectVoucher();	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String invoice = session.createSQLQuery(" Select cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from "+voucher+" where Voucher_No like 'JV-NO-%' ").list().iterator().next().toString();

		return invoice;
	}
	private boolean tableCheck(){
		for(int a=0;a<tbAmount.size();a++)
		{
			if( Double.parseDouble(tbNewUnitPrice.get(a).getValue().toString().isEmpty()?"0.00": tbNewUnitPrice.get(a).getValue().toString())==0 &&!tbLedgerId.get(a).getValue().toString().isEmpty()&&tbCmbStore.get(a).getValue()!=null){
				return false;
			}
		}
		return true;
	}
	private boolean checkValidation(){
		if(cmbLCNo.getValue()!=null)
		{
			if(tableCheck())
			{
				return true;
			}
			else{
				showNotification("Please Provide all Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide L/C No",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private void lcInfoLoad(){
		String sql="select mLCValueUSD,(SELECT (sum(CrAmount)-sum(DrAmount))amount FROM dbo.rptCostLedger(  '"+fiscaleOpen+"',  '"+fiscalClose+"', "+ 
				" vLedgerID,  '%','1') ) from  tbLcOpeningInfo where vLcNo='"+cmbLCNo.getItemCaption(cmbLCNo.getValue())+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			txtLCValueUSD.setValue(cm.setComma(Double.parseDouble("0"+element[0])));
			txtLedgerBalance.setValue(cm.setComma(Double.parseDouble(element[1].toString())));
		}
	}
	private void lcDetailsLoad(){
		/*String sql="select a.vProductId,(select vLedgerCode from tbRawItemInfo where vRawItemCode=a.vProductId)ledgerId," +
				" b.Ledger_Name, a.vProductName,a.vProductUnit,a.mQuantity,a.mRate,a.mAmount from tbLcOpeningDetails a  "
				+ " inner join tbLedger b on a.vLcNo=b.Ledger_Name where b.Ledger_Id = '"+cmbLCNo.getValue().toString()+"' " +
				"or b.Ledger_Name =  '"+cmbLCNo.getValue().toString()+"'  ";*/
		
		String sql="select a.vProductId,(select vLedgerCode from tbRawItemInfo where vRawItemCode=a.vProductId)ledgerId," +
				" b.Ledger_Name, a.vProductName,a.vProductUnit,a.mQuantity,a.mRate,a.mAmount from tbLcOpeningDetails a  "
				+ " inner join tbLedger b on (SElect vLedgerID from tbLcOpeningInfo c where a.vTransactionID = c.vTransactionID)=b.Ledger_Id where b.Ledger_Id = '"+cmbLCNo.getValue().toString()+"' " +
				"or b.Ledger_Name =  '"+cmbLCNo.getValue().toString()+"'  ";
		Iterator<?>iter=dbService(sql);
		int a=0;
		while(iter.hasNext())
		{
			Object element[]=(Object[])iter.next();
			tbItemId.get(a).setValue(element[0]);
			tbLedgerId.get(a).setValue(element[1]);
			tbItemName.get(a).setValue(element[3]);
			tbUnit.get(a).setValue(element[4]);
			tbLcQty.get(a).setValue(cm.setComma(Double.parseDouble("0"+df.format(element[5]))));
			tbUnitPrice.get(a).setValue(df4.format(element[6]));
			tbAmount.get(a).setValue(cm.setComma(Double.parseDouble("0"+df.format(element[7]))));
			tbRcvQty.get(a).setValue(cm.setComma(Double.parseDouble("0"+df.format(element[5]))));
			rateCalculation(a);
			a++;
		}
	}
	private void rateCalculation(int index){
		double ledgerBalance,totalLCValueUSD,ProductWiseLCValueUSD,rate,qty,amount;
		ledgerBalance=Double.parseDouble("0"+txtLedgerBalance.getValue().toString().replace(",", ""));
		totalLCValueUSD=Double.parseDouble("0"+txtLCValueUSD.getValue().toString().replace(",", ""));
		ProductWiseLCValueUSD=Double.parseDouble("0"+tbAmount.get(index).getValue().toString().replace(",", ""));
		qty=Double.parseDouble("0"+tbRcvQty.get(index).getValue().toString().replace(",", ""));
		rate=((ledgerBalance/totalLCValueUSD)*ProductWiseLCValueUSD)/qty;
		amount=qty*rate;
		//tbNewUnitPrice.get(index).setValue(cm.setComma(Double.parseDouble("0"+df4.format(rate))));
		tbNewUnitPrice.get(index).setValue(Double.parseDouble("0"+df4.format(rate)));
		tbNewAmount.get(index).setValue(cm.setComma(Double.parseDouble("0"+df.format(amount))));
		System.out.print("Pribt");
	}
	/*private void mrrEventDataLoad(){
		String sql="select SupplierId ledgerid,(select Ledger_Name from tbLedger where Ledger_Id=SupplierId)lcNo  "+
				" from tbRawPurchaseInfo where transactionType='L / C' and flag=0 and MrrNo='"+cmbMrrNo.getValue()+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			lcLedger=element[0].toString();
			txtLCNo.setValue(element[1].toString());
		}
		
	}*/
	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}
	private void tableClear(){
		for(int a=0;a<sl.size();a++){
			tbItemName.get(a).setValue("");
			tbItemId.get(a).setValue("");
			tbLedgerId.get(a).setValue("");
			tbUnit.get(a).setValue("");
			tbRcvQty.get(a).setValue("");
			tbLcQty.get(a).setValue("");		
			tbUnitPrice.get(a).setValue("");
			tbNewAmount.get(a).setValue("");
			tbNewUnitPrice.get(a).setValue("");
			tbAmount.get(a).setValue("");
			tbCmbStore.get(a).setValue(null);
			tbTxtRemarks.get(a).setValue("");
		}
	}
	private void txtClear(){
		txtmrrNo.setValue("");
		txtreceiptNo.setValue("");
		cmbLCNo.setValue(null);
		lcValueBdt=0.0;
		lcLedger="";
		tableClear();
	}
	private void newBtnAction()
	{
		btnIni(false);
		componentIni(false);
		txtClear();
		isUpdate = false;
		cmbLCNo.focus();	
	}
	private Iterator<?> dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator<?> iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){

				session.close();
			}
		}
		return iter;
	}
	private void focusEnter() {
		allComp.add(cmbLCNo);
		

		for(int a=0;a<tbAmount.size();a++){
			allComp.add(tbRcvQty.get(a));
			allComp.add(tbNewUnitPrice.get(a));
			allComp.add(tbCmbStore.get(a));
			allComp.add(tbTxtRemarks.get(a));
		}
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}
	private void componentIni(boolean b) {
		//cmbMrrNo.setEnabled(!b);
		txtmrrNo.setEnabled(!b);
		txtreceiptNo.setEnabled(!b);
		cmbLCNo.setEnabled(!b);
		txtLedgerBalance.setEnabled(!b);
		dDate.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);
		txtLCValueUSD.setEnabled(!b);
		table.setEnabled(!b);
	}
	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}
	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(true);
		}
		else
		{
			cButton.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(true);
		}
		else
		{
			cButton.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(true);
		}
		else
		{
			cButton.btnDelete.setVisible(false);
		}
	}
	private void tableInitialize(){
		for(int a=0;a<8;a++){
			tableRowAdd(a);
		}
	}
	private void amountBDTCalc( int ar){
		double qty,rate,amount;
		qty=Double.parseDouble(tbRcvQty.get(ar).getValue().toString().isEmpty()?"0":tbRcvQty.get(ar).getValue().toString().replace(",", ""));
		rate=Double.parseDouble(tbNewUnitPrice.get(ar).getValue().toString().isEmpty()?"0":tbNewUnitPrice.get(ar).getValue().toString().replace(",", ""));
		amount=qty*rate;
		tbNewAmount.get(ar).setValue(cm.setComma(amount));
	}
	private void tableRowAdd(final int ar)
	{
		sl.add(ar, new Label(""));
		sl.get(ar).setWidth("100%");
		sl.get(ar).setValue(ar+1);
		
		tbItemId.add(ar, new Label());
		tbItemId.get(ar).setWidth("100%");
		
		tbLedgerId.add(ar, new Label());
		tbLedgerId.get(ar).setWidth("100%");
		
		tbItemName.add(ar, new Label());
		tbItemName.get(ar).setWidth("100%");
		
		tbUnit.add(ar, new Label());
		tbUnit.get(ar).setWidth("100%");
		
		tbLcQty.add(ar, new TextRead(1));
		tbLcQty.get(ar).setWidth("100%");
		tbLcQty.get(ar).setImmediate(true);

		tbRcvQty.add(ar, new AmountField());
		tbRcvQty.get(ar).setWidth("100%");
		tbRcvQty.get(ar).setImmediate(true);
		
		tbRcvQty.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(!tbRcvQty.get(ar).getValue().toString().isEmpty()){
					rateCalculation(ar);
				}
				else{
					tbNewUnitPrice.get(ar).setValue("");
				}
			}
		});
		
		tbUnitPrice.add(ar, new AmountField());
		tbUnitPrice.get(ar).setWidth("100%");
		tbUnitPrice.get(ar).setImmediate(true);
		
		tbAmount.add(ar, new TextRead(1));
		tbAmount.get(ar).setWidth("100%");
		tbAmount.get(ar).setImmediate(true);

		tbNewUnitPrice.add(ar, new AmountField());
		tbNewUnitPrice.get(ar).setWidth("100%");
		tbNewUnitPrice.get(ar).setImmediate(true);
		
		tbNewUnitPrice.get(ar).addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				amountBDTCalc(ar);
			}
		});
		
		tbNewAmount.add(ar, new TextRead(1));
		tbNewAmount.get(ar).setWidth("100%");
		tbNewAmount.get(ar).setImmediate(true);
		
		tbCmbStore.add(ar,new ComboBox());
		tbCmbStore.get(ar).setWidth("100%");
		tbCmbStore.get(ar).setImmediate(true);
		tbCmbStore.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		Iterator<?> iter=dbService("select  vDepoId,vDepoName from tbDepoInformation");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			tbCmbStore.get(ar).addItem(element[0]);
			tbCmbStore.get(ar).setItemCaption(element[0], element[1].toString());
		}
		
		tbTxtRemarks.add(ar, new TextField());
		tbTxtRemarks.get(ar).setWidth("100%");
		tbTxtRemarks.get(ar).setImmediate(true);
		
		table.addItem(new Object[]{sl.get(ar),tbItemId.get(ar),tbLedgerId.get(ar),tbItemName.get(ar),tbUnit.get(ar),tbLcQty.get(ar),
				tbRcvQty.get(ar),tbUnitPrice.get(ar),tbAmount.get(ar),tbNewUnitPrice.get(ar),
				tbNewAmount.get(ar),tbCmbStore.get(ar),tbTxtRemarks.get(ar)},ar);
	}
	private AbsoluteLayout buildMainLayout(){
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("1180px");
		setHeight("540px");
		
		txtmrrNo = new TextRead(1);
		txtmrrNo.setImmediate(true);
		txtmrrNo.setWidth("120px");
		txtmrrNo.setHeight("24px");
		mainLayout.addComponent(new Label("MRR No : "),"top:20px;left:20px;");
		mainLayout.addComponent(txtmrrNo,"top:18px;left:140px;");
		
		cmbLCNo= new ComboBox();
		cmbLCNo.setImmediate(true);
		cmbLCNo.setWidth("210px");
		cmbLCNo.setHeight("24px");
		cmbLCNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//txtLCNo.setReadOnly(true);
		//cmbLCNo.setEnabled(false);
		mainLayout.addComponent(new Label("L/C No :"),"top:45px;left:20px;");
		mainLayout.addComponent(cmbLCNo, "top:43.0px;left:140.0px;");
		
		txtLedgerBalance= new TextRead(1);
		txtLedgerBalance.setImmediate(true);
		txtLedgerBalance.setWidth("120px");
		txtLedgerBalance.setHeight("24px");
		mainLayout.addComponent(new Label("Ledger Balance :"),"top:70px;left:360px;");
		mainLayout.addComponent(txtLedgerBalance, "top:68.0px;left:460.0px;");
		
		// txtreceiptNo
				txtreceiptNo = new TextRead(1);
				txtreceiptNo.setImmediate(true);
				txtreceiptNo.setWidth("120px");
				txtreceiptNo.setHeight("-1px");
				mainLayout.addComponent(txtreceiptNo, "top:68.0px;left:140.0px;");
				mainLayout.addComponent(new Label("Receipt No: "), "top:70.0px;left:20.0px;");
		
		dDate = new PopupDateField();
		dDate.setWidth("120px");
		dDate.setHeight("-1px");
		dDate.setImmediate(true);
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		mainLayout.addComponent(new Label("Date : "),"top:20px;left:360px;");
		mainLayout.addComponent(dDate, "top:18.0px;left:460.0px;");
		
		txtVoucherNo= new TextRead(1);
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("120px");
		txtVoucherNo.setHeight("24px");
		txtVoucherNo.setVisible(false);
		
		//mainLayout.addComponent(new Label("Voucher No :"),"top:55px;left:740px;");
		mainLayout.addComponent(txtVoucherNo, "top:55.0px;left:840.0px;");
		
		txtLCValueUSD= new TextRead(1);
		txtLCValueUSD.setImmediate(true);
		txtLCValueUSD.setWidth("120px");
		txtLCValueUSD.setHeight("24px");
		mainLayout.addComponent(new Label("L/C Value USD :"),"top:45px;left:360px;");
		mainLayout.addComponent(txtLCValueUSD, "top:43.0px;left:460.0px;");
		
		table.setFooterVisible(true);
		table.setWidth("1160px");
		table.setHeight("280px");
		table.setColumnCollapsingAllowed(true);
		table.setColumnFooter("Amount: ", "");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Product ID", Label.class, new Label());
		table.setColumnWidth("Product ID", 30);
		table.setColumnCollapsed("Product ID", true);
		
		table.addContainerProperty("Ledger ID", Label.class, new Label());
		table.setColumnWidth("Ledger ID", 30);
		table.setColumnCollapsed("Ledger ID", true);
		
		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name", 300);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit", 35);
		
		table.addContainerProperty("L/C Qty", TextRead.class, new TextRead(1));
		table.setColumnWidth("L/C Qty", 70);
		
		table.addContainerProperty("RECEIVE Qty", AmountField.class, new AmountField());
		table.setColumnWidth("RECEIVE Qty", 70);
		
		table.addContainerProperty("LC Rate USD", AmountField.class, new AmountField());
		table.setColumnWidth("LC Rate USD", 60);
		
		table.addContainerProperty("LC Value USD", TextRead.class, new TextRead(1));
		table.setColumnWidth("LC Value USD", 80);
		
		table.addContainerProperty("Rate BDT", AmountField.class, new AmountField());
		table.setColumnWidth("Rate BDT", 70);
		
		table.addContainerProperty("Amount BDT", TextRead.class, new TextRead(1));
		table.setColumnWidth("Amount BDT", 90);
		
		table.addContainerProperty("Store Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Store Name", 100);
		
		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 100);
		
		/*table.addContainerProperty("New Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("New Amount", 70);*/
		

		table.setColumnFooter("Amount BDT", "Total: 0.0");
		table.setColumnFooter("Value USD", "Total: 0.0");
		
		Label lblLine = new Label("<b><font color='#e65100'>==============================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:400px;left:10px;");
		mainLayout.addComponent(cButton, "top:440px;left:350px;");
		
		mainLayout.addComponent(table,"top:100.0px;left:20.0px;");
		tableInitialize();
		
		return mainLayout;
	}
}
