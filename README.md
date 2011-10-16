=== Usage:
  ```java -jar lces-asset-deployer.jar myconfig.yml```
	  
	  where the config.yml looks like:
	  
```
### Required arguments
file: ${path_to_your_asset}\MyApp.swf
asset_name: MyApp.swf
lces_path: /Applications/MyProcess/1.0/Assets
### Optional arguments
#ejb_host: localhost
#ejb_port: 1099
#username: administrator
#password: password
#mime_type: blah
#description: blah
```

based on: http://help.adobe.com/en_US/livecycle/9.0/programLC/help/000315.html#1548555
(including the comment at the bottom about refreshing the assets)

Required jars (can't be supplied here - you'll need a livecycle install):
adobe-livecycle-client.jar
adobe-repository-client.jar
adobe-usermanager-client.jar
adobe-utilities.jar
commons-codec-1.3.jar
jbossall-client.jar
snakeyaml-1.8.jar

These JAR files are located in the following path: ${Livecycle install directory}/Adobe/Adobe LiveCycle ES2/LiveCycle_ES_SDK/client-libs/common
For complete details about the location of these JAR files, see "Including LiveCycle ES2 library files" in Programming with LiveCycle ES2

