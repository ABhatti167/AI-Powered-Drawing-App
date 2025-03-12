package ca.utoronto.utm.paint;

import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy{

    private PolylineCommand polylineCommand;

    PolylineManipulatorStrategy(PaintModel paintModel) {
        super(paintModel);
    }

    @Override
    public void mouserightClick(MouseEvent e) {
        if (this.polylineCommand != null) {
            this.polylineCommand.add(new Point((int) e.getX(), (int) e.getY()));
        }

        System.out.println("right click");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point start = new Point((int) e.getX(), (int) e.getY());
        System.out.println("left click");
        this.polylineCommand = new PolylineCommand();
        this.polylineCommand.add(start);
        this.addCommand(this.polylineCommand);
    }

}
