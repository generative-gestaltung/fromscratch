package de.fromscratch.api;

/**
 * Abstract mathematical node.
 * 
 * @author maxg
 *
 */
public interface Node {
	public String getId ();
	public void setId (String id);
	public void update(float time);
}