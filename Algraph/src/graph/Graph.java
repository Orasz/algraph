package graph;

import java.util.ArrayList;
import java.util.HashMap;
//perch� abbiamo usato un treeset e non un hashset? domanda pi� probabile del sorgere del sole
//import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Graph<T extends Comparable<T>> implements IGraph<T> {

	HashMap<Node<T>, HashMap<Node<T>, Integer>> vertexes;
	
	public Graph(){
		this.vertexes = new HashMap<Node<T>, HashMap<Node<T>, Integer>>();
	}
	
	@Override
	public void insertNode(Node<T> u) {
		if (!this.vertexes.containsKey(u)){
			HashMap<Node<T>, Integer> eHashMap = new HashMap<Node<T>, Integer>();
			this.vertexes.put(u, eHashMap);
		}
			
	}

	@Override
	public void deleteNode(Node<T> u) {
		if (this.vertexes.containsKey(u)){
			this.vertexes.remove(u);
		}
		for (Entry<Node<T>, HashMap<Node<T>, Integer>> e : this.vertexes.entrySet()) {
			if (e.getValue().containsKey(u)){
				e.getValue().remove(u);
			}
		}
	}

	
	
	@Override
	public void insertEdge(Node<T> u, Node<T> v, Integer cost) {
		
		if (this.vertexes.containsKey(u) && this.vertexes.containsKey(v))
			this.vertexes.get(u).put(v, cost);
	
	}

	
	@Override
	public void deleteEdge(Node<T> u, Node<T> v) {
		
		if (this.vertexes.containsKey(u) && this.vertexes.containsKey(v))
			this.vertexes.get(u).remove(v);
	}


	
	@Override
	public Set<Node<T>> adj(Node<T> u) {
		
		Set<Node<T>> adjSet = new TreeSet<Node<T>>();
		
		if (this.vertexes.containsKey(u)) {
			adjSet = this.vertexes.get(u).keySet();
		}
		
		return adjSet;
	}
	
	public Set<Entry<Node<T>, Integer>> adj_edges(Node<T> u) {
		
		Set<Entry<Node<T>, Integer>> adjSet = new TreeSet<Entry<Node<T>, Integer>>();
		
		if (this.vertexes.containsKey(u)) {
			adjSet = this.vertexes.get(u).entrySet();
		}
		return adjSet;
	}

	
	@Override
	public Set<Node<T>> V() {
		Set<Node<T>> set = this.vertexes.keySet();
		return set;
	}

	
	
	
	@Override
	// NOTA: metodo utility per verificare il contenuto delle liste di adiacenza
	public void print() {
		
		for (Entry<Node<T>, HashMap<Node<T>, Integer>> e : this.vertexes.entrySet()) {
			
			System.out.print(e.getKey() + " : ");
			
			for (Node<T> l : e.getValue().keySet()) {
				System.out.print("(" + l + "," + e.getValue().get(l) + ")");
			}
			
			System.out.println();
			
		}
		
		
	}
	
		
	public ArrayList<HashMap<String, Integer>> doBellmanFord(String start){
		ArrayList<HashMap<String, Integer>> M = new ArrayList<HashMap<String, Integer>>();

		for(Node<T> node : this.V()) {
			M.add(new HashMap<String, Integer> ());
			M.get(0).put(node.toString(), Integer.MAX_VALUE - 100);
		}
		M.get(0).put(start, 0);
		
		for(int i = 1; i < this.V().size(); i ++) {
			System.out.println(i + "  " + M);
			for(Node<T> node : this.V()) 
				M.get(i).put(node.toString(), M.get(i - 1).get(node.toString()));
			
			for(Node<T> start_node : this.V()) {
				for(Entry<Node<T>, Integer> end_node : this.adj_edges(start_node)) 
					M.get(i).put(end_node.getKey().toString(), Math.min(M.get(i).get(end_node.getKey().toString()), end_node.getValue() + M.get(i).get(start_node.toString())));
			}
		}	
		return M;
	}
	
	
	public Pane getFxGraph(int size_x, int size_y, int offset, double radius) {
		Pane view = new Pane();
		HashMap<String, Circle> circles = new HashMap<String, Circle>();
		HashMap<Line, String> edges = new HashMap<Line, String>();
		
		// Compute coordinates
		int n_nodes = this.vertexes.size();
		float x_nodes = (float) n_nodes / 3;

		double x_coord = offset;
		double y_coord = offset + 20;
		int raw = 0, coloumn = 0;
		for (Entry<Node<T>, HashMap<Node<T>, Integer>> node : this.vertexes.entrySet()) {
			Circle circle = new Circle();
			Text text;
			circle.setFill(javafx.scene.paint.Color.RED);
			circle.setCenterX(x_coord);
			circle.setCenterY(y_coord);
			circle.setRadius(radius);
			circle.setId(node.getKey().toString());
			circles.put(node.getKey().toString(), circle);//associ nome nodo a oggetto FX
			text = new Text(x_coord, y_coord, node.getKey().toString());//crei oggetto text passando coordinante e nome nodo
			//debug System.out.println(x_coord);
			//offset
			if(coloumn % 2 == 0)
				y_coord += (int) radius * 3;
			else
				y_coord -= (int) radius * 3;
			
			x_coord += (size_x - offset * 2) / x_nodes;
			if(x_coord >= size_x) {
				y_coord += (size_y - offset * 2) / 3;
				x_coord = offset ;
				raw ++;
				if(raw % 2 != 0)
					x_coord += (int) radius * 3;
				
			}
			else
				coloumn ++;
			view.getChildren().add(circle);
			view.getChildren().add(text);
		}
		//insert edges
		for (Entry<Node<T>, HashMap<Node<T>, Integer>> start : this.vertexes.entrySet()) {
			for(Entry <Node<T>, Integer> end : start.getValue().entrySet()) {
				Integer cost;
				cost = end.getValue();
				//coordinate non perfette, andrebbe alzato un po' 
				// e se ci son due archi si sovrappongono lol, con calma
				double mx = (circles.get(start.getKey().toString()).getCenterX() + circles.get(end.getKey().toString()).getCenterX())/2;
				double my = (circles.get(start.getKey().toString()).getCenterY() + circles.get(end.getKey().toString()).getCenterY())/2;
				double m = Math.atan2(circles.get(end.getKey().toString()).getCenterY() - circles.get(start.getKey().toString()).getCenterY() , circles.get(end.getKey().toString()).getCenterX() - circles.get(start.getKey().toString()).getCenterX());
				Line tl = new Line(mx, my, mx - 10 * Math.cos(m + Math.toRadians(90)) , my - 10 * Math.sin(m + Math.toRadians(90)));
				Text costText = new Text(tl.getEndX(),tl.getEndY(),cost.toString());
				costText.setRotate(Math.toDegrees(m));
				//costText.getTransforms().add(new Rotate(m));
				view.getChildren().addAll(drawEdge(circles.get(start.getKey().toString()), circles.get(end.getKey().toString()), 9, 20, radius));
				view.getChildren().add(costText);
			}
		}
		return view;
	}

	public Line[] drawEdge(Circle u, Circle v, double arrow_angle, double arrow_length, double radius) {
		double dx, dy, m;
		Line edge, up, down;
		dx = v.getCenterX() - u.getCenterX();
		dy = v.getCenterY() - u.getCenterY();
		m = Math.atan2(dy, dx);
		edge = new Line();
			edge.setStartX(u.getCenterX() + radius * Math.cos(m));
			edge.setStartY(u.getCenterY() + radius * Math.sin(m));
			edge.setEndX(v.getCenterX() - radius * Math.cos(m));
			edge.setEndY(v.getCenterY() - radius * Math.sin(m));
			edge.setStrokeWidth(2);
		up = new Line();
			up.setStartX(edge.getEndX());
			up.setStartY(edge.getEndY());
			up.setEndX(edge.getEndX() - arrow_length * Math.cos(m + Math.toRadians(arrow_angle)));
			up.setEndY(edge.getEndY() - arrow_length * Math.sin(m + Math.toRadians(arrow_angle)));
			up.setStrokeWidth(3);
		down = new Line();
			down.setStartX(edge.getEndX());
			down.setStartY(edge.getEndY());
			down.setEndX(edge.getEndX() - arrow_length * Math.cos(m + Math.toRadians(-arrow_angle)));
			down.setEndY(edge.getEndY() - arrow_length * Math.sin(m + Math.toRadians(-arrow_angle)));
			down.setStrokeWidth(3);
		Line[] res = {edge, up, down};
		return res;	
	}
	
	
}