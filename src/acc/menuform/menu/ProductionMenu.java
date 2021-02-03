package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.transaction.CashReceiptVoucherOld;
import acc.reportmodule.asset.AssetLedgerRpt;
import acc.reportmodule.asset.AssetRegisterRpt;
import acc.reportmodule.asset.AssetScheduleRpt;
import acc.reportmodule.asset.LeasedAssetScheduleRpt;
import acc.reportmodule.daybook.DayBook;
import acc.reportmodule.financialStatement.AdjustedTrialRpt;
import acc.reportmodule.financialStatement.BalanceSheetRpt;
import acc.reportmodule.financialStatement.CashTrialRpt;
import acc.reportmodule.financialStatement.OpeningTrialRpt;
import acc.reportmodule.financialStatement.ProfitLossRpt;
import acc.reportmodule.ledger.GeneralLedger;
import acc.reportmodule.ledger.LedgerGroup;
import acc.reportmodule.mis.BankPositionRpt;
import acc.reportmodule.mis.CashFlowYearly;
import acc.reportmodule.mis.DailyCashStatementRpt;
import acc.reportmodule.mis.DailyTransactionRpt;
import acc.reportmodule.mis.GroupLedgerSummary;
import acc.reportmodule.mis.InterestCalculation;
import acc.reportmodule.mis.MonthlyExpensetRpt;
import acc.reportmodule.mis.ReceivableAging;
import acc.reportmodule.voucher.UnusedChequeList;
import acc.reportmodule.voucher.Voucher;

import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.ReceycleModuleSetup.RecycleItemInformation;
import com.example.RecycleModuleTransaction.DailyCrashing;
import com.example.RecycleModuleTransaction.DailyCrashingIssue;
import com.example.RecycleModuleTransaction.RecycledItemOpening;
import com.example.RecycleModuleTransaction.RejectedItemIssue;
import com.example.RecycleModuleTransaction.RejectedItemOpeningStock;
import com.example.RecycleModuleTransaction.RejectedItemReceipt;
import com.example.RecycleModuleTransaction.RejectedproductIssueReceived;
import com.example.RecycledModuleReport.RptRecycledCrashingMaterialOpeningStock;
import com.example.RecycledModuleReport.RptRecycledCrassingStatement;
import com.example.RecycledModuleReport.RptRecycledIssueSatatementCrashingMaterial;
import com.example.RecycledModuleReport.RptRecycledItemInfo;
import com.example.RecycledModuleReport.RptRecycledRejectionOpenigStock;
import com.example.RecycledModuleReport.RptRecycledStoreRejectQtyStockReport;
import com.example.RecycledModuleReport.RptRecycledSummary;
import com.example.ThirdPartyReport.RptThirdPartyIssueRegister;
import com.example.ThirdPartyReport.RptThirdPartyItemInformation;
import com.example.ThirdPartyReport.RptThirdPartyReceiptRegister;
import com.example.ThirdPartyReport.RptThirdPartyStockSummary;
import com.example.ThirdPartyReport.RptThirdpartyStockLedger;
import com.example.ThirdPartySetup.ThirdpartyRMInfo;
import com.example.productionReport.MachineWiseProduction;
import com.example.productionReport.MachineWiseProduction1;
import com.example.productionReport.MouldWiseProduction;
import com.example.productionReport.MouldWiseProduction1;
import com.example.productionReport.ProductionApproveNonApproveStatus;
import com.example.productionReport.RptApproveStatusDateBetween;
import com.example.productionReport.RptAssembleStock;
import com.example.productionReport.RptBatchWiseStock;
import com.example.productionReport.RptBotleIssueAsOnDate;
import com.example.productionReport.RptBottleCost;
import com.example.productionReport.RptBottleIssue;
import com.example.productionReport.RptBottleIssueRegister;
import com.example.productionReport.RptDailyProduction;
import com.example.productionReport.RptDailyProductionSummary;
import com.example.productionReport.RptDataEntryStatusDateBetween;
import com.example.productionReport.RptFgProductionDetailsAsOnDate;
import com.example.productionReport.RptFgProductionDetailsDateBetween;
import com.example.productionReport.RptFgStock;
import com.example.productionReport.RptFgStockAsOnDate;
import com.example.productionReport.RptFgStockDateBetween;
import com.example.productionReport.RptFgStockMoulding;
import com.example.productionReport.RptFloorStockAsOnDate;
import com.example.productionReport.RptFloorStockDateBetween;
import com.example.productionReport.RptIssueToLabelingPrinting;
import com.example.productionReport.RptJobOrder;
import com.example.productionReport.RptJobOrderRemaining;
import com.example.productionReport.RptJobOrderWiseProduction;
import com.example.productionReport.RptLabelCost;
import com.example.productionReport.RptLabelDailyProduction;
import com.example.productionReport.RptLabelDailyProductionSummary;
import com.example.productionReport.RptLabelIssue;
import com.example.productionReport.RptLabelIssueAsOnDate;
import com.example.productionReport.RptLabelIssueRegister;
import com.example.productionReport.RptLabelOpeningStock;
import com.example.productionReport.RptLabelStockAsOnDate;
import com.example.productionReport.RptLabelStockDateBetween;
import com.example.productionReport.RptLabelStockSummary;
import com.example.productionReport.RptLacqureStock;
import com.example.productionReport.RptMixerIssue;
import com.example.productionReport.RptMouldingDailyProduction;
import com.example.productionReport.RptMouldingInformation;
import com.example.productionReport.RptMouldingOpeningStock;
import com.example.productionReport.RptMouldingStockAsOnDate;
import com.example.productionReport.RptMouldingStockDateBetween;
import com.example.productionReport.RptMouldingStockSummary;
import com.example.productionReport.RptProcessOpening;
import com.example.productionReport.RptProductWiseProduction;
import com.example.productionReport.RptProductionAccountingValuetion;
import com.example.productionReport.RptProductionStep;
import com.example.productionReport.RptProductionSummary;
import com.example.productionReport.RptRequisitionProduction0;
import com.example.productionReport.RptSBMStock;
import com.example.productionReport.RptSectionOpeningStock;
import com.example.productionReport.RptSectionOpeningStockold;
import com.example.productionReport.RptSemiFgProductionDetailsAsOnDate;
import com.example.productionReport.RptSemiFgProductionDetailsDateBetween;
import com.example.productionReport.RptSemiFgStockAsOnDate;
import com.example.productionReport.RptSemiFgStockDateBetween;
import com.example.productionReport.RptStandardInfo;
import com.example.productionReport.RptTubeIssue;
import com.example.productionReport.RptTubeIssueAsOnDate;
import com.example.productionReport.RptTubeIssueRegister;
import com.example.productionReport.RptTubeOpeningStock;
import com.example.productionReport.RptTubeStockAsOnDate;
import com.example.productionReport.RptTubeStockDateBetween;
import com.example.productionReport.RptTubeStockSummary;
import com.example.productionReport.RptmixerIssueReturn;
import com.example.productionReport.SectionWiseStock;
//import com.example.productionSetup.CostSheet;
import com.example.productionSetup.CostSheet;
import com.example.productionSetup.FgOpening;
import com.example.productionSetup.FinishedStandard;
import com.example.productionSetup.ProductionOpening;
import com.example.productionSetup.ProductionStepProcess;
import com.example.productionSetup.ProductionStepProcessold;
import com.example.productionSetup.ProductionSubStepProcess;
import com.example.productionSetup.ProductionSubSubStepProcess;
import com.example.productionSetup.ProductionType;
import com.example.productionSetup.StandardFormFinished;
import com.example.productionTransaction.LabelProductionEntry;
import com.example.productionSetup.sectionOpeningStock;
import com.example.productionTransaction.AdjustmentEntry;
import com.example.productionTransaction.AssembleRequisitionEntry;
import com.example.productionTransaction.DailyProductionEntryLabelingPrinting;
import com.example.productionTransaction.DailyProductionEntryLacqure;
import com.example.productionTransaction.DailyProductionEntrySBM;
import com.example.productionTransaction.FgAssemble;
import com.example.productionTransaction.IssueToAssemble;
import com.example.productionTransaction.IssueToLabelingPrinting;
import com.example.productionTransaction.IssueToLacqure;
import com.example.productionTransaction.IssueToSBM;
import com.example.productionTransaction.ItemActivation;
import com.example.productionTransaction.JobOrder;
import com.example.productionTransaction.LabelIssueEntry;
import com.example.productionTransaction.LabelingStoreReceivedApprove;
import com.example.productionTransaction.LacqureRequisitionEntry;
import com.example.productionTransaction.MixtureIssueEntry;
import com.example.productionTransaction.MixtureIssueReturnEntry;
import com.example.productionTransaction.MouldinFgGroupIssue;
import com.example.productionTransaction.MouldingIssueEntry;
import com.example.productionTransaction.MouldingProductionEntry;
import com.example.productionTransaction.MouldingStoreReceivedApprove;
import com.example.productionTransaction.ProductionRequistion;
import com.example.productionTransaction.ProductionRequistion0;
import com.example.productionTransaction.PurchaseOrderActiveInactiveProduction;
import com.example.productionTransaction.RequisitionEntryLabelingPrinting;
import com.example.productionTransaction.SectionReceivedApprove;
import com.example.productionTransaction.StoreReceivedApproveMoulding;
import com.example.productionTransaction.StretchBlowMoldingRequsition;
import com.example.productionTransaction.TubeIssueEntry;
import com.example.productionTransaction.TubeProductionEntry;
import com.example.productionTransaction.TubeWastageOpeningStock;
import com.example.productionTransaction.TubeWastageReceiptEntry;
import com.example.productionTransaction.WastageOpeningStock;
import com.example.productionTransaction.WastageReceiptEntry;
import com.example.thirdpartyTransaction.ThirdPartyRMIssue;
import com.example.thirdpartyTransaction.ThirdPartyRMOpeningStock;
import com.example.thirdpartyTransaction.ThirdPartyReceipt;
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

import database.hibernate.TbCompanyInfo;

@SuppressWarnings("serial")
public class ProductionMenu 
{	
	private HashMap winMap = new HashMap();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object productionSetup = null;
	Object productionTransaction = null;
	Object productionReport = null;
	Object tube = null;
	Object label = null;
	Object moulding = null;
	Object LabelingPrinting = null;
	Object LacqureProduction = null;
	Object SBMProduction = null;
	Object Approved = null;
	Object tuberepot = null;
	Object labelreport = null;
	Object MixingReport = null;
	Object ProductionReport = null;
	Object StockReport = null;
	Object costReport = null;
	Object sectionOpeningStockRpt = null;
	Object jobOrderRpt = null;
	Object requisitionRpt = null;
	Object jobOrderWiseProdRpt = null;
	Object jobOrderWiseProdRemainingRpt = null;
	Object assemble = null;
	Object thirdPartyModule=null;
	Object thirdpartysetup=null;
	Object thirdpartyTransaction=null;
	Object thirpartyReport=null;
	Object crashingModule=null;
	Object crashingModuleSteup=null;
	Object crashingModuleTransaction=null;
	Object crashingModuleReport=null;



	public ProductionMenu(Object productionModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();
		if(isValidMenu("productionSetup"))
		{
			productionSetup = addCaptionedItem("PRODUCTION SETUP", productionModule);
			addProductionSetupChild(productionSetup);
		}

		if(isValidMenu("productionTransaction"))
		{
			productionTransaction = addCaptionedItem("PRODUCTION TRANSACTION", productionModule);
			addProductionTransactionChild(productionTransaction);
		}
		if(isValidMenu("productionReports"))
		{
			productionReport = addCaptionedItem("PRODUCTION REPORT", productionModule);
			addProductionReportChild(productionReport);
		}

		/*if(isValidMenu("thirdpartyItem"))
		{
			thirdPartyModule = addCaptionedItem("THIRD PARTY R/M", productionModule);
			addThirdParty(thirdPartyModule);
		}

		if(isValidMenu("recyclemodule"))
		{
			crashingModule = addCaptionedItem("CRASHING MODULE", productionModule);
			addrecyclemodule(crashingModule);
		}*/
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


	private void addrecyclemodule(Object crashingModule)
	{
		if(isValidMenu("recyclemodulesetup"))
		{
			crashingModuleSteup = addCaptionedItem("CRASHING MODULE SETUP", crashingModule); 
			addRecycleModuleSetup(crashingModuleSteup); 
		}

		if(isValidMenu("recyclemoduleTransaction"))
		{
			crashingModuleTransaction = addCaptionedItem("CRASHING MODULE TRANSACTION", crashingModule);
			addRecycleModuleTransaction(crashingModuleTransaction); 
			//addProductionTransactionChild(productionTransaction);
		}
		if(isValidMenu("crashingModuleReport"))
		{
			crashingModuleReport = addCaptionedItem("CRASHING MODULE REPORTS", crashingModule);
			addRecycleModuleReports(crashingModuleReport);
			//addProductionReportChild(productionReport);
		}	

	}

	private void addRecycleModuleSetup(Object crashingModuleSteup)
	{

		if(isValidMenu("recycleItem"))
		{
			addCaptionedItem("RECYCLED ITEM INFORMATION", crashingModuleSteup); 
		}		
	}

	private void addRecycleModuleTransaction(Object crashingModuleTransaction)
	{

		if(isValidMenu("recycleopening"))
		{
			addCaptionedItem("RECYCLED ITEM OPENING STOCK", crashingModuleTransaction);
		}

		if(isValidMenu("rejecteditemopening"))
		{
			addCaptionedItem("REJECTED ITEM OPENING STOCK", crashingModuleTransaction);
		}

		if(isValidMenu("rejecteditemreceipt"))
		{
			addCaptionedItem("REJECTED ITEM RECEIPT", crashingModuleTransaction);
		}

		if(isValidMenu("rejecteditemIssue"))
		{
			addCaptionedItem("REJECTED ITEM ISSUE", crashingModuleTransaction);
		}

		if(isValidMenu("rejecteditemIssueRcv"))
		{
			addCaptionedItem("REJECTED ITEM ISSUE RECEIVE", crashingModuleTransaction);
		}


		if(isValidMenu("dailycrashing"))
		{
			addCaptionedItem("DAILY CRASHING", crashingModuleTransaction);
		}

		if(isValidMenu("crashingIssue"))
		{
			addCaptionedItem("CRASHING ISSUE", crashingModuleTransaction);
		}

		/*if(isValidMenu("thirdpartyIssue"))
	{
		addCaptionedItem("THIRD PARTY R/M ISSUE", thirdpartytransaction);
	}*/

	}


	private void addRecycleModuleReports(Object recyclemoduleReport)
	{
		if(isValidMenu("RecycledItemInfo"))
		{
			addCaptionedItem("RECYCLED ITEM INFORMATION.", recyclemoduleReport);
		}

		if(isValidMenu("recycledRejectionOpenigStock"))
		{
			addCaptionedItem("REJECTION OPENING STOCK", recyclemoduleReport);
		}

		if(isValidMenu("recycledCrashingMaterialOpeningStock"))
		{
			addCaptionedItem("CRASHING MATERIAL OPENING STOCK", recyclemoduleReport);
		}

		if(isValidMenu("RptIssueSatatementcrashingMaterial"))
		{
			addCaptionedItem("ISSUE SATATEMENT CRASHING MATERIAL", recyclemoduleReport);
		}
		if(isValidMenu("crashingStatement"))
		{
			addCaptionedItem("CRASHING STATEMENT", recyclemoduleReport);
		}
		if(isValidMenu("rptStoreRejectQtyStockReport"))
		{
			addCaptionedItem("STORE REJECT QTY STOCK REPORT", recyclemoduleReport);
		}

		if(isValidMenu("recycleSummary"))
		{
			addCaptionedItem("RECYCLE SUMMARY", recyclemoduleReport);
		}

	}

	private void addProductionSetupChild(Object productionSetup)
	{
		if(isValidMenu("productionType"))
		{
			addCaptionedItem("PRODUCTION TYPE", productionSetup);
		}
		if(isValidMenu("productionStep"))
		{
			//addCaptionedItem("PRODUCTION STEP PROCESS OLD", productionSetup);
		}
		if(isValidMenu("productionStep1"))
		{
			addCaptionedItem("PRODUCTION STEP PROCESS", productionSetup);
		}

		/*if(isValidMenu("productionSubStep"))
		{
			addCaptionedItem("PRODUCTION SUB STEP PROCESS", productionSetup);
		}
		if(isValidMenu("productionSubSubStep"))
		{
			addCaptionedItem("PRODUCTION SUB SUB STEP PROCESS", productionSetup);
		}*/

		if(isValidMenu("sectionOpeningStock"))
		{
			// addCaptionedItem("SECTION OPENING STOCK OLD", productionSetup);

		}
		if(isValidMenu("sectionOpeningStock1"))
		{
			addCaptionedItem("SECTION OPENING STOCK", productionSetup);

		}
		if(isValidMenu("processopening"))
		{
			//addCaptionedItem("PROCESS OPENING STOCK.", productionSetup);

		}
		if(isValidMenu("mouldingWastageOpening"))
		{
			//addCaptionedItem("REJECT OPENING", productionSetup);
		}
	}

	private void addProductionTransactionChild(Object productionTransaction)
	{
		if(isValidMenu("ItemGroup"))
		{
			addCaptionedItem("RAW ITEM GROUP", productionTransaction);
		}
		if(isValidMenu("joborder"))
		{
			addCaptionedItem("JOB ORDER", productionTransaction);
		}

		if(isValidMenu("JobOrderActiveInaction"))
		{
			addCaptionedItem("JOB ORDER ACTIVE / INACTIVE", productionTransaction);
		}


		if(isValidMenu("IssueToLabelingOrPrinting"))
		{
			addCaptionedItem("ISSUE TO LABELING/PRINTING/CAP FOLDING/SHRINK/ASSEMBLE", productionTransaction);

		}
		

		if(isValidMenu("mouldingproduction"))
		{
			moulding = addCaptionedItem("MOULDING PRODUCTION", productionTransaction);
			addmoulding(moulding);

		}

		if(isValidMenu("labelingPrintingproduction"))
		{
			LabelingPrinting = addCaptionedItem("LABELING/PRINTING/CAP FOLDING/SHRINK PRODUCTION", productionTransaction);
			addLabelingPrinting(LabelingPrinting);

		}
		if(isValidMenu("LacqureProduction"))
		{
			LacqureProduction = addCaptionedItem("LACQURE PRODUCTION", productionTransaction);
			addLacqure(LacqureProduction);

		}
		if(isValidMenu("SBMProduction"))
		{
			SBMProduction = addCaptionedItem("SBM PRODUCTION", productionTransaction);
			addSBM(SBMProduction);

		}
		if(isValidMenu("Assemble"))
		{
			assemble = addCaptionedItem("ASSEMBLE", productionTransaction);
			addAssemble(assemble);

		}
		if(isValidMenu("approved"))
		{
			Approved = addCaptionedItem("RECEIVED APPROVE", productionTransaction);
			addApproved(Approved);

		}

	}
	private void addAssemble(Object LacqureProduction) {
		if(isValidMenu("AssembleRequisiton"))
		{
			addCaptionedItem("ASSEMBLE REQUISITION", LacqureProduction);

		}
		if(isValidMenu("IssueToAssemble"))
		{
			addCaptionedItem("ISSUE TO ASSEMBLE", LacqureProduction);

		}
		if(isValidMenu("dailyAssembleEntry"))
		{
			addCaptionedItem("DAILY ASSEMBLE ENTRY", LacqureProduction);
		}

	}
	private void addLacqure(Object LacqureProduction) {
		if(isValidMenu("LacqureRequisiton"))
		{
			addCaptionedItem("LACQURE REQUISITION", LacqureProduction);

		}
		if(isValidMenu("IssueToLacqure"))
		{
			addCaptionedItem("ISSUE TO LACQURE", LacqureProduction);

		}
		if(isValidMenu("dailyProductionEntryLacqure"))
		{
			addCaptionedItem("DAILY PRODUTION ENTRY[LACQURE]", LacqureProduction);

		}
	}


	private void addSBM(Object SBMProduction) {
		if(isValidMenu("sbmRequisition"))
		{
			addCaptionedItem("SBM REQUISITION", SBMProduction);
		}
		if(isValidMenu("IssueToSBM"))
		{
			addCaptionedItem("ISSUE TO SBM", SBMProduction);

		}
		if(isValidMenu("SBMProudctionEntry"))
		{
			addCaptionedItem("DAILY PRODUCTION ENTRY[SBM]", SBMProduction);

		}
	}
	private void addApproved(Object approved) {
		if(isValidMenu("ReceiveApprove"))
		{
			addCaptionedItem("SECTION RECEIVED APPROVE", approved);

		}
		if(isValidMenu("StoreReceiveApproveMoulding"))
		{
			addCaptionedItem("STORE RECEIVED APPROVE [MOULDING]", approved);

		}
		if(isValidMenu("StoreReceiveApproveLabelingPrinting"))
		{
			addCaptionedItem("STORE RECEIVED APPROVE [LABELING/PRINTING/CAP/SBM]", approved);

		}
	}

	private void addLabelingPrinting(Object labelingPrinting) {
		if(isValidMenu("RequisitionEntryLabelingPrinting"))
		{
			addCaptionedItem("REQUISITON ENTRY[LABELING/PRINTING/CAP FOLDING/SHRINK]", labelingPrinting);

		}

		if(isValidMenu("DailyProudctionLabelingPrinting"))
		{
			addCaptionedItem("DAILY PRODUCTION ENTRY[LABELING/PRINTING/CAP FOLDING/SHRINK]", LabelingPrinting);

		}

		if(isValidMenu("AdjustmentEntry"))
		{
			addCaptionedItem("ADJUSTMENT ENTRY", LabelingPrinting);

		}
	}

	private void addtubechild(Object tubeproduction)
	{
		/*if(isValidMenu("productionopening"))
		{
			addCaptionedItem("PRODUCTION OPENING", productionTransaction);
		}*/

		if(isValidMenu("tubeWastageOpening"))
		{
			addCaptionedItem("WASTAGE OPENING ", tubeproduction);

		}

		if(isValidMenu("tubeproductionIssue"))
		{
			addCaptionedItem("WASTAGE RECEIPT ENTRY ", tubeproduction);

		}

		if(isValidMenu("tubeproductionIssue"))
		{
			addCaptionedItem("ISSUE TO PROCESS", tubeproduction);

		}

		if(isValidMenu("tubeproductionEntry"))
		{
			addCaptionedItem("DAILY PRODUCTION ENTRY", tubeproduction);
		}

	}

	private void addlabechild(Object labelproduction)
	{
		/*if(isValidMenu("productionopening"))
		{
			addCaptionedItem("PRODUCTION OPENING", productionTransaction);
		}*/

		if(isValidMenu("labelproductionIssue"))
		{
			addCaptionedItem("ISSUE TO PROCESS ", labelproduction);

		}

		if(isValidMenu("labelproductionEntry"))
		{
			addCaptionedItem("DAILY PRODUCTION ENTRY ", labelproduction);
		}

	}

	private void addmoulding(Object mouldingproduction)
	{

		if(isValidMenu("RequisitionEntry"))
		{
			addCaptionedItem("REQUISITON ENTRY", mouldingproduction);

		}
		if(isValidMenu("mouldingproductionIssue"))
		{
			addCaptionedItem("REJECT RECEIPT ENTRY", mouldingproduction);

		}
		if(isValidMenu("mouldingproductionEntry"))
		{
			addCaptionedItem("MIXTURE ISSUE ENTRY.", mouldingproduction);

		}
		if(isValidMenu("mouldingproductionReturnEntry"))
		{
			addCaptionedItem("MIXTURE ISSUE RETURN ENTRY.", mouldingproduction);

		}
		if(isValidMenu("mouldingproductionIssue"))
		{
			addCaptionedItem("ISSUE TO LABEL OR PRINTING", mouldingproduction);

		}

		if(isValidMenu("mouldingproductionEntry"))
		{
			addCaptionedItem("DAILY PRODUCTION ENTRY.", mouldingproduction);
		}

	}


	private void addProductionReportChild(Object productionReport)
	{
		if(isValidMenu("sectionOpeningStockRpt"))
		{
			//sectionOpeningStockRpt= addCaptionedItem("SECTION OPENING STOCK ", productionReport);
			//			addmouldingreport(mouldingReport);
		}
		if(isValidMenu("jobOrderRpt"))
		{
			jobOrderRpt= addCaptionedItem("JOB ORDER REPORT", productionReport);
			//			addmouldingreport(mouldingReport);
		}

		if(isValidMenu("jobOrderWiseProdRpt"))
		{
			//jobOrderWiseProdRpt= addCaptionedItem("JOB ORDER WISE PRODUCTION REPORT", productionReport);
			//			addmouldingreport(mouldingReport);
		}

		if(isValidMenu("jobOrderWiseProdRemainingRpt"))
		{
			//jobOrderWiseProdRemainingRpt= addCaptionedItem("JOB ORDER WISE PRODUCTION REMAINING", productionReport);
			//			addmouldingreport(mouldingReport);
		}

		if(isValidMenu("requisitionRpt"))
		{
			requisitionRpt= addCaptionedItem("REQUISITION REPORT", productionReport);
			//			addmouldingreport(mouldingReport);
		}
		if(isValidMenu("issuetoLabelingPrinting"))
		{
			requisitionRpt= addCaptionedItem("ISSUE TO LABELING/PRINTING/CAP/SBM/ASSEMBLE REPORT", productionReport);
			//			addmouldingreport(mouldingReport);
		}
		if(isValidMenu("dateWiseDataEntryCheck"))
		{
			addCaptionedItem("DATE WISE PRODUCTION DATA ENTRY STATUS", productionReport);

		}


		if(isValidMenu("dateWiseApproveCheck"))
		{
			addCaptionedItem("DATE WISE STORE APPROVE STATUS", productionReport);

		}

		if(isValidMenu("dateWiseApproveNonCheck"))
		{
			addCaptionedItem("DATE WISE STORE APPROVE NON APPROVE  STATUS", productionReport);

		}

		if(isValidMenu("mouldingproductionEvalution"))
		{
			addCaptionedItem("PRODUCTION DATA ACCOUNTING TRANSACTION (JOURNAL)", productionReport);
		}
		if(isValidMenu("standardInfo"))
		{
			addCaptionedItem("STANDARD-INFO", productionReport);

		}


		if(isValidMenu("tubeStockSummary1"))
		{
			//addCaptionedItem("PROCESS OPENING", productionReport);

		}

		if(isValidMenu("mouldingReport"))
		{
			MixingReport= addCaptionedItem("MIXING", productionReport);
			addMixingReport(MixingReport);
		}
		if(isValidMenu("ProductionReport"))
		{
			ProductionReport= addCaptionedItem("PRODUCTION", productionReport);
			addProductReport(ProductionReport);
		}
		if(isValidMenu("mouldingReport"))
		{
			StockReport= addCaptionedItem("STOCK", productionReport);
			addStockReport(StockReport);
		}



		/*if(isValidMenu("costReport"))
		{
			costReport= addCaptionedItem("COST REPORTS", productionReport);
			addcostreport(costReport);
		}*/
	}
	private void addProductReport(Object ProductReport){

		if(isValidMenu("dailyProductionReportMoulding"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT  ", ProductReport);

		}

		if(isValidMenu("productionsummary"))
		{
			addCaptionedItem("PRODUCTION SUMMARY", ProductReport);

		}


		/*if(isValidMenu("dailyProductionReportSummary"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT SUMMARY", ProductReport);

		}*/
		if(isValidMenu("mahineWiseproduction"))
		{
			addCaptionedItem("MACHINE WISE PRODUCTION", ProductReport);
		}
		if(isValidMenu("mouldwiseproduction"))
		{
			addCaptionedItem("MOULD WISE PRODUCTION.", ProductReport);
		}
		if(isValidMenu("productWiseproduction"))
		{
			addCaptionedItem("PRODUCT WISE PRODUCTION.", ProductReport);
		}

		/*if(isValidMenu("dailyProductionReportMoulding"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT  ", mouldingReport);

		}*/

		/*if(isValidMenu("semiFgProductionDateBetween"))
		{
			addCaptionedItem("SEMI FG PRODUCTION DETAILS DATE BETWEEN", ProductReport);

		}
		if(isValidMenu("semiFgProductionAsOnDate"))
		{
			addCaptionedItem("SEMI FG PRODUCTION DETAILS AS ON DATE", ProductReport);

		}
		if(isValidMenu("FgProductionDateBetween"))
		{
			addCaptionedItem("FG PRODUCTION DETAILS DATE BETWEEN", ProductReport);

		}
		if(isValidMenu("FgProductionAsOnDate"))
		{
			addCaptionedItem("FG PRODUCTION DETAILS AS ON DATE", ProductReport);

		}*/
	}
	private void addStockReport(Object StockReport){



		if(isValidMenu("semiFgStock"))
		{
			addCaptionedItem("SEMI FG STOCK", StockReport);

		}

		if(isValidMenu("FgStock"))
		{
			addCaptionedItem("FG STOCK", StockReport);

		}

		if(isValidMenu("FgStockMoulding"))
		{
			addCaptionedItem("FG STOCK(Moulding)", StockReport);

		}

		//SECTIONWISE
		if(isValidMenu("FgStocksectionWise"))
		{
			addCaptionedItem("SECTIONWISE FG STOCK", StockReport);

		}


		if(isValidMenu("BatchWiseStock"))
		{
			addCaptionedItem("BATCH WISE STOCK", StockReport);

		}
		if(isValidMenu("AssembleStock"))
		{
			addCaptionedItem("ASSEMBLE STOCK", StockReport);

		}
		if(isValidMenu("LacqureStock"))
		{
			addCaptionedItem("LACQURE STOCK", StockReport);

		}	
		if(isValidMenu("SBMStock"))
		{
			addCaptionedItem("SBM STOCK", StockReport);

		}
		if(isValidMenu("semiFgProductionAsOnDate"))
		{
			addCaptionedItem("FLOOR STOCK AS ON DATE", StockReport);

		}
		if(isValidMenu("semiFgProductionDateBetween"))
		{
			addCaptionedItem("FLOOR STOCK DATE BETWEEN", StockReport);

		}
	}
	private void addtubereport(Object tuberepot)
	{
		if(isValidMenu("tubeIssue"))
		{
			addCaptionedItem("TUBE PRODUCTION ISSUE", tuberepot);

		}

		if(isValidMenu("tubeIssueRegister"))
		{
			addCaptionedItem("TUBE PRODUCTION ISSUE REGISTER", tuberepot);

		}
		if(isValidMenu("tubeIssueRegisterAsOnDate"))
		{
			addCaptionedItem("TUBE PRODUCTION ISSUE REGISTER AS ON DATE", tuberepot);

		}

		if(isValidMenu("dailyProductionReport"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT", tuberepot);

		}
		if(isValidMenu("dailyProductionReportSummary"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT SUMMARY", tuberepot);

		}
		if(isValidMenu("tubeOpeningStock"))
		{
			addCaptionedItem("TUBE OPENING STOCK", tuberepot);

		}
		if(isValidMenu("tubeStockAsOnDate"))
		{
			addCaptionedItem("TUBE STOCK AS ON DATE", tuberepot);

		}
		if(isValidMenu("tubeStockDateBetween"))
		{
			addCaptionedItem("TUBE STOCK DATE BETWEEN", tuberepot);

		}
		if(isValidMenu("tubeStockSummary"))
		{
			addCaptionedItem("TUBE STOCK SUMMARY", tuberepot);

		}

	}

	private void addlabelreport(Object labelreport)
	{
		if(isValidMenu("labelIssue"))
		{
			addCaptionedItem("LABEL PRODUCTION ISSUE", labelreport);

		}

		if(isValidMenu("labelIssueRegister"))
		{
			addCaptionedItem("LABEL PRODUCTION ISSUE REGISTER", labelreport);

		}
		if(isValidMenu("labelIssueRegisterAsOnDate"))
		{
			addCaptionedItem("LABEL PRODUCTION ISSUE REGISTER AS ON DATE", labelreport);

		}

		if(isValidMenu("dailyProductionReportLabel"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT ", labelreport);

		}
		if(isValidMenu("dailyProductionReportSummary"))
		{
			addCaptionedItem("DAILY PRODUCTION REPORT SUMMARY ", labelreport);

		}
		if(isValidMenu("labelOpeningStock"))
		{
			addCaptionedItem("LABEL OPENING STOCK", labelreport);

		}
		if(isValidMenu("labelStockAsOnDate"))
		{
			addCaptionedItem("LABEL STOCK AS ON DATE", labelreport);

		}
		if(isValidMenu("labelStockDateBetween"))
		{
			addCaptionedItem("LABEL STOCK DATE BETWEEN", labelreport);

		}
		if(isValidMenu("labelStockSummary"))
		{
			addCaptionedItem("LABEL STOCK SUMMARY", labelreport);

		}


	}

	private void addMixingReport(Object mouldingReport)
	{
		/*if(isValidMenu("BottleIssue"))
		{
			addCaptionedItem("MOULDING PRODUCTION ISSUE", mouldingReport);
		}

		if(isValidMenu("bottleIssueRegister"))
		{
			addCaptionedItem("MOULDING PRODUCTION ISSUE REGISTER", mouldingReport);

		}
		if(isValidMenu("bottleIssueRegisterAsOnDate"))
		{
			addCaptionedItem("MOULDING PRODUCTION ISSUE REGISTER AS ON DATE", mouldingReport);

		}*/
		if(isValidMenu("mixtureIssueEntryReport"))
		{
			addCaptionedItem("MIXTURE ISSUE REPORT", mouldingReport);

		}
		if(isValidMenu("mixtureIssueReturnEntryReport"))
		{
			addCaptionedItem("MIXTURE ISSUE RETURN REPORT", mouldingReport);

		}

		/*if(isValidMenu("semiFgProductionAsOnDate"))
		{
			addCaptionedItem("SECTION STOCK AS ON DATE", mouldingReport);

		}
		if(isValidMenu("semiFgProductionDateBetween"))
		{
			addCaptionedItem("SECTION STOCK DATE BETWEEN", mouldingReport);

		}*/


		/*if(isValidMenu("tubeOpeningStock"))
		{
			addCaptionedItem("MOULDING OPENING STOCK", mouldingReport);

		}
		if(isValidMenu("tubeStockAsOnDate"))
		{
			addCaptionedItem("MOULDING STOCK AS ON DATE", mouldingReport);

		}
		if(isValidMenu("tubeStockDateBetween"))
		{
			addCaptionedItem("MOULDING STOCK DATE BETWEEN", mouldingReport);

		}
		if(isValidMenu("tubeStockSummary"))
		{
			addCaptionedItem("MOULDING STOCK SUMMARY", mouldingReport);

		}*/


	}

	private void addcostreport(Object costReport)
	{
		if(isValidMenu("labelCost"))
		{
			addCaptionedItem("LABEL COST REPORT", costReport);

		}
		if(isValidMenu("bottleCost"))
		{
			addCaptionedItem("BOTTLE COST REPORT", costReport);

		}

	}


	private void addSubChild(Object trialBalance, Object profitLossAccount, Object balanceSheet)
	{
		/*if(isValidMenu("openingTrialBalance"))
		{
			addCaptionedItem("OPENING TRIAL BALANCE", trialBalance);
		}*/
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

				// PRODUCTION SETUP
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION TYPE"))
				{
					showWindow(new ProductionType(sessionBean),event.getItem(),"productionSetup");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION STEP PROCESS OLD "))
				{
					//showWindow(new ProductionStepProcessold(sessionBean),event.getItem(),"productionSetup");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION STEP PROCESS"))
				{
					showWindow(new ProductionStepProcess(sessionBean),event.getItem(),"productionSetup1");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION SUB STEP PROCESS"))
				{
					showWindow(new ProductionSubStepProcess(sessionBean),event.getItem(),"productionSetup");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION SUB SUB STEP PROCESS"))
				{
					showWindow(new ProductionSubSubStepProcess(sessionBean),event.getItem(),"productionSetup");
				}
				/*if(event.getItem().toString().equalsIgnoreCase("STANDARD SETTINGS"))
				{
					showWindow(new FinishedStandard(sessionBean),event.getItem(),"FinishedGoodsStandard");
				}
				if(event.getItem().toString().equalsIgnoreCase("COST SHEET"))
				{
					showWindow(new CostSheet(sessionBean),event.getItem(),"costSheet");
				}*/

				/*if(event.getItem().toString().equalsIgnoreCase("PRODUCTION OPENING"))
				{
					showWindow(new ProductionOpening(sessionBean),event.getItem(),"productionTransaction");
				}*/
				if(event.getItem().toString().equalsIgnoreCase("SECTION OPENING STOCK"))
				{
					showWindow(new sectionOpeningStock(sessionBean),event.getItem(),"sectionOpeningStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("PROCESS OPENING STOCK"))
				{
					showWindow( new ProductionOpening (sessionBean),event.getItem(),"productionOpeningStock");
				}

				/*if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS OPENING STOCK"))
				{
					showWindow( new FgOpening (sessionBean),event.getItem(),"fgOpening");
				}*/

				if(event.getItem().toString().equalsIgnoreCase("RAW ITEM GROUP"))
				{
					showWindow(new ItemActivation(sessionBean),event.getItem(),"ItemGroup");
				}
				if(event.getItem().toString().equalsIgnoreCase("JOB ORDER"))
				{
					showWindow(new JobOrder(sessionBean),event.getItem(),"joborder");
				}

				if(event.getItem().toString().equalsIgnoreCase("JOB ORDER ACTIVE / INACTIVE"))
				{
					showWindow(new PurchaseOrderActiveInactiveProduction(sessionBean),event.getItem(),"JobOrderActiveInaction");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION RECEIVED APPROVE"))
				{
					showWindow( new SectionReceivedApprove (sessionBean),event.getItem(),"ReceiveApprove");
				}
				if(event.getItem().toString().equalsIgnoreCase("STORE RECEIVED APPROVE [MOULDING]"))
				{
					showWindow( new MouldingStoreReceivedApprove (sessionBean),event.getItem(),"StoreReceiveApproveMoulding");
				}
				if(event.getItem().toString().equalsIgnoreCase("STORE RECEIVED APPROVE [LABELING/PRINTING/CAP/SBM]"))
				{
					showWindow( new LabelingStoreReceivedApprove (sessionBean),event.getItem(),"StoreReceiveApproveLabelingPrinting");
				}
				if(event.getItem().toString().equalsIgnoreCase("ADJUSTMENT ENTRY"))
				{
					showWindow( new AdjustmentEntry (sessionBean),event.getItem(),"AdjustmentEntry");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITON ENTRY"))
				{
					showWindow( new ProductionRequistion (sessionBean,"",0),event.getItem(),"RequisitionEntry");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITON ENTRY[LABELING/PRINTING/CAP FOLDING/SHRINK]"))
				{
					showWindow( new RequisitionEntryLabelingPrinting (sessionBean,"",0),event.getItem(),"RequisitionEntryLabelingPrinting");
				}
				if(event.getItem().toString().equalsIgnoreCase("ASSEMBLE REQUISITION"))
				{
					showWindow( new AssembleRequisitionEntry (sessionBean,"",0),event.getItem(),"AssembleRequisiton");
					
					
				}
				if(event.getItem().toString().equalsIgnoreCase("LACQURE REQUISITION"))
				{
					showWindow( new LacqureRequisitionEntry (sessionBean,"",0),event.getItem(),"LacqureRequisiton");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO LACQURE"))
				{
					showWindow( new IssueToLacqure (sessionBean,"",0),event.getItem(),"IssueToLacqure");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO ASSEMBLE"))
				{
					showWindow( new IssueToAssemble (sessionBean,"",0),event.getItem(),"IssueToAssemble");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUTION ENTRY[LACQURE]"))
				{
					showWindow( new DailyProductionEntryLacqure (sessionBean,"",0),event.getItem(),"dailyProductionEntryLacqure");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO LABELING/PRINTING/CAP FOLDING/SHRINK/ASSEMBLE"))
				{
					showWindow( new IssueToLabelingPrinting (sessionBean,"",0),event.getItem(),"IssueToLabelingOrPrinting");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION ENTRY[LABELING/PRINTING/CAP FOLDING/SHRINK]"))
				{
					showWindow( new DailyProductionEntryLabelingPrinting (sessionBean,"",0),event.getItem(),"DailyProudctionLabelingPrinting");
				}
               //-------------------------------------SBM-------------------------------//
				
				if(event.getItem().toString().equalsIgnoreCase("SBM REQUISITION"))
				{
					showWindow( new StretchBlowMoldingRequsition(sessionBean,"",0),event.getItem(),"sbmRequisition");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO SBM"))
				{
					showWindow( new IssueToSBM (sessionBean,"",0),event.getItem(),"IssueToSBM");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION ENTRY[SBM]"))
				{
					showWindow( new DailyProductionEntrySBM (sessionBean,"",0),event.getItem(),"SBMProudctionEntry");
				}

				//Tube Producdtion
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO PROCESS"))
				{
					showWindow(new TubeIssueEntry(sessionBean),event.getItem(),"tubeproductionIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION ENTRY"))
				{
					showWindow(new TubeProductionEntry(sessionBean),event.getItem(),"productionTransaction");
				}

				if(event.getItem().toString().equalsIgnoreCase("TUBE PRODUCTION ISSUE REGISTER"))
				{
					showWindow(new RptTubeIssueRegister(sessionBean,""),event.getItem(),"RptTubeIssueRegister");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT"))
				{
					showWindow(new RptDailyProduction(sessionBean,""),event.getItem(),"productionOpeningStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("TUBE PRODUCTION ISSUE REGISTER AS ON DATE"))
				{
					showWindow(new RptTubeIssueAsOnDate(sessionBean,""),event.getItem(),"tubeIssueRegisterAsOnDate");
				}

				if(event.getItem().toString().equalsIgnoreCase("TUBE PRODUCTION ISSUE"))
				{
					showWindow(new RptTubeIssue(sessionBean,""),event.getItem(),"tubeIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT"))
				{
					showWindow(new RptDailyProduction(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT SUMMARY"))
				{
					showWindow(new RptDailyProductionSummary(sessionBean,""),event.getItem(),"productionOpeningStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("SEMI FG PRODUCTION DETAILS DATE BETWEEN"))
				{
					showWindow(new RptSemiFgProductionDetailsDateBetween(sessionBean,""),event.getItem(),"semiFgProductionDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("SEMI FG PRODUCTION DETAILS AS ON DATE"))
				{
					showWindow(new RptSemiFgProductionDetailsAsOnDate(sessionBean,""),event.getItem(),"semiFgProductionAsOnDate");
				}

				if(event.getItem().toString().equalsIgnoreCase("FG PRODUCTION DETAILS DATE BETWEEN"))
				{
					showWindow(new RptFgProductionDetailsDateBetween(sessionBean,""),event.getItem(),"semiFgProductionDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG PRODUCTION DETAILS AS ON DATE"))
				{
					showWindow(new RptFgProductionDetailsAsOnDate(sessionBean,""),event.getItem(),"semiFgProductionAsOnDate");
				}
				if(event.getItem().toString().equalsIgnoreCase("MIXTURE ISSUE REPORT"))
				{
					showWindow(new RptMixerIssue(sessionBean,""),event.getItem(),"mixtureIssueEntryReport");
				}
				if(event.getItem().toString().equalsIgnoreCase("MIXTURE ISSUE RETURN REPORT"))
				{
					showWindow(new RptmixerIssueReturn(sessionBean,""),event.getItem(),"mixtureIssueReturnEntryReport");
				}


				if(event.getItem().toString().equalsIgnoreCase("SECTION STOCK DATE BETWEEN"))
				{
					showWindow(new RptSemiFgStockDateBetween(sessionBean,""),event.getItem(),"semiFgProductionDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION STOCK AS ON DATE"))
				{
					showWindow(new RptSemiFgStockAsOnDate(sessionBean,""),event.getItem(),"semiFgProductionAsOnDate");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG STOCK DATE BETWEEN"))
				{
					showWindow(new RptFgStockDateBetween(sessionBean,""),event.getItem(),"semiFgProductionDateBetween");
				}

				if(event.getItem().toString().equalsIgnoreCase("BATCH WISE STOCK"))
				{
					showWindow(new RptBatchWiseStock(sessionBean),event.getItem(),"BatchWiseStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG STOCK AS ON DATE"))
				{
					showWindow(new RptFgStockAsOnDate(sessionBean,""),event.getItem(),"semiFgProductionAsOnDate");
				}

				if(event.getItem().toString().equalsIgnoreCase("SEMI FG STOCK"))
				{
					showWindow(new RptSemiFgStockDateBetween(sessionBean,""),event.getItem(),"semiFgStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG STOCK"))
				{
					showWindow(new RptFgStock(sessionBean,""),event.getItem(),"FgStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("ASSEMBLE STOCK"))
				{
					showWindow(new RptAssembleStock(sessionBean,""),event.getItem(),"AssembleStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("LACQURE STOCK"))
				{
					showWindow(new RptLacqureStock(sessionBean,""),event.getItem(),"LacqureStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("SBM STOCK"))
				{
					showWindow(new RptSBMStock(sessionBean,""),event.getItem(),"SBMStock");
				}
				


				if(event.getItem().toString().equalsIgnoreCase("FLOOR STOCK DATE BETWEEN"))
				{
					showWindow(new RptFloorStockDateBetween(sessionBean,""),event.getItem(),"semiFgProductionDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("FLOOR STOCK AS ON DATE"))
				{
					showWindow(new RptFloorStockAsOnDate(sessionBean,""),event.getItem(),"semiFgProductionAsOnDate");
				}


				if(event.getItem().toString().equalsIgnoreCase("TUBE OPENING STOCK"))
				{
					showWindow(new RptTubeOpeningStock(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("TUBE STOCK AS ON DATE"))
				{
					showWindow(new RptTubeStockAsOnDate(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("TUBE STOCK DATE BETWEEN"))
				{
					showWindow(new RptTubeStockDateBetween(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("TUBE STOCK SUMMARY"))
				{
					showWindow(new RptTubeStockSummary(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				//Label Producdtion

				if(event.getItem().toString().equalsIgnoreCase("REJECT OPENING "))
				{
					showWindow(new TubeWastageOpeningStock(sessionBean),event.getItem(),"tubeWastageOpening");
				}

				if(event.getItem().toString().equalsIgnoreCase("REJECT RECEIPT ENTRY "))
				{
					showWindow(new TubeWastageReceiptEntry(sessionBean),event.getItem(),"tubeproductionIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO PROCESS "))
				{
					showWindow(new LabelIssueEntry(sessionBean),event.getItem(),"productionTransaction");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION ENTRY "))
				{
					showWindow(new LabelProductionEntry(sessionBean),event.getItem(),"productionTransaction");
				}

				// Label Production Report
				if(event.getItem().toString().equalsIgnoreCase("LABEL PRODUCTION ISSUE"))
				{
					showWindow(new RptLabelIssue(sessionBean,""),event.getItem(),"labelIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("LABEL PRODUCTION ISSUE REGISTER"))
				{
					showWindow(new RptLabelIssueRegister(sessionBean,""),event.getItem(),"RptLabelIssueRegister");
				}

				if(event.getItem().toString().equalsIgnoreCase("LABEL PRODUCTION ISSUE REGISTER AS ON DATE"))
				{
					showWindow(new RptLabelIssueAsOnDate(sessionBean,""),event.getItem(),"tubeIssueRegisterAsOnDate");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT "))
				{
					showWindow(new RptLabelDailyProduction(sessionBean,""),event.getItem(),"dailyProductionReportLabel");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT SUMMARY "))
				{
					showWindow(new RptLabelDailyProductionSummary(sessionBean,""),event.getItem(),"dailyProductionReportSummary");
				}
				if(event.getItem().toString().equalsIgnoreCase("LABEL OPENING STOCK"))
				{
					showWindow(new RptLabelOpeningStock(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("LABEL STOCK AS ON DATE"))
				{
					showWindow(new RptLabelStockAsOnDate(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("LABEL STOCK DATE BETWEEN"))
				{
					showWindow(new RptLabelStockDateBetween(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("LABEL STOCK SUMMARY"))
				{
					showWindow(new RptLabelStockSummary(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("PROCESS OPENING STOCK."))
				{
					showWindow(new ProductionOpening(sessionBean),event.getItem(),"tubeStockSummary2");
				}

				//Moulding Producdtion
				if(event.getItem().toString().equalsIgnoreCase("REJECT OPENING"))
				{
					showWindow(new WastageOpeningStock(sessionBean),event.getItem(),"mouldingWastageOpening");
				}

				if(event.getItem().toString().equalsIgnoreCase("REJECT RECEIPT ENTRY"))
				{
					showWindow(new WastageReceiptEntry(sessionBean),event.getItem(),"mouldingproductionIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO LABEL OR PRINTING"))
				{
					showWindow(new MouldingIssueEntry(sessionBean),event.getItem(),"mouldingproductionIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION ENTRY."))
				{
					showWindow(new MouldingProductionEntry(sessionBean),event.getItem(),"mouldingproductionIssue");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY ASSEMBLE ENTRY"))
				{
					showWindow(new FgAssemble(sessionBean),event.getItem(),"dailyAssembleEntry");
				}
				if(event.getItem().toString().equalsIgnoreCase("MIXTURE ISSUE ENTRY."))
				{
					showWindow(new MixtureIssueEntry(sessionBean),event.getItem(),"mouldingproductionEntry");
				}
				if(event.getItem().toString().equalsIgnoreCase("MIXTURE ISSUE RETURN ENTRY."))
				{
					showWindow(new MixtureIssueReturnEntry(sessionBean),event.getItem(),"mouldingproductionReturnEntry");
				}
				// Cost reports
				if(event.getItem().toString().equalsIgnoreCase("LABEL COST REPORT"))
				{
					showWindow(new RptLabelCost(sessionBean,""),event.getItem(),"labelCost");
				}
				if(event.getItem().toString().equalsIgnoreCase("BOTTLE COST REPORT"))
				{
					showWindow(new RptBottleCost(sessionBean,""),event.getItem(),"bottleCost");
				}

				// Bottle Production Report

				if(event.getItem().toString().equalsIgnoreCase("MOULDING PRODUCTION ISSUE REGISTER AS ON DATE"))
				{
					showWindow(new RptBotleIssueAsOnDate(sessionBean,""),event.getItem(),"bottleIssueRegisterAsOnDate");
				}

				if(event.getItem().toString().equalsIgnoreCase("MOULDING PRODUCTION ISSUE REGISTER"))
				{
					showWindow(new RptBottleIssueRegister(sessionBean,""),event.getItem(),"bottleIssueRegister");
				}

				if(event.getItem().toString().equalsIgnoreCase("MOULD WISE PRODUCTION."))
				{
					//showWindow(new MouldWiseProduction1(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
					showWindow(new MouldWiseProduction(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("MACHINE WISE PRODUCTION"))
				{
					//showWindow(new MachineWiseProduction1(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
					showWindow(new MachineWiseProduction(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCT WISE PRODUCTION."))
				{
					//showWindow(new MachineWiseProduction1(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
					showWindow(new RptSemiFgProductionDetailsDateBetween(sessionBean,""),event.getItem(),"productWiseproduction");
				}


				if(event.getItem().toString().equalsIgnoreCase("MOULDING PRODUCTION ISSUE"))
				{
					showWindow(new RptBottleIssue(sessionBean,""),event.getItem(),"BottleIssue");
				}

				if(event.getItem().toString().equalsIgnoreCase("DATE WISE STORE APPROVE STATUS"))
				{
					showWindow(new RptApproveStatusDateBetween(sessionBean,""),event.getItem(),"dateWiseApproveCheck");
				}

				//ProductionApproveNonApproveStatus

				if(event.getItem().toString().equalsIgnoreCase("DATE WISE STORE APPROVE NON APPROVE  STATUS"))
				{
					showWindow(new ProductionApproveNonApproveStatus(sessionBean,""),event.getItem(),"dateWiseApproveNonCheck");
				}

				if(event.getItem().toString().equalsIgnoreCase("DATE WISE PRODUCTION DATA ENTRY STATUS"))
				{
					showWindow(new RptDataEntryStatusDateBetween(sessionBean,""),event.getItem(),"dateWiseDataEntryCheck");
				}
				if(event.getItem().toString().equalsIgnoreCase("DAILY PRODUCTION REPORT  "))
				{
					showWindow(new RptMouldingDailyProduction(sessionBean,""),event.getItem(),"dailyProductionReportMoulding");
				}

				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION SUMMARY"))
				{
					showWindow(new RptProductionSummary(sessionBean,""),event.getItem(),"productionsummary");
				}

				if(event.getItem().toString().equalsIgnoreCase("MOULDING OPENING STOCK"))
				{
					showWindow(new RptMouldingOpeningStock(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("MOULDING STOCK AS ON DATE"))
				{
					showWindow(new RptMouldingStockAsOnDate(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("MOULDING STOCK DATE BETWEEN"))
				{
					showWindow(new RptMouldingStockDateBetween(sessionBean,""),event.getItem(),"productionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("MOULDING STOCK SUMMARY"))
				{
					showWindow(new RptMouldingStockSummary(sessionBean,""),event.getItem(),"productionOpeningStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("SECTION OPENING STOCK OLD"))
				{
					showWindow(new RptSectionOpeningStockold(sessionBean,""),event.getItem(),"sectionOpeningStockRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION OPENING STOCK "))
				{
					showWindow(new RptSectionOpeningStock(sessionBean,""),event.getItem(),"sectionOpeningStockRpt1");
				}

				if(event.getItem().toString().equalsIgnoreCase("JOB ORDER REPORT"))
				{
					showWindow(new RptJobOrder(sessionBean,""),event.getItem(),"jobOrderRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION REPORT"))
				{
					showWindow(new RptRequisitionProduction0(sessionBean,""),event.getItem(),"requisitionRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE TO LABELING/PRINTING/CAP/SBM/ASSEMBLE REPORT"))
				{
					showWindow(new RptIssueToLabelingPrinting(sessionBean,""),event.getItem(),"issuetoLabelingPrinting");
				}
				if(event.getItem().toString().equalsIgnoreCase("JOB ORDER WISE PRODUCTION REPORT"))
				{
					showWindow(new RptJobOrderWiseProduction(sessionBean,""),event.getItem(),"jobOrderWiseProdRpt");
				}


				if(event.getItem().toString().equalsIgnoreCase("JOB ORDER WISE PRODUCTION REMAINING"))
				{
					showWindow(new RptJobOrderRemaining(sessionBean,""),event.getItem(),"jobOrderWiseProdRemainingRpt");
				}

				if(event.getItem().toString().equalsIgnoreCase("PROCESS OPENING"))
				{
					showWindow(new RptProcessOpening(sessionBean),event.getItem(),"productionReport");
				}

				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION DATA ACCOUNTING TRANSACTION (JOURNAL)"))
				{
					showWindow(new RptProductionAccountingValuetion(sessionBean,""),event.getItem(),"mouldingproductionEvalution");
				}

				if(event.getItem().toString().equalsIgnoreCase("STANDARD-INFO"))
				{
					showWindow(new RptStandardInfo(sessionBean,""),event.getItem(),"standardInfo");
				}




				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M INFORMATION"))
				{
					showWindow(new ThirdpartyRMInfo(sessionBean),event.getItem(),"thirdpartRM"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M OPENING STOCK"))
				{
					showWindow(new ThirdPartyRMOpeningStock(sessionBean),event.getItem(),"thirdpartRMOpening"); 
				}

				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M RECEIPT"))
				{
					showWindow(new ThirdPartyReceipt(sessionBean),event.getItem(),"thirdpartyReceipt"); 
				}

				/*if(isValidMenu("thirdpartyIssue"))
					{
						addCaptionedItem("THIRD PARTY R/M ISSUE", thirdpartytransaction);
					}*/

				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ISSUE"))
				{
					showWindow(new ThirdPartyRMIssue(sessionBean),event.getItem(),"thirdpartyIssue"); 
				}

				//RECYCLED ITEM INFORMATION

				if(event.getItem().toString().equalsIgnoreCase("RECYCLED ITEM INFORMATION"))
				{
					showWindow(new RecycleItemInformation(sessionBean),event.getItem(),"recycleItem"); 
				}

				//RECYCLED ITEM OPENING STOCK


				//RECYCLED ITEM OPENING STOCK

				if(event.getItem().toString().equalsIgnoreCase("RECYCLED ITEM OPENING STOCK"))
				{
					showWindow(new RecycledItemOpening(sessionBean),event.getItem(),"recycleopening"); 

				}


				if(event.getItem().toString().equalsIgnoreCase("REJECTED ITEM OPENING STOCK"))
				{
					showWindow(new RejectedItemOpeningStock(sessionBean),event.getItem(),"rejecteditemopening"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("REJECTED ITEM RECEIPT"))
				{
					showWindow(new RejectedItemReceipt(sessionBean),event.getItem(),"rejecteditemreceipt"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("REJECTED ITEM ISSUE"))
				{
					showWindow(new RejectedItemIssue(sessionBean),event.getItem(),"rejecteditemIssue"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("DAILY CRASHING"))
				{
					showWindow(new DailyCrashing(sessionBean),event.getItem(),"dailycrashing"); 
				}



				if(event.getItem().toString().equalsIgnoreCase("CRASHING ISSUE"))
				{
					showWindow(new DailyCrashingIssue(sessionBean),event.getItem(),"crashingIssue"); 
				}

				/*if(isValidMenu("rejecteditemIssueRcv"))
					{
						addCaptionedItem("REJECTED ITEM ISSUE RECEIVE", recyclemoduleTransaction);
					}*/

				//REJECTED ITEM ISSUE RECEIVE

				if(event.getItem().toString().equalsIgnoreCase("REJECTED ITEM ISSUE RECEIVE"))
				{
					showWindow(new RejectedproductIssueReceived(sessionBean),event.getItem(),"rejecteditemIssueRcv"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ITEM INFORMATION"))
				{
					showWindow(new RptThirdPartyItemInformation(sessionBean,""),event.getItem(),"thirdpartyRMitemInfo"); 
				}



				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ITEM RECEIPT REGISTER"))
				{
					showWindow(new RptThirdPartyReceiptRegister(sessionBean,""),event.getItem(),"thirdpartyRMReceiptRegister"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ITEM ISSUE REGISTER"))
				{
					showWindow(new RptThirdPartyIssueRegister(sessionBean,""),event.getItem(),"thirdpartyRMIssueRegister"); 
				}



				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ITEM STOCK LEDGER"))
				{
					showWindow(new RptThirdpartyStockLedger(sessionBean,""),event.getItem(),"thirdpartyRMStockLedger"); 
				}

				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY R/M ITEM STOCK SUMMARY"))
				{
					showWindow(new RptThirdPartyStockSummary(sessionBean,""),event.getItem(),"thirdpartyRMStockSummary"); 
				}


				if(event.getItem().toString().equalsIgnoreCase("CRASHING MATERIAL OPENING STOCK"))
				{
					showWindow(new RptRecycledCrashingMaterialOpeningStock(sessionBean,""),event.getItem(),"recycledCrashingMaterialOpeningStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("REJECTION OPENING STOCK"))
				{
					showWindow(new RptRecycledRejectionOpenigStock(sessionBean,""),event.getItem(),"recycledRejectionOpenigStock");
				}

				if(event.getItem().toString().equalsIgnoreCase("ISSUE SATATEMENT CRASHING MATERIAL"))
				{
					showWindow(new RptRecycledIssueSatatementCrashingMaterial(sessionBean,""),event.getItem(),"RptIssueSatatementcrashingMaterial");
				}

				if(event.getItem().toString().equalsIgnoreCase("CRASHING STATEMENT"))
				{
					showWindow(new RptRecycledCrassingStatement(sessionBean,""),event.getItem(),"crashingStatement");
				}

				if(event.getItem().toString().equalsIgnoreCase("STORE REJECT QTY STOCK REPORT"))
				{
					showWindow(new RptRecycledStoreRejectQtyStockReport(sessionBean,""),event.getItem(),"rptStoreRejectQtyStockReport");
				}

				if(event.getItem().toString().equalsIgnoreCase("RECYCLED ITEM INFORMATION."))
				{
					showWindow(new RptRecycledItemInfo(sessionBean,""),event.getItem(),"RecycledItemInfo");
				}

				if(event.getItem().toString().equalsIgnoreCase("RECYCLE SUMMARY"))
				{
					showWindow(new RptRecycledSummary(sessionBean,""),event.getItem(),"recycleSummary");
				}

				if(isValidMenu("FgStocksectionWise"))
				{
					//addCaptionedItem("SECTIONWISE STOCK", StockReport);

				}

				if(event.getItem().toString().equalsIgnoreCase("SECTIONWISE FG STOCK"))
				{
					showWindow(new SectionWiseStock(sessionBean,""),event.getItem(),"FgStocksectionWise");
				}




				if(event.getItem().toString().equalsIgnoreCase("FG STOCK(Moulding)"))
				{
					showWindow(new RptFgStockMoulding(sessionBean,""),event.getItem(),"FgStockMoulding");
				}


				/*if(event.getItem().toString().equalsIgnoreCase("MOULD INFORMATION "))
				{
					showWindow(new RptMouldingInformation(sessionBean),event.getItem(),"tubeStockSummary2");
				}
				 */

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
