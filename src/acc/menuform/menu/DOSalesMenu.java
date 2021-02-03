package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.DoSalesModule.CancelTheDeliveryChallan;
import acc.appform.DoSalesModule.DeliveryChallan;
import acc.appform.DoSalesModule.DemandOrder;
import acc.appform.DoSalesModule.AuthorityApprove;
import acc.appform.DoSalesModule.FgDeliveryChallanInvoiceDelete;
import acc.appform.DoSalesModule.PurchaseOrderActiveInactive;
import acc.appform.DoSalesModule.ReturnInvoice;
import acc.appform.DoSalesModule.SalesInvoice;
import acc.appform.DoSalesModule.accessDoReports;
import acc.appform.DoSalesModule.accessDoTransaction;
import acc.appform.DoSalesModule.PurchaseOrderCancel;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.reportform.DoSalesModule.RptChallan;
import com.reportform.DoSalesModule.RptDateAndPartyWiseWiseDeliveryChallan;
import com.reportform.DoSalesModule.RptDateWiseSalesSummary;
import com.reportform.DoSalesModule.RptDeliveryChallanDeleteStatement;
import com.reportform.DoSalesModule.RptDeliveryChallanInvoiceDeleteStatement;
import com.reportform.DoSalesModule.RptDemandOrderInfo;
import com.reportform.DoSalesModule.RptDestinationWiseDeliveryChallan;
import com.reportform.DoSalesModule.RptGatePass;
import com.reportform.DoSalesModule.RptMonthlySalesSummary;
import com.reportform.DoSalesModule.RptPurchaseOrderCancel;
import com.reportform.DoSalesModule.RptSRwiseDo;
import com.reportform.DoSalesModule.RptSalesInvoiceReport;
import com.reportform.DoSalesModule.RptSalesInvoiceReportPaidUnpaid;
import com.reportform.DoSalesModule.RptYearlySalesSummary;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class DOSalesMenu 
{	
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object transaction = null;
	Object reports = null;

	public DOSalesMenu(Object transactionModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;
		treeAction();
		if(isValidMenu("doSalesTransaction"))
		{
			transaction = addCaptionedItem("PO & SALES TRANSACTION", transactionModule);
			addTransectionChild(transaction);
		}
		if(isValidMenu("doSalesReports"))
		{
			reports = addCaptionedItem("PO & SALES REPORTS", transactionModule);
			addReportsChild(reports);
		}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = tree.addItem();
		final Item item = tree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);
		p.setValue(caption);
		if(parent != null)
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}
		return id;
	}

	private void addTransectionChild(Object transection)
	{
		if(isValidMenu("demand"))
		{
			addCaptionedItem("PURCHASE ORDER ", transection);
		}
		if(isValidMenu("demandApprove"))
		{
			addCaptionedItem("PURCHASE ORDER APPROVE", transection);
		}
		
		if(isValidMenu("sales"))
		{
			addCaptionedItem("SALES INVOICE", transection);
		}
		if(isValidMenu("ReturnInvoice"))
		{
			addCaptionedItem("RETURN INVOICE", transection);
		}
		if(isValidMenu("purchaseOrderCancel"))
		{
			addCaptionedItem("PURCHASE ORDER CANCEL", transection);
		}
		if(isValidMenu("purchaseOrderActiveAndInactive"))
		{
			addCaptionedItem("PURCHASE ORDER ACTIVE/INACTIVE", transection);
		}
		
		if(isValidMenu("FgDeliveryChallanInvoiceDelete"))
		{
			addCaptionedItem("FG DELIVERY CHALLAN INVOICE DELETE ", transection);
		}
		
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("demandRpt"))
		{
			addCaptionedItem("PURCHASE ORDER REPORT", reports);
		}
		if(isValidMenu("demandcancel"))
		{
			addCaptionedItem("PURCHASE ORDER CANCEL REPORT", reports);
		}
		if(isValidMenu("AsOndemandRpt"))
		{
			addCaptionedItem("AS ON DATE PO BALANCE", reports);
		}
		if(isValidMenu("GatePass"))
		{
			addCaptionedItem("GATE PASS", reports);
		}
		if(isValidMenu("Challan"))
		{
			addCaptionedItem("DELIVERY CHALLAN REPORT", reports);
		}
		if(isValidMenu("DateAndPartyWiseWiseDeliveryChallan"))
		{
			addCaptionedItem("DATE AND PARTY WISE DELIVERY CHALLAN", reports);
		}
		if(isValidMenu("DestinationWiseWiseDeliveryChallan"))
		{
			addCaptionedItem("DESTINATION WISE DELIVERY CHALLAN", reports);
		}
		if(isValidMenu("salesBillReport"))
		{
			addCaptionedItem("SALES INVOICE REPORT", reports);
		}
		if(isValidMenu("salesBillReportPaidUnpaid"))
		{
			addCaptionedItem("SALES INVOICE REPORT PAID/UNPAID", reports);
		}
		if(isValidMenu("dateWiseSalesStatement"))
		{
			addCaptionedItem("DATE WISE SALES STATEMENT", reports);
		}
		if(isValidMenu("monthlySalesStatement"))
		{
			addCaptionedItem("MONTHLY SALES STATEMENT", reports);
		}
		if(isValidMenu("yearlySalesStatement"))
		{
			addCaptionedItem("YEARLY SALES STATEMENT", reports);
		}
		if(isValidMenu("DeliveryChallanDeleteStatement"))
		{
			addCaptionedItem("DELIVERY CHALLAN DELETE STATEMENT", reports);
		}
		if(isValidMenu("InvoiceDeleteStatement"))
		{
			addCaptionedItem("INVOICE DELETE STATEMENT", reports);
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
				if(event.getItem().toString().equalsIgnoreCase("PO & SALES TRANSACTION"))
				{
					showWindow(new accessDoTransaction(sessionBean),event.getItem(),"doSalesTransaction");
				}
				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("PO & SALES REPORTS"))
				{
					showWindow(new accessDoReports(sessionBean),event.getItem(),"doSalesReports");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER "))
				{
					showWindow(new DemandOrder(sessionBean),event.getItem(),"demand");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER APPROVE"))
				{
					showWindow(new AuthorityApprove(sessionBean, "purchaseOrder"),event.getItem(),"demandApprove");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("SALES INVOICE"))
				{
					showWindow(new SalesInvoice(sessionBean),event.getItem(),"sales");
				}
				if(event.getItem().toString().equalsIgnoreCase("RETURN INVOICE"))
				{
					showWindow(new ReturnInvoice(sessionBean),event.getItem(),"ReturnInvoice");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER CANCEL"))
				{
					showWindow(new PurchaseOrderCancel(sessionBean),event.getItem(),"purchaseOrderCancel");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER ACTIVE/INACTIVE"))
				{
					showWindow(new PurchaseOrderActiveInactive(sessionBean),event.getItem(),"purchaseOrderActiveAndInactive");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG DELIVERY CHALLAN DELETE "))
				{
					showWindow(new CancelTheDeliveryChallan(sessionBean),event.getItem(),"cancelTheDeliveryChallan");
				}
				if(event.getItem().toString().equalsIgnoreCase("FG DELIVERY CHALLAN INVOICE DELETE "))
				{
					showWindow(new FgDeliveryChallanInvoiceDelete(sessionBean),event.getItem(),"FgDeliveryChallanInvoiceDelete");
				}
				
				//REPORTS
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER REPORT"))
				{
					showWindow(new RptDemandOrderInfo(sessionBean),event.getItem(),"demandRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("AS ON DATE PO BALANCE"))
				{
					showWindow(new RptDateAndPartyWiseWiseDeliveryChallan(sessionBean,"poBalance"),event.getItem(),"AsOndemandRpt");
				}
				if(event.getItem().toString().equalsIgnoreCase("GATE PASS"))
				{
					showWindow(new RptGatePass(sessionBean),event.getItem(),"GatePass");
				}
				if(event.getItem().toString().equalsIgnoreCase("DELIVERY CHALLAN REPORT"))
				{
					showWindow(new RptChallan(sessionBean),event.getItem(),"Challan");
				}				
				if(event.getItem().toString().equalsIgnoreCase("DATE AND PARTY WISE DELIVERY CHALLAN"))
				{
					showWindow(new RptDateAndPartyWiseWiseDeliveryChallan(sessionBean,"dateWise"),event.getItem(),"DateAndPartyWiseWiseDeliveryChallan");
				}
				if(event.getItem().toString().equalsIgnoreCase("DESTINATION WISE DELIVERY CHALLAN"))
				{
					showWindow(new RptDestinationWiseDeliveryChallan(sessionBean),event.getItem(),"DestinationWiseWiseDeliveryChallan");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("TSO WISE PO STATEMENT"))
				{
					showWindow(new RptSRwiseDo(sessionBean),event.getItem(),"sr");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALES INVOICE REPORT"))
				{
					showWindow(new RptSalesInvoiceReport(sessionBean),event.getItem(),"salesBillReport");
				}
				if(event.getItem().toString().equalsIgnoreCase("SALES INVOICE REPORT PAID/UNPAID"))
				{
					showWindow(new RptSalesInvoiceReportPaidUnpaid(sessionBean),event.getItem(),"salesBillReportPaidUnpaid");
				}
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE SALES STATEMENT"))
				{
					showWindow(new RptDateWiseSalesSummary(sessionBean),event.getItem(),"dateWiseSalesStatement");
				}
				if(event.getItem().toString().equalsIgnoreCase("MONTHLY SALES STATEMENT"))
				{
					showWindow(new RptMonthlySalesSummary(sessionBean),event.getItem(),"monthlySalesStatement");
				}
				if(event.getItem().toString().equalsIgnoreCase("YEARLY SALES STATEMENT"))
				{
					showWindow(new RptYearlySalesSummary(sessionBean),event.getItem(),"yearlySalesStatement");
				}
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ORDER CANCEL REPORT"))
				{
					showWindow(new RptPurchaseOrderCancel(sessionBean,""),event.getItem(),"demandcancel");
				}
				if(event.getItem().toString().equalsIgnoreCase("DELIVERY CHALLAN DELETE STATEMENT"))
				{
					showWindow(new RptDeliveryChallanDeleteStatement(sessionBean),event.getItem(),"DeliveryChallanDeleteStatement");
				}
				if(event.getItem().toString().equalsIgnoreCase("INVOICE DELETE STATEMENT"))
				{
					showWindow(new RptDeliveryChallanInvoiceDeleteStatement(sessionBean),event.getItem(),"InvoiceDeleteStatement");
				}

			}
		});
	}

	private boolean isValidMenu(String id)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery("SELECT menuId FROM tbAuthentication "
					+ "WHERE userId = '"+sessionBean.getUserId()+"' AND menuId = '"+id+"'").list().iterator();
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