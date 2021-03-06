package com.github.cypher.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Updater extends Thread {

	// Holds all updatable classes
	// Initiated to size 10 but will resize if necessary
	// The value (Integer) represents the "tick interval". I.e. the Updatable will be notified every {i}'th tic (where i is the value of the Integer)
	private final Map<Updatable, Integer> watching = new ConcurrentHashMap<>(10);

	private volatile boolean stopping = false;

	// The time between each tic in ms
	private final int interval;

	interface Updatable {

		// To be called continuously at a defined interval
		void update();
	}

	Updater(int interval) {
		this.interval = interval;
	}

	// Tic loop
	@Override
	public void run() {
		for (int i = 1; !Thread.interrupted() && !stopping ; i++) {
			// Notify all Updatable classes
			for (Map.Entry<Updatable, Integer> entry : watching.entrySet()) {
				if (i % entry.getValue() == 0) {
					entry.getKey().update();
				}
			}

			// Sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// Register a listener. The listener will be notified every {i}'th tic
	public void add(Integer i, Updatable u) {
		watching.put(u, i);
	}

	// Remove a listener
	public void remove(Updatable u) {
		watching.remove(u);
	}

	public void endGracefully() {
		stopping = true;
	}
}
