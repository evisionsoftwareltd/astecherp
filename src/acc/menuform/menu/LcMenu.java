package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.LcModule.LcCharge;
import acc.appform.LcModule.LcChargeEntryMulti;
import acc.appform.LcModule.LcChargeHeadEntry;
import acc.appform.LcModule.LcCloseing;
import acc.appform.LcModule.LcClosingLast;
import acc.appform.LcModule.LcInformation;
import acc.appform.LcModule.LtrInformation;
import acc.appform.LcModule.accessReports;
import acc.appform.LcModule.accessTransaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
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

public class LcMenu 
{
	Tree tree;
	SessionBean sessionBean;
	Component component;

	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();

	Object transaction = null;
	Object reports = null;

	private static final Object CAPTION_PROPERTY = "caption";

	public LcMenu(Object lcLoanModule, Tree tree, SessionBean sessionBean,Component component) 
	{	
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("lcTransaction"))
		{
			transaction = addCaptionedItem("L/C TRANSACTION", lcLoanModule);
			addTransectionChild(transaction);
		}

		if(isValidMenu("lcReports"))
		{
			reports = addCaptionedItem("L/C REPORTS", lcLoanModule);
			addReportsChild(reports);
		}
	}

	private void addTransectionChild(Object transection)
	{
		if(isValidMenu("lcChargeEntry"))
		{
			addCaptionedItem("L/C CHARGE INFORMATION", transection);
		}
		if(isValidMenu("lcOpening"))
		{
			addCaptionedItem("L/C INFORMATION", transection);
		}
		if(isValidMenu("lcChargeInfo"))
		{
			addCaptionedItem("L/C CHARGE ENTRY", transection);
		}
		/*if(isValidMenu("lcChargeInfoMulti"))
		{
			addCaptionedItem("L/C CHARGE ENTRY.", transection);
		}*/
		if(isValidMenu("ltrInformation"))
		{
			addCaptionedItem("LTR INFORMATION", transection);
		}
		if(isValidMenu("lcClosing"))
		{
			addCaptionedItem("L/C CLOSING", transection);
		}

		/*if(isValidMenu("customDutyCharge"))
		{
			addCaptionedItem("CUSTOM DUTY & OTHER CHARGE", transection);
		}

		if(isValidMenu("loanCreate"))
		{
			addCaptionedItem("LOAN CREATE(LTR)", transection);
		}

		if(isValidMenu("loanAdjustment"))
		{
			addCaptionedItem("LOAN ADJUST/PAYMENT", transection);
		}

		if(isValidMenu("loanBalance"))
		{
			addCaptionedItem("LOAN BALANCE", transection);
		}

		if(isValidMenu("loanCost"))
		{
			addCaptionedItem("LOAN COST", transection);
		}*/
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("lcInfo"))
		{
			addCaptionedItem("L/C INFO", reports);
		}
		if(isValidMenu("lcRegister"))
		{
			addCaptionedItem("L/C REGISTER", reports);
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

	@SuppressWarnings("serial")
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				System.out.println(event.getItemId()+" "+event.getItem());

				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("L/C CHARGE INFORMATION"))
				{
					showWindow(new LcChargeHeadEntry(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C TRANSACTION"))
				{
					showWindow(new accessTransaction(sessionBean),event.getItem(),"lcTransaction");
				}
				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("L/C REPORTS"))
				{
					//showWindow(new accessReports(sessionBean),event.getItem(),"lcReports");
				}

				if(event.getItem().toString().equalsIgnoreCase("L/C INFORMATION"))
				{
					showWindow(new LcInformation(sessionBean),event.getItem(),"lcOpening");
				}
				if(event.getItem().toString().equalsIgnoreCase("LTR INFORMATION"))
				{
					showWindow(new LtrInformation(sessionBean),event.getItem(),"ltrInformation");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C CHARGE ENTRY"))
				{
					showWindow(new LcCharge(sessionBean),event.getItem(),"lcChargeInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C CHARGE ENTRY."))
				{
					showWindow(new LcChargeEntryMulti(sessionBean),event.getItem(),"lcChargeEntryMulti");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C CLOSING"))
				{
					showWindow(new LcClosingLast(sessionBean),event.getItem(),"lcClosing");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C INFO"))
				{
					showWindow(new RptLcInfo(sessionBean),event.getItem(),"lcInfo");
				}
				if(event.getItem().toString().equalsIgnoreCase("L/C REGISTER"))
				{
					showWindow(new RptLcRegister(sessionBean),event.getItem(),"lcInfo");
				}
			}
		});
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
