package ca.utoronto.utm.paint;

import java.io.*;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class View implements EventHandler<ActionEvent> {

	private PaintModel paintModel;
	private PaintPanel paintPanel;
	private ShapeChooserPanel shapeChooserPanel;
	private Stage stage;

	public View(PaintModel model, Stage stage) {
		this.stage = stage;
		this.paintModel = model;
		initUI(stage);
	}

	public PaintModel getPaintModel() {
		return this.paintModel;
	}

	public void setPaintModel(PaintModel paintModel) {
		this.paintModel=paintModel;
		this.paintPanel.setPaintModel(paintModel);
	}
	private void initUI(Stage stage) {

		this.paintPanel = new PaintPanel(this.paintModel);
		this.shapeChooserPanel = new ShapeChooserPanel(this);

		BorderPane root = new BorderPane();
		root.setTop(createMenuBar());
		root.setCenter(this.paintPanel);
		root.setLeft(this.shapeChooserPanel);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Paint");
		stage.show();
	}

	public PaintPanel getPaintPanel() {
		return paintPanel;
	}

	public ShapeChooserPanel getShapeChooserPanel() {
		return shapeChooserPanel;
	}

	private MenuBar createMenuBar() {

		MenuBar menuBar = new MenuBar();
		Menu menu;
		MenuItem menuItem;

		// A menu for File

		menu = new Menu("File");

		menuItem = new MenuItem("New");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Open");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Save");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Exit");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		// Another menu for Edit

		menu = new Menu("Edit");

		menuItem = new MenuItem("Cut");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Copy");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Paste");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menu.getItems().add(new SeparatorMenuItem());

		menuItem = new MenuItem("Undo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuItem = new MenuItem("Redo");
		menuItem.setOnAction(this);
		menu.getItems().add(menuItem);

		menuBar.getMenus().add(menu);

		return menuBar;
	}

	public void setPaintPanelShapeManipulatorStrategy(ShapeManipulatorStrategy strategy) {
		this.paintPanel.setShapeManipulatorStrategy(strategy);
	}

	@Override
	public void handle(ActionEvent event) {
		System.out.println(((MenuItem) event.getSource()).getText());
		String command = ((MenuItem) event.getSource()).getText();
		if (command.equals("Open")) {
			FileChooser fc = new FileChooser();
			File file = fc.showOpenDialog(this.stage);

			if (file != null) {
				System.out.println("Opening: " + file.getName() + "." + "\n");

                BufferedReader bufferedReader = null; // BufferedReader bufferedReader=null; // FIX THIS

                try {
                    bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                PaintModel paintModel = new PaintModel();
				PaintFileParser parser = new PaintFileParser(); // PaintFileParser parser = new PaintFileParser();

				parser.parse(bufferedReader,  paintModel); // parser.parse(bufferedReader,  paintModel);
				System.out.println(parser.getErrorMessage());
				System.out.println(paintModel.getCommands().size());

				this.setPaintModel(paintModel);
			} else {
				System.out.println("Open command cancelled by user." + "\n");
			}
		} else if (command.equals("Save")) {
			FileChooser fc = new FileChooser();

			fc.getExtensionFilters().add(
					new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt")
			);

			File file = fc.showSaveDialog(this.stage);

			if (file != null) {
				// This is where a real application would open the file.
				System.out.println("Saving: " + file.getName() + "." + "\n");
				// Add something like the following...

                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(file);

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                View.save(writer, this.paintModel);
			} else {
				System.out.println("Save command cancelled by user." + "\n");
			}
		} else if (command.equals("New")) {
			// this.paintModel.reset();
			this.setPaintModel(new PaintModel());
		} else if (command.equals("Exit")) {
			Platform.exit();
		} 
	}

	/**
	 * Save the given paintModel to the open file
	 * @param writer
	 * @param paintModel
	 */
	public static void save(PrintWriter writer, PaintModel paintModel) {
		writer.println("Paint Save File Version 1.0");

		ArrayList<PaintCommand> contents = paintModel.getCommands();

		for (PaintCommand command : contents) {

			if (command instanceof CircleCommand) {
				writer.println("Circle");
				writer.println(command);
				writer.println("center:("+((CircleCommand) command).getCentre().x+","+((CircleCommand) command).getCentre().y+")");
				writer.println("radius:"+((CircleCommand) command).getRadius());
				writer.println("End Circle");

			} else if (command instanceof SquiggleCommand) {

				writer.println("Squiggle");
				writer.println(command);
				writer.println("points");

				for (Point p : ((SquiggleCommand) command).getPoints()) {
					writer.println("\tpoint:(" + p.x + "," + p.y + ")");
				}
				writer.println("end points");
				writer.println("End Squiggle");

			} else if (command instanceof PolylineCommand) {

				writer.println("Polyline");
				writer.println(command);

				writer.println("points");

				for (Point p : ((PolylineCommand) command).getPoints()) {
					writer.println("point:(" + p.x + "," + p.y + ")");
				}
				writer.println("end points");

				writer.println("End Polyline");

			} else if (command instanceof RectangleCommand) {

				writer.println("Rectangle");
				writer.println(command);
				writer.println("\tp1:("+((RectangleCommand) command).getP1().x+","+((RectangleCommand) command).getP1().y+")");
				writer.println("\tp2:("+((RectangleCommand) command).getP2().x+","+((RectangleCommand) command).getP2().y+")");
				writer.println("End Rectangle");
			}
		}

		writer.println("End Paint Save File");
		writer.close();
	}
}
