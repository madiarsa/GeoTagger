<?xml version="1.0" encoding="UTF-8"?>
<jsp:root
		version="2.0"
		xmlns:jsp="http://java.sun.com/JSP/Page"
		xmlns:c="http://java.sun.com/jsp/jstl/core"
		xmlns:fn="http://java.sun.com/jsp/jstl/functions"
		xmlns:geotag="http://www.horizon.ac.uk/jsp/geotagger">
<jsp:directive.page
		contentType="text/plain; charset=UTF-8" 
		pageEncoding="UTF-8"
		isELIgnored="false" />
<jsp:text>
${geotag:serializeToJSON(tags)}
</jsp:text>
</jsp:root>
