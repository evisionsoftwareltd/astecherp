package acc.appform.transaction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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


public class ReceiptNonMember extends HorizontalLayout
{
	//private VerticalLayout left = new VerticalLayout();
	private Panel left = new Panel();
	private static final List<String> vFor = Arrays.asList(new String[]{
            "Account","Supplier"});
	private static final List<String> receipt = Arrays.asList(new String[] {
            "Cash","Bank"});
	private Panel right = new Panel();
	public OptionGroup voucherFor = new OptionGroup("Voucher For:", vFor);
    public OptionGroup secDeposit = new OptionGroup("Security Deposit Receipt", receipt);
    
    public DateField date = new DateField("Date:");
    public TextField mrNO = new TextField("MR. No:");
    
    public ComboBox section = new ComboBox("Section:");
    public TextRead voucherNo = new TextRead("Voucher No:");
    
    public ComboBox bankName = new ComboBox("Bank Name:");
    public ComboBox depositAc = new ComboBox("Deposit A/C No:");
    
    public TextField cNo = new TextField("CQ. No:");
    public DateField cdate = new DateField("CQ. Date:");
    
    public ComboBox acHead = new ComboBox("Account Head:");
    public TextField receivedBy = new TextField("Received By:");
    
    public TextField authorizationNo = new TextField("Authorization No:");
    
    public Table table = new Table();
    public TextField particular = new TextField();
    public NumberField amount = new NumberField();

    private HorizontalLayout horLayout = new HorizontalLayout();
    private FormLayout leftFormLayout = new FormLayout();
    private FormLayout rightFormLayout = new FormLayout();
    

	public ReceiptNonMember(){
		
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
        left.addComponent(voucherFor);
        voucherFor.setImmediate(true);
        left.addComponent(secDeposit);
        secDeposit.setImmediate(true);
        
        VerticalLayout rigthtLayout = (VerticalLayout) right.getContent();
        rigthtLayout.setMargin(true); // we want a margin
        rigthtLayout.setSpacing(true);
        this.addComponent(right);
        right.setHeight("335px");
        
        table.setFooterVisible(true);
		table.setWidth("535px");
        table.setHeight("95px");
        
        table.addContainerProperty("Particular", TextField.class, particular,null,null,Table.ALIGN_CENTER);
        particular.setWidth("360px");
        particular.setHeight("45px");
        
        table.setColumnWidth("Particular", 360);
        table.addContainerProperty("Amount", TextField.class, amount,null,null,Table.ALIGN_CENTER);
        table.setColumnWidth("Amount", 135);
        amount.setWidth("135px");
        amount.setHeight("45px");
        amount.setStyleName("fright");
        table.addItem(new Object[]{particular,amount},0); 
        
        
        right.addComponent(horLayout);
        horLayout.addComponent(leftFormLayout);
        horLayout.addComponent(rightFormLayout);
        horLayout.setSpacing(true);
        
        leftFormLayout.addComponent(date);
        rightFormLayout.addComponent(mrNO);
        
        leftFormLayout.addComponent(section);
        rightFormLayout.addComponent(voucherNo);
        
        leftFormLayout.addComponent(acHead);
        rightFormLayout.addComponent(receivedBy);
        
        leftFormLayout.addComponent(bankName);
        rightFormLayout.addComponent(depositAc);
        
        leftFormLayout.addComponent(cNo);
        rightFormLayout.addComponent(cdate);
        
        leftFormLayout.addComponent(authorizationNo); 
        
        right.addComponent(table);
        
        setRadioAction();
        voucherFor.setValue("Account");
        secDeposit.setValue("Cash");
        costCentreIni();
	}
	private void setRadioAction(){
		voucherFor.addListener(new Property.ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(voucherFor.getValue()!=null){
					if(voucherFor.getValue().toString().equalsIgnoreCase("Account")){
						section.setVisible(true);
					}
					else if(voucherFor.getValue().toString().equalsIgnoreCase("Supplier")){
						//bankSelect();
						section.setVisible(false);
					}
				}
			}
		});
		
		secDeposit.addListener(new Property.ValueChangeListener(){
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(secDeposit.getValue()!=null){
					if(secDeposit.getValue().toString().equalsIgnoreCase("Cash")){
						cashSelect();
					}
					else if(secDeposit.getValue().toString().equalsIgnoreCase("Bank")){
						bankSelect();
					}
				}
			}
		});
	}
	private void cashSelect(){
		bankName.setVisible(false);
		depositAc.setVisible(false);
		cNo.setVisible(false);
		cdate.setVisible(false);
		authorizationNo.setVisible(false);
	}
	private void bankSelect(){
		bankName.setVisible(true);
		depositAc.setVisible(true);
		cNo.setVisible(true);
		cdate.setVisible(true);
		authorizationNo.setVisible(false);
		
		cNo.setCaption("CQ. No:");
		cdate.setCaption("CQ. Date:");
	}
	private void creditCardSelect(){
		bankName.setVisible(true);
		depositAc.setVisible(true);
		cNo.setVisible(true);
		cdate.setVisible(true);
		authorizationNo.setVisible(true);
		
		cNo.setCaption("Credit Card No:");
		cdate.setCaption("Date:");
	}
	private void costCentreIni(){
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List group = session.createSQLQuery("select id,costCentreName from tbCostCentre order by costCentreName").list();
			for (Iterator iter = group.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				section.addItem(element[0].toString());
				section.setItemCaption(element[0].toString(), element[1].toString());
			}
			section.setValue(null);
			//section.setNullSelectionAllowed(false);
		}catch(Exception exp){
			this.getParent().getWindow().showNotification(
					"Error",
					exp+"",
					Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
