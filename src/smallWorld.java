import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.LinkedList;
public class smallWorld {
    static ArrayList<ActorRecord> act = new ArrayList<ActorRecord>();
    static Graphl mainGraph;
    static String[][] movies;
    static int actorStart, actorEnd;
    static int[] parents;
    static Stack<Integer> path;
    
    public static void main(String[] args) throws Exception {
        String fname = "path to list of actors";     
        RetrieveActors ra = new RetrieveActors(fname);

        String content;
        String[] tkn;
        
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
        createGraph();
    }
    
    private static void createGraph(){
        mainGraph = new Graphl(act.size());
        movies = new String[act.size()][act.size()];
        parents = new int[act.size()];
        path = new Stack<Integer>();

        for(int x = 0; x < parents.length;x++) parents[x] = -9;
        for(int a1 = 0; a1 < act.size(); a1++){
            for(int movieIndex = 0; movieIndex < act.get(a1).movies.size(); movieIndex++){
                for(int a2 = 0; a2< a1; a2++){
                    if(a2 == a1){
                        continue;
                    }
                    if(act.get(a2).appearedIn(act.get(a1).movies.get(movieIndex))){
                        createEdge(a1,a2,act.get(a1).movies.get(movieIndex));
                    }
                }
            }
        }

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
            BFS(mainGraph,actorStart);
        }
    }
    
    private static void createEdge(int a, int b, String movie){
        mainGraph.setEdge(a, b, 1);
        mainGraph.setEdge(b, a, 1);
        movies[a][b] = movie;
        movies[b][a] = movie;
    }
    
    private static String getMovie(int a, int b){
        return movies[a][b];
    }
    
    private static void BFS(Graph G, int start){
        LinkedList<Integer> Q = new LinkedList<Integer>();
        Q.addLast(start);
        G.setMark(start, 1);
        while (Q.size() > 0) {
            int v = Q.removeFirst();
            for (int w = G.first(v); w < G.n(); w = G.next(v, w)){
                if (G.getMark(w) == 0) {
                    setParent(v, w);
                    G.setMark(w, G.getMark(v) + 1);
                    Q.addLast(w);
                }
                if(act.get(w).name.equals(act.get(actorEnd).name)){
                    path = getPath(w);
                    printPath(); 
                }
            }
        }
        System.out.println("No path between " + act.get(actorStart).name + " and " + act.get(actorEnd).name);
    }
    
    private static void setParent(int parent, int child){
        if(child == actorStart) parents[child] = -1;
        if(parents[child] == -9) parents[child] = parent;
        
    }
    
    private static Stack<Integer> getPath(int child){
    	path.push(child);
    	if (parents[child] == -9) return path;
        return getPath(parents[child]);
    }
    
    private static void printPath(){
        System.out.println("Shortest path between " + act.get(actorStart).name + " and " + act.get(actorEnd).name);
        System.out.println("Distance: " + (path.size()-1) + "; the chain is:");
        int temp1,temp2;
        String movieTemp;
        while(path.size()>1) {
        	temp1 = path.pop();
        	temp2 = path.peek();
        	movieTemp = getMovie(temp1,temp2);
            System.out.println(act.get(temp1).name + " appeared with " + act.get(temp2).name + " in " + movieTemp);
        }
        System.exit(0);
    }   
}