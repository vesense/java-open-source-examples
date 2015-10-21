package com.vesense.demobank.yarn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

public class AppMaster {

	public static void main(String[] args) throws Exception {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				long count = 0;
				while (true) {
					count++;
					System.out.println("app master : " + count);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};

		new Thread(r).start();

		final int n = Integer.valueOf(args[0]);
		final Path jarPath = new Path(args[1]);

		// Initialize clients to ResourceManager and NodeManagers
		Configuration conf = new YarnConfiguration();

		AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
		rmClient.init(conf);
		rmClient.start();

		NMClient nmClient = NMClient.createNMClient();
		nmClient.init(conf);
		nmClient.start();

		// Register with ResourceManager
		System.out.println("registerApplicationMaster start");
		RegisterApplicationMasterResponse resp = rmClient
				.registerApplicationMaster("", 0, "");
		Resource mrc = resp.getMaximumResourceCapability();
		System.out.println("max res: " + mrc);
		System.out.println("registerApplicationMaster ok");

		// Priority for worker containers - priorities are intra-application
		Priority priority = Records.newRecord(Priority.class);
		priority.setPriority(0);

		// Resource requirements for worker containers
		Resource capability = Records.newRecord(Resource.class);
		capability.setMemory(128);
		capability.setVirtualCores(1);

		// Make container requests to ResourceManager
		for (int i = 0; i < n; ++i) {
			ContainerRequest containerAsk = new ContainerRequest(capability,
					NodeSelector.getNode(), // node
					null, // rack
					priority,
					false);// disable relax Locality
			System.out.println("Making Container Request " + i);
			rmClient.addContainerRequest(containerAsk);
		}

		// Obtain allocated containers, launch and check for responses
		int completedContainers = 0;
		while (completedContainers < n) {
			// long running services. We always send 50% progress.
			AllocateResponse response = rmClient.allocate(0.5f);
			for (Container container : response.getAllocatedContainers()) {
				// Launch container by create ContainerLaunchContext
				ContainerLaunchContext ctx = Records
						.newRecord(ContainerLaunchContext.class);

				// set command
				ctx.setCommands(Collections.singletonList("$JAVA_HOME/bin/java"
						+ " -Xmx256M" + " com.vesense.demobank.yarn.APPWorker"
						+ " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR
						+ "/stdout" + " 2>"
						+ ApplicationConstants.LOG_DIR_EXPANSION_VAR
						+ "/stderr"));

				// set jar
				LocalResource appJar = Records.newRecord(LocalResource.class);
				AppClient.setupAppMasterJar(jarPath, appJar);
				ctx.setLocalResources(Collections.singletonMap("simpleapp.jar",
						appJar));

				// set worker classpath
				Map<String, String> appEnv = new HashMap<String, String>();
				AppClient.setupAppMasterEnv(appEnv);
				ctx.setEnvironment(appEnv);

				System.out.println("Launching container " + container.getId());
				nmClient.startContainer(container, ctx);
			}
			for (ContainerStatus status : response
					.getCompletedContainersStatuses()) {
				++completedContainers;
				System.out.println("Completed container "
						+ status.getContainerId());
			}
			Thread.sleep(100);
		}

		// Un-register with ResourceManager
		rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED,
				"", "");
	}

}
