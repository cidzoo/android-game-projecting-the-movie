package com.gt.proto_v01;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseCubicOut;





public class MenuSlider extends Entity implements IScrollDetectorListener,IClickDetectorListener, IOnSceneTouchListener 
{

	// ===========================================================
	// Constants
	// ===========================================================

	/**
	 * Duration of the inertia effect
	 */
	protected static float INERTIA_DURATION = 1.2f;
	/**
	 * Multiplier applied to last move to defined inertia distance
	 */
	protected static float INERTIA_COEF = 5;

	// ===========================================================
	// Fields - Items and global properties
	// ===========================================================
	/**
	 * A list that contains items textures to build menu.
	 */
	protected List<ITextureRegion> columns = new ArrayList<ITextureRegion>();	
	/**
	 * The width of the window view, the menu slider is in full screen
	 */
	protected float cameraWidth;

	// ===========================================================
	// Fields - View objects
	// ===========================================================
    /**
     * Entity object that contains items sprites, it moves on scroll.
     */
    protected Entity container;
	/**
	 * A list that contains menu items sprites.
	 */
	protected List<Sprite> sprites = new ArrayList<Sprite>();
	
	// ===========================================================
	// Fields - Detectors
	// ===========================================================
	/**
	 * An object which detect the scroll action on touch
	 */
	protected SurfaceScrollDetector scrollDetector;
	/**
	 * An object which detect the click action on touch
	 */
	protected ClickDetector clickDetector;
    /**
     * Current item index clicked
     */
    protected int iItemClicked = -1;
	
	// ===========================================================
	// Fields - Move and inertia
	// ===========================================================
    /**
     * The minimum X position of the container
     */
    protected float minX = 0;
    /**
     * The maximum X position of the container
     */
    protected float maxX = 0;
    /**
     * The current X position of the container
     */
    protected float currentX = 0;
    /**
     * Last X offset
     */
    protected float lastMove, lastLastMove;
    /**
     * Current inertia movement
     */
	protected MoveModifier inertiaMove;
	
	private Proto_v01 parent;
	
	public MenuSlider(Proto_v01 parent){
		this.parent = parent;
	}
    private int gap2=0;

	// ===========================================================
	// Methodes - Initialisation and menu preparation
	// ===========================================================
	
	/**
	 * Add a texture as a new item
	 * 
	 * @param texture (ITextureRegion) - texture of a menu item
	 */
	public void addItem(ITextureRegion texture) {
		columns.add(texture);
	}

	/**
	 * Prepare the menu before show it.
	 * 
	 * @param camera_width (int) - the width of the screen
	 * @param offsetX (int) - horizontal offset from the left of the screen
	 * @param offsetY (int) - vertical offset from the right of the screen
	 * @param gap (int) - space between two sprites
	 */
	public void createMenu(int camera_width, int offsetX, int offsetY, int gap) 
	{
		detachChildren();
		cameraWidth = camera_width;
		createMenuBoxes(offsetX, offsetY, gap);
	}

	/**
	 * Create or clean the container, create sprites for each menu item.
	 * 
	 * @param offsetX (int) - horizontal offset from the left of the screen
	 * @param offsetY (int) - vertical offset from the right of the screen
	 * @param gap (int) - space between two sprites
	 */
	protected void createMenuBoxes(int offsetX, int offsetY, int gap) {
		
		gap2=gap;
		
		if(container == null){
			container = new Entity();
		}else{
			detachChild(container);
			container.detachChildren();
			sprites.removeAll(null);
		}
		
		int spriteX = offsetX;
		int spriteY = offsetY;

		// current item counter
		int iItem = 1;

		for (int x = 0; x < columns.size(); x++) {

			// On Touch, save the clicked item in case it's a click and not a
			// scroll.
			final int itemToLoad = iItem;

			ITextureRegion texture = columns.get(x);

			
			  Sprite sprite = new Sprite(spriteX, spriteY, texture, this.getVertexBufferObjectManager()) { 
				  	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) { iItemClicked = itemToLoad;
					return false;
				}
			};
			iItem++;

			container.attachChild(sprite);
			sprites.add(sprite);
			spriteX += gap + sprite.getWidth();
			
		}

		minX = -(spriteX- gap/2 - offsetX - cameraWidth/2);
		maxX = 0;
		attachChild(container);
		
	}
	
	// ===========================================================
	// Methodes - Show and Hide menu
	// ===========================================================
	
	private VertexBufferObjectManager getVertexBufferObjectManager() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * When menu is attach to be use, call this method to activate detector and touch events
	 * 
	 * @param scene (Scene) - scene where the menu is attached
	 */
	public void onShow(Scene scene)
	{
		if(scrollDetector == null)
			scrollDetector = new SurfaceScrollDetector(this);
		if(clickDetector == null)
			clickDetector = new ClickDetector(this);
		
		scrollDetector.reset();
		clickDetector.reset();
		scrollDetector.setEnabled(true);
		clickDetector.setEnabled(true);

		scene.setOnSceneTouchListener(this);
		
		for (int x = 0; x < sprites.size(); x++) {
			Sprite sprite = sprites.get(x);
			scene.registerTouchArea(sprite);
		}
	}


	/**
	 * When menu is detach to be use, call this method to deactivate detector and touch events and reset container moves
	 * 
	 * @param scene (Scene) - scene from the menu is detached
	 */
	public void onHide(Scene scene)
	{
		scrollDetector.setEnabled(false);
		clickDetector.setEnabled(false);

		scene.setOnSceneTouchListener(null);
		
		for (int x = 0; x < sprites.size(); x++) {
			Sprite sprite = sprites.get(x);
			scene.unregisterTouchArea(sprite);
		}

		if(inertiaMove != null){
			container.unregisterEntityModifier(inertiaMove);
			inertiaMove = null;		
		}
		currentX = 0;
		container.setPosition(0,0);
	}
	
	// ===========================================================
	// Methodes - Events
	// ===========================================================

	/** 
	 * Transmit touch event to detectors to catch correct touch action
	 * 
	 * @see org.andengine.entity.scene.Scene.IOnSceneTouchListener#onSceneTouchEvent(org.andengine.entity.scene.Scene, org.andengine.input.touch.TouchEvent)
	 */
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) 
	{
		 clickDetector.onTouchEvent(pSceneTouchEvent);
         scrollDetector.onTouchEvent(pSceneTouchEvent);
         return true;
	}

	/** 
	 * On scroll started, reset inertia.
	 *
	 * @see org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener#onScrollStarted(org.andengine.input.touch.detector.ScrollDetector, int, float, float)
	 */
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) 
	{
		container.unregisterEntityModifier(inertiaMove);
		inertiaMove = null;
		currentX = container.getX();
	}

	/** 
	 * On scroll, move the container inside min-max bounds and save move offset for inertia.
	 *
	 * @see org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener#onScroll(org.andengine.input.touch.detector.ScrollDetector, int, float, float)
	 */
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
        
		lastMove = pDistanceX;
		
        //Return if ends are reached
		float next = currentX + pDistanceX;
		if ( ((currentX + pDistanceX) < minX)  ){    
			next = minX;  
	    }else if((currentX + pDistanceX) > maxX){  
			next = maxX;
	    }
        
        //Center camera to the current point
        currentX = next;
        container.setPosition(currentX, 0);
        
	}

	/** 
	 * On scroll ended, start inertia move.
	 *
	 * @see org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener#onScrollFinished(org.andengine.input.touch.detector.ScrollDetector, int, float, float)
	 */
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		
		int T = gap2 + 501; //gap2 = gap in method createMenuBoxes; 501 = width of the sprite
		float next = currentX + lastMove*INERTIA_COEF;
		if ( (next < minX)  ){    
			next = minX;  
	    }else if(next > maxX){  
			next = maxX;
	    }else { //to put the sprite in the middle of the screen
	    	next = (int) Math.round(next/T);
	    	next = next * T;
	    	}
				 
		inertiaMove = new MoveModifier(INERTIA_DURATION,currentX,next,0,0,EaseCubicOut.getInstance());
		container.registerEntityModifier(inertiaMove);
		
		if(lastMove!=lastLastMove)
			lastLastMove=lastMove;
	}


	/** 
	 *On click action: to launch the LevelXX.java
	 * 
	 * @see org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener#onClick(org.andengine.input.touch.detector.ClickDetector, int, float, float)
	 */
	public void onClick(ClickDetector pClickDetector, int pPointerID,
			float pSceneX, float pSceneY) {
		
		Debug.d("On click item "+iItemClicked);
		
		if(lastMove==lastLastMove)
			parent.startLevel(iItemClicked);
	}
	
	

}