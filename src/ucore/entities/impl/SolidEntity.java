package ucore.entities.impl;

import com.badlogic.gdx.math.Vector2;
import ucore.entities.trait.SolidTrait;
import ucore.util.Translator;

public abstract class SolidEntity extends BaseEntity implements SolidTrait{
    protected transient Vector2 velocity = new Translator(0f, 0.0001f);
    private transient Vector2 lastPosition = new Translator();

    @Override
    public Vector2 lastPosition(){
        return lastPosition;
    }

    @Override
    public Vector2 getVelocity(){
        return velocity;
    }
}
