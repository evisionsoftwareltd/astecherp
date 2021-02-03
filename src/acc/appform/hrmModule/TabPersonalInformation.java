package acc.appform.hrmModule;

import com.vaadin.ui.VerticalLayout;
import com.common.share.AmountField;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TabPersonalInformation extends VerticalLayout
{
	public AbsoluteLayout mainLayout;
	public TextField txtRelation2;
	public Label lblRelation2;
	public TextField txtNomineeName2;
	public Label lblNomineeName2;
	public Label lblNominee2;
	public TextField txtRelation;
	public Label lblRelation;
	public TextField txtNomineeNane;
	public Label lblNomineeName;
	public Label lblNominee1;
	public AmountField amntNumofChild;
	public Label lblNumofChild;
	public TextField txtSpouseOccupation;
	public Label lblSpouseOccupation;
	public TextField txtSpouseName;
	public Label lblSpouseName;
	public PopupDateField DMarriageDate;
	
	public Label lblMarriageDate;
	public ComboBox cmbMaritalStatus;
	public Label lblMaritalStatus;
	public ComboBox cmbBloodGroup;
	public Label lblBloodGroup;
	public TextArea txtMailing;
	public Label lblMailing;
	public TextArea txtPerAddress;
	public Label lblPerAddress;
	public Label lblMother;
	public TextField txtMotherName;
	public TextField txtFatherName;
	public Label lblFatherName;
	
	private static final String[] blood = new String[] { "A+", "B+", "AB+", "O+", "A-", "B-", "O-", "AB-"};
	private static final String[] marital = new String[] { "Married", "Unmarried", "Divorce"};

	public TabPersonalInformation()
	{
		buildMainLayout();
		addComponent(mainLayout);
	}

	public AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("710px");
		mainLayout.setHeight("320px");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("710px");
		setHeight("320px");
		
		// lblFatherName
		lblFatherName = new Label();
		lblFatherName.setImmediate(false);
		lblFatherName.setWidth("-1px");
		lblFatherName.setHeight("-1px");
		lblFatherName.setValue("Father's Name:");
		mainLayout.addComponent(lblFatherName, "top:24.0px;left:32.0px;");
		
		// txtFatherName
		txtFatherName = new TextField();
		txtFatherName.setImmediate(false);
		txtFatherName.setWidth("223px");
		txtFatherName.setHeight("-1px");
		mainLayout.addComponent(txtFatherName, "top:20.0px;left:117.0px;");
		
		// txtMotherName
		txtMotherName = new TextField();
		txtMotherName.setImmediate(false);
		txtMotherName.setWidth("223px");
		txtMotherName.setHeight("-1px");
		mainLayout.addComponent(txtMotherName, "top:46.0px;left:117.0px;");
		
		// lblMother
		lblMother = new Label();
		lblMother.setImmediate(false);
		lblMother.setWidth("-1px");
		lblMother.setHeight("18px");
		lblMother.setValue("Mother's Name:");
		mainLayout.addComponent(lblMother, "top:48.0px;left:30.0px;");
		
		// lblPerAddress
		lblPerAddress = new Label();
		lblPerAddress.setImmediate(false);
		lblPerAddress.setWidth("-1px");
		lblPerAddress.setHeight("-1px");
		lblPerAddress.setValue("Permanent Address:");
		mainLayout.addComponent(lblPerAddress, "top:92.0px;left:2.0px;");
		
		// txtPerAddress
		txtPerAddress = new TextArea();
		txtPerAddress.setImmediate(false);
		txtPerAddress.setWidth("224px");
		txtPerAddress.setHeight("55px");
		mainLayout.addComponent(txtPerAddress, "top:74.0px;left:116.0px;");
		
		// lblMailing
		lblMailing = new Label();
		lblMailing.setImmediate(false);
		lblMailing.setWidth("-1px");
		lblMailing.setHeight("-1px");
		lblMailing.setValue("Mailing Address:");
		mainLayout.addComponent(lblMailing, "top:145.0px;left:24.0px;");
		
		// txtMailing
		txtMailing = new TextArea();
		txtMailing.setImmediate(false);
		txtMailing.setWidth("223px");
		txtMailing.setHeight("55px");
		mainLayout.addComponent(txtMailing, "top:132.0px;left:117.0px;");
		
		// lblBloodGroup
		lblBloodGroup = new Label();
		lblBloodGroup.setImmediate(false);
		lblBloodGroup.setWidth("-1px");
		lblBloodGroup.setHeight("-1px");
		lblBloodGroup.setValue("Blood Group:");
		mainLayout.addComponent(lblBloodGroup, "top:202.0px;left:44.0px;");
		
		// cmnBloodGroup
		cmbBloodGroup = new ComboBox();
		cmbBloodGroup.setImmediate(false);
		cmbBloodGroup.setWidth("123px");
		cmbBloodGroup.setHeight("-1px");
		addBloodGroup();
		mainLayout.addComponent(cmbBloodGroup, "top:200.0px;left:117.0px;");
		
		// lblMaritalStatus
		lblMaritalStatus = new Label();
		lblMaritalStatus.setImmediate(false);
		lblMaritalStatus.setWidth("-1px");
		lblMaritalStatus.setHeight("-1px");
		lblMaritalStatus.setValue("Marital Status:");
		mainLayout.addComponent(lblMaritalStatus, "top:228.0px;left:38.0px;");
		
		// cmbMaritalStatus
		cmbMaritalStatus = new ComboBox();
		cmbMaritalStatus.setImmediate(false);
		cmbMaritalStatus.setWidth("123px");
		cmbMaritalStatus.setHeight("-1px");
		addMaritalStatus();
		mainLayout.addComponent(cmbMaritalStatus, "top:226.0px;left:117.0px;");
		
		// lblMarriageDate
		lblMarriageDate = new Label();
		lblMarriageDate.setImmediate(false);
		lblMarriageDate.setWidth("-1px");
		lblMarriageDate.setHeight("-1px");
		lblMarriageDate.setValue("Marriage Date:");
		mainLayout.addComponent(lblMarriageDate, "top:251.0px;left:36.0px;");
		
		// DMarriageDate
		DMarriageDate = new PopupDateField();
		DMarriageDate.setImmediate(true);
		DMarriageDate.setWidth("122px");
		DMarriageDate.setHeight("-1px");
		DMarriageDate.setInvalidAllowed(false);
		mainLayout.addComponent(DMarriageDate, "top:251.0px;left:118.0px;");
		
		DMarriageDate.setResolution(PopupDateField.RESOLUTION_DAY);
		DMarriageDate.setDateFormat("dd-MM-yyyy");
		DMarriageDate.setValue(new java.util.Date());
		
		// lblSpouseName
		lblSpouseName = new Label();
		lblSpouseName.setImmediate(false);
		lblSpouseName.setWidth("-1px");
		lblSpouseName.setHeight("-1px");
		lblSpouseName.setValue("Spouse Name:");
		mainLayout.addComponent(lblSpouseName, "top:24.0px;left:400.0px;");
		
		// txtSpouseName
		txtSpouseName = new TextField();
		txtSpouseName.setImmediate(false);
		txtSpouseName.setWidth("217px");
		txtSpouseName.setHeight("-1px");
		mainLayout.addComponent(txtSpouseName, "top:20.0px;left:485.0px;");
		
		// lblSpouseOccupation
		lblSpouseOccupation = new Label();
		lblSpouseOccupation.setImmediate(false);
		lblSpouseOccupation.setWidth("-1px");
		lblSpouseOccupation.setHeight("-1px");
		lblSpouseOccupation.setValue("Spouse Occupation:");
		mainLayout.addComponent(lblSpouseOccupation, "top:48.0px;left:372.0px;");
		
		// txtSpouseOccupation
		txtSpouseOccupation = new TextField();
		txtSpouseOccupation.setImmediate(false);
		txtSpouseOccupation.setWidth("217px");
		txtSpouseOccupation.setHeight("-1px");
		mainLayout.addComponent(txtSpouseOccupation, "top:46.0px;left:485.0px;");
		
		// lblNumofChild
		lblNumofChild = new Label();
		lblNumofChild.setImmediate(false);
		lblNumofChild.setWidth("-1px");
		lblNumofChild.setHeight("-1px");
		lblNumofChild.setValue("Number of Child:");
		mainLayout.addComponent(lblNumofChild, "top:78.0px;left:390.0px;");
		
		// amntNumofChild
		amntNumofChild = new AmountField();
		amntNumofChild.setImmediate(false);
		amntNumofChild.setWidth("134px");
		amntNumofChild.setHeight("-1px");
		mainLayout.addComponent(amntNumofChild, "top:72.0px;left:486.0px;");
		
		// lblNominee1
		lblNominee1 = new Label();
		lblNominee1.setImmediate(false);
		lblNominee1.setWidth("-1px");
		lblNominee1.setHeight("-1px");
		lblNominee1.setValue("Nominee1");
		mainLayout.addComponent(lblNominee1, "top:108.0px;left:486.0px;");
		
		// lblNomineeName
		lblNomineeName = new Label();
		lblNomineeName.setImmediate(false);
		lblNomineeName.setWidth("-1px");
		lblNomineeName.setHeight("-1px");
		lblNomineeName.setValue("Nominee Name:");
		mainLayout.addComponent(lblNomineeName, "top:138.0px;left:392.0px;");
		
		// txtNomineeNane
		txtNomineeNane = new TextField();
		txtNomineeNane.setImmediate(false);
		txtNomineeNane.setWidth("135px");
		txtNomineeNane.setHeight("-1px");
		mainLayout.addComponent(txtNomineeNane, "top:134.0px;left:485.0px;");
		
		// lblRelation
		lblRelation = new Label();
		lblRelation.setImmediate(false);
		lblRelation.setWidth("-1px");
		lblRelation.setHeight("-1px");
		lblRelation.setValue("Relation:");
		mainLayout.addComponent(lblRelation, "top:164.0px;left:434.0px;");
		
		// txtRelation
		txtRelation = new TextField();
		txtRelation.setImmediate(false);
		txtRelation.setWidth("135px");
		txtRelation.setHeight("-1px");
		mainLayout.addComponent(txtRelation, "top:160.0px;left:485.0px;");
		
		// lblNominee2
		lblNominee2 = new Label();
		lblNominee2.setImmediate(false);
		lblNominee2.setWidth("-1px");
		lblNominee2.setHeight("-1px");
		lblNominee2.setValue("Nominee2");
		mainLayout.addComponent(lblNominee2, "top:194.0px;left:485.0px;");
		
		// lblNomineeName2
		lblNomineeName2 = new Label();
		lblNomineeName2.setImmediate(false);
		lblNomineeName2.setWidth("-1px");
		lblNomineeName2.setHeight("-1px");
		lblNomineeName2.setValue("Nominee Name:");
		mainLayout.addComponent(lblNomineeName2, "top:224.0px;left:392.0px;");
		
		// txtNomineeName2
		txtNomineeName2 = new TextField();
		txtNomineeName2.setImmediate(false);
		txtNomineeName2.setWidth("135px");
		txtNomineeName2.setHeight("-1px");
		mainLayout.addComponent(txtNomineeName2, "top:220.0px;left:485.0px;");
		
		// lblRelation2
		lblRelation2 = new Label();
		lblRelation2.setImmediate(false);
		lblRelation2.setWidth("-1px");
		lblRelation2.setHeight("-1px");
		lblRelation2.setValue("Relation:");
		mainLayout.addComponent(lblRelation2, "top:250.0px;left:434.0px;");
		
		// txtRelation2
		txtRelation2 = new TextField();
		txtRelation2.setImmediate(false);
		txtRelation2.setWidth("135px");
		txtRelation2.setHeight("-1px");
		mainLayout.addComponent(txtRelation2, "top:246.0px;left:485.0px;");
		
		return mainLayout;
	}

	private void addBloodGroup()
	{
		for(int i=0;i<blood.length;i++)
		{
			cmbBloodGroup.addItem(blood[i]);
		}
	}
	
	private void addMaritalStatus()
	{
		for(int i=0;i<marital.length;i++)
		{
			cmbMaritalStatus.addItem(marital[i]);
		}
	}

}
