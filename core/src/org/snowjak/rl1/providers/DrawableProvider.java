/**
 * 
 */
package org.snowjak.rl1.providers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;

import org.snowjak.rl1.drawable.Drawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * @author snowjak88
 *
 */
public class DrawableProvider extends Provider<Drawable> {
	
	private final JsonReader json = new JsonReader();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.snowjak.rl1.providers.Provider#load(java.lang.String)
	 */
	@Override
	protected Optional<Drawable> load(String name) {
		
		final FileHandle fh = Gdx.files.internal("drawables/" + name + ".json");
		
		if (!fh.exists())
			return Optional.empty();
		
		try {
			final JsonValue obj = json.parse(fh);
			
			final Drawable d = new Drawable();
			final ArrayList<ArrayList<ArrayList<Character>>> rep;
			
			if (!obj.has("representation"))
				throw new SerializationException();
			if (!obj.get("representation").isArray())
				throw new SerializationException();
			
			rep = new ArrayList<>();
			
			for (JsonValue r : obj.get("representation")) {
				if (!r.isArray())
					throw new SerializationException();
				
				final ArrayList<ArrayList<Character>> xList = new ArrayList<>();
				for (JsonValue x : r) {
					if (!x.isArray())
						throw new SerializationException();
					
					final ArrayList<Character> yList = new ArrayList<>();
					for (JsonValue y : x)
						yList.add(y.asChar());
					
					xList.add(yList);
				}
				
				rep.add(xList);
			}
			
			int rSize = rep.size(), xSize = 0, ySize = 0;
			for (int r = 0; r < rep.size(); r++) {
				if (rep.get(r).size() > xSize)
					xSize = rep.get(r).size();
				
				for (int x = 0; x < rep.get(r).size(); x++) {
					if (rep.get(r).get(x).size() > ySize)
						ySize = rep.get(r).get(x).size();
				}
			}
			
			d.representation = new char[rSize][xSize][ySize];
			
			for (int r = 0; r < rep.size(); r++)
				for (int x = 0; x < rep.get(r).size(); x++)
					for (int y = 0; y < rep.get(r).get(x).size(); y++)
						d.representation[r][x][y] = rep.get(r).get(x).get(y);
					
			if (obj.has("color") && !(obj.getString("color").trim().isEmpty())) {
				
				final String colorName = obj.getString("color");
				
				//
				// First, try to evaluate colorName as a hex code (RRGGBBAA)
				try {
					d.color = Color.valueOf(colorName);
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					//
					// Second, try to evaluate colorName as the name of a public static field on
					// com.badlogic.gdx.graphics.Color
					d.color = getByName(colorName);
				}
			}
			
			return Optional.of(d);
			
		} catch (SerializationException e) {
			
		}
		
		return Optional.empty();
	}
	
	private Color getByName(String name) {
		
		try {
			
			final Field f = Color.class.getDeclaredField(name);
			if (Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers())
					&& Modifier.isFinal(f.getModifiers())) {
				final Object v = f.get(null);
				if (v instanceof Color)
					return (Color) v;
				else
					return null;
			}
			
		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
}
