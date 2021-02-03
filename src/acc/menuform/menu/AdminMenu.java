package acc.menuform.menu;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Session;

import acc.appform.hrmModule.DepartmentInformation;
import acc.appform.hrmModule.SectionInformation;
import acc.appform.setupTransaction.AreaInformation;
import acc.appform.setupTransaction.DepoInformation;
import acc.appform.setupTransaction.DivisionInformation;
import acc.appform.setupTransaction.MachineInformation;
import acc.appform.setupTransaction.PartyInformation;
import acc.appform.setupTransaction.SupplierInformation;

import com.common.share.ChangePass;
import com.common.share.CompanyInfo;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.UserAutentication;
import com.common.share.UserCreate;
import com.reportform.setupReport.RptMachineInfo;
import com.reportform.setupReport.RptPartyInfo;
import com.reportform.setupReport.RptSupplierInfo;
import com.share.accessModule.accessAdminReports;
import com.share.accessModule.accessAdminTransaction;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class AdminMenu 
{
	private Tree tree;
	private SessionBean sessionBean;
	private Component component;

	private HashMap<Object, Object> winMap = new HashMap<Object, Object>();
	private static final Object CAPTION_PROPERTY = "caption";

	private Object transaction = null, reports = null;

	public AdminMenu(Object adminMenu,Tree tree,SessionBean sessionBean,Component component)
	{
		this.tree = tree;
		this.sessionBean = sessionBean;
		this.component = component;

		treeAction();

		if(isValidMenu("adminTransaction"))
		{
			transaction = addCaptionedItem("SETUP TRANSACTION", adminMenu);
			addTransectionChild(transaction);
		}

		if(isValidMenu("adminReports"))
		{
			reports = addCaptionedItem("SETUP REPORTS", adminMenu);
			addReportsChild(reports);
		}
	}

	private void addTransectionChild(Object transection)
	{
		if(isValidMenu("companyInformation"))
		{
			addCaptionedItem("COMPANY INFORMATION", transection);
		}

		if(isValidMenu("deptInformation"))
		{
			addCaptionedItem("DEPARTMENT INFORMATION", transection);
		}
		if(isValidMenu("SectionInformation"))
		{
			addCaptionedItem("SECTION INFORMATION", transection);
		}
		if(isValidMenu("divisionInformation"))
		{
			addCaptionedItem("DIVISION INFORMATION", transection);
		}

		if(isValidMenu("areaInformation"))
		{
			addCaptionedItem("ZONE INFORMATION", transection);
		}

		if(isValidMenu("partyInformation"))
		{
			addCaptionedItem("PARTY INFORMATION", transection);
		}

		if(isValidMenu("supplierInformation"))
		{
			addCaptionedItem("SUPPLIER INFORMATION", transection);
		}

		if(isValidMenu("machineInformation"))
		{
			addCaptionedItem("MACHINE INFORMATION", transection);
		}

		if(isValidMenu("depoInformation"))
		{
			addCaptionedItem("STORE INFORMATION", transection);
		}

		if(isValidMenu("userCreate"))
		{
			addCaptionedItem("USER CREATE", transection);
		}

		if(isValidMenu("userAuthentication"))
		{
			addCaptionedItem("USER AUTHENTICATION", transection);
		}

		if(isValidMenu("changePassword"))
		{
			addCaptionedItem("CHANGE PASSWORD", transection);
		}

		if(isValidMenu("logout"))
		{
			addCaptionedItem("LOGOUT", transection);
		}
	}

	private void addReportsChild(Object reports)
	{
		if(isValidMenu("supplierInfo"))
		{
			addCaptionedItem("SUPPLIER INFO", reports);
		}

		if(isValidMenu("partyInfo"))
		{
			addCaptionedItem("PARTY INFO", reports);
		}

		/*if(isValidMenu("machineInfo"))
		{
			addCaptionedItem("MACHINE INFO", reports);
		}*/
		if(isValidMenu("machineInformation"))
		{
			addCaptionedItem("MACHINE INFORMATION.", reports);
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
			String sql = "SELECT menuId FROM tbAuthentication WHERE userId = '"+sessionBean.getUserId()+
					"' AND menuId = '"+id+"' ";
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
	public void treeAction()
	{
		tree.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				System.out.println(event.getItemId()+" "+event.getItem());

				// TRANSACTION
				if(event.getItem().toString().equalsIgnoreCase("SETUP TRANSACTION"))
				{
					showWindow(new accessAdminTransaction(sessionBean),event.getItem(),"adminTransaction");
				}
				// REPORTS
				if(event.getItem().toString().equalsIgnoreCase("SETUP REPORTS"))
				{
					showWindow(new accessAdminReports(sessionBean),event.getItem(),"adminReports");
				}

				//Transection
				{
					// company information
					if(event.getItem().toString().equalsIgnoreCase("COMPANY INFORMATION"))
					{
						showWindow(new CompanyInfo(sessionBean),event.getItem(),"companyInformation");
					}

					if(event.getItem().toString().equalsIgnoreCase("DEPARTMENT INFORMATION"))
					{
						showWindow(new DepartmentInformation(sessionBean),event.getItem(),"deptInformation");
					}
					if(event.getItem().toString().equalsIgnoreCase("SECTION INFORMATION"))
					{
						showWindow(new SectionInformation(sessionBean),event.getItem(),"SectionInformation");
					}

					//Party Info
					if(event.getItem().toString().equalsIgnoreCase("PARTY INFORMATION"))
					{
						showWindow(new PartyInformation(sessionBean),event.getItem(),"partyInformation");
					}

					//DIVISION INFORMATION
					if(event.getItem().toString().equalsIgnoreCase("DIVISION INFORMATION"))
					{
						showWindow(new DivisionInformation(sessionBean),event.getItem(),"divisionInformation");
					}

					//AREA INFORMATION
					if(event.getItem().toString().equalsIgnoreCase("ZONE INFORMATION"))
					{
						showWindow(new AreaInformation(sessionBean),event.getItem(),"areaInformation");
					}

					//Supplier Info
					if(event.getItem().toString().equalsIgnoreCase("SUPPLIER INFORMATION"))
					{
						showWindow(new SupplierInformation(sessionBean),event.getItem(),"supplierInformation");
					}

					//Supplier Info
					if(event.getItem().toString().equalsIgnoreCase("MACHINE INFORMATION"))
					{
						showWindow(new MachineInformation(sessionBean),event.getItem(),"machineInformation");
					}

					//Supplier Info
					if(event.getItem().toString().equalsIgnoreCase("STORE INFORMATION"))
					{
						showWindow(new DepoInformation(sessionBean),event.getItem(),"depoInformation");
					}

					// user create
					if(event.getItem().toString().equalsIgnoreCase("USER CREATE"))
					{
						showWindow(new UserCreate(sessionBean),event.getItem(),"userCreate");
					}
					//USER AUTHENTICATION
					if(event.getItem().toString().equalsIgnoreCase("USER AUTHENTICATION"))
					{
						if(!sessionBean.getAuthenticWindow())
						{
							Window userAuthen = new UserAutentication(sessionBean);
							userAuthen.center();
							component.getWindow().addWindow(userAuthen);
							userAuthen.setCloseShortcut(KeyCode.ESCAPE);

							userAuthen.addListener(new Window.CloseListener()
							{
								public void windowClose(CloseEvent e)
								{
									sessionBean.setAuthenticWindow(false);
								}
							});
						}
						else
						{
							sessionBean.setPermitForm("userAuthentication","USER AUTHENTICATION");
						}
					}

					// change password
					if(event.getItem().toString().equalsIgnoreCase("CHANGE PASSWORD"))
					{
						showWindow(new ChangePass(sessionBean),event.getItem(),"changePassword");
					}
				}

				//Reports
				{
					//Supplier Info
					if(event.getItem().toString().equalsIgnoreCase("SUPPLIER INFO"))
					{
						showWindow(new RptSupplierInfo(sessionBean),event.getItem(),"supplierInfo");
					}
					if(event.getItem().toString().equalsIgnoreCase("PARTY INFO"))
					{
						showWindow(new RptPartyInfo(sessionBean),event.getItem(),"partyInfo");
					}
					/*
					if(event.getItem().toString().equalsIgnoreCase("MACHINE INFO"))
					{
						showWindow(new RptMachineInfo(sessionBean),event.getItem(),"machineInfo");
					}*/
					if(event.getItem().toString().equalsIgnoreCase("MACHINE INFORMATION."))
					{
						showWindow(new RptMachineInfo(sessionBean,""),event.getItem(),"machineInfo");
					}
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