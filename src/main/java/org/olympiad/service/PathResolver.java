package org.olympiad.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olympiad.model.Answer;
import org.olympiad.model.Edge;
import org.olympiad.model.Map;
import org.olympiad.model.Vertex;

import java.util.*;

public class PathResolver {
    private static final Logger logger = LogManager.getLogger(PathResolver.class);

    private int[][] graph;
    private PriorityQueue<Mine> mines = new PriorityQueue<>(Collections.reverseOrder());
    private Map map;

    public Answer findAnswer(Map map) {
        this.map = map;
        fillInfo();
        List<Integer> path = findPath();

        Answer answer = new Answer(path);
        logger.info("Response: {}", answer);
        return answer;
    }
    private ArrayList<Integer> findPath(){
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Integer> temp;
        ArrayList<Integer> minPath = null;
        int source = 0;
        int needed = map.goal().resources();
        int size = map.robot().size();
        int space = size;
        do {
            if(needed <= 0){
                if (source != 0){
                    path.remove(path.size() - 1);
                    path.addAll(Dejkstra(source, 0));
                }
                break;
            }
            if(space == 0){
                if(path.size() > 0) path.remove(path.size() - 1);
                path.addAll(Dejkstra(source, 0));
                space = size;
                source = 0;
            }
            else {
                Mine m = mines.peek();
                minPath = Dejkstra(source, m.vertex);
                if (minPath.contains(0) && source != 0) {
                    if (size >= m.resources) {
                        needed = needed - m.resources;
                        space = size - m.resources;
                        mines.remove();
                    } else {
                        needed = needed - size;
                        space = 0;
                        mines.remove();
                        mines.add(new Mine(m.vertex, m.resources - size));
                    }
                } else {
                    if (space >= m.resources) {
                        space = space - m.resources;
                        needed = needed - m.resources;
                        mines.remove();
                    } else {
                        needed = needed - space;
                        space = 0;
                        mines.remove();
                        mines.add(new Mine(m.vertex, m.resources - space));
                    }
                }
                source = m.vertex;
                if (path.size() > 0) path.remove(path.size() - 1);
                path.addAll(minPath);
            }
        } while(true);

        return path;
    }


    private void fillInfo(){
        for(Vertex vertex : map.vertex()){
            if(vertex.resources() > 0){
                mines.add(new Mine(vertex.id(), vertex.resources()));
            }
        }
        graph = new int[map.vertex().size()][map.vertex().size()];
        for(int i = 0; i < map.vertex().size(); i++){
            for(int j = 0; j < map.vertex().size(); j++) {
                graph[i][j] = -1;
            }
        }
        for(Edge edge : map.edge()) {
            graph[edge.start()][edge.stop()] = edge.size();
            graph[edge.stop()][edge.start()] = edge.size();
        }

    }

    private ArrayList<Integer> Dejkstra(int source, int target){
        int numVertex = graph[0].length;
        int[] dist = new int[numVertex];
        HashSet<Integer> visited = new HashSet<>();
        ArrayList<ArrayList<Integer>> path = new ArrayList<>();
        for (int v = 0; v < numVertex; v++) {
            dist[v] = Integer.MAX_VALUE;
            path.add(new ArrayList<>());
        }
        dist[source] = 0;
        path.get(source).add(source);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(dist[source], source));

        while(visited.size() < numVertex) {

            if (queue.isEmpty()){
                break;
            }
            int v = queue.remove().node;
            if(visited.contains(v)){
                continue;
            }
            visited.add(v);

            for (int i = 0; i < numVertex; i++) {
                if (graph[v][i] != -1 && !visited.contains(i)) {
                    if (dist[v] + graph[v][i] < dist[i]) {
                        dist[i] = dist[v] + graph[v][i];
                        ArrayList<Integer> newPath = new ArrayList<>(path.get(v));
                        newPath.add(i);
                        path.set(i, newPath);
                        queue.add(new Node(dist[i], i));
                    }
                }
            }

        }

        return path.get(target);
    }


}
