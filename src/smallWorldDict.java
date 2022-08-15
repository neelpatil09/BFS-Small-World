import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;

public class smallWorldDict {
	static ArrayList<ActorRecord> act = new ArrayList<ActorRecord>();
	static TreeMap<String, ArrayList<Integer>> movieD = new TreeMap<String, ArrayList<Integer>>();
	static int actorStart, actorEnd;
	static int[] parents;
	static Stack<Integer> path = new Stack<Integer>();
	static int[] checked;
	static long start1, end1;
	public static void main(String[] args) throws Exception {
		String fname = "/Users/neelpatil/Documents/CS114Java/actresses.list.gz";
		String fname1 = "/Users/neelpatil/Documents/CS114Java/actors.list.gz";
		long start = System.currentTimeMillis();
		RetrieveActors ra = new RetrieveActors(fname);
        String content;
        String[] tkn;
        TreeMap<String, Integer> actors = new TreeMap<String,Integer>();
        while ((content = ra.getNext()) != null) {
        	tkn = content.split("@@@");
        	ActorRecord ar = new ActorRecord(tkn[0]);
        	for (int i = 1; i < tkn.length; ++i){
        		if(tkn[i].substring(0, 2).equals("FM"))
        		ar.addMovie(tkn[i].substring(2));
        	}
        	act.add(ar); 
        }
        ra.close();
        RetrieveActors ra1 = new RetrieveActors(fname1);
        String content1;
        String[] tkn1;
        while ((content1 = ra1.getNext()) != null) {
        	tkn1 = content1.split("@@@");
        	ActorRecord ar1 = new ActorRecord(tkn1[0]);
        	for (int i = 1; i < tkn1.length; ++i){
        		if(tkn1[i].substring(0, 2).equals("FM"))
        		ar1.addMovie(tkn1[i].substring(2));
        	}
        	act.add(ar1); 
        }
        ra1.close();
        
        /*
        for(int x = 0; x < act.size(); x++) actors.put(act.get(x).name, x);
        System.out.println(act.get(728113).name);
        System.out.println(actors.get("Bacon, Kevin (I)"));
        System.out.println(actors.get("Lamarr, Hedy"));
        */
        long end = System.currentTimeMillis();
        System.out.println(act.size() + " actors and actresses. Done in " + (end-start) + " ms");
        parents = new int[act.size()];
        checked = new int[act.size()];
        for(int x = 0; x < parents.length; x++)parents[x] = -9;
        
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter first actor index");
        int actor1 = myObj.nextInt();
        System.out.println("Enter second actor index");
        int actor2 = myObj.nextInt();
        if(actor1 == actor2 || actor1 < 0 || actor2 < 0 || actor1 > act.size()-1 || actor2 > act.size()-1){
            System.out.println("Enter valid indices!");
            System.exit(0);
        }
        else{
            actorStart = actor1;
            actorEnd = actor2;
            createMovieMap();
        }
        
	}
	private static void createMovieMap() {
		start1 = System.currentTimeMillis();
		for(int actor = 0; actor < act.size(); actor++) {
			for(int movie = 0; movie < act.get(actor).movies.size(); movie++) {
				if(!movieD.containsKey(act.get(actor).movies.get(movie))) {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(actor);
					movieD.put(act.get(actor).movies.get(movie),temp);
				}
				else {
					ArrayList<Integer> temp = movieD.get(act.get(actor).movies.get(movie));
					temp.add(actor);
					movieD.put(act.get(actor).movies.get(movie), temp);
				}
			}
		}
		findActor(actorStart);
	}
	private static void findActor(int start) {
		LinkedList<Integer> Q = new LinkedList<Integer>();
        Q.addLast(start);
        while (Q.size() > 0) {
            int v = Q.removeFirst();
            for(int movie = 0; movie < act.get(v).movies.size(); movie++) {
            	ArrayList<Integer> temp = movieD.get(act.get(v).movies.get(movie));
            	for(int actor = 0; actor < temp.size(); actor++) {
            		if(checked[temp.get(actor)] == 1) continue;
            		setParent(v, temp.get(actor));
            		if(temp.get(actor) == actorEnd) {
            			path = getPath(temp.get(actor));
            			printPath();
            		}
            		Q.add(temp.get(actor));
            	}
            }
        }
        System.out.println("No path between " + act.get(actorStart).name + " and " + act.get(actorEnd).name);
        System.exit(0);
	}
	private static void setParent(int parent, int child) {
		checked[child] = 1;
		if(child == actorStart) parents[child] = -1;
        if(parents[child] == -9) parents[child] = parent;
	}
	private static Stack<Integer> getPath(int child) {
		path.push(child);
    	if (parents[child] == -9 || parents[child] == -1) return path;
        return getPath(parents[child]);
	}
	private static String commonMovie(int a1, int a2) {
		for(int a = 0; a < act.get(a1).movies.size();a++) {
			if(act.get(a2).appearedIn(act.get(a1).movies.get(a))) return act.get(a1).movies.get(a);
		}
		return "This should not have happened!";
	}
	private static void printPath() {
		System.out.println("Shortest path between " + act.get(actorStart).name + " and " + act.get(actorEnd).name);
        System.out.println("Distance: " + (path.size()-1) + "; the chain is:");
        int temp1,temp2;
        String movieTemp;
        while(path.size()>1) {
        	temp1 = path.pop();
        	temp2 = path.peek();
        	movieTemp = commonMovie(temp1,temp2);
            System.out.println(act.get(temp1).name + " appeared with " + act.get(temp2).name + " in " + movieTemp);
        }
        end1 = System.currentTimeMillis();
        System.out.println("Path found in " + (end1-start1) + " ms");
        System.exit(0);
	}
}
