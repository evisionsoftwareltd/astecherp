package acc.appform.transaction;


import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class FundRequisition extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");
		private SessionBean sessionBean;
		private VerticalLayout mainLayout = new VerticalLayout();
		private VerticalLayout vl = new VerticalLayout();
		private HorizontalLayout btnL = new HorizontalLayout();
		private HorizontalLayout space = new HorizontalLayout();		
		private HorizontalLayout hL1 = new HorizontalLayout();  		
		private TextField voucherNo = new TextField();
		private DateField date = new DateField();
		private TextField description = new TextField("Description:");
		private Table table = new Table();
		private Label sl[];
		private ComboBox acHead[];
		private TextField balance[];
		private TextField billUnderProc[];
		private TextField requisition[];
		private TextField approved[];
		
		
		private int tr = 100;
		
		
		public FundRequisition(SessionBean sessionBean){
			this.sessionBean = sessionBean;
			this.setWidth("800px");
			this.setCaption("FUND REQUISITION :: "+this.sessionBean.getCompany());
			this.setResizable(false);
			
			GridLayout titleGrid = new GridLayout(1,1);
			titleGrid.addComponent(new Label("<h3><u>FUND REQUISITION</u></h3>",Label.CONTENT_XHTML));
			mainLayout.addComponent(titleGrid);
			mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);
			
			hL1.addComponent(new Label("Voucher No:"));
			hL1.addComponent(voucherNo);
			HorizontalLayout sp1 = new HorizontalLayout();
			hL1.addComponent(sp1);
			sp1.setWidth("200px");
			hL1.addComponent(new Label("Date:"));
			hL1.addComponent(date);
			hL1.setSpacing(true);
			hL1.setMargin(true);
			
			
			date.setValue(new java.util.Date());
		    date.setResolution(PopupDateField.RESOLUTION_DAY);
		    date.setDateFormat("dd-MM-yy");
		    date.setInvalidAllowed(false);
		    date.setImmediate(true);
		   
		    table.setFooterVisible(true);
			table.setWidth("715px");
	        table.setHeight("280px");
	       
	        table.addContainerProperty("SL", Label.class, new Label());
	        
	       // table.setc
	        table.addContainerProperty("Accounts Head", ComboBox.class, new ComboBox(),null,null,Table.ALIGN_CENTER);
	        table.addContainerProperty("Balance", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
	        table.setColumnFooter("Balance", "Total:");
	        table.addContainerProperty("Bill Under Process", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
	        table.setColumnWidth("Bill Under Process",110);
	        table.addContainerProperty("Requisition", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
	        table.setColumnWidth("Requisition",70);
	        table.addContainerProperty("Approved", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
	        table.setColumnWidth("Approved",75);
	        tableInitialise();
	        buttonActionAdd();
	        
	        space.setWidth("85px");
	        btnL.addComponent(space);
	        btnL.addComponent(button);
	        btnL.setSpacing(true);
	        vl.setMargin(true);
	        vl.addComponent(table);
	        vl.addComponent(description);
	        description.setRows(2);
	        description.setWidth("715px");
	        mainLayout.addComponent(hL1);
	        mainLayout.addComponent(vl);
	        mainLayout.addComponent(btnL);
	        this.addComponent(mainLayout);
	        
		}
		private void buttonActionAdd()
		{
			
			button.btnNew.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        getWindow().showNotification(event.toString());
			    }
			});
			button.btnEdit.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        
			    }
			});
			button.btnSave.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        
			    }
			});
			button.btnRefresh.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        
			    }
			});
			button.btnDelete.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        
			    }
			});
			button.btnFind.addListener( new ClickListener(){
				public void buttonClick(ClickEvent event) {
			        
			    }
			});
			button.btnExit.addListener( new ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
			        close();
			    }
			});
		}
		private void tableInitialise()
		{	try
			{
				sl = new Label[tr];
				acHead = new ComboBox[tr];
				balance = new TextField[tr];
				billUnderProc = new TextField[tr];
				requisition = new TextField[tr];
				approved = new TextField[tr];
				for(int i=0;i<8;i++){
					sl[i] = new Label(""+(i+1));
					acHead[i] = new ComboBox();
					acHead[i].setWidth("250px");
					balance[i] = new TextField();
					balance[i].setWidth("80px");
					billUnderProc[i] = new TextField();
					billUnderProc[i].setWidth("90px");
					requisition[i] = new TextField();
					requisition[i].setWidth("70px");
					approved[i] = new TextField();
					approved[i].setWidth("70px");
					table.addItem(new Object[]{sl[i],acHead[i],balance[i],billUnderProc[i],requisition[i],approved[i]},i); 
				}
			}catch(Exception exp){
				System.out.println(exp);
			}
		}
	}

