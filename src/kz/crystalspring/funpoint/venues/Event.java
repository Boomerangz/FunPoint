package kz.crystalspring.funpoint.venues;
import android.database.Cursor;
public class Event
{
	private static final String COLUMN_NAME="title";
	private static final String COLUMN_DESC="description";
	
	
	
	private String name;
	private String description;
	
	Event(String name, String description)
	{
		setName(name);
		setDescription(description);
	}
	
	Event ()
	{
	}
	
	Event (Cursor cursor) throws Exception
	{
		String[] columnNames=cursor.getColumnNames();
		String nm=null;
		String dsc=null;
		int i=0;
		for (String columName:columnNames)
		{
			if (columName.trim().toUpperCase().equals(COLUMN_NAME.toUpperCase().trim()))
			{
				nm=cursor.getString(i);
			}
			if (columName.trim().toUpperCase().equals(COLUMN_DESC.toUpperCase().trim()))
			{
				dsc=cursor.getString(i);
			}
			i++;
		}
		if (nm!=null&&dsc!=null)
		{
			setName(nm);
			setDescription(dsc);
		}
		else 
		{
			throw new Exception("Name or Description not found in cursor");
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	
}
