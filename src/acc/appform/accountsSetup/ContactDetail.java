package acc.appform.accountsSetup;

import com.common.share.ExampleUtil;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.Filtering;

@SuppressWarnings("serial")
public class ContactDetail extends VerticalLayout {
	
	private FormLayout formLayout = new FormLayout();
	public TextField address = new TextField("Address:");
	public TextField city = new TextField("City:");
	public TextField postCode = new TextField("Post Code:");
	public TextField stateDiv = new TextField("State Division:");
	public ComboBox country = new ComboBox("Country:", ExampleUtil
            .getISO3166Container());
	public TextField telNo = new TextField("Telephone:");
	public TextField mobNo = new TextField("Mobile No:");
	public TextField fax = new TextField("Fax:");
	public TextField eMail = new TextField("E-Mail:");
	
//	private String w1 = "260px";
//	private String w2 = "180px";
	private String w1 = "30%";
	private String w2 = "20%";
	public ContactDetail(){
		
		formLayout.addComponent(address);
		address.setWidth(w1);
		address.setRows(2);
		formLayout.addComponent(city);
		city.setWidth(w1);
		formLayout.addComponent(postCode);
		formLayout.addComponent(stateDiv);
		stateDiv.setWidth(w1);
		
		formLayout.addComponent(country);
		formLayout.addComponent(telNo);
		telNo.setWidth(w2);
		formLayout.addComponent(mobNo);
		mobNo.setWidth(w2);
		formLayout.addComponent(fax);
		fax.setWidth(w2);
		formLayout.addComponent(eMail);
		eMail.setWidth(w1);
		this.addComponent(formLayout);
		formLayout.setHeight("40%");
		nationalitySet();
	}
	private void nationalitySet(){
		country.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
		country.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

        // Sets the icon to use with the items
		country.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);

        // Set a reasonable width
		country.setWidth(w1);

        // Set the appropriate filtering mode for this example
		country.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
		country.setImmediate(true);
		country.setValue("BD");
       // l.addListener(this);

        // Disallow null selections
		country.setNullSelectionAllowed(false);
		
	}
}
