<!DOCTYPE html>
<html>
<head>
	<base href="@APP.VERSION@"/>
	
	<title>Application</title>
	
	<@css.bundle theme="blue"@ />
	<@css.bundle alternateTheme="red"@ />
	
	<!-- libraries thirdparty (js-bundler doesn't support third party libraries yet) -->
	<script id="sl4b" type="text/javascript" src="unbundled-resources/javascript-thirdparty/sl4b/index.js"
	 user="" credentialsprovider="keymaster" enableflash commondomain="@LIBERATOR.DOMAIN@"
	 keymasterurl="servlet/StandardKeyMaster" keymasterxhrurl="servlet/XHRKeymaster"
	 keymasterconnectiontimeout="60000" keymasterkeepaliveinterval="30000"
	 clocksyncstrategy="GF_SlidingClockSyncStrategy"
	 service="unbundled-resources/liberator-service-configuration.xml"></script>
	
	<@js.bundle@ />
	<@i18n.bundle@ />
	<@css.bundle@/>
	
	<script>
		var oApp = new jspapp.ShinyApp();
	</script>
</head>
<body onload="oApp.initWebcentric(event);" onselectstart="return suppress_unwanted_select(event);">
</body>
</html>