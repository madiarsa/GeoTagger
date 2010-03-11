package horizon.geotagger.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public abstract class ServletWithView 
extends HttpServlet
{
	private static final long serialVersionUID = -6338041578551530078L;
	
	private static Logger logger = Logger.getLogger(ServletWithView.class);

	@Override
	protected final void doDelete(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException 
	{	
		try
		{
			forward(request, response, handleDelete(request, response));
		}
		catch(UnsupportedOperationException e)
		{
			logger.warn(e.getMessage(), e);
			super.doDelete(request, response);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}

	@Override
	protected final void doGet(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException 
	{
		try
		{
			forward(request, response, handleGet(request, response));
		}
		catch(UnsupportedOperationException e)
		{
			logger.warn(e.getMessage(), e);
			super.doGet(request, response);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}

	@Override
	protected final void doPost(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException 
	{
		try
		{
			forward(request, response, handlePost(request, response));
		}
		catch(UnsupportedOperationException e)
		{
			logger.warn(e.getMessage(), e);
			super.doPost(request, response);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}

	@Override
	protected final void doPut(
			HttpServletRequest request, HttpServletResponse response)
	throws	ServletException,
			IOException 
	{
		try
		{
			forward(request, response, handlePut(request, response));
		}
		catch(UnsupportedOperationException e)
		{
			logger.warn(e.getMessage(), e);
			super.doPut(request, response);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
	
	protected ModelAndView handleDelete(
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	protected ModelAndView handleGet(
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	protected ModelAndView handlePost(
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	protected ModelAndView handlePut(
			HttpServletRequest request, HttpServletResponse response)
	throws Exception
	{
		throw new UnsupportedOperationException();
	}
	
	private void forward(
			HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView)
	throws	ServletException,
			IOException
	{
		for(Map.Entry<String, Object> entry : modelAndView.getModel().entrySet())
			request.setAttribute(entry.getKey(), entry.getValue());
		request.getRequestDispatcher(modelAndView.getView())
				.forward(request, response);
	}
}
