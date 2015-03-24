package com.steelmantools.smartear;

import java.util.List;



import com.steelmantools.smartear.model.Channel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAssignmentAdapter extends BaseAdapter {

	private List<Channel> channels;
	private LayoutInflater layoutInflater;

	public ChannelAssignmentAdapter(ChannelAssignmentActivity activity, List<Channel> channels) {
		layoutInflater = LayoutInflater.from(activity);
		this.channels = channels;
	}

	static class ViewHolder {
		public TextView number;
		public TextView name;
	}

	@Override
	public int getCount() {
		return channels.size();
	}

	@Override
	public Object getItem(int position) {
		return channels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_channel, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.number = (TextView) convertView.findViewById(R.id.channelNumber);
			viewHolder.name = (TextView) convertView.findViewById(R.id.channelName);
			convertView.setTag(viewHolder);
		}

		Channel channel = channels.get(position);
		int channelColor = channel.getAndroidColor(convertView.getContext());
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.number.setText(channel.getNumber().toString());
		holder.number.setTextColor(channelColor);
		String name = channel.getName();
		if (name != null && !"".equals(name)) {
			holder.name.setText(name);
		} else {
			holder.name.setText(R.string.tap_to_select_location);
		}
		holder.name.setTextColor(channelColor);
		
		return convertView;
	}

}
