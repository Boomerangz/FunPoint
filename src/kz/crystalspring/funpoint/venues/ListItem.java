package kz.crystalspring.funpoint.venues;

import android.view.View;

public interface ListItem
{
	public View getView(View convertView, int position);
	public String getShortCharacteristic();
}
