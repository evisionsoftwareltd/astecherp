package acc.appform.transportModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.TextRead;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class VehicleFindWindow extends Window 
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private SessionBean sessionBean;
	private String frmName;
	private TextRead findVehicleID = new TextRead();
	private String vehicleID;
	private Table table=new Table();
	
	private ArrayList<Label> lblSerial = new ArrayList<Label>();
	private ArrayList<Label> lblVehicleId = new ArrayList<Label>();
	private ArrayList<Label> lblVehicleType = new ArrayList<Label>();
	private ArrayList<Label> lblRegNo = new ArrayList<Label>();
	private ArrayList<Label> lblSupplier = new ArrayList<Label>();
	
	public VehicleFindWindow(SessionBean sessionBean,TextRead findVehicleID,String frmName)
	{
		this.findVehicleID=findVehicleID;
		this.sessionBean=sessionBean;
		this.setCaption("VEHICLE INFO FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("620");
		this.setHeight("480px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		dataLoad();
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<17;i++)
		{
			tableRowAdd(i);
		}
	}
	
	public void tableRowAdd(final int ar)
	{
		lblSerial.add(ar,new Label());
		lblSerial.get(ar).setWidth("100%");
		lblSerial.get(ar).setHeight("16px");
		
		lblVehicleId.add(ar,new Label());
		lblVehicleId.get(ar).setWidth("100%");
		
		lblVehicleType.add(ar,new Label());
		lblVehicleType.get(ar).setWidth("100%");

		lblRegNo.add(ar,new Label());
		lblRegNo.get(ar).setWidth("100%");
		
		lblSupplier.add(ar,new Label());
		lblSupplier.get(ar).setWidth("100%");
		
		table.addItem(new Object[]{lblSerial.get(ar),lblVehicleId.get(ar),lblVehicleType.get(ar),lblRegNo.get(ar),lblSupplier.get(ar)},ar);
	}
	
	public void setEventAction()
	{
		/*btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});
		*/
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					vehicleID=lblVehicleId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println("Got id: "+vehicleID);
					findVehicleID.setValue(vehicleID);
					
					windowClose();
				}
			}
		});
	}
	
	private void windowClose()
	{
		this.close();
	}
	
	private void compInit()
	{		
		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("400px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);
		
		table.addContainerProperty("Vehicle ID", Label.class, null);
		table.setColumnWidth("Vehicle ID",55);
		
		table.addContainerProperty("Vehicle Type", Label.class, new Label());
		table.setColumnWidth("Vehicle Id",70);
		
		table.addContainerProperty("Reg. Number", Label.class, new Label());
		table.setColumnWidth("Reg. Name",250);
		
		table.addContainerProperty("Driver Name", Label.class, new Label());
		table.setColumnWidth("Driver Name",120);
		
		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER });
	}
	
	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
	
	private void dataLoad()
	{
		Transaction tx;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = null;
			String sql = null;

			if(frmName.equals("VehicleInfo"))
			{
				sql = "SELECT vehicleId,vehicleType,regNumber,driverName from tbVehicleInfo Order by vehicleId ASC";
				lst= session.createSQLQuery(sql).list();
				System.out.println(sql);

				int i=0;
				if(!lst.isEmpty())
				{
					tableclear();
					for (Iterator iter = lst.iterator(); iter.hasNext();)
					{
						Object[] element = (Object[]) iter.next();
													
							lblSerial.get(i).setValue(i+1);
							lblVehicleId.get(i).setValue(element[0].toString());
							lblVehicleType.get(i).setValue(element[1].toString());
							lblRegNo.get(i).setValue(element[2].toString());
							lblSupplier.get(i).setValue(element[3].toString());
							
							if((i)==lblSerial.size()-1) 
							{
								tableRowAdd(i+1);
							}
							i++;
					}
				}
				else
				{
					getParent().showNotification("Warning: ","There are no Data.");
				}
			}
			
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	private void tableclear()
	{
		for(int i=0; i<lblSerial.size(); i++)
		{
//			lblId.get(i).setValue("");
//			lblAllName.get(i).setValue("");
		}
	}
}
