package acc.appform.LcModule;

import com.common.share.SessionBean;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class accessTransaction extends Window
{
	public accessTransaction(SessionBean sessionBean)
	{
		System.out.println("LC Transaction");
		this.setVisible(false);
	}
}
