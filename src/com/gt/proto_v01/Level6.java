//package com.gt.proto_v01;
//
//import org.andengine.engine.camera.Camera;
//import org.andengine.engine.options.EngineOptions;
//import org.andengine.engine.options.ScreenOrientation;
//import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
//import org.andengine.entity.scene.Scene;
//import org.andengine.entity.sprite.Sprite;
//import org.andengine.extension.physics.box2d.PhysicsConnector;
//import org.andengine.extension.physics.box2d.PhysicsFactory;
//import org.andengine.extension.physics.box2d.PhysicsWorld;
//import org.andengine.opengl.texture.TextureOptions;
//import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
//import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
//import org.andengine.opengl.texture.region.TextureRegion;
//import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
//import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
//import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
//import org.andengine.ui.activity.SimpleBaseGameActivity;
//
//import android.util.Log;
//
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

package com.gt.proto_v01;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.content.Intent;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Level6 extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener {

	protected static final int CAMERA_WIDTH = 800;
	protected static final int CAMERA_HEIGHT = 480;

	private BitmapTextureAtlas mBitmapTextureAtlas, bgBitmapTextureAtlas,
			woodboardBitmapTextureAtlas, ropeBitmapTextureAtlas, clapBitmapTextureAtlas;

	private Scene mScene;

	protected ITiledTextureRegion mCircleFaceTextureRegion;
	protected ITiledTextureRegion bgTextureRegion, woodboardTextureRegion, mRopeTextureRegion, clapTextureRegion;

	private ITexture buttonPlayTexture, buttonRestartTexture, projTexture, successTexture;
	private ITextureRegion buttonPlayTextureRegion, buttonRestartTextureRegion, projTextureRegion,
			successTextureRegion;

	protected PhysicsWorld mPhysicsWorld;
	
	private Joint ropeJoin;
	
	 private Sound mVictoireSound, mHitBobineSound, mClapSound;

	Sprite buttonPlay, success, buttonRestart;

	AnimatedSprite asWb1, asWb2, asWb3, asClap;
	Body bWb1, bWb2, bWb3, bClap;
	float xWb1, xWb2, xWb3, yWb1, yWb2, yWb3;
	float wb1Angle, wb2Angle, wb3Angle;

	AnimatedSprite asBobine;
	Body bBobine;
	Body dernierFragmentCorde;
	Sprite spriteDernierFragment;

	boolean levelDone = false;
	
	Rectangle bgSucess;

	float yOnTouchDown = 0;
	float xOnTouchDown = 0;

	boolean wasOnRotatePointWb1 = false;
	boolean wasOnMovePointWb1 = false;
	boolean wasOnRotatePointWb2 = false;
	boolean wasOnMovePointWb2 = false;
	boolean levelPlayed=false;
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        engineOptions.getAudioOptions().setNeedsSound(true);
        
        return engineOptions; 
	}

	@Override
	public void onCreateResources() {
		try {
			this.buttonPlayTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/button_play.png");
						}
					});

			this.buttonPlayTexture.load();
			this.buttonPlayTextureRegion = TextureRegionFactory
					.extractFromTexture(this.buttonPlayTexture);
			
			// Button Restart
			this.buttonRestartTexture = new BitmapTexture(
					this.getTextureManager(), new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/button_restart.png");
						}
					});

			this.buttonRestartTexture.load();
			this.buttonRestartTextureRegion = TextureRegionFactory
					.extractFromTexture(this.buttonRestartTexture);
			// ----
			this.projTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/proj3.png");
						}
					});

			this.projTexture.load();
			this.projTextureRegion = TextureRegionFactory
					.extractFromTexture(this.projTexture);
			// ---
			this.successTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/success.png");
						}
					});

			this.successTexture.load();
			this.successTextureRegion = TextureRegionFactory
					.extractFromTexture(this.successTexture);
		} catch (IOException e) {
			Debug.e(e);
		}

		// ************************
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
		this.bgBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 800, 480, TextureOptions.BILINEAR);

		this.woodboardBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 170, 10, TextureOptions.BILINEAR);
		
		this.ropeBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 5, 5, TextureOptions.BILINEAR);
		
		this.clapBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 50, 52, TextureOptions.BILINEAR);

		// --------

		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"bobine3.png", 0, 0, 1, 1); // 64x32
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bgBitmapTextureAtlas, this,
						"fond_aventure.png", 0, 0, 1, 1); // 64x32
		this.woodboardTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.woodboardBitmapTextureAtlas, this,
						"woodboard.png", 0, 0, 1, 1);
		this.clapTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.clapBitmapTextureAtlas, this,
						"clap.png", 0, 0, 1, 1);
		
		this.mRopeTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.ropeBitmapTextureAtlas, this,
						"corde3.png", 0, 0, 1, 1);

		this.woodboardBitmapTextureAtlas.load();
		this.mBitmapTextureAtlas.load();
		this.bgBitmapTextureAtlas.load();
		this.ropeBitmapTextureAtlas.load();
		this.clapBitmapTextureAtlas.load();
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mVictoireSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "victoire.ogg");
			mVictoireSound.setVolume((float) 0.3);
			this.mHitBobineSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "metal_hit.ogg");
			this.mClapSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "seat_clap.ogg");
			mClapSound.setVolume((float) 0.5);
		} catch (final IOException e) {
			Debug.e(e);
		}

	}

	@Override
	public Scene onCreateScene() {

		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		
		

		mScene.registerUpdateHandler(new TimerHandler(0.1f, true,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						if (levelPlayed && !levelDone) {
							if (bBobine.getPosition().x < 21
									&& bBobine.getPosition().x > 20) {
								if (bBobine.getPosition().y < 13
										&& bBobine.getPosition().y > 12) {
									mScene.attachChild(bgSucess);
									mScene.attachChild(success);
									Level6.this.mVictoireSound.play();
									levelDone = true;
									//Vector2 gravity = new Vector2(0, 0);
									bBobine.setType(BodyType.StaticBody);
									//mPhysicsWorld.setGravity(gravity);
									mScene.detachChild(buttonRestart);
								}
							}
						}
					}

				}));
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();

		final Rectangle inventory = new Rectangle(5, CAMERA_HEIGHT - 50, 500,
				45, vertexBufferObjectManager);
		inventory.setColor(0.2f, 0.2f, 0.2f, 0.5f);
		this.mScene.attachChild(inventory);
		
		bgSucess = new Rectangle(0, 0, 800,
				480, vertexBufferObjectManager);
		bgSucess.setColor(0.0f, 0.0f, 0.0f, 0.6f);

		Sprite bgSprite = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				bgTextureRegion, this.getVertexBufferObjectManager());
		SpriteBackground background = new SpriteBackground(bgSprite);
		mScene.setBackground(background);

		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);

		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 55,
				CAMERA_WIDTH, 1, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2,
				vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT,
				vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2,
				CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
				0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, wallFixtureDef);

		ground.setColor(0, 0, 0, 0);
		roof.setColor(0, 0, 0, 0);
		left.setColor(0, 0, 0, 0);
		right.setColor(0, 0, 0, 0);
		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		// *********************
		// proj
		final Sprite proj = new Sprite(CAMERA_WIDTH - 180, CAMERA_HEIGHT - 120,
				this.projTextureRegion, this.getVertexBufferObjectManager());
		mScene.attachChild(proj);

		// projbody
		final Rectangle projr1 = new Rectangle(CAMERA_WIDTH - 173,
				CAMERA_HEIGHT - 54, 40, 2, vertexBufferObjectManager);
		Body projb1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, projr1,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				projr1, projb1, true, true));
		projr1.setColor(0, 0, 0, 0);
		projb1.setTransform(projb1.getPosition(), (float) 0.78);
		this.mScene.attachChild(projr1);

		final Rectangle projr2 = new Rectangle(CAMERA_WIDTH - 132,
				CAMERA_HEIGHT - 81, 60, 1, vertexBufferObjectManager);
		Body projb2 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, projr2,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				projr2, projb2, true, true));
		projr2.setColor(0, 0, 0, 0);
		projb2.setTransform(projb2.getPosition(), (float) 1.57);
		this.mScene.attachChild(projr2);
		final Rectangle projr3 = new Rectangle(CAMERA_WIDTH - 90,
				CAMERA_HEIGHT - 110, 50, 50, vertexBufferObjectManager);
		Body projb3 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, projr3,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				projr3, projb3, true, true));
		projr3.setColor(0, 0, 0, 0);
		this.mScene.attachChild(projr3);
		
		final Rectangle projr4 = new Rectangle(CAMERA_WIDTH - 70,
				CAMERA_HEIGHT - 120, 10, 60, vertexBufferObjectManager);
		Body projb4 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, projr4,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				projr4, projb4, true, true));
		projr4.setColor(0, 0, 0, 0);
		this.mScene.attachChild(projr4);
		// **********************
		// *** BOBINE *** //
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1,
				0.5f, 0.5f);

		asBobine = new AnimatedSprite(50, 50, this.mCircleFaceTextureRegion,
				this.getVertexBufferObjectManager());
		// face.setScale(MathUtils.random(0.5f, 1.25f));
		bBobine = PhysicsFactory.createCircleBody(this.mPhysicsWorld, asBobine,
				BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(1, 0, 0.5f));
		bBobine.setUserData("bobine");

		this.mScene.attachChild(asBobine);
		
		// ---
		buttonRestart = new Sprite(CAMERA_WIDTH - 120, 40,this.buttonRestartTextureRegion,this.getVertexBufferObjectManager());
		mScene.attachChild(buttonRestart);
		
		buttonPlay = new Sprite(CAMERA_WIDTH - 120, 40,
				this.buttonPlayTextureRegion,
				this.getVertexBufferObjectManager());
		mScene.attachChild(buttonPlay);
		
		success = new Sprite(CAMERA_WIDTH / 2 - 128, CAMERA_HEIGHT / 2 - 128,
				this.successTextureRegion, this.getVertexBufferObjectManager());

		// *********************
		// ** WOOD BOARDS ***//

		asWb1 = new AnimatedSprite(50, CAMERA_HEIGHT - 30,
				this.woodboardTextureRegion,
				this.getVertexBufferObjectManager());
		bWb1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asWb1,
				BodyType.KinematicBody, objectFixtureDef);
		this.mScene.attachChild(asWb1);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb1,
				bWb1, true, true));

		asWb2 = new AnimatedSprite(250, CAMERA_HEIGHT - 30, this.woodboardTextureRegion,
				this.getVertexBufferObjectManager());
		bWb2 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asWb2,
				BodyType.KinematicBody, objectFixtureDef);
		this.mScene.attachChild(asWb2);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb2,
				bWb2, true, true));

		asWb3 = new AnimatedSprite(300, 50, this.woodboardTextureRegion,
				this.getVertexBufferObjectManager());
		bWb3 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asWb3,
				BodyType.KinematicBody, objectFixtureDef);
		this.mScene.attachChild(asWb3);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb3,
				bWb3, true, true));
		
		asClap = new AnimatedSprite(500, 200, this.clapTextureRegion,
				this.getVertexBufferObjectManager());
		bClap = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asClap,
				BodyType.KinematicBody, objectFixtureDef);
		bClap.setUserData("clap");
		this.mScene.attachChild(asClap);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb3,
				bWb3, true, true));
		
//		wb3Angle = (float) 0.37;
//		bWb3.setTransform(bWb3.getPosition(), wb3Angle);
		
		Body corde = makeRope(50, 370, 50);
//		mScene.detachChild(corde);
//		asWb3.attachChild(corde);
		joinRopeBodies(bWb3, corde, mRopeTextureRegion.getHeight());

		return this.mScene;
	}
	
	@Override
	public synchronized void onGameCreated() {
		this.mPhysicsWorld.setContactListener(createContactListener());
		super.onGameCreated();
	}

	public Body makeRope(int links, float x, float y) {
//		mPhysicsWorld.setContinuousPhysics(false);
	    final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(0.7f, -1f, 1f);
	    Sprite l1 = new Sprite(x, y, mRopeTextureRegion.getWidth(), mRopeTextureRegion.getHeight(), mRopeTextureRegion, this.getVertexBufferObjectManager());
	 
	    Body b1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, l1, BodyType.DynamicBody, objectFixtureDef);
	    Body premierLien = b1;
	    
	    this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(l1, b1, true, true));
	    this.mScene.attachChild(l1);
	               
	    final float linkHeight = mRopeTextureRegion.getHeight();
	    for (int i = 1; i < links; ++i) {
	        Sprite l2 = new Sprite(x, y + (i * linkHeight), mRopeTextureRegion.getWidth(), linkHeight, mRopeTextureRegion, this.getVertexBufferObjectManager());
	        Body b2 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, l2, BodyType.DynamicBody, objectFixtureDef);
	        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(l2, b2, true, true));
	        this.mScene.attachChild(l2);
	        
	        
	        joinRopeBodies(b1, b2, linkHeight);
	 
	        b1 = b2;
	        l1 = l2;
	        spriteDernierFragment = l2;
	    }
	    
	    //dernierFragmentCorde = PhysicsFactory.createBoxBody(this.mPhysicsWorld, l1, BodyType.DynamicBody, objectFixtureDef);
	    b1.setUserData("dernierFragmentCorde");
	    
	    return premierLien;
	}
	 
	public void joinRopeBodies(Body chainLinkBody1, Body chainLinkBody2, float bodyHeight) {
	    // FIRST CREATE TWO BODIES, THEN USE THIS CODE TO JOIN THEM TOGETHER
	    RevoluteJointDef chainLinkDef = new RevoluteJointDef();
	    chainLinkDef.collideConnected = true;
	    chainLinkDef.initialize(chainLinkBody1, chainLinkBody2, chainLinkBody2.getWorldCenter());
	    // NEXT IS DISTANCE OF ANCHOR AWAY FROM CENTER OF PREVIOUS BODY
	    // USUALLY EQUALS PREVIOUS BODY LENGTH
	    chainLinkDef.localAnchorA.set(0.0f, ((bodyHeight/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
	    // NEXT IS DISTANCE OF ANCHOR AWAY FROM CENTER OF THIS BODY
	    // USUALLY EQUALS THIS BODY LENGTH
	    //chainLinkDef.localAnchorB.set(0.0f, -((bodyHeight/4) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
	    chainLinkDef.enableMotor = false;
	    chainLinkDef.motorSpeed = 0;
	    chainLinkDef.enableLimit = false;
	    chainLinkDef.lowerAngle = MathUtils.degToRad(0);
	    //chainLinkDef.upperAngle = MathUtils.degToRad(180);
	    chainLinkDef.upperAngle = MathUtils.degToRad(90);
	    // NOW THAT WE DEFINED THE JOINT, HERE WE ACTUALLY CREATE THE JOINT
	    mPhysicsWorld.createJoint(chainLinkDef);
	}
	
	public void attachBobineToRope(Body dernierFragment, Body bobine, float hauteurBody){
		RevoluteJointDef chainLinkDef2 = new RevoluteJointDef();
		chainLinkDef2.localAnchorA.set(0.0f, ((hauteurBody/4) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
		chainLinkDef2.initialize(dernierFragment, bobine, bobine.getWorldCenter());
	    // NEXT IS DISTANCE OF ANCHOR AWAY FROM CENTER OF THIS BODY
	    // USUALLY EQUALS THIS BODY LENGTH
	    chainLinkDef2.localAnchorB.set(0.0f, -((hauteurBody/4) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
	    ropeJoin = mPhysicsWorld.createJoint(chainLinkDef2);
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionDown()) {
				float x, y, xW, yW;

				float positionX = (asWb1.getX() + asWb1.getRotationCenterX())
						+ (float) (Math
								.cos(Math.toRadians(asWb1.getRotation()))
								* asWb1.getWidth() / 2);
				float positionY = (asWb1.getY() + asWb1.getRotationCenterY())
						+ (float) (Math
								.sin(Math.toRadians(asWb1.getRotation()))
								* asWb1.getWidth() / 2);

				x = asWb1.getX();
				y = asWb1.getY();
				xW = asWb1.getWidth();
				yW = asWb1.getHeight();
				if (pSceneTouchEvent.getX() < positionX + 20
						&& pSceneTouchEvent.getX() > positionX - 30) {
					if (pSceneTouchEvent.getY() > positionY - 20
							&& pSceneTouchEvent.getY() < positionY + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
						xOnTouchDown = pSceneTouchEvent.getX();
						wasOnRotatePointWb1 = true;
					}
				} else {
					if (pSceneTouchEvent.getX() > x + 30
							&& pSceneTouchEvent.getX() < x + xW - 30) {
						if (pSceneTouchEvent.getY() > y - 20
								&& pSceneTouchEvent.getY() < y + yW + 20) {
							wasOnMovePointWb1 = true;
						}
					}
				}

				float positionX2 = (asWb2.getX() + asWb2.getRotationCenterX())
						+ (float) (Math
								.cos(Math.toRadians(asWb2.getRotation()))
								* asWb2.getWidth() / 2);
				float positionY2 = (asWb2.getY() + asWb2.getRotationCenterY())
						+ (float) (Math
								.sin(Math.toRadians(asWb2.getRotation()))
								* asWb2.getWidth() / 2);

				x = asWb2.getX();
				y = asWb2.getY();
				xW = asWb2.getWidth();
				yW = asWb2.getHeight();
				if (pSceneTouchEvent.getX() < positionX2 + 20
						&& pSceneTouchEvent.getX() > positionX2 - 30) {
					if (pSceneTouchEvent.getY() > positionY2 - 20
							&& pSceneTouchEvent.getY() < positionY2 + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
						xOnTouchDown = pSceneTouchEvent.getX();
						wasOnRotatePointWb2 = true;
					}
				} else {
					if (pSceneTouchEvent.getX() > x + 30
							&& pSceneTouchEvent.getX() < x + xW - 30) {
						if (pSceneTouchEvent.getY() > y - 20
								&& pSceneTouchEvent.getY() < y + yW + 20) {
							wasOnMovePointWb2 = true;
						}
					}
				}
				
				// play level and after restart
				if (pSceneTouchEvent.getX() > CAMERA_WIDTH - 120
						&& pSceneTouchEvent.getX() < CAMERA_WIDTH - 40) {
					if (pSceneTouchEvent.getY() > 40
							&& pSceneTouchEvent.getY() < 120) {
						if (!levelPlayed) {
							Vector2 gravity = new Vector2(0,
									SensorManager.GRAVITY_EARTH);
							this.mPhysicsWorld.setGravity(gravity);
							mScene.detachChild(buttonPlay);
							this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
									asBobine, bBobine, true, true));
							levelPlayed = true;
							
							
						}else if (levelDone){
							//if the level is done, no action is needed
							//cannot restart the level anymore
						}
						else { // to restart
							Intent intent = getIntent();
							finish();
							startActivity(intent);
						}
					}
				}
				
				// when the level is finished, touch the clap to continue
				else if(levelDone && pSceneTouchEvent.getX() > CAMERA_WIDTH/2 - 128
						&& pSceneTouchEvent.getX() < CAMERA_WIDTH/2 + 128){
					if (pSceneTouchEvent.getY() > CAMERA_HEIGHT/2 -128
							&& pSceneTouchEvent.getY() < CAMERA_HEIGHT/2 + 128) {
						//startNextLevel(); NOT IMPLEMENTED COZ THIS IS THE LAST LEVEL
					}
				}

				return true;
			} else {
				if (pSceneTouchEvent.isActionMove() && !levelPlayed) {
					if (wasOnMovePointWb1){
							bWb1.setTransform(
									pSceneTouchEvent.getX()
											/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
									pSceneTouchEvent.getY()
											/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
									bWb1.getAngle());
						
					} else {

						if (wasOnRotatePointWb1) {
							float pValueX = pSceneTouchEvent.getX();
					        float pValueY = CAMERA_HEIGHT - pSceneTouchEvent.getY();

					        float directionX = pValueX - asWb1.getX();
					        float directionY = (CAMERA_HEIGHT - pValueY) - asWb1.getY();

					        float rotationAngle = (float) Math.atan2(directionY, directionX);

					        bWb1.setTransform(bWb1.getPosition(), rotationAngle);

						}
						else{
							if (wasOnMovePointWb2){
								bWb2.setTransform(
										pSceneTouchEvent.getX()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
										pSceneTouchEvent.getY()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
										bWb2.getAngle());
							
						} else {

							if (wasOnRotatePointWb2) {
								float pValueX = pSceneTouchEvent.getX();
						        float pValueY = CAMERA_HEIGHT - pSceneTouchEvent.getY();

						        float directionX = pValueX - asWb2.getX();
						        float directionY = (CAMERA_HEIGHT - pValueY) - asWb2.getY();

						        float rotationAngle = (float) Math.atan2(directionY, directionX);

						        bWb2.setTransform(bWb2.getPosition(), rotationAngle);

							}
						}
						}
					}
					
				} else {
					if (pSceneTouchEvent.isActionUp()) {
						wasOnRotatePointWb1 = false;
						wasOnMovePointWb1 = false;
						wasOnRotatePointWb2 = false;
						wasOnMovePointWb2 = false;
					}
				}
			}
		}

		return false;
	}
	
	private ContactListener createContactListener(){
    	ContactListener contactListener = new ContactListener(){
    		public void beginContact(Contact contact){
	            
	            /* rope */
    			final Fixture x1 = contact.getFixtureA();
    			final Fixture x2 = contact.getFixtureB();
    			
    			if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
    				if(x1.getBody().getUserData().equals("dernierFragmentCorde") && x2.getBody().getUserData().equals("bobine")){
    					//mScene.detachChild(asBobine);
    					runOnUpdateThread(new Runnable() {
							public void run(){
								attachBobineToRope(x2.getBody(), x1.getBody(), mRopeTextureRegion.getHeight());
							}
						});
        			}
    			} 
    			if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
    				if(ropeJoin != null && x1.getBody().getUserData().equals("clap") && x2.getBody().getUserData().equals("bobine")){
    					//mScene.detachChild(asBobine);
    					runOnUpdateThread(new Runnable() {
							public void run(){
								mPhysicsWorld.destroyJoint(ropeJoin);
								if(!mClapSound.isReleased()){
									mClapSound.play();
				            	}
							}
						});
        			}
    			}
    			
//    			/* bobine hit sound */
//				if(contact.getFixtureA().equals(asWb1));
//	            {
//	            	
//	            	if(!mHitBobineSound.isReleased()){
//	            		mHitBobineSound.setVolume((float) 1.0 * bBobine.getLinearVelocity().len2()/10);
//	            		mHitBobineSound.play();
//	            	}
//	            }
    		}
    		
    		@Override
			public void endContact(Contact contact) {
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
    	};
			
			return contactListener;
    	}

	@Override
	public void onAccelerationAccuracyChanged(
			final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
