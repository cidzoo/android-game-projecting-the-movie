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
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.SensorManager;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

//===========================================================
// Level3.java - Projet Game Tech - HES-SO Master
// 
// Niveau de Valentin (démo téléporteur)
// ===========================================================
public class Level3 extends SimpleBaseGameActivity implements
        IAccelerationListener, IOnSceneTouchListener {

    protected static final int CAMERA_WIDTH = 800;
    protected static final int CAMERA_HEIGHT = 480;
    
    //private boolean needExplosion = true;
    private Sound mExplosionSound;
    private Sound mTeleportationSound;
    private Sound mVictoireSound;
    
    private BitmapTextureAtlas mBitmapTextureAtlas, bgBitmapTextureAtlas,
            laserBitmapTextureAtlas, teleporteurBitmapTextureAtlas,
            mFireTextureAtlas, testPointTextureAtlas;
    
    private ITextureRegion mParticleTextureRegion;
    private Scene mScene;
    
    protected ITiledTextureRegion mCircleFaceTextureRegion;
    protected ITiledTextureRegion bgTextureRegion,
            dematerialiseurTextureRegion, testPointTextureRegion;
    
    private ITexture buttonPlayTexture, buttonRestartTexture, projTexture, successTexture,
            laserTexture;
    
    private ITextureRegion buttonPlayTextureRegion, buttonRestartTextureRegion, projTextureRegion,
            laserTextureRegion, successTextureRegion;
    
    protected PhysicsWorld mPhysicsWorld;
    
    Sprite buttonPlay, success, laser, buttonRestart;
    
    AnimatedSprite asTeleporteur1, asTeleporteur2, asTestPoint;
    
    Body bTeleporteur1, bTeleporteur2, bTestPoint;
    
    float xWb1, xWb2, xWb3, yWb1, yWb2, yWb3;
    float wb1Angle, wb2Angle, wb3Angle;
    
    AnimatedSprite asBobine;
    Body bBobine;
    
    private boolean levelDone = false;
    private boolean bobineDetruite = false;
    float yOnTouchDown = 0;
    float xOnTouchDown = 0;
    
    boolean wasOnRotatePointTeleporteur1 = false;
    boolean wasOnMovePointTeleporteur1 = false;
    boolean wasOnRotatePointTeleporteur2 = false;
    boolean wasOnMovePointTeleporteur2 = false;
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
            this.buttonPlayTextureRegion = TextureRegionFactory.extractFromTexture(this.buttonPlayTexture);

            // Button Restart
            this.buttonRestartTexture = new BitmapTexture(
                    this.getTextureManager(), new IInputStreamOpener() {

                @Override
                public InputStream open() throws IOException {
                    return getAssets().open("gfx/button_restart.png");
                }
            });

            this.buttonRestartTexture.load();
            this.buttonRestartTextureRegion = TextureRegionFactory.extractFromTexture(this.buttonRestartTexture);

            // ----
            this.projTexture = new BitmapTexture(this.getTextureManager(),
                    new IInputStreamOpener() {

                        @Override
                        public InputStream open() throws IOException {
                            return getAssets().open("gfx/proj3.png");
                        }
                    });

            this.projTexture.load();
            this.projTextureRegion = TextureRegionFactory.extractFromTexture(this.projTexture);
            // ---

            // --- laser
            this.laserTexture = new BitmapTexture(this.getTextureManager(),
                    new IInputStreamOpener() {

                        @Override
                        public InputStream open() throws IOException {
                            return getAssets().open("gfx/laser.png");
                        }
                    });

            this.laserTexture.load();
            this.laserTextureRegion = TextureRegionFactory.extractFromTexture(this.laserTexture);

            this.successTexture = new BitmapTexture(this.getTextureManager(),
                    new IInputStreamOpener() {

                        @Override
                        public InputStream open() throws IOException {
                            return getAssets().open("gfx/success.png");
                        }
                    });

            this.successTexture.load();
            this.successTextureRegion = TextureRegionFactory.extractFromTexture(this.successTexture);
        } catch (IOException e) {
            Debug.e(e);
        }

        // ************************
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
        this.bgBitmapTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 800, 480, TextureOptions.BILINEAR);

        this.laserBitmapTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 800, 47, TextureOptions.BILINEAR);

        this.teleporteurBitmapTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 180, 33, TextureOptions.BILINEAR);
        
        this.testPointTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 10, 10, TextureOptions.BILINEAR);

        this.mFireTextureAtlas = new BitmapTextureAtlas(
                this.getTextureManager(), 32, 32,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this,
                "particle_point.png", 0, 0);

        // --------

        this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this,
                "bobine3.png", 0, 0, 1, 1); // 64x32
        this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bgBitmapTextureAtlas, this,
                "fond_scienceFiction2.png", 0, 0, 1, 1); // 64x32
        this.laserTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.laserBitmapTextureAtlas, this,
                "laser.png", 0, 0, 1, 1);
        this.dematerialiseurTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.teleporteurBitmapTextureAtlas, this,
                "dematerialiseur.png", 0, 0, 1, 1);
        //this.testPointTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.testPointTextureAtlas, this, "testPoint.png", 0, 0, 1, 1);

        this.laserBitmapTextureAtlas.load();
        this.teleporteurBitmapTextureAtlas.load();
        this.mBitmapTextureAtlas.load();
        this.bgBitmapTextureAtlas.load();
        this.mBitmapTextureAtlas.load();
        this.mFireTextureAtlas.load();
        this.testPointTextureAtlas.load();
        
        SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mExplosionSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.mp3");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mTeleportationSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "teleportation.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mVictoireSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "victoire.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
    }

    @Override
    public Scene onCreateScene() {

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();

        final CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
                Level3.CAMERA_WIDTH * 0.5f, Level3.CAMERA_HEIGHT * 0.5f + 20,
                80);
        final SpriteParticleSystem particleSystem = new SpriteParticleSystem(
                particleEmitter, 60, 60, 360, this.mParticleTextureRegion,
                this.getVertexBufferObjectManager());

        mScene.registerUpdateHandler(new TimerHandler(0.1f, true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(final TimerHandler pTimerHandler) {
                        if (!levelDone && !bobineDetruite) {
                            if (bBobine.getPosition().x < 21
                                    && bBobine.getPosition().x > 20) {
                                if (bBobine.getPosition().y < 13
                                        && bBobine.getPosition().y > 12) {
                                    mScene.attachChild(success);
                                    Level3.this.mVictoireSound.play();
                                    levelDone = true;
                                    Vector2 gravity = new Vector2(0, 0);
                                    bBobine.setType(BodyType.StaticBody);
                                    mPhysicsWorld.setGravity(gravity);
                                }
                            }
                            if (asBobine.collidesWith(laser)) {
                            	if(!bobineDetruite){
                            		Level3.this.mExplosionSound.play();
                            	}
                            	bobineDetruite = true;
                                mScene.detachChild(asBobine);
                                asBobine.setVisible(false);
                                asBobine.detachSelf();
                                
                                particleSystem.detachSelf();
                                mScene.attachChild(particleSystem);
                                particleEmitter.setCenter(asBobine.getX(),
                                        asBobine.getY() - 20);

                                mScene.registerUpdateHandler(new TimerHandler(
                                        4f, new ITimerCallback() {

                                    @Override
                                    public void onTimePassed(
                                            final TimerHandler pTimerHandler) {
                                        particleSystem.setParticlesSpawnEnabled(false);
                                        mScene.unregisterUpdateHandler(pTimerHandler);
                                    }
                                }));
                                mScene.registerUpdateHandler(new TimerHandler(
                                        2f, new ITimerCallback() {

                                    @Override
                                    public void onTimePassed(
                                            final TimerHandler pTimerHandler) {
                                        particleSystem.setParticlesSpawnEnabled(false);
                                        mScene.unregisterUpdateHandler(pTimerHandler);
                                    }
                                }));
                                
                                
                            }
                        }
                    }
                }));
        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

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
        this.mPhysicsWorld.setContactListener(createContactListener());

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

        // laser
        laser = new Sprite(0, CAMERA_HEIGHT - 300, this.laserTextureRegion,
                this.getVertexBufferObjectManager());
        mScene.attachChild(laser);

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
        // *** BOBINE *** //
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1,
                0.5f, 0.5f);

        asBobine = new AnimatedSprite(50, 50, this.mCircleFaceTextureRegion,
                this.getVertexBufferObjectManager());
        // face.setScale(MathUtils.random(0.5f, 1.25f));
        bBobine = PhysicsFactory.createCircleBody(this.mPhysicsWorld, asBobine,
                BodyType.DynamicBody, objectFixtureDef);
        bBobine.setUserData("bobine");

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
        // ** TELEPORTEURS ***//

        asTeleporteur1 = new AnimatedSprite(50, CAMERA_HEIGHT - 30,
                this.dematerialiseurTextureRegion,
                this.getVertexBufferObjectManager());
        
		bTeleporteur1 = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
                asTeleporteur1, BodyType.KinematicBody, objectFixtureDef);
		bTeleporteur1.setUserData("teleporteur1");
        
        this.mScene.attachChild(asTeleporteur1);
        
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
                asTeleporteur1, bTeleporteur1, true, true));

        asTeleporteur2 = new AnimatedSprite(250, CAMERA_HEIGHT - 30,
                this.dematerialiseurTextureRegion,
                this.getVertexBufferObjectManager());
        bTeleporteur2 = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
                asTeleporteur2, BodyType.KinematicBody, objectFixtureDef);
        
        this.mScene.attachChild(asTeleporteur2);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
                asTeleporteur2, bTeleporteur2, true, true));

        particleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(1,
                0, 0));
        particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(0));
        particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(
                GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
        particleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(
                -2, 2, -20, -10));
        particleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(
                0.0f, 360.0f));
        particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(6));

        particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0,
                5, 1.0f, 2.0f));
        particleSystem.addParticleModifier(new ColorParticleModifier<Sprite>(0,
                3, 1, 1, 0, 0.5f, 0, 0));
        particleSystem.addParticleModifier(new ColorParticleModifier<Sprite>(4,
                6, 1, 1, 0.5f, 1, 0, 1));
        particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0,
                1, 0, 1));
        particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(5,
                6, 1, 0));

        return this.mScene;
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene,
            final TouchEvent pSceneTouchEvent) {
        if (this.mPhysicsWorld != null) {
        	if (pSceneTouchEvent.isActionDown()) {
				float x, y, xW, yW;

				float positionX = (asTeleporteur1.getX() + asTeleporteur1.getRotationCenterX())
						+ (float) (Math
								.cos(Math.toRadians(asTeleporteur1.getRotation()))
								* asTeleporteur1.getWidth() / 2);
				float positionY = (asTeleporteur1.getY() + asTeleporteur1.getRotationCenterY())
						+ (float) (Math
								.sin(Math.toRadians(asTeleporteur1.getRotation()))
								* asTeleporteur1.getWidth() / 2);

				x = asTeleporteur1.getX();
				y = asTeleporteur1.getY();
				xW = asTeleporteur1.getWidth();
				yW = asTeleporteur1.getHeight();
				if (pSceneTouchEvent.getX() < positionX + 20
						&& pSceneTouchEvent.getX() > positionX - 30) {
					if (pSceneTouchEvent.getY() > positionY - 20
							&& pSceneTouchEvent.getY() < positionY + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
						xOnTouchDown = pSceneTouchEvent.getX();
						wasOnRotatePointTeleporteur1 = true;
					}
				} else {
					if (pSceneTouchEvent.getX() > x + 30
							&& pSceneTouchEvent.getX() < x + xW - 30) {
						if (pSceneTouchEvent.getY() > y - 20
								&& pSceneTouchEvent.getY() < y + yW + 20) {
							wasOnMovePointTeleporteur1 = true;
						}
					}
				}
				float positionX2 = (asTeleporteur2.getX() + asTeleporteur2.getRotationCenterX())
						+ (float) (Math
								.cos(Math.toRadians(asTeleporteur2.getRotation()))
								* asTeleporteur2.getWidth() / 2);
				float positionY2 = (asTeleporteur2.getY() + asTeleporteur2.getRotationCenterY())
						+ (float) (Math
								.sin(Math.toRadians(asTeleporteur2.getRotation()))
								* asTeleporteur2.getWidth() / 2);
				
				x = asTeleporteur2.getX();
				y = asTeleporteur2.getY();
				xW = asTeleporteur2.getWidth();
				yW = asTeleporteur2.getHeight();
				if (pSceneTouchEvent.getX() < positionX2 + 20
						&& pSceneTouchEvent.getX() > positionX2 - 30) {
					if (pSceneTouchEvent.getY() > positionY2 - 20
							&& pSceneTouchEvent.getY() < positionY2 + 20) {
						yOnTouchDown = pSceneTouchEvent.getY();
						xOnTouchDown = pSceneTouchEvent.getX();
						wasOnRotatePointTeleporteur2 = true;
					}
				} else {
					if (pSceneTouchEvent.getX() > x + 30
							&& pSceneTouchEvent.getX() < x + xW - 30) {
						if (pSceneTouchEvent.getY() > y - 20
								&& pSceneTouchEvent.getY() < y + yW + 20) {
							wasOnMovePointTeleporteur2 = true;
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
						bBobine.setType(BodyType.DynamicBody);
                        Vector2 vector = bBobine.getLinearVelocity();
                        bBobine.setLinearVelocity(vector.x, vector.y + 20); // Permet de définir la vitesse de la bobine
						mScene.detachChild(buttonPlay);
						//bBobine.applyLinearImpulse(220, -50, bBobine.getPosition().x, bBobine.getPosition().y);
						levelPlayed=true;
						}
						else{ //to restart
							Intent intent = getIntent();
							finish();
							startActivity(intent);
						}
					}
				}
				
                
                return true;
            } else {
                if (pSceneTouchEvent.isActionMove() && !levelPlayed) {
                    if (wasOnMovePointTeleporteur1) {
                        bTeleporteur1.setTransform(
                                pSceneTouchEvent.getX()
                                / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                                pSceneTouchEvent.getY()
                                / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                                bTeleporteur1.getAngle());

                    } else {

                        if (wasOnRotatePointTeleporteur1) {
                        	float pValueX = pSceneTouchEvent.getX();
					        float pValueY = CAMERA_HEIGHT - pSceneTouchEvent.getY();

					        float directionX = pValueX - asTeleporteur1.getX();
					        float directionY = (CAMERA_HEIGHT - pValueY) - asTeleporteur1.getY();

					        float rotationAngle = (float) Math.atan2(directionY, directionX);

					        bTeleporteur1.setTransform(bTeleporteur1.getPosition(), rotationAngle);

                        } else {
                            if (wasOnMovePointTeleporteur2) {
                                bTeleporteur2.setTransform(
                                        pSceneTouchEvent.getX()
                                        / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                                        pSceneTouchEvent.getY()
                                        / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                                        bTeleporteur2.getAngle());

                            } else {

                                if (wasOnRotatePointTeleporteur2) {
                                	float pValueX = pSceneTouchEvent.getX();
							        float pValueY = CAMERA_HEIGHT - pSceneTouchEvent.getY();

							        float directionX = pValueX - asTeleporteur2.getX();
							        float directionY = (CAMERA_HEIGHT - pValueY) - asTeleporteur2.getY();

							        float rotationAngle = (float) Math.atan2(directionY, directionX);

							        bTeleporteur2.setTransform(bTeleporteur2.getPosition(), rotationAngle);

                                }
                            }
                        }
                    }
                } else {
                    if (pSceneTouchEvent.isActionUp()) {
                        wasOnRotatePointTeleporteur1 = false;
                        wasOnMovePointTeleporteur1 = false;
                        wasOnRotatePointTeleporteur2 = false;
                        wasOnMovePointTeleporteur2 = false;
                    }
                }
            }
        }

        return false;
    }

    private ContactListener createContactListener(){
    	ContactListener contactListener = new ContactListener(){
    		@SuppressLint({ "FloatMath", "FloatMath" })
			public void beginContact(Contact contact){
    			
    			final Fixture x1 = contact.getFixtureA();
    			final Fixture x2 = contact.getFixtureB();
    			
    			if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
    				if(x1.getBody().getUserData().equals("teleporteur1") && x2.getBody().getUserData().equals("bobine")){
    					
    					if(asBobine.getX() > asTeleporteur1.getX() && (asBobine.getX()+asBobine.getWidth()) < (asTeleporteur1.getX()+asTeleporteur2.getWidth())){
    						
    						Level3.this.mTeleportationSound.play();
    						
    						runOnUpdateThread(new Runnable() {
    							public void run(){
    								
    								Vector2 vector = x2.getBody().getLinearVelocity();
    	    						float vectorX = vector.x;
    	    						float vectorY = vector.y;
    	    						
    	    						float nombre = vectorX + vectorY;
                        		
    								float positionX = (asTeleporteur2.getX()+asTeleporteur2.getRotationCenterX()) + -1*(float)(Math.sin(Math.toRadians(asTeleporteur2.getRotation()))*asTeleporteur2.getHeight());
    								float positionY = (asTeleporteur2.getY()+asTeleporteur2.getRotationCenterY()) + (float)(Math.cos(Math.toRadians(asTeleporteur2.getRotation()))*asTeleporteur2.getHeight());
                        		
//                      	  		asTestPoint = new AnimatedSprite(((asTeleporteur2.getX()+asTeleporteur2.getRotationCenterX()) + -1*(float)(Math.sin(Math.toRadians(asTeleporteur2.getRotation()))*asTeleporteur2.getHeight())), ((asTeleporteur2.getY()+asTeleporteur2.getRotationCenterY()) + (float)(Math.cos(Math.toRadians(asTeleporteur2.getRotation()))*asTeleporteur2.getHeight())), testPointTextureRegion, getVertexBufferObjectManager());
//                          	  	mScene.attachChild(asTestPoint);
    								
    								x2.getBody().setTransform(positionX/32, positionY/32, 0);
    								x2.getBody().setLinearVelocity(-1 * nombre * (float)Math.sin(Math.toRadians(asTeleporteur2.getRotation())), nombre * (float)(Math.cos(Math.toRadians(asTeleporteur2.getRotation()))));
    							}
    						});
    					}
        			}
    			}
    		}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
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
        // final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(),
        // pAccelerationData.getY());
        // this.mPhysicsWorld.setGravity(gravity);
        // Vector2Pool.recycle(gravity);
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