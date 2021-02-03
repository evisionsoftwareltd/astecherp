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
import com.example.productionReport.RptProductionStep;
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

public class CostingMenu {

	Tree tree;
	SessionBean sessionBean;
	Component component;

	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();

	Object transaction = null;
	Object reports = null;

	private static final Object CAPTION_PROPERTY = "caption";

	public CostingMenu(Object lcLoanModule, Tree tree, SessionBean sessionBean,Component component) 
	{	
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("costingTransaction"))
		{
			transaction = addCaptionedItem("COSTING TRANSACTION", lcLoanModule);
			addTransectionChild(transaction);
		}

		if(isValidMenu("costingReports"))
		{
			reports = addCaptionedItem("COSTING REPORTS", lcLoanModule);
			addReportsChild(reports);
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
