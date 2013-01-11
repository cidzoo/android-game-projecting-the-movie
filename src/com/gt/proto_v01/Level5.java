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

import android.content.Intent;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Level5 extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener {

	protected static final int CAMERA_WIDTH = 800;
	protected static final int CAMERA_HEIGHT = 480;

	private BitmapTextureAtlas mBitmapTextureAtlas, bgBitmapTextureAtlas,
			woodboardBitmapTextureAtlas, seatMpBitmapTextureAtlas;

	private Scene mScene;

	// -- for a new physics object from png :
	protected ITiledTextureRegion speakersTextureRegion;
	private BitmapTextureAtlas speakersBitmapTextureAtlas;
	private ITexture speakersTexture;
	AnimatedSprite asSpeakers;
	Body bSpeakers;
	// --------------------------------------

	protected ITiledTextureRegion mCircleFaceTextureRegion;
	protected ITiledTextureRegion bgTextureRegion, woodboardTextureRegion,
			seatMpTextureRegion;

	private ITexture buttonPlayTexture, buttonRestartTexture, projTexture,
			successTexture, seatTexture;
	private ITextureRegion buttonPlayTextureRegion, buttonRestartTextureRegion,
			projTextureRegion, successTextureRegion, seatTextureRegion;

	protected PhysicsWorld mPhysicsWorld;

	 private Sound mVictoireSound;

	Sprite buttonPlay, success, buttonRestart;

	AnimatedSprite asWb1, asWb2, asWb3, asSeatMp;
	Body bWb1, bWb2, bWb3, bSeatMp;
	float xWb1, xWb2, xWb3, yWb1, yWb2, yWb3;
	float wb1Angle, wb2Angle, wb3Angle;

	AnimatedSprite asBobine;
	Body bBobine;

	boolean levelDone = false;

	float yOnTouchDown = 0;

	boolean wasOnRotatePointWb1 = false;
	boolean wasOnMovePointWb1 = false;
	boolean wasOnRotatePointWb2 = false;
	boolean wasOnMovePointWb2 = false;
	boolean levelPlayed=false;

	RevoluteJoint revJoint2;

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
			this.seatTexture = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/seat.png");
						}
					});

			this.seatTexture.load();
			this.seatTextureRegion = TextureRegionFactory
					.extractFromTexture(this.seatTexture);
			// ---
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
		this.seatMpBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 75, 22, TextureOptions.BILINEAR);

		// --------

		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"bobine3.png", 0, 0, 1, 1); // 64x32
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bgBitmapTextureAtlas, this,
						"bgRideau.png", 0, 0, 1, 1); // 64x32
		this.woodboardTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.woodboardBitmapTextureAtlas, this,
						"woodboard.png", 0, 0, 1, 1);
		this.seatMpTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.seatMpBitmapTextureAtlas, this,
						"seat_mp.png", 0, 0, 1, 1);

		this.woodboardBitmapTextureAtlas.load();
		this.mBitmapTextureAtlas.load();
		this.bgBitmapTextureAtlas.load();
		this.seatMpBitmapTextureAtlas.load();

		// -- one new object
		this.speakersBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 64, 200, TextureOptions.BILINEAR);
		this.speakersTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.speakersBitmapTextureAtlas, this,
						"speakers.png", 0, 0, 1, 1);
		this.speakersBitmapTextureAtlas.load();
		// ----------------------------------------------
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mVictoireSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "victoire.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}

	}

	@Override
	public Scene onCreateScene() {
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1,
				0.5f, 0.5f);

		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();

		mScene.registerUpdateHandler(new TimerHandler(0.1f, true,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						if (!levelDone) {
							if (bBobine.getPosition().x < 21
									&& bBobine.getPosition().x > 20) {
								if (bBobine.getPosition().y < 13
										&& bBobine.getPosition().y > 12) {
									mScene.attachChild(success);
									Level5.this.mVictoireSound.play();
									levelDone = true;
									Vector2 gravity = new Vector2(0, 0);
									bBobine.setType(BodyType.StaticBody);
									mPhysicsWorld.setGravity(gravity);
								}
							}
							if (bBobine.getPosition().x < 11.80
									&& bBobine.getPosition().x > 11) {
								if (bBobine.getPosition().y < 10.40
										&& bBobine.getPosition().y > 10) {
									bSeatMp.setAngularVelocity(100f);
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

		// **********************
		// *********************
		// seat
		final Sprite seat = new Sprite(CAMERA_WIDTH / 2 - 50,
				CAMERA_HEIGHT - 190, this.seatTextureRegion,
				this.getVertexBufferObjectManager());

		// seat body
		asSeatMp = new AnimatedSprite(CAMERA_WIDTH / 2 - 55,
				CAMERA_HEIGHT - 110, this.seatMpTextureRegion,
				this.getVertexBufferObjectManager());
		// asWb1.setScale(MathUtils.random(0.5f, 1.25f));
		bSeatMp = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asSeatMp,
				BodyType.DynamicBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				asSeatMp, bSeatMp, true, true));
		// ------
		Body anchorBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
				bSeatMp.getPosition().x * 32f - 50 + asSeatMp.getWidth(),
				bSeatMp.getPosition().y * 32f - 5, 1, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(1, 0, 0.5f));
		// Body anchorBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
		// 500,
		// 200, 1, BodyType.StaticBody,
		// PhysicsFactory.createFixtureDef(1, 0, 0.5f));
		// createCircleBody(this.mPhysicsWorld, ,
		// BodyType.DynamicBody,
		// PhysicsFactory.createFixtureDef(1, 0, 0.5f));

		RevoluteJointDef revJoint = new RevoluteJointDef();
		revJoint.bodyA = bSeatMp;
		revJoint.bodyB = anchorBody;
		revJoint.collideConnected = false;
		revJoint.localAnchorA.set(new Vector2(0 + (asSeatMp.getWidth() / 2)
				/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0));
		revJoint.localAnchorB.set(new Vector2(0, 0));
		revJoint.enableMotor = true;
		RevoluteJoint revJoint2 = (RevoluteJoint) mPhysicsWorld
				.createJoint(revJoint);
		this.mScene.attachChild(asSeatMp);
		// ----
		final Rectangle rSeatBase = new Rectangle(CAMERA_WIDTH / 2 - 50,
				CAMERA_HEIGHT - 88, 65, 2, vertexBufferObjectManager);
		Body bSeatBase = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				rSeatBase, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				rSeatBase, bSeatBase, true, true));
		rSeatBase.setColor(0f, 0f, 0f, 0f);
		this.mScene.attachChild(rSeatBase);

		final Rectangle rSeatBack = new Rectangle(CAMERA_WIDTH / 2 + 10,
				CAMERA_HEIGHT - 137, 65, 2, vertexBufferObjectManager);
		Body bSeatBack = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				rSeatBack, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(0, 0, 0.5f));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				rSeatBack, bSeatBack, true, true));
		rSeatBack.setColor(0f, 0f, 0f, 0f);
		bSeatBack.setTransform(bSeatBack.getPosition(), (float) 1.57);
		this.mScene.attachChild(rSeatBack);

		mScene.attachChild(seat);
		// **********************
		// *** BOBINE *** //

		asBobine = new AnimatedSprite(50, 50, this.mCircleFaceTextureRegion,
				this.getVertexBufferObjectManager());
		// face.setScale(MathUtils.random(0.5f, 1.25f));
		bBobine = PhysicsFactory.createCircleBody(this.mPhysicsWorld, asBobine,
				BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(1, 0, 0.5f));
		this.mScene.attachChild(asBobine);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				asBobine, bBobine, true, true));
		// ---
		buttonRestart = new Sprite(CAMERA_WIDTH - 120, 40,this.buttonRestartTextureRegion,this.getVertexBufferObjectManager());
		mScene.attachChild(buttonRestart);
		
		buttonPlay = new Sprite(CAMERA_WIDTH - 120, 40,
				this.buttonPlayTextureRegion,
				this.getVertexBufferObjectManager());
		mScene.attachChild(buttonPlay);

		success = new Sprite(CAMERA_WIDTH / 2 - 70, CAMERA_HEIGHT / 2 - 70,
				this.successTextureRegion, this.getVertexBufferObjectManager());

		// *********************

		// ** WOOD BOARDS ***//

		asWb1 = new AnimatedSprite(50, CAMERA_HEIGHT - 30,
				this.woodboardTextureRegion,
				this.getVertexBufferObjectManager());
		// asWb1.setScale(MathUtils.random(0.5f, 1.25f));
		bWb1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asWb1,
				BodyType.StaticBody, objectFixtureDef);
		this.mScene.attachChild(asWb1);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb1,
				bWb1, true, true));

		asWb3 = new AnimatedSprite(CAMERA_WIDTH - 150, 250,
				this.woodboardTextureRegion,
				this.getVertexBufferObjectManager());
		// asWb2.setScale(MathUtils.random(0.5f, 1.25f));
		bWb3 = PhysicsFactory.createBoxBody(this.mPhysicsWorld, asWb3,
				BodyType.StaticBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(asWb3,
				bWb3, true, true));
		// wb3Angle = (float) 0.37;
		bWb3.setTransform(bWb3.getPosition(), 2f);
		this.mScene.attachChild(asWb3);

		// RevoluteJoint revJoint2 =
		// (RevoluteJoint)mPhysicsWorld.createJoint(revJoint);

		// revJoint.bodyA.setTransform(bWb3.getPosition(), 1.57f);

		// *** speakers ***//
		asSpeakers = new AnimatedSprite(CAMERA_WIDTH - 300,
				CAMERA_HEIGHT - 260, this.speakersTextureRegion,
				this.getVertexBufferObjectManager());
		// asWb2.setScale(MathUtils.random(0.5f, 1.25f));
		bSpeakers = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				asSpeakers, BodyType.StaticBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				asSpeakers, bSpeakers, true, true));
		// wb3Angle = (float) 0.37;
		// bSpeakers.setTransform(bSpeakers.getPosition(), 0.7f);
		this.mScene.attachChild(asSpeakers);
		// *****************//

		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {

			if (pSceneTouchEvent.isActionDown()) {
				float x, y, xW, yW;
				x = asWb1.getX();
				y = asWb1.getY();
				xW = asWb1.getWidth();
				yW = asWb1.getHeight();
				if (pSceneTouchEvent.getX() < x + xW + 20
						&& pSceneTouchEvent.getX() > x + xW - 30) {
					if (pSceneTouchEvent.getY() > y - 20
							&& pSceneTouchEvent.getY() < y + yW + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
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
				x = asWb3.getX();
				y = asWb3.getY();
				xW = asWb3.getWidth();
				yW = asWb3.getHeight();
				if (pSceneTouchEvent.getX() < x + xW + 20
						&& pSceneTouchEvent.getX() > x + xW - 30) {
					if (pSceneTouchEvent.getY() > y - 20
							&& pSceneTouchEvent.getY() < y + yW + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
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
				
				
				//play level and after restart
				if (pSceneTouchEvent.getX() > CAMERA_WIDTH - 120
					&& pSceneTouchEvent.getX() < CAMERA_WIDTH - 40) {
					if (pSceneTouchEvent.getY() > 40
							&& pSceneTouchEvent.getY() < 120) {
						if(!levelPlayed){
						Vector2 gravity = new Vector2(0,
								SensorManager.GRAVITY_EARTH);
						this.mPhysicsWorld.setGravity(gravity);
						mScene.detachChild(buttonPlay);
						bBobine.setType(BodyType.DynamicBody);
						levelPlayed=true;
						}
						else{ //to restart
							Intent intent = getIntent();
							finish();
							startActivity(intent);
						}
					}
				}

				if (pSceneTouchEvent.getX() < 400) {
						Log.d("myFlags", "X is " + bBobine.getPosition().x
								+ " and Y is " + bBobine.getPosition().y);
						// bSeatMp.setAngularVelocity(100f);
					}
				
				return true;
			} else {
				if (pSceneTouchEvent.isActionMove()) {
					float angle = 0;
					if (wasOnMovePointWb1) {
						bWb1.setTransform(
								pSceneTouchEvent.getX()
										/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
								pSceneTouchEvent.getY()
										/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
								bWb1.getAngle());

					} else {

						if (wasOnRotatePointWb1) {
							angle = pSceneTouchEvent.getY() - yOnTouchDown;
							bWb1.setTransform(bWb1.getPosition(), angle / 100);

						} else {
							if (wasOnMovePointWb2) {
								bWb3.setTransform(
										pSceneTouchEvent.getX()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
										pSceneTouchEvent.getY()
												/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
										bWb3.getAngle());

							} else {

								if (wasOnRotatePointWb2) {
									angle = pSceneTouchEvent.getY()
											- yOnTouchDown;
									bWb3.setTransform(bWb3.getPosition(),
											angle / 100);

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
