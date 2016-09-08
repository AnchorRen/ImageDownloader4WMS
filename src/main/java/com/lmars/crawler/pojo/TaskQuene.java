package com.lmars.crawler.pojo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TaskQuene {

	public List<UrlParams> queue = new LinkedList<UrlParams>();   // 添加一项任务
    
	
    public List<UrlParams> getQueue() {
		return queue;
	}

	public void setQueue(List<UrlParams> queue) {
		this.queue = queue;
	}

	public synchronized void addTask(UrlParams task) {   
        if (task != null) {   
            queue.add(task);   
        }   
    }     
     
    public synchronized void finishTask(UrlParams task) {  
    if (task != null) {   
        queue.remove(task);   
        }   
    }     
     
    public synchronized Object getTask() {   
        Iterator<UrlParams> it = queue.iterator();   
        Object task;   
        while (it.hasNext()) {   
            task = it.next();   //新建任务返回
            queue.remove(task);
            return task;   
            }   
            return null;   
      }   
}
