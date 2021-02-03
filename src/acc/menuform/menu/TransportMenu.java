package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.transportModule.DayOff;
import acc.appform.transportModule.VehicleInfo;
import acc.appform.transportModule.VehicleMaintain;
import acc.appform.transportModule.vehicleReports;
import acc.appform.transportModule.vehicleTransaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.reportform.vehicleReport.RptVehicleInfo;
import com.reportform.vehicleReport.RptIndividualVehicleReport;
import com.reportform.vehicleReport.RptDailyVehicleMainten;
import com.reportform.vehicleReport.RptDateWiseVehicleMainten;
import com.reportform.vehicleReport.RptMonthWiseVehicleMainten;
import com.reportform.vehicleReport.RptVehicleMaintenOfaMonth;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class TransportMenu 
{
	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	Tree tree;
	SessionBean sessionBean;
	Component component;

	Object transaction = null;
	Object reports = null;

	Object ledgerBalance = null;
	Object fundPosition = null;

	public TransportMenu(Object vehicleModule,Tree tree,SessionBean sessionBean,Component component) 
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("vehicleTransaction"))
		{
			transaction = addCaptionedItem("VEHICLE TRANSACTION", vehicleModule);
			addTransectionChild(transaction);
		}
		if(isValidMenu("vehicleReports"))
		{
			reports = addCaptionedItem("VEHICLE REPORTS", vehicleModule);
			addReportsChild(reports);
		}
	}

	private void addTransectionChild(Object transaction)
	{
		if(isValidMenu("vehicleInformation"))
		{
			addCaptionedItem("VEHICLE INFORMATION", transaction);
		}

		if(isValidMenu("vehicleMaintenance"))
		{
			addCaptionedItem("VEHICLE MAINTENANCE", transaction);
		}
		if(isValidMenu("DayOff"))
		{
			addCaptionedItem("DAY OFF", transaction);
		}
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("individualVehicleReport"))
		{
			addCaptionedItem("INDIVIDUAL VEHICLE REPORT", reports);
		}
		if(isValidMenu("vehicleInfo"))
		{
			addCaptionedItem("VEHICLE INFO", reports);
		}
		if(isValidMenu("DailyVehicleMaintenance"))
		{
			addCaptionedItem("DAILY VEHICLE MAINTENANCE", reports);
		}
		if(isValidMenu("DateWiseVehicleMaintenance"))
		{
			addCaptionedItem("DATE WISE VEHICLE MAINTENANCE", reports);
		}
		if(isValidMenu("MonthWiseVehicleMaintenance"))
		{
			addCaptionedItem("MONTH WISE VEHICLE MAINTENANCE", reports);
		}
		if(isValidMenu("DayWiseVehicleMaintenance"))
		{
			//			saddCaptionedItem("DAY WISE VEHICLE MAINTENANCE", reports);
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
				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("VEHICLE TRANSACTION"))
				{
					showWindow(new vehicleTransaction(sessionBean),event.getItem(),"vehicleTransaction");
				}
				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("VEHICLE REPORTS"))
				{
					showWindow(new vehicleReports(sessionBean),event.getItem(),"vehicleReports");
				}

				// vehicle Information
				if(event.getItem().toString().equalsIgnoreCase("VEHICLE INFORMATION"))
				{
					showWindow(new VehicleInfo(sessionBean),event.getItem(),"vehicleInformation");
				}
				
				if(event.getItem().toString().equalsIgnoreCase("DAY OFF"))
				{
					showWindow(new DayOff(sessionBean),event.getItem(),"vehicleInformation");
				}

				// vehicle Maintenance
				if(event.getItem().toString().equalsIgnoreCase("VEHICLE MAINTENANCE"))
				{
					showWindow(new VehicleMaintain(sessionBean),event.getItem(),"vehicleMaintenance");
				}

				// vehicle Info
				if(event.getItem().toString().equalsIgnoreCase("VEHICLE INFO"))
				{
					showWindow(new RptVehicleInfo(sessionBean),event.getItem(),"vehicleInfo");
				}

				// individual Vehicle Report
				if(event.getItem().toString().equalsIgnoreCase("INDIVIDUAL VEHICLE REPORT"))
				{
					showWindow(new RptIndividualVehicleReport(sessionBean),event.getItem(),"individualVehicleReport");
				}

				// Daily Vehicle Maintenance Report
				if(event.getItem().toString().equalsIgnoreCase("DAILY VEHICLE MAINTENANCE"))
				{
					showWindow(new RptDailyVehicleMainten(sessionBean),event.getItem(),"DailyVehicleMaintenance");
				}

				// Date Wise Vehicle Maintenance Report
				if(event.getItem().toString().equalsIgnoreCase("DATE WISE VEHICLE MAINTENANCE"))
				{
					showWindow(new RptDateWiseVehicleMainten(sessionBean),event.getItem(),"DateWiseVehicleMaintenance");
				}

				// Month Wise Vehicle Maintenance Report
				if(event.getItem().toString().equalsIgnoreCase("MONTH WISE VEHICLE MAINTENANCE"))
				{
					showWindow(new RptMonthWiseVehicleMainten(sessionBean),event.getItem(),"MonthWiseVehicleMaintenance");
				}

				// Month Wise Vehicle Maintenance Report
				if(event.getItem().toString().equalsIgnoreCase("DAY WISE VEHICLE MAINTENANCE"))
				{
					showWindow(new RptVehicleMaintenOfaMonth(sessionBean),event.getItem(),"DayWiseVehicleMaintenance");
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

	private  boolean isOpen(String id)
	{
		return !winMap.containsKey(id);
	}
}
