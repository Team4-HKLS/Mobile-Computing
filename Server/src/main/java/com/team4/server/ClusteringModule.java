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
import java.util.Set;

/* input: student list of collected data files
 * output: clusters by all neighbors (regardless of signal strength)
 *         ex) [[a,b,c],[d],[e]] (ArrayList of Set)
 */

public class ClusteringModule {
	static final int Noise = -2, undefined = -1;
	static final int referenceRSSI = -60;
	static final int threshold = 4;	
	static final int k = 3;
	static final int minPts = 3; // DBSCAN min points
	ArrayList<String> studentList;
	Map<String, Set<String>> allNeighbors;
	Map<String, Integer> label = new HashMap<String, Integer>();

	public ArrayList<Set<String>> start(ArrayList<String> studentList) {
		this.studentList = studentList;
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
			allNeighbors.put(id, findNeighborsNear(folder));
		}
		dbscan(minPts-1);
		
		return clustering();
	}
	
	// calculate neighbors of each device.
	private Set<String> findNeighborsNaive(File folder) {
		Set<String> neighbors = new HashSet<String>();
		
		String[] txts = folder.list();
		Arrays.sort(txts);
		int studentIndex = studentList.indexOf(folder.getName());
		int neighborIndex = 0;
		
		try {
			for (int i = 0; i < txts.length; i++) {
				if(studentIndex == neighborIndex)
					neighborIndex += 1;
				//File file = new File(folder.getPath() + "\\" + txts[i]); // text file
				File file = new File(folder.getPath() + "/" + txts[i]);
				
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				while ((line = br.readLine()) != null) {
					String[] tokens = line.split("\\s+");
					
					// format: date + time + MACaddress(=ID) + SS + txPower
					if (tokens.length > 4 && neighborIndex == studentList.indexOf(tokens[2])) {
						neighbors.add(tokens[2]);
					}
				}
				neighborIndex += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return neighbors;
	}
	
	private Set<String> findNeighborsNear(File folder) {
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
	
	private void dbscan(int minPts) {
		int C = 0;
		Set<String> S;
		for(String P : studentList) {
			if(label.containsKey(P)) continue;
			Set<String> neighbors = allNeighbors.get(P);
			
			if(neighbors.size() < minPts) {
				label.put(P, Noise);
				continue;
			}
			C = C + 1;
			label.put(P, C);
			S = neighbors;
			S.add(P);
			Iterator<String> iter = S.iterator();
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
		}
		
		for(String s : studentList) {
			System.out.println("student " + s + ": " + label.get(s));
		}
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

