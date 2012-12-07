package com.gt.proto_v01;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Intent;



public class Proto_v01 extends SimpleBaseGameActivity {
	

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	public BuildableBitmapTextureAtlas bmpTextureAtlas;
	
	private ITextureRegion buttonSlideTextureRegion1;
	private ITextureRegion buttonSlideTextureRegion2;
	private ITextureRegion buttonSlideTextureRegion3;
	private ITextureRegion buttonSlideTextureRegion4;
	private ITextureRegion buttonSlideTextureRegion5;
	private ITextureRegion buttonSlideTextureRegion6;
	
	private MenuSlider menuSlider;
	

	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/levelMenu/");

		bmpTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(),305*5, 305*5);
		
		buttonSlideTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level1.png");
		buttonSlideTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level2.png");
		buttonSlideTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level3.png");
		buttonSlideTextureRegion4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level4.png");
		buttonSlideTextureRegion5 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level5.png");
		buttonSlideTextureRegion6 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "level5.png");
		
		try {
			bmpTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			bmpTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene();
		scene.setBackground(new Background(0.3f, 0.3f, 0.3f));

		menuSlider = new MenuSlider(this);
		menuSlider.addItem(buttonSlideTextureRegion1);
		menuSlider.addItem(buttonSlideTextureRegion2);
		menuSlider.addItem(buttonSlideTextureRegion3);
		menuSlider.addItem(buttonSlideTextureRegion4);
		menuSlider.addItem(buttonSlideTextureRegion5);
		menuSlider.addItem(buttonSlideTextureRegion6);
		
		// In this example, buttons have the same size, I place the first item in the middle of the screen
		int xOffset = (int) ((CAMERA_WIDTH - buttonSlideTextureRegion1.getWidth())/2); 
		int yOffset = (int) ((CAMERA_HEIGHT - buttonSlideTextureRegion1.getHeight())/2);
		int gap = 100;
		menuSlider.createMenu(CAMERA_WIDTH, xOffset, yOffset, gap);
		
		

		scene.attachChild(menuSlider);
		menuSlider.onShow(scene);
		
		return scene;
	}
	
	//Method to launch the level clicked in the menu
	public void startLevel(int level){
		Intent intent;
		
		try {
			Class<?> classe = Class.forName("com.gt.proto_v01.Level" + level);
			
			intent = new Intent(Proto_v01.this, classe);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Lauch of Level FAILED");
		}
		
	}
}