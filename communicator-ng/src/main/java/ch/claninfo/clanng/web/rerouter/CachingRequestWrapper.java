package ch.claninfo.clanng.web.rerouter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Simple wrapper to provide a caheable servlet input stream;
 */
public class CachingRequestWrapper extends HttpServletRequestWrapper {

	private CachedInputStream cachedInputStream;

	public CachingRequestWrapper(ServletRequest request) throws IOException {
		super((HttpServletRequest) request);
		cachedInputStream = new CachedInputStream(super.getInputStream());
	}

	@Override
	public CachedInputStream getInputStream() throws IOException {
		return cachedInputStream;
	}

	public class CachedInputStream extends ServletInputStream {

		/**
		 * Will only cache up to this number of bytes.
		 */
		private static final int MAX_CACHE_BYTES = 10000;

		private final ServletInputStream remainderInputStream;
		private final ByteArrayOutputStream cachedSink = new ByteArrayOutputStream();
		private byte[] readInput;

		private int marker = 0;

		CachedInputStream(ServletInputStream inputStream) {
			this.remainderInputStream = inputStream;
			readInput = new byte[0];
		}


		@Override
		public boolean isFinished() {
			return remainderInputStream.isFinished();
		}

		@Override
		public boolean isReady() {
			return remainderInputStream.isReady();
		}

		@Override
		public void setReadListener(ReadListener listener) {
			remainderInputStream.setReadListener(listener);
		}

		@Override
		public int read() throws IOException {
			if (marker >= readInput.length) {
				int read = remainderInputStream.read();
				if (read != -1 && nOfBytesAvailableInCache() > 0) {
					marker++;
					cachedSink.write(read);
				}
				return read;
			}

			return readInput[marker++];
		}

		public int nOfBytesAvailableInCache() {
			return MAX_CACHE_BYTES - cachedSink.size();
		}

		@Override
		public void reset() throws IOException {
			if (nOfBytesAvailableInCache() <= 0) {
				throw new IOException(
						String.format("Stream reset no longer possible. Maximum %d bytes cached, but %d bytes read.",
						              MAX_CACHE_BYTES, marker));
			}
			marker = 0;
			readInput = cachedSink.toByteArray();
		}
	}
}