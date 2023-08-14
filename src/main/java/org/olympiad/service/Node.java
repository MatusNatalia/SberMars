package org.olympiad.service;

public class Node implements Comparable<Node> {
    public Integer dist;
    public Integer node;

    public Node(Integer dist, Integer node){
        this.dist = dist;
        this.node = node;
    }
    @Override
    public int compareTo(Node n) {
        return this.dist.compareTo(n.dist);
    }
}
