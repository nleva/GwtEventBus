package ru.sendto.gwt.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event bus
 * @author Lev Nadeinsky
 */
public class Bus {

	/**
	 * Event
	 * @author Lev Nadeinsky
	 */
	public static interface Event<A,R>{
		R invoke (A a);
	}
	/**
	 * Event without return value
	 * @author Lev Nadeinsky
	 */
	public static interface VoidEvent<A> extends Event<A, Void>{
		@Override
		default Void invoke (A a) {
			invokeVoid(a);
			return null;
		};

		void invokeVoid (A a);
	}
	/**
	 * Hashmap for event binding
	 * @author Lev Nadeinsky
	 */
	static class EventMap extends HashMap<Class, List<Event>>{
		
		HashMap<Event,List<List<Event>>> reverseMap = new HashMap<>();
		
		@Override
		public List<Event> get(Object key) {
			List<Event> list = super.get(key);
			return list==null?new ArrayList<Bus.Event>():list;
		}
		
		public <A,R> void wire(Class<A> key, Event<A,R> value) {
			List<Event> list = get(key);
			list.add(value);
			put(key, list);
			List<List<Event>> lists = reverseMap.get(value);
			if(lists==null) {
				lists=new ArrayList<>();
				reverseMap.put(value, lists);
			}
			lists.add(list);
		}

		
		public <A,R> void remove(Event<A,R> value) {
			final List<List<Event>> lists = reverseMap.get(value);
			if(lists==null)
				return;
			lists.forEach(list->list.remove(value));
		}
		
		
		
		public <A,R> List<R>  fire (A o){

			Class<A> clz = (Class<A>) o.getClass();
			List tmp =  get(clz);
			List<Event<A,R>> list =tmp;
			
			Object[] results = new Object[list.size()];
			for (int i=0 ; i< list.size(); i++){//Event<A, R> event : list) {
				results[i]=list.get(i).invoke(o);
			}
			
			return (List<R>) Arrays.asList(results);
		}
	}
	
	//Map for events
	private EventMap map = new EventMap();
	//instance of a primary bus
	static private Bus bus;
	//map of buss
	static private Map<String,Bus> busMap = new HashMap<>();
	private Bus() {
	}
	/**
	 * Add listener
	 * @param key - object class
	 * @param value - event listener
	 * @param <A> - event listener
	 * @param <R> - event listener
	 */
	public <A,R> void listen(Class<A> key, Event<A,R> value) {
		map.wire(key, value);
	}
	/**
	 * Add void listener
	 * @param key - object class
	 * @param value - event listener
	 * @param <A> - event listener
	 */
	public <A> void listen(Class<A> key, VoidEvent<A> value) {
		map.wire(key, value);
	}
	
	public <A,R> void removeAllListeners(Event<A,R> value) {
		map.remove(value);
	}
	
	public <A,R> List<R> fire (A o){
		return map.fire(o);
	}

	/**
	 * Get bus instance
	 * @return event bus
	 */
	static public Bus get(){
		return bus==null?bus=new Bus():bus;
	}
	/**
	 * Get bus instance by name
	 * @param busName bus name
	 * @return event bus
	 */
	static public Bus getBy(String busName){
		if(busName==null)
			return get();
		
		Bus bus = busMap.get(busName);
		if(bus==null) {
			bus=new Bus();
			busMap.put(busName, bus);
		}
		return bus;
	}
	/**
	 * Get bus instance by class name
	 * @param busName bus name
	 * @return event bus
	 */
	static public Bus getBy(Class<?> busName){
		if(busName==null)
			return get();
		
		return getBy(busName.getName());
	}
	/**
	 * Get bus instance by object class name
	 * @param busName bus name
	 * @return event bus
	 */
	static public Bus getBy(Object busName){
		if(busName==null)
			return get();
			
		return getBy(busName.getClass());
	}
	
}
