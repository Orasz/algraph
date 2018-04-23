	package algraph;
	
	import graph.Graph;
	import graph.Node;
	import javafx.beans.property.SimpleObjectProperty;
	import javafx.beans.value.ObservableValue;
	import javafx.collections.FXCollections;
	import javafx.collections.ObservableList;
	import javafx.event.ActionEvent;
	import javafx.event.EventHandler;
	import javafx.fxml.FXML;
	import javafx.scene.control.Button;
	import javafx.scene.control.ChoiceBox;
	import javafx.scene.control.Label;
	import javafx.scene.control.ProgressBar;
	import javafx.scene.control.Slider;
	import javafx.scene.control.SplitPane;
	import javafx.scene.control.TableColumn;
	import javafx.scene.control.TableColumn.CellDataFeatures;
	import javafx.scene.control.TableView;
	import javafx.scene.control.TreeTableView;
	import javafx.scene.control.cell.PropertyValueFactory;
	import javafx.scene.input.MouseEvent;
	import javafx.scene.layout.AnchorPane;
	import javafx.scene.layout.Pane;
	import javafx.stage.FileChooser;
	import javafx.util.Callback;
	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.PrintWriter;
	import java.util.ArrayList;
	import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
	import java.util.Map.Entry;
	import java.util.Random;
	import java.util.stream.Collectors;
	import java.util.stream.IntStream;
	import javafx.scene.shape.Circle;

	public class Controller {
	
		    @FXML private Pane view_pane;
		    @FXML private SplitPane split_pane;
		    @FXML private AnchorPane left_pane;
		    @FXML private Label status;
		    @FXML private Button auto_gen;
		    @FXML private Button file_gen;
		    @FXML private Button save_to_file;
		    @FXML private ChoiceBox<Integer> choice_box_nodes;
		    @FXML private ChoiceBox<String> choice_box_startbellman;
		    @FXML private ProgressBar progress_gen;
		    @FXML private Button full_bellman_ford;
		    @FXML private Button step_bellman_ford; 
		    @FXML private TableView<Entry<String,Integer>>  cost_table;
		    @FXML private TableColumn<Entry<String, Integer>, String> node_column;
		    @FXML private TableColumn<Entry<String, Integer>, Integer> cost_column;
	
		    
		    protected Integer step;
		    protected Graph<String> graph;
		    Random random;
		    
		    @FXML
		    public void initialize() {
	        	random =  new Random((long)1917);
	        	split_pane.setDividerPositions(0.245);
	            left_pane.maxWidthProperty().bind(split_pane.widthProperty().multiply(0.24));
		        choice_box_nodes.setItems(FXCollections.observableList(IntStream.range(2, 15).boxed().collect(Collectors.toList())));
		        choice_box_nodes.setValue(3);
	            
	            node_column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry<String, Integer>, String>, ObservableValue<String>>() {
		            @Override
		            public ObservableValue<String> call(TableColumn.CellDataFeatures<Entry<String, Integer>, String> p) {
		                return new SimpleObjectProperty<String>(p.getValue().getKey());
		            }
	
		        });
	
		        cost_column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Entry<String, Integer>, Integer>, ObservableValue<Integer>>() {
		            @Override
		            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Entry<String, Integer>, Integer> p) {
		                return new SimpleObjectProperty<Integer>(p.getValue().getValue());
		            }
		        });
	
	
		            
		        choice_box_startbellman.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		 
		            @Override
		            public void handle(MouseEvent t) {
		            	cost_table.getItems().removeAll(cost_table.getItems());
		            	step = -1;
		            }
		        });
		            
		    	
		    	auto_gen.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		 
		            @Override
		            public void handle(MouseEvent t) {
		            	step = -1;
		            	graph = new Graph<String>();
		    			for(int i = 0; i < choice_box_nodes.getValue(); i++)
		    				graph.insertNode(new Node<String>(String.valueOf(i)));
		    				
		    			for(Node<String> start_node : graph.V()) 
		    				for(Node<String> end_node : graph.V()) 
			    				if(random.nextInt(100) < 25 && !start_node.equals(end_node))
			    					graph.insertEdge(start_node, end_node, random.nextInt(10) - 2);	
		    			initializeGraph();
		            }
		         });
		    	
		    	file_gen.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		
		    		@Override
		            public void handle(MouseEvent t) {  
		    			HashMap<String, HashMap<String, Integer>> hash = new HashMap<String, HashMap<String, Integer>>();
		    			step = -1;
			            graph = new Graph<String>();
		            	FileChooser fileChooser = new FileChooser();
		    			fileChooser.setTitle("Open Resource File");
		    			File selected_file = fileChooser.showOpenDialog(view_pane.getScene().getWindow());	
		    			try {
							BufferedReader reader = new BufferedReader(new FileReader(selected_file.getAbsolutePath()));
							String line = reader.readLine();
							while(line!=null) {
								System.out.println(line.split("->").length);

								if(!line.contains("->") || line.split("->").length > 2 || line.split("->").length == 0) {
									System.out.println(line.split("->").length);
									throw new Exception("Wrong format");
								
								}
								hash.put(line.split("->")[0], new HashMap<String, Integer>());
								if(line.split("->").length > 1)
									for (String s : line.split("->")[1].split(";")) {
										hash.get(line.split("->")[0]).put(s.split(",")[0], Integer.decode(s.split(",")[1]));
									}
								line = reader.readLine();
							}
							for(Entry<String, HashMap<String, Integer>> node : hash.entrySet()) {
								graph.insertNode(new Node<String>(node.getKey()));
							}
							for(Node<String> start_node : graph.V()) {
								for(Node<String> end_node : graph.V()) {
									if (hash.get(start_node.toString()).containsKey(end_node.toString()))
										graph.insertEdge(start_node, end_node, hash.get(start_node.toString()).get(end_node.toString()));
								}
							}
							initializeGraph();
							status.setText("Okay loading file");
						}
		    			 catch (Exception e) {
							status.setText("Error loading file");
						}
		    		
		    			
		            }
		         });
		    	
		    	save_to_file.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		
		    		@Override
		            public void handle(MouseEvent t) { 
		    			if(graph==null || graph.V().size() < 1) {
		    				status.setText("Can't save because no nodes");
		    				return;
		    			}
		    			else
		    				status.setText("Okay");
		    			String line;
		            	FileChooser fileChooser = new FileChooser();
		    			fileChooser.setTitle("Save Graph File");
		    			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		                fileChooser.getExtensionFilters().add(extFilter);
		                
		                //Show save file dialog
		              	File selected_file = fileChooser.showSaveDialog(view_pane.getScene().getWindow());	
              			System.out.println(selected_file);

		              	if (selected_file == null) {
		    				return;
		    			}
		              	try {
		              		PrintWriter writer = new PrintWriter(selected_file, "UTF-8");
		              		for (Node<String> start_node : graph.V()) {
		              			line = start_node + "->";
		              			for (Entry<Node<String>, Integer> edge : graph.adj_edges(start_node)) {
		              				line += edge.getKey() + "," + edge.getValue() + ";";
		              			}
		              			writer.println(line);
		              			System.out.println(line);
		              		}
			              	writer.close();

							
						}
		    			 catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		            }
		         });
		    	
		    	
		    	
		    	full_bellman_ford.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		@Override
		            public void handle(MouseEvent t) {
		    			if(graph==null || graph.V().size() < 1) {
		    				status.setText("Can't do bellmanford because no nodes");
		    				return;
		    			}
		    			else
		    				status.setText("Okay");
		    			HashMap<String, Integer> map = graph.doBellmanFord(choice_box_startbellman.getValue()).get(graph.doBellmanFord("0").size() - 2);
		    	        ObservableList<Entry<String, Integer>> items = FXCollections.observableArrayList(map.entrySet());
		    	        cost_table.setItems(items);
	
		    		}
		    	});
		    	
		    	step_bellman_ford.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    		@Override
		            public void handle(MouseEvent t) {
		    			if(graph==null || graph.V().size() < 1) {
		    				status.setText("Can't do bellmanford because no nodes");
		    				return;
		    			}
		    			else
		    				status.setText("Okay");
		    			
		    			HashMap<String, Integer> old_items = new HashMap<String, Integer> ();
		    			ArrayList<HashMap<String, Integer>> array = graph.doBellmanFord(choice_box_startbellman.getValue().toString());
		    			if(step < array.size() - 1) {
		    				step ++;
		    				if(step > 0)
		    					old_items = array.get(step - 1);
		    			}
		    			
		    			HashMap<String, Integer> map = array.get(step);
		    			ObservableList<Entry<String, Integer>> items = FXCollections.observableArrayList(map.entrySet());
		    			if(! old_items.entrySet().equals(map.entrySet()) && map.entrySet().size() > 0) {
		    				cost_table.getItems().removeAll(cost_table.getItems());
		    				cost_table.setItems(items);
		    	        }
		    			
		    			for(javafx.scene.Node child : view_pane.getChildren()) {
		    				if (child.getClass() == Circle.class) {
		    					Circle circle = (Circle) child;
    							circle.setFill(javafx.scene.paint.Color.RED);
		    				}
		    			}
		    			
		    			for(javafx.scene.Node child : view_pane.getChildren()) {
		    				if (child.getClass() == Circle.class) {
		    					if(step >= 1 && map.get(child.getId()) < array.get(step - 1).get(child.getId())){
		    						Circle circle = (Circle) child;
	    							circle.setFill(javafx.scene.paint.Color.BLUE);
		    					}
		    				}
		    			}
		    			
		    		}
		    	});
		    		
		    	
		    }
		    
		    public void initializeGraph() {
    			view_pane.getChildren().removeAll(view_pane.getChildren());
    			Pane tmp = graph.getFxGraph((int) view_pane.getWidth(), (int) view_pane.getHeight(), 20, 20);
    			view_pane.getChildren().addAll(tmp.getChildren());
    			ArrayList <String>nodes_list = new ArrayList<String>();
    			for(Node<String> node_value : graph.V())
    				nodes_list.add(node_value.toString());
		        choice_box_startbellman.setItems(FXCollections.observableList(nodes_list));
		        choice_box_startbellman.setValue(nodes_list.get(0).toString());
		        cost_table.getItems().removeAll(cost_table.getItems());


		    }
	}
	;