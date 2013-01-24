package com.gt.proto_v01;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.view.KeyEvent;



public class Proto_v01 extends BaseGameActivity{
	private final int CAMERA_WIDTH = 800;
	private final int CAMERA_HEIGHT = 480;
	
	int xOffset; 
	int yOffset;
	int gap;
	
	private Camera camera;
	private Scene splashScene;
	private Scene mainScene;
	
    private BitmapTextureAtlas splashTextureAtlas, bgBitmapTextureAtlas;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;
    
	private enum SceneType
	{
		SPLASH,
		MAIN,
		OPTIONS,
		WORLD_SELECTION,
		LEVEL_SELECTION,
		CONTROLLER
	}
	
	private ITextureRegion buttonSlideTextureRegion1;
	private ITextureRegion buttonSlideTextureRegion2;
	private ITextureRegion buttonSlideTextureRegion3;
	private ITextureRegion buttonSlideTextureRegion4;
	private ITextureRegion buttonSlideTextureRegion5;
	private ITextureRegion buttonSlideTextureRegion6;
	
	public BuildableBitmapTextureAtlas bmpTextureAtlas;
	protected ITextureRegion bgTextureRegion;
	private MenuSlider menuSlider;
	
	private SceneType currentScene = SceneType.SPLASH;
	
	boolean gamePaused=false;
	
	 private Sound menuSound;
	 static int callCounts = 0;
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		engineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 480, TextureOptions.DEFAULT);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "splash.png", 0, 0);
        splashTextureAtlas.load();
       
        pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		initSplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                loadResources();
                loadScenes();         
                splash.detachSelf();
                mEngine.setScene(mainScene);
                currentScene = SceneType.MAIN;
                mainScene.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
        		{
                    public void onTimePassed(final TimerHandler pTimerHandler) 
                    {
                        mEngine.unregisterUpdateHandler(pTimerHandler);
                        menuSound.play(); 
                    }
        		}));
            }
		}));
  
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
	    {	    	
	    	switch (currentScene)
	    	{
	    		case SPLASH:
	    			break;
	    		case MAIN:
	    			System.exit(0);
	    			break;
	    	}
	    }
	    return false; 
	}
	
	public void loadResources() 
	{
		// Load your game resources here!
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		bmpTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(),501*5, 312*5);
		
		buttonSlideTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level1.png");
		buttonSlideTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level2.png");
		buttonSlideTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level3.png");
		buttonSlideTextureRegion4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level4.png");
		buttonSlideTextureRegion5 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level5.png");
		buttonSlideTextureRegion6 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bmpTextureAtlas, this, "levelMenu/level6.png");
		try {
			bmpTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			bmpTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.bgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 480, TextureOptions.BILINEAR);
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bgBitmapTextureAtlas, this,"menu_bg_800x480.png", 0, 0, 1, 1); // 64x32
		this.bgBitmapTextureAtlas.load();
		
		//the sound for the menu
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.menuSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "menu2.mp3");
			this.menuSound.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	private void loadScenes()
	{
		// load your game here, your scenes
		mainScene = new Scene();
		//load background for the menu
		Sprite bgSprite = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, bgTextureRegion, this.getVertexBufferObjectManager());
		SpriteBackground background = new SpriteBackground(bgSprite);
		mainScene.setBackground(background);
		
		//create the MenuSlider with the buttons
		menuSlider = new MenuSlider(this);
		menuSlider.addItem(buttonSlideTextureRegion1);
		menuSlider.addItem(buttonSlideTextureRegion2);
		menuSlider.addItem(buttonSlideTextureRegion3);
		menuSlider.addItem(buttonSlideTextureRegion4);
		menuSlider.addItem(buttonSlideTextureRegion5);
		menuSlider.addItem(buttonSlideTextureRegion6);
		
		// place the first button in the middle of the screen, with a gap between two buttons of 50px
		xOffset = (int) ((CAMERA_WIDTH - buttonSlideTextureRegion1.getWidth())/2); 
		yOffset = (int) ((CAMERA_HEIGHT - buttonSlideTextureRegion1.getHeight())/2);
		gap = 50;
		menuSlider.createMenu(CAMERA_WIDTH, xOffset, yOffset, gap);
		
		mainScene.attachChild(menuSlider);
		menuSlider.onShow(mainScene);
	}

	//self-explicit ;-)
	private void initSplashScene()
	{
		Debug.d("InitSplashScene", "begin!");
    	splashScene = new Scene();
    	splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager())
    	{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
    	};
    	splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
    	splashScene.attachChild(splash);
	}

	//Method to launch the level clicked in the menu
	public void startLevel(int level){
		Intent intent;
		try {
			//creating the name of the class to be launched with the param passed from MenuSlider.java
			Class<?> classe = Class.forName("com.gt.proto_v01.Level" + level);
			
			intent = new Intent(Proto_v01.this, classe);
			startActivity(intent);
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Lauch of Level FAILED");
		}
	}
	
	@Override
	public void onResumeGame() {
		callCounts++;
		if(gamePaused){
			
			menuSlider = new MenuSlider(this);
			menuSlider.addItem(buttonSlideTextureRegion1);
			menuSlider.addItem(buttonSlideTextureRegion2);
			menuSlider.addItem(buttonSlideTextureRegion3);
			menuSlider.addItem(buttonSlideTextureRegion4);
			menuSlider.addItem(buttonSlideTextureRegion5);
			menuSlider.addItem(buttonSlideTextureRegion6);
			
			// place the first button in the middle of the screen, with a gap between two buttons of 50px
			menuSlider.createMenu(CAMERA_WIDTH, xOffset, yOffset, gap);
			
			mainScene.attachChild(menuSlider);
			menuSlider.onShow(mainScene);
		
			try{
				menuSound.resume();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		

		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		menuSlider.onHide(mainScene);
		gamePaused=true;
		menuSlider=null;
		menuSound.pause();
	}
}