package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.LcModule.LcCharge;
import acc.appform.LcModule.LcChargeEntryMulti;
import acc.appform.LcModule.LcChargeHeadEntry;
import acc.appform.LcModule.LcCloseing;
import acc.appform.LcModule.LcInformation;
import acc.appform.LcModule.LtrInformation;
import acc.appform.LcModule.accessReports;
import acc.appform.LcModule.accessTransaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.CostingReport.COGPBetweenDate;
import com.example.CostingReport.RptConversionCostEntry;
import com.example.CostingReport.RptLedgerMapping;
import com.example.CostingTransaction.ConversionCostEntry;
import com.example.CostingTransaction.ConversionCostHeadWiseLedgerMapping;
import com.example.CostingTransaction.ConvertionCostHeadInfo;
import com.example.CostingTransaction.DailyProductionEntryLabelingPrintingTest;
import com.example.CostingTransaction.ProductionStepSelection;
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
import com.example.productionReport.RptProductionStep;
import com.example.thirdpartyTransaction.ThirdPartyRMIssue;
import com.example.thirdpartyTransaction.ThirdPartyRMOpeningStock;
import com.example.thirdpartyTransaction.ThirdPartyReceipt;
import com.reportform.lcReport.RptLcInfo;
import com.reportform.lcReport.RptLcRegister;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;



public class CrashingMenu {

	Tree tree;
	SessionBean sessionBean;
	Component component;

	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();

	Object transaction = null;
	Object reports = null;
	
	Object thirdPartyModule=null;
	Object thirdpartysetup=null;
	Object thirdpartyTransaction=null;
	Object thirpartyReport=null;
	Object crashingModule=null;
	Object crashingModuleSteup=null;
	Object crashingModuleTransaction=null;
	Object crashingModuleReport=null;

	private static final Object CAPTION_PROPERTY = "caption";

	public CrashingMenu(Object crashingModule, Tree tree, SessionBean sessionBean,Component component) 
	{	
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

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
	
	
	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				System.out.println(event.getItemId()+" "+event.getItem());

				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION STEP SELECTION"))
				{
					showWindow(new ProductionStepSelection(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("CONVERSION COST HEAD INFO."))
				{
					showWindow(new ConvertionCostHeadInfo(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("CONVERSION COST HEAD WISE LEDGER MAPPING"))
				{
					showWindow(new ConversionCostHeadWiseLedgerMapping(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("CONVERSION COST ENTRY"))
				{
					showWindow(new ConversionCostEntry(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("COGP BETWEEN DATE"))
				{
					showWindow(new COGPBetweenDate(sessionBean),event.getItem(),"CogpDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("HEAD WISE LEDGER MAPPING"))
				{
					showWindow(new RptLedgerMapping(sessionBean,""),event.getItem(),"LedgerMappingRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("Printing Test Entry"))
				{
					showWindow(new DailyProductionEntryLabelingPrintingTest(sessionBean,"",0),event.getItem(),"CogpDateBetween");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCTION STEP SELECTION."))
				{
					//showWindow(new MachineWiseProduction1(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
					showWindow(new RptProductionStep(sessionBean,""),event.getItem(),"productionStep");
				}
				if(event.getItem().toString().equalsIgnoreCase("CONVERSION COST"))
				{
					//showWindow(new MachineWiseProduction1(sessionBean,""),event.getItem(),"mouldingproductionGroupIssue");
					showWindow(new RptConversionCostEntry(sessionBean),event.getItem(),"CostEntry");
				}
				
				
//THIRD PARTY R/M INFORMATION
				
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
				
				
				
			}
		});
	}
	private void addTransectionChild(Object transection)
	{
		if(isValidMenu("headInfo"))
		{
			addCaptionedItem("CONVERSION COST HEAD INFO.", transection);
		}
		if(isValidMenu("LedgerMapping"))
		{
			addCaptionedItem("CONVERSION COST HEAD WISE LEDGER MAPPING", transection);
		}
		if(isValidMenu("stepSelection"))
		{
			addCaptionedItem("PRODUCTION STEP SELECTION", transection);
		}
		if(isValidMenu("ConversionCostEntry"))
		{
			addCaptionedItem("CONVERSION COST ENTRY", transection);
		}
		/*if(isValidMenu("ConversionCostEntry"))
		{
			addCaptionedItem("Printing Test Entry", transection);
		}*/
	}
	private void addReportsChild(Object report)
	{
		if(isValidMenu("productionStep"))
		{
			addCaptionedItem("PRODUCTION STEP SELECTION.", report);
		}
		if(isValidMenu("LedgerMappingRpt"))
		{
			addCaptionedItem("HEAD WISE LEDGER MAPPING", report);
		}
		if(isValidMenu("CostEntry"))
		{
			addCaptionedItem("CONVERSION COST", report);
		}
		if(isValidMenu("CogpDateBetween"))
		{
			addCaptionedItem("COGP BETWEEN DATE", report);
		}
		
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
	// check is valid menu for add menu bar
		private boolean isValidMenu(String id)
		{	
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				 session.beginTransaction();

				System.out.println(id+" "+sessionBean.getUserId());

				System.out.println("SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
						"' AND menuId = '"+id+"'");

				Iterator<?> iter = session.createSQLQuery("SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
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
}
