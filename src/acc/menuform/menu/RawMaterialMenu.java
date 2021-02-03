package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.DoSalesModule.CancelTheDeliveryChallan;
import acc.appform.DoSalesModule.DeliveryChallan;
import acc.appform.LcModule.LcCloseing;
import acc.appform.LcModule.LcClosingtemporary;
import acc.appform.transaction.AuditApprove;
import acc.appform.transaction.BankReconciliitionEntry;
import acc.appform.transaction.BankVoucherPayMulti;
import acc.appform.transaction.BankVoucherReceive;
import acc.appform.transaction.CashReceiptVoucherOld;
import acc.appform.transaction.accountsMaster;
import acc.appform.transaction.accountsReports;
import acc.appform.transaction.accountsTransaction;
import acc.appform.transaction.ContraVoucher;
import acc.appform.transaction.JournalVoucher;
import acc.appform.transaction.RcptAgnstInvoice;
import acc.appform.transaction.journalAgnstInvoice;
import acc.reportmodule.asset.AssetLedgerRpt;
import acc.reportmodule.asset.AssetRegisterRpt;
import acc.reportmodule.asset.AssetScheduleRpt;
import acc.reportmodule.asset.LeasedAssetScheduleRpt;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.productionTransaction.IssueToAssemble;
import com.example.rawMaterialReport.DateWisePurchaseRpt;
import com.example.rawMaterialReport.DepartmentWiseIssue;
import com.example.rawMaterialReport.DeptWiseIssueSummary;
import com.example.rawMaterialReport.DpetWiseRqIssue;
import com.example.rawMaterialReport.GoodsReceiveNote;
import com.example.rawMaterialReport.IssueRegister;
import com.example.rawMaterialReport.IssueSummary;
import com.example.rawMaterialReport.ItemWisePurcaseRegisterRpt;
import com.example.rawMaterialReport.RawMaterialsItemInformationAsOnDate;
import com.example.rawMaterialReport.RawPurchaseOrderRpt;
import com.example.rawMaterialReport.RawPurchaseorderRegister;
import com.example.rawMaterialReport.RawRequisitionForm;
import com.example.rawMaterialReport.RawRequisitionRegister;
import com.example.rawMaterialReport.RawRequisitionRegisterDepartmentWise;
import com.example.rawMaterialReport.ReceiveSummary;
import com.example.rawMaterialReport.RptAvgPerDayIssue;
import com.example.rawMaterialReport.RptCateWiseStockAsOnDate;
import com.example.rawMaterialReport.RptCateWiseStockDateBetween;
import com.example.rawMaterialReport.RptDeptWiseIssueNew;
import com.example.rawMaterialReport.RptFirstMoving;
import com.example.rawMaterialReport.RptIssueRegister;
import com.example.rawMaterialReport.RptIssueReturnRegister;
import com.example.rawMaterialReport.RptItemInformation0;
import com.example.rawMaterialReport.RptItemInformationAsOnDate;
import com.example.rawMaterialReport.RptItemWiseKardex;
import com.example.rawMaterialReport.RptLoanIssueReturn;
import com.example.rawMaterialReport.RptLoanIssueRtnSummary;
import com.example.rawMaterialReport.RptLoanReceiveReturn;
import com.example.rawMaterialReport.RptLoanReceiveRtnSummary;
import com.example.rawMaterialReport.RptLowerLevelStock;
import com.example.rawMaterialReport.RptMaximumLevelStock;
import com.example.rawMaterialReport.RptMinimumLevelStock;
import com.example.rawMaterialReport.RptNonmoving;
import com.example.rawMaterialReport.RptOpeningStock;
import com.example.rawMaterialReport.RptPendingPurchaseOrder;
import com.example.rawMaterialReport.RptPoBalanceStatement;
import com.example.rawMaterialReport.RptProductWiseStockRegister;
import com.example.rawMaterialReport.RptPurchaseOrderRegister;
import com.example.rawMaterialReport.RptRequisitionProduction;
import com.example.rawMaterialReport.RptStoreWiseStock;
import com.example.rawMaterialReport.Rptslowmoving;
import com.example.rawMaterialReport.SpareItemInfo;
import com.example.rawMaterialReport.StockReport;
import com.example.rawMaterialReport.SupplierWisePurchaseRpt;
import com.example.rawMaterialSetup.ProductionStepProcess;
import com.example.rawMaterialSetup.ProductionType;
import com.example.rawMaterialSetup.RawItemCategory;
import com.example.rawMaterialSetup.RawItemInfo;
import com.example.rawMaterialSetup.RawItemSubCategory;
import com.example.rawMaterialSetup.RawItemSubSubCategory;
import com.example.rawMaterialSetup.rawMaterialReport;
import com.example.rawMaterialSetup.rawMaterialSetup;
import com.example.rawMaterialSetup.rawMeterialTransaction;
import com.example.rawMaterialTransaction.IssueToAssembleImported;
import com.example.rawMaterialTransaction.LoanIssueRcvRtn;
import com.example.rawMaterialTransaction.LoanRcvIssueRtn;
import com.example.rawMaterialTransaction.OpeningRateEdit;
import com.example.rawMaterialTransaction.PurchaeVoucher;
import com.example.rawMaterialTransaction.PurchaseOrderActiveInactiveInventory;
import com.example.rawMaterialTransaction.RawMaterialsIssue;
import com.example.rawMaterialTransaction.RawMaterialsOpeningStock;
import com.example.rawMaterialTransaction.RawMaterialsPurchaseReceipt;
import com.example.rawMaterialTransaction.StockSearch;
//import com.example.rawMaterialTransaction.RawMaterialsPurchaseReceipt;
import com.example.rawMaterialTransaction.RawMaterialsPurchaseReceiptSpare;
import com.example.rawMaterialTransaction.RawPurchaseOrder;
import com.example.rawMaterialTransaction.RawmaterialIssueReturn;
import com.example.rawMaterialTransaction.RequistionEntry;
import com.example.rawMaterialTransaction.RequsitionForHeadOffice;
//import com.example.rawMeterialTransaction.RawMaterialPRecipt;


import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class RawMaterialMenu
{
	private HashMap winMap = new HashMap();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object fixedAsset = null;

	Object masterSetup = null;
	Object transaction = null;
	Object report = null;

	Object general = null;
	Object requisition = null;
	Object purchaseOrder = null;
	Object purchase = null;
	Object issue = null;
	Object stock = null;
	Object rawpackingreport=null;
	Object sparereport=null;

	Object ItemInfo=null;
	Object StockReport=null;
	Object Requistion=null;
	Object PurchaseOrder=null;
	Object Purchase=null;
	Object Issue=null;
	Object loanreport=null;
	Object Issuereturnreport =null;
	Object misreport =null;
	
	Object thirdPartyModule=null;
    Object thirdpartysetup=null;
	Object thirdpartyTransaction=null;
	Object thirpartyReport=null;


	public RawMaterialMenu(Object inventoryModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("rawMeterialSetup"))
		{
			masterSetup = addCaptionedItem("INVENTORY SETUP", inventoryModule);
			addMasterChild(masterSetup);
		}
		if(isValidMenu("rawMeterialTransaction"))
		{
			transaction = addCaptionedItem("INVENTORY TRANSACTION", inventoryModule);
			addTransactionChild(transaction);
		}
		
		if(isValidMenu("rawMeterialReport"))
		{
			report = addCaptionedItem("INVENTORY REPORT", inventoryModule);
			addReportChild(report);
		}
		
		if(isValidMenu("thirdpartyItem"))
		{
			thirdPartyModule = addCaptionedItem("THIRD PARTY RAWMATERIAL", inventoryModule);
			addThirdParty(thirdPartyModule);
		}
		
		
	}
	

private void addThirdParty(Object addThirdParty)
	{
		if(isValidMenu("thirdpartysetup"))
		{
			thirdpartysetup = addCaptionedItem("Third Party R/M SETUP", addThirdParty);
			addThirPartySetUp(thirdpartysetup);
		}

		if(isValidMenu("productionTransaction"))
		{
			thirdpartyTransaction = addCaptionedItem("Third Party R/M Transaction", addThirdParty);
			addThirPartyTransaction(thirdpartyTransaction);
			
		}
		if(isValidMenu("productionReports"))
		{
			thirpartyReport = addCaptionedItem("Third Party R/M Reports", addThirdParty);
			addThirdPartyReport(thirpartyReport);
			
		}	
		
	}


private void addThirPartySetUp(Object thirdpartysetup)
{
	
	if(isValidMenu("thirdpartRM"))
	{
		addCaptionedItem("THIRD PARTY R/M INFORMATION", thirdpartysetup);
	}
		
}

private void addThirPartyTransaction(Object thirdpartytransaction)
{
	
	if(isValidMenu("thirdpartRMOpening"))
	{
		addCaptionedItem("THIRD PARTY R/M OPENING STOCK", thirdpartytransaction);
	}
	
	if(isValidMenu("thirdpartyReceipt"))
	{
		addCaptionedItem("THIRD PARTY R/M RECEIPT", thirdpartytransaction);
	}
	
	if(isValidMenu("thirdpartyIssue"))
	{
		addCaptionedItem("THIRD PARTY R/M ISSUE", thirdpartytransaction);
	}
		
}

private void addThirdPartyReport(Object thirpartyReport)
{
	
	if(isValidMenu("thirdpartyRMitemInfo"))
	{
		addCaptionedItem("THIRD PARTY R/M ITEM INFORMATION", thirpartyReport);
	}
	
	if(isValidMenu("thirdpartyRMReceiptRegister"))
	{
		addCaptionedItem("THIRD PARTY R/M ITEM RECEIPT REGISTER", thirpartyReport);
	}
	
	if(isValidMenu("thirdpartyRMIssueRegister"))
	{
		addCaptionedItem("THIRD PARTY R/M ITEM ISSUE REGISTER", thirpartyReport);
	}
	
	if(isValidMenu("thirdpartyRMStockLedger"))
	{
		addCaptionedItem("THIRD PARTY R/M ITEM STOCK LEDGER", thirpartyReport);
	}
	
	if(isValidMenu("thirdpartyRMStockSummary"))
	{
		addCaptionedItem("THIRD PARTY R/M ITEM STOCK SUMMARY", thirpartyReport);
	}
		
}

	private void addMasterChild(Object masterSetup)
	{
		if(isValidMenu("itemCategory"))
		{
			addCaptionedItem("CATEGORY", masterSetup);
		}

		if(isValidMenu("itemSubCategory"))
		{
			addCaptionedItem("SUB CATEGORY", masterSetup);
		}


		if(isValidMenu("itemsubSubCategory"))
		{
			//addCaptionedItem("SUB SUBCATEGORY", masterSetup);
		}

		if(isValidMenu("itemInformation"))
		{
			addCaptionedItem("INFORMATION", masterSetup);
		}
		
		/*if(isValidMenu("itemProductTionype"))
		{
			addCaptionedItem("PRODUCTION TYPE", masterSetup);
		}

		if(isValidMenu("itemProductTionProcess"))
		{
			addCaptionedItem("PRODUCTION STEP/PROCESS", masterSetup);
		}*/
	}

	private void addTransactionChild(Object transaction)
	{
		if(isValidMenu("openingStock"))
		{
			addCaptionedItem("OPENING STOCK", transaction);
		}
		if(isValidMenu("openingRateEdit"))
		{
			addCaptionedItem("OPENING RATE EDIT", transaction);
		}

		if(isValidMenu("requisition"))
		{
			addCaptionedItem("REQUISITION ENTRY", transaction);
		}

		if(isValidMenu("purchaseOrder"))
		{
			addCaptionedItem("PURCHASE ORDER", transaction);
		}

		if(isValidMenu("purchaseOrderActiveInactive"))
		{
			addCaptionedItem("PURCHASE ORDER ACTIVE / INACTIVE", transaction);
		}

		if(isValidMenu("purchaseReceipt"))
		{
			addCaptionedItem("MATERIALS RECEIPT(LOCAL & CASH)", transaction);
		}
		if(isValidMenu("purchaseReceiptSpare"))
		{
			//addCaptionedItem("MATERIALS RECEIPT(SPARE PARTS)", transaction);
		}

		if(isValidMenu("purchaseReceiptLC"))
		{
			addCaptionedItem("MATERIALS RECEIPT(L/C)", transaction);
		}

		/*if(isValidMenu("purchaseReceiptLCtemporary"))
		{
			addCaptionedItem("MATERIALS RECEIPT(L/C-2016-2017)", transaction);
		}*/

		if(isValidMenu("storejournal"))
		{
			//addCaptionedItem("STORE JOURNAL", transaction);
		}

		/*if(isValidMenu("returnToSupplier"))
		{
			addCaptionedItem("RETURN TO SUPPLIER", transaction);
		}*/

		if(isValidMenu("issue"))
		{
			addCaptionedItem("ISSUE", transaction);
		}

		if(isValidMenu("issueAssemble"))
		{
			addCaptionedItem("ISSUE TO ASSEMBLE(IMPORTED MATERIALS)", transaction); 
		}


		/*if(isValidMenu("issueReturn"))
		{
			addCaptionedItem("ISSUE RETURN", transaction);
		}*/

		/*if(isValidMenu("issueReturn"))
		{
			addCaptionedItem("WASTAGE ENTRY", transaction);
		}*/
		if(isValidMenu("issueReturn"))
		{
			addCaptionedItem("ISSUE RETURN", transaction);
		}

		if(isValidMenu("loanIssueRcvRtn"))
		{
			addCaptionedItem("LOAN ISSUE/RECEIVE RETURN", transaction);
		}

		if(isValidMenu("loanRcvIssueRtn"))
		{
			addCaptionedItem("LOAN RECEIVE/ISSUE RETURN", transaction);
		}

		if(isValidMenu("StockSearch"))
		{
			addCaptionedItem("STOCK SEARCH", transaction);
		}
		if(isValidMenu("DelChallan"))
		{
			addCaptionedItem("FG DELIVERY CHALLAN", transaction);
		}
		if(isValidMenu("cancelTheDeliveryChallan"))
		{
			addCaptionedItem("FG DELIVERY CHALLAN DELETE ", transaction);
		}

	}

	private void addReportChild(Object report)
	{	

		/*if(isValidMenu("rawpackingreport"))
		{
			rawpackingreport = addCaptionedItem("RAW/PACKING MATERIALS",report );
			addRawpackingreport(rawpackingreport);
		}

		if(isValidMenu("sparereport"))
		{
			sparereport = addCaptionedItem("SPARE PARTS",report );
			addSparepartsReport(sparereport);
		}
		 */

		if(isValidMenu("ItemInfo")){
			ItemInfo=addCaptionedItem("ITEM INFORMATION", report);
			addItemInfoChild(ItemInfo);
		}
		if(isValidMenu("Requistion")){
			Requistion=addCaptionedItem("REQUISITION", report);
			addRequistionChild(Requistion);
		}
		if(isValidMenu("PurchaseOrder")){
			PurchaseOrder=addCaptionedItem("PURCHASE ORDER", report);
			addPurchaseOrderChild(PurchaseOrder);
		}
		if(isValidMenu("Purchase")){
			Purchase=addCaptionedItem("PURCHASE", report);
			addPurchaseChild(Purchase);
		}
		if(isValidMenu("Issue")){
			Issue=addCaptionedItem("ISSUE", report);
			addIssueChild(Issue);
		}
		if(isValidMenu("IssueReturnReport"))
		{
			Issuereturnreport=addCaptionedItem("ISSUE RETURN", report);
			addIssueReturnChild(Issuereturnreport);
		}


		if(isValidMenu("StockReport")){
			StockReport=addCaptionedItem("STOCK REPORT", report);
			addStockInfoChild(StockReport);
		}


		if(isValidMenu("loanreport"))
		{
			loanreport=addCaptionedItem("LOAN", report);
			addloanChild(loanreport);
		}

		if(isValidMenu("misreport")){
			misreport=addCaptionedItem("MIS REPORT", report);
			addMisChild(misreport);
		}

		/*if(isValidMenu("general"))
		{
			general = addCaptionedItem("GENERAL ",report );
			addGeneralInfoChild(general);
		}
		if(isValidMenu("requisition "))
		{
			requisition = addCaptionedItem("REQUISITION ", report);
			addRequisitionChild(requisition);
		}
		if(isValidMenu("purchaseOrder"))
		{
			purchaseOrder = addCaptionedItem("PURCHASE ORDER ", report);
			addPurchaseOrderChild(purchaseOrder);
		}
		if(isValidMenu("purchase"))
		{
			purchase = addCaptionedItem("PURCHASE ", report);
			addPurchaseChild(purchase);
		}
		if(isValidMenu("issue"))
		{
			issue = addCaptionedItem("ISSUE ", report);
			addIssueChild(issue);
		}
		if(isValidMenu("stock"))
		{
			stock = addCaptionedItem("STOCK ", report);
			addStockChild(stock);
		}*/
	}


	private void addMisChild(Object mischild)
	{
		if(isValidMenu("nonmoving"))
		{
			addCaptionedItem("NON MOVING STATEMANT", mischild);
		}

		if(isValidMenu("sloving"))
		{
			addCaptionedItem("SLOW MOVING STATEMANT", mischild);
		}

		if(isValidMenu("firstmoving"))
		{
			addCaptionedItem("FIRST MOVING STATEMANT", mischild);
		}
	}


	private void addloanChild(Object loanreport)
	{
		if(isValidMenu("loanissueretrunstmant"))
		{
			addCaptionedItem("LOAN ISSUE/ISSUE RETURN STATEMANT", loanreport);
		}

		if(isValidMenu("loanreceivereturnstatement"))
		{
			addCaptionedItem("LOAN RECEIVE/RETURN STATEMANT", loanreport);
		}

		if(isValidMenu("rptloanIssueReturnSummary"))
		{

			addCaptionedItem("LOAN ISSUE/RETURN SUMMARY SUPPLIER WISE", loanreport);

		}

		if(isValidMenu("rptloanReceiveReturnSummary"))
		{

			addCaptionedItem("LOAN RECEIVE/RETURN SUMMARY SUPPLIER WISE", loanreport);

		}


		/*	if(isValidMenu("loanissuereturnsummarysup"))
		{
			addCaptionedItem("LOAN ISSUE/ISSUE RETURN SUMMARY SUPPLIER WISE", loanreport);
		}

		if(isValidMenu("loanreceivereturnsummarsup"))
		{
			addCaptionedItem("LOAN RECEIVE/RETURN SUMMARY SUPPLIER WISE", loanreport);
		}
		if(isValidMenu("loanissuesummary"))
		{
			addCaptionedItem("LOAN ISSUE/ISSUE RETURN SUMMAY AS ON DATE", loanreport);
		}
		if(isValidMenu("loanreceivesummary"))
		{
			addCaptionedItem("LOAN RECEIVE/RECEIVE RETURN SUMMARY AS ON DATE", loanreport);
		}*/

	}


	private void addGeneralInfoChild(Object reports)
	{
		if(isValidMenu("supplierInformation "))
		{
			addCaptionedItem("SUPPLIER INFORMATION ", reports);
		}

		if(isValidMenu("itemInformation "))
		{
			addCaptionedItem("ITEM INFORMATION ", reports);
		}
	}

	private void addItemInfoChild(Object ItemInfo){

		if(isValidMenu("itemInformationreport"))
		{
			addCaptionedItem("ITEM INFORMATION.", ItemInfo);
		}
		if(isValidMenu("itemInformationAsOnDatereport"))
		{
			addCaptionedItem("ITEM INFORMATION WITH STOCK", ItemInfo);
		}
		if(isValidMenu("rptSlowmoving"))
		{

			addCaptionedItem("SLOWMOVING ITEM INFORMATION", ItemInfo);

		}
		if(isValidMenu("rptnonmoving"))
		{

			addCaptionedItem("NONMOVING ITEM INFORMATION", ItemInfo);

		}

	}
	private void addStockInfoChild(Object StockReport){
		if(isValidMenu("openingstockreport"))
		{
			addCaptionedItem("ITEMS OPENING STOCK", StockReport);
		}
		if(isValidMenu("itemwisestockregister"))
		{
			addCaptionedItem("ITEM WISE STOCK REGISTER", StockReport);
		}
		if(isValidMenu("itemwisekardex"))
		{
			addCaptionedItem("ITEM WISE KARDEX", StockReport);
		}

		if(isValidMenu("storeWiseStock"))
		{
			//addCaptionedItem("STORE WISE ITEM STOCK", StockReport);
		}
		if(isValidMenu("stocksummary"))
		{
			addCaptionedItem("STOCK SUMMARY WITH VALUE", StockReport);
		}
		if(isValidMenu("stocksummaryWithoutValue"))
		{
			//addCaptionedItem("STOCK SUMMARY WITHOUT VALUE", StockReport);
		}
		if(isValidMenu("catWiseStockAsOnDate"))
		{
			addCaptionedItem("CATEGORY WISE STOCK AS ON DATE", StockReport);
		}
		if(isValidMenu("catWiseStockDateBetween"))
		{
			addCaptionedItem("CATEGORY WISE STOCK DATE BETWEEN", StockReport);
		}
		if(isValidMenu("rptminimumStock"))
		{
			addCaptionedItem("MINIMUM LEVEL STOCK STATEMENT", StockReport);
		}
		if(isValidMenu("rptmaximumStock"))
		{
			addCaptionedItem("MAXIMUM LEVEL STOCK STATEMENT", StockReport);
		}
		if(isValidMenu("rptReorderStock"))
		{
			addCaptionedItem("RE-ORDER LEVEL STOCK STATEMENT", StockReport);
		}

	}
	private void addRequistionChild(Object Requistion){

		if(isValidMenu("rawRequisitionInfo"))
		{
			addCaptionedItem("REQUISITION", Requistion);
		}

		if(isValidMenu("rawRequisitionRegisterDepartmentWise"))
		{
			addCaptionedItem("DEPARTMENT WISE REQUISITION", Requistion);
		}

		if(isValidMenu("rawRequisitionRegister"))
		{
			//addCaptionedItem("REQUISITION REGISTER", Requistion);
		}

		if(isValidMenu("rawRequisitionStatus"))
		{
			//addCaptionedItem("PENDING REQUISITION", Requistion);
		}
	}
	private void addRawpackingreport(Object reports)
	{


		if(isValidMenu("ItemInfo")){
			ItemInfo=addCaptionedItem("ITEM INFORMATION", reports);
			addItemInfoChild(ItemInfo);
		}
		if(isValidMenu("Requistion")){
			Requistion=addCaptionedItem("REQUISITION", reports);
			addRequistionChild(Requistion);
		}
		if(isValidMenu("PurchaseOrder")){
			PurchaseOrder=addCaptionedItem("PURCHASE ORDER", reports);
			addPurchaseOrderChild(PurchaseOrder);
		}
		if(isValidMenu("Purchase")){
			Purchase=addCaptionedItem("PURCHASE", reports);
			addPurchaseChild(Purchase);
		}
		if(isValidMenu("Issue")){
			Issue=addCaptionedItem("ISSUE", reports);
			addIssueChild(Issue);
		}

		if(isValidMenu("StockReport"))
		{
			StockReport=addCaptionedItem("STOCK REPORT", reports);
			addStockInfoChild(StockReport);
		}


		/*if(isValidMenu("itemInformationraw"))
		{
			addCaptionedItem("RAW MATERIAL ITEM INFORMATION(As On Date)", reports);
		}

		if(isValidMenu("rawpackingopeningstock"))
		{
			addCaptionedItem("RAW/PACKING MATERIALS OPENING STOCK", reports);
		}
		if(isValidMenu("rawRequisitionInfo"))
		{
			addCaptionedItem("RAW MATERIAL REQUISITION INFO", reports);
		}

		if(isValidMenu("rawRequisitionRegister"))
		{
			addCaptionedItem("RAW MATERIAL REQUISITION REGISTER", reports);
		}
		if(isValidMenu("purchaseOrder  "))
		{
			addCaptionedItem("RAW MATERIAL PURCHASE ORDER", reports);
		}

		if(isValidMenu("purchaseOrderRegister"))
		{
			addCaptionedItem("RAW MATERIAL PURCHASE ORDER REGISTER", reports);
		}
		if(isValidMenu("rptGoodReceiveNote"))
		{
			addCaptionedItem("RAW MATERIAL GOOD RECEIVE NOTE", reports);
		}
		if(isValidMenu("rptItemWisePurchaseRegister"))
		{
			addCaptionedItem("RAW MATERIAL ITEM WISE PURCHASE REGISTER", reports);
		}
		if(isValidMenu("rptDateWisePurchaseStatement"))
		{
			addCaptionedItem("RAW MATERIAL DATE WISE PURCHASE STATEMENT", reports);
		}
		if(isValidMenu("rptDateWisePurchaseStatement"))
		{
			addCaptionedItem("RAW MATERIAL SUPPLIER WISE PURCHASE STATEMENT", reports);
		}
		if(isValidMenu("rptDeptWiseIssue"))
		{
			addCaptionedItem("RAW MATERIAL DEPARTMENT WISE ISSUE", reports);
		}
		if(isValidMenu("rptIssueRegister"))
		{
			addCaptionedItem("RAW MATERIAL ISSUE REGISTER", reports);
		}

		if(isValidMenu("itemwisestockregister"))
		{
			addCaptionedItem("ITEM WISE STOCK REGISTER", reports);
		}

		if(isValidMenu("stocksummary"))
		{
			addCaptionedItem("STOCK SUMMARY", reports);
		}*/

	}



	private void addSparepartsReport(Object reports)
	{
		if(isValidMenu("itemInformationsapre"))
		{
			addCaptionedItem("SPARE PARTS INFORMATION", reports);
		}

		if(isValidMenu("spareopeningstock"))
		{
			addCaptionedItem("SPARE PARTS OPENING STOCK", reports);
		}
	}



	private void addRequisitionChild(Object reports)
	{
		if(isValidMenu("requisition."))
		{
			addCaptionedItem("REQUISITION.", reports);
		}

		if(isValidMenu("requisitionRegister"))
		{
			addCaptionedItem("DEPARTMENT WISE REQUISITION", reports);
		}

		if(isValidMenu("requisitionForHeadOffice"))
		{
			addCaptionedItem("REQUISITION FOR HEAD OFFICE", reports);
		}
	}

	private void addPurchaseOrderChild(Object PurchaseOrder)
	{
		if(isValidMenu("purchaseOrder."))
		{
			addCaptionedItem("PURCHASE ORDER.", PurchaseOrder);
		}

		if(isValidMenu("purchaseOrderRegisterSupplierWise"))
		{
			addCaptionedItem("SUPPLIER WISE PURCHASE ORDER REGISTER", PurchaseOrder);
		}

		if(isValidMenu("purchaseOrderRegister"))
		{
			//addCaptionedItem("PURCHASE ORDER REGISTER", PurchaseOrder);
		}

		if(isValidMenu("purchaseOrderStatus"))
		{
			//addCaptionedItem("PENDING PURCHASE ORDER", PurchaseOrder);
		}
		
		if(isValidMenu("PO Balance Statement"))
		{
			addCaptionedItem("PO BALANCE STATEMENT", PurchaseOrder);
		}
	}

	private void addPurchaseChild(Object Purchase)
	{
		if(isValidMenu("rptGoodReceiveNote"))
		{
			addCaptionedItem("GOOD RECEIVE NOTE", Purchase);
		}
		if(isValidMenu("rptItemWisePurchaseRegister"))
		{
			addCaptionedItem("ITEM WISE PURCHASE STATEMENT", Purchase);
		}
		if(isValidMenu("rptDateWisePurchaseStatement"))
		{
			addCaptionedItem("DATE WISE PURCHASE STATEMENT", Purchase);
		}
		if(isValidMenu("rptSupplierWisePurchaseStatement"))
		{
			addCaptionedItem("SUPPLIER WISE PURCHASE STATEMENT", Purchase);
		}
		if(isValidMenu("receivesummary"))
		{
			addCaptionedItem("RECEIVE SUMMARY DATE BETWEEN", Purchase);
		}
	}

	private void addIssueChild(Object Issue)
	{
		/*if(isValidMenu("deliveryChallan"))
		{
			addCaptionedItem("DELIVERY CHALLAN", Issue);
		}*/
		if(isValidMenu("sectionwiseissueregister"))
		{
			addCaptionedItem("SECTION WISE ISSUE REGISTER", Issue);
		}
		if(isValidMenu("sectionwiseissueSummary"))
		{
			addCaptionedItem("SECTION WISE ISSUE SUMMARY", Issue);
		}
		/*if(isValidMenu("RptRequisitionProduction"))
		{
			addCaptionedItem("REQUISITION WISE ISSUE", Issue);
		}*/

		if(isValidMenu("dptreqWiseIssue"))
		{
			addCaptionedItem("REQUISITION WISE ISSUE(Department Wise)", Issue);
		}
		if(isValidMenu("issueRegister"))
		{
			addCaptionedItem("ISSUE REGISTER", Issue);
		}
		if(isValidMenu("AvgPerDayIssue"))
		{
			addCaptionedItem("AVERAGE PER DAY ISSUE", Issue);
		}

		if(isValidMenu("IssueSummary"))
		{
			addCaptionedItem("ISSUE SUMMARY DATE BETWEEN", Issue);
		}

	}

	private void addIssueReturnChild(Object IssueReturn)
	{
		if(isValidMenu("issuereturnregister"))
		{
			addCaptionedItem("ISSUE RETURN REGISTER", IssueReturn);
		}

	}

	private void addStockChild(Object reports)
	{
		if(isValidMenu("openingStock."))
		{
			addCaptionedItem("OPENING STOCK.", reports);
		}

		if(isValidMenu("itemWiseStock"))
		{
			addCaptionedItem("ITEM WISE STOCK (BY QTY)", reports);
		}

		if(isValidMenu("stockSummary"))
		{
			addCaptionedItem("STOCK SUMMARY WITH VALUE", reports);
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

	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				System.out.println(event.getItemId()+" "+event.getItem());

				// RAW-METERIAL SETUP
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL SETUP"))
				{
					showWindow(new rawMaterialSetup(sessionBean),event.getItem(),"rawMeterialSetup");
				}
				// RAW-METERIAL TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL TRANSACTION"))
				{
					showWindow(new rawMeterialTransaction(sessionBean),event.getItem(),"rawMeterialTransaction");
				}
				// RAW-METERIAL REPORT
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL REPORT"))
				{
					showWindow(new rawMaterialReport(sessionBean),event.getItem(),"rawMeterialReport");
				}

				// ITEM CATEGORY
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY"))
				{
					showWindow(new RawItemCategory(sessionBean),event.getItem(),"itemCategory");
				}

				// ITEM SUB-CATEGORY
				if(event.getItem().toString().equalsIgnoreCase("SUB CATEGORY"))
				{
					showWindow(new RawItemSubCategory(sessionBean),event.getItem(),"itemSubCategory");
				}

				if(event.getItem().toString().equalsIgnoreCase("SUB SUBCATEGORY"))
				{
					//showWindow(new RawItemSubSubCategory(sessionBean),event.getItem(),"itemsubSubCategory");
				}

				/*//production type
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION TYPE"))
				{
					showWindow(new ProductionType(sessionBean),event.getItem(),"itemSubCategory");
				}

				//production Step/Process
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION STEP/PROCESS"))
				{
					showWindow(new ProductionStepProcess(sessionBean),event.getItem(),"itemSubCategory");
				}*/

				// STOCK ITEM INFORMATION
				if(event.getItem().toString().equalsIgnoreCase("INFORMATION"))
				{
					showWindow(new RawItemInfo(sessionBean),event.getItem(),"itemInformation");
				}

				if(event.getItem().toString().equalsIgnoreCase("OPENING STOCK"))
				{
					showWindow(new RawMaterialsOpeningStock(sessionBean),event.getItem(),"openingStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("OPENING RATE EDIT"))
				{
					showWindow(new OpeningRateEdit(sessionBean),event.getItem(),"openingRateEdit");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION ENTRY"))
				{
					showWindow(new RequsitionForHeadOffice(sessionBean),event.getItem(),"requisitionEntry");

				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER"))
				{
					showWindow(new RawPurchaseOrder(sessionBean),event.getItem(),"requisitionEntry");
				}

				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER Active / Inactive"))
				{
					showWindow(new PurchaseOrderActiveInactiveInventory(sessionBean),event.getItem(),"purchaseOrderActiveInactive");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE"))
				{
					showWindow(new RawMaterialsIssue(sessionBean),event.getItem(),"issue");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO ASSEMBLE(IMPORTED MATERIALS)"))
				{
					showWindow( new IssueToAssembleImported (sessionBean,"",0),event.getItem(),"issueAssemble"); 
				}

				if(event.getItem().toString().equalsIgnoreCase("MATERIALS RECEIPT(LOCAL & CASH)"))
				{
					showWindow(new RawMaterialsPurchaseReceipt(sessionBean),event.getItem(),"purchaseReceipt");
				}

				if(event.getItem().toString().equalsIgnoreCase("MATERIALS RECEIPT(SPARE PARTS)"))
				{
					showWindow(new RawMaterialsPurchaseReceiptSpare(sessionBean),event.getItem(),"purchaseReceiptSpare");
				}
				if(event.getItem().toString().equalsIgnoreCase("MATERIALS RECEIPT(L/C)"))
				{
					showWindow(new LcCloseing(sessionBean),event.getItem(),"purchaseReceiptLC");
				}

				if(event.getItem().toString().equalsIgnoreCase("MATERIALS RECEIPT(L/C-2016-2017)"))
				{
					showWindow(new LcClosingtemporary(sessionBean),event.getItem(),"purchaseReceiptLC");
				}

				/*if (event.getItem().toString().equalsIgnoreCase("PURCHASE RECEIPT"))
				{
					showWindow(new RawMaterialPRecipt(sessionBean), event.getItem(),"MReceipt");
				}*/

				if(event.getItem().toString().equalsIgnoreCase("LOAN ISSUE/RECEIVE RETURN"))
				{
					showWindow(new LoanIssueRcvRtn(sessionBean),event.getItem(),"loanIssueRcvRtn");
				}

				if(event.getItem().toString().equalsIgnoreCase("LOAN RECEIVE/ISSUE RETURN"))
				{
					showWindow(new LoanRcvIssueRtn(sessionBean),event.getItem(),"loanRcvIssueRtn");
				}


				if(event.getItem().toString().equalsIgnoreCase("STOCK SEARCH"))
				{
					showWindow(new StockSearch(sessionBean),event.getItem(),"StockSearch");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FG DELIVERY CHALLAN"))
				{
					showWindow(new DeliveryChallan(sessionBean),event.getItem(),"DelChallan");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FG DELIVERY CHALLAN DELETE "))
				{
					showWindow(new CancelTheDeliveryChallan(sessionBean),event.getItem(),"cancelTheDeliveryChallan");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE RETURN"))
				{
					showWindow(new RawmaterialIssueReturn(sessionBean),event.getItem(),"issueReturn");
				}

				//Report
				if(event.getItem().toString().equalsIgnoreCase("ITEM INFORMATION."))
				{
					showWindow(new RptItemInformation0(sessionBean,""),event.getItem(),"itemInformationreport");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM INFORMATION WITH STOCK"))
				{
					showWindow(new RptItemInformationAsOnDate(sessionBean,""),event.getItem(),"itemInformationAsOnDatereport");
				}

				if(event.getItem().toString().equalsIgnoreCase("SLOWMOVING ITEM INFORMATION"))
				{
					showWindow(new Rptslowmoving("", sessionBean),event.getItem(),"rptSlowmoving");
				}

				if(event.getItem().toString().equalsIgnoreCase("NONMOVING ITEM INFORMATION"))
				{
					showWindow(new RptNonmoving("", sessionBean),event.getItem(),"rptnonmoving");
				}


				//Stock Report
				if(event.getItem().toString().equalsIgnoreCase("MINIMUM LEVEL STOCK STATEMENT"))
				{
					showWindow(new RptMinimumLevelStock(sessionBean,""),event.getItem(),"rptminimumStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("MAXIMUM LEVEL STOCK STATEMENT"))
				{
					showWindow(new RptMaximumLevelStock(sessionBean,""),event.getItem(),"rptmaximumStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("RE-ORDER LEVEL STOCK STATEMENT"))
				{
					showWindow(new RptLowerLevelStock(sessionBean,""),event.getItem(),"rptReorderStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY WISE STOCK AS ON DATE"))
				{
					showWindow(new RptCateWiseStockAsOnDate(sessionBean,""),event.getItem(),"catWiseStockAsOnDate");
				}
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY WISE STOCK DATE BETWEEN"))
				{
					showWindow(new RptCateWiseStockDateBetween(sessionBean,""),event.getItem(),"catWiseStockDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("STORE WISE ITEM STOCK"))
				{
					showWindow(new RptStoreWiseStock(sessionBean,""),event.getItem(),"storeWiseStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEMS OPENING STOCK"))
				{
					showWindow(new RptOpeningStock(sessionBean,""),event.getItem(),"openingstockreport");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE STOCK REGISTER"))
				{
					showWindow(new RptProductWiseStockRegister(sessionBean,""),event.getItem(),"itemwisestockregister");
				}

				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE KARDEX"))
				{
					showWindow(new RptItemWiseKardex(sessionBean,""),event.getItem(),"itemwisekardex");
				}
				if(event.getItem().toString().equalsIgnoreCase("STOCK SUMMARY WITH VALUE"))
				{
					showWindow(new StockReport(sessionBean,""),event.getItem(),"stocksummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("RAW MATERIAL ITEM INFORMATION(As On Date)"))
				{
					showWindow(new RawMaterialsItemInformationAsOnDate(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION"))
				{
					showWindow(new RawRequisitionForm(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("DEPARTMENT WISE REQUISITION"))
				{
					showWindow(new RawRequisitionRegisterDepartmentWise(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION REGISTER"))
				{
					showWindow(new RawRequisitionRegister(sessionBean,""),event.getItem(),"itemInformationraw");
				}

				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER."))
				{
					showWindow(new RawPurchaseOrderRpt(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("SUPPLIER WISE PURCHASE ORDER REGISTER"))
				{
					showWindow(new RawPurchaseorderRegister(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER REGISTER"))
				{
					showWindow(new RptPurchaseOrderRegister(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("PENDING PURCHASE ORDER"))
				{
					showWindow(new RptPendingPurchaseOrder(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("PO BALANCE STATEMENT"))
				{
					showWindow(new RptPoBalanceStatement(sessionBean),event.getItem(),"itemInformationraw");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("RECEIVE SUMMARY DATE BETWEEN"))
				{
					showWindow(new ReceiveSummary(sessionBean, ""),event.getItem(),"receivesummary");
				}



				if(event.getItem().toString().equalsIgnoreCase("GOOD RECEIVE NOTE"))
				{
					showWindow(new GoodsReceiveNote(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE PURCHASE STATEMENT"))
				{
					showWindow(new ItemWisePurcaseRegisterRpt(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE PURCHASE STATEMENT"))
				{
					showWindow(new DateWisePurchaseRpt(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("SUPPLIER WISE PURCHASE STATEMENT"))
				{
					showWindow(new SupplierWisePurchaseRpt(sessionBean,""),event.getItem(),"itemInformationraw");
				}


				if(event.getItem().toString().equalsIgnoreCase("AVERAGE PER DAY ISSUE"))
				{
					showWindow(new RptAvgPerDayIssue(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE REGISTER"))
				{
					showWindow(new RptIssueRegister(sessionBean,""),event.getItem(),"itemInformationraw");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE RETURN REGISTER"))
				{
					showWindow(new RptIssueReturnRegister(sessionBean,""),event.getItem(),"issuereturnregister");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ISSUE REGISTER"))
				{
					showWindow(new RptDeptWiseIssueNew(sessionBean,""),event.getItem(),"sectionwiseissueregister");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ISSUE SUMMARY"))
				{
					showWindow(new DeptWiseIssueSummary(sessionBean,""),event.getItem(),"sectionwiseissuesummary");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE SUMMARY DATE BETWEEN"))
				{
					showWindow(new IssueSummary(sessionBean,""),event.getItem(),"issueSummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("REQUISITION WISE ISSUE(Department Wise)"))
				{
					showWindow(new DpetWiseRqIssue(sessionBean,""),event.getItem(),"RptRequisitionProduction");
				}


				/*if(event.getItem().toString().equalsIgnoreCase("REQUISITION WISE ISSUE"))
				{
					showWindow(new RptRequisitionProduction(sessionBean,""),event.getItem(),"RptRequisitionProduction");
				}*/


				if(event.getItem().toString().equalsIgnoreCase("RAW/PACKING MATERIALS OPENING STOCK"))
				{
					showWindow(new RptOpeningStock(sessionBean,""),event.getItem(),"rawpackingopeningstock");
				}

				if(event.getItem().toString().equalsIgnoreCase("SPARE PARTS INFORMATION"))
				{
					showWindow(new SpareItemInfo(sessionBean,""),event.getItem(),"itemInformationspare");
				}


				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE STOCK REGISTER"))
				{
					showWindow(new RptProductWiseStockRegister(sessionBean,""),event.getItem(),"itemwisestockregister");
				}

				if(event.getItem().toString().equalsIgnoreCase("STOCK SUMMARY WITH VALUE"))
				{
					showWindow(new StockReport(sessionBean,""),event.getItem(),"stocksummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("LOAN ISSUE/ISSUE RETURN STATEMANT"))
				{
					showWindow(new RptLoanIssueReturn(sessionBean,""),event.getItem(),"stocksummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("LOAN RECEIVE/RETURN STATEMANT"))
				{
					showWindow(new RptLoanReceiveReturn(sessionBean,""),event.getItem(),"stocksummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("LOAN ISSUE/RETURN SUMMARY SUPPLIER WISE"))
				{
					showWindow(new RptLoanIssueRtnSummary(sessionBean, ""),event.getItem(),"rptloanIssueReturnSummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("LOAN RECEIVE/RETURN SUMMARY SUPPLIER WISE"))
				{
					showWindow(new RptLoanReceiveRtnSummary(sessionBean, ""),event.getItem(),"rptloanReceiveReturnSummary");
				}

				//nonmoving


				if(event.getItem().toString().equalsIgnoreCase("NON MOVING STATEMANT"))
				{
					showWindow(new RptNonmoving("",sessionBean),event.getItem(),"nonmoving");
				}

				//SLOW MOVING STATEMANT

				if(event.getItem().toString().equalsIgnoreCase("SLOW MOVING STATEMANT"))
				{
					showWindow(new Rptslowmoving("",sessionBean),event.getItem(),"slowmoving");
				}

				//FIRST MOVING STATEMANT

				if(event.getItem().toString().equalsIgnoreCase("FIRST MOVING STATEMANT"))
				{
					showWindow(new RptFirstMoving("",sessionBean),event.getItem(),"firstmoving");
				}

			}
		});
	}

	// check is valid menu for add menu bar
	private boolean isValidMenu(String id)
	{	
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			System.out.println(id+" "+sessionBean.getUserId());

			System.out.println("SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
					"' AND menuId = '"+id+"'");

			Iterator iter = session.createSQLQuery("SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
					"' AND menuId = '"+id+"'").list().iterator();

			if(iter.hasNext())
			{
				return false;
			}
			else
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
