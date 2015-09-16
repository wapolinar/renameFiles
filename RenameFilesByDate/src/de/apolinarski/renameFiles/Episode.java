package de.apolinarski.renameFiles;

public class Episode {

	private String title;
	private String number;
	
	public Episode()
	{
		//Do nothing
	}
	
	public Episode(String title, String number)
	{
		setTitle(title);
		setNumber(number);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = DateFile.filenameQuoter(title);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = DateFile.filenameQuoter(number);
	}
	
	@Override
	public String toString() {
		StringBuilder result=new StringBuilder();
		result.append(number);
		result.append(" - ");
		result.append(title);
		return result.toString();
	}
}
