package ucore.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import ucore.function.Consumer;

import java.util.Iterator;

/**
 * A basic quad tree.
 * <p>
 * This class represents any node, but you will likely only interact with the root node.
 *
 * @param <Rectangle> The type of object this quad tree should contain. An object only requires some way of getting rough bounds.
 * @author xSke
 */
public class RectQuadTree{
    private int maxObjectsPerNode;

    private int level;
    private Rectangle bounds;
    private Array<Rectangle> objects;

    private boolean leaf;
    private RectQuadTree bottomLeftChild;
    private RectQuadTree bottomRightChild;
    private RectQuadTree topLeftChild;
    private RectQuadTree topRightChild;

    /**
     * Constructs a new quad tree.
     *
     * @param maxObjectsPerNode How many objects may be in a node before it will split.
     * Should be around 3-5 for optimal results, depending on your use case.
     * @param bounds The total bounds of this root node
     */
    public RectQuadTree(int maxObjectsPerNode, Rectangle bounds){
        this(maxObjectsPerNode, 0, bounds);
    }

    private RectQuadTree(int maxObjectsPerNode, int level, Rectangle bounds){
        this.level = level;
        this.bounds = bounds;
        this.maxObjectsPerNode = maxObjectsPerNode;
        objects = new Array<>();
        leaf = true;
    }

    private void split(){
        if(!leaf) return;

        float subW = bounds.width / 2;
        float subH = bounds.height / 2;

        leaf = false;
        bottomLeftChild = new RectQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x, bounds.y, subW, subH));
        bottomRightChild = new RectQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x + subW, bounds.y, subW, subH));
        topLeftChild = new RectQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x, bounds.y + subH, subW, subH));
        topRightChild = new RectQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x + subW, bounds.y + subH, subW, subH));

        // Transfer objects to children if they fit entirely in one
        for(Iterator<Rectangle> iterator = objects.iterator(); iterator.hasNext(); ){
            Rectangle obj = iterator.next();
            RectQuadTree child = getFittingChild(obj);
            if(child != null){
                child.insert(obj);
                iterator.remove();
            }
        }
    }

    private void unsplit(){
        if(leaf) return;
        leaf = true;

        objects.addAll(bottomLeftChild.objects);
        objects.addAll(bottomRightChild.objects);
        objects.addAll(topLeftChild.objects);
        objects.addAll(topRightChild.objects);
        bottomLeftChild = bottomRightChild = topLeftChild = topRightChild = null;
    }

    /**
     * Inserts an object into this node or its child nodes. This will split a leaf node if it exceeds the object limit.
     */
    public void insert(Rectangle obj){
        if(!bounds.overlaps(obj)){
            // New object not in quad tree, ignoring
            // throw an exception?
            return;
        }

        if(leaf && (objects.size + 1) > maxObjectsPerNode) split();

        if(leaf){
            // Leaf, so no need to add to children, just add to root
            objects.add(obj);
        }else{
            // Add to relevant child, or root if can't fit completely in a child
            RectQuadTree child = getFittingChild(obj);
            if(child != null){
                child.insert(obj);
            }else{
                objects.add(obj);
            }
        }
    }

    /**
     * Removes an object from this node or its child nodes.
     */
    public void remove(Rectangle obj){
        if(leaf){
            // Leaf, no children, remove from root
            objects.removeValue(obj, true);
        }else{
            // Remove from relevant child
            RectQuadTree child = getFittingChild(obj);

            if(child != null){
                child.remove(obj);
            }else{
                // Or root if object doesn't fit in a child
                objects.removeValue(obj, true);
            }

            if(getTotalObjectCount() <= maxObjectsPerNode) unsplit();
        }
    }

    /** Removes all objects. */
    public void clear(){
        objects.clear();
        if(bottomLeftChild != null) bottomLeftChild.clear();
        if(bottomRightChild != null) bottomRightChild.clear();
        if(topLeftChild != null) topLeftChild.clear();
        if(topRightChild != null) topRightChild.clear();
    }

    private RectQuadTree getFittingChild(Rectangle boundingBox){
        float verticalMidpoint = bounds.x + (bounds.width / 2);
        float horizontalMidpoint = bounds.y + (bounds.height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = boundingBox.y > horizontalMidpoint;
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = boundingBox.y < horizontalMidpoint && (boundingBox.y + boundingBox.height) < horizontalMidpoint;

        // Object can completely fit within the left quadrants
        if(boundingBox.x < verticalMidpoint && boundingBox.x + boundingBox.width < verticalMidpoint){
            if(topQuadrant){
                return topLeftChild;
            }else if(bottomQuadrant){
                return bottomLeftChild;
            }
        }
        // Object can completely fit within the right quadrants
        else if(boundingBox.x > verticalMidpoint){
            if(topQuadrant){
                return topRightChild;
            }else if(bottomQuadrant){
                return bottomRightChild;
            }
        }

        // Else, object needs to be in parent cause it can't fit completely in a quadrant
        return null;
    }

    /**
     * Returns the leaf node directly at the given coordinates, or null if the coordinates are outside this node's bounds.
     */
    public RectQuadTree getNodeAt(float x, float y){
        if(!bounds.contains(x, y)) return null;
        if(leaf) return this;

        if(topLeftChild.bounds.contains(x, y)) return topLeftChild.getNodeAt(x, y);
        if(topRightChild.bounds.contains(x, y)) return topRightChild.getNodeAt(x, y);
        if(bottomLeftChild.bounds.contains(x, y)) return bottomLeftChild.getNodeAt(x, y);
        if(bottomRightChild.bounds.contains(x, y)) return bottomRightChild.getNodeAt(x, y);

        // This should never happen
        return null;
    }

    /**
     * Processes objects that may intersect the given rectangle.
     * <p>
     * This will result in false positives, but never a false negative.
     */
    public void getIntersect(Consumer<Rectangle> out, Rectangle toCheck){
        if(!leaf){
            if(topLeftChild.bounds.overlaps(toCheck)) topLeftChild.getIntersect(out, toCheck);
            if(topRightChild.bounds.overlaps(toCheck)) topRightChild.getIntersect(out, toCheck);
            if(bottomLeftChild.bounds.overlaps(toCheck)) bottomLeftChild.getIntersect(out, toCheck);
            if(bottomRightChild.bounds.overlaps(toCheck)) bottomRightChild.getIntersect(out, toCheck);
        }

        for(Rectangle t : objects)
            out.accept(t);
    }

    /**
     * Fills the out parameter with any objects that may intersect the given rectangle.
     * <p>
     * This will result in false positives, but never a false negative.
     */
    public void getIntersect(Array<Rectangle> out, Rectangle toCheck){
        if(!leaf){
            if(topLeftChild.bounds.overlaps(toCheck)) topLeftChild.getIntersect(out, toCheck);
            if(topRightChild.bounds.overlaps(toCheck)) topRightChild.getIntersect(out, toCheck);
            if(bottomLeftChild.bounds.overlaps(toCheck)) bottomLeftChild.getIntersect(out, toCheck);
            if(bottomRightChild.bounds.overlaps(toCheck)) bottomRightChild.getIntersect(out, toCheck);
        }

        out.addAll(objects);
    }

    /**
     * Returns whether this node is a leaf node (has no child nodes)
     */
    public boolean isLeaf(){
        return leaf;
    }

    /**
     * Returns the bottom left child node, or null if this node is a leaf node.
     */
    public RectQuadTree getBottomLeftChild(){
        return bottomLeftChild;
    }

    /**
     * Returns the bottom right child node, or null if this node is a leaf node.
     */
    public RectQuadTree getBottomRightChild(){
        return bottomRightChild;
    }

    /**
     * Returns the top left child node, or null if this node is a leaf node.
     */
    public RectQuadTree getTopLeftChild(){
        return topLeftChild;
    }

    /**
     * Returns the top right child node, or null if this node is a leaf node.
     */
    public RectQuadTree getTopRightChild(){
        return topRightChild;
    }

    /**
     * Returns the entire bounds of this node.
     */
    public Rectangle getBounds(){
        return bounds;
    }

    /**
     * Returns the objects in this node only.
     * <p>
     * If this node isn't a leaf node, it will only return the objects that don't fit perfectly into a specific child node (lie on a border).
     */
    public Array<Rectangle> getObjects(){
        return objects;
    }

    /**
     * Returns the total number of objects in this node and all child nodes, recursively
     */
    public int getTotalObjectCount(){
        int count = objects.size;
        if(!leaf){
            count += topLeftChild.getTotalObjectCount();
            count += topRightChild.getTotalObjectCount();
            count += bottomLeftChild.getTotalObjectCount();
            count += bottomRightChild.getTotalObjectCount();
        }
        return count;
    }

    /**
     * Fills the out array with all objects in this node and all child nodes, recursively.
     */
    public void getAllChildren(Array<Rectangle> out){
        out.addAll(objects);

        if(!leaf){
            topLeftChild.getAllChildren(out);
            topRightChild.getAllChildren(out);
            bottomLeftChild.getAllChildren(out);
            bottomRightChild.getAllChildren(out);
        }
    }
}
