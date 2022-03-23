import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.TreeSet;

public class Screen extends JPanel implements ActionListener, KeyListener, MouseListener{
	
	//instance variables
	private final int screenWidth = 1900;
	private final int screenHeight = 950;
	
	private JButton saveChangesButton, addNewEventButton, addNewPeriodButton, removeEventButton;
	private JButton showTaggedEventsButton, hideTaggedEventsButton, addTagButton, removeTagButton;
	private JButton prevImageButton, nextImageButton, addImageButton, deleteImageButton;
	private JTextArea descriptionTextArea, tagTextArea, imageNameTextArea;
	private JScrollPane descriptionPane, tagPane;
	private JTextField titleField, monthField, dayField, yearField, month2Field, day2Field, year2Field;
	private JTextField redField, greenField, blueField, hexField, imageCaptionField;
	private JComboBox eventShapeComboBox, eventAlignmentComboBox, categoryComboBox, tagComboBox;
	private JCheckBox eventPositionCheckBox, BCCheckBox, BCCheckBox2, presentCheckBox;
	private JFileChooser chooser;
	private FileNameExtensionFilter filter;
	
	private Timeline timeline;
	private Event selectedEvent;
	private TreeSet<Event> eventTree;
	private boolean editMode, modernDating, controlKeyDown, shiftKeyDown, showTagHider, darkMode;
	private byte taggedEventsVisibility; // -1 is hide tagged events, 0 is show all, 1 is show only tagged events
	private String[] tagList;
	private byte imageIndex;
	private Color backgroundColor;
	private final Color lightModeColor, darkModeColor;
	private final int descriptionPaneX1, descriptionPaneY1, descriptionPaneW1, descriptionPaneH1, descriptionPaneX2, descriptionPaneY2, descriptionPaneW2, descriptionPaneH2;
	
	//constructor
	@SuppressWarnings("unchecked")
	public Screen(){
		setLayout(null);
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		
		eventTree = new TreeSet<Event>();
		readFromFile();
		
		
		titleField = new JTextField("Event Title");
		titleField.setBounds(30, 30, screenWidth/2, 30);
		add(titleField);
		
		descriptionTextArea = new JTextArea();
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		
		descriptionPaneX1 = screenWidth*1/20;
		descriptionPaneY1 = screenHeight*1/20;
		descriptionPaneW1 = screenWidth*12/20;
		descriptionPaneH1 = screenHeight*18/20;
		
		descriptionPaneX2 = titleField.getX();
		descriptionPaneY2 = titleField.getY() + titleField.getHeight() + 30;
		descriptionPaneW2 = titleField.getWidth();
		descriptionPaneH2 = screenHeight - 30 - (titleField.getY() + titleField.getHeight() + 30);
		
		descriptionPane = new JScrollPane(descriptionTextArea); 
        descriptionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        descriptionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		descriptionPane.setBounds(descriptionPaneX1, descriptionPaneY1, descriptionPaneW1, descriptionPaneH1);
		add(descriptionPane);
		
		monthField = new JTextField("Month");
		monthField.setBounds(titleField.getX() + titleField.getWidth() + 30, titleField.getY(), 80, 30);
		add(monthField);		
		
		dayField = new JTextField("Day");
		dayField.setBounds(monthField.getX() + monthField.getWidth() + 15, monthField.getY(), 30, monthField.getHeight());
		add(dayField);

		yearField = new JTextField("Year");
		yearField.setBounds(dayField.getX() + dayField.getWidth() + 15, dayField.getY(), 60, dayField.getHeight());
		add(yearField);
		
		BCCheckBox = new JCheckBox("BC");
		BCCheckBox.setBounds(yearField.getX() + yearField.getWidth() + 10, yearField.getY(), 60, yearField.getHeight());
		BCCheckBox.setOpaque(false);
		add(BCCheckBox);
		
		month2Field = new JTextField("Month");
		month2Field.setBounds(BCCheckBox.getX() + BCCheckBox.getWidth() + 60, BCCheckBox.getY(), monthField.getWidth(), BCCheckBox.getHeight());
		add(month2Field);
		
		day2Field = new JTextField("Day");
		day2Field.setBounds(month2Field.getX() + month2Field.getWidth() + 15, month2Field.getY(), 30, month2Field.getHeight());
		add(day2Field);
		
		year2Field = new JTextField("Year");
		year2Field.setBounds(day2Field.getX() + day2Field.getWidth() + 15, day2Field.getY(), 60, day2Field.getHeight());
		add(year2Field);
		
		BCCheckBox2 = new JCheckBox("BC");
		BCCheckBox2.setBounds(year2Field.getX() + year2Field.getWidth() + 10, year2Field.getY(), BCCheckBox.getWidth(), BCCheckBox.getHeight());
		BCCheckBox2.setOpaque(false);
		add(BCCheckBox2);
		
		presentCheckBox = new JCheckBox("present");
		presentCheckBox.setOpaque(false);
		add(presentCheckBox);
		presentCheckBox.addActionListener(this);
		
		redField = new JTextField("RED");
		redField.setBounds(monthField.getX(), descriptionPaneY2 + 10, 30, monthField.getHeight());
		add(redField);
		
		greenField = new JTextField("GRN");
		greenField.setBounds(redField.getX() + redField.getWidth() + 20, redField.getY(), redField.getWidth(), redField.getHeight());
		add(greenField);
		
		blueField = new JTextField("BLU");
		blueField.setBounds(greenField.getX() + greenField.getWidth() + 20, greenField.getY(), greenField.getWidth(), greenField.getHeight());
		add(blueField);
		
		hexField = new JTextField("#HEX");
		hexField.setBounds((redField.getX() + blueField.getX() + blueField.getWidth() - 70)/2, redField.getY() + redField.getHeight() + 20, 70, redField.getHeight());
		add(hexField);
		
		DLList<Tag> categoriesList = new DLList<Tag>();
		try{
			File file = new File("categories.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[1000];
			reader.read(text);
			String s = new String(text);
			String[] categoriesArr = s.split("\n");
			for (String each : categoriesArr){
				String[] categoryArr = each.split(";");
				if (categoryArr.length == 4)
					categoriesList.add(new Tag(categoryArr[0], new Color(Integer.parseInt(categoryArr[1]), Integer.parseInt(categoryArr[2]), Integer.parseInt(categoryArr[3]))));
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in Screen() attempting to set up categories");
			System.err.println(ex);
			categoriesList.add(new Tag("<category>", new Color(128, 128, 128)));	//gray
		}
		
		Tag[] categories = new Tag[categoriesList.size()];
		for (int i = 0; i < categories.length; i++)
			categories[i] = categoriesList.get(i);
		
		categoryComboBox = new JComboBox(categories);
		categoryComboBox.setBounds(blueField.getX() + blueField.getWidth() + 150, blueField.getY(), 400, 40);
		add(categoryComboBox);
		
		eventPositionCheckBox = new JCheckBox();
		eventShapeComboBox = new JComboBox();
		
		String[] orientations = {"Left-aligned", "Centered", "Right-aligned"};
		eventAlignmentComboBox = new JComboBox(orientations);
		eventAlignmentComboBox.setBounds((screenWidth + categoryComboBox.getX() + categoryComboBox.getWidth() - 140)/2, categoryComboBox.getY(), 140, categoryComboBox.getHeight());
		add(eventAlignmentComboBox);
		
		tagTextArea = new JTextArea();
		tagTextArea.setLineWrap(true);
		tagTextArea.setWrapStyleWord(true);
		tagTextArea.setEditable(false);
		
		tagPane = new JScrollPane(tagTextArea); 
        tagPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tagPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tagPane.setBounds(redField.getX() - 10, eventAlignmentComboBox.getY() + eventAlignmentComboBox.getHeight() + 100, 500, 100);
		add(tagPane);
		
		DLList<String> tagStrings = new DLList<String>();
		try{
			File file = new File("tags.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[10000];
			reader.read(text);
			String s = new String(text);
			String[] tagsArr = s.split("\n");
			for (String each : tagsArr){
				if (each.contains("//") || each.length() == 0 || (int)each.charAt(0) == 0)
					continue;
				tagStrings.add(new String(each));
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in Screen() attempting to set up tags");
			System.err.println(ex);
			tagStrings.add(new String("temp"));
		}
		
		String[] tagsArr = new String[tagStrings.size()];
		for (int i = 0; i < tagsArr.length; i++)
			tagsArr[i] = tagStrings.get(i);
		tagComboBox = new JComboBox(tagsArr);
		tagComboBox.setBounds(tagPane.getX() + tagPane.getWidth() + 30, tagPane.getY(), 300, 40);
		add(tagComboBox);
		
		imageNameTextArea = new JTextArea("<select image>");
		// imageNameTextArea.setBounds
		
		saveChangesButton = new JButton("Save changes");
		saveChangesButton.setBounds(screenWidth*3/4 - 160 - 30, screenHeight - 2*40 - 30, 160, 40);
		add(saveChangesButton);
		saveChangesButton.addActionListener(this);
		
		removeEventButton = new JButton("Delete Event");
		removeEventButton.setBounds(saveChangesButton.getX() + saveChangesButton.getWidth() + 60, saveChangesButton.getY(), saveChangesButton.getWidth(), saveChangesButton.getHeight());
		add(removeEventButton);
		removeEventButton.addActionListener(this);
		
		addNewEventButton = new JButton("Add event");
		addNewEventButton.setBounds(screenWidth/2 - saveChangesButton.getWidth() - 30, 10, saveChangesButton.getWidth(), saveChangesButton.getHeight());
		add(addNewEventButton);
		addNewEventButton.addActionListener(this);
		
		addNewPeriodButton = new JButton("Add period");
		addNewPeriodButton.setBounds(screenWidth/2 + 30, 10, saveChangesButton.getWidth(), saveChangesButton.getHeight());
		add(addNewPeriodButton);
		addNewPeriodButton.addActionListener(this);
		
		addTagButton = new JButton("Add tag");
		addTagButton.setBounds(tagComboBox.getX(), tagPane.getY() + tagPane.getHeight() - saveChangesButton.getHeight(), tagComboBox.getWidth()/2 - 5, saveChangesButton.getHeight());
		add(addTagButton);
		addTagButton.addActionListener(this);
		
		removeTagButton = new JButton("Remove tag");
		removeTagButton.setBounds(tagComboBox.getX() + tagComboBox.getWidth() - addTagButton.getWidth(), addTagButton.getY(), addTagButton.getWidth(), saveChangesButton.getHeight());
		add(removeTagButton);
		removeTagButton.addActionListener(this);
		
		showTaggedEventsButton = new JButton("Show tagged events");
		showTaggedEventsButton.setBounds(tagPane.getX() + tagPane.getWidth()/2 - 200 - 30, addTagButton.getY() + addTagButton.getHeight() + 15, 200, saveChangesButton.getHeight());
		add(showTaggedEventsButton);
		showTaggedEventsButton.addActionListener(this);
		
		hideTaggedEventsButton = new JButton("Hide tagged events");
		hideTaggedEventsButton.setBounds(removeTagButton.getX(), removeTagButton.getY() + removeTagButton.getHeight() + 15, showTaggedEventsButton.getWidth(), removeTagButton.getHeight());
		add(hideTaggedEventsButton);
		hideTaggedEventsButton.addActionListener(this);
		
		prevImageButton = new JButton("^ ^ ^");
		prevImageButton.setBounds(descriptionPaneX1 + descriptionPaneW1 + (screenWidth - descriptionPaneX1 - descriptionPaneW1 - 80)/2, descriptionPaneY1, 80, 40);
		add(prevImageButton);
		prevImageButton.addActionListener(this);
		
		nextImageButton = new JButton("v v v");
		nextImageButton.setBounds(prevImageButton.getX(), descriptionPaneY1+ descriptionPaneH1 - prevImageButton.getHeight(), prevImageButton.getWidth(), prevImageButton.getHeight());
		add(nextImageButton);
		nextImageButton.addActionListener(this);
		
		timeline = new Timeline(screenWidth, screenHeight);
		editMode = false;
		modernDating = false;
		controlKeyDown = false;
		shiftKeyDown = false;
		showTagHider = false;
		darkMode = true;
		taggedEventsVisibility = 0;
		imageIndex = 0;
		
		chooser = new JFileChooser("images");
		filter = new FileNameExtensionFilter("image types", "jpg", "jpeg", "png", "svg", "gif");
		chooser.setFileFilter(filter);
		
		lightModeColor = new Color(230, 230, 230);
		darkModeColor = new Color(40, 40, 40);
		backgroundColor = darkModeColor;
		
		updateComponentVisibility(true);
	}
	
	//methods
	public Dimension getPreferredSize(){
		return new Dimension(screenWidth, screenHeight);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, screenWidth, screenHeight);
		
		if (selectedEvent == null && !showTagHider)
			timeline.drawTimeline(g, eventTree, tagList, taggedEventsVisibility, modernDating, darkMode);
		
		if (selectedEvent != null){
			if (selectedEvent.getImages().size() >= imageIndex+1){
				selectedEvent.getImages().get(imageIndex).drawFromFile(g, prevImageButton.getX() + prevImageButton.getWidth()/2, descriptionPane.getY() + descriptionPane.getHeight()/2, screenWidth - descriptionPane.getX() - descriptionPane.getWidth() - 2*5, nextImageButton.getY() - prevImageButton.getY() - prevImageButton.getHeight() - 2*5);
			}
			
			if (editMode){
				g.setColor(Color.red);
				g.fillRect(redField.getX() - 10, redField.getY() - 10, redField.getWidth() + 20, redField.getHeight() + 20);
				g.setColor(Color.green);
				g.fillRect(greenField.getX() - 10, greenField.getY() - 10, greenField.getWidth() + 20, greenField.getHeight() + 20);
				g.setColor(Color.blue);
				g.fillRect(blueField.getX() - 10, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
				g.setColor(selectedEvent.getColor());
				g.fillRect(blueField.getX() + blueField.getWidth() + 50, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
				g.setColor(Color.black);
				g.drawRect(blueField.getX() + blueField.getWidth() + 50, blueField.getY() - 10, blueField.getWidth() + 20, blueField.getHeight() + 20);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == saveChangesButton){
			saveChanges();
		} else if (e.getSource() == addNewEventButton){
			Event event = new Event(false);
			eventTree.add(event);
			selectedEvent = event;
			updateComponentVisibility(false);
			initializeFieldText();
		} else if (e.getSource() == addNewPeriodButton){
			Event event = new Event(true);
			eventTree.add(event);
			selectedEvent = event;
			updateComponentVisibility(true);
			initializeFieldText();
		} else if (e.getSource() == removeEventButton){
			eventTree.remove(selectedEvent);			
			selectedEvent = null;
			updateComponentVisibility(true);
			writeToFile();
		} else if (e.getSource() == presentCheckBox){
			presentFieldHandler();
		} else if (e.getSource() == showTaggedEventsButton){
			taggedEventsVisibility = 1;
			showTagHider = false;
			
			if (!tagTextArea.getText().equals("none"))
				tagList = tagTextArea.getText().split(", ");
		} else if (e.getSource() == hideTaggedEventsButton){
			showTagHider = false;
			if (tagTextArea.getText().equals("none"))
				taggedEventsVisibility = 0;
			else {
				taggedEventsVisibility = -1;
				tagList = tagTextArea.getText().split(", ");
			}
		} else if (e.getSource() == addTagButton){
			String tag = (String)tagComboBox.getSelectedItem();
			if (showTagHider){
				if (!tagTextArea.getText().contains(tag))
					tagTextArea.setText(tagTextArea.getText().equals("none") ? tag : (tagTextArea.getText() + ", " + tag));
			} else {
				if (selectedEvent.addTag(tag))
					tagTextArea.setText(selectedEvent.getTagString());
			}
		} else if (e.getSource() == removeTagButton){
			String tag = (String)tagComboBox.getSelectedItem();
			if (showTagHider){
				if (!tagTextArea.getText().contains(tag)){}
				else if (tagTextArea.getText().contains(", ")){
					int tagSpot = tagTextArea.getText().indexOf(tag);
					String s = tagTextArea.getText().substring(0, tagSpot);
					if (tagSpot + tag.length() == tagTextArea.getText().length())
						s = tagTextArea.getText().substring(0, tagSpot - 2);
					else
						s += tagTextArea.getText().substring(tagSpot + tag.length() + 2, tagTextArea.getText().length());
					tagTextArea.setText(s);
				} else
					tagTextArea.setText("none");
			} else {
				selectedEvent.getTags().remove(tag);
				tagTextArea.setText(selectedEvent.getTagString());
			}
		} else if (e.getSource() == prevImageButton){
			imageIndex--;
		} else if (e.getSource() == nextImageButton){
			imageIndex++;
		}
		
		repaint();
	}
	
	private void saveChanges(){		
		Event event = new Event(selectedEvent.getIsPeriod());
		boolean allChecksPassed = false;
		try{
			String title = titleField.getText();
			String description = descriptionTextArea.getText();
			String monthString = monthField.getText();
			int month = 0;
			if ("1234567890".contains("" + monthString.charAt(0)))
				month = Integer.parseInt(monthString);
			else{
				for (int i = 0; i < Date.getMonthNames().length; i++){
					if (monthString.substring(0, 3).toUpperCase().equals(Date.getMonthNames()[i].substring(0, 3).toUpperCase()))
						month = i+1;
				}
			}
			int day = Integer.parseInt(dayField.getText());
			int year = Integer.parseInt(yearField.getText());
			if (BCCheckBox.isSelected())
				year = (year - 1) * -1;
			
			if (month <= 0 || day <= 0 || month > 12)
				throw new IndexOutOfBoundsException("month or day input too low, or month input too high");
			if (Date.getMonthLengths()[month-1] < day && !(month == 2 && day == 29 && Date.isLeapYear(year)))
				throw new IndexOutOfBoundsException("day input too high");
			
			int red = 128;
			int green = 128;
			int blue = 128;
			
			Tag category = new Tag("null", new Color(red, green, blue));
			if (categoryComboBox.getSelectedItem() instanceof Tag && !category.equals("<category>")){
				category = (Tag)categoryComboBox.getSelectedItem();
				Color color = category.getColor();
				red = color.getRed();
				green = color.getGreen();
				blue = color.getBlue();
			} else {
			
				try{
					red = Integer.parseInt(redField.getText());
					green = Integer.parseInt(greenField.getText());
					blue = Integer.parseInt(blueField.getText());
				} catch (NumberFormatException ex){}
				
				if (red == selectedEvent.getColor().getRed() && green == selectedEvent.getColor().getGreen() && blue == selectedEvent.getColor().getBlue() 
					|| red < 0 || green < 0 || blue < 0 || red > 255 || green > 255 || blue > 255){
					int[] rgbColors = Event.HextoRGB(hexField.getText());
					red = rgbColors[0];
					green = rgbColors[1];
					blue = rgbColors[2];
				}
			}
			
			String[] tagArr = tagTextArea.getText().split(", ");
			DLList<String> tags = new DLList<String>();
			if (tagArr.length > 0 && !tagArr[0].equals("none") && tagArr[0].length() > 1){
				for (int i = 0; i < tagArr.length; i++)
					tags.add(tagArr[i]);
			}
			
			if (selectedEvent.getIsPeriod() && presentCheckBox.isSelected()){
				event = new Event(title, description, month, day, year, true, red, green, blue, category.toString(), tags, null);
				allChecksPassed = true;
			} else if (selectedEvent.getIsPeriod() && !presentCheckBox.isSelected()){
				String month2String = month2Field.getText();
				int month2 = 0;
				if ("1234567890".contains("" + month2String.charAt(0)))
					month2 = Integer.parseInt(month2String);
				else{
					for (int i = 0; i < Date.getMonthNames().length; i++){
						if (month2String.substring(0, 3).toUpperCase().equals(Date.getMonthNames()[i].substring(0, 3).toUpperCase())){
							month2 = i+1;
							break;
						}
					}
				}
				int day2 = Integer.parseInt(day2Field.getText());
				int year2 = Integer.parseInt(year2Field.getText());
				if (BCCheckBox2.isSelected())
					year2 = (year2 - 1) * -1;
				
				if (month2 <= 0 || day2 <= 0 || month2 > 12)
					throw new IndexOutOfBoundsException("month2 or year2 input too low, or month2 input too high");
				if (Date.getMonthLengths()[month2-1] < day2 && !(month2 == 2 && day2 == 29 && Date.isLeapYear(year2)))
					throw new IndexOutOfBoundsException("day2 input too high");
				if (year2 < year || (year2 == year && month2 < month) || (year2 == year && month2 == month && day2 < day))
					throw new IndexOutOfBoundsException("date2 occurs before date1");
				if (year == year2 && month == month2 && day == day2)
					throw new IndexOutOfBoundsException("date1 is the same as date2");
				
				event = new Event(title, description, month, day, year, month2, day2, year2, red, green, blue, category.getTitle(), tags, new DLList<MyImage>());
				allChecksPassed = true;
			} else {
				String alignment = eventAlignmentComboBox.getSelectedItem().toString();
				event = new Event(title, description, month, day, year, red, green, blue, category.getTitle(), alignment, tags, new DLList<MyImage>());
				allChecksPassed = true;
			}
		} catch (Exception ex){
			System.err.println("Exception occurred in actionPerformed() attempting to save changes");
			System.err.println(ex);
			ex.printStackTrace();
		}
		
		//saving the changed event, then removing the old and adding the new allows the new event to be sorted if the date is changed
		if (allChecksPassed){
			eventTree.remove(selectedEvent);
			eventTree.add(event);
			selectedEvent = null;
			updateComponentVisibility(event.getIsPeriod());
			writeToFile();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readFromFile(){
		/* try {
			FileInputStream fis = new FileInputStream("events.txt");
			ObjectInputStream in = new ObjectInputStream(fis);
			eventTree = (TreeSet<Event>)(in.readObject());
			in.close();
		} catch (Exception ex){
			System.err.println(ex);
			readFromTimelineFile();
		} */
		
		File file = new File("events.txt");
		String eventString = "";
         
        try (FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)) {
            //read all bytes from buffered input stream and create string out of it
            String s = new String(bis.readAllBytes());
			String[] eventArr = s.split("\nend\n\n");
			eventTree = new TreeSet<Event>();
			
			for (int i = 0; i < eventArr.length; i++){
				eventString = eventArr[i];
				String title = eventString.substring(eventString.indexOf("Title: ") + 7, eventString.indexOf("\nDescription: "));
				String description = eventString.substring(eventString.indexOf("Description: ") + 13, eventString.indexOf("\nisPeriod: "));
				boolean isPeriod = Boolean.parseBoolean(eventString.substring(eventString.indexOf("isPeriod: ") + 10, eventString.indexOf("\nDate: ")));
				eventString = eventString.substring(eventString.indexOf("\nDate: ") + 7);
				
				int month = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
				eventString = eventString.substring(eventString.indexOf("/") + 1);
				int day = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
				eventString = eventString.substring(eventString.indexOf("/") + 1);
				
				Event event;
				if (isPeriod){
					int year = Integer.parseInt(eventString.substring(0, eventString.indexOf(" - ")));
					eventString = eventString.substring(eventString.indexOf(" - ") + 3);
					
					String date2String = eventString.substring(0, eventString.indexOf("\nColor: "));
					eventString = eventString.substring(eventString.indexOf("Color: ") + 7);
					
					int red = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int green = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int blue = Integer.parseInt(eventString.substring(0, eventString.indexOf("\nCategory: ")));
					eventString = eventString.substring(eventString.indexOf("Category: ") + 10);
					String category = eventString.substring(0, eventString.indexOf("\nAlignment: "));
					eventString = eventString.substring(eventString.indexOf("Alignment: ") + 11);
					String alignment = eventString.substring(0, eventString.indexOf("\nTags: "));
					eventString = eventString.substring(eventString.indexOf("Tags: ") + 6);
					String[] tagArr = eventString.substring(0, eventString.indexOf("\nImages: ")).split(", ");
					DLList<String> tagList = new DLList<String>();
					for (int j = 0; j < tagArr.length; j++){
						if (tagArr[j].equals("none"))
							break;
						tagList.add(tagArr[j]);
					}
					
					eventString = eventString.substring(eventString.indexOf("Images: ") + 8);
					DLList<MyImage> imgList = new DLList<MyImage>();
					if (!eventString.equals("none")){
						// System.out.println("string: " + eventString);
						String[] imgArr = eventString.split(" \\| ");
						// System.out.println("length: " + imgArr.length);
						
						for (int j = 0; j < imgArr.length; j++){
							// System.out.println(imgArr[j]);
							imgList.add(new MyImage(imgArr[j].substring(0, imgArr[j].indexOf(";")), imgArr[j].substring(imgArr[j].indexOf(";")+1)));
						}
					}
					
					if (date2String.equals("present")){
						event = new Event(title, description, month, day, year, true, red, green, blue, category, tagList, imgList);
					} else {
						int month2 = Integer.parseInt(date2String.substring(0, date2String.indexOf("/")));
						date2String = date2String.substring(date2String.indexOf("/") + 1);
						int day2 = Integer.parseInt(date2String.substring(0, date2String.indexOf("/")));
						date2String = date2String.substring(date2String.indexOf("/") + 1);
						int year2 = Integer.parseInt(date2String);
						
						event = new Event(title, description, month, day, year, month2, day2, year2, red, green, blue, category, tagList, imgList);
					}
				} else {
					int year = Integer.parseInt(eventString.substring(0, eventString.indexOf("\nColor: ")));
					eventString = eventString.substring(eventString.indexOf("Color: ") + 7);
					int red = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int green = Integer.parseInt(eventString.substring(0, eventString.indexOf("/")));
					eventString = eventString.substring(eventString.indexOf("/") + 1);
					int blue = Integer.parseInt(eventString.substring(0, eventString.indexOf("\nCategory: ")));
					eventString = eventString.substring(eventString.indexOf("Category: ") + 10);
					String category = eventString.substring(0, eventString.indexOf("\nAlignment: "));
					eventString = eventString.substring(eventString.indexOf("Alignment: ") + 11);
					String alignment = eventString.substring(0, eventString.indexOf("\nTags: "));
					eventString = eventString.substring(eventString.indexOf("Tags: ") + 6);
					String[] tagArr = eventString.substring(0, eventString.indexOf("\nImages: ")).split(", ");
					DLList<String> tagList = new DLList<String>();
					for (int j = 0; j < tagArr.length; j++){
						if (tagArr[j].equals("none"))
							break;
						tagList.add(tagArr[j]);
					}
					
					eventString = eventString.substring(eventString.indexOf("Images: ") + 8);
					String[] imgArr = eventString.split(" | ");
					DLList<MyImage> imgList = new DLList<MyImage>();
					for (int j = 0; j < imgArr.length; j++){
						if (imgArr[j].equals("none"))
							break;
						imgList.add(new MyImage(imgArr[j].substring(0, imgArr[j].indexOf(";")), imgArr[j].substring(imgArr[j].indexOf(";")+1)));
					}
					
					event = new Event(title, description, month, day, year, red, green, blue, category, alignment, tagList, imgList);
				}
				
				eventTree.add(event);
			}
			
            bis.close();
            fis.close();
        } catch (IOException e) {
			System.err.println("IOException occurred in readFromFile()");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
			System.err.println("IndexOutOfBoundsException occurred in Screen.readFromFile()");
			System.err.println(eventString);
			e.printStackTrace();
		}
	}
	
	private void readFromTimelineFile(){
		try{
			eventTree = new TreeSet<Event>();
			File file = new File("US History Timeline.txt");
			FileReader reader = new FileReader(file);
			char[] text = new char[473453];
			reader.read(text);
			String s = new String(text);
			
			String s1 = s.substring(s.indexOf("Type: Event"), s.indexOf("Type: Period"));
			String s2 = s.substring(s.indexOf("Type: Period"), s.indexOf("Type: Group"));
			
			String[] events = s1.split("Type: Event\n");
			for (int i = 1; i < events.length; i++){
				
				String title, description;
				if (events[i].indexOf("Description:\n") == -1){
					title = events[i].substring(events[i].indexOf("Name:\n")+"Name:\n".length(), events[i].indexOf("Time:"));
					description = "";
				} else {
					title = events[i].substring(events[i].indexOf("Name:\n")+"Name:\n".length(), events[i].indexOf("Description:\n"));
					description = events[i].substring(events[i].indexOf("Description:\n")+"Description:\n".length(), events[i].indexOf("Time:"));
				}
				
				
				String timeStr = events[i].substring(events[i].indexOf("Time: ")+"Time: ".length(), events[i].indexOf("\n", events[i].indexOf("Time")));
				int year = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int month = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int day;
				
				if (timeStr.indexOf(' ') == -1)
					day = Integer.parseInt(timeStr.substring(0));
				else
					day = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				
				eventTree.add(new Event(title, description, month, day, year, 128, 128, 128, "<category>", "Centered", null, null));
			}
			
			String[] periods = s2.split("Type: Period\n");
			for (int i = 1; i < periods.length; i++){
				String title, description;
				if (periods[i].indexOf("Description:\n") == -1){
					title = periods[i].substring(periods[i].indexOf("Name:\n")+"Name:\n".length(), periods[i].indexOf("Time from:"));
					description = "";
				} else {
					title = periods[i].substring(periods[i].indexOf("Name:\n")+"Name:\n".length(), periods[i].indexOf("Description:\n"));
					description = periods[i].substring(periods[i].indexOf("Description:\n")+"Description:\n".length(), periods[i].indexOf("Time from:"));
				}
				
				
				String timeStr = periods[i].substring(periods[i].indexOf("Time from: ")+11, periods[i].indexOf("\n", periods[i].indexOf("Time from: ")));
				int year = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int month = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				timeStr = timeStr.substring(timeStr.indexOf(' ')+1);
				
				int day;
				
				if (timeStr.indexOf(' ') == -1)
					day = Integer.parseInt(timeStr.substring(0));
				else
					day = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(' ')));
				
				String timeStr2 = periods[i].substring(periods[i].indexOf("Time to: ")+9, periods[i].indexOf("\n", periods[i].indexOf("Time to: ")));
				int year2, month2, day2;
				
				if (timeStr2.equals("@")){
					year2 = Event.today().getYear();
					month2 = Event.today().getMonth();
					day2 = Event.today().getDay();
				} else {
				
					year2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
					timeStr2 = timeStr2.substring(timeStr2.indexOf(' ')+1);
					
					if (timeStr2.indexOf(' ') == -1)
						month2 = Integer.parseInt(timeStr2.substring(0));
					else
						month2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
					
					timeStr2 = timeStr2.substring(timeStr2.indexOf(' ')+1);
					
					if (timeStr2.length() == 0)
						day2 = 1;
					else if (timeStr2.indexOf(' ') == -1)
						day2 = Integer.parseInt(timeStr2.substring(0));
					else
						day2 = Integer.parseInt(timeStr2.substring(0, timeStr2.indexOf(' ')));
				}
				
				eventTree.add(new Event(title, description, month, day, year, month2, day2, year2, 128, 128, 128, "null", null, null));
			}
			
			reader.close();
			
		} catch (Exception ex){
			ex.printStackTrace();	
		}
	}
	
	private void writeToFile(){
		File file = new File("events.txt");
		String s = "";
		for (Event e : eventTree){
			s += e.toStringVerbose() + "\nend\n\n";
		}
		
		try(FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            //convert string to byte array
            byte[] bytes = s.getBytes();
            //write byte array to file
            bos.write(bytes);
            bos.close();
            fos.close();
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
			System.err.println("Exception occurred in writeToFile()");
            e.printStackTrace();
        }
	}

	public void keyPressed(KeyEvent e){
		// System.out.println(e.getKeyCode());
		
		if (e.getKeyCode() == 16){ //shift
			shiftKeyDown = true;
		} else if (e.getKeyCode() == 17){ //control
			controlKeyDown = true;
		} else if (e.getKeyCode() == 27){ //escape
			selectedEvent = null;
			showTagHider = false;
			updateComponentVisibility(true);
		}else if (e.getKeyCode() == 37){ //left arrow
			//this shifts the timeline back 1, 10, 100, or 1000 years depending on whether control or shift is held
			if (controlKeyDown){
				if (shiftKeyDown)
					timeline.setCenterYear(timeline.getCenterYear()-1000);
				else
					timeline.setCenterYear(timeline.getCenterYear()-10);
			} else {
				if (shiftKeyDown)
					timeline.setCenterYear(timeline.getCenterYear()-100);
				else
					timeline.setCenterYear(timeline.getCenterYear()-1);
			}
			//timeline.setCenterYear(timeline.getCenterYear() - (int)Math.pow(10, (shiftKeyDown ? 2 : 0) + (controlKeyDown ? 1 : 0)));
			//the above line does the same thing and is more concise and nice-looking, but i believe it is slower because of the pow method
		} else if (e.getKeyCode() == 38){ //up arrow
			timeline.shiftTimelineUp();
		} else if (e.getKeyCode() == 39){ //right arrow
			//this shifts the timeline forward 1, 10, 100, or 1000 years depending on whether control or shift is held
			if (controlKeyDown){
				if (shiftKeyDown)
					timeline.setCenterYear(timeline.getCenterYear()+1000);
				else
					timeline.setCenterYear(timeline.getCenterYear()+10);
			} else {
				if (shiftKeyDown)
					timeline.setCenterYear(timeline.getCenterYear()+100);
				else
					timeline.setCenterYear(timeline.getCenterYear()+1);
			}
		} else if (e.getKeyCode() == 40){ //down arrow
			timeline.shiftTimelineDown();
		} else if (e.getKeyCode() == 45){ //minus/underscore
			if (controlKeyDown)
				timeline.zoomOut();
		} else if (e.getKeyCode() == 61){ //equals/plus
			if (controlKeyDown)
				timeline.zoomIn();
		} else if (e.getKeyCode() == 112){ //F1
			editMode = !editMode;
			showTagHider = false;
			updateComponentVisibility(selectedEvent == null ? true : selectedEvent.getIsPeriod());
		} else if (e.getKeyCode() == 113){ //F2
			modernDating = !modernDating;
			BCCheckBox.setText(modernDating ? "BCE" : "BC");
			BCCheckBox2.setText(modernDating ? "BCE" : "BC");
		} else if (e.getKeyCode() == 114){ //F3
			showTagHider = !showTagHider;
			editMode = false;
			selectedEvent = null;
			updateComponentVisibility(true);
			
			if (showTagHider){
				if (tagList == null || tagList.length == 0)
					tagTextArea.setText("none");
				else{
					String s = "";
					for (int i = 0; i < tagList.length; i++)
						s += tagList[i] + ", ";
					
					tagTextArea.setText(s.substring(0, s.length() - 2));
				}
			}
		} else if (e.getKeyCode() == 115){ //F4
			darkMode = !darkMode;
			backgroundColor = darkMode ? darkModeColor : lightModeColor;
		}
		
		repaint();
	}
	
	public void keyReleased(KeyEvent e){
		if (e.getKeyCode() == 16){ //shift
			shiftKeyDown = false;
		} else if (e.getKeyCode() == 17){ //control
			controlKeyDown = false;
		}
	}
	
	public void keyTyped(KeyEvent e){}
	
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();
		// System.out.println("(" + mouseX + ", " + mouseY + ")");
		
		boolean clickedOnEvent = false;
		for (int i = 0; i < timeline.getDrawnEventCoordinates().size() + timeline.getDrawnPeriodCoordinates().size(); i++){
			Rectangle rect = null;
			if (i < timeline.getDrawnEventCoordinates().size())
				rect = timeline.getDrawnEventCoordinates().get(i);
			else
				rect = timeline.getDrawnPeriodCoordinates().get(i - timeline.getDrawnEventCoordinates().size());
			
			if (mouseX >= rect.getX() && mouseX <= rect.getX2() && mouseY >= rect.getY() && mouseY <= rect.getY2()){
				selectedEvent = rect.getEvent();
				initializeFieldText();
				clickedOnEvent = true;
			}
		}
		
		if (!clickedOnEvent){
			selectedEvent = null;
		}
		
		updateComponentVisibility(selectedEvent == null ? true : selectedEvent.getIsPeriod());
		repaint();
	}
	
	private void updateComponentVisibility(boolean isPeriod){
		boolean showEditTools1 = editMode && selectedEvent == null;
		boolean showEditTools2 = editMode && selectedEvent != null;
		
		addNewEventButton.setVisible(showEditTools1);
		addNewPeriodButton.setVisible(showEditTools1);
		titleField.setVisible(showEditTools2);
		descriptionPane.setVisible(selectedEvent != null);
		descriptionTextArea.setCaretPosition(0);
		monthField.setVisible(showEditTools2);
		dayField.setVisible(showEditTools2);
		yearField.setVisible(showEditTools2);
		redField.setVisible(showEditTools2);
		greenField.setVisible(showEditTools2);
		blueField.setVisible(showEditTools2);
		hexField.setVisible(showEditTools2);
		BCCheckBox.setVisible(showEditTools2);
		presentCheckBox.setVisible(showEditTools2);
		eventPositionCheckBox.setVisible(showEditTools2);
		eventShapeComboBox.setVisible(showEditTools2);
		saveChangesButton.setVisible(showEditTools2);
		removeEventButton.setVisible(showEditTools2);
		categoryComboBox.setVisible(showEditTools2);
		
		if (showEditTools2){
			descriptionTextArea.setEditable(true);
			descriptionTextArea.setFont(new Font("Ubuntu", Font.PLAIN, 15));
			descriptionPane.setBounds(descriptionPaneX2, descriptionPaneY2, descriptionPaneW2, descriptionPaneH2);
		} else if (!editMode && selectedEvent != null){
			descriptionTextArea.setEditable(false);
			descriptionTextArea.setFont(new Font("Ubuntu", Font.PLAIN, 30));
			descriptionTextArea.setText(selectedEvent.toString(Event.today().getYear(), modernDating));
			descriptionPane.setBounds(descriptionPaneX1, descriptionPaneY1, descriptionPaneW1, descriptionPaneH1);
			prevImageButton.setVisible(imageIndex > 0);
			nextImageButton.setVisible(imageIndex < selectedEvent.getImages().size() - 1);
		}
			
		prevImageButton.setVisible(selectedEvent != null && selectedEvent.getImages().size() > 0);
		nextImageButton.setVisible(selectedEvent != null && selectedEvent.getImages().size() > 0);
		
		
		if (isPeriod){
			month2Field.setVisible(showEditTools2);
			day2Field.setVisible(showEditTools2);
			year2Field.setVisible(showEditTools2);
			BCCheckBox2.setVisible(showEditTools2);
			eventAlignmentComboBox.setVisible(false);
			presentCheckBox.setBounds(BCCheckBox2.getX() + BCCheckBox2.getWidth() + 40, BCCheckBox2.getY(), 90, BCCheckBox2.getHeight());
		} else {
			month2Field.setVisible(false);
			day2Field.setVisible(false);
			year2Field.setVisible(false);
			BCCheckBox2.setVisible(false);
			eventAlignmentComboBox.setVisible(showEditTools2);
			presentCheckBox.setBounds(BCCheckBox.getX() + BCCheckBox.getWidth() + 40, BCCheckBox.getY(), 90, BCCheckBox.getHeight());
		}
		
		tagComboBox.setVisible(showEditTools2 || showTagHider);
		tagPane.setVisible(showEditTools2 || showTagHider);
		addTagButton.setVisible(showEditTools2 || showTagHider);
		removeTagButton.setVisible(showEditTools2 || showTagHider);
		showTaggedEventsButton.setVisible(showTagHider);
		hideTaggedEventsButton.setVisible(showTagHider);
	}
	
	private void initializeFieldText(){
		titleField.setText(selectedEvent.getTitle());
		descriptionTextArea.setText(selectedEvent.getDescription());
		tagComboBox.setSelectedIndex(0);
		
		redField.setText(Date.numFormat(selectedEvent.getColor().getRed(), 3));
		greenField.setText(Date.numFormat(selectedEvent.getColor().getGreen(), 3));
		blueField.setText(Date.numFormat(selectedEvent.getColor().getBlue(), 3));
		hexField.setText(selectedEvent.RGBtoHex(selectedEvent.getColor().getRed(), selectedEvent.getColor().getGreen(), selectedEvent.getColor().getBlue()));
		
		Tag temp1 = new Tag(selectedEvent.getCategory(), selectedEvent.getColor());
		boolean categoryBoxSet = false;
		for (int j = 0; j < categoryComboBox.getItemCount(); j++){
			Tag temp2 = (Tag)categoryComboBox.getItemAt(j);
			if (temp1.getTitle().equals(temp2.getTitle())){
				categoryComboBox.setSelectedItem(temp2);
				categoryBoxSet = true;
				break;
			}
		}
		
		if (!categoryBoxSet)
			categoryComboBox.setSelectedIndex(0);
		
		tagTextArea.setText(selectedEvent.getTagString());
		
		if (selectedEvent.getYear() <= 0)
			yearField.setText(Integer.toString(-selectedEvent.getYear()+1));
		else
			yearField.setText(Integer.toString(selectedEvent.getYear()));
		
		if (selectedEvent.isPresent()){
			presentCheckBox.setSelected(true);
		} else {
			presentCheckBox.setSelected(false);
			monthField.setText(Integer.toString(selectedEvent.getMonth()));
			dayField.setText(Integer.toString(selectedEvent.getDay()));
			BCCheckBox.setSelected(selectedEvent.getYear() < 0);
			
			if (selectedEvent.getIsPeriod()){
				month2Field.setText(Integer.toString(selectedEvent.getMonth2()));
				day2Field.setText(Integer.toString(selectedEvent.getDay2()));
				BCCheckBox2.setSelected(selectedEvent.getYear2() < 0);
				
				if (selectedEvent.getYear2() <= 0)
					year2Field.setText(Integer.toString(-selectedEvent.getYear2()+1));
				else
					year2Field.setText(Integer.toString(selectedEvent.getYear2()));
			}
		}
		
		presentFieldHandler();
		
		if (selectedEvent.getIsPeriod()){
			eventAlignmentComboBox.setSelectedItem(selectedEvent.getAlignment());
			eventAlignmentComboBox.setSelectedIndex(1);
		}
	}
	
	private void presentFieldHandler(){
		if (presentCheckBox.isSelected()){
			if (selectedEvent.getIsPeriod()){
				month2Field.setEditable(false);
				month2Field.setText(Event.today().getMonth() + "");
				day2Field.setEditable(false);
				day2Field.setText(Event.today().getDay() + "");
				year2Field.setEditable(false);
				year2Field.setText(Event.today().getYear() + "");
				BCCheckBox2.setEnabled(false);
				BCCheckBox2.setSelected(false);
			} else {
				monthField.setEditable(false);
				monthField.setText(Event.today().getMonth() + "");
				dayField.setEditable(false);
				dayField.setText(Event.today().getDay() + "");
				yearField.setEditable(false);
				yearField.setText(Event.today().getYear() + "");
				BCCheckBox.setEnabled(false);
				BCCheckBox.setSelected(false);
			}
		} else {
			monthField.setEditable(true);
			dayField.setEditable(true);
			yearField.setEditable(true);
			BCCheckBox.setEnabled(true);
			if (selectedEvent.getIsPeriod()){
				month2Field.setEditable(true);
				day2Field.setEditable(true);
				year2Field.setEditable(true);
				BCCheckBox2.setEnabled(true);
			}
		}
	}
}