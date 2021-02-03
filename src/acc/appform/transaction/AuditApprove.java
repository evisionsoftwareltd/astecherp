package acc.appform.transaction;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewer;
import com.common.share.SessionFactoryUtil;
import com.common.share.SessionBean;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.TextRead;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;

@SuppressWarnings("serial")
public class AuditApprove extends Window
{
	CommonButton button = new CommonButton("", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout dateLayout = new HorizontalLayout();
	private HorizontalLayout bottomLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private Table table = new Table();
	private NativeSelect voucherType = new NativeSelect();
	private DateField fromDate = new PopupDateField();
	private DateField toDate = new PopupDateField();
	private ArrayList<CheckBox> select = new ArrayList<CheckBox>();
	private ArrayList<Label> voucherNo = new ArrayList<Label>();
	private ArrayList<Label> voucherDate = new ArrayList<Label>();
	private ArrayList<Label> voucherDateDB = new ArrayList<Label>();
	private ArrayList<Label> Narration = new ArrayList<Label>();
	private ArrayList<TextRead> DrAmount = new ArrayList<TextRead>();
	private ArrayList<TextRead> CrAmount = new ArrayList<TextRead>();
	private ArrayList<Label> paidTo = new ArrayList<Label>();
	private ArrayList<NativeButton> btnDetails = new ArrayList<NativeButton>();

	private ArrayList<Label> userName = new ArrayList<Label>();
	private ArrayList<Label> userIp = new ArrayList<Label>();
	private ArrayList<Label> entryTime = new ArrayList<Label>();

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private NumberFormat frmt = new DecimalFormat("#0.00");
	private boolean tick = false;
	private boolean isUpdate = false;
	private String title = "";
	int row = 8;
	String h="28px";
	String w="100px";
	String strVoucher = "";

	public AuditApprove(final SessionBean sessionBean,String title)
	{
		this.title = title;
		this.sessionBean = sessionBean;		
		this.setWidth("1050px");

		if(this.title.equals("Audit"))
			this.setCaption("VOUCHER AUDIT :: "+sessionBean.getCompany());
		if(this.title.equals("Approved"))
			this.setCaption("VOUCHER APPROVE :: "+sessionBean.getCompany());

		this.setResizable(false);
		EventHandling();
		addAllComponent();

		if(this.title.equals("Audit"))
		{
			button.btnSave.setCaption("Audited");
			button.btnSave.setWidth("80px");
		}
		if(this.title.equals("Approved"))
		{
			button.btnSave.setCaption("Approved");
			button.btnSave.setWidth("90px");
		}

		this.addComponent(mainLayout);
		button.btnNew.focus();
		btnIni(true);
		button.btnEdit.setVisible(false);
	}

	private void EventHandling()
	{
		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				updateEvent();
				isUpdate = true;
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				final MessageBox mb = new MessageBox(getParent(), "Are You Sure?" + sessionBean.getCompany(), MessageBox.Icon.QUESTION, "Do You Want To Save All Data.", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							Transaction tx = session.beginTransaction();
							if(isUpdate)
							{
								try
								{
									mb.buttonLayout.getComponent(0).setEnabled(false);
									saveEvent(session);
									btnIni(true);
									isUpdate = false;
									strVoucher = "";
									tx.commit();
									tableClear();
									showNotification("Information :", "Updated Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
								}
								catch(Exception ex)
								{
									tx.rollback();
									showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								try
								{
									mb.buttonLayout.getComponent(0).setEnabled(false);
									saveEvent(session);
									btnIni(true);
									strVoucher = "";
									tx.commit();
									tableClear();
									//	showNotification("Information :", "Saved Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
								}
								catch(Exception ex)
								{
									tx.rollback();
									showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
								}
							}
						}
					}
				});

			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				try
				{
					tableClear();
					isUpdate = false;
				}
				catch(Exception ex)
				{
					showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findEvent();
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		table.addListener(new Table.HeaderClickListener() 
		{
			public void headerClick(HeaderClickEvent event) 
			{
				//////System.out.println(event.getPropertyId());
				if(event.getPropertyId().toString() == "")
				{
					if(tick)
						tick = false;
					else
						tick = true;
					strVoucher = "";
					String temp = ""; 
					for(int i = 0; i < select.size(); i++)
					{
						//tableRowsAction(i);
						select.get(i).setValue(tick);
						if(!temp.equals(voucherNo.get(i).getValue().toString().trim()))
						{   
							if(isUpdate) {

								strVoucher = select.get(i).getValue().toString().equals("false")? strVoucher.equals("")? "'"+ voucherNo.get(i).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(i).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(i).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(i).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(i).getValue().toString()+"'", "");
								////System.out.println("KL");
								////System.out.println(strVoucher);
							}
							else	
								strVoucher = select.get(i).getValue().toString().equals("true")? strVoucher.equals("")? "'"+ voucherNo.get(i).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(i).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(i).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(i).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(i).getValue().toString()+"'", "");
								////System.out.println("KL");
								strVoucher = !strVoucher.equals("") &&  strVoucher.substring(0,1).equals(",")? strVoucher.replaceFirst(",", ""):strVoucher;
								temp = voucherNo.get(i).getValue().toString().trim();		

								////System.out.println(strVoucher);

						}
					}
				}
			}
		});		

	}

	private void findEvent() 
	{
		String query = "";
		try
		{
			if(dateCompare())
			{
				/*Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				Transaction tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;*/
				String voucher = new AstechacApplication().tbVoucherName(dtfYMD.format(toDate.getValue()));
				//System.out.println("haha : "+VOU);

				if(this.title.equals("Audit"))
				{
					//					query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from vwVoucher where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 0 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("dca"))
						query = "SELECT v.Voucher_No, convert(varchar,v.Date,105) Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM  "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId,v.AuditApproveFlag,l.companyId,v.userId,v.userIp,v.entryTime HAVING v.CrAmount = 0 and (v.vouchertype = 'dca') AND (v.companyId = '"+ sessionBean.getCompanyId() +"')  AND (l.companyId = '"+ sessionBean.getCompanyId() +"') and v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 0 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("dba"))
						query = "SELECT     v.Voucher_No, CONVERT(varchar, v.Date, 105) AS Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb  FROM "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId, v.auditapproveflag,l.companyId,v.userId,v.userIp,v.entryTime HAVING (v.vouchertype = 'dba') AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') AND (v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"') AND (v.auditapproveflag = 0) AND (v.CrAmount = 0) order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No ";
					if(voucherType.getValue().toString().equalsIgnoreCase("cca"))
						query = "SELECT v.Voucher_No, convert(varchar,v.Date,105) Date, l.Ledger_Name, v.DrAmount, v.CrAmount, ISNULL(v.TransactionWith, '')  AS TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM  "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, ISNULL(v.TransactionWith, ''), l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId,v.AuditApproveFlag,l.companyId,v.userId,v.userIp,v.entryTime HAVING      v.DrAmount = 0 AND (v.vouchertype in('cca','cci')) AND (v.companyId = '"+ sessionBean.getCompanyId() +"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') and v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 0 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("cba"))
						query = "SELECT     v.Voucher_No, CONVERT(varchar, v.Date, 105) AS Date, l.Ledger_Name, v.DrAmount, v.CrAmount, ISNULL(v.TransactionWith, '')  AS TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, ISNULL(v.TransactionWith, ''), l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId, v.auditapproveflag,l.companyId,v.userId,v.userIp,v.entryTime HAVING (v.vouchertype in ('cba','cbi'))AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') AND (v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"') AND (v.auditapproveflag = 0) AND (v.CrAmount <> 0) order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No ";
					if(voucherType.getValue().toString().equalsIgnoreCase("jau"))
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.Ledger_Name from tbLedger as led where led.Ledger_Id = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"' AND  v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 0 and ((v.vouchertype = 'jau') or (v.vouchertype = 'jcv') or (v.vouchertype = 'jai')) order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("pao"))
						//query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"' AND  Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and isnull(AuditApproveFlag,0) = 0 and vouchertype = 'pao' order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.Ledger_Name from tbLedger as led where led.Ledger_Id = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"' AND  v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and isnull(v.AuditApproveFlag,0) = 0 and (v.vouchertype = 'pao') and v.CrAmount = 0  order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("daj"))
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.vDescription from tbFixedAsset as led where led.vLedgerID = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"' AND  v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and isnull(v.AuditApproveFlag,0) = 0 and (v.vouchertype = 'daj') and v.DrAmount = 0 order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					//query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and isnull(AuditApproveFlag,0) = 0 and vouchertype = 'daj' order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";

				}
				if(this.title.equals("Approved"))
				{
					//					query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from vwVoucher where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 1 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("dca"))
						query = "SELECT v.Voucher_No, convert(varchar,v.Date,105) Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM  "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId,v.AuditApproveFlag,l.companyId,v.userId,v.userIp,v.entryTime HAVING    v.CrAmount = 0  AND (v.vouchertype = 'dca') AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') and v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 1 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("dba"))
						query = "SELECT     v.Voucher_No, CONVERT(varchar, v.Date, 105) AS Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId, v.auditapproveflag,l.companyId,v.userId,v.userIp,v.entryTime HAVING (v.vouchertype = 'dba') AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') AND (v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"') AND (v.auditapproveflag = 1) AND (v.CrAmount = 0) order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No ";
					if(voucherType.getValue().toString().equalsIgnoreCase("cca"))
						query = "SELECT v.Voucher_No, convert(varchar,v.Date,105) Date, l.Ledger_Name, v.DrAmount, v.CrAmount, ISNULL(v.TransactionWith, '')  AS TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM  "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, ISNULL(v.TransactionWith, ''), l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId,v.AuditApproveFlag,l.companyId,v.userId,v.userIp,v.entryTime HAVING    v.DrAmount = 0  AND (v.vouchertype in('cca','cci')) AND (v.companyId = '"+ sessionBean.getCompanyId() +"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') and v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 1 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					//query = "SELECT v.Voucher_No, convert(varchar,v.Date,105) Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith FROM  dbo.vwVoucher AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId,v.AuditApproveFlag HAVING      (v.Ledger_Id <> 'AL183') AND (v.vouchertype = 'cca') AND (v.companyId = '"+ sessionBean.getCompanyId() +"') and v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 1 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("cba"))
						query = "SELECT     v.Voucher_No, CONVERT(varchar, v.Date, 105) AS Date, l.Ledger_Name, v.DrAmount, v.CrAmount, ISNULL(v.TransactionWith, '')  AS TransactionWith,(Select distinct name from tbLogin as l where l.userId=v.userId)as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb FROM "+voucher+" AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, ISNULL(v.TransactionWith, ''), l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId, v.auditapproveflag,l.companyId,v.userId,v.userIp,v.entryTime HAVING (v.vouchertype in ('cba','cbi'))AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (l.companyId = '"+ sessionBean.getCompanyId() +"') AND (v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"') AND (v.auditapproveflag = 1) AND (v.CrAmount <> 0) order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No ";
					//query = "SELECT     v.Voucher_No, CONVERT(varchar, v.Date, 105) AS Date, l.Ledger_Name, v.DrAmount, v.CrAmount, v.TransactionWith FROM dbo.vwVoucher AS v LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id GROUP BY v.Voucher_No, v.Date, v.TransactionWith, l.Ledger_Name, v.DrAmount, v.Ledger_Id, v.CrAmount, v.vouchertype, v.companyId, v.auditapproveflag HAVING (v.vouchertype = 'cba') AND (v.companyId = '"+ sessionBean.getCompanyId()+"') AND (v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"') AND (v.auditapproveflag = 1) AND (v.CrAmount <> 0) order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No ";
					if(voucherType.getValue().toString().equalsIgnoreCase("jau"))
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.Ledger_Name from tbLedger as led where led.Ledger_Id = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"'  AND v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and v.AuditApproveFlag = 1  and ((v.vouchertype = 'jau') or (v.vouchertype = 'jcv') or (v.vouchertype = 'jai')) order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("pao"))
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.Ledger_Name from tbLedger as led where led.Ledger_Id = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"' AND  v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"'  and isnull(v.AuditApproveFlag,0) = 1 and (v.vouchertype = 'pao') and v.CrAmount = 0  order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					//query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"'  AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 1 and vouchertype = 'pao' order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
					if(voucherType.getValue().toString().equalsIgnoreCase("daj"))
						query = "select v.Voucher_No, convert(varchar,v.Date,105) Date, (Select distinct led.vDescription from tbFixedAsset as led where led.vLedgerID = v.Ledger_Id) as ledgerName, v.DrAmount, v.CrAmount, isnull(v.TransactionWith, '')TransactionWith,(Select distinct l.name from tbLogin as l where l.userId = v.userId) as userName,v.userIp,v.entryTime,convert(Date,v.Date,105) Datedb from "+voucher+" as v where v.companyId = '"+ sessionBean.getCompanyId() +"' AND  v.Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and isnull(v.AuditApproveFlag,0) = 1 and (v.vouchertype = 'daj') and v.DrAmount = 0 order by convert(int,replace(v.Voucher_No,substring(v.Voucher_No,0,6),'')) desc, v.Date, v.Voucher_No";
					//query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 1 and vouchertype = 'daj' order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No";
				}
				//		////System.out.println(query);
				System.out.println(query);
				tableFill(query, 0);
				btnIni(false);
			}
			else
				showNotification("Warning :", "Please Enter Date between Fiscal Year.", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void tableFill(String query, int a)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		List<?> list = session.createSQLQuery(query).list();
		int i = 0;
		for(Iterator<?> iter = list.iterator();iter.hasNext();)
		{
			Object[] element = (Object[]) iter.next();				
			if(select.size() <= i)
				tableRowAdd(i);
			select.get(i).setValue(a == 1?true: false);
			voucherNo.get(i).setValue(element[0].toString());
			voucherDate.get(i).setValue(element[1].toString());
		
			Narration.get(i).setValue(element[2].toString());
			DrAmount.get(i).setValue(frmt.format(Double.parseDouble(element[3].toString())));
			CrAmount.get(i).setValue(frmt.format(Double.parseDouble(element[4].toString())));
			paidTo.get(i).setValue(element[5].toString());
			userName.get(i).setValue(element[6].toString());
			userIp.get(i).setValue(element[7].toString());
			entryTime.get(i).setValue(element[8].toString());
			voucherDateDB.get(i).setValue(element[9]);
			i++;

		}
	}

	private void updateEvent()
	{
		String query = "";

		try
		{
			if(dateCompare())
			{
				String voucher = new AstechacApplication().tbVoucherName(dtfYMD.format(toDate.getValue()));

				if(this.title.equals("Audit"))
					query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith,convert(Date,Date,105) dbDate from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 1 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No  ";
				
				if(this.title.equals("Approved"))
					query = "select Voucher_No, convert(varchar,Date,105) Date, Narration, DrAmount, CrAmount, isnull(TransactionWith, '')TransactionWith,convert(Date,Date,105) dbDate  from "+voucher+" where companyId = '"+ sessionBean.getCompanyId() +"' AND Date between '"+ dtfYMD.format(fromDate.getValue()) +"' and '"+ dtfYMD.format(toDate.getValue()) +"' and AuditApproveFlag = 2 order by convert(int,replace(Voucher_No,substring(Voucher_No,0,6),'')) desc, Date, Voucher_No  ";
				tableFill(query, 1);
				btnIni(false);
			}
			else
				showNotification("Warning :", "Please Enter Date in Fiscal Year.", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveEvent(Session session)
	{
		if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(toDate.getValue())))
		{
			String query = "";

			String voucher = new AstechacApplication().tbVoucherName(dtfYMD.format(toDate.getValue()));

			if(isUpdate)				
				for(int i = 0; i < select.size(); i++)
				{
					if(select.get(i).getValue().toString().equals("false"))
					{
						if(this.title.equals("Audit"))
							query = "update "+voucher+" set AuditApproveFlag = 0 where Voucher_No in ("+ strVoucher +") AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
						if(this.title.equals("Approved"))
							query = "update "+voucher+" set AuditApproveFlag = 1,chqClear = 1 where Voucher_No in ("+ strVoucher +") AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
						session.createSQLQuery(query).executeUpdate();
					}
				}
			else
			{	int j = 0;
				for(int i = 0; i < select.size(); i++)
				{
					if(select.get(i).booleanValue())
					{
						if(this.title.equals("Audit"))
						{	
							if(sessionBean.isAdmin()||sessionBean.isSuperAdmin())
							{
								query = "update "+voucher+" set AuditApproveFlag = 1,audit_by = '" + sessionBean.getUserId().toString() + "' where Voucher_No in ("+ strVoucher +") AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
								session.createSQLQuery(query).executeUpdate();
								if(j == 0)
								{
									showNotification("Information :", "Voucher Audited Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
								}
								j++;
							}
							else
							{
								showNotification("Authentication Failed","You have not proper authentication for audit.",Notification.TYPE_ERROR_MESSAGE);
							}
						}

						if(this.title.equals("Approved"))
						{
							if(sessionBean.isSuperAdmin())
							{
								query = "update "+voucher+" set AuditApproveFlag = 2,approve_by = '" + sessionBean.getUserId().toString() + "',chqClear = 1  where Voucher_No in ("+ strVoucher +") AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
								session.createSQLQuery(query).executeUpdate();
								if(j == 0)
								{
									showNotification("Information :", "Voucher Approved Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
								}
								j++;
							}
							else
							{
								showNotification("Authentication Failed","You have not proper authentication for approval.",Notification.TYPE_ERROR_MESSAGE);
							}
						}
					}
				}
			}
		}
		else
			showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	
	}

	private void addAllComponent()
	{
		voucherType.setWidth("175px");
		voucherType.setImmediate(true);
		voucherType.addItem("dca");
		voucherType.setItemCaption("dca", "Cash Payment Voucher");
		voucherType.addItem("cca");
		voucherType.setItemCaption("cca", "Cash Receipt Voucher");
		voucherType.addItem("dba");
		voucherType.setItemCaption("dba", "Bank Payment Voucher");
		voucherType.addItem("cba");
		voucherType.setItemCaption("cba", "Bank Receipt Voucher");
		voucherType.addItem("jau");
		voucherType.setItemCaption("jau", "Journal Voucher");//Apllicable for "jcv" also 
		voucherType.addItem("pao");
		voucherType.setItemCaption("pao", "FixedAsset Voucher");
		voucherType.addItem("daj");
		voucherType.setItemCaption("daj", "Deprication Voucher");

		voucherType.setValue("dca");
		voucherType.setNullSelectionAllowed(false);

		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		dateLayout.addComponent(new Label("Voucher Type :"));
		dateLayout.addComponent(voucherType);
		dateLayout.addComponent(new Label("From :"));
		dateLayout.addComponent(fromDate);	
		dateLayout.addComponent(new Label("To :"));
		dateLayout.addComponent(toDate);
		dateLayout.setSpacing(true);

		dateLayout.addComponent(button.btnFind);
		mainLayout.addComponent(dateLayout);
		mainLayout.setComponentAlignment(dateLayout, Alignment.MIDDLE_CENTER);		
		mainLayout.addComponent(table);
		mainLayout.addComponent(bottomLayout);
		btnLayout.addComponent(button);		
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);
		table.setWidth("1200px");
		table.setHeight("350px");
		table.addContainerProperty("", CheckBox.class, null);		
		table.setColumnWidth("", 18);
		table.addContainerProperty("Voucher No", Label.class,null);
		table.setColumnWidth("Voucher No", 70);
		table.addContainerProperty("Date", Label.class, null);
		table.setColumnWidth("Date", 65);
		table.addContainerProperty("Ledger Name", Label.class,null);		
		table.setColumnWidth("Ledger Name", 180);
		table.addContainerProperty("Dr Amount", TextRead.class, null);
		table.setColumnWidth("Dr Amount", 60);
		table.addContainerProperty("Cr Amount", TextRead.class, null);
		table.setColumnWidth("Cr Amount", 60);
		table.addContainerProperty("Paid To", Label.class, null);
		table.setColumnWidth("Paid To", 85);
		/*table.addContainerProperty("Details", NativeButton.class, null);
		table.setColumnWidth("Details", 35);*/

		table.addContainerProperty("User Name", Label.class, null);
		table.setColumnWidth("User Name", 70);
		table.addContainerProperty("User Ip", Label.class, null);
		table.setColumnWidth("User Ip", 80);
		table.addContainerProperty("Entry Time", Label.class, null);
		table.setColumnWidth("Entry Time", 140);

		table.addContainerProperty("Details", NativeButton.class, null);
		table.setColumnWidth("Details", 35);

		bottomLayout.setSpacing(true);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		table.setImmediate(true);
		mainLayout.setSpacing(true);
		Component ob[]={voucherType,fromDate,toDate,button.btnFind,button.btnEdit, button.btnSave, button.btnRefresh};
		new FocusMoveByEnter(this,ob);

		//Component ob[] = {button.btnUpdate, button.btnSave, button.btnRefresh};
		//new FocusMoveByEnter(this,ob);
	}

	/*private void tableInitialise()
	{		
		for(int i=0;i<8;i++)
		{
			tableRowAdd(i);
		}
	}*/
	//private void


	private void tableRowAdd(final int ans)
	{
		select.add(ans, new CheckBox());
		select.get(ans).setWidth("100%");
		select.get(ans).setImmediate(true);
		voucherNo.add(ans, new Label());
		voucherNo.get(ans).setWidth("100%");
		voucherDate.add(ans, new Label());
		voucherDate.get(ans).setWidth("100%");
		
		voucherDateDB.add(ans, new Label());
		voucherDateDB.get(ans).setWidth("100%");
		
		
		Narration.add(ans, new Label());
		Narration.get(ans).setWidth("100%");
		DrAmount.add(ans, new TextRead());
		DrAmount.get(ans).setWidth("100%");
		CrAmount.add(ans, new TextRead());
		CrAmount.get(ans).setWidth("100%");
		paidTo.add(ans, new Label());
		paidTo.get(ans).setWidth("100%");
		/*btnDetails.add(ans, new NativeButton("..."));
		btnDetails.get(ans).setWidth("30px");*/

		userName.add(ans, new Label());
		userName.get(ans).setWidth("100%");
		userIp.add(ans, new Label());
		userIp.get(ans).setWidth("100%");
		entryTime.add(ans, new Label());
		entryTime.get(ans).setWidth("100%");

		btnDetails.add(ans, new NativeButton("..."));
		btnDetails.get(ans).setWidth("35px");

		select.get(ans).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				strVoucher = "";
				tableRowsAction(ans);				
			}
		});

		btnDetails.get(ans).addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				detailsAction(ans);
			}
		});

		//	table.addItem(new Object[]{select.get(ans),voucherNo.get(ans), voucherDate.get(ans), Narration.get(ans), DrAmount.get(ans), CrAmount.get(ans), paidTo.get(ans), btnDetails.get(ans),userName.get(ans),userIp.get(ans),entryTime.get(ans)},ans);
		table.addItem(new Object[]{select.get(ans),voucherNo.get(ans), voucherDate.get(ans), Narration.get(ans), DrAmount.get(ans), CrAmount.get(ans), paidTo.get(ans),userName.get(ans),userIp.get(ans),entryTime.get(ans), btnDetails.get(ans)},ans);

	}

	private void tableRowsAction(int i)
	{
		try
		{
			if(isUpdate)
				strVoucher = select.get(i).getValue().toString().equals("false")? strVoucher.equals("")? "'"+ voucherNo.get(i).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(i).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(i).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(i).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(i).getValue().toString()+"'", "");
				else
					strVoucher = select.get(i).getValue().toString().equals("true")? strVoucher.equals("")? "'"+ voucherNo.get(i).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(i).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(i).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(i).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(i).getValue().toString()+"'", "");


					strVoucher = !strVoucher.equals("") &&  strVoucher.substring(0,1).equals(",")? strVoucher.replaceFirst(",", ""):strVoucher;


					int start = i <= 20?0:i-20;

					int end = i + 20 >= select.size()? select.size(): i+20;

					String voucher = voucherNo.get(i).getValue().toString();
					//strVoucher = voucherNo.get(i).getValue().toString();
					strVoucher = select.get(i).getValue().toString().equals("false")? strVoucher.equals("")? "'"+ voucherNo.get(i).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(i).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(i).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(i).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(i).getValue().toString()+"'", "");
					////System.out.println("m2l");

					for( int j = start; j < end; j++)
					{
						if(i != j)
							if(voucher.equals(voucherNo.get(j).getValue().toString()))
							{
								select.get(j).setValue(select.get(i).getValue().toString().equals("true")?true: false);
							}

					}



					///////////////////

					if(tick)
						tick = false;
					else
						tick = true;
					strVoucher = "";
					String temp = ""; 
					for(int k = 0; k < select.size(); k++)
					{
						//tableRowsAction(i);
						//	select.get(k).setValue(tick);
						if(!temp.equals(voucherNo.get(k).getValue().toString().trim()))
						{   
							if(isUpdate) 
							{

								strVoucher = select.get(k).getValue().toString().equals("false")? strVoucher.equals("")? "'"+ voucherNo.get(k).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(k).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(k).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(k).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(k).getValue().toString()+"'", "");
								////System.out.println("KL");
								////System.out.println(strVoucher);
							}
							else	
								strVoucher = select.get(k).getValue().toString().equals("true")? strVoucher.equals("")? "'"+ voucherNo.get(k).getValue().toString()+"'":strVoucher + ","+ "'" +voucherNo.get(k).getValue().toString()+"'":strVoucher.indexOf("'"+voucherNo.get(k).getValue().toString()+"'")<= 0?strVoucher.replaceAll("'"+voucherNo.get(k).getValue().toString()+"'", ""):strVoucher.replaceAll(","+"'"+voucherNo.get(k).getValue().toString()+"'", "");

								strVoucher = !strVoucher.equals("") &&  strVoucher.substring(0,1).equals(",")? strVoucher.replaceFirst(",", ""):strVoucher;
								temp = voucherNo.get(k).getValue().toString().trim();		

								////System.out.println(strVoucher);

						}
					}
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void detailsAction(int i)
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().openSession();
	    tx=session.beginTransaction();
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String link = getApplication().getURL().toString();
			Window win =null;
			
			System.out.println("Date is:"+voucherDateDB.get(i).getValue());
			String d1=voucherDateDB.get(i).getValue().toString();
		    Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(d1); 
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date1)+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;

			if(!fsl.equals("0"))
			{
				session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				tx.commit();
			}
			
			
			if(link.endsWith("astecherp/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("astecherp/", ""));
			}

			if(voucherNo.get(i).getValue().toString().substring(0, 6).equals("DR-BK-"))
			{
				String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+voucherNo.get(i).getValue().toString()+"')"
						+ " AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int), CrAmount DESC";
				hm.put("sql",sql);
				 win = new ReportViewer(hm,"report/account/voucher/BankPaymentVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("BANK PAYMENT VOUCHER :: "+sessionBean.getCompany());
			}
			else if (voucherNo.get(i).getValue().toString().substring(0, 6).equals("CR-BK-"))
			{
				//String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+voucherNo.get(i).getValue().toString()+"') and companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount Desc";
				String sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+voucherNo.get(i).getValue().toString()+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount Desc";

				System.out.println(sql);

				hm.put("sql",sql);
				win = new ReportViewer(hm,"report/account/voucher/BankReceiveVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				getParent().getWindow().addWindow(win);
				win.setCaption("BANK RECEIVED VOUCHER :: "+sessionBean.getCompany());
			}

			else if (voucherNo.get(i).getValue().toString().substring(0, 6).equals("DR-CH-"))
			{
				int voucherCount = 1;

				Iterator<?> iter = session.createSQLQuery(" select COUNT(Voucher_No) from vwVoucher where Voucher_No = '"+voucherNo.get(i).getValue().toString()+"' ").list().iterator();
				if(iter.hasNext())
					voucherCount = Integer.valueOf(iter.next().toString());

				if(voucherCount>2)
				{
					String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+voucherNo.get(i).getValue().toString()+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";
					System.out.println(sql);
					hm.put("sql",sql);
					win = new ReportViewer(hm,"report/account/voucher/CashPaymentVoucher.jasper",
							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				}
				else
				{
					String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+voucherNo.get(i).getValue().toString()+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";
					System.out.println(sql);
					hm.put("sql",sql);
					win = new ReportViewer(hm,"report/account/voucher/CashPaymentVoucher.jasper",
							getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				}

				win.setStyleName("cwindow");
				getParent().getWindow().addWindow(win);
				win.setCaption("CASH PAYMENT VOUCHER :: "+sessionBean.getCompany());
			}
			else if (voucherNo.get(i).getValue().toString().substring(0, 6).equals("CR-CH-"))
			{
				//String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+voucherNo.get(i).getValue().toString()+"')  AND (companyId = '"+ sessionBean.getCompanyId() +"') AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";
				String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+voucherNo.get(i).getValue().toString()+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";

				hm.put("sql",sql);
				System.out.println(sql);
				win = new ReportViewer(hm,"report/account/voucher/CashReceipttVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				getParent().getWindow().addWindow(win);
				win.setCaption("CASH RECEIVED VOUCHER :: "+sessionBean.getCompany());
			}

			else if (voucherNo.get(i).getValue().toString().substring(0, 6).equals("JV-NO-"))
			{
				String	sql = "SELECT * FROM vwJournalVoucher WHERE Voucher_No in('"+voucherNo.get(i).getValue().toString()+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
				System.out.println(sql);

				hm.put("sql",sql);
				win = new ReportViewer(hm,"report/account/voucher/JournalVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				getParent().getWindow().addWindow(win);
				win.setCaption("JOURNAL VOUCHER :: "+sessionBean.getCompany());
			}

		}
		catch(Exception ex)
		{
			showNotification("Warning!", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void tableClear()
	{
		try
		{
			strVoucher = "";
			select.clear();
			voucherNo.clear();
			voucherDate.clear();
			voucherDateDB.clear();
			Narration.clear();
			DrAmount.clear();
			CrAmount.clear();
			paidTo.clear();
			btnDetails.clear();
			table.removeAllItems();
			btnIni(true);
		}
		catch(Exception ex)
		{
			showNotification("Warning!", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}	

	private void btnIni(boolean stat)
	{
		button.btnEdit.setEnabled(stat);
		button.btnSave.setEnabled(!stat);
		button.btnRefresh.setEnabled(!stat);
		button.btnFind.setEnabled(stat);
	}

	@SuppressWarnings("deprecation")
	private boolean dateCompare()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			if(f.equals("1"))
			{
				return true;
			}
			else
			{
				showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
}