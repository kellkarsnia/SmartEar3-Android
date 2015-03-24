package com.steelmantools.smartear;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProcessingDialogFragment extends DialogFragment {

	public static ProcessingDialogFragment newInstance() {
		ProcessingDialogFragment fragment = new ProcessingDialogFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 getDialog().setTitle(R.string.encoding_video);
		 View root = inflater.inflate(R.layout.fragment_processing, null);
		 return root;
	 }

}
