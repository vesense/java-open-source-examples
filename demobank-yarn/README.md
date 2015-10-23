# demo for app on yarn
This demo is a long running service. It's an ordinary app on yarn.

By this demo, we could learn how yarn works step by step.

In addition, we use NodeSelector to decide which node the worker assigned.

AppClient:

1. create yarn-client via yarn-configrution
2. create yarn-app via yarn-client
3. setup app-submit-context for yarn-app
4. yarn-client submit yarn-app

AppMaster:

1. create rm-client
2. Register with ResourceManager
3. create container-request
4. Obtain allocated containers, launch and check for responses
5. Un-register with ResourceManager

##Deploy
    $ bin/hadoop fs -put demobank-yarn-0.0.1-SNAPSHOT-jar-with-dependencies.jar /apps/

    $ bin/hadoop jar demobank-yarn-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.vesense.demobank.yarn.AppClient 2 hdfs:///apps/demobank-yarn-0.0.1-SNAPSHOT-jar-with-dependencies.jar

the first param is worker number, the second param is jar path.


	
