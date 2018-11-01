/**
 * 
 */
package org.snowjak.rl1.util.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowjak88
 *
 */
public interface Listenable<T extends Event> {
	
	public EventRouter getEventRouter();
	
	@SuppressWarnings("unchecked")
	public default Registration addListener(Object listener, String methodName) {
		
		Method method = null;
		Class<? extends Event> parameterType = null;
		for (Method m : listener.getClass().getMethods())
			if (m.getName().equals(methodName))
				if (m.getParameterTypes().length == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
					method = m;
					parameterType = (Class<? extends Event>) m.getParameterTypes()[0];
					break;
				}
			
		if (method == null || parameterType == null)
			throw new IllegalArgumentException("Given listener method \"" + methodName
					+ "\" does not have a single parameter subclassed from " + Event.class.getName() + ".");
		
		return getEventRouter().addListener(listener, method, parameterType);
	}
	
	public default <V extends T> void fireEvent(V event) {
		
		getEventRouter().handleEvent(event);
	}
	
	@FunctionalInterface
	public interface Registration {
		
		public void remove();
	}
	
	public static class EventRouter {
		
		private final Collection<ListenerMethod> listeners = new LinkedList<>();
		
		public Registration addListener(Object listener, Method method, Class<? extends Event> parameterType) {
			
			final ListenerMethod lm = new ListenerMethod(listener, method, parameterType);
			listeners.add(lm);
			return () -> listeners.remove(lm);
		}
		
		public <V extends Event> void handleEvent(V event) {
			
			for (ListenerMethod lm : listeners.toArray(new ListenerMethod[0]))
				if (lm.canHandle(event.getClass()))
					lm.handle(event);
		}
	}
	
	public static class ListenerMethod {
		
		private static final Logger LOG = LoggerFactory.getLogger(ListenerMethod.class);
		
		private final Object listener;
		private final Method method;
		private final Class<? extends Event> parameterType;
		
		/**
		 * @param listener
		 * @param method
		 * @param parameterType
		 */
		public ListenerMethod(Object listener, Method method, Class<? extends Event> parameterType) {
			
			this.listener = listener;
			this.method = method;
			this.parameterType = parameterType;
		}
		
		public boolean canHandle(Class<? extends Event> argumentType) {
			
			return this.parameterType.isAssignableFrom(argumentType);
		}
		
		public <V extends Event> void handle(V argument) {
			
			try {
				method.invoke(listener, argument);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error("Cannot invoke listener-method {} on {} for {}", method.toString(), listener.toString(),
						argument.toString());
			}
		}
	}
}
