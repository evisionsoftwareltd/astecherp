package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.FinishedGoodsModule.FinishedGoodsGroupInformation;
import acc.appform.FinishedGoodsModule.FinishedGoodsInformation;
import acc.appform.FinishedGoodsModule.MasterProductOpeningStock;

import acc.appform.FinishedGoodsModule.IngridiantDeliveryReport;
import acc.appform.FinishedGoodsModule.MouldInfo;
import acc.appform.FinishedGoodsModule.ProductInformation;
import acc.appform.FinishedGoodsModule.RptAssembleProductOpeningStockEditLog;
import acc.appform.FinishedGoodsModule.RptFinishedGoodsOpeningEditLog;
import acc.appform.FinishedGoodsModule.RptFinishedGoodsStockReport;
import acc.appform.FinishedGoodsModule.RptIncompletemasterproductinfo;
import acc.appform.FinishedGoodsModule.RptItemInformation3rd0;
import acc.appform.FinishedGoodsModule.RptReadyforSalesProductStockLedger;
import acc.appform.FinishedGoodsModule.RptThirdPartyIntemReceipt;
import acc.appform.FinishedGoodsModule.RptThirdPartyItemWiseStockLedger;
import acc.appform.FinishedGoodsModule.RptThirtPartyItemDelivery;
import acc.appform.FinishedGoodsModule.SemiFgInformation;
import acc.appform.FinishedGoodsModule.SemiFgSubInformation;
import acc.appform.FinishedGoodsModule.SourceInformation;
import acc.appform.FinishedGoodsModule.ThirdPartyItemInfo;
import acc.appform.FinishedGoodsModule.ThirdPartyItemOpeningStock;
import acc.appform.FinishedGoodsModule.ThirdPartyItemReceiptInfo;
import acc.appform.FinishedGoodsModule.finishGoodsReport;
import acc.appform.FinishedGoodsModule.finishGoodsSetup;
import acc.appform.FinishedGoodsModule.finishGoodsTransaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.example.productionReport.RptFgOpening;
import com.example.productionReport.RptFinishedGoodsFormulation;
import com.example.productionReport.RptBottleCost;
import com.example.productionReport.RptConvertionCostPartyAndFgWise;
import com.example.productionReport.RptFinishedGoodsInformation;
import com.example.productionReport.RptInkFormulation;
import com.example.productionReport.RptLabelCost;
import com.example.productionReport.RptMasterProductOpening;
import com.example.productionReport.RptMasterProductWithMultipleSecondaryFG;
import com.example.productionReport.RptMouldingInformation;
import com.example.productionReport.RptRmConsumptionFGWise;
import com.example.productionReport.RptRmConsumptionFGWiseRmWise;
import com.example.productionReport.RptSecOpeningStock;
import com.example.productionReport.RptSemiFgOpening;
import com.example.productionReport.RptSemiFinishedGoodsInformation;
import com.example.productionReport.RptSemiFinishedGoodsSubInformation;
import com.example.productionReport.RptTubeCogpStandard;
import com.example.productionSetup.ColorFormulation;
import com.example.productionSetup.CostSheet;
import com.example.productionSetup.FgOpening;
import com.example.productionSetup.FinishedStandard;
import com.example.productionSetup.InkFormulation;
import com.example.productionSetup.SemiFgOpeningStock;

import com.reportform.FinishedGoodsModule.RptCategoryAndSubCategoryWiseProductList;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class FinishedGoodsMenu 
{	
	private HashMap winMap = new HashMap();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	public FinishedGoodsMenu(Object transactionModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		Object setup = null;
		Object transaction = null;
		Object thirdPLabel= null;
		Object reports = null;

		treeAction();

		if(isValidMenu("finishGoodsSetup"))
		{
			setup = addCaptionedItem("FINISHED GOODS SETUP", transactionModule);
			addSetupChild(setup);
		}

		if(isValidMenu("thirdPartyLabel"))
		{
			thirdPLabel = addCaptionedItem("THIRD PARTY LABEL", transactionModule);
			addthirdPLabel(thirdPLabel);
		}
		/*if(isValidMenu("finishGoodsTransaction"))
		{
			transaction = addCaptionedItem("FINISHED GOODS TRANSACTION", transactionModule);
			addTransectionChild(transaction);
		}*/

		if(isValidMenu("finishGoodsReport"))
		{
			reports = addCaptionedItem("FINISHED GOODS REPORT", transactionModule);
			addReportsChild(reports);
		}
		
	}

	private void addSetupChild(Object setup)
	{
		/*if(isValidMenu("catInformation"))
		{
			addCaptionedItem("CATEGORY INFORMATION", setup);
		}

		if(isValidMenu("SubcategoryInformation"))
		{
			addCaptionedItem("SUB-CATEGORY INFORMATION", setup);
		}

		if(isValidMenu("brandInformation"))
		{
			addCaptionedItem("BRAND INFORMATION", setup);
		}

		if(isValidMenu("designInformation"))
		{
			addCaptionedItem("DESIGN INFORMATION", setup);
		}

		if(isValidMenu("colorInformation"))
		{
			addCaptionedItem("COLOR INFORMATION", setup);
		}

		if(isValidMenu("sizeInformation"))
		{
			addCaptionedItem("SIZE INFORMATION", setup);
		}*/
		if(isValidMenu("mouldinformation"))
		{
	         addCaptionedItem("MOULD INFORMATION", setup);
		}
		if(isValidMenu("semifinishGoodsInformation"))
		{
			addCaptionedItem("SEMI FINISHED GOODS INFORMATION", setup);
		}
		
		if(isValidMenu("semifinishGoodsSubInformation"))
		{
			addCaptionedItem("SECANDARY/FINISHED GOODS(PRINTING/LABELING/CAP/SBM/SHRINK) INFORMATION", setup);
		}
		
		/*if(isValidMenu("finishGoodsInformation"))
		{
			addCaptionedItem("FINISH GOODS INFORMATION", setup);
		}*/
		
		if(isValidMenu("finishGoodsInformation"))
		{
			addCaptionedItem("MASTER PRODUCT INFORMATION", setup);
		}
		/*if(isValidMenu("finishedGoodsGroup"))
		{
			addCaptionedItem("FINISH GOODS GROUP INFORMATION", setup);
		}*/
		if(isValidMenu("FinishedGoodsStandard"))
		{
			addCaptionedItem("PRODUCT FORMULATION", setup);
		}
		if(isValidMenu("INKFORMULATION"))
		{
			addCaptionedItem("INK FORMULATION", setup);
		}
		
		if(isValidMenu("semifgOpening"))
		{
			 addCaptionedItem("SEMI FINISHED GOODS OPENING STOCK", setup);
			
		}
		
		if(isValidMenu("fgOpening"))
		{
			 addCaptionedItem("SECANDARY/FINISHED GOODS OPENING STOCK[PRINTING/LABELING/CAP/SBM/SHRINK]", setup);
			
		}
		if(isValidMenu("masterProuctOpening"))
		{
			 addCaptionedItem("ASSEMBLE PRODUCT OPENING STOCK", setup);
			
		}
		
		if(isValidMenu("costSheet"))
		{
			//addCaptionedItem("COST SHEET", setup);
		}
		/*if(isValidMenu("colorFormulation"))
		{
			 addCaptionedItem("COLOR FORMULATION", setup);
			
		}*/
	}

	private void addthirdPLabel(Object thirdPLabel)
	{
		
		/////////////////// THIRD PARTY 
		if(isValidMenu("thirdPartySourceInfo"))
		{
			 addCaptionedItem("SOURCE INFORMATION", thirdPLabel);
		}
		if(isValidMenu("thirdpartyItemInformation"))
		{
			 addCaptionedItem("THIRD PARTY LABEL INFORMATION", thirdPLabel);
		}
		
		if(isValidMenu("thirdPartyOpeningStock"))
		{
			 addCaptionedItem("THIRD PARTY LABEL OPENING STOCK", thirdPLabel);
		}
		if(isValidMenu("thirdPartyItemReceipInfo"))
		{
			 addCaptionedItem("THIRD PARTY LABEL RECEIPT", thirdPLabel);
		}
		///////////////////
		
	
			
		
	}

	private void addTransectionChild(Object transection)
	{

	}


	private void addReportsChild(Object reports)
	{
		if(isValidMenu("mouldInfo"))
		{
			addCaptionedItem("MOULD INFORMATION ", reports);
		
		}
		if(isValidMenu("semifinishGoodsInformation"))
		{
			addCaptionedItem("SEMI FINISHED GOODS INFORMATION.", reports);
		}
		if(isValidMenu("semifinishGoodsSubInformation"))
		{
			addCaptionedItem("FINISHED GOODS[LABEL/PRINTING/CAP/SBM] INFORMATION.", reports);
		}
		if(isValidMenu("ProductList1"))
		{
			addCaptionedItem("MASTER PRODUCT INFORMATION.", reports);
		}
		if(isValidMenu("MasterProductwithMultipleSecondaryFG"))
		{
			addCaptionedItem("MASTER PRODUCT WITH MULTIPLE SECONDARY FG", reports);
		}

		if(isValidMenu("finishedGoodsFormulation"))
		{
			addCaptionedItem("PRODUCT FORMULATION.", reports);
		}
		if(isValidMenu("InkFormulationReport"))
		{
			addCaptionedItem("INK FORMULATION REPORT", reports);
		}
		if(isValidMenu("semiFgOpeningStock"))
		{
			addCaptionedItem("SEMI FINISHED GOODS OPENING STOCK.", reports);
		}
		if(isValidMenu("FgOpeningStock"))
		{
			addCaptionedItem("FINISHED GOODS OPENING STOCK [PRINTING/LABELING/CAP/SBM].", reports);
		}
		if(isValidMenu("ReadyforSaleProduct"))
		{
			addCaptionedItem("ASSEMBLE OPENING", reports);
		}
		
		if(isValidMenu("RptSectionOpeningStock"))
		{
			addCaptionedItem("SECTION OPENING", reports);
		}
		
		
		if(isValidMenu("InkFormulationReport"))
		{
			addCaptionedItem("INK FORMULATION REPORT", reports);
		}
		if(isValidMenu("ProductList"))
		{
			addCaptionedItem("FINISHED GOODS LIST", reports);
		}
		if(isValidMenu("labelCost"))
		{
			//addCaptionedItem("COST REPORT", reports);
		
		}
		
		
		/*if(isValidMenu("bottleCost"))
		{
			addCaptionedItem("BOTTLE COST REPORT", reports);
		
		}*/
		if(isValidMenu("COGPRMCONSUMPTIONFGWISE"))
		{
			//addCaptionedItem("FINISHED PRODUCT WITH CONSUMPTION", reports);
		
		}
		if(isValidMenu("COGPRMCONSUMPTIONFGAndRmWise"))
		{
			//addCaptionedItem("FINISHED PRODUCT WITH CONSUMPTION RM WISE", reports);
		
		}
		if(isValidMenu("CONVERTINCOSTPARTYANDFGWISE"))
		{
			//addCaptionedItem("CONVERTION COST PARTY AND FG WISE", reports);
		
		}
		if(isValidMenu("COGPSTANDARD"))
		{
			//addCaptionedItem("COGP STANDARD", reports);
		}
		
		if(isValidMenu("itemInformation3rdreport"))
		{
			addCaptionedItem("THIRD PARTY ITEM INFORMATION REPORT", reports);
		}

		if(isValidMenu("thirdPartyItemReceipt"))
		{
			addCaptionedItem("THIRD PARTY ITEM RECEIPT REPORT", reports);
		}
		
		if(isValidMenu("thirdPartyItemDeliveryReport"))
		{
			addCaptionedItem("THIRD PARTY DELIVERY REPORT", reports);
		}
		
		if(isValidMenu("thirdPartyStockLedger"))
		{
			addCaptionedItem("THIRD PARTY STOCK LEDGER", reports);
		}
		
		if(isValidMenu("incompletemasterproductinfo"))
		{
			addCaptionedItem("INCOMPLETE MASTER PRODUCT INFORMATION", reports);
		}
		if(isValidMenu("RptFinishedGoodStockLedger"))
		{
			addCaptionedItem("FINISHED GOOD STOCK LEDGER", reports);
		}
		
		
		if(isValidMenu("RptReadyforSalesProductStockLedger"))
		{
			addCaptionedItem("READY FOR SALES STOCK DETAILS", reports);
		}
		
		if(isValidMenu("RpingridiantDeliveryReport"))
		{
			addCaptionedItem("INGRIDIANT DELIVERY REPORT", reports);
		}
		if(isValidMenu("fgOpeningEditLog"))
		{
			addCaptionedItem("FINISHED GOODS OPENING EDIT LOG", reports);
		}
		if(isValidMenu("ASSEMBLEOpeningEditLog"))
		{
			addCaptionedItem("ASSEMBLE PRODUCT OPENING STOCK EDIT LOG", reports);
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

				// FINISH GOODS SETUP
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS SETUP"))
				{
					showWindow(new finishGoodsSetup(sessionBean),event.getItem(),"finishGoodsSetup");
				}
				// FINISH GOODS TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS TRANSACTION"))
				{
					showWindow(new finishGoodsTransaction(sessionBean),event.getItem(),"finishGoodsTransaction");
				}
				// FINISH GOODS REPORT
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS REPORT"))
				{
					showWindow(new finishGoodsReport(sessionBean),event.getItem(),"finishGoodsReport");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECANDARY/FINISHED GOODS OPENING STOCK[PRINTING/LABELING/CAP/SBM/SHRINK]"))
				{
					showWindow( new FgOpening (sessionBean),event.getItem(),"fgOpening");
				}
				if(event.getItem().toString().equalsIgnoreCase("ASSEMBLE PRODUCT OPENING STOCK"))
				{
					showWindow( new MasterProductOpeningStock(sessionBean),event.getItem(),"masterProuctOpening");
				}
				if(event.getItem().toString().equalsIgnoreCase("COLOR FORMULATION"))
				{
					showWindow( new ColorFormulation (sessionBean),event.getItem(),"colorFormulation");
				}
				// PRODUCTION INFORMATION
				if(event.getItem().toString().equalsIgnoreCase("SEMI FINISHED GOODS INFORMATION"))
				{
					showWindow(new SemiFgInformation(sessionBean),event.getItem(),"semifinishGoodsInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("SECANDARY/FINISHED GOODS(PRINTING/LABELING/CAP/SBM/SHRINK) INFORMATION"))
				{
					showWindow(new SemiFgSubInformation(sessionBean),event.getItem(),"semifinishGoodsSubInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("MOULD INFORMATION"))
				{
				  showWindow(new MouldInfo(sessionBean),event.getItem(),"mouldinformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("MASTER PRODUCT INFORMATION"))
				{
					showWindow(new FinishedGoodsInformation(sessionBean),event.getItem(),"finishGoodsInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("FINISH GOODS GROUP INFORMATION"))
				{
					showWindow(new FinishedGoodsGroupInformation(sessionBean),event.getItem(),"finishGoodsGroupInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("PRODUCT FORMULATION"))
				{
					showWindow(new FinishedStandard(sessionBean),event.getItem(),"FinishedGoodsStandard");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INK FORMULATION REPORT"))
				{
					showWindow(new RptInkFormulation(sessionBean,""),event.getItem(),"InkFormulationReport");
				}
				if(event.getItem().toString().equalsIgnoreCase("INK FORMULATION"))
				{
					showWindow(new InkFormulation(sessionBean),event.getItem(),"INKFORMULATION");
				}
				if(event.getItem().toString().equalsIgnoreCase("COST SHEET"))
				{
					showWindow(new CostSheet(sessionBean),event.getItem(),"costSheet");
				}
				// PRODUCTION INFORMATION REPORTS

				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS LIST"))
				{
					showWindow(new RptCategoryAndSubCategoryWiseProductList(sessionBean),event.getItem(),"ProductList");
				}	
				if(event.getItem().toString().equalsIgnoreCase("PRODUCT FORMULATION."))
				{
					showWindow(new RptFinishedGoodsFormulation(sessionBean,""),event.getItem(),"finishedGoodsFormulation");
				}
				if(event.getItem().toString().equalsIgnoreCase("COST REPORT"))
				{
					showWindow(new RptLabelCost(sessionBean,""),event.getItem(),"labelCost");
				}
				if(event.getItem().toString().equalsIgnoreCase("COGP STANDARD"))
				{
					showWindow(new RptTubeCogpStandard(sessionBean,""),event.getItem(),"COGPSTANDARD");
				}
				if(event.getItem().toString().equalsIgnoreCase("FINISHED PRODUCT WITH CONSUMPTION"))
				{
					showWindow(new RptRmConsumptionFGWise(sessionBean,""),event.getItem(),"COGPRMCONSUMPTIONFGWISE");
				}
				if(event.getItem().toString().equalsIgnoreCase("FINISHED PRODUCT WITH CONSUMPTION RM WISE"))
				{
					showWindow(new RptRmConsumptionFGWiseRmWise(sessionBean,""),event.getItem(),"COGPRMCONSUMPTIONFGAndRmWise");
				}
				if(event.getItem().toString().equalsIgnoreCase("CONVERTION COST PARTY AND FG WISE"))
				{
					showWindow(new RptConvertionCostPartyAndFgWise(sessionBean,""),event.getItem(),"CONVERTINCOSTPARTYANDFGWISE");
				}
				if(event.getItem().toString().equalsIgnoreCase("MASTER PRODUCT INFORMATION."))
				{
					showWindow(new RptFinishedGoodsInformation(sessionBean,""),event.getItem(),"ProductList1");
				}
				if(event.getItem().toString().equalsIgnoreCase("MASTER PRODUCT WITH MULTIPLE SECONDARY FG"))
				{
					showWindow(new RptMasterProductWithMultipleSecondaryFG(sessionBean,""),event.getItem(),"MasterProductwithMultipleSecondaryFG");
				}
				if(event.getItem().toString().equalsIgnoreCase("SEMI FINISHED GOODS INFORMATION."))
				{
					showWindow(new RptSemiFinishedGoodsInformation(sessionBean),event.getItem(),"semifinishGoodsInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS[LABEL/PRINTING/CAP/SBM] INFORMATION."))
				{
					showWindow(new RptSemiFinishedGoodsSubInformation(sessionBean),event.getItem(),"semifinishGoodsSubInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("MOULD INFORMATION "))
				{
					showWindow(new RptMouldingInformation(sessionBean),event.getItem(),"mouldInfo");
				}	
				//SemiFgOpeningStock
				
				if(event.getItem().toString().equalsIgnoreCase("SEMI FINISHED GOODS OPENING STOCK"))
				{
					showWindow(new SemiFgOpeningStock(sessionBean),event.getItem(),"semiFgOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("SEMI FINISHED GOODS OPENING STOCK."))
				{
					showWindow(new RptSemiFgOpening(sessionBean,""),event.getItem(),"FgOpeningStock");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS OPENING STOCK [PRINTING/LABELING/CAP/SBM]."))
				{
					showWindow(new RptFgOpening(sessionBean,""),event.getItem(),"mouldInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("ASSEMBLE OPENING"))
				{
					showWindow(new RptMasterProductOpening(sessionBean,""),event.getItem(),"ReadyforSaleProduct");
				}
				////////////////////Third Party Info
				if(event.getItem().toString().equalsIgnoreCase("SOURCE INFORMATION"))
				{
					showWindow(new SourceInformation(sessionBean),event.getItem(),"thirdPartySourceInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY LABEL INFORMATION"))
				{
					showWindow(new ThirdPartyItemInfo(sessionBean),event.getItem(),"thirdpartyItemInformation");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY LABEL OPENING STOCK"))
				{
					showWindow(new ThirdPartyItemOpeningStock(sessionBean),event.getItem(),"thirdPartyOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY LABEL RECEIPT"))
				{
					showWindow(new ThirdPartyItemReceiptInfo(sessionBean),event.getItem(),"thirdPartyItemReceipInfo");
				}
				
				//////////////Report Third Party
				
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY ITEM INFORMATION REPORT"))
				{
					showWindow(new RptItemInformation3rd0(sessionBean,""),event.getItem(),"itemInformation3rdreport");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY ITEM RECEIPT REPORT"))
				{
					showWindow(new RptThirdPartyIntemReceipt(sessionBean,""),event.getItem(),"thirdPartyItemReceipt");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY DELIVERY REPORT"))
				{
					showWindow(new RptThirtPartyItemDelivery(sessionBean,""),event.getItem(),"thirdPartyItemDeliveryReport");
				}
				
			
				if(event.getItem().toString().equalsIgnoreCase("THIRD PARTY STOCK LEDGER"))
				{
					showWindow(new RptThirdPartyItemWiseStockLedger(sessionBean,""),event.getItem(),"thirdPartyStockLedger");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("INCOMPLETE MASTER PRODUCT INFORMATION"))
				{
					showWindow(new RptIncompletemasterproductinfo(sessionBean,""),event.getItem(),"incompletemasterproductinfo");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOOD STOCK LEDGER"))
				{
					showWindow(new RptFinishedGoodsStockReport(sessionBean,""),event.getItem(),"RptFinishedGoodStockLedger");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("READY FOR SALES STOCK DETAILS"))
				{
					showWindow(new RptReadyforSalesProductStockLedger(sessionBean,""),event.getItem(),"RptReadyforSalesProductStockLedger");
				}
				if(event.getItem().toString().equalsIgnoreCase("INGRIDIANT DELIVERY REPORT"))
				{
					showWindow(new IngridiantDeliveryReport(sessionBean,""),event.getItem(),"RpingridiantDeliveryReport");
				}
				
				
				/*if(isValidMenu("RptSectionOpeningStock"))
				{
					addCaptionedItem("SECTION OPENING", reports);
				}*/
				
				if(event.getItem().toString().equalsIgnoreCase("SECTION OPENING"))
				{
					showWindow(new RptSecOpeningStock(sessionBean,""),event.getItem(),"RptSectionOpeningStock");
				}
				if(event.getItem().toString().equalsIgnoreCase("FINISHED GOODS OPENING EDIT LOG"))
				{
					showWindow(new RptFinishedGoodsOpeningEditLog(sessionBean,""),event.getItem(),"fgOpeningEditLog");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("ASSEMBLE PRODUCT OPENING STOCK EDIT LOG"))
				{
					showWindow(new RptAssembleProductOpeningStockEditLog(sessionBean,""),event.getItem(),"ASSEMBLEOpeningEditLog");
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
