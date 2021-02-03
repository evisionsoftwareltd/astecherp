package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class BankReconciliitionEntry extends Window
{
	CommonButton button = new CommonButton("", "Save", "", "", "Refresh", "", "", "Preview", "", "");
	private SessionBean sessionBean;
	private HorizontalLayout topHorLayout = new HorizontalLayout();
	private VerticalLayout vl = new VerticalLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	
	private ComboBox bankList = new ComboBox();
	private NativeSelect recType = new NativeSelect();
	//private NativeButton proceed = new NativeButton("Proceed");

	private Label ttlProduct = new Label("Total Product:");
	private Label ttlAmt = new Label("Total Amount:");

	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	
	private Table table = new Table();
	private ArrayList<Label> sl = new ArrayList<Label>();
	private ArrayList<Label> tranDate = new ArrayList<Label>();
	private ArrayList<Label> cheqNo = new ArrayList<Label>();
	private ArrayList<Label> amount = new ArrayList<Label>();
	private ArrayList<Label> type = new ArrayList<Label>();
	private ArrayList<DateField> clearDate = new ArrayList<DateField>();
	private ArrayList<Label> voucherNo = new ArrayList<Label>();
	private ArrayList<Label> chqDate = new ArrayList<Label>();

	private int tVoucher = 0;
	private SimpleDateFormat dtfDMY = new SimpleDateFormat("dd-MM-yy");
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");

	public BankReconciliitionEntry(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("825px");
		this.setCaption("BANK RECONCILATION ENTRY :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		topHorLayout.addComponent(new Label("Bank List:"));
		bankList.setImmediate(true);
		topHorLayout.addComponent(bankList);
		bankList.setWidth("260px");
		topHorLayout.addComponent(new Label("Type:"));
		topHorLayout.addComponent(recType);
		recType.setImmediate(true);
		recType.addItem(1);
		recType.setItemCaption(1,"Cleared");
		recType.addItem(2);
		recType.setItemCaption(2,"Un-Cleared");
		recType.setValue(2);
		recType.setNullSelectionAllowed(false);
		recType.setWidth("140px");
	//	topHorLayout.addComponent(button.btnPreview);
		topHorLayout.setMargin(true);
		topHorLayout.setSpacing(true);
		
		
        horLayout.addComponent(new Label("From Date :"));        
        fromDate.setValue(new java.util.Date());
        fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
        fromDate.setDateFormat("dd-MM-yy");
        fromDate.setInvalidAllowed(false);
        fromDate.setImmediate(true);
        horLayout.addComponent(fromDate);
        
        horLayout.addComponent(new Label("To Date :"));
        toDate.setValue(new java.util.Date());
        toDate.setResolution(PopupDateField.RESOLUTION_DAY);
        toDate.setDateFormat("dd-MM-yy");
        toDate.setInvalidAllowed(false);
        toDate.setImmediate(true);
        horLayout.addComponent(toDate);
		
        horLayout.addComponent(button.btnPreview);
        horLayout.setMargin(true);
        horLayout.setSpacing(true);
		
		
		//table.setSelectable(false);
		table.setWidth("740px");
		table.setHeight("255px");

		table.addContainerProperty("Sl", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.addContainerProperty("Tran. Date", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Tran. Date",70);
		table.addContainerProperty("Cheque No", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Cheque No",90);
		table.addContainerProperty("Amount", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.addContainerProperty("Type", Label.class, new Label(),null,null,Table.ALIGN_CENTER);

		table.addContainerProperty("Cleared Date",DateField.class, new DateField(),null,null,Table.ALIGN_CENTER);
		table.addContainerProperty("Voucher No", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.addContainerProperty("CHQ. Date", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		tableInitialise();
		buttonActionAdd();

		space.setWidth("320px");
		btnL.addComponent(space);
		btnL.addComponent(button);
//		btnL.addComponent(cancelBtn);
		btnL.setSpacing(true);
		vl.setMargin(true);
		vl.addComponent(table);

		this.addComponent(topHorLayout);
		this.addComponent(horLayout);
		this.addComponent(vl);
		this.addComponent(btnL);

		bankInitialise();
		setEditable(true);

		bankList.focus();
		Component ob[] = {bankList,recType,fromDate,toDate,button.btnPreview,button.btnSave,button.btnRefresh};
		new FocusMoveByEnter(this,ob);
		buttonShortCut();
	}
	private void buttonShortCut(){
		button.btnPreview.setClickShortcut(KeyCode.P, ModifierKey.ALT);
		button.btnSave.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		button.btnRefresh.setClickShortcut(KeyCode.C, ModifierKey.ALT);
	}
	
	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				proceedBtnAction(event);
			}
		});
		
		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				saveBtnAction();
			}
		});
		
		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				cancelBtnAction(event);
			}
		});
		
	}
	private void proceedBtnAction(ClickEvent event){
		if(chkDate())
		{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql = "";

			/*SELECT id.bankId,id.chequeNo,id.voucherNo,substring(id.voucherNo,1,2),
			id.chequeDate,isnull(id.clearanceDate,'1900-01-01') FROM VwChequeDetails
			WHERE isnull(id.clearanceDate,'1900-01-01') = '1900-01-01'
			ORDER BY id.chequeDate */
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			//String voucher =  "chequeDetails"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();

			if(recType.getValue().toString().equalsIgnoreCase("1"))
				sql = "SELECT distinct(id.voucherNo),id.date,(id.drAmount+id.crAmount),id.vouchertype,"+
				"id.chequeNo,id.chequeDate,id.clearanceDate,isnull(id.clearanceDate,'') "+
				"FROM VwChequeRegister WHERE id.ledgerId = '"+bankList.getValue()+
				"' AND isnull(id.clearanceDate,'1900-01-01') != '1900-01-01' and id.date >= '"+dtfYMD.format(fromDate.getValue())+"' and id.date <= '"+dtfYMD.format(toDate.getValue())+"' ORDER BY id.date";
			else
				sql = "SELECT distinct(id.voucherNo),id.date,(id.drAmount+id.crAmount),id.vouchertype,"+
				"id.chequeNo,id.chequeDate,id.clearanceDate,isnull(id.clearanceDate,'') "+
				"FROM VwChequeRegister WHERE id.ledgerId = '"+bankList.getValue()+
				"' AND isnull(id.clearanceDate,'1900-01-01') = '1900-01-01' and id.date >= '"+dtfYMD.format(fromDate.getValue())+"' and id.date <= '"+dtfYMD.format(toDate.getValue())+"' ORDER BY id.date";
			
			
			System.out.println(sql);

			List list = session.createQuery(sql).list();
			tx.commit();
			int i=0;
			tVoucher = 0;
			table.removeAllItems();
			sl.clear();
			tranDate.clear();
			cheqNo.clear();
			amount.clear();
			type.clear();
			clearDate.clear();
			voucherNo.clear();
			chqDate.clear();

			for (Iterator iter = list.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				sl.add(i,new Label());
				sl.get(i).setValue(i+1);

				tranDate.add(i,new Label());
				tranDate.get(i).setWidth("70px");
				tranDate.get(i).setValue(dtfDMY.format(Date.parse(element[1].toString().replace("-", "/").substring(0,10).trim())));

				cheqNo.add(i,new Label());
				cheqNo.get(i).setWidth("90px");
				cheqNo.get(i).setValue(element[4]);
				cheqNo.get(i).setStyleName("fleft");

				amount.add(i,new Label());
				amount.get(i).setValue(element[2]);
				amount.get(i).setStyleName("fright");
				amount.get(i).setWidth("80px");

				type.add(i,new Label());
				type.get(i).setWidth("50px");

				clearDate.add(i,new DateField());
				clearDate.get(i).setValue(null);
				clearDate.get(i).setResolution(PopupDateField.RESOLUTION_DAY);
				clearDate.get(i).setDateFormat("dd-MM-yy");
				clearDate.get(i).setInvalidAllowed(false);
				clearDate.get(i).setImmediate(true);
				if(recType.getValue().toString().equalsIgnoreCase("1"))
					clearDate.get(i).setValue(new Date(element[6].toString().replace("-", "/").substring(0,10).trim()));

				voucherNo.add(i,new Label());
				voucherNo.get(i).setValue(element[0]);
				voucherNo.get(i).setStyleName("fleft");

				chqDate.add(i,new Label());
				chqDate.get(i).setValue(dtfDMY.format(Date.parse(element[5].toString().replace("-", "/").substring(0,10).trim())));
				table.addItem(new Object[]{sl.get(i),tranDate.get(i),cheqNo.get(i),amount.get(i),type.get(i),
						clearDate.get(i),voucherNo.get(i),chqDate.get(i)},i); 
				i++;
				tVoucher++;
			}
			
			if(voucherNo.size()>0)
			{
				setEditable(false);
			}
			else
			{
				showNotification("","There are no cheque under your given criteria.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		}
	}

	private void saveBtnAction()
	{
		if(sessionBean.isSubmitable())
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save data?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{	mb.buttonLayout.getComponent(0).setEnabled(false);		
						saveData();
					}
				}
			});
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void saveData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();

			for(int i=0;i<voucherNo.size();i++)
			{
				if(clearDate.get(i).getValue()!=null)
				{
					session.createSQLQuery("UPDATE "+cheque+" SET clearanceDate = '"+dtfYMD.format(clearDate.get(i).getValue())+"' WHERE "+
							"voucher_No = '"+voucherNo.get(i).getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
					session.createSQLQuery("UPDATE "+voucher+" SET chqClear = 1,Date = '"+dtfYMD.format(clearDate.get(i).getValue())+
							"' WHERE Voucher_No = '"+voucherNo.get(i).getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

					session.createSQLQuery("INSERT INTO tbChqClearHistory(voucher_no,transDate,clearDate,ctype,userId,userIp,entryTime, companyId) " +
							"VALUES('"+voucherNo.get(i).getValue()+"','"+getTranDate(tranDate.get(i).getValue()+"")+"','"+
							dtfYMD.format(clearDate.get(i).getValue())+"','clear','"+sessionBean.getUserId()+
							"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')").executeUpdate();
				}
				else if(recType.getValue().toString().equalsIgnoreCase("1"))
				{
					session.createSQLQuery("UPDATE "+cheque+" SET clearanceDate = NULL WHERE voucher_No = '"+voucherNo.get(i).getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
					session.createSQLQuery("UPDATE "+voucher+" SET chqClear = 0 WHERE Voucher_No = '"+voucherNo.get(i).getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

					session.createSQLQuery("INSERT INTO tbChqClearHistory(voucher_no,transDate,ctype,userId,userIp,entryTime, companyId) " +
							"VALUES('"+voucherNo.get(i).getValue()+"','"+getTranDate(tranDate.get(i).getValue()+"")+"','un-clear','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')").executeUpdate();
				}
			}
			tx.commit();
			showNotification("Desired Information save successfully.");
			table.removeAllItems();
			setEditable(true);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void cancelBtnAction(ClickEvent event)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to cancel the save mode?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{

					table.removeAllItems();
					setEditable(true);
				}
			}
		});
	}
	private void tableInitialise()
	{	
		/*try
		{
			for(int i=0;i<tVoucher;i++){
				sl[i] = new Label();
				sl[i].setValue(i+1);
				tranDate[i] = new Label();
				tranDate[i].setWidth("70px");
				cheqNo[i] = new Label();
				cheqNo[i].setWidth("90px");
				amount[i] = new Label();
				amount[i].setWidth("80px");
				type[i] = new Label();
				type[i].setWidth("50px");
				clearDate[i] = new DateField();
				clearDate[i] = new DateField();
				clearDate[i].setValue(null);
				clearDate[i].setResolution(PopupDateField.RESOLUTION_DAY);
				clearDate[i].setDateFormat("dd-MM-yy");
				clearDate[i].setInvalidAllowed(false);
				clearDate[i].setImmediate(true);
				voucherNo[i] = new Label();
				chqDate[i] = new Label();
				table.addItem(new Object[]{sl[i],tranDate[i],cheqNo[i],amount[i],type[i],clearDate[i],voucherNo[i],chqDate[i]},i); 
			}
		}catch(Exception exp){
			System.out.println(exp); 
		}*/
	}
	private void bankInitialise()
	{
		//SELECT distinct(id.bankId) FROM VwChequeDetails
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			bankList.removeAllItems();
	//		System.out.println("SELECT distinct(Ledger_Id) FROM tbLedger where Create_From ='A7' and companyId = '"+sessionBean.getCompanyId()+"' ");
			List list = session.createSQLQuery("SELECT distinct(Ledger_Id) FROM tbLedger where Create_From ='A7' and companyId = '"+ sessionBean.getCompanyId() +"'").list();
			
			for (Iterator iter = list.iterator(); iter.hasNext();) 
			{
				String ledId = iter.next().toString();
				bankList.addItem(ledId);
				bankList.setItemCaption(ledId, getBankName(ledId));
				bankList.setValue(ledId);
			}
			bankList.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private String getBankName(String str)
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			return session.createSQLQuery("SELECT ledger_Name FROM TbLedger WHERE ledger_Id = '"+str+"' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"')").list().iterator().next().toString();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return "";
		}
	}
	
	private void setEditable(boolean t)
	{
		bankList.setEnabled(t);
		recType.setEnabled(t);
		button.btnPreview.setEnabled(t);
		//for(int i=0;i<clearDate.size();i++)
		//clearDate.get(i).setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
	}
	
	private String getTranDate(String str)
	{
		return "20"+str.substring(6,8)+"-"+str.substring(3,5)+"-"+str.substring(0,2);
	}
	
	private boolean chkDate()
	{


		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
			if (f.equals("1"))	

				//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				//					&&
				//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}
