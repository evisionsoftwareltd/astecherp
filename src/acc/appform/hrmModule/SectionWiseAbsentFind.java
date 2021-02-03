package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SectionWiseAbsentFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtShiftId;
	private TextField dDate;
	private TextField txtDepartment;
	private Table table=new Table();

	public String receiptShiftId = "";

	private ArrayList<Label> Date = new ArrayList<Label>();
	private ArrayList<Label> lblDeptID = new ArrayList<Label>();
	private ArrayList<Label> lblDeptName = new ArrayList<Label>();
	private ArrayList<Label> lbShiftID = new ArrayList<Label>();
	private ArrayList<Label> lbShiftName = new ArrayList<Label>();

	private String frmName;
	@SuppressWarnings("unused")
	private SessionBean sessionBean;
	public SectionWiseAbsentFind(SessionBean sessionBean,TextField txtFindDepartment,TextField txtShiftId,TextField dDate,String frmName)
	{
		this.txtShiftId = txtShiftId;
		this.dDate=dDate;
		this.txtDepartment=txtFindDepartment;
		this.sessionBean=sessionBean;
		this.setCaption("FIND SECTION WISE ABSENT :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("350px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		if(frmName.equalsIgnoreCase("SectionWiseAbsent"))
		{
			Date.add(ar, new Label(""));
			Date.get(ar).setWidth("100%");
			Date.get(ar).setImmediate(true);
			Date.get(ar).setHeight("23px");

			lblDeptID.add(ar, new Label(""));
			lblDeptID.get(ar).setWidth("100%");
			lblDeptID.get(ar).setImmediate(true);
			
			lblDeptName.add(ar, new Label(""));
			lblDeptName.get(ar).setWidth("100%");
			lblDeptName.get(ar).setImmediate(true);
			lblDeptName.get(ar).setHeight("23px");

			lbShiftID.add(ar, new Label(""));
			lbShiftID.get(ar).setWidth("100%");
			lbShiftID.get(ar).setImmediate(true);
			
			lbShiftName.add(ar, new Label(""));
			lbShiftName.get(ar).setWidth("100%");
			lbShiftName.get(ar).setImmediate(true);
			lbShiftName.get(ar).setHeight("23px");

			table.addItem(new Object[]{Date.get(ar),lblDeptID.get(ar),lblDeptName.get(ar),lbShiftID.get(ar),
					lbShiftName.get(ar)},ar);
		}
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					if(frmName.equalsIgnoreCase("SectionWiseAbsent"))
					{
						txtDepartment.setValue(lblDeptID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
						receiptShiftId=lbShiftID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtShiftId.setValue(receiptShiftId);
						dDate.setValue(Date.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					}
					
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<Date.size(); i++)
		{
			Date.get(i).setValue("");
			lbShiftID.get(i).setValue("");
			lbShiftName.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="";
			if(frmName.equalsIgnoreCase("SectionWiseAbsent"))
				query="select distinct tap.dDate,tap.vDepartmentID,dept.vDepartmentName,tap.vSectionID,tsi.SectionName " +
						"from tbAbsentAsPunishment tap inner join tbSectionInfo tsi on tap.vSectionID=tsi.vSectionID " +
						"inner join tbDepartmentInfo dept on dept.vDepartmentID=tap.vDepartmentID order by tap.dDate desc";
			List <?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					Date.get(i).setValue(element[0].toString());
					lblDeptID.get(i).setValue(element[1].toString());
					lblDeptName.get(i).setValue(element[2].toString());
					lbShiftID.get(i).setValue(element[3].toString());
					lbShiftName.get(i).setValue(element[4].toString());

					if((i)==Date.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("tableDataAdding", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		if(frmName.equalsIgnoreCase("SectionWiseAbsent"))
		{
			table.addContainerProperty("Date", Label.class, new Label());
			table.setColumnWidth("Date",80);

			table.addContainerProperty("Department ID", Label.class, new Label());
			table.setColumnWidth("Department ID", 40);
			
			table.addContainerProperty("Department Name", Label.class, new Label());
			table.setColumnWidth("Department Name",90);

			table.addContainerProperty("Section ID", Label.class, new Label());
			table.setColumnWidth("Section ID", 40);
			
			table.addContainerProperty("Section Name", Label.class, new Label());
			table.setColumnWidth("Section Name",90);
			
			table.setColumnCollapsed("Department ID", true);
			table.setColumnCollapsed("Section ID", true);
		}
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}