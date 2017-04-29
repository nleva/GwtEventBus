package ru.sendto.gwt.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Lev Nadeinsky
 * @date	2017-04-29
 */
public class Bus {

	public static interface Event<A,R>{
		R invoke (A a);
	}
	public static interface VoidEvent<A>{
		void invoke (A a);
	}
	
	static class EventMap extends HashMap<Class, List<Event>>{
		
		@Override
		public List<Event> get(Object key) {
			// TODO Auto-generated method stub
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
		
		public <A,R> List<R> 
						fire
						(A o){
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
	
	
	private EventMap map = new EventMap();
	static private Bus bus;
	private Bus() {
	}
	
	public <A,R> void listen(Class<A> key, Event<A,R> value) {
		map.listen(key, value);
	}
	public <A> void listen(Class<A> key, VoidEvent<A> value) {
		map.listen(key, value);
	}
	public <A,R> List<R> 
		fire (A o){
		return map.fire(o);
	}

	static public Bus get(){
		return bus==null?bus=new Bus():bus;
	}
	
}
