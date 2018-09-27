package com.basiscomponents.bc.util;

public class CloseableWrapper<T extends AutoCloseable> implements AutoCloseable {
	
	private T closeable;
	private boolean willBeClosed;

	CloseableWrapper(final T closeable, final boolean willBeClosed) {
		this.closeable = closeable;
		this.willBeClosed = willBeClosed;
	}

	@Override
	public void close() throws Exception {
		if (willBeClosed) {
			closeable.close();
		}
	}

	public T getCloseable() {
		return closeable;
	}

}
