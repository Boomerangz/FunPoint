package kz.crystalspring.views;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import kz.crystalspring.funpoint.R;
import kz.crystalspring.funpoint.venues.FSQItem;
import kz.crystalspring.funpoint.venues.VenueComment;
import kz.crystalspring.pointplus.ProjectUtils;

public class CommentsWrapper
{
	List<VenueComment> commentsList;
	Context context;

	public CommentsWrapper(FSQItem item, Context context)
	{
		commentsList = item.getOptionalInfo().getCommentsList();
		this.context = context;
	}

	public View getView()
	{
		LinearLayout lv = new LinearLayout(context);
		lv.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lv.setLayoutParams(lp);
		VenueCommentsAdapter adapter=new VenueCommentsAdapter(context, commentsList);
		adapter.fillLayout(lv);
		return lv;
	}
}

class VenueCommentsAdapter
{
	List<VenueComment> data;
	private LayoutInflater mInflater;

	public VenueCommentsAdapter(Context context, List<VenueComment> commentList)
	{
		this.data = commentList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount()
	{
		return data.size();
	}

	public Object getItem(int position)
	{
		return data.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position)
	{
		final ViewHolder holder;
		View convertView = null;
		convertView = mInflater.inflate(R.layout.comment_list_item, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		holder.author = (TextView) convertView.findViewById(R.id.author);
		holder.layout = (View) convertView.findViewById(R.id.comment_layout);
		holder.openMoreButton = (ImageView) convertView
				.findViewById(R.id.more_button);
		holder.dateTime = (TextView) convertView.findViewById(R.id.date_time);
		// convertView.setMinimumHeight(60);
		convertView.setTag(holder);
		holder.author.setText(data.get(position).getAuthor());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

		String sDateTime = ProjectUtils.dateToRelativeString(data.get(position)
				.getCreatedAt());
		holder.dateTime.setText(sDateTime);

		final String fullText = data.get(position).getText();
		final String shortText;
		if (!data.get(position).isLongText())
		{
			shortText = null;
			holder.text.setText(fullText);

		} else
		{
			shortText = data.get(position).getShortText();
			holder.text.setText(shortText);
		}
		if (data.get(position).isLongText())
			holder.openMoreButton.setOnClickListener(new OnClickListener()
			{
				boolean shrt = false;

				@Override
				public void onClick(View v)
				{
					if (shrt)
						holder.text.setText(shortText);
					else
						holder.text.setText(fullText);
					shrt = !shrt;
				}
			});
		else
			holder.openMoreButton.setVisibility(View.GONE);
		return convertView;
	}

	public void fillLayout(LinearLayout layout)
	{
		layout.removeAllViews();
		for (int i = 0; i < getCount(); i++)
			layout.addView(getView(i));
	}

	static class ViewHolder
	{
		TextView author;
		TextView text;
		TextView dateTime;
		ImageView openMoreButton;
		View layout;
	}

}
