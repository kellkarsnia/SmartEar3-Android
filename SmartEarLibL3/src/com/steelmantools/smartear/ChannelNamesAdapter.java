package com.steelmantools.smartear;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ChannelNamesAdapter extends BaseAdapter implements SpinnerAdapter  {

	private List<String> names;
	private LayoutInflater layoutInflater;
	private List<String> originalNames;
	private Activity activity;
	private ChannelAssignmentDialogFragment frag;

	public ChannelNamesAdapter(Activity activity, ChannelAssignmentDialogFragment frag) {
		this.activity = activity;
		this.frag = frag;
		this.names = ChannelNameHelper.getChannelNames(activity);
		layoutInflater = LayoutInflater.from(activity);
		originalNames = Arrays.asList(activity.getResources().getStringArray(R.array.channel_names));
	}

	static class ViewHolder {
		public TextView nameTextView;
		public Button deleteButton;
	}

	@Override
	public int getCount() {
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_channel_name, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.channelNameText);
			viewHolder.deleteButton = (Button) convertView.findViewById(R.id.deleteChannelNameButton);
			convertView.setTag(viewHolder);
		}

		final String channelName = names.get(position);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.nameTextView.setText(channelName);
		if (!originalNames.contains(channelName)) {
			holder.deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ChannelNameHelper.removeChannelName(ChannelNamesAdapter.this.activity, channelName);
					frag.refreshUI();
				}
			});
			holder.deleteButton.setVisibility(View.VISIBLE);
		} else {
			holder.deleteButton.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public int getPosition(String partName) {
		return names.indexOf(partName);
	}

}
