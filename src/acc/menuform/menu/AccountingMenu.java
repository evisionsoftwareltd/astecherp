package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.accountsSetup.ChequeBookCancel;
import acc.appform.accountsSetup.ChequeBookEntry;
import acc.appform.accountsSetup.CostInformation;
import acc.appform.accountsSetup.GroupCreate;
import acc.appform.accountsSetup.LedgerCreate;
import acc.appform.accountsSetup.Narration;
import acc.appform.accountsSetup.SubGroupCreate;
import acc.appform.accountsSetup.TransferLedger;
import acc.appform.accountsSetup.YearlyBudget;
import acc.appform.accountsSetup.fiscalYearClosing;
import acc.appform.transaction.*;
import acc.reportmodule.daybook.DayBook;
import acc.reportmodule.financialStatement.AdjustedTrialRpt;
import acc.reportmodule.financialStatement.AdjustedTrialRptBetDate;
import acc.reportmodule.financialStatement.BalanceSheetRpt;
import acc.reportmodule.financialStatement.CashTrialRpt;
import acc.reportmodule.financialStatement.OpeningTrialRpt;
import acc.reportmodule.financialStatement.ProfitLossRpt;
import acc.reportmodule.ledger.GeneralLedger;
import acc.reportmodule.ledger.LedgerGroup;
import acc.reportmodule.mis.BankPositionRpt;
import acc.reportmodule.mis.CashFlowYearly;
import acc.reportmodule.mis.ChartOfAccounts;
import acc.reportmodule.mis.DailyCashStatementRpt;
import acc.reportmodule.mis.DailyTransactionRpt;
import acc.reportmodule.mis.GroupLedgerSummary;
import acc.reportmodule.mis.InterestCalculation;
import acc.reportmodule.mis.MonthlyExpensetRpt;
import acc.reportmodule.mis.ReceivableAging;
import acc.reportmodule.mis.RptCancelChequeList;
import acc.reportmodule.mis.RptTransactionTracking;
import acc.reportmodule.mis.RptVoucherEditDelete;
import acc.reportmodule.mis.TestGroupLedgerSummary;
import acc.reportmodule.voucher.UnusedChequeList;
import acc.reportmodule.voucher.Voucher;
import acc.reportmodule.voucher.accessBalanceSheet;
import acc.reportmodule.voucher.accessDayBooks;
import acc.reportmodule.voucher.accessLedger;
import acc.reportmodule.voucher.accessMis;
import acc.reportmodule.voucher.accessProfitLoss;
import acc.reportmodule.voucher.accessTrialBalance;
import acc.reportmodule.voucher.accessVoucher;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.rawMaterialTransaction.PurchaeVoucher;
import com.reportform.setupReport.RptDataEntryStatus;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class AccountingMenu
{	
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	private Tree tree;
	private SessionBean sessionBean;
	private Component component;

	private Object masterSetup = null;
	private Object accountsTransaction = null;
	private Object accountsReport = null;

	//Report Module
	private Object voucher = null;
	private Object dayBooks = null;
	private Object ledger = null;
	private Object trialBalance = null;
	private Object profitLossAccount = null;
	private Object balanceSheet = null;
	private Object mis = null;

	public AccountingMenu(Object accountingMenu,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("acccountsMastersetup"))
		{
			masterSetup = addCaptionedItem("ACCOUNTS MASTER SETUP", accountingMenu);
			masterSetup(masterSetup);
		}
		if(isValidMenu("acccountsTransaction"))
		{
			accountsTransaction = addCaptionedItem("ACCOUNTS TRANSACTION", accountingMenu);
			trasacTion(accountsTransaction);
		}
		if(isValidMenu("accountsReports"))
		{
			accountsReport = addCaptionedItem("ACCOUNTS REPORT", accountingMenu);
			addChild(accountsReport);
		}
	}

	private void masterSetup(Object masterSetup)
	{
		if(isValidMenu("costCenterInformation"))
		{
			addCaptionedItem("COST CENTER INFORMATION", masterSetup);
		}
		if(isValidMenu("createGroup"))
		{
			addCaptionedItem("CREATE GROUP", masterSetup);
		}
		if(isValidMenu("createSubGroup"))
		{
			addCaptionedItem("CREATE SUB GROUP", masterSetup);
		}
		if(isValidMenu("createLedger"))
		{
			addCaptionedItem("CREATE LEDGER", masterSetup);
		}
		if(isValidMenu("transferLedger"))
		{
			addCaptionedItem("TRANSFER LEDGER", masterSetup);
		}
		if(isValidMenu("createNarration"))
		{
			addCaptionedItem("CREATE NARRATION", masterSetup);
		}
		if(isValidMenu("chequeBookEntry"))
		{
			addCaptionedItem("CHEQUE BOOK ENTRY", masterSetup);
		}
		if(isValidMenu("chequeBookCancel"))
		{
			addCaptionedItem("CHEQUE BOOK CANCELLATION", masterSetup);
		}
		if(isValidMenu("yearlyBudgetDeclare"))
		{
			addCaptionedItem("YEARLY BUDGET DECLARE", masterSetup);
		}
		if(isValidMenu("fiscalYearClosing"))
		{
			addCaptionedItem("FISCAL YEAR CLOSING", masterSetup);
		}
	}

	private void trasacTion(Object accountsTransaction)
	{
		if(isValidMenu("cashPaymentVoucherMulti"))
		{
			addCaptionedItem("CASH PAYMENT VOUCHER", accountsTransaction);
		}
		if(isValidMenu("bankPaymentVoucherMulti"))
		{
			addCaptionedItem("BANK PAYMENT VOUCHER", accountsTransaction);
		}
		if(isValidMenu("cashReceivedVoucher"))
		{
			addCaptionedItem("CASH RECEIVED VOUCHER", accountsTransaction);
		}
		if(isValidMenu("bankReceivedVoucher"))
		{
			addCaptionedItem("BANK RECEIVED VOUCHER", accountsTransaction);
		}
		if (isValidMenu("RcptAgnstInvoice"))
		{
			addCaptionedItem("RECEIVED AGAINST INVOICE", accountsTransaction);
		}
		if (isValidMenu("journalAgnstInvoice"))
		{
			addCaptionedItem("JOURNAL AGAINST INVOICE", accountsTransaction);
		}
		if(isValidMenu("journalVoucher"))
		{
			addCaptionedItem("JOURNAL VOUCHER", accountsTransaction);
		}
		if(isValidMenu("storejournal"))
		{
			addCaptionedItem("STORE JOURNAL", accountsTransaction);
		}
		if(isValidMenu("contraEntry"))
		{
			addCaptionedItem("CONTRA ENTRY", accountsTransaction);
		}
		if(isValidMenu("voucherAudit"))
		{
			addCaptionedItem("VOUCHER AUDIT", accountsTransaction);
		}
		if(isValidMenu("voucherApprove"))
		{
			addCaptionedItem("VOUCHER APPROVE", accountsTransaction);
		}
		/*if(isValidMenu("bankReconcillationEntry"))
		{
			addCaptionedItem("BANK RECONCILLATION ENTRY", accountsTransaction);
		}*/
		if(isValidMenu("moneyReceipt"))
		{
			addCaptionedItem("MONEY RECEIPT", accountsTransaction);
		}
		if(isValidMenu("debitNote"))
		{
			addCaptionedItem("DEBIT NOTE", accountsTransaction);
		}
		if(isValidMenu("debitNoteApprove"))
		{
			addCaptionedItem("DEBIT NOTE APPROVE", accountsTransaction);
		}
	}

	private void addChild(Object accountsReport)
	{
		if(isValidMenu("voucherMenu"))
		{
			voucher = addCaptionedItem("VOUCHERS", accountsReport);
			addVoucherChild(voucher);
		}
		if(isValidMenu("dayBooks"))
		{
			dayBooks = addCaptionedItem("DAY BOOKS", accountsReport);
			addDaybookChild(dayBooks);
		}
		if(isValidMenu("ledger"))
		{
			ledger = addCaptionedItem("LEDGER", accountsReport);
			addLedgerChild(ledger);
		}
		if(isValidMenu("trialBalance"))
		{
			trialBalance = addCaptionedItem("TRIAL BALANCE", accountsReport);
			addTrialBalance(trialBalance);
		}
		if(isValidMenu("profitLossAccount"))
		{
			profitLossAccount = addCaptionedItem("PROFIT & LOSS ACCOUNT", accountsReport);
			addProfitLossAccount(profitLossAccount);
		}
		if(isValidMenu("balanceSheet"))
		{
			balanceSheet = addCaptionedItem("BALANCE SHEET", accountsReport);
			addBalanceSheet(profitLossAccount);
		}
		if(isValidMenu("mis"))
		{
			mis = addCaptionedItem("MIS", accountsReport);
			addMisChild(mis);
		}
		
		if(isValidMenu("transactionstatus"))
		{
			addCaptionedItem("TRANSACTION STATUS", accountsReport);
		}
	}

	private void addVoucherChild(Object voucher)
	{
		if(isValidMenu("voucher"))
		{
			addCaptionedItem("VOUCHER", voucher);
		}
		if(isValidMenu("voucherEditDelete"))
		{
			addCaptionedItem("VOUCHER EDIT DELETE REPORT", voucher);
		}
	}

	private void addDaybookChild(Object dayBooks)
	{
		if(isValidMenu("cashBook"))
		{
			addCaptionedItem("CASH BOOK", dayBooks);
		}
		if(isValidMenu("bankBook"))
		{
			addCaptionedItem("BANK BOOK", dayBooks);
		}
		if(isValidMenu("journalBook"))
		{
			addCaptionedItem("JOURNAL BOOK", dayBooks);
		}
		/*if(isValidMenu("salesBook"))
		{
			addCaptionedItem("SALES BOOK", dayBooks);
		}*/
	}

	private void addLedgerChild(Object ledger)
	{
		if(isValidMenu("generalLedger"))
		{
			addCaptionedItem("GENERAL LEDGER", ledger);
		}
		/*if(isValidMenu("ledgerGroup"))
		{
			addCaptionedItem("LEDGER (GROUP)", ledger);
		}*/
		if(isValidMenu("debtorsLedger"))
		{
			addCaptionedItem("DEBTORS LEDGER", ledger);
		}
		if(isValidMenu("creditorsLedger"))
		{
			addCaptionedItem("CREDITORS LEDGER", ledger);
		}
	}

	private void addTrialBalance(Object trialBalance)
	{
		if(isValidMenu("openingTrialBalance"))
		{
			addCaptionedItem("OPENING TRIAL BALANCE", trialBalance);
		}
		if(isValidMenu("openingTrialBalanceGroupWise"))
		{
			addCaptionedItem("OPENING TRIAL BALANCE (GROUP WISE)", trialBalance);
		}
		if(isValidMenu("adjustedTrialBalance"))
		{
			addCaptionedItem("ADJUSTED TRIAL BALANCE", trialBalance);
		}
		if(isValidMenu("adjustedTrialBalanceGroupWise"))
		{
			addCaptionedItem("ADJUSTED TRIAL BALANCE (GROUP WISE)", trialBalance);
		}
		if(isValidMenu("adjustedTrialBalanceBetweenDate"))
		{
			addCaptionedItem("ADJUSTED TRIAL BALANCE BETWEEN DATE", trialBalance);
		}
	}

	private void addProfitLossAccount(Object profitLossAccount)
	{
		if(isValidMenu("costCenterWiseProfitLoss"))
		{
			addCaptionedItem("COST CENTER WISE PROFIT & LOSS", profitLossAccount);
		}
		if(isValidMenu("dateWiseProfitLossStatementSummary"))
		{
			addCaptionedItem("DATE WISE STATEMENT (SUMMARY)", profitLossAccount);
		}
		if(isValidMenu("asOnDateWiseProfitLossStatementSummary"))
		{
			addCaptionedItem("AS ON DATE STATEMENT (SUMMARY)", profitLossAccount);
		}
		if(isValidMenu("profitLossStatementDetails"))
		{
			addCaptionedItem("PROFIT & LOSS STATEMENT (DETAILS)", profitLossAccount);
		}
		if(isValidMenu("profitLossStatementComparative"))
		{
			addCaptionedItem("PROFIT & LOSS STATEMENT (COMPARATIVE)", profitLossAccount);
		}
	}

	private void addBalanceSheet(Object voucher)
	{
		if(isValidMenu("balanceSheetStatement"))
		{
			addCaptionedItem("BALANCE SHEET STATEMENT", balanceSheet);
		}
		if(isValidMenu("balanceSheetDetails"))
		{
			addCaptionedItem("BALANCE SHEET DETAILS", balanceSheet);
		}
		if(isValidMenu("balanceSheetComperative"))
		{
			addCaptionedItem("BALANCE SHEET COMPARATIVE", balanceSheet);
		}
		
		if(isValidMenu("balanceSheetComperativenotes"))
		{
			addCaptionedItem("BALANCE SHEET NOTES COMPARATIVE", balanceSheet); 
		}
	}

	private void addMisChild(Object mis)
	{
		if(isValidMenu("chartOfAccounts"))
		{
			addCaptionedItem("CHART OF ACCOUNTS", mis);
		}
		if(isValidMenu("ledgerGroupSummaryAsOnDate"))
		{
			addCaptionedItem("LEDGER GROUP SUMMARY AS ON DATE", mis);
		}
		if(isValidMenu("ledgerGroupSummaryDateRange"))
		{
			addCaptionedItem("LEDGER GROUP SUMMARY DATE RANGE", mis);
		}
		if(isValidMenu("monthWiseGroupSummary"))
		{
			addCaptionedItem("MONTH WISE GROUP SUMMARY", mis);
		}
		if(isValidMenu("bankPosition"))
		{
			addCaptionedItem("BANK POSITION", mis);
		}
		if(isValidMenu("dailyChequeRegister"))
		{
			addCaptionedItem("DAILY CHEQUE REGISTER", mis);
		}
		if(isValidMenu("outwardChequeRegisterOnDateRange"))
		{
			addCaptionedItem("OUTWARD CHEQUE REGISTER (ON DATE RANGE)", mis);
		}
		if(isValidMenu("inwardChequeRegisterOnDateRange"))
		{
			addCaptionedItem("INWARD CHEQUE REGISTER (ON DATE RANGE)", mis);
		}
		if(isValidMenu("receivedChequeRegister"))
		{
			addCaptionedItem("RECEIVED CHEQUE REGISTER", mis);
		}
		if(isValidMenu("unusedChequeList"))
		{
			addCaptionedItem("UNUSED CHEQUE LIST", mis);
		}
		if(isValidMenu("cancelChequeList"))
		{
			addCaptionedItem("CANCEL CHEQUE LIST", mis);
		}
		if(isValidMenu("statementOfCashFlow"))
		{
			addCaptionedItem("STATEMENT OF CASH FLOW", mis);
		}
		if(isValidMenu("dailyTransactionSummary"))
		{
			addCaptionedItem("DAILY TRANSACTION SUMMARY", mis);
		}
		if(isValidMenu("dailyCashStatement"))
		{
			addCaptionedItem("DAILY CASH STATEMENT", mis);
		}
		if(isValidMenu("monthWiseExpensesSummary"))
		{
			addCaptionedItem("MONTH WISE EXPENSES SUMMARY", mis);
		}
		if(isValidMenu("interestCalculation"))
		{
			addCaptionedItem("INTEREST CALCULATION", mis);
		}
		if(isValidMenu("receivableAging"))
		{
			addCaptionedItem("RECEIVABLE AGING", mis);
		}
		if(isValidMenu("voucherEditDelete"))
		{
			addCaptionedItem("VOUCHER EDIT DELETE REPORT", mis);
		}
		if(isValidMenu("transactionTracking"))
		{
			addCaptionedItem("TRANSACTION TRACKING", mis);
		}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}

		return id;
	}

	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				System.out.println(event.getItemId()+" "+event.getItem());
				// ACCOUNTS MASTER SETUP
				if(event.getItem().toString().equalsIgnoreCase("ACCOUNTS MASTER SETUP"))
				{
					showWindow(new accountsMaster(sessionBean),event.getItem(),"acccountsMastersetup");
				}
				// ACCOUNTS TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("ACCOUNTS TRANSACTION"))
				{
					showWindow(new accountsTransaction(sessionBean),event.getItem(),"acccountsTransaction");
				}
				// ACCOUNTS REPORTS
				if(event.getItem().toString().equalsIgnoreCase("ACCOUNTS REPORT"))
				{
					showWindow(new accountsReports(sessionBean),event.getItem(),"accountsReports");
				}

				// VOUCHERS
				if(event.getItem().toString().equalsIgnoreCase("VOUCHERS"))
				{
					showWindow(new accessVoucher(sessionBean),event.getItem(),"voucherMenu");
				}
				// DAY BOOKS
				if(event.getItem().toString().equalsIgnoreCase("DAY BOOKS"))
				{
					showWindow(new accessDayBooks(sessionBean),event.getItem(),"dayBooks");
				}
				// LEDGER
				if(event.getItem().toString().equalsIgnoreCase("LEDGER"))
				{
					showWindow(new accessLedger(sessionBean),event.getItem(),"ledger");
				}
				// TRIAL BALANCE
				if(event.getItem().toString().equalsIgnoreCase("TRIAL BALANCE"))
				{
					showWindow(new accessTrialBalance(sessionBean),event.getItem(),"trialBalance");
				}
				// PROFIT & LOSS ACCOUNT
				if(event.getItem().toString().equalsIgnoreCase("PROFIT & LOSS ACCOUNT"))
				{
					showWindow(new accessProfitLoss(sessionBean),event.getItem(),"profitLossAccount");
				}
				// BALANCE SHEET
				if(event.getItem().toString().equalsIgnoreCase("BALANCE SHEET"))
				{
					showWindow(new accessBalanceSheet(sessionBean),event.getItem(),"balanceSheet");
				}
				// MIS
				if(event.getItem().toString().equalsIgnoreCase("MIS"))
				{
					showWindow(new accessMis(sessionBean),event.getItem(),"mis");
				}

				if(event.getItem().toString().equalsIgnoreCase("COST CENTER INFORMATION"))
				{
					showWindow(new CostInformation(sessionBean),event.getItem(),"costCenterInformation");
				}

				// create group information
				if(event.getItem().toString().equalsIgnoreCase("CREATE GROUP"))
				{
					showWindow(new GroupCreate(sessionBean),event.getItem(),"createGroup");
				}

				// create sub group information
				if(event.getItem().toString().equalsIgnoreCase("CREATE SUB GROUP"))
				{
					showWindow(new SubGroupCreate(sessionBean),event.getItem(),"createSubGroup");
				}

				// create ledger information
				if(event.getItem().toString().equalsIgnoreCase("CREATE LEDGER"))
				{
					showWindow(new LedgerCreate(sessionBean),event.getItem(),"createLedger");
				}

				// transfer ledger information
				if(event.getItem().toString().equalsIgnoreCase("TRANSFER LEDGER"))
				{
					showWindow(new TransferLedger(sessionBean),event.getItem(),"transferLedger");
				}

				// create narration information
				if(event.getItem().toString().equalsIgnoreCase("CREATE NARRATION"))
				{
					showWindow(new Narration(sessionBean),event.getItem(),"createNarration");
				}
				
				// cheque Book Entry
				if(event.getItem().toString().equalsIgnoreCase("CHEQUE BOOK ENTRY"))
				{
					showWindow(new ChequeBookEntry(sessionBean),event.getItem(),"chequeBookEntry");
				}

				// cheque Book Cancel
				if(event.getItem().toString().equalsIgnoreCase("CHEQUE BOOK CANCELLATION"))
				{
					showWindow(new ChequeBookCancel(sessionBean),event.getItem(),"chequeBookCancel");
				}

				// yearly Budget Declare information
				if(event.getItem().toString().equalsIgnoreCase("YEARLY BUDGET DECLARE"))
				{
					showWindow(new YearlyBudget(sessionBean),event.getItem(),"yearlyBudgetDeclare");
				}

				// fiscal year closing
				if(event.getItem().toString().equalsIgnoreCase("FISCAL YEAR CLOSING"))
				{
					showWindow(new fiscalYearClosing(sessionBean),event.getItem(),"fiscalYearClosing");
				}

				if(event.getItem().toString().equalsIgnoreCase("CASH PAYMENT VOUCHER"))
				{
					showWindow(new CashVoucherPayMulti(sessionBean),event.getItem(),"cashPaymentVoucherMulti");
				}

				// bank payment voucher MULTI
				if(event.getItem().toString().equalsIgnoreCase("BANK PAYMENT VOUCHER"))
				{
					showWindow(new BankVoucherPayMulti(sessionBean),event.getItem(),"bankPaymentVoucherMulti");
				}

				// cash Received Voucher
				if(event.getItem().toString().equalsIgnoreCase("CASH RECEIVED VOUCHER"))
				{
					showWindow(new CashReceiptVoucher(sessionBean),event.getItem(),"cashReceivedVoucher");
				}

				// bank Received Voucher
				if(event.getItem().toString().equalsIgnoreCase("BANK RECEIVED VOUCHER"))
				{
					showWindow(new BankVoucherReceive(sessionBean),event.getItem(),"bankReceivedVoucher");
				}

				// bank Receipt Against Invoice
				if(event.getItem().toString().equalsIgnoreCase("BANK RECEIPT AGAINST INVOICE"))
				{
					showWindow(new RcptAgnstInvoice(sessionBean,"bankReceiptAgnstInvoice"),event.getItem(),"bankReceiptAgainstInvoice");
				}

				// cash Receipt Against Invoice
				if(event.getItem().toString().equalsIgnoreCase("CASH RECEIPT AGAINST INVOICE"))
				{
					showWindow(new RcptAgnstInvoice(sessionBean,"cashReceiptAgnstInvoice"),event.getItem(),"bankReceiptAgainstInvoice");
				}

				// bank Receipt Against Invoice
				if(event.getItem().toString().equalsIgnoreCase("RECEIVED AGAINST INVOICE"))
				{
					showWindow(new RcecivedAgnstInvoice(sessionBean,"RcptAgnstInvoice"),event.getItem(),"RcptAgnstInvoice");
				}

				// journal Against Invoice
				if(event.getItem().toString().equalsIgnoreCase("JOURNAL AGAINST INVOICE"))
				{
					showWindow(new journalAgnstInvoice(sessionBean),event.getItem(),"journalAgainstInvoice");
				}

				// journal voucher
				if(event.getItem().toString().equalsIgnoreCase("JOURNAL VOUCHER"))
				{
					showWindow(new JournalVoucher(sessionBean),event.getItem(),"journalVoucher");
				}

				//Store Journal
				if(event.getItem().toString().equalsIgnoreCase("STORE JOURNAL"))
				{
					showWindow(new PurchaeVoucher(sessionBean),event.getItem(),"storejournal");
				}

				// contra Entry
				if(event.getItem().toString().equalsIgnoreCase("CONTRA ENTRY"))
				{
					showWindow(new ContraVoucher(sessionBean),event.getItem(),"contraEntry");
				}

				// BANK RECONCILLATION ENTRY
				if(event.getItem().toString().equalsIgnoreCase("BANK RECONCILLATION ENTRY"))
				{
					showWindow(new BankReconciliitionEntry(sessionBean),event.getItem(),"bankReconcillationEntry");
				}

				// moneyReceipt
				if(event.getItem().toString().equalsIgnoreCase("MONEY RECEIPT"))
				{
					showWindow(new MoneyReceipt(sessionBean),event.getItem(),"moneyReceipt");
				}

				if(event.getItem().toString().equalsIgnoreCase("DEBIT NOTE"))
				{
					showWindow(new DebitNote(sessionBean),event.getItem(),"debitNote");
				}
				if(event.getItem().toString().equalsIgnoreCase("DEBIT NOTE APPROVE"))
				{
					showWindow(new DebitNoteApprove(sessionBean),event.getItem(),"debitNoteApprove");
				}

				// voucher Audit
				if(event.getItem().toString().equalsIgnoreCase("VOUCHER AUDIT"))
				{
					showWindow(new AuditApprove(sessionBean, "Audit"),event.getItem(),"voucherAudit");
				}

				// voucher Approve
				if(event.getItem().toString().equalsIgnoreCase("VOUCHER APPROVE"))
				{
					showWindow(new AuditApprove(sessionBean, "Approved"),event.getItem(),"voucherApprove");
				}

				/////Accounts Report
				if(event.getItem().toString().equalsIgnoreCase("UNUSED CHEQUE LIST"))
				{
					showWindow(new UnusedChequeList(sessionBean),event.getItem(),"unusedChequeList");
				}

				// CANCEL CHEQUE LIST
				if(event.getItem().toString().equalsIgnoreCase("CANCEL CHEQUE LIST"))
				{
					showWindow(new RptCancelChequeList(sessionBean),event.getItem(),"cancelChequeList");
				}

				// voucher
				if(event.getItem().toString().equalsIgnoreCase("VOUCHER"))
				{
					showWindow(new Voucher(sessionBean),event.getItem(),"voucher");
				}

				// cashBook
				if(event.getItem().toString().equalsIgnoreCase("CASH BOOK"))
				{
					showWindow(new DayBook(sessionBean,"c"),event.getItem(),"cashBook");
				}

				// bankBook
				if(event.getItem().toString().equalsIgnoreCase("BANK BOOK"))
				{
					showWindow(new DayBook(sessionBean,"b"),event.getItem(),"bankBook");
				}

				// journalBook
				if(event.getItem().toString().equalsIgnoreCase("JOURNAL BOOK"))
				{
					showWindow(new DayBook(sessionBean,"j"),event.getItem(),"journalBook");
				}

				// salesBook
				/*if(event.getItem().toString().equalsIgnoreCase("SALES BOOK"))
				{
					showWindow(new DayBook(sessionBean,"s"),event.getItem(),"salesBook");
				}*/

				// generalLedger
				if(event.getItem().toString().equalsIgnoreCase("GENERAL LEDGER"))
				{
					showWindow(new GeneralLedger(sessionBean,"g"),event.getItem(),"generalLedger");
				}

				// ledgerGroup
				/*if(event.getItem().toString().equalsIgnoreCase("LEDGER (GROUP)"))
				{
					showWindow(new LedgerGroup(sessionBean),event.getItem(),"ledgerGroup");
				}
*/
				// debtorsLedger
				if(event.getItem().toString().equalsIgnoreCase("DEBTORS LEDGER"))
				{
					showWindow(new GeneralLedger(sessionBean,"d"),event.getItem(),"debtorsLedger");
				}

				// creditorsLedger
				if(event.getItem().toString().equalsIgnoreCase("CREDITORS LEDGER"))
				{
					showWindow(new GeneralLedger(sessionBean,"c"),event.getItem(),"creditorsLedger");
				}

				// openingTrialBalance
				if(event.getItem().toString().equalsIgnoreCase("OPENING TRIAL BALANCE"))
				{
					showWindow(new OpeningTrialRpt(sessionBean,"a"),event.getItem(),"openingTrialBalance");
				}

				// openingTrialBalanceGroupWise
				if(event.getItem().toString().equalsIgnoreCase("OPENING TRIAL BALANCE (GROUP WISE)"))
				{
					showWindow(new OpeningTrialRpt(sessionBean,"b"),event.getItem(),"openingTrialBalanceGroupWise");
				}

				// cashTrialBalance
				if(event.getItem().toString().equalsIgnoreCase("CASH TRIAL BALANCE"))
				{
					showWindow(new CashTrialRpt(sessionBean),event.getItem(),"cashTrialBalance");
				}

				// adjustedTrialBalance
				if(event.getItem().toString().equalsIgnoreCase("ADJUSTED TRIAL BALANCE"))
				{
					showWindow(new AdjustedTrialRpt(sessionBean,"a"),event.getItem(),"adjustedTrialBalance");
				}

				// adjustedTrialBalanceGroupWise
				if(event.getItem().toString().equalsIgnoreCase("ADJUSTED TRIAL BALANCE (GROUP WISE)"))
				{
					showWindow(new AdjustedTrialRpt(sessionBean,"g"),event.getItem(),"adjustedTrialBalanceGroupWise");
				}
				
				// adjustedTrialBalance
				if(event.getItem().toString().equalsIgnoreCase("ADJUSTED TRIAL BALANCE BETWEEN DATE"))
				{
					showWindow(new AdjustedTrialRptBetDate(sessionBean,"a"),event.getItem(),"adjustedTrialBalanceBetweenDate");
				}

				// costCenterWiseProfitLoss
				if(event.getItem().toString().equalsIgnoreCase("COST CENTER WISE PROFIT & LOSS"))
				{
					showWindow(new DayBook(sessionBean,"plc"),event.getItem(),"costCenterWiseProfitLoss");
				}

				// dateWiseProfitLossStatementSummary
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE STATEMENT (SUMMARY)"))
				{
					showWindow(new ProfitLossRpt(sessionBean,"s"),event.getItem(),"dateWiseProfitLossStatementSummary");
				}

				// asOnDateWiseProfitLossStatementSummary
				if(event.getItem().toString().equalsIgnoreCase("AS ON DATE STATEMENT (SUMMARY)"))
				{
					showWindow(new ProfitLossRpt(sessionBean,"a"),event.getItem(),"asOnDateWiseProfitLossStatementSummary");
				}

				// profitLossStatementDetails
				if(event.getItem().toString().equalsIgnoreCase("PROFIT & LOSS STATEMENT (DETAILS)"))
				{
					showWindow(new ProfitLossRpt(sessionBean,"d"),event.getItem(),"profitLossStatementDetails");
				}
				// profitLossStatementComparative
				if(event.getItem().toString().equalsIgnoreCase("PROFIT & LOSS STATEMENT (COMPARATIVE)"))
				{
					showWindow(new ProfitLossRpt(sessionBean,"comparativePlStatement"),event.getItem(),"profitLossStatementComparative");
				}

				// balanceSheetStatement
				if(event.getItem().toString().equalsIgnoreCase("BALANCE SHEET STATEMENT"))
				{
					showWindow(new BalanceSheetRpt(sessionBean,"s"),event.getItem(),"balanceSheetStatement");
				}

				// balanceSheetDetails
				if(event.getItem().toString().equalsIgnoreCase("BALANCE SHEET DETAILS"))
				{
					showWindow(new BalanceSheetRpt(sessionBean,"d"),event.getItem(),"balanceSheetDetails");
				}

				// balanceSheetComparative
				if(event.getItem().toString().equalsIgnoreCase("BALANCE SHEET COMPARATIVE"))
				{
					showWindow(new BalanceSheetRpt(sessionBean,"comparativeBalanceSheetStatement"),event.getItem(),"balanceSheetComperative");
				}
				
				// balanceSheetComparative
				if(event.getItem().toString().equalsIgnoreCase("BALANCE SHEET NOTES COMPARATIVE"))
				{
					showWindow(new BalanceSheetRpt(sessionBean,"balanceSheetComperativenotes"),event.getItem(),"balanceSheetComperativenotes");
				}


				// chartOfAccounts
				if(event.getItem().toString().equalsIgnoreCase("CHART OF ACCOUNTS"))
				{
					showWindow(new ChartOfAccounts(sessionBean,""),event.getItem(),"chartOfAccounts");
				}

				/*// assetSchedule
			if(event.getItem().toString().equalsIgnoreCase("FIXED ASSET SCHEDULE"))
			{
				showWindow(new AssetScheduleRpt(sessionBean),event.getItem(),"fixedAssetSchedule");
			}

			// leasedAssetSchedule
			if(event.getItem().toString().equalsIgnoreCase("LEASED ASSET SCHEDULE"))
			{
				showWindow(new LeasedAssetScheduleRpt(sessionBean),event.getItem(),"leasedAssetSchedule");
			}

			// assetLedger
			if(event.getItem().toString().equalsIgnoreCase("FIXED ASSET LEDGER"))
			{
				showWindow(new AssetLedgerRpt(sessionBean),event.getItem(),"fixedAssetLedger");
			}

			// assetRegister
			if(event.getItem().toString().equalsIgnoreCase("FIXED ASSET REGISTER"))
			{
				showWindow(new AssetRegisterRpt(sessionBean),event.getItem(),"fixedAssetRegister");
			}*/

				// ledgerGroupSummaryAsOnDate
				if(event.getItem().toString().equalsIgnoreCase("LEDGER GROUP SUMMARY AS ON DATE"))
				{
					//showWindow(new GroupLedgerSummary(sessionBean,"asOnDate"),event.getItem(),"ledgerGroupSummaryAsOnDate");
					showWindow(new TestGroupLedgerSummary(sessionBean,"asOnDate"),event.getItem(),"ledgerGroupSummaryAsOnDate");
				}

				// ledgerGroupSummaryDateRange
				if(event.getItem().toString().equalsIgnoreCase("LEDGER GROUP SUMMARY DATE RANGE"))
				{
					showWindow(new TestGroupLedgerSummary(sessionBean,"dateRange"),event.getItem(),"ledgerGroupSummaryAsOnDate");
				}

				// monthWiseGroupSummary
				if(event.getItem().toString().equalsIgnoreCase("MONTH WISE GROUP SUMMARY"))
				{
					showWindow(new GroupLedgerSummary(sessionBean,""),event.getItem(),"monthWiseGroupSummary");
				}

				// bankPosition
				if(event.getItem().toString().equalsIgnoreCase("BANK POSITION"))
				{
					showWindow(new BankPositionRpt(sessionBean),event.getItem(),"bankPosition");
				}

				// dailyChequeRegister
				if(event.getItem().toString().equalsIgnoreCase("DAILY CHEQUE REGISTER"))
				{
					showWindow(new DayBook(sessionBean,"dcr"),event.getItem(),"dailyChequeRegister");
				}

				// outwardChequeRegisterOnDateRange
				if(event.getItem().toString().equalsIgnoreCase("OUTWARD CHEQUE REGISTER (ON DATE RANGE)"))
				{
					showWindow(new DayBook(sessionBean,"cr"),event.getItem(),"outwardChequeRegisterOnDateRange");
				}

				// inwardChequeRegisterOnDateRange
				if(event.getItem().toString().equalsIgnoreCase("INWARD CHEQUE REGISTER (ON DATE RANGE)"))
				{
					showWindow(new DayBook(sessionBean,"crIn"),event.getItem(),"inwardChequeRegisterOnDateRange");
				}

				// receivedChequeRegister
				if(event.getItem().toString().equalsIgnoreCase("RECEIVED CHEQUE REGISTER"))
				{
					showWindow(new DayBook(sessionBean,"crp"),event.getItem(),"receivedChequeRegister");
				}

				// statementOfCashFlow
				if(event.getItem().toString().equalsIgnoreCase("STATEMENT OF CASH FLOW"))
				{
					showWindow(new CashFlowYearly(sessionBean),event.getItem(),"statementOfCashFlow");
				}

				// dailyTransactionSummary
				if(event.getItem().toString().equalsIgnoreCase("DAILY TRANSACTION SUMMARY"))
				{
					showWindow(new DailyTransactionRpt(sessionBean),event.getItem(),"dailyTransactionSummary");
				}

				// dailyCashStatement
				if(event.getItem().toString().equalsIgnoreCase("DAILY CASH STATEMENT"))
				{
					showWindow(new DailyCashStatementRpt(sessionBean),event.getItem(),"dailyCashStatement");
				}

				// monthWiseExpensesSummary
				if(event.getItem().toString().equalsIgnoreCase("MONTH WISE EXPENSES SUMMARY"))
				{
					showWindow(new MonthlyExpensetRpt(sessionBean),event.getItem(),"monthWiseExpensesSummary");
				}

				// interestCalculation
				if(event.getItem().toString().equalsIgnoreCase("INTEREST CALCULATION"))
				{
					showWindow(new InterestCalculation(sessionBean),event.getItem(),"interestCalculation");
				}

				// receivableAging
				if(event.getItem().toString().equalsIgnoreCase("RECEIVABLE AGING"))
				{
					showWindow(new ReceivableAging(sessionBean),event.getItem(),"receivableAging");
				}

				if(event.getItem().toString().equalsIgnoreCase("VOUCHER EDIT DELETE REPORT"))
				{
					showWindow(new RptVoucherEditDelete(sessionBean),event.getItem(),"voucherEditDelete");
				}

				//TransactionTraking
				if(event.getItem().toString().equalsIgnoreCase("TRANSACTION TRACKING"))
				{
					showWindow(new RptTransactionTracking(sessionBean),event.getItem(),"transactionTracking");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("TRANSACTION STATUS"))
				{
					showWindow(new RptDataEntryStatus(sessionBean),event.getItem(),"transactionstatus");
				}
			}
		});
	}

	// check is valid menu for add menu bar
	private boolean isValidMenu(String id)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery("SELECT menuId FROM tbAuthentication WHERE"
					+ " userId = '"+sessionBean.getUserId()+"' AND menuId = '"+id+"'").list().iterator();
			if(!iter.hasNext())
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
		return false;
	}

	@SuppressWarnings("serial")
	private void showWindow(Window win, Object selectedItem,String mid)
	{
		try
		{
			final String id = selectedItem+"";
			if(!sessionBean.getAuthenticWindow())
			{
				if(isOpen(id))
				{
					win.center();
					win.setStyleName("cwindow");
					component.getWindow().addWindow(win);
					win.setCloseShortcut(KeyCode.ESCAPE);
					winMap.put(id,id);
					win.addListener(new Window.CloseListener() 
					{
						public void windowClose(CloseEvent e) 
						{
							winMap.remove(id);                	
						}
					});
				}
			}
			else
			{
				sessionBean.setPermitForm(mid,selectedItem.toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}

	private  boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}