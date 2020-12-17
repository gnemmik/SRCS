package srcs.workflow.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphImpl<T> implements Graph<T>{
	private List<T> nodes = new ArrayList<>();
	private Map<T,List<T>> edges = new HashMap<>();

	@Override
	public Iterator<T> iterator() {
		return nodes.iterator();
	}

	@Override
	public void addNode(T n) throws IllegalArgumentException {
		if(nodes.contains(n)) throw new IllegalArgumentException();
		else nodes.add(n);
	}

	@Override
	public void addEdge(T from, T to) throws IllegalArgumentException {
		if(!nodes.contains(from) || !nodes.contains(to)) throw new IllegalArgumentException();
		if(existEdge(from, to)) throw new IllegalArgumentException();
		
		if(edges.containsKey(from)) {
			List<T> values = edges.get(from);
			values.add(to);
			edges.put(from, values);
		}else {
			List<T> values = new ArrayList<>();
			values.add(to);
			edges.put(from, values);
		}
	}

	@Override
	public boolean existEdge(T from, T to) {
		if(edges.containsKey(from) && edges.get(from).contains(to)) return true;
		return false;
	}

	@Override
	public boolean existNode(T n) {
		return nodes.contains(n);
	}

	@Override
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public List<T> getNeighborsOut(T from) throws IllegalArgumentException {
		if(nodes.contains(from)) {
			if(edges.containsKey(from)) return edges.get(from);
			else return new ArrayList<>();
		}
		else throw new IllegalArgumentException();
	}

	@Override
	public List<T> getNeighborsIn(T to) throws IllegalArgumentException {
		if(nodes.contains(to)) {
			List<T> in = new ArrayList<>();
			for(Map.Entry<T, List<T>> entry : edges.entrySet()) {
				if(entry.getValue().contains(to)) {
					in.add(entry.getKey());
					continue;
				}
			}
			return in;
		}else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Set<T> accessible(T from) throws IllegalArgumentException {
		Set<T> visited = new HashSet<>();
		return accessible(visited, from);
	}
	
	private Set<T> accessible(Set<T> visited, T from){
		Set<T> access = new HashSet<>();
		if(visited.contains(from)) return access;
		else {
			visited.add(from);
			for(T node : getNeighborsOut(from)) {
				access.add(node);
				access.addAll(accessible(visited, node));
			}
			return access;
		}
	}

	@Override
	public boolean isDAG() {
		for(T node : nodes) 
			if(accessible(node).contains(node)) return false;
		return true;
	}

}
