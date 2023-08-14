package org.olympiad.service;

public class Mine implements Comparable<Mine> {
    public Integer resources;
    public Integer vertex;

    public Mine(Integer vertex, Integer resources){
        this.vertex = vertex;
        this.resources = resources;
    }
    @Override
    public int compareTo(Mine n) {
        return this.resources.compareTo(n.resources);
    }
}
