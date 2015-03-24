//package com.steelmantools.smartear;
//
//import java.util.List;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnLongClickListener;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.steelmantools.smartear.model.SavedRecording;
//
//public class BluetoothSelectChannelActivity extends FragmentActivity {
//
//	public static final String LOG_TAG = BluetoothSelectChannelActivity.class.getSimpleName();
//
//	private List<PairedDevices> devices;
//	public SavedRecording selectedRecording;
//
//	private ListView recordingsListView;
//	private Button emailButton;
//	private Button deleteButton;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.activity_saved_recordings_screen);
//
//		recordingsListView = (ListView) findViewById(R.id.recordingsListView);
//
//		emailButton = (Button) findViewById(R.id.emailButton);
//		emailButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (selectedRecording != null) {
//					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//					if (selectedRecording.isVideo()) {
//						emailIntent.setType("video/mp4");
//						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SmartEAR 2 MP4 File");
//					} else {
//						emailIntent.setType("audio/wav");
//						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SmartEAR 2 Wave File");
//					}
//					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Created using the SmartEAR 2 App");
//					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selectedRecording.getFile()));
//					startActivity(emailIntent);
//				}
//			}
//		});
//		deleteButton = (Button) findViewById(R.id.deleteButton);
//		deleteButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (selectedRecording != null) {
//					showDeleteConfirmationDialog();
//				}
//			}
//		});
//		deleteButton.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				showDeleteAllConfirmationDialog();
//				return true;
//			}
//		});
//		refreshAdapter();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//	}
//
//	public void refreshAdapter() {
//		//devices = SavedRecording.getAllRecordings();
//		//recordingsListView.setAdapter(new BluetoothSelectAdapter(this, devices));
//	}
//
//	public void refreshClearAdapter() {
//		selectedRecording = null;
//		refreshAdapter();
//	}
//
//	public void showDeleteConfirmationDialog() {
//		DialogFragment newFragment = DeleteDialogFragment.newInstance(selectedRecording, false);
//		newFragment.show(getSupportFragmentManager(), "deleteDialog");
//	}
//
//	public void showDeleteAllConfirmationDialog() {
//		DialogFragment newFragment = DeleteDialogFragment.newInstance(selectedRecording, true);
//		newFragment.show(getSupportFragmentManager(), "deleteDialog");
//	}
//
//	public void itemClicked(View background, int position) {
//		//selectedRecording = devices.get(position);
//		((BluetoothSelectAdapter) recordingsListView.getAdapter()).notifyDataSetChanged();
//	}
//
//	public static class DeleteDialogFragment extends DialogFragment {
//
//		public static DeleteDialogFragment newInstance(SavedRecording selectedRecording, boolean deleteAll) {
//			DeleteDialogFragment frag = new DeleteDialogFragment();
//			Bundle args = new Bundle();
//			args.putSerializable("selectedRecording", selectedRecording);
//			args.putBoolean("deleteAll", deleteAll);
//			frag.setArguments(args);
//			return frag;
//		}
//
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			final SavedRecording selectedRecording = (SavedRecording) getArguments().getSerializable(
//					"selectedRecording");
//			final boolean deleteAll = getArguments().getBoolean("deleteAll", false);
//			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setPositiveButton(
//					android.R.string.ok, new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							if (deleteAll) {
//								for (SavedRecording r : SavedRecording.getAllRecordings()) {
//									try {
//										r.delete();
//									} catch (Exception e) {
//										Log.e(SavedRecordingsActivity.LOG_TAG, "Failure while deleting all recordings",
//												e);
//									}
//								}
//							} else {
//								if (selectedRecording.delete()) {
//									Toast.makeText(getActivity(), "Recording deleted", Toast.LENGTH_SHORT).show();
//								}
//							}
//							((SavedRecordingsActivity) getActivity()).refreshClearAdapter();
//						}
//					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//				}
//			});
//
//			if (deleteAll) {
//				dialogBuilder.setTitle(R.string.DELETE_ALL_RECORDINGS).setMessage(R.string.delete_all_confirmation);
//			} else {
//				dialogBuilder.setTitle(R.string.delete_file).setMessage(selectedRecording.getFile().getName());
//			}
//
//			return dialogBuilder.create();
//		}
//	}
//
//}
