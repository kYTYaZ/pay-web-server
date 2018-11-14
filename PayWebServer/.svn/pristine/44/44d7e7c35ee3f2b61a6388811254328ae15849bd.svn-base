package com.huateng.pay.po.queue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;

public class NotifyQueue<T> {
	
	private  LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	
	public NotifyQueue(){
		
	}
	
	public NotifyQueue(int capacity){
		queue = new LinkedBlockingQueue<T>(capacity);
	}
	
	public boolean add(T element){
		return queue.offer(element);
	}
	
	public T poll(){
		return queue.poll();
	}
	
	public T peek(){
		return queue.peek();
	}
	
	public boolean remove(T element){
		return queue.remove(element);
	}
	
	public synchronized int getSize(){
		return queue.size();
	}
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public void drainTo(Collection<? super T> collection,int maxElements){
		queue.drainTo(collection,maxElements);
	}
	
	public List<List<T>> divideQueue(int elemnetsNum){
		
		List<List<T>> list = Lists.newArrayList();
		
		int count = queue.size();
		
		if(count <= 0){
			return null;
		}
		
		if(count <= elemnetsNum){
			List<T> notifyMessageList = Lists.newArrayList();
			this.drainTo(notifyMessageList, count);
			list.add(notifyMessageList);
			return list;
		}
		
		int step = count % elemnetsNum == 0 ? (count / elemnetsNum) : (count / elemnetsNum) + 1;
		
		for(int i = 0; i < step; i++){
			List<T> notifyMessageList = Lists.newArrayList();
			this.drainTo(notifyMessageList, elemnetsNum);
			list.add(notifyMessageList);
		}
		
		return list;
		
	}
	
}
