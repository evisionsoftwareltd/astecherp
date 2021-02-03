package acc.appform.accountsSetup;

import com.common.share.ExampleUtil;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AcOpenOtherInfo extends VerticalLayout {
	
	private HorizontalLayout hLayout = new HorizontalLayout();
	private VerticalLayout lvLayout = new VerticalLayout();
	private VerticalLayout rvLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	
	private Panel resLayout = new Panel("Residency :");
	public CheckBox rseident = new CheckBox("Resident");
	public CheckBox nonReseident = new CheckBox("Non Resident");
	
	public ComboBox nationality = new ComboBox("Nationality:", ExampleUtil
            .getISO3166Container());
	private PopupDateField birthDate = new PopupDateField("Birth Date:");
	
	private Panel cycleCodePan = new Panel("Statement Cycle Code:");
	public CheckBox daily = new CheckBox("Daily");
	public CheckBox weekly = new CheckBox("Weekly");
	public CheckBox fortnightly = new CheckBox("Fortnightly");
	public CheckBox monthly = new CheckBox("Monthly");
	
	public TextField intRefNo = new TextField("International Ref. No.:");
	
	private Panel caseOfComPan = new Panel("In Case of Company-");
	private FormLayout caseComFormLayout = new FormLayout();
	public TextField regNo = new TextField("Registration No.:");
	public PopupDateField regDate = new PopupDateField("Date of Rgistration:");
	
	private FormLayout commisionFormLayout = new FormLayout();
	private Panel rPanel = new Panel("Commision Rate Information:");
	public TextField comCharge = new TextField("Commission Charge:");
	public TextField minCharge = new TextField("Minimum Charge:");
	public TextField crdLimit = new TextField("Credit Limit:");
	
	private String commWidth = "220px";
	
	@SuppressWarnings("deprecation")
	public AcOpenOtherInfo(){
		
		resLayout.setLayout(new HorizontalLayout());
		
		resLayout.addComponent(rseident);
		resLayout.addComponent(nonReseident);
		nationalitySet();
		formLayout.addComponent(resLayout);
	    formLayout.addComponent(nationality);
	    
	    formLayout.addComponent(birthDate);
	    birthDate.setValue(new java.util.Date());
	    birthDate.setResolution(PopupDateField.RESOLUTION_DAY);
	    birthDate.setDateFormat("dd-MM-yy");
	    birthDate.setImmediate(true);
	    //birthDate.setLocale(new Locale("en", "GB"));
	    
	    cycleCodePan.setLayout(new HorizontalLayout());
	    cycleCodePan.addComponent(daily);
	    cycleCodePan.addComponent(weekly);
	    cycleCodePan.addComponent(fortnightly);
	    cycleCodePan.addComponent(monthly);
	    
	    formLayout.addComponent(cycleCodePan);
	    intRefNo.setWidth(commWidth);
	    formLayout.addComponent(intRefNo);
	    
	    caseComFormLayout.addComponent(regNo);
	    regNo.setWidth(commWidth);
	    caseComFormLayout.addComponent(regDate);
	    caseOfComPan.addComponent(caseComFormLayout);
	    regDate.setValue(new java.util.Date());
	    regDate.setResolution(PopupDateField.RESOLUTION_DAY);
	    regDate.setDateFormat("dd-MM-yy");
	    regDate.setImmediate(true);
	    //regDate.setLocale(new Locale("en", "GB"));
	    
	    //issueDate.setWidth(commonWidth);
	    rvLayout.setMargin(true);
	    commisionFormLayout.addComponent(comCharge);
	    commisionFormLayout.addComponent(minCharge);
	    commisionFormLayout.addComponent(crdLimit);
	    
	    rPanel.addComponent(commisionFormLayout);
	    
	    rvLayout.addComponent(rPanel);
	    
	   //hLayout.setSpacing(true);
	    HorizontalLayout midSpace = new HorizontalLayout();
	    midSpace.setWidth("40px");
	    lvLayout.addComponent(formLayout);
	    lvLayout.addComponent(caseOfComPan);
	    hLayout.addComponent( lvLayout);
	    hLayout.addComponent(midSpace);
	    hLayout.addComponent( rvLayout);
	    this.addComponent(hLayout);

	}
	private void nationalitySet(){
		nationality.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
		nationality.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

        // Sets the icon to use with the items
		nationality.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);

        // Set a reasonable width
		nationality.setWidth(220, UNITS_PIXELS);

        // Set the appropriate filtering mode for this example
		nationality.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
		nationality.setImmediate(true);
		nationality.setValue("BD");
       // l.addListener(this);

        // Disallow null selections
		nationality.setNullSelectionAllowed(false);
		
	}
}
