package acc.appform.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.AmountField;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;


public class ReceiptMember extends HorizontalLayout{
	//private VerticalLayout left = new VerticalLayout();
	private Panel left = new Panel();
	private static final List<String> receipt = Arrays.asList(new String[] {
            "Cash","Bank","Credit Card" });
	private Panel right = new Panel();
	public OptionGroup regReceipt = new OptionGroup("Regular Receipt", receipt);
    public OptionGroup secReceipt = new OptionGroup("Security Deposit Receipt", receipt);
    
    public DateField date = new DateField("Date:");
    public TextField mrNO = new TextField("MR. No:");
    
    public ComboBox memberCode = new ComboBox("Member Code:");
    public TextRead memberName = new TextRead("Member Name:");
    
    public ComboBox memberBank = new ComboBox("Member Bank:");
    public ComboBox depositAc = new ComboBox("Deposit A/C No:");
    
    public ArrayList<Label> sl = new ArrayList<Label>();
    public ArrayList<ComboBox> memberId = new ArrayList<ComboBox>();
    public ArrayList<Label> name = new ArrayList<Label>();
    public ArrayList<AmountField> amt = new ArrayList<AmountField>();
    
    public TextField cNo = new TextField("CQ. No:");
    public DateField cdate = new DateField("CQ. Date:");
    
    public TextField authorizationNo = new TextField("Authorization No:");
    
    public Table table = new Table();
    public TextField particular = new TextField();
    public NumberField amount = new NumberField();

    private HorizontalLayout horLayout = new HorizontalLayout();
    private FormLayout leftFormLayout = new FormLayout();
    private FormLayout rightFormLayout = new FormLayout();
    
    private HashMap midHM = new HashMap();
    
    private boolean isBankSelect = false;
    
	public ReceiptMember(){
		
		date.setValue(new java.util.Date());
	    date.setResolution(PopupDateField.RESOLUTION_DAY);
	    date.setDateFormat("dd-MM-yy");
	    date.setInvalidAllowed(false);
	    date.setImmediate(true);
	    date.setWidth("85px");
	    
	    cdate.setValue(new java.util.Date());
	    cdate.setResolution(PopupDateField.RESOLUTION_DAY);
	    cdate.setDateFormat("dd-MM-yy");
	    cdate.setInvalidAllowed(false);
	    cdate.setImmediate(true);
	    cdate.setWidth("85px");
	    
		//this.setHeight("340px");
		VerticalLayout leftLayout = (VerticalLayout) left.getContent();
		leftLayout.setMargin(true); // we want a margin
		leftLayout.setSpacing(true);
        this.addComponent(left);
        //this.setComponentAlignment(left, Alignment.MIDDLE_CENTER);
        
        left.setHeight("340px");
        Label lbl = new Label();
        left.addComponent(lbl);
        lbl.setHeight("50px");
        left.addComponent(regReceipt);
        regReceipt.setImmediate(true);
        regReceipt.setNullSelectionAllowed(true);
        left.addComponent(secReceipt);
        secReceipt.setImmediate(true);
        secReceipt.setNullSelectionAllowed(true);
        
        VerticalLayout rigthtLayout = (VerticalLayout) right.getContent();
        rigthtLayout.setMargin(true); // we want a margin
        rigthtLayout.setSpacing(true);
        this.addComponent(right);
        right.setHeight("335px");
        
        table.setFooterVisible(true);
		table.setWidth("535px");
        table.setHeight("105px");
        
        table.addContainerProperty("Particular", TextField.class, particular,null,null,Table.ALIGN_CENTER);
        particular.setWidth("360px");
        particular.setHeight("55px");
        
        table.setColumnWidth("Particular", 360);
        table.addContainerProperty("Amount", TextField.class, amount,null,null,Table.ALIGN_CENTER);
        table.setColumnWidth("Amount", 135);
        amount.setWidth("135px");
        amount.setHeight("55px");
        amount.setStyleName("fright");
        table.addItem(new Object[]{particular,amount},0); 
        
        
        right.addComponent(horLayout);
        horLayout.addComponent(leftFormLayout);
        horLayout.addComponent(rightFormLayout);
        horLayout.setSpacing(true);
        
        leftFormLayout.addComponent(date);
        rightFormLayout.addComponent(mrNO);
        mrNO.setWidth("180px");
        
        leftFormLayout.addComponent(memberCode);
        memberCode.setImmediate(true);
        rightFormLayout.addComponent(memberName);
        
        leftFormLayout.addComponent(memberBank);
        rightFormLayout.addComponent(depositAc);
        depositAc.setWidth("180px");
        
        leftFormLayout.addComponent(cNo);
        rightFormLayout.addComponent(cdate);
        
        leftFormLayout.addComponent(authorizationNo); 
        
        right.addComponent(table);
        
        setRadioAction();
        regReceipt.setValue("Cash");
        memberCodeInitialise();
	}
	private void tableInitialise(){
		memberCode.setVisible(true);
		memberName.setVisible(true);
		authorizationNo.setVisible(true);
		
		table.removeAllItems();
		table.removeContainerProperty("SL");
		table.removeContainerProperty("Member Id");
		table.removeContainerProperty("Member Name");
		table.removeContainerProperty("Amount");
		
		table.setFooterVisible(true);
		table.setWidth("535px");
        table.setHeight("105px");
        
        table.addContainerProperty("Particular", TextField.class, particular,null,null,Table.ALIGN_CENTER);
        
        table.setColumnWidth("Particular", 360);
        table.addContainerProperty("Amount", TextField.class, amount,null,null,Table.ALIGN_CENTER);
        table.setColumnWidth("Amount", 135);
        
        table.addItem(new Object[]{particular,amount},0); 
	}
	private void setRadioAction(){
		regReceipt.addListener(new Property.ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(regReceipt.getValue()!=null){
					secReceipt.setValue(null);
					if(regReceipt.getValue().toString().equalsIgnoreCase("Cash")){
					if(isBankSelect)
						tableInitialise();
						cashSelect();
						isBankSelect = false;
					}
					else if(regReceipt.getValue().toString().equalsIgnoreCase("Bank")){
						if(!isBankSelect)
							bankSelect();
					}
					else{
						if(isBankSelect)
							tableInitialise();
						creditCardSelect();
						isBankSelect = false;
					}
				}
			}
		});
		
		secReceipt.addListener(new Property.ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(secReceipt.getValue()!=null){
					regReceipt.setValue(null);
					if(secReceipt.getValue().toString().equalsIgnoreCase("Cash")){
						if(isBankSelect)
							tableInitialise();
						cashSelect();
						isBankSelect = false;
					}
					else if(secReceipt.getValue().toString().equalsIgnoreCase("Bank")){
						if(!isBankSelect)
							bankSelect();
					}
					else {
						if(isBankSelect)
							tableInitialise();
						creditCardSelect();
						isBankSelect = false;
					}
				}
			}
		});
	}
	public void cashSelect(){
		
		memberBank.setVisible(false);
		depositAc.setVisible(false);
		cNo.setVisible(false);
		cdate.setVisible(false);
		authorizationNo.setVisible(false);
		
	}
	private void bankSelect(){
		memberBank.setVisible(true);
		depositAc.setVisible(true);
		cNo.setVisible(true);
		cdate.setVisible(true);
		authorizationNo.setVisible(false);
		
		cNo.setCaption("CQ. No:");
		cdate.setCaption("CQ. Date:");
		
		table.removeAllItems();
		table.removeContainerProperty("Particular");
		table.removeContainerProperty("Amount");
		
		table.addContainerProperty("SL", Integer.class,new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL", 20);
		
		table.addContainerProperty("Member Id", ComboBox.class,new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Member Id", 85);
	    
	    table.addContainerProperty("Member Name", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
	    table.setColumnWidth("Member Name", 260);
	    
	    table.addContainerProperty("Amount", AmountField.class, new AmountField(),null,null,Table.ALIGN_CENTER);
	    table.setColumnWidth("Amount", 80);
	   
		memberCode.setVisible(false);
		memberName.setVisible(false);
		
		table.setHeight("155px");
		sl.clear();
		memberId.clear();
		name.clear();
		amt.clear();
		for(int i=0;i<20;i++){
			tableRowAdd(i);	
		}
		isBankSelect = !isBankSelect;
	}
	private void tableRowAdd(int ar){
		try{
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			List bh = session.createSQLQuery("SELECT mem_ID,mem_Name FROM CCLNEW.dbo.MemberInformation ORDER BY mem_ID").list();
			
			sl.add(ar, new Label((ar+1)+""));
			sl.get(ar).setWidth("20px");
			
			memberId.add(ar, new ComboBox());
			memberId.get(ar).setWidth("85px");
			memberId.get(ar).setImmediate(true);
			
			memberId.get(ar).addItem("x#"+ar);
			memberId.get(ar).setItemCaption("x#"+ar,"");
			memberId.get(ar).setNullSelectionAllowed(false);
			
			for (Iterator iter = bh.iterator(); iter.hasNext();) { 
				Object[] element = (Object[]) iter.next();
				memberId.get(ar).addItem(element[0].toString()+"#"+ar);
				memberId.get(ar).setItemCaption(element[0].toString()+"#"+ar, element[0].toString());
				if(ar==0)
				midHM.put(element[0].toString(), element[1].toString());
			}
			memberId.get(ar).addListener(new ValueChangeListener(){
				@Override
				public void valueChange(ValueChangeEvent event) {
					StringTokenizer st = new StringTokenizer(event.getProperty()+"","#");
					st.nextToken();
					int r = Integer.valueOf(st.nextToken());
					st = new StringTokenizer(memberId.get(r).getValue().toString()+"","#");
					memberIdSelect(st.nextToken(),r);
				}
			});
			name.add(ar, new Label());
			name.get(ar).setWidth("260px");
			name.get(ar).setStyleName("fleft");
			
			amt.add(ar, new AmountField());
			amt.get(ar).setWidth("80px");
			table.addItem(new Object[]{sl.get(ar),memberId.get(ar),name.get(ar),amt.get(ar)},ar); 
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
	private void memberIdSelect(String head,int t){
		if(midHM.get(head)!=null)
			name.get(t).setValue(midHM.get(head));
		else
			name.get(t).setValue("");
	}
	private void creditCardSelect(){
		memberBank.setVisible(true);
		depositAc.setVisible(true);
		cNo.setVisible(true);
		cdate.setVisible(true);
		authorizationNo.setVisible(true);
		
		cNo.setCaption("Credit Card No:");
		cdate.setCaption("Date:");
	}
	private void memberCodeInitialise(){
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			List bh = session.createSQLQuery("SELECT mem_ID,mem_Name FROM CCLNEW.dbo.MemberInformation ORDER BY mem_ID").list();
			
			for (Iterator iter = bh.iterator(); iter.hasNext();) { 
				Object[] element = (Object[]) iter.next();
				memberCode.addItem(element[0].toString());
				midHM.put(element[0].toString(), element[1].toString());
			}
			memberCode.addListener(new ValueChangeListener(){
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(memberCode.getValue()!=null)
						memberName.setValue(midHM.get(memberCode.getValue()+""));
					else
						memberName.setValue("");
				}
			});
		}catch(Exception exp){
			exp.printStackTrace();
		}	
	}
}
