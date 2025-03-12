package ca.utoronto.utm.paint;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint
 * save file format, see the associated documentation.
 *
 * @author
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel;

	/**
	 * Below are Patterns used in parsing
	 */
	private Pattern pFileStart = Pattern.compile("^\\s*Paint\\s+Save\\s+File\\s+Version\\s+1\\.0\\s*$|^PaintSaveFileVersion1\\.0$");
	private Pattern pFileEnd = Pattern.compile("^\\s*End\\s+Paint\\s+Save\\s+File\\s*$|^EndPaintSaveFile$");


	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	// ADD MORE!!
	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd=Pattern.compile("^EndSquiggle$");

	private Pattern pPolylineStart=Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd=Pattern.compile("^EndPolyline$");

	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd=Pattern.compile("^EndRectangle$");

	String color = "\\s*color:\\d+,\\d+,\\d+";

	private Pattern pFilled = Pattern.compile("^\\s*filled:(true|false)$");
	private Pattern pColor= Pattern.compile(color);

	/**
	 * Store an appropriate error message in this, including
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}

	/**
	 *
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}

	/**
	 * Parse the specified file
	 * @param fileName
	 * @return
	 */
	public boolean parse(String fileName){
		boolean retVal = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			PaintModel pm = new PaintModel();
			retVal = this.parse(br, pm);
		} catch (FileNotFoundException e) {
			error("File Not Found: "+fileName);
		} finally {
			try { br.close(); } catch (Exception e){};
		}
		return retVal;
	}

	/**
	 * Parse the specified inputStream as a Paint Save File Format file.
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream){
		PaintModel pm = new PaintModel();
		return this.parse(inputStream, pm);
	}

	private static ArrayList<Integer> extractNumbers(String line) {
		ArrayList<Integer> numbers = new ArrayList<>();

		// Regex for coordinates in parentheses, with optional "point:", "center:", or other prefix (e.g., "p1:")
		Pattern parenthesesPattern = Pattern.compile("\\b(?:point:|center:|\\w+:)?\\s*\\((-?\\d+),\\s*(-?\\d+)\\)\\s*");

		Matcher parenthesesMatcher = parenthesesPattern.matcher(line);

		// Regex for "color:" pattern
		Pattern colorPattern = Pattern.compile("\\bcolor:\\s*(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d),\\s*(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d),\\s*(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\b");
		Matcher colorMatcher = colorPattern.matcher(line);

		// Regex for "radius:" pattern
		Pattern radiusPattern = Pattern.compile("^\\s*radius:(\\d+)\\s*$");
		Matcher radiusMatcher = radiusPattern.matcher(line);

		// Match coordinates in parentheses with optional "point:", "center:", or other prefix
		if (parenthesesMatcher.find()) {
			numbers.add(Integer.parseInt(parenthesesMatcher.group(1))); // First number
			numbers.add(Integer.parseInt(parenthesesMatcher.group(2))); // Second number
		}
		// Match "color:" pattern
		else if (colorMatcher.find()) {
			numbers.add(Integer.parseInt(colorMatcher.group(1))); // First number
			numbers.add(Integer.parseInt(colorMatcher.group(2))); // Second number
			numbers.add(Integer.parseInt(colorMatcher.group(3))); // Third number
		}
		// Match "radius:" pattern
		else if (radiusMatcher.find()) {
			numbers.add(Integer.parseInt(radiusMatcher.group(1))); // Radius value
		}

		return numbers;
	}






	public boolean containsTrue(String input) {
		return input != null && input.contains("true");
	}

	private String toString_error(PaintCommand drawing, Color color, Boolean filled) {

		if (color == null) {
			return ("Expected " + instance_string(drawing) + " color");
		} else if (drawing == null) {
			return ("Expected Start of Shape or End Paint Save File");
		} else if (filled == null) {
			return "Expected" + instance_string(drawing) + " filled";
		}
		return "";
	}

	private String instance_string(PaintCommand drawing) {
		if (drawing instanceof RectangleCommand) {
			return "Rectangle";
		} else if (drawing instanceof SquiggleCommand) {
			return "Squiggle";
		} else if (drawing instanceof PolylineCommand) {
			return "Polyline";
		} else if (drawing instanceof CircleCommand) {
			return "Circle";
		}
		return "";
	}

	private String rect_error_string(PaintCommand drawing) {

		if (drawing instanceof RectangleCommand) {
			Point pt = new Point(-10000,-10000);
			if (pt.x == ((RectangleCommand) drawing).getP1().x && pt.y == ((RectangleCommand) drawing).getP1().y) {
				return "Expected" + instance_string(drawing) + " p1";
			} else if (pt.x == ((RectangleCommand) drawing).getP2().x && pt.y == ((RectangleCommand) drawing).getP2().y) {
				return "Expected" + instance_string(drawing) + " p2";
			}
		}
		return "";
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 *
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";

		// During the parse, we will be building one of the
		// following commands. As we parse the file, we modify
		// the appropriate command.

		CircleCommand circleCommand = null;
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;

		Color color_picked = null;
		Boolean filled = null;

		Point center, p1 = null, p2 = null;
		Integer radius = null;
		boolean fileEndReached = false;
		int state=0;

		try {
			Matcher m; String l;

			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+",""); // right at the start of the while loop

				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);

				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						} else if (!l.trim().isEmpty()) {
							error("Expected Start of Paint Save File");
							return false;
						}
					case 1: // Looking for the start of a new object or end of the save file or for end of file
						m=pCircleStart.matcher(l); // starts circle
						if(m.matches()){
							state=3;
							break;
						}

						m=pSquiggleStart.matcher(l);
						if(m.matches()){ // starts squiggle
							state=9;
							break;
						}
						m=pPolylineStart.matcher(l);
						if(m.matches()){ // starts polyline
							state=16;
							break;
						}

						m=pRectangleStart.matcher(l);
						if(m.matches()){ // starts rectangle
							state=23;
							break;
						}

						m = pFileEnd.matcher(l);
						if (m.matches()) {
							state = 28;
							fileEndReached = true; // Mark the end of the file
							break;
						}
						if (!l.trim().isEmpty()) {
							error("Unexpected content or undefined message");
							return false;
						}
						break;

					case 3: //deals with Circle color

						if (pColor.matcher(l).matches()) {
							ArrayList<Integer> numbers = extractNumbers(l);

							circleCommand = new CircleCommand(new Point(0, 0), 0);

							if (extractNumbers(l).size() == 3) {
								color_picked = Color.rgb(numbers.get(0), numbers.get(1), numbers.get(2));
								circleCommand.setColor(color_picked);
								state = 4;
							} else {
								error("Expected Circle Color");
								return false;
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Circle Color");
							return false;
						}

						break;
					case 4: // deals with circle fill

						if (pFilled.matcher(l).matches()) {
							if (circleCommand != null) {
								filled = containsTrue(l);
								circleCommand.setFill(containsTrue(l));
								state = 5;
							} else {
								error("Expected Start of Shape or End Paint Save File");
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Circle filled");
							return false;
						}

						break;
					case 5: // deals with circle center
						Pattern pCenter = Pattern.compile("^\\s*center:\\((-?\\d+),(-?\\d+)\\)$");

						if (pCenter.matcher(l).matches()) {
							if (circleCommand != null && extractNumbers(l).size() == 2) {
								circleCommand.setCentre(new Point(extractNumbers(l).get(0), extractNumbers(l).get(1)));
								state = 6;
							} else {
								error("Expected Start of Shape or End Paint Save File");
								return false;
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Circle center");
							return false;
						}

						break;
					case 6: // deals with circle radius
						Pattern pRadius = Pattern.compile("^\\s*radius:(\\d+)\\s*$");


						if (pRadius.matcher(l).matches()) {
							if (circleCommand != null && !extractNumbers(l).isEmpty()) {
								circleCommand.setRadius(extractNumbers(l).getFirst());
								state = 7;
							} else {
								error("Expected Start of Paint Save File");
								return false;
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Circle radius");
							return false;
						}

						break;
					case 7: // deals with end circle

						if (pCircleEnd.matcher(l).matches()) {
							if (!toString_error(circleCommand, color_picked, filled).isEmpty()) {
								error(toString_error(circleCommand, color_picked, filled));
								return false;
							}
							if (circleCommand != null) {
								paintModel.addCommand(circleCommand);
								circleCommand = null;
							} else {
								error("Expected Start of Shape or End Paint Save File");
								return false;
							}
							color_picked = null;
							filled = null;
							state = 1;
						} else if (!l.trim().isEmpty()) {
							error("Expected Circle end");
							return false;
						}

						break;
					case 9: // deals with squiggle color

						if (pColor.matcher(l).matches()) {
							ArrayList<Integer> numbers = extractNumbers(l);

							squiggleCommand = new SquiggleCommand();
							if (extractNumbers(l).size() == 3) {
								color_picked = Color.rgb(numbers.get(0), numbers.get(1), numbers.get(2));
								squiggleCommand.setColor(color_picked);
								state = 10;
							} else {
								error("Expected Squiggle color");
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Squiggle color");
							return false;
						}

						break;
					case 10: // deals with squiggle fill

						if (pFilled.matcher(l).matches()) {
							if (squiggleCommand != null) {
								filled = containsTrue(l);
								squiggleCommand.setFill(containsTrue(l));
								state = 11;
							} else {
								error("Expected Start of Shape or End Paint Save File");
							}
						}  else if (!l.trim().isEmpty()) {
							error("Expected Squiggle fill");
							return false;
						}

						break;
					case 11: // deals with squiggle points start
						// points
						Pattern point_start_squiggle = Pattern.compile("^\\s*points$");

						if (point_start_squiggle.matcher(l).matches()) {
							state = 12;
						} else if (!l.trim().isEmpty()) {
							error("Expected Squiggle fill");
							return false;
						}

						break;
					case 12: // deals with squiggle point
						Pattern point = Pattern.compile("^\\s*point:\\((-?\\d+),\\s*(-?\\d+)\\)\\s*$");
						Pattern point_end_squiggle = Pattern.compile("^\\s*endpoints\\s*$");


						if (point.matcher(l).matches() || point_end_squiggle.matcher(l).matches()) {
							if (point_end_squiggle.matcher(l).matches()) {
								state = 14;
							} else if (squiggleCommand != null && extractNumbers(l).size() == 2) {
								squiggleCommand.add(new Point(extractNumbers(l).get(0), extractNumbers(l).get(1)));

							} else {
								error("Expected Start of Shape or End Paint Save File");
							}
						}  else if (!l.trim().isEmpty()) {
							error("Expected Squiggle point");
							return false;
						}
						break;
					case 14: // deals with squiggle end
						// End Squiggle
						Pattern pend_squiggle = Pattern.compile("^\\s*EndSquiggle$");


						if (pend_squiggle.matcher(l).matches()) {
							if (!toString_error(squiggleCommand, color_picked, filled).isEmpty()) {
								error(toString_error(squiggleCommand, color_picked, filled));
								return false;
							} else if (squiggleCommand != null) {
								if (squiggleCommand.getPoints().isEmpty()) {
									error("Expected Polyline Points");
									return false;
								}
							}

							if (squiggleCommand != null) {
								paintModel.addCommand(squiggleCommand);
								squiggleCommand = null;
								color_picked = null;
								filled = null;
								state = 1;
							} else {
								error("Expected Start of Shape or End Paint Save File");
								return false;
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Squiggle end");
							return false;
						}
						break;
					case 16: // deals with polyline color
						// Regex input

						if (pColor.matcher(l).matches()) {
							ArrayList<Integer> numbers = extractNumbers(l);

							polylineCommand = new PolylineCommand();
							if (extractNumbers(l).size() == 3) {
								color_picked = Color.rgb(numbers.get(0), numbers.get(1), numbers.get(2));
								polylineCommand.setColor(color_picked);
								state = 17;
							} else {
								error("Expected Polyline color");
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Polyline color");
							return false;
						}
						break;
					case 17: // deals with polyline fill

						if (pFilled.matcher(l).matches()) {
							if (polylineCommand != null) {
								filled = containsTrue(l);
								polylineCommand.setFill(containsTrue(l));
								state = 18;
							} else {
								error("Expected Start of Shape or End Paint Save File");
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Polyline end");
							return false;
						}
						break;
					case 18: // deals with polyline points start

						Pattern point_start_Polyline = Pattern.compile("^\\s*points$");

						if (point_start_Polyline.matcher(l).matches()) {
							state = 19;
						} else if (!l.trim().isEmpty()) {
							error("Expected Polyline end");
						}

						break;
					case 19: // deals with polyline point

						Pattern point_Polyline = Pattern.compile("^\\s*point:\\((-?\\d+),\\s*(-?\\d+)\\)\\s*$");

						Pattern point_end_Polyline = Pattern.compile("^\\s*endpoints\\s*$");



						if (point_Polyline.matcher(l).matches() || point_end_Polyline.matcher(l).matches()) {
							if (polylineCommand != null && extractNumbers(l).size() == 2) {
								polylineCommand.add(new Point(extractNumbers(l).get(0), extractNumbers(l).get(1)));
							} else if (point_end_Polyline.matcher(l).matches()) {
								state = 21;
							} else {
								error("Expected Polyline points");
							}
						} else if (!l.trim().isEmpty()) {
							error( "Expected Polyline points");
							return false;
						}

						break;

					case 21: // deals with polyline end
						// End Polyline
						Pattern pend_Polyline = Pattern.compile("^\\s*EndPolyline$");


						if (pend_Polyline.matcher(l).matches()) {
							if (!toString_error(polylineCommand, color_picked, filled).isEmpty()) {
								error(toString_error(polylineCommand, color_picked, filled));
								return false;
							} else if (polylineCommand != null) {
								if (polylineCommand.points.isEmpty()) {
									error( "Expected Polyline Points");
									return false;
								}
							}

							if (polylineCommand != null) {
								paintModel.addCommand(polylineCommand);
								state = 1;
								polylineCommand = null;
								color_picked = null;
								filled = null;
							} else {
								error("Expected Start of Shape or End Paint Save File");
								return false;
							}
						}

						break;

					case 23: // deals with rectangle color

						if (pColor.matcher(l).matches()) {
							ArrayList<Integer> numbers = extractNumbers(l);

							rectangleCommand = new RectangleCommand(new Point(0,0), new Point(0,0));

							if (extractNumbers(l).size() == 3) {
								color_picked = Color.rgb(numbers.get(0), numbers.get(1), numbers.get(2));
								rectangleCommand.setColor(color_picked);
								state = 24;
							} else {
								error("Expected Rectangle color");
								error("rect color error");
							}
						}  else if (!l.trim().isEmpty()) {
							error("Expected Rectangle color");
							return false;
						}
						break;
					case 24: // deals with rectangle fill
						if (pFilled.matcher(l).matches()) {
							if (rectangleCommand != null) {
								filled = containsTrue(l);
								rectangleCommand.setFill(containsTrue(l));
								state = 25;
							}
						}   else if (!l.trim().isEmpty()) {
							error("Expected Rectangle fill");
							return false;
						}

						break;
					case 25: // deals with rectangle p1
						Pattern p1_rect = Pattern.compile("^\\s*p1:\\((-?\\d+),(-?\\d+)\\)\\s*$");

						System.out.println(l);
						System.out.println(extractNumbers(l));

						if (p1_rect.matcher(l).matches()) {
							if (rectangleCommand != null && extractNumbers(l).size() == 2) {
								rectangleCommand.setP1(new Point(extractNumbers(l).getFirst(), extractNumbers(l).get(1)));
								p1 = new Point(extractNumbers(l).getFirst(), extractNumbers(l).get(1));
								state = 26;
							} else if (!l.trim().isEmpty()) {
									error("Expected Rectangle color");
									return false;
							}
						} else if (!l.trim().isEmpty()) {
							error("Expected Rectangle p1");
							return false;
						}
						break;
					case 26: // deals with rectangle p2
						Pattern p2_rect = Pattern.compile("^\\s*p2:\\((-?\\d+),(-?\\d+)\\)\\s*$");

						if (p2_rect.matcher(l).matches()) {
							if (rectangleCommand != null && !extractNumbers(l).isEmpty()) {
								rectangleCommand.setP2(new Point(extractNumbers(l).getFirst(), extractNumbers(l).get(1)));
								p2 = new Point(extractNumbers(l).getFirst(), extractNumbers(l).get(1));
								state = 27;
							}  else if (!l.trim().isEmpty()) {
								error("Expected Rectangle p2");
								return false;
							}
						}  else if (!l.trim().isEmpty()) {
							error("Expected Rectangle p2");
							return false;
						}
						break;
					case 27: // deals with rectangle end

						// End Squiggle
						Pattern pend_rect = Pattern.compile("^\\s*EndRectangle$");

						if (pend_rect.matcher(l).matches()) {
							if (!toString_error(rectangleCommand, color_picked, filled).isEmpty()) {
								error(toString_error(rectangleCommand, color_picked, filled));
								return false;
							} else if (!rect_error_string(rectangleCommand).isEmpty()) {
								error(rect_error_string(rectangleCommand));
								return false;
							} else if (p1 == null) {
								error("Expected Rectangle p1" );
								return false;
							} else if (p2 == null) {
								error("Expected Rectangle p2" );
								return false;
							}

							if (rectangleCommand != null) {
								paintModel.addCommand(rectangleCommand);
								rectangleCommand = null;
								p1 = null;
								p2 = null;
								color_picked = null;
								filled = null;
								state = 1;
							} else {
								error("Expected Start of Shape or End Paint Save File");
								return false;
							}
						}  else if (!l.trim().isEmpty()) {
							error("Expected Rectangle end");
							return false;
						}

						break;
					case 28: //extra content after file
						if (!l.trim().isEmpty()) {
							error("Unexpected content or undefined message");
							return false;
						}
						return true;
					// ...
					/**
					 * I have around 20+/-5 cases in my FSM. If you have too many
					 * more or less, you are doing something wrong. Too few, and I bet I can find
					 * a bad file that you will say is good. Too many and you are not capturing the right concepts.
					 *
					 * Here are the errors I catch. All of these should be in your code.
					 *
					 	error("Expected Start of Paint Save File");
						error("Expected Start of Shape or End Paint Save File");
						error("Expected Circle color");
						error("Expected Circle filled");
						error("Expected Circle center");
						error("Expected Circle Radius");
						error("Expected End Circle");
						error("Expected Rectangle color");
						error("Expected Rectangle filled");
						error("Expected Rectangle p1");
						error("Expected Rectangle p2");
						error("Expected End Rectangle");
						error("Expected Squiggle color");
						error("Expected Squiggle filled");
						error("Expected Squiggle points");
						error("Expected Squiggle point or end points");
						error("Expected End Squiggle");
						error("Expected Polyline color");
						error("Expected Polyline filled");
						error("Expected Polyline points");
						error("Expected Polyline point or end points");
						error("Expected End Polyline");
						error("Extra content after End of File");
						error("Unexpected end of file");
					 */
				}
			}
			return true;
		}  catch (Exception e){
			System.err.println(e.getMessage());
		}

		return true;
	}
}
