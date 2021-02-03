package acc.appform.hrmModule;

import java.util.ArrayList;

import com.common.share.FocusMoveByEnterForm;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabEducation extends Form
{
	VerticalLayout educationMainLayout = new VerticalLayout();
	VerticalLayout mainLayout = new VerticalLayout();
	VerticalLayout tableLayout=new VerticalLayout();
	VerticalLayout blankLayout=new VerticalLayout();
	HorizontalLayout lowerLayout=new HorizontalLayout();
	ArrayList<Component> allComp = new ArrayList<Component>();

	public Table table = new Table();
	public ArrayList<TextField> tblTxtExam = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtGroup = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtInstitute = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtBoard = new ArrayList<TextField>();
	public ArrayList<TextField> tblTxtDivision = new ArrayList<TextField>();
	public ArrayList<InlineDateField> tblDateYear = new ArrayList<InlineDateField>();

	public Label lblOtherQualification = new Label("Other Qualification:");
	public Label lblComputerSkill = new Label("Computer Skill:");
	public TextArea txtOtherQualification = new TextArea();
	public TextArea txtComputerSkill = new TextArea();

	String smallTextField="80px";
	String largeTextField="200px";
	String comboWidth="160px";

	public TabEducation()
	{
		init();
		addCmp();
		tableInitialise();
		focusMove();
	}

	public void tableInitialise()
	{
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tblTxtExam.add(ar,new TextField());
		tblTxtExam.get(ar).setWidth("100%");
		//tblTxtExam.get(ar).setValue(ar+1);

		tblTxtGroup.add(ar,new TextField());
		tblTxtGroup.get(ar).setWidth("100%");

		tblTxtInstitute.add(ar,new TextField());
		tblTxtInstitute.get(ar).setWidth("100%");

		tblTxtBoard.add(ar,new TextField());
		tblTxtBoard.get(ar).setWidth("100%");

		tblTxtDivision.add(ar,new TextField());
		tblTxtDivision.get(ar).setWidth("100%");

		tblDateYear.add(ar,new InlineDateField());
		tblDateYear.get(ar).setWidth("100%");
		tblDateYear.get(ar).setResolution(InlineDateField.RESOLUTION_YEAR);
		tblDateYear.get(ar).setImmediate(true);

		table.addItem(new Object[]{tblTxtExam.get(ar),tblTxtGroup.get(ar),tblTxtInstitute.get(ar),tblTxtBoard.get(ar),tblTxtDivision.get(ar),tblDateYear.get(ar)},ar);
	}

	public void init()
	{	
		table.setWidth("100%");
		table.setHeight("190px");
		table.addContainerProperty("Exam", TextField.class , new TextField());
		table.setColumnWidth("Exam",100);
		table.addContainerProperty("Group", TextField.class , new TextField());
		table.setColumnWidth("Group",110);
		table.addContainerProperty("Institute", TextField.class , new TextField());
		table.setColumnWidth("Institute",180);
		table.addContainerProperty("Board/University", TextField.class , new TextField());
		table.setColumnWidth("Board/University",200);
		table.addContainerProperty("Div/Class", TextField.class , new TextField());
		table.setColumnWidth("Div/Class",80);
		table.addContainerProperty("Year", InlineDateField.class , new InlineDateField());
		table.setColumnWidth("Year",110);
		table.setColumnCollapsingAllowed(true);

		tableLayout.addComponent(table);

		txtOtherQualification.setWidth("250px");
		txtOtherQualification.setHeight("100px");

		txtComputerSkill.setWidth("250px");
		txtComputerSkill.setHeight("100px");

		mainLayout.setSpacing(true);
	}

	private void focusMove()
	{
		for(int i=0;i<tblTxtExam.size();i++)
		{
			allComp.add(tblTxtExam.get(i));
			allComp.add(tblTxtGroup.get(i));
			allComp.add(tblTxtInstitute.get(i));
			allComp.add(tblTxtBoard.get(i));
			allComp.add(tblTxtDivision.get(i));	
			allComp.add(tblDateYear.get(i));
		}
		allComp.add(txtOtherQualification);
		allComp.add(txtComputerSkill);

		new FocusMoveByEnterForm(this,allComp);
	}

	public void addCmp()
	{
		lowerLayout.addComponent(lblOtherQualification);
		lowerLayout.addComponent(txtOtherQualification);
		lowerLayout.addComponent(lblComputerSkill);
		lowerLayout.addComponent(txtComputerSkill);
		lowerLayout.setSpacing(true);

		blankLayout.setHeight("10px");


		mainLayout.addComponent(tableLayout);
		mainLayout.addComponent(blankLayout);
		mainLayout.addComponent(lowerLayout);
		mainLayout.setComponentAlignment(lowerLayout, Alignment.MIDDLE_CENTER);
		mainLayout.setSpacing(true);

		mainLayout.setWidth("846px");
		mainLayout.setHeight("290px");
		mainLayout.setMargin(false);

		setWidth("820px");
		setHeight("320px");

		educationMainLayout.addComponent(mainLayout);
		educationMainLayout.setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
		getFooter().addComponent(educationMainLayout);
	}
}
