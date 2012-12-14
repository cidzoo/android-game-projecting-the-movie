package com.gt.proto_v01;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PhysicalBody {

	ITiledTextureRegion textureRegion;
	BitmapTextureAtlas bitmapTextureAtlas;
	ITexture texture;
	AnimatedSprite as;
	Body b;

	public PhysicalBody(String img,int width,int height, SimpleBaseGameActivity  c,
			PhysicsWorld mPhysicsWorld,Scene mScene) {
		this.bitmapTextureAtlas = new BitmapTextureAtlas(c.getTextureManager(), width, height,
				TextureOptions.BILINEAR);
		this.textureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bitmapTextureAtlas, c,
						"speakers.png", 0, 0, 1, 1);
		this.bitmapTextureAtlas.load();
		// -------------------------------------
		as = new AnimatedSprite(0, 0, this.textureRegion, c.getVertexBufferObjectManager());
		b = PhysicsFactory.createBoxBody(mPhysicsWorld, as,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(1, 0, 0.5f));

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(as, b,
				true, true));
		mScene.attachChild(as);

	}

	void setPosition(float x, float y) {
		b.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y
				/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, b.getAngle());
	}

	void setAngle(float angle) {
		b.setTransform(b.getPosition(), angle);
	}

}

/*
 * 
 * 		this.bitmapTextureAtlas = new BitmapTextureAtlas(tm, width, height,
				TextureOptions.BILINEAR);
		this.textureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bitmapTextureAtlas, c,
						"speakers.png", 0, 0, 1, 1);
		this.bitmapTextureAtlas.load();
		// -------------------------------------
		as = new AnimatedSprite(0, 0, this.textureRegion, vbom);
		b = PhysicsFactory.createBoxBody(mPhysicsWorld, as,
				BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(1, 0, 0.5f));

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(as, b,
				true, true));
		mScene.attachChild(as);

	}

	void setPosition(float x, float y) {
		b.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y
				/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, b.getAngle());
	}

	void setAngle(float angle) {
		b.setTransform(b.getPosition(), angle);
	}

}
*/
