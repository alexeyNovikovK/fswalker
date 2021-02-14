package com.mycompany.fswalker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;

public class ThreadPool {
    public ThreadPool(int tc){
        threadCount = tc;
        tasks = new LinkedBlockingQueue<>();
        threads = new ArrayList<>();
    }
    public void addTask(Runnable task) throws InterruptedException {
        tasks.put(task);
    }
    public void start(){
        threads.clear();
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Worker());
            t.start();
            threads.add(t);
        }
    }
    public boolean isTasksCompleted(){
        boolean rslt = true;
        for (Thread t : threads){
            if (t.getState() != Thread.State.WAITING)
                rslt = false;
        }
        if (rslt){
            for (Thread t : threads)
                t.interrupt();
        }
        return rslt;
    }

    private Runnable getNextTask() throws InterruptedException {
        return tasks.take();
    }

    private class Worker implements Runnable {
        @Override
        public void run(){
            try {
                while (!Thread.interrupted()) {
                    ThreadPool.this.getNextTask().run();
                }
            }
            catch(InterruptedException ex){
                Log.info("The thread has ended.");
            }
        }
    }

    private final int threadCount;
    private final LinkedBlockingQueue<Runnable> tasks;
    private final ArrayList<Thread> threads;
}
