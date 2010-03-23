package horizon.geotagger;

import java.util.logging.Logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class JavaLoggingAppender 
extends AppenderSkeleton
{	
	@Override
	protected void append(LoggingEvent logEvent)
	{
		Logger logger = Logger.getLogger(logEvent.getLoggerName());
		String msg = getLayout().format(logEvent);
		switch(logEvent.getLevel().toInt())
		{
		case Level.TRACE_INT:
		case Level.ALL_INT:
		case Level.DEBUG_INT:
			logger.info(msg);
			break;
		case Level.ERROR_INT:
		case Level.FATAL_INT:
			logger.severe(msg);
			break;
		case Level.INFO_INT:
			logger.info(msg);
			break;
		case Level.WARN_INT:
			logger.warning(msg);
			break;
		case Level.OFF_INT:
		default:
		}
	}

	@Override
	public void close()
	{		
	}

	@Override
	public boolean requiresLayout()
	{
		return true;
	}
}
