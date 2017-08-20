package ru.sendto.gwt.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event bus
 * @author Lev Nadeinsky
 * @date	2017-04-29
 */
public class Bus {

	/**
	 * Event
	 * @author Lev Nadeinsky
	 * @date	2017-05-01
	 */
	public static interface Event<A,R>{
		R invoke (A a);
	}
	/**
	 * Event without return value
	 * @author Lev Nadeinsky
	 * @date	2017-05-01
	 */
	public static interface VoidEvent<A>{
		void invoke (A a);
	}
	/**
	 * Hashmap for event binding
	 * @author Lev Nadeinsky
	 * @date	2017-05-01
	 */
	static class EventMap extends HashMap<Class, List<Event>>{
		
		@Override
		public List<Event> get(Object key) {
			List<Event> list = super.get(key);
			return list==null?new ArrayList<Bus.Event>():list;
		}
		
		public <A,R> void listen(Class<A> key, Event<A,R> value) {
			List<Event> list = get(key);
			list.add(value);
			put(key, list);
		}
		
		public <A> void listen(Class<A> key, VoidEvent<A> value) {
			List<Event> list = get(key);
			list.add(t->{value.invoke((A)t);return null;});
			put(key, list);
		}
		
		public <A,R> List<R>  fire (A o){
			Class<A> clz = (Class<A>) o.getClass();
			
			List tmp =  get(clz);
			List<Event<A,R>> list =tmp;
			Object[] results = new Object[list.size()];
			for (int i=0 ; i< list.size(); i++){//Event<A, R> event : list) {
				results[i]=list.get(0).invoke(o);
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
	 */
	public <A,R> void listen(Class<A> key, Event<A,R> value) {
		map.listen(key, value);
	}
	/**
	 * Add void listener
	 * @param key - object class
	 * @param value - event listener
	 */
	public <A> void listen(Class<A> key, VoidEvent<A> value) {
		map.listen(key, value);
	}
	public <A,R> List<R> 
		fire (A o){
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
	 * @return event bus
	 */
	static public Bus get(String busName){
		Bus bus = busMap.get(busName);
		if(bus==null) {
			bus=new Bus();
			busMap.put(busName, bus);
		}
		return bus;
	}
	/**
	 * Get bus instance by class name
	 * @return event bus
	 */
	static public Bus get(Class<?> busName){
		Bus bus = busMap.get(busName);
		if(bus==null) {
			bus=new Bus();
			busMap.put(busName.getName(), bus);
		}
		return bus;
	}
	/**
	 * Get bus instance by object class name
	 * @return event bus
	 */
	static public Bus get(Object busName){
		Bus bus = busMap.get(busName);
		if(bus==null) {
			bus=new Bus();
			busMap.put(busName.getClass().getName(), bus);
		}
		return bus;
	}
	
}
