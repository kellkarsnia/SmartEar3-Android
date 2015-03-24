package com.steelmantools.smartear;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.steelmantools.smartear.utils.TunnelPlayerWorkaround;
import com.steelmantools.smartear.video.VideoRecorder;
import com.steelmantools.smartear.visualizer.VisualizerView;
import com.steelmantools.smartear.audio.AudioService;
import com.steelmantools.smartear.logging.Log;
import com.steelmantools.smartear.model.SavedRecording;
import com.steelmantools.smartear.renderer.BarGraphRenderer;
import com.steelmantools.smartear.renderer.CircleBarRenderer;
import com.steelmantools.smartear.renderer.CircleRenderer;
import com.steelmantools.smartear.renderer.LineRenderer;

/**
 * Demo to show how to use VisualizerView
 */
public class GraphPlaybackActivity extends Activity {
  private MediaPlayer mPlayer;
  private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
  private VisualizerView mVisualizerView;
  protected LinearLayout graphLayoutPlayback; // available to do screen grabs
  protected LineGraphView graphView;
  protected GraphViewSeries graphDataSeries1;
  TextView textViewFileName;
  private SavedRecording recording;
  private static String filename;
  private ImageButton pauseButtonPlayback;

  private static File fileSelectedFromAct;
	
    public static final String OUTPUT_PATH;
    static {
            File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            OUTPUT_PATH = musicDir.getAbsolutePath() + "/SmartEar";
    }
    
    private File getFileHardcoded() {
        File rootDir = new File(OUTPUT_PATH);
        rootDir.mkdirs();
//        SharedPreferences prefs = this.getSharedPreferences("SmartearPrefs", Context.MODE_PRIVATE);
//        return new File(rootDir, "smartear_" + dateFormat.format(now) + "." + suffix);
        return new File(rootDir, "smartear_01_27_2014_10_55_39.wav");
}
    
	public static void onRecordClick(File fileSelected, String fileName){
		
		fileSelectedFromAct = fileSelected;
		filename = fileName;
		//getBytesForFile();
	}

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.main);
    setContentView(R.layout.activity_graph_playback_screen);
    
    //filename = recording.getFile().getName();
    textViewFileName = (TextView)findViewById(R.id.textViewFileName);
    textViewFileName.setText(filename);
    
    graphLayoutPlayback = (LinearLayout) findViewById(R.id.graphLayoutPlayback);
    
	graphView = new LineGraphView(this, "");
//	graphView.setDrawBackground(false);
	graphView.setDrawBackground(true);
	graphView.setScrollable(true);
	graphView.setViewPort(0, 20);
	graphView.setScalable(true);
	graphView.setManualYAxisBounds(120, 0);
	graphView.setHorizontalLabels(new String[] {});
	//graphView.setHorizontalLabels(10);
	
	pauseButtonPlayback = (ImageButton)findViewById(R.id.pauseButtonPlayback);
	pauseButtonPlayback.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			togglePlaybackPause();
		}
	});

	GraphViewStyle graphStyle = new GraphViewStyle();
	graphStyle.setGridColor(Color.GRAY);
	graphStyle.setVerticalLabelsColor(Color.RED);
//	graphStyle.setNumHorizontalLabels(100);
	graphStyle.setNumHorizontalLabels(100);

	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
		graphStyle.setTextSize((float) 15);
		graphStyle.setVerticalLabelsWidth(30);
	} else {
		graphStyle.setVerticalLabelsWidth(55);
	}

	graphView.setGraphViewStyle(graphStyle);

	graphLayoutPlayback.addView(graphView);
	
//	graphDataSeries1 = new GraphViewSeries(new GraphViewData[] {});
//	graphDataSeries1.getStyle().color = GlobalSettings.getChannels().get(0).getAndroidColor(this);
//	graphDataSeries1.getStyle().thickness = 8;
    
    
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    initTunnelPlayerWorkaround();
    init();
  }

  @Override
  protected void onPause()
  {
    cleanUp();
    super.onPause();
  }

  @Override
  protected void onDestroy()
  {
    cleanUp();
    super.onDestroy();
  }

  private void init()
  {
//	mPlayer = MediaPlayer.create(this, Uri.fromFile(getFileHardcoded()));
//    mPlayer.setLooping(true);
//    mPlayer.start();
    
	mPlayer = MediaPlayer.create(this, Uri.fromFile(fileSelectedFromAct));
    mPlayer.setLooping(false);
    mPlayer.start();
    
//    mPlayer.setOnCompletionListener(new
//    	    OnCompletionListener() {        
//    	        @Override
//    	        public void onCompletion(MediaPlayer arg0) {
//    	        Intent stopplay= new Intent(GraphPlaybackActivity.this,SavedRecordingsActivity.class);
//    	        startActivity(stopplay);                
//    	    }
//    	});
    
//    mPlayer = MediaPlayer.create(this, Uri.fromFile(file));
//	int duration = mPlayer.getDuration();
//	mPlayer.reset();
//	mPlayer.release();
//
//    // We need to link the visualizer view to the media player so that
//    // it displays something
//    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
//    mVisualizerView.link(mPlayer);
    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
    mVisualizerView.link(mPlayer);
	  //getLengthForVis(this);

    // Start with just line renderer
    //addLineRenderer();
    addBarGraphRenderers();
  }
  
	public String getLengthForVis(Context context) {
//		try {
//			MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(file));
//			int duration = mp.getDuration();
//			mp.reset();
//			mp.release();
//			return String.format("%d m %d s", TimeUnit.MILLISECONDS.toMinutes(duration),
//					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)), Locale.US);
//		} catch (Exception e) {
//			return "";
//		}
//		getFileHardcoded();
//	    mPlayer = MediaPlayer.create(this, Uri.fromFile(file));
		mPlayer = MediaPlayer.create(this, Uri.fromFile(getFileHardcoded()));
		int duration = mPlayer.getDuration();
		mPlayer.reset();
		mPlayer.release();

	    // We need to link the visualizer view to the media player so that
	    // it displays something
	    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
	    mVisualizerView.link(mPlayer);

	    // Start with just line renderer
	    //addLineRenderer();
	    addBarGraphRenderers();
	    
		return String.format("%d m %d s", TimeUnit.MILLISECONDS.toMinutes(duration),
		TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)), Locale.US);
	}

  private void cleanUp()
  {
    if (mPlayer != null)
    {
      mVisualizerView.release();
      mPlayer.release();
      mPlayer = null;
    }
    
    if (mSilentPlayer != null)
    {
      mSilentPlayer.release();
      mSilentPlayer = null;
    }
  }
  
  // Workaround (for Galaxy S4)
  //
  // "Visualization does not work on the new Galaxy devices"
  //    https://github.com/felixpalmer/android-visualizer/issues/5
  //
  // NOTE: 
  //   This code is not required for visualizing default "test.mp3" file,
  //   because tunnel player is used when duration is longer than 1 minute.
  //   (default "test.mp3" file: 8 seconds)
  //
  private void initTunnelPlayerWorkaround() {
    // Read "tunnel.decode" system property to determine
    // the workaround is needed
    if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(this)) {
      mSilentPlayer = TunnelPlayerWorkaround.createSilentMediaPlayer(this);
    }
  }

//  // Methods for adding renderers to visualizer
//  private void addBarGraphRenderers()
//  {
//    Paint paint = new Paint();
//    paint.setStrokeWidth(50f);
//    paint.setAntiAlias(true);
//    paint.setColor(Color.argb(200, 56, 138, 252));
//    BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
//    mVisualizerView.addRenderer(barGraphRendererBottom);
//
//    Paint paint2 = new Paint();
//    paint2.setStrokeWidth(12f);
//    paint2.setAntiAlias(true);
//    paint2.setColor(Color.argb(200, 181, 111, 233));
//    BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
//    mVisualizerView.addRenderer(barGraphRendererTop);
//  }
  
  // Methods for adding renderers to visualizer
  private void addBarGraphRenderers()
  {
//    Paint paint = new Paint();
//    paint.setStrokeWidth(50f);
//    paint.setAntiAlias(true);
//    paint.setColor(Color.argb(200, 56, 138, 252));
//    BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
//    mVisualizerView.addRenderer(barGraphRendererBottom);

    Paint paint = new Paint();
    paint.setStrokeWidth(12f);
    paint.setAntiAlias(true);
//    paint.setColor(Color.argb(200, 181, 111, 233));
    paint.setColor(Color.argb(255, 255, 255, 255));
    //BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
    //mVisualizerView.addRenderer(barGraphRendererTop);
    BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(4, paint, false);
    mVisualizerView.addRenderer(barGraphRendererBottom);
  }
  
  private void togglePlaybackPause() {
		 //pause/start playback
			
			if (mPlayer.isPlaying()) {
	            if (mPlayer != null) {
	            	mPlayer.pause();
	            	pauseButtonPlayback.setImageResource(R.drawable.play);
	            }
	        }else{
	        	mPlayer.start();
	        	pauseButtonPlayback.setImageResource(R.drawable.pause);
	        }
		}
//
//  private void addCircleBarRenderer()
//  {
//    Paint paint = new Paint();
//    paint.setStrokeWidth(8f);
//    paint.setAntiAlias(true);
//    paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
//    paint.setColor(Color.argb(255, 222, 92, 143));
//    CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
//    mVisualizerView.addRenderer(circleBarRenderer);
//  }
//
//  private void addCircleRenderer()
//  {
//    Paint paint = new Paint();
//    paint.setStrokeWidth(3f);
//    paint.setAntiAlias(true);
//    paint.setColor(Color.argb(255, 222, 92, 143));
//    CircleRenderer circleRenderer = new CircleRenderer(paint, true);
//    mVisualizerView.addRenderer(circleRenderer);
//  }

//  private void addLineRenderer()
//  {
//    Paint linePaint = new Paint();
//    linePaint.setStrokeWidth(1f);
//    linePaint.setAntiAlias(true);
//    linePaint.setColor(Color.argb(88, 0, 128, 255));
//
//    Paint lineFlashPaint = new Paint();
//    lineFlashPaint.setStrokeWidth(5f);
//    lineFlashPaint.setAntiAlias(true);
//    lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
//    LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
//    mVisualizerView.addRenderer(lineRenderer);
//  }

  // Actions for buttons defined in xml
  public void startPressed(View view) throws IllegalStateException, IOException
  {
    if(mPlayer.isPlaying())
    {
      return;
    }
    mPlayer.prepare();
    mPlayer.start();
  }

  public void stopPressed(View view)
  {
    mPlayer.stop();
    mPlayer.release();
  }

//  public void barPressed(View view)
//  {
//    addBarGraphRenderers();
//  }
//
//  public void circlePressed(View view)
//  {
//    addCircleRenderer();
//  }
//
//  public void circleBarPressed(View view)
//  {
//    addCircleBarRenderer();
//  }

//  public void linePressed(View view)
//  {
//    addLineRenderer();
//  }

  public void clearPressed(View view)
  {
    mVisualizerView.clearRenderers();
  }
}