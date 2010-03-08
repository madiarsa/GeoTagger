package horizon.geotagger.sensors;

import horizon.geotagger.sensors.LoggingServiceDescriptor;

interface SensorServiceControl 
{
	List<LoggingServiceDescriptor> getLoggers();
	
	void stopLogger(in LoggingServiceDescriptor descriptor);
	
	void startLogger(in LoggingServiceDescriptor descriptor);
	
	boolean getLoggerStatus(in LoggingServiceDescriptor descriptor);
}