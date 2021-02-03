    package com.example.productionTransaction;
	import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.classic.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
	import com.vaadin.event.ItemClickEvent;
	import com.vaadin.event.ItemClickEvent.ItemClickListener;
	import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button.ClickListener;
	import com.vaadin.ui.FormLayout;
	import com.vaadin.ui.HorizontalLayout;
	import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Table;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class StretchBlowMoldingRequsitionFindWindow extends Window {

		private SessionBean sessionBean;
		private HorizontalLayout hrLayout=new HorizontalLayout();
		private VerticalLayout mainLayout=new VerticalLayout();
		private FormLayout cmbLayout=new FormLayout();
		private HorizontalLayout btnLayout=new HorizontalLayout();
		private TextField txtReceiptAreaId =new TextField();
		private String 	receiptAreaId="";
		private Table table=new Table();
		
		private ArrayList<Label> lblsl = new ArrayList<Label>();
		private ArrayList<Label> lblTo = new ArrayList<Label>();
		private ArrayList<Label> lblBatchNo = new ArrayList<Label>();
		private ArrayList<Label> lblReqNo = new ArrayList<Label>();
		private ArrayList<Label> lblReqDate = new ArrayList<Label>();
		
		private PopupDateField dFromDate,dToDate;
		private NativeButton findButton = new NativeButton("Find");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		
		
		public StretchBlowMoldingRequsitionFindWindow(SessionBean sessionBean,TextField BatchNo)
		{
			this.txtReceiptAreaId = BatchNo;
			this.sessionBean=sessionBean;
			this.setCaption("STRETCH BLOW MOLDING REQUISITION FIND :: "+sessionBean.getCompany());
			this.center();
			this.setWidth("550px");
			this.setCloseShortcut(KeyCode.ESCAPE);
			this.setModal(true);
			this.setResizable(false);
			this.setStyleName("cwindow");

			compInit();
			compAdd();
			tableInitialise();
			setEventAction();
			tableclear();
		}
		
		public void setEventAction()
		{
			table.addListener(new ItemClickListener() 
			{
				public void itemClick(ItemClickEvent event) 
				{
					if(event.isDoubleClick())
					{
						receiptAreaId = lblBatchNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
						txtReceiptAreaId.setValue(receiptAreaId);
						System.out.println("DivisionId: "+receiptAreaId);
						windowClose();
						
					}
				}
			});
			findButton.addListener(new ClickListener() {
				
				
				public void buttonClick(ClickEvent event) {
					tableClear();
					findButtonEvent();
				}
			});
		}
		private void tableClear(){
			for(int a=0;a<lblBatchNo.size();a++){
				lblTo.get(a).setValue("");
				lblBatchNo.get(a).setValue("");
				lblReqNo.get(a).setValue("");
				lblReqDate.get(a).setValue("");
			}
		}
		private Iterator<?> dbService(String sql){
			Session session=SessionFactoryUtil.getInstance().openSession();
			Iterator<?> iter=null;
			try{
				iter= session.createSQLQuery(sql).list().iterator();
			}
			catch(Exception exp){
				showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{
				if(session!=null){
					session.close();
				}
			}
			return iter;
		}
		private void findButtonEvent() {
			int a=0;
			Iterator iter=dbService("select vReqTo,vBatchNo,vReqNo,convert(varchar(10),dReqDate,103) "
					+ "from tbStretchBlowMoldingRequisitionInfo where dReqDate between "
					+ "'"+dateFormat.format(dFromDate.getValue())+"' "
							+ "and '"+dateFormat.format(dToDate.getValue())+"' "
									+ "order by dReqDate,vReqFrom");
			if(iter.hasNext()){
				while(iter.hasNext()){
					Object element[]=(Object[])iter.next();
					lblTo.get(a).setValue(element[0]);
					lblBatchNo.get(a).setValue(element[1]);
					lblReqNo.get(a).setValue(element[2]);
					lblReqDate.get(a).setValue(element[3]);
					if(a==lblBatchNo.size()-1){
						tableRowAdd(a+1);
					}
					a++;
				}
			}
			else{
				showNotification("Sorr!!","There ar no data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		private void tableclear()
		{
			/*for(int i=0; i<lblJobId.size(); i++)
			{
				lblClientName.get(i).setValue("");
				lblJobId.get(i).setValue("");
				lblJobName.get(i).setValue("");
			}*/
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
			lblsl.add(ar, new Label(""));
			lblsl.get(ar).setWidth("100%");
			lblsl.get(ar).setImmediate(true);
			lblsl.get(ar).setHeight("12px");
			lblsl.get(ar).setValue(ar+1);
			
			lblTo.add(ar, new Label(""));
			lblTo.get(ar).setWidth("100%");
			lblTo.get(ar).setImmediate(true);
			lblTo.get(ar).setHeight("12px");
			
			lblBatchNo.add(ar, new Label(""));
			lblBatchNo.get(ar).setWidth("100%");
			lblBatchNo.get(ar).setImmediate(true);

			lblReqNo.add(ar, new Label(""));
			lblReqNo.get(ar).setWidth("100%");
			lblReqNo.get(ar).setImmediate(true);
			

			lblReqDate.add(ar, new Label(""));
			lblReqDate.get(ar).setWidth("100%");
			lblReqDate.get(ar).setImmediate(true);
			

			table.addItem(new Object[]{lblsl.get(ar),lblTo.get(ar),lblBatchNo.get(ar),lblReqNo.get(ar),lblReqDate.get(ar)},ar);
		}
		
		

		private void windowClose()
		{
			this.close();
		}

		private void compInit()
		{
			hrLayout.setSpacing(true);
			
			hrLayout.addComponent(new Label("From Date: "));
			dFromDate = new PopupDateField();
			dFromDate.setImmediate(true);
			dFromDate.setWidth("110px");
			dFromDate.setDateFormat("dd-MM-yyyy");
			dFromDate.setValue(new java.util.Date());
			dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
			
			
			dToDate = new PopupDateField();
			dToDate.setImmediate(true);
			dToDate.setWidth("110px");
			dToDate.setDateFormat("dd-MM-yyyy");
			dToDate.setValue(new java.util.Date());
			dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
			
			findButton.setWidth("100px");
			findButton.setHeight("28px");
			findButton.setIcon(new ThemeResource("../icons/icon_get_world.gif"));
			
			hrLayout.addComponent(dFromDate);
			hrLayout.addComponent(new Label("To Date: "));
			hrLayout.addComponent(dToDate);
			hrLayout.addComponent(findButton);
			mainLayout.addComponent(hrLayout);
			
			mainLayout.setSpacing(true);
			table.setSelectable(true);
			table.setWidth("100%");
			table.setHeight("150px");

			table.setImmediate(true); // react at once when something is selected
			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);
			table.setWidth("100%");
			table.setHeight("154px");
			
			table.addContainerProperty("SL", Label.class, new Label());
			table.setColumnWidth("SL",25);
			
			table.addContainerProperty("To", Label.class, new Label());
			table.setColumnWidth("To",100);
			
			table.addContainerProperty("Batch No", Label.class, new Label());
			table.setColumnWidth("Batch No",90);
			
			table.addContainerProperty("Req No", Label.class, new Label());
			table.setColumnWidth("Req No",90);
			
			table.addContainerProperty("Req Date", Label.class, new Label());
			table.setColumnWidth("Req Date",100);
			//table.setColumnCollapsed("Transaction Id", true);
		}

		private void compAdd()
		{
			mainLayout.addComponent(table);
			addComponent(mainLayout);
		} 

	}
