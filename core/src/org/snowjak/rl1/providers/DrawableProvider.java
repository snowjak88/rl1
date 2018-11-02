/**
 * 
 */
package org.snowjak.rl1.providers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
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
				
				final ArrayList<ArrayList<Character>> yList = new ArrayList<>();
				for (JsonValue y : r) {
					if (!y.isArray())
						throw new SerializationException();
					
					final ArrayList<Character> xList = new ArrayList<>();
					for (JsonValue x : y)
						xList.add(x.asChar());
					
					yList.add(xList);
				}
				
				rep.add(yList);
			}
			
			int rSize = rep.size(), xSize = 0, ySize = 0;
			for (int r = 0; r < rep.size(); r++) {
				if (rep.get(r).size() > ySize)
					ySize = rep.get(r).size();
				
				for (int y = 0; y < rep.get(r).size(); y++) {
					if (rep.get(r).get(y).size() > xSize)
						xSize = rep.get(r).get(y).size();
				}
			}
			
			d.representation = new char[rSize][ySize][xSize];
			
			for (int r = 0; r < rep.size(); r++)
				for (int y = 0; y < rep.get(r).size(); y++)
					for (int x = 0; x < rep.get(r).get(y).size(); x++)
						d.representation[r][x][y] = rep.get(r).get(y).get(x);
						
			//
			//
			//
			
			if (obj.has("modifiable"))
				d.modifiable = obj.getBoolean("modifiable");
				
			//
			//
			//
			
			if (obj.has("color") && obj.get("color").isArray()) {
				
				final List<Color> colors = new ArrayList<>();
				for (String colorName : obj.get("color").asStringArray()) {
					
					//
					// First, try to evaluate colorName as a hex code (RRGGBBAA)
					try {
						colors.add(Color.valueOf(colorName));
					} catch (IndexOutOfBoundsException | NumberFormatException e) {
						//
						// Second, try to evaluate colorName as the name of a public static field on
						// com.badlogic.gdx.graphics.Color
						colors.add(getByName(colorName));
					}
				}
				d.color = colors.toArray(new Color[0]);
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
