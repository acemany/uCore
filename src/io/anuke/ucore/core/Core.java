package io.anuke.ucore.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.scene.Scene;
import io.anuke.ucore.scene.Skin;

public class Core{
	public static OrthographicCamera camera = new OrthographicCamera();
	public static Batch batch = new SpriteBatch();
	public static Atlas atlas;
	public static BitmapFont font;
	public static int cameraScale = 1;
	
	public static Scene scene;
	public static Skin skin;
	
	public static void setScene(Scene ascene, Skin askin){
		if(ascene != null)scene = ascene;
		if(askin != null)skin = askin;
	}
}