package ucore.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ucore.graphics.CustomSurface;
import ucore.graphics.Shader;
import ucore.graphics.Surface;
import ucore.scene.utils.ScissorStack;

import java.util.Stack;

import static ucore.core.Core.batch;

public class Graphics{
    private static Vector3 vec3 = new Vector3();
    private static Vector2 mouse = new Vector2();
    private static Vector2 size = new Vector2();

    private static TextureRegion tempregion = new TextureRegion();

    private static Stack<Batch> batches = new Stack<>();

    private static Array<Surface> surfaceArray = new Array<>();
    private static Stack<Surface> surfaceStack = new Stack<>();

    private static Surface effects1;
    private static boolean scaleEffects = true;

    private static Shader[] currentShaders;
    private static Shader[] tmpShaders = {null};

    private static boolean clipping;
    private static boolean wasClipped;
    private static Rectangle clipRect = new Rectangle();

    public static int width(){
        return Gdx.graphics.getWidth();
    }

    public static int height(){
        return Gdx.graphics.getHeight();
    }

    /** Mouse coords. */
    public static Vector2 mouse(){
        mouse.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        return mouse;
    }

    /** World coordinates for current mouse coords. */
    public static Vector2 mouseWorld(){
        Core.camera.unproject(vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        return mouse.set(vec3.x, vec3.y);
    }

    /** World coordinates for supplied screen coords. */
    public static Vector2 world(float screenx, float screeny){
        Core.camera.unproject(vec3.set(screenx, screeny, 0));
        return mouse.set(vec3.x, vec3.y);
    }

    /** Screen coordinates for supplied world coords. */
    public static Vector2 screen(float worldx, float worldy){
        Core.camera.project(vec3.set(worldx, worldy, 0));
        return mouse.set(vec3.x, vec3.y);
    }

    /** Screen size. */
    public static Vector2 size(){
        return size.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public static void beginClip(float x, float y, float width, float height){
        if(clipping) throw new IllegalStateException("Call endClip() first.");
        if(drawing()) flush();

        wasClipped = ScissorStack.pushScissors(clipRect.set(x, y, width, height));
        clipping = true;
    }

    public static void endClip(){
        if(!clipping) throw new IllegalStateException("Call beginClip() first.");
        if(wasClipped){
            if(drawing()) Graphics.flush();
            ScissorStack.popScissors();
        }
        clipping = false;
    }

    public static void setAdditiveBlending(){
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    }

    public static void setNormalBlending(){
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void clear(Color color){
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void clear(float r, float g, float b){
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void clear(float r, float g, float b, float a){
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void useBatch(Batch batch){
        if(batches.isEmpty())
            batches.push(Core.batch);
        batches.push(batch);
        Core.batch = batch;
    }

    public static void popBatch(){
        batches.pop();
        Core.batch = batches.peek();
    }

    public static Array<Surface> getSurfaces(){
        return surfaceArray;
    }

    public static Surface currentSurface(){
        return surfaceStack.isEmpty() ? null : surfaceStack.peek();
    }

    /** Adds a custom surface that handles events. */
    public static CustomSurface createSurface(CustomSurface surface){
        surfaceArray.add(surface);
        return surface;
    }

    /** Creates a surface, sized to the screen */
    public static Surface createSurface(){
        return createSurface(-1, 0);
    }

    public static Surface createSurface(int scale){
        return createSurface(scale, 0);
    }

    /**
     * Creates a surface, scale times smaller than the screen. Useful for
     * pixelated things.
     */
    public static Surface createSurface(int scale, int bind){
        Surface s = new Surface(scale, bind);
        surfaceArray.add(s);
        return s;
    }

    /** Begins drawing on a surface, clearing it. */
    public static void surface(Surface surface){
        surface(surface, true);
    }

    public static void surface(Surface surface, boolean clear){
        surface(surface, clear, true);
    }

    /** Begins drawing on a surface. */
    public static void surface(Surface surface, boolean clear, boolean viewport){
        //if(!surfaceStack.isEmpty()){
            //end();
           // surfaceStack.peek().end(false);
        //}

        surfaceStack.push(surface);

        if(drawing())
            end();

        surface.begin(clear, viewport);

        begin();
    }

    /** Ends drawing on the current surface. */
    public static void surface(){
        Graphics.checkSurface();

        Surface surface = surfaceStack.pop();

        if(Core.batch.isDrawing()) end();

        Surface current = surfaceStack.empty() ? null : surfaceStack.peek();

        if(current != null){
            current.begin(false);
        }else{
            surface.end(true);
        }

        begin();
    }

    /** Ends the current surface and draws its contents onto the screen. */
    public static void flushSurface(){
        Graphics.flushSurface(null);
    }

    /** Ends the current surface and draws its contents onto the specified surface. */
    public static void flushSurface(Surface dest){
        Graphics.checkSurface();

        Surface surface = surfaceStack.pop();

        if(batch.isDrawing()) end();

        Surface current = surfaceStack.empty() ? null : surfaceStack.peek();

        if(current != null){
            current.begin(false, false);
        }else{
            surface.end(true);
        }

        beginCam();

        if(dest != null) surface(dest);

        batch.draw(surface.texture(),
                Core.camera.position.x - Core.camera.viewportWidth / 2 * Core.camera.zoom,
                Core.camera.position.y + Core.camera.viewportHeight / 2 * Core.camera.zoom,
                Core.camera.viewportWidth * Core.camera.zoom, -Core.camera.viewportHeight * Core.camera.zoom);

        if(dest != null) surface();
    }

    public static Surface getEffectSurface(){
        return effects1;
    }

    /** Sets the batch projection matrix to the screen, without the camera. */
    public static void setScreen(){
        boolean drawing = batch.isDrawing();

        if(drawing)
            end();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        begin();
    }

    static void checkSurface(){
        if(surfaceStack.isEmpty())
            throw new RuntimeException("Surface stack is empty! Set a surface first.");
    }

    /**Begin the postprocessing shader.*/
    public static void beginShaders(Shader types){
        tmpShaders[0] = types;
        currentShaders = tmpShaders;

        batch.flush();

        surface(effects1, true, false);
    }


    /** End the postprocessing shader. */
    public static void endShaders(){
        Shader shader = currentShaders[0];
        tempregion.setRegion(currentSurface().texture());
        shader.region = tempregion;

        Graphics.shader(shader);
        shader.program().begin();
        shader.applyParams();
        shader.program().end();
        flushSurface();
        Graphics.shader();
    }

    public static void flush(){
        Core.batch.flush();
    }

    /** Set the shader, applying immediately. */
    public static void shader(Shader shader){
        shader(shader, true);
    }

    public static void shader(Shader shader, boolean applyOnce){
        boolean drawing = batch.isDrawing();

        if(applyOnce && drawing){
            end();
        }

        batch.setShader(shader.program());

        if(applyOnce){
            shader.program().begin();
            shader.applyParams();
            shader.program().end();
            if(drawing) begin();
        }
    }

    /** Revert to the default shader. */
    public static void shader(){
        batch.setShader(null);
    }

    public static void setScaleEffects(boolean scl){
        scaleEffects = scl;
    }

    public static void resize(){
        if(Gdx.graphics.getWidth() <= 2 || Gdx.graphics.getHeight() <= 2) return;

        if(effects1 == null){
            effects1 = Graphics.createSurface(scaleEffects ? Core.cameraScale : 1);
        }

        for(Surface surface : surfaceArray){
            surface.onResize();
        }
    }

    public static void setCameraScale(int scale){
        Core.cameraScale = scale;
        Core.camera.viewportWidth = Gdx.graphics.getWidth() / scale;
        Core.camera.viewportHeight = Gdx.graphics.getHeight() / scale;
        for(Surface surface : surfaceArray){
            surface.onResize();
        }
        Core.camera.update();
    }

    /** Begins the batch and sets the camera projection matrix. */
    public static void beginCam(){
        batch.setProjectionMatrix(Core.camera.combined);
        batch.begin();
    }

    /** Begins the batch. */
    public static void begin(){
        batch.begin();
    }

    /** Ends the batch */
    public static void end(){
        batch.end();
    }

    public static boolean drawing(){
        return batch.isDrawing();
    }

    static void dispose(){
        for(Surface surface : surfaceArray){
            surface.dispose();
        }
        surfaceArray.clear();
    }
}
