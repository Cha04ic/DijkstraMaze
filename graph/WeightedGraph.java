package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * <P>This class represents a general "directed graph", which could 
 * be used for any purpose.  The graph is viewed as a collection 
 * of vertices, which are sometimes connected by weighted, directed
 * edges.</P> 
 * 
 * <P>This graph will never store duplicate vertices.</P>
 * 
 * <P>The weights will always be non-negative integers.</P>
 * 
 * <P>The WeightedGraph will be capable of performing three algorithms:
 * Depth-First-Search, Breadth-First-Search, and Djikatra's.</P>
 * 
 * <P>The Weighted Graph will maintain a collection of 
 * "GraphAlgorithmObservers", which will be notified during the
 * performance of the graph algorithms to update the observers
 * on how the algorithms are progressing.</P>
 */
public class WeightedGraph<V> {

    private Map<V, Map<V, Integer>> graph;
    /* Collection of observers.  Be sure to initialize this list
     * in the constructor.  The method "addObserver" will be
     * called to populate this collection.  Your graph algorithms 
     * (DFS, BFS, and Dijkstra) will notify these observers to let 
     * them know how the algorithms are progressing. 
     */
    private Collection<GraphAlgorithmObserver<V>> observerList;
    
    /** Initialize the data structures to "empty", including
     * the collection of GraphAlgorithmObservers (observerList).
     */
    public WeightedGraph() {
        observerList = new ArrayList<>();
        graph = new HashMap<>();
    }

    /** Add a GraphAlgorithmObserver to the collection maintained
     * by this graph (observerList).
     * 
     * @param observer
     */
    public void addObserver(GraphAlgorithmObserver<V> observer) {
        observerList.add(observer);
    }

    /** Add a vertex to the graph.  If the vertex is already in the
     * graph, throw an IllegalArgumentException.
     * 
     * @param vertex vertex to be added to the graph
     * @throws IllegalArgumentException if the vertex is already in
     * the graph
     */
    public void addVertex(V vertex) {
        if(graph.containsKey(vertex)) {
            throw new IllegalArgumentException("No Duplicates");
        } else {
            graph.put(vertex, new HashMap<>());
        }
    }

    /** Searches for a given vertex.
     * 
     * @param vertex the vertex we are looking for
     * @return true if the vertex is in the graph, false otherwise.
     */
    public boolean containsVertex(V vertex) {
        return graph.containsKey(vertex);
    }

    /** 
     * <P>Add an edge from one vertex of the graph to another, with
     * the weight specified.</P>
     * 
     * <P>The two vertices must already be present in the graph.</P>
     * 
     * <P>This method throws an IllegalArgumentExeption in three
     * cases:</P>
     * <P>1. The "from" vertex is not already in the graph.</P>
     * <P>2. The "to" vertex is not already in the graph.</P>
     * <P>3. The weight is less than 0.</P>
     * 
     * @param from the vertex the edge leads from
     * @param to the vertex the edge leads to
     * @param weight the (non-negative) weight of this edge
     * @throws IllegalArgumentException when either vertex
     * is not in the graph, or the weight is negative.
     */
    public void addEdge(V from, V to, Integer weight) {
        if (!containsVertex(from)) {
            throw new IllegalArgumentException("Vertex " + from + " not found in the graph.");
        } else if (!containsVertex(to)) {
            throw new IllegalArgumentException("Vertex " + to + " not found in the graph.");
        } else if (weight < 0) {
            throw new IllegalArgumentException("Negative weight not allowed: " + weight);
        } else {
            graph.get(from).put(to, weight);
        }
    }

    /** 
     * <P>Returns weight of the edge connecting one vertex
     * to another.  Returns null if the edge does not
     * exist.</P>
     * 
     * <P>Throws an IllegalArgumentException if either
     * of the vertices specified are not in the graph.</P>
     * 
     * @param from vertex where edge begins
     * @param to vertex where edge terminates
     * @return weight of the edge, or null if there is
     * no edge connecting these vertices
     * @throws IllegalArgumentException if either of
     * the vertices specified are not in the graph.
     */
    public Integer getWeight(V from, V to) {
        if(!(containsVertex(from) && containsVertex(to))) {
            throw new IllegalArgumentException("Vertices are not in graph");
        } else {
            Map<V, Integer> neighbor = graph.get(from);
            if(neighbor != null && neighbor.containsKey(to)) {
                return neighbor.get(to);//make sure key exists
            } else {
                return null;
            }
        }
    }

    /** 
     * <P>This method will perform a Breadth-First-Search on the graph.
     * The search will begin at the "start" vertex and conclude once
     * the "end" vertex has been reached.</P>
     * 
     * <P>Before the search begins, this method will go through the
     * collection of Observers, calling notifyBFSHasBegun on each
     * one.</P>
     * 
     * <P>Just after a particular vertex is visited, this method will
     * go through the collection of observers calling notifyVisit
     * on each one (passing in the vertex being visited as the
     * argument.)</P>
     * 
     * <P>After the "end" vertex has been visited, this method will
     * go through the collection of observers calling 
     * notifySearchIsOver on each one, after which the method 
     * should terminate immediately, without processing further 
     * vertices.</P> 
     * 
     * @param start vertex where search begins
     * @param end the algorithm terminates just after this vertex
     * is visited
     */
    public void DoBFS(V start, V end) {
        for(GraphAlgorithmObserver<V> observer : observerList) {
            observer.notifyBFSHasBegun();//search has begun
        }
        Set<V>visited = new HashSet<V>();//make a set for visited vertices
        Queue<V>queue = new LinkedList<>();//queue for BFS
        queue.add(start);
        while(!(queue.isEmpty())) {
            V current = queue.poll();//remove head from queue and store it in current
            if(!(visited.contains(current))) {
                for(GraphAlgorithmObserver<V> observer : observerList) {
                    observer.notifyVisit(current);//visit current
                }
                visited.add(current);//add current to visited 
                for(V adjacency : graph.get(current).keySet()) {
                    if(!(visited.contains(adjacency))) {
                        queue.add(adjacency);//add adjacency to queue if not in visited
                    }
                }
                if (current.equals(end)) {//end case
                    for (GraphAlgorithmObserver<V> observer : observerList) {
                        observer.notifySearchIsOver();//search is over
                    } 
                    break;//break out of loop
                }
            }
        }
    }

    /** 
     * <P>This method will perform a Depth-First-Search on the graph.
     * The search will begin at the "start" vertex and conclude once
     * the "end" vertex has been reached.</P>
     * 
     * <P>Before the search begins, this method will go through the
     * collection of Observers, calling notifyDFSHasBegun on each
     * one.</P>
     * 
     * <P>Just after a particular vertex is visited, this method will
     * go through the collection of observers calling notifyVisit
     * on each one (passing in the vertex being visited as the
     * argument.)</P>
     * 
     * <P>After the "end" vertex has been visited, this method will
     * go through the collection of observers calling 
     * notifySearchIsOver on each one, after which the method 
     * should terminate immediately, without visiting further 
     * vertices.</P> 
     * 
     * @param start vertex where search begins
     * @param end the algorithm terminates just after this vertex
     * is visited
     */
    public void DoDFS(V start, V end) {
        for (GraphAlgorithmObserver<V> observer : observerList) {
            observer.notifyDFSHasBegun();//search has begun
        }
        Set<V> visited = new HashSet<>();//make a set for
        if (DFSHelper(start, end, visited)) {//recursive helper returns boolean
            for (GraphAlgorithmObserver<V> observer : observerList) {
                observer.notifySearchIsOver();//if statement is true, search is over
            }
        }
    }
    /*
     * Recursive helper method for DoDFS that takes in a start and end vertex
     * as well as a set that represents visited vertices
     * Returns a boolean
     */
    private boolean DFSHelper(V start, V end, Set<V> visited) {
        for (GraphAlgorithmObserver<V> observer : observerList) {
            observer.notifyVisit(start);//visit vertex
        }
        if (start.equals(end)) {//end condition
            System.out.println("Found end: " + start);
            return true;  // Indicate that the search is over
        }

        visited.add(start);//add start to visited set

        for (V adjacency : graph.get(start).keySet()) {
            if (!visited.contains(adjacency) && DFSHelper(adjacency, end, visited)) {
                return true; // search is over
            }
        }

        return false;  // Indicate that the search is not over yet
    }

    /** 
     * <P>Perform Dijkstra's algorithm, beginning at the "start"
     * vertex.</P>
     * 
     * <P>The algorithm DOES NOT terminate when the "end" vertex
     * is reached.  It will continue until EVERY vertex in the
     * graph has been added to the finished set.</P>
     * 
     * <P>Before the algorithm begins, this method goes through 
     * the collection of Observers, calling notifyDijkstraHasBegun 
     * on each Observer.</P>
     * 
     * <P>Each time a vertex is added to the "finished set", this 
     * method goes through the collection of Observers, calling 
     * notifyDijkstraVertexFinished on each one (passing the vertex
     * that was just added to the finished set as the first argument,
     * and the optimal "cost" of the path leading to that vertex as
     * the second argument.)</P>
     * 
     * <P>After all of the vertices have been added to the finished
     * set, the algorithm will calculate the "least cost" path
     * of vertices leading from the starting vertex to the ending
     * vertex.  Next, it will go through the collection 
     * of observers, calling notifyDijkstraIsOver on each one, 
     * passing in as the argument the "lowest cost" sequence of 
     * vertices that leads from start to end (I.e. the first vertex
     * in the list will be the "start" vertex, and the last vertex
     * in the list will be the "end" vertex.)</P>
     * 
     * @param start vertex where algorithm will start
     * @param end special vertex used as the end of the path 
     * reported to observers via the notifyDijkstraIsOver method.
     */
    public void DoDijsktra(V start, V end) {
        for (GraphAlgorithmObserver<V> observer : observerList) {
            observer.notifyDijkstraHasBegun();//traversal has begun
        }

        Set<V> finished = new HashSet<>();//set to contain finished vertices
        Map<V, V> pred = new HashMap<>();//map for predecessors
        Map<V, Integer> costs = new HashMap<>();//map for costs
        costs.put(start, 0);
        // Initialize costs and pred with INFINITY and null for all vertices
        for (V vertex : graph.keySet()) {
            if(!(vertex.equals(start))) {
                costs.put(vertex, Integer.MAX_VALUE);
            }
            pred.put(vertex, null);
        }
        while (finished.size() != graph.size()) {
            V smallestVertex = null;//temp variable
            int smallestCost = Integer.MAX_VALUE;//temp variable
            for (V vertex : costs.keySet()) {//loop through every key in costs
                int currentCost = costs.get(vertex);//initialize currentCost
                if (!(finished.contains(vertex)) && currentCost < smallestCost) {
                    smallestVertex = vertex;
                    smallestCost = currentCost;
                }//find smallestVertex and cost
            }

            finished.add(smallestVertex);//add smallestVertex to finished set

            for (GraphAlgorithmObserver<V> observer : observerList) {
                observer.notifyDijkstraVertexFinished(smallestVertex, smallestCost);
                //notify observer
            }
            for (V neighbor : graph.get(smallestVertex).keySet()) {
                if (!(finished.contains(neighbor))) {
                    int neighborCost = costs.get(neighbor);
                    int currentCost = costs.get(smallestVertex);
                    int weight = graph.get(smallestVertex).get(neighbor);
                    if (currentCost + weight < neighborCost) {
                        costs.put(neighbor, currentCost + weight);
                        pred.put(neighbor, smallestVertex);
                    }
                }
            }
        }//to find least cost path
        List<V> leastCostPath = new ArrayList<>();//list with store the path
        V current = end;//temporary

        // Reconstruct the path
        while (current != null) {
            leastCostPath.add(current);//add predecessors to list
            current = pred.get(current);//will go through all predecessors of current
        }

        Collections.reverse(leastCostPath);//reverse the list since we added from the end

        for (GraphAlgorithmObserver<V> observer : observerList) {
            observer.notifyDijkstraIsOver(leastCostPath);//algorithm is over
        }
    }
}
