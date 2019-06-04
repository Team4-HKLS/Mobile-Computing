package com.team4.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.List;

/* input: student list of collected data files
 * output: clusters by all neighbors (regardless of signal strength)
 *         ex) [[a,b,c],[d],[e]] (ArrayList of Set)
 */

public class ClusteringModule {
	static final int Noise = -2, undefined = -1;
	static final int referenceRSSI = -60; // 1m rssi
	static final int threshold = 10;	// distance threshold
	static final int minPts = 3; // DBSCAN min points
	private ArrayList<String> studentList;
	private Map<String, Set<String>> allNeighbors;
	private Map<String, Integer> label = new HashMap<String, Integer>(); // (deviceID, clusterID)

	public void start(ArrayList<Student> List) {
		if(List.isEmpty()) {
			System.out.println("#################### No elements to cluster ####################");
			return;
		}
		this.studentList = new ArrayList<String>();
		for(Student s : List) {
			this.studentList.add(s.getDeviceID());
			System.out.println("Add student " + s.getDeviceID());
		}
		allNeighbors = new HashMap<String, Set<String>>();

		//String path = ClusteringModule.class.getResource("").getPath();
		String path = "/home/kwkwon/DeviceFolder";
		ArrayList<String> folders = getFolderList(path);

		// For each device, find neighbors and store to allNeighbors.
		Iterator<String> it = folders.iterator();
		while (it.hasNext()) {
			String id = it.next();
			//File folder = new File(path + "\\" + id);
			File folder = new File(path + "/" + id);
			allNeighbors.put(id, findNeighbors(folder));
		}

		System.out.println("################### Call dbscan ####################");
		dbscan(minPts-1);

		// Find the largest cluster.
		Map<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for (Integer t : label.values()) {
			Integer c = temp.get(t);
			temp.put(t, (c == null) ? 1 : c + 1);
		}
		int largest_cls = 0;
		for (Map.Entry<Integer, Integer> m : temp.entrySet()) {
			if (m.getKey() == -2) continue;
			System.out.println("The number of elements in cluster #" + m.getKey() + " is " + m.getValue());
			if (largest_cls < m.getValue()) {
				largest_cls = m.getKey();
			}
		}
		System.out.println("########## The largest cluster is #" + largest_cls + " ###########");
		
		// Write the attendance result to each student object.
		Iterator<Student> it2 = List.iterator();
		while (it2.hasNext()) {
			Student s = it2.next();
			if (label.get(s.getDeviceID()) == largest_cls)
				s.setClusteringResult(true);
			else
				s.setClusteringResult(false);
		}	
	}
	
	private Set<String> findNeighbors(File folder) {
		Set<String> neighbors = new HashSet<String>();
		
		String[] txts = folder.list();
		Arrays.sort(txts);
		int studentIndex = studentList.indexOf(folder.getName());
		int neighborIndex;
		double distance;
		int cnt;
		
		try {
			neighborIndex = 0;
			for (int i = 0; i < txts.length; i++) {
				if(studentIndex == neighborIndex)
					neighborIndex += 1;
				//File file = new File(folder.getPath() + "\\" + txts[i]); // text file
				File file = new File(folder.getPath() + "/" + txts[i]); // text file
				
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				distance = 0; cnt = 0;
				while ((line = br.readLine()) != null) {
					String[] tokens = line.split("\\s+");
					
					// format: date + time + MACaddress(=ID) + SS + txPower
					if (tokens.length > 4 && neighborIndex == studentList.indexOf(tokens[2])) {
						distance += Math.pow(10,  (float) (referenceRSSI - Integer.parseInt(tokens[3]))/20);
						cnt += 1;
					}
				}
				System.out.println("distance from " + studentIndex + " to " + neighborIndex + ": " + ((float) distance / cnt));
				if (((float) distance / cnt) < threshold) {
					neighbors.add(studentList.get(neighborIndex));
				}
				
				neighborIndex += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return neighbors;
	}
	
	/*
	private ArrayList<Set<String>> clustering() {
		ArrayList<Set<String>> clusters = new ArrayList<Set<String>>();
		for(String student : allNeighbors.keySet()) {
			if(!addToExistClusters(clusters, student)) {
				Set<String> temp = allNeighbors.get(student);
				temp.add(student);
				clusters.add(temp);
			}
		}
		
		return clusters;
	}
	
	private boolean addToExistClusters(ArrayList<Set<String>> clusters, String student) {
		for(Set<String> key : clusters) {
			if(key.contains(student)) {
				clusters.remove(key);
				key.addAll(allNeighbors.get(student));
				clusters.add(key);
				return true;
			}
		}
		
		return false;
	}
	*/
	private void dbscan(int minPts) {
		int C = 0;
		Set<String> s;
		for(String P : studentList) {
			if(label.containsKey(P)) continue;
			Set<String> neighbors = allNeighbors.get(P);
			
			if(neighbors.size() < minPts) {
				label.put(P, Noise);
				continue;
			}
			// The case that new seed node is found
			C = C + 1;
			label.put(P, C);
			s = neighbors;
			s.add(P);
			//Iterator<String> iter = s.iterator();
			String S[] = new String[s.size()];
			S = s.toArray(S);

			int k = -1;
			//for(int i=0; i<S.length; i++) {
			while((k+1) < S.length) {
				k += 1;
				String Q = S[k];
				if(label.containsKey(Q) && label.get(Q) == Noise) {
					label.put(Q, C);
				}
				if(label.containsKey(Q)) {
					continue;
				}
				label.put(Q, C);
				Set<String> N = allNeighbors.get(Q);
				if(N.size() >= minPts) {
					List list = new ArrayList(Arrays.asList(S));
					list.addAll(Arrays.asList(N.toArray()));
					S = (String[]) list.toArray(new String[0]);
					//S = list.stream().toArray(String[]::new);
					//Object[] c = list.toArray();
					//S = Arrays.toString(c);
					
					//S = (String[])ArrayUtils.addAll(S, N.toArray());
				}
			}
			/*
			Set<String> T = new HashSet<String>();
			while(iter.hasNext()) {
				String Q = iter.next();
				if(label.containsKey(Q) && label.get(Q) == Noise) {
					label.put(Q, C);					
				}
				if(label.containsKey(Q)) {
					continue;
				}
				label.put(Q, C);
				Set<String> N = allNeighbors.get(Q);
				if(N.size() >= minPts) {
					T.addAll(N);
				}
				if(!iter.hasNext() && !T.isEmpty()) {
					iter = T.iterator();
					T.clear();
				}
			}
			*/
		}

		for(int i=1; i<C+1; i++) {
			List lst = new ArrayList<String>();
			Iterator<String> iter = label.keySet().iterator();
			while(iter.hasNext()) {
				String str = iter.next();
				if(label.get(str) == i)
					lst.add(str);
			}
			System.out.println("--Cluster " + i + " (#:" + lst.size() + ")\n" + lst);
		}
		/*
		for(String student : studentList) {
			System.out.println("student " + student + " => " + label.get(student));
		}
		*/
	}
	
	private ArrayList<String> getFolderList(String path) {
		File file = new File(path);
		String[] folders = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		return new ArrayList<String>(Arrays.asList(folders));
	}

}

