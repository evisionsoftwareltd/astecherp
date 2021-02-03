package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.sparePartsReport.DateWisePurchaseSPRpt;
import com.example.sparePartsReport.DeptWiseIssueSummarySP;
import com.example.sparePartsReport.DpetWiseRqIssue;
import com.example.sparePartsReport.GoodsReceiveNote;
import com.example.sparePartsReport.IssueSummarySP;
import com.example.sparePartsReport.ItemWisePurcaseRegisterSPRpt;
import com.example.sparePartsReport.RawRequisitionForm;
import com.example.sparePartsReport.RawRequisitionRegister;
import com.example.sparePartsReport.RawRequisitionRegisterDepartmentWise;
import com.example.sparePartsReport.ReceiveSummarySP;
import com.example.sparePartsReport.RptAvgPerDayIssueSP;
import com.example.sparePartsReport.RptCateWiseStockSPAsOnDate;
import com.example.sparePartsReport.RptCateWiseStockSPDateBetween;
import com.example.sparePartsReport.RptDeptWiseIssueNewSP;
import com.example.sparePartsReport.RptIssueRegisterSP;
import com.example.sparePartsReport.RptIssueReturnRegister;
import com.example.sparePartsReport.RptItemInformationSP0;
import com.example.sparePartsReport.RptItemInformationSPAsOnDate;
import com.example.sparePartsReport.RptItemWiseKardexSP;
import com.example.sparePartsReport.RptLowerLevelStockSP;
import com.example.sparePartsReport.RptMaximumLevelStockSP;
import com.example.sparePartsReport.RptMinimumLevelStockSP;
import com.example.sparePartsReport.RptOpeningStockSP;
import com.example.sparePartsReport.RptProductWiseStockRegister;
import com.example.sparePartsReport.RptStoreWiseStock;
import com.example.sparePartsReport.SpareItemInfo;
import com.example.sparePartsReport.StockReportSP;
import com.example.sparePartsReport.SupplierWisePurchaseRpt;
//import com.example.sparePartsSetupProductionStepProcess;
import com.example.sparePartsSetup.SpareItemCategory;
import com.example.sparePartsSetup.SpareItemSubCategory;
import com.example.sparePartsSetup.RawItemSubSubCategory;
import com.example.sparePartsSetup.SparePartsItemInfo;
import com.example.sparePartsSetup.SpareRackInfo;
import com.example.sparePartsSetup.SpareSubRackInfo;
import com.example.sparePartsSetup.sparePartsReport;
import com.example.sparePartsSetup.sparePartsSetup;
import com.example.sparePartsSetup.sparePartsTransaction;
import com.example.sparePartsTransaction.SparePartsIssueReturn;
import com.example.sparePartsTransaction.SparePartsOpeningStock;
import com.example.sparePartsTransaction.PurchaseOrderActiveInactiveInventory;
//import com.example.sparePartsTransaction.RawMaterialsPurchaseReceipt;
import com.example.sparePartsTransaction.SparePartsIssue;
import com.example.sparePartsTransaction.SparePartsPurchaseReceipt;
import com.example.sparePartsTransaction.SparePartsRequsitionForHeadOffice;
import com.example.sparePartsTransaction.SparePartsStockSearch;
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

public class SparePartsMenu
{
	private HashMap winMap = new HashMap();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;
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
	
	Object ItemInfoSpare=null;
	Object StockReportSpare=null;
	Object RequistionSpare=null;
	Object PurchaseOrder=null;
	Object receiptSpare=null;
	Object IssueSpare=null;
	Object IssuereturnreportSpare =null;
	
	public SparePartsMenu(Object sparepartsModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("sparePartsSetup"))
		{
			masterSetup = addCaptionedItem("SPARE PARTS SETUP", sparepartsModule);
			addMasterChild(masterSetup);
		}
		if(isValidMenu("sparePartsTransaction"))
		{
			transaction = addCaptionedItem("SPARE PARTS TRANSACTION", sparepartsModule);
			addTransactionChild(transaction);
		}
		if(isValidMenu("sparePartsReport"))
		{
			report = addCaptionedItem("SPARE PARTS REPORT", sparepartsModule);
			addReportChild(report);
		}
	}

	private void addMasterChild(Object masterSetup)
	{
		if(isValidMenu("itemCategorySP"))
		{
			addCaptionedItem("CATEGORY ", masterSetup);
		}

		if(isValidMenu("itemSubCategorySP"))
		{
			addCaptionedItem("SUB CATEGORY ", masterSetup);
		}
		
		if(isValidMenu("rackInfoSP"))
		{
			addCaptionedItem("RAC INFORMATION", masterSetup);
		}
		if(isValidMenu("subRackInfoSP"))
		{
			addCaptionedItem("SUB RAC INFORMATION", masterSetup);
		}

		if(isValidMenu("itemInformationSP"))
		{
			addCaptionedItem("SPARE ITEM INFORMATION ", masterSetup);
		}
	}

	private void addTransactionChild(Object transaction)
	{
		if(isValidMenu("openingStockSP"))
		{
			addCaptionedItem("OPENING STOCK ", transaction);
		}
		if(isValidMenu("requisitionSP"))
		{
			addCaptionedItem("REQUISITION ENTRY ", transaction);
		}
		
		if(isValidMenu("requisitionApproveSP"))
		{
			//addCaptionedItem("REQUISITION APPROVE ", transaction);
		}
		
		if(isValidMenu("ReceiptSP"))
		{
			addCaptionedItem("RECEIPT ENTRY ", transaction);
		}

		if(isValidMenu("ReceiptReturnSP"))
		{
		//	addCaptionedItem("RECEIPT RETURN ENTRY ", transaction);
		}

		if(isValidMenu("issueSP"))
		{
			addCaptionedItem("ISSUE ENTRY ", transaction);
		}
		
		if(isValidMenu("issueReturnSP"))
		{
			addCaptionedItem("ISSUE RETURN ", transaction);
		}

		if(isValidMenu("wastageSP"))
		{
			//addCaptionedItem("WASTAGE/BROKEN/EXPIRED ENTRY", transaction);
		}
		if(isValidMenu("StockSearchSP"))
		{
			addCaptionedItem("STOCK SEARCH ", transaction);
		}

	}

	private void addReportChild(Object report)
	{	

		if(isValidMenu("ItemInfo_SP")){
			ItemInfoSpare=addCaptionedItem("ITEM INFORMATION", report);
			addItemInfoChild(ItemInfoSpare);
		}
		if(isValidMenu("Requistion_SP")){
			RequistionSpare=addCaptionedItem("REQUISITION", report);
			addRequistionChild(RequistionSpare);
		}

		if(isValidMenu("Purchase_SP")){
			receiptSpare=addCaptionedItem("PURCHASE", report);
			addPurchaseChild(receiptSpare);
		}
		if(isValidMenu("Issue_SP")){
			IssueSpare=addCaptionedItem("ISSUE", report);
			addIssueChild(IssueSpare);
		}
		if(isValidMenu("IssueReturnReport_SP"))
		{
			IssuereturnreportSpare=addCaptionedItem("ISSUE RETURN", report);
			addIssueReturnChild(IssuereturnreportSpare);
		}

		if(isValidMenu("StockReport_SP")){
			StockReportSpare=addCaptionedItem("STOCK REPORT", report);
			addStockInfoChild(StockReportSpare);
		}
	}
	
	private void addItemInfoChild(Object ItemInfo){

		if(isValidMenu("itemInformationreportsp"))
		{
			addCaptionedItem("ITEM INFORMATION. ", ItemInfo);
		}
		if(isValidMenu("itemInformationAsOnDatereportsp"))
		{
			//addCaptionedItem("ITEM INFORMATION WITH STOCK ", ItemInfo);
		}
		if(isValidMenu("rptSlowmovingsp"))
     	{
			//addCaptionedItem("SLOWMOVING ITEM INFORMATION ", ItemInfo);
     	}
		if(isValidMenu("rptnonmovingsp"))
     	{
			//addCaptionedItem("NONMOVING ITEM INFORMATION ", ItemInfo);
     	}
	}
	private void addRequistionChild(Object Requistion){

		if(isValidMenu("spareRequisitionInfo"))
		{
			addCaptionedItem("REQUISITION. ", Requistion);
		}

		if(isValidMenu("spareRequisitionRegisterDepartmentWise"))
		{
			addCaptionedItem("DEPARTMENT WISE REQUISITION ", Requistion);
		}
	}
/*	
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
	}*/
	private void addPurchaseChild(Object Purchase)
	{
		if(isValidMenu("rptGoodReceiveNotesp"))
		{
			addCaptionedItem("GOOD RECEIVE NOTE ", Purchase);
		}
		if(isValidMenu("rptItemWisePurchaseRegistersp"))
		{
			addCaptionedItem("ITEM WISE PURCHASE STATEMENT ", Purchase);
		}
		if(isValidMenu("rptDateWisePurchaseStatementsp"))
		{
			addCaptionedItem("DATE WISE PURCHASE STATEMENT ", Purchase);
		}
		if(isValidMenu("rptSupplierWisePurchaseStatementsp"))
		{
			addCaptionedItem("SUPPLIER WISE PURCHASE STATEMENT ", Purchase);
		}
		if(isValidMenu("receivesummarysp"))
		{
			addCaptionedItem("RECEIVE SUMMARY DATE BETWEEN ", Purchase);
		}
	}
	private void addIssueChild(Object Issue)
	{
		if(isValidMenu("sectionwiseissueregistersp"))
		{
			addCaptionedItem("SECTION WISE ISSUE REGISTER ", Issue);
		}
		if(isValidMenu("sectionwiseissueSummarysp"))
		{
			addCaptionedItem("SECTION WISE ISSUE SUMMARY ", Issue);
		}
		/*if(isValidMenu("RptRequisitionProduction"))
		{
			addCaptionedItem("REQUISITION WISE ISSUE", Issue);
		}*/

		if(isValidMenu("dptreqWiseIssuesp"))
		{
			addCaptionedItem("REQUISITION WISE ISSUE(Department Wise) ", Issue);
		}
		if(isValidMenu("issueRegistersp"))
		{
			addCaptionedItem("ISSUE REGISTER ", Issue);
		}
		if(isValidMenu("AvgPerDayIssuesp"))
		{
			addCaptionedItem("AVERAGE PER DAY ISSUE ", Issue);
		}

		if(isValidMenu("IssueSummarysp"))
		{
			addCaptionedItem("ISSUE SUMMARY DATE BETWEEN ", Issue);
		}

	}

	private void addIssueReturnChild(Object IssueReturn)
	{
		if(isValidMenu("issuereturnregistersp"))
		{
			addCaptionedItem("ISSUE RETURN REGISTER ", IssueReturn);
		}
	}
	
	private void addStockInfoChild(Object StockReport){
		if(isValidMenu("openingstockreportsp"))
		{
			addCaptionedItem("ITEMS OPENING STOCK ", StockReport);
		}
		if(isValidMenu("itemwisestockregistersp"))
		{
			addCaptionedItem("ITEM WISE STOCK REGISTER ", StockReport);
		}
		if(isValidMenu("itemwisekardexsp"))
		{
			addCaptionedItem("ITEM WISE KARDEX ", StockReport);
		}

		if(isValidMenu("storeWiseStocksp"))
		{
			//addCaptionedItem("STORE WISE ITEM STOCK", StockReport);
		}
		if(isValidMenu("stocksummarysp"))
		{
			addCaptionedItem("STOCK SUMMARY WITH VALUE ", StockReport);
		}
		if(isValidMenu("stocksummaryWithoutValuesp"))
		{
			//addCaptionedItem("STOCK SUMMARY WITHOUT VALUE", StockReport);
		}
		if(isValidMenu("catWiseStockAsOnDatesp"))
		{
			addCaptionedItem("CATEGORY WISE STOCK AS ON DATE ", StockReport);
		}
		if(isValidMenu("catWiseStockDateBetweensp"))
		{
			addCaptionedItem("CATEGORY WISE STOCK DATE BETWEEN ", StockReport);
		}
		if(isValidMenu("rptminimumStocksp"))
		{
			addCaptionedItem("MINIMUM LEVEL STOCK STATEMENT ", StockReport);
		}
		if(isValidMenu("rptmaximumStocksp"))
		{
			addCaptionedItem("MAXIMUM LEVEL STOCK STATEMENT ", StockReport);
		}
		if(isValidMenu("rptReorderStocksp"))
		{
			addCaptionedItem("RE-ORDER LEVEL STOCK STATEMENT ", StockReport);
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

			/*	// RAW-METERIAL SETUP
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL SETUP"))
				{
					showWindow(new sparePartsSetup(sessionBean),event.getItem(),"sparePartsSetup");
				}
				// RAW-METERIAL TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL TRANSACTION"))
				{
					showWindow(new sparePartsTransaction(sessionBean),event.getItem(),"sparePartsTransaction");
				}
				// RAW-METERIAL REPORT
				if(event.getItem().toString().equalsIgnoreCase("RAW-METERIAL REPORT"))
				{
					showWindow(new sparePartsReport(sessionBean),event.getItem(),"sparePartsReport");
				}*/
//========================SETUP======================================================
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY "))
				{
					showWindow(new SpareItemCategory(sessionBean),event.getItem(),"itemCategorySP");
				}

				if(event.getItem().toString().equalsIgnoreCase("SUB CATEGORY "))
				{
					showWindow(new SpareItemSubCategory(sessionBean),event.getItem(),"itemSubCategorySP");
				}

				if(event.getItem().toString().equalsIgnoreCase("RAC INFORMATION"))
				{
					showWindow(new SpareRackInfo(sessionBean),event.getItem(),"rackInfoSP");
				}
				if(event.getItem().toString().equalsIgnoreCase("SUB RAC INFORMATION"))
				{
					showWindow(new SpareSubRackInfo(sessionBean),event.getItem(),"subRackInfoSP");
				}

				if(event.getItem().toString().equalsIgnoreCase("SPARE ITEM INFORMATION "))
				{
					showWindow(new SparePartsItemInfo(sessionBean),event.getItem(),"itemInformationSP");
				}
				
//========================TRANSACTION======================================================
				if(event.getItem().toString().equalsIgnoreCase("OPENING STOCK "))
				{
					showWindow(new SparePartsOpeningStock(sessionBean),event.getItem(),"openingStockSP");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION ENTRY "))
				{
					showWindow(new SparePartsRequsitionForHeadOffice(sessionBean),event.getItem(),"requisitionEntrySP");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION APPROVE"))
				{
					showWindow(new PurchaseOrderActiveInactiveInventory(sessionBean),event.getItem(),"requisitionApproveSP");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("RECEIPT ENTRY "))
				{
					showWindow(new SparePartsPurchaseReceipt(sessionBean),event.getItem(),"ReceiptSP");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("RECEIPT RETURN ENTRY "))
				{
				//	showWindow(new SparePartsPurchaseReceipt(sessionBean),event.getItem(),"ReceiptReturnSP");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("ISSUE ENTRY "))
				{
					showWindow(new SparePartsIssue(sessionBean),event.getItem(),"issueSP");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("ISSUE RETURN "))
				{
					showWindow(new SparePartsIssueReturn(sessionBean),event.getItem(),"issueReturnSP");
				}
				if(event.getItem().toString().equalsIgnoreCase("WASTAGE/BROKEN/EXPIRED ENTRY"))
				{
					//showWindow(new RawmaterialIssueReturn(sessionBean),event.getItem(),"wastageSP");
				}
			
				if(event.getItem().toString().equalsIgnoreCase("STOCK SEARCH "))
				{
					showWindow(new SparePartsStockSearch(sessionBean),event.getItem(),"StockSearchSP");
				}
			
	//========================REPORT======================================================		

				//==///////----------------ITEM INFORMATION.----------------------/////===//
				if(event.getItem().toString().equalsIgnoreCase("ITEM INFORMATION. "))
				{
					showWindow(new RptItemInformationSP0(sessionBean,""),event.getItem(),"itemInformationreportsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM INFORMATION WITH STOCK "))
				{
					showWindow(new RptItemInformationSPAsOnDate(sessionBean,""),event.getItem(),"itemInformationAsOnDatereportsp");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SLOWMOVING ITEM INFORMATION "))
				{
					//showWindow(new Rptslowmoving("", sessionBean),event.getItem(),"rptSlowmovingsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("NONMOVING ITEM INFORMATION "))
				{
					//showWindow(new RptNonmoving("", sessionBean),event.getItem(),"rptnonmovingsp");
				}
				
				//==///////----------------REQUISITION INFORMATION.----------------------/////===//
				
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION. "))
				{
					showWindow(new RawRequisitionForm(sessionBean,""),event.getItem(),"spareRequisitionInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("DEPARTMENT WISE REQUISITION "))
				{
					showWindow(new RawRequisitionRegisterDepartmentWise(sessionBean,""),event.getItem(),"spareRequisitionRegisterDepartmentWise");
				}
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION REGISTER "))
				{
					showWindow(new RawRequisitionRegister(sessionBean,""),event.getItem(),"spareRequisitionRegister");
				}
				
				//==///////----------------RECEIPT INFORMATION.----------------------/////===//
				
				if(event.getItem().toString().equalsIgnoreCase("GOOD RECEIVE NOTE "))
				{
					showWindow(new GoodsReceiveNote(sessionBean,"rptGoodReceiveNotesp"),event.getItem(),"rptGoodReceiveNotesp");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE PURCHASE STATEMENT "))
				{
					showWindow(new ItemWisePurcaseRegisterSPRpt(sessionBean,"rptItemWisePurchaseRegistersp"),event.getItem(),"rptItemWisePurchaseRegistersp");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE PURCHASE STATEMENT "))
				{
					showWindow(new DateWisePurchaseSPRpt(sessionBean,"rptDateWisePurchaseStatementsp"),event.getItem(),"rptDateWisePurchaseStatementsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("SUPPLIER WISE PURCHASE STATEMENT "))
				{
					showWindow(new SupplierWisePurchaseRpt(sessionBean,"rptSupplierWisePurchaseStatementsp"),event.getItem(),"rptSupplierWisePurchaseStatementsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("RECEIVE SUMMARY DATE BETWEEN "))
				{
					showWindow(new ReceiveSummarySP(sessionBean, ""),event.getItem(),"receivesummarysp");
				}


				//==///////----------------ISSUE INFORMATION.----------------------/////===//
				
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ISSUE REGISTER "))
				{
					showWindow(new RptDeptWiseIssueNewSP(sessionBean,""),event.getItem(),"sectionwiseissueregistersp");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECTION WISE ISSUE SUMMARY "))
				{
					showWindow(new DeptWiseIssueSummarySP(sessionBean,""),event.getItem(),"sectionwiseissueSummarysp");
				}
				/*if(event.getItem().toString().equalsIgnoreCase("REQUISITION WISE ISSUE"))
				{
					showWindow(new RptRequisitionProduction(sessionBean,""),event.getItem(),"RptRequisitionProduction");
				}*/
				if(event.getItem().toString().equalsIgnoreCase("REQUISITION WISE ISSUE(Department Wise) "))
				{
					showWindow(new DpetWiseRqIssue(sessionBean,""),event.getItem(),"dptreqWiseIssuesp");
				}
				if(event.getItem().toString().equalsIgnoreCase("ISSUE REGISTER "))
				{
					showWindow(new RptIssueRegisterSP(sessionBean,""),event.getItem(),"issueRegistersp");
				}

				if(event.getItem().toString().equalsIgnoreCase("AVERAGE PER DAY ISSUE "))
				{
					showWindow(new RptAvgPerDayIssueSP(sessionBean,""),event.getItem(),"AvgPerDayIssuesp");
				}
		
				if(event.getItem().toString().equalsIgnoreCase("ISSUE SUMMARY DATE BETWEEN "))
				{
					showWindow(new IssueSummarySP(sessionBean,""),event.getItem(),"IssueSummarysp");
				}

				//==///////----------------ISSUE RETURN INFORMATION REPORT----------------------/////===//
				
				
				if(event.getItem().toString().equalsIgnoreCase("ISSUE RETURN REGISTER "))
				{
					showWindow(new RptIssueReturnRegister(sessionBean,""),event.getItem(),"issuereturnregistersp");
				}
				
				//==///////----------------STOCK REPORT----------------------/////===//
				
				if(event.getItem().toString().equalsIgnoreCase("ITEMS OPENING STOCK "))
				{
					showWindow(new RptOpeningStockSP(sessionBean,""),event.getItem(),"openingstockreportsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE STOCK REGISTER "))
				{
					showWindow(new RptProductWiseStockRegister(sessionBean,""),event.getItem(),"itemwisestockregistersp");
				}
				if(event.getItem().toString().equalsIgnoreCase("ITEM WISE KARDEX "))
				{
					showWindow(new RptItemWiseKardexSP(sessionBean,""),event.getItem(),"itemwisekardexsp");
				}
				if(event.getItem().toString().equalsIgnoreCase("STOCK SUMMARY WITH VALUE "))
				{
					showWindow(new StockReportSP(sessionBean,""),event.getItem(),"stocksummarysp");
				}
				if(event.getItem().toString().equalsIgnoreCase("MINIMUM LEVEL STOCK STATEMENT "))
				{
					showWindow(new RptMinimumLevelStockSP(sessionBean,""),event.getItem(),"rptminimumStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("MAXIMUM LEVEL STOCK STATEMENT "))
				{
					showWindow(new RptMaximumLevelStockSP(sessionBean,""),event.getItem(),"rptmaximumStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("RE-ORDER LEVEL STOCK STATEMENT "))
				{
					showWindow(new RptLowerLevelStockSP(sessionBean,""),event.getItem(),"rptReorderStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY WISE STOCK AS ON DATE "))
				{
					showWindow(new RptCateWiseStockSPAsOnDate(sessionBean,""),event.getItem(),"catWiseStockAsOnDate");
				}
				if(event.getItem().toString().equalsIgnoreCase("CATEGORY WISE STOCK DATE BETWEEN "))
				{
					showWindow(new RptCateWiseStockSPDateBetween(sessionBean,""),event.getItem(),"catWiseStockDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("STORE WISE ITEM STOCK "))
				{
					showWindow(new RptStoreWiseStock(sessionBean,""),event.getItem(),"storeWiseStock");
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
