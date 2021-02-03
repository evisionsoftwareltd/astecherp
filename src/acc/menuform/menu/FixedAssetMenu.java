package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.asset.*;
import acc.reportmodule.asset.AssetLedgerRpt;
import acc.reportmodule.asset.AssetRegisterRpt;
import acc.reportmodule.asset.AssetScheduleRpt;
import acc.reportmodule.asset.LeasedAssetScheduleRpt;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class FixedAssetMenu
{
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	
	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object fixedAsset = null;

	Object assetTransaction = null;
	Object assetReport = null;

	public FixedAssetMenu(Object assetModule,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("assetTransaction"))
		{
			assetTransaction = addCaptionedItem("ASSET TRANSACTION", assetModule);
			addTransectionChild(assetTransaction);
		}
		if(isValidMenu("assetReport"))
		{
			assetReport = addCaptionedItem("ASSET REPORT", assetModule);
			addReportsChild(assetReport);
		}
	}

	private void addTransectionChild(Object transaction)
	{
		if(isValidMenu("openingBalance"))
		{
			addCaptionedItem("OPENING BALANCE", transaction);
		}

		if(isValidMenu("purchase"))
		{
			addCaptionedItem("PURCHASE ASSET", transaction);
		}

		if(isValidMenu("assetSales"))
		{
			addCaptionedItem("ASSET SALES", transaction);
		}

		if(isValidMenu("depreciationCharges"))
		{
			addCaptionedItem("DEPRECIATION CHARGES", transaction);
		}
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("fixedAssetSchedule"))
		{
			addCaptionedItem("FIXED ASSET SCHEDULE", reports);
		}
		if(isValidMenu("leasedAssetSchedule"))
		{
			//	addCaptionedItem("LEASED ASSET SCHEDULE", reports);
		}
		if(isValidMenu("fixedAssetLedger"))
		{
			addCaptionedItem("FIXED ASSET LEDGER", reports);
		}
		if(isValidMenu("fixedAssetRegister"))
		{
			addCaptionedItem("FIXED ASSET REGISTER", reports);
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

				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("ASSET TRANSACTION"))
				{
					showWindow(new fixedTransaction(sessionBean),event.getItem(),"assetTransaction");
				}

				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("ASSET REPORT"))
				{
					showWindow(new fixedReports(sessionBean),event.getItem(),"assetReport");
				}

				// opening Balance
				if(event.getItem().toString().equalsIgnoreCase("OPENING BALANCE"))
				{
					showWindow(new FixedAssetOpen(sessionBean),event.getItem(),"OpeningBalance");
				}

				// purchase
				if(event.getItem().toString().equalsIgnoreCase("PURCHASE ASSET"))
				{
					showWindow(new FixedAssetPurchase(sessionBean),event.getItem(),"purchase");
				}

				// asset Sales
				if(event.getItem().toString().equalsIgnoreCase("ASSET SALES"))
				{
					showWindow(new AssetSales(sessionBean),event.getItem(),"assetSales");
				}

				// depreciation Charges
				if(event.getItem().toString().equalsIgnoreCase("DEPRECIATION CHARGES"))
				{
					showWindow(new DepreciationCharge(sessionBean),event.getItem(),"depreciationCharges");
				}

				// assetSchedule
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
			session.beginTransaction();
			String sql = "SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
					"' AND menuId = '"+id+"'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
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

	private boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}