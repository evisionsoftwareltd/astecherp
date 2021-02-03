package com.example.productionSetup;

import com.common.share.SessionBean;
import com.vaadin.ui.Window;

public class productionTransaction extends Window
{
	public productionTransaction(SessionBean sessionBean)
	{
		System.out.println("Production Transaction");
		this.setVisible(false);
	}
}
