package horizon.geotagger.sensors;

interface SensorServiceLogger
{
    void log(in long timestamp, in String identifier, in String dataBlob);
}