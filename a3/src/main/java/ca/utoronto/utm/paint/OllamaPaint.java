package ca.utoronto.utm.paint;


public class OllamaPaint extends Ollama{
    public OllamaPaint(String host){
        super(host);
    }

    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName){
        // YOUR CODE GOES HERE

        //prompt 1
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String system="Here is a breakdown of what I want you to observe. in the file you will see what each line means " +format;
        String response = this.call(system, prompt);

        //prompt 2
         format = FileIO.readResourceFile("paintSaveFileExample1.txt");
         system="The answer to this question should be a Paint Save File. After observing the format here is an example of a proper paint save file." +format;
         response = this.call(system, prompt);

        format = FileIO.readResourceFile("paintSaveFileExample1.txt");
        system="here is another example of a proper paint save file. Notice how the drawings are made. Also the center of the board is usually at (200,200) so try to keep your drawings\" +\n" +
                "                \"around that area" +format;
        response = this.call(system, prompt);

        format = FileIO.readResourceFile("paintSaveFileExample1.txt");
        system="This format here is another example of a proper paint save file. Notice how we start with Paint Save File Format Version 1.0 and end with" +
                "End Paint Save File" +format;
        response = this.call(system, prompt);

        system="Keep in mind there can only be 4 types of drawings, Circle, Squiggle, Polyline and Rectangle, and from now on when I tell you to do something " +
                "it means that you should only make Paint Files and nothing else. Also the center of the board is usually at (200,200) SO START DRAWING AT THIS POINT." +
                "around that area";
        response = this.call(system, prompt);

        format = FileIO.readResourceFile("paintSaveFileExample1.txt");
        system="here is another example of a proper paint save file. You will always have to start and end the file exactly as its done here?" +format;
        response = this.call(system, prompt);

        FileIO.writeHomeFile(response, outFileName);
        editHomeFile(outFileName);
    }
    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName){
        // YOUR CODE GOES HERE
        // Your job is to create the right system and prompt.
        // then call Ollama and write the new file in the home directory
        // HINT: You should have a collection of resources, examples, prompt wrapper etc. available
        // in the resources directory. See OllamaNumberedFile as an example.

        String format = FileIO.readResourceFile("paintSaveFileExample1.txt");
        String system="The answer to this question should be in the Paint Save File format I provided. " +format;
        String f = format;

        String fullPrompt ="Produce a new Paint Save File, resulting in, "+prompt+ "."+f;

        String warning = "Produce a new Paint Save File, resulting in, "+prompt+ ", following the format here, Also the center of the board is usually at (200,200) so try to keep your drawings\" +\n" +
                "                \"around that area "+f;

        this.call(system, warning);
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
        editHomeFile(outFileName);
    }

    /**
     * newFile1: I am going to draw an ice-cream cone
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile1(String outFileName) {
        // YOUR CODE GOES HERE
        String starting_prompt = "I want you to make a Paint Save File that draws a ice cream cone. " +
                "So 3 circles are needed and using polyline draw A BIG TRIANGLE in the bottom using a POLYLINE WITH 4 POINTS. Write commands that look like " +
                "what you saw in the Paint Save examples and do not make anything else up. Everything you write must be influenced from what" +
                "I said so far.";

        newFile(starting_prompt, outFileName);
    }

    /**
     * newFile2: DESCRIBE YOUR INTERESTING NEW PAINT FILE HERE
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile2(String outFileName) {
        // YOUR CODE GOES HERE
        String starting_prompt = "I want you to make a Paint Save File that draws a landscape with the sun. " +
                "So a green rectangle made from the rgb color covering the lower half of the screen with a yellow circle illuminating above it.";

        newFile(starting_prompt, outFileName);
    }

    /**
     * newFile3: DESCRIBE YOUR INTERESTING NEW PAINT FILE HERE
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile3(String outFileName) {
        // YOUR CODE GOES HERE
        String starting_prompt = "NOW LISTEN TO ME. I WANT YOU TO MAKE A SNOWMAN USING 3 CIRCLES AND POLYLINES TO MODEL A TRIANGLE AND PROVIDE ONLY" +
                "A PAINT SAVE FILE FOR IT. ";

        newFile(starting_prompt, outFileName);
    }

    /**
     * modifyFile1: MODIFY inFileName TO PRODUCE outFileName BY ...
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE


        modifyFile("I want you to make the format similar to the previous example. but to make the ice cream cone.", inFileName, outFileName);
        modifyFile("it must follow this format. Only give me the new file and do not type anything else otherwise " +
                "response it will break the file.", inFileName, outFileName);
        modifyFile("Stop saying Here is the new Paint Save File:\n did you not here what i said dumbass. start your respone with Paint Save File Version 1.0" +
                "", inFileName, outFileName);
        modifyFile("Theres no such thing as a triangle you have to use polyline to model it now make the paint save file again. Additionally remmeber that the " +
                "coordinates you give will be used by java Graphics context to draw.", inFileName, outFileName);
    }

    /**
     * modifyFile2: MODIFY inFileName TO PRODUCE outFileName BY making a landscape with a sun
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE
        modifyFile("Next, using what you learned about the Paint Save File from the example provided, make a rectangular landscape " +
                "with the sun illuminating over it.", inFileName, outFileName);
        modifyFile("the resulting file must follow the format provided.", inFileName, outFileName);
        modifyFile("start your respone with Paint Save File Version 1.0 and end with End Paint Save File" +
                "", inFileName, outFileName);
    }
    /**
     * modifyFile3: Making a snowman
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        // YOUR CODE GOES HERE
        modifyFile("Next, using what you learned about the Paint Save File from the example provided, make a snowman by" +
                "using 3 Circles, and Polylines only.", inFileName, outFileName);
        modifyFile("the resulting file must follow the format provided.", inFileName, outFileName);
        modifyFile("start your respone with Paint Save File Version 1.0 and end with End Paint Save File" +
                "", inFileName, outFileName);
    }

    public static void editHomeFile(String fileName) {
        // Define the markers
        String startMarker = "Paint Save File Version 1.0";
        String endMarker = "End Paint Save File";

        // Read file content using the readFile method
        String fileContent = FileIO.readHomeFile(fileName);

        // Check if content is not empty
        if (fileContent != null && !fileContent.isEmpty()) {
            // StringBuilder to store modified content
            StringBuilder content = new StringBuilder();
            boolean capturing = false;

            // Split file content by lines
            String[] lines = fileContent.split("\n");

            for (String line : lines) {
                // Start capturing content after startMarker is found
                if (line.contains(startMarker)) {
                    capturing = true;
                    content.append(line).append("\n");
                    continue;
                }

                // Stop capturing content after endMarker is found
                if (line.contains(endMarker) && capturing) {
                    capturing = false;
                    content.append(line).append("\n");
                    break;
                }

                // Add lines to content only when capturing is true
                if (capturing) {
                    content.append(line).append("\n");
                }
            }

            // Write the modified content back using writeHomeFile
            FileIO.writeHomeFile(content.toString(), fileName);
        }
    }




    public static void main(String [] args){
        String prompt = null;


        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        OllamaPaint op = new OllamaPaint("dh2010pc30.utm.utoronto.ca"); // Replace this with your assigned Ollama server.

        /*
        //create OllamaPaintFile1
        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");

        //modify OllamaPaintFile1 and store results in new OllamaPaintFile2
        op.modifyFile("Remove all shapes except for the circles.","OllamaPaintFile1.txt", "OllamaPaintFile2.txt" );

        // create OllamaPaintFile3
        prompt="Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");

        //modify OllamaPaintFile3 and store results in OllamaPaintFile4
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt" );

        // create OllamaPaintFile4
        prompt="Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile4.txt");

        //modify OllamaPaintFile4 and store results in OllamaPaintFile5
        prompt="Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile4.txt", "OllamaPaintFile5.txt" );
        */

        for(int i=1;i<=3;i++){
            op.newFile1("PaintFile1_"+i+".txt");
            op.newFile2("PaintFile2_"+i+".txt");
            op.newFile3("PaintFile3_"+i+".txt");
        }
        for(int i=1;i<=3;i++){
            for(int j=1;j<=3;j++) {
                op.modifyFile1("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_1.txt");
                op.modifyFile2("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_2.txt");
                op.modifyFile3("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_3.txt");
            }
        }
    }
}
