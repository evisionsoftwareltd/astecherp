package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.AmountCommaSeperator;
import com.common.share.AmountField;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabSalaryStructure extends VerticalLayout
{
	public AbsoluteLayout mainLayout;

	public Label lblGross;
	public AmountCommaSeperator txtGross;
	public Label lblBasic;
	public AmountField txtBasic;
	public Label lblHouseRent;
	public AmountField txtHouseRent;
	public Label lblMedical;
	public AmountField txtMedical;
	public Label lblCon;
	public AmountField txtCon;
	public Label lblAttBonus;
	public AmountCommaSeperator txtAttBonus;
	public AmountCommaSeperator txtFridayLunch;

	public Label lblProvidentFund;
	public AmountCommaSeperator txtProvidentFund;
	public Label lblKhichuri;
	public AmountCommaSeperator txtKhichuri;
	public Label lblTax;
	public AmountCommaSeperator txtTax;
	public Label lblInsurance;
	public AmountCommaSeperator txtInsurance;
	public OptionGroup opgBank;
	public List<?> lstBank = Arrays.asList(new String [] {"Bank A/C","Mobile A/C"});
	public Label lblBankName;
	public ComboBox cmbBankName;
	public Label lblBranchName;
	public ComboBox cmbBranchName;
	public Label lblAccountNo;
	public TextField txtAccountNo;

	public AmountCommaSeperator txtDearnessAllowance;
	public AmountCommaSeperator txtFireAllowance;
	String BankFlag = "";

	public String textFieldWidth = "130px";
	ArrayList<Component> allComp = new ArrayList<Component>();	
	public TabSalaryStructure() 
	{
		buildMainLayout();
		addComponent(mainLayout);
		addBankName();
		addBranchName();
		setEventAction();
	}

	public void addBankName()
	{
		cmbBankName.removeAllItems();
		String query = "  select id,bankName from tbBankName order by bankName";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> list = session.createSQLQuery(query).list();	

			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBankName.addItem(element[0].toString());
				cmbBankName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{

		}
		finally{session.close();}
	}

	public void addBranchName(){
		cmbBranchName.removeAllItems();
		String query = "select * from tbBankBranch order by branchName";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		List <?> list = session.createSQLQuery(query).list();	
		try
		{
			for(Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbBranchName.addItem(element[0].toString());
				cmbBranchName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception exp)
		{

		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		opgBank.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				BankFlag = "";
				if(opgBank.getValue()=="Bank A/C")
				{
					lblAccountNo.setValue("Bank Account No : ");
					if(!txtAccountNo.getValue().toString().trim().isEmpty())
						BankFlag = event.getProperty()+"";
				}
				else if(opgBank.getValue()=="Mobile A/C")
				{
					lblAccountNo.setValue("Mobile Account No : ");
					if(!txtAccountNo.getValue().toString().trim().isEmpty())
						BankFlag = event.getProperty()+"";
				}
			}
		});
		txtAccountNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				BankFlag = "";
				if(opgBank.getValue()=="Bank A/C")
				{
					lblAccountNo.setValue("Bank Account No : ");
					if(!txtAccountNo.getValue().toString().trim().isEmpty())
						BankFlag = "Bank A/C";
				}
				else if(opgBank.getValue()=="Mobile A/C")
				{
					lblAccountNo.setValue("Mobile Account No : ");
					if(!txtAccountNo.getValue().toString().trim().isEmpty())
						BankFlag = "Mobile A/C";
				}
			}
		});
	}

	public AbsoluteLayout buildMainLayout() {

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("870px");
		mainLayout.setHeight("325px");
		mainLayout.setMargin(false);

		setWidth("100%");
		setHeight("100%");

		// lblBasic
		lblGross = new Label();
		lblGross.setImmediate(false);
		lblGross.setWidth("100.0%");
		lblGross.setHeight("-1px");
		lblGross.setValue("Gross Salary :");
		mainLayout.addComponent(lblGross,"top:25.0px;left:30.0px;");

		// txtBasic
		txtGross = new AmountCommaSeperator();
		txtGross.setImmediate(true);
		txtGross.setWidth(textFieldWidth);
		txtGross.setHeight("-1px");
		mainLayout.addComponent(txtGross,
				"top:24.0px;right:111.0px;left:177.0px;");

		// lblHouseRent
		lblBasic = new Label();
		lblBasic.setImmediate(false);
		lblBasic.setWidth("100.0%");
		lblBasic.setHeight("-1px");
		lblBasic.setValue("Basic (60 %) :");
		mainLayout.addComponent(lblBasic,"top:50.0px;left:30.0px;");

		// txtHouseRent
		txtBasic = new AmountField();
		txtBasic.setImmediate(true);
		txtBasic.setWidth(textFieldWidth);
		txtBasic.setHeight("24px");
		mainLayout.addComponent(txtBasic,
				"top:49.0px;right:111.0px;left:177.0px;");

		// lblMedical
		lblHouseRent = new Label();
		lblHouseRent.setImmediate(false);
		lblHouseRent.setWidth("100.0%");
		lblHouseRent.setHeight("-1px");
		lblHouseRent.setValue("House Rent (25 %)  :");
		mainLayout.addComponent(lblHouseRent,"top:75.0px;left:30.0px;");

		// txtMedical
		txtHouseRent = new AmountField();
		txtHouseRent.setImmediate(true);
		txtHouseRent.setWidth(textFieldWidth);
		txtHouseRent.setHeight("24px");
		mainLayout.addComponent(txtHouseRent,
				"top:74.0px;right:111.0px;left:177.0px;");

		// lblCon
		lblMedical = new Label();
		lblMedical.setImmediate(false);
		lblMedical.setWidth("100.0%");
		lblMedical.setHeight("-1px");
		lblMedical.setValue("Medical Allowance (7.5 %)  :");
		mainLayout.addComponent(lblMedical,"top:100.0px;left:30.0px;");

		// txtCon
		txtMedical = new AmountField();
		txtMedical.setImmediate(true);
		txtMedical.setWidth(textFieldWidth);
		txtMedical.setHeight("24px");
		mainLayout.addComponent(txtMedical,
				"top:99.0px;right:111.0px;left:177.0px;");

		// lblAttBonus
		lblCon = new Label();
		lblCon.setImmediate(false);
		lblCon.setWidth("100.0%");
		lblCon.setHeight("-1px");
		lblCon.setValue("Conveyance (7.5 %):");
		lblCon.setVisible(true);
		mainLayout.addComponent(lblCon,"top:125.0px;left:30.0px;");

		// txtAttBonus
		txtCon = new AmountField();
		txtCon.setImmediate(true);
		txtCon.setWidth(textFieldWidth);
		txtCon.setHeight("24px");
		mainLayout.addComponent(txtCon,"top:124.0px;right:111.0px;left:177.0px;");

		// lblAttBonus
		lblAttBonus = new Label();
		lblAttBonus.setImmediate(false);
		lblAttBonus.setWidth("100.0%");
		lblAttBonus.setHeight("-1px");
		lblAttBonus.setValue("Attendance Bonus:");
		lblAttBonus.setVisible(true);
		mainLayout.addComponent(lblAttBonus,"top:150.0px;left:30.0px;");

		// txtAttBonus
		txtAttBonus = new AmountCommaSeperator();
		//txtAttBonus.setValue("200.0");
		txtAttBonus.setImmediate(true);
		txtAttBonus.setWidth(textFieldWidth);
		txtAttBonus.setHeight("24px");
		mainLayout.addComponent(txtAttBonus,"top:149.0px;right:111.0px;left:177.0px;");

		txtFridayLunch=new AmountCommaSeperator();
		txtFridayLunch.setImmediate(true);
		txtFridayLunch.setWidth(textFieldWidth);
		txtFridayLunch.setHeight("24px");
		mainLayout.addComponent(new Label("Friday Lunch Allowance :"), "top:174.0px;left:30.0px");
		mainLayout.addComponent(txtFridayLunch,"top:175.0px;left:177.0px");

		// lblProvidentFund
		lblProvidentFund = new Label();
		lblProvidentFund.setImmediate(false);
		lblProvidentFund.setWidth("100.0%");
		lblProvidentFund.setHeight("-1px");
		lblProvidentFund.setValue("Provident Fund :");
		mainLayout.addComponent(lblProvidentFund,"top:25.0px;left:452.0px;");

		// txtProvidentFund
		txtProvidentFund = new AmountCommaSeperator();
		txtProvidentFund.setImmediate(true);
		txtProvidentFund.setWidth(textFieldWidth);
		txtProvidentFund.setHeight("-1px");
		mainLayout.addComponent(txtProvidentFund,
				"top:24.0px;right:111.0px;left:545.0px;");

		// lblTax
		lblTax = new Label();
		lblTax.setImmediate(false);
		lblTax.setWidth("100.0%");
		lblTax.setHeight("-1px");
		lblTax.setValue("Tax:");
		mainLayout.addComponent(lblTax,"top:50.0px;left:460.0px; ");

		// txtTax
		txtTax = new AmountCommaSeperator();
		txtTax.setImmediate(true);
		txtTax.setWidth(textFieldWidth);
		txtTax.setHeight("24px");
		mainLayout.addComponent(txtTax,"top:49.0px;right:111.0px;left:545.0px; ");

		// lblTax
		lblInsurance = new Label();
		lblInsurance.setImmediate(false);
		lblInsurance.setWidth("100.0%");
		lblInsurance.setHeight("-1px");
		lblInsurance.setValue("Insurance:");
		mainLayout.addComponent(lblInsurance,"top:75.0px;left:460.0px; ");

		// txtTax
		txtInsurance = new AmountCommaSeperator();
		txtInsurance.setImmediate(true);
		txtInsurance.setWidth(textFieldWidth);
		txtInsurance.setHeight("24px");
		mainLayout.addComponent(txtInsurance,"top:73.0px;right:111.0px;left:545.0px; ");

		txtDearnessAllowance=new AmountCommaSeperator();
		txtDearnessAllowance.setImmediate(true);
		txtDearnessAllowance.setWidth(textFieldWidth);
		txtDearnessAllowance.setHeight("24px");
		mainLayout.addComponent(new Label("Dearness Allowance : "),"top:99.0px;left:420.0px;");
		mainLayout.addComponent(txtDearnessAllowance, "top:97.0px;left:545.0px");

		txtFireAllowance=new AmountCommaSeperator();
		txtFireAllowance.setImmediate(true);
		txtFireAllowance.setWidth(textFieldWidth);
		txtFireAllowance.setHeight("24px");
		mainLayout.addComponent(new Label("Fire Allowance : "),"top:124.0px;left:445.0px;");
		mainLayout.addComponent(txtFireAllowance, "top:122.0px;left:545.0px");

		opgBank = new OptionGroup("",lstBank);
		opgBank.setImmediate(true);
		opgBank.setStyleName("horizontal");
		opgBank.setValue("Bank A/C");
		mainLayout.addComponent(opgBank, "top:149.0px;left:470.0px;");

		// lblBankName
		lblBankName = new Label();
		lblBankName.setImmediate(false);
		lblBankName.setWidth("100.0%");
		lblBankName.setHeight("-1px");
		lblBankName.setValue("Bank Name :");
		mainLayout.addComponent(lblBankName,"top:174.0px;left:460.0px;");

		// cmbBankName
		cmbBankName = new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setWidth("200px");
		cmbBankName.setHeight("24px");
		mainLayout.addComponent(cmbBankName,"top:172.0px;right:111.0px;left:545.0px;");

		// lblBranchName
		lblBranchName = new Label();
		lblBranchName.setImmediate(false);
		lblBranchName.setWidth("100.0%");
		lblBranchName.setHeight("-1px");
		lblBranchName.setValue("Branch Name :");
		mainLayout.addComponent(lblBranchName,"top:199.0px;left:450.0px;");

		// cmbBranchName
		cmbBranchName = new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setWidth("200px");
		cmbBranchName.setHeight("24px");
		mainLayout.addComponent(cmbBranchName,"top:197.0px;right:111.0px;left:545.0px;");

		// lblAccountNo
		lblAccountNo = new Label();
		lblAccountNo.setImmediate(false);
		lblAccountNo.setWidth("100.0%");
		lblAccountNo.setHeight("-1px");
		lblAccountNo.setValue("Account No :");
		mainLayout.addComponent(lblAccountNo,"top:224.0px;left:430.0px;");

		// txtAccountNo
		txtAccountNo = new TextField();
		txtAccountNo.setImmediate(true);
		txtAccountNo.setWidth("180px");
		txtAccountNo.setHeight("-1px");
		mainLayout.addComponent(txtAccountNo, "top:222.0px;right:111.0px;left:545.0px;");
		return mainLayout;
	}
}