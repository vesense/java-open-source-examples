package com.vesense.demobank.yarn;

import java.util.ArrayList;
import java.util.List;

public class NodeSelector {

	static List<String> list = new ArrayList<String>();
	static int point = 0;

	static {
		list.add("pc-host01");
		list.add("pc-host02");
		list.add("pc-host03");
	}

	//round-robin policy
	public static String[] getNode() {
		if (point >= list.size()) {
			point = 0;
		}
		return new String[] { list.get(point++) };
	}

}
