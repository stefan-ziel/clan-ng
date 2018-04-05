package ch.claninfo.clanng.web.servlets;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.MimeTypeUtils;
import org.xml.sax.SAXException;

import ch.claninfo.clanng.business.logic.exceptions.InvalidRequestException;
import ch.claninfo.clanng.web.connect.NgSessionFactory;
import ch.claninfo.clanng.web.metadata.MetaRequestHandler;
import ch.claninfo.common.saxconnect.SAXHandler;
import ch.claninfo.common.xml.SAXUtils;
import ch.claninfo.common.xml.StreamSerializer;

@Configurable
public class CommunicatorNg extends HttpServlet {

	private static final String CMD_META = "meta"; //$NON-NLS-1$ ;

	private static final Logger LOG = LogManager.getLogger();

	@Inject
	private NgSessionFactory sessionFactory;
	@Inject
	private MetaRequestHandler metaRequestHandler;

	/**
	 * @param pReq
	 * @param pResp
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest pReq, HttpServletResponse pResp) throws ServletException, IOException {
		try {
			if (pReq.getParameter(CMD_META) != null) {
				metaRequestHandler.doGet(pReq, pResp);

			} else {
				pReq.getRequestDispatcher("version.jsp").forward(pReq, pResp);
			}
		}
		catch (InvalidRequestException e) {
			pReq.setAttribute("exception", e);
			pReq.getRequestDispatcher("problem.jsp").forward(pReq, pResp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType(MimeTypeUtils.APPLICATION_XML_VALUE);
			String responseEncoding = "UTF-8";
			resp.setCharacterEncoding(responseEncoding);

			try {
				SAXHandler handler = new SAXHandler(sessionFactory);
				handler.setContentHandler(new StreamSerializer(resp.getOutputStream(), responseEncoding));

				SAXUtils.parse(req.getInputStream(), handler);
			}
			catch (SAXException e) {
				throw new IOException(e);
			}
		}
		catch (Throwable e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOG.error("Exception while processing request.", e);
			throw e;
		}
	}
}
